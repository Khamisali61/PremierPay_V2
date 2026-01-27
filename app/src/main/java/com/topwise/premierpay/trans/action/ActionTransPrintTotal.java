package com.topwise.premierpay.trans.action;

import android.content.Context;
import android.os.Handler;

import com.topwise.premierpay.daoutils.DaoUtilsStore;
import com.topwise.premierpay.daoutils.entity.TotaTransdata;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.trans.receipt.PrintListenerImpl;
import com.topwise.premierpay.trans.receipt.ReceiptPrintTotal;

import java.util.List;

/**
 * 创建日期：2021/6/1 on 14:47
 * 描述:
 * 作者:wangweicheng
 */
public class ActionTransPrintTotal extends AAction {
    /**
     * 子类构造方法必须调用super设置ActionStartListener
     *
     * @param listener {@link ActionStartListener}
     */
    public ActionTransPrintTotal(ActionStartListener listener) {
        super(listener);
    }
    private Context context;
    private Handler handler;
    private boolean isRe;
    private TotaTransdata totaTransdata;
    private String title;

    public void setParam(Context context,Handler handler) {
        this.context = context;
        this.handler = handler;
        this.isRe = false;
    }

    public void setParam(Context context,Handler handler,boolean isRe) {
        this.context = context;
        this.handler = handler;
        this.isRe = isRe;
    }

    @Override
    protected void process() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isRe) {
                    title = "RE RECORD SUMMARY";
                    List<TotaTransdata> totaTransdatas = DaoUtilsStore.getInstance().getmTotaTransdata().queryAll();
                    if (totaTransdatas != null && totaTransdatas.size() > 0)
                        totaTransdata = totaTransdatas.get(0);

                } else {
                    title = "RECORD SUMMARY";
                    int transCount = DaoUtilsStore.getInstance().getmTransDaoUtils().getTransCount();
                    if (transCount == 0) {
                        return;
                    }
                    totaTransdata = Component.calcTotal();
                }

                ReceiptPrintTotal receiptPrintTotal = ReceiptPrintTotal.getInstance();
                PrintListenerImpl listener = new PrintListenerImpl( handler);
                int print = receiptPrintTotal.print(title, totaTransdata, listener);
                setResult(new ActionResult(TransResult.SUCC, null));
            }
        });
    }
}
