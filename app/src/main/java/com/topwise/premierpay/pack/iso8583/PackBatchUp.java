package com.topwise.premierpay.pack.iso8583;

import com.topwise.toptool.api.packer.Iso8583Exception;
import com.topwise.premierpay.pack.PackListener;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransResult;

/**
 * 创建日期：2021/6/1 on 17:57
 * 描述:
 * 作者:wangweicheng
 */
public class PackBatchUp extends PackIso8583{
    public PackBatchUp(PackListener listener) {
        super(listener);
    }

    @Override
    public byte[] pack(TransData transData) {
        if (transData == null) {
            return null;
        }

        int ret = setMandatoryData(transData);
        if (ret != TransResult.SUCC) {
            return null;
        }
        try {
            entity.setFieldValue("11", String.valueOf(transData.getTransNo()));
        } catch (Iso8583Exception e) {
            e.printStackTrace();
        }
        setBitDataF48(transData);
        setBitDataF60(transData);
        return pack(false);
    }
}
