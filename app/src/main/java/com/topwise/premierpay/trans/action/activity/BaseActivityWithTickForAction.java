package com.topwise.premierpay.trans.action.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.WindowManager;

import com.topwise.manager.AppLog;
import com.topwise.premierpay.app.BaseActivity;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.core.TransContext;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.utils.TickTimer;

public abstract class BaseActivityWithTickForAction extends BaseActivity {
    private static final String TAG = TopApplication.APPNANE + BaseActivityWithTickForAction.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        tickTimerStart();
        super.onCreate(savedInstanceState);
        TransContext.getInstance().setCurrentContext(this);
        //防止息屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private static TickTimer tickTimer;

    /**
     * 界面超时定时器， 默认超时60秒
     */
    public void tickTimerStart() {
        tickTimerStart(60);
    }

    /**
     * 界面超时定时器
     *
     * @param timOut
     *            ： 单位秒
     */
    public void tickTimerStart(int timOut) {
        if (tickTimer != null)
            tickTimer.cancel();

        tickTimer = new TickTimer(timOut, 1);
        tickTimer.setTimeCountListener(new TickTimer.TickTimerListener() {

            @Override
            public void onTick(long leftTime) {
                AppLog.i(TAG, "onTick:" + leftTime);

                String format = String.format("%02d", leftTime);

                Message m = new Message();
                m.what = TIP_TIME;
                m.obj = format;
                handler.sendMessage(m);
            }

            @Override
            public void onFinish() {
                onTimout();
            }
        });
        tickTimer.start();
    }

    public void tickTimerStop() {
        if (tickTimer != null) {
            tickTimer.cancel();
            tickTimer = null;
        }
    }

    public void onTimout() {
        ActionResult result = new ActionResult(TransResult.ERR_TIMEOUT, null);
        finish(result);
    }

    boolean hasfinish = false;

    public void finish(ActionResult result) {
        if (hasfinish) {
            return;
        }
        hasfinish = true;

        doFinish(result);
    }

    private void doFinish(ActionResult result) {
        tickTimerStop();
        AAction action = TransContext.getInstance().getCurrentAction();
        if (action != null) {
            action.setResult(result);
        } else {
            finish();
        }
    }

    public void finish(boolean hasfinish, ActionResult result) {
        if (hasfinish) {
            return;
        }
        doFinish(result);
    }
}
