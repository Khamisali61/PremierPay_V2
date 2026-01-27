package com.topwise.premierpay.trans;

import android.content.Context;
import android.os.Handler;

import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.ETransType;

/**
 * 创建日期：2021/4/6 on 17:39
 * 描述:
 * 作者:  wangweicheng
 */
public class  TransQrQuery extends BaseTrans {
    private String title = "";

    public TransQrQuery(Context context, Handler handler, TransEndListener transListener) {
        super(context, handler, ETransType.TRANS_REFUND, transListener);
        title = ETransType.TRANS_REFUND.getTransName().toUpperCase();
    }

    @Override
    public void onActionResult(String currentState, ActionResult result) {

    }

    @Override
    protected void bindStateOnAction() {

    }
}