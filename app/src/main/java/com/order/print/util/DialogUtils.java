package com.order.print.util;

import android.content.Context;

import com.order.print.ui.BaseActivity;
import com.order.print.ui.view.CenterLoadingView;

import java.lang.ref.WeakReference;

/**
 * Created by pt198 on 06/09/2018.
 */

public class DialogUtils {
    private static WeakReference<CenterLoadingView> loading;
    public static void loading(Context context, String title) {
        if(null==loading){
            loading = new WeakReference<>(new CenterLoadingView(context));
        }
        loading.get().setTitle(title);
        loading.get().show();
    }
    public static void dissLoad()  {
        if(loading!=null&&loading.get()!=null){
            loading.get().dismiss();
        }
    }

}
