package com.topwise.premierpay.pack.iso8583;

import com.topwise.premierpay.pack.PackListener;
import com.topwise.premierpay.trans.model.TransData;

/**
 * 创建日期：2021/5/17 on 11:26
 * 描述:
 * 作者:wangweicheng
 */
public class PackBalance extends PackIso8583 {

    public PackBalance(PackListener listener) {
        super(listener);
    }

    @Override
    public byte[] pack(TransData transData) {
        try {
            if (transData == null) {
                return null;
            }
            setFinancialData(transData);
            setBitDataF60(transData);
            return pack(true);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
