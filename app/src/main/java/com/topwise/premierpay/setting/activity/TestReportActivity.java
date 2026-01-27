package com.topwise.premierpay.setting.activity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.topwise.manager.AppLog;
import com.topwise.manager.utlis.DataUtils;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.daoutils.DaoUtilsStore;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.action.activity.BaseActivityWithTickForAction;
import com.topwise.premierpay.trans.core.TransContext;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.trans.model.TransStatusSum;
import com.topwise.premierpay.transmit.TransProcessListenerImpl;
import com.topwise.premierpay.view.TopToast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * 创建日期：2021/3/30 on 10:03
 * 描述:
 * 作者:  wangweicheng
 */
public class TestReportActivity extends BaseActivityWithTickForAction {
    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/topwise/";
    private static final String FileName = "net_status.log";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_trans_netstatus_layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        ((TextView)findViewById(R.id.header_title)).setText("Test Report");

        String temp = "";
        temp = TopApplication.sysParam.get(SysParam.MERCH_NAME);
        ((TextView)findViewById(R.id.tv_merchant_name)).setText(temp);

        temp = TopApplication.sysParam.get(SysParam.MERCH_ID);
        ((TextView)findViewById(R.id.tv_merchant_num)).setText(temp);

        temp = TopApplication.sysParam.get(SysParam.TERMINAL_ID);
        ((TextView)findViewById(R.id.tv_terminal_num)).setText(temp);

        temp = TopApplication.sysParam.get(SysParam.BATCH_NO);
        if (!DataUtils.isNullString(temp)) temp = String.format("%06d",Long.valueOf(temp));
        ((TextView)findViewById(R.id.tv_batch_num)).setText(temp);

       // temp = Device.getDateTime();
        ((TextView)findViewById(R.id.tv_data_time)).setText(temp);

