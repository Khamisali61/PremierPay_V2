package com.topwise.premierpay.mpesa;

import android.util.Log;

public class MpesaCallbackHandler {
    private static final String TAG = "MpesaCallback";

    public static void logTransaction(String requestId, String phone, String amount, String status) {
        // Isolated logging logic - does not touch main DB
        Log.i(TAG, "TRANSACTION LOGged: [ReqID: " + requestId + "] [Phone: " + phone + "] [Amt: " + amount + "] [Status: " + status + "]");

        // Future: Insert into isolated 'mpesa_transactions' table here
    }
}