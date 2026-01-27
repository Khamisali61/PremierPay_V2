package com.topwise.premierpay.daoutils.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 创建日期：2021/5/28 on 13:57
 * 描述:
 * 作者:wangweicheng
 */
@Entity
public class TotaTransdata {
    @Id
    private Long id;

    /**
     * Merchant No
     * 商户号
     */
    private String merchantID;
    /**
     * terminal No
     * 终端号
     */
    private String terminalID;
    /**
     * operator
     * 操作员号
     */
    private String operatorID;
    /**
     * batch
     * 批次号
     */
    private String batchNo;

    /**
     * 时间日期
     */
    private String datetime;
    //bank card ============
    
    private Long bankNumberTotal;
    private Long bankAmountTotal;

    private Long bankSaleNumberTotal;
    private Long bankSaleAmountTotal;

    private Long bankVoidNumberTotal;
    private Long bankVoidAmountTotal;

    private Long bankRefundNumberTotal;
    private Long bankRefundAmountTotal;
    //bank card end ============

    private Long qrNumberTotal;
    private Long qrAmountTotal;

    private Long qrSaleNumberTotal;
    private Long qrSaleAmountTotal;

    private Long qrVoidNumberTotal;
    private Long qrVoidAmountTotal;

    private Long qrRefundNumberTotal;
    private Long qrRefundAmountTotal;

