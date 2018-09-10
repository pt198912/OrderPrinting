package com.order.print;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.marswin89.marsdaemon.DaemonClient;
import com.marswin89.marsdaemon.DaemonConfigurations;
import com.order.print.receiver.Receiver1;
import com.order.print.receiver.Receiver2;
import com.order.print.service.OrderJobService;
import com.order.print.service.Service2;

import org.xutils.x;

/**
 * Created by pt198 on 03/09/2018.
 */

public class App extends Application {
    private static App sInstance;
    private DaemonClient mDaemonClient;
    public static App getInstance() {
        return sInstance;
    }
//    private BluetoothDevice mConnectedDevice;
//
//    public void setConnectedDevice(BluetoothDevice connectedDevice) {
//        this.mConnectedDevice = connectedDevice;
//    }
//
//    public BluetoothDevice getConnectedDevice() {
//        return mConnectedDevice;
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance=this;
        initXUtils();
    }
    private void initXUtils(){
        x.Ext.init(this);
        x.Ext.setDebug(true);//是否输出Debug日志
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mDaemonClient = new DaemonClient(createDaemonConfigurations());
        mDaemonClient.onAttachBaseContext(base);
    }



    private DaemonConfigurations createDaemonConfigurations(){
        DaemonConfigurations.DaemonConfiguration configuration1 = new DaemonConfigurations.DaemonConfiguration(
                "com.marswin89.marsdaemon.demo:process1",
                OrderJobService.class.getCanonicalName(),
                Receiver1.class.getCanonicalName());
        DaemonConfigurations.DaemonConfiguration configuration2 = new DaemonConfigurations.DaemonConfiguration(
                "com.marswin89.marsdaemon.demo:process2",
                Service2.class.getCanonicalName(),
                Receiver2.class.getCanonicalName());
        DaemonConfigurations.DaemonListener listener = new MyDaemonListener();
        //return new DaemonConfigurations(configuration1, configuration2);//listener can be null
        return new DaemonConfigurations(configuration1, configuration2, listener);
    }


    class MyDaemonListener implements DaemonConfigurations.DaemonListener{
        @Override
        public void onPersistentStart(Context context) {
        }

        @Override
        public void onDaemonAssistantStart(Context context) {
        }

        @Override
        public void onWatchDaemonDaed() {
        }
    }
}
