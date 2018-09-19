package com.order.print.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.order.print.bean.Order;
import com.order.print.bean.OrderAddr;
import com.order.print.bean.OrderItem;
import com.order.print.biz.BluetoothBiz;
import com.order.print.biz.OrderPrintBiz;
import com.order.print.database.DbManager;
import com.order.print.service.OrderJobService;
import com.order.print.threadpool.CustomThreadPool;
import com.order.print.util.IntentUtils;

import java.util.ArrayList;
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
        addDbDataForTest();
    }

    private void addDbDataForTest(){
        CustomThreadPool.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                Log.d("pengtao", "run: ");
                for(int i=0;i<20;i++){
                    Order o=new Order();
                    o.setOrder_id(123+i);
                    OrderAddr addr=new OrderAddr();
                    addr.setAddr("asdsa"+i);
                    addr.setMobile("123"+i);
                    addr.setName("pt"+i);
                    addr.setOrderId(123+i);
                    o.setAddr(addr);
                    o.setCreate_time(124);
                    List<OrderItem> items=new ArrayList<>();
                    for(int j=0;j<3;j++){
                        OrderItem item=new OrderItem();
                        item.setName("item"+j);
                        item.setNum(j);
                        item.setOrderId(123+i);
                        items.add(item);
                    }
                    o.setItems(items);
                    Log.d("pengtao", "insertOrder: ");
                    DbManager.getInstance().insertOrder(o);
                }
            }
        });
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


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        //模拟Home键操作
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
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
        IntentUtils.startActivity(this,HistoryOrderListActvity.class);
    }
}
