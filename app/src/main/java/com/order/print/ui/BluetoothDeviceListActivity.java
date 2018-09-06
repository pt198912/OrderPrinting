package com.order.print.ui;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
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

import com.gprinter.io.GpDevice;
import com.order.print.App;
import com.order.print.R;
import com.order.print.bean.BluetoothBean;
import com.order.print.biz.BluetoothBiz;
import com.order.print.ui.adapter.DeviceAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BluetoothDeviceListActivity extends BaseActivity implements BluetoothBiz.OnBluetoothStateListener{
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.lv_bluetooth_list)
    ListView lvBluetoothList;
    MyBluetoothAdapter mAdapter;
    List<BluetoothBean> mDatas=new ArrayList();
    List<BluetoothBean> mBluetoothList=new ArrayList<>();
    ProgressDialog pdConnect;
    private static final String TAG = "BluetoothDeviceListActi";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_device_list);
        ButterKnife.bind(this);
        initView();
        BluetoothBiz.getInstance().setListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchBluetooth();
    }

    private void initView(){
        mAdapter=new MyBluetoothAdapter();
        lvBluetoothList.setAdapter(mAdapter);
        lvBluetoothList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (0 != mBluetoothList.size()) {
                    pdConnect = ProgressDialog.show(BluetoothDeviceListActivity.this, "", "开始连接", true, true);
                    pdConnect.setCanceledOnTouchOutside(false);
                    pdConnect.show();
                    BluetoothBiz.getInstance().connect(mBluetoothList.get(position).mBluetoothAddress, mBluetoothList.get(position).mBluetoothDevice);
                }
            }
        });
    }
    private void searchBluetooth(){
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
        String desc="";
        boolean connected=false;
        switch (status) {
            case GpDevice.STATE_CONNECTED:
                App.getInstance().setConnectedDevice(device);
                desc="已连接";
                connected=true;
                break;
            case GpDevice.STATE_LISTEN:
                Log.i(TAG,  "state:STATE_LISTEN");
                desc="未连接";
                App.getInstance().setConnectedDevice(null);
                connected=false;
                break;
            case GpDevice.STATE_CONNECTING:
                Log.i(TAG, "state:STATE_CONNECTING");
                desc="连接中";
                App.getInstance().setConnectedDevice(null);
                connected=false;
                break;
            case GpDevice.STATE_NONE:
                Log.i(TAG,  "state:STATE_NONE");
                desc="未连接";
                App.getInstance().setConnectedDevice(null);
                connected=false;
                break;
            default:
                Log.i(TAG,  "state:default");
                desc="未连接";
                App.getInstance().setConnectedDevice(null);
                connected=false;
                break;
        }
        final boolean connRes=connected;
        Log.d(TAG, "onPrintDeviceConnStatusChanged: "+desc+","+status);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
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
            String a = "未知状态";
            switch (status) {
                case BluetoothDevice.BOND_BONDING:
                    a = "连接中";
                    break;
                case BluetoothDevice.BOND_BONDED:
                    a = "连接完成";
                    break;
                case BluetoothDevice.BOND_NONE:
                    a = "未连接/取消连接";
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
