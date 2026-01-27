package com.topwise.premierpay.trans;

import android.content.Context;
import android.os.Handler;

import com.topwise.premierpay.trans.action.ActionInputTransData;
import com.topwise.premierpay.trans.action.ActionQrScan;
import com.topwise.premierpay.trans.action.ActionTransOnline;
import com.topwise.premierpay.trans.action.ActionTransState;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.State;
import com.topwise.premierpay.trans.model.TransResult;

/**
 * 创建日期：2021/4/6 on 17:38
 * 描述:
 * 作者:  wangweicheng
 */
public class TransQrSave extends BaseTrans {
    private String title = "";

    public TransQrSave(Context context, Handler handler, TransEndListener transListener) {
        super(context, handler, ETransType.TRANS_QR_SALE, transListener);
        title = ETransType.TRANS_QR_SALE.getTransName().toUpperCase();
    }

    @Override
    public void onActionResult(String currentState, ActionResult result) {
        State state = State.valueOf(currentState);
        int ret = result.getRet();
        transData.setTransresult(ret);

        if (ret != TransResult.SUCC && state != State.ONLINE) {
            transEnd(result);
            return;
        }
        switch (state){
            case ENTER_AMOUNT:
                String amount = (String) result.getData();
                transData.setAmount(amount);
                gotoState(State.ENTER_SCAN.toString());
                break;
            case ENTER_SCAN:
                String qrdata = (String) result.getData();
                transData.setPan(qrdata);
                transData.setQrCode(qrdata);
                transData.setEnterMode(Component.EnterMode.QR);
                gotoState(State.ONLINE.toString());
                break;
            case ONLINE:
                gotoState(State.TRANS_STATE.toString());
                break;
            default:
                transEnd(result);
                break;
        }
    }

    @Override
    protected void bindStateOnAction() {

        bind(State.ENTER_AMOUNT.toString(), new ActionInputTransData(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionInputTransData) action).setParam(getCurrentContext(), handler, title,1)
                        .setInputLine1("", 9, false);

            }
        }));

        bind(State.ENTER_SCAN.toString(), new ActionQrScan(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionQrScan) action).setParam(getCurrentContext(),title, transData.getAmount());
            }
        }));

        bind(State.ONLINE.toString(), new ActionTransOnline(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionTransOnline) action).setParam(getCurrentContext(),transData);
            }
        }));

        bind(State.TRANS_STATE.toString(),  new ActionTransState(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {

                ((ActionTransState) action).setParam(getCurrentContext(),title,transData);
            }
        }));
        gotoState(State.ENTER_AMOUNT.toString());
    }
}
