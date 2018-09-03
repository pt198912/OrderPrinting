package com.order.print.net;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hjw on 2018/7/29.
 */

public class MyResponseCallback<T> implements Serializable {

    public MyResponseCallback() {
    }

    public void onSuccess(T data) {
    }
    public void onSuccessList(List<T> data) {
    }

    public void onFailure(MyException e) {
    }
}