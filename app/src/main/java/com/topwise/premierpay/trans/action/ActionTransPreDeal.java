package com.topwise.premierpay.trans.action;

import android.content.Context;

import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.ETransType;

/**
 * 交易前 参数下载判断
 */
public class ActionTransPreDeal extends AAction {
    /**
     * 子类构造方法必须调用super设置ActionStartListener
     *
     * @param listener {@link ActionStartListener}
     */
    public ActionTransPreDeal(ActionStartListener listener) {
        super(listener);
    }

    private Context context;
    private ETransType transType;

    /**
     * 设置action运行时参数
     *
     * @param context
     * @param transType
     */
    public void setParam(Context context, ETransType transType) {
        this.context = context;
        this.transType = transType;
    }

    @Override
    protected void process() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 执行交易预处理
                int ret = Component.transPreDeal(context, transType);
                setResult(new ActionResult(ret, null));
            }
        }).start();
    }

    @Override
    public void setResult(ActionResult result) {
        super.setResult(result);
        context = null;
    }
}
