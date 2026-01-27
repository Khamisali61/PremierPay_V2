package com.topwise.toptool.api.exception;

public abstract class AGeneralException extends Exception {
  private static final long serialVersionUID = 1L;
  
  private String errModule = "";
  
  private int errCode;
  
  private String errMsg = "";
  
  public AGeneralException(String paramString1, int paramInt, String paramString2) {
    super(String.valueOf(paramString1) + "#" + paramInt + "(" + paramString2 + ")");
    this.errModule = paramString1;
    this.errCode = paramInt;
    this.errMsg = paramString2;
  }
  
  public AGeneralException(String paramString1, int paramInt, String paramString2, String paramString3) {
    super(String.valueOf(paramString1) + "#" + paramInt + "(" + paramString2 + ")[" + paramString3 + "]");
    this.errModule = paramString1;
    this.errCode = paramInt;
    this.errMsg = paramString2;
  }
  
  public AGeneralException(String paramString1, int paramInt, String paramString2, Throwable paramThrowable) {
    super(String.valueOf(paramString1) + "#" + paramInt + "(" + paramString2 + ")", paramThrowable);
    this.errModule = paramString1;
    this.errCode = paramInt;
    this.errMsg = paramString2;
  }
  
  public AGeneralException(String paramString1, int paramInt, String paramString2, String paramString3, Throwable paramThrowable) {
    super(String.valueOf(paramString1) + "#" + paramInt + "(" + paramString2 + ")[" + paramString3 + "]", paramThrowable);
    this.errModule = paramString1;
    this.errCode = paramInt;
    this.errMsg = paramString2;
  }
  
  public String getErrModule() {
    return this.errModule;
  }
  
  public int getErrCode() {
    return this.errCode;
  }
  
  public String getErrMsg() {
    return this.errMsg;
  }
}
