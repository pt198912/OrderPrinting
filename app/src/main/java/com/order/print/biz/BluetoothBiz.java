package com.order.print.biz;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;

import android.content.Intent;
import android.content.IntentFilter;

import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;


import android.widget.Toast;


import com.gprinter.io.GpDevice;
import com.gprinter.aidl.GpService;
import com.order.print.App;

import com.order.print.IBluetoothService;
import com.order.print.bean.BluetoothBean;
import com.order.print.util.Constants;
import com.order.print.util.IntentUtils;
import com.order.print.util.SharePrefUtil;


import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;

import java.util.UUID;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


/**
 * Created by pt198 on 04/09/2018.
 */

public class BluetoothBiz {
    private ArrayList<BluetoothBean> mBluetoothList;
    BluetoothAdapter adapter;
    MyBroadcastReceiver receiver;
    ArrayList<BluetoothBean> mBluetoothList2;
    Thread mThread;
    BluetoothSocket socket;
    private GpService mGpService = null;
    private boolean mRegistered;
    private static final String TAG = "BluetoothBiz";
    private IBluetoothService mIBlueService;
    public void setGpService(GpService gpService) {
        this.mGpService = gpService;
    }

    private BluetoothBiz(){

    }
    public void init(){
        bindBluetoothService();
    }

