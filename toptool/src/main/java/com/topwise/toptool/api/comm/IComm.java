package com.topwise.toptool.api.comm;

public interface IComm {
    int getConnectTimeout();

    void setConnectTimeout(int paramInt);

    int getSendTimeout();

    void setSendTimeout(int paramInt);

    int getRecvTimeout();

    void setRecvTimeout(int paramInt);

    void connect() throws CommException;

    EConnectStatus getConnectStatus();

    void disconnect() throws CommException;

    void send(byte[] paramArrayOfbyte) throws CommException;

    byte[] recv(int paramInt) throws CommException;

    byte[] recvNonBlocking() throws CommException;

    void reset();

    void cancelRecv();

    public enum EConnectStatus {
        DISCONNECTED, CONNECTING, CONNECTED;
    }
}
