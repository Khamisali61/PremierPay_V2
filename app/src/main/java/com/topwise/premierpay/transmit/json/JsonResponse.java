package com.topwise.premierpay.transmit.json;

public class JsonResponse {
    int retCode;
    String data;

    public int getRetCode() {
        return retCode;
    }

    public JsonResponse(int retCode, String data) {
        this.retCode = retCode;
        this.data = data;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
