package com.order.print.bean;

public class AppConfig {
    long ApiInterval;

    String SystemImage;

    int SmallVar;

    String Package;

    public String getPackage() {
        return Package;
    }

    public int getSmallVar() {
        return SmallVar;
    }
    public long getApiInterval() {
        return ApiInterval;
    }

    public String getSystemImage() {
        return SystemImage;
    }

    public void setApiInterval(long apiInterval) {
        ApiInterval = apiInterval;
    }



}
