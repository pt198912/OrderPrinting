package com.order.print.biz;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.order.print.App;
import com.order.print.ui.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import am.example.printer.util.StringUtils;

public class UpdateApk {
    private String mSavedPath=Environment.getExternalStorageDirectory() + "/";
    public void downloadApk(Context context, String url)
    {
        if(StringUtils.isNullOrEmpty(url)){
            Toast.makeText(context, "下载地址不正确，无法更新apk", Toast.LENGTH_SHORT).show();
            return;
        }
        mDownloadDialog=new ProgressDialog(context);
        mDownloadDialog.setMessage("下载中...%0");
        mDownloadDialog.setCancelable(true);
        if(!mDownloadDialog.isShowing()) {
            mDownloadDialog.show();
        }
        // 启动新线程下载软件
        new DownloadApkThread(url).start();
    }

//    private ProgressBar mProgress;
    private ProgressDialog mDownloadDialog;
    /* 下载中 */
    private static final int DOWNLOAD = 1;
    /* 下载结束 */
    private static final int DOWNLOAD_FINISH = 2;
    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                // 正在下载
                case DOWNLOAD:
                    // 设置进度条位置
                    mDownloadDialog.setMessage("下载中..."+(int)msg.obj+"%");
                    break;
                case DOWNLOAD_FINISH:
                    // 安装文件
                    mDownloadDialog.dismiss();
                    installApk(App.getInstance());

                    break;
                default:
                    break;
            }
        };
    };
    /**
     * 下载文件线程
     *
     * @author coolszy
     *@date 2012-4-26
     *@blog http://blog.92coding.com
     */
    private static final String TAG = "UpdateApk";
    private class DownloadApkThread extends Thread
    {
        private String mUrl;
        public DownloadApkThread(String url){
            this.mUrl=url;
        }
        @Override
        public void run()
        {
            try
            {
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                {
                    Log.d(TAG, "run: start");
                    // 获得存储卡的路径
                    String sdpath = Environment.getExternalStorageDirectory() + "/";
                    mSavedPath = sdpath;
                    URL url = new URL(mUrl);
                    // 创建连接
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    Log.d(TAG, "run: connect");
                    // 获取文件大小
                    int length = conn.getContentLength();
                    Log.d(TAG, "run: length "+length);
                    // 创建输入流
                    InputStream is = conn.getInputStream();

                    File file = new File(mSavedPath);
                    // 判断文件目录是否存在
                    if (!file.getParentFile().exists())
                    {
                        file.getParentFile().mkdirs();
                    }
                    if(!file.exists()){
                        file.createNewFile();
                    }
                    File apkFile = new File(mSavedPath, "orderPrint.apk");
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    // 缓存
                    byte buf[] = new byte[1024*40];
                    // 写入到文件中
                    do
                    {
                        int numread = is.read(buf);
                        count += numread;
                        Log.d(TAG, "run: read "+numread);
                        // 计算进度条位置
                        int progress = (int) (((float) count / length) * 100);
                        // 更新进度
                        Message.obtain(mHandler,DOWNLOAD,progress).sendToTarget();
                        if (numread <= 0)
                        {
                            // 下载完成
                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                            break;
                        }
                        // 写入文件
                        fos.write(buf, 0, numread);
                    } while (true);// 点击取消就停止下载.
                    fos.close();
                    is.close();
                }
            } catch (MalformedURLException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            // 取消下载对话框显示
            mDownloadDialog.dismiss();
        }
    };

    /**
     * 安装APK文件
     */
    private void installApk(Context context)
    {
        File apkfile = new File(mSavedPath, "orderPrint.apk");
        if (!apkfile.exists())
        {
            return;
        }

        // 通过Intent安装APK文件
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri uriForFile = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", apkfile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uriForFile, context.getContentResolver().getType(uriForFile));
        }else {
            intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);


    }

}
