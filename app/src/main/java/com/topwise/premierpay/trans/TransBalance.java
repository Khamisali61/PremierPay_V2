package com.topwise.premierpay.trans;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.topwise.manager.emv.enums.EPinType;
import com.topwise.premierpay.trans.action.ActionEmvProcess;
import com.topwise.premierpay.trans.action.ActionEnterPin;
import com.topwise.premierpay.trans.action.ActionSearchCard;
import com.topwise.premierpay.trans.action.ActionTransOnline;
import com.topwise.premierpay.trans.action.ActionTransState;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.State;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransResult;

/**
 * 创建日期：2021/4/6 on 16:33
 * 描述:
 * 作者:  wangweicheng
 */
public class TransBalance extends BaseTrans {
    private String title = "";

    public TransBalance(Context context, Handler handler, TransEndListener transListener) {
        super(context, handler, ETransType.BALANCE, transListener);
        title = ETransType.BALANCE.getTransName().toUpperCase();
    }

    @Override
    public void onActionResult(String currentState, ActionResult result) {
        State state = State.valueOf(currentState);
        int ret = result.getRet();
        if (ret != TransResult.SUCC) {
            transEnd(result);
            return;
        }
        switch (state){
            case CHECK_CARD:
                ActionSearchCard.CardInformation cardInfo = (ActionSearchCard.CardInformation) result.getData();
                saveCardInfo(cardInfo, transData, false);
                // 手输卡号处理
                byte mode = cardInfo.getSearchMode();
                if (mode == ActionSearchCard.SearchMode.SWIPE) {
                    gotoState(State.ENTER_PIN.toString());
                } else if (mode == ActionSearchCard.SearchMode.INSERT ||
                        mode == ActionSearchCard.SearchMode.TAP) {
                    gotoState(State.EMV_PROC.toString());
                }
                break;
            case EMV_PROC:
                transData = (TransData) result.getData();
                gotoState(State.TRANS_STATE.toString());

                break;
            case ENTER_PIN:
                String pinblock = (String) result.getData();
                if (!TextUtils.isEmpty(pinblock)){
                    transData.setPin(pinblock);
                    transData.setHasPin(true);
                }
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

    /**
     * 1.寻卡
     * 2.密码
     * 3.联机
     * 4.显示
     */
    @Override
    protected void bindStateOnAction() {

        bind(State.CHECK_CARD.toString(), new ActionSearchCard(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                mode = ActionSearchCard.SearchMode.SWIPE | ActionSearchCard.SearchMode.INSERT| ActionSearchCard.SearchMode.TAP;
                ((ActionSearchCard) action).setParam(getCurrentContext(),title,mode,transData.getAmount());
            }
        }));

        bind(State.ENTER_PIN.toString(), new ActionEnterPin(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionEnterPin) action).setParam(getCurrentContext(),title,transData.getPan(),
                        transData.getAmount(),
                        EPinType.ONLINE_PIN_REQ);
            }
        }));

        bind(State.ONLINE.toString(), new ActionTransOnline(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionTransOnline) action).setParam(getCurrentContext(),transData);
            }
        }));

        bind(State.EMV_PROC.toString(),  new ActionEmvProcess(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionEmvProcess) action).setParam(getCurrentContext(),handler,transData);
            }
        }));

        bind(State.TRANS_STATE.toString(), new ActionTransState(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionTransState) action).setParam(getCurrentContext(),title,transData);
            }
        }));

        gotoState(State.CHECK_CARD.toString());
    }
}
