package com.topwise.premierpay.setting.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.topwise.premierpay.R;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.action.activity.BaseActivityWithTickForAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.trans.model.EUIParamKeys;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.utils.QRCodePrintUtil;
import com.topwise.premierpay.view.MTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 创建日期：2021/3/30 on 14:55
 * 描述:
 * 作者:  wangweicheng
 */
public class SettingAboutActivity extends BaseActivityWithTickForAction implements View.OnClickListener  {
    private static final String TAG = TopApplication.APPNANE + SettingAboutActivity.class.getSimpleName();
    private TextView tVtime;
    private TextView mVersionView;
    private TextView mSdkVersionView;
    private TextView mMerchantId;
    private TextView mMerchantName;
    private TextView mTermialId;
    private TextView mcc;
    private TextView mSn;
    private TextView mRomVerion;
    private TextView mSecVerion;
    private TextView mHost;
    private TextView mProt;
    private MTextView mHostUrl;
    private ImageView imageView;

    private String isAction;

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ("1".equals(isAction)) {
            tickTimerStop();
        } else {
            tickTimerStart(120);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_about_layout;
    }

    @Override
    protected void initViews() {
        ((TextView)findViewById(R.id.header_title)).setText(getString(R.string.set_setting_about));
        tVtime = (TextView)findViewById(R.id.header_time);
        imageView = (ImageView)findViewById(R.id.app_icon);

        String mid = TopApplication.sysParam.get(SysParam.MERCH_ID);
        String tid = TopApplication.sysParam.get(SysParam.TERMINAL_ID);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("MID", mid);
            jsonObject.put("TID", tid);
            String mJson = jsonObject.toString();
            if (!TextUtils.isEmpty(mJson)) {
                Bitmap qrImage = QRCodePrintUtil.genQrCode(mJson, 380, 380);
                imageView.setImageBitmap(qrImage);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected void loadParam() {
        Bundle bundle = getIntent().getExtras();
        isAction = bundle.getString(EUIParamKeys.PROMPT_1.toString(),"0");

        String temp = TopApplication.sysParam.get(SysParam.MERCH_ID);

        mMerchantId = (TextView) findViewById(R.id.merchine_id);
        if (!TextUtils.isEmpty(temp)) {
            mMerchantId.setText(temp);
        }

        mMerchantName = (TextView) findViewById(R.id.merchine_name);
        temp = TopApplication.sysParam.get(SysParam.MERCH_NAME);
        if (!TextUtils.isEmpty(temp)) {
            mMerchantName.setText(temp);
        }
        mTermialId = (TextView) findViewById(R.id.term_id);
        temp = TopApplication.sysParam.get(SysParam.TERMINAL_ID);
        if (!TextUtils.isEmpty(temp)) {
            mTermialId.setText(temp);
        }
        mcc = (TextView) findViewById(R.id.mcc);
        temp = TopApplication.sysParam.get(SysParam.PARAM_MCC);
        if (!TextUtils.isEmpty(temp)) {
            mcc.setText(temp);
        }

        mHost  = (TextView) findViewById(R.id.host_id);
        temp = TopApplication.sysParam.get(SysParam.HOSTIP);
        if (!TextUtils.isEmpty(temp)) {
            mHost.setText(temp);
        }
        mProt  = (TextView) findViewById(R.id.port_id);
        temp = TopApplication.sysParam.get(SysParam.HOSTPORT);
        if (!TextUtils.isEmpty(temp)) {
            mProt.setText(temp);
        }
        mHostUrl = (MTextView) findViewById(R.id.host_url);
        temp = TopApplication.sysParam.get(SysParam.PARAM_URL);
        if (!TextUtils.isEmpty(temp)) {
            mHostUrl.setText(temp);
        }

        mVersionView = (TextView) findViewById(R.id.version);
//      mVersionView.setText(getString(R.string.about_version) + getCustomVersionMsg(null,this));
        mVersionView.setText( "V"+ TopApplication.version);

        mSdkVersionView = (TextView) findViewById(R.id.sdkversion);
        mSdkVersionView.setText( "V"+Device.getCurrentSdkVersion());

        mRomVerion = (TextView) findViewById(R.id.tv_romversion);
        mRomVerion.setText( Device.getRomVersion());

        mSecVerion = (TextView) findViewById(R.id.tv_secversion);
        mSecVerion.setText( "V"+Device.getSecurityDriverVersion());

        mSn = (TextView) findViewById(R.id.tv_sn);
        temp = Device.getSn();
        if (!TextUtils.isEmpty(temp)) {
            mSn.setText(Device.gerModel() + temp);
        }
    }

    @Override
    protected void handleMsg(Message msg) {
        switch (msg.what){
            case TIP_TIME:
                String time = (String)msg.obj;
                if (!TextUtils.isEmpty(time))
                    tVtime.setText(time);

                if (Integer.valueOf(time) == 0) {
                    if ("1".equals(isAction)) {
                        finish(new ActionResult(TransResult.ERR_ABORTED,null));
                    } else {
                        ActivityStack.getInstance().pop();
                    }
                }
                break;
        }
    }

    public static String getCustomVersionMsg(String originalMsg, Context context) {
        StringBuilder version = new StringBuilder();
        if (originalMsg != null) {
            version.append(originalMsg);
            version.append("-");
        }
        InputStream in;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try {
            in = context.getAssets().open("version.ver");
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            if ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        version.append(content.toString());
        return version.toString();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ("1".equals(isAction)) {
                finish(new ActionResult(TransResult.ERR_ABORTED,null));
            } else {
                ActivityStack.getInstance().pop();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
