package com.topwise.toptool.api.packer;

import java.util.List;

public interface ITlv {
  ITlvDataObjList createTlvDataObjectList();
  
  ITlvDataObj createTlvDataObject();
  
  byte[] pack(ITlvDataObj paramITlvDataObj) throws TlvException;
  
  byte[] pack(ITlvDataObjList paramITlvDataObjList) throws TlvException;
  
  ITlvDataObjList unpack(byte[] paramArrayOfbyte) throws TlvException;

  ITlvDataObjList unpackDDol(byte[] paramArrayOfbyte) throws TlvException;
  public static interface ITlvDataObj {
    boolean isConstructed();
    
    int setTag(byte[] param1ArrayOfbyte);
    
    int setTag(int param1Int);
    
    void setValue(byte param1Byte);
    
    void setValue(byte[] param1ArrayOfbyte);
    
    byte[] getValue();
    
    byte[] getTag();
    
    Integer getIntTag();

    void setLength(int length);

    int getLength();

    String toString();
  }
  
  public static interface ITlvDataObjList {
    void addDataObj(ITlvDataObj param1ITlvDataObj);
    
    List<ITlvDataObj> getDataObjectList();
    
    ITlvDataObj getByTag(byte[] param1ArrayOfbyte);
    
    ITlvDataObj getByTag(int param1Int);
    
    List<ITlvDataObj> getDataObjectListByTag(byte[] param1ArrayOfbyte);
    
    List<ITlvDataObj> getDataObjectListByTag(int param1Int);
    
    byte[] getValueByTag(byte[] param1ArrayOfbyte);
    
    byte[] getValueByTag(int param1Int);
    
    List<byte[]> getValueListByTag(byte[] param1ArrayOfbyte);
    
    List<byte[]> getValueListByTag(int param1Int);
    
    int getIndexByTag(byte[] param1ArrayOfbyte);
    
    int getIndexByTag(int param1Int);
    
    int[] getIndicesByTag(byte[] param1ArrayOfbyte);
    
    int[] getIndicesByTag(int param1Int);
    
    boolean updateValueByTag(byte[] param1ArrayOfbyte1, byte[] param1ArrayOfbyte2);
    
    boolean updateValueByTag(int param1Int, byte[] param1ArrayOfbyte);
    
    boolean updateValueByTag(byte[] param1ArrayOfbyte1, byte[] param1ArrayOfbyte2, int param1Int);
    
    boolean updateValueByTag(int param1Int1, byte[] param1ArrayOfbyte, int param1Int2);
    
    void removeByTag(byte[] param1ArrayOfbyte);
    
    void removeByTag(int param1Int);
    
    void removeByTag(byte[] param1ArrayOfbyte, int param1Int);
    
    void removeByTag(int param1Int1, int param1Int2);

    int getSize();
    ITlvDataObj getIndexTag(int index);
  }
}
