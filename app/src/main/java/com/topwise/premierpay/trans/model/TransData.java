package com.topwise.premierpay.trans.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;

@Entity
public class TransData implements Serializable,Cloneable {
    static final long serialVersionUID = 42L;

    @Id
    private Long id;

    private String orderNo; // 订单号	使用交易流水号接口获取
    private String orderNodis; // pos订单号 备注信息
    private String amount; // 交易金额  //单位是元 a
    private String cardAmount; // 卡交易金额
    private String cashAmount; // 现金交易金额
    private String tipAmount; // 小费金额
    private String balance; // 余额
    private String balanceFlag; // 余额标识C/D

    //emv
    private byte emvResult; // EMV交易的执行状态
    private String interOrgCode; // 国际组织代码

    @Unique
    private long transNo; // pos流水号 trace number
    private long origTransNo; // 原pos流水号
    private long batchNo; // 批次号
    private long origBatchNo; // 原批次号
    private String transType; // 交易类型
    private String origTransType; // 原交易类型
    private String merchID;
    private String mcc;

    private boolean isUpload; // 是否已批上送
    private String transState; // 交易状态
    private String oper;
    private String track1; // 磁道一信息
    private String track2; // 磁道二数据
    private String track3; // 磁道三数据
    private boolean isEncTrack; // 磁道是否加密
    private String reason; // 冲正原因
    private String reserved; // 63域附加域
    private String datetime; // 交易时间
    private String UnNum; //随机数
    private String time; // 交易时间
    private String date; // 交易日期
    private String termID;
    private String pan; // 主账号
    private String expDate; // 卡有效期
    private String cardSerialNo; // 23 域，卡片序列号
    private int enterMode; // 输入模式
    private boolean hasPin; // 是否有输密码
    private String sendIccData; // IC卡信息,55域
    private String scriptTag; // IC卡信息tag 9F5B
    private String dupIccData; // IC卡冲正信息,55域
    private boolean isOnlineTrans; // 是否为联机交易
    private String origDate; // 原交易日期
    private String origTime; // 原交易日期
    private String isserCode; // 发卡行标识码
    private String acqCode; // 收单机构标识码
    private boolean isSupportBypass;

    private String issuerResp; // 发卡方保留域
    private String centerResp; // 中国银联保留域
    private String recvBankResp;// 受理机构保留域

//    private String TVR;
//    private String TSI;
    private String ICPositiveData;
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
    private String origProcCode; //原消息码
    private String qrCode; //二维码数据
    private String origQrCode; //原二维码数据
    private String qrVoucher;
    private String origQrVoucher;
    private String pinKsn; // pinKsn
    private String dataKsn; // dataKsn
    private String field52; //打印提示
    private String field58; //打印数据
    private String field22; //22域
    private String procCode; //消息码
    private String cardHolderName; //持卡人姓名

    private String ElecSignature; //电子签名数据
    //card type
//    public static final byte KERNTYPE_MC = 0x02;
//    public static final byte KERNTYPE_VISA = 0x03;
//    public static final byte KERNTYPE_AMEX = 0x04;
//    public static final byte KERNTYPE_JCB = 0x05;
//    public static final byte KERNTYPE_ZIP = 0x06; //Discover ZIP or 16
//    public static final byte KERNTYPE_DPAS = 0x06;//Discover DPAS
//    public static final byte KERNTYPE_QPBOC = 0x07;
//    public static final byte KERNTYPE_RUPAY = 0x0D;
//    public static final byte  KERNTYPE_PURE = 0x12; //add wwc

    private int kernelType; // 0-emv 2- MC 3 -VISA 4 AMEX 5-JCB 6-_ZIP 7-QPBOC  13 -RUPAY 18 -PURE

    private boolean needScript;
    private String tag71;
    private String tag72;
    private String tag8A;
    private String tag91;

    private String fingerprint;  //指纹

    @Transient
    private int transresult;
    @Transient //：该属性不会被存入数据库中。
    private String pin;
    @Transient
    private boolean isReversal;
    @Transient
    private String field3;
    @Transient
    private String field46;
    @Transient
    private String field48;
    @Transient
    private String field57;
    @Transient
    private String field60;
    @Transient
    private String field62;
    @Transient
    private String field63;
    @Transient
    private boolean isSM; // 是否支持国密
    @Transient
    private String recvIccData;
    @Transient
    private String header;
    @Transient
    private String tpdu;

