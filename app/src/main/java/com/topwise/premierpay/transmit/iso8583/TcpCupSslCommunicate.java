package com.topwise.premierpay.transmit.iso8583;

import com.topwise.toptool.api.comm.CommException;
import com.topwise.toptool.api.comm.ICommHelper;
import com.topwise.toptool.api.comm.ISslKeyStore;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.trans.model.TransResult;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TcpCupSslCommunicate extends ATcpCommunicate {
    private static final String TAG = TcpCupSslCommunicate.class.getSimpleName();

    private InputStream keyStoreStream;

    public TcpCupSslCommunicate(InputStream keyStoreStream) {
        this.keyStoreStream = keyStoreStream;
    }

    @Override
    public int onConnect() {
        int ret = setTcpCommParam();
        if (ret != TransResult.SUCC) {
            return ret;
        }

        int timeout = 6 * 1000;
        // 启用主通讯地址
        ret = TransResult.ERR_CONNECT;

        hostIp = getMainHostIp();
        hostPort = getMainHostPort();
        connectTomeOut = getOutTime();
        onShowMsg(TopApplication.mApp.getString(R.string.wait_connect));
        ret = connectCupSLL(hostIp, hostPort, timeout*1000);
        if (ret != TransResult.ERR_CONNECT) {
            return ret;
        }

        return ret;
    }

    @Override
    public int onSend(byte[] data) {
        try {
            onShowMsg(TopApplication.mApp.getString(R.string.wait_send));
            client.send(getCupSslPackage(data));
            return TransResult.SUCC;
        } catch (CommException e) {

            e.printStackTrace();
        }
        return TransResult.ERR_SEND;
    }

    @Override
    public CommResponse onRecv() {
        onShowMsg(TopApplication.mApp.getString(R.string.wait_recv));
        String sslType = "SSL";
        if (sslType.equals("SSL")) {
            byte[] data = new byte[0];
            while (true) {
                byte[] temp = null;
                try {
                    temp = client.recv(1);
                } catch (CommException e) {
                    e.printStackTrace();
                    return new CommResponse(TransResult.ERR_RECV, null);
                }
                if (temp != null && temp.length == 1) {
                    data = byteMerger(data, temp);
                    String s = new String(data);
                    if (s.contains("\r\n\r\n")) {
                        if (!s.contains("200 OK")) {
                            return new CommResponse(TransResult.ERR_RECV, null);
                        }
                        break;
                    }
                }
            }
        }
        try {
            byte[] lenBuf = client.recv(2);
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
        if (client != null){
            try {
                client.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private int connectCupSLL(String hostIp, int port, int timeout) {
        if (hostIp == null || hostIp.length() == 0 || hostIp.equals("0.0.0.0")) {
            return TransResult.ERR_CONNECT;
        }

        ICommHelper commHelper = TopApplication.iTool.getCommHelper();
        ISslKeyStore keyStore = commHelper.createSslKeyStore();
        if (keyStoreStream != null) {
            try {
                keyStoreStream.reset();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
        keyStore.setTrustStore(keyStoreStream);
        client = commHelper.createSslClient(hostIp, port, keyStore);
        client.setConnectTimeout(timeout);
        client.setRecvTimeout(timeout);
        try {
            client.connect();
            return TransResult.SUCC;
        } catch (CommException e) {

            e.printStackTrace();
        }
        return TransResult.ERR_CONNECT;
    }


    byte[] getCupSslPackage(byte[] req) {
        String CUP_HOST_NAME = hostIp + ":" + hostPort;
        String CUP_URL = "http://" + CUP_HOST_NAME + "/unp/webtrans/VPB_lb";
        // IHttp http = device.getGl().getPacker().getHttp();
        // IHttpRequest request = http.createHttpRequest();
        // request.setMethod(Method.POST);
        // request.setURI(CUP_URL);
        // HashMap<String, String> headerMap = new HashMap<String, String>();
        // // headerMap.put("HOST: ", CUP_HOST_NAME);
        // headerMap.put("User-Agent", "Donjin Http 0.1");
        // headerMap.put("Cache-Control", "no-cache");
        // headerMap.put("Content-Type", "x-ISO-TPDU/x-auth");
        // headerMap.put("Accept", "*/*");
        // // headerMap.put("Content-Length:", String.format("%d", req.length));
        // request.setHeader(headerMap);
        // request.setData(req);
        // try {
        // return http.pack(request);
        // } catch (HttpException e) {
        //
        // e.printStackTrace();
        // }
        // return req;

        StringBuilder httpsReq = new StringBuilder();
        httpsReq.append("POST ");
        httpsReq.append(CUP_URL);
        httpsReq.append(" HTTP/1.1");
        httpsReq.append("\r\n");
        httpsReq.append("HOST: ");
        httpsReq.append(CUP_HOST_NAME);
        httpsReq.append("\r\n");
        httpsReq.append("User-Agent: Donjin Http 0.1");
        httpsReq.append("\r\n");
        httpsReq.append("Cache-Control: no-cache");
        httpsReq.append("\r\n");
        httpsReq.append("Content-Type: x-ISO-TPDU/x-auth");
        httpsReq.append("\r\n");
        httpsReq.append("Accept: */*");
        httpsReq.append("\r\n");
        httpsReq.append("Content-Length: ");
        httpsReq.append(String.format("%d", req.length));
        httpsReq.append("\r\n");
        httpsReq.append("\r\n");
        byte[] header = httpsReq.toString().getBytes();
        byte[] bReq = byteMerger(header, req);
        return byteMerger(bReq, "\r\n".getBytes());
    }

    byte[] byteMerger(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
        byte[] arrayOfByte = new byte[paramArrayOfByte1.length + paramArrayOfByte2.length];
        System.arraycopy(paramArrayOfByte1, 0, arrayOfByte, 0, paramArrayOfByte1.length);
        System.arraycopy(paramArrayOfByte2, 0, arrayOfByte, paramArrayOfByte1.length, paramArrayOfByte2.length);
        return arrayOfByte;
    }


}
