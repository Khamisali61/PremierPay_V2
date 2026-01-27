package com.topwise.premierpay.trans.action;

import android.content.Context;

import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.transmit.TransOnline;
import com.topwise.premierpay.transmit.TransProcessListenerImpl;

public class ActionParamDownload extends AAction {
    /**
     * 子类构造方法必须调用super设置ActionStartListener
     *
     * @param listener {@link ActionStartListener}
     */
    public ActionParamDownload(ActionStartListener listener) {
        super(listener);
    }
    private Context context;
    private int type;

    public void setParam(Context context) {
        this.context = context;
        this.type = 0;
    }

    public void setParam(Context context,int type) {
        this.context = context;
        this.type = type;
    }

    @Override
    protected void process() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TransProcessListenerImpl iListener = new TransProcessListenerImpl();
                int ret = -1;
                if (type == 0) {
                    ret = TransOnline.posParamDownload(iListener, context);
                } else if (type == 1) {
                    ret = TransOnline.posApplicationDownload(iListener, context);
                }
                setResult(new ActionResult(ret, null));
            }
        }).start();
    }
}
