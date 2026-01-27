package com.topwise.premierpay.utils;

import android.os.CountDownTimer;

public class TickTimer extends CountDownTimer {

    public interface TickTimerListener {
        void onFinish();

        void onTick(long leftTime);
    }

    private TickTimerListener listener;

    public void setTimeCountListener(TickTimerListener listener) {
        this.listener = listener;
    }

    public TickTimer(long timeout, long tickInterval) {
        super(timeout * 1000, tickInterval * 1000);
    }

    @Override
    public void onFinish() {
        if (listener != null)
            listener.onFinish();
    }

    @Override
    public void onTick(long millisUntilFinished) {
        if (listener != null)
            listener.onTick(millisUntilFinished / 1000);
    }
}