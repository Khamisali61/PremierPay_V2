package com.topwise.premierpay.pack.json;

import android.text.TextUtils;

import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.pack.PackListener;

/**
 * 创建日期：2021/4/13 on 9:16
 * 描述:
 * 作者:  wangweicheng
 */
public class JsonPackPreAuth extends PackJson {
    public JsonPackPreAuth(PackListener listener) {
        super(listener);
    }

    @Override
    public String pack(TransData transData) {
        String temp = "";

        try {
            ETransType eTransType = ETransType.valueOf(transData.getTransType());
            int ret = setFinancialData(transData);
            temp = "20"; //代表 52 53 都是使用 DUKPT 加密
            if (transData.isEncTrack()){
                temp = "22";
            }
            if (!TextUtils.isEmpty(temp))
                sendData.setF047(temp);

            //57
            temp = String.format("%06d",transData.getBatchNo());
            if (!TextUtils.isEmpty(temp))
                sendData.setF057(temp);

            //62
            temp = String.format("%06d",transData.getTransNo());
            if (!TextUtils.isEmpty(temp))
                sendData.setF062(temp);


            return pack(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
