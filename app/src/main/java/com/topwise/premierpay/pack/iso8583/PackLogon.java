package com.topwise.premierpay.pack.iso8583;

import android.text.TextUtils;

import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.pack.PackListener;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.trans.model.TransData;

public class PackLogon extends PackIso8583 {
    private static final String TAG =  TopApplication.APPNANE + PackLogon.class.getSimpleName();

    public PackLogon(PackListener listener) {
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
            temp = String.valueOf(transData.getTransNo());
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("11", temp);
            }
            setBitDataF60(transData);
            String sn = Device.getSn();
            if (sn != null) {
                String len = String.format("%02d", 4 + sn.length());
                entity.setFieldValue("62", "Sequence No" + len + "9515" + sn); // 1234为随便写的认证编号，以前每款设备过认证时银联会分配一个
            }

            temp = transData.getOper();
            if (!TextUtils.isEmpty(temp)) {
                temp = "01";
            }
            String f63 = temp + " ";
//            String len = String.format("%04d",f63.length());
//            String s = TopApplication.convert.bcdToStr(f63.getBytes());
//            byte[] bytes = TopApplication.convert.strToBcd(len + s, IConvert.EPaddingPosition.PADDING_RIGHT);
            entity.setFieldValue("63", f63);
//            entity.setFieldValue("63", bytes);

//            entity.setFieldValue("65", topApplication.convert.strToBcd("1234567890132456", IConvert.EPaddingPosition.PADDING_LEFT));
            return pack(false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
