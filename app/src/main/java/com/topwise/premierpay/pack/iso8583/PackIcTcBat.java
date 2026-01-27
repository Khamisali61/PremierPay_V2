package com.topwise.premierpay.pack.iso8583;

import com.topwise.toptool.api.convert.IConvert;
import com.topwise.toptool.api.packer.Iso8583Exception;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.pack.PackListener;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransResult;

/**
 * 创建日期：2021/6/3 on 9:52
 * 描述:
 * 作者:wangweicheng
 */
public class PackIcTcBat extends PackIso8583 {

    public PackIcTcBat(PackListener listener) {
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
            entity.setFieldValue("2", transData.getPan());
            entity.setFieldValue("4", transData.getAmount());
            entity.setFieldValue("11", String.valueOf(transData.getTransNo()));
            entity.setFieldValue("22", getInputMethod(transData.getEnterMode(), transData.getHasPin()));
            entity.setFieldValue("23", transData.getCardSerialNo());
            String temp = transData.getSendIccData();
            if (temp != null) {
                entity.setFieldValue("55", TopApplication.convert.strToBcd(temp, IConvert.EPaddingPosition.PADDING_LEFT));
            }
            entity.setFieldValue("60", transData.getField60());
            entity.setFieldValue("62", transData.getField62());
        } catch (Iso8583Exception e) {
            e.printStackTrace();
            return null;
        }

        return pack(false);
    }
}
