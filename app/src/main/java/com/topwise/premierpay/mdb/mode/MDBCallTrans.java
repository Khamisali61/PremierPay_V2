package com.topwise.premierpay.mdb.mode;

/**
 * author: hubeiye
 * created on: 2025/4/11 下午3:57
 * description:
 */
public class MDBCallTrans {
/**
 * isNeedPrintReceipt : false
 * lsOrderNo : 1234567809012
 * orderNo : 1234567809012
 * counterNo : 1234
 * projectTag : TOPWISEMISAPP
 * amt : 000000000001
 */

public static final String TRANS_TYPE_SALE = "sale";
public static final String TRANS_TYPE_REVOKE = "revoke";
public static final String TRANS_TYPE_CANCEL = "cancel";
public static final int RESULT_SUCCESS_CODE = 0;
public static final int RESULT_FAIL_CODE = -1;

public int getResultCode() {
        return resultCode;
        }

public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
        }

private int resultCode; // 0:success -1:fail
private String orderNo;
private String amt;
private String transType; // Sale Revoke Cancel

public String getTransType() {
        return transType;
        }

public void setTransType(String transType) {
        this.transType = transType;
        }


public String getOrderNo() {
        return orderNo;
        }

public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
        }

public String getAmt() {
        return amt;
        }

public void setAmt(String amt) {
        this.amt = amt;
        }
        }
