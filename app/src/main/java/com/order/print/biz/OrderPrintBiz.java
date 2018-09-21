package com.order.print.biz;


import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.gprinter.aidl.GpService;
//import com.gprinter.aidl.GpService;
import com.order.print.App;
import com.order.print.bean.Order;
import com.order.print.bean.QueryOrderResult;
import com.order.print.database.DbManager;
import com.order.print.net.MyException;
import com.order.print.net.MyResponse;
import com.order.print.net.MyResponseCallback;
import com.order.print.threadpool.CustomThreadPool;
import com.order.print.util.HttpUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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


    public void init(){
//        bindService();
        startTimer();
        startPrintTask();
    }
    public void release(){
        stopTimer();
        stopPrintTask();
    }
    private void stopPrintTask(){
        mLoop=false;
        if(mPrintTh!=null&&!mPrintTh.isInterrupted()){
            mPrintTh.interrupt();
        }
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
                            &&BluetoothInfoManager.getInstance().isConnected()){
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
                    boolean isHistory=false;
                    for(int i=0;i<mHistoryOrders.size();i++){
                        if(mHistoryOrders.get(i).getOrder_id()==order.getOrder_id()){
                            isHistory=true;
                            break;
                        }
                    }
                    if(isHistory){
                        mHistoryOrders.removeAll(mPrintingList);
                        mDatas.removeAll(mPrintingList);
                        Log.d(TAG, "onSuccess: isHistory mHistoryOrders.size "+mHistoryOrders.size());
                        synchronized (mPrintTh) {
                            mPrintTh.notifyAll();
                        }
                    }else {
//                        CustomThreadPool.getInstance().submit(new Runnable() {
//                            @Override
//                            public void run() {
//                                Log.d(TAG, "run: insertOrder");
//                                DbManager.getInstance().insertOrder(mPrintingList);
//                            }
//                        });
//
//                        mDatas.removeAll(mPrintingList);
//                        Log.d(TAG, "onSuccess: mPrintTh.notify");
//                        synchronized (mPrintTh) {
//                            mPrintTh.notifyAll();
//                        }
                        HttpUtils.updateOrderStatus(order.getOrder_id() + "", "1", new MyResponseCallback<MyResponse>() {
                            @Override
                            public void onSuccess(MyResponse data) {

                            }

                            @Override
                            public void onSuccessList(List<MyResponse> data) {
                                CustomThreadPool.getInstance().submit(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d(TAG, "run: insertOrder");
                                        DbManager.getInstance().insertOrder(mPrintingList);
                                    }
                                });

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
                        }, MyResponse.class);
                    }
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

    public void printOneOrder(List<Order> orders){
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

    private List<Order> mHistoryOrders=new ArrayList<>();

    public void addHistoryOrderList(List<Order> orders){
        if(orders!=null) {
            mHistoryOrders.addAll(orders);
        }
        this.mDatas.addAll(0,orders);
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
    private void stopTimer(){
        if(mTimer!=null){
            mTimer.cancel();
        }
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
        if(mHistoryOrders!=null){
            mDatas.addAll(mHistoryOrders);
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

//    private void startService() {
//        IntentUtils.startService(App.getInstance(),GpService.class);
//
//    }

//    private void bindService() {
//        startService();
//        PrinterServiceConnection conn = new PrinterServiceConnection();
//        final Intent intent = new Intent();
//        intent.setAction("com.gprinter.aidl.GpService");
//        intent.setPackage(App.getInstance().getPackageName());
//        App.getInstance().bindService(intent, conn, Context.BIND_AUTO_CREATE);
//    }


//    private class PrinterServiceConnection implements ServiceConnection {
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            mGpService = null;
//            Log.d(TAG, "onServiceDisconnected: ");
//        }
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            Log.d(TAG, "onServiceConnected: ");
//            mGpService = GpService.Stub.asInterface(service);
//            BluetoothBiz.getInstance().setGpService(mGpService);
//        }
//    }
    public static OrderPrintBiz getInstance() {
        return SingletonInstance.INSTANCE;
    }
}
