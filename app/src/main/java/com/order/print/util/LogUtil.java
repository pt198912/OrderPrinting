package com.order.print.util;

import com.alibaba.fastjson.JSON;
import com.order.print.App;
import com.order.print.bean.Order;
import com.tencent.stat.StatService;

import java.util.Properties;

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
