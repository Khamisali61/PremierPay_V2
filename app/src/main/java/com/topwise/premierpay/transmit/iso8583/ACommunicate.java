package com.topwise.premierpay.transmit.iso8583;

import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.transmit.TransProcessListener;

public abstract class ACommunicate {

    /**
     * 建立连接
     *
     * @return
     */
    public abstract int onConnect();

    /**
     * 发送数据
     *
     * @param data
     * @return
     */
    public abstract int onSend(byte[] data);

    /**
     * 接收数据
     *
     * @return
     */
    public abstract CommResponse onRecv();

    /**
     * 关闭连接
     */
    public abstract void onClose();

    public TransProcessListener transProcessListener;

    /**
     * 设置监听器
     *
     * @param listener
     */
    public void setTransProcessListener(TransProcessListener listener) {
        this.transProcessListener = listener;
    }

    public void onShowMsg(String msg) {
        if (transProcessListener != null) {
            String sTime = TopApplication.sysParam.get(SysParam.COMM_TIMEOUT);
            int timeOut = Integer.valueOf(sTime);
            if (timeOut == 0 ) timeOut = 30;

            transProcessListener.onShowProgress(msg, timeOut);
        }
    }

    public void onShowMsg(String msg, int timeout) {
        if (transProcessListener != null) {
            transProcessListener.onShowProgress(msg, timeout);
        }
    }
}
