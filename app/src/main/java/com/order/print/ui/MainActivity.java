package com.order.print.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.drm.ProcessedData;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jiangdg.keepappalive.receiver.ScreenReceiverUtil;
import com.jiangdg.keepappalive.service.DaemonService;
import com.jiangdg.keepappalive.service.PlayerMusicService;
import com.jiangdg.keepappalive.utils.HwPushManager;
import com.jiangdg.keepappalive.utils.JobSchedulerManager;
import com.jiangdg.keepappalive.utils.ScreenManager;
import com.order.print.App;
import com.order.print.R;
import com.order.print.bean.AppConfig;
import com.order.print.bean.BluetoothBean;
import com.order.print.bean.Order;
import com.order.print.bean.OrderAddr;
import com.order.print.bean.OrderItem;
import com.order.print.biz.BluetoothBiz;
import com.order.print.biz.BluetoothInfoManager;
import com.order.print.biz.OrderPrintBiz;
import com.order.print.biz.UpdateApk;
import com.order.print.database.DbManager;
import com.order.print.net.MyException;
import com.order.print.net.MyResponse;
import com.order.print.net.MyResponseCallback;
import com.order.print.player.VoicePlayerManager;
import com.order.print.service.OrderJobService;
import com.order.print.service.PrintService;
import com.order.print.threadpool.CustomThreadPool;
import com.order.print.util.DesUtils;
import com.order.print.util.HttpUtils;
import com.order.print.util.IntentUtils;
import com.order.print.util.LogUtil;
import com.order.print.util.Logs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import am.example.printer.util.FileUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.order.print.player.VoicePlayerManager.VOICE_BLUE_DISCONN;
import static com.order.print.player.VoicePlayerManager.VOICE_NEW_ORDER;

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
    @BindView(R.id.tv_test_print)
    TextView tvTestPrint;
    @BindView(R.id.tv_new_order)
    TextView tvNewOrder;
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
    private static final String TAG = "MainActivity";

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
        getAppConfig(true);

