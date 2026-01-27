package com.topwise.premierpay.setting.country;

public class CurrencyBean {
    public String name;
    public String symbol;
    public String code;
    public String region;

    public void setName(String name) {
        this.name = name;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }


    public CurrencyBean(String name, String symbol, String code) {
        this.name = name;
        this.symbol = symbol;
        this.code = code;
    }
    public CurrencyBean() {

    }

    @Override
    public String toString() {
        return "CurrencyBean{" +
                "name='" + name + '\'' +
                ", symbol='" + symbol + '\'' +
                ", code='" + code + '\'' +
                ", region='" + region + '\'' +
                '}';
    }

    // Getters
    public String getName() { return name; }
    public String getSymbol() { return symbol; }
    public String getCode() { return code; }
}