package com.topwise.premierpay.trans;

import android.content.Context;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;

import com.topwise.kdialog.DialogSureCancel;
import com.topwise.kdialog.IkeyListener;

import com.topwise.manager.AppLog;
import com.topwise.manager.emv.enums.EPinType;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.daoutils.DaoUtilsStore;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.action.ActionBTEmvProcess;
import com.topwise.premierpay.trans.action.ActionElecSign;
import com.topwise.premierpay.trans.action.ActionEmvProcess;
import com.topwise.premierpay.trans.action.ActionEnterPin;
import com.topwise.premierpay.trans.action.ActionInputData;
import com.topwise.premierpay.trans.action.ActionInputTransData;
import com.topwise.premierpay.trans.action.ActionInputpwd;
import com.topwise.premierpay.trans.action.ActionSearchCard;
import com.topwise.premierpay.trans.action.ActionTransOnline;
import com.topwise.premierpay.trans.action.ActionTransState;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.State;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.utils.Utils;


/**
 * 创建日期：2021/4/6 on 16:28
 * 描述:
 * 作者:  wangweicheng
 */
public class TransRefund extends BaseTrans {
    private String title = "";

    private TransData origTransdata;
    private byte cardSearchMode;

    public TransRefund(Context context, Handler handler, TransEndListener transListener) {
        super(context, handler, ETransType.TRANS_REFUND, transListener);
        title = ETransType.TRANS_REFUND.getTransName().toUpperCase();
    }

