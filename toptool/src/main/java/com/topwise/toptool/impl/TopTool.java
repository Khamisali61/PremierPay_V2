package com.topwise.toptool.impl;

import android.content.Context;
import android.util.Log;

import com.topwise.toptool.api.ITool;
import com.topwise.toptool.api.algo.IAlgo;
import com.topwise.toptool.api.comm.ICommHelper;
import com.topwise.toptool.api.convert.IConvert;
import com.topwise.toptool.api.packer.IPacker;
import com.topwise.toptool.api.utils.IUtils;


public class TopTool implements ITool {
    private static final String VERSION = "V1.00.02_20210511";

    static {
        Log.e("topTool", "topTool version: " + VERSION);
    }
    private static TopTool instance;


    private TopTool() {

    }

    public synchronized static TopTool getInstance() {
        if (instance == null) {
            instance = new TopTool();
        }
        return instance;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public IAlgo getAlgo() {
        Algo algo = Algo.getInstance();
        return algo;
    }

    @Override
    public IConvert getConvert() {
        Convert convert = Convert.getInstance();
        return convert;
    }

    /**
     * Gets the group package interface
     * @return
     */
    @Override
    public IPacker getPacker() {
        Packer packer = Packer.getInstance();
        return packer;
    }
    @Override
    public IUtils getUtils() {
        Utils utils = Utils.getInstance();
        return utils;
    }
    @Override
    public ICommHelper getCommHelper() {
        CommHelper commHelper = CommHelper.getInstance();
        return commHelper;
    }
}
