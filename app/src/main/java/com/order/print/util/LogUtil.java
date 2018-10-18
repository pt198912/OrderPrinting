package com.order.print.util;

import com.alibaba.fastjson.JSON;
import com.order.print.App;
import com.order.print.bean.Order;
import com.order.print.threadpool.CustomThreadPool;
import com.tencent.stat.StatService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by pt198 on 17/10/2018.
 */

public class LogUtil {
    public static void log(String eventKey,Properties prop) {
        StatService.trackCustomKVEvent(App.getInstance(), eventKey, prop);
    }
    public static void logPrintInfo(Order order) {
        Properties prop = new Properties();
        prop.setProperty("order", JSON.toJSONString(order));
        StatService.trackCustomKVEvent(App.getInstance(), "print", prop);
    }
    public static void logPrintHttpInfo(String info){
        Properties prop = new Properties();
        prop.setProperty("http_info", info);
        StatService.trackCustomKVEvent(App.getInstance(), "print", prop);
    }
    public static void logAddHistiryOrder(Order order){
        Properties prop = new Properties();
        prop.setProperty("addHisOrder", JSON.toJSONString(order));
        StatService.trackCustomKVEvent(App.getInstance(), "print", prop);
    }






    public static void logUpdateOrder(Order order){
        Properties prop = new Properties();
        prop.setProperty("udpate", JSON.toJSONString(order));
        StatService.trackCustomKVEvent(App.getInstance(), "print", prop);
    }
}
