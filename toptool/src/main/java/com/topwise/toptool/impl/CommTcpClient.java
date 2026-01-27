package com.topwise.toptool.impl;

import android.content.Context;

import com.topwise.toptool.api.comm.CommException;
import com.topwise.toptool.api.comm.ICommTcpClient;
import com.topwise.toptool.api.utils.AppLog;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;


class CommTcpClient extends CommTcpConnectedSocket implements ICommTcpClient {
    private Context context;

    private static final String TAG = CommTcpClient.class.getSimpleName();

    private String host;
    private int port;

    private SocketChannel client;

    private ByteBuffer garbageBuffer = ByteBuffer.allocate(1024);

    public CommTcpClient(String host, int port) {
        super();
        this.context = context;
        this.host = TopTool.getInstance().getUtils().formatIpAddress(host);
        this.port = port;
    }

    @Override
    public synchronized void connect() throws CommException {
        try {
            /*
             * NON-BLOCKING, using selector
             */
            //WARNING: this will cause always DNS for all 'host', even if it's an IPv4 address!!!
            //SocketAddress svrAddr = new InetSocketAddress(host, port);
            SocketAddress svrAddr = CommHelper.createSocketAddress(host, port);

            client = SocketChannel.open();
            client.configureBlocking(false);

            Selector selector = Selector.open();
            SelectionKey key = client.register(selector, SelectionKey.OP_CONNECT);

            client.connect(svrAddr);

            int readyChannels = selector.select(connectTimeout);
            if (readyChannels == 0) {
                AppLog.w(TAG, "no channel ready!");
                throw new IOException("No channel ready!");
            }

            if (key.isConnectable()) {
                if (client.finishConnect()) {
                    // For socketchannel, this doesn't mean it's connected (if
                    // you unplug the cable or swith off wifi, it also returns
                    // true
                    // we can check further later with reset() (try to read..)
                    // to check if it's really connected.
                    // MyLog.i(TAG, "connected!");
                } else {
                    AppLog.w(TAG, "not connected!");
                    throw new IOException("Not connected!");
                }
            } else {
                AppLog.e(TAG, "not connectable!");
                throw new IOException("Not connectable!");
            }

            // cancel this key, so we can set back to blocking mode
            key.cancel();

            // NOTE: this is used to check if it's really connected or NOT (by
            // reading it)!
            while (client.read(garbageBuffer) > 0) {
                garbageBuffer.clear();
            }

            client.configureBlocking(true);

            setConnectedSocket(client.socket());

            AppLog.d(TAG, "tcp connected!");

        } catch (Exception e) {
            e.printStackTrace();
            throw new CommException(CommException.ERR_CONNECT);
        }
    }
}
