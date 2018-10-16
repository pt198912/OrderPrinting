package com.order.print.util;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.order.print.biz.UserInfoManager;
import com.order.print.net.MyException;
import com.order.print.net.MyRequest;
import com.order.print.net.MyResponse;
import com.order.print.net.MyResponseCallback;

import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pt198 on 05/09/2018.
 */

public class HttpUtils {
    public static <T> void login(String name, String pwd, MyResponseCallback<T> cb,Class<T> cls){
        Map<String,String> paras=new HashMap<>();
        paras.put("name",name);
        paras.put("pass",pwd);
        MyRequest.sendPostRequest(HttpApi.LOGIN,paras,cb,cls,false);
    }
    public static <T> void queryOrderPage( MyResponseCallback<T> cb,Class<T> cls){
        Map<String,String> headers=new HashMap<>();
        headers.put("XX-Token", UserInfoManager.getInstance().getToken());
        MyRequest.sendPostRequest(HttpApi.QUERY_ORDER,headers,null,cb,cls,false);
    }
    public static <T> void updateOrderStatus(String id, String status, MyResponseCallback<T> cb,Class<T> cls){
        Map<String,String> paras=new HashMap<>();
        paras.put("id",id);
        paras.put("status",status);
        Map<String,String> headers=new HashMap<>();
        headers.put("XX-Token", UserInfoManager.getInstance().getToken());
        MyRequest.sendPostRequest(HttpApi.UPDATE_ORDER,headers,paras,cb,cls,true);
    }
    public static <T> void resetOrderStatus(String id, String status, MyResponseCallback<T> cb,Class<T> cls){
        Map<String,String> paras=new HashMap<>();
        paras.put("id",id);
        Map<String,String> headers=new HashMap<>();
        headers.put("XX-Token", UserInfoManager.getInstance().getToken());
        MyRequest.sendPostRequest(HttpApi.RESET_ORDER_STATUS,headers,paras,cb,cls,true);
    }
    public static <T> void getAppConfig(final MyResponseCallback<T> callback, final Class<T> cls){
        RequestParams reParams = new RequestParams(HttpApi.GET_APP_CONFIG);
        reParams.setCharset("utf-8");
        x.http().request(HttpMethod.GET, reParams, new Callback.CommonCallback<String>() {


            @Override
            public void onSuccess(String result) {

                Log.i(Constants.HTTP_TAG, "response-data:" + result);
                T response= JSON.parseObject(result,cls);
                if (null != response) {
                    callback.onSuccess(response);
                }else{
                    callback.onFailure(new MyException());
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(Constants.HTTP_TAG, "request-error:" + ex.toString());
                callback.onFailure(new MyException("服务器异常"));
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e(Constants.HTTP_TAG, "request-cancell: " + cex.getCause().toString());
                callback.onFailure(new MyException(cex.getMessage()));
            }

            @Override
            public void onFinished() {
                Log.e(Constants.HTTP_TAG, "request-finished");
            }
        });

    }
}
