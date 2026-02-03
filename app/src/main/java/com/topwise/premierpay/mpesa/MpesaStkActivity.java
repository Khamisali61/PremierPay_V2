package com.topwise.premierpay.mpesa;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.topwise.premierpay.R;
import com.topwise.premierpay.trans.action.activity.ConsumeSuccessActivity;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.app.TopApplication;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class MpesaStkActivity extends Activity {

    private MpesaService mpesaService;
    private Handler handler = new Handler(Looper.getMainLooper());
    private String currentAmount = "0.00";
    private String currentPhone = "";
    private String checkoutRequestId = "";
    private String internalTransactionId = "";
    private boolean isPolling = false;
    private Runnable pollingRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra("amount")) {
            currentAmount = getIntent().getStringExtra("amount");
        }

        mpesaService = new MpesaService();
        showInputScreen();
    }

    @Override
    protected void onDestroy() {
        stopPolling();
        super.onDestroy();
    }

    private void showInputScreen() {
        setContentView(R.layout.activity_mpesa_stk);

        TextView tvTotalAmount = findViewById(R.id.tv_total_amount);
        // Only set if we passed an amount, otherwise allow edit
        if (!currentAmount.equals("0.00")) {
             tvTotalAmount.setText("KES " + formatAmount(currentAmount));
        } else {
             tvTotalAmount.setText("Enter Amount Below");
        }

        final EditText etPhone = findViewById(R.id.et_phone_number);
        final EditText etAmount = findViewById(R.id.et_amount); // New dedicated amount field

        // Prevent system keyboard
        etPhone.setShowSoftInputOnFocus(false);
        etAmount.setShowSoftInputOnFocus(false);

        // Pre-fill if we have it
        if (!currentAmount.equals("0.00")) {
            etAmount.setText(currentAmount);
            etAmount.setEnabled(false); // Lock if passed from cart
        }

        Button btnSend = findViewById(R.id.btn_send_stk);
        ImageButton btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etPhone.getText().toString().trim();
                String amountInput = etAmount.getText().toString().trim();

                if (phone.isEmpty()) {
                    Toast.makeText(MpesaStkActivity.this, "Please enter Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (amountInput.isEmpty()) {
                    Toast.makeText(MpesaStkActivity.this, "Please enter Amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (phone.length() < 9) {
                    Toast.makeText(MpesaStkActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Add country code if missing
                if (!phone.startsWith("254")) {
                    currentPhone = "254" + phone;
                } else {
                    currentPhone = phone;
                }

                currentAmount = amountInput;

                initiateTransaction(currentPhone, currentAmount);
            }
        });
    }

    private void initiateTransaction(final String phone, final String amount) {
        // Show immediate feedback
        final Button btnSend = findViewById(R.id.btn_send_stk);
        btnSend.setText("Sending...");
        btnSend.setEnabled(false);

        // Use SysParam.MERCH_ID or similar if available, else fallback logic in Service will handle it (or use placeholder)
        // Since we are inside the app, we should try to get the real merchant ID.
        // Assuming SysParam.MERCH_ID is available via a getter or public static.
        // Based on memory: SysParam.MERCH_ID is the constant.
        // We need to read it from SharedPreferences or SysParam instance.
        String merchantId = TopApplication.sysParam.get(SysParam.MERCH_ID);
        if (merchantId == null || merchantId.isEmpty()) {
            merchantId = "000000"; // Fallback/Demo
        }

        mpesaService.initiateStkPush(phone, amount, merchantId, new MpesaService.MpesaCallback() {
            @Override
            public void onSuccess(final String internalId, final String reqId, final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        internalTransactionId = internalId;
                        checkoutRequestId = reqId;
                        showProcessingScreen();
                    }
                });
            }

            @Override
            public void onError(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MpesaStkActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                        btnSend.setText("Send STK Push");
                        btnSend.setEnabled(true);
                    }
                });
            }
        });
    }

    private void showProcessingScreen() {
        setContentView(R.layout.activity_mpesa_processing);

        TextView tvPhone = findViewById(R.id.tv_customer_phone);
        TextView tvAmount = findViewById(R.id.tv_processing_amount);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        ImageButton btnBack = findViewById(R.id.btn_back);
        Button btnConfirm = findViewById(R.id.btn_confirm_completion);
        Button btnCheck = findViewById(R.id.btn_check_status);

        tvPhone.setText("+" + currentPhone);
        tvAmount.setText("KES " + formatAmount(currentAmount));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPolling();
                finish();
            }
        });

        // Manual check overrides polling
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MpesaStkActivity.this, "Checking status...", Toast.LENGTH_SHORT).show();
                checkStatusOnce();
            }
        });

        // Start Polling
        startPolling();
    }

    private void startPolling() {
        isPolling = true;
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isPolling) return;

                mpesaService.checkTransactionStatus(checkoutRequestId, new MpesaService.StatusCallback() {
                    @Override
                    public void onResult(final String status, final String resultCode, final String resultDesc) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if ("SUCCESS".equalsIgnoreCase(status)) {
                                    stopPolling();
                                    showResultScreen(true, resultDesc); // resultDesc might contain receipt number
                                } else if ("FAILED".equalsIgnoreCase(status) || "CANCELLED".equalsIgnoreCase(status)) {
                                    stopPolling();
                                    showResultScreen(false, resultDesc);
                                } else {
                                    // PENDING, continue polling
                                    if (isPolling) {
                                        handler.postDelayed(pollingRunnable, 3000); // 3 seconds
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        // Network error or 404, just retry
                         if (isPolling) {
                            handler.postDelayed(pollingRunnable, 3000);
                        }
                    }
                });
            }
        };
        handler.post(pollingRunnable);
    }

    private void checkStatusOnce() {
         mpesaService.checkTransactionStatus(checkoutRequestId, new MpesaService.StatusCallback() {
            @Override
            public void onResult(final String status, final String resultCode, final String resultDesc) {
                 runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         Toast.makeText(MpesaStkActivity.this, "Status: " + status, Toast.LENGTH_SHORT).show();
                    }
                 });
            }
            @Override
            public void onError(final String error) {
                 runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         Toast.makeText(MpesaStkActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                 });
            }
         });
    }

    private void stopPolling() {
        isPolling = false;
        if (pollingRunnable != null) {
            handler.removeCallbacks(pollingRunnable);
        }
    }

    private void showResultScreen(boolean success, final String message) {
        setContentView(R.layout.activity_mpesa_result);

        ImageView ivIcon = findViewById(R.id.iv_status_icon);
        TextView tvTitle = findViewById(R.id.tv_status_title);
        TextView tvDesc = findViewById(R.id.tv_status_desc);
        TextView tvHeaderAmount = findViewById(R.id.tv_header_amount);

        TextView tvTransId = findViewById(R.id.tv_trans_id);
        TextView tvPhone = findViewById(R.id.tv_phone_number);
        TextView tvAmountPaid = findViewById(R.id.tv_amount_paid);

        Button btnPrint = findViewById(R.id.btn_action_primary);
        Button btnHome = findViewById(R.id.btn_action_secondary);
        ImageButton btnClose = findViewById(R.id.btn_close);
        ImageButton btnBack = findViewById(R.id.btn_back);

        tvHeaderAmount.setText("KES " + formatAmount(currentAmount));
        tvPhone.setText(formatPhoneNumber(currentPhone));
        tvAmountPaid.setText("KES " + formatAmount(currentAmount));
        tvTransId.setText(checkoutRequestId); // Use Request ID as Ref

        if (success) {
            ivIcon.setImageResource(R.drawable.ic_check_circle_large);
            ivIcon.setColorFilter(getResources().getColor(R.color.brand_lime));
            tvTitle.setText("Payment Successful");
            tvDesc.setText("Your transaction has been processed.");

            // Auto Print
            // The message here acts as the Receipt Number based on backend logic
            printReceipt(message);
        } else {
            // Failure state
            ivIcon.setImageResource(R.drawable.ic_cancel_circle_large);
            ivIcon.setColorFilter(getResources().getColor(android.R.color.holo_red_dark));
            tvTitle.setText("Payment Failed");
            tvDesc.setText(message); // Show Safaricom reason
            tvTitle.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

            // Even if failed, we might want to print a declined receipt?
            // Requirement says: "Trigger ... as soon as the final status (SUCCESS or FAILED) is received."
             printReceipt(message);
        }

        View.OnClickListener goHome = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Return to main activity
            }
        };

        btnHome.setOnClickListener(goHome);
        btnClose.setOnClickListener(goHome);
        btnBack.setOnClickListener(goHome);

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printReceipt(message);
            }
        });
    }

    private void printReceipt(final String receiptNumberOrReason) {
         final boolean isSuccess = receiptNumberOrReason != null && !receiptNumberOrReason.startsWith("Request cancelled") && !receiptNumberOrReason.contains("insufficient");
         final String status = isSuccess ? "SUCCESS" : "FAILED";

         new Thread(new Runnable() {
             @Override
             public void run() {
                 MpesaReceiptGenerator generator = new MpesaReceiptGenerator(
                     MpesaStkActivity.this,
                     currentPhone,
                     currentAmount,
                     checkoutRequestId,
                     status,
                     receiptNumberOrReason
                 );
                 MpesaReceiptPrinter.getInstance().print(generator);
             }
         }).start();

         Toast.makeText(this, "Printing Receipt...", Toast.LENGTH_SHORT).show();
    }

    private String formatAmount(String amount) {
        try {
            double value = Double.parseDouble(amount);
            return String.format(Locale.US, "%,.2f", value);
        } catch (NumberFormatException e) {
            return amount;
        }
    }

    private String formatPhoneNumber(String phone) {
        if (phone != null && phone.length() >= 12) {
            return "+" + phone.substring(0, 3) + " " + phone.substring(3, 6) + " *** " + phone.substring(phone.length() - 3);
        }
        return phone;
    }
}