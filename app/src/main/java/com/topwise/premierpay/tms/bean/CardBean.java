package com.topwise.premierpay.tms.bean;

public class CardBean {
    private String CardID;
    private String CardDisplayName;
    private String IssuerName;
    private String PanLength;
    private String RangeLow;
    private String RangeHigh;

    public String getCardID() {
        return CardID;
    }

    public void setCardID(String cardID) {
        CardID = cardID;
    }

    public String getCardDisplayName() {
        return CardDisplayName;
    }

    public void setCardDisplayName(String cardDisplayName) {
        CardDisplayName = cardDisplayName;
    }

    public String getIssuerName() {
        return IssuerName;
    }

    public void setIssuerName(String issuerName) {
        IssuerName = issuerName;
    }

    public String getPanLength() {
        return PanLength;
    }

    public void setPanLength(String panLength) {
        PanLength = panLength;
    }

    public String getRangeLow() {
        return RangeLow;
    }

    public void setRangeLow(String rangeLow) {
        RangeLow = rangeLow;
    }

    public String getRangeHigh() {
        return RangeHigh;
    }

    public void setRangeHigh(String rangeHigh) {
        RangeHigh = rangeHigh;
    }

    @Override
    public String toString() {
        return "CardBean{" +
                "CardID='" + CardID + '\'' +
                ", CardDisplayName='" + CardDisplayName + '\'' +
                ", IssuerName='" + IssuerName + '\'' +
                ", PanLength='" + PanLength + '\'' +
                ", RangeLow='" + RangeLow + '\'' +
                ", RangeHigh='" + RangeHigh + '\'' +
                '}';
    }
}
