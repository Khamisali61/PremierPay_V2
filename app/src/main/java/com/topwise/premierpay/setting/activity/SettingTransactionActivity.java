package com.topwise.premierpay.setting.activity;

import android.content.Intent;
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
import com.topwise.kdialog.DialogSingleChoice;
import com.topwise.kdialog.IkeyListener;
import com.topwise.kdialog.adapter.SingleBean;
import com.topwise.manager.AppLog;
import com.topwise.manager.utlis.DataUtils;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.setting.country.CurrencyActivity;
import com.topwise.premierpay.utils.Utils;
import com.topwise.premierpay.view.TopToast;
import com.topwise.premierpay.trans.action.activity.BaseActivityWithTickForAction;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：2021/3/30 on 10:03
 * 描述:
 * 作者:  wangweicheng
 */
public class SettingTransactionActivity extends BaseActivityWithTickForAction implements View.OnClickListener  {
    private static final String TAG = TopApplication.APPNANE + SettingTerminalActivity.class.getSimpleName();
    private TextView tVtime;
    private TextView tVserial;
    private TextView tVbatch;
    private TextView tVprint;
    private TextView tVprintGray;
    private TextView tVrfFree;

    private TextView tVCvmLimit;
    private TextView tVContactLimit;
    private TextView tVFlootLimit;

    private TextView tTerCap;
    private TextView tTerCountryName;


    private CheckBox cElecSignCon;
    private CheckBox cBvoidpwd;
    private CheckBox cBAuthvoidpwd;
    private CheckBox cBAuthcmdvoidpwd;
    private CheckBox cBrfFree;
    private CheckBox cBvoidSwipingCard;
    private CheckBox cBAuthVoidSwipingCard;
    private CheckBox cBAuthcmdvoidSwipingCard;
    private CheckBox cBtrackEn;
    private CheckBox cFingerprint;
    private CheckBox cBtestControl;

