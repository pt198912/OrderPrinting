package com.order.print.util;

/**
 * Created by pt198 on 05/09/2018.
 */

public class HttpApi {
    public static final String BASE_URL = "http://fjhaoshi.test.yiqianye.vip/api/v1/";
    public static final String LOGIN = BASE_URL + "user/login";
    public static final String QUERY_ORDER = BASE_URL + "order/latest";
    public static final String UPDATE_ORDER = BASE_URL + "order/oprint";
    public static final String RESET_ORDER_STATUS = BASE_URL + "order/reprint?id=";
}
