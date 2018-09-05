package com.order.print.biz;

import com.order.print.util.Constants;
import com.order.print.util.SharePrefUtil;

/**
 * Created by pt198 on 05/09/2018.
 */

public class UserInfoManager {
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
}
