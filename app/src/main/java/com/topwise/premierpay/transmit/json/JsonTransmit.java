package com.topwise.premierpay.transmit.json;

import android.text.TextUtils;

import com.topwise.manager.utlis.DataUtils;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.daoutils.DaoUtilsStore;
import com.topwise.premierpay.daoutils.entity.DupTransdata;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.ResponseCode;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.transmit.TransProcessListener;
import com.topwise.premierpay.transmit.iso8583.Transmit;

import java.util.List;

/**
 * 创建日期：2021/4/7 on 16:57
 * 描述:
 * 作者:  wangweicheng
 */
public class JsonTransmit {
    private static final String TAG =  TopApplication.APPNANE + Transmit.class.getSimpleName();
    private static JsonTransmit transmit;

    private JsonTransmit() {

    }

    public static JsonTransmit getInstance() {
        if (transmit == null) {
            transmit = new JsonTransmit();
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
        //  for (int i = 0; i < 3; i++){
        ret = JsonOnline.getInstance().online(transData, listener);
        if (ret == TransResult.SUCC){
            String retCode = transData.getResponseCode();

            if (!"00".equals(retCode)){
                ResponseCode responseCode = TopApplication.rspCode.parse(retCode);
                if (listener != null){
                    Device.beepFail();
                    String field46 = transData.getField46();
                    if (!DataUtils.isNullString(field46)) {
                        listener.onShowFailWithConfirm(field46,Component.FAILED_DIALOG_SHOW_TIME);
                    } else {
                        listener.onShowFailWithConfirm(
                                "Err code:"
                                        + responseCode.getCode()
                                        + "\n"
                                        + responseCode.getMessage(),Component.FAILED_DIALOG_SHOW_TIME);
                    }
                }
                return TransResult.ERR_ABORTED;
            }
        }
        // }
        return ret;
    }

    /**
     * 冲正处理
     *
     * @return
     */
    public int sendReversal(TransProcessListener listener){
        List<DupTransdata> dupTransdata = DaoUtilsStore.getInstance().getmDupTransDaoUtils().queryAll();
        if (dupTransdata.size() == 0)
            return TransResult.SUCC;

        int ret = TransResult.SUCC;
        TransData transData = Component.transInit(dupTransdata.get(0));
        ETransType eTransType = ETransType.valueOf(transData.getTransType());
        //set
        listener.onUpdateProgressTitle(eTransType.getTransName() + " Reversal"); //

        String reversl = TopApplication.sysParam.get(SysParam.REVERSL_CTRL);
        if (TextUtils.isEmpty(reversl)) reversl = "3"; //

//        for (int i = 0; i < (Integer.valueOf(reversl) + 1); i++){
            ret = JsonOnline.getInstance().online(transData, listener);
//            //to do 是否需要继续冲正
//        }
        if (ret == TransResult.SUCC) {
            String retCode = transData.getResponseCode();
            // 冲正收到响应码12或者25的响应码，应默认为冲正成功
//            if (retCode.equals("00") || retCode.equals("12") || retCode.equals("25")) {
//                boolean deleteAll = DaoUtilsStore.getInstance().getmDupTransDaoUtils().deleteAll();
//                AppLog.i(TAG, "DupTransDao deleteAll :" + deleteAll);
//                return TransResult.SUCC;
//            }
        }
        boolean deleteAll = DaoUtilsStore.getInstance().getmDupTransDaoUtils().deleteAll();
        return TransResult.SUCC;
        //==
//        return ret;
    }

    //脚本上送

    //脱机交易上送

    //联机交易电子签名上送

    //离线类交易电子签名上送

    //重新上送失败签名
}
