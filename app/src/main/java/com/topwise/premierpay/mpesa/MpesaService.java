package com.topwise.premierpay.mpesa;

import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MpesaService {

    private static final String TAG = "MpesaService";
    // Using the IP provided by user.
    // WARNING: Using 'https' with an IP often causes SSL hostname verification issues.
    // We will implement a trust-all mechanism for this specific IP/Port if needed,
    // or rely on the user having a valid cert.
    // For this implementation, I will add a TrustManager to ignore SSL errors for this specific task
    // as it's a common requirement for dev/IP-based servers.
    private static final String BASE_URL = "https://212.22.185.4:18425";

    public interface MpesaCallback {
        void onSuccess(String internalId, String checkoutRequestId, String message);
        void onError(String error);
    }

    public interface StatusCallback {
        void onResult(String status, String resultCode, String resultDesc);
        void onError(String error);
    }

    public MpesaService() {
    }

    public void initiateStkPush(final String phoneNumber, final String amount, final String merchantNumber, final MpesaCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(BASE_URL + "/api/mpesa/initiate");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    if (conn instanceof HttpsURLConnection) {
                        HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
                        httpsConn.setHostnameVerifier(DO_NOT_VERIFY);
                        httpsConn.setSSLSocketFactory(getUnsafeSslSocketFactory());
                    }

                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("APP-AUTH-KEY", MpesaConfig.APP_AUTH_KEY);
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(15000);
                    conn.setReadTimeout(15000);

                    JSONObject body = new JSONObject();
                    body.put("phoneNumber", phoneNumber);
                    body.put("amount", amount);
                    body.put("merchantNumber", merchantNumber);

                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(body.toString().getBytes(StandardCharsets.UTF_8));
                    }

                    int code = conn.getResponseCode();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(code >= 200 && code < 300 ? conn.getInputStream() : conn.getErrorStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) response.append(line);

                    if (code == 200) {
                        JSONObject json = new JSONObject(response.toString());
                        String internalId = json.optString("internalTransactionId");
                        String checkoutRequestId = json.optString("CheckoutRequestID");
                        String message = json.optString("message");
                        callback.onSuccess(internalId, checkoutRequestId, message);
                    } else {
                        JSONObject json = new JSONObject(response.toString());
                        String error = json.optString("error", "Unknown Error");
                        callback.onError(error);
                    }

                } catch (Exception e) {
                    Log.e(TAG, "STK Push Init Error", e);
                    callback.onError("Connection Error: " + e.toString());
                }
            }
        }).start();
    }

    public void checkTransactionStatus(final String checkoutRequestId, final StatusCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(BASE_URL + "/api/mpesa/status?checkoutRequestId=" + checkoutRequestId);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                     if (conn instanceof HttpsURLConnection) {
                        HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
                        httpsConn.setHostnameVerifier(DO_NOT_VERIFY);
                        httpsConn.setSSLSocketFactory(getUnsafeSslSocketFactory());
                    }

                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("APP-AUTH-KEY", MpesaConfig.APP_AUTH_KEY);
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);

                    int code = conn.getResponseCode();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(code == 200 ? conn.getInputStream() : conn.getErrorStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) response.append(line);

                    if (code == 200) {
                        JSONObject json = new JSONObject(response.toString());
                        callback.onResult(json.optString("status"), json.optString("resultCode"), json.optString("resultDesc"));
                    } else {
                        // 404 means not found or pending handling issue, but typically we just report error
                        callback.onError("Status Check Failed: " + code);
                    }

                } catch (Exception e) {
                    Log.e(TAG, "Status Check Error", e);
                    callback.onError("Connection Error: " + e.toString());
                }
            }
        }).start();
    }

    // SSL Bypass for IP-based HTTPS
    private final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    private javax.net.ssl.SSLSocketFactory getUnsafeSslSocketFactory() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[] {};
                }
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }
            } };

            SSLContext sc = SSLContext.getInstance("TLSv1.2");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            return sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}