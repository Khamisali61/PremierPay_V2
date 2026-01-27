package com.topwise.premierpay.trans.model;

import android.content.Context;
import android.os.Build;
import android.os.ConditionVariable;
import android.text.TextUtils;

import com.topwise.manager.AppLog;
import com.topwise.premierpay.transmit.TransProcessListenerImpl;
import com.topwise.premierpay.transmit.iso8583.Transmit;
import com.topwise.toptool.api.convert.IConvert;
import com.topwise.premierpay.BuildConfig;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.daoutils.DaoUtilsStore;
import com.topwise.premierpay.daoutils.entity.DupTransdata;
import com.topwise.premierpay.daoutils.entity.ScriptTransdata;
import com.topwise.premierpay.daoutils.entity.TotaTransdata;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.core.TransContext;
import com.topwise.premierpay.utils.ConfiUtils;
import com.topwise.premierpay.utils.NetWorkUtils;
import com.topwise.premierpay.utils.Utils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

public class Component {
    /**
     * 打印字库路径
     */
    public static final String FONT_PATH = TopApplication.mApp.getFilesDir().getAbsolutePath() + File.separator;
    /**
     * 字库名称  Topwise45   Topwise55  HYQIHEI-65 HYQIHEI-75S
     */
    public static final String FONT_NAME = "Topwise65.ttf";

