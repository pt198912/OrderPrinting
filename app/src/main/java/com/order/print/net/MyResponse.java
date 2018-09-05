package com.order.print.net;

/**
 * Created by hjw on 16/5/5.
 */
public class MyResponse {
    //    {
//        "status": true,
//            "message": "",
//            "result": {"token": "1d51d9b9fbdb349b76296e4dbbad8bcb"}
//            "code" : 1001
//    }
    private boolean status;
    private String message;
    private Object result;
    private int code=200;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
