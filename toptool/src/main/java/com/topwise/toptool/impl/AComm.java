package com.topwise.toptool.impl;

import com.topwise.toptool.api.comm.CommException;
import com.topwise.toptool.api.comm.IComm;

abstract class AComm implements IComm {
    int connectTimeout = 20000;
    int sendTimeout = 20000;
    int recvTimeout = 20000;

    @Override
    public int getConnectTimeout() {
        return connectTimeout;
    }

    @Override
    public void setConnectTimeout(int timeoutMs) {
        connectTimeout = timeoutMs;
    }

    @Override
    public int getSendTimeout() {
        return sendTimeout;
    }

    @Override
    public void setSendTimeout(int timeoutMs) {
        sendTimeout = timeoutMs;
    }

    @Override
    public int getRecvTimeout() {
        return recvTimeout;
    }

    @Override
    public void setRecvTimeout(int timeoutMs) {
        recvTimeout = timeoutMs;
    }

    @Override
    public abstract void connect() throws CommException;

    @Override
    public abstract EConnectStatus getConnectStatus();

    @Override
    public abstract void disconnect() throws CommException;

    @Override
    public abstract void send(byte[] data) throws CommException;

    @Override
    public abstract byte[] recv(int expLen) throws CommException;

    @Override
    public abstract byte[] recvNonBlocking() throws CommException;

    @Override
    public abstract void reset();

    @Override
    public abstract void cancelRecv();
}