    public static TotaTransdata calcTotal(){
        SysParam sysParam = TopApplication.sysParam;
        TotaTransdata totaTransdata = new TotaTransdata();
        totaTransdata.setTerminalID(sysParam.get(SysParam.TERMINAL_ID));
        totaTransdata.setMerchantID(sysParam.get(SysParam.MERCH_ID));
        totaTransdata.setBatchNo(String.format("%06d",Long.parseLong(sysParam.get(SysParam.BATCH_NO))));
        totaTransdata.setDatetime(Device.getDateTime());

        List<String[]> transInfo = DaoUtilsStore.getInstance().getmTransDaoUtils().getTransInfoGroupByTransType();
        if (transInfo == null) return null;
        AppLog.d("","transInfo " + transInfo.size());
        for (int i = 0; i < transInfo.size(); i++) {
            String[] item = transInfo.get(i);
            String count = item[0];
            String sumAmount = item[1];
            String transType = item[2];
            String transStatus = item[3];
            AppLog.d("SummaryFragment","count "+count+" sumAmount " + sumAmount + " transType "
                    +transType + " transStatus " + transStatus);

            //Sale
            if (ETransType.TRANS_SALE.toString().equals(transType)
                    && (ETransStatus.NORMAL.toString().equals(transStatus) || ETransStatus.VOID.toString().equals(transStatus))){
                totaTransdata.setBankSaleNumberTotal(Long.valueOf(count) +
                        (totaTransdata.getBankSaleNumberTotal() !=null ? totaTransdata.getBankSaleNumberTotal():0));
                totaTransdata.setBankSaleAmountTotal(Long.valueOf(sumAmount) +
                        (totaTransdata.getBankSaleAmountTotal() !=null ? totaTransdata.getBankSaleAmountTotal():0));
            }
            //Void
            if (ETransType.TRANS_VOID.toString().equals(transType)
                    && ETransStatus.NORMAL.toString().equals(transStatus)){
                totaTransdata.setBankVoidNumberTotal(Long.valueOf(count));
                totaTransdata.setBankVoidAmountTotal(Long.valueOf(sumAmount));
            }
            //Refund
            if (ETransType.TRANS_REFUND.toString().equals(transType)
                    && ETransStatus.NORMAL.toString().equals(transStatus)){
                totaTransdata.setBankRefundNumberTotal(Long.valueOf(count));
                totaTransdata.setBankRefundAmountTotal(Long.valueOf(sumAmount));
            }

            //qr Sale
            if (ETransType.TRANS_QR_SALE.toString().equals(transType)
                    && (ETransStatus.NORMAL.toString().equals(transStatus) ||ETransStatus.VOID.toString().equals(transStatus))){
                totaTransdata.setQrSaleNumberTotal(Long.valueOf(count) +
                        (totaTransdata.getQrSaleNumberTotal() !=null ? totaTransdata.getQrSaleNumberTotal():0));
                totaTransdata.setQrSaleAmountTotal(Long.valueOf(sumAmount) +
                        (totaTransdata.getQrSaleAmountTotal() !=null ? totaTransdata.getQrSaleAmountTotal():0));
            }
            //qr Void
            if (ETransType.TRANS_QR_VOID.toString().equals(transType)
                    && ETransStatus.NORMAL.toString().equals(transStatus)){
                totaTransdata.setQrVoidNumberTotal(Long.valueOf(count));
                totaTransdata.setQrVoidAmountTotal(Long.valueOf(sumAmount));
            }
            //qr Refund
            if (ETransType.TRANS_QR_REFUND.toString().equals(transType)
                    && ETransStatus.NORMAL.toString().equals(transStatus)){
                totaTransdata.setQrRefundNumberTotal(Long.valueOf(count));
                totaTransdata.setQrRefundAmountTotal(Long.valueOf(sumAmount));
            }
        }
        //笔数相加
        Long totalBankNumber = (totaTransdata.getBankSaleNumberTotal() != null ? totaTransdata.getBankSaleNumberTotal() : 0)+
                (totaTransdata.getBankVoidNumberTotal() != null ? totaTransdata.getBankVoidNumberTotal() : 0)+
                (totaTransdata.getBankRefundNumberTotal() != null ? totaTransdata.getBankRefundNumberTotal() : 0);
        Long totalQrNumber = (totaTransdata.getQrSaleNumberTotal()!= null ? totaTransdata.getQrSaleNumberTotal() : 0) +
                (totaTransdata.getQrVoidNumberTotal() != null ? totaTransdata.getQrVoidNumberTotal() : 0)+
                (totaTransdata.getQrRefundNumberTotal()!= null ? totaTransdata.getQrRefundNumberTotal() : 0);

        totaTransdata.setBankNumberTotal(totalBankNumber);
        totaTransdata.setQrNumberTotal(totalQrNumber);

        Long totalBankAmount = (totaTransdata.getBankSaleAmountTotal()!= null ? totaTransdata.getBankSaleAmountTotal() : 0) -
                (totaTransdata.getBankVoidAmountTotal() != null ? totaTransdata.getBankVoidAmountTotal() : 0)-
                (totaTransdata.getBankRefundAmountTotal()!= null ? totaTransdata.getBankRefundAmountTotal() : 0);
        Long totalQrAmount  = (totaTransdata.getQrSaleAmountTotal() != null ? totaTransdata.getQrSaleAmountTotal() : 0)-
                (totaTransdata.getQrVoidAmountTotal() != null ? totaTransdata.getQrVoidAmountTotal() : 0)-
                (totaTransdata.getQrRefundAmountTotal()!= null ? totaTransdata.getQrRefundAmountTotal() : 0);

        totaTransdata.setBankAmountTotal(totalBankAmount);
        totaTransdata.setQrAmountTotal(totalQrAmount);

        return totaTransdata;
    }

