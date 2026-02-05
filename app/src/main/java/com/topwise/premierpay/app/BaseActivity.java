package com.topwise.premierpay.app;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.view.View;
import android.view.Window;

import com.topwise.manager.AppLog;
import com.topwise.premierpay.trans.model.Device;

public abstract class BaseActivity extends FragmentActivity {
    protected static final int TIP_TIME = 0x99;
    public Context mContext;
    protected UIHandler handler = new UIHandler(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Device.isPhysicalKeyDevice()) {
            Configuration config = getResources().getConfiguration();
            config.smallestScreenWidthDp = 320;
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        }
        super.onCreate(savedInstanceState);
        mContext = this;
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        View view = View.inflate(this, getLayoutId(), null);
//        if (isSetGrayBackground) {
//            view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryBackground));
//        }
        setContentView(view);
        loadParam();
        initViews();
        setListeners();
        ActivityStack.getInstance().push(this);
    }

    /**
     * 初始化控件
     */
    protected abstract void initViews();

    /**
     * 设置监听器
     */
    protected abstract void setListeners();

    /**
     * 加载调用参数
     */
    protected abstract void loadParam();

    /**
     * 获取布局文件ID
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * handler消息处理
     *
     * @param msg
     */
    protected abstract void handleMsg(Message msg);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        TopApplication.isRuning =false;
    }

}
