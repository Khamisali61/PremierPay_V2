package com.topwise.premierpay.pack.json;

import android.text.TextUtils;

import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.pack.PackListener;

public class JsonPackLogOn extends PackJson {
    public JsonPackLogOn(PackListener listener) {
        super(listener);
    }

    @Override
    public String pack(TransData transData) {
        String temp = "";
        try {
            ETransType transType = ETransType.valueOf(transData.getTransType());
            temp = transType.getMsgType();
            if (!TextUtils.isEmpty(temp))
                sendData.setMsgType(temp);
            temp = transType.getProcCode();
            if (!TextUtils.isEmpty(temp))
                sendData.setF003(temp);
            temp = String.format("%06d",transData.getTransNo());
            if (!TextUtils.isEmpty(temp))
                sendData.setF011(temp);
            temp = transData.getTermID();
            if (!TextUtils.isEmpty(temp))
                sendData.setF041(temp);
            temp = transData.getMerchID();
            if (!TextUtils.isEmpty(temp))
                sendData.setF042(temp);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return pack(false);
    }
}
