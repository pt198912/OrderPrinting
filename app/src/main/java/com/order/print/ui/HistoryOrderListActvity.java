package com.order.print.ui;

import android.content.Intent;
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

import com.order.print.R;
import com.order.print.bean.Order;
import com.order.print.biz.OrderPrintBiz;
import com.order.print.database.DbManager;
import com.order.print.threadpool.CustomThreadPool;
import com.order.print.util.IntentUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HistoryOrderListActvity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.lv_his)
    ListView lvHis;
    private List<Order> mDatas=new ArrayList<>();
    private OrderListAdapter mAdapter;
    private static final String TAG = "HistoryOrderListActvity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_his_order);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initData(){
        CustomThreadPool.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                mDatas.clear();
                Log.d(TAG, "initData:");
                List<Order> datas= DbManager.getInstance().queryAllOrders();
                Log.d(TAG, "run: "+datas.size());
                if(datas!=null) {
                    mDatas.addAll(datas);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

    }

    private void initView() {
        tvTitle.setText("历史订单");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("打印");
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderPrintBiz.getInstance().addHistoryOrderList(mDatas);
            }
        });
        mAdapter=new OrderListAdapter();
        lvHis.setAdapter(mAdapter);
        lvHis.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent detail=new Intent(HistoryOrderListActvity.this,OrderDetailActivity.class);
                detail.putExtra("extra",mDatas.get(i));
                startActivity(detail);
            }
        });
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
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
                view = LayoutInflater.from(HistoryOrderListActvity.this).inflate(R.layout.list_item_order, null);
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
