package com.topwise.premierpay.pack.json;

import android.text.TextUtils;

import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.pack.PackListener;

/**
 * 创建日期：2021/4/13 on 9:41
 * 描述:
 * 作者:  wangweicheng
 */
public class JsonPackPreAuthCompletion extends PackJson {
    public JsonPackPreAuthCompletion(PackListener listener) {
        super(listener);
    }

    @Override
    public String pack(TransData transData) {
        String temp = "";
        ETransType transType = ETransType.valueOf(transData.getTransType());

        //msg
        temp = transType.getMsgType();
        if (!TextUtils.isEmpty(temp))
            sendData.setMsgType(temp);

        temp = transData.getOrigProcCode();
        if (!TextUtils.isEmpty(temp))
            sendData.setF003(temp);

        temp = transData.getAmount();
        if (!TextUtils.isEmpty(temp))
            sendData.setF004(temp);

        //11
        temp = String.format("%06d",transData.getOrigTransNo());
        if (!TextUtils.isEmpty(temp))
            sendData.setF011(temp);

        //12

        //13

        //22
        temp = transData.getField22();
        if (!TextUtils.isEmpty(temp))
            sendData.setF022(temp);

        //25
        temp = transType.getServiceCode();
        if (!TextUtils.isEmpty(temp))
            sendData.setF025(temp);
        //37
        temp = transData.getOrigRefNo();
        if (!TextUtils.isEmpty(temp))
            sendData.setF037(temp);

        //38
        temp = transData.getOrigAuthCode();
        if (!TextUtils.isEmpty(temp))
            sendData.setF038(temp);

        temp = transData.getTermID();
        if (!TextUtils.isEmpty(temp))
            sendData.setF041(temp);


        temp = transData.getMerchID();
        if (!TextUtils.isEmpty(temp))
            sendData.setF042(temp);
        // field 55
        temp = transData.getSendIccData();
        if (!TextUtils.isEmpty(temp)) {
            sendData.setF055(temp);
        }

        //57
        temp = String.format("%06d",transData.getOrigBatchNo());
        if (!TextUtils.isEmpty(temp))
            sendData.setF057(temp);

        //62
        temp = String.format("%06d",transData.getOrigTransNo());
        if (!TextUtils.isEmpty(temp))
            sendData.setF062(temp);

        return pack(false);
    }
}
