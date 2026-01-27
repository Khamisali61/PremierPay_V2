package com.topwise.toptool.impl;

import android.content.Context;

import com.topwise.toptool.api.packer.IIso8583;
import com.topwise.toptool.api.packer.Iso8583Exception;

import java.util.HashMap;
import java.util.List;

public class PackerIso8583 implements IIso8583 {
    private static PackerIso8583 instance;

    private PackerIso8583() {
    }

    public synchronized static PackerIso8583 getInstance() {
        if (instance == null) {
            instance = new PackerIso8583();
        }

        return instance;
    }

    @Override
    public IIso8583Entity getEntity() {
        return PackerIso8583Entity.getInstance();
    }

    @Override
    public byte[] pack() throws Iso8583Exception {
        return PackerIso8583Entity.getInstance().pack();
    }

    @Override
    public byte[] pack(List<String> fieldIds) throws Iso8583Exception {
        return PackerIso8583Entity.getInstance().pack(fieldIds);
    }

    @Override
    public HashMap<String, byte[]> unpack(byte[] message, boolean isWithHeader) throws Iso8583Exception {
        return PackerIso8583Entity.getInstance().unpack(message, isWithHeader);
    }

}
