package com.topwise.premierpay.pack.iso8583;

import android.text.TextUtils;

import com.topwise.manager.AppLog;
import com.topwise.premierpay.pack.PackListener;
import com.topwise.premierpay.trans.model.TransData;

public class PackAuthVoid extends PackIso8583{
    public PackAuthVoid(PackListener listener) {
        super(listener);
    }

    @Override
    public byte[] pack(TransData transData) {
        try {
            String temp = "";
            if (transData == null) {
                return null;
            }
            AppLog.d("PackAuthVoid","OrigAuthCode=== start pack");
            setFinancialData(transData);

            temp = transData.getOrigRefNo();
            if (temp != null && !TextUtils.isEmpty(temp)) {
                AppLog.d("PackAuthVoid","OrigAuthCode=== " + temp);
                entity.setFieldValue("37", temp);
            }

            temp = transData.getOrigAuthCode();
            if (temp != null && !TextUtils.isEmpty(temp)) {
                AppLog.d("PackAuthVoid","OrigAuthCode=== " + temp);
                entity.setFieldValue("38", temp);
            }
//            setBitDataF60(transData);
            return pack(true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
