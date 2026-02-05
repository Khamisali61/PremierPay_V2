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
import android.widget.TextView;

import com.topwise.manager.AppLog;
import com.topwise.manager.utlis.DataUtils;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.EUIParamKeys;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.utils.Utils;
import com.topwise.premierpay.view.TopToast;

/**
 * 创建日期：2021/5/25 on 9:55
 * 描述:
 * 作者:wangweicheng
 */
public class InputData2Activity extends BaseActivityWithTickForAction implements View.OnClickListener{
    private static final String TAG =  TopApplication.APPNANE + InputData2Activity.class.getSimpleName();
    private EditText editText;
    private EditText editText2;
    private Button button;
    private int minLen1;
    private int maxLen1;
    private int inTpye1;
    private String hint1;

    private int minLen2;
    private int maxLen2;
    private int inTpye2;
    private String hint2;
    private String navTitle;
    private TextView tVtitle,tVtime;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_confirm:
                process();
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_input_two_data_layout;
    }

    @Override
    protected void initViews() {
        tVtitle = (TextView)findViewById(R.id.header_title);
        tVtitle.setText(navTitle.toUpperCase());

        tVtime = (TextView)findViewById(R.id.header_time);

        editText = (EditText)findViewById(R.id.ed_indata);
        editText2 = (EditText)findViewById(R.id.ed_indata_two);
        button = (Button)findViewById(R.id.bt_confirm);
        showSoftInputFromWindow(editText);

        if (!DataUtils.isNullString(hint1))
            editText.setHint(hint1);
        editText.setInputType(inTpye1);
        editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxLen1) });
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

        if (!DataUtils.isNullString(hint2))
            editText2.setHint(hint2);
        editText2.setInputType(inTpye2);
        editText2.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxLen2) });
        editText2.setOnKeyListener(new View.OnKeyListener() {
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
        String content = editText.getText().toString().trim();
        String extraContent = editText2.getText().toString().trim();
        if (DataUtils.isNullString(content)) {
            editText.setText("");
            editText.requestFocus();
            return;
        }

        if ( content.length() < minLen1 ||  content.length() > maxLen1) {
            TopToast.showFailToast(this,"please enter again");
            return;
        }
        if (!DataUtils.isNullString(extraContent)){ //check vale
            boolean check = Utils.checkMMdd(extraContent);
            if (!check){
                TopToast.showFailToast(this,"The Original date is wrong, please re-enter ");
                editText2.setText("");
                editText2.requestFocus();
                return;
            }
        }else{
            extraContent = "";
        }

        ActionResult result = new ActionResult(TransResult.SUCC, new String[] { content, extraContent });
        finish(result);
    }

    @Override
    protected void setListeners() {
        button.setOnClickListener(this);
    }

    @Override
    protected void loadParam() {
        navTitle = getIntent().getStringExtra(EUIParamKeys.NAV_TITLE.toString());
        minLen1 = getIntent().getIntExtra(EUIParamKeys.INPUT_MIN_LEN_1.toString(), 12);
        maxLen1 = getIntent().getIntExtra(EUIParamKeys.INPUT_MAX_LEN_1.toString(), 12);
        inTpye1 = getIntent().getIntExtra(EUIParamKeys.INPUT_TYPE_1.toString(), InputType.TYPE_CLASS_NUMBER);
        hint1 =   getIntent().getStringExtra(EUIParamKeys.PROMPT_1.toString());

        minLen1 = getIntent().getIntExtra(EUIParamKeys.INPUT_MIN_LEN_1.toString(), 0);
        maxLen2 = getIntent().getIntExtra(EUIParamKeys.INPUT_MAX_LEN_2.toString(), 4);
        inTpye2 = getIntent().getIntExtra(EUIParamKeys.INPUT_TYPE_2.toString(), InputType.TYPE_CLASS_NUMBER);
        hint2 =   getIntent().getStringExtra(EUIParamKeys.PROMPT_2.toString());

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
