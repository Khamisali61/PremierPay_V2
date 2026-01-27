package com.topwise.premierpay.view;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;

import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;

public class TopToast extends Toast {
    @ColorInt
    private static final int ERROR_COLOR = Color.parseColor("#FD4C5B");

    @ColorInt
    private static final int INFO_COLOR = Color.parseColor("#3F51B5");

    @ColorInt
    private static final int SUCCESS_COLOR = Color.parseColor("#388E3C");

    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */
    public TopToast(Context context) {
        super(context);
    }

    public static void  showNormalToast(Context context, String content) {
        // 获取系统的LayoutInflater
        prepare();

        LayoutInflater inflater = (LayoutInflater) TopApplication.mApp.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.top_toast_layout, null);


        TextView tv_content = (TextView)view.findViewById(R.id.tv_content);
        tv_content.setText(content);
        tv_content.setBackgroundResource(R.drawable.toast_normal_bg);
        // 实例化toast
        Toast  mToast = new Toast(TopApplication.mApp);

        mToast.setView(view);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER,0,0);
        mToast.show();
        loop();
    }

    public static void  showFailToast(Context context, String content) {
        // 获取系统的LayoutInflater
        prepare();

        LayoutInflater inflater = (LayoutInflater)TopApplication.mApp.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.top_toast_layout, null);

        TextView tv_content = (TextView)view.findViewById(R.id.tv_content);
        tv_content.setText(content);
        tv_content.setBackgroundResource(R.drawable.toast_fail_bg);
        //实例化toast
        Toast  mToast = new Toast(TopApplication.mApp);
        mToast.setView(view);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER,0,0);
        mToast.show();
        loop();
    }

    public static void  showScuessToast(String content){
        // 获取系统的LayoutInflater
        prepare();
        LayoutInflater inflater = (LayoutInflater)TopApplication.mApp.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.top_toast_layout, null);


        TextView tv_content = (TextView)view.findViewById(R.id.tv_content);
        tv_content.setText(content);
        tv_content.setBackgroundResource(R.drawable.toast_scuess_bg);
        // 实例化toast
        Toast  mToast = new Toast(TopApplication.mApp);
        mToast.setView(view);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER,0,0);
        mToast.show();
        loop();
    }

    private static  void prepare() {
        if (!isMainThread()) {
            Looper.prepare();
        }
    }

    private static void loop() {
       if (!isMainThread()) {
            Looper.loop();
        }
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }
}
