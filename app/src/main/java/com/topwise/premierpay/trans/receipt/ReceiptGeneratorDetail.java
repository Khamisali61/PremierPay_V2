package com.topwise.premierpay.trans.receipt;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;

import com.topwise.iamge.api.IImgProcessing;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.utils.Utils;

import java.util.List;

/**
 * 创建日期：2021/5/28 on 17:56
 * 描述:
 * 作者:wangweicheng
 */
public class ReceiptGeneratorDetail implements IReceiptGenerator{
    private boolean isLast = false;
    private String title;
    public ReceiptGeneratorDetail(List<TransData> transDatasList,boolean isLast,String title) {
        this.transDatasList = transDatasList;
        this.isLast = isLast;
        this.title = title;
    }
    /**
     * 生成明细单主信息时用这个构造方法
     */
    public ReceiptGeneratorDetail() {

    }

    private List<TransData> transDatasList;
    @Override
    public Bitmap generateBinmap() {
        return null;
    }

    public Bitmap generateBitmapHead() {
        IImgProcessing imgProcessing1 = TopApplication.topImage.getImgProcessing();
        IImgProcessing.IPage page = imgProcessing1.createPage();
        Context context = TopApplication.mApp;
        page.setTypeFace(TYPE_FACE);
        SysParam sysParam = TopApplication.sysParam;
        String temp = "";
        // 凭单抬头
        page.addLine().addUnit(title, FONT_BIG, IImgProcessing.IPage.EAlign.CENTER);
        // 商户编号
        page.addLine().addUnit(context.getString(R.string.receipt_merchant_code), FONT_NORMAL)
                .addUnit(sysParam.get(SysParam.MERCH_ID), FONT_NORMAL, IImgProcessing.IPage.EAlign.RIGHT);
        // 终端编号/操作员号
        page.addLine().addUnit(context.getString(R.string.receipt_terminal_code_space), FONT_NORMAL)
                .addUnit(sysParam.get(SysParam.TERMINAL_ID), FONT_NORMAL, IImgProcessing.IPage.EAlign.RIGHT);
        page.addLine().addUnit(context.getString(R.string.receipt_oper_id_space), FONT_NORMAL)
                .addUnit("01", FONT_NORMAL, IImgProcessing.IPage.EAlign.RIGHT);
        // 批次号
        page.addLine()
                .addUnit(context.getString(R.string.receipt_batch_num_space)
                                + String.format("%06d", Long.parseLong(TopApplication.sysParam.get(SysParam.BATCH_NO))), FONT_NORMAL);
        // 日期时间
        String date = Device.getDateTime();
        page.addLine().addUnit(context.getString(R.string.receipt_date) + date, FONT_NORMAL);
        //
        // 交易信息
        page.addLine().addUnit("VOUCHER", FONT_NORMAL, (float) 2).addUnit("TYPE", FONT_NORMAL, IImgProcessing.IPage.EAlign.CENTER, (float) 1)
                .addUnit("AMOUNT", FONT_NORMAL, IImgProcessing.IPage.EAlign.RIGHT, (float) 3);
        page.addLine().addUnit("CARD NO", FONT_NORMAL, (float) 2).addUnit("AUTH NO", FONT_NORMAL, IImgProcessing.IPage.EAlign.RIGHT);
        page.addLine().addUnit("----------------------------------", FONT_SMALL);
        temp = "";
        String temp2 = "";
        for (TransData transData : transDatasList){
            String eTransType = transData.getTransType();
            // 交易类型对应的标志转换
            String type = "";
            if (eTransType.equals(ETransType.TRANS_SALE.toString())
                    || eTransType.equals(ETransType.TRANS_QR_SALE.toString())) {
                type = "S";
            }else if (eTransType.equals(ETransType.TRANS_REFUND.toString())
                    || eTransType.equals(ETransType.TRANS_QR_REFUND.toString())) {
                type = "R";
            }else if (eTransType.equals(ETransType.TRANS_VOID.toString())
                    || eTransType.equals(ETransType.TRANS_QR_VOID.toString())) {
                type = "V";
            }else {
                type = "N";
            }

            // 流水号/交易类型/金额
            temp = Utils.ftoYuan(transData.getAmount());
            temp2 = String.format("%06d", transData.getTransNo());
            page.addLine().addUnit(temp2, FONT_NORMAL, (float) 2).addUnit(type, FONT_NORMAL, IImgProcessing.IPage.EAlign.CENTER, (float) 1)
                    .addUnit(temp, FONT_NORMAL, IImgProcessing.IPage.EAlign.RIGHT, (float) 3);

            // 卡号/授权号

            // 卡号
          if (eTransType.equals(ETransType.TRANS_QR_SALE.toString())
                    || eTransType.equals(ETransType.TRANS_QR_VOID.toString())
                    || eTransType.equals(ETransType.TRANS_QR_REFUND.toString())) {
                temp = Utils.maskedCardNo(transData.getQrCode());
            } else {
                temp = Utils.maskedCardNo(transData.getPan());
                if (!transData.getIsOnlineTrans()) {
                    temp = transData.getPan();
                }
            }

            temp2 = transData.getAuthCode() == null ? "" : transData.getAuthCode();
            page.addLine().addUnit(temp, FONT_NORMAL, (float) 3).addUnit(temp2, FONT_NORMAL, IImgProcessing.IPage.EAlign.RIGHT);

        }
        if (isLast) {
            // 最后一次走纸
            page.addLine().addUnit("----------------------------------", FONT_SMALL);
            if("T6".equals(Build.MODEL)) {
                page.addLine().addUnit("\n\n\n\n\n\n\n", FONT_NORMAL);
            }else{
                page.addLine().addUnit("\n\n\n\n", FONT_NORMAL);
            }
        }
        return imgProcessing1.pageToBitmap(page, 384);
    }

