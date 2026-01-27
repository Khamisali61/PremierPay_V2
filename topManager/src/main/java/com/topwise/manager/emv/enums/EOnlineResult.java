package com.topwise.manager.emv.enums;

/**
 * 创建日期：2021/6/15 on 11:18
 * 描述:
 * 作者:wangweicheng
 */
public enum EOnlineResult {
    ONLINE_APPROVE((byte) 0), //Online Return code (Online Approved)
    ONLINE_FAILED((byte) 1),  //Online Return code (Online Failed)
    ONLINE_REFER((byte) 2),   //Online Return code (Online Reference)
    ONLINE_DENIAL((byte) 3),  //Online Return code (Online Denial)
    ONLINE_ABORT((byte) 4),  //Compatible PBOC(Transaction Terminate)
    NEED_INSERT((byte) 5),  //Response Code 65
    NEED_PWD((byte) 6);  //Response Code 65

    private byte onlineResult;

    EOnlineResult(byte onlineResult) {
        this.onlineResult = onlineResult;
    }

    public byte getOnlineResult() {
        return this.onlineResult;
    }

    public byte index() {
        return (byte)ordinal();
    }

    @Override
    public String toString() {
        return "EOnlineResult{" + "onlineResult=" + onlineResult + '}';
    }
}
