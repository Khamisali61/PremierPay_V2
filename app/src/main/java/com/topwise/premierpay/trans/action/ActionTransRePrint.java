package com.topwise.premierpay.trans.action;

import android.content.Context;
import android.os.Handler;

import com.topwise.premierpay.daoutils.DaoUtilsStore;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.trans.receipt.PrintListenerImpl;
import com.topwise.premierpay.trans.receipt.ReceiptPrintTrans;

import java.util.List;

/**
 * 创建日期：2021/4/13 on 10:39
 * 描述:
 * 作者:  wangweicheng
 */
public class ActionTransRePrint extends AAction {
    /**
     * 子类构造方法必须调用super设置ActionStartListener
     *
     * @param listener {@link ActionStartListener}
     */
    public ActionTransRePrint(ActionStartListener listener) {
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<TransData> transDataList = DaoUtilsStore.getInstance().getmTransDaoUtils().queryDescAll();
                if (transDataList == null || transDataList.size() == 0) {
                    setResult(new ActionResult(TransResult.ERR_NO_TRANS, null));
                    return;
                }
                transData = transDataList.get(0);
                ReceiptPrintTrans receiptPrintTrans = ReceiptPrintTrans.getInstance();
                PrintListenerImpl listener = new PrintListenerImpl(handler);
                receiptPrintTrans.print(transData, true, listener);
                setResult(new ActionResult(TransResult.SUCC, transData));
            }
        }).start();
    }
}
