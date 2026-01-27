package com.topwise.premierpay.trans;

import android.content.Context;
import android.os.Handler;
import android.text.InputType;

import com.topwise.manager.AppLog;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.daoutils.DaoUtilsStore;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.action.ActionDispTransDetail;
import com.topwise.premierpay.trans.action.ActionInputData;
import com.topwise.premierpay.trans.action.ActionInputpwd;
import com.topwise.premierpay.trans.action.ActionTransOnline;
import com.topwise.premierpay.trans.action.ActionTransState;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ATransaction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.State;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.utils.Utils;

import java.util.LinkedHashMap;

/**
 * 创建日期：2021/5/27 on 10:58
 * 描述:
 * 作者:wangweicheng
 */
public class TransQrVoid extends BaseTrans{
    private String title = "";
    private TransData origTransData;

    public TransQrVoid(Context context, Handler handler, ATransaction.TransEndListener transListener) {
        super(context, handler, ETransType.TRANS_QR_VOID, transListener);
        title = ETransType.TRANS_QR_VOID.getTransName().toUpperCase();
    }

    @Override
    public void onActionResult(String currentState, ActionResult result) {
        State state = State.valueOf(currentState);
        int ret = result.getRet();
        AppLog.d("onActionResult","ret= " + ret + " state= " + state.toString());
        transData.setTransresult(ret);

        if (ret != TransResult.SUCC && state != State.ONLINE) {
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
                String qrScan = (String)result.getData();
                transData.setOrigQrCode(qrScan);
                transData.setEnterMode(Component.EnterMode.QR);
                validateorigTransData(qrScan);
                break;
            case TRANS_DETAIL:
                gotoState(State.ONLINE.toString());
                break;
            case ONLINE:
                if ("00".equals(transData.getResponseCode())){
                    origTransData.setTransState(Component.ETransStatus.VOID.toString());
                    boolean update = DaoUtilsStore.getInstance().getmTransDaoUtils().update(origTransData);
                }
                gotoState(State.TRANS_STATE.toString());
                break;
            default:
                transEnd(result);
                break;
        }
    }

    private void validateorigTransData(String qrScan) {

        origTransData = DaoUtilsStore.getInstance().getmTransDaoUtils().queryByTransNo(TransData.class, qrScan);

        if (origTransData == null) {
            // 交易不存在
            transEnd(new ActionResult(TransResult.ERR_NO_ORIG_TRANS, null));
            return;
        }
        String trType = origTransData.getTransType();
        if (!trType.equals(ETransType.TRANS_QR_SALE.toString())) {
            transEnd(new ActionResult(TransResult.ERR_VOID_UNSUPPORT, null));
            return;
        }
        String trStatus = origTransData.getTransState();
        // 已撤销交易，不能重复撤销/已调整交易不可撤销
        if (trStatus.equals(Component.ETransStatus.VOID.toString())) {
            transEnd(new ActionResult(TransResult.ERR_HAS_VOID, null));
            return;
        } else if (trStatus.equals(Component.ETransStatus.ADJUST.toString())) {
            transEnd(new ActionResult(TransResult.ERR_VOID_UNSUPPORT, null));
            return;
        }

        copyorigTransData();
        gotoState(State.TRANS_DETAIL.toString());
    }
    // 设置原交易记录
    private void copyorigTransData() {
        transData.setAmount(origTransData.getAmount());
        transData.setQrCode(origTransData.getQrCode());
        transData.setOrigBatchNo(origTransData.getBatchNo());
        transData.setOrigAuthCode(origTransData.getAuthCode());
        transData.setOrigRefNo(origTransData.getRefNo());
        transData.setOrigTransNo(origTransData.getTransNo());
        transData.setPan(origTransData.getPan());
        transData.setExpDate(origTransData.getExpDate());
        transData.setOrigQrVoucher(origTransData.getQrVoucher());
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
                        .setInputLine1(getCurrentContext().getString(R.string.set_please_enter_old_qr_voucher ),
                                InputType.TYPE_CLASS_NUMBER,6,6,true);
            }
        },handler));

        bind(State.TRANS_DETAIL.toString(), new ActionDispTransDetail(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                String transType = ETransType.valueOf(origTransData.getTransType()).getTransName();

                String formater = Utils.ftoYuan(origTransData.getAmount());

                String amount = context.getString(R.string.trans_amount_default, formater);

                // 日期时间
                String temp = Utils.getTransDataTime(origTransData);

                LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                map.put(context.getString(R.string.trans_type), transType.toUpperCase());
                map.put(context.getString(R.string.merchine_id), origTransData.getMerchID());
                map.put(context.getString(R.string.term_id), origTransData.getTermID());
                map.put(context.getString(R.string.trans_amount_0), amount);
                map.put(context.getString(R.string.old_pay_voucher_num), transData.getOrigQrVoucher());
                map.put(context.getString(R.string.trans_Rrn_id), transData.getOrigRefNo());

                map.put(context.getString(R.string.trans_data_time), temp);
                ((ActionDispTransDetail) action).setParam(getCurrentContext(), handler,
                        title, map,true);
            }
        }));

        bind(State.ONLINE.toString(), new ActionTransOnline(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionTransOnline) action).setParam(getCurrentContext(),transData);
            }
        }));

        bind(State.TRANS_STATE.toString(),  new ActionTransState(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {

                ((ActionTransState) action).setParam(getCurrentContext(),title,transData);
            }
        }));

        gotoState(State.CHECK_PWD.toString());
    }
}
