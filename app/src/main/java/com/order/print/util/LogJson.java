package com.order.print.util;

import com.alibaba.fastjson.JSONArray;

import java.util.List;

/**
 * Created by pt198 on 17/10/2018.
 */

public class LogJson {
    private String FunctionName ;
    private List<Param> Params;
    private class Param{
        private int ItemType;
        private String ItemValue;
    }
    public String toJson(){
        return this.toJson();
    }

    public String getFunctionName() {
        return FunctionName;
    }

    public void setFunctionName(String functionName) {
        FunctionName = functionName;
    }

    public List<Param> getParams() {
        return Params;
    }

    public void setParams(List<Param> params) {
        Params = params;
    }

}
