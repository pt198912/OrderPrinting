package com.order.print.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.order.print.App;

/**
 * Created by pt198 on 18/09/2018.
 */

public class OrderContentProvider extends ContentProvider {
    private static final UriMatcher matcher;
    private DBHelper helper;
    private SQLiteDatabase db;

    private static final String AUTHORITY = "com.order.print.db";
    private static final int TYPE_ALL = 0;
    private static final int TYPE_ONE = 1;
//    private static final int ADDR_ALL = 2;
//    private static final int ADDR_ONE = 3;
//    private static final int ITEM_ALL = 4;
//    private static final int ITEM_ONE = 5;
    private static final int ORDER_URI_CODE = 3;
    private static final int ADDR_URI_CODE = 4;
    private static final int ORDER_ITEM_URI_CODE =5;
    private static final int ORDER_URI_CODE_SINGLE = 6;
    private static final int ADDR_URI_CODE_SINGLE = 7;
    private static final int ORDER_ITEM_URI_CODE_SINGLE =8;
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.order.print";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.order.print";
    //数据改变后立即重新查询
    public static final Uri NOTIFY_URI = Uri.parse("content://" + AUTHORITY + "/");
    public static final Uri ORDER_URI = Uri.parse("content://" + AUTHORITY + "/"+DBHelper.TABLE_ORDERS);
    public static final Uri ADDR_URI = Uri.parse("content://" + AUTHORITY + "/"+DBHelper.TABLE_ADDR);
    public static final Uri ORDER_ITEM_URI = Uri.parse("content://" + AUTHORITY + "/"+DBHelper.TABLE_ORDER_ITEM);
    private static final String TAG = "OrderContentProvider";
    private String getTableName(Uri uri) {
        String tableName = "";
        switch (matcher.match(uri)) {
            case ORDER_URI_CODE:
                tableName = DBHelper.TABLE_ORDERS;
                break;
            case ADDR_URI_CODE:
                tableName = DBHelper.TABLE_ADDR;
                break;
            case ORDER_ITEM_URI_CODE:
                tableName = DBHelper.TABLE_ORDER_ITEM;
                break;
        }
        return tableName;
    }

    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, DBHelper.TABLE_ORDERS, ORDER_URI_CODE);  //匹配记录集合
        matcher.addURI(AUTHORITY, DBHelper.TABLE_ORDERS+"/#", ORDER_URI_CODE_SINGLE); //匹配单条记录
        matcher.addURI(AUTHORITY, DBHelper.TABLE_ADDR, ADDR_URI_CODE);  //匹配记录集合
        matcher.addURI(AUTHORITY, DBHelper.TABLE_ADDR+"/#", ADDR_URI_CODE_SINGLE); //匹配单条记录
        matcher.addURI(AUTHORITY, DBHelper.TABLE_ORDER_ITEM, ORDER_ITEM_URI_CODE);  //匹配记录集合
        matcher.addURI(AUTHORITY, DBHelper.TABLE_ORDER_ITEM+"/#", ORDER_ITEM_URI_CODE_SINGLE); //匹配单条记录
    }

    @Override
    public boolean onCreate() {
        helper = new DBHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        int match = matcher.match(uri);
        switch (match) {
            case ORDER_URI_CODE:
            case ORDER_ITEM_URI_CODE:
            case ADDR_URI_CODE:
                return CONTENT_TYPE;
            case ORDER_URI_CODE_SINGLE:
            case ORDER_ITEM_URI_CODE_SINGLE:
            case ADDR_URI_CODE_SINGLE:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        db = helper.getReadableDatabase();
        int match = matcher.match(uri);
        String table=getTableName(uri);
        switch (match) {
            case ORDER_URI_CODE:
            case ORDER_ITEM_URI_CODE:
            case ADDR_URI_CODE:
                selection=null;
                selectionArgs=null;
                break;
            case ORDER_URI_CODE_SINGLE:
            case ORDER_ITEM_URI_CODE_SINGLE:
            case ADDR_URI_CODE_SINGLE:
                long _id = ContentUris.parseId(uri);
                selection = "_id = ?";
                selectionArgs = new String[]{String.valueOf(_id)};
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return db.query(table, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        String table=getTableName(uri);
        Log.d(TAG, "insert: table "+table);
        if (TextUtils.isEmpty(table)) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = helper.getWritableDatabase();
        long rowId = db.insert(table, null, values);
        Log.d(TAG, "insert: rowId "+rowId);
        if (rowId > 0) {
            notifyDataChanged(table);
            return ContentUris.withAppendedId(uri, rowId);
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        db = helper.getWritableDatabase();
        int match = matcher.match(uri);
        String table=getTableName(uri);
        switch (match) {
            case ORDER_URI_CODE:
            case ORDER_ITEM_URI_CODE:
            case ADDR_URI_CODE:
                selection=null;
                selectionArgs=null;
                break;
            case ORDER_URI_CODE_SINGLE:
            case ORDER_ITEM_URI_CODE_SINGLE:
            case ADDR_URI_CODE_SINGLE:
                long _id = ContentUris.parseId(uri);
                selection = "_id = ?";
                selectionArgs = new String[]{String.valueOf(_id)};
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        int count = db.delete(table, selection, selectionArgs);
        if (count > 0) {
            notifyDataChanged(table);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        db = helper.getWritableDatabase();
        int match = matcher.match(uri);
        String table=getTableName(uri);
        switch (match) {
            case ORDER_URI_CODE:
            case ORDER_ITEM_URI_CODE:
            case ADDR_URI_CODE:
                selection=null;
                selectionArgs=null;
                break;
            case ORDER_URI_CODE_SINGLE:
            case ORDER_ITEM_URI_CODE_SINGLE:
            case ADDR_URI_CODE_SINGLE:
                long _id = ContentUris.parseId(uri);
                selection = "_id = ?";
                selectionArgs = new String[]{String.valueOf(_id)};
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        int count = db.update(table, values, selection, selectionArgs);
        if (count > 0) {
            notifyDataChanged(table);
        }
        return count;
    }

    //通知指定URI数据已改变
    private void notifyDataChanged(String table) {
        getContext().getContentResolver().notifyChange(Uri.withAppendedPath(NOTIFY_URI,table), null);
    }

}
