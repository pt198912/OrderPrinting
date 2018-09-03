package com.order.print.net;

/**
 * Created by hjw on 2018/7/29.
 */
public class MyException extends Exception {
    /**
     * -1:为返回结果格式错误,为空或者格式不符合Json;
     * 0: 调用成功
     * 其余则按照后台定义
     */
    private int code;
    private String msg;

    public MyException() {
        super();
    }


    public MyException(String msg) {
        super(msg);
    }

    public MyException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public MyException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public MyException(Throwable throwable) {
        super(throwable);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
