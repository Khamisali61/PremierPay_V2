package com.topwise.premierpay.setting.activity;

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
import com.topwise.kdialog.IkeyListener;
import com.topwise.manager.AppLog;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.action.activity.BaseActivityWithTickForAction;
import com.topwise.premierpay.utils.ConfiUtils;

/**
 * 创建日期：2021/3/31 on 9:48
 * 描述:
 * 作者:  wangweicheng
 */
public class SettingCommManageActivity extends BaseActivityWithTickForAction implements View.OnClickListener {
    private static final String TAG = TopApplication.APPNANE + SettingCommManageActivity.class.getSimpleName();
    private TextView tVtime;
    private TextView tVtpdu;
    private TextView tVIp;
    private TextView tVProt;
    private TextView tVIp1;
    private TextView tVProt1;
    private TextView tVComtime;
    private TextView tVhttpsUrl;

    private RelativeLayout rLIpBackups1;
    private RelativeLayout rLPortBackups1;
    private RelativeLayout rLcomTime;
    private CheckBox cBEnableSsl;
    private CheckBox cBEnableBackups;
    private CheckBox cBEnableOnline;

    private DialogEditSureCancel dialogEditSureCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tickTimerStart(120);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_set_comm_ip:
                String sIp = tVIp.getText().toString();
                setParam(new OnListener() {
                    @Override
                    public void onCallBack(String data) {
                        AppLog.i(TAG,"ip " + data);
                        if (!TextUtils.isEmpty(data)){
                            TopApplication.sysParam.set(SysParam.HOSTIP,data);
                            tVIp.setText(data);
                        }
                    }
                },getString(R.string.host_ip_1),
                        getString(R.string.host_ip_1),
                        sIp,
                        InputType.TYPE_CLASS_TEXT,50);
                break;
            case R.id.rl_set_comm_port:
                String sPort = tVProt.getText().toString();
                setParam(new OnListener() {
                             @Override
                             public void onCallBack(String data) {
                                 AppLog.i(TAG,"sPort " + data);
                                 if (!TextUtils.isEmpty(data)){
                                     TopApplication.sysParam.set(SysParam.HOSTPORT,data);
                                     tVProt.setText(data);
                                 }
                             }
                         },getString(R.string.host_port_1),
                        getString(R.string.host_port_1),
                        sPort,
                        InputType.TYPE_CLASS_NUMBER,5);
                break;
            case R.id.rl_set_comm_ip1:
                String sIp1 = tVIp1.getText().toString();
                setParam(new OnListener() {
                             @Override
                             public void onCallBack(String data) {
                                 AppLog.i(TAG,"ip " + data);
                                 if (!TextUtils.isEmpty(data)){
                                     TopApplication.sysParam.set(SysParam.HOSTIP_BACKUPS,data);
                                     tVIp1.setText(data);
                                 }
                             }
                         },getString(R.string.host_ip_2),
                        getString(R.string.host_ip_2),
                        sIp1,
                        InputType.TYPE_CLASS_TEXT,50);
                break;
            case R.id.rl_set_comm_port1:
                String sPort2 = tVProt1.getText().toString();
                setParam(new OnListener() {
                             @Override
                             public void onCallBack(String data) {
                                 AppLog.i(TAG,"sPort2 " + data);
                                 if (!TextUtils.isEmpty(data)){
                                     TopApplication.sysParam.set(SysParam.HOSTPORT_BACKUPS,data);
                                     tVProt1.setText(data);
                                 }
                             }
                         },getString(R.string.host_port_1),
                        getString(R.string.host_port_1),
                        sPort2,
                        InputType.TYPE_CLASS_NUMBER,5);
                break;
            case R.id.rl_set_comm_time:
                String time = tVComtime.getText().toString();
                setParam(new OnListener() {
                             @Override
                             public void onCallBack(String data) {
                                 AppLog.i(TAG,"time " + data);
                                 if (!TextUtils.isEmpty(data)){
                                     TopApplication.sysParam.set(SysParam.COMM_TIMEOUT,data);
                                     tVComtime.setText(data);
                                 }
                             }
                         },getString(R.string.communication_time),
                        getString(R.string.communication_time),
                        time,
                        InputType.TYPE_CLASS_NUMBER,2);
                break;
            case R.id.rl_set_comm_url:
                String url = tVhttpsUrl.getText().toString();
                setParam(new OnListener() {
                             @Override
                             public void onCallBack(String data) {
                                 AppLog.i(TAG,"url " + data);
                                 if (!TextUtils.isEmpty(data)){
                                     TopApplication.sysParam.set(SysParam.PARAM_URL,data);
                                     tVhttpsUrl.setText(data);
                                 }
                             }
                         },getString(R.string.https_url),
                        getString(R.string.https_url),
                        url,
                        InputType.TYPE_CLASS_TEXT,80);
                break;
            case R.id.rl_set_comm_tpdu:
                String tpdu = tVtpdu.getText().toString();
                setParam(new OnListener() {
                             @Override
                             public void onCallBack(String data) {
                                 AppLog.i(TAG,"url " + data);
                                 if (!TextUtils.isEmpty(data)){
                                     TopApplication.sysParam.set(SysParam.APP_TPDU,data);
                                     tVtpdu.setText(data);
                                 }
                             }
                         },getString(R.string.tpdu_value),
                        getString(R.string.tpdu_value),
                        tpdu,
                        InputType.TYPE_CLASS_NUMBER,10);
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_comm_manage_layout;
    }

    @Override
    protected void initViews() {
        ((TextView)findViewById(R.id.header_title)).setText(getString(R.string.set_commun_param));
        tVtime = (TextView)findViewById(R.id.header_time);

        String temp = TopApplication.sysParam.get(SysParam.APP_TPDU);
        //TPDU
        tVtpdu = (TextView)findViewById(R.id.tv_set_comm_tpdu);
        if (!TextUtils.isEmpty(temp)){
            tVtpdu.setText(temp);
        }
        temp = TopApplication.sysParam.get(SysParam.HOSTIP);
        //ip
        tVIp = (TextView)findViewById(R.id.tv_set_comm_ip);
        if (!TextUtils.isEmpty(temp)){
            tVIp.setText(temp);
        }
        //port
        temp = TopApplication.sysParam.get(SysParam.HOSTPORT);
        tVProt = (TextView)findViewById(R.id.tv_set_comm_port);
        if (!TextUtils.isEmpty(temp)){
            tVProt.setText(temp);
        }

        //time
        temp = TopApplication.sysParam.get(SysParam.COMM_TIMEOUT);
        tVComtime = (TextView)findViewById(R.id.tv_set_comm_time);
        if (!TextUtils.isEmpty(temp)){
            tVComtime.setText(temp);
        }

        temp = TopApplication.sysParam.get(SysParam.PARAM_URL);
        tVhttpsUrl = (TextView)findViewById(R.id.tv_set_comm_url);
        if (!TextUtils.isEmpty(temp)){
            tVhttpsUrl.setText(temp);
        }

        cBEnableOnline = (CheckBox) findViewById(R.id.ck_set_online);
        if (ConfiUtils.isDebug) {
            cBEnableOnline.setChecked(false);
        } else {
            cBEnableOnline.setChecked(true);
        }

        cBEnableOnline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppLog.d(TAG, " cBEnableOnline " +isChecked);
                if (isChecked) {
                    TopApplication.sysParam.set(SysParam.COMMUNICATION_MODE, true);
                    ConfiUtils.isDebug = false;
                } else {
                    TopApplication.sysParam.set(SysParam.COMMUNICATION_MODE, false);
                    ConfiUtils.isDebug = true;
                }
            }
        });

        cBEnableBackups = (CheckBox) findViewById(R.id.ck_set_set_comm_backups);
        tVIp1 = (TextView)findViewById(R.id.tv_set_comm_ip1);
        tVProt1 = (TextView)findViewById(R.id.tv_set_comm_port1);

        cBEnableBackups.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppLog.d(TAG, " cBEnableBackups " +isChecked);
                if (isChecked){
                    rLIpBackups1.setVisibility(View.VISIBLE);
                    rLPortBackups1.setVisibility(View.VISIBLE);
                    TopApplication.sysParam.set(SysParam.APP_COMM_ENABLE_BACKUPS,"Y");

                    handler.sendEmptyMessage(UP_BACKS);
                }else {
                    rLIpBackups1.setVisibility(View.GONE);
                    rLPortBackups1.setVisibility(View.GONE);
                    TopApplication.sysParam.set(SysParam.APP_COMM_ENABLE_BACKUPS,"N");
                }
            }
        });

        rLIpBackups1 = (RelativeLayout) findViewById(R.id.rl_set_comm_ip1);
        rLPortBackups1 = (RelativeLayout) findViewById(R.id.rl_set_comm_port1);


        temp = TopApplication.sysParam.get(SysParam.APP_COMM_ENABLE_BACKUPS);
        if (SysParam.Constant.YES.equals(temp)){
            cBEnableBackups.setChecked(true);
            rLIpBackups1.setVisibility(View.VISIBLE);
            rLPortBackups1.setVisibility(View.VISIBLE);

        }else {
            cBEnableBackups.setChecked(false);
            rLIpBackups1.setVisibility(View.GONE);
            rLPortBackups1.setVisibility(View.GONE);
        }

        //ssl
        temp = TopApplication.sysParam.get(SysParam.APP_COMM_TYPE_SSL);
        cBEnableSsl = (CheckBox) findViewById(R.id.ck_set_set_comm_enable_ssl);
        if (SysParam.Constant.YES.equals(temp)){
            cBEnableSsl.setChecked(true);
        }else {
            cBEnableSsl.setChecked(false);
        }

        cBEnableSsl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppLog.d(TAG, " cBEnableSsl " +isChecked);
                if (isChecked){
                    TopApplication.sysParam.set(SysParam.APP_COMM_TYPE_SSL,"Y");
                }else {
                    TopApplication.sysParam.set(SysParam.APP_COMM_TYPE_SSL,"N");
                }
            }
        });

    }

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
     * @param maxlen
     */
    private void setParam(final OnListener listener,String title,String hint,String connet,int type,int maxlen){
        if (dialogEditSureCancel != null){
            dialogEditSureCancel.dismiss();
            dialogEditSureCancel = null;
        }
        dialogEditSureCancel = new DialogEditSureCancel(SettingCommManageActivity.this);
        dialogEditSureCancel.setMaxlenth(maxlen);
        dialogEditSureCancel.setTitle(title);
        dialogEditSureCancel.setInputType(type);
        dialogEditSureCancel.setHint(hint);
        dialogEditSureCancel.setConnent(connet);



        dialogEditSureCancel.setMyListener(new IkeyListener() {
            @Override
            public void onConfirm(String text) {
                if (!TextUtils.isEmpty(text)){
                    listener.onCallBack(text);
                }
            }

            @Override
            public void onCancel(int res) {
                listener.onCallBack("");
            }
        } );
        dialogEditSureCancel.show();
    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected void loadParam() {

    }

    private final static int UP_BACKS = 0x01;
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
            case UP_BACKS:
                //ip1
                AppLog.i(TAG," UP_BACKSUP_BACKSUP_BACKSUP_BACKS");
                String temp = TopApplication.sysParam.get(SysParam.HOSTIP_BACKUPS);
                if (!TextUtils.isEmpty(temp)){
                    tVIp1.setText(temp);
                }
                //port1
                temp = TopApplication.sysParam.get(SysParam.HOSTPORT_BACKUPS);
                if (!TextUtils.isEmpty(temp)){
                    tVProt1.setText(temp);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TopApplication.isRuning = false;
    }
}
