package com.topwise.premierpay.daoutils.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 创建日期：2021/4/8 on 16:49
 * 描述: 冲正表
 * 作者:  wangweicheng
 */
@Entity
public class DupTransdata implements Serializable {
    static final long serialVersionUID = 42L;
    @Id
    private Long id;

    private String orderNo; // 订单号	使用交易流水号接口获取
    private String orderNodis; // pos订单号 备注信息
    private String amount; // 交易金额  //单位是元 a
    private String tipAmount; // 小费金额
    private String balance; // 余额
    private String balanceFlag; // 余额标识C/D
    private long transNo; // pos流水号
    private long origTransNo; // 原pos流水号
    private long batchNo; // 批次号
    private String transType; // 交易类型
    private String merchID;

    private String origProcCode; //原消息码
    private String qrCode; //二维码数据
    private String origQrCode; //原二维码数据
    private String qrVoucher;
    private String origQrVoucher;


    private String transState; // 交易状态
    private String oper;
    private String track1; // 磁道一信息
    private String track2; // 磁道二数据
    private String track3; // 磁道三数据
    private boolean isEncTrack; // 磁道是否加密
    private String reason; // 冲正原因
    private String reserved; // 63域附加域
    private String datetime; // 交易时间
    private String time; // 交易时间
    private String date; // 交易日期
    private String termID;
    private String pan; // 主账号
    private String expDate; // 卡有效期
    private String cardSerialNo; // 23 域，卡片序列号
    private int enterMode; // 输入模式
    private boolean hasPin; // 是否有输密码
    private String sendIccData; // IC卡信息,55域
    private String dupIccData; // IC卡冲正信息,55域
    private String origDate; // 原交易日期
    private String ICPositiveData;
    private String tag9F5B;
    /**
     * 响应码
     */
    private String responseCode;
    /**
     * 相应码对应的错误信息
     */
    private String responseMsg;
    private String settleDate; // 清算日期
    private String acqCenterCode; // 受理方标识码,pos中心号(返回包时用)
    private String refNo; // 系统参考号
    private String origRefNo; // 原系统参考号
    private String authCode; // 授权码
    private String origAuthCode; // 原授权码
    private String tc; // IC卡交易证书(TC值)tag9f26,(BIN)
    private String arqc; // 授权请求密文(ARQC)
    private String arpc; // 授权响应密文(ARPC)
    private String tvr; // 终端验证结果(TVR)值tag95
    private String aid; // 应用标识符AID
    private String emvAppLabel; // 应用标签
    private String emvAppName; // 应用首选名称
    private String tsi; // 交易状态信息(TSI)tag9B
    private String atc; // 应用交易计数器(ATC)值tag9f36
    private String field22; //22域
    private String field62; //22域

    @Generated(hash = 2117309074)
    public DupTransdata(Long id, String orderNo, String orderNodis, String amount,
            String tipAmount, String balance, String balanceFlag, long transNo,
            long origTransNo, long batchNo, String transType, String merchID,
            String origProcCode, String qrCode, String origQrCode, String qrVoucher,
            String origQrVoucher, String transState, String oper, String track1,
            String track2, String track3, boolean isEncTrack, String reason,
            String reserved, String datetime, String time, String date,
            String termID, String pan, String expDate, String cardSerialNo,
            int enterMode, boolean hasPin, String sendIccData, String dupIccData,
            String origDate, String ICPositiveData, String tag9F5B,
            String responseCode, String responseMsg, String settleDate,
            String acqCenterCode, String refNo, String origRefNo, String authCode,
            String origAuthCode, String tc, String arqc, String arpc, String tvr,
            String aid, String emvAppLabel, String emvAppName, String tsi,
            String atc, String field22, String field62) {
        this.id = id;
        this.orderNo = orderNo;
        this.orderNodis = orderNodis;
        this.amount = amount;
        this.tipAmount = tipAmount;
        this.balance = balance;
        this.balanceFlag = balanceFlag;
        this.transNo = transNo;
        this.origTransNo = origTransNo;
        this.batchNo = batchNo;
        this.transType = transType;
        this.merchID = merchID;
        this.origProcCode = origProcCode;
        this.qrCode = qrCode;
        this.origQrCode = origQrCode;
        this.qrVoucher = qrVoucher;
        this.origQrVoucher = origQrVoucher;
        this.transState = transState;
        this.oper = oper;
        this.track1 = track1;
        this.track2 = track2;
        this.track3 = track3;
        this.isEncTrack = isEncTrack;
        this.reason = reason;
        this.reserved = reserved;
        this.datetime = datetime;
        this.time = time;
        this.date = date;
        this.termID = termID;
        this.pan = pan;
        this.expDate = expDate;
        this.cardSerialNo = cardSerialNo;
        this.enterMode = enterMode;
        this.hasPin = hasPin;
        this.sendIccData = sendIccData;
        this.dupIccData = dupIccData;
        this.origDate = origDate;
        this.ICPositiveData = ICPositiveData;
        this.tag9F5B = tag9F5B;
        this.responseCode = responseCode;
        this.responseMsg = responseMsg;
        this.settleDate = settleDate;
        this.acqCenterCode = acqCenterCode;
        this.refNo = refNo;
        this.origRefNo = origRefNo;
        this.authCode = authCode;
        this.origAuthCode = origAuthCode;
        this.tc = tc;
        this.arqc = arqc;
        this.arpc = arpc;
        this.tvr = tvr;
        this.aid = aid;
        this.emvAppLabel = emvAppLabel;
        this.emvAppName = emvAppName;
        this.tsi = tsi;
        this.atc = atc;
        this.field22 = field22;
        this.field62 = field62;
    }

    @Generated(hash = 2099557796)
    public DupTransdata() {
    }

    public String getOrigDate() {
        return origDate;
    }