    @Override
    public void onActionResult(String currentState, ActionResult result) {
        State state = State.valueOf(currentState);
        int ret = result.getRet();
        transData.setTransresult(ret);
        AppLog.d("onActionResult","ret"+ret+"state"+state);

        if (ret != TransResult.SUCC && (state == State.EMV_PROC || state == State.ONLINE)) {
            if (result.getData() != null) {
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
        switch (state){
            case CHECK_PWD:
                String data = (String)result.getData();
                String sys_pwd = TopApplication.sysParam.get(SysParam.SEC_MNGPWD);
                if (sys_pwd.equals(data)){
                    gotoState(State.ENTER_DATA.toString());
                }else {
                    transEnd(new ActionResult(TransResult.ERR_SUPPWD_WRONG,null));
                }
                break;
            case ENTER_DATA:
                String[] infos = (String[])result.getData();
                transData.setOrigRefNo(infos[0]);
                transData.setOrigDate(infos[1]);
                getOriginPurchaseData(infos[0]);
                gotoState(State.ENTER_AMOUNT.toString());
                break;
            case ENTER_AMOUNT:
                String amount = (String) result.getData();
                transData.setAmount(amount);
                AppLog.d("onActionResult","ENTER_AMOUNT amount: " + amount);
                gotoState(State.CHECK_CARD.toString());

                break;
            case CHECK_CARD:
                ActionSearchCard.CardInformation cardInfo = (ActionSearchCard.CardInformation) result.getData();
                saveCardInfo(cardInfo, transData, false);
                // 手输卡号处理
                cardSearchMode = cardInfo.getSearchMode();
                AppLog.d("onActionResult","cardSearchMode: " + cardSearchMode);
                if (cardSearchMode == ActionSearchCard.SearchMode.SWIPE || cardSearchMode == ActionSearchCard.SearchMode.KEYIN ) {
                    gotoState(State.ENTER_PIN.toString());
                } else if (cardSearchMode == ActionSearchCard.SearchMode.INSERT ||
                        cardSearchMode == ActionSearchCard.SearchMode.TAP) {
                    gotoState(State.EMV_PROC.toString());
                }
                break;
            case EMV_PROC:
                if ("Y".equals(TopApplication.sysParam.get(SysParam.PARAM_ELEC_SIGN))) {
                    gotoState(State.ELEC_SIGN.toString());
                }else {
                    gotoState(State.TRANS_STATE.toString());
                }
                break;
            case ENTER_PIN:
                String pinblock = (String) result.getData();
                transData.setPin(pinblock);
                if (pinblock != null && !TextUtils.isEmpty(pinblock)){
                    transData.setHasPin(true);
                }
                gotoState(State.ONLINE.toString());
                break;
            case ONLINE:
                gotoState(State.TRANS_STATE.toString());
                break;
            case ELEC_SIGN:
                String  eSign = (String) result.getData();
                if (!TextUtils.isEmpty(eSign)){
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

        bind(State.CHECK_PWD.toString(),  new ActionInputpwd(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionInputpwd)action).setParam(getCurrentContext(),handler,1,
                        getCurrentContext().getString(R.string.set_please_enter_supervisor_password ),
                        getCurrentContext().getString(R.string.set_please_enter_pwd));
            }
        }));

        bind(State.ENTER_DATA.toString(), new ActionInputData(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionInputData)action).setParam(getCurrentContext(),title)
                        .setInputLine1("", InputType.TYPE_CLASS_NUMBER,12,12)
                        .setInputLine2("MMdd", InputType.TYPE_CLASS_NUMBER,4,0);
            }
        },handler,2));

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
                mode = ActionSearchCard.SearchMode.SWIPE |
                        ActionSearchCard.SearchMode.INSERT |
                        ActionSearchCard.SearchMode.TAP|
                        ActionSearchCard.SearchMode.KEYIN;
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

        AAction  emvAAction  ;
        if (TopApplication.sysParam.getInt(SysParam.DEVICE_MODE) == 0) {

            emvAAction =new ActionEmvProcess(new AAction.ActionStartListener() {
                @Override
                public void onStart(AAction action) {
                    ((ActionEmvProcess) action).setParam(getCurrentContext(),handler,transData);
                }
            });
        } else {
            emvAAction =new ActionBTEmvProcess(new AAction.ActionStartListener() {
                @Override
                public void onStart(AAction action) {
                    ((ActionBTEmvProcess) action).setParam(getCurrentContext(),handler,transData);
                }
            });
        }
        bind(State.EMV_PROC.toString(), emvAAction);

        bind(State.TRANS_STATE.toString(), new ActionTransState(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {

                ((ActionTransState) action).setParam(getCurrentContext(),title,transData);
            }
        }));
        bind(State.ELEC_SIGN.toString(),new ActionElecSign(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionElecSign) action).setParam(getCurrentContext(),title,transData);
            }
        }));

        gotoState(State.CHECK_PWD.toString());
    }

    private void checkAmount(String amontStr) {

        final String AmontStr = getCurrentContext().getString(R.string.trans_amount_info,Utils.ftoYuan(amontStr));
        handler.post(new Runnable() {
            @Override
            public void run() {
                DialogSureCancel dialogSureCancel = new DialogSureCancel(getCurrentContext());
                dialogSureCancel.setTitle(title);
                dialogSureCancel.setContent(AmontStr);
                dialogSureCancel.show();
                dialogSureCancel.setMyLietener(new IkeyListener() {
                    @Override
                    public void onConfirm(String text) {
                        gotoState(State.ONLINE.toString());
                    }

                    @Override
                    public void onCancel(int res) {
                        gotoState(State.ENTER_AMOUNT.toString());
                    }
                });
            }
        });
    }


    private void getOriginPurchaseData(String origRefNo){
        origTransdata = DaoUtilsStore.getInstance().getmTransDaoUtils().queryByRefNo(TransData.class, origRefNo);
        if (origTransdata != null){

//            if (ETransType.TRANS_SALE != ETransType.valueOf(origTransdata.getTransType())){
//                transEnd(new ActionResult(TransResult.ERR_VOID_UNSUPPORT,null));
//                return;
//            }
            String trStatus = origTransdata.getTransState();
            String authCode = origTransdata.getAuthCode();
            if (authCode == null || TextUtils.isEmpty(authCode)) {
                // TODO: 2025/6/4 call user to input auth code
            }
            transData.setOrigAuthCode(authCode);
        }
    }

}