    public static TransStatusSum calNetStatus(){
        SysParam sysParam = TopApplication.sysParam;
        TransStatusSum totaTransdata = new TransStatusSum();
        totaTransdata.setTerminalID(sysParam.get(SysParam.TERMINAL_ID));
        totaTransdata.setMerchantID(sysParam.get(SysParam.MERCH_ID));
        totaTransdata.setBatchNo(String.format("%06d",Long.parseLong(sysParam.get(SysParam.BATCH_NO))));

        List<TransStatusSum> list = DaoUtilsStore.getInstance().getmTransStatusDaoUtils().queryAll();
        if (list == null||list.size()==0) {
            return new TransStatusSum();
        }
        AppLog.d("","transInfo " + list.size());
        long wifiTotal =0l;
        long mobileTotal =0l;
        long wifiSucc =0l;
        long mobileSucc =0l;


        long netFailCount =0l;
        long cardFailCount =0l;
        long unknownFailCount =0l;
        long totalFailCount =0l;

        long totalComTime =0L;
        long totalConnectTime =0L;
        long totalSendTime =0L;
        long totalReceiveTime =0L;
        long totalPackTime =0L;
        long totalUnPackTime =0L;
        int count = list.size();
        for ( int i = 0;i<count;i++) {
            TransStatusSum bean = list.get(i);

           int result =   bean.getResult();
           if(i == 0){
               totaTransdata.setBeginTime(bean.getDatetime());
           }
            if(i == count-1){
                totaTransdata.setEndTime(bean.getDatetime());
            }
          /********Statistical transaction data ***********/
          if(result != TransResult.SUCC){
                switch (result){
                    case TransResult.ERR_CHECK_CARD:
                         cardFailCount ++;
                        break;
                    case TransResult.ERR_SEND:
                    case TransResult.ERR_RECV:
                        netFailCount ++;
                        break;
                    case TransResult.ERR_CONNECT:
                        netFailCount ++;
                        if(bean.getCommType()== 0){
                            wifiTotal++;
                        }else{
                            mobileTotal++;
                        }
                        break;
                    default:
                        unknownFailCount ++;
                        break;
                }
                totalFailCount = cardFailCount+netFailCount+unknownFailCount;
                continue;
          }
         /********Statistical network  detail ***********/
           if(bean.getCommType()== 0){
                wifiTotal++;
               if (bean.getResult()== 0){
                   wifiSucc++;
               }
           }else{
                mobileTotal++;
                if (bean.getResult()== 0){
                    mobileSucc++;
                }
           }
           totalConnectTime += bean.getConnectTime();
           totalComTime += bean.getConnectTime();

           totalSendTime +=bean.getSendTime();
           totalComTime += bean.getSendTime();

           totalReceiveTime +=bean.getReceiveTime();
           totalComTime += bean.getReceiveTime();

           totalPackTime +=bean.getPackTime();
           totalUnPackTime +=bean.getUnpackTime();
        }
        long successCount  = count -totalFailCount;
        if(successCount>0){
            totaTransdata.setConnectTime(totalConnectTime/successCount);
            totaTransdata.setSendTime(totalSendTime/successCount);
            totaTransdata.setReceiveTime(totalReceiveTime/successCount);
            totaTransdata.setPackTime(totalPackTime/successCount);
            totaTransdata.setUnpackTime(totalUnPackTime/successCount);
            totaTransdata.setCommTime(totalComTime/successCount);
        }


        totaTransdata.setWifiTotal(wifiTotal);
        totaTransdata.setWifiSucc(wifiSucc);
        totaTransdata.setWifiRate(getRate(wifiSucc,wifiTotal));

        totaTransdata.setMobileSucc(mobileSucc);
        totaTransdata.setMobileTotal(mobileTotal);
        totaTransdata.setMobileRate(getRate(mobileSucc,mobileTotal));

        totaTransdata.setNetTotal(mobileTotal+wifiTotal);
        totaTransdata.setNetSucc(mobileSucc+wifiSucc);

        totaTransdata.setNetTotalSuccRate(getRate(mobileSucc+wifiSucc,mobileTotal+wifiTotal));


        totaTransdata.setCardFailCount(cardFailCount);
        totaTransdata.setCardFailRate(getRate(cardFailCount,count));

        totaTransdata.setNetFailCount(netFailCount);
        totaTransdata.setNetFailRate(getRate(netFailCount,count));

        totaTransdata.setUnknownFailCount(unknownFailCount);
        totaTransdata.setUnknownFailRate(getRate(unknownFailCount,count));

        totaTransdata.setTotalFailCount(totalFailCount);
        totaTransdata.setTotalFailRate(getRate(totalFailCount,count));

        totaTransdata.setTotal(count);


        return totaTransdata;
    }
    private static String getRate(float part, float total){

      if(total ==0){
          return "";
      }
      float num= part*100/total;
      DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        String s = df.format(num)+"%";//返回的是String类型
      return s;

    }