    public String getField22() {
        return field22;
    }

    public void setField22(String field22) {
        this.field22 = field22;
    }

    public void setOrigDate(String origDate) {
        this.origDate = origDate;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderNodis() {
        return orderNodis;
    }

    public void setOrderNodis(String orderNodis) {
        this.orderNodis = orderNodis;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(String tipAmount) {
        this.tipAmount = tipAmount;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getBalanceFlag() {
        return balanceFlag;
    }

    public void setBalanceFlag(String balanceFlag) {
        this.balanceFlag = balanceFlag;
    }

    public long getTransNo() {
        return transNo;
    }

    public void setTransNo(long transNo) {
        this.transNo = transNo;
    }

    public long getOrigTransNo() {
        return origTransNo;
    }

    public void setOrigTransNo(long origTransNo) {
        this.origTransNo = origTransNo;
    }

    public long getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(long batchNo) {
        this.batchNo = batchNo;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getMerchID() {
        return merchID;
    }

    public void setMerchID(String merchID) {
        this.merchID = merchID;
    }

    public String getTransState() {
        return transState;
    }

    public void setTransState(String transState) {
        this.transState = transState;
    }

    public String getOper() {
        return oper;
    }

    public void setOper(String oper) {
        this.oper = oper;
    }

    public String getTrack1() {
        return track1;
    }

    public void setTrack1(String track1) {
        this.track1 = track1;
    }

    public String getTrack2() {
        return track2;
    }

    public void setTrack2(String track2) {
        this.track2 = track2;
    }

    public String getTrack3() {
        return track3;
    }

    public void setTrack3(String track3) {
        this.track3 = track3;
    }

    public boolean isEncTrack() {
        return isEncTrack;
    }

    public void setEncTrack(boolean encTrack) {
        isEncTrack = encTrack;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReserved() {
        return reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTermID() {
        return termID;
    }

    public void setTermID(String termID) {
        this.termID = termID;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getCardSerialNo() {
        return cardSerialNo;
    }

    public void setCardSerialNo(String cardSerialNo) {
        this.cardSerialNo = cardSerialNo;
    }

    public int getEnterMode() {
        return enterMode;
    }

    public void setEnterMode(int enterMode) {
        this.enterMode = enterMode;
    }

    public boolean isHasPin() {
        return hasPin;
    }

    public void setHasPin(boolean hasPin) {
        this.hasPin = hasPin;
    }

    public String getSendIccData() {
        return sendIccData;
    }

    public void setSendIccData(String sendIccData) {
        this.sendIccData = sendIccData;
    }

    public String getDupIccData() {
        return dupIccData;
    }

    public void setDupIccData(String dupIccData) {
        this.dupIccData = dupIccData;
    }

    public String getICPositiveData() {
        return ICPositiveData;
    }

    public void setICPositiveData(String ICPositiveData) {
        this.ICPositiveData = ICPositiveData;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public String getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    public String getAcqCenterCode() {
        return acqCenterCode;
    }

    public void setAcqCenterCode(String acqCenterCode) {
        this.acqCenterCode = acqCenterCode;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public String getOrigRefNo() {
        return origRefNo;
    }

    public void setOrigRefNo(String origRefNo) {
        this.origRefNo = origRefNo;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getOrigAuthCode() {
        return origAuthCode;
    }

    public void setOrigAuthCode(String origAuthCode) {
        this.origAuthCode = origAuthCode;
    }

    public String getTc() {
        return tc;
    }

    public void setTc(String tc) {
        this.tc = tc;
    }

    public String getArqc() {
        return arqc;
    }

    public void setArqc(String arqc) {
        this.arqc = arqc;
    }

    public String getArpc() {
        return arpc;
    }

    public void setArpc(String arpc) {
        this.arpc = arpc;
    }

    public String getTvr() {
        return tvr;
    }

    public void setTvr(String tvr) {
        this.tvr = tvr;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getEmvAppLabel() {
        return emvAppLabel;
    }

    public void setEmvAppLabel(String emvAppLabel) {
        this.emvAppLabel = emvAppLabel;
    }

    public String getEmvAppName() {
        return emvAppName;
    }

    public void setEmvAppName(String emvAppName) {
        this.emvAppName = emvAppName;
    }

    public String getTsi() {
        return tsi;
    }

    public void setTsi(String tsi) {
        this.tsi = tsi;
    }

    public String getAtc() {
        return atc;
    }

    public void setAtc(String atc) {
        this.atc = atc;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getIsEncTrack() {
        return this.isEncTrack;
    }

    public void setIsEncTrack(boolean isEncTrack) {
        this.isEncTrack = isEncTrack;
    }

    public boolean getHasPin() {
        return this.hasPin;
    }

    public String getField62() {
        return this.field62;
    }

    public void setField62(String field62) {
        this.field62 = field62;
    }

    public String getTag9F5B() {
        return this.tag9F5B;
    }

    public void setTag9F5B(String tag9F5B) {
        this.tag9F5B = tag9F5B;
    }

    public String getOrigProcCode() {
        return this.origProcCode;
    }

    public void setOrigProcCode(String origProcCode) {
        this.origProcCode = origProcCode;
    }

    public String getQrCode() {
        return this.qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getOrigQrCode() {
        return this.origQrCode;
    }

    public void setOrigQrCode(String origQrCode) {
        this.origQrCode = origQrCode;
    }

    public String getQrVoucher() {
        return this.qrVoucher;
    }

    public void setQrVoucher(String qrVoucher) {
        this.qrVoucher = qrVoucher;
    }

    public String getOrigQrVoucher() {
        return this.origQrVoucher;
    }

    public void setOrigQrVoucher(String origQrVoucher) {
        this.origQrVoucher = origQrVoucher;
    }
}
