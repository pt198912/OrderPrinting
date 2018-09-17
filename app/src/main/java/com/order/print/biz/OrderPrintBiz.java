package com.order.print.biz;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.gprinter.aidl.GpService;
import com.gprinter.command.EscCommand;
import com.gprinter.command.GpCom;
import com.gprinter.command.GpUtils;
import com.gprinter.command.LabelCommand;
//import com.gprinter.aidl.GpService;
import com.order.print.App;
import com.order.print.bean.Order;
import com.order.print.bean.QueryOrderResult;
import com.order.print.net.MyException;
import com.order.print.net.MyResponse;
import com.order.print.net.MyResponseCallback;
import com.order.print.ui.BluetoothDeviceListActivity;
import com.order.print.util.DialogUtils;
import com.order.print.util.HttpUtils;
import com.order.print.util.IntentUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import am.example.printer.data.TestPrintDataMaker;
import am.util.printer.PrintExecutor;
import am.util.printer.PrintSocketHolder;
import am.util.printer.PrinterWriter;
import am.util.printer.PrinterWriter58mm;

/**
 * Created by pt198 on 04/09/2018.
 */

public class OrderPrintBiz implements MyResponseCallback<QueryOrderResult>, PrintExecutor.OnPrintResultListener {
    private GpService mGpService = null;
    private Handler mHandler;
    private BluetoothDevice mDevice;
    private PrintExecutor executor;
    private TestPrintDataMaker maker;
    List<Order> mDatas = Collections.synchronizedList(new ArrayList<Order>());
    private int type = PrinterWriter58mm.TYPE_58;
    private int height = PrinterWriter.HEIGHT_PARTING_DEFAULT;
    private static final String TAG = "OrderPrintBiz";
    private static class SingletonInstance{
        private static final OrderPrintBiz INSTANCE=new OrderPrintBiz();
    }
    private OrderPrintBiz(){
        mHandler=new Handler();
    }

    public List<Order> getDatas() {
        return mDatas;
    }