    /**
     * Check the types of transactions that need to be maintained
     * @param transData
     * @return
     */
    public static boolean checkSave(TransData transData){
        ETransType eTransType = ETransType.valueOf(transData.getTransType());
        switch (eTransType){
            case TRANS_SALE:
            case TRANS_VOID:
            case TRANS_REFUND:
            case TRANS_QR_SALE:
            case TRANS_QR_VOID:
            case TRANS_QR_REFUND:
            case TRANS_AUTO_SALE:
            case TRANS_SALE_WITH_CASH:
            case TRANS_CASH:
            case TRANS_BUY:
            case TRANS_PAYMENT:
            case TRANS_TRANSFER:
            case TRANS_PRE_AUTH:
            case TRANS_PRE_AUTH_CMP:
            case TRANS_PRE_AUTH_VOID:
                return true;
            default:
                return false;
        }
    }
//    类描述 LED灯常量类 常量名 常量值 描述
//    ALL 0(int) 所有灯操作位
//    RED 1(int) 红灯操作位
//    GREEN 2(int) 绿灯操作位
//    YELLOW 3(int) 黄灯操作位
//    BLUE 4(int) 蓝灯操作位
    public static final int ALL = 0;
    public static final int GREEN = 1;
    public static final int YELLOW = 2;
    public static final int RED = 3;
    public static final int BLUE = 4;


    public static final String REASON_NO_RECV = "98";
    public static final String REASON_MACWRONG = "A0";
    public static final String REASON_OTHERS = "06";
    /**
     * 交易成功，弹出对话框的显示时间, 单位秒
     */
    public final static int SUCCESS_DIALOG_SHOW_TIME = 5;
    /**
     * 失败 时弹出框的显示时间, 单位秒
     */
    public final static int FAILED_DIALOG_SHOW_TIME = 8;

    public static class EnterMode {
        /**
         * 手工输入
         */
        public static final int MANAUL = 1;
        /**
         * 刷卡
         */
        public static final int SWIPE = 2;
        /**
         * 插卡
         */
        public static final int INSERT = 3;
        /**
         * IC卡回退
         */
        public static final int FALLBACK = 4;
        /**
         * 预约支付
         */
        public static final int PHONE = 5;
        /**
         * 非接快速支付
         */
        public static final int QPBOC = 6;
        /**
         * 非接完整PBOC
         */
        public static final int CLSS_PBOC = 7;
        /**
         * 非接读取CUPMobile
         */
        public static final int MOBILE = 8;
        /**
         * 扫码支付
         */
        public static final int QR = 9;
    }

    public enum ETransStatus {
        /**
         * 正常 成功
         */
        NORMAL,
        /**
         * 已撤销
         */
        VOID,
        /**
         * 已调整
         */
        ADJUST,
        /**
         * 未知
         */
        UNKNOW
    }
    private static final long MAX_TRANS_NO = 999999;
    private static final long MAX_BATCH_NO = 999999;

    /**
     * 流水号+1
     */
    public static void incTransNo() {
        long transNo = Long.parseLong(TopApplication.sysParam.get(SysParam.TRANS_NO));
        if (transNo >= MAX_TRANS_NO) {
            transNo = 0;
        }
        transNo++;
        TopApplication.sysParam.set(SysParam.TRANS_NO, String.valueOf(transNo));
    }

    /**
     * 批次号+1
     */
    public static void incBatchNo() {
        long batchNo = Long.parseLong(TopApplication.sysParam.get(SysParam.BATCH_NO));
        if (batchNo >= MAX_BATCH_NO) {
            batchNo = 0;
        }
        batchNo++;
        TopApplication.sysParam.set(SysParam.BATCH_NO, String.valueOf(batchNo));
    }
    // 获取流水号
    private static long getTransNo() {
        long transNo = Long.parseLong(TopApplication.sysParam.get(SysParam.TRANS_NO));
        if (transNo == 0) {
            transNo += 1;
            TopApplication.sysParam.set(SysParam.TRANS_NO, String.valueOf(transNo));
        }
        return transNo;
    }

    public static ScriptTransdata scriptTransInit(){
        SysParam sysParam = TopApplication.sysParam;
        String temp = "";
        ScriptTransdata scriptTransdata = new ScriptTransdata();
        scriptTransdata.setMerchID(sysParam.get(SysParam.MERCH_ID));
        scriptTransdata.setTermID(sysParam.get(SysParam.TERMINAL_ID));
        scriptTransdata.setTransNo(getTransNo());
        scriptTransdata.setDatetime(Device.getPosDateTime());
        return scriptTransdata;
    }

