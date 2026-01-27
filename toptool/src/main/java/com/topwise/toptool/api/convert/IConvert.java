package com.topwise.toptool.api.convert;

public interface IConvert {
  long strToLong(String paramString,EPaddingPosition paramEPaddingPosition) throws IllegalArgumentException;

  String bcdToStr(byte[] paramArrayOfbyte) throws IllegalArgumentException;
  
  byte[] strToBcd(String paramString, EPaddingPosition paramEPaddingPosition) throws IllegalArgumentException;
  
  void longToByteArray(long paramLong, byte[] paramArrayOfbyte, int paramInt, EEndian paramEEndian) throws IllegalArgumentException;
  
  byte[] longToByteArray(long paramLong, EEndian paramEEndian) throws IllegalArgumentException;
  
  void intToByteArray(int paramInt1, byte[] paramArrayOfbyte, int paramInt2, EEndian paramEEndian) throws IllegalArgumentException;
  
  byte[] intToByteArray(int paramInt, EEndian paramEEndian) throws IllegalArgumentException;
  
  void shortToByteArray(short paramShort, byte[] paramArrayOfbyte, int paramInt, EEndian paramEEndian) throws IllegalArgumentException;
  
  byte[] shortToByteArray(short paramShort, EEndian paramEEndian) throws IllegalArgumentException;
  
  long longFromByteArray(byte[] paramArrayOfbyte, int paramInt, EEndian paramEEndian) throws IllegalArgumentException;
  
  int intFromByteArray(byte[] paramArrayOfbyte, int paramInt, EEndian paramEEndian) throws IllegalArgumentException;
  
  short shortFromByteArray(byte[] paramArrayOfbyte, int paramInt, EEndian paramEEndian) throws IllegalArgumentException;
  
  String stringPadding(String paramString, char paramChar, long paramLong, EPaddingPosition paramEPaddingPosition) throws IllegalArgumentException;
  
  String amountMajorToMinUnit(double paramDouble, ECurrencyExponent paramECurrencyExponent) throws IllegalArgumentException;
  
  String amountMajorToMinUnit(String paramString, ECurrencyExponent paramECurrencyExponent) throws IllegalArgumentException;
  
  String amountMinUnitToMajor(String paramString, ECurrencyExponent paramECurrencyExponent, boolean paramBoolean) throws IllegalArgumentException;
  
  public enum EPaddingPosition {
    PADDING_LEFT, PADDING_RIGHT;
  }
  
  public enum EEndian {
    LITTLE_ENDIAN, BIG_ENDIAN;
  }
  
  public enum ECurrencyExponent {
    CURRENCY_EXPONENT_0, CURRENCY_EXPONENT_1, CURRENCY_EXPONENT_2, CURRENCY_EXPONENT_3;
  }
}