    //Auth
    private Long authCmdNumberTotal;
    private Long authCmdAmountTotal;
    @Generated(hash = 151904548)
    public TotaTransdata(Long id, String merchantID, String terminalID,
            String operatorID, String batchNo, String datetime,
            Long bankNumberTotal, Long bankAmountTotal, Long bankSaleNumberTotal,
            Long bankSaleAmountTotal, Long bankVoidNumberTotal,
            Long bankVoidAmountTotal, Long bankRefundNumberTotal,
            Long bankRefundAmountTotal, Long qrNumberTotal, Long qrAmountTotal,
            Long qrSaleNumberTotal, Long qrSaleAmountTotal, Long qrVoidNumberTotal,
            Long qrVoidAmountTotal, Long qrRefundNumberTotal,
            Long qrRefundAmountTotal, Long authCmdNumberTotal,
            Long authCmdAmountTotal) {
        this.id = id;
        this.merchantID = merchantID;
        this.terminalID = terminalID;
        this.operatorID = operatorID;
        this.batchNo = batchNo;
        this.datetime = datetime;
        this.bankNumberTotal = bankNumberTotal;
        this.bankAmountTotal = bankAmountTotal;
        this.bankSaleNumberTotal = bankSaleNumberTotal;
        this.bankSaleAmountTotal = bankSaleAmountTotal;
        this.bankVoidNumberTotal = bankVoidNumberTotal;
        this.bankVoidAmountTotal = bankVoidAmountTotal;
        this.bankRefundNumberTotal = bankRefundNumberTotal;
        this.bankRefundAmountTotal = bankRefundAmountTotal;
        this.qrNumberTotal = qrNumberTotal;
        this.qrAmountTotal = qrAmountTotal;
        this.qrSaleNumberTotal = qrSaleNumberTotal;
        this.qrSaleAmountTotal = qrSaleAmountTotal;
        this.qrVoidNumberTotal = qrVoidNumberTotal;
        this.qrVoidAmountTotal = qrVoidAmountTotal;
        this.qrRefundNumberTotal = qrRefundNumberTotal;
        this.qrRefundAmountTotal = qrRefundAmountTotal;
        this.authCmdNumberTotal = authCmdNumberTotal;
        this.authCmdAmountTotal = authCmdAmountTotal;
    }
    @Generated(hash = 44915409)
    public TotaTransdata() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getMerchantID() {
        return this.merchantID;
    }
    public void setMerchantID(String merchantID) {
        this.merchantID = merchantID;
    }
    public String getTerminalID() {
        return this.terminalID;
    }
    public void setTerminalID(String terminalID) {
        this.terminalID = terminalID;
    }
    public String getOperatorID() {
        return this.operatorID;
    }
    public void setOperatorID(String operatorID) {
        this.operatorID = operatorID;
    }
    public String getBatchNo() {
        return this.batchNo;
    }
    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }
    public String getDatetime() {
        return this.datetime;
    }
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
    public Long getBankNumberTotal() {
        return this.bankNumberTotal;
    }
    public void setBankNumberTotal(Long bankNumberTotal) {
        this.bankNumberTotal = bankNumberTotal;
    }
    public Long getBankAmountTotal() {
        return this.bankAmountTotal;
    }
    public void setBankAmountTotal(Long bankAmountTotal) {
        this.bankAmountTotal = bankAmountTotal;
    }
    public Long getBankSaleNumberTotal() {
        return this.bankSaleNumberTotal;
    }
    public void setBankSaleNumberTotal(Long bankSaleNumberTotal) {
        this.bankSaleNumberTotal = bankSaleNumberTotal;
    }
    public Long getBankSaleAmountTotal() {
        return this.bankSaleAmountTotal;
    }
    public void setBankSaleAmountTotal(Long bankSaleAmountTotal) {
        this.bankSaleAmountTotal = bankSaleAmountTotal;
    }
    public Long getBankVoidNumberTotal() {
        return this.bankVoidNumberTotal;
    }
    public void setBankVoidNumberTotal(Long bankVoidNumberTotal) {
        this.bankVoidNumberTotal = bankVoidNumberTotal;
    }
    public Long getBankVoidAmountTotal() {
        return this.bankVoidAmountTotal;
    }
    public void setBankVoidAmountTotal(Long bankVoidAmountTotal) {
        this.bankVoidAmountTotal = bankVoidAmountTotal;
    }
    public Long getBankRefundNumberTotal() {
        return this.bankRefundNumberTotal;
    }
    public void setBankRefundNumberTotal(Long bankRefundNumberTotal) {
        this.bankRefundNumberTotal = bankRefundNumberTotal;
    }
    public Long getBankRefundAmountTotal() {
        return this.bankRefundAmountTotal;
    }
    public void setBankRefundAmountTotal(Long bankRefundAmountTotal) {
        this.bankRefundAmountTotal = bankRefundAmountTotal;
    }
    public Long getQrNumberTotal() {
        return this.qrNumberTotal;
    }
    public void setQrNumberTotal(Long qrNumberTotal) {
        this.qrNumberTotal = qrNumberTotal;
    }
    public Long getQrAmountTotal() {
        return this.qrAmountTotal;
    }
    public void setQrAmountTotal(Long qrAmountTotal) {
        this.qrAmountTotal = qrAmountTotal;
    }
    public Long getQrSaleNumberTotal() {
        return this.qrSaleNumberTotal;
    }
    public void setQrSaleNumberTotal(Long qrSaleNumberTotal) {
        this.qrSaleNumberTotal = qrSaleNumberTotal;
    }
    public Long getQrSaleAmountTotal() {
        return this.qrSaleAmountTotal;
    }
    public void setQrSaleAmountTotal(Long qrSaleAmountTotal) {
        this.qrSaleAmountTotal = qrSaleAmountTotal;
    }
    public Long getQrVoidNumberTotal() {
        return this.qrVoidNumberTotal;
    }
    public void setQrVoidNumberTotal(Long qrVoidNumberTotal) {
        this.qrVoidNumberTotal = qrVoidNumberTotal;
    }
    public Long getQrVoidAmountTotal() {
        return this.qrVoidAmountTotal;
    }
    public void setQrVoidAmountTotal(Long qrVoidAmountTotal) {
        this.qrVoidAmountTotal = qrVoidAmountTotal;
    }
    public Long getQrRefundNumberTotal() {
        return this.qrRefundNumberTotal;
    }
    public void setQrRefundNumberTotal(Long qrRefundNumberTotal) {
        this.qrRefundNumberTotal = qrRefundNumberTotal;
    }
    public Long getQrRefundAmountTotal() {
        return this.qrRefundAmountTotal;
    }
    public void setQrRefundAmountTotal(Long qrRefundAmountTotal) {
        this.qrRefundAmountTotal = qrRefundAmountTotal;
    }
    public Long getAuthCmdNumberTotal() {
        return this.authCmdNumberTotal;
    }
    public void setAuthCmdNumberTotal(Long authCmdNumberTotal) {
        this.authCmdNumberTotal = authCmdNumberTotal;
    }
    public Long getAuthCmdAmountTotal() {
        return this.authCmdAmountTotal;
    }
    public void setAuthCmdAmountTotal(Long authCmdAmountTotal) {
        this.authCmdAmountTotal = authCmdAmountTotal;
    }
}
