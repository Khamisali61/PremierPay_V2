package com.topwise.premierpay.transmit;

import android.graphics.Bitmap;

import com.topwise.premierpay.trans.model.TransData;

public interface TransProcessListener {
    public void onShowProgress(String message, int timeout);

    public void onUpdateProgressTitle(String title);
    public void onUpdateMsg(String tip);
    public void onHideProgress();

    public int onShowMessageWithConfirm(String message, int timeout);

    public int onInputOnlinePin(TransData transData);

    public byte[] onCalcMac(byte[] data);

    public byte[] onEncTrack(byte[] track);

    public int onShowErrorBitmap(Bitmap bitmap, String message, int timeout);

    public int onShowSuccessMsgWithConfirm(String message, int timeout);

    public int onShowFailWithConfirm(String message, int timeout);
}
