package com.topwise.premierpay.trans.action.activity;

import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.topwise.manager.AppLog;
import com.topwise.manager.utlis.DataUtils;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.utils.ScanCodeUtils;
import com.topwise.premierpay.view.TopToast;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.EUIParamKeys;
import com.topwise.premierpay.trans.model.TransResult;

/**
 * 创建日期：2021/4/12 on 16:27
 * 描述:
 * 作者:  wangweicheng
 */
public class InputDataActivity extends BaseActivityWithTickForAction implements View.OnClickListener {
    private static final String TAG =  TopApplication.APPNANE + InputDataActivity.class.getSimpleName();

    private EditText editText;
    private Button button;
    private int maxLen;
    private int minLen;
    private int inTpye;
    private String navTitle;
    private String hint;
    private Boolean supScan;
    private TextView tVtitle,tVtime;
    private ImageView imageView;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_confirm:
                process();
                break;
            case R.id.iv_scan:
                ScanCodeUtils scanCodeUtils = new ScanCodeUtils("","");
                scanCodeUtils.setOnScanListener(new ScanCodeUtils.onScanListener() {
                    @Override
                    public void onCancel(int r) {
                        AppLog.d(TAG,"ActionQrScan onCancel ======== " + r);
                        finish(new ActionResult(TransResult.ERR_ABORTED, null));
                    }

                    @Override
                    public void onResult(final String s) {
                        if (s != null && s.length() > 0) {
                            AppLog.d(TAG,"ActionQrScan onResult ======== " + s);

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    editText.setText(s);
                                }
                            });

                        } else {
                            finish(new ActionResult(TransResult.ERR_ABORTED, null));
                        }
                    }
                });
                scanCodeUtils.startScan();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_inputdata_layout;
    }

    @Override
    protected void initViews() {
        tVtitle = (TextView)findViewById(R.id.header_title);
        tVtitle.setText(navTitle.toUpperCase());

        tVtime = (TextView)findViewById(R.id.header_time);

        editText = (EditText)findViewById(R.id.ed_indata);
        button = (Button)findViewById(R.id.bt_confirm);
        showSoftInputFromWindow(editText);

        editText.setInputType(inTpye );
        imageView = (ImageView)findViewById(R.id.iv_scan);
        if (supScan){
            imageView.setVisibility(View.VISIBLE);
        }else {
            imageView.setVisibility(View.GONE);
        }

        if (!DataUtils.isNullString(hint))
            editText.setHint(hint);

        editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxLen) });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                AppLog.i(TAG,"onKey " + keyCode);
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    process();
                    return true;
                }
                return false;
            }
        });
    }

    private void process() {
        AppLog.i(TAG,"process confirm onClick");
        String editString = editText.getText().toString();
        if (TextUtils.isEmpty(editString)){
            TopToast.showFailToast(this,"input error!");
            return;
        }
        if ( editString.length() < minLen ||  editString.length() > maxLen) {
            TopToast.showFailToast(this,"Please Input Correct Transaction Number");
            return;
        }

        ActionResult result = new ActionResult(TransResult.SUCC, editString);
        finish(result);

    }

    @Override
    protected void setListeners() {
        button.setOnClickListener(this);
        imageView.setOnClickListener(this);
    }

    @Override
    protected void loadParam() {

        supScan = getIntent().getBooleanExtra(EUIParamKeys.SUPPORT_SCAN.toString(),false);
        hint = getIntent().getStringExtra(EUIParamKeys.PROMPT_1.toString());
        navTitle = getIntent().getStringExtra(EUIParamKeys.NAV_TITLE.toString());
        minLen = getIntent().getIntExtra(EUIParamKeys.INPUT_MIN_LEN_1.toString(), 0);
        maxLen = getIntent().getIntExtra(EUIParamKeys.INPUT_MAX_LEN_1.toString(), 6);
        inTpye = getIntent().getIntExtra(EUIParamKeys.INPUT_TYPE_1.toString(), InputType.TYPE_CLASS_NUMBER);
        AppLog.i(TAG,"supScan " + supScan + "，hint " + hint + "，navTitle " + navTitle + "，minLen " + minLen + "，maxLen " + maxLen + "，inTpye " + inTpye);
    }

    @Override
    protected void handleMsg(Message msg) {
        switch (msg.what){
            case TIP_TIME:
                String time = (String)msg.obj;
                if (!TextUtils.isEmpty(time))
                    tVtime.setText(time);
                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ActionResult result = new ActionResult(TransResult.ERR_ABORTED, null);
            finish(result);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showSoftInputFromWindow(EditText editText){
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

}
