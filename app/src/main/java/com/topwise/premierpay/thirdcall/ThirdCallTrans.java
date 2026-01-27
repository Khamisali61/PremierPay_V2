package com.topwise.premierpay.thirdcall;

public class ThirdCallTrans {
    /**
     * isNeedPrintReceipt : false
     * lsOrderNo : 1234567809012
     * orderNo : 1234567809012
     * counterNo : 1234
     * projectTag : TOPWISEMISAPP
     * amt : 000000000001
     */

    public boolean isNeedPrintReceipt;
    public String lsOrderNo;
    public String orderNo;
    public String counterNo;
    public String projectTag;
    public String amt;

    public boolean getIsNeedPrintReceipt() {
        return isNeedPrintReceipt;
    }

    public void setIsNeedPrintReceipt(boolean isNeedPrintReceipt) {
        this.isNeedPrintReceipt = isNeedPrintReceipt;
    }

    public String getLsOrderNo() {
        return lsOrderNo;
    }

    public void setLsOrderNo(String lsOrderNo) {
        this.lsOrderNo = lsOrderNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getCounterNo() {
        return counterNo;
    }

    public void setCounterNo(String counterNo) {
        this.counterNo = counterNo;
    }

    public String getProjectTag() {
        return projectTag;
    }

    public void setProjectTag(String projectTag) {
        this.projectTag = projectTag;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }
}
