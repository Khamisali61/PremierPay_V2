package com.topwise.premierpay.trans;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.TextUtils;

import com.topwise.kdialog.DialogSure;
import com.topwise.manager.AppLog;
import com.topwise.manager.emv.entity.EmvErrorCode;
import com.topwise.manager.emv.enums.EPinType;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.mdb.mode.MDBCallTrans;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.thirdcall.ThirdCallTrans;
import com.topwise.premierpay.trans.action.ActionBTEmvProcess;
import com.topwise.premierpay.trans.action.ActionCardConfirm;
import com.topwise.premierpay.trans.action.ActionCheckTurnScreen;
import com.topwise.premierpay.trans.action.ActionElecSign;
import com.topwise.premierpay.trans.action.ActionEmvProcess;
import com.topwise.premierpay.trans.action.ActionEnterPin;
import com.topwise.premierpay.trans.action.ActionFingerprint;
import com.topwise.premierpay.trans.action.ActionInputTransData;
import com.topwise.premierpay.trans.action.ActionSearchCard;
import com.topwise.premierpay.trans.action.ActionShowMessage;
import com.topwise.premierpay.trans.action.ActionTransOnline;
import com.topwise.premierpay.trans.action.ActionTransState;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.State;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.utils.Utils;
import com.topwise.premierpay.view.TopToast;

public class TransSale extends BaseTrans {
    private String title = "";
    private byte mode = -1;

    private String turnScreenMessage = null;
    private String turnScreenMessage2 = null;

    public TransSale(Context context, Handler handler, TransEndListener transListener) {
        super(context, handler, ETransType.TRANS_SALE, transListener);
        title = ETransType.TRANS_SALE.getTransName().toUpperCase();
    }

    public TransSale(Context context, Handler handler, TransEndListener transListener, ThirdCallTrans thirdCallTrans) {
         this(context, handler, transListener);
         this.thirdCallTrans = thirdCallTrans;
    }

    public TransSale(Context context, Handler handler, TransEndListener transListener, MDBCallTrans mdbCallTrans) {
        this(context, handler, transListener);
        this.mdbCallTrans = mdbCallTrans;
    }


