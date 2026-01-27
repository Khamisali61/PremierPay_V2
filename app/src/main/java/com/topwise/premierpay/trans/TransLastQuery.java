package com.topwise.premierpay.trans;

import android.content.Context;
import android.os.Handler;
import android.text.InputType;

import com.topwise.premierpay.trans.action.ActionInputData;
import com.topwise.premierpay.trans.action.ActionTransOnline;
import com.topwise.premierpay.trans.action.ActionTransState;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.State;
import com.topwise.premierpay.trans.model.TransResult;

/**
 * 创建日期：2021/4/7 on 16:38
 * 描述:
 * 作者:  wangweicheng
 */
public class TransLastQuery extends BaseTrans {
    private String title = "";

    public TransLastQuery(Context context, Handler handler, TransEndListener transListener) {
        super(context, handler, ETransType.TRANS_LAST_STATUS, transListener);
        title = ETransType.TRANS_LAST_STATUS.getTransName().toUpperCase();
    }

    @Override
    public void onActionResult(String currentState, ActionResult result) {
        State state = State.valueOf(currentState);
        int ret = result.getRet();

        if (state == State.ONLINE){
            gotoState(State.TRANS_STATE.toString());
            return;
        }

        if (ret != TransResult.SUCC) {
            transEnd(result);
            return;
        }
        switch (state){
            case ENTER_DATA:
                String data = (String)result.getData();
                transData.setOrigTransNo(Long.valueOf(data));
                gotoState(State.ONLINE.toString());
                break;
        }
    }

    @Override
    protected void bindStateOnAction() {
        ActionInputData actionInputData = new ActionInputData(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionInputData)action).setParam(getCurrentContext(),title)
                        .setInputLine1("", InputType.TYPE_CLASS_NUMBER,6,6);
            }
        },handler);
        bind(State.ENTER_DATA.toString(), actionInputData);

        ActionTransOnline actionTransJsonOnline = new ActionTransOnline(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionTransOnline) action).setParam(getCurrentContext(),transData);
            }
        });
        bind(State.ONLINE.toString(), actionTransJsonOnline);

        ActionTransState actionTransState = new ActionTransState(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionTransState) action).setParam(getCurrentContext(),title,transData);
            }
        });
        bind(State.TRANS_STATE.toString(), actionTransState);

        gotoState(State.ENTER_DATA.toString());
    }
}
