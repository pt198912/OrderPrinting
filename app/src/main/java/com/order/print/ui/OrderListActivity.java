package com.order.print.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Toast;

import com.order.print.App;
import com.order.print.R;
import com.order.print.bean.Order;
import com.order.print.bean.QueryOrderResult;
import com.order.print.biz.BluetoothInfoManager;
import com.order.print.biz.OrderPrintBiz;
import com.order.print.net.MyException;
import com.order.print.net.MyResponse;
import com.order.print.net.MyResponseCallback;
import com.order.print.util.Constants;
import com.order.print.util.DialogUtils;
import com.order.print.util.HttpUtils;
import com.order.print.util.IntentUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import am.example.printer.data.TestPrintDataMaker;
import am.example.printer.dialogs.BluetoothTestDialogFragment;
import am.util.printer.PrintExecutor;
import am.util.printer.PrintSocketHolder;
import am.util.printer.PrinterWriter;
import am.util.printer.PrinterWriter58mm;
import am.util.printer.PrinterWriter80mm;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.order.print.util.Constants.ACTION_UPDATE_CONN_STATE;

public class OrderListActivity extends BaseActivity implements MyResponseCallback<QueryOrderResult>, PrintExecutor.OnPrintResultListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.lv_order_list)
    ListView lvOrderList;
    @BindView(R.id.smart_layout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.tv_right)
    TextView rightTv;
    List<Order> mDatas = Collections.synchronizedList(new ArrayList<Order>());
    OrderListAdapter mAdapter;
    @BindView(R.id.tv_conn_state)
    TextView tvConnState;
    private boolean mLoop=true;
    UpdateConnStateRecevier mReceiver;
    private int type = PrinterWriter58mm.TYPE_58;
    private int height = PrinterWriter.HEIGHT_PARTING_DEFAULT;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        ButterKnife.bind(this);
        initView();
        DialogUtils.loading(this, "");
        startTimer();
        registeReceiver();
