package com.topwise.premierpay.trans;

import android.content.Context;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;

import com.topwise.manager.emv.entity.EmvOutCome;
import com.topwise.manager.emv.enums.EPinType;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.trans.action.ActionCardConfirm;
import com.topwise.premierpay.trans.action.ActionEmvProcess;
import com.topwise.premierpay.trans.action.ActionEnterPin;
import com.topwise.premierpay.trans.action.ActionInputData;
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

public class TransTransfer extends BaseTrans {
    private String title = "";
    private byte mode = -1;

    public TransTransfer(Context context, Handler handler, TransEndListener transListener) {
        super(context, handler, ETransType.TRANS_TRANSFER, transListener);
        title = ETransType.TRANS_TRANSFER.getTransName().toUpperCase();
    }



    @Override
    public void onActionResult(String currentState, ActionResult result) {
        State state = State.valueOf(currentState);
        int ret = result.getRet();

        transData.setTransresult(ret);

        if (state == State.TRANS_STATE){
            transEnd(new ActionResult(TransResult.ERR_ABORTED,null));
            return;
        }
        if (ret != TransResult.SUCC && (state == State.EMV_PROC || state == State.ONLINE)) {
            if (result.getData() != null){
                transData = (TransData) result.getData();
            }
            title = ETransType.valueOf(transData.getTransType()).getTransName().toUpperCase();
            gotoState(State.TRANS_STATE.toString());
            return;
        }
        if (ret != TransResult.SUCC) {
            transEnd(result);
            return;
        }
        switch (state) {
            case ENTER_AMOUNT:
                String amount = (String) result.getData();
                transData.setAmount(amount);
                gotoState(State.ENTER_DATA.toString());
                break;
            case ENTER_DATA:
                gotoState(State.CHECK_CARD.toString());
                break;
            case CHECK_CARD:
                ActionSearchCard.CardInformation cardInfo = (ActionSearchCard.CardInformation) result.getData();
                saveCardInfo(cardInfo, transData, false);
                // 手输卡号处理
                byte mode = cardInfo.getSearchMode();
                if (mode == ActionSearchCard.SearchMode.SWIPE) {
                    transData.setPan(cardInfo.getPan());
                    transData.setTrack2(cardInfo.getTrack2());
                    transData.setExpDate(cardInfo.getExpDate());
                    gotoState(State.CHECK_CARD_NO.toString());
                } else if (mode == ActionSearchCard.SearchMode.INSERT ||
                        mode == ActionSearchCard.SearchMode.TAP) {
                    // EMV处理  插卡跟挥卡走
                    gotoState(State.EMV_PROC.toString());
                }
                break;
            case CHECK_CARD_NO:
                gotoState(State.ENTER_PIN.toString());
                break;
            case EMV_PROC:
                EmvOutCome emvOutCome = (EmvOutCome) result.getData();

//                if (EmvOutCome.ARQC == emvOutCome){
//                    gotoState(State.ONLINE.toString());
//                }else if (EmvOutCome.APPROVED == emvOutCome
//                        || EmvOutCome.OFFLINE_APPROVED == emvOutCome) {
//
//                    gotoState(State.TRANS_STATE.toString());
//                }else {
//                    transEnd(result);
//                }
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

        bind(State.ENTER_AMOUNT.toString(), new ActionInputTransData(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionInputTransData) action).setParam(getCurrentContext(), handler, title,1)
                        .setInputLine1("", 9, false);

            }
        }));
        bind(State.ENTER_DATA.toString(), new ActionInputData(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionInputData)action).setParam(getCurrentContext(),title)
                        .setInputLine1(getCurrentContext().getString(R.string.set_please_enter_account ),
                                InputType.TYPE_CLASS_NUMBER,19,12,true);
            }
        },handler));

        bind(State.CHECK_CARD.toString(), new ActionSearchCard(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                mode = ActionSearchCard.SearchMode.SWIPE | ActionSearchCard.SearchMode.INSERT| ActionSearchCard.SearchMode.TAP;
                ((ActionSearchCard) action).setParam(getCurrentContext(),title,mode,transData.getAmount());
            }
        }));
        bind(State.CHECK_CARD_NO.toString(), new ActionCardConfirm(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ETransType eTransType = ETransType.valueOf(transData.getTransType());
                ((ActionCardConfirm)action).setParam(ActivityStack.getInstance().top(),eTransType.getTransName(),transData.getPan(),transData.getAmount());
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

        bind(State.EMV_PROC.toString(), new ActionEmvProcess(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionEmvProcess) action).setParam(getCurrentContext(),handler,transData);
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
