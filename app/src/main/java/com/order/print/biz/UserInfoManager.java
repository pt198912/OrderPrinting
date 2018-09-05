package com.order.print.biz;

import com.order.print.util.Constants;
import com.order.print.util.SharePrefUtil;

/**
 * Created by pt198 on 05/09/2018.
 */

public class UserInfoManager {
    private String name;
    private String pwd;
    private String token;
    private UserInfoManager(){

    }
    private static class SingletonInstance{
        private static final UserInfoManager INSTANCE=new UserInfoManager();
    }
    public static UserInfoManager getInstance(){
        return SingletonInstance.INSTANCE;
    }
    public String getToken(){
        String token=SharePrefUtil.getInstance().getString(Constants.SP_KEY_TOKEN);
        return token;
    }
    public String getName(){
        return SharePrefUtil.getInstance().getString(Constants.SP_KEY_USER_NAME);
    }
    public String getPwd(){
        return SharePrefUtil.getInstance().getString(Constants.SP_KEY_PWD);
    }
    public void setToken(String token){
        this.token=token;
        SharePrefUtil.getInstance().setString(Constants.SP_KEY_TOKEN,token);
    }

    public void setName(String name) {
        this.name = name;
        SharePrefUtil.getInstance().setString(Constants.SP_KEY_USER_NAME,name);
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
        SharePrefUtil.getInstance().setString(Constants.SP_KEY_PWD,pwd);
    }
}
