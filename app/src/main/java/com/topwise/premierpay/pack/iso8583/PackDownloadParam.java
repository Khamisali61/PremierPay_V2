package com.topwise.premierpay.pack.iso8583;

import com.topwise.premierpay.pack.PackListener;
import com.topwise.premierpay.trans.model.TransData;

/**
 * 创建日期：2021/5/17 on 10:44
 * 描述:
 * 作者:wangweicheng
 */
public class PackDownloadParam extends PackIso8583 {
    public PackDownloadParam(PackListener listener) {
        super(listener);
    }

    @Override
    public byte[] pack(TransData transData) {
        try {
            if (transData == null) {
                return null;
            }

            setMandatoryData(transData);
            setBitDataF60(transData);
            return pack(false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
