package com.order.print.util;

import com.order.print.biz.UserInfoManager;
import com.order.print.net.MyRequest;
import com.order.print.net.MyResponseCallback;

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
        MyRequest.sendPostRequest(HttpApi.UPDATE_ORDER,headers,paras,cb,cls,false);
    }
    public static <T> void resetOrderStatus(String id, String status, MyResponseCallback<T> cb,Class<T> cls){
        Map<String,String> paras=new HashMap<>();
        paras.put("id",id);
        Map<String,String> headers=new HashMap<>();
        headers.put("XX-Token", UserInfoManager.getInstance().getToken());
        MyRequest.sendPostRequest(HttpApi.UPDATE_ORDER,headers,paras,cb,cls,false);
    }
}
