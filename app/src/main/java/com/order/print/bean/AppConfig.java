package com.order.print.bean;

public class AppConfig {
    private long ApiInterval;

    private String SystemImage;

    private int SmallVar;

    private String Package;

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

    public void setSystemImage(String systemImage) {
        SystemImage = systemImage;
    }

    public void setSmallVar(int smallVar) {
        SmallVar = smallVar;
    }

    public void setPackage(String aPackage) {
        Package = aPackage;
    }
}
