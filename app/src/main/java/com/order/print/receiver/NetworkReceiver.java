package com.order.print.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;

import com.order.print.R;
import com.order.print.player.VoicePlayerManager;

public class NetworkReceiver extends BroadcastReceiver {
    private final static String TAG = NetworkReceiver.class.getName();

    private String getConnectionType(int type) {
        String connType = "";
        if (type == ConnectivityManager.TYPE_MOBILE) {
            connType = "3G网络数据";
        } else if (type == ConnectivityManager.TYPE_WIFI) {
            connType = "WIFI网络";
        }
        return connType;
    }
    private NetworkInfo.State mLastConnState=NetworkInfo.State.DISCONNECTED;
    @Override
    public void onReceive(Context context, Intent intent) {
        // 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
        Log.d(TAG, "onReceive: "+intent.getAction());
//        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
//            //获取联网状态的NetworkInfo对象
//            NetworkInfo info = intent
//                    .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
//            if (info != null) {
//                //如果当前的网络连接成功并且网络连接可用
//                if (info.isConnected()) {
//                    if (info.getType() == ConnectivityManager.TYPE_WIFI
//                            || info.getType() == ConnectivityManager.TYPE_MOBILE) {
//                        Log.i(TAG, getConnectionType(info.getType()) + "连上");
//                        if(mLastConnState==NetworkInfo.State.DISCONNECTED) {
//                            Log.d(TAG, "onReceive: playVoice ");
//                            VoicePlayerManager.getInstance().playVoice(VoicePlayerManager.VOICE_NET_CONN);
//                        }
//                        mLastConnState=NetworkInfo.State.CONNECTED;
//                    }
//                } else {
//                    Log.i(TAG, getConnectionType(info.getType()) + "断开");
//                    if(mLastConnState==NetworkInfo.State.CONNECTED) {
//                        Log.d(TAG, "onReceive: playVoice ");
//                        VoicePlayerManager.getInstance().playVoice(VoicePlayerManager.VOICE_NET_DISCONN);
//                    }
//                    mLastConnState = NetworkInfo.State.DISCONNECTED;
//                }
//            }
//        }
    }
}