//        startTimer();
//        addDbDataForTest();
//        updateData();
    }

    public boolean checkPermission(AppCompatActivity activity) {
        boolean isGranted = true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
//            if (activity.checkSelfPermission(Manifest.permission.REQUEST_INSTALL_PACKAGES) != PERMISSION_GRANTED) {
//                isGranted = false;
//            }
            if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                //如果没有写sd卡权限
                isGranted = false;
            }
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
                isGranted = false;
            }
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
                isGranted = false;
            }

            if (!isGranted) {
                activity.requestPermissions(
                        new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                                Manifest.permission.READ_PHONE_STATE,
//                                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
//                                Manifest.permission.VIBRATE,
//                                Manifest.permission.RECORD_AUDIO,
//                                Manifest.permission.CALL_PHONE,

//                                Manifest.permission.CAMERA
                        },
                        102);
            }else{

                boolean haveInstallPermission = getPackageManager().canRequestPackageInstalls();
                if (haveInstallPermission) {
                    UpdateApk apk=new UpdateApk();
                    apk.downloadApk(MainActivity.this,mUpdatePkg);
                } else {
                    startInstallPermissionSettingActivity();
                }
            }
        }else{
            UpdateApk apk=new UpdateApk();
            apk.downloadApk(MainActivity.this,mUpdatePkg);
        }
        return isGranted;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity() {
        Log.d(TAG, "startInstallPermissionSettingActivity: ");
        Uri packageURI = Uri.parse("package:" + getPackageName());
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        startActivityForResult(intent, 103);
    }

    /**
     * 申请权限回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 102:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    UpdateApk apk=new UpdateApk();
                    apk.downloadApk(MainActivity.this,mUpdatePkg);
                } else {
                    //  引导用户手动开启安装权限
                    startInstallPermissionSettingActivity();
                    Toast.makeText(this, "请先授予读取sd卡权限和安装权限，才能完成apk更新", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 103:
                Log.d(TAG, "onActivityResult: resultCode "+resultCode);
                boolean haveInstallPermission = getPackageManager().canRequestPackageInstalls();
                Log.d(TAG, "checkPermission: haveInstallPermission"+haveInstallPermission);
                if (haveInstallPermission) {
                    UpdateApk apk = new UpdateApk();
                    apk.downloadApk(MainActivity.this, mUpdatePkg);
                } else {
                    //  引导用户手动开启安装权限
                    startInstallPermissionSettingActivity();
                    Toast.makeText(this, "请先授予安装权限，才能完成apk更新", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    AlertDialog mDlg;
    private String mExpireTime="2018-11-17 00:00:00";
    private String mUpdatePkg;
    private void getAppConfig(final boolean firstInit){
        final ProgressDialog dlg=showLoadingDlg();
        dlg.setCancelable(false);
        HttpUtils.getAppConfig(new MyResponseCallback<AppConfig>() {
            @Override
            public void onSuccess(final AppConfig data) {
                dlg.dismiss();
                if(data!=null) {
                   LogUtil.logPrintHttpInfo(data.getSystemImage()+","+data.getPackage());
                    App.getInstance().setQueryOrderDuration((int) data.getApiInterval());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String expireTime=data.getSystemImage();

                    Date date =null;
                    try {
                        String decryptStr=DesUtils.DecodeDES(expireTime,DesUtils.password);
                        Log.d(TAG, "onSuccess: decryptStr "+decryptStr);
                        date=(Date) sdf.parse(decryptStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if(date!=null&&date.getTime()<=System.currentTimeMillis()){
                        mDlg=new AlertDialog.Builder(MainActivity.this).setMessage("软件试用期结束，请联系管理员").setPositiveButton("更新", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                System.exit(0);
                            }
                        }).show();
                        mDlg.setCancelable(false);
                        return;
                    }
                    PackageInfo packageInfo= null;
                    try {
                        packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);
                        int versionCode=packageInfo.versionCode;
                        if(data!=null){
                            Log.d(TAG, "onSuccess: versionCOde "+versionCode+","+data.getSmallVar()+","+data.getPackage());
                            if(data.getSmallVar()>versionCode){
                                mDlg=new AlertDialog.Builder(MainActivity.this).setMessage("有新的版本").setPositiveButton("更新", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mUpdatePkg=data.getPackage();
                                        checkPermission(MainActivity.this);

                                    }
                                }).show();
                            }
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }



                }
                if(firstInit) {
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                        startOrderJobServiceBelowM();
                    } else {
                        startOrderJobServiceAboveM();
                    }

                    initBluetoothBiz();
                }else{
                    OrderPrintBiz.getInstance().startPrintService();
                    tvStartBluetooth.setText("停止服务");
                }
            }

            @Override
            public void onSuccessList(List<AppConfig> data) {

            }

            @Override
            public void onFailure(MyException e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
                LogUtil.logPrintHttpInfo(e.getCause()+","+e.getMessage()+","+e.getCode());
//                Logs.getInstance().writeEvent("http_error",e.getMessage()+",code "+e.getCode());
                dlg.dismiss();
                mDlg=new AlertDialog.Builder(MainActivity.this).setMessage("获取配置失败，程序将在获取配置成功后启动").setPositiveButton("重试", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getAppConfig(firstInit);
                    }
                }).show();
                mDlg.setCancelable(false);
            }
        },AppConfig.class);
    }


    private void updateData() {
//        for(int i=481;i<=487;i++) {
        HttpUtils.resetOrderStatus("847", "0", new MyResponseCallback<MyResponse>() {
            @Override
            public void onSuccess(MyResponse data) {

            }

            @Override
            public void onSuccessList(List<MyResponse> data) {
                Log.d(TAG, "onSuccessList: ");
            }

            @Override
            public void onFailure(MyException e) {
                Log.d(TAG, "onFailure: ");
            }
        }, MyResponse.class);
//        }
    }

    private void addDbDataForTest() {
        CustomThreadPool.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                Log.d("pengtao", "run: ");
                for (int i = 0; i < 20; i++) {
                    Order o = new Order();
                    o.setOrder_id(123 + i);
                    OrderAddr addr = new OrderAddr();
                    addr.setAddr("asdsa" + i);
                    addr.setMobile("123" + i);
                    addr.setName("pt" + i);
                    addr.setOrderId(123 + i);
                    o.setAddr(addr);
                    o.setCreate_time(124);
                    List<OrderItem> items = new ArrayList<>();
                    for (int j = 0; j < 3; j++) {
                        OrderItem item = new OrderItem();
                        item.setName("item" + j);
                        item.setNum(j);
                        item.setOrderId(123 + i);
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

    private ProgressDialog showLoadingDlg() {
        pdSearch = ProgressDialog.show(this, "", "连接中", true, true);
        pdSearch.setCanceledOnTouchOutside(false);
        pdSearch.show();
        return pdSearch;
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

    private Order createNewOrder(int i){
        Order o = new Order();
        o.setOrder_id(123 + i);
        OrderAddr addr = new OrderAddr();
        addr.setAddr("asdsa" + i);
        addr.setMobile("123" + i);
        addr.setName("pt" + i);
        addr.setOrderId(123 + i);
        o.setAddr(addr);
        o.setCreate_time(124);
        List<OrderItem> items = new ArrayList<>();
        for (int j = 0; j < 3; j++) {
            OrderItem item = new OrderItem();
            item.setName("item" + j);
            item.setNum(j);
            item.setOrderId(123 + i);
            items.add(item);
        }
        o.setItems(items);
        return o;
    }

    private Timer mTimer;
    private void startTimer(){
        if(mTimer!=null){
            mTimer.cancel();
        }
        mTimer=new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
//                OrderPrintBiz.getInstance().addNewOrder(createNewOrder(1));
                VoicePlayerManager.getInstance().playVoice(VOICE_BLUE_DISCONN);
                VoicePlayerManager.getInstance().playVoice(VOICE_NEW_ORDER);
            }
        };
        mTimer.schedule(task,0,300);
    }

    @OnClick({R.id.tv_setting, R.id.tv_order_list, R.id.tv_start_bluetooth,R.id.tv_test_print,R.id.tv_new_order})
    public void onViewClicked(View view) {
        switch (view.getId()) {
//            case R.id.tv_search_bluetooth:
//                IntentUtils.startActivity(this, BluetoothDeviceListActivity.class);
//                break;
            case R.id.tv_test_print:
                if(BluetoothInfoManager.getInstance().getConnectedBluetooth()==null||!BluetoothInfoManager.getInstance().isConnected()){
                    VoicePlayerManager.getInstance().playVoice(VOICE_BLUE_DISCONN);
                    Toast.makeText(this, "蓝牙未连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                OrderPrintBiz.getInstance().addNewOrder(createNewOrder(0));
                break;
            case R.id.tv_new_order:
//                startTimer();
                break;
            case R.id.tv_setting:
                IntentUtils.startActivity(this, SettingActivity.class);
                break;
            case R.id.tv_order_list:
                IntentUtils.startActivity(this, OrderListActivity.class);
                break;
            case R.id.tv_start_bluetooth:
                if (App.getInstance().isPrintOrderFlag()) {
                    OrderPrintBiz.getInstance().stopPrintService();
                    tvStartBluetooth.setText("开始服务");

                } else {
                    getAppConfig(false);
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
//        mScreenListener.setScreenReceiverListener(mScreenListenerer);
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
        IntentUtils.startService(this, PrintService.class);
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
        IntentUtils.startActivity(this, HistoryOrderListActvity.class);
    }


}
