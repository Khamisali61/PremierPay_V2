package com.topwise.premierpay.trans.action;

import android.content.Context;

import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.transmit.json.JsonTransmit;
import com.topwise.premierpay.transmit.TransProcessListenerImpl;

public class ActionTransJsonOnline extends AAction {
    /**
     * 子类构造方法必须调用super设置ActionStartListener
     *
     * @param listener {@link ActionStartListener}
     */
    public ActionTransJsonOnline(ActionStartListener listener) {
        super(listener);
    }
    private Context context;
    private TransData transData;

    public void setParam(Context context, TransData transData) {
        this.context = context;
        this.transData = transData;
    }

    @Override
    protected void process() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TransProcessListenerImpl transProcessListenerImpl = new TransProcessListenerImpl();
                int ret = JsonTransmit.getInstance().transmit(transData, transProcessListenerImpl);
                transProcessListenerImpl.onHideProgress();
                setResult(new ActionResult(ret, null));
            }
        }).start();
    }
}
