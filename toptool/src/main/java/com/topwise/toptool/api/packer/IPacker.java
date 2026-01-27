package com.topwise.toptool.api.packer;

public interface IPacker {
  IApdu getApdu();
  
  IIso8583 getIso8583();
  
  ITlv getTlv();

}