//        startPrintTask();
    }

    private void registeReceiver(){
        mReceiver=new UpdateConnStateRecevier();
        IntentFilter filter=new IntentFilter();
        filter.addAction(ACTION_UPDATE_CONN_STATE);
        registerReceiver(mReceiver,filter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    private class UpdateConnStateRecevier extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Constants.ACTION_UPDATE_CONN_STATE)){
                updateBlueConnState();
            }
        }
    }
    private Timer mTimer;
    private void startTimer(){
        if(mTimer!=null){
            mTimer.cancel();
        }
        mTimer=new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getOrderList();

//                        mDatas.clear();
//                        mDatas.addAll(OrderPrintBiz.getInstance().getDatas());
//                        mAdapter.notifyDataSetChanged();
//                        DialogUtils.dissLoad();
                    }
                });

            }
        };
        mTimer.schedule(task,0,App.getInstance().getQueryOrderDuration());
    }

    private Thread mPrintTh;
    private List<Order> mPrintingList=new ArrayList<>();
    private void startPrintTask(){
        mPrintTh= new Thread(){
            @Override
            public void run() {
                while(App.getInstance().isPrintOrderFlag()){
                    if(mDatas.size()>0&&BluetoothInfoManager.getInstance().getConnectedBluetooth()!=null
                            &&BluetoothInfoManager.getInstance().getConnectedBluetooth().getBondState()== BluetoothDevice.BOND_BONDED){
                        List<Order> list=mDatas.subList(0,1);
                        mPrintingList.clear();
                        mPrintingList.addAll(list);
                        printOneOrder(list);
                        synchronized (mPrintTh){
                            try {
                                this.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }else{
                        try {
                            Thread.sleep(3000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        mPrintTh.start();
    }

    private BluetoothDevice mDevice;
    private PrintExecutor executor;
    private TestPrintDataMaker maker;

    private static final String TAG = "OrderListActivity";
    @Override
    public void onResult(int errorCode) {
        Log.d(TAG, "onResult: " + errorCode);
        switch (errorCode) {
            case PrintSocketHolder.ERROR_0:
                if(mPrintingList.size()>0) {
                    Order order=mPrintingList.get(0);
                    HttpUtils.updateOrderStatus(order.getOrder_id()+"", "1", new MyResponseCallback<MyResponse>() {
                        @Override
                        public void onSuccess(MyResponse data) {
                            mDatas.removeAll(mPrintingList);
                            mAdapter.notifyDataSetChanged();
                            Log.d(TAG, "onSuccess: mPrintTh.notify");
                            synchronized (mPrintTh) {
                                mPrintTh.notify();
                            }

                        }

                        @Override
                        public void onSuccessList(List<MyResponse> data) {

                        }

                        @Override
                        public void onFailure(MyException e) {

                        }
                    },MyResponse.class);
                }

                break;
            case PrintSocketHolder.ERROR_1:
//                dialog.setState(R.string.printer_result_message_2);
                break;
            case PrintSocketHolder.ERROR_2:
//                dialog.setState(R.string.printer_result_message_3);
                break;
            case PrintSocketHolder.ERROR_3:
//                dialog.setState(R.string.printer_result_message_4);
                break;
            case PrintSocketHolder.ERROR_4:
//                dialog.setState(R.string.printer_result_message_5);
                break;
            case PrintSocketHolder.ERROR_5:
//                dialog.setState(R.string.printer_result_message_6);
                break;
            case PrintSocketHolder.ERROR_6:
//                dialog.setState(R.string.printer_result_message_7);
                break;
            case PrintSocketHolder.ERROR_100:
//                dialog.setState(R.string.printer_result_message_8);
                break;
        }
    }

    private void printOneOrder(List<Order> orders){
            mDevice= BluetoothInfoManager.getInstance().getConnectedBluetooth();
            if (mDevice == null)
                return;
            if (executor == null) {
                executor = new PrintExecutor(mDevice, type);
//                executor.setOnStateChangedListener(this);
                executor.setOnPrintResultListener(this);
            }
            executor.setDevice(mDevice);
            maker = new TestPrintDataMaker(this, "", 500, height,orders);
            executor.doPrinterRequestAsync(maker);
    }

    private void initView() {
        tvTitle.setText("订单列表");
//        rightTv.setText("设置");
        mAdapter = new OrderListAdapter();
        lvOrderList.setAdapter(mAdapter);
        lvOrderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent detail=new Intent(OrderListActivity.this,OrderDetailActivity.class);
                detail.putExtra("extra",mDatas.get(i));
                startActivity(detail);
            }
        });
        smartRefreshLayout.setOnRefreshListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                getOrderList();
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                getOrderList();
            }
        });
        tvConnState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent blue=new Intent(OrderListActivity.this,BluetoothDeviceListActivity.class);
                startActivity(blue);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBlueConnState();
    }

    private void updateBlueConnState(){
        if(BluetoothInfoManager.getInstance().getConnectedBluetooth()!=null&&BluetoothInfoManager.getInstance().isConnected()){
            tvConnState.setText(String.format(getResources().getString(R.string.label_conncted),BluetoothInfoManager.getInstance().getConnectedBluetooth().getAddress())+"");
        }else{
            tvConnState.setText(getResources().getString(R.string.label_no_conn));
        }

    }

    private void getOrderList() {
        Log.d(TAG, "getOrderList: ");
        HttpUtils.queryOrderPage(this, QueryOrderResult.class);
    }

    @Override
    public void onSuccess(QueryOrderResult data) {
        DialogUtils.dissLoad();
        smartRefreshLayout.finishRefresh();
        mDatas.clear();
        mDatas.addAll(data.getData());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSuccessList(List<QueryOrderResult> data) {

    }

    @Override
    public void onFailure(MyException e) {
        DialogUtils.dissLoad();
//        Toast.makeText(this, "查询订单失败", Toast.LENGTH_SHORT).show();
        smartRefreshLayout.finishRefresh();
    }

    @OnClick({R.id.iv_back,R.id.tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
//                checkBluetooth();
//                IntentUtils.startActivity(this,SettingActivity.class);
                break;
        }
    }

    public void checkBluetooth() {
        if (BluetoothInfoManager.getInstance().getConnectedBluetooth() == null) {
            Toast.makeText(App.getInstance(), "蓝牙未连接", Toast.LENGTH_SHORT).show();
            Intent i=new Intent(App.getInstance(), BluetoothDeviceListActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            App.getInstance().startActivity(i);
            return;
        }
        // 载入设备
        showBluetoothTest();
    }

    private void showBluetoothTest() {
        int width;
        try {
            width = 500;
        } catch (Exception e) {
            width = 500;
        }
        String strQRCode ="";
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("blue");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        BluetoothTestDialogFragment fragment = BluetoothTestDialogFragment
                .getFragment(type, width, height, strQRCode,(ArrayList<Order>) mDatas);
        fragment.show(ft, "blue");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DialogUtils.dissLoad();
        if(mTimer!=null){
            mTimer.cancel();
        }
        unregisterReceiver(mReceiver);
    }

    class OrderListAdapter extends BaseAdapter {
        private SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault());

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int i) {
            return mDatas.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null) {
                view = LayoutInflater.from(OrderListActivity.this).inflate(R.layout.list_item_order, null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.tvCount.setText(mDatas.get(i).getItems().size() + "");
            holder.tvDate.setText(sdf.format(new Date(mDatas.get(i).getCreate_time())));
            holder.tvOrderNo.setText(mDatas.get(i).getOrder_id() + "");
            holder.userName.setText(mDatas.get(i).getAddr().getName());
            return view;
        }


        class ViewHolder {
            @BindView(R.id.user_name)
            TextView userName;
            @BindView(R.id.tv_order_no)
            TextView tvOrderNo;
            @BindView(R.id.tv_count)
            TextView tvCount;
            @BindView(R.id.tv_date)
            TextView tvDate;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
}
