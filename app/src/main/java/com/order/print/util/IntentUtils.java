package com.order.print.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import static com.order.print.util.Constants.BUNDLE_KEY_EXTRA;

/**
 * Created by pt198 on 03/09/2018.
 */

public class IntentUtils {
    public static void startActivity(Context cxt, Class<?> cls){
        startActivity(cxt,cls,null);
    }
    public static void startActivity(Context cxt, Class<?> cls, Bundle bundle){
        if(cxt==null||cls==null){
            return;
        }
        Intent intent=new Intent(cxt,cls);
        if(bundle!=null) {
            intent.putExtra(BUNDLE_KEY_EXTRA, bundle);
        }
        cxt.startActivity(intent);
    }
    public static void startService(Context cxt, Class<?> cls){
        startService(cxt,cls,null);
    }
    public static void startService(Context cxt, Class<?> cls, Bundle bundle){
        if(cxt==null||cls==null){
            return;
        }
        Intent intent=new Intent(cxt,cls);
        if(bundle!=null) {
            intent.putExtra(BUNDLE_KEY_EXTRA, bundle);
        }
        cxt.startService(intent);
    }

}
