package com.topwise.premierpay.app;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public class UIHandler extends Handler {
    WeakReference<BaseActivity> softReference;

    public UIHandler(BaseActivity activity) {
        softReference = new WeakReference<BaseActivity>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        BaseActivity activity = softReference.get();
        if (activity != null) {
            activity.handleMsg(msg);
        }
    }
}

