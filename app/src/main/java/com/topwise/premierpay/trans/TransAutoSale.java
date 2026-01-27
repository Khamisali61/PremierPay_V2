package com.topwise.premierpay.trans;

import static com.topwise.premierpay.trans.model.TransResult.ERR_CHECK_CARD;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;

import com.topwise.manager.AppLog;
import com.topwise.premierpay.daoutils.DaoUtilsStore;
import com.topwise.premierpay.trans.action.ActionEmvProcess;
import com.topwise.premierpay.trans.action.ActionInputTransData;
import com.topwise.premierpay.trans.action.ActionSearchCard;
import com.topwise.premierpay.trans.action.ActionTransOnline;
import com.topwise.premierpay.trans.action.ActionTransState;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.State;
import com.topwise.premierpay.trans.model.TestParam;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.trans.model.TransStatusSum;
import com.topwise.premierpay.utils.ThreadPoolUtils;

import java.util.List;

public class TransAutoSale extends BaseTrans {

    private static final String TAG = BaseTrans.class.getSimpleName();

    private String title = "";
    private byte mode = -1;
    private long startTime ;

    public TransAutoSale(Context context, Handler handler, TransEndListener transListener) {
        super(context, handler, ETransType.TRANS_AUTO_SALE, transListener);
        title = ETransType.TRANS_AUTO_SALE.getTransName().toUpperCase();
    }

    @Override
    public void onActionResult(String currentState, ActionResult result) {
        State state = State.valueOf(currentState);
        int ret = result.getRet();
        AppLog.d("onActionResult","ret= " + ret + " state= " + state.toString());

        transData.setTransresult(ret);

        if (ret != TransResult.SUCC){
            if(state  == State.CHECK_CARD) {
                transData.getTransStatusSum().setResult(TransResult.ERR_CHECK_CARD);
                DaoUtilsStore.getInstance().updateStatus(transData);
            }else if(state  == State.EMV_PROC) {
                int  transRet = transData.getTransStatusSum().getResult();
                if(!(transRet ==  TransResult.ERR_CONNECT ||transRet ==  TransResult.ERR_SEND ||
                        transRet ==  TransResult.ERR_RECV)){
                    transData.getTransStatusSum().setResult(ERR_CHECK_CARD);
                    DaoUtilsStore.getInstance().updateStatus(transData);
                    AppLog.d(TAG,"save emv error");
                }
            }

            Device.closeAllLed();
            TestParam testParam =  DaoUtilsStore.getInstance().getTestParam();
            List<TransStatusSum> list = DaoUtilsStore.getInstance().getmTransStatusDaoUtils().queryAll();
            int count = (list== null?0:list.size());


            if(count>= testParam.getTotalNum() || ret == TransResult.ERR_CANCEL){
                transListener.onEnd(result);
                setTransRunning(false);
            }else{
                ThreadPoolUtils.getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        long currTime  = System.currentTimeMillis();
                        long intervalTime = currTime-startTime;
                        if(intervalTime<testParam.getEachTransTime()*1000){
                            SystemClock.sleep(testParam.getEachTransTime()*1000-intervalTime);
                        }
                        transListener.onEnd(result);
                        transData = Component.transInit();
                        transData.setTransType(transType.toString());
                        gotoState(State.ENTER_AMOUNT.toString());
                    }
                });


            }
            return;
        }
        switch (state){
            case ENTER_AMOUNT:
                startTime = System.currentTimeMillis();
                String amount = (String) result.getData();
                transData.setAmount(amount);
                DaoUtilsStore.getInstance().updateStatus(transData);
                gotoState(State.CHECK_CARD.toString());
                break;
            case CHECK_CARD:
                ActionSearchCard.CardInformation cardInfo = (ActionSearchCard.CardInformation) result.getData();
                saveCardInfo(cardInfo, transData, false);
                // 手输卡号处理
                byte mode = cardInfo.getSearchMode();
                if (mode == ActionSearchCard.SearchMode.SWIPE) {
                    //tese
                    transData.setPan(cardInfo.getPan());
                    transData.setTrack2(cardInfo.getTrack2());
                    transData.setExpDate(cardInfo.getExpDate());
                    gotoState(State.ENTER_PIN.toString());
                } else if (mode == ActionSearchCard.SearchMode.INSERT ||
                        mode == ActionSearchCard.SearchMode.TAP) {
                    // EMV处理  插卡跟挥卡走
                    gotoState(State.EMV_PROC.toString());
                }
                break;
            case EMV_PROC:
                transData.getTransStatusSum().setResult(TransResult.SUCC);
                DaoUtilsStore.getInstance().updateStatus(transData);
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
        bind(State.ENTER_AMOUNT.toString(), new ActionInputTransData(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                DaoUtilsStore.getInstance().updateStatus(transData);
                ((ActionInputTransData) action).setParam(getCurrentContext(), handler, title,1)
                        .setInputLine1("", 9, false)
                        .setStressTest(true);
                transData.setStressTest(true);

            }
        }));
        bind(State.CHECK_CARD.toString(), new ActionSearchCard(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                mode = ActionSearchCard.SearchMode.SWIPE | ActionSearchCard.SearchMode.INSERT| ActionSearchCard.SearchMode.TAP;
                ((ActionSearchCard) action).setParam(getCurrentContext(), true,title,mode,transData.getAmount());
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
        AppLog.d("jeremy","==================== ");
        gotoState(State.ENTER_AMOUNT.toString());
    }
}

