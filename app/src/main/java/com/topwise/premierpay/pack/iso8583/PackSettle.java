package com.topwise.premierpay.pack.iso8583;

import com.topwise.toptool.api.packer.Iso8583Exception;
import com.topwise.premierpay.pack.PackListener;
import com.topwise.premierpay.trans.model.TransData;

/**
 * 创建日期：2021/6/1 on 17:54
 * 描述:
 * 作者:wangweicheng
 */
public class PackSettle extends PackIso8583{
    public PackSettle(PackListener listener) {
        super(listener);
    }

    @Override
    public byte[] pack(TransData transData) {
        if (transData == null) {
            return null;
        }

        setMandatoryData(transData);

        try {
            // field 11
            String temp = String.valueOf(transData.getTransNo());
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("11", temp);
            }
            // field 48
            setBitDataF48(transData);
            // field 49
            entity.setFieldValue("49", "156");
            setBitDataF60(transData);
            temp = transData.getOper();
            if (temp == null || temp.length() == 0) {
                temp = "01";
            }
            String f63 = temp + " ";
            entity.setFieldValue("63", f63);
        } catch (Iso8583Exception e) {

            e.printStackTrace();
        }

        return pack(false);
    }
}
