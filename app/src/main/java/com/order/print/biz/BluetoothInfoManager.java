package com.order.print.biz;

import android.bluetooth.BluetoothDevice;

/**
 * Created by pt198 on 10/09/2018.
 */

public class BluetoothInfoManager {
    private BluetoothDevice mConnectedBluetooth;
    private static class SingletonInstance{
        private static final BluetoothInfoManager INSTANCE=new BluetoothInfoManager();
    }
    private BluetoothInfoManager(){

    }
    public static BluetoothInfoManager getInstance(){
        return BluetoothInfoManager.SingletonInstance.INSTANCE;
    }

    public void setConnectedBluetooth(BluetoothDevice connectedBluetooth) {
        this.mConnectedBluetooth = connectedBluetooth;
    }

    public BluetoothDevice getConnectedBluetooth() {
        return mConnectedBluetooth;
    }
}