package com.topwise.toptool.api.comm;


import com.topwise.toptool.api.exception.AGeneralException;

import java.util.HashMap;

public class CommException extends AGeneralException {
    private static final long serialVersionUID = 1L;

    private static final String MODULE = "COMM";

    public static final int ERR_CONNECT = 1;

    public static final int ERR_SEND = 2;

    public static final int ERR_RECV = 3;

    public static final int ERR_DISCONNECT = 4;

    public static final int ERR_HTTP_GET = 5;

    public static final int ERR_HTTP_POST = 6;

    public static final int ERR_CANCEL = 7;

    public static final int ERR_KEY_MANAGEMENT = 8;

    private static HashMap<Integer, String> errToMsgMapping;

    static {
        (errToMsgMapping = new HashMap<Integer, String>()).put(Integer.valueOf(1), "connect error");
        errToMsgMapping.put(Integer.valueOf(2), "send error");
        errToMsgMapping.put(Integer.valueOf(3), "recv error");
        errToMsgMapping.put(Integer.valueOf(4), "disconnect error");
        errToMsgMapping.put(Integer.valueOf(5), "http get error");
        errToMsgMapping.put(Integer.valueOf(6), "http post error");
        errToMsgMapping.put(Integer.valueOf(7), "canceled");
        errToMsgMapping.put(Integer.valueOf(8), "key management error");
    }

    public CommException(int paramInt) {
        super("COMM", paramInt, errToMsgMapping.get(Integer.valueOf(paramInt)));
    }

    public CommException(int paramInt, Throwable paramThrowable) {
        super("COMM", paramInt, errToMsgMapping.get(Integer.valueOf(paramInt)), paramThrowable);
    }

    public CommException(int paramInt, String paramString) {
        super("COMM", paramInt, errToMsgMapping.get(Integer.valueOf(paramInt)), paramString);
    }

    public CommException(int paramInt, String paramString, Throwable paramThrowable) {
        super("COMM", paramInt, errToMsgMapping.get(Integer.valueOf(paramInt)), paramString, paramThrowable);
    }
}
