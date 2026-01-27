package com.topwise.toptool.impl;

import com.topwise.toptool.api.comm.CommException;
import com.topwise.toptool.api.utils.AppLog;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;


class CommTcpConnectedSocket extends AComm {

    private static final String TAG = CommTcpConnectedSocket.class.getSimpleName();

    private Socket connectedSocket;
    private InputStream inputStream;
    private OutputStream outputStream;

    private boolean isConnected;

    private volatile boolean cancelRecvFlag;

    private byte[] recvBuffer = new byte[102400];

    public void setConnectedSocket(Socket socket) {
        this.connectedSocket = socket;
        try {
            outputStream = connectedSocket.getOutputStream();
            inputStream = connectedSocket.getInputStream();
            isConnected = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public CommTcpConnectedSocket( ) {
    }
    public CommTcpConnectedSocket( Socket socket) {
        setConnectedSocket(socket);
    }

    @Override
    public synchronized void connect() throws CommException {
        // do NOTHING for connected socket
    }

    @Override
    public synchronized EConnectStatus getConnectStatus() {
        if (isConnected) {
            return EConnectStatus.CONNECTED;
        } else {
            return EConnectStatus.DISCONNECTED;
        }
    }

    @Override
    public synchronized void disconnect() throws CommException {
        try {
            AppLog.d(TAG, "closing...");
            if (isConnected) {
                connectedSocket.shutdownInput();
                connectedSocket.shutdownOutput();
                connectedSocket.close();
                AppLog.d(TAG, "socket closed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CommException(CommException.ERR_DISCONNECT);
        } finally {
            connectedSocket = null;
            inputStream = null;
            outputStream = null;
            isConnected = false;
            AppLog.d(TAG, "close finally");
        }
    }

    @Override
    public synchronized void send(byte[] data) throws CommException {
        try {
            if (connectedSocket != null) {
                connectedSocket.setSoTimeout(getSendTimeout());
            }
            outputStream.write(data);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CommException(CommException.ERR_SEND);
        }
    }

    @Override
    public synchronized byte[] recv(int expLen) throws CommException {
        if (expLen == 0) {
            return new byte[0];
        }

        byte[] buf = new byte[expLen];

        try {
            int len = 0;

            int totalLen = 0;
            int cLen;
            if (connectedSocket != null) {
                connectedSocket.setSoTimeout(getRecvTimeout());
            }
            cancelRecvFlag = false;
            while (totalLen < expLen) {
                if (cancelRecvFlag) {
                    AppLog.w(TAG, "recv cancelled! currently recved " + totalLen);
                    throw new CommException(CommException.ERR_CANCEL);
                }
                cLen = inputStream.read(buf, totalLen, expLen - totalLen);
                // NOTE: if connection is closed, read will return -1 and NO
                // exception is thrown.
                if (cLen < 0) {
                    AppLog.w(TAG, "connection was closed by peer!");
                    if (totalLen > 0) {
                        break;
                    } else {
                        throw new IOException("Conntection reset");
                    }
                } else if (cLen == 0) { // receive timeout
                    AppLog.w(TAG, "tcp recv timed out!");
                    break;
                } else {
                    totalLen += cLen;
                    AppLog.d(TAG, "recved " + cLen + ", total " + totalLen);
                }
            }

            len = totalLen;
            byte[] ret = new byte[len];
            System.arraycopy(buf, 0, ret, 0, len);
            return ret;
        } catch (SocketTimeoutException stoe) {
            // receive timeout is NOT treated as an error, instead, returning empty byte array.
            // tcp client does NOT enter this case, while a socket for the tcp server does
            // stoe.printStackTrace();
            return new byte[0];
        } catch (CommException ce) {
            ce.printStackTrace();
            if (ce.getErrCode() == CommException.ERR_CANCEL) {
                throw ce;
            } else {
                throw new CommException(CommException.ERR_RECV);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // disconnect(); //FIXME: should I disconnect actively?
            throw new CommException(CommException.ERR_RECV);
        }
    }

    @Override
    public synchronized byte[] recvNonBlocking() throws CommException {
        try {
            // set it to non-blocking and read till no data available.
            // client.configureBlocking(false);

            // FIXME! it's NOT real non-blocking
            connectedSocket.setSoTimeout(1);

            byte[] ret = new byte[0];
            int bytesRead = inputStream.read(recvBuffer);
            if (bytesRead > 0) {
                ret = new byte[bytesRead];
                System.arraycopy(recvBuffer, 0, ret, 0, bytesRead);
            }
            // client.configureBlocking(true);

            return ret;
        } catch (SocketTimeoutException sote) {
            // sote.printStackTrace();
            return new byte[0];
        } catch (Exception e) {
            e.printStackTrace();
            throw new CommException(CommException.ERR_RECV);
        }
    }

    @Override
    public synchronized void reset() {
        try {
            // set it to non-blocking and read till no data available.
            // client.configureBlocking(false);

            // FIXME! it's NOT real non-blocking
            connectedSocket.setSoTimeout(1);

            while (inputStream.read(recvBuffer) > 0) {
                ;
            }

            // client.configureBlocking(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancelRecv() {
        cancelRecvFlag = true;
    }

}
