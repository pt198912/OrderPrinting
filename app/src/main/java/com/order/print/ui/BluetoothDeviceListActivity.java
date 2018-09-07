package com.order.print.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bluetoothlib.BlueToothMode;
import com.example.bluetoothlib.BluetoothConnectionCreator;
import com.gprinter.io.GpDevice;
import com.order.print.App;
import com.order.print.R;
import com.order.print.bean.BluetoothBean;
import com.order.print.biz.BluetoothBiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BluetoothDeviceListActivity extends BaseActivity implements BluetoothBiz.OnBluetoothStateListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.lv_bluetooth_list)
    ListView lvBluetoothList;
    MyBluetoothAdapter mAdapter;
    List<BluetoothBean> mDatas = new ArrayList();
    List<BluetoothBean> mBluetoothList = new ArrayList<>();
    ProgressDialog pdConnect;
    private static final String TAG = "BluetoothDeviceListActi";

    BluetoothAdapter mBluetoothAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_device_list);
        ButterKnife.bind(this);
        initView();
        BluetoothBiz.getInstance().setListener(this);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init(){
        //@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        //首先获取BluetoothManager
        BluetoothManager bluetoothManager=(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        //获得蓝牙适配器。蓝牙适配器是我们操作蓝牙的主要对象，可以从中获得配对过的蓝牙集合，可以获得蓝牙传输对象等等

        //获取BluetoothAdapter
        if (bluetoothManager != null) {
            mBluetoothAdapter= bluetoothManager.getAdapter();
        }
        getBoundedDevices();
        searchBluetooth();
    }

    private void disconnectBluetooth(){
        BluetoothBiz.getInstance().disconnect();
    }

    private void getBoundedDevices(){
        Set<BluetoothDevice> blues=mBluetoothAdapter.getBondedDevices();
        for(BluetoothDevice device:blues){
            BluetoothBean bean=new BluetoothBean();
            bean.mBluetoothDevice=device;
            bean.mBluetoothAddress=device.getAddress();
            bean.mBluetoothName=device.getName();
            mBluetoothList.add(bean);
        }

    }

    private void initView() {
        tvTitle.setText("设备列表");
        mAdapter = new MyBluetoothAdapter();
        lvBluetoothList.setAdapter(mAdapter);
        lvBluetoothList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                BluetoothDevice device=mBluetoothList.get(position).mBluetoothDevice;
                if(device.getBondState()==BluetoothDevice.BOND_BONDED){
                    new AlertDialog.Builder(BluetoothDeviceListActivity.this).setMessage("确定断开蓝牙吗？").setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            disconnectBluetooth();
                        }
                    }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                }else{
                    pdConnect = ProgressDialog.show(BluetoothDeviceListActivity.this, "", "开始连接", true, true);
                    pdConnect.setCanceledOnTouchOutside(false);
                    pdConnect.show();
                    BluetoothBiz.getInstance().connect(mBluetoothList.get(position).mBluetoothAddress, mBluetoothList.get(position).mBluetoothDevice);

                }
            }
        });
    }

    private void searchBluetooth() {
        BluetoothBiz.getInstance().searchBlueToothDevice(this);
    }

    @Override
    public void onDiscoveryFinish(ArrayList<BluetoothBean> datas) {
//        mBluetoothList.clear();
//        mBluetoothList.addAll(datas);
//        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDiscoveryFound(BluetoothBean data) {
        mBluetoothList.add(data);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPrintDeviceConnStatusChanged(BluetoothDevice device, int status) {
        String desc = "";
        boolean connected = false;
        switch (status) {
            case GpDevice.STATE_CONNECTED:
                App.getInstance().setConnectedDevice(device);
                desc = "已连接";
                connected = true;
                break;
            case GpDevice.STATE_LISTEN:
                Log.i(TAG, "state:STATE_LISTEN");
                desc = "未连接";
                App.getInstance().setConnectedDevice(null);
                connected = false;
                break;
            case GpDevice.STATE_CONNECTING:
                Log.i(TAG, "state:STATE_CONNECTING");
                desc = "连接中";
                App.getInstance().setConnectedDevice(null);
                connected = false;
                break;
            case GpDevice.STATE_NONE:
                Log.i(TAG, "state:STATE_NONE");
                desc = "未连接";
                App.getInstance().setConnectedDevice(null);
                connected = false;
                break;
            default:
                Log.i(TAG, "state:default");
                desc = "未连接";
                App.getInstance().setConnectedDevice(null);
                connected = false;
                break;
        }
        final boolean connRes = connected;
        Log.d(TAG, "onPrintDeviceConnStatusChanged: " + desc + "," + status);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

    class MyBluetoothAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mBluetoothList.size();
        }

        @Override
        public Object getItem(int position) {
            return mBluetoothList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(App.getInstance()).inflate(R.layout.item_bluetooth, parent, false);
                holder = new ViewHolder();
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.item_text = convertView.findViewById(R.id.item_text);
            holder.item_text_address = convertView.findViewById(R.id.item_text_address);
            holder.tv_conn_state = convertView.findViewById(R.id.tv_conn_state);
            holder.item_text.setText(mBluetoothList.get(position).mBluetoothName);
            holder.item_text_address.setText(mBluetoothList.get(position).mBluetoothAddress);
            holder.tv_conn_state.setText(bltStatus(mBluetoothList.get(position).mBluetoothDevice.getBondState()));
            return convertView;
        }

        public String bltStatus(int status) {
            String a = "未连接";
            switch (status) {
                case BluetoothDevice.BOND_BONDING:
                    a = "连接中";
                    break;
                case BluetoothDevice.BOND_BONDED:
                    a = "已配对";
                    break;
                case BluetoothDevice.BOND_NONE:
                    a = "未连接";
                    break;
            }
            return a;
        }

        class ViewHolder {
            TextView item_text;
            TextView item_text_address;
            TextView tv_conn_state;
        }
    }

}
