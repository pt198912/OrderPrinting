package com.order.print.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.order.print.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_right)
    ImageView ivRight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        initView();
    }

    private void initView(){
        tvTitle.setText("设置");
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
