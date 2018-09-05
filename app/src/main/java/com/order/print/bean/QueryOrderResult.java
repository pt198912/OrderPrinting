package com.order.print.bean;

import java.util.List;

/**
 * Created by pt198 on 05/09/2018.
 */

public class QueryOrderResult {
//    "total": 1,
//            "per_page": 20,
//            "current_page": 1,
//            "last_page": 1,
//            "data": [{
//        "order_id": 841,                 订单编号
//        "shop_id": 265,
//                "logistics": 0.01,              配送费
//        "need_pay": 0.03,                已付
//        "full_reduce_price": 0,         优惠
//        "create_time": 1536052752,      下单时间（时间戳）
//        "cj_money": 0.01,               餐具
//        "shop_name": "一千零一夜",      店铺名称
//        "addr": {
//            "name": "李科",            用户名称
//            "mobile": "13599923090",   电话
//            "addr": "集美区 孙厝乐安南里118号"        用户地址
//        },
//        "items": [{
//            "num": 1,                   商品数量
//            "total_price": 0.01,            商品费用
//            "name": "烤深海秋刀鱼"           商品名称
//        }]
//    }]
    private int total;
    private int per_page;
    private int last_page;
    private List<Order> data;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPer_page() {
        return per_page;
    }

    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }

    public int getLast_page() {
        return last_page;
    }

    public void setLast_page(int last_page) {
        this.last_page = last_page;
    }

    public List<Order> getData() {
        return data;
    }

    public void setData(List<Order> data) {
        this.data = data;
    }
}
