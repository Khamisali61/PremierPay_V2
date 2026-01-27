package com.topwise.premierpay.trans;

import android.content.Context;
import android.os.Handler;

import com.topwise.premierpay.trans.action.ActionTransTmsParamerDownload;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.State;
import com.topwise.premierpay.trans.model.TransResult;

/**
 * 创建日期：2021/5/8 on 15:57
 * 描述:
 * 作者:wangweicheng
 */
public class TransDownloadParamer extends BaseTrans {
    private String title = "";

    public TransDownloadParamer(Context context, Handler handler, TransEndListener transListener) {
        super(context, handler, ETransType.PARAM_DOWNLOAD, transListener);
        title = ETransType.PARAM_DOWNLOAD.getTransName().toUpperCase();
    }

    @Override
    public void onActionResult(String currentState, ActionResult result) {
        State state = State.valueOf(currentState);
        int ret = result.getRet();
        if (ret != TransResult.SUCC) {
            transEnd(result);
            return;
        }
        switch (state) {
            case ONLINE:
                String data = (String) result.getData();
                transEnd(new ActionResult(TransResult.SUCC,data));
                break;
            default:
                transEnd(result);
                break;
        }
    }

    @Override
    protected void bindStateOnAction() {
        ActionTransTmsParamerDownload actionTransTmsParamerDownload = new ActionTransTmsParamerDownload(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionTransTmsParamerDownload) action).setParam(getCurrentContext());
            }
        });
        bind(State.ONLINE.toString(), actionTransTmsParamerDownload);
        gotoState(State.ONLINE.toString());
    }
}