    @Override
    public void onActionResult(String currentState, ActionResult result) {
        State state = State.valueOf(currentState);
        int ret = result.getRet();
        transData.setTransresult(ret);

        AppLog.d("onActionResult","ret"+ret+"state"+state);
        if (state == State.TRANS_STATE) {
            transEnd(new ActionResult(TransResult.ERR_ABORTED,null));
            return;
        }

        if ((ret != TransResult.SUCC) && (state == State.ELEC_SIGN || state == State.FINGER_PRINT)) {
            gotoState(State.TRANS_STATE.toString());
            return;
        }

        if (ret == TransResult.ERR_RF_MULTI_CARD) {
            turnScreenMessage = "Multiple contactless cards detected";
            turnScreenMessage2 = "Please use a single contactless card to execute the transaction";
            gotoState(State.CHECK_TURN_SCREEN.toString());
            return;
        }

        if (ret == EmvErrorCode.EMV_FALLBACK) {
            mode = ActionSearchCard.SearchMode.SWIPE;
            TopToast.showFailToast(getCurrentContext(),"EMV FALLBACK");
            gotoState(State.CHECK_CARD.toString());
            return;
        }



        if (ret == EmvErrorCode.EMV_DECLINED){
            gotoState(State.TRANS_STATE.toString());
            return;
        }
        if (ret == EmvErrorCode.CLSS_NEED_CONTACT) {
            mode = ActionSearchCard.SearchMode.INSERT;
            TopToast.showFailToast(getCurrentContext(),"Please Use Contact");
            Component.incTranNoTime(transData);
            gotoState(State.CHECK_CARD_2.toString());
            return;
        }

        if (ret == EmvErrorCode.CLSS_NEED_PWD) {
            Component.incTranNoTime(transData);
            gotoState(State.ENTER_PIN.toString());
            return;
        }

        if (ret == EmvErrorCode.CLSS_USE_CONTACT) {
            mode = ActionSearchCard.SearchMode.INSERT|ActionSearchCard.SearchMode.SWIPE;
            TopToast.showFailToast(getCurrentContext(),"Insert, Swipe, or Try Another Card");
            gotoState(State.CHECK_CARD.toString());
            return;
        }

        if (ret == EmvErrorCode.CLSS_CARD_NOT_SUPPORT){
            gotoState(State.TRANS_STATE.toString());
            return;
        }

        if (ret == EmvErrorCode.EMV_SEE_PHONE) {
            gotoState(State.SHOW_MESSAGE.toString());
            return;
        }

        if (ret != TransResult.SUCC && (state == State.EMV_PROC || state == State.ONLINE)) {
            if (result.getData() != null) {
                transData = (TransData) result.getData();
            }
            if ("Y".equals(TopApplication.sysParam.get(SysParam.AUTO_IN_MDB))) {
                if (transData.getTransresult() == TransResult.ERR_ABORTED) {
                    result.setData("Transaction Declined");
                    transEnd(result);
                    return;
                }
            }
            title = ETransType.valueOf(transData.getTransType()).getTransName().toUpperCase();
            gotoState(State.TRANS_STATE.toString());
            return;
        }

        if (ret != TransResult.SUCC) {
            gotoState(State.TRANS_STATE.toString());
            return;
        }

        switch (state) {
            case ENTER_AMOUNT:
                String amount = (String) result.getData();
                transData.setAmount(amount);
                mode = ActionSearchCard.SearchMode.SWIPE | ActionSearchCard.SearchMode.INSERT | ActionSearchCard.SearchMode.TAP;
                gotoState(State.CHECK_CARD.toString());
                break;
            case CHECK_CARD:
            case CHECK_CARD_2:
                ActionSearchCard.CardInformation cardInfo = (ActionSearchCard.CardInformation) result.getData();
                saveCardInfo(cardInfo, transData, false);
                // 手输卡号处理
                byte mode = cardInfo.getSearchMode();
                if (mode == ActionSearchCard.SearchMode.SWIPE) {
                    //tese
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
            case SHOW_MESSAGE:
                gotoState(State.CHECK_CARD.toString());
                break;
            case CHECK_TURN_SCREEN:
                gotoState(State.CHECK_CARD.toString());
                break;
            case CHECK_CARD_NO:
                if ("Y".equals(TopApplication.sysParam.get(SysParam.PARAM_FINGERPRINT))) {
                    gotoState(State.FINGER_PRINT.toString());
                } else {
                    gotoState(State.ENTER_PIN.toString());
                }
                break;
            case EMV_PROC: // EMV_PROC步骤已经联机完成，直接跳电子签名步骤
                transData = (TransData) result.getData();
                if ("Y".equals(TopApplication.sysParam.get(SysParam.PARAM_ELEC_SIGN))) {
                    gotoState(State.ELEC_SIGN.toString());
                } else {
                    gotoState(State.TRANS_STATE.toString());
                }
                break;
            case ENTER_PIN:
                String pinblock = (String) result.getData();
                if (!TextUtils.isEmpty(pinblock)) {
                    transData.setPin(pinblock);
                    transData.setHasPin(true);
                }
                gotoState(State.ONLINE.toString());
                break;
            case FINGER_PRINT:
                String fingerprintBuf = (String) result.getData();
                transData.setFingerprint(fingerprintBuf);
                gotoState(State.ONLINE.toString());
                break;
            case ONLINE:
                if ("Y".equals(TopApplication.sysParam.get(SysParam.PARAM_ELEC_SIGN))) {
                    gotoState(State.ELEC_SIGN.toString());
                } else {
                    gotoState(State.TRANS_STATE.toString());
                }
                break;
            case ELEC_SIGN:
                String eSign = (String) result.getData();
                if (!TextUtils.isEmpty(eSign)) {
                    transData.setElecSignature(eSign);
                }
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

        bind(State.CHECK_CARD.toString(), new ActionSearchCard(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionSearchCard) action).setParam(getCurrentContext(), title, mode, transData.getAmount());
            }
        }));
        bind(State.CHECK_CARD_2.toString(), new ActionSearchCard(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionSearchCard) action).setParam(getCurrentContext(), title, mode, transData.getAmount());
            }
        }));

        bind(State.CHECK_CARD_NO.toString(), new ActionCardConfirm(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ETransType eTransType = ETransType.valueOf(transData.getTransType());
                ((ActionCardConfirm)action).setParam(ActivityStack.getInstance().top(), eTransType.getTransName(), transData.getPan(),transData.getAmount());
            }
        }));

        bind(State.SHOW_MESSAGE.toString(),new ActionShowMessage(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionShowMessage)action).setParam(handler,title,"Please See Phone");
            }
        })) ;

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

        AAction emvAAction;
        if (TopApplication.sysParam.getInt(SysParam.DEVICE_MODE) == 0) {
            emvAAction = new ActionEmvProcess(new AAction.ActionStartListener() {
                @Override
                public void onStart(AAction action) {
                    ((ActionEmvProcess) action).setParam(getCurrentContext(),handler,transData);
                }
            });
        } else {
            emvAAction = new ActionBTEmvProcess(new AAction.ActionStartListener() {
                @Override
                public void onStart(AAction action) {
                    ((ActionBTEmvProcess) action).setParam(getCurrentContext(),handler,transData);
                }
            });
        }
        bind(State.EMV_PROC.toString(), emvAAction);

        bind(State.ELEC_SIGN.toString(), new ActionElecSign(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionElecSign) action).setParam(getCurrentContext(), title, transData);
            }
        }));

        bind(State.TRANS_STATE.toString(), new ActionTransState(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionTransState) action).setParam(getCurrentContext(),title,transData);
            }
        }));

        bind(State.FINGER_PRINT.toString(),  new ActionFingerprint(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionFingerprint) action).setParam(getCurrentContext(),title,transData);
            }
        }));

        bind(State.CHECK_TURN_SCREEN.toString(), new ActionCheckTurnScreen(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionCheckTurnScreen) action).setParam(getCurrentContext(), title, turnScreenMessage, turnScreenMessage2);
            }
        }));

        if (TextUtils.isEmpty(transData.getAmount())) {
            gotoState(State.ENTER_AMOUNT.toString());
        } else {
            gotoState(State.CHECK_CARD.toString());
        }
    }
}
