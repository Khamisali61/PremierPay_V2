package com.topwise.premierpay.tms.bean;

public class HostBean {
    private String AcquireID;
    private String AcquireName;
    private String TerminalID;
    private String MerchantID;
    private String AcquireInstitution;
    private String ConnectionTimeOut;
    private String ReceiveTimeOut;
    private String CommunicationMethod;
    private String NII;
    private boolean SSLEnable;
    private String IP;
    private String Port;
    private String BatchNumber;
    private String HostType;
    private String CurrencyCode;

    public String getAcquireID() {
        return AcquireID;
    }

    public void setAcquireID(String acquireID) {
        AcquireID = acquireID;
    }

    public String getAcquireName() {
        return AcquireName;
    }

    public void setAcquireName(String acquireName) {
        AcquireName = acquireName;
    }

    public String getTerminalID() {
        return TerminalID;
    }

    public void setTerminalID(String terminalID) {
        TerminalID = terminalID;
    }

    public String getMerchantID() {
        return MerchantID;
    }

    public void setMerchantID(String merchantID) {
        MerchantID = merchantID;
    }

    public String getAcquireInstitution() {
        return AcquireInstitution;
    }

    public void setAcquireInstitution(String acquireInstitution) {
        AcquireInstitution = acquireInstitution;
    }

    public String getConnectionTimeOut() {
        return ConnectionTimeOut;
    }

    public void setConnectionTimeOut(String connectionTimeOut) {
        ConnectionTimeOut = connectionTimeOut;
    }

    public String getReceiveTimeOut() {
        return ReceiveTimeOut;
    }

    public void setReceiveTimeOut(String receiveTimeOut) {
        ReceiveTimeOut = receiveTimeOut;
    }

    public String getCommunicationMethod() {
        return CommunicationMethod;
    }

    public void setCommunicationMethod(String communicationMethod) {
        CommunicationMethod = communicationMethod;
    }

    public String getNII() {
        return NII;
    }

    public void setNII(String NII) {
        this.NII = NII;
    }

    public boolean isSSLEnable() {
        return SSLEnable;
    }

    public void setSSLEnable(boolean SSLEnable) {
        this.SSLEnable = SSLEnable;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getPort() {
        return Port;
    }

    public void setPort(String port) {
        Port = port;
    }

    public String getBatchNumber() {
        return BatchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        BatchNumber = batchNumber;
    }

    public String getHostType() {
        return HostType;
    }

    public void setHostType(String hostType) {
        HostType = hostType;
    }

    public String getCurrencyCode() {
        return CurrencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        CurrencyCode = currencyCode;
    }

    @Override
    public String toString() {
        return "HostBean{" +
                "AcquireID='" + AcquireID + '\'' +
                ", AcquireName='" + AcquireName + '\'' +
                ", TerminalID='" + TerminalID + '\'' +
                ", MerchantID='" + MerchantID + '\'' +
                ", AcquireInstitution='" + AcquireInstitution + '\'' +
                ", ConnectionTimeOut='" + ConnectionTimeOut + '\'' +
                ", ReceiveTimeOut='" + ReceiveTimeOut + '\'' +
                ", CommunicationMethod='" + CommunicationMethod + '\'' +
                ", NII='" + NII + '\'' +
                ", SSLEnable=" + SSLEnable +
                ", IP='" + IP + '\'' +
                ", Port='" + Port + '\'' +
                ", BatchNumber='" + BatchNumber + '\'' +
                ", HostType='" + HostType + '\'' +
                ", CurrencyCode='" + CurrencyCode + '\'' +
                '}';
    }
}
