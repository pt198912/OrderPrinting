package com.order.print.biz;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gprinter.io.GpDevice;
import com.gprinter.service.GpPrintService;

/**
 * Created by pt198 on 04/09/2018.
 */

public class BluetoothService extends Service {
    private static final String TAG = "BluetoothService";
    public static final String ACTION_CONNECT_STATUS = "action.connect.status";
//    private IBinder mBinder=new IBluetoothService.stub(){
//
//    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerBroadcast(this);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        BluetoothBiz.getInstance().searchBlueToothDevice(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(printerStatusBroadcastReceiver);
    }


    public void registerBroadcast(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CONNECT_STATUS);
        context.registerReceiver(printerStatusBroadcastReceiver, filter);
    }

    private BroadcastReceiver printerStatusBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_CONNECT_STATUS.equals(intent.getAction())) {
                int type = intent.getIntExtra(GpPrintService.CONNECT_STATUS, 0);
                int id = intent.getIntExtra(GpPrintService.PRINTER_ID, 0);
                if (type == GpDevice.STATE_CONNECTING) {
                    Log.i(TAG, "onReceive(MainActivity.java:430)--->> " + "STATE_CONNECTING");
                } else if (type == GpDevice.STATE_NONE) {
                    Log.i(TAG, "onReceive(MainActivity.java:432)--->> " + "STATE_NONE");
//                    showErrorDialog();
                } else if (type == GpDevice.STATE_VALID_PRINTER) {
                    //打印机-有效的打印机
                    Log.i(TAG, "onReceive(MainActivity.java:436)--->> " + "STATE_VALID_PRINTER");
                } else if (type == GpDevice.STATE_INVALID_PRINTER) {
                    Log.i(TAG, "onReceive(MainActivity.java:438)--->> " + "STATE_INVALID_PRINTER");
                } else if (type == GpDevice.STATE_CONNECTED) {
                    //表示已连接可以打印
                    Log.i(TAG, "onReceive(MainActivity.java:441)--->> " + "STATE_CONNECTED");
                    unregisterReceiver(printerStatusBroadcastReceiver);
//                    showSuccessDialog();
                } else if (type == GpDevice.STATE_LISTEN) {
                    Log.i(TAG, "onReceive(MainActivity.java:445)--->> " + "STATE_LISTEN");
                }
            }
        }
    };
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }
}
