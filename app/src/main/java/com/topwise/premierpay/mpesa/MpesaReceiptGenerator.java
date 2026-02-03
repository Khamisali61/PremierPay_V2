package com.topwise.premierpay.mpesa;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.topwise.premierpay.trans.receipt.AReceiptPrint;
import com.topwise.premierpay.trans.receipt.IReceiptGenerator;
import com.topwise.premierpay.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MpesaReceiptGenerator implements IReceiptGenerator {

    private String phoneNumber;

    @Override
    public String generateStr() {
        return ""; // Not used for bitmap generation
    }
    private String amount;
    private String transactionId;
    private String status; // SUCCESS or FAILED
    private String message; // Receipt Number or Failure Reason
    private Context context;

    public MpesaReceiptGenerator(Context context, String phoneNumber, String amount, String transactionId, String status, String message) {
        this.context = context;
        this.phoneNumber = phoneNumber;
        this.amount = amount;
        this.transactionId = transactionId;
        this.status = status;
        this.message = message;
    }

    @Override
    public Bitmap generateBitmap() {
        return generateBinmap();
    }

    @Override
    public Bitmap generateBinmap() {
        int width = 384;
        // Estimate height based on content
        int height = 600;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        TextPaint paint = new TextPaint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(24);
        paint.setAntiAlias(true);

        TextPaint titlePaint = new TextPaint(paint);
        titlePaint.setTextSize(30);
        titlePaint.setTypeface(Typeface.DEFAULT_BOLD);
        titlePaint.setTextAlign(Paint.Align.CENTER);

        int y = 40;

        // Title
        canvas.drawText("M-PESA RECEIPT", width / 2, y, titlePaint);
        y += 40;

        // Merchant Name
        canvas.drawText("PremierPay POS", width / 2, y, paint);
        y += 40;

        // Date
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Date: " + date, 20, y, paint);
        y += 30;

        canvas.drawLine(10, y, width - 10, y, paint);
        y += 30;

        // Status
        if ("SUCCESS".equalsIgnoreCase(status)) {
             paint.setTextSize(28);
             paint.setTypeface(Typeface.DEFAULT_BOLD);
             canvas.drawText("APPROVED", 20, y, paint);
             paint.setTextSize(24);
             paint.setTypeface(Typeface.DEFAULT);
             y += 40;
             canvas.drawText("Receipt No: " + message, 20, y, paint);
        } else {
             paint.setTextSize(28);
             paint.setTypeface(Typeface.DEFAULT_BOLD);
             canvas.drawText("DECLINED", 20, y, paint);
             paint.setTextSize(24);
             paint.setTypeface(Typeface.DEFAULT);
             y += 40;
             canvas.drawText("Reason: " + message, 20, y, paint);
        }
        y += 40;

        // Details
        canvas.drawText("Phone: " + phoneNumber, 20, y, paint);
        y += 30;
        canvas.drawText("Trans ID: " + transactionId, 20, y, paint);
        y += 30;

        paint.setTextSize(32);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        y += 20;
        canvas.drawText("Amount: KES " + amount, 20, y, paint);

        y += 60;
        paint.setTextSize(20);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("*** CUSTOMER COPY ***", width / 2, y, paint);

        return bitmap;
    }
}