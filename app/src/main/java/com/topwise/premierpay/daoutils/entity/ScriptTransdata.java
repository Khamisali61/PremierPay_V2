package com.topwise.premierpay.daoutils.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 创建日期：2021/4/19 on 16:20
 * 描述:
 * 作者:wangweicheng
 */
@Entity
public class ScriptTransdata {
    @Id
    private Long id;

    private String scriptData; // 脚本数据
    private long transNo; // 原pos流水号
    private long origTransNo; // 原pos流水号
    private long origBatchNo; // 原批次号
    private String origRefNo; // 原系统参考号
    private String origAuthCode; // 原授权码
    private String origDate; // 原交易日期
    private String origAmount; // 原交易金额
    private String origF62; // 原交易
    private String datetime; // 交易时间
    private String termID;
    private String merchID;
    
    @Generated(hash = 770272860)
    public ScriptTransdata(Long id, String scriptData, long transNo,
            long origTransNo, long origBatchNo, String origRefNo,
            String origAuthCode, String origDate, String origAmount, String origF62,
            String datetime, String termID, String merchID) {
        this.id = id;
        this.scriptData = scriptData;
        this.transNo = transNo;
        this.origTransNo = origTransNo;
        this.origBatchNo = origBatchNo;
        this.origRefNo = origRefNo;
        this.origAuthCode = origAuthCode;
        this.origDate = origDate;
        this.origAmount = origAmount;
        this.origF62 = origF62;
        this.datetime = datetime;
        this.termID = termID;
        this.merchID = merchID;
    }
    @Generated(hash = 1459212917)
    public ScriptTransdata() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getScriptData() {
        return this.scriptData;
    }
    public void setScriptData(String scriptData) {
        this.scriptData = scriptData;
    }
    public long getTransNo() {
        return this.transNo;
    }
    public void setTransNo(long transNo) {
        this.transNo = transNo;
    }
    public long getOrigTransNo() {
        return this.origTransNo;
    }
    public void setOrigTransNo(long origTransNo) {
        this.origTransNo = origTransNo;
    }
    public long getOrigBatchNo() {
        return this.origBatchNo;
    }
    public void setOrigBatchNo(long origBatchNo) {
        this.origBatchNo = origBatchNo;
    }
    public String getOrigRefNo() {
        return this.origRefNo;
    }
    public void setOrigRefNo(String origRefNo) {
        this.origRefNo = origRefNo;
    }
    public String getOrigAuthCode() {
        return this.origAuthCode;
    }
    public void setOrigAuthCode(String origAuthCode) {
        this.origAuthCode = origAuthCode;
    }
    public String getOrigDate() {
        return this.origDate;
    }
    public void setOrigDate(String origDate) {
        this.origDate = origDate;
    }
    public String getOrigAmount() {
        return this.origAmount;
    }
    public void setOrigAmount(String origAmount) {
        this.origAmount = origAmount;
    }
    public String getOrigF62() {
        return this.origF62;
    }
    public void setOrigF62(String origF62) {
        this.origF62 = origF62;
    }
    public String getDatetime() {
        return this.datetime;
    }
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
    public String getTermID() {
        return this.termID;
    }
    public void setTermID(String termID) {
        this.termID = termID;
    }
    public String getMerchID() {
        return this.merchID;
    }
    public void setMerchID(String merchID) {
        this.merchID = merchID;
    }

    
}
