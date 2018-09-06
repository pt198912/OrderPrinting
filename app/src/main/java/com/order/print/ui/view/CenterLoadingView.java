package com.order.print.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.order.print.R;


public class CenterLoadingView extends Dialog {



    private ImageView ivImage;
    private TextView tvMsg;
    private Context context;

    private Animation animation;



    public CenterLoadingView(Context context) {
        super(context, R.style.loading_dialog);
        this.context = context;
        init();
    }

    private void init() {
        setContentView(R.layout.common_dialog_loading_layout);

        tvMsg = findViewById(R.id.tvMsg);
        ivImage = findViewById(R.id.ivImage);

        initAnim();
    }

    private void initAnim() {
        animation = AnimationUtils.loadAnimation(context, R.anim.loading_anim);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);
    }




    @Override
    public void show() {
        super.show();
        ivImage.startAnimation(animation);
    }

    @Override
    public void hide() {
        super.hide();

    }

    @Override
    public void dismiss() {
        super.dismiss();
        ivImage.clearAnimation();
    }


    @Override
    public void setTitle(CharSequence title) {
        if (!TextUtils.isEmpty(title) && null!=tvMsg) {
            tvMsg.setText(title);
        }
    }


    public static void dismissDialog(CenterLoadingView loadingDialog) {
        if (null == loadingDialog) { return; }
        loadingDialog.dismiss();
    }


}