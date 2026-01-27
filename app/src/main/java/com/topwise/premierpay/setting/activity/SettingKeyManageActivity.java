package com.topwise.premierpay.setting.activity;

import static com.topwise.premierpay.app.TopApplication.sysParam;

import android.os.Bundle;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.topwise.kdialog.DialogEditSureCancel;
import com.topwise.kdialog.DialogSure;
import com.topwise.kdialog.IkeyListener;

import com.topwise.toptool.api.convert.IConvert;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.action.activity.BaseActivityWithTickForAction;
import com.topwise.premierpay.trans.model.Device;

/**
 * 创建日期：2021/3/30 on 19:15
 * 描述:
 * 作者:  wangweicheng
 */
public class SettingKeyManageActivity extends BaseActivityWithTickForAction implements View.OnClickListener {
    private static final String TAG = TopApplication.APPNANE + SettingKeyManageActivity.class.getSimpleName();
    private TextView tVtime;
    private TextView tVmkindex;
    private RelativeLayout rlPinkey;
    private RelativeLayout rlDatakey;
    private CheckBox setPciMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tickTimerStart(120);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_set_master_key_index:
                String mkIndex = tVmkindex.getText().toString();
                setParam(new OnListener() {
                    @Override
                    public void onCallBack(String data) {
                        if (!TextUtils.isEmpty(data)) {
                            sysParam.set(SysParam.MK_INDEX,data);
                            tVmkindex.setText(data);
                        }

                    }
                },getString(R.string.set_master_key_index),getString(R.string.set_master_key_index),
                        mkIndex, InputType.TYPE_CLASS_NUMBER,1);
                break;
            case R.id.rl_set_pin_key:
                //1. index
                //2. key
                setParam(new OnListener() {
                             @Override
                             public void onCallBack(String data) {
                                 if (!TextUtils.isEmpty(data)) {

                                     boolean b = Device.writeTPK(TopApplication.convert.strToBcd(data, IConvert.EPaddingPosition.PADDING_RIGHT), null);
                                     DialogSure dialogSure = new DialogSure(SettingKeyManageActivity.this);
                                     dialogSure.tickTimerStart(5);
                                     dialogSure.setContent(getString(R.string.set_enter_pek));
                                     if (b) {
                                         dialogSure.setSucessLogo();
                                         Device.beepSucc();
                                     } else {
                                         dialogSure.setFailLogo();
                                         Device.beepFail();
                                     }
                                     dialogSure.show();
                                 }
                             }
                         },getString(R.string.set_enter_pek),getString(R.string.set_enter_pek),
                        "", InputType.TYPE_CLASS_TEXT,32);
                //3. ksn
                break;
            case R.id.rl_set_mac_key:
                //1. index
                //2. key
                setParam(new OnListener() {
                             @Override
                             public void onCallBack(String data) {
                                 if (!TextUtils.isEmpty(data)) {

                                     boolean b = Device.writeMAK(TopApplication.convert.strToBcd(data, IConvert.EPaddingPosition.PADDING_RIGHT), null);
                                     DialogSure dialogSure = new DialogSure(SettingKeyManageActivity.this);
                                     dialogSure.tickTimerStart(5);
                                     dialogSure.setContent(getString(R.string.set_enter_mak));
                                     if (b) {
                                         dialogSure.setSucessLogo();
                                         Device.beepSucc();
                                     } else {
                                         dialogSure.setFailLogo();
                                         Device.beepFail();
                                     }
                                     dialogSure.show();
                                 }
                             }
                         },getString(R.string.set_enter_mak),getString(R.string.set_enter_mak),
                        "", InputType.TYPE_CLASS_TEXT,32);
                //3. ksn
                break;
            case R.id.rl_set_data_key:
                //1. index
                //2. key
                //3. ksn
                break;
            case R.id.rl_set_unionpay_key:
                setParam(new OnListener() {
                             @Override
                             public void onCallBack(String data) {
                                 if (!TextUtils.isEmpty(data)) {

                                     boolean b = Device.writeTMK(TopApplication.convert.strToBcd(data, IConvert.EPaddingPosition.PADDING_RIGHT));
                                     DialogSure dialogSure = new DialogSure(SettingKeyManageActivity.this);
                                     dialogSure.tickTimerStart(5);
                                     dialogSure.setContent(getString(R.string.set_enter_unionpay_key));
                                     if (b) {
                                         dialogSure.setSucessLogo();
                                         Device.beepSucc();
                                     } else {
                                         dialogSure.setFailLogo();
                                         Device.beepFail();
                                     }
                                     dialogSure.show();
                                 }
                             }
                         },getString(R.string.set_enter_unionpay_key),getString(R.string.set_enter_unionpay_key),
                        "", InputType.TYPE_CLASS_TEXT,32);
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_key_layout;
    }

    @Override
    protected void initViews() {
        ((TextView)findViewById(R.id.header_title)).setText(getString(R.string.set_key_param));
        tVtime = (TextView)findViewById(R.id.header_time);

        tVmkindex = (TextView)findViewById(R.id.tv_master_key_index);
        String temp = sysParam.get(SysParam.MK_INDEX);
        if (!TextUtils.isEmpty(temp)){
            tVmkindex.setText(temp);
        }

        setPciMode = (CheckBox) findViewById(R.id.ck_set_pci_mode);
        setPciMode.setChecked((boolean) sysParam.get(SysParam.PCI_MODE, false));
        setPciMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sysParam.set(SysParam.PCI_MODE, isChecked);
            }
        });
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

                if (Integer.valueOf(time) == 0) {
                    if (dialogEditSureCancel != null)
                        dialogEditSureCancel.dismiss();
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

    private DialogEditSureCancel dialogEditSureCancel;

    public interface OnListener {
        public void onCallBack(String data);
    }

    /**
     *
     * @param listener
     * @param title
     * @param hint
     * @param connet
     * @param type InputType.TYPE_CLASS_NUMBER
     * @param maxLen
     */
    private void setParam(final OnListener listener, String title, String hint, String connet, int type, int maxLen) {
        if (dialogEditSureCancel != null) {
            dialogEditSureCancel.dismiss();
            dialogEditSureCancel = null;
        }
        dialogEditSureCancel = new DialogEditSureCancel(SettingKeyManageActivity.this);
        dialogEditSureCancel.setTitle(title);
        dialogEditSureCancel.setHint(hint);
        dialogEditSureCancel.setConnent(connet);
        dialogEditSureCancel.setInputType(type);
        dialogEditSureCancel.setMaxlenth(maxLen);

        dialogEditSureCancel.setMyListener(new IkeyListener() {
            @Override
            public void onConfirm(String text) {
                if (!TextUtils.isEmpty(text)) {
                    listener.onCallBack(text);
                }
            }

            @Override
            public void onCancel(int res) {
                listener.onCallBack("");
            }
        });

        dialogEditSureCancel.show();
    }
}
