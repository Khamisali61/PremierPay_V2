package com.topwise.premierpay.mpesa;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.topwise.premierpay.R;

public class MpesaStkActivity extends Activity {

    private EditText etPhone, etAmount;
    private Button btnSend;
    private MpesaService mpesaService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpesa_stk);

        etPhone = findViewById(R.id.et_mpesa_phone);
        etAmount = findViewById(R.id.et_mpesa_amount);
        btnSend = findViewById(R.id.btn_send_stk);
        mpesaService = new MpesaService();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etPhone.getText().toString().trim();
                String amount = etAmount.getText().toString().trim();

                if (phone.isEmpty() || amount.isEmpty()) {
                    Toast.makeText(MpesaStkActivity.this, "Please enter Phone and Amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                btnSend.setEnabled(false);
                btnSend.setText("Sending...");

                mpesaService.initiateStkPush(phone, amount, new MpesaService.MpesaCallback() {
                    @Override
                    public void onSuccess(final String reqId, final String msg) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MpesaStkActivity.this, msg, Toast.LENGTH_LONG).show();
                                btnSend.setText("Send STK Push");
                                btnSend.setEnabled(true);
                            }
                        });
                    }

                    @Override
                    public void onError(final String error) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MpesaStkActivity.this, error, Toast.LENGTH_LONG).show();
                                btnSend.setText("Send STK Push");
                                btnSend.setEnabled(true);
                            }
                        });
                    }
                });
            }
        });
    }
}