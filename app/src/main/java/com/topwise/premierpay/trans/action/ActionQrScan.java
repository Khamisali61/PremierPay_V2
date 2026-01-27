package com.topwise.premierpay.trans.action;

import android.content.Context;

import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.utils.ScanCodeUtils;
import com.topwise.premierpay.utils.Utils;

/**
 * 创建日期：2021/4/22 on 16:34
 * 描述:
 * 作者:wangweicheng
 */
public class ActionQrScan extends AAction {
    /**
     * 子类构造方法必须调用super设置ActionStartListener
     *
     * @param listener {@link ActionStartListener}
     */
    public ActionQrScan(ActionStartListener listener) {
        super(listener);
    }
    private String title;
    private String amount;
    private Context context;
    public void setParam(Context context,String title, String amount) {
        this.title = title;
        this.amount = amount;
        this.context = context;
    }
    @Override
    protected void process() {
        String amounty = TopApplication.sysParam.get(SysParam.APP_PARAM_TRANS_CURRENCY_SYMBOL) + Utils.ftoYuan(amount);
        ScanCodeUtils scanCodeUtils = new ScanCodeUtils(title, amounty);
        scanCodeUtils.setOnScanListener(new ScanCodeUtils.onScanListener() {
            @Override
            public void onCancel(int r) {
                setResult(new ActionResult(TransResult.ERR_ABORTED, null));
            }

            @Override
            public void onResult(String s) {
                if (s != null && !s.isEmpty()) {
                    setResult(new ActionResult(TransResult.SUCC, s));
                } else {
                    setResult(new ActionResult(TransResult.ERR_ABORTED, null));
                }
            }
        });
        scanCodeUtils.startScan();
    }
}
