package com.order.print.bean;


import java.io.Serializable;


/**
 * Created by pt198 on 05/09/2018.
 */

public class OrderAddr implements Serializable{
    private static final long serialVersionUID = 681648025284427839L;
    //    "name": "李科",            用户名称
//            "mobile": "13599923090",   电话
//            "addr": "集美区 孙厝乐安南里118号"        用户地址

    private int orderId;
    private String name;

    private String mobile;
    private String addr;


    public String getName() {
        return name;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
}
