package com.topwise.premierpay.pack.iso8583;

import com.topwise.premierpay.pack.PackListener;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.TransData;

/**
 * 创建日期：2021/6/3 on 15:13
 * 描述:
 * 作者:wangweicheng
 */
public class PackBatchUpNotice extends PackIso8583 {

    public PackBatchUpNotice(PackListener listener) {
        super(listener);
    }

    @Override
    public byte[] pack(TransData transData) {
        if (transData == null) {
            return null;
        }

        // 当前交易类型，通知类批上送
        String transType = transData.getTransType();
        // 原交易类型
        String origTransType = transData.getOrigTransType();
        // 切换交易类型(退货， 预授权完成通知)
        transData.setTransType(origTransType);
        if (origTransType == null) {
            return null;
        }
        byte[] buf = null;
        byte[] temp = ETransType.valueOf(origTransType).getpackager(listener).pack(transData);
        if (temp != null) {
            buf = new byte[temp.length - 8];
            System.arraycopy(temp, 0, buf, 0, temp.length - 8);
            // 把0220转成0320
            buf[11] = 0x03;
            // 去掉bit64
            buf[11 + 2 + 8 - 1] &= 0xfe;
        }
        // 恢复交易类型
        transData.setTransType(transType);

        return buf;
    }

}
