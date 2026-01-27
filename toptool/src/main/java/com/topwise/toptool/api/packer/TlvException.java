package com.topwise.toptool.api.packer;

import com.topwise.toptool.api.exception.AGeneralException;

import java.util.HashMap;

public class TlvException extends AGeneralException {
    private static final long serialVersionUID = 1L;

    private static final String MODULE = "TLV";

    public static final int ERR_INVALID_ARG = 1;

    public static final int ERR_NO_TAG = 2;

    public static final int ERR_LENGTH_OF_L_TOO_LONG = 3;

    public static final int ERR_DATA_CORRUPTED = 4;

    private static HashMap<Integer, String> errToMsgMapping;

    static {
        (errToMsgMapping = new HashMap<Integer, String>()).put(Integer.valueOf(1), "invalid argument");
        errToMsgMapping.put(Integer.valueOf(2), "no TAG to pack");
        errToMsgMapping.put(Integer.valueOf(3), "length of L is too long during unpack");
        errToMsgMapping.put(Integer.valueOf(4), "data corrupted detected during unpack");
    }

    public TlvException(int paramInt) {
        super("TLV", paramInt, errToMsgMapping.get(Integer.valueOf(paramInt)));
    }

    public TlvException(int paramInt, Throwable paramThrowable) {
        super("TLV", paramInt, errToMsgMapping.get(Integer.valueOf(paramInt)), paramThrowable);
    }

    public TlvException(int paramInt, String paramString) {
        super("TLV", paramInt, errToMsgMapping.get(Integer.valueOf(paramInt)), paramString);
    }

    public TlvException(int paramInt, String paramString, Throwable paramThrowable) {
        super("TLV", paramInt, errToMsgMapping.get(Integer.valueOf(paramInt)), paramString, paramThrowable);
    }
}
