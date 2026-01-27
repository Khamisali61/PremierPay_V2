package com.topwise.premierpay.utils;

import com.topwise.manager.AppLog;
import com.topwise.premierpay.trans.core.ActionResult;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkhttpUtils {
    private static final String TAG = OkhttpUtils.class.getSimpleName();
    public final static int CONNECT_TIMEOUT = 30;
    public final static int READ_TIMEOUT = 100;
    public final static int WRITE_TIMEOUT = 30;
    private static OkhttpUtils okhttp = null;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void close() {
        if (okhttp != null) {
            okhttp = null;
        }
    }

    public static OkhttpUtils getInstance() {
        if (okhttp == null) {
            okhttp = new OkhttpUtils();
        }
        return okhttp;
    }

    public ActionResult synOkhttp(String path, String post_s) {
        AppLog.e("wwc","path " + path);

        OkHttpClient client = new OkHttpClient.Builder()
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS).build();

        RequestBody body = RequestBody.create(JSON,post_s);
        final Request request = new Request.Builder()
                .url(path)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .addHeader("Accept", "application/json")
                .post(body)
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return new ActionResult(response.code(),"返回码 " + response.code());
            }
            AppLog.e("222"," synOkhttp" + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ActionResult(-1,  null);
    }
}
