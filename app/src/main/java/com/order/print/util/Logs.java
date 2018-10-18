package com.order.print.util;

import android.os.Environment;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by sunxx on 2017/12/14.
 * 日志文件
 */
public  class Logs {
    public static String TIRE_TAG="1";
    public static String PHY_TAG="2";
    public static String STATE_TAG="3";
    public static boolean isDebug=false;
    public final static String FILE_PATH = "/aa/bb";//包名文件夹
    public static File file;
    public  static FileWriter fos;
    public static Logs logs;
    public Logs() {
    }
    public static synchronized Logs getInstance(){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()  + "/log.txt";
        try {
                file = new File(path);
                if(!file.exists()){
                    file.createNewFile();
                }
                fos = new FileWriter(file, true);
           
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (logs==null){
            logs=new Logs();
        }
        return logs;
    }
    private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //写入日志文件
    public  void writeEvent(String tag,String msg){
        if (!isDebug){
            return;
        }
        if (fos==null){
            return;
        }
        try
        {
            fos.write((sdf.format(System.currentTimeMillis())+"|"+tag+"|"+msg+"\r"));
            fos.flush();
            fos.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
