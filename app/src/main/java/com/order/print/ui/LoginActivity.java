package com.order.print.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.order.print.R;
import com.order.print.bean.LoginBean;
import com.order.print.net.MyException;
import com.order.print.net.MyResponseCallback;
import com.order.print.util.HttpUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by pt198 on 03/09/2018.
 */

public class LoginActivity extends BaseActivity implements MyResponseCallback<LoginBean> {
    @BindView(R.id.login_et_phone)
    EditText loginEtPhone;
    @BindView(R.id.login_et_pswd)
    EditText loginEtPswd;
    @BindView(R.id.login_iv_display)
    ImageView loginIvDisplay;
    @BindView(R.id.login_login)
    TextView loginLogin;
    @BindView(R.id.login_sign)
    TextView loginSign;
    @BindView(R.id.login_forgetpwd)
    TextView loginForgetpwd;
    private static final String TAG = "LoginActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @Override
    public void onSuccess(LoginBean data) {

    }

    @Override
    public void onSuccessList(List<LoginBean> data) {

    }

    @Override
    public void onFailure(MyException e) {

    }

    private void login(String name,String pwd) {
        HttpUtils.login(name,pwd,this,LoginBean.class);
    }

    @OnClick(R.id.login_login)
    public void onViewClicked() {
        String name=loginEtPhone.getText().toString();
        String pwd=loginEtPswd.getText().toString();
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pwd)){
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        login(name,pwd);
    }
}
