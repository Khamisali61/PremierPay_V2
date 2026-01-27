package com.topwise.premierpay.tms.bean;

import java.util.List;

public class ParameterBean {
    private String merchantID;
    private String terminalID;
    private String countryCode;
    private String currencyCode;
    private String merchantName;
    private String merchantLocation;
    private String merchantCity;
    private String merchantState;
    private String merchantCountry;
    private String mcc;
    private boolean saleEnable=true;
    private boolean authEnable=true;
    private boolean refundEnable=true;
    private boolean pinPreAuthVoid;
    private boolean cardPreAuthVoid;
    private boolean manualKeyEnable=true;
    private boolean magStripeEnable=false;
    private boolean signature=false;
    private String securityPassword;
    private String adminPassword;
    private String supervisorPassword;
    private String ip;
    private String port;
    private String reversalControl;
    private String terminalFloorLmt;
    private String terminalTransactionLmt;
    private String terminalCvmLmt;
    private String serialNo;
    private String batchNo;

    private List<CapkBean> CAPKS;

    private List<AidBean> AIDS;

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

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String value) { this.countryCode = value; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String value) { this.currencyCode = value; }

    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String value) { this.merchantName = value; }
    public boolean getSaleEnable() { return saleEnable; }
    public void setSaleEnable(boolean value) { this.saleEnable = value; }

    public boolean getAuthEnable() { return authEnable; }
    public void setAuthEnable(boolean value) { this.authEnable = value; }

    public boolean getRefundEnable() { return refundEnable; }
    public void setRefundEnable(boolean value) { this.refundEnable = value; }

    public boolean getManualKeyEnable() { return manualKeyEnable; }
    public void setManualKeyEnable(boolean value) { this.manualKeyEnable = value; }

    public boolean getMagStripeEnable() { return magStripeEnable; }
    public void setMagStripeEnable(boolean value) { this.magStripeEnable = value; }

    public List<CapkBean> getCapks() { return CAPKS; }
    public void setCapks(List<CapkBean> value) { this.CAPKS = value; }

    public List<AidBean> getAids() { return AIDS; }
    public void setAids(List<AidBean> value) { this.AIDS = value; }

    public String getMerchantLocation() {
        return merchantLocation;
    }

    public void setMerchantLocation(String merchantLocation) {
        this.merchantLocation = merchantLocation;
    }

    public String getMerchantCity() {
        return merchantCity;
    }

    public void setMerchantCity(String merchantCity) {
        this.merchantCity = merchantCity;
    }

    public String getMerchantState() {
        return merchantState;
    }

    public void setMerchantState(String merchantState) {
        this.merchantState = merchantState;
    }

    public String getMerchantCountry() {
        return merchantCountry;
    }

    public void setMerchantCountry(String merchantCountry) {
        this.merchantCountry = merchantCountry;
    }

    public boolean isPinPreAuthVoid() {
        return pinPreAuthVoid;
    }

    public void setPinPreAuthVoid(boolean pinPreAuthVoid) {
        this.pinPreAuthVoid = pinPreAuthVoid;
    }

    public boolean isCardPreAuthVoid() {
        return cardPreAuthVoid;
    }

    public void setCardPreAuthVoid(boolean cardPreAuthVoid) {
        this.cardPreAuthVoid = cardPreAuthVoid;
    }

    public boolean isSignature() {
        return signature;
    }

    public void setSignature(boolean signature) {
        this.signature = signature;
    }

    public String getSecurityPassword() {
        return securityPassword;
    }

    public void setSecurityPassword(String securityPassword) {
        this.securityPassword = securityPassword;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getSupervisorPassword() {
        return supervisorPassword;
    }

    public void setSupervisorPassword(String supervisorPassword) {
        this.supervisorPassword = supervisorPassword;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getReversalControl() {
        return reversalControl;
    }

    public void setReversalControl(String reversalControl) {
        this.reversalControl = reversalControl;
    }

    public String getTerminalCvmLmt() {
        return terminalCvmLmt;
    }

    public void setTerminalCvmLmt(String terminalCvmLmt) {
        this.terminalCvmLmt = terminalCvmLmt;
    }

    public String getTerminalTransactionLmt() {
        return terminalTransactionLmt;
    }

    public void setTerminalTransactionLmt(String terminalTransactionLmt) {
        this.terminalTransactionLmt = terminalTransactionLmt;
    }

    public String getTerminalFloorLmt() {
        return terminalFloorLmt;
    }

    public void setTerminalFloorLmt(String terminalFloorLmt) {
        this.terminalFloorLmt = terminalFloorLmt;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    @Override
    public String toString() {
        return "ParameterBean{" +
                "merchantID='" + merchantID + '\'' +
                ", terminalID='" + terminalID + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", merchantLocation='" + merchantLocation + '\'' +
                ", merchantCity='" + merchantCity + '\'' +
                ", merchantState='" + merchantState + '\'' +
                ", merchantCountry='" + merchantCountry + '\'' +
                ", mcc='" + mcc + '\'' +
                ", saleEnable=" + saleEnable +
                ", authEnable=" + authEnable +
                ", refundEnable=" + refundEnable +
                ", pinPreAuthVoid=" + pinPreAuthVoid +
                ", cardPreAuthVoid=" + cardPreAuthVoid +
                ", manualKeyEnable=" + manualKeyEnable +
                ", magStripeEnable=" + magStripeEnable +
                ", signature=" + signature +
                ", securityPassword='" + securityPassword + '\'' +
                ", adminPassword='" + adminPassword + '\'' +
                ", supervisorPassword='" + supervisorPassword + '\'' +
                ", ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                ", reversalControl='" + reversalControl + '\'' +
                ", terminalFloorLmt='" + terminalFloorLmt + '\'' +
                ", terminalTransactionLmt='" + terminalTransactionLmt + '\'' +
                ", terminalCvmLmt='" + terminalCvmLmt + '\'' +
                ", serialNo='" + serialNo + '\'' +
                ", batchNo='" + batchNo + '\'' +
                ", CAPKS=" + CAPKS +
                ", AIDS=" + AIDS +
                '}';
    }
}
