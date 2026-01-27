package com.topwise.manager.emv.enums;

public enum ETransStatus {
    OFFLINE_APPROVE(0x01),
    ONLINE_APPROVE(0x02),
    OFFLINE_DECLINED(0x03),
    ONLINE_DECLINED(0x04),
    ONLINE_REQUEST(0x05),
    END_APPLICATION(0x06),
    SELECT_NEXT_AID(0x07),
    TRY_ANOTHER_CARD(0x08),
    TRY_ANOTHER_INTERFACE(0x09),
    TRY_AGAIN(0x0A),
    SEE_PHONE_TRY_AGAIN(0x0B),
    NEED_INSERT(0x0C),
    NEED_PWD(0x0D),
    NA(0xFF),
    ;


    private int transStatus;

    ETransStatus(int transStatus) {
        this.transStatus = transStatus;
    }

    public int getTransStatus() {
        return transStatus;
    }

    @Override
    public String toString() {
        return "ETransStatus{" + "nStatus=" + transStatus + '}';
    }

    public byte index() {
        return (byte)ordinal();
    }
}
