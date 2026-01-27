package com.topwise.premierpay.trans.action.activity;

import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.topwise.premierpay.R;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.EUIParamKeys;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.utils.PanUtils;

public class CardConfirmActivity extends BaseActivityWithTickForAction implements View.OnClickListener {
    private String panBlock;
    private String amount;
    private String navTitle;
    private TextView mTextCardNo;
    private TextView tVtitle;
    private TextView tVtime;

    @Override
    public void onClick(View v) {
        ActionResult result;
        switch (v.getId()) {
            case R.id.btn_cancle:
                result = new ActionResult(TransResult.ERR_ABORTED, null);
                finish(result);
                break;
            case R.id.btn_ok:
                result = new ActionResult(TransResult.SUCC, null);
                finish(result);
                break;
            default:
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_card_confirm;
    }

    @Override
    protected void initViews() {
        mTextCardNo = (TextView) findViewById(R.id.card_num);
        mTextCardNo.setText(panBlock);
        mTextCardNo.setText(PanUtils.maskedCardNo(panBlock));

        tVtitle = (TextView)findViewById(R.id.header_title);
        tVtitle.setText(navTitle.toUpperCase());

        tVtime = (TextView)findViewById(R.id.header_time);
        findViewById(R.id.btn_ok).requestFocus();
    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected void loadParam() {
        navTitle = getIntent().getStringExtra(EUIParamKeys.NAV_TITLE.toString());
        panBlock = getIntent().getStringExtra(EUIParamKeys.PANBLOCK.toString());
        amount = getIntent().getStringExtra(EUIParamKeys.TRANS_AMOUNT.toString());
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
}
