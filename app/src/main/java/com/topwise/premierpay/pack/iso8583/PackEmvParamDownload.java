package com.topwise.premierpay.pack.iso8583;

import com.topwise.toptool.api.convert.IConvert;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.pack.PackListener;
import com.topwise.premierpay.trans.model.TransData;

/**
 * 创建日期：2021/5/17 on 9:32
 * 描述:
 * 作者:wangweicheng
 */
public class PackEmvParamDownload extends PackIso8583 {
    public PackEmvParamDownload(PackListener listener) {
        super(listener);
    }

    @Override
    public byte[] pack(TransData transData) {
        try {
            String temp = "";
            if (transData == null) {
                return null;
            }

            setMandatoryData(transData);
            setBitDataF60(transData);

            temp = transData.getField62();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("62", TopApplication.convert.strToBcd(temp, IConvert.EPaddingPosition.PADDING_LEFT));
            }

            return pack(false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
