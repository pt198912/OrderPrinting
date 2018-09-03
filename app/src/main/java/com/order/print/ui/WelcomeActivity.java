package com.order.print.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.order.print.R;
import com.order.print.util.Constants;
import com.order.print.util.IntentUtils;
import com.order.print.util.SharePrefUtil;

/**
 * Created by pt198 on 03/09/2018.
 */

public class WelcomeActivity extends BaseActivity {
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

    }
}
