package com.order.print.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.order.print.R;
import com.order.print.bean.Order;
import com.order.print.bean.OrderItem;
import com.order.print.biz.BluetoothBiz;
import com.order.print.biz.OrderPrintBiz;
import com.order.print.util.ScreenUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pt198 on 19/09/2018.
 */

public class OrderDetailActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_addr)
    TextView tvAddr;
    @BindView(R.id.ll_goods)
    LinearLayout llGoods;
    @BindView(R.id.tv_total_num)
    TextView tvTotalNum;
    @BindView(R.id.tv_tableware)
    TextView tvTableware;
    @BindView(R.id.tv_distribution_fees)
    TextView tvDistributionFees;
    @BindView(R.id.tv_favourable)
    TextView tvFavourable;
    private Order mOrder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_his_order_detail);
        ButterKnife.bind(this);
        getIntntData();
        initView();
    }

    private void getIntntData() {
        mOrder = (Order) getIntent().getSerializableExtra("extra");

    }
    private void initView(){
        tvTitle.setText("订单详情");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("打印");
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Order> orders=new ArrayList<Order>();
                OrderPrintBiz.getInstance().addHistoryOrderList(orders);
            }
        });
        for(int i=0;i<mOrder.getItems().size();i++){
            OrderItem item=mOrder.getItems().get(i);
            View content=LayoutInflater.from(this).inflate(R.layout.goods_item,null);
            TextView name=content.findViewById(R.id.tv_goods_name);
            TextView num=content.findViewById(R.id.tv_goods_num);
            TextView total=content.findViewById(R.id.tv_goods_total_money);
            name.setText(item.getName());
            num.setText("X"+item.getNum());
            total.setText("￥"+item.getTotal_price());
            llGoods.addView(content,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dip2px(this,48)));
        }
        tvName.setText(mOrder.getAddr().getName());
        tvPhone.setText(mOrder.getAddr().getMobile());
        tvAddr.setText(mOrder.getAddr().getAddr());
        tvTotalNum.setText("￥"+mOrder.getNeed_pay()+"");
        tvTableware.setText("￥"+mOrder.getCj_money()+"");
        tvDistributionFees.setText("￥"+mOrder.getLogistics());
        tvFavourable.setText("￥"+mOrder.getFull_reduce_price());
    }

    @OnClick({R.id.iv_back, R.id.tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                List<Order> orders=new ArrayList<>();
                orders.add(mOrder);
                OrderPrintBiz.getInstance().addHistoryOrderList(orders);
                break;
        }
    }
}
