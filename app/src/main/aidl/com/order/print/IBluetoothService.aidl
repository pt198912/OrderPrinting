// IBluetoothService.aidl
package com.order.print;

// Declare any non-default types here with import statements
import com.order.print.IPrintStatusChangeListener;
interface IBluetoothService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
    void setPrintStatusChangeListener(IPrintStatusChangeListener listener);
}
