package com.topwise.premierpay.tms;

import android.os.RemoteException;

import com.google.gson.Gson;
import com.topwise.cloudpos.aidl.tm.AidlTM;
import com.topwise.cloudpos.aidl.tm.AidlTMListener;
import com.topwise.manager.AppLog;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.param.AidParam;
import com.topwise.premierpay.param.AppCombinationHelper;
import com.topwise.premierpay.param.CapkParam;
import com.topwise.premierpay.param.LoadParam;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.tms.bean.ParameterBean;
import com.topwise.premierpay.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.CountDownLatch;

/**
 * 创建日期：2021/5/11 on 10:07
 * 描述:
 * 作者:wangweicheng
 */
public class TmsParamDownload {
    public static final String LOGO_PATH = TopApplication.mApp.getFilesDir() + File.separator + "logo.png";
    private final String TAG = "TmsParamDownload";
    private AidlTM aidltm;
    private String param;

    public String downloadParam() {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        aidltm = TopApplication.usdkManage.getTmsManager();
        try {
            aidltm.getTMSParameter(new AidlTMListener.Stub() {
                @Override
                public void onResult(String s) throws RemoteException {
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        param = jsonObject.getString("terminal_param");
                        AppLog.v(TAG, "onResult: " + param);
                        TopApplication.sysParam.set(SysParam.TMS_PARAM_STR, param);
                        Gson gson = new Gson();
                        TopApplication.parameterBean = gson.fromJson(param, ParameterBean.class);

                        Utils.loadAidCapk();
                        Utils.loadTmsOtherParams();
                        countDownLatch.countDown();
                    } catch (JSONException e) {
                        AppLog.v(TAG, "onResult: " + e);
                    }
                }

                @Override
                public void onError(String s) throws RemoteException {
                    AppLog.v(TAG, "onError: " + s);
                    countDownLatch.countDown();
                }

                @Override
                public void onTimeOut() throws RemoteException {
                    AppLog.v(TAG, "onTimeOut");
                    countDownLatch.countDown();
                }
            }, 10000);
        } catch (RemoteException e) {

        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {

        }
        return param;
    }

}
