package com.topwise.premierpay.trans.receipt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.RemoteException;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.topwise.cloudpos.aidl.printer.AidlPrinter;
import com.topwise.cloudpos.aidl.printer.Align;
import com.topwise.cloudpos.aidl.printer.ImageUnit;
import com.topwise.cloudpos.aidl.printer.PrintItemObj;
import com.topwise.cloudpos.aidl.printer.PrintTemplate;
import com.topwise.cloudpos.aidl.printer.TextUnit;

import com.topwise.cloudpos.data.PrinterConstant;
import com.topwise.iamge.api.IImgProcessing;
import com.topwise.iamge.api.IImgProcessing.IPage.*;
import com.topwise.manager.utlis.DataUtils;

import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.utils.Utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：2021/4/2 on 14:09
 * 描述:
 * 作者:  wangweicheng
 */
public class ReceiptGeneratorTrans implements IReceiptGenerator {

    int receiptNo = 0;
    private TransData transData;
    private boolean isRePrint = false;
    private int receiptMax = 0;
    private String TAG = ReceiptGeneratorTrans.class.getSimpleName();

    /**
     *
     * @param transData
     * @param currentReceiptNo
     * @param receiptMax
     * @param isReprint
     */
    public ReceiptGeneratorTrans(TransData transData, int currentReceiptNo, int receiptMax, boolean isReprint) {
        this.transData = transData;
        this.receiptNo = currentReceiptNo;
        this.isRePrint = isReprint;
        this.receiptMax = receiptMax;
    }
    public void printRemote(){
        String temp = "",temp1 = "";
        Context context = TopApplication.mApp;

        ETransType eTransType = ETransType.valueOf(transData.getTransType());

        List<PrintItemObj> list = new ArrayList<>();
        PrintItemObj textUnit = new PrintItemObj(context.getString(R.string.pos_ticket), PrinterConstant.FontSize.LARGE,false, PrintItemObj.ALIGN.CENTER);
        list.add(textUnit);

        temp = context.getString(R.string.receipt_en_merchant_name)+TopApplication.sysParam.get(SysParam.MERCH_NAME);
        textUnit = new   PrintItemObj(temp);
        list.add(textUnit);


          //商户编号
        temp = context.getString(R.string.receipt_en_merchant_code)+"00000000000001";
        textUnit = new   PrintItemObj(temp);
        list.add(textUnit);
        // 终端编号  操作员

        temp = context.getString(R.string.receipt_en_terminal_code_space)+TopApplication.sysParam.get(SysParam.TERMINAL_ID);
        textUnit = new   PrintItemObj(temp);
        list.add(textUnit);



        temp = context.getString(R.string.receipt_en_oper_id_space)+"01";
        textUnit = new   PrintItemObj(temp);
        list.add(textUnit);


        //卡号
        int enterMode = transData.getEnterMode();
        temp = Utils.maskedCardNo(transData.getPan());
        if (enterMode == Component.EnterMode.MANAUL) {
            temp += " M";
        } else if (enterMode == Component.EnterMode.SWIPE) {
            temp += " S";
        } else if (enterMode == Component.EnterMode.INSERT) {
            temp += " I";
        } else if (enterMode == Component.EnterMode.CLSS_PBOC || enterMode == Component.EnterMode.QPBOC) {
            temp += " C";
        }
        textUnit = new PrintItemObj(temp, PrinterConstant.FontSize.LARGE,true, PrintItemObj.ALIGN.CENTER);
        list.add(textUnit);
        //交易类型
        textUnit = new PrintItemObj(eTransType.getTransName().toUpperCase(), PrinterConstant.FontSize.LARGE,false, PrintItemObj.ALIGN.CENTER);
        list.add(textUnit);

        //有效期

        temp = context.getString(R.string.receipt_en_card_date)+transData.getExpDate();
        textUnit = new   PrintItemObj(temp);
        list.add(textUnit);

        //流水
        temp =context.getString(R.string.receipt_en_trans_no)+ String.format("%06d",transData.getTransNo());
        textUnit = new   PrintItemObj(temp);
        list.add(textUnit);

        temp1 =context.getString(R.string.receipt_en_batch_num_colon)+ String.format("%06d",transData.getBatchNo());
        textUnit = new   PrintItemObj(temp1);
        list.add(textUnit);
        //授权码

        temp = context.getString(R.string.receipt_en_auth_code)+transData.getAuthCode();
        textUnit = new   PrintItemObj(temp);
        list.add(textUnit);

        temp = context.getString(R.string.receipt_en_ref_no)+transData.getRefNo();
        textUnit = new   PrintItemObj(temp);
        list.add(textUnit);
        String year = transData.getDatetime().substring(0,4);
        String date = transData.getDate();
        String time = transData.getTime();

        temp = year + "/" + date.substring(0, 2) + "/" + date.substring(2, 4) + " "
                + time.substring(0, 2) + ":" + time.substring(2, 4) + ":" + time.substring(4);
        //时间日期
        temp = context.getString(R.string.receipt_en_date)+temp;
        textUnit = new   PrintItemObj(temp);
        list.add(textUnit);
        String curr = TopApplication.sysParam.get(SysParam.APP_PARAM_TRANS_CURRENCY_SYMBOL);

        temp = curr+":"+Utils.ftoYuan(transData.getAmount());

        textUnit = new   PrintItemObj(temp,PrinterConstant.FontSize.LARGE,true, PrintItemObj.ALIGN.CENTER);
        list.add(textUnit);

        //AID	    M	Tag 84
        //App Name	M	Tag 9F12
        //TC	    M	Tag 9F26 (2GAC)
        //TVR	    M	Tag 95 (2GAC)
        //TSI	    M	Tag 9B (2GAC)
        //Card Holder Name	M	Tag 5F20
        //AID  Appname
        temp = transData.getAid();
        if (!TextUtils.isEmpty(temp)){
            temp = "AID:" + temp;
            textUnit = new   PrintItemObj(temp,PrinterConstant.FontSize.SMALL);
            list.add(textUnit);
        }
        temp = transData.getEmvAppName();
        if (!TextUtils.isEmpty(temp)){
            temp = "APPNAME:" + temp;
            textUnit = new   PrintItemObj(temp,PrinterConstant.FontSize.SMALL);
            list.add(textUnit);
        }
        temp = transData.getTc();
        if (!TextUtils.isEmpty(temp)){
            temp = "TC:" + temp;
            textUnit = new   PrintItemObj(temp,PrinterConstant.FontSize.SMALL);
            list.add(textUnit);
        }
        temp = transData.getTvr();
        temp1 = transData.getTsi();
        if (!TextUtils.isEmpty(temp) && !TextUtils.isEmpty(temp1)){
            temp ="TVR:" + temp + "  TSI:" + temp1;
            textUnit = new   PrintItemObj(temp,PrinterConstant.FontSize.SMALL);
            list.add(textUnit);
        }
        //TVR  TSI

        //MTI 0210 - DE52
        //0 - Signature
        //1 – PIN verified OK, Signature not required.
        //2 - PIN verified OK, Signature not required.
        //3 – (Blank)
        //4 – Signature
        temp = transData.getCardHolderName();
        if (!TextUtils.isEmpty(temp)) {
            textUnit = new   PrintItemObj(temp);
            list.add(textUnit);
        }


        if (isRePrint){
            temp = context.getString(R.string.receipt_print_agian);
            textUnit = new   PrintItemObj(temp,PrinterConstant.FontSize.SMALL);
            list.add(textUnit);
            if ("VOID".equals(transData.getTransState())){
                temp = context.getString(R.string.receipt_had_void);
                textUnit = new   PrintItemObj(temp,PrinterConstant.FontSize.SMALL);
                list.add(textUnit);
            }
        }
        /********Sign ******/
        textUnit = new   PrintItemObj("\n");
        list.add(textUnit);

        temp = context.getString(R.string.receipt_en_verify);
        textUnit = new   PrintItemObj(temp,PrinterConstant.FontSize.SMALL);
        list.add(textUnit);
        //
        if (receiptMax == 3){
            if (receiptNo == 0) {
                temp = context.getString(R.string.receipt_stub_acquire);
                textUnit = new   PrintItemObj(temp,PrinterConstant.FontSize.SMALL);
                list.add(textUnit);
            } else if (receiptNo == 1) {
                temp = context.getString(R.string.receipt_stub_merchant);
                textUnit = new   PrintItemObj(temp,PrinterConstant.FontSize.SMALL,false,PrintItemObj.ALIGN.CENTER);
                list.add(textUnit);
            } else {
                temp = context.getString(R.string.receipt_stub_user);
                textUnit = new   PrintItemObj(temp,PrinterConstant.FontSize.SMALL,false,PrintItemObj.ALIGN.CENTER);
                list.add(textUnit);
            }
        }else { //1 - 2
            if (receiptNo == 0){
                temp = context.getString(R.string.receipt_stub_merchant);
                textUnit = new   PrintItemObj(temp,PrinterConstant.FontSize.SMALL,false,PrintItemObj.ALIGN.CENTER);
                list.add(textUnit);

                textUnit = new   PrintItemObj("\n\n");
                list.add(textUnit);
            }else {
                temp = context.getString(R.string.receipt_stub_user);
                textUnit = new   PrintItemObj(temp,PrinterConstant.FontSize.SMALL,false,PrintItemObj.ALIGN.CENTER);
                list.add(textUnit);
            }
        }
        AidlPrinter printer = TopApplication.usdkManage.getPrinter();
        try {
            printer.printText(list,null);
            printer.start(null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Bitmap generateBinmap() {
        PrintTemplate template = PrintTemplate.getInstance();
        String temp = "",temp1 = "";
        Context context = TopApplication.mApp;
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),"Topwise65.ttf");
        template.init(context, typeface);
        template.clear();
        // 凭单抬头

        ETransType eTransType = ETransType.valueOf(transData.getTransType());

        List<TextUnit> list = new ArrayList<>();
        TextUnit textUnit = new TextUnit(context.getString(R.string.pos_ticket), LARGE, Align.CENTER);
        list.add(textUnit);
//        Bitmap imageLogoFile = Utils.getImageFromAssetsFile();
//        ImageUnit imageUnit = new ImageUnit(Align.RIGHT,imageLogoFile, imageLogoFile.getWidth(), imageLogoFile.getHeight());
//        template.add(imageUnit,list);
        // 商户名称
//        Bitmap imageLogoFile = Utils.getImageFromAssetsFile();
//        ImageUnit imageUnit = new ImageUnit(Align.RIGHT,imageLogoFile, imageLogoFile.getWidth(), imageLogoFile.getHeight());
//        template.add(imageUnit);
        // 商户名称
        temp = TopApplication.sysParam.get(SysParam.MERCH_NAME);
        template.add(1,new TextUnit(context.getString(R.string.receipt_en_merchant_name), NORMAL, Align.LEFT),1,new TextUnit(temp, NORMAL, Align.RIGHT));
        //商户编号
        template.add(1,new TextUnit(context.getString(R.string.receipt_en_merchant_code) , NORMAL, Align.LEFT),1,new TextUnit(TopApplication.sysParam.get(SysParam.MERCH_ID) , NORMAL, Align.RIGHT));

        // 终端编号  操作员
        template.add(1,new TextUnit(context.getString(R.string.receipt_en_terminal_code_space) , NORMAL, Align.LEFT),1,new TextUnit(TopApplication.sysParam.get(SysParam.TERMINAL_ID) , NORMAL, Align.RIGHT));

        template.add(1,new TextUnit(context.getString(R.string.receipt_en_oper_id_space) , NORMAL, Align.LEFT),1,new TextUnit("01" , NORMAL, Align.RIGHT));

        //发卡行 收单行
        temp = transData.getIsserCode();
        if (DataUtils.isNullString(temp)) temp = "";
        temp1 = transData.getAcqCode();
        if (DataUtils.isNullString(temp1)) temp1 = "";

        template.add(1,new TextUnit(context.getString(R.string.receipt_en_card_issue)  , NORMAL, Align.LEFT),1,new TextUnit(temp , NORMAL, Align.RIGHT));
        template.add(1,new TextUnit(context.getString(R.string.receipt_en_card_acquire)  , NORMAL, Align.LEFT),1,new TextUnit(temp1 , NORMAL, Align.RIGHT));


        //卡号
        int enterMode = transData.getEnterMode();
        temp = Utils.maskedCardNo(transData.getPan());
        if (enterMode == Component.EnterMode.MANAUL) {
            temp += " M";
        } else if (enterMode == Component.EnterMode.SWIPE) {
            temp += " S";
        } else if (enterMode == Component.EnterMode.INSERT) {
            temp += " I";
        } else if (enterMode == Component.EnterMode.CLSS_PBOC || enterMode == Component.EnterMode.QPBOC) {
            temp += " C";
        }
        template.add(new TextUnit(temp, LARGE, Align.CENTER).setBold(true));

        //交易类型
        template.add(new TextUnit(eTransType.getTransName().toUpperCase() , PrinterConstant.FontSize.LARGE, Align.CENTER));

        //有效期
        temp = transData.getExpDate();
        template.add(1,new TextUnit(context.getString(R.string.receipt_en_card_date), NORMAL, Align.LEFT),1,new TextUnit(temp, NORMAL, Align.RIGHT));


        //流水
        temp = String.format("%06d",transData.getTransNo());
        temp1 = String.format("%06d",transData.getBatchNo());
        template.add(1,new TextUnit(context.getString(R.string.receipt_en_trans_no), NORMAL, Align.LEFT),1,new TextUnit(temp, NORMAL, Align.RIGHT));
        // 批次
        template.add(1,new TextUnit(context.getString(R.string.receipt_en_batch_num_colon), NORMAL, Align.LEFT),1,new TextUnit(temp1, NORMAL, Align.RIGHT));
        //授权码
        temp = transData.getAuthCode();
        template.add(1,new TextUnit(context.getString(R.string.receipt_en_auth_code), NORMAL, Align.LEFT),1,new TextUnit(temp, NORMAL, Align.RIGHT));
        //系统参考号
        temp = transData.getRefNo();
        template.add(1,new TextUnit(context.getString(R.string.receipt_en_ref_no), NORMAL, Align.LEFT),1,new TextUnit(temp, NORMAL, Align.RIGHT));
        String year = transData.getDatetime().substring(0,4);
        String date = transData.getDate();
        String time = transData.getTime();

        temp = year + "/" + date.substring(0, 2) + "/" + date.substring(2, 4) + " "
                + time.substring(0, 2) + ":" + time.substring(2, 4) + ":" + time.substring(4);
        //时间日期
        template.add(1,new TextUnit(context.getString(R.string.receipt_en_date) , NORMAL, Align.LEFT),1,new TextUnit(temp, NORMAL, Align.RIGHT));


        temp = Utils.ftoYuan(transData.getAmount());
        //金额
        String curr = TopApplication.sysParam.get(SysParam.APP_PARAM_TRANS_CURRENCY_SYMBOL);

        template.add(new TextUnit(curr+temp, LARGE, Align.CENTER).setBold(true));


        //AID	    M	Tag 84
        //App Name	M	Tag 9F12
        //TC	    M	Tag 9F26 (2GAC)
        //TVR	    M	Tag 95 (2GAC)
        //TSI	    M	Tag 9B (2GAC)
        //Card Holder Name	M	Tag 5F20
        //AID  Appname
        temp = transData.getAid();
        if (!TextUtils.isEmpty(temp))
            template.add(new TextUnit("AID:" + temp, SMALL, Align.LEFT));
        temp = transData.getEmvAppName();
        if (!TextUtils.isEmpty(temp))
            template.add(new TextUnit("APPNAME:" + temp, SMALL, Align.LEFT));
        temp = transData.getTc();
        if (!TextUtils.isEmpty(temp))
            template.add(new TextUnit("TC:" + temp, SMALL, Align.LEFT));
        temp = transData.getTvr();
        temp1 = transData.getTsi();
        if (!TextUtils.isEmpty(temp) && !TextUtils.isEmpty(temp1))
            template.add(new TextUnit("TVR:" + temp + "  TSI:" + temp1, SMALL, Align.LEFT));
        //TVR  TSI

        //MTI 0210 - DE52
        //0 - Signature
        //1 – PIN verified OK, Signature not required.
        //2 - PIN verified OK, Signature not required.
        //3 – (Blank)
        //4 – Signature
        temp = transData.getCardHolderName();
        if (!TextUtils.isEmpty(temp)) {
            template.add(new TextUnit(temp, NORMAL, Align.LEFT));
        }
        temp = transData.getElecSignature();
        if ((!TextUtils.isEmpty(temp) ) && ("Y".equals(TopApplication.sysParam.get(SysParam.PARAM_ELEC_SIGN))) ){
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(Utils.getEsignPath(context)+temp);
                int length = fis.available();
                byte [] buffer = new byte[length];
                fis.read(buffer);
                Bitmap bitmap = TopApplication.topImage.getImgProcessing().jbigToBitmap(buffer);
                template.add(new ImageUnit(Align.CENTER,bitmap,bitmap.getWidth(),bitmap.getHeight()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(fis!= null){
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else{
            template.add(new TextUnit("\n"));
        }



        if (isRePrint){
            //template.add(new TextUnit("\n"));
            template.add(new TextUnit(context.getString(R.string.receipt_print_agian),NORMAL, Align.LEFT));
            if ("VOID".equals(transData.getTransState())){
                template.add(new TextUnit(context.getString(R.string.receipt_had_void),NORMAL, Align.LEFT));
            }
        }

        template.add(new TextUnit(context.getString(R.string.receipt_en_verify),SMALL, Align.CENTER));
        //
        if (receiptMax == 3){
            if (receiptNo == 0) {
                template.add(new TextUnit(context.getString(R.string.receipt_stub_acquire),SMALL, Align.CENTER));
            } else if (receiptNo == 1) {
                template.add(new TextUnit(context.getString(R.string.receipt_stub_merchant),SMALL, Align.CENTER));
            } else {
                template.add(new TextUnit(context.getString(R.string.receipt_stub_user),SMALL, Align.CENTER));
            }
        }else { //1 - 2
            if (receiptNo == 0){
                template.add(new TextUnit(context.getString(R.string.receipt_stub_merchant),SMALL, Align.CENTER));
            }else {
                template.add(new TextUnit(context.getString(R.string.receipt_stub_user),SMALL, Align.CENTER));
            }
        }

        template.add(new TextUnit(getTerminalandAppVersion(),SMALL, Align.CENTER));
        template.add(new TextUnit("\n\n\n"));
        return template.getPrintBitmap();
    }

    @Override
    public Bitmap generateBitmap() {
        IImgProcessing imgProcessing = TopApplication.topImage.getImgProcessing();
        IImgProcessing.IPage page = imgProcessing.createPage();
        String temp = "",temp1 = "";
        Context context = TopApplication.mApp;
        page.setTypeFace(TYPE_FACE);
        // 交易类型
        ETransType transType = ETransType.valueOf(transData.getTransType());
        SysParam sysParam = TopApplication.sysParam;

//        Bitmap imageLogoFile = Utils.getImageFromAssetsFile();
//        page.addLine().addUnit(imageLogoFile, EAlign.CENTER).addUnit(context.getString(R.string.pos_ticket),FONT_BIG, EAlign.CENTER);

        page.addLine().addUnit(context.getString(R.string.test_application), FONT_BIG);
        // 商户名称
        temp = TopApplication.sysParam.get(SysParam.MERCH_NAME);
        page.addLine().addUnit(context.getString(R.string.receipt_en_merchant_name),NORMAL, EAlign.LEFT);
        page.addLine().addUnit(temp,NORMAL, EAlign.LEFT);


        //商户编号
        page.addLine().addUnit(context.getString(R.string.receipt_en_merchant_code),NORMAL, EAlign.LEFT);
        page.addLine().addUnit(TopApplication.sysParam.get(SysParam.MERCH_ID),NORMAL, EAlign.LEFT);

        // 终端编号
        page.addLine().addUnit(context.getString(R.string.receipt_en_terminal_code_space),NORMAL, EAlign.LEFT);
        page.addLine().addUnit(TopApplication.sysParam.get(SysParam.TERMINAL_ID),NORMAL, EAlign.LEFT);
        //操作员
        page.addLine().addUnit(context.getString(R.string.receipt_en_oper_id_space) +"01",NORMAL, EAlign.LEFT);
        int enterMode = transData.getEnterMode();
        //发卡行 收单行
        temp = transData.getIsserCode();
        if (DataUtils.isNullString(temp)) temp = "";
        temp1 = transData.getAcqCode();
        if (DataUtils.isNullString(temp1)) temp1 = "";
        page.addLine().addUnit(context.getString(R.string.receipt_en_card_issue) + temp,NORMAL, EAlign.LEFT);
        page.addLine().addUnit(context.getString(R.string.receipt_en_card_acquire) + temp1,NORMAL, EAlign.LEFT);

        if (ETransType.TRANS_QR_SALE == transType || ETransType.TRANS_QR_VOID == transType ||
                ETransType.TRANS_QR_REFUND == transType ){

            temp = Utils.maskedCardNo(transData.getQrCode());
            page.addLine().addUnit(context.getString(R.string.scan_pay_code) ,NORMAL, EAlign.LEFT);
            page.addLine().addUnit(temp ,NORMAL, EAlign.LEFT);
        }else {
            //卡号
            temp = Utils.maskedCardNo(transData.getPan());
            if (enterMode == Component.EnterMode.MANAUL) {
                temp += " /M";
            } else if (enterMode == Component.EnterMode.SWIPE) {
                temp += " /S";
            } else if (enterMode == Component.EnterMode.INSERT) {
                temp += " /I";
            } else if (enterMode == Component.EnterMode.CLSS_PBOC || enterMode == Component.EnterMode.QPBOC) {
                temp += " /C";
            }
            page.addLine().addUnit(context.getString(R.string.receipt_en_card_no) ,NORMAL, EAlign.LEFT);
            page.addLine().addUnit(temp ,NORMAL, EAlign.LEFT);
        }



        //交易类型
        page.addLine().addUnit(context.getString(R.string.receipt_en_trans_type) ,NORMAL, EAlign.LEFT);
        page.addLine().addUnit(transType.getTransName().toUpperCase() ,LARGE, EAlign.CENTER);

        if (ETransType.TRANS_QR_SALE == transType || ETransType.TRANS_QR_VOID == transType ||
                ETransType.TRANS_QR_REFUND == transType ){
            if (transType == ETransType.TRANS_QR_VOID || transType == ETransType.TRANS_QR_REFUND){
                page.addLine().addUnit(context.getString(R.string.orig_pay_voucher_num), FONT_NORMAL);
                page.addLine().addUnit(imgProcessing.generateBarCode(
                        transData.getOrigQrVoucher(), 400, 100, BarcodeFormat.CODE_128), EAlign.CENTER);
                page.addLine().addUnit(transData.getOrigQrVoucher(), FONT_NORMAL, EAlign.CENTER);
            }else {
                page.addLine().addUnit(context.getString(R.string.pay_voucher_num), FONT_NORMAL);
                page.addLine().addUnit(imgProcessing
                        .generateBarCode(transData.getQrVoucher(), 400, 100, BarcodeFormat.CODE_128), EAlign.CENTER);
                page.addLine().addUnit(transData.getQrVoucher(), FONT_NORMAL, EAlign.CENTER);
            }
        }else {

            //有效期
            temp = transData.getExpDate();
            if (!DataUtils.isNullString(temp)){
                temp = temp.substring(0,2) + "/" + temp.substring(2);
            }else {
                temp = "";
            }
            page.addLine().addUnit(context.getString(R.string.receipt_en_card_date) +" " +temp,NORMAL, EAlign.LEFT);

            //流水
            temp = String.format("%06d",transData.getTransNo());
            temp1 = String.format("%06d",transData.getBatchNo());
            page.addLine().addUnit(context.getString(R.string.receipt_en_trans_no) +temp,NORMAL, EAlign.LEFT);
            // 批次
            page.addLine().addUnit(context.getString(R.string.receipt_en_batch_num_colon)  +temp1,NORMAL, EAlign.LEFT);

            //授权码
            temp = transData.getAuthCode();
            if (DataUtils.isNullString(temp)) temp= "";
            page.addLine().addUnit(context.getString(R.string.receipt_en_auth_code) +temp,NORMAL, EAlign.LEFT);

            //系统参考号
            temp = transData.getRefNo();
            if (DataUtils.isNullString(temp)) temp= "";
            page.addLine().addUnit(context.getString(R.string.receipt_en_ref_no) +temp,NORMAL, EAlign.LEFT);
        }

        temp = Utils.getTransDataTime(transData);
        //时间日期
        page.addLine().addUnit(context.getString(R.string.receipt_en_date) ,NORMAL, EAlign.LEFT);
        page.addLine().addUnit(temp,NORMAL, EAlign.LEFT);



        temp = Utils.ftoYuan(transData.getAmount());
        //金额
        page.addLine().addUnit(context.getString(R.string.receipt_en_amount_sale) ,NORMAL, EAlign.LEFT);
        if (transType == ETransType.TRANS_QR_VOID ||transType == ETransType.TRANS_QR_REFUND
                ||transType == ETransType.TRANS_VOID  ||transType == ETransType.TRANS_REFUND ){
            page.addLine().addUnit("RMB: -"+temp ,NORMAL, EAlign.CENTER);
        }else {
            page.addLine().addUnit("RMB: "+temp ,NORMAL, EAlign.CENTER);
        }


        //AID	    M	Tag 84
        //App Name	M	Tag 9F12
        //TC	    M	Tag 9F26 (2GAC)
        //TVR	    M	Tag 95 (2GAC)
        //TSI	    M	Tag 9B (2GAC)
        //Card Holder Name	M	Tag 5F20
        //AID  Appname
        temp = transData.getAid();
        if (!TextUtils.isEmpty(temp))
            page.addLine().addUnit("AID:" + temp,SMALL, EAlign.LEFT);

        temp = transData.getEmvAppName();
        if (!TextUtils.isEmpty(temp))
            page.addLine().addUnit("APPNAME:" + temp,SMALL, EAlign.LEFT);

        temp = transData.getTc();
        if (!TextUtils.isEmpty(temp))
            page.addLine().addUnit("TC:" + temp,SMALL, EAlign.LEFT);

        temp = transData.getTvr();
        temp1 = transData.getTsi();
        if (!TextUtils.isEmpty(temp) && !TextUtils.isEmpty(temp1))
            page.addLine().addUnit("TVR:" + temp + "  TSI:" + temp1,SMALL, EAlign.LEFT);

        //TVR  TSI

        temp = transData.getCardHolderName();
        if (!TextUtils.isEmpty(temp))
            page.addLine().addUnit(temp,SMALL, EAlign.LEFT);

        //
        temp = transData.getElecSignature();
        if ((!TextUtils.isEmpty(temp) ) && ("Y".equals(TopApplication.sysParam.get(SysParam.PARAM_ELEC_SIGN))) ){
            FileInputStream fis = null;
            try {
                 fis = new FileInputStream(Utils.getEsignPath(context)+temp);
                int length = fis.available();
                byte [] buffer = new byte[length];
                fis.read(buffer);
                Bitmap bitmap = TopApplication.topImage.getImgProcessing().jbigToBitmap(buffer);
                page.addLine().addUnit(bitmap,EAlign.CENTER);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(fis!= null){
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }



        if (isRePrint){
            //template.add(new TextUnit("\n"));
            page.addLine().addUnit(context.getString(R.string.receipt_print_agian),NORMAL, EAlign.LEFT);
            if ("VOID".equals(transData.getTransState())){
                page.addLine().addUnit(context.getString(R.string.receipt_had_void),NORMAL, EAlign.LEFT);
            }
        }
        page.addLine().addUnit(context.getString(R.string.receipt_en_verify),SMALL, EAlign.CENTER);

        //
        if (receiptMax == 3){
            if (receiptNo == 0) {
                page.addLine().addUnit(context.getString(R.string.receipt_stub_acquire),SMALL, EAlign.CENTER);
            } else if (receiptNo == 1) {
                page.addLine().addUnit(context.getString(R.string.receipt_stub_merchant),SMALL, EAlign.CENTER);
            } else {
                page.addLine().addUnit(context.getString(R.string.receipt_stub_user),SMALL, EAlign.CENTER);
            }
        }else { //1 - 2
            if (receiptNo == 0){
                page.addLine().addUnit(context.getString(R.string.receipt_stub_merchant),SMALL, EAlign.CENTER);
            }else {
                page.addLine().addUnit(context.getString(R.string.receipt_stub_user),SMALL, EAlign.CENTER);
            }
        }
        page.addLine().addUnit(getTerminalandAppVersion(),SMALL, EAlign.CENTER);

        page.addLine().addUnit("\n\n\n\n", FONT_NORMAL);
        return imgProcessing.pageToBitmap(page, 384);
    }

    @Override
    public String generateStr() {
        return null;
    }

    private String getTerminalandAppVersion(){
        return Device.gerModel() +" " + TopApplication.version;
    }
}
