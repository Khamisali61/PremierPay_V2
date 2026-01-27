package com.topwise.premierpay.pack.iso8583;

import com.topwise.premierpay.pack.PackListener;
import com.topwise.premierpay.trans.model.TransData;

/**
 * 创建日期：2021/5/25 on 16:44
 * 描述:
 * 作者:wangweicheng
 */
public class PackRefund extends PackIso8583 {

    public PackRefund(PackListener listener) {
        super(listener);
    }

    @Override
    public byte[] pack(TransData transData) {
        try {
            String temp = "";
            if (transData == null) {
                return null;
            }
            setFinancialData(transData);
//            setBitDataF60(transData);

            //Field37
            temp = transData.getOrigRefNo();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("37", temp);
            }

            temp = transData.getOrigAuthCode();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("38", temp);
            }

            return pack(true);

        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //    @Override
//    public byte[] pack(TransData transData) {
//        try {
//            String temp = "";
//
//            if (transData == null) {
//                return null;
//            }
//
//            setFinancialData(transData);
//
//            entity.setFieldValue("37", transData.getOrigRefNo());
//
//            setBitDataF60(transData);
//
//            // field 61 原交易信息
//            String f61 = "";
//            temp = String.format("%06d", transData.getOrigBatchNo());
//            if (temp != null && temp.length() > 0) {
//                f61 += temp;
//            } else {
//                f61 += "000000";
//            }
//            temp = String.format("%06d", transData.getOrigTransNo());
//            if (temp != null && temp.length() > 0) {
//                f61 += temp;
//            } else {
//                f61 += "000000";
//            }
//            temp = transData.getOrigDate();
//            if (temp != null && temp.length() > 0) {
//                f61 += temp;
//            } else {
//                f61 += "0000";
//            }
//
//            entity.setFieldValue("61", f61);
//
//            // field 63
//            temp = transData.getInterOrgCode();
//            if (temp == null || temp.length() == 0) {
//                temp = "000";
//            }
//            entity.setFieldValue("63", temp);
//
//            return pack(true);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
}