    @Override
    public Bitmap generateBitmap() {
        IImgProcessing imgProcessing1 = TopApplication.topImage.getImgProcessing();
        IImgProcessing.IPage page = imgProcessing1.createPage();
        Context context = TopApplication.mApp;

        page.setTypeFace(TYPE_FACE);
        String temp = "";
        String temp2 = "";

        for (TransData transData : transDatasList) {
            String transType = transData.getTransType();
            // 交易类型对应的标志转换
            String type = "";
            // all_unionpay
            if (transType.equals(ETransType.TRANS_SALE.toString())
                    || transType.equals(ETransType.TRANS_QR_SALE.toString())
                ) {
                type = "S";
            } else if (transType.equals(ETransType.TRANS_PRE_AUTH_CMP.toString())) {
                type = "P";
            } else if (transType.equals(ETransType.TRANS_REFUND.toString())
                    || transType.equals(ETransType.TRANS_QR_REFUND.toString())
                    ) {
                // all_unionpay
                type = "R";
            }else if (transType.equals(ETransType.TRANS_VOID.toString())
                    || transType.equals(ETransType.TRANS_QR_VOID.toString())) {
                type = "V";
            }  else {
                type = "N";
            }

            // 流水号/交易类型/金额
            temp = Utils.ftoYuan(transData.getAmount());
            temp2 = String.format("%06d", transData.getTransNo());
            page.addLine().addUnit(temp2, FONT_NORMAL, (float) 2).addUnit(type, FONT_NORMAL, IImgProcessing.IPage.EAlign.CENTER, (float) 1)
                    .addUnit(temp, FONT_NORMAL, IImgProcessing.IPage.EAlign.RIGHT, (float) 3);

            // 卡号/授权号

            // 卡号
          if (transType.equals(ETransType.TRANS_QR_VOID.toString())
                    || transType.equals(ETransType.TRANS_QR_SALE.toString())
                    || transType.equals(ETransType.TRANS_QR_REFUND.toString())) {
                temp = Utils.maskedCardNo(transData.getQrCode());
            } else {
                temp = Utils.maskedCardNo(transData.getPan());
                if (!transData.getIsOnlineTrans()) {
                    temp = transData.getPan();
                }
            }

            temp2 = transData.getAuthCode() == null ? "" : transData.getAuthCode();
            page.addLine().addUnit(temp, FONT_NORMAL, (float) 3).addUnit(temp2, FONT_NORMAL, IImgProcessing.IPage.EAlign.RIGHT);
        }
        if (isLast) {
            // 最后一次走纸
            page.addLine().addUnit("----------------------------------", FONT_SMALL);
            if("T6".equals(Build.MODEL)) {
                page.addLine().addUnit("\n\n\n\n\n\n\n", FONT_NORMAL);
            }else{
                page.addLine().addUnit("\n\n\n\n", FONT_NORMAL);
            }

        }
        return imgProcessing1.pageToBitmap(page, 384);

    }

    @Override
    public String generateStr() {
        return null;
    }
}
