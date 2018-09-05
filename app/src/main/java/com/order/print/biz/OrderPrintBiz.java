package com.order.print.biz;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import com.order.print.util.IntentUtils;

import java.util.Vector;

/**
 * Created by pt198 on 04/09/2018.
 */

public class OrderPrintBiz {
    private GpService mGpService = null;
    private static final String TAG = "OrderPrintBiz";
    private static class SingletonInstance{
        private static final OrderPrintBiz INSTANCE=new OrderPrintBiz();
    }
    private OrderPrintBiz(){

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
        bindService();
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
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mGpService = GpService.Stub.asInterface(service);
            BluetoothBiz.getInstance().setGpService(mGpService);
        }
    }
    public static OrderPrintBiz getInstance() {
        return SingletonInstance.INSTANCE;
    }
}