    private RelativeLayout rLFreeLimit;
    private DialogEditSureCancel dialogEditSureCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tickTimerStart(120);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_set_transcation_serial_num:
                setSerial();
                break;
            case R.id.rl_set_transcation_batch_num:
                setBatch();
                break;
            case R.id.rl_set_transcation_prit_num:
                setPrint();
                break;
            case R.id.rl_set_transcation_prit_gray:
                setPrintGray();
                break;
            case R.id.ck_set_transcation_elec_sign_control:
                if (cElecSignCon.isChecked()){
                    TopApplication.sysParam.set(SysParam.PARAM_ELEC_SIGN,SysParam.Constant.YES);
                }else {
                    TopApplication.sysParam.set(SysParam.PARAM_ELEC_SIGN,SysParam.Constant.NO);
                }
                break;
            case R.id.ck_set_transcation_fingerprint_control:
                if (cFingerprint.isChecked()){
                    TopApplication.sysParam.set(SysParam.PARAM_FINGERPRINT,SysParam.Constant.YES);
                }else {
                    TopApplication.sysParam.set(SysParam.PARAM_FINGERPRINT,SysParam.Constant.NO);
                }
                break;
            case R.id.ck_set_transcation_void_flag:
                if (cBvoidpwd.isChecked()){
                    TopApplication.sysParam.set(SysParam.IPTC_VOID,SysParam.Constant.YES);
                }else {
                    TopApplication.sysParam.set(SysParam.IPTC_VOID,SysParam.Constant.NO);
                }
             //   TopToast.showNormalToast(this,cBvoidpwd.isChecked()+"");
                break;
            case R.id.ck_set_transcation_auth_void_flag:
                if (cBAuthvoidpwd.isChecked()){
                    TopApplication.sysParam.set(SysParam.IPTC_PAVOID,SysParam.Constant.YES);
                }else {
                    TopApplication.sysParam.set(SysParam.IPTC_PAVOID,SysParam.Constant.NO);
                }
               // TopToast.showNormalToast(this,cBAuthvoidpwd.isChecked()+"");
                break;
            case R.id.ck_set_transcation_authcmd_void_flag:
                if (cBAuthcmdvoidpwd.isChecked()){
                    TopApplication.sysParam.set(SysParam.IPTC_PACVOID,SysParam.Constant.YES);
                }else {
                    TopApplication.sysParam.set(SysParam.IPTC_PACVOID,SysParam.Constant.NO);
                }
               // TopToast.showNormalToast(this,cBAuthcmdvoidpwd.isChecked()+"");
                break;
            case R.id.ck_set_transcation_free_flag:
                if (cBrfFree.isChecked()){
                    TopApplication.sysParam.set(SysParam.QUICK_PASS_TRANS_PIN_FREE_SWITCH,SysParam.Constant.YES);
                    rLFreeLimit.setVisibility(View.VISIBLE);
                    String temp1 =  TopApplication.sysParam.get(SysParam.QUICK_PASS_TRANS_PIN_FREE_AMOUNT);
                    if (!TextUtils.isEmpty(temp1)){
                        tVrfFree.setText(temp1);
                    }
                }else {
                    TopApplication.sysParam.set(SysParam.QUICK_PASS_TRANS_PIN_FREE_SWITCH,SysParam.Constant.NO);
                    rLFreeLimit.setVisibility(View.GONE);
                }
                break;
            case R.id.rl_set_transcation_free_limit_flag:
                setFree();
                break;
            case R.id.ck_set_transcation_swiping_void_flag:
                if (cBvoidSwipingCard.isChecked()){
                    TopApplication.sysParam.set(SysParam.UCTC_VOID,SysParam.Constant.YES);
                }else {
                    TopApplication.sysParam.set(SysParam.UCTC_VOID,SysParam.Constant.NO);
                }
                break;
            case R.id.ck_set_transcation_swiping_auth_void_flag:
                if (cBAuthVoidSwipingCard.isChecked()){
                    TopApplication.sysParam.set(SysParam.UCTC_PAVOID,SysParam.Constant.YES);
                }else {
                    TopApplication.sysParam.set(SysParam.UCTC_PAVOID,SysParam.Constant.NO);
                }
                break;
            case R.id.ck_set_transcation_swiping_authcmd_void_flag:
                if (cBAuthcmdvoidSwipingCard.isChecked()){
                    TopApplication.sysParam.set(SysParam.UCTC_PACVOID,SysParam.Constant.YES);
                }else {
                    TopApplication.sysParam.set(SysParam.UCTC_PACVOID,SysParam.Constant.NO);
                }
                break;

                //test parameter
            case R.id.rl_set_transcation_test_cvm_limit:
                String temp = tVCvmLimit.getText().toString();
                setTesrParam(R.id.rl_set_transcation_test_cvm_limit,temp,"CVM Limit",tVCvmLimit,12,InputType.TYPE_CLASS_NUMBER);
                break;
            case R.id.rl_set_transcation_test_contactless_limit:
                String temp1 = tVContactLimit.getText().toString();
                setTesrParam(R.id.rl_set_transcation_test_contactless_limit,temp1,"ContactLess Trans Limit",tVContactLimit,12,InputType.TYPE_CLASS_NUMBER);
                break;
            case R.id.rl_set_transcation_test_floot_limit:
                String temp2 = tVFlootLimit.getText().toString();
                setTesrParam(R.id.rl_set_transcation_test_floot_limit,temp2,"Floot Limit",tVFlootLimit,12,InputType.TYPE_CLASS_NUMBER);
                break;
            case R.id.rl_set_transcation_ter_cap:
                String temp3 = tTerCap.getText().toString();
                setTesrParam(R.id.rl_set_transcation_ter_cap,temp3,"Terminal Capabilities",tTerCap,6,InputType.TYPE_CLASS_TEXT);
                break;

