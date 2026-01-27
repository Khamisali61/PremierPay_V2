package com.topwise.manager.emv.enums;

public enum ECVMStatus {
    ENTER_OK("OK"),
    ENTER_BYPASS("BYPASS"),
    ENTER_CANCEL("CANCEL"),
    ENTER_TIME_OUT("TIME OUT"),
    ENTER_RFU("RFU"),
    ;

    private String msg;

    ECVMStatus(String msg) {
        this.msg = msg;
    }

    public byte index() {
        return (byte)ordinal();
    }

    @Override
    public String toString() {
        return "ECVMStatus{" + "msg='" + msg + '}';
    }
}
