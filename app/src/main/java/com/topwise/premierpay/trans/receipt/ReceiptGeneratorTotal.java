package com.topwise.premierpay.trans.receipt;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;

import com.topwise.iamge.api.IImgProcessing;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.daoutils.entity.TotaTransdata;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.utils.Utils;

/**
 * 创建日期：2021/6/1 on 11:35
 * 描述:
 * 作者:wangweicheng
 */
public class ReceiptGeneratorTotal implements IReceiptGenerator{
    public ReceiptGeneratorTotal(String title, TotaTransdata transTotal) {
        this.title = title;
        this.transTotal = transTotal;
    }
    String title;
    TotaTransdata transTotal;
    @Override
    public Bitmap generateBinmap() {
        return null;
    }

    @Override
    public Bitmap generateBitmap() {
        IImgProcessing imgProcessing1 = TopApplication.topImage.getImgProcessing();
        IImgProcessing.IPage page = imgProcessing1.createPage();
        Context context = TopApplication.mApp;
        page.setTypeFace(TYPE_FACE);
        SysParam sysParam = TopApplication.sysParam;
        String temp = "";

        // 凭单抬头
        page.addLine().addUnit(title, FONT_BIG, IImgProcessing.IPage.EAlign.CENTER);

        // 商户编号
        page.addLine().addUnit(context.getString(R.string.receipt_merchant_code) + transTotal.getMerchantID(),
                FONT_NORMAL);

        // 终端编号/操作员号
        page.addLine().addUnit(context.getString(R.string.receipt_terminal_code_space) + transTotal.getTerminalID(),
                FONT_NORMAL);
        temp =transTotal.getOperatorID() != null ? transTotal.getOperatorID() : "01";
        page.addLine().addUnit(context.getString(R.string.receipt_oper_id_space) + temp ,
                FONT_NORMAL);

        // 批次号
        page.addLine().addUnit(context.getString(R.string.receipt_batch_num_space)
                + String.format("%06d", Long.parseLong(transTotal.getBatchNo())), FONT_NORMAL);

        // 日期时间
        String date = Device.getDateTime();


        page.addLine().addUnit(context.getString(R.string.receipt_date) + date, FONT_NORMAL);
        page.addLine().addUnit("TYPE", FONT_NORMAL, (float) 1).addUnit("NUMBER", FONT_NORMAL, IImgProcessing.IPage.EAlign.CENTER, (float) 1)
                .addUnit("AMOUNT", FONT_NORMAL, IImgProcessing.IPage.EAlign.RIGHT, (float) 1);

        page.addLine().addUnit("----------------------------------", FONT_SMALL,IImgProcessing.IPage.EAlign.CENTER);

        page.addLine().addUnit("Bank Card", FONT_NORMAL);

        Long bankSaleNumberTotal = transTotal.getBankSaleNumberTotal() != null?transTotal.getBankSaleNumberTotal() :0L;
        Long bankSaleAmountTotal = transTotal.getBankSaleAmountTotal() != null?transTotal.getBankSaleAmountTotal() :0L;
        page.addLine().addUnit("SALE", FONT_NORMAL, (float) 1)
                .addUnit(String.valueOf(bankSaleNumberTotal), FONT_NORMAL, IImgProcessing.IPage.EAlign.CENTER, (float) 1)
                .addUnit(Utils.ftoYuan(bankSaleAmountTotal), FONT_NORMAL, IImgProcessing.IPage.EAlign.RIGHT, (float) 1);

        Long bankVoidNumberTotal = transTotal.getBankVoidNumberTotal() != null?transTotal.getBankVoidNumberTotal() :0L;
        Long bankVoidAmountTotal = transTotal.getBankVoidAmountTotal() != null?transTotal.getBankVoidAmountTotal() :0L;

        page.addLine().addUnit("VOID", FONT_NORMAL, (float) 1)
                .addUnit(String.valueOf(bankVoidNumberTotal), FONT_NORMAL, IImgProcessing.IPage.EAlign.CENTER, (float) 1)
                .addUnit(Utils.ftoYuan(bankVoidAmountTotal), FONT_NORMAL, IImgProcessing.IPage.EAlign.RIGHT, (float) 1);

        Long bankRefundNumberTotal = transTotal.getBankRefundNumberTotal() != null?transTotal.getBankRefundNumberTotal() :0L;
        Long bankRefundAmountTotal = transTotal.getBankRefundAmountTotal() != null?transTotal.getBankRefundAmountTotal() :0L;
        page.addLine().addUnit("REFUND", FONT_NORMAL, (float) 1)
                .addUnit(String.valueOf(bankRefundNumberTotal), FONT_NORMAL, IImgProcessing.IPage.EAlign.CENTER, (float) 1)
                .addUnit(Utils.ftoYuan(bankRefundAmountTotal), FONT_NORMAL, IImgProcessing.IPage.EAlign.RIGHT, (float) 1);

        page.addLine().addUnit("----------------------------------", FONT_SMALL,IImgProcessing.IPage.EAlign.CENTER);

        page.addLine().addUnit("Qr", FONT_NORMAL);
        Long qrSaleNumberTotal = transTotal.getQrSaleNumberTotal() != null?transTotal.getQrSaleNumberTotal() :0L;
        Long qrSaleAmountTotal = transTotal.getQrSaleAmountTotal() != null?transTotal.getQrSaleAmountTotal() :0L;
        page.addLine().addUnit("SALE", FONT_NORMAL, (float) 1)
                .addUnit(String.valueOf(qrSaleNumberTotal), FONT_NORMAL, IImgProcessing.IPage.EAlign.CENTER, (float) 1)
                .addUnit(Utils.ftoYuan(qrSaleAmountTotal), FONT_NORMAL, IImgProcessing.IPage.EAlign.RIGHT, (float) 1);

        Long qrVoidNumberTotal = transTotal.getQrVoidNumberTotal() != null?transTotal.getQrVoidNumberTotal() :0L;
        Long qrVoidAmountTotal = transTotal.getQrVoidAmountTotal() != null?transTotal.getQrVoidAmountTotal() :0L;

        page.addLine().addUnit("VOID", FONT_NORMAL, (float) 1)
                .addUnit(String.valueOf(qrVoidNumberTotal), FONT_NORMAL, IImgProcessing.IPage.EAlign.CENTER, (float) 1)
                .addUnit(Utils.ftoYuan(qrVoidAmountTotal), FONT_NORMAL, IImgProcessing.IPage.EAlign.RIGHT, (float) 1);

        Long qrRefundNumberTotal = transTotal.getQrRefundNumberTotal() != null?transTotal.getQrRefundNumberTotal() :0L;
        Long qrRefundAmountTotal = transTotal.getQrRefundAmountTotal() != null?transTotal.getQrRefundAmountTotal() :0L;

        page.addLine().addUnit("REFUND", FONT_NORMAL, (float) 1)
                .addUnit(String.valueOf(qrRefundNumberTotal), FONT_NORMAL, IImgProcessing.IPage.EAlign.CENTER, (float) 1)
                .addUnit(Utils.ftoYuan(qrRefundAmountTotal), FONT_NORMAL, IImgProcessing.IPage.EAlign.RIGHT, (float) 1);
        page.addLine().addUnit("----------------------------------", FONT_SMALL,IImgProcessing.IPage.EAlign.CENTER);
//        Summary
        Long summaryNumberTotal = bankSaleNumberTotal + bankVoidNumberTotal + bankRefundNumberTotal
                + qrSaleNumberTotal +qrVoidNumberTotal +qrRefundNumberTotal;
        Long summaryAmountTotal = (bankSaleAmountTotal - bankVoidAmountTotal - bankRefundNumberTotal)
                +(qrSaleAmountTotal - qrVoidNumberTotal - qrRefundAmountTotal);
        page.addLine().addUnit("SUMMARY", FONT_NORMAL, (float) 1)
                .addUnit(String.valueOf(summaryNumberTotal), FONT_NORMAL, IImgProcessing.IPage.EAlign.CENTER, (float) 1)
                .addUnit(Utils.ftoYuan(summaryAmountTotal), FONT_NORMAL, IImgProcessing.IPage.EAlign.RIGHT, (float) 1);
        page.addLine().addUnit("----------------------------------", FONT_SMALL,IImgProcessing.IPage.EAlign.CENTER);
        if("T6".equals(Build.MODEL)) {
            page.addLine().addUnit("\n\n\n\n\n\n\n", FONT_NORMAL);
        }else{
            page.addLine().addUnit("\n\n\n\n", FONT_NORMAL);
        }        return imgProcessing1.pageToBitmap(page, 384);
    }

    @Override
    public String generateStr() {
        return null;
    }
}
