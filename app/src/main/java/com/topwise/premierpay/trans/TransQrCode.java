package com.topwise.premierpay.trans;

import android.content.Context;
import android.os.Handler;

import com.topwise.premierpay.trans.action.ActionInputTransData;
import com.topwise.premierpay.trans.action.ActionShowCode;
import com.topwise.premierpay.trans.action.ActionTransState;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.State;
import com.topwise.premierpay.trans.model.TransResult;

/**
 * 创建日期：2021/4/6 on 17:38
 * 描述:
 * 作者:  wangweicheng
 */
public class TransQrCode extends BaseTrans {
    private String title = "";

    private String amount = "";

    public TransQrCode(Context context, Handler handler, TransEndListener transListener) {
        super(context, handler, ETransType.TRANS_QR_CODE, transListener);
        title = ETransType.TRANS_QR_CODE.getTransName().toUpperCase();
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
                amount = (String) result.getData();
                gotoState(State.SHOW_CODE.toString());
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
        bind(State.SHOW_CODE.toString(), new ActionShowCode(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionShowCode) action).setParam(getCurrentContext(), title, amount);
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
