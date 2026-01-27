package com.topwise.premierpay.transmit.iso8583;

import android.text.TextUtils;

import com.topwise.toptool.api.comm.IComm;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.param.SysParam;


public abstract class ATcpCommunicate extends ACommunicate {
    public final static int MAXBUFFERLEN = 2048; /*定义ISO_data结构中最长的缓冲区*/
    protected String hostIp;
    protected int hostPort;
    protected int connectTomeOut;

    protected IComm client;
    /**
     * 设置TCP通讯的相关参数
     *
     * @return
     */
    public int setTcpCommParam(){
        return 0;
    }

    protected String getMainHostIp() {
        SysParam sysParam = TopApplication.sysParam;
        String hostIp = sysParam.get(SysParam.HOSTIP);
        return hostIp;
    }

    protected int getMainHostPort() {
        SysParam sysParam = TopApplication.sysParam;

        String outTime = sysParam.get(SysParam.HOSTPORT);
        if (TextUtils.isEmpty(outTime)) {
            outTime = "0";
        }
        return Integer.parseInt(outTime);
    }

    protected int getOutTime() {
        SysParam sysParam = TopApplication.sysParam;

        String hostPort = sysParam.get(SysParam.COMM_TIMEOUT);
        if (hostPort == null || hostPort.length() == 0) {
            hostPort = "30";
        }
        return Integer.parseInt(hostPort);
    }
}
