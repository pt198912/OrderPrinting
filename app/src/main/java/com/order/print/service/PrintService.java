package com.order.print.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.order.print.biz.OrderPrintBiz;

public class PrintService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        OrderPrintBiz.getInstance().init();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return Service.START_NOT_STICKY;
    }
}
