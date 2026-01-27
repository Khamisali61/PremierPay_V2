package com.topwise.premierpay.trans;

import android.content.Context;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;

import com.topwise.manager.emv.enums.EPinType;
import com.topwise.manager.utlis.DataUtils;

import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.daoutils.DaoUtilsStore;
import com.topwise.premierpay.mdb.mode.MDBCallTrans;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.thirdcall.ThirdCallTrans;
import com.topwise.premierpay.trans.action.ActionBTEmvProcess;
import com.topwise.premierpay.trans.action.ActionDispTransDetail;
import com.topwise.premierpay.trans.action.ActionEmvProcess;
import com.topwise.premierpay.trans.action.ActionEnterPin;
import com.topwise.premierpay.trans.action.ActionInputData;
import com.topwise.premierpay.trans.action.ActionInputpwd;
import com.topwise.premierpay.trans.action.ActionSearchCard;
import com.topwise.premierpay.trans.action.ActionTransOnline;
import com.topwise.premierpay.trans.action.ActionTransState;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.State;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.utils.Utils;

import java.util.LinkedHashMap;

/**
 * 创建日期：2021/4/6 on 16:28
 * 描述:
 * 作者:  wangweicheng
 */
public class TransVoid extends BaseTrans {
    private String title = "";
    private TransData origTransdata;

    public TransVoid(Context context, Handler handler, TransEndListener transListener) {
        super(context, handler, ETransType.TRANS_VOID, transListener);
        title = ETransType.TRANS_VOID.getTransName().toUpperCase();
    }

    public TransVoid(Context context, Handler handler, TransEndListener transListener, ThirdCallTrans thirdCallTrans) {
        this( context,  handler,  transListener);
        this.thirdCallTrans = thirdCallTrans;
    }

    public TransVoid(Context context, Handler handler, TransEndListener transListener, MDBCallTrans mdbCallTrans) {
        this( context,  handler,  transListener);
        this.mdbCallTrans = mdbCallTrans;
    }

    @Override
    public void onActionResult(String currentState, ActionResult result) {
        State state = State.valueOf(currentState);
        int ret = result.getRet();

        if (ret != TransResult.SUCC) {
            if (State.ONLINE ==state){
                gotoState(State.TRANS_STATE.toString());
                return;
            }
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
                data = (String)result.getData();
                if (DataUtils.isNullString(data)){
                    transEnd(new ActionResult(TransResult.ERR_ABORTED,null));
                    return;
                }
                validateOrigTransData(data);
                break;
            case ONLINE:
                gotoState(State.TRANS_STATE.toString());
                break;
            case TRANS_DETAIL:
                checkCardAndPin();
                break;
            case CHECK_CARD:
                ActionSearchCard.CardInformation cardInfo = (ActionSearchCard.CardInformation) result.getData();
                saveCardInfo(cardInfo, transData, false);
                // 手输卡号处理
                byte mode = cardInfo.getSearchMode();
                if (mode == ActionSearchCard.SearchMode.SWIPE) {

                    // 输密码
                    if (!transData.getPan().equals(origTransdata.getPan())) {
                        transEnd(new ActionResult(TransResult.ERR_CARD_NO, null));
                        return;
                    }
                    checkPin();
                } else if (mode == ActionSearchCard.SearchMode.INSERT ||
                        mode == ActionSearchCard.SearchMode.TAP) {
                    // EMV处理  插卡跟挥卡走
                    // TopToast.showNormalToast(getCurrentContext(),mode + "");
                    gotoState(State.EMV_PROC.toString());
                }
                break;
            case ENTER_PIN:
                String pinblock = (String) result.getData();
                if (!TextUtils.isEmpty(pinblock)){
                    transData.setPin(pinblock);
                    transData.setHasPin(true);
                }
                gotoState(State.ONLINE.toString());
                break;

            case EMV_PROC:
                transData = (TransData) result.getData();
                gotoState(State.ONLINE.toString());
                break;
            default:
                transEnd(result);
        }
    }

