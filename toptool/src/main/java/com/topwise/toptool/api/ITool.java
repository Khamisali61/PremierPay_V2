package com.topwise.toptool.api;


import com.topwise.toptool.api.algo.IAlgo;
import com.topwise.toptool.api.comm.ICommHelper;
import com.topwise.toptool.api.convert.IConvert;
import com.topwise.toptool.api.packer.IPacker;
import com.topwise.toptool.api.utils.IUtils;

public interface ITool {
    String getVersion();
    IAlgo getAlgo();
    IConvert getConvert();
    IPacker getPacker();
    IUtils getUtils();
    ICommHelper getCommHelper();

}
