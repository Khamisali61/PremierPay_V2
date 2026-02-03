package com.topwise.premierpay.trans.action.activity;

import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.topwise.manager.AppLog;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;

import com.topwise.premierpay.daoutils.DaoUtilsStore;

import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.EUIParamKeys;
import com.topwise.premierpay.trans.model.ResponseCode;
import com.topwise.premierpay.trans.model.TestParam;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.trans.model.TransStatusSum;
import com.topwise.premierpay.trans.receipt.PrintListenerImpl;
import com.topwise.premierpay.trans.receipt.ReceiptPrintTrans;
import com.topwise.premierpay.transmit.TransProcessListenerImpl;
import com.topwise.premierpay.transmit.iso8583.Transmit;
import com.topwise.premierpay.utils.SmallScreenUtil;
import com.topwise.premierpay.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConsumeSuccessActivity extends BaseActivityWithTickForAction implements View.OnClickListener {
    private static final String TAG = TopApplication.APPNANE + ConsumeSuccessActivity.class.getSimpleName();

    protected static final int TIP_PRINT = 0x01;
    protected static final int TIP_PRINT_END = 0x02;

    private TextView tVtitle;

    // New UI Components
    private TextView tvStatusText;
    private TextView tvFailureReason;
    private TextView tvTotalAmount;
    private TextView tvAuthCode;
    private TextView tvCardNo;
    private TextView tvMerchantId;
    private ImageView ivTransStatus;
    private View btnClose;
    private View btnPrintReceipt;
    private View btnEmailReceipt;
    private Button btnDone;

    private String navTitle;
    private TransData transData;
    private String resCode;
    List<String> result;

    private void closePrint(){
        if (receiptPrintTrans != null)
            receiptPrintTrans.close();

        if (listener != null) {
            listener.setRun(false);
            listener = null;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_done || id == R.id.btn_close) {
            closePrint();
            ActionResult result = new ActionResult(transData.isStressTest() ? TransResult.CONTINUE : TransResult.ERR_ABORTED, null);
            finish(result);
        } else if (id == R.id.btn_print_receipt) {
            if (transData.isNeedPrint()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        receiptPrintTrans = ReceiptPrintTrans.getInstance();
                        listener = new PrintListenerImpl(handler, transData.isStressTest());
                        receiptPrintTrans.print(transData, false, listener);
                    }
                }).start();
            }
        } else if (id == R.id.btn_email_receipt) {
            // Email functionality stub
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_consume_result;
    }

    @Override
    protected void initViews() {
        // Initialize New UI Components
        tVtitle = (TextView)findViewById(R.id.header_title);
        tvStatusText = (TextView) findViewById(R.id.tv_status_text);
        tvFailureReason = (TextView) findViewById(R.id.tv_failure_reason);
        tvTotalAmount = (TextView) findViewById(R.id.tv_total_amount);
        tvAuthCode = (TextView) findViewById(R.id.tv_auth_code);
        tvCardNo = (TextView) findViewById(R.id.tv_card_no);
        tvMerchantId = (TextView) findViewById(R.id.tv_merchant_id);
        ivTransStatus = (ImageView) findViewById(R.id.iv_trans_status);

        btnClose = findViewById(R.id.btn_close);
        btnPrintReceipt = findViewById(R.id.btn_print_receipt);
        btnEmailReceipt = findViewById(R.id.btn_email_receipt);
        btnDone = (Button) findViewById(R.id.btn_done);

        // Set Listeners
        btnClose.setOnClickListener(this);
        btnPrintReceipt.setOnClickListener(this);
        btnEmailReceipt.setOnClickListener(this);
        btnDone.setOnClickListener(this);

        // Logic
        tVtitle.setText(navTitle);
        result = new ArrayList();
        boolean isSuccess = false;

        if (transData.isStressTest()) {
            btnDone.setEnabled(false);
            tickTimerStop();
        }

        AppLog.e(TAG,"Status ResponseCode " + resCode + " Transresult" + transData.getTransresult());

        // Determine Success/Fail
        if ("00".equals(resCode) && TransResult.SUCC == transData.getTransresult()) {
            isSuccess = true;
            // Green Theme
            ivTransStatus.setImageResource(R.drawable.ic_check_circle_large);
            tvStatusText.setText("Approved");
            tvStatusText.setTextColor(getResources().getColor(R.color.brand_navy)); // or success green if preferred
            tvFailureReason.setVisibility(View.GONE);

            boolean delete =  DaoUtilsStore.getInstance().getmDupTransDaoUtils().deleteAll();
            AppLog.e(TAG,"Delete reversal file before print: " + delete);

            if (Component.checkSave(transData) && delete) {
                boolean save = DaoUtilsStore.getInstance().getmTransDaoUtils().save(transData);
                AppLog.e(TAG, "transData saved: " + save);
                handler.sendEmptyMessageDelayed(TIP_PRINT, 1000);
            }
            Device.openGreenLed();
            Device.beepSucc();
        } else {
            isSuccess = false;
            // Red Theme
            ivTransStatus.setImageResource(R.drawable.ic_cancel_circle_large);
            tvStatusText.setText("Transaction Declined");
            tvStatusText.setTextColor(getResources().getColor(R.color.brand_navy));

            // Failure Reason
            tvFailureReason.setVisibility(View.VISIBLE);
            String responseCode = transData.getResponseCode();
            if (!TextUtils.isEmpty(responseCode)) {
                ResponseCode rc = TopApplication.rspCode.parse(responseCode);
                tvFailureReason.setText(rc.getMessage()); // e.g. "Insufficient Funds"
            } else {
                tvFailureReason.setText(getString(R.string.result_sucess_failure));
            }

            AppLog.e(TAG, "transData.getTransresult() " + transData.getTransresult());
            toReversal();
            testExit();
            Device.openRedLed();
            Device.beepFail();
        }

        // Map Data Fields
        String currency = TopApplication.sysParam.get(SysParam.APP_PARAM_TRANS_CURRENCY_SYMBOL);
        String amountStr = Utils.ftoYuan(transData.getAmount());
        tvTotalAmount.setText(currency + amountStr);

        tvAuthCode.setText(TextUtils.isEmpty(transData.getAuthCode()) ? "" : transData.getAuthCode());

        String pan = transData.getPan();
        tvCardNo.setText(TextUtils.isEmpty(pan) ? "" : Utils.maskedCardNo(pan));

        // Assuming Merchant ID is available via SysParam as it's often global,
        // or check if TransData has it. If not, fallback to SysParam.
        String mid = TopApplication.sysParam.get(SysParam.MERCH_ID);
        tvMerchantId.setText(mid);

        // Update Small Screen (Legacy support if needed)
        result.add(0, isSuccess ? "Approved" : "Failure");
        result.add(2, currency + amountStr);
        SmallScreenUtil.getInstance().showResult(isSuccess, result);
    }

    private void testExit() {
        if (transData.isStressTest()) {
            btnDone.setEnabled(true);
            TestParam testParam = DaoUtilsStore.getInstance().getTestParam();
            final int intervalTime = testParam.getIntervalMode()==0?testParam.getIntervalTime(): getRandom();
            tickTimerStart(intervalTime);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(intervalTime*1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnDone.performClick();
                        }
                    });
                }
            }).start();

        }
    }
    private void toReversal() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppLog.d(TAG, "ConsumeSuccessActivity toReversal");
                // 立即冲正，在这处理
                TransProcessListenerImpl listenerImpl = new TransProcessListenerImpl();
                int ret = Transmit.getInstance().sendReversal(listenerImpl);
                AppLog.d(TAG, "ConsumeSuccessActivity toReversal ret" + ret);
                listenerImpl.onHideProgress();
                Device.closeAllLed();
            }
        }).start();
    }

    private int getRandom() {
        Random random =  new Random(60);
        return random.nextInt();
    }

    @Override
    protected void setListeners() {
        // Listeners set in initViews
    }

    @Override
    protected void loadParam() {
        Bundle bundle = getIntent().getExtras();
        navTitle = getIntent().getStringExtra(EUIParamKeys.NAV_TITLE.toString());
        transData = (TransData) bundle.getSerializable(EUIParamKeys.CONTENT.toString());
        resCode = transData.getResponseCode();
    }

    private PrintListenerImpl listener;
    private ReceiptPrintTrans  receiptPrintTrans;

    @Override
    protected void handleMsg(Message msg) {
        switch (msg.what){
            case TIP_TIME:
                String time = (String)msg.obj;
                if (!TextUtils.isEmpty(time))
                    tVtime.setText(time);
                break;
            case TIP_PRINT:
                if(!transData.isNeedPrint()) {
                    break;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        receiptPrintTrans = ReceiptPrintTrans.getInstance();
                        listener = new PrintListenerImpl( handler,transData.isStressTest());
                        receiptPrintTrans.print(transData, false, listener);
                    }
                }).start();
                break;
            case TIP_PRINT_END:
                testExit();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            closePrint();
            ActionResult result = new ActionResult(TransResult.ERR_ABORTED, null);
            finish(result);
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            closePrint();
            ActionResult result = new ActionResult(TransResult.ERR_ABORTED, null);
            finish(result);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // Legacy helper for initializing details list - kept if needed for other logic or SmallScreenUtil
    private List<String []> init(TransData transData) {
        String temp = "";
        ETransType transType = ETransType.valueOf(transData.getTransType());
        List<String []> l = new ArrayList<String []>();

        //==type
        String [] t = new String[2];
        t[0] = getString(R.string.result_sucess_consume_type);
        t[1] = transType.getTransName().toUpperCase();
        result.add(t[1]);
        l.add(t);

        // ... (rest of legacy logic remains if truly needed, otherwise can be simplified)

        return  l;
    }
}

