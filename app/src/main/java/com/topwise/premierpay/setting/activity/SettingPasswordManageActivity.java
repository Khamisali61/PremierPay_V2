package com.topwise.premierpay.setting.activity;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.topwise.premierpay.R;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.action.activity.BaseActivityWithTickForAction;
import com.topwise.premierpay.view.ChangePwdDialog;

/**
 * 创建日期：2021/3/30 on 16:37
 * 描述:
 * 作者:  wangweicheng
 */
public class SettingPasswordManageActivity extends BaseActivityWithTickForAction implements View.OnClickListener {
    private static final String TAG = TopApplication.APPNANE + SettingPasswordManageActivity.class.getSimpleName();
    private TextView tVtime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tickTimerStart(120);
    }

    private ChangePwdDialog changePwdDialog;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_set_sys_admin_pwd:
                if (changePwdDialog != null) {
                    changePwdDialog.dismiss();
                    changePwdDialog = null;
                }
                changePwdDialog = new ChangePwdDialog(SettingPasswordManageActivity.this,handler,
                        getString(R.string.set_system_administrator_password),8);
                changePwdDialog.setListener(new ChangePwdDialog.OnListener() {
                    @Override
                    public void onSucc(String data) {
                        if (!TextUtils.isEmpty(data)) {
                            TopApplication.sysParam.set(SysParam.SEC_SYSPWD,data);
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                changePwdDialog.show();
                break;

            case R.id.rl_set_security_pwd:
                if (changePwdDialog != null) {
                    changePwdDialog.dismiss();
                    changePwdDialog = null;
                }
                changePwdDialog = new ChangePwdDialog(SettingPasswordManageActivity.this,handler,
                        getString(R.string.set_security_password),6);
                changePwdDialog.setListener(new ChangePwdDialog.OnListener() {
                    @Override
                    public void onSucc(String data) {
                        if (!TextUtils.isEmpty(data)) {
                            TopApplication.sysParam.set(SysParam.SEC_SECPWD,data);
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                changePwdDialog.show();
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_password_layout;
    }

    @Override
    protected void initViews() {
        ((TextView)findViewById(R.id.header_title)).setText(getString(R.string.set_operator_param));
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

                if (Integer.valueOf(time) == 0){
                    if (changePwdDialog != null)
                        changePwdDialog.dismiss();
                    ActivityStack.getInstance().pop();
                }

                break;
        }
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
