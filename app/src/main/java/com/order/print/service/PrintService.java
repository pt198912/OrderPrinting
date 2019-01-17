package com.order.print.service;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.order.print.R;
import com.order.print.biz.OrderPrintBiz;

public class PrintService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            setForegroundService();
        }

    }

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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        OrderPrintBiz.getInstance().init();
        return Service.START_STICKY;
    }
}
