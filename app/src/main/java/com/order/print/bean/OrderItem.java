package com.order.print.bean;


import java.io.Serializable;

/**
 * Created by pt198 on 05/09/2018.
 */

public class OrderItem implements Serializable{
    private static final long serialVersionUID = -8781602424821812295L;
    //      "num": 1,                   商品数量
//            "total_price": 0.01,            商品费用
//            "name": "烤深海秋刀鱼"           商品名称
    private int orderId;
    private int num;
    private double total_price;
    private String name;


    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
