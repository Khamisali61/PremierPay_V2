package com.topwise.toptool.impl;

import android.content.Context;

import com.topwise.toptool.api.utils.AppLog;
import com.topwise.toptool.api.utils.IUtils;


public class UtilsRingBuffer implements IUtils.IRingBuffer {
    private static String TAG = "RingBuffer";

    private byte[] buffer;
    private int wp = 0; // write pointer
    private int rp = 0; // read pointer
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public UtilsRingBuffer(int size) {
        buffer = new byte[size];
    }

    synchronized public int available() {
        return statusForRead()[0];
    }

    synchronized public int read(byte[] out, int offset, int exp) {
        int[] status = statusForRead();
        if (exp > status[0]) {
            exp = status[0];
        }

        if (exp <= status[1]) {
            System.arraycopy(buffer, rp, out, offset, exp);
            rp += exp;
            rp %= buffer.length;

            return exp;
        } else {
            System.arraycopy(buffer, rp, out, offset, status[1]);
            System.arraycopy(buffer, 0, out, offset + status[1], Math.min(exp - status[1], status[2]));
            rp = Math.min(exp - status[1], status[2]);

            return status[1] + rp;
        }
    }

    synchronized public byte[] read() {
        int[] status = statusForRead();
        byte[] ret = new byte[status[0]];
        System.arraycopy(buffer, rp, ret, 0, status[1]);
        rp += status[1];
        rp %= buffer.length;
        System.arraycopy(buffer, 0, ret, status[1], status[2]);
        rp += status[2];
        return ret;
    }

    synchronized public int write(byte[] data, int len) {
        int[] status = statusForWrite();
        int realLen = len;

        if (realLen > status[0]) {
            AppLog.w(TAG,
                    String.format("len %d too long, free space %d not enough, only %d will be saved!", realLen,
                            status[0], status[0]));
            realLen = status[0];
        }

        if (realLen <= status[1]) {
            System.arraycopy(data, 0, buffer, wp, realLen);
            wp += realLen;
            wp %= buffer.length;
        } else {
            System.arraycopy(data, 0, buffer, wp, status[1]);
            System.arraycopy(data, status[1], buffer, 0, realLen - status[1]);
            wp = realLen - status[1];
        }
        return realLen;
    }

    // NOTE: don't set buffer to null!
    synchronized public void reset() {
        wp = 0;
        rp = 0;
    }

    /**
     * ��ȡ�ɶ��ֽ���
     *
     * @return int[0]��ʾ�ܹ��ɶ�ȡ�ֽ�, int[1]Ϊ��ָ��ǰ����ֽ���(���ڲ�ʹ��), int[2]Ϊ��ָ�������ֽ���(���ڲ�ʹ��)
     */
    // 0 1 2
    // total bytes/forward bytes/backward bytes
    synchronized private int[] statusForRead() {
        int[] ret = new int[3];
        if (wp >= rp) {
            ret[1] = wp - rp;
            ret[2] = 0;
        } else {
            ret[1] = buffer.length - rp;
            ret[2] = wp;
        }

        ret[0] = ret[1] + ret[2];
        return ret;
    }

    /**
     * ��ȡ��д�ֽ���
     *
     * @return int[0]��ʾ�ܹ���д�ֽ���, int[1]Ϊдָ��ǰ����ֽ���(���ڲ�ʹ��), int[2]Ϊдָ�������ֽ���(���ڲ�ʹ��)
     */
    // 0 1 2
    // free bytes/forward bytes/backward bytes
    synchronized private int[] statusForWrite() {
        int[] ret = new int[3];
        if (wp >= rp) {
            ret[1] = buffer.length - wp;
            if (rp == 0) {
                ret[1]--; // so that the wp won't overlap with rp.
            }

            ret[2] = rp;
            if (ret[2] > 0) {
                ret[2]--;
            }
        } else {
            ret[1] = rp - wp - 1;
            ret[2] = 0;
        }

        ret[0] = ret[1] + ret[2]; // maximum buffer.length - 1;
        return ret;
    }

}
