package com.topwise.manager.emv.entity;

import com.topwise.cloudpos.aidl.emv.level2.PreProcResult;
import com.topwise.cloudpos.aidl.emv.level2.TransParam;
import com.topwise.cloudpos.struct.BytesUtil;
import com.topwise.toptool.impl.TopTool;

import java.util.Arrays;

/**
 * 创建日期：2021/6/16 on 11:02
 * 描述:
 * 作者:wangweicheng
 */
public class ClssTransParam {
    private byte kernType;
    private int nFinalSelectFCIdataLen;
    private byte[] aucFinalSelectFCIdata;
    private PreProcResult preProcResult;
    private TransParam transParam;

    private boolean clssForceOnlinePin; //是否强制联机 ture 强制
    private boolean bSupSimpleProc; //Is it a simple process


    public boolean isClssForceOnlinePin() {
        return clssForceOnlinePin;
    }

    public void setClssForceOnlinePin(boolean clssForceOnlinePin) {
        this.clssForceOnlinePin = clssForceOnlinePin;
    }

    public byte getKernType() {
        return kernType;
    }

    public void setKernType(byte kernType) {
        this.kernType = kernType;
    }

    public int getnFinalSelectFCIdataLen() {
        return nFinalSelectFCIdataLen;
    }

    public void setnFinalSelectFCIdataLen(int nFinalSelectFCIdataLen) {
        this.nFinalSelectFCIdataLen = nFinalSelectFCIdataLen;
    }

    public byte[] getAucFinalSelectFCIdata() {
        return aucFinalSelectFCIdata;
    }

    public void setAucFinalSelectFCIdata(byte[] aucFinalSelectFCIdata) {
        this.aucFinalSelectFCIdata = aucFinalSelectFCIdata;
    }

    public PreProcResult getPreProcResult() {
        return preProcResult;
    }

    public void setPreProcResult(PreProcResult preProcResult) {
        this.preProcResult = preProcResult;
    }

    public TransParam getTransParam() {
        return transParam;
    }

    public void setTransParam(TransParam transParam) {
        this.transParam = transParam;
    }

    public boolean isbSupSimpleProc() {
        return bSupSimpleProc;
    }

    public void setbSupSimpleProc(boolean bSupSimpleProc) {
        this.bSupSimpleProc = bSupSimpleProc;
    }

    private String getFinalSelectFCIData(){
        if (aucFinalSelectFCIdata != null){
           return TopTool.getInstance().getConvert().bcdToStr(aucFinalSelectFCIdata);
        }
        return "null";
    }

    @Override
    public String toString() {
        return "ClssTransParam{" +
                "kernType=" + kernType +
                ", nFinalSelectFCIdataLen=" + nFinalSelectFCIdataLen +
                ", aucFinalSelectFCIdata=" + getFinalSelectFCIData() +
                ", preProcResult=" + preProcResult +
                ", transParam=" + transParam +
                ", clssForceOnlinePin=" + clssForceOnlinePin +
                ", bSupSimpleProc=" + bSupSimpleProc +
                '}';
    }
}
