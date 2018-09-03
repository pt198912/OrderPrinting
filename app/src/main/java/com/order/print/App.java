package com.order.print;

import android.app.Application;

/**
 * Created by pt198 on 03/09/2018.
 */

public class App extends Application {
    private static App sInstance;

    public static App getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance=this;
    }
}
