package com.topwise.toptool.impl;

import android.annotation.SuppressLint;
import android.content.Context;

import com.topwise.toptool.api.utils.IUtils;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils implements IUtils {
    private static Utils instance;
    private Context context;

    private Utils() {
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public synchronized static Utils getInstance() {
        if (instance == null) {
            instance = new Utils();
        }

        return instance;
    }

    @Override
    public boolean isByteArrayValueSame(byte[] a, int aOffset, byte[] b, int bOffset, int len) {
        if (a == null || b == null) {
            return false;
        }

        if ((aOffset + len) > a.length || (bOffset + len) > b.length) {
            return false;
        }

        for (int i = 0; i < len; i++) {
            if (a[aOffset + i] != b[bOffset + i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * file sytem sync
     */
    @Override
    public void fileSystemSync() {
        try {
            Runtime.getRuntime().exec("sync");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public boolean isDateValid(String date) {
        boolean ret = true;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            // strict mode
            dateFormat.setLenient(false);
            dateFormat.parse(date);
        } catch (Exception e) {
            ret = false;
        }
        return ret;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public boolean isTimeValid(String time) {
        // TODO Auto-generated method stub
        boolean ret = true;
        SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss");
        try {
            // strict mode
            dateFormat.setLenient(false);
            dateFormat.parse(time);
        } catch (Exception e) {
            ret = false;
        }
        return ret;
    }

    @Override
    public boolean isIpv4(String ip) {
        if (ip == null) {
            return false;
        }

        String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

    @Override
    public String formatIpAddress(String ip) {
        if (ip == null) {
            return null;
        }

        String temp = ip.replaceAll("0*(\\d+)", "$1");
        if (!isIpv4(temp)) {
            return ip;
        }

        return temp;
    }

    @Override
    public IRingBuffer createRingBuffer(int size) {
        UtilsRingBuffer rb = new UtilsRingBuffer(size);
        rb.setContext(context);
        return rb;
    }
}
