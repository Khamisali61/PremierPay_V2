package com.topwise.toptool.api.packer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public interface IIso8583 {
  IIso8583Entity getEntity();
  
  byte[] pack() throws Iso8583Exception;
  
  byte[] pack(List<String> paramList) throws Iso8583Exception;
  
  HashMap<String, byte[]> unpack(byte[] paramArrayOfbyte, boolean paramBoolean) throws Iso8583Exception;
  
  public static interface IIso8583Entity {
//    IIso8583Entity loadTemplate(String param1String) throws Iso8583Exception, IOException, XmlPullParserException;
    
    IIso8583Entity loadTemplate(InputStream param1InputStream) throws Iso8583Exception, IOException, XmlPullParserException;
    
    IIso8583Entity setSecondaryBitmapOnOff(boolean param1Boolean);
    
    boolean getSecondaryBitmapOnOff();
    
    IIso8583Entity setSupportTertiaryBitmap(boolean param1Boolean);
    
    boolean isSupportTertiaryBitmap();
    
    IIso8583Entity setVarLenFormat(EVarLenFormat param1EVarLenFormat);
    
    EVarLenFormat getVarLenFormat();
    
    IFieldAttrs createFieldAttrs();
    
    IIso8583Entity setFieldAttrs(String param1String, IFieldAttrs param1IFieldAttrs) throws Iso8583Exception;
    
    IIso8583Entity setFieldValue(String param1String1, String param1String2) throws Iso8583Exception;
    
    IIso8583Entity setFieldValue(String param1String, byte[] param1ArrayOfbyte) throws Iso8583Exception;
    
    boolean hasField(String param1String);
    
    IIso8583Entity resetFieldValue(String param1String) throws Iso8583Exception;
    
    IIso8583Entity resetAllFieldsValue();
    
    IIso8583Entity resetAll();
    
    IIso8583Entity dump();
    
    public enum EVarLenFormat {
      BCD, ASC, BIN;
    }
    
    public static interface IFieldAttrs {
      IFieldAttrs setFormat(String param2String);
      
      String getFormat();
      
      IFieldAttrs setFormatUnpack(String param2String);
      
      String getFormatUnpack();
      
      IFieldAttrs setPaddingPosition(EPaddingPosition param2EPaddingPosition);
      
      EPaddingPosition getPaddingPosition();
      
      IFieldAttrs setPaddingPositionUnpack(EPaddingPosition param2EPaddingPosition);
      
      EPaddingPosition getPaddingPositionUnpack();
      
      IFieldAttrs setPaddingChar(String param2String);
      
      String getPaddingChar();
      
      IFieldAttrs setPaddingCharUnpack(String param2String);
      
      String getPaddingCharUnpack();
      
      IFieldAttrs setDescription(String param2String);
      
      String getDescription();
      
      IFieldAttrs setVarLenFormat(EVarLenFormat param2EVarLenFormat);
      
      EVarLenFormat getVarLenFormat();
      
      IFieldAttrs setVarLenFormatUnpack(EVarLenFormat param2EVarLenFormat);
      
      EVarLenFormat getVarLenFormatUnpack();
      
      public enum EPaddingPosition {
        PADDING_LEFT, PADDING_RIGHT;
      }
    }
  }
}