    /**
     * 交易初始化
     *
     * @return
     */
    public static TransData transInit() {
        SysParam sysParam = TopApplication.sysParam;
        String temp = "";
        TransData transData = new TransData();
        TransStatusSum transStatus = new TransStatusSum();
        transData.setTransStatusSum(transStatus);

        //取随机数会耗时，放在交易初始化的时候先取
        transData.setUnNum(Utils.getRandom());


        // TODO: 7/3/2025 test data
        String merchat = sysParam.get(SysParam.MERCH_ID);
//        merchat = TopApplication.convert.stringPadding(merchat, (char) 0x20,15, IConvert.EPaddingPosition.PADDING_RIGHT);
        merchat = TopApplication.convert.stringPadding(merchat, (char) 0x30,15, IConvert.EPaddingPosition.PADDING_LEFT);
        transData.setMerchID(merchat);
        transData.setTermID(sysParam.get(SysParam.TERMINAL_ID));
        transData.setMcc(sysParam.get(SysParam.PARAM_MCC));
        Component.incTransNo();
        transData.setTransNo(getTransNo());
        transStatus.setId(getTransNo());

        transData.setBatchNo(Long.parseLong(sysParam.get(SysParam.BATCH_NO)));
        transData.setDate(Device.getDate().substring(4));
        transData.setTime(Device.getTime());
        transStatus.setDatetime(Device.getDateTime());

        transData.setDatetime(Device.getPosDateTime());

        transData.setHeader("603200322012");
        transData.setTpdu(sysParam.get(SysParam.APP_TPDU));
        // 冲正原因
        transData.setReason(REASON_NO_RECV);
        String sOper = TopApplication.controller.getString(Controller.CUR_OPERID);
        if (TextUtils.isEmpty(sOper)) sOper = "01";
        transData.setOper(sOper);
        transData.setTransState(ETransStatus.NORMAL.toString());

        if (SysParam.Constant.YES.equals(sysParam.get(SysParam.OTHTC_TRACK_ENCRYPT))) {
            transData.setEncTrack(true);
        }

        return transData;
    }

    /**
     * 初始化冲正
     * @param transData
     * @return
     */
    public static DupTransdata transInitDup(TransData transData){
        DupTransdata dupTransdata = new DupTransdata();
        dupTransdata.setMerchID(transData.getMerchID());
        dupTransdata.setTermID(transData.getTermID());
        dupTransdata.setTransNo(transData.getTransNo());
        dupTransdata.setBatchNo(transData.getBatchNo());
        dupTransdata.setDate(transData.getDate());
        dupTransdata.setTime(transData.getTime());
        dupTransdata.setDatetime(transData.getDatetime());
        dupTransdata.setReason(transData.getReason());
        dupTransdata.setOper(transData.getOper());
        dupTransdata.setTransState(ETransStatus.NORMAL.toString());

        dupTransdata.setAmount(transData.getAmount());
        dupTransdata.setField22(transData.getField22());
        dupTransdata.setField62(transData.getField62());
        dupTransdata.setSendIccData(transData.getSendIccData());
        dupTransdata.setICPositiveData(transData.getICPositiveData());
        dupTransdata.setTransType(transData.getTransType());
        dupTransdata.setEnterMode(transData.getEnterMode());
        dupTransdata.setPan(transData.getPan());

        dupTransdata.setExpDate(transData.getExpDate());
        dupTransdata.setCardSerialNo(transData.getCardSerialNo());
        dupTransdata.setHasPin(transData.getHasPin());
        //
        dupTransdata.setQrCode(transData.getQrCode());
        dupTransdata.setQrVoucher(transData.getQrVoucher());

        dupTransdata.setOrigQrCode(transData.getOrigQrCode());
        dupTransdata.setOrigQrVoucher(transData.getOrigQrVoucher());

        return dupTransdata;
    }

    /**
     * 交易初始化
     *
     * @param transData
     */
    public static void transInit(TransData transData) {
        SysParam sysParam = TopApplication.sysParam;
        transData.setMerchID(sysParam.get(SysParam.MERCH_ID));
        transData.setTermID(sysParam.get(SysParam.TERMINAL_ID));
        transData.setTransNo(getTransNo());
        transData.setBatchNo(Long.parseLong(sysParam.get(SysParam.BATCH_NO)));
        transData.setDate(Device.getDate().substring(4));
        transData.setTime(Device.getTime());
        transData.setHeader("603200322012");

        transData.setTpdu(sysParam.get(SysParam.APP_TPDU));
        // 冲正原因
        transData.setReason("06");
        transData.setOper(TransContext.getInstance().getOperID());
        transData.setTransState(ETransStatus.NORMAL.toString());
        // 返回55密码错第二次的密码限制
        transData.setIsSupportBypass(true);

        if (SysParam.Constant.YES.equals(sysParam.get(SysParam.OTHTC_TRACK_ENCRYPT))) {
            transData.setIsEncTrack(true);
        }
    }

