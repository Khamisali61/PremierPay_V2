package com.topwise.premierpay.pack.json;

import android.text.TextUtils;

import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.pack.PackListener;

/**
 * 创建日期：2021/4/13 on 17:08
 * 描述:
 * 作者:  wangweicheng
 */
public class JsonPackVoid extends PackJson {
    public JsonPackVoid(PackListener listener) {
        super(listener);
    }

    @Override
    public String pack(TransData transData) {
        //
        String temp = "";
        ETransType transType = ETransType.valueOf(transData.getTransType());

        //msg
        temp = transType.getMsgType();
        if (!TextUtils.isEmpty(temp))
            sendData.setMsgType(temp);

        temp = "000000";
        if (!TextUtils.isEmpty(temp))
            sendData.setF003(temp);

        temp = transData.getAmount();
        if (!TextUtils.isEmpty(temp))
            sendData.setF004(temp);

        //11
        temp = String.format("%06d",transData.getTransNo());
        if (!TextUtils.isEmpty(temp))
            sendData.setF011(temp);

        //12
        temp = transData.getTime();
        if (!TextUtils.isEmpty(temp))
            sendData.setF012(temp);
        //13
        temp = transData.getDate();
        if (!TextUtils.isEmpty(temp))
            sendData.setF013(temp);
        //22
//        temp = transData.getField22();
//        if (!TextUtils.isEmpty(temp))
//            sendData.setF022(temp);
        //37
        temp = transData.getOrigRefNo();
        if (!TextUtils.isEmpty(temp))
            sendData.setF037(temp);

        //39
//        17 - User Cancellation
//        91 - Transaction TimedOut
//        E1 - ARPC Failed
//        E2 - No AAC /TC received from Card for 2GAC
//        22 - Hardware Failure.

        sendData.setF039("17");

        temp = transData.getTermID();
        if (!TextUtils.isEmpty(temp))
            sendData.setF041(temp);


        temp = transData.getMerchID();
        if (!TextUtils.isEmpty(temp))
            sendData.setF042(temp);
        // field 55
        temp = transData.getSendIccData();
        if (!TextUtils.isEmpty(temp)) {
//            sendData.setF055(temp);
        }

        //57
        temp = transData.getField57();
        if (!TextUtils.isEmpty(temp))
            sendData.setF057(temp);

        //62
        temp = transData.getField62();
        if (!TextUtils.isEmpty(temp))
            sendData.setF062(temp);


        return pack(false);
    }
}
