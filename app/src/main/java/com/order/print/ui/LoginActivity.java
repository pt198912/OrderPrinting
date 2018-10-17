package com.order.print.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.order.print.R;
import com.order.print.bean.LoginBean;
import com.order.print.biz.UserInfoManager;
import com.order.print.net.MyException;
import com.order.print.net.MyResponseCallback;
import com.order.print.util.DialogUtils;
import com.order.print.util.HttpUtils;
import com.order.print.util.IntentUtils;

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
    private String name;
    private String pwd;
    private boolean displayPwd = true;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        String phone = UserInfoManager.getInstance().getName();
        if (!TextUtils.isEmpty(phone)) {
            loginEtPhone.setText(phone);
        }
    }

    @Override
    public void onSuccess(LoginBean data) {
        DialogUtils.dissLoad();
        UserInfoManager.getInstance().setName(name);
        UserInfoManager.getInstance().setPwd(pwd);
        UserInfoManager.getInstance().setToken(data.getToken());
        IntentUtils.startActivity(this, MainActivity.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DialogUtils.dissLoad();
    }

    @Override
    public void onSuccessList(List<LoginBean> data) {

    }

    @Override
    public void onFailure(MyException e) {
        DialogUtils.dissLoad();
        Toast.makeText(this, "登录失败，" + e.getMsg(), Toast.LENGTH_SHORT).show();
    }

    private void login(String name, String pwd) {
        HttpUtils.login(name, pwd, this, LoginBean.class);
    }

    @OnClick({R.id.login_login,R.id.login_iv_display})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.login_login:
                name = loginEtPhone.getText().toString();
                pwd = loginEtPswd.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                DialogUtils.loading(this, "");
                login(name, pwd);
                break;
            case R.id.login_iv_display:
                if (displayPwd) {
                    //明文显示
                    loginEtPswd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    displayPwd = false;
                    loginIvDisplay.setImageResource(R.mipmap.login2_display);
                } else {
                    //暗文显示
                    loginEtPswd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    displayPwd = true;
                    loginIvDisplay.setImageResource(R.mipmap.login2_hide);
                }
                loginEtPswd.setSelection(loginEtPswd.getText().length());
                break;
        }
    }


}