    @Transient
    private boolean isNeedPrint = true;

    @Transient
    private boolean isStressTest;

    @Transient
    private TransStatusSum transStatusSum;

    @Generated(hash = 927652043)
    public TransData(Long id, String orderNo, String orderNodis, String amount, String cardAmount,
            String cashAmount, String tipAmount, String balance, String balanceFlag, byte emvResult,
            String interOrgCode, long transNo, long origTransNo, long batchNo, long origBatchNo,
            String transType, String origTransType, String merchID, String mcc, boolean isUpload,
            String transState, String oper, String track1, String track2, String track3,
            boolean isEncTrack, String reason, String reserved, String datetime, String UnNum,
            String time, String date, String termID, String pan, String expDate, String cardSerialNo,
            int enterMode, boolean hasPin, String sendIccData, String scriptTag, String dupIccData,
            boolean isOnlineTrans, String origDate, String origTime, String isserCode, String acqCode,
            boolean isSupportBypass, String issuerResp, String centerResp, String recvBankResp,
            String ICPositiveData, String responseCode, String responseMsg, String settleDate,
            String acqCenterCode, String refNo, String origRefNo, String authCode, String origAuthCode,
            String tc, String arqc, String arpc, String tvr, String aid, String emvAppLabel,
            String emvAppName, String tsi, String atc, String origProcCode, String qrCode,
            String origQrCode, String qrVoucher, String origQrVoucher, String pinKsn, String dataKsn,
            String field52, String field58, String field22, String procCode, String cardHolderName,
            String ElecSignature, int kernelType, boolean needScript, String tag71, String tag72,
            String tag8A, String tag91, String fingerprint) {
        this.id = id;
        this.orderNo = orderNo;
        this.orderNodis = orderNodis;
        this.amount = amount;
        this.cardAmount = cardAmount;
        this.cashAmount = cashAmount;
        this.tipAmount = tipAmount;
        this.balance = balance;
        this.balanceFlag = balanceFlag;
        this.emvResult = emvResult;
        this.interOrgCode = interOrgCode;
        this.transNo = transNo;
        this.origTransNo = origTransNo;
        this.batchNo = batchNo;
        this.origBatchNo = origBatchNo;
        this.transType = transType;
        this.origTransType = origTransType;
        this.merchID = merchID;
        this.mcc = mcc;
        this.isUpload = isUpload;
        this.transState = transState;
        this.oper = oper;
        this.track1 = track1;
        this.track2 = track2;
        this.track3 = track3;
        this.isEncTrack = isEncTrack;
        this.reason = reason;
        this.reserved = reserved;
        this.datetime = datetime;
        this.UnNum = UnNum;
        this.time = time;
        this.date = date;
        this.termID = termID;
        this.pan = pan;
        this.expDate = expDate;
        this.cardSerialNo = cardSerialNo;
        this.enterMode = enterMode;
        this.hasPin = hasPin;
        this.sendIccData = sendIccData;
        this.scriptTag = scriptTag;
        this.dupIccData = dupIccData;
        this.isOnlineTrans = isOnlineTrans;
        this.origDate = origDate;
        this.origTime = origTime;
        this.isserCode = isserCode;
        this.acqCode = acqCode;
        this.isSupportBypass = isSupportBypass;
        this.issuerResp = issuerResp;
        this.centerResp = centerResp;
        this.recvBankResp = recvBankResp;
        this.ICPositiveData = ICPositiveData;
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
        this.origProcCode = origProcCode;
        this.qrCode = qrCode;
        this.origQrCode = origQrCode;
        this.qrVoucher = qrVoucher;
        this.origQrVoucher = origQrVoucher;
        this.pinKsn = pinKsn;
        this.dataKsn = dataKsn;
        this.field52 = field52;
        this.field58 = field58;
        this.field22 = field22;
        this.procCode = procCode;
        this.cardHolderName = cardHolderName;
        this.ElecSignature = ElecSignature;
        this.kernelType = kernelType;
        this.needScript = needScript;
        this.tag71 = tag71;
        this.tag72 = tag72;
        this.tag8A = tag8A;
        this.tag91 = tag91;
        this.fingerprint = fingerprint;
    }

    @Generated(hash = 216917008)
    public TransData() {
    }

    public String getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(String cashAmount) {
        this.cashAmount = cashAmount;
    }

