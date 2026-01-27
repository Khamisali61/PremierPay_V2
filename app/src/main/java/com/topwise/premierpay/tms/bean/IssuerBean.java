package com.topwise.premierpay.tms.bean;
public class IssuerBean {
    private String IssuerId;
    private String IssuerName;
    private String ExpiredMask;
    private String AmountForContact;
    private String AmountForMag;
    private String AmountForContactless;
    private boolean IssuerEnabled;

    public String getIssuerId() {
        return IssuerId;
    }

    public void setIssuerId(String issuerId) {
        IssuerId = issuerId;
    }

    public String getIssuerName() {
        return IssuerName;
    }

    public void setIssuerName(String issuerName) {
        IssuerName = issuerName;
    }

    public String getExpiredMask() {
        return ExpiredMask;
    }

    public void setExpiredMask(String expiredMask) {
        ExpiredMask = expiredMask;
    }

    public String getAmountForContact() {
        return AmountForContact;
    }

    public void setAmountForContact(String amountForContact) {
        AmountForContact = amountForContact;
    }

    public String getAmountForMag() {
        return AmountForMag;
    }

    public void setAmountForMag(String amountForMag) {
        AmountForMag = amountForMag;
    }

    public String getAmountForContactless() {
        return AmountForContactless;
    }

    public void setAmountForContactless(String amountForContactless) {
        AmountForContactless = amountForContactless;
    }

    public boolean isIssuerEnabled() {
        return IssuerEnabled;
    }

    public void setIssuerEnabled(boolean issuerEnabled) {
        IssuerEnabled = issuerEnabled;
    }

    @Override
    public String toString() {
        return "IssuerBean{" +
                "IssuerId='" + IssuerId + '\'' +
                ", IssuerName='" + IssuerName + '\'' +
                ", ExpiredMask='" + ExpiredMask + '\'' +
                ", AmountForContact='" + AmountForContact + '\'' +
                ", AmountForMag='" + AmountForMag + '\'' +
                ", AmountForContactless='" + AmountForContactless + '\'' +
                ", IssuerEnabled=" + IssuerEnabled +
                '}';
    }
}
