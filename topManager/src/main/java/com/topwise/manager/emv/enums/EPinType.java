package com.topwise.manager.emv.enums;

public enum EPinType {
    ONLINE_PIN_REQ((byte)0x00),
    OFFLINE_PLAIN_TEXT_PIN_REQ((byte)0x01),
    PCI_MODE_REQ((byte)0x02),
    ;


    private byte type;

    EPinType(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "EPinType{" + "type=" + type + '}';
    }
}
