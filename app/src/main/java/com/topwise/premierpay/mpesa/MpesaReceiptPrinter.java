package com.topwise.premierpay.mpesa;

import com.topwise.premierpay.trans.receipt.AReceiptPrint;

public class MpesaReceiptPrinter extends AReceiptPrint {

    private static MpesaReceiptPrinter instance;
    private MpesaReceiptGenerator generator;

    private MpesaReceiptPrinter() {
    }

    public synchronized static MpesaReceiptPrinter getInstance() {
        if (instance == null) {
            instance = new MpesaReceiptPrinter();
        }
        return instance;
    }

    public void print(MpesaReceiptGenerator generator) {
        this.generator = generator;
        this.receiptNum = 1; // Always print 1 copy for now
        // AReceiptPrint.printBitmap() will check TopApplication.usdkManage.getPrinter()
        // We can call printBitmap(null) just to trigger the internal logic since printBitmap(bitmap) is protected?
        // Wait, printBitmap(Bitmap) is protected in AReceiptPrint.
        // So we can call it here since we extend it.

        if (generator != null) {
             printBitmap(generator.generateBinmap());
        }
    }
}