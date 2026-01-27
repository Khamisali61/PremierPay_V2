package com.topwise.toptool.impl;

import android.os.ConditionVariable;
import android.os.Looper;

import com.topwise.toptool.api.comm.CommException;
import com.topwise.toptool.api.comm.ICommSslClient;
import com.topwise.toptool.api.comm.ISslKeyStore;
import com.topwise.toptool.api.utils.AppLog;
import com.topwise.toptool.api.utils.IUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketAddress;

import javax.net.ssl.SSLSocket;


class CommSslClient extends AComm implements ICommSslClient {
    private static final String TAG = CommSslClient.class.getSimpleName();


    private String host;
    private int port;
    private ISslKeyStore sslKeyStore;

    private SSLSocket sslSocket;

    private boolean isConnected;

    private InputStream inputStream;
    private OutputStream outputStream;

    private volatile boolean cancelRecvFlag;

    private ConditionVariable cvWaitReadThreadStart;

    public CommSslClient( String host, int port, ISslKeyStore keystore) {

        this.host = host;
        this.port = port;
        this.sslKeyStore = keystore;
    }

    @Override
    public synchronized void connect() throws CommException {
        try {
            CommSslSocketFactoryExt sslSocketFactory;
            if (sslKeyStore == null) {
                sslSocketFactory = new CommSslSocketFactoryExt(null, null, null);
            } else {
                sslSocketFactory = new CommSslSocketFactoryExt(sslKeyStore.getKeyStore(),
                        sslKeyStore.getKeyStorePassword(), sslKeyStore.getTrustStore());

            }

            sslSocket = (SSLSocket) sslSocketFactory.createSocket();

            // WARNING: this will cause always DNS for all 'host', even if it's an IPv4 address!!!
            // SocketAddress svrAddr = new InetSocketAddress(host, port);
            SocketAddress svrAddr = CommHelper.createSocketAddress(host, port);

            // set sotimeout to limit the negotiation timeout
            sslSocket.setSoTimeout(getConnectTimeout());

            sslSocket.connect(svrAddr, getConnectTimeout());
            inputStream = sslSocket.getInputStream();
            outputStream = sslSocket.getOutputStream();
            startRecvThread();
            isConnected = true;
            AppLog.d(TAG, "ssl connected");
        } catch (Exception e) {
            throw new CommException(CommException.ERR_CONNECT, e.getCause());
        }
    }

    @Override
    public synchronized EConnectStatus getConnectStatus() {
        if (isConnected) {
            return EConnectStatus.CONNECTED;
        }
        return EConnectStatus.DISCONNECTED;
    }

    @Override
    public synchronized void disconnect() throws CommException {
        try {
            if (sslSocket != null) {
                sslSocket.close();
                sslSocket = null;
                AppLog.d(TAG, "ssl socket closed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CommException(CommException.ERR_DISCONNECT);
        } finally {
            inputStream = null;
            outputStream = null;
            isConnected = false;
            AppLog.d(TAG, "ssl close finally");
        }
    }

    @Override
    public synchronized void send(byte[] data) throws CommException {
        try {
            outputStream.write(data);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CommException(CommException.ERR_SEND, e.getCause());
        }
    }

    @Override
    public synchronized byte[] recv(int expLen) throws CommException {
        if (expLen <= 0) {
            return new byte[0];
        }

        byte[] buf = new byte[expLen];
        int len = 0;
        try {
            int totalLen = 0;
            int cLen;
            long countDown = getRecvTimeout();
            long end = System.currentTimeMillis() + countDown;
            AppLog.d(TAG, "timeout " + countDown);
            cancelRecvFlag = false;
            while (totalLen < expLen && (System.currentTimeMillis() < end)) {
                if (cancelRecvFlag) {
                    AppLog.w(TAG, "recv cancelled! currently recved " + totalLen);
                    throw new CommException(CommException.ERR_CANCEL);
                }
                cLen = sslRingBuffer.read(buf, totalLen, expLen - totalLen);
                totalLen += cLen;
                Thread.yield();
                if (sslReadException != null) {
                    // try again
                    AppLog.w(TAG, "recv exception! try check ringbuffer again..., current total " + totalLen);
                    cLen = sslRingBuffer.read(buf, totalLen, expLen - totalLen);
                    totalLen += cLen;

                    if (totalLen <= 0) {
                        // really nothing! exception!
                        AppLog.e(TAG, "nothing in ringbuffer, throw exception");
                        throw sslReadException;
                    } else {
                        AppLog.w(TAG, totalLen + "bytes in ringbuffer, return data");
                        break;
                    }
                }
            }

            if (totalLen == 0) {
                // throw new IOException("Recv timeout");
                AppLog.w(TAG, "recv nothing");
            }

            len = totalLen;

            byte[] ret = new byte[len];
            System.arraycopy(buf, 0, ret, 0, len);

            return ret;
        } catch (CommException ce) {
            if (ce.getErrCode() == CommException.ERR_CANCEL) {
                throw ce;
            } else {
                throw new CommException(CommException.ERR_RECV, ce.getCause());
            }
        } catch (Exception e) {
            throw new CommException(CommException.ERR_RECV, e.getCause());
        }

    }

    @Override
    public synchronized byte[] recvNonBlocking() throws CommException {
        if (sslRingBuffer != null) {
            return sslRingBuffer.read();
        } else {
            return new byte[0];
        }
    }

    @Override
    public synchronized void reset() {
        // reset bt ring buffer
        if (sslRingBuffer != null) {
            sslRingBuffer.reset();
        }
    }

    @Override
    public void cancelRecv() {
        cancelRecvFlag = true;
    }

    private Exception sslReadException = null;
    private SslReadThread sslReadThread = null;

    private IUtils.IRingBuffer sslRingBuffer;

    private void startRecvThread() {
        if (sslReadException != null) {
            sslReadThread = null;
            sslReadException = null;
        }

        if (sslReadThread == null) {
            sslReadThread = new SslReadThread();
            sslReadThread.start();

            cvWaitReadThreadStart = new ConditionVariable();
            cvWaitReadThreadStart.block(2000);
        }
    }

    class SslReadThread extends Thread {
        private byte[] tmpBuffer;

        public SslReadThread() {
            tmpBuffer = new byte[10240];
            sslRingBuffer = TopTool.getInstance().getUtils().createRingBuffer(10240);
        }

        public void run() {
            Looper.prepare();
            try {

                if (cvWaitReadThreadStart != null) {
                    cvWaitReadThreadStart.open();
                }

                while (true) {
                    int len = inputStream.read(tmpBuffer);
                    if (len < 0) {
                        throw new IOException("input stream read error: " + len);
                    } else {
                        sslRingBuffer.write(tmpBuffer, len);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                sslReadException = e;
            }
        }
    }

    private byte[] ipv4String2ByteArray(String ipv4) {
        byte[] ret = new byte[4];
        String[] strs = ipv4.split("\\.");
        for (int i = 0; i < strs.length; i++) {
            ret[i] = (byte) Integer.parseInt(strs[i]);
        }
        return ret;
    }

}