    public static TransData transInit(DupTransdata dupTransdata){
        SysParam sysParam = TopApplication.sysParam;
        TransData transData = new TransData();
        TransStatusSum transStatusSum =  new TransStatusSum();


        transData.setMerchID(sysParam.get(SysParam.MERCH_ID));
        transData.setTermID(sysParam.get(SysParam.TERMINAL_ID));
        transData.setTransNo(dupTransdata.getTransNo());
        transStatusSum.setId(dupTransdata.getTransNo());
        transData.setTransStatusSum(transStatusSum);
    //        transData.setTransNo(getTransNo());
        transData.setOrigTransNo(dupTransdata.getTransNo());
        transData.setBatchNo(Long.parseLong(sysParam.get(SysParam.BATCH_NO)));
//        transData.setDate(Device.getDate().substring(4));
//        transData.setTime(Device.getTime());
        transData.setDate(dupTransdata.getDate());
        transData.setTime(dupTransdata.getTime());
        transData.setDatetime(Device.getPosDateTime());

        transData.setEnterMode(dupTransdata.getEnterMode());
        transData.setAmount(dupTransdata.getAmount());
        transData.setICPositiveData(dupTransdata.getICPositiveData());
        transData.setScriptTag(dupTransdata.getTag9F5B());
        // 冲正原因
        transData.setReason(dupTransdata.getReason());
        String sOper = TopApplication.controller.getString(Controller.CUR_OPERID);
        if (TextUtils.isEmpty(sOper)) sOper = "01";
        transData.setOper(sOper);
        transData.setTransState(ETransStatus.NORMAL.toString());

        transData.setTransType(dupTransdata.getTransType());
        transData.setReversal(true);
        transData.setPan(dupTransdata.getPan());
        transData.setField22(dupTransdata.getField22());
        transData.setField62(dupTransdata.getField62());
        transData.setSendIccData(dupTransdata.getSendIccData());
        transData.setExpDate(dupTransdata.getExpDate());
        transData.setCardSerialNo(dupTransdata.getCardSerialNo());
        transData.setHasPin(dupTransdata.getHasPin());
        transData.setHeader("603200322012");
        transData.setTpdu(sysParam.get(SysParam.APP_TPDU));

        transData.setQrCode(dupTransdata.getQrCode());
        transData.setQrVoucher(dupTransdata.getQrVoucher());

        transData.setOrigQrCode(dupTransdata.getOrigQrCode());
        transData.setOrigQrVoucher(dupTransdata.getOrigQrVoucher());

        return transData;
    }

    public static void incTranNoTime(TransData transData) {
        Component.incTransNo();
        transData.setTransNo(getTransNo());
        TransStatusSum transStatus = transData.getTransStatusSum();
        transStatus.setId(getTransNo());
        transData.setDate(Device.getDate().substring(4));
        transData.setTime(Device.getTime());
        transStatus.setDatetime(Device.getDateTime());
        transData.setDatetime(Device.getPosDateTime());
    }

    /**
     * 是否支持该交易
     * @param transType
     * @return
     */
    private static boolean checkType(ETransType transType) {
//        if (transType == ETransType.TRANS_PRE_AUTH ||
//                transType == ETransType.TRANS_PRE_AUTH_CMP) {
//            return false ;
//        }
        return true;
    }

    /**
     * 根据交易类型、冲正标识确认当前交易是否预处理
     *
     * @param transType
     * @return true:需要预处理 false:不需要预处理 备注：签到，签退，结算，参数下发，公钥下载，冲正类不需要预处理,新增交易类型时，需修改添加交易类型判断
     */
    private static boolean IsNeedPreDeal(ETransType transType) {
        if (transType == ETransType.LOGON ||
                transType == ETransType.LOGOUT ||
                transType == ETransType.PARAM_DOWNLOAD ||
                transType == ETransType.TRANS_NETWORK) {
            return false;
        }
        return true;
    }

