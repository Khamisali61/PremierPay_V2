package com.topwise.premierpay.trans.action;

import android.content.Context;
import android.os.Handler;

import com.topwise.premierpay.daoutils.DaoUtilsStore;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.trans.receipt.PrintListenerImpl;
import com.topwise.premierpay.trans.receipt.ReceiptPrintDetail;

import java.util.List;

/**
 * 创建日期：2021/5/28 on 17:49
 * 描述:
 * 作者:wangweicheng
 */
public class ActionTransPrintDetail extends AAction {
    /**
     * 子类构造方法必须调用super设置ActionStartListener
     *
     * @param listener {@link ActionStartListener}
     */
    public ActionTransPrintDetail(ActionStartListener listener) {
        super(listener);
    }
    private Context context;
    private TransData transData;
    private Handler handler;

    public void setParam(Context context,Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    @Override
    protected void process() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                List<TransData> transDatas = DaoUtilsStore.getInstance().getmTransDaoUtils().queryAll();
                if (transDatas == null || transDatas.size() == 0){
                    setResult(new ActionResult(TransResult.ERR_NO_TRANS, null));
                    return;
                }
                ReceiptPrintDetail receiptPrintDetail = ReceiptPrintDetail.getInstance();
                PrintListenerImpl listener = new PrintListenerImpl( handler);
                int print = receiptPrintDetail.print("TRANSCATION RECORDS", transDatas, listener);
                setResult(new ActionResult(TransResult.SUCC, null));
            }
        });
    }
}
