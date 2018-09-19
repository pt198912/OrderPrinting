package com.order.print.util;

import android.content.Context;
import android.view.WindowManager;

import com.order.print.App;


/**
 * Created by pt198 on 14/08/2018.
 */

public class ScreenUtils {
    public static int dip2px(Context context, float dpValue) {

        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);

    }
    public static int getScreenWidth(){
        WindowManager wm=(WindowManager) App.getInstance().getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }
    public static int getScreenHeight(){
        WindowManager wm=(WindowManager) App.getInstance().getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }
}
