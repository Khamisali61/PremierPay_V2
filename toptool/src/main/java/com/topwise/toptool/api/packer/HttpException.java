package com.topwise.toptool.api.packer;


import com.topwise.toptool.api.exception.AGeneralException;

import java.util.HashMap;

public class HttpException extends AGeneralException {
  private static final long serialVersionUID = 1L;
  
  private static final String MODULE = "Http";
  
  public static final int ERR_URL_NULL = 1;
  
  public static final int ERR_RESP_INVALID = 2;
  
  public static final int ERR_RESP_HEADER_INVALID = 3;
  
  public static final int ERR_RESP_STATUS = 4;
  
  public static final int ERR_RESP_CONTENTLEN = 5;
  
  public static final int ERR_REQUEST_NULL = 6;
  
  public static final int ERR_RESPONSE_CHUNK = 7;
  
  public static final int ERR_URL_FORMAT = 8;
  
  public static final int ERR_REQUEST_HEADER_FORMAT = 9;
  
  public static final int ERR_RESPONSE_DATA_UNCOMPLETE = 10;
  
  private static HashMap<Integer, String> errToMsgMapping;
  
  static {
    (errToMsgMapping = new HashMap<Integer, String>()).put(Integer.valueOf(1), "url of request is null");
    errToMsgMapping.put(Integer.valueOf(2), "the response is invalid");
    errToMsgMapping.put(Integer.valueOf(3), "the header of response is invalid");
    errToMsgMapping.put(Integer.valueOf(4), "the status is not complete");
    errToMsgMapping.put(Integer.valueOf(5), "the Content-Length of response is invalid");
    errToMsgMapping.put(Integer.valueOf(6), "request is null");
    errToMsgMapping.put(Integer.valueOf(7), "chunk of response error");
    errToMsgMapping.put(Integer.valueOf(8), "url of request error");
    errToMsgMapping.put(Integer.valueOf(9), "header of request convert error");
    errToMsgMapping.put(Integer.valueOf(10), "the recv data is not enough");
  }
  
  public HttpException(int paramInt) {
    super("Http", paramInt, errToMsgMapping.get(Integer.valueOf(paramInt)));
  }
  
  public HttpException(int paramInt, Throwable paramThrowable) {
    super("Http", paramInt, errToMsgMapping.get(Integer.valueOf(paramInt)), paramThrowable);
  }
  
  public HttpException(int paramInt, String paramString) {
    super("Http", paramInt, errToMsgMapping.get(Integer.valueOf(paramInt)), paramString);
  }
  
  public HttpException(int paramInt, String paramString, Throwable paramThrowable) {
    super("Http", paramInt, errToMsgMapping.get(Integer.valueOf(paramInt)), paramString, paramThrowable);
  }
}
