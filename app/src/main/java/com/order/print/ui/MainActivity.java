package com.order.print.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    //    @BindView(R.id.tv_search_bluetooth)
//    TextView tvSearchBluetooth;
    @BindView(R.id.tv_setting)
    TextView tvSetting;
    @BindView(R.id.tv_order_list)
    TextView tvOrderList;
    @BindView(R.id.tv_start_bluetooth)
    TextView tvStartBluetooth;
    @BindView(R.id.tv_setting)
    TextView tvSetting;
    @BindView(R.id.tv_order_his_list)
    TextView tvOrderHisList;
    //    @BindView(R.id.tv_stop_bluetooth)
//    TextView tvStopBluetooth;
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
        ButterKnife.bind(this);
        if (App.getInstance().isPrintOrderFlag()) {
            tvStartBluetooth.setText("停止服务");
        } else {
            tvStartBluetooth.setText("开始服务");
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            startOrderJobServiceBelowM();
        } else {
            startOrderJobServiceAboveM();
        }


        initBluetoothBiz();

    }

    private void initPrintBiz() {
        OrderPrintBiz.getInstance().init();
    }

    private void initBluetoothBiz() {
        BluetoothBiz.getInstance().init();
    }

    private void showLoadingDlg() {
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

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        //模拟Home键操作
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
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


    @OnClick({R.id.tv_setting, R.id.tv_order_list, R.id.tv_start_bluetooth})
    public void onViewClicked(View view) {
        switch (view.getId()) {
//            case R.id.tv_search_bluetooth:
//                IntentUtils.startActivity(this, BluetoothDeviceListActivity.class);
//                break;
            case R.id.tv_setting:
                IntentUtils.startActivity(this, SettingActivity.class);
                break;
            case R.id.tv_order_list:
                IntentUtils.startActivity(this, OrderListActivity.class);
                break;
            case R.id.tv_start_bluetooth:
                if (App.getInstance().isPrintOrderFlag()) {
                    OrderPrintBiz.getInstance().stopPrintService();
                } else {
                    OrderPrintBiz.getInstance().startPrintService();
                }
                break;
//            case R.id.tv_stop_bluetooth:
//                break;
        }
    }


    private void startOrderJobServiceBelowM() {
        IntentUtils.startService(this, OrderJobService.class);
    }

    private void startOrderJobServiceAboveM() {
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
        Intent intent = new Intent(this, PlayerMusicService.class);
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
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
        }
        return super.onKeyDown(keyCode, event);
    }


    @OnClick(R.id.tv_order_his_list)
    public void onViewClicked() {
        IntentUtils.startActivity(this,His);
    }
}
