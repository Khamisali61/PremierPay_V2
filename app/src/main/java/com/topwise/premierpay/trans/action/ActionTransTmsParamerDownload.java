package com.topwise.premierpay.trans.action;

import android.content.Context;

import com.topwise.premierpay.tms.TmsParamDownload;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.transmit.TransProcessListenerImpl;

/**
 * 创建日期：2021/5/12 on 10:46
 * 描述:
 * 作者:wangweicheng
 */
public class ActionTransTmsParamerDownload extends AAction {
    /**
     * 子类构造方法必须调用super设置ActionStartListener
     *
     * @param listener {@link ActionStartListener}
     */
    public ActionTransTmsParamerDownload(ActionStartListener listener) {
        super(listener);
    }
    private Context context;


    public void setParam(Context context) {
        this.context = context;

    }
    @Override
    protected void process() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TransProcessListenerImpl transProcessListener = new TransProcessListenerImpl();
                transProcessListener.onUpdateProgressTitle("TMS parameter download");
                transProcessListener.onShowProgress("Receiving...",30);
                TmsParamDownload tmsParamDownload = new TmsParamDownload();
                String data = tmsParamDownload.downloadParam();
                transProcessListener.onHideProgress();
                setResult(new ActionResult(TransResult.SUCC, data));
            }
        }).start();
    }
}
