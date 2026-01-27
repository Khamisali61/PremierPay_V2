package com.topwise.premierpay.pack.iso8583;

import com.topwise.premierpay.pack.PackListener;
import com.topwise.premierpay.trans.model.TransData;

/**
 * 创建日期：2021/5/27 on 13:38
 * 描述:
 * 作者:wangweicheng
 */
public class PackQrvoid extends PackIso8583 {
    public PackQrvoid(PackListener listener) {
        super(listener);
    }

    @Override
    public byte[] pack(TransData transData) {
        try {
            if (transData == null) {
                return null;
            }

            setVoidCommonData(transData);

           /* int ret = setBitDataF59(transData);
            if (ret!= TransResult.SUCC) {
                return null;
            }*/

            setBitDataF60(transData);

            return pack(true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

