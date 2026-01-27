package com.topwise.premierpay.pack.json;

import android.text.TextUtils;

import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.pack.PackListener;

/**
 * 创建日期：2021/4/13 on 9:48
 * 描述:
 * 作者:  wangweicheng
 */
public class JsonPackLstStatus extends PackJson {
    public JsonPackLstStatus(PackListener listener) {
        super(listener);
    }

    @Override
    public String pack(TransData transData) {
        String temp = "";

        try {
            ETransType eTransType = ETransType.valueOf(transData.getTransType());

            temp = eTransType.getMsgType();
            if (!TextUtils.isEmpty(temp))
                sendData.setMsgType(temp);

            //3
            temp = eTransType.getProcCode();
            if (!TextUtils.isEmpty(temp))
                sendData.setF003(temp);

            //11
            temp = String.format("%06d",transData.getTransNo());
            if (!TextUtils.isEmpty(temp))
                sendData.setF011(temp);

            //41
            temp = transData.getTermID();
            if (!TextUtils.isEmpty(temp))
                sendData.setF041(temp);

            //42
            temp = transData.getMerchID();
            if (!TextUtils.isEmpty(temp))
                sendData.setF042(temp);

            //57
            temp = String.format("%06d",transData.getBatchNo());
            if (!TextUtils.isEmpty(temp))
                sendData.setF057(temp);

            //62
            temp = String.format("%06d",transData.getOrigTransNo());
            if (!TextUtils.isEmpty(temp))
                sendData.setF062(temp);


            return pack(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
