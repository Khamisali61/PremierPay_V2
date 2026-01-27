package com.topwise.premierpay.pack.iso8583;

import com.topwise.toptool.api.packer.Iso8583Exception;
import com.topwise.premierpay.pack.PackListener;
import com.topwise.premierpay.trans.model.TransData;

/**
 * 创建日期：2021/6/3 on 9:28
 * 描述:
 * 作者:wangweicheng
 */
public class PackOfflineTransSend extends PackIso8583 {

    public PackOfflineTransSend(PackListener listener) {
        super(listener);
    }

    @Override
    public byte[] pack(TransData transData) {
        if (transData == null) {
            return null;
        }

        try {
            String temp = "";
            setFinancialData(transData);
            // field 60
            setBitDataF60(transData);
            // field 63
            temp = transData.getInterOrgCode();
            if (temp == null || temp.length() == 0) {
                temp = "000";
            }
            entity.setFieldValue("63", temp);
        } catch (Iso8583Exception e) {
            e.printStackTrace();
        }

        return pack(true);
    }
}