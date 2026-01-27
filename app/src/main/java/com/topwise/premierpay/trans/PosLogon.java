package com.topwise.premierpay.trans;

import android.content.Context;
import android.os.Handler;

import com.topwise.manager.AppLog;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.transmit.TransOnline;
import com.topwise.premierpay.transmit.TransProcessListenerImpl;

public class PosLogon extends BaseTrans {
    private static final String TAG = TopApplication.APPNANE + PosLogon.class.getSimpleName();

    public PosLogon(Context context, Handler handler, TransEndListener transListener) {
        super(context, handler, ETransType.LOGON, transListener);
    }

    @Override
    public void onActionResult(String currentState, ActionResult result) {

    }

    @Override
    protected void bindStateOnAction() {
        AppLog.i(TAG,"bindStateOnAction");
        new Thread(new Runnable() {
            @Override
            public void run() {
                TransProcessListenerImpl processListener = new TransProcessListenerImpl();
                int ret = TransOnline.posLogon(processListener, getCurrentContext());
                if (ret != TransResult.SUCC) {
                    processListener.onHideProgress();
                    transEnd(new ActionResult(ret, null));
                    return;
                }
                ret = TransOnline.downLoadCheck(false, true, processListener);
                processListener.onHideProgress();
                transEnd(new ActionResult(ret, null));
            }
        }).start();
    }
}
