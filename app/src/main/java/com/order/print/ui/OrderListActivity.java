package com.order.print.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.order.print.net.MyException;
import com.order.print.net.MyResponseCallback;
import com.order.print.util.DialogUtils;
import com.order.print.util.HttpUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import am.example.printer.dialogs.BluetoothTestDialogFragment;
import am.util.printer.PrinterWriter;
import am.util.printer.PrinterWriter80mm;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderListActivity extends BaseActivity implements MyResponseCallback<QueryOrderResult> {
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
    List<Order> mDatas = new ArrayList<>();
    OrderListAdapter mAdapter;
    @BindView(R.id.tv_conn_state)
    TextView tvConnState;

    private int type = PrinterWriter80mm.TYPE_80;
    private int height = PrinterWriter.HEIGHT_PARTING_DEFAULT;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        ButterKnife.bind(this);
        initView();
        DialogUtils.loading(this, "");
        getOrderList();
    }

    private void initView() {
        tvTitle.setText("订单列表");
        rightTv.setText("打印");
        if(BluetoothInfoManager.getInstance().getConnectedBluetooth()!=null){
            tvConnState.setText(String.format(getResources().getString(R.string.label_conncted),BluetoothInfoManager.getInstance().getConnectedBluetooth().getAddress()));
        }else{
            tvConnState.setText(getResources().getString(R.string.label_no_conn));
            tvConnState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent blue=new Intent(OrderListActivity.this,BluetoothDeviceListActivity.class);
                    startActivity(blue);
                    finish();
                }
            });
        }
        mAdapter = new OrderListAdapter();
        lvOrderList.setAdapter(mAdapter);
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
    }

    private void getOrderList() {
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
        Toast.makeText(this, "查询订单失败", Toast.LENGTH_SHORT).show();
        smartRefreshLayout.finishRefresh();
    }

    @OnClick({R.id.iv_back,R.id.tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                checkBluetooth();
                break;
        }
    }

    public void checkBluetooth() {
        if (BluetoothInfoManager.getInstance().getConnectedBluetooth() == null) {
            Toast.makeText(App.getInstance(), "蓝牙未连接", Toast.LENGTH_SHORT).show();
            Intent i=new Intent(App.getInstance(), BluetoothDeviceListActivity.class);
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
                .getFragment(type, width, height, strQRCode);
        fragment.show(ft, "blue");
    }


    class OrderListAdapter extends BaseAdapter {
        private SimpleDateFormat sdf = new SimpleDateFormat("mm-dd HH:mm:ss");

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
            holder.tvDate.setText(sdf.format(mDatas.get(i).getCreate_time()));
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
