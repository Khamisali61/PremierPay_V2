package com.topwise.premierpay.trans;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.topwise.manager.emv.enums.EPinType;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.action.ActionElecSign;
import com.topwise.premierpay.trans.action.ActionEmvProcess;
import com.topwise.premierpay.trans.action.ActionEnterPin;
import com.topwise.premierpay.trans.action.ActionInputTransData;
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
 * 创建日期：2021/4/7 on 16:22
 * 描述:
 * 作者:  wangweicheng
 */
public class TransCashOnly extends BaseTrans {
    private String title = "";
    private byte mode = -1;

    public TransCashOnly(Context context, Handler handler, TransEndListener transListener) {
        super(context, handler, ETransType.TRANS_CASH, transListener);
        title = ETransType.TRANS_CASH.getTransName().toUpperCase();
    }

    @Override
    public void onActionResult(String currentState, ActionResult result) {
        State state = State.valueOf(currentState);
        int ret = result.getRet();

        if (state == State.TRANS_STATE){
            transEnd(new ActionResult(TransResult.ERR_ABORTED,null));
            return;
        }

        if (ret != TransResult.SUCC && (state == State.EMV_PROC || state == State.ONLINE)) {
            gotoState(State.TRANS_STATE.toString());
            return;
        }
        if (ret != TransResult.SUCC) {
            transEnd(result);
            return;
        }
        switch (state){
            case ENTER_AMOUNT:
                String amount = (String) result.getData();
                transData.setAmount(amount);
                transData.setCashAmount(amount);
                gotoState(State.CHECK_CARD.toString());
                break;
            case CHECK_CARD:
                ActionSearchCard.CardInformation cardInfo = (ActionSearchCard.CardInformation) result.getData();
                saveCardInfo(cardInfo, transData, false);
                // 手输卡号处理
                byte mode = cardInfo.getSearchMode();
                if (mode == ActionSearchCard.SearchMode.SWIPE) {
                    //tese
//                    transData.setTrack2("5204740000001002D25121011111199911111");
//                    transData.setTrack2("5186007600090864D25121061111199911111"); // MasterCard (PIN - 9086)
//                    transData.setPan("5186007600090864");

                    gotoState(State.ENTER_PIN.toString());
                } else if (mode == ActionSearchCard.SearchMode.INSERT ||
                        mode == ActionSearchCard.SearchMode.TAP) {
                    // EMV处理  插卡跟挥卡走
                    // TopToast.showNormalToast(getCurrentContext(),mode + "");
                    gotoState(State.EMV_PROC.toString());
                }
                break;
            case EMV_PROC:
                transData = (TransData) result.getData();
                if ("Y".equals(TopApplication.sysParam.get(SysParam.PARAM_ELEC_SIGN))) {
                    gotoState(State.ELEC_SIGN.toString());
                }else {
                    gotoState(State.TRANS_STATE.toString());
                }
                break;
            case ELEC_SIGN:
                String  eSign = (String) result.getData();
                if (!TextUtils.isEmpty(eSign)){
                    transData.setElecSignature(eSign);
                }
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

    @Override
    protected void bindStateOnAction() {
        ActionInputTransData amountAction = new ActionInputTransData(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionInputTransData) action).setParam(getCurrentContext(), handler, title,1)
                        .setInputLine1(getCurrentContext().getString(R.string.amount_input_tip_cash),  9, false);

            }
        });
        bind(State.ENTER_AMOUNT.toString(), amountAction);

        ActionSearchCard CardAction = new ActionSearchCard(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                mode =  ActionSearchCard.SearchMode.INSERT| ActionSearchCard.SearchMode.TAP;
                ((ActionSearchCard) action).setParam(getCurrentContext(),title,mode,null
                        ,transData.getCashAmount());
            }
        });
        bind(State.CHECK_CARD.toString(), CardAction);

        ActionEnterPin actionEnterPin = new ActionEnterPin(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionEnterPin) action).setParam(getCurrentContext(),title,transData.getPan(),
                        transData.getAmount(),
                        EPinType.ONLINE_PIN_REQ);
            }
        });
        bind(State.ENTER_PIN.toString(), actionEnterPin);

        ActionTransOnline actionTransOnline = new ActionTransOnline(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionTransOnline) action).setParam(getCurrentContext(),transData);
            }
        });
        bind(State.ONLINE.toString(), actionTransOnline);

        ActionEmvProcess actionEmvProcess = new ActionEmvProcess(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionEmvProcess) action).setParam(getCurrentContext(),handler,transData);
            }
        });
        bind(State.EMV_PROC.toString(), actionEmvProcess);

        bind(State.ELEC_SIGN.toString(),new ActionElecSign(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionElecSign) action).setParam(getCurrentContext(),title,transData);
            }
        }));
        ActionTransState actionTransState = new ActionTransState(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionTransState) action).setParam(getCurrentContext(),title,transData);
            }
        });
        bind(State.TRANS_STATE.toString(), actionTransState);

        gotoState(State.ENTER_AMOUNT.toString());
    }
}
