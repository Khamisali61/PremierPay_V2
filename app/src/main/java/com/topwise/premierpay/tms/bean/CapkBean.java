package com.topwise.premierpay.tms.bean;

public class CapkBean {
    private String KeyID;
    private String RID;
    private String Modulus;
    private String Exponent;
    private String CheckSum;
    private String HashIndex;
    private String ArithIndex;
    private String ExpDate;

    public String getKeyID() {
        return KeyID;
    }

    public void setKeyID(String keyID) {
        this.KeyID = keyID;
    }

    public String getRID() {
        return RID;
    }

    public void setRID(String RID) {
        this.RID = RID;
    }

    public String getModulus() {
        return Modulus;
    }

    public void setModulus(String modulus) {
        Modulus = modulus;
    }

    public String getExponent() {
        return Exponent;
    }

    public void setExponent(String exponent) {
        Exponent = exponent;
    }

    public String getCheckSum() {
        return CheckSum;
    }

    public void setCheckSum(String checkSum) {
        CheckSum = checkSum;
    }

    public String getHashIndex() {
        return HashIndex;
    }

    public void setHashIndex(String hashIndex) {
        HashIndex = hashIndex;
    }

    public String getArithIndex() {
        return ArithIndex;
    }

    public void setArithIndex(String arithIndex) {
        ArithIndex = arithIndex;
    }

    public String getExpDate() {
        return ExpDate;
    }

    public void setExpDate(String expDate) {
        ExpDate = expDate;
    }

    @Override
    public String toString() {
        return "CapkBean{" +
                "KeyID='" + KeyID + '\'' +
                ", RID='" + RID + '\'' +
                ", Modulus='" + Modulus + '\'' +
                ", Exponent='" + Exponent + '\'' +
                ", CheckSum='" + CheckSum + '\'' +
                ", HashIndex='" + HashIndex + '\'' +
                ", ArithIndex='" + ArithIndex + '\'' +
                ", ExpDate='" + ExpDate + '\'' +
                '}';
    }
}
