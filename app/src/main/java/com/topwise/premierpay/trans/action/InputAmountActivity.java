package com.topwise.premierpay.trans.action;

import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.topwise.toptool.api.utils.AppLog;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.daoutils.DaoUtilsStore;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.action.activity.BaseActivityWithTickForAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.EUIParamKeys;
import com.topwise.premierpay.trans.model.TestParam;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.utils.ScreenUtils;
import com.topwise.premierpay.utils.SmallScreenUtil;
import com.topwise.premierpay.utils.ThreadPoolUtils;
import com.topwise.premierpay.utils.Utils;
import com.topwise.premierpay.view.TopToast;

import java.util.Random;

public class InputAmountActivity extends BaseActivityWithTickForAction implements View.OnClickListener {
    private static final String TAG = TopApplication.APPNANE + InputAmountActivity.class.getSimpleName();
    private TextView tVtitle;
    private TextView tVtime;
    private Button btnConfirm;
    private String navTitle;
    private String tip;

    private StringBuilder mAmountBuilder;
    private MaterialTextView  mTextAmount;
    Long maxValue = 1000000000L; // 10 million (10,000,000.00)

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        int len = mAmountBuilder.length();
        ActionResult result;
        switch (viewId) {
            case  R.id.btn0:
                if (len<12 && len>0) {
                    mAmountBuilder.append("0");
                    setTextA();
                }
                break;
            case R.id.btn00:
                if (len < 11 && len > 0) {
                     mAmountBuilder.append("00");
                     setTextA();
                } else if (len < 12 && len > 0) {
                     mAmountBuilder.append("0");
                     setTextA();
                }
                break;
            case  R.id.btn1:
                if (len < 12) {
                    mAmountBuilder.append("1");
                    setTextA();
                }
                break;
            case  R.id.btn2:
                if (len < 12) {
                    mAmountBuilder.append("2");
                    setTextA();
                }
                break;
            case  R.id.btn3:
                if (len < 12) {
                    mAmountBuilder.append("3");
                    setTextA();
                }
                break;
            case  R.id.btn4:
                if (len < 12) {
                    mAmountBuilder.append("4");
                    setTextA();
                }
                break;
            case  R.id.btn5:
                if (len < 12) {
                    mAmountBuilder.append("5");
                    setTextA();
                }
                break;
            case  R.id.btn6:
                if (len < 12) {
                    mAmountBuilder.append("6");
                    setTextA();
                }
                break;
            case  R.id.btn7:
                if (len < 12) {
                    mAmountBuilder.append("7");
                    setTextA();
                }
                break;
            case  R.id.btn8:
                if (len < 12) {
                    mAmountBuilder.append("8");
                    setTextA();
                }
                break;
            case  R.id.btn9:
                if (len < 12) {
                    mAmountBuilder.append("9");
                    setTextA();
                }
                break;

            case R.id.btnConfirm:
                setTextA();
                String amountStr = mAmountBuilder.toString();
                if (TextUtils.isEmpty(amountStr)){
                    Snackbar.make(v, "kindly input amount", Snackbar.LENGTH_LONG).show();
                    break;
                }
                long amount = Long.valueOf(amountStr);
                AppLog.i(TAG ,"：" + amount);
                if (TextUtils.isEmpty(amountStr) || amount == 0) {
                    break;
                }
                result = new ActionResult(TransResult.SUCC, amountStr);
                finish(result);
                break;
            case R.id.btnClear:
                if (len > 0) {
                    mAmountBuilder.delete(len-1, len);
                    setTextA();
                }
                break;
            case R.id.btn_clear:
                mAmountBuilder.delete(0, mTextAmount.length());
                setTextA();
                break;
            case R.id.back_btn:
                result = new ActionResult(TransResult.ERR_ABORTED, null);
                finish(result);
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_amount;
    }

    @Override
    protected void initViews() {
        TextView currSymbol = (TextView)findViewById(R.id.currency_icon);
        String curr = TopApplication.sysParam.get(SysParam.APP_PARAM_TRANS_CURRENCY_SYMBOL);
        if (currSymbol != null) currSymbol.setText(curr);

        mTextAmount = (MaterialTextView) findViewById(R.id.output);

        btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(this);

        findViewById(R.id.btn0).setOnClickListener(this);
        View btn00 = findViewById(R.id.btn00);
        if (btn00 != null) btn00.setOnClickListener(this);

        findViewById(R.id.back_btn).setOnClickListener(this);
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
        findViewById(R.id.btn4).setOnClickListener(this);
        findViewById(R.id.btn5).setOnClickListener(this);
        findViewById(R.id.btn6).setOnClickListener(this);
        findViewById(R.id.btn7).setOnClickListener(this);
        findViewById(R.id.btn8).setOnClickListener(this);
        findViewById(R.id.btn9).setOnClickListener(this);

        View btnClear = findViewById(R.id.btnClear);
        if (btnClear != null) btnClear.setOnClickListener(this);

        // btn_clear might not exist in new layout
        View btnClearAll = findViewById(R.id.btn_clear);
        if (btnClearAll != null) btnClearAll.setOnClickListener(this);

        mAmountBuilder = new StringBuilder();
        setTextA();
        if (ScreenUtils.getScreenWidth() == 320 && ScreenUtils.getScreenHeight() == 240) {
            findViewById(R.id.keypad_layout).setVisibility(View.GONE);
        }
    }

