package com.order.print.service;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gprinter.io.GpDevice;
import com.gprinter.service.GpPrintService;
import com.order.print.App;
import com.order.print.IBluetoothService;
import com.order.print.IPrintStatusChangeListener;
import com.order.print.R;
import com.order.print.biz.BluetoothBiz;
import com.order.print.biz.BluetoothInfoManager;
import com.order.print.player.VoicePlayerManager;
import com.order.print.util.Constants;
import com.order.print.util.SharePrefUtil;

import java.util.Timer;
import java.util.TimerTask;

import static com.order.print.biz.BluetoothBiz.BLUE_CONNECTED;

/**
 * Created by pt198 on 04/09/2018.
 */

public class BluetoothService extends Service {
    private static final String TAG = "BluetoothService";
    private IPrintStatusChangeListener mPrintStatusChangeListener;
    private IBinder mBinder=new IBluetoothService.Stub(){

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void setPrintStatusChangeListener(IPrintStatusChangeListener listener) throws RemoteException {
            mPrintStatusChangeListener=listener;
        }
    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BluetoothBiz.getInstance().registerReceicer(this);
//        startTimer();
        autoConnLastBlueDevice();
    }
    private void autoConnLastBlueDevice(){
        BluetoothBiz.getInstance().autoConnect();
    }
//    private Timer mTimer;
//    private void startTimer(){
//        if(mTimer!=null){
//            mTimer.cancel();
//        }
//        mTimer=new Timer();
//        TimerTask task=new TimerTask() {
//            @Override
//            public void run() {
//                if(BluetoothBiz.getInstance().getSocket()!=null){
//                    boolean connected=BluetoothBiz.getInstance().getSocket().isConnected();
//                    BluetoothBiz.OnBluetoothStateListener listener=BluetoothBiz.getInstance().getListener();
//                    Log.d(TAG, "blue state connected "+connected);
//                    if(connected){
//                        if(!BluetoothInfoManager.getInstance().isConnected()) {
//                            VoicePlayerManager.getInstance().playVoice(R.raw.blue_conn);
//                            if(listener!=null){
//                                listener.onConnectionChanged(BLUE_CONNECTED);
//                            }
//                            BluetoothInfoManager.getInstance().setConnected(true);
//                        }
//                        Log.d(TAG, " bluetooth connected");
//                    }else{
//                        if(BluetoothInfoManager.getInstance().isConnected()) {
//                            Log.d(TAG, " bluetooth disconnected");
//                            VoicePlayerManager.getInstance().playVoice(R.raw.blue_disconn);
//                            BluetoothInfoManager.getInstance().setConnected(false);
//                            if (listener != null) {
//                                listener.onConnectionChanged(BluetoothBiz.BLUE_DISCONNECTD);
//                            }
//                        }
//                    }
//                }
//
//            }
//        };
//        mTimer.schedule(task,0, 500);
//    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BluetoothBiz.getInstance().unregisterRecevier(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }
}
