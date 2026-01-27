package com.topwise.toptool.api.packer;

import com.topwise.toptool.api.exception.AGeneralException;

import java.util.HashMap;

public class Iso8583Exception extends AGeneralException {
  private static final long serialVersionUID = 1L;
  
  private static final String MODULE = "Iso8583";
  
  public static final int ERR_ARG = 1;
  
  public static final int ERR_VAR_LEN_FORMAT = 2;
  
  public static final int ERR_FIELD_ID = 3;
  
  public static final int ERR_FIELD_FORMAT = 4;
  
  public static final int ERR_PACK_FIELD_NO_VALUE = 5;
  
  public static final int ERR_PACK_FIELD_VALUE_TOO_LONG = 6;
  
  public static final int ERR_PACK_FIELD_VALUE_LENGTH_NOT_EQUAL = 7;
  
  public static final int ERR_UNPACK_DATA_OUT_OF_RANGE = 8;
  
  public static final int ERR_UNPACK_FIELD_FORMAT_NOT_SET = 9;
  
  public static final int ERR_BITMAP_OPERATION = 10;
  
  private static HashMap<Integer, String> errToMsgMapping;
  
  static {
    (errToMsgMapping = new HashMap<Integer, String>()).put(Integer.valueOf(1), "invalid argument");
    errToMsgMapping.put(Integer.valueOf(2), "invalid variable length format");
    errToMsgMapping.put(Integer.valueOf(3), "invalid field id");
    errToMsgMapping.put(Integer.valueOf(4), "invalid field format");
    errToMsgMapping.put(Integer.valueOf(5), "field has no value for packing");
    errToMsgMapping.put(Integer.valueOf(6), "field value is too long for packing");
    errToMsgMapping.put(Integer.valueOf(7),
        "field value length is not equal to the fixed length for packing");
    errToMsgMapping.put(Integer.valueOf(8), "data length out of range during unpacking");
    errToMsgMapping.put(Integer.valueOf(9), "field format not set before unpacking");
    errToMsgMapping.put(Integer.valueOf(10), "bitmap operation error");
  }
  
  public Iso8583Exception(int paramInt) {
    super("Iso8583", paramInt, errToMsgMapping.get(Integer.valueOf(paramInt)));
  }
  
  public Iso8583Exception(int paramInt, Throwable paramThrowable) {
    super("Iso8583", paramInt, errToMsgMapping.get(Integer.valueOf(paramInt)), paramThrowable);
  }
  
  public Iso8583Exception(int paramInt, String paramString) {
    super("Iso8583", paramInt, errToMsgMapping.get(Integer.valueOf(paramInt)), paramString);
  }
  
  public Iso8583Exception(int paramInt, String paramString, Throwable paramThrowable) {
    super("Iso8583", paramInt, errToMsgMapping.get(Integer.valueOf(paramInt)), paramString, paramThrowable);
  }
}
