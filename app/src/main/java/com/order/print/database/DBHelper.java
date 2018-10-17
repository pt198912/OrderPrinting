package com.order.print.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.order.print.bean.OrderAddr;
import com.order.print.bean.OrderItem;

import java.util.List;

/**
 * Created by pt198 on 18/09/2018.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME="order_print.db";
    private static final int DB_VERSION=2;
    public DBHelper(Context context){
        this(context,DB_NAME,null,DB_VERSION);
    }
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static final String TABLE_ORDERS="orders";
    public static final String TABLE_ADDR="addrs";
    public static final String TABLE_ORDER_ITEM="items";
    private static final String SQL_CREATE_TABLE_ORDER="create table if not exists "+TABLE_ORDERS+" (" +
            "order_id integer primary key," +
            "shop_id integer ," +
            "logistics text," +
            "need_pay text," +
            "full_reduce_price varchar," +
            "create_time text," +
            "cj_money text," +
            "shop_name text)";
    private static final String SQL_CREATE_TABLE_ADDR="create table if not exists "+TABLE_ADDR+" (" +
            "order_id  integer primary key," +
            "name text," +
            "mobile text," +
            "addr text)";
    private static final String SQL_CREATE_TABLE_ITEMS="create table if not exists "+TABLE_ORDER_ITEM+" (" +
            "order_id  integer," +
            "num integer," +
            "total_price text," +
            "name text)";
    private static final String SQL_DROP_TABLE_ORDER="drop table "+TABLE_ORDERS;
    private static final String SQL_DROP_TABLE_ADDR="drop table "+TABLE_ADDR;
    private static final String SQL_DROP_TABLE_ITEMS="drop table "+TABLE_ORDER_ITEM;
    private static final String TAG = "DBHelper";
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate: ");
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_ORDER);
        Log.d(TAG, "onCreate: SQL_CREATE_TABLE_ORDER success");
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_ADDR);
        Log.d(TAG, "onCreate: SQL_CREATE_TABLE_ADDR success");
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_ITEMS);
        Log.d(TAG, "onCreate: SQL_CREATE_TABLE_ITEMS success");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DROP_TABLE_ORDER);
        sqLiteDatabase.execSQL(SQL_DROP_TABLE_ADDR);
        sqLiteDatabase.execSQL(SQL_DROP_TABLE_ITEMS);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_ORDER);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_ADDR);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_ITEMS);
    }
}
