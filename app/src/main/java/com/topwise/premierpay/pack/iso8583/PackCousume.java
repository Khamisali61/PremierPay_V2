package com.topwise.premierpay.pack.iso8583;

import com.topwise.premierpay.pack.PackListener;
import com.topwise.premierpay.trans.model.TransData;

public class PackCousume extends PackIso8583 {

    public PackCousume(PackListener listener) {
        super(listener);
    }

    @Override
    public byte[] pack(TransData transData) {
        try {
            String temp = "";
            if (transData == null) {
                return null;
            }
            setFinancialData(transData);
//            setBitDataF60(transData);
            return pack(true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
