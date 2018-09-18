package com.order.print.threadpool;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by pt198 on 18/09/2018.
 */

public class CustomThreadPool {
    private ExecutorService mExecutor;
    private static final int MAX_THREAD=4;
    private static class SingletonInstance{
        private static final CustomThreadPool INSTANCE=new CustomThreadPool();
    }
    private CustomThreadPool(){
        mExecutor= Executors.newFixedThreadPool(MAX_THREAD);
    }
    public static CustomThreadPool getInstance(){
        return SingletonInstance.INSTANCE;
    }
    public void submit(Runnable run){
        mExecutor.submit(run);
    }
}
