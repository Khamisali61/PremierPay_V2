package com.topwise.premierpay.trans.action.activity;

import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.topwise.manager.AppLog;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;

import com.topwise.premierpay.daoutils.DaoUtilsStore;

import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.EUIParamKeys;
import com.topwise.premierpay.trans.model.ResponseCode;
import com.topwise.premierpay.trans.model.TestParam;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.trans.model.TransStatusSum;
import com.topwise.premierpay.trans.receipt.PrintListenerImpl;
import com.topwise.premierpay.trans.receipt.ReceiptPrintTrans;
import com.topwise.premierpay.transmit.TransProcessListenerImpl;
import com.topwise.premierpay.transmit.iso8583.Transmit;
import com.topwise.premierpay.utils.SmallScreenUtil;
import com.topwise.premierpay.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConsumeSuccessActivity extends BaseActivityWithTickForAction implements View.OnClickListener {
    private static final String TAG = TopApplication.APPNANE + ConsumeSuccessActivity.class.getSimpleName();

    protected static final int TIP_PRINT = 0x01;
    protected static final int TIP_PRINT_END = 0x02;

    private TextView mConsumeAmt;

    private TextView tVtitle;
    private TextView tVtime;

    private TextView tVconsume_text;
    private String navTitle;
    private TransData transData;
    private ListView listView;
    private ImageView imageView;
    private String resCode;
    private Button rlExit;
    private RelativeLayout relativeLayout;
    List<String> result;

    private void closePrint(){
        if (receiptPrintTrans != null)
            receiptPrintTrans.close();

        if (listener != null) {
            listener.setRun(false);
            listener = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_exit_exit:
                closePrint();
                ActionResult result = new ActionResult(transData.isStressTest()?TransResult.CONTINUE:TransResult.ERR_ABORTED, null);
                finish(result);
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_consume_result;
    }

    @Override
    protected void initViews() {
        tVtitle = (TextView)findViewById(R.id.header_title);
        tVtitle.setText(navTitle);

        String temp = "";
        imageView = (ImageView)findViewById(R.id.iv_trans_states);
        tVtime = (TextView)findViewById(R.id.header_time);

        mConsumeAmt = (TextView) findViewById(R.id.consume_amount);
        relativeLayout = (RelativeLayout) findViewById(R.id.rl_amount);
        temp = transData.getAmount();
        String sYuan = Utils.ftoYuan(temp);
        if (!TextUtils.isEmpty(sYuan)) {
            mConsumeAmt.setText(TopApplication.sysParam.get(SysParam.APP_PARAM_TRANS_CURRENCY_SYMBOL) +sYuan);
        } else {
            relativeLayout.setVisibility(View.GONE);
        }
        result = new ArrayList();
        boolean isSuccess = false;

        listView = (ListView) findViewById(R.id.lv_trans);
        listView.setAdapter(new TransAdapter(this, init(transData)));
        tVconsume_text = (TextView) findViewById(R.id.consume_sucess_text);

        rlExit  = (Button) findViewById(R.id.btn_exit_exit);
        if (transData.isStressTest()) {
            rlExit.setEnabled(false);
            tickTimerStop();
        }
        ETransType transType = ETransType.valueOf(transData.getTransType());
        AppLog.e(TAG,"状态 ResponseCode " + resCode + " Transresult" + transData.getTransresult());

        if ("00".equals(resCode) && TransResult.SUCC == transData.getTransresult()) {
            imageView.setBackgroundResource(R.mipmap.app_success);
            isSuccess = true;
            boolean delete =  DaoUtilsStore.getInstance().getmDupTransDaoUtils().deleteAll();
            AppLog.e(TAG,"打印前先删除冲正文件 transData getmDupTransDaoUtils deleteAll " + delete);
            // 显示成功 就保存
            if (Component.checkSave(transData) && delete) {
                boolean save = DaoUtilsStore.getInstance().getmTransDaoUtils().save(transData);
                AppLog.e(TAG, "transData " + save);
                handler.sendEmptyMessageDelayed(TIP_PRINT,1000);
            }
            Device.openGreenLed();
            Device.beepSucc();
        } else {
            imageView.setBackgroundResource(R.mipmap.app_fail);
            tVconsume_text.setTextColor(getResources().getColor(R.color.red));
            isSuccess = false;

            String responseCode = transData.getResponseCode();
            if (!TextUtils.isEmpty(responseCode)) {
                ResponseCode resCode = TopApplication.rspCode.parse(responseCode);
                tVconsume_text.setText(responseCode + " \n "+resCode.getMessage());
            }  else {
                tVconsume_text.setText(getString(R.string.result_sucess_failure));
            }
            AppLog.e(TAG, "transData.getTransresult() " + transData.getTransresult());
//            if (transData.getTransresult() == TransResult.ERR_ABORTED) {
            toReversal();
//            }
            testExit();
            Device.openRedLed();
            Device.beepFail();
        }

        result.add(0,isSuccess?"Approved":"Failure");
        result.add(2,TopApplication.sysParam.get(SysParam.APP_PARAM_TRANS_CURRENCY_SYMBOL) +sYuan);
        SmallScreenUtil.getInstance().showResult(isSuccess, result);
    }

    private void testExit() {
        if (transData.isStressTest()) {
            rlExit.setEnabled(true);
            TestParam testParam = DaoUtilsStore.getInstance().getTestParam();
            final int intervalTime = testParam.getIntervalMode()==0?testParam.getIntervalTime(): getRandom();
            tickTimerStart(intervalTime);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(intervalTime*1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rlExit.performClick();
                        }
                    });
                }
            }).start();

        }
    }
    private void toReversal() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppLog.d(TAG, "ConsumeSuccessActivity toReversal");
                // 立即冲正，在这处理
                TransProcessListenerImpl listenerImpl = new TransProcessListenerImpl();
                int ret = Transmit.getInstance().sendReversal(listenerImpl);
                AppLog.d(TAG, "ConsumeSuccessActivity toReversal ret" + ret);
                listenerImpl.onHideProgress();
                Device.closeAllLed();
            }
        }).start();
    }

    private int getRandom() {
        Random random =  new Random(60);
        return random.nextInt();
    }

    @Override
    protected void setListeners() {
        rlExit.setOnClickListener(this);
    }

    @Override
    protected void loadParam() {
        Bundle bundle = getIntent().getExtras();
        navTitle = getIntent().getStringExtra(EUIParamKeys.NAV_TITLE.toString());
        transData = (TransData) bundle.getSerializable(EUIParamKeys.CONTENT.toString());
        resCode = transData.getResponseCode();
    }

    private PrintListenerImpl listener;
    private ReceiptPrintTrans  receiptPrintTrans;

    @Override
    protected void handleMsg(Message msg) {
        switch (msg.what){
            case TIP_TIME:
                String time = (String)msg.obj;
                if (!TextUtils.isEmpty(time))
                    tVtime.setText(time);
                break;
            case TIP_PRINT:
                if(!transData.isNeedPrint()) {
                    break;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        receiptPrintTrans = ReceiptPrintTrans.getInstance();
                        listener = new PrintListenerImpl( handler,transData.isStressTest());
                        receiptPrintTrans.print(transData, false, listener);
                    }
                }).start();
                break;
            case TIP_PRINT_END:
                testExit();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            closePrint();
            ActionResult result = new ActionResult(TransResult.ERR_ABORTED, null);
            finish(result);
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            closePrint();
            ActionResult result = new ActionResult(TransResult.ERR_ABORTED, null);
            finish(result);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private List<String []> init(TransData transData) {
        String temp = "";
        ETransType transType = ETransType.valueOf(transData.getTransType());
        List<String []> l = new ArrayList<String []>();

        //==type
        String [] t = new String[2];
        t[0] = getString(R.string.result_sucess_consume_type);
        t[1] = transType.getTransName().toUpperCase();
        result.add(t[1]);
        l.add(t);

        if (ETransType.TRANS_SALE_WITH_CASH == transType) {
            temp = transData.getCardAmount();
            if (!TextUtils.isEmpty(temp)) {
                t = new String[2];
                t[0] = "Card Amount:";
                temp = Utils.ftoYuan(temp);
                if (!TextUtils.isEmpty(temp)){
                    t[1] = temp;
                }else {
                    t[1] = "";
                }
                l.add(t);
            }
            temp = transData.getCashAmount();
            if (!TextUtils.isEmpty(temp)) {
                t = new String[2];
                t[0] = "Cash Amount:";
                temp = Utils.ftoYuan(temp);
                if (!TextUtils.isEmpty(temp)) {
                    t[1] = temp;
                } else {
                    t[1] = "";
                }
                l.add(t);
            }
        }

        //==cardnum
        t = new String[2];
        t[0] = getString(R.string.result_sucess_consume_cardnum);
        temp = transData.getPan();
        if (!TextUtils.isEmpty(temp)) {
            t[1] = Utils.maskedCardNo(temp);
        } else {
            t[1] = "";
        }
        result.add(t[1]);
        l.add(t);
        //== vouchernum
        t = new String[2];
        t[0] = getString(R.string.result_sucess_consume_vouchernum);
        temp = String.format("%06d",     transData.getTransNo());
        t[1] = temp;
        l.add(t);
        //== r
        t = new String[2];
        t[0] = getString(R.string.result_sucess_consume_referencenum);
        temp =  transData.getRefNo();
        if (!TextUtils.isEmpty(temp)) {
            t[1] = temp;
        } else {
            t[1] = "";
        }
        l.add(t);

        if (ETransType.BALANCE == transType){
            t = new String[2];
            t[0] = getString(R.string.title_balance);
            temp =  transData.getBalance();
            if (!TextUtils.isEmpty(temp)) {
                t[1] = "￥ " + Utils.ftoYuan(temp);
            }else {
                t[1] = "1000.00";   //jeremy for Pura test
            }
            result.add("Balance:"+t[1]);
            l.add(t);
        }
        t = new String[2];
        t[0] = getString(R.string.result_sucess_consume_time);
        temp = Utils.getTransDataTime(transData);
        if (!TextUtils.isEmpty(temp)) {
            t[1] = temp;
        } else {
            t[1] = "";
        }
        l.add(t);
        if (transData.isStressTest()) {
            TransStatusSum transStatusSum = Component.calNetStatus();
            t = new String[2];
            t[0] = getString(R.string.fail_to_total);
            t[1]  = (transStatusSum.getNetFailCount())+"/"+transStatusSum.getNetTotal();
            l.add(t);
        }
        return  l;
    }
}

