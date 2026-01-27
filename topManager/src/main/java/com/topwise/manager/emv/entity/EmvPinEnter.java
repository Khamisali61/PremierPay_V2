package com.topwise.manager.emv.entity;

import com.topwise.manager.emv.enums.ECVMStatus;

public class EmvPinEnter {
    private ECVMStatus ecvmStatus; // CVM status
    private String PlainTextPin; // Offline plain PIN

    public ECVMStatus getEcvmStatus() {
        return ecvmStatus;
    }

    public void setEcvmStatus(ECVMStatus ecvmStatus) {
        this.ecvmStatus = ecvmStatus;
    }

    public String getPlainTextPin() {
        return PlainTextPin;
    }

    public void setPlainTextPin(String plainTextPin) {
        PlainTextPin = plainTextPin;
    }

    @Override
    public String toString() {
        return "PinEnter{" + "ecvmStatus=" + ecvmStatus + '}';
    }
}
