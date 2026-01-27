package com.topwise.premierpay.pack.json;

import android.text.TextUtils;

import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.pack.PackListener;

public class JsonPackHandshake extends PackJson {

    public JsonPackHandshake(PackListener listener) {
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
                sendData.setF070("301");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return pack(false);
    }
}