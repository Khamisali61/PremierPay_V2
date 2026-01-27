package com.topwise.premierpay.trans.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

@Entity
public class TransStatusSum implements Serializable, Cloneable {
    static final long serialVersionUID = 42L;

    @Id
    private Long id =0L;
    /*******
     * 0:wifi ;1 :mobile;
     * ******/
    private int commType;
    private long connectTime;
    private long sendTime;
    private long receiveTime;
    private long packTime;
    private long unpackTime;
    private long commTime;
    private String singleValue;
    private int  result = -1;
    private String datetime;

    private int  connCount;

    @Transient
    private long wifiTotal ;


    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Transient

    private String beginTime;
    @Transient

    private String endTime;

    @Transient
    private long wifiSucc;
    @Transient
    private String wifiRate ="";

    @Transient
    private long mobileTotal;

    @Transient
    private long mobileSucc ;

    @Transient
    private String mobileRate ="";

    @Transient
    private long netTotal;

    @Transient
    private long netSucc;

    @Transient
    private String netTotalSuccRate ="";

    @Transient
    private long total;

    @Transient
    private long totalSucc;


    @Transient
    private String merchantID;
    @Transient
    private String terminalID;

    @Transient
    private String batchNo;

    @Transient
    private long cardFailCount;
    @Transient
    private String cardFailRate;


    @Transient
    private long netFailCount;
    @Transient
    private String netFailRate;

    @Transient
    private long unknownFailCount;
    @Transient
    private String unknownFailRate;



    @Transient
    private long totalFailCount;
    @Transient
    private String totalFailRate;

    @Generated(hash = 1146594149)
    public TransStatusSum(Long id, int commType, long connectTime, long sendTime,
            long receiveTime, long packTime, long unpackTime, long commTime,
            String singleValue, int result, String datetime, int connCount) {
        this.id = id;
        this.commType = commType;
        this.connectTime = connectTime;
        this.sendTime = sendTime;
        this.receiveTime = receiveTime;
        this.packTime = packTime;
        this.unpackTime = unpackTime;
        this.commTime = commTime;
        this.singleValue = singleValue;
        this.result = result;
        this.datetime = datetime;
        this.connCount = connCount;
    }

    @Generated(hash = 1756754626)
    public TransStatusSum() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCommType() {
        return commType;
    }

    public void setCommType(int commType) {
        this.commType = commType;
    }

    public long getConnectTime() {
        return connectTime;
    }

    public void setConnectTime(long connectTime) {
        this.connectTime = connectTime;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public long getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(long receiveTime) {
        this.receiveTime = receiveTime;
    }

    public long getPackTime() {
        return packTime;
    }

    public void setPackTime(long packTime) {
        this.packTime = packTime;
    }

    public long getUnpackTime() {
        return unpackTime;
    }

    public void setUnpackTime(long unpackTime) {
        this.unpackTime = unpackTime;
    }

    public long getCommTime() {
        return commTime;
    }

    public void setCommTime(long commTime) {
        this.commTime = commTime;
    }

    public String getSingleValue() {
        return singleValue;
    }

    public void setSingleValue(String singleValue) {
        this.singleValue = singleValue;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public long getWifiTotal() {
        return wifiTotal;
    }

    public void setWifiTotal(long wifiTotal) {
        this.wifiTotal = wifiTotal;
    }

    public long getWifiSucc() {
        return wifiSucc;
    }

    public void setWifiSucc(long wifiSucc) {
        this.wifiSucc = wifiSucc;
    }

    public String getWifiRate() {
        return wifiRate;
    }

    public void setWifiRate(String wifiRate) {
        this.wifiRate = wifiRate;
    }

    public long getMobileTotal() {
        return mobileTotal;
    }

    public void setMobileTotal(long mobileTotal) {
        this.mobileTotal = mobileTotal;
    }

    public long getMobileSucc() {
        return mobileSucc;
    }

    public void setMobileSucc(long mobileSucc) {
        this.mobileSucc = mobileSucc;
    }

    public String getMobileRate() {
        return mobileRate;
    }

    public void setMobileRate(String mobileRate) {
        this.mobileRate = mobileRate;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getTotalSucc() {
        return totalSucc;
    }

    public void setTotalSucc(long totalSucc) {
        this.totalSucc = totalSucc;
    }


    public String getMerchantID() {
        return merchantID;
    }

    public void setMerchantID(String merchantID) {
        this.merchantID = merchantID;
    }

    public String getTerminalID() {
        return terminalID;
    }

    public void setTerminalID(String terminalID) {
        this.terminalID = terminalID;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public int getConnCount() {
        return connCount;
    }

    public void setConnCount(int connCount) {
        this.connCount = connCount;
    }


    public String getCardFailRate() {
        return cardFailRate;
    }

    public void setCardFailRate(String cardFailRate) {
        this.cardFailRate = cardFailRate;
    }



    public void setNetFailCount(long netFailCount) {
        this.netFailCount = netFailCount;
    }

    public String getNetFailRate() {
        return netFailRate;
    }

    public void setNetFailRate(String netFailRate) {
        this.netFailRate = netFailRate;
    }



    public String getUnknownFailRate() {
        return unknownFailRate;
    }

    public void setUnknownFailRate(String unknownFailRate) {
        this.unknownFailRate = unknownFailRate;
    }


    public String getTotalFailRate() {
        return totalFailRate;
    }

    public void setTotalFailRate(String totalFailRate) {
        this.totalFailRate = totalFailRate;
    }

    public long getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(long netTotal) {
        this.netTotal = netTotal;
    }

    public long getNetSucc() {
        return netSucc;
    }

    public void setNetSucc(long netSucc) {
        this.netSucc = netSucc;
    }

    public String getNetTotalSuccRate() {
        return netTotalSuccRate;
    }

    public void setNetTotalSuccRate(String netTotalSuccRate) {
        this.netTotalSuccRate = netTotalSuccRate;
    }

    public long getCardFailCount() {
        return cardFailCount;
    }

    public void setCardFailCount(long cardFailCount) {
        this.cardFailCount = cardFailCount;
    }

    public long getNetFailCount() {
        return netFailCount;
    }

    public long getUnknownFailCount() {
        return unknownFailCount;
    }

    public void setUnknownFailCount(long unknownFailCount) {
        this.unknownFailCount = unknownFailCount;
    }

    public long getTotalFailCount() {
        return totalFailCount;
    }

    public void setTotalFailCount(long totalFailCount) {
        this.totalFailCount = totalFailCount;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }
}