    private void setTextA() {
        String curr = TopApplication.sysParam.get(SysParam.APP_PARAM_TRANS_CURRENCY_SYMBOL);
        if (curr == null) curr = "Ksh"; // Fallback

        String amountStr = mAmountBuilder.toString();
        AppLog.v(TAG, "amountStr = "+amountStr);
        if (TextUtils.isEmpty(amountStr)) {
            mTextAmount.setText("0.00");
            if (btnConfirm != null) btnConfirm.setText("CHARGE " + curr + " 0.00");
            SmallScreenUtil.getInstance().showAmount(navTitle, curr+"0.00");
            return ;
        }
        long amount = Long.valueOf(amountStr);
        AppLog.v(TAG, "amount long  = "+amount);
        if (!amountStr.isEmpty() && java.lang.Long.valueOf(amountStr) > maxValue) {
            int len = amountStr.length() -9;
            mAmountBuilder.delete(0, mTextAmount.length());
            mAmountBuilder.append(amountStr.substring(0,amountStr.length() -len));
            Snackbar.make(findViewById(R.id.btnConfirm), "Max value is 10 Million", Snackbar.LENGTH_LONG).show();
            return;
        } else {
            String formattedAmount = String.format("%d.%02d", amount/100, amount%100);
            String displayAmount = Utils.amountAddComma(formattedAmount);
            mTextAmount.setText(displayAmount);
            if (btnConfirm != null) btnConfirm.setText("CHARGE " + curr + " " + displayAmount);
            SmallScreenUtil.getInstance().showAmount(navTitle, curr + displayAmount);
        }
    }

    @Override
    protected void setListeners() {
    }

    @Override
    protected void loadParam() {
        navTitle = getIntent().getStringExtra(EUIParamKeys.NAV_TITLE.toString());
        tip = getIntent().getStringExtra(EUIParamKeys.PROMPT_1.toString());
        boolean stressTest = getIntent().getBooleanExtra(EUIParamKeys.STRESS_TEST.toString(),false);
        if (stressTest) {
            Random random = new Random();
            final String amout  = Integer.toString(Math.abs(random.nextInt())%1000000+1);
            TestParam testParam = DaoUtilsStore.getInstance().getTestParam();
            final int delayTime = testParam.getDelayTime();
            ThreadPoolUtils.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(delayTime * 1000L);
                    finish(new ActionResult(TransResult.SUCC, amout));
                }
            });
        }
    }

    @Override
    protected void handleMsg(Message msg) {
        switch (msg.what) {
            case TIP_TIME:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.v(TAG,"keycode "+keyCode);
        int len = mAmountBuilder.length();
        Log.v(TAG,"len "+len);

        ActionResult result;
        switch (keyCode) {
            case   KeyEvent.KEYCODE_0:
                  if (len== 0)
                      break;
            case   KeyEvent.KEYCODE_1:
            case   KeyEvent.KEYCODE_2:
            case   KeyEvent.KEYCODE_3:
            case   KeyEvent.KEYCODE_4:
            case   KeyEvent.KEYCODE_5:
            case   KeyEvent.KEYCODE_6:
            case   KeyEvent.KEYCODE_7:
            case   KeyEvent.KEYCODE_8:
            case   KeyEvent.KEYCODE_9:
                if (len < 12) {
                    int code =  keyCode - KeyEvent.KEYCODE_0;
                    mAmountBuilder.append(code);
                    setTextA();
                }
                break;
            case KeyEvent.KEYCODE_ENTER:
                String amountStr = mAmountBuilder.toString();
                if (TextUtils.isEmpty(amountStr)) {
                    TopToast.showScuessToast(getString(R.string.input_correct_amount));
                    break;
                }
                long amount = Long.valueOf(amountStr);
                AppLog.i(TAG ,"：" + amount);
                if (TextUtils.isEmpty(amountStr) || amount == 0) {
                    break;
                }
                result = new ActionResult(TransResult.SUCC, amountStr);
                finish(result);
                break;
            case  KeyEvent.KEYCODE_DEL:
                if (len > 0) {
                    mAmountBuilder.delete(len-1, len);
                    setTextA();
                }
                break;
            case  KeyEvent.KEYCODE_BACK:
                result = new ActionResult(TransResult.ERR_ABORTED, null);
                finish(result);
                return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
