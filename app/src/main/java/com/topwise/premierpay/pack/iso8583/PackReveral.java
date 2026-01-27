package com.topwise.premierpay.pack.iso8583;

import com.topwise.premierpay.pack.PackListener;
import com.topwise.premierpay.trans.model.TransData;

/**
 * 创建日期：2021/5/24 on 11:30
 * 描述:
 * 作者:wangweicheng
 */
public class PackReveral extends PackIso8583{

    public PackReveral(PackListener listener) {
        super(listener);
    }

    @Override
    public byte[] pack(TransData transData) {
        try {
            if (transData == null) {
                return null;
            }

            setRevCommonData(transData);

           /* int ret = setBitDataF59(transData);
            if (ret != TransResult.SUCC) {
                return null;
            }*/

//            setBitDataF60(transData);// all_unionpay

            return pack(false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
