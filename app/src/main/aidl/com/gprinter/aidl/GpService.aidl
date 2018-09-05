// GpService.aidl
package com.gprinter.aidl;

// Declare any non-default types here with import statements

interface GpService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
    int openPort(int PrinterId,int PortType,String DeviceName,int PortNumber);
    void closePort(int PrinterId);
    int getPrinterConnectStatus(int PrinterId);
    int printeTestPage(int PrinterId);
    void queryPrinterStatus(int PrinterId,int Timesout,int requestCode);
    int getPrinterCommandType(int PrinterId);
    int sendEscCommand(int PrinterId, String b64);
    int sendLabelCommand(int PrinterId, String  b64);
    void isUserExperience(boolean userExperience);
    String getClientID();
    int setServerIP(String ip, int port);
}