    public void printOrder() {
        Log.i(TAG, "printOrder(MainActivity.java:500)--->> " + "printOrder");
        LabelCommand tsc = new LabelCommand();
        tsc.addSize(40, 30); // 设置标签尺寸，按照实际尺寸设置
        tsc.addGap(1); // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
        tsc.addDirection(LabelCommand.DIRECTION.FORWARD, LabelCommand.MIRROR.NORMAL);// 设置打印方向
        tsc.addReference(0, 0);// 设置原点坐标
        tsc.addTear(EscCommand.ENABLE.ON); // 撕纸模式开启
        Log.i(TAG, "sendLabel(MainActivity.java:507)--->> " + EscCommand.ENABLE.ON.getValue());

        tsc.addCls();// 清除打印缓冲区
        // 绘制简体中文
        tsc.addText(30, 20, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                "花满楼");

        tsc.addText(30, 70, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                "仓库：1号仓");
        //180
        tsc.addText(200, 20, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                "箱号：2");
        tsc.addText(30, 110, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                "线路：A19");
        tsc.addText(30, 150, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                "数量：5");
        tsc.addText(30, 190, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                "日期：2017年11月21日");

        // 绘制图片
//        Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//        tsc.addBitmap(20, 50, LabelCommand.BITMAP_MODE.OVERWRITE, b.getWidth(), b);

        //二维码
        tsc.addQRCode(200, 70, LabelCommand.EEC.LEVEL_L, 4, LabelCommand.ROTATION.ROTATION_0, " www.gprinter.com.cn");

        // 绘制一维条码
//        tsc.add1DBarcode(20, 250, LabelCommand.BARCODETYPE.CODE128, 100, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, "Gprinter");

        tsc.addPrint(1, 1); // 打印标签
        tsc.addSound(2, 100); // 打印标签后 蜂鸣器响
//        tsc.addCashdrwer(LabelCommand.FOOT.F5, 255, 255);

        Vector<Byte> datas = tsc.getCommand(); // 发送数据
        byte[] bytes = GpUtils.ByteTo_byte(datas);
        String str = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rel=0;
        try {
            rel = mGpService.sendLabelCommand(0, str);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                Toast.makeText(App.getInstance(), GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void init(){
//        bindService();
        startTimer();
        startPrintTask();
    }
    private Thread mPrintTh;
    private List<Order> mPrintingList=Collections.synchronizedList(new ArrayList<Order>());
    private boolean mLoop=true;
    private void startPrintTask(){
        mPrintTh= new Thread(){
            @Override
            public void run() {
                while(mLoop){
                    Log.d(TAG, "run:isPrintOrderFlag "+App.getInstance().isPrintOrderFlag()+",mDatas.size() "+mDatas.size());
                    if(App.getInstance().isPrintOrderFlag()&&mDatas.size()>0&&BluetoothInfoManager.getInstance().getConnectedBluetooth()!=null
                            &&BluetoothInfoManager.getInstance().getConnectedBluetooth().getBondState()== BluetoothDevice.BOND_BONDED){
                        List<Order> list=mDatas.subList(0,1);
                        mPrintingList.clear();
                        mPrintingList.addAll(list);
                        printOneOrder(list);
                        synchronized (this){
                            try {
                                this.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }else{
                        try {
                            Thread.sleep(3000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        mPrintTh.start();
    }

    @Override
    public void onResult(int errorCode) {
        Log.d(TAG, "onResult: " + errorCode);
        switch (errorCode) {
            case PrintSocketHolder.ERROR_0:
                if(mPrintingList.size()>0) {
                    Order order=mPrintingList.get(0);
                    HttpUtils.updateOrderStatus(order.getOrder_id()+"", "1", new MyResponseCallback<MyResponse>() {
                        @Override
                        public void onSuccess(MyResponse data) {

                        }

                        @Override
                        public void onSuccessList(List<MyResponse> data) {
                            mDatas.removeAll(mPrintingList);
                            Log.d(TAG, "onSuccess: mPrintTh.notify");
                            synchronized (mPrintTh) {
                                mPrintTh.notifyAll();
                            }

                        }

                        @Override
                        public void onFailure(MyException e) {
                            synchronized (mPrintTh) {
                                mPrintTh.notifyAll();
                            }
                        }
                    },MyResponse.class);
                }

                break;
            case PrintSocketHolder.ERROR_1:
//                dialog.setState(R.string.printer_result_message_2);
                break;
            case PrintSocketHolder.ERROR_2:
//                dialog.setState(R.string.printer_result_message_3);
                break;
            case PrintSocketHolder.ERROR_3:
//                dialog.setState(R.string.printer_result_message_4);
                break;
            case PrintSocketHolder.ERROR_4:
//                dialog.setState(R.string.printer_result_message_5);
                break;
            case PrintSocketHolder.ERROR_5:
//                dialog.setState(R.string.printer_result_message_6);
                break;
            case PrintSocketHolder.ERROR_6:
//                dialog.setState(R.string.printer_result_message_7);
                break;
            case PrintSocketHolder.ERROR_100:
//                dialog.setState(R.string.printer_result_message_8);
                break;
        }
    }

    private void printOneOrder(List<Order> orders){
        Log.d(TAG, "printOneOrder: ");
        mDevice= BluetoothInfoManager.getInstance().getConnectedBluetooth();
        if (mDevice == null)
            return;
        if (executor == null) {
            executor = new PrintExecutor(mDevice, type);
//                executor.setOnStateChangedListener(this);
            executor.setOnPrintResultListener(this);
        }
        executor.setDevice(mDevice);
        maker = new TestPrintDataMaker(App.getInstance(), "", 500, height,orders);
        executor.doPrinterRequestAsync(maker);
    }


    private Timer mTimer;
    private void startTimer(){
        if(mTimer!=null){
            mTimer.cancel();
        }
        mTimer=new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        getOrderList();
                    }
                });

            }
        };
        mTimer.schedule(task,0,App.getInstance().getQueryOrderDuration());
    }
    private void getOrderList() {
        Log.d(TAG, "getOrderList: ");
        HttpUtils.queryOrderPage(this, QueryOrderResult.class);
    }
    @Override
    public void onSuccess(QueryOrderResult data) {
        mDatas.clear();
        try {
            Log.d(TAG, "onSuccess: " + data.getData().size());
        }catch (Exception e){
            e.printStackTrace();
        }
        mDatas.addAll(data.getData());

    }

    @Override
    public void onSuccessList(List<QueryOrderResult> data) {

    }

    @Override
    public void onFailure(MyException e) {

        Toast.makeText(App.getInstance(), "查询订单失败", Toast.LENGTH_SHORT).show();
    }

    public void startPrintService(){
        App.getInstance().setPrintOrderFlag(true);
    }

    public void stopPrintService(){
        App.getInstance().setPrintOrderFlag(false);
    }

    private void startService() {
        IntentUtils.startService(App.getInstance(),GpService.class);

    }

    private void bindService() {
        startService();
        PrinterServiceConnection conn = new PrinterServiceConnection();
        final Intent intent = new Intent();
        intent.setAction("com.gprinter.aidl.GpService");
        intent.setPackage(App.getInstance().getPackageName());
        App.getInstance().bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }




    private class PrinterServiceConnection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mGpService = null;
            Log.d(TAG, "onServiceDisconnected: ");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: ");
            mGpService = GpService.Stub.asInterface(service);
            BluetoothBiz.getInstance().setGpService(mGpService);
        }
    }
    public static OrderPrintBiz getInstance() {
        return SingletonInstance.INSTANCE;
    }
}
