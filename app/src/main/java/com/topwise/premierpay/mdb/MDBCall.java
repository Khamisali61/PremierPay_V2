package com.topwise.premierpay.mdb;

import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.topwise.cloudpos.aidl.pinpad.AidlPinpad;
import com.topwise.cloudpos.data.SerialportConstant;
import com.topwise.manager.AppLog;
import com.topwise.manager.TopUsdkManage;
import com.topwise.premierpay.BuildConfig;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.mdb.mode.MDBCallTrans;
import com.topwise.premierpay.trans.BaseTrans;
import com.topwise.premierpay.trans.TransSale;
import com.topwise.premierpay.trans.TransVoid;
import com.topwise.premierpay.trans.core.ATransaction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.core.TransContext;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.trans.model.ResponseCode;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransResult;

import org.json.JSONException;
import org.json.JSONObject;

public class MDBCall {
    private String TAG ="ThirdCall";

    /***输入**/
    public static final String INTENT_TAG_TRANSID = "transId";  //Sale ：card sale   Revoke :card revoke
    public static final String INTENT_TAG_APPNAME = "appName";  //BankApp
    public static final String INTENT_TAG_TRANSDATA = "transData";

    /***输出**/
    public static String RESULT_CODE_TAG = "resultCode";
    public static final String RESULT_MSG_TAG = "resultMsg";
    public static final String TRANS_DATA_TAG = "transData";

    public MDBCallTrans mdbCallTrans ;
    private Activity mContext;
    private TransData transData;
    private BaseTrans trans = null;
    private Handler mHandler;
    public MDBCall(Activity context, Handler handler) {
        this.mContext = context;
        this.mHandler  = handler;
    }

    public void doTrans(String s) {

        Gson jsonGson = new GsonBuilder().create();
        mdbCallTrans = jsonGson.fromJson(s, MDBCallTrans.class);
        AppLog.d(TAG," mdbCallTrans "+mdbCallTrans.toString());
        if (mdbCallTrans == null /* ||TextUtils.isEmpty(thirdCallTrans.getAmt())*/) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                switch (mdbCallTrans.getTransType()) {
                    case MDBCallTrans.TRANS_TYPE_SALE:
                        trans = new TransSale(mContext, mHandler, transEndListener, mdbCallTrans);
                        break;
                    case MDBCallTrans.TRANS_TYPE_REVOKE:
                        trans = new TransVoid(mContext, mHandler, transEndListener, mdbCallTrans);
                        break;
                    case MDBCallTrans.TRANS_TYPE_CANCEL:
                        closePinpad();
                        TransContext.close();
                        Device.closeAllLed();
                        BaseTrans.setTransRunning(false);
                        TransContext.getInstance().setCurrentContext(null);
                        ActivityStack.getInstance().popMDB();
                        transEndListener.onEnd(new ActionResult(-1,""));
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

    private void closePinpad() {
        AidlPinpad mPinpad = TopApplication.usdkManage.getPinpad(0);
        if (mPinpad != null) {
            if (BuildConfig.POS_MODE ==1) {
                try {
                    mPinpad.stopGetPin();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                AppLog.v(TAG, "closePinpad stopGetPin");
                SystemClock.sleep(30);
            }
        }
    }

    private ATransaction.TransEndListener transEndListener = new ATransaction.TransEndListener() {
        @Override
        public void onEnd(ActionResult result) {
            AppLog.d(TAG,"TransEndListener end   ");
            if (trans == null) {
                sendResult(-1, "");
                return;
            }
            transData = trans.getTransData();
            AppLog.d(TAG,"transData.getResponseCode()  "+transData.getResponseCode());
            AppLog.d(TAG,"transData.getTransresult()  "+transData.getTransresult());
            if (transData == null) {
                sendResult(-2, "");
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


        MDBCallTrans mdbCallTrans = new MDBCallTrans();
        if (resultCode==0) {
            mdbCallTrans.setResultCode(MDBCallTrans.RESULT_SUCCESS_CODE);
        }else{
            mdbCallTrans.setResultCode(MDBCallTrans.RESULT_FAIL_CODE);
        }
        // 将对象转换成 JSON 字符串
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("resultCode", mdbCallTrans.getResultCode());
            String transData = jsonObject.toString();
            TopApplication.usdkManage.getMDBSerial(SerialportConstant.PORT_ONE).sendTransResult(transData);
        } catch (JSONException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
