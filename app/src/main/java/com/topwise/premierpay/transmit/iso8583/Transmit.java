package com.topwise.premierpay.transmit.iso8583;

import com.topwise.manager.AppLog;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.daoutils.DaoUtilsStore;
import com.topwise.premierpay.daoutils.entity.DupTransdata;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.core.TransContext;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.transmit.TransProcessListener;

import java.util.List;

public class Transmit {
    private static final String TAG = TopApplication.APPNANE +Transmit.class.getSimpleName();
    private static Transmit transmit;

    private Transmit() {

    }

    public static Transmit getInstance() {
        if (transmit == null) {
            transmit = new Transmit();
        }

        return transmit;
    }

    public int transmit(TransData transData, TransProcessListener listener) {
        int ret = 0;
        ETransType transType = ETransType.valueOf(transData.getTransType());

        // 处理脚本
        if (transType.isScriptSend()) {
//            int scriptRet = sendScriptResult(listener);
//            if (scriptRet == TransResult.EXPED_TIMEOUT)
//                return scriptRet;
        }

        // 处理冲正
        if (transType.isDupSend()) {
//            int dupRet = sendReversal(listener);
//            Log.d("POSLOGON1", "ret:" + dupRet);
//            // all_unionpay
//            if (dupRet == TransResult.ERR_MAC || ret == TransResult.EXPED_TIMEOUT || ret == TransResult.ERR_ABORTED) {
//                return dupRet;
//            }
        }

        if (listener != null) {
            listener.onUpdateProgressTitle(transType.getTransName());
        }

        // for (int i = 0; i < 3; i++) {
        ret = Online.getInstance().online(transData, listener);
        transData.getTransStatusSum().setResult(ret);
        DaoUtilsStore.getInstance().updateStatus(transData);
        if (ret == TransResult.ERR_HOST_REJECT) {
            return TransResult.ERR_HOST_REJECT;
        } else if (ret != TransResult.SUCC){
            return TransResult.ERR_CONNECT;
        }
       // }

        return TransResult.SUCC;
    }

    /**
     * 冲正处理
     *
     * @return
     */
    public int sendReversal(TransProcessListener listener) {
        List<DupTransdata> dupTransdata = DaoUtilsStore.getInstance().getmDupTransDaoUtils().queryAll();
        AppLog.d("Transmit","sendReversal dupTransdata.size()=== " + dupTransdata.size());
        if (dupTransdata.size() == 0)
            return TransResult.SUCC;

        int ret = 0;
        DupTransdata dupTransdata1 = dupTransdata.get(0);
        TransData transData = Component.transInit(dupTransdata.get(0));
        ETransType eTransType = ETransType.valueOf(transData.getTransType());
        long transNo = transData.getTransNo();
        String dupReason = transData.getReason();
        String dupRetCodeString = "";

        ETransType transType = ETransType.valueOf(transData.getTransType());
        if (
                transType == ETransType.TRANS_PRE_AUTH_VOID || transType == ETransType.TRANS_PRE_AUTH_CMP ||
//                || transType == ETransType.AUTHCMVOID
                        transType == ETransType.TRANS_VOID) {
            transData.setOrigAuthCode(transData.getOrigAuthCode());
        } else {
            transData.setOrigAuthCode(transData.getAuthCode());
        }
        transData.setReversal(true);

        int retry = Integer.parseInt(TopApplication.sysParam.get(SysParam.REVERSL_CTRL));
        if (listener != null) {
            listener.onUpdateProgressTitle(eTransType.getTransName() +" Reversal"); //
        }

        for (int i = 0; i < (retry + 1); i++) {
            ret = Online.getInstance().online(transData, listener);
            if (ret == TransResult.SUCC) {
                String retCode = transData.getResponseCode();
                // 冲正收到响应码12或者25的响应码，应默认为冲正成功
//                if (retCode.equals("00") || retCode.equals("12") || retCode.equals("25")) {
                if (retCode.equals("00")) {
                    boolean delRet = DaoUtilsStore.getInstance().getmDupTransDaoUtils().deleteAll();
                    listener.onShowSuccessMsgWithConfirm("Reversal Success", 3);
                    AppLog.d("Transmit","sendReversal delRet=== " + delRet);
                    return TransResult.SUCC;
                }
                dupRetCodeString = retCode;

                dupTransdata1.setReason(Component.REASON_OTHERS);
                dupTransdata1.setOrigDate(transData.getDate());
                DaoUtilsStore.getInstance().getmDupTransDaoUtils().update(dupTransdata1);
                continue;
            } else if (ret == TransResult.ERR_MAC) {
                // 冲正如果mac错,则跳出循环,下一次联机交易还是要进行冲正
                return TransResult.ERR_MAC;
            }
            if (ret == TransResult.ERR_CONNECT || ret == TransResult.ERR_PACK || ret == TransResult.ERR_SEND) {
                if (listener != null) {
                    listener.onShowMessageWithConfirm(
                            TransResult.getMessage(TransContext.getInstance().getCurrentContext(), ret),
                           8);
                }
                return TransResult.ERR_ABORTED;
            }

            if (ret == TransResult.ERR_RECV) {
                dupTransdata1.setReason(Component.REASON_NO_RECV);
                dupTransdata1.setOrigDate(transData.getDate());
                DaoUtilsStore.getInstance().getmDupTransDaoUtils().update(dupTransdata1);
            }
            continue;
        }
        if (listener != null) {
            listener.onShowMessageWithConfirm(dupRetCodeString,
                    8);
        }
//        DaoUtilsStore.getInstance().getmDupTransDaoUtils().deleteAll();
        return ret;
    }

}
