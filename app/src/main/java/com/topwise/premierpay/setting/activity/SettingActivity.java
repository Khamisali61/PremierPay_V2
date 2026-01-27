package com.topwise.premierpay.setting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.trans.action.activity.BaseActivityWithTickForAction;
import com.topwise.premierpay.trans.model.EUIParamKeys;

public class SettingActivity extends BaseActivityWithTickForAction implements View.OnClickListener {
    private static final String TAG = TopApplication.APPNANE + SettingActivity.class.getSimpleName();
    private TextView tVtime;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        ((TextView)findViewById(R.id.header_title)).setText(getString(R.string.set_setting));
        tVtime = (TextView)findViewById(R.id.header_time);
    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected void loadParam() {

    }

    @Override
    protected void handleMsg(Message msg) {
        switch (msg.what){
            case TIP_TIME:
                String time = (String)msg.obj;
                if (!TextUtils.isEmpty(time))
                    tVtime.setText(time);

                if (Integer.valueOf(time) == 1){
                    ActivityStack.getInstance().pop();
                }

                break;
        }
    }

    @Override
    public void onClick(View v) {
        tickTimerStop();

        switch (v.getId()){
            case R.id.rl_set_terminal:
                startActivity(new Intent(SettingActivity.this, SettingTerminalActivity.class));
                break;
            case R.id.rl_set_transcation:
                startActivity(new Intent(SettingActivity.this, SettingTransactionActivity.class));
                break;
            case R.id.rl_set_key:
                startActivity(new Intent(SettingActivity.this, SettingKeyManageActivity.class));
                break;
            case R.id.rl_set_operator:
                startActivity(new Intent(SettingActivity.this, SettingPasswordManageActivity.class));
                break;
            case R.id.rl_set_commun:
                startActivity(new Intent(SettingActivity.this, SettingCommManageActivity.class));
                break;
            case R.id.rl_set_other_manage:
                startActivity(new Intent(SettingActivity.this, SettingOtherManageActivity.class));
                break;
            case R.id.rl_set_about:
                Intent  intent = new Intent(SettingActivity.this, SettingAboutActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(EUIParamKeys.PROMPT_1.toString(), "0");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tickTimerStart(180);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            tickTimerStop();
            ActivityStack.getInstance().pop();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
