package com.order.print.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.order.print.App;

/**
 * Created by pt198 on 03/09/2018.
 */

public class SharePrefUtil {
    private SharedPreferences mSp;
    private static final String KEY_SP_NAME="user_info";
    private SharePrefUtil(){
        mSp= App.getInstance().getSharedPreferences(KEY_SP_NAME, Context.MODE_PRIVATE);
    }
    private static class SingletonInstance {
        private static final SharePrefUtil INSTANCE = new SharePrefUtil();
    }

    public static SharePrefUtil getInstance() {
        return SingletonInstance.INSTANCE;
    }

    public String getString(String key) {
        return mSp.getString(key,"");
    }
    public int getInt(String key) {
        return mSp.getInt(key,0);
    }
    public boolean getBoolean(String key) {
        return mSp.getBoolean(key,false);
    }
    public float getFloat(String key) {
        return mSp.getFloat(key,0.0f);
    }
    public long getLong(String key) {
        return mSp.getLong(key,0L);
    }
    public void setString(String key,String s){
        mSp.edit().putString(key,s).commit();
    }
    public void setInt(String key,int s){
        mSp.edit().putInt(key,s).commit();
    }
    public void setFloat(String key,float s){
        mSp.edit().putFloat(key,s).commit();
    }
    public void setLong(String key,long s){
        mSp.edit().putFloat(key,s).commit();
    }

}
