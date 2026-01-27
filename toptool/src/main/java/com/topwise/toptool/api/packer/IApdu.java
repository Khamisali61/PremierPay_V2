package com.topwise.toptool.api.packer;

public interface IApdu {
  IApduReq createReq(byte paramByte1, byte paramByte2);
  
  IApduReq createReq(byte paramByte1, byte paramByte2, byte[] paramArrayOfbyte);
  
  IApduReq createReq(byte paramByte1, byte paramByte2, byte[] paramArrayOfbyte, short paramShort);
  
  IApduReq createReq(byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4);
  
  IApduReq createReq(byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4, byte[] paramArrayOfbyte);
  
  IApduReq createReq(byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4, byte[] paramArrayOfbyte, short paramShort);
  
  IApduResp unpack(byte[] paramArrayOfbyte);
  
  public static interface IApduReq {
    void setLengthOfLcLeTo2Bytes();
    
    void setLcAlwaysPresent();
    
    void setLeNotPresent();
    
    void setCla(byte param1Byte);
    
    byte getCla();
    
    void setIns(byte param1Byte);
    
    byte getIns();
    
    void setP1(byte param1Byte);
    
    byte getP1();
    
    void setP2(byte param1Byte);
    
    byte getP2();
    
    void setData(byte[] param1ArrayOfbyte);
    
    byte[] getData();
    
    void setLe(short param1Short);
    
    short getLe();
    
    byte[] pack();
  }
  
  public static interface IApduResp {
    byte[] getData();
    
    short getStatus();
    
    String getStatusString();
  }
}
