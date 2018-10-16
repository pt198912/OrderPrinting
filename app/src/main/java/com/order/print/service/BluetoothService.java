package com.order.print.service;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

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

    /**
     *通过通知启动服务
     */
    @TargetApi(Build.VERSION_CODES.N)
    public void  setForegroundService()
    {
        //设定的通知渠道名称
        String channelName =CHANNEL_NAME;
        //设置通知的重要程度
        int importance = NotificationManager.IMPORTANCE_LOW;
        //构建通知渠道
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
        channel.setDescription("");
        //在创建的通知渠道上发送通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher) //设置通知图标
                .setContentTitle("")//设置通知标题
                .setContentText("")//设置通知内容
                .setAutoCancel(true) //用户触摸时，自动关闭
                .setOngoing(true);//设置处于运行状态
        //向系统注册通知渠道，注册后不能改变重要性以及其他通知行为
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService( Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
        //将服务置于启动状态 NOTIFICATION_ID指的是创建的通知的ID
        startForeground(NOTIFICATION_ID,builder.build());
    }


    //Channel ID 必须保证唯一
    private static final String CHANNEL_ID = "com.appname.notification.channel";
    private static final String CHANNEL_NAME="blue_channel";
    private static final int NOTIFICATION_ID=0x1200;
    /**
     *创建通知渠道
     */
    private void createNotificationChannel() {
        // 在API>=26的时候创建通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //设定的通知渠道名称
            String channelName = CHANNEL_NAME;
            //设置通知的重要程度
            int importance = NotificationManager.IMPORTANCE_HIGH;
            //构建通知渠道
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription("");
            //向系统注册通知渠道，注册后不能改变重要性以及其他通知行为
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            setForegroundService();
        }
        BluetoothBiz.getInstance().registerReceicer(this);
        startTimer();
        autoConnLastBlueDevice();
    }

    private void autoConnLastBlueDevice(){
        BluetoothBiz.getInstance().autoConnect();
    }
    /**
     * 网络已经连接，然后去判断是wifi连接还是GPRS连接
     * 设置一些自己的逻辑调用
     */
    private boolean isNetworkAvailable(){
        ConnectivityManager manager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo=manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiInfo= manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(mobileInfo==null&&wifiInfo==null){
            return false;
        }
        NetworkInfo.State mobile = null;
        if(mobileInfo!=null) {
            mobile=mobileInfo.getState();
        }
        NetworkInfo.State wifi =null;
        if(wifiInfo!=null) {
            wifi=wifiInfo.getState();
        }
        if(mobile==NetworkInfo.State.CONNECTED||wifi==NetworkInfo.State.CONNECTED){
//            Log.d(TAG, "isNetworkAvailable: true");
            return true;
        }else{
//            Log.d(TAG, "isNetworkAvailable: false");
            return false;
        }
    }
    private Timer mTimer;
    private NetworkInfo.State mLastNetState= NetworkInfo.State.DISCONNECTED;
    private void startTimer(){
        if(mTimer!=null){
            mTimer.cancel();
        }
        mTimer=new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                if(isNetworkAvailable()){
                    if(mLastNetState==NetworkInfo.State.DISCONNECTED) {
                        Log.d(TAG, "onReceive: playVoice VOICE_NET_CONN");
                        VoicePlayerManager.getInstance().playVoice(VoicePlayerManager.VOICE_NET_CONN);
                    }
                    mLastNetState= NetworkInfo.State.CONNECTED;
                }else{
                    if(mLastNetState==NetworkInfo.State.CONNECTED) {
                        Log.d(TAG, "onReceive: playVoice VOICE_NET_DISCONN");
                        VoicePlayerManager.getInstance().playVoice(VoicePlayerManager.VOICE_NET_DISCONN);
                    }
                    mLastNetState=NetworkInfo.State.DISCONNECTED;
                }
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

            }
        };
        mTimer.schedule(task,0, 1000);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BluetoothBiz.getInstance().unregisterRecevier(this);
        if(mTimer!=null){
            mTimer.cancel();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
