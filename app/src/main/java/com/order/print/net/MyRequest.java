package com.order.print.net;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.order.print.util.Constants;

import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.security.MessageDigest;
import java.util.Map;

/**
 * Created by hjw on 2018/7/29.
 */

public class MyRequest {



    /**
     * @param url       请求地址
     * @param paramsMap 请求参数
     * @param callBack  回调
     * @param clazz     bean类
     * @param <T>
     */
    public static <T> void sendPostRequest(String url, Map<String, String> paramsMap, MyResponseCallback<T> callBack, Class<T> clazz,boolean resultIsList) {
        sendRequest(HttpMethod.POST, url, paramsMap, callBack, clazz, resultIsList);
    }
    public static <T> void sendPostRequest(String url, Map<String, String> headers,Map<String, String> paramsMap, MyResponseCallback<T> callBack, Class<T> clazz,boolean resultIsList) {
        sendRequest(HttpMethod.POST, url,headers, paramsMap, callBack, clazz, resultIsList);
    }

    /**
     * @param url       请求地址
     * @param paramsMap 请求参数
     * @param callBack  回调
     * @param clazz     bean类
     * @param <T>
     */
    public static <T> void sendGetRequest(String url, Map<String, String> paramsMap, MyResponseCallback<T> callBack, Class<T> clazz,boolean resultIsList) {
            sendRequest(HttpMethod.GET, url, paramsMap, callBack, clazz,resultIsList);
    }


    public static String string2Sha1(String str){
        if(str==null||str.length()==0){
            return null;
        }
        char hexDigits[] = {'0','1','2','3','4','5','6','7','8','9',
                'a','b','c','d','e','f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes("UTF-8"));

            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j*2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }
    }

    public static <T> void sendRequest(HttpMethod method, final String url, Map<String, String> paramsMap, final MyResponseCallback<T> callBack, final Class<T> clazz, final boolean resultIsList) {
        sendRequest(method,url,null,paramsMap,callBack,clazz,resultIsList);
    }

    public static <T> void sendRequest(HttpMethod method, final String url, Map<String, String> headers,Map<String, String> paramsMap, final MyResponseCallback<T> callBack, final Class<T> clazz, final boolean resultIsList) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        Log.i(Constants.HTTP_TAG, "--------------------------- request-start ----------------------------");
        Log.i(Constants.HTTP_TAG, "request-method:" + method.toString());
        Log.i(Constants.HTTP_TAG, "request-url:" + url);


        RequestParams reParams = new RequestParams(url);
        reParams.setCharset("utf-8");
//        reParams.addBodyParameter("appVersion", App.appVersionName);
//        reParams.addBodyParameter("version", "v1.0");
//        reParams.addBodyParameter("deviceId", App.deviceId + "");
//        reParams.addBodyParameter("deviceModel", App.deviceModel + "");
//        reParams.addBodyParameter("osType", "android");
//        reParams.addBodyParameter("osVersion", App.osVersion + "");
//        reParams.addBodyParameter("seqId", System.currentTimeMillis() + "");
//        if(null!=App.getLoginUser()){
//            reParams.addBodyParameter("uId", App.getLoginUser().getuId());
//        }

        if (null != headers && headers.size() > 0) {
            for (String key : headers.keySet()) {
                reParams.addHeader(key, headers.get(key));
            }
        }

        //解析封装参数
        if (null != paramsMap && paramsMap.size() > 0) {
            for (String key : paramsMap.keySet()) {
                reParams.addBodyParameter(key, paramsMap.get(key));
            }
        }

        Log.i(Constants.HTTP_TAG, "request-params:" + paramsMap);

        x.http().request(method, reParams, new Callback.CommonCallback<String>() {


            @Override
            public void onSuccess(String result) {

                Log.i(Constants.HTTP_TAG, "response-data:" + result);

                MyResponse response = JSON.parseObject(result, MyResponse.class);
                if (null != response) {
                    if (response.getCode() != 200) {
                        callBack.onFailure(new MyException(response.getCode(),response.getMessage()));
                    } else {
                        if(response.getResult()!=null){
                            if(resultIsList){
                                callBack.onSuccessList(JSON.parseArray(response.getResult().toString(), clazz));
                            }else{
                                callBack.onSuccess(JSON.parseObject(response.getResult().toString(), clazz));
                            }
                        }else{
                            if(resultIsList){
                                callBack.onSuccessList(null);
                            }else{
                                callBack.onSuccess(null);
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(Constants.HTTP_TAG, "request-error:" + ex.toString());
                callBack.onFailure(new MyException("服务器异常"));
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e(Constants.HTTP_TAG, "request-cancell: " + cex.getCause().toString());
                callBack.onFailure(new MyException(cex.getMessage()));
            }

            @Override
            public void onFinished() {
                Log.e(Constants.HTTP_TAG, "request-finished");
            }
        });


    }

}