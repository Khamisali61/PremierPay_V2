package com.topwise.premierpay.trans.record;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.topwise.iamge.api.IImgProcessing;
import com.topwise.premierpay.BuildConfig;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.app.BaseActivity;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.EUIParamKeys;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.receipt.IReceiptGenerator;
import com.topwise.premierpay.trans.receipt.PrintListenerImpl;
import com.topwise.premierpay.trans.receipt.ReceiptPrintTrans;
import com.topwise.premierpay.utils.Utils;

/**
 * 创建日期：2021/4/8 on 15:49
 * 描述:
 * 作者:  wangweicheng
 */
public class DetailTransActivity extends BaseActivity implements View.OnClickListener {
    private TextView tVtitle;
    private ImageView iVview;
    private TransData transData;
    @Override
    protected int getLayoutId() {
        return R.layout.detail_trans_layout;
    }

    @Override
    protected void initViews() {
        int iReceiptGeneratorSize = 0;
        if (Utils.isLowPix(this)) {
            iReceiptGeneratorSize = IReceiptGenerator.SMALL;
        } else {
            iReceiptGeneratorSize = IReceiptGenerator.LARGE;
        }
        tVtitle = (TextView)findViewById(R.id.header_title);
        tVtitle.setText("Detail reports");
        //=================================set 
        IImgProcessing imgProcessing = TopApplication.topImage.getImgProcessing();
        IImgProcessing.IPage page = imgProcessing.createPage();
        // 交易类型
        ETransType transType = ETransType.valueOf(transData.getTransType());
        SysParam sysParam = TopApplication.sysParam;
        // 商户名称
        page.addLine().addUnit(getString(R.string.receipt_en_merchant_name) , iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.LEFT);
        page.addLine().addUnit(sysParam.get(SysParam.MERCH_NAME),iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.RIGHT);
        //=================================
        String temp = "";
        //商户号
        temp = transData.getMerchID();
        if (TextUtils.isEmpty(temp)) temp = TopApplication.sysParam.get(SysParam.MERCH_ID);
        page.addLine().addUnit(getString(R.string.receipt_en_merchant_code) , iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.LEFT);
        page.addLine().addUnit(temp,iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.RIGHT);

        //终端号
        temp = transData.getTermID();
        if (TextUtils.isEmpty(temp)) temp = TopApplication.sysParam.get(SysParam.TERMINAL_ID);
        page.addLine().addUnit(getString(R.string.receipt_en_terminal_code_space) , iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.LEFT);
        page.addLine().addUnit(temp,iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.RIGHT);
        //交易类型
        temp =ETransType.valueOf( transData.getTransType()).getTransName().toUpperCase();
        page.addLine().addUnit(getString(R.string.receipt_en_trans_type) , iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.LEFT,(float) 1)
                .addUnit(temp,iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.RIGHT,(float) 1);
        //金额
        temp = Utils.ftoYuan(transData.getAmount());
        page.addLine().addUnit(getString(R.string.receipt_en_amount) , iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.LEFT,(float) 1)
                .addUnit(temp,iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.RIGHT,(float) 1);
        //状态
        temp = transData.getTransState();
        page.addLine().addUnit("STATE" , iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.LEFT,(float) 1)
                .addUnit(temp,iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.RIGHT,(float) 1);


        //卡号类型
        if (transType == ETransType.TRANS_QR_VOID || transType == ETransType.TRANS_QR_SALE ||
                transType == ETransType.TRANS_QR_REFUND) {
            temp = transData.getOrigQrVoucher();
            if (transType == ETransType.TRANS_QR_VOID || transType == ETransType.TRANS_QR_REFUND){
                page.addLine().addUnit(getString(R.string.orig_pay_voucher_num) , iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.LEFT);
                page.addLine().addUnit(temp,iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.RIGHT);
            }else {
                temp = transData.getQrVoucher();
                page.addLine().addUnit(getString(R.string.pay_voucher_num), iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.LEFT);
                page.addLine().addUnit(temp, iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.RIGHT);

            }
        } else {
            //卡号
            int enterMode = transData.getEnterMode();
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
            page.addLine().addUnit(getString(R.string.receipt_en_card_no)  , iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.LEFT,(float) 1)
                    .addUnit(temp,iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.RIGHT,(float) 1);

            //授权码
            temp = transData.getAuthCode();
            page.addLine().addUnit(getString(R.string.receipt_en_auth_code)  , iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.LEFT,(float) 1)
                    .addUnit(temp,iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.RIGHT,(float) 1);

            temp = transData.getExpDate();
            page.addLine().addUnit(getString(R.string.receipt_en_card_date)  , iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.LEFT,(float) 1)
                    .addUnit(temp,iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.RIGHT,(float) 1);
        }

        //参考号
        temp = transData.getRefNo();
        page.addLine().addUnit(getString(R.string.receipt_en_ref_no)  , iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.LEFT,(float) 1)
                .addUnit(temp,iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.RIGHT,(float) 1);
        //流水号
        temp = String.format("%06d",transData.getTransNo());
        page.addLine().addUnit(getString(R.string.receipt_en_trans_no)  , iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.LEFT,(float) 1)
                .addUnit(temp,iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.RIGHT,(float) 1);

        //批次号
        temp = String.format("%06d",transData.getBatchNo());
        page.addLine().addUnit(getString(R.string.receipt_en_batch_num_colon)  , iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.LEFT,(float) 1)
                .addUnit(temp,iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.RIGHT,(float) 1);
        //时间

        //yyyyMMdd
        temp = Utils.getTransDataTime(transData);
        page.addLine().addUnit(getString(R.string.receipt_en_date)  , iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.LEFT,(float) 1)
                .addUnit(temp,iReceiptGeneratorSize, IImgProcessing.IPage.EAlign.RIGHT,(float) 1);

        Bitmap bitmap = imgProcessing.pageToBitmap(page, Utils.getScreenWidth(this) -30);
        ((ImageView)findViewById(R.id.iv_detail)).setImageBitmap(bitmap);
    }


    @Override
    protected void setListeners() {

    }

    @Override
    protected void loadParam() {
        Bundle bundle = getIntent().getExtras();
        transData = (TransData) bundle.getSerializable(EUIParamKeys.CONTENT.toString());
    }

    @Override
    protected void handleMsg(Message msg) {

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ActivityStack.getInstance().pop();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_confirm:
                ActivityStack.getInstance().pop();
                break;
            case R.id.bt_reprint:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ReceiptPrintTrans receiptPrintTrans = ReceiptPrintTrans.getInstance();
                        PrintListenerImpl listener = new PrintListenerImpl( handler);
                        receiptPrintTrans.print(transData, true, listener);
                    }
                }).start();
                break;
        }
    }
}
