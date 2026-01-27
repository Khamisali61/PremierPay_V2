package com.topwise.premierpay.pack.iso8583;

import com.topwise.premierpay.pack.PackListener;
import com.topwise.premierpay.trans.model.TransData;

/**
 * 创建日期：2021/5/27 on 10:05
 * 描述:
 * 作者:wangweicheng
 */
public class PackQrSale extends PackIso8583 {

    public PackQrSale(PackListener listener) {
        super(listener);
    }

    @Override
    public byte[] pack(TransData transData) {
        if (transData == null) {
            return null;
        }

        setFinancialData(transData);

        // 处理59域数据
//        int ret = setBitDataF59(transData);
//        if (ret!= TransResult.SUCC) {
//            return null;
//        }

        // 扫码的60域不用处理
        setBitDataF60(transData);

        return pack(true);
    }

}