    public String getField22() {
        return field22;
    }

    public void setField22(String field22) {
        this.field22 = field22;
    }

    public String getOrigDate() {
        return origDate;
    }

    public void setOrigDate(String origDate) {
        this.origDate = origDate;
    }

    public boolean isOnlineTrans() {
        return isOnlineTrans;
    }

    public void setOnlineTrans(boolean onlineTrans) {
        isOnlineTrans = onlineTrans;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getField58() {
        return field58;
    }

    public void setField58(String field58) {
        this.field58 = field58;
    }

    public String getPinKsn() {
        return pinKsn;
    }

    public void setPinKsn(String pinKsn) {
        this.pinKsn = pinKsn;
    }

    public String getDataKsn() {
        return dataKsn;
    }

    public void setDataKsn(String dataKsn) {
        this.dataKsn = dataKsn;
    }

    public String getField46() {
        return field46;
    }

    public void setField46(String field46) {
        this.field46 = field46;
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

    public String getField63() {
        return field63;
    }

    public void setField63(String field63) {
        this.field63 = field63;
    }

    public String getRecvIccData() {
        return recvIccData;
    }

    public void setRecvIccData(String recvIccData) {
        this.recvIccData = recvIccData;
    }

    public String getField48() {
        return field48;
    }

    public void setField48(String field48) {
        this.field48 = field48;
    }

    public String getField60() {
        return field60;
    }

    public void setField60(String field60) {
        this.field60 = field60;
    }

    public String getField62() {
        return field62;
    }

    public void setField62(String field62) {
        this.field62 = field62;
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

    public String getCardSerialNo() {
        return cardSerialNo;
    }

    public void setCardSerialNo(String cardSerialNo) {
        this.cardSerialNo = cardSerialNo;
    }

    public boolean isSM() {
        return isSM;
    }

    public void setSM(boolean SM) {
        isSM = SM;
    }

    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }


    public String getICPositiveData() {
        return ICPositiveData;
    }

    public void setICPositiveData(String ICPositiveData) {
        this.ICPositiveData = ICPositiveData;
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

    public boolean isHasPin() {
        return hasPin;
    }

    public void setHasPin(boolean hasPin) {
        this.hasPin = hasPin;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
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

    public int getEnterMode() {
        return enterMode;
    }

    public void setEnterMode(int enterMode) {
        this.enterMode = enterMode;
    }

    public boolean isReversal() {
        return isReversal;
    }

    public void setReversal(boolean reversal) {
        isReversal = reversal;
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

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getMerchID() {
        return merchID;
    }

    public void setMerchID(String merchID) {
        this.merchID = merchID;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getTpdu() {
        return tpdu;
    }

    public void setTpdu(String tpdu) {
        this.tpdu = tpdu;
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

    public boolean getIsOnlineTrans() {
        return this.isOnlineTrans;
    }

    public void setIsOnlineTrans(boolean isOnlineTrans) {
        this.isOnlineTrans = isOnlineTrans;
    }

    public long getOrigBatchNo() {
        return this.origBatchNo;
    }

    public void setOrigBatchNo(long origBatchNo) {
        this.origBatchNo = origBatchNo;
    }

    public String getProcCode() {
        return this.procCode;
    }

    public void setProcCode(String procCode) {
        this.procCode = procCode;
    }

    public String getOrigProcCode() {
        return this.origProcCode;
    }

    public void setOrigProcCode(String origProcCode) {
        this.origProcCode = origProcCode;
    }

    public String getCardAmount() {
        return this.cardAmount;
    }

    public void setCardAmount(String cardAmount) {
        this.cardAmount = cardAmount;
    }

    public String getField52() {
        return this.field52;
    }

    public void setField52(String field52) {
        this.field52 = field52;
    }

    public int getKernelType() {
        return this.kernelType;
    }

    public void setKernelType(int kernelType) {
        this.kernelType = kernelType;
    }
    public String getCardHolderName() {
        return this.cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getField57() {
        return field57;
    }

    public void setField57(String field57) {
        this.field57 = field57;
    }

    public String getOrigTime() {
        return this.origTime;
    }

    public void setOrigTime(String origTime) {
        this.origTime = origTime;
    }

    public boolean getNeedScript() {
        return this.needScript;
    }

    public void setNeedScript(boolean needScript) {
        this.needScript = needScript;
    }

    public String getTag8A() {
        return this.tag8A;
    }

    public void setTag8A(String tag8A) {
        this.tag8A = tag8A;
    }

    public String getTag91() {
        return this.tag91;
    }

    public void setTag91(String tag91) {
        this.tag91 = tag91;
    }

    public String getTag71() {
        return this.tag71;
    }

    public void setTag71(String tag71) {
        this.tag71 = tag71;
    }

    public String getTag72() {
        return this.tag72;
    }

    public void setTag72(String tag72) {
        this.tag72 = tag72;
    }

    public String getScriptTag() {
        return this.scriptTag;
    }

    public void setScriptTag(String scriptTag) {
        this.scriptTag = scriptTag;
    }

    public String getIsserCode() {
        return this.isserCode;
    }

    public void setIsserCode(String isserCode) {
        this.isserCode = isserCode;
    }

    public String getAcqCode() {
        return this.acqCode;
    }

    public void setAcqCode(String acqCode) {
        this.acqCode = acqCode;
    }

    public String getInterOrgCode() {
        return this.interOrgCode;
    }

    public void setInterOrgCode(String interOrgCode) {
        this.interOrgCode = interOrgCode;
    }

    public int getTransresult() {
        return transresult;
    }

    public void setTransresult(int transresult) {
        this.transresult = transresult;
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

    public TransData clone() {
        TransData obj = null;
        try {
            obj = (TransData) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public boolean getIsUpload() {
        return this.isUpload;
    }

    public void setIsUpload(boolean isUpload) {
        this.isUpload = isUpload;
    }

    public byte getEmvResult() {
        return this.emvResult;
    }

    public void setEmvResult(byte emvResult) {
        this.emvResult = emvResult;
    }

    public String getOrigTransType() {
        return this.origTransType;
    }

    public void setOrigTransType(String origTransType) {
        this.origTransType = origTransType;
    }

    public boolean getIsSupportBypass() {
        return this.isSupportBypass;
    }

    public void setIsSupportBypass(boolean isSupportBypass) {
        this.isSupportBypass = isSupportBypass;
    }

    public String getIssuerResp() {
        return this.issuerResp;
    }

    public void setIssuerResp(String issuerResp) {
        this.issuerResp = issuerResp;
    }

    public String getCenterResp() {
        return this.centerResp;
    }

    public void setCenterResp(String centerResp) {
        this.centerResp = centerResp;
    }

    public String getRecvBankResp() {
        return this.recvBankResp;
    }

    public void setRecvBankResp(String recvBankResp) {
        this.recvBankResp = recvBankResp;
    }
    public TransStatusSum getTransStatusSum() {
        return transStatusSum;
    }

    public void setTransStatusSum(TransStatusSum transStatusSum) {
        this.transStatusSum = transStatusSum;
    }

    public boolean isStressTest() {
        return isStressTest;
    }

    public void setStressTest(boolean stressTest) {
        isStressTest = stressTest;
    }

    @Override
    public String toString() {
        return "TransData{" +
                "id=" + id +
                ", orderNo='" + orderNo + '\'' +
                ", orderNodis='" + orderNodis + '\'' +
                ", amount='" + amount + '\'' +
                ", cardAmount='" + cardAmount + '\'' +
                ", cashAmount='" + cashAmount + '\'' +
                ", tipAmount='" + tipAmount + '\'' +
                ", balance='" + balance + '\'' +
                ", balanceFlag='" + balanceFlag + '\'' +
                ", emvResult=" + emvResult +
                ", interOrgCode='" + interOrgCode + '\'' +
                ", transNo=" + transNo +
                ", origTransNo=" + origTransNo +
                ", batchNo=" + batchNo +
                ", origBatchNo=" + origBatchNo +
                ", transType='" + transType + '\'' +
                ", origTransType='" + origTransType + '\'' +
                ", merchID='" + merchID + '\'' +
                ", mcc='" + mcc + '\'' +
                ", isUpload=" + isUpload +
                ", transState='" + transState + '\'' +
                ", oper='" + oper + '\'' +
                ", track1='" + track1 + '\'' +
                ", track2='" + track2 + '\'' +
                ", track3='" + track3 + '\'' +
                ", isEncTrack=" + isEncTrack +
                ", reason='" + reason + '\'' +
                ", reserved='" + reserved + '\'' +
                ", datetime='" + datetime + '\'' +
                ", UnNum='" + UnNum + '\'' +
                ", time='" + time + '\'' +
                ", date='" + date + '\'' +
                ", termID='" + termID + '\'' +
                ", pan='" + pan + '\'' +
                ", expDate='" + expDate + '\'' +
                ", cardSerialNo='" + cardSerialNo + '\'' +
                ", enterMode=" + enterMode +
                ", hasPin=" + hasPin +
                ", sendIccData='" + sendIccData + '\'' +
                ", scriptTag='" + scriptTag + '\'' +
                ", dupIccData='" + dupIccData + '\'' +
                ", isOnlineTrans=" + isOnlineTrans +
                ", origDate='" + origDate + '\'' +
                ", origTime='" + origTime + '\'' +
                ", isserCode='" + isserCode + '\'' +
                ", acqCode='" + acqCode + '\'' +
                ", isSupportBypass=" + isSupportBypass +
                ", issuerResp='" + issuerResp + '\'' +
                ", centerResp='" + centerResp + '\'' +
                ", recvBankResp='" + recvBankResp + '\'' +
                ", ICPositiveData='" + ICPositiveData + '\'' +
                ", responseCode='" + responseCode + '\'' +
                ", responseMsg='" + responseMsg + '\'' +
                ", settleDate='" + settleDate + '\'' +
                ", acqCenterCode='" + acqCenterCode + '\'' +
                ", refNo='" + refNo + '\'' +
                ", origRefNo='" + origRefNo + '\'' +
                ", authCode='" + authCode + '\'' +
                ", origAuthCode='" + origAuthCode + '\'' +
                ", tc='" + tc + '\'' +
                ", arqc='" + arqc + '\'' +
                ", arpc='" + arpc + '\'' +
                ", tvr='" + tvr + '\'' +
                ", aid='" + aid + '\'' +
                ", emvAppLabel='" + emvAppLabel + '\'' +
                ", emvAppName='" + emvAppName + '\'' +
                ", tsi='" + tsi + '\'' +
                ", atc='" + atc + '\'' +
                ", origProcCode='" + origProcCode + '\'' +
                ", qrCode='" + qrCode + '\'' +
                ", origQrCode='" + origQrCode + '\'' +
                ", qrVoucher='" + qrVoucher + '\'' +
                ", origQrVoucher='" + origQrVoucher + '\'' +
                ", pinKsn='" + pinKsn + '\'' +
                ", dataKsn='" + dataKsn + '\'' +
                ", field52='" + field52 + '\'' +
                ", field58='" + field58 + '\'' +
                ", field22='" + field22 + '\'' +
                ", procCode='" + procCode + '\'' +
                ", cardHolderName='" + cardHolderName + '\'' +
                ", ElecSignature='" + ElecSignature + '\'' +
                ", kernelType=" + kernelType +
                ", needScript=" + needScript +
                ", tag71='" + tag71 + '\'' +
                ", tag72='" + tag72 + '\'' +
                ", tag8A='" + tag8A + '\'' +
                ", tag91='" + tag91 + '\'' +
                ", fingerprint='" + fingerprint + '\'' +
                ", transresult=" + transresult +
                ", pin='" + pin + '\'' +
                ", isReversal=" + isReversal +
                ", field3='" + field3 + '\'' +
                ", field46='" + field46 + '\'' +
                ", field48='" + field48 + '\'' +
                ", field57='" + field57 + '\'' +
                ", field60='" + field60 + '\'' +
                ", field62='" + field62 + '\'' +
                ", field63='" + field63 + '\'' +
                ", isSM=" + isSM +
                ", recvIccData='" + recvIccData + '\'' +
                ", header='" + header + '\'' +
                ", tpdu='" + tpdu + '\'' +
                ", isNeedPrint=" + isNeedPrint +
                ", isStressTest=" + isStressTest +
                ", transStatusSum=" + transStatusSum +
                '}';
    }

    public String getUnNum() {
        return this.UnNum;
    }

    public void setUnNum(String UnNum) {
        this.UnNum = UnNum;
    }

    public String getElecSignature() {
        return this.ElecSignature;
    }

    public void setElecSignature(String ElecSignature) {
        this.ElecSignature = ElecSignature;
    }
    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public boolean isNeedPrint() {
        return isNeedPrint;
    }

    public void setNeedPrint(boolean needPrint) {
        isNeedPrint = needPrint;
    }
}
