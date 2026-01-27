package com.topwise.premierpay.trans.action.activity;

import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.topwise.toptool.api.utils.AppLog;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.daoutils.DaoUtilsStore;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.EUIParamKeys;
import com.topwise.premierpay.trans.model.TestParam;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.utils.SmallScreenUtil;
import com.topwise.premierpay.utils.ThreadPoolUtils;
import com.topwise.premierpay.view.TopToast;

import java.util.Random;

public class InputAmountDataActivity extends BaseActivityWithTickForAction implements View.OnClickListener {
    private static final String TAG = TopApplication.APPNANE +InputAmountDataActivity.class.getSimpleName();
    private TextView tVtitle;
    private TextView tVtime;
    private Button Llbutton;
    private String navTitle;
    private String tip;

    private StringBuilder mAmountBuilder;
    private TextView  mTextAmount;

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

            case R.id.button_ok:
                String amountStr = mAmountBuilder.toString();
                if (TextUtils.isEmpty(amountStr)){
                    TopToast.showScuessToast(getString(R.string.input_correct_amount));
                    break;
                }
                long amount = Long.valueOf(amountStr);
                com.topwise.toptool.api.utils.AppLog.i(TAG ,"：" + amount);
                if (TextUtils.isEmpty(amountStr) || amount == 0) {
                    break;
                }
                result = new ActionResult(TransResult.SUCC, amountStr);
                finish(result);
                break;
            case R.id.btn_del:
                if (len > 0) {
                    mAmountBuilder.delete(len-1, len);
                    setTextA();
                }
                break;
            case R.id.btn_clear:
                mAmountBuilder.delete(0, mTextAmount.length());
                setTextA();
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_inamount;
    }

    @Override
    protected void initViews() {
        tVtitle = (TextView)findViewById(R.id.header_title);
        tVtitle.setText(navTitle);

        tVtime = (TextView)findViewById(R.id.header_time);
        mTextAmount = (TextView) findViewById(R.id.tv_amount);
        findViewById(R.id.btn0).setOnClickListener(this);
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
        findViewById(R.id.btn4).setOnClickListener(this);
        findViewById(R.id.btn5).setOnClickListener(this);
        findViewById(R.id.btn6).setOnClickListener(this);
        findViewById(R.id.btn7).setOnClickListener(this);
        findViewById(R.id.btn8).setOnClickListener(this);
        findViewById(R.id.btn9).setOnClickListener(this);
        findViewById(R.id.btn_del).setOnClickListener(this);
        findViewById(R.id.btn_clear).setOnClickListener(this);
        findViewById(R.id.button_ok).setOnClickListener(this);

        mAmountBuilder = new StringBuilder();
        setTextA();
    }

    private void setTextA() {
        String amountStr = mAmountBuilder.toString();
        AppLog.v(TAG, "amountStr = "+amountStr);
        String curr =TopApplication.sysParam.get(SysParam.APP_PARAM_TRANS_CURRENCY_SYMBOL) ;
        if (TextUtils.isEmpty(amountStr)) {
            mTextAmount.setText(curr +"0.00");
            SmallScreenUtil.getInstance().showAmount(tVtitle.getText().toString(), curr+"0.00");
            return ;
        }
        long amount = Long.valueOf(amountStr);
        AppLog.v(TAG, "amount long  = "+amount);

        amountStr = curr + String.format("%d.%02d", amount/100, amount%100);
        AppLog.v(TAG, "format  = "+amountStr);
        mTextAmount.setText(amountStr);
        SmallScreenUtil.getInstance().showAmount(tVtitle.getText().toString(), amountStr);
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
                String time = (String)msg.obj;
                if (!TextUtils.isEmpty(time))
                    tVtime.setText(time);
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
