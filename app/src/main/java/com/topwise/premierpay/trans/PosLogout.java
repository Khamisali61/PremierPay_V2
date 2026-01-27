package com.topwise.premierpay.trans;

import android.content.Context;
import android.os.Handler;

import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.Controller;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.TransResult;

/**
 * 创建日期：2021/3/31 on 17:12
 * 描述:
 * 作者:  wangweicheng
 */
public class PosLogout extends BaseTrans {
    private static final String TAG =  TopApplication.APPNANE + PosLogout.class.getSimpleName();

    public PosLogout(Context context, Handler handler, TransEndListener transListener) {
        super(context, handler, ETransType.LOGOUT, transListener);
    }

    @Override
    public void onActionResult(String currentState, ActionResult result) {

    }

    @Override
    protected void bindStateOnAction() {
        TopApplication.controller.set(Controller.POS_LOGON_STATUS, Controller.Constant.NO);
        TopApplication.controller.set(Controller.OPERATOR_LOGON_STATUS, Controller.Constant.NO);
        transEnd(new ActionResult(TransResult.SUCC, null));
    }
}