    @Override
    protected void bindStateOnAction() {
        bind(State.CHECK_PWD.toString(), new ActionInputpwd(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionInputpwd)action).setParam(getCurrentContext(),handler,1,
                        getCurrentContext().getString(R.string.set_please_enter_supervisor_password ),
                        getCurrentContext().getString(R.string.set_please_enter_pwd));
            }
        }));

        ActionInputData actionInputData = new ActionInputData(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionInputData)action).setParam(getCurrentContext(),title)
                        .setInputLine1("Please input original Invoice Number", InputType.TYPE_CLASS_NUMBER,6,6);
            }
        },handler);
        bind(State.ENTER_DATA.toString(), actionInputData);

        bind(State.TRANS_DETAIL.toString(), new ActionDispTransDetail(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                String transType = ETransType.valueOf(origTransdata.getTransType()).getTransName();

                String formater = Utils.ftoYuan(origTransdata.getAmount());

                String amount = context.getString(R.string.trans_amount_default, formater);

                // 日期时间
                String temp = Utils.getTransDataTime(origTransdata);

                LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                map.put(context.getString(R.string.trans_type), transType.toUpperCase());
                map.put(context.getString(R.string.merchine_id), origTransdata.getMerchID());
                map.put(context.getString(R.string.term_id), origTransdata.getTermID());
                map.put(context.getString(R.string.trans_amount_0), amount);
                map.put(context.getString(R.string.trans_auth_id), transData.getOrigAuthCode());
                map.put(context.getString(R.string.trans_Rrn_id), transData.getOrigRefNo());
                map.put(context.getString(R.string.trans_data_time), temp);
                ((ActionDispTransDetail) action).setParam(getCurrentContext(), handler,
                        title, map,true);
            }
        }));

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
        AAction  emvAAction  ;
        if(TopApplication.sysParam.getInt(SysParam.DEVICE_MODE) ==0 ){

            emvAAction =new ActionEmvProcess(new AAction.ActionStartListener() {
                @Override
                public void onStart(AAction action) {
                    ((ActionEmvProcess) action).setParam(getCurrentContext(),handler,transData);
                }
            });
        }else{
            emvAAction =new ActionBTEmvProcess(new AAction.ActionStartListener() {
                @Override
                public void onStart(AAction action) {
                    ((ActionBTEmvProcess) action).setParam(getCurrentContext(),handler,transData);
                }
            });
        }

        bind(State.EMV_PROC.toString(),emvAAction);

        bind(State.TRANS_STATE.toString(),  new ActionTransState(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionTransState) action).setParam(getCurrentContext(),title,transData);
            }
        }));

        gotoState(State.CHECK_PWD.toString());
    }

    private void validateOrigTransData(String origTransNo) {
        origTransdata = DaoUtilsStore.getInstance().getmTransDaoUtils().queryByTransNo(TransData.class, origTransNo);
        if (origTransdata != null) {
            if (ETransType.TRANS_SALE != ETransType.valueOf(origTransdata.getTransType())){
                transEnd(new ActionResult(TransResult.ERR_VOID_UNSUPPORT,null));
                return;
            }
            String trStatus = origTransdata.getTransState();
            //如果是已经撤销的不可以撤销
            if (!Component.ETransStatus.NORMAL.toString().equals(trStatus)){
                transEnd(new ActionResult(TransResult.ERR_HAS_VOID,null));
                return;
            }
            copyOrigTransData();
            gotoState(State.TRANS_DETAIL.toString());
        } else {
            transEnd(new ActionResult(TransResult.ERR_NO_TRANS,null));
        }
    }

    // 检查是否需要刷卡和输密码
    private void checkCardAndPin() {
        // 撤销是否需要刷卡
        if (SysParam.Constant.YES.equals(TopApplication.sysParam.get(SysParam.UCTC_VOID))) {
            gotoState(State.CHECK_CARD.toString());
        } else {
            // 撤销不需要刷卡
            transData.setEnterMode(Component.EnterMode.MANAUL);
            checkPin();
        }
    }
    // 检查是否需要输密码
    private void checkPin() {
        // 撤销需要输密码
        if (SysParam.Constant.YES.equals(TopApplication.sysParam.get(SysParam.IPTC_VOID))) {
            gotoState(State.ENTER_PIN.toString());
        } else {
            // 撤销不需要输密码
            transData.setPin("");
            transData.setHasPin(false);
            gotoState(State.ONLINE.toString());
        }
    }
    // 设置原交易记录
    private void copyOrigTransData() {
        transData.setAmount(origTransdata.getAmount());
        transData.setOrigBatchNo(origTransdata.getBatchNo());
        transData.setOrigAuthCode(origTransdata.getAuthCode());
        transData.setOrigRefNo(origTransdata.getRefNo());
        transData.setOrigTransNo(origTransdata.getTransNo());
        transData.setPan(origTransdata.getPan());
        transData.setExpDate(origTransdata.getExpDate());
        transData.setField22(origTransdata.getField22());
        transData.setOrigProcCode(origTransdata.getProcCode());
    }
}
