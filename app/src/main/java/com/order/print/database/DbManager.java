package com.order.print.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.order.print.App;
import com.order.print.bean.Order;
import com.order.print.bean.OrderAddr;
import com.order.print.bean.OrderItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pt198 on 18/09/2018.
 */

public class DbManager {
    private ContentResolver mCr;
    private static final String TAG = "DbManager";
    private static class SingletonInstance{
        private static final DbManager INSTANCE=new DbManager();
    }
    private DbManager(){
        mCr=App.getInstance().getContentResolver();
    }
    public static DbManager getInstance(){
        return SingletonInstance.INSTANCE;
    }
    public void insertOrder(List<Order> orders){
        for(Order order:orders) {
            ContentValues values = new ContentValues();
            values.put("order_id", order.getOrder_id());
            values.put("shop_id", order.getShop_id());
            values.put("create_time", order.getCreate_time());
            values.put("cj_money", order.getCj_money());
            values.put("full_reduce_price", order.getFull_reduce_price());
            values.put("logistics", order.getLogistics());
            values.put("need_pay", order.getNeed_pay());
            values.put("shop_name", order.getShop_name());
            mCr.insert(OrderContentProvider.ORDER_URI, values);
            insertAddr(order.getAddr());
            insertOrderItems(order.getItems());
        }
    }
    public void insertOrder(Order order){
        ContentValues values=new ContentValues();
        values.put("order_id",order.getOrder_id());
        values.put("shop_id",order.getShop_id());
        values.put("create_time",order.getCreate_time());
        values.put("cj_money",order.getCj_money());
        values.put("full_reduce_price",order.getFull_reduce_price());
        values.put("logistics",order.getLogistics());
        values.put("need_pay",order.getNeed_pay());
        values.put("shop_name",order.getShop_name());
        Uri uri=mCr.insert(OrderContentProvider.ORDER_URI,values);
        Log.d("pengtao", "insertOrder: uri "+uri.toString());
        insertAddr(order.getAddr());
        insertOrderItems(order.getItems());
    }
    public void insertAddr(OrderAddr addr){
        ContentValues values=new ContentValues();
        values.put("name",addr.getName());
        values.put("mobile",addr.getMobile());
        values.put("addr",addr.getAddr());
        values.put("orderId",addr.getOrderId());
        mCr.insert(OrderContentProvider.ADDR_URI,values);
    }
    public void insertOrderItems(List<OrderItem> items){
        for(OrderItem item:items) {
            ContentValues values = new ContentValues();
            values.put("name", item.getName());
            values.put("num", item.getNum());
            values.put("total_price", item.getTotal_price());
            values.put("orderId", item.getOrderId());
            mCr.insert(OrderContentProvider.ORDER_ITEM_URI, values);
        }
    }
    public List<Order> queryAllOrders(){
        Cursor c=mCr.query(OrderContentProvider.ORDER_URI,null,null,null,null);
        Log.d(TAG, "queryAllOrders: "+c.getCount());
        List<Order> list=new ArrayList<>();
        if(c!=null){
            while(c.moveToNext()){
                Order o=new Order();
                o.setOrder_id(c.getInt(c.getColumnIndex("order_id")));
                o.setShop_id(c.getInt(c.getColumnIndex("shop_id")));
                o.setCreate_time(Long.parseLong(c.getString(c.getColumnIndex("create_time"))));
                o.setCj_money(Double.parseDouble(c.getString(c.getColumnIndex("cj_money"))));
                o.setFull_reduce_price(Double.parseDouble(c.getString(c.getColumnIndex("full_reduce_price"))));
                o.setLogistics(Double.parseDouble(c.getString(c.getColumnIndex("logistics"))));
                o.setNeed_pay(Double.parseDouble(c.getString(c.getColumnIndex("need_pay"))));
                o.setShop_name(c.getString(c.getColumnIndex("shop_name")));
                OrderAddr addr=queryAddr(o.getOrder_id());
                o.setAddr(addr);
                List<OrderItem> items=queryOrderItems(o.getOrder_id());
                o.setItems(items);
                list.add(o);
            }
        }
        return list;
    }
    private OrderAddr queryAddr(int orderId){
        Cursor c=mCr.query(OrderContentProvider.ADDR_URI,null,"orderId=?",new String[]{orderId+""},null);
        if(c!=null&&c.moveToNext()){
            OrderAddr addr=new OrderAddr();
            addr.setName(c.getString(c.getColumnIndex("name")));
            addr.setMobile(c.getString(c.getColumnIndex("mobile")));
            addr.setAddr(c.getString(c.getColumnIndex("addr")));
            addr.setOrderId(c.getInt(c.getColumnIndex("orderId")));
            return addr;
        }
        return null;
    }
    private List<OrderItem> queryOrderItems(int orderId){
        Cursor c=mCr.query(OrderContentProvider.ORDER_ITEM_URI,null,"orderId=?",new String[]{orderId+""},null);
        List<OrderItem> items=new ArrayList<>();
        if(c!=null){
            while(c.moveToNext()) {
                OrderItem item = new OrderItem();
                item.setName(c.getString(c.getColumnIndex("name")));
                item.setNum(c.getInt(c.getColumnIndex("num")));
                item.setTotal_price(Double.parseDouble(c.getString(c.getColumnIndex("total_price"))));
                item.setOrderId(c.getInt(c.getColumnIndex("orderId")));
                items.add(item);
            }
        }
        return items;
    }
}
