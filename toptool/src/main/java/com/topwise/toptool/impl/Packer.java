package com.topwise.toptool.impl;

import android.content.Context;

import com.topwise.toptool.api.packer.IApdu;
import com.topwise.toptool.api.packer.IIso8583;
import com.topwise.toptool.api.packer.IPacker;
import com.topwise.toptool.api.packer.ITlv;


public class Packer implements IPacker {
    private static Packer instance;
    private Context context;

    private Packer() {
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public synchronized static Packer getInstance() {
        if (instance == null) {
            instance = new Packer();
        }

        return instance;
    }

    @Override
    public IApdu getApdu() {
        PackerApdu apdu = PackerApdu.getInstance();
        return apdu;
    }

    @Override
    public IIso8583 getIso8583() {
        PackerIso8583 iso8583 = PackerIso8583.getInstance();
        return iso8583;
    }

    @Override
    public ITlv getTlv() {
        PackerTlv tlv = PackerTlv.getInstance();
        return tlv;
    }
}
