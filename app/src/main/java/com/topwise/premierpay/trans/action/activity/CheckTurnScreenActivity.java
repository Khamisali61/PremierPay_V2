package com.topwise.premierpay.trans.action.activity;

import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.topwise.premierpay.R;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.EUIParamKeys;
import com.topwise.premierpay.trans.model.TransResult;

/**
 * @author Victor(xiedianxin)
 * @brief description
 * @date 2023-08-08
 */
public class CheckTurnScreenActivity extends BaseActivityWithTickForAction {

    private String message;

    private String message2;

    private String navTitle;

    private TextView textMessage;

    private TextView textMessage2;

    private TextView textViewTitle;

    private TextView textTime;

    private Button confirm;

    private Button cancel;

    private ActionResult result;

    @Override
    protected void initViews() {
        textMessage = (TextView) findViewById(R.id.turn_screen_text);
        textMessage.setText(message);

        textMessage2 = (TextView) findViewById(R.id.turn_screen_text2);
        textMessage2.setText(message2);

        textViewTitle = (TextView) findViewById(R.id.header_title);
        textViewTitle.setText(navTitle.toUpperCase());

        textTime = (TextView) findViewById(R.id.header_time);

        confirm = (Button) findViewById(R.id.confirm_turn_screen);
        confirm.requestFocus();
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result = new ActionResult(TransResult.SUCC, null);
                finish(result);
            }
        });

        cancel = (Button) findViewById(R.id.cancel_turn_screen);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result = new ActionResult(TransResult.ERR_ABORTED, null);
                finish(result);
            }
        });
    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected void loadParam() {
        navTitle = getIntent().getStringExtra(EUIParamKeys.NAV_TITLE.toString());
        message = getIntent().getStringExtra(EUIParamKeys.PROMPT_1.toString());
        message2 = getIntent().getStringExtra(EUIParamKeys.PROMPT_2.toString());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_turn_screen;
    }

    @Override
    protected void handleMsg(Message msg) {
        if (msg.what == TIP_TIME) {
            String time = (String) msg.obj;
            if (!TextUtils.isEmpty(time)) {
                textTime.setText(time);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            result = new ActionResult(TransResult.ERR_ABORTED,null);
            finish(result);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
