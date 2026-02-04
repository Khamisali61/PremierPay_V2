package com.topwise.premierpay.trans.receipt;

import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransResult;

/**
 * 创建日期：2021/4/2 on 14:11
 * 描述:
 * 作者:  wangweicheng
 */
public class ReceiptPrintTrans extends AReceiptPrint {

    private static ReceiptPrintTrans receiptPrinterTrans;

    private ReceiptPrintTrans() {

    }

    public synchronized static ReceiptPrintTrans getInstance() {
        if (receiptPrinterTrans == null) {
            receiptPrinterTrans = new ReceiptPrintTrans();
        }

        return receiptPrinterTrans;
    }

    public int print(TransData transData, boolean isRePrint, PrintListener listener) {
        int ret = 0;
        this.listener = listener;

        receiptNum = getVoucherNum();
        if (isRePrint || transData.getTransresult() != TransResult.SUCC)
            receiptNum = 1;

   //     if (listener != null)
//            listener.onShowMessage(null, FinancialApplication.mApp.getString(R.string.print_wait));

        for (int i = 0; i < receiptNum; i++) {
            ReceiptGeneratorTrans receiptGeneratorTrans = new ReceiptGeneratorTrans(transData, i, receiptNum, isRePrint);

            if (TopApplication.sysParam.getInt(SysParam.DEVICE_MODE) == 0) {
                ret = printBitmap(receiptGeneratorTrans.generateBinmap());
            } else {
                receiptGeneratorTrans.printRemote();
                ret = 0;
            }
            if (ret == -1) {
                break;
            }
        }
        if (listener != null) {
            listener.onEnd();
        }

        this.listener = null;
        return TransResult.SUCC;
    }

}