    private void bindBluetoothService(){
        IntentUtils.bindService(App.getInstance(), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mIBlueService=IBluetoothService.Stub.asInterface(iBinder);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mIBlueService=null;
            }
        },BluetoothService.class);
    }

    private static class SingletonInstance{
        private static final BluetoothBiz INSTANCE=new BluetoothBiz();
    }
    public static BluetoothBiz getInstance(){
        return SingletonInstance.INSTANCE;
    }
    public void searchBlueToothDevice(Context context) {
        Log.i(TAG, "searchBlueToothDevice");
        mBluetoothList = new ArrayList<>();
        // 检查设备是否支持蓝牙
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            Toast.makeText(App.getInstance(), "当前设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }
        // 如果蓝牙已经关闭就打开蓝牙
        if (!adapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            App.getInstance().startActivity(intent);
            return;
        }
//        // 获取已配对的蓝牙设备
//        Set<BluetoothDevice> devices = adapter.getBondedDevices();
//        // 遍历
//        int count = 0;
//        for (BluetoothDevice pairedDevice : devices) {
//            Log.i(TAG, "searchBlueToothDevice(MainActivity.java:137)--->> " + pairedDevice.getName());
//            if (pairedDevice.getName() == null) {
//                return;
//            } else if (pairedDevice.getName().startsWith("Printer_29D0")) {
//                count++;
//                deviceAddress = pairedDevice.getAddress();
//                mBluetoothDevice = adapter.getRemoteDevice(deviceAddress);
//                connect(deviceAddress, mBluetoothDevice);
//                break;
//            }
//        }
        if (adapter.isEnabled()) {
            if(adapter.isDiscovering()){
                adapter.cancelDiscovery();
            }
            //开始搜索
            adapter.startDiscovery();
            if(!mRegistered) {
                // 设置广播信息过滤
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
                intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                // 注册广播接收器，接收并处理搜索结果
                receiver = new MyBroadcastReceiver();
                context.registerReceiver(receiver, intentFilter);
                mRegistered=true;
            }
        }
    }

    public void unregisterRecevier(Context context){
        if(mRegistered) {
            mRegistered=false;
            context.unregisterReceiver(receiver);
        }
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //找到设备,有可能重复搜索同一设备,可在结束后做去重操作
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device == null) {
                    return;
                }
                if (device.getName() == null) {
                    return;
                }

                BluetoothBean bluetoothBean = new BluetoothBean();
                bluetoothBean.mBluetoothName = device.getName();
                bluetoothBean.mBluetoothAddress = device.getAddress();
                bluetoothBean.mBluetoothDevice = adapter.getRemoteDevice(bluetoothBean.mBluetoothAddress);
                mBluetoothList.add(bluetoothBean);
                if(mListener!=null){
                    mListener.onDiscoveryFound(bluetoothBean);
                }
                Log.i(TAG, "ACTION_FOUND " + device.getName());

//                if (device.getName().startsWith("Printer_29D0")) {
//                    //取消搜索
//                    adapter.cancelDiscovery();
//                    deviceAddress = device.getAddress();
//                    mBluetoothDevice = adapter.getRemoteDevice(deviceAddress);
//                    connectState = device.getBondState();
//                    switch (connectState) {
//                        // 未配对
//                        case BluetoothDevice.BOND_NONE:
//                            // 配对
//                            try {
//                                Method createBondMethod = mBluetoothDevice.getClass().getMethod("createBond");
//                                createBondMethod.invoke(mBluetoothDevice);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            break;
//                        // 已配对
//                        case BluetoothDevice.BOND_BONDED:
//                            if (device.getName().startsWith("Printer_29D0")) {
//                                connect(deviceAddress, mBluetoothDevice);
//                            }
//                            break;
//                    }
//                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.i(TAG, "搜索完成");

                if (0 == mBluetoothList.size())
                    Toast.makeText(App.getInstance(), "搜索不到蓝牙设备", Toast.LENGTH_SHORT).show();
                else {
                    //去重HashSet add会返回一个boolean值，插入的值已经存在就会返回false 所以true就是不重复的
                    HashSet<BluetoothBean> set = new HashSet<>();
                    mBluetoothList2 = new ArrayList<>();
                    for (BluetoothBean bean : mBluetoothList) {
                        boolean add = set.add(bean);
                        if (add) {
                            mBluetoothList2.add(bean);
                        }
                    }
                    if(mListener!=null){
                        mListener.onDiscoveryFinish(mBluetoothList2);
                    }

                }

            }
        }
    }
    private OnBluetoothStateListener mListener;
    public interface OnBluetoothStateListener{
        void onDiscoveryFinish(ArrayList<BluetoothBean> datas);
        void onDiscoveryFound(BluetoothBean data);
        void onPrintDeviceConnStatusChanged(BluetoothDevice device,int status);

    }

    public void setListener(OnBluetoothStateListener listener) {
        this.mListener = listener;
    }
    public synchronized void bond(BluetoothDevice device){
        // 配对
        try {
            Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
            createBondMethod.invoke(device);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 启动连接蓝牙的线程方法
     */
    public synchronized void connect(BluetoothDevice device) {
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
        if (socket != null) {
            try {
                mGpService.closePort(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            socket = null;
        }
        mThread = new ConnectThread(device);
        mThread.start();
    }

    public synchronized void disconnect(){
        if (socket != null) {
            try {
                mGpService.closePort(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            socket = null;
        }
        BluetoothInfoManager.getInstance().setConnectedBluetooth(null);
    }

    private class ConnectThread extends Thread {
        private BluetoothDevice mmDevice;
        private OutputStream mmOutStream;
        //这条是蓝牙串口通用的UUID，不要更改
        private UUID myUuid =
                UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        public ConnectThread( BluetoothDevice device) {
            mmDevice = device;

            try {
                if (socket == null) {
                    socket = device.createRfcommSocketToServiceRecord(myUuid);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {
            adapter.cancelDiscovery();
            try {
                Log.i(TAG,  "连接socket");
                if (socket.isConnected()) {
                    Log.i(TAG, "已经连接过了");
                    SharePrefUtil.getInstance().setString(Constants.SP_KEY_CONNECTED_BLUETOOTH,mmDevice.getAddress());
                    BluetoothInfoManager.getInstance().setConnectedBluetooth(mmDevice);
                    if(mListener!=null) {
                        mListener.onPrintDeviceConnStatusChanged(mmDevice,GpDevice.STATE_CONNECTED);
                    }
                } else {
                    if (socket != null) {
                        try {
                            if (mGpService != null) {
                                int state = mGpService.getPrinterConnectStatus(0);
                                if(mListener!=null) {
                                    mListener.onPrintDeviceConnStatusChanged(mmDevice,state);
                                }
                                switch (state) {
                                    case GpDevice.STATE_CONNECTED:
                                        BluetoothInfoManager.getInstance().setConnectedBluetooth(mmDevice);
                                        SharePrefUtil.getInstance().setString(Constants.SP_KEY_CONNECTED_BLUETOOTH,mmDevice.getAddress());
                                        break;
                                    case GpDevice.STATE_LISTEN:
                                        Log.i(TAG,  "state:STATE_LISTEN");
                                        break;
                                    case GpDevice.STATE_CONNECTING:
                                        Log.i(TAG, "state:STATE_CONNECTING");
                                        break;
                                    case GpDevice.STATE_NONE:
                                        Log.i(TAG,  "state:STATE_NONE");
                                        mGpService.openPort(0, 4, mmDevice.getAddress(), 0);
                                        break;
                                    default:
                                        Log.i(TAG,  "state:default");
                                        break;
                                }
                            } else {
                                Log.i(TAG,  "mGpService IS NULL");
                                if(mListener!=null) {
                                    mListener.onPrintDeviceConnStatusChanged(mmDevice,GpDevice.STATE_NONE);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if(mListener!=null) {
                                mListener.onPrintDeviceConnStatusChanged(mmDevice,GpDevice.STATE_NONE);
                            }
                        }
                    }
                }
            } catch (Exception connectException) {
                Log.i(TAG,  "连接失败");
                if(mListener!=null) {
                    mListener.onPrintDeviceConnStatusChanged(mmDevice,GpDevice.STATE_NONE);
                }
                try {
                    if (socket != null) {
                        mGpService.closePort(0);
                        socket = null;
                    }
                } catch (Exception closeException) {

                }
            }
        }
    }
}
