package com.order.print.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.order.print.R;
import com.order.print.bean.LoginBean;
import com.order.print.biz.UserInfoManager;
import com.order.print.net.MyException;
import com.order.print.net.MyResponseCallback;
import com.order.print.util.Constants;
import com.order.print.util.HttpUtils;
import com.order.print.util.IntentUtils;
import com.order.print.util.SharePrefUtil;

import java.util.List;

/**
 * Created by pt198 on 03/09/2018.
 */

public class WelcomeActivity extends BaseActivity implements MyResponseCallback<LoginBean> {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        init();
    }
    private void init(){
        String userName=SharePrefUtil.getInstance().getString(Constants.SP_KEY_USER_NAME);
        String pwd=SharePrefUtil.getInstance().getString(Constants.SP_KEY_PWD);
        if(!TextUtils.isEmpty(userName)&&!TextUtils.isEmpty(pwd)){
            autoLogin(userName,pwd);
        }else{
            IntentUtils.startActivity(this,LoginActivity.class);
            finish();
        }
    }
    private void autoLogin(String userName,String pwd){
        HttpUtils.login(userName,pwd,this,LoginBean.class);
    }
    @Override
    public void onSuccess(LoginBean data) {
        UserInfoManager.getInstance().setToken(data.getToken());
        IntentUtils.startActivity(this,MainActivity.class);
        finish();
    }

    @Override
    public void onSuccessList(List<LoginBean> data) {

    }

    @Override
    public void onFailure(MyException e) {
        Toast.makeText(this, "登录失败，"+e.getMsg(), Toast.LENGTH_SHORT).show();
        IntentUtils.startActivity(this,LoginActivity.class);
    }
}
