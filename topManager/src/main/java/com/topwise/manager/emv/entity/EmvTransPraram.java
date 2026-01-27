package com.topwise.manager.emv.entity;


/**
 * 创建日期：2021/6/15 on 19:38
 * 描述:
 * 作者:wangweicheng
 */
public class EmvTransPraram  {
    private byte transType;
    private long amount;
    private long amountOther;
    private long transNo;
    private String aucTransDate;
    private String aucTransTime;
    private String aucUnNumber;
    private String aucTransCurCode;
    private boolean bSupSimpleProc; //Is it a simple process
    private boolean clssForceOnlinePin;//Force password  ture yes
    private boolean bSup2GAC ;//

    private String mcc;
    public boolean isbSup2GAC() {
        return bSup2GAC;
    }

    public void setbSup2GAC(boolean bSup2GAC) {
        this.bSup2GAC = bSup2GAC;
    }

    public boolean isbSupSimpleProc() {
        return bSupSimpleProc;
    }

    public void setbSupSimpleProc(boolean bSupSimpleProc) {
        this.bSupSimpleProc = bSupSimpleProc;
    }

    public boolean isClssForceOnlinePin() {
        return clssForceOnlinePin;
    }

    public void setClssForceOnlinePin(boolean clssForceOnlinePin) {
        this.clssForceOnlinePin = clssForceOnlinePin;
    }

    /**
     * default
     * @param transType
     */
    public EmvTransPraram(byte transType) {
        this.transType = transType;
        this.bSupSimpleProc = false;
        this.clssForceOnlinePin = false;
        this.bSup2GAC = true;
    }

    public byte getTransType() {
        return transType;
    }

    public void setTransType(byte transType) {
        this.transType = transType;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getAmountOther() {
        return amountOther;
    }

    public void setAmountOther(long amountOther) {
        this.amountOther = amountOther;
    }

    public String getAucTransDate() {
        return aucTransDate;
    }

    public void setAucTransDate(String transData) {
        this.aucTransDate = transData;
    }

    public String getAucTransTime() {
        return aucTransTime;
    }

    public void setAucTransTime(String aucTransTime) {
        this.aucTransTime = aucTransTime;
    }

    public String getAucUnNumber() {
        return aucUnNumber;
    }

    public void setAucUnNumber(String aucUnNumber) {
        this.aucUnNumber = aucUnNumber;
    }

    public String getAucTransCurCode() {
        return aucTransCurCode;
    }

    public void setAucTransCurCode(String aucTransCurCode) {
        this.aucTransCurCode = aucTransCurCode;
    }

    public long getTransNo() {
        return transNo;
    }

    public void setTransNo(long transNo) {
        this.transNo = transNo;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    @Override
    public String toString() {
        return "EmvTransPraram{" +
                "transType=" + transType +
                ", amount=" + amount +
                ", amountOther=" + amountOther +
                ", transNo=" + transNo +
                ", aucTransDate='" + aucTransDate + '\'' +
                ", aucTransTime='" + aucTransTime + '\'' +
                ", aucUnNumber='" + aucUnNumber + '\'' +
                ", aucTransCurCode='" + aucTransCurCode + '\'' +
                ", bSupSimpleProc=" + bSupSimpleProc +
                ", clssForceOnlinePin=" + clssForceOnlinePin +
                ", bSup2GAC=" + bSup2GAC +
                ", mcc='" + mcc + '\'' +
                '}';
    }
}
