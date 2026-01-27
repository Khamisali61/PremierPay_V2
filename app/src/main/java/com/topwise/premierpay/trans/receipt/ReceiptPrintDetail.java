package com.topwise.premierpay.trans.receipt;

import com.topwise.premierpay.trans.model.TransData;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：2021/5/28 on 17:57
 * 描述:
 * 作者:wangweicheng
 */
public class ReceiptPrintDetail extends AReceiptPrint {

    private ReceiptPrintDetail() {

    }

    public static ReceiptPrintDetail getInstance() {
        return ReceiptPrintDetail.SingletonHolder.sInstance;
    }

    //静态内部类
    private static class SingletonHolder {
        private static final ReceiptPrintDetail sInstance = new ReceiptPrintDetail();
    }

    public int print(String title, List<TransData> list, PrintListener listener) {
        int ret = 0;
        ReceiptGeneratorDetail receiptGeneratorDetail = new ReceiptGeneratorDetail();

        List<TransData> details = new ArrayList<TransData>();
        boolean isFirst = true;
        int count = 0;
        for (TransData data : list) {
            details.add(data);
            count++;
            if (count == list.size() || count % 20 == 0) {
                if (count != list.size() && count % 20 == 0) {
                    receiptGeneratorDetail = new ReceiptGeneratorDetail(details,false,title);
                } else {
                    // 最后一次打印交易详情
                    receiptGeneratorDetail = new ReceiptGeneratorDetail(details,true,title);
                }
                this.listener = listener;
                if (isFirst) {
                    ret = printBitmap(receiptGeneratorDetail.generateBitmapHead());
                    isFirst = false;
                } else {
                    ret = printBitmap(receiptGeneratorDetail.generateBitmap());
                }
                if (ret != 0) {
                    if (listener != null) {
                        listener.onEnd();
                    }
                    return ret;
                }
                details = null;
                details = new ArrayList<TransData>();
            }
        }
        if (listener != null) {
            listener.onEnd();
        }
        return ret;
    }
}
