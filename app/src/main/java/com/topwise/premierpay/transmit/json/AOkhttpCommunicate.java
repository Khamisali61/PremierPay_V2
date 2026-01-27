package com.topwise.premierpay.transmit.json;

import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.transmit.TransProcessListener;

public abstract class AOkhttpCommunicate {

    /**
     * 初始化
     *
     * @return
     */
    public abstract int onInitPath();

    /**
     * 发送数据 接收数据
     *
     * @param data
     * @return
     */
    public abstract JsonResponse onSendAndRecv(String data);

    /**
     * 发送数据 接收数据
     *
     * @param data
     * @return
     */
    public abstract void onSendAndRecv(String data, IRecvListener onRecvCallback);

    public TransProcessListener transProcessListener;

    /**
     * 设置监听器
     *
     * @param listener
     */
    protected void setTransProcessListener(TransProcessListener listener) {
        this.transProcessListener = listener;
    }

    protected void onShowMsg(String msg) {
        if (transProcessListener != null) {
            String sTime = TopApplication.sysParam.get(SysParam.COMM_TIMEOUT);
            int timeOut = Integer.valueOf(sTime);
            if (timeOut == 0 ) timeOut = 30;

            transProcessListener.onShowProgress(msg, timeOut);
        }
    }

    protected void onShowMsg(String msg, int timeout) {
        if (transProcessListener != null) {
            transProcessListener.onShowProgress(msg, timeout);
        }
    }
}
