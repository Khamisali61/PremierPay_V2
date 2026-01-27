package com.topwise.premierpay.thirdcall;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.topwise.manager.AppLog;
import com.topwise.manager.TopUsdkManage;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.trans.BaseTrans;
import com.topwise.premierpay.trans.TransSale;
import com.topwise.premierpay.trans.TransVoid;
import com.topwise.premierpay.trans.core.ATransaction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.ResponseCode;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransResult;

public class ThirdCall {
    private String TAG ="ThirdCall";

    /***输入**/
    public static final String INTENT_TAG_TRANSID = "transId";  //Sale ：card sale   Revoke :card revoke
    public static final String INTENT_TAG_APPNAME = "appName";  //BankApp
    public static final String INTENT_TAG_TRANSDATA = "transData";

    /***输出**/
    public static String RESULT_CODE_TAG = "resultCode";
    public static final String RESULT_MSG_TAG = "resultMsg";
    public static final String TRANS_DATA_TAG = "transData";

    public ThirdCallTrans thirdCallTrans ;
    private Activity mContext;
    private Intent mIntent ;
    private Handler mHandler;
    private TransData transData;
    private BaseTrans trans = null;

    public ThirdCall(Activity context, Intent intent, Handler handler) {
        this.mContext = context;
        this.mIntent  = intent;
        this.mHandler  = handler;
    }

    public void doTrans() {
        String transId = mIntent.getStringExtra(INTENT_TAG_TRANSID);
        String callTransData = mIntent.getStringExtra(INTENT_TAG_TRANSDATA);
        AppLog.d(TAG," callTransData " + callTransData);
        AppLog.d(TAG," transId " + transId);

        if (TextUtils.isEmpty(callTransData)) {
           return;
        }

        Gson jsonGson = new GsonBuilder().create();
        thirdCallTrans = jsonGson.fromJson(callTransData, ThirdCallTrans.class);
        AppLog.d(TAG," thidCallTrans "+thirdCallTrans.toString());
        if (thirdCallTrans == null /* ||TextUtils.isEmpty(thirdCallTrans.getAmt())*/) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                switch (transId) {
                    case "Sale":
                        trans = new TransSale(mContext, mHandler, transEndListener, thirdCallTrans);
                        break;
                    case "Revoke":
                        trans = new TransVoid(mContext, mHandler, transEndListener, thirdCallTrans);
                        break;
                    case "Main_menu":
                        return;
                    default:
                        sendResult(-1,"No Support Transaction!");
                        return;
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    SystemClock.sleep(200);
                }
                try {
                    TopUsdkManage.getInstance().getBuzzer().beep(1,300);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                trans.execute();
            }
        }).start();
    }

    private ATransaction.TransEndListener transEndListener = new ATransaction.TransEndListener() {
        @Override
        public void onEnd(ActionResult result) {
            AppLog.d(TAG,"TransEndListener end   ");
            transData = trans.getTransData();
            AppLog.d(TAG,"transData.getResponseCode()  "+transData.getResponseCode());
            AppLog.d(TAG,"transData.getTransresult()  "+transData.getTransresult());
            if (transData == null) {
                sendResult(-1, "");
            } else if ("00".equals(transData.getResponseCode())) {
                sendResult(0, "");
            } else {
                String error ="";
                if (!TextUtils.isEmpty(transData.getResponseCode())) {
                    ResponseCode resCode = TopApplication.rspCode.parse(transData.getResponseCode());
                    error = resCode.getMessage();
                }
                if (TextUtils.isEmpty(error)) {
                    error = TransResult.getMessage(mContext, result.getRet());
                }
                sendResult(-1, error);
            }
        }
    };

    protected void sendResult(int resultCode, String errorTip) {
        AppLog.d(TAG,"resultCode    " + resultCode);

        Intent intent = new Intent();
        intent.putExtra(RESULT_CODE_TAG, resultCode + "");
        if (!TextUtils.isEmpty(errorTip)) {
            intent.putExtra(RESULT_MSG_TAG, errorTip);
        }
        if (resultCode == 0) {
            Gson jsonGson = new GsonBuilder().create();
            String jsonStr = jsonGson.toJson(transData);
            AppLog.d(TAG,"jsonStr    " + jsonStr);
            intent.putExtra(TRANS_DATA_TAG, jsonStr);
        }
        mContext.setResult(resultCode, intent);
        mContext.finish();
        System.exit(0);
    }
}
