package com.topwise.premierpay.pack.iso8583;

import com.topwise.premierpay.pack.PackListener;
import com.topwise.premierpay.trans.model.TransData;

/**
 * 创建日期：2021/5/24 on 10:07
 * 描述:
 * 作者:wangweicheng
 */
public class PackVoid extends PackIso8583{
    public PackVoid(PackListener listener) {
        super(listener);
    }

    @Override
    public byte[] pack(TransData transData) {
        try {
            if (transData == null) {
                return null;
            }

            setVoidCommonData(transData);
            setBitDataF60(transData);

            String f61 = "";
            String temp = String.format("%06d", transData.getOrigBatchNo());
            if (temp != null && temp.length() > 0) {
                f61 += temp;
            } else {
                f61 += "000000";
            }
            temp = String.format("%06d", transData.getOrigTransNo());
            if (temp != null && temp.length() > 0) {
                f61 += temp;
            } else {
                f61 += "000000";
            }

            entity.setFieldValue("61", f61);

            return pack(true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
