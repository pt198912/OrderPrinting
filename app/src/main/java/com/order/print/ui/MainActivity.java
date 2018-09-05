package com.order.print.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jiangdg.keepappalive.receiver.ScreenReceiverUtil;
import com.jiangdg.keepappalive.service.DaemonService;
import com.jiangdg.keepappalive.service.PlayerMusicService;
import com.jiangdg.keepappalive.utils.HwPushManager;
import com.jiangdg.keepappalive.utils.JobSchedulerManager;
import com.jiangdg.keepappalive.utils.ScreenManager;
import com.order.print.App;
import com.order.print.R;
import com.order.print.bean.BluetoothBean;
import com.order.print.biz.BluetoothBiz;
import com.order.print.biz.OrderPrintBiz;
import com.order.print.service.OrderJobService;
import com.order.print.util.IntentUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BluetoothBiz.OnBluetoothStateListener{
    // JobService，执行系统任务
    private JobSchedulerManager mJobManager;
    // 1像素Activity管理类
    private ScreenManager mScreenManager;
    // 动态注册锁屏等广播
    private ScreenReceiverUtil mScreenListener;
    // 华为推送管理类
    private HwPushManager mHwPushManager;
    ProgressDialog pdSearch;
    List<BluetoothBean> mBluetoothList;
    PopupWindow pw;
    ProgressDialog pdConnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT<=Build.VERSION_CODES.M) {
            startOrderJobServiceBelowM();
        }else{
            startOrderJobServiceAboveM();
        }
    }
    private void showLoadingDlg(){
        pdSearch = ProgressDialog.show(this, "", "连接中", true, true);
        pdSearch.setCanceledOnTouchOutside(false);
        pdSearch.show();
    }
    private ScreenReceiverUtil.SreenStateListener mScreenListenerer = new ScreenReceiverUtil.SreenStateListener() {
        @Override
        public void onSreenOn() {
            // 亮屏，移除"1像素"
            mScreenManager.finishActivity();
        }

        @Override
        public void onSreenOff() {
            // 接到锁屏广播，将SportsActivity切换到可见模式
            // "咕咚"、"乐动力"、"悦动圈"就是这么做滴
//            Intent intent = new Intent(SportsActivity.this,SportsActivity.class);
//            startActivity(intent);
            // 如果你觉得，直接跳出SportActivity很不爽
            // 那么，我们就制造个"1像素"惨案
            mScreenManager.startActivity();
        }

        @Override
        public void onUserPresent() {
            // 解锁，暂不用，保留
        }
    };
    private void showSuccessDialog() {
        pdSearch.dismiss();
        DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Dialog.BUTTON_POSITIVE:
                        OrderPrintBiz.getInstance().printOrder();
                        break;
                    case Dialog.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("提示");
        builder.setMessage("连接成功，是否开始打印?");
        builder.setPositiveButton("确定", mOnClickListener);
        builder.setNegativeButton("取消", mOnClickListener);
        builder.create().show();
    }

    private void showErrorDialog() {
        pdSearch.dismiss();
        DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Dialog.BUTTON_POSITIVE:
                        showLoadingDlg();
                        BluetoothBiz.getInstance().searchBlueToothDevice(MainActivity.this);
                        break;
                    case Dialog.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("提示");
        builder.setMessage("连接失败，是否重试?");
        builder.setPositiveButton("确定", mOnClickListener);
        builder.setNegativeButton("取消", mOnClickListener);
        builder.create().show();
    }

    private void showBluetoothPop(final List<BluetoothBean> bluetoothList) {

        View view = LayoutInflater.from(this).inflate(R.layout.layout_bluetooth, null);
        ListView mListView = view.findViewById(R.id.lv_bluetooth);
        MyBluetoothAdapter myBluetoothAdapter = new MyBluetoothAdapter();
        mListView.setAdapter(myBluetoothAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (0 != mBluetoothList.size()) {
                    closePopupWindow();
                    pdConnect = ProgressDialog.show(MainActivity.this, "", "开始连接", true, true);
                    pdConnect.setCanceledOnTouchOutside(false);
                    pdConnect.show();
                    BluetoothBiz.getInstance().connect(bluetoothList.get(position).mBluetoothAddress, bluetoothList.get(position).mBluetoothDevice);
                }
            }
        });
        pw = new PopupWindow(view, (int) (getScreenWidth(this) * 0.8), -2);
        closePopupWindow();
        pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pw.setOutsideTouchable(true);
        pw.setFocusable(true);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
        pw.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        pw.setAnimationStyle(R.style.PopAnim);
        //显示
        pw.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    private void closePopupWindow() {
        if (pw != null && pw.isShowing()) {
            pw.dismiss();
            pw = null;
        }
    }

    public int getScreenWidth(Activity act) {
        DisplayMetrics dm = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    @Override
    public void onDiscoveryFinish(ArrayList<BluetoothBean> datas) {
        mBluetoothList.clear();
        mBluetoothList.addAll(datas);
        showBluetoothPop(datas);
    }

    class MyBluetoothAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mBluetoothList.size();
        }

        @Override
        public Object getItem(int position) {
            return mBluetoothList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(App.getInstance()).inflate(R.layout.item_bluetooth, parent, false);
                holder = new ViewHolder();
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.item_text = convertView.findViewById(R.id.item_text);
            holder.item_text_address = convertView.findViewById(R.id.item_text_address);
            holder.item_text.setText(mBluetoothList.get(position).mBluetoothName);
            holder.item_text_address.setText(mBluetoothList.get(position).mBluetoothAddress);
            return convertView;
        }

        class ViewHolder {
            TextView item_text;
            TextView item_text_address;
        }
    }

    private void startOrderJobServiceBelowM(){
        IntentUtils.startService(this, OrderJobService.class);
    }

    private void startOrderJobServiceAboveM(){
        // 1. 注册锁屏广播监听器
        mScreenListener = new ScreenReceiverUtil(this);
        mScreenManager = ScreenManager.getScreenManagerInstance(this);
        mScreenListener.setScreenReceiverListener(mScreenListenerer);
        // 2. 启动系统任务
        mJobManager = JobSchedulerManager.getJobSchedulerInstance(this);
        mJobManager.startJobScheduler();
        // 3. 华为推送保活，允许接收透传
        mHwPushManager = HwPushManager.getInstance(this);
        mHwPushManager.startRequestToken();
        mHwPushManager.isEnableReceiveNormalMsg(true);
        mHwPushManager.isEnableReceiverNotifyMsg(true);
        //  启动前台Service
        startDaemonService();
        //  启动播放音乐Service
        startPlayMusicService();
    }
    private void stopPlayMusicService() {
        Intent intent = new Intent(this, PlayerMusicService.class);
        stopService(intent);
    }

    private void startPlayMusicService() {
        Intent intent = new Intent(this,PlayerMusicService.class);
        startService(intent);
    }

    private void startDaemonService() {
        Intent intent = new Intent(this, DaemonService.class);
        startService(intent);
    }

    private void stopDaemonService() {
        Intent intent = new Intent(this, DaemonService.class);
        stopService(intent);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 禁用返回键
        if(keyCode == KeyEvent.KEYCODE_BACK){
            moveTaskToBack(false);
        }
        return super.onKeyDown(keyCode, event);
    }
}
