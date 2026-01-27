package com.topwise.premierpay.transmit.iso8583;

import com.topwise.manager.AppLog;
import com.topwise.toptool.api.comm.CommException;
import com.topwise.toptool.api.comm.ICommHelper;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.trans.model.TransResult;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TcpNoSslCommunicate extends ATcpCommunicate {
    private static final String TAG =  TopApplication.APPNANE +TcpNoSslCommunicate.class.getSimpleName();

    @Override
    public int onConnect() {
        AppLog.d(TAG, "onConnect ...");
        hostIp = getMainHostIp();
        hostPort = getMainHostPort();
        connectTomeOut = getOutTime();
        AppLog.d(TAG, "Send hostIp = " + hostIp);
        AppLog.d(TAG, "Send hostPort = " + hostPort);
        AppLog.d(TAG, "Send hostPort = " + connectTomeOut);
        onShowMsg(TopApplication.mApp.getString(R.string.wait_connect));
        return connectNoSLL(hostIp,hostPort,connectTomeOut*1000);
    }

    @Override
    public int onSend(byte[] sendPacket) {
        AppLog.d(TAG, "onSend ...");
        try {
            onShowMsg(TopApplication.mApp.getString(R.string.wait_send));
           client.send(sendPacket);
            return TransResult.SUCC;
        } catch (CommException e) {

            e.printStackTrace();
        }
        return TransResult.ERR_SEND;
    }


    @Override
    public CommResponse onRecv() {
        AppLog.d(TAG, "onRecv ...");
        onShowMsg(TopApplication.mApp.getString(R.string.wait_recv));
        try {
                byte[] lenBuf = client.recv(2);
                AppLog.d(TAG, "lenBuf" + TopApplication.convert.bcdToStr(lenBuf));
                if (lenBuf == null || lenBuf.length != 2) {
                    return new CommResponse(TransResult.ERR_RECV, null);
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = (((lenBuf[0] << 8) & 0xff00) | (lenBuf[1] & 0xff));
                byte[] rsp = client.recv(len);
                if (rsp == null || rsp.length != len) {
                    return new CommResponse(TransResult.ERR_RECV, null);
                }
                baos.write(rsp);
                rsp = baos.toByteArray();
                AppLog.d(TAG, "rsp" + TopApplication.convert.bcdToStr(rsp));
                return new CommResponse(TransResult.SUCC, rsp);

        } catch (IOException e) {

            e.printStackTrace();
        } catch (CommException e) {

            e.printStackTrace();
        }

        return new CommResponse(TransResult.ERR_RECV, null);
    }

    @Override
    public void onClose() {
        try {
            client.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int connectNoSLL(String hostIp, int port, int timeout) {
        if (hostIp == null || hostIp.length() == 0 || hostIp.equals("0.0.0.0")) {
            return TransResult.ERR_CONNECT;
        }

        ICommHelper commHelper = TopApplication.iTool.getCommHelper();
        client = commHelper.createTcpClient(hostIp, port);
        client.setConnectTimeout(timeout);
        client.setRecvTimeout(timeout);
        try {
            client.connect();
            AppLog.d(TAG, "connectNoSLL end: ");
            return TransResult.SUCC;
        } catch (CommException e) {

            e.printStackTrace();
        }
        return TransResult.ERR_CONNECT;
    }
}
