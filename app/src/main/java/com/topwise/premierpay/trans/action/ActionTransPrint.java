package com.topwise.premierpay.trans.action;

import android.content.Context;
import android.os.Handler;

import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.trans.receipt.PrintListenerImpl;
import com.topwise.premierpay.trans.receipt.ReceiptPrintTrans;

/**
 * 创建日期：2021/4/2 on 13:59
 * 描述:
 * 作者:  wangweicheng
 */
public class ActionTransPrint extends AAction {
    /**
     * 子类构造方法必须调用super设置ActionStartListener
     *
     * @param listener {@link ActionStartListener}
     */
    public ActionTransPrint(ActionStartListener listener) {
        super(listener);
    }
    private Context context;
    private TransData transData;
    private Handler handler;
    private boolean isRepring;
    public void setParam(Context context,Handler handler, TransData transData) {
        this.context = context;
        this.transData = transData;
        this.handler = handler;
        this.isRepring = false;
    }
    public void setParam(Context context,Handler handler, TransData transData,boolean isRepring) {
        this.context = context;
        this.transData = transData;
        this.handler = handler;
        this.isRepring = isRepring;
    }

    @Override
    protected void process() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                ReceiptPrintTrans receiptPrintTrans = ReceiptPrintTrans.getInstance();
                PrintListenerImpl listener = new PrintListenerImpl(handler);
                receiptPrintTrans.print(transData, false, listener);
                setResult(new ActionResult(TransResult.SUCC, transData));
            }
        }).start();
    }
}