            case R.id.rl_set_transcation_tran_curr_code:
                Intent intent =new Intent(this, CurrencyActivity.class);
                startActivity(intent);
                break;

        }

    }
    private SettingClearDialog selectDialog;
    private DialogSingleChoice dialogSingleChoice;
    private void setPrintGray() {
        if (dialogSingleChoice != null ){
            dialogSingleChoice.dismiss();
            dialogSingleChoice = null;
        }
        String temp = TopApplication.sysParam.get(SysParam.APP_PRINT_GRAY);
        int index = Integer.valueOf(temp);

        List<SingleBean> singles = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            SingleBean s = new SingleBean("Level " + i);
            if (index == i){
                s.setSelect(true);
            }else {
                s.setSelect(false);
            }
            singles.add(s);
        }

        dialogSingleChoice = new DialogSingleChoice(this);
        dialogSingleChoice.setTitle("Select print gray");
        dialogSingleChoice.setListdata(singles);
        dialogSingleChoice.setMyLietener(new IkeyListener() {
            @Override
            public void onConfirm(String text) {
                if (!DataUtils.isNullString(text)){
                    String vale = String.valueOf(Integer.valueOf(text) + 1);
                    String format = "level " + vale;
                    tVprintGray.setText(format);
                    TopApplication.sysParam.set(SysParam.APP_PRINT_GRAY,vale );
                }
            }

            @Override
            public void onCancel(int res) {
                AppLog.d(TAG,"select onCancel==========");
            }
        });
        dialogSingleChoice.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        String temp = TopApplication.sysParam.get(SysParam.APP_PARAM_TRANS_CURRENCY_NAME);
        if (!DataUtils.isNullString(temp)){
            tTerCountryName.setText(temp);
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_transaction_layout;
    }

    @Override
    protected void initViews() {
        String temp1= "";
        ((TextView)findViewById(R.id.header_title)).setText(getString(R.string.set_transcation_param));
        tVtime = (TextView)findViewById(R.id.header_time);
        String temp = TopApplication.sysParam.get(SysParam.TRANS_NO);
        //流水号
        tVserial = (TextView)findViewById(R.id.tv_transcation_serial_num);
        if (!TextUtils.isEmpty(temp)){
            String format = String.format("%06d", Long.valueOf(temp));
            tVserial.setText(format);
        }
        //批次号
        tVbatch = (TextView)findViewById(R.id.tv_transcation_batch_num);
        temp = TopApplication.sysParam.get(SysParam.BATCH_NO);
        if (!TextUtils.isEmpty(temp)){
            String format = String.format("%06d", Long.valueOf(temp));
            tVbatch.setText(format);
        }
        //打印联数
        tVprint = (TextView)findViewById(R.id.tv_transcation_prit_num);
        temp = TopApplication.sysParam.get(SysParam.APP_PRINT);
        if (!TextUtils.isEmpty(temp)){
            String format = String.format("%d", Long.valueOf(temp));
            tVprint.setText(format);
        }
        //打印灰度

        tVprintGray = (TextView)findViewById(R.id.tv_transcation_prit_gray);
        temp = TopApplication.sysParam.get(SysParam.APP_PRINT_GRAY);
        if (!TextUtils.isEmpty(temp)){
            String format = "level " + String.format("%d", Long.valueOf(temp));
            tVprintGray.setText(format);
        }
        //消费撤销输密
        cBvoidpwd = (CheckBox)findViewById(R.id.ck_set_transcation_void_flag);
        temp =  TopApplication.sysParam.get(SysParam.IPTC_VOID);
        AppLog.i(TAG,"init IPTC_VOID " + temp);
        if (SysParam.Constant.YES.equals(temp)){
            cBvoidpwd.setChecked(true);
        }else {
            cBvoidpwd.setChecked(false);
        }
        //
        cElecSignCon = (CheckBox)findViewById(R.id.ck_set_transcation_elec_sign_control);
        temp =  TopApplication.sysParam.get(SysParam.PARAM_ELEC_SIGN);
        AppLog.i(TAG,"init PARAM_ELEC_SIGN " + temp);
        if (SysParam.Constant.YES.equals(temp)){
            cElecSignCon.setChecked(true);
        }else {
            cElecSignCon.setChecked(false);
        }

        AppLog.i(TAG,"init PARAM_FINGERPRINT " + temp);
        if(TopApplication.usdkManage.getFingerprint()!=null) {
            findViewById(R.id.rl_set_transcation_fingerprint_control).setVisibility(View.VISIBLE);
            cFingerprint = (CheckBox)findViewById(R.id.ck_set_transcation_fingerprint_control);
            temp =  TopApplication.sysParam.get(SysParam.PARAM_FINGERPRINT);
            if (SysParam.Constant.YES.equals(temp)) {
                cFingerprint.setChecked(true);
            } else {
                cFingerprint.setChecked(false);
            }
        }
        //预授权撤销输密
        cBAuthvoidpwd = (CheckBox)findViewById(R.id.ck_set_transcation_auth_void_flag);
        temp =  TopApplication.sysParam.get(SysParam.IPTC_PAVOID);
        AppLog.i(TAG,"init IPTC_PAVOID " + temp);
        if (SysParam.Constant.YES.equals(temp)){
            cBAuthvoidpwd.setChecked(true);
        }else {
            cBAuthvoidpwd.setChecked(false);
        }
        //预授权完成撤销输密
        cBAuthcmdvoidpwd = (CheckBox)findViewById(R.id.ck_set_transcation_authcmd_void_flag);
        temp =  TopApplication.sysParam.get(SysParam.IPTC_PACVOID);
        AppLog.i(TAG,"init IPTC_PAVOID " + temp);
        if (SysParam.Constant.YES.equals(temp)){
            cBAuthcmdvoidpwd.setChecked(true);
        }else {
            cBAuthcmdvoidpwd.setChecked(false);
        }

        //非接
        cBrfFree = (CheckBox)findViewById(R.id.ck_set_transcation_free_flag);
        rLFreeLimit = (RelativeLayout)findViewById(R.id.rl_set_transcation_free_limit_flag);
        tVrfFree = (TextView)findViewById(R.id.tv_transcation_free_limit);
        temp =  TopApplication.sysParam.get(SysParam.QUICK_PASS_TRANS_PIN_FREE_SWITCH);
        temp1 =  TopApplication.sysParam.get(SysParam.QUICK_PASS_TRANS_PIN_FREE_AMOUNT);
        if (SysParam.Constant.YES.equals(temp)){
            cBrfFree.setChecked(true);
            rLFreeLimit.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(temp1)){
                tVrfFree.setText(temp1);
            }
        }else {
            cBrfFree.setChecked(false);
            rLFreeLimit.setVisibility(View.GONE);
        }
        //撤销刷卡
        cBvoidSwipingCard = (CheckBox)findViewById(R.id.ck_set_transcation_swiping_void_flag);
        temp =  TopApplication.sysParam.get(SysParam.UCTC_VOID);
        AppLog.i(TAG,"init UCTC_VOID " + temp);
        if (SysParam.Constant.YES.equals(temp)){
            cBvoidSwipingCard.setChecked(true);
        }else {
            cBvoidSwipingCard.setChecked(false);
        }
        //预授权撤销刷卡
        cBAuthVoidSwipingCard = (CheckBox) findViewById(R.id.ck_set_transcation_swiping_auth_void_flag);
        temp = TopApplication.sysParam.get(SysParam.UCTC_PAVOID);
        AppLog.i(TAG,"init UCTC_PAVOID " + temp);
        if (SysParam.Constant.YES.equals(temp)){
            cBAuthVoidSwipingCard.setChecked(true);
        }else {
            cBAuthVoidSwipingCard.setChecked(false);
        }
        //预授权完成撤销
        cBAuthcmdvoidSwipingCard= (CheckBox)findViewById(R.id.ck_set_transcation_swiping_authcmd_void_flag);
        temp =  TopApplication.sysParam.get(SysParam.UCTC_PACVOID);
        AppLog.i(TAG,"init UCTC_PACVOID " + temp);
        if (SysParam.Constant.YES.equals(temp)){
            cBAuthcmdvoidSwipingCard.setChecked(true);
        }else {
            cBAuthcmdvoidSwipingCard.setChecked(false);
        }

        //磁道加密
        cBtrackEn = (CheckBox)findViewById(R.id.ck_set_track_en_flag);
        cBtrackEn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    TopApplication.sysParam.set(SysParam.OTHTC_TRACK_ENCRYPT,"Y");
                }else {
                    TopApplication.sysParam.set(SysParam.OTHTC_TRACK_ENCRYPT,"N");
                }
            }
        });
        temp =  TopApplication.sysParam.get(SysParam.OTHTC_TRACK_ENCRYPT);
        AppLog.i(TAG,"init OTHTC_TRACK_ENCRYPT " + temp);
        if (SysParam.Constant.YES.equals(temp)){
            cBtrackEn.setChecked(true);
        }else {
            cBtrackEn.setChecked(false);
        }


        //
        tVCvmLimit = (TextView)findViewById(R.id.tv_transcation_test_cvm_limit);
        tVContactLimit= (TextView)findViewById(R.id.tv_transcation_test_contactless_limit);
        tVFlootLimit = (TextView)findViewById(R.id.tv_transcation_test_floot_limit);
        temp = TopApplication.sysParam.get(SysParam.CVM_LIMIT);
        if (!DataUtils.isNullString(temp)){
            tVCvmLimit.setText(temp);
        }
        temp = TopApplication.sysParam.get(SysParam.CONTACTLESS_LIMIT);
        if (!DataUtils.isNullString(temp)){
            tVContactLimit.setText(temp);
        }
        temp = TopApplication.sysParam.get(SysParam.FLOOR_LIMIT);
        if (!DataUtils.isNullString(temp)){
            tVFlootLimit.setText(temp);
        }
        tTerCap = (TextView)findViewById(R.id.tv_transcation_ter_cap);
        tTerCountryName = (TextView)findViewById(R.id.tv_transcation_tran_curr_name);

        temp = TopApplication.sysParam.get(SysParam.APP_PARAM_TER_CAP);
        if (!DataUtils.isNullString(temp)){
            tTerCap.setText(temp);
        }

        temp = TopApplication.sysParam.get(SysParam.APP_PARAM_TRANS_CURRENCY_NAME);
        if (!DataUtils.isNullString(temp)){
            tTerCountryName.setText(temp);
        }


        cBtestControl = (CheckBox)findViewById(R.id.ck_set_transcation_test_control);
        cBtestControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    TopApplication.sysParam.set(SysParam.APP_TEST_CONTROL,"Y");


                    TopApplication.sysParam.set(SysParam.PARAM_URL,"https://europa-sandbox.perseuspay.com/io/v1.0/h2hpayments");
                }else {
                    TopApplication.sysParam.set(SysParam.APP_TEST_CONTROL,"N");

                    TopApplication.sysParam.set(SysParam.PARAM_URL,"https://Online.paynext.co.in:7908/io/v1.0/h2hpayments");

                    TopApplication.sysParam.set(SysParam.CVM_LIMIT,"500000");
                    TopApplication.sysParam.set(SysParam.CONTACTLESS_LIMIT,"9999999999");
                    TopApplication.sysParam.set(SysParam.FLOOR_LIMIT,"0");

                }
            }
        });
        temp =  TopApplication.sysParam.get(SysParam.APP_TEST_CONTROL);
        AppLog.i(TAG,"init OTHTC_TRACK_ENCRYPT " + temp);
        if (SysParam.Constant.YES.equals(temp)){
            cBtestControl.setChecked(true);
        }else {
            cBtestControl.setChecked(false);
        }
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
                    if (dialogEditSureCancel != null)
                        dialogEditSureCancel.dismiss();
                    ActivityStack.getInstance().pop();
                }

                break;
        }
    }

    /**
     * 设置流水号
     */
    private void setSerial() {
        if (dialogEditSureCancel != null) {
            dialogEditSureCancel.dismiss();
            dialogEditSureCancel = null;
        }
        final String serial = tVserial.getText().toString();
        dialogEditSureCancel = new DialogEditSureCancel(SettingTransactionActivity.this);
        dialogEditSureCancel.setTitle( getString(R.string.set_transcation_serial_num));
        dialogEditSureCancel.setHint( getString(R.string.set_serial_num_please));
        dialogEditSureCancel.setConnent(serial);
        dialogEditSureCancel.setInputType(InputType.TYPE_CLASS_NUMBER);
        dialogEditSureCancel.setMaxlenth(6);
        dialogEditSureCancel.setMyListener(new IkeyListener() {
            @Override
            public void onConfirm(String text) {
                if (!TextUtils.isEmpty(text)) {
                    AppLog.i(TAG,text);
                    // 如果有流水号 不允许修改

                    //
                    String format = String.format("%06d", Long.valueOf(text));

                    if (serial.equals(format))
                        return;

                    TopApplication.sysParam.set(SysParam.TRANS_NO,format);
                    tVserial.setText(format);
                }
            }

            @Override
            public void onCancel(int res) {

            }
        });

        dialogEditSureCancel.show();
    }

    /**
     * 设置批次号
     */
    private void setBatch(){
        if (dialogEditSureCancel != null) {
            dialogEditSureCancel.dismiss();
            dialogEditSureCancel = null;
        }
        final String serial = tVbatch.getText().toString();
        dialogEditSureCancel = new DialogEditSureCancel(SettingTransactionActivity.this);
        dialogEditSureCancel.setTitle( getString(R.string.set_transcation_batch_num));
        dialogEditSureCancel.setHint( getString(R.string.set_batch_num_please));
        dialogEditSureCancel.setConnent(serial);
        dialogEditSureCancel.setInputType(InputType.TYPE_CLASS_NUMBER);
        dialogEditSureCancel.setMaxlenth(6);
        dialogEditSureCancel.setMyListener(new IkeyListener() {
            @Override
            public void onConfirm(String text) {
                if (!TextUtils.isEmpty(text)) {
                    AppLog.i(TAG,text);
                    // 如果有流水号 不允许修改

                    //
                    String format = String.format("%06d", Long.valueOf(text));

                    if (serial.equals(format))return;
                    TopApplication.sysParam.set(SysParam.BATCH_NO,format);
                    tVbatch.setText(format);
                }
            }

            @Override
            public void onCancel(int res) {

            }
        });

        dialogEditSureCancel.show();
    }

    /**
     * 设置打印联数
     */
    private void setPrint(){
        if (dialogEditSureCancel != null) {
            dialogEditSureCancel.dismiss();
            dialogEditSureCancel = null;
        }

        final String print = tVprint.getText().toString();
        dialogEditSureCancel = new DialogEditSureCancel(SettingTransactionActivity.this);
        dialogEditSureCancel.setTitle( getString(R.string.set_transcation_prit_num));
        dialogEditSureCancel.setHint( getString(R.string.set_print_num_please));
        dialogEditSureCancel.setConnent(print);
        dialogEditSureCancel.setInputType(InputType.TYPE_CLASS_NUMBER);
        dialogEditSureCancel.setMaxlenth(1);
        dialogEditSureCancel.setMyListener(new IkeyListener() {
            @Override
            public void onConfirm(String text) {
                if (!TextUtils.isEmpty(text)) {
                    AppLog.i(TAG,text);

                    //打印联数 1 - 3
                    int printNum = Integer.valueOf(text);
                    if (printNum >= 1 && printNum <= 3){
                        String format = String.format("%d", printNum);
                        if (print.equals(format))
                            return;
                        TopApplication.sysParam.set(SysParam.APP_PRINT,format);
                        tVprint.setText(format);
                    } else {
                        TopToast.showFailToast(SettingTransactionActivity.this,
                                getString(R.string.set_transcation_prit_scope));
                    }
                }
            }

            @Override
            public void onCancel(int res) {

            }
        });

        dialogEditSureCancel.show();
    }


    /**
     * 设置非接免密限额
     */
    private void setFree() {
        if (dialogEditSureCancel != null) {
            dialogEditSureCancel.dismiss();
            dialogEditSureCancel = null;
        }
        final String free = tVrfFree.getText().toString();
        dialogEditSureCancel = new DialogEditSureCancel(SettingTransactionActivity.this);
        dialogEditSureCancel.setTitle( getString(R.string.set_transcation_free_limit_flag));
        dialogEditSureCancel.setHint( getString(R.string.set_transcation_free_limit_flag));
        dialogEditSureCancel.setConnent(free);
        dialogEditSureCancel.setInputType(InputType.TYPE_CLASS_NUMBER);
        dialogEditSureCancel.setMaxlenth(9);
        dialogEditSureCancel.setMyListener(new IkeyListener() {
            @Override
            public void onConfirm(String text) {
                if (!TextUtils.isEmpty(text)) {
                    AppLog.i(TAG,text);
                    TopApplication.sysParam.set(SysParam.QUICK_PASS_TRANS_PIN_FREE_AMOUNT,text);
                    tVrfFree.setText(text);
                }
            }

            @Override
            public void onCancel(int res) {

            }
        });

        dialogEditSureCancel.show();
    }

    /**
     * 设置非接免密限额
     */
    private void setTesrParam(final int id, String defaultString, final String title, final TextView textView, final int maxLen, final int inputType){
        if (dialogEditSureCancel != null) {
            dialogEditSureCancel.dismiss();
            dialogEditSureCancel = null;
        }
        dialogEditSureCancel = new DialogEditSureCancel(SettingTransactionActivity.this);
        dialogEditSureCancel.setTitle(title);
        dialogEditSureCancel.setHint(title);
        dialogEditSureCancel.setMaxlenth(maxLen);
        dialogEditSureCancel.setConnent(defaultString);
        dialogEditSureCancel.setInputType(inputType);
        dialogEditSureCancel.setMyListener(new IkeyListener() {
            @Override
            public void onConfirm(String text) {
                if (!TextUtils.isEmpty(text)) {
                    AppLog.i(TAG,"setTesrParam"+title +" "+text);
                    textView.setText(text);
                    switch (id) {
                        case R.id.rl_set_transcation_test_cvm_limit:
                            TopApplication.sysParam.set(SysParam.CVM_LIMIT,text);
                            Utils.reloadCombinationList();
                            break;
                        case R.id.rl_set_transcation_test_contactless_limit:
                            TopApplication.sysParam.set(SysParam.CONTACTLESS_LIMIT,text);
                            Utils.reloadCombinationList();
                            break;
                        case R.id.rl_set_transcation_test_floot_limit:
                            TopApplication.sysParam.set(SysParam.FLOOR_LIMIT,text);
                            Utils.reloadCombinationList();
                            break;
                        case R.id.rl_set_transcation_ter_cap:
                            TopApplication.sysParam.set(SysParam.APP_PARAM_TER_CAP,text);
                            break;
                        case R.id.rl_set_transcation_tran_curr_code:
                            TopApplication.sysParam.set(SysParam.APP_PARAM_TRANS_CURRENCY_CODE,text);
                            break;
                    }
                }
            }

            @Override
            public void onCancel(int res) {

            }
        });

        dialogEditSureCancel.show();
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
