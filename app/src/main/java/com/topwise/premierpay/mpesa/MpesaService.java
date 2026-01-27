package com.topwise.premierpay.mpesa;

import android.util.Base64;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MpesaService {

    private static final String TAG = "MpesaService";

    public interface MpesaCallback {
        void onSuccess(String checkoutRequestId, String message);
        void onError(String error);
    }

    public void initiateStkPush(final String phoneNumber, final String amount, final MpesaCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String token = authenticate();
                    if (token == null) {
                        callback.onError("Authentication failed");
                        return;
                    }
                    performStkPush(token, phoneNumber, amount, callback);
                } catch (Exception e) {
                    Log.e(TAG, "STK Push Error", e);
                    callback.onError("System Error: " + e.getMessage());
                }
            }
        }).start();
    }

    private String authenticate() {
        try {
            URL url = new URL(MpesaConfig.getBaseUrl() + "/oauth/v1/generate?grant_type=client_credentials");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            String keys = MpesaConfig.CONSUMER_KEY + ":" + MpesaConfig.CONSUMER_SECRET;
            String auth = "Basic " + Base64.encodeToString(keys.getBytes(), Base64.NO_WRAP);
            conn.setRequestProperty("Authorization", auth);

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) response.append(line);
                JSONObject json = new JSONObject(response.toString());
                return json.getString("access_token");
            }
        } catch (Exception e) {
            Log.e(TAG, "Auth Error", e);
        }
        return null;
    }

    private void performStkPush(String token, String phone, String amount, MpesaCallback callback) {
        try {
            URL url = new URL(MpesaConfig.getBaseUrl() + "/mpesa/stkpush/v1/processrequest");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
            String password = Base64.encodeToString((MpesaConfig.BUSINESS_SHORT_CODE + MpesaConfig.PASSKEY + timestamp).getBytes(), Base64.NO_WRAP);

            JSONObject body = new JSONObject();
            body.put("BusinessShortCode", MpesaConfig.BUSINESS_SHORT_CODE);
            body.put("Password", password);
            body.put("Timestamp", timestamp);
            body.put("TransactionType", "CustomerPayBillOnline");
            body.put("Amount", amount);
            body.put("PartyA", phone);
            body.put("PartyB", MpesaConfig.BUSINESS_SHORT_CODE);
            body.put("PhoneNumber", phone);
            body.put("CallBackURL", "https://webhook.site/093ee290-8356-4dc7-a188-795cffdabfb5"); // Replace with valid URL
            body.put("AccountReference", "PremierPay");
            body.put("TransactionDesc", "Payment");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.toString().getBytes(StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();
            BufferedReader reader = new BufferedReader(new InputStreamReader(code == 200 ? conn.getInputStream() : conn.getErrorStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) response.append(line);

            if (code == 200) {
                JSONObject json = new JSONObject(response.toString());
                // Log via dedicated handler
                MpesaCallbackHandler.logTransaction(json.optString("CheckoutRequestID"), phone, amount, "PENDING");
                callback.onSuccess(json.optString("CheckoutRequestID"), "STK Push Sent. Response: " + json.optString("ResponseDescription"));
            } else {
                callback.onError("Request Failed: " + response.toString());
            }

        } catch (Exception e) {
            callback.onError("Connection Error: " + e.getMessage());
        }
    }
}