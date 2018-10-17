package com.order.print.bean;

public class AppConfig {
    long ApiInterval;
    UpdateInfo Upgrade;
    String SystemImage;
    public long getApiInterval() {
        return ApiInterval;
    }

    public String getSystemImage() {
        return SystemImage;
    }

    public void setApiInterval(long apiInterval) {
        ApiInterval = apiInterval;
    }
    public static class UpdateInfo{
        private int SmallVar;
        private String Package;

        public String getPackage() {
            return Package;
        }

        public int getSmallVar() {
            return SmallVar;
        }
    }

    public UpdateInfo getUpgrade() {
        return Upgrade;
    }
}