        findViewById(R.id.bt_upload_trans_net).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upLoadNet();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                final TransStatusSum statusSum = Component.calNetStatus();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (statusSum != null) {
                            ((TextView) findViewById(R.id.connect_avg_time)).setText(statusSum.getConnectTime() + "");
                            ((TextView) findViewById(R.id.send_avg_time)).setText(statusSum.getSendTime() + "");
                            ((TextView) findViewById(R.id.receive_avg_time)).setText(statusSum.getReceiveTime() + "");
                            ((TextView) findViewById(R.id.communication_avg_time)).setText(statusSum.getCommTime() + "");
                            ((TextView) findViewById(R.id.pack_avg_time)).setText(statusSum.getPackTime() + "");
                            ((TextView) findViewById(R.id.unpack_avg_time)).setText(statusSum.getUnpackTime() + "");
                            ((TextView) findViewById(R.id.wifi_succ_num)).setText(statusSum.getWifiSucc() + "");
                            ((TextView) findViewById(R.id.wifi_total_num)).setText(statusSum.getWifiTotal() + "");
                            ((TextView) findViewById(R.id.wifi_succ_rate)).setText(statusSum.getWifiRate() + "");
                            ((TextView) findViewById(R.id.mobile_succ_num)).setText(statusSum.getMobileSucc() + "");
                            ((TextView) findViewById(R.id.mobile_total_num)).setText(statusSum.getMobileTotal() + "");
                            ((TextView) findViewById(R.id.mobile_succ_rate)).setText(statusSum.getMobileRate() + "");
                            ((TextView) findViewById(R.id.total_succ_num)).setText(statusSum.getNetSucc()+ "");
                            ((TextView) findViewById(R.id.total_total_num)).setText(statusSum.getNetTotal() + "");
                            ((TextView) findViewById(R.id.total_total_rate)).setText(statusSum.getNetTotalSuccRate() + "");


                            ((TextView) findViewById(R.id.total_count)).setText(statusSum.getTotal() + "");
                            ((TextView) findViewById(R.id.network_fail_no)).setText(statusSum.getNetFailCount()+ "");
                            ((TextView) findViewById(R.id.network_fail_rate)).setText(statusSum.getNetFailRate() + "");
                            ((TextView) findViewById(R.id.card_fail_no)).setText(statusSum.getCardFailCount() + "");
                            ((TextView) findViewById(R.id.card_fail_rate)).setText(statusSum.getCardFailRate() + "");
                            ((TextView) findViewById(R.id.other_fail_no)).setText(statusSum.getUnknownFailCount() + "");
                            ((TextView) findViewById(R.id.other_fail_rate)).setText(statusSum.getUnknownFailRate() + "");
                            ((TextView) findViewById(R.id.total_fail_no)).setText(statusSum.getTotalFailCount() + "");
                            ((TextView) findViewById(R.id.total_fail_rate)).setText(statusSum.getTotalFailRate() + "");

                            ((TextView)findViewById(R.id.tv_begin_time)).setText(statusSum.getBeginTime());
                            ((TextView)findViewById(R.id.tv_end_time)).setText(statusSum.getEndTime());
                            long beginTime =  Device.timeConverter(statusSum.getBeginTime());
                            long endTime =  Device.timeConverter(statusSum.getEndTime());

                            ((TextView)findViewById(R.id.tv_data_time)).setText((endTime-beginTime)+"s");
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected void loadParam() {

    }

    @Override
    protected void handleMsg(Message msg) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            tickTimerStop();
            ActivityStack.getInstance().pop();
            TopApplication.isRuning =false;
            TransContext.getInstance().setCurrentContext(null);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void upLoadNet() {
        int transCount = DaoUtilsStore.getInstance().getmTransStatusDaoUtils().getTransCount();
        AppLog.d("DetailActivity", "trans status  " + transCount);
        if (transCount == 0) {
            TopToast.showFailToast(TopApplication.mApp, "No transaction record");
            return;
        }
        //判断是否打印明细
        new Thread(new Runnable() {
            @Override
            public void run() {
                TransProcessListenerImpl transProcessListenerImpl = new TransProcessListenerImpl();
                transProcessListenerImpl.onShowProgress(getString(R.string.wait_process), 60);
                generateFile();
                transProcessListenerImpl.onHideProgress();
            }
        }).start();
    }

    private void generateFile() {
        BufferedWriter fos = null;
        try {
            File file = new File(PATH);
            if (!file.exists()) {
                file.mkdir();
            }
            file = new File(PATH, FileName);
            fos = new BufferedWriter(new FileWriter(file, true));

            StringBuffer total = new StringBuffer();
            String temp = "";
            temp = getString(R.string.merchine_name) + ":" + TopApplication.sysParam.get(SysParam.MERCH_NAME) + "\n";
            temp += getString(R.string.merchine_id) + ":" + TopApplication.sysParam.get(SysParam.MERCH_ID) + "\n";
            temp += getString(R.string.term_id) + ":" + TopApplication.sysParam.get(SysParam.TERMINAL_ID) + "\n";
            temp += getString(R.string.set_transcation_batch_num) + ":" + String.format("%06d", Long.valueOf(TopApplication.sysParam.get(SysParam.BATCH_NO))) + "\n";

            TransStatusSum statusSum = Component.calNetStatus();

            temp += getString(R.string.connect_avg_time) + ":" + statusSum.getConnectTime() + "\n";
            temp += getString(R.string.send_avg_time) + ":" + statusSum.getSendTime() + "\n";
            temp += getString(R.string.receive_avg_time) + ":" + statusSum.getReceiveTime() + "\n";
            temp += getString(R.string.communication_avg_time) + ":" + statusSum.getCommTime() + "\n";
            temp += getString(R.string.pack_avg_time) + ":" + statusSum.getPackTime() + "\n";
            temp += getString(R.string.unpack_avg_time) + ":" + statusSum.getUnpackTime() + "\n";
            temp += getString(R.string.wifi_succ_num) + ":" + statusSum.getWifiSucc() + "\n";
            temp += getString(R.string.wifi_total) + ":" + statusSum.getWifiTotal() + "\n";
            temp += getString(R.string.wifi_succ_rate) + ":" + statusSum.getWifiRate() + "\n";


            temp += getString(R.string.mobile_succ_num) + ":" + statusSum.getMobileSucc() + "\n";
            temp += getString(R.string.mobile_total_num) + ":" + statusSum.getMobileTotal() + "\n";
            temp += getString(R.string.mobile_succ_rate) + ":" + statusSum.getMobileRate() + "\n";
            temp += getString(R.string.total_succ_num) + ":" + statusSum.getNetSucc() + "\n";
            temp += getString(R.string.total_total_num) + ":" + statusSum.getNetTotal() + "\n";
            temp += getString(R.string.total_total_rate) + ":" + statusSum.getNetTotalSuccRate() + "\n\n";
            fos.write(temp);


            List<TransStatusSum> list = DaoUtilsStore.getInstance().getmTransStatusDaoUtils().queryAll();
            for (TransStatusSum bean : list) {
                temp =  getString(R.string.receipt_en_date) + ":" +bean.getDatetime() + "\n";
                temp += getString(R.string.pay_voucher_num) + ":" +String.format("%06d",bean.getId()) + "\n";
                temp += getString(R.string.communication_type) + ":" +(bean.getCommType()==0?"Wifi":"Moblie") + "\n";
                temp += getString(R.string.single_value) + ":" +bean.getSingleValue() + "\n";
                temp += getString(R.string.comm_result) + ":" + TransResult.getMessage(this,bean.getResult()) + "\n";
                temp += getString(R.string.time_statistrics) + ":" +"Connection=" +bean.getConnectTime() +" ,Pack=" +bean.getPackTime()
                        +" ,Send=" +bean.getSendTime() +" ,Receive=" +bean.getReceiveTime()+" ,Unpack=" +bean.getUnpackTime()
                        +" ,Communication=" +(bean.getConnectTime()+bean.getPackTime()+bean.getSendTime() +bean.getReceiveTime()+bean.getUnpackTime()) +"\n\n";
                fos.write(temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TopToast.showNormalToast(TopApplication.mApp ,"File Save On " + PATH + FileName);
            }
        });
    }
}

