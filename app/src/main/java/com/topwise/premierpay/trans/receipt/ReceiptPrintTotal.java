package com.topwise.premierpay.trans.receipt;

import com.topwise.premierpay.daoutils.entity.TotaTransdata;

/**
 * 创建日期：2021/6/1 on 11:39
 * 描述:
 * 作者:wangweicheng
 */
public class ReceiptPrintTotal extends AReceiptPrint {
    private static ReceiptPrintTotal receiptPrintTotal;

    private ReceiptPrintTotal() {

    }

    public synchronized static ReceiptPrintTotal getInstance() {
        if (receiptPrintTotal == null) {
            receiptPrintTotal = new ReceiptPrintTotal();
        }

        return receiptPrintTotal;
    }

    public int print(String title, TotaTransdata transTotal, PrintListener listener){

        this.listener = listener;

        if (listener != null) {
//            listener.onShowMessage(null, TopApplication.mApp.getString(R.string.print_wait));
        }
        ReceiptGeneratorTotal receiptGeneratorTotal = new ReceiptGeneratorTotal(title, transTotal);
        printBitmap(receiptGeneratorTotal.generateBitmap());
        if (listener != null) {
            listener.onEnd();
        }
        return 0;

    }
}
