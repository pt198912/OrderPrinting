package com.order.print.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by pt198 on 05/09/2018.
 */

public class Order implements Serializable{
//     "order_id": 841,                 订单编号
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
    private int order_id;
    private int shop_id;
    private double logistics;
    private double need_pay;
    private double full_reduce_price;
    private long create_time;
    private double cj_money;
    private String shop_name;
    private OrderAddr addr;
    private List<OrderItem> items;

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getShop_id() {
        return shop_id;
    }

    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }

    public double getLogistics() {
        return logistics;
    }

    public void setLogistics(double logistics) {
        this.logistics = logistics;
    }

    public double getNeed_pay() {
        return need_pay;
    }

    public void setNeed_pay(double need_pay) {
        this.need_pay = need_pay;
    }

    public double getFull_reduce_price() {
        return full_reduce_price;
    }

    public void setFull_reduce_price(double full_reduce_price) {
        this.full_reduce_price = full_reduce_price;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public double getCj_money() {
        return cj_money;
    }

    public void setCj_money(double cj_money) {
        this.cj_money = cj_money;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public OrderAddr getAddr() {
        return addr;
    }

    public void setAddr(OrderAddr addr) {
        this.addr = addr;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
