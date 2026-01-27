package com.topwise.premierpay.transmit.json;

import android.text.TextUtils;

import com.topwise.manager.AppLog;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.model.TransResult;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpCommunicate extends AOkhttpCommunicate {
    private static final String TAG = OkHttpCommunicate.class.getSimpleName();
    protected String hostIp;
    protected String path;
    protected int hostPort;
    protected int connectTomeOut;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    @Override
    public int onInitPath() {
        connectTomeOut = getOutTime();
        path = getMainHostUrl();
        return TransResult.SUCC;
    }

    @Override
    public JsonResponse onSendAndRecv(final String data) {
        onShowMsg(TopApplication.mApp.getString(R.string.wait_recv),connectTomeOut);
        //=========
        AppLog.e(TAG,"url ==== " + path +"  "+ connectTomeOut);
        OkHttpClient client = new OkHttpClient.Builder()
                .writeTimeout(connectTomeOut, TimeUnit.SECONDS)
                .readTimeout(connectTomeOut, TimeUnit.SECONDS)
                .connectTimeout(connectTomeOut, TimeUnit.SECONDS).build();


        RequestBody body = RequestBody.create(JSON, data);
        final Request request = new Request.Builder()
                .url(path)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .addHeader("Accept", "application/json")
                .post(body)
                .build();
        //直接执行==
        Response response = null;
        try {
            response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                return new JsonResponse(TransResult.ERR_RECV,null);
            }
            return new JsonResponse(TransResult.SUCC, response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof SocketTimeoutException) {
                //造成的原因是服务器响应超时
                return new JsonResponse(TransResult.ERR_RECV, null);
            } else if (e instanceof ConnectException) {
                //造成的原因是服务器请求超时
                return new JsonResponse(TransResult.ERR_CONNECT, null);
            }
        } finally {
            if (response != null)
                response.body().close();
        }
        return new JsonResponse(TransResult.ERR_RECV, null);
    }

    @Override
    public void onSendAndRecv(String data, final IRecvListener onRecvCallback) {
        onShowMsg(TopApplication.mApp.getString(R.string.wait_recv),connectTomeOut);
        connectTomeOut = 1;
        //=========
        AppLog.e(TAG,"url ==== " + path +"  "+ connectTomeOut);
        OkHttpClient client = new OkHttpClient.Builder()
                .writeTimeout(connectTomeOut, TimeUnit.SECONDS)
                .readTimeout(connectTomeOut, TimeUnit.SECONDS)
                .connectTimeout(connectTomeOut, TimeUnit.SECONDS).build();

        RequestBody body = RequestBody.create(JSON,data);
        final Request request = new Request.Builder()
                .url(path )
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .addHeader("Accept", "application/json")
                .post(body)
                .build();
        //捕获异常
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    onRecvCallback.onRecvCallback(new JsonResponse(TransResult.ERR_RECV,null));
                }
                onRecvCallback.onRecvCallback(new JsonResponse(TransResult.SUCC, response.body().string()));
            }
        });

    }


    private String getPath(){
        hostIp = getMainHostIp() ;
        hostPort = getMainHostPort();

        return "https://" + hostIp +":" + hostPort+ "/io/v1.0/h2hpayments";
//        return "https://" + hostIp + "/io/v1.0/h2hpayments";
    }

    protected String getMainHostUrl() {
        SysParam sysParam = TopApplication.sysParam;
        String hostIp = sysParam.get(SysParam.PARAM_URL);
        return hostIp;//"europa-sandbox.perseuspay.com";
    }

    protected String getMainHostIp() {
        SysParam sysParam = TopApplication.sysParam;
        String hostIp = sysParam.get(SysParam.HOSTIP);
        return hostIp;//"europa-sandbox.perseuspay.com";
    }

    protected int getMainHostPort() {
        SysParam sysParam = TopApplication.sysParam;

        String port = sysParam.get(SysParam.HOSTPORT);
        if (TextUtils.isEmpty(port)) {
            port = "443";
        }
        return Integer.parseInt(port);
    }
    protected int getOutTime() {
        SysParam sysParam = TopApplication.sysParam;

        String time = sysParam.get(SysParam.COMM_TIMEOUT);
        if (TextUtils.isEmpty(time)) {
            time = "30";
        }
        return Integer.parseInt(time);
    }

}