    /**
     * 判断终端是否签到
     * @return true：已签到 false：未签到
     */
    private static boolean isLogon() {

        if (TopApplication.controller.get(Controller.POS_LOGON_STATUS) == Controller.Constant.YES  || BuildConfig.CHANNEL.equals("topwise")) {
            return true;
        }

        return false;
    }
    /**
     * 检查是否达结算要求
     *
     * @return 0：不用结算 1：结算提醒,立即 2：结算提醒，稍后 3：结算提醒,空间不足
     */
    private static int checkSettle() {
//        // 获取交易笔数
//        long cnt = TransData.getTransCount();
//        // 获取允许的最大交易笔数
//        long maxCnt = Long.MAX_VALUE;
//        String temp = FinancialApplication.sysParam.get(SysParam.MAX_TRANS_COUNT);
//        if (temp != null) {
//            maxCnt = Long.parseLong(temp);
//        }
//        // 判断交易笔数是否超限
//        if (cnt >= maxCnt) {
//            if (cnt >= maxCnt + 10) {
//                return TransResult.ERR_NEED_SETTLE_NOW; // 结算提醒,立即
//            } else {
//                return TransResult.ERR_NEED_SETTLE_LATER; // 结算提醒,稍后
//            }
//        }
//        // 判断存储空间大小
//        if (!hasFreeSpace()) {
//            return TransResult.ERR_NO_FREE_SPACE; // 存储空间不足,需要结算
//        }
        return TransResult.SUCC; // 不用结算
    }

    /**
     * 交易预处理，检查是否签到， 是否需要结束， 是否继续批上送， 是否支持该交易， 是否需要参数下载
     *
     * @param context
     * @param transType
     * @return
     */
    public static int transPreDeal(final Context context, ETransType transType) {
        if (!(Build.MANUFACTURER.equalsIgnoreCase("topwise") || Build.MANUFACTURER.equalsIgnoreCase("Gertec")) && TopApplication.sysParam.getInt(SysParam.DEVICE_MODE )!=1 ){
            return TransResult.ERR_NO_BT_MODE;
        }
        if (!NetWorkUtils.isNetworkAvailable(context) && !ConfiUtils.isDebug) {
            return TransResult.ERR_NO_NET;
        }
        // TODO: 7/4/2025 Darrick reversal for transaction
        if (isNeedReversal()) {
            cv.close();
            toReversal();
            cv.block();
            AppLog.d("TAG", "transPreDeal reversalRet" + reversalRet);
            if (reversalRet != TransResult.SUCC) {
                return reversalRet;
            }
        }
        if (!IsNeedPreDeal(transType)) {
            return TransResult.SUCC;
        }
        if (!checkType(transType)) {
            return TransResult.ERR_NOT_SUPPORT_TRANS;
        }

        // 检测电量状态，暂不处理，后续再确定需不需要 fix me
        // 判断终端签到状态
//        if (!isLogon()) {
//            return TransResult.ERR_NOT_LOGON;
//        }
        // 判断是否需要结算
        int ret = checkSettle();
        if (ret != TransResult.SUCC) {
            return ret;
        }
        return TransResult.SUCC;
    }

    static ConditionVariable cv = new ConditionVariable();
    static int reversalRet = TransResult.ERR_ABORTED;
    static TransProcessListenerImpl listenerImpl = new TransProcessListenerImpl();
    public static boolean isNeedReversal() {
        List<DupTransdata> dupTransdata = DaoUtilsStore.getInstance().getmDupTransDaoUtils().queryAll();
        return (dupTransdata != null || !TextUtils.isEmpty(dupTransdata.toString()));
    }


    private static void toReversal() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppLog.d("TAG", "Component toReversal");
                // 立即冲正，在这处理
//                TransProcessListenerImpl listenerImpl = new TransProcessListenerImpl();
                reversalRet = Transmit.getInstance().sendReversal(listenerImpl);
                listenerImpl.onHideProgress();
                Device.closeAllLed();
                cv.open();
            }
        }).start();
    }

}