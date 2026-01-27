package com.topwise.premierpay.trans.action.activity;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.EUIParamKeys;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.utils.SmallScreenUtil;
import com.topwise.premierpay.view.SignatureView;
import com.topwise.premierpay.view.TopToast;

public class ElecSignActivity extends BaseActivityWithTickForAction implements View.OnClickListener{
    private static final String TAG =  TopApplication.APPNANE + ElecSignActivity.class.getSimpleName();
    private SignatureView signatureView;
    private String navTitle;
    private TransData transData;
    private TextView tVtime;
    private TextView tVtitle;

    @Override
    public void onClick(View v) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_elec_sign_layout;
    }

    @Override
    protected void initViews() {
        signatureView = (SignatureView)findViewById(R.id.sv_elec_signe);
        signatureView.setContent(transData.getDatetime());

        findViewById(R.id.bt_elec_sign_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signatureView.clear();
            }
        });

        findViewById(R.id.bt_elec_sign_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!signatureView.getTouched()) {
                    TopToast.showFailToast(mContext, getString(R.string.no_sign_tip));
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String file =  System.currentTimeMillis()+"";
                        boolean ret  = signatureView.save(file,true,0);
                        finish(new ActionResult( TransResult.SUCC, ret?file:""));
                    }
                }).start();
            }
        });

        tVtime = (TextView)findViewById(R.id.header_time);
        tVtitle = (TextView)findViewById(R.id.header_title);
        tVtitle.setText(navTitle.toUpperCase());
        SmallScreenUtil.getInstance().showMessage("electronic signature");
    }

    @Override
    protected void setListeners() {

    }

    @Override
    public void onTimout() {
        ActionResult result = new ActionResult(TransResult.SUCC, null);
        finish(result);
    }

    @Override
    protected void loadParam() {
        Bundle bundle = getIntent().getExtras();
        navTitle = getIntent().getStringExtra(EUIParamKeys.NAV_TITLE.toString());
        transData = (TransData) bundle.getSerializable(EUIParamKeys.CONTENT.toString());
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
            ActionResult result = new ActionResult(TransResult.SUCC, null);
            finish(result);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DEL) {
            signatureView.clear();
        }
        return super.onKeyDown(keyCode, event);
    }
}
