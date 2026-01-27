package com.topwise.toptool.impl;

import com.topwise.toptool.api.comm.ICommHelper;
import com.topwise.toptool.api.comm.ICommSslClient;
import com.topwise.toptool.api.comm.ICommTcpClient;
import com.topwise.toptool.api.comm.ISslKeyStore;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class CommHelper implements ICommHelper {
    private static CommHelper instance;


    private CommHelper() {
    }


    public synchronized static CommHelper getInstance() {
        if (instance == null) {
            instance = new CommHelper();
        }

        return instance;
    }
    @Override
    public ISslKeyStore createSslKeyStore() {
        CommSslKeyStore ks = new CommSslKeyStore();
        return ks;
    }

    @Override
    public ICommSslClient createSslClient(String host, int port, ISslKeyStore keystore) {
        CommSslClient sslClient = new CommSslClient( host, port, keystore);
        return sslClient;
    }

    @Override
    public ICommTcpClient createTcpClient(String host, int port) {
        CommTcpClient tcpClient = new CommTcpClient(host, port);
        return tcpClient;
    }
    /**
     * ��IPv4��ַת��������
     *
     * @param ipv4
     *            ipv4��ַ�ַ���
     * @return ���Ϊ�Ϸ�ipv4��ַ����4�ֽ�����,���򷵻�null
     */
    private static byte[] ipv4String2ByteArray(String ipv4) {
        if (!Utils.getInstance().isIpv4(ipv4)) {
            return null;
        }
        byte[] ret = new byte[4];
        String[] strs = ipv4.split("\\.");
        for (int i = 0; i < strs.length; i++) {
            ret[i] = (byte) Integer.parseInt(strs[i]);
        }
        return ret;
    }
    /**
     * create socketAddress object, this method prevents unnecessary DNS.
     *
     * @param host
     *            host or IP
     * @param port
     *            port
     * @return
     * @throws UnknownHostException
     */
    static SocketAddress createSocketAddress(String host, int port) throws UnknownHostException {
        InetAddress netAddr = null;
        if (Utils.getInstance().isIpv4(host)) {
            // warning: do NOT use this, this will still cause DNS!!!
            // netAddr = InetAddress.getByAddress(CommonUtils.ipv4String2ByteArray(host));
            netAddr = InetAddress.getByAddress(host, CommHelper.ipv4String2ByteArray(host));
        } else {
            netAddr = InetAddress.getByName(host);
        }

        // WARNING: this will cause always DNS for all 'host', even if it's an IPv4 address!!!
        // SocketAddress svrAddr = new InetSocketAddress(host, port);
        SocketAddress svrAddr = new InetSocketAddress(netAddr, port);
        return svrAddr;
    }
}
