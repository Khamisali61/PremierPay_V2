package com.topwise.premierpay.pack.json;

import android.text.TextUtils;

import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.pack.PackListener;

public class JsonPackSale extends PackJson {
    public JsonPackSale(PackListener listener) {
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


            //54 cash amount
//            Account Type n 2 00
//            Amount Type n 2 01 – Ledger Balance 02 – Available Balance 40 – Cash @ POS, Sale with Cash 56 - TIP
//            Currency Code n 3 356 Sign x 1 C or D
//            Amount n 12 Amount – 100.00 will be represented as 000000010000
            if (eTransType == ETransType.TRANS_SALE_WITH_CASH ||
                    eTransType == ETransType.TRANS_CASH){ //需要54
                temp =  transData.getCashAmount();
                if (!TextUtils.isEmpty(temp)){
                    String formatAmount = "0040156D" + String.format("%012d", Long.valueOf(temp));
                    sendData.setF054(formatAmount);
                }

            }
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
