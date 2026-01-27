package com.topwise.premierpay.emv;

import android.text.TextUtils;

import com.topwise.cloudpos.aidl.emv.level2.EmvKernelConfig;
import com.topwise.cloudpos.aidl.emv.level2.EmvTerminalInfo;
import com.topwise.manager.AppLog;
import com.topwise.manager.emv.entity.EmvErrorCode;
import com.topwise.toptool.api.convert.IConvert;
import com.topwise.toptool.api.packer.ITlv;
import com.topwise.toptool.impl.TopTool;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.daoutils.DaoUtilsStore;
import com.topwise.premierpay.daoutils.entity.DupTransdata;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.model.TransData;

import java.util.List;

/**
 * 创建日期：2021/4/14 on 9:30
 * 描述: 保存 ic data
 * 作者:  wangweicheng
 */
public class EmvResultUtlis {
    private static final String TAG = EmvResultUtlis.class.getSimpleName();
    /**
     *    * 交易结果
     *      * 批准: 0x01
     *      * 拒绝: 0x02
     *      * 终止: 0x03
     */
    /**
     * 联机批准
     */
    public final static byte ONLINE_APPROVED = 0x01;
    /**
     * 交易拒绝
     */
    public final static byte ONLINE_DENIED = 0x02;
    /**
     * 脱机批准
     */
    public final static byte OFFLINE_APPROVED = 0x03;

    public static boolean checkRupayScriptContactlessResult(TransData transData) {
        // 8A 72 91
        boolean is72 = false;
        boolean is91 = false;
        String scriptData = transData.getRecvIccData();
        if (TextUtils.isEmpty(scriptData))
            return false;

        try {
            String temp = "";
            AppLog.emvd("EmvResultUtlis scriptData=================" + scriptData);
            ITlv tlv = TopApplication.packer.getTlv();
            ITlv.ITlvDataObjList list = tlv.unpack(TopApplication.convert.strToBcd(scriptData, IConvert.EPaddingPosition.PADDING_LEFT));
            byte[] value8A = list.getValueByTag(0x8A);
            if (value8A != null && value8A.length > 0){
                temp = TopApplication.convert.bcdToStr(value8A);
                transData.setTag8A(temp);
                AppLog.emvd("EmvResultUtlis value8A=================" + temp);
            }
            byte[] value71 = list.getValueByTag(0x71);
            if (value71 != null && value71.length > 0){
                temp = TopApplication.convert.bcdToStr(value71);
                AppLog.emvd("EmvResultUtlis value71=================" + temp);
                transData.setTag71(temp);
            }

            byte[] value72 = list.getValueByTag(0x72);
            if (value72 != null && value72.length > 0){
                temp = TopApplication.convert.bcdToStr(value72);
                AppLog.emvd("EmvResultUtlis value72=================" + temp);
                is72 = true;
                transData.setTag72(temp);
            }
            byte[] value91 = list.getValueByTag(0x91);
            if (value91 != null && value91.length > 0){
                temp = TopApplication.convert.bcdToStr(value91);
                AppLog.emvd("EmvResultUtlis value91=================" + temp);

                AppLog.emvd("EmvResultUtlis value91=================" + Integer.toHexString(value91[4] & 0xFF));
                AppLog.emvd("EmvResultUtlis value91=================" + Integer.toHexString(value91[5] & 0xFF));

                is91 = true;
                transData.setTag91(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        AppLog.emvd("EmvResultUtlis is72 =================" +is72);
        AppLog.emvd("EmvResultUtlis is91 =================" +is91);
        if (is72 || is91) {
            return true;
        } else {
            return false;
        }
    }

    public static void updataDupTransdata(String reson) {
        AppLog.emvd("updataDupTransdata reson " + reson);
        List<DupTransdata> dupTransdatas = DaoUtilsStore.getInstance().getmDupTransDaoUtils().queryAll();
        if (dupTransdatas.size() > 0) {
            DupTransdata dupTransdata = dupTransdatas.get(0);
            dupTransdata.setReason(reson);
            boolean update = DaoUtilsStore.getInstance().getmDupTransDaoUtils().update(dupTransdata);
            AppLog.emvd("updataDupTransdata " + update );
        }
    }

    //set kernal data
    /**
     * set init EmvTerminalInfo
     * @return
     */
      public static EmvTerminalInfo emvTerminalInfo;
      public static EmvKernelConfig emvKernelConfig;

    public static EmvTerminalInfo setEmvTerminalInfo() {
        if (emvTerminalInfo != null) {
            return emvTerminalInfo;
        }

        emvTerminalInfo = new EmvTerminalInfo();
        IConvert convert = TopTool.getInstance().getConvert();
        String transCurCode = TopApplication.sysParam.get(SysParam.APP_PARAM_TRANS_CURRENCY_CODE);
        String terCountryCode = TopApplication.sysParam.get(SysParam.APP_PARAM_TER_COUNTRY_CODE);
        String terCapabilities = TopApplication.sysParam.get(SysParam.APP_PARAM_TER_CAP);

        if (TextUtils.isEmpty(transCurCode)){
            transCurCode = "0840";
        }
        if (TextUtils.isEmpty(terCountryCode)){
            terCountryCode = "0840";
        }
        if (TextUtils.isEmpty(terCapabilities)){
            terCapabilities = "E0F8C8";
        }
        //UL L3 JCB Terminal Floor Limit需要和案例配置相同
        emvTerminalInfo.setUnTerminalFloorLimit(10000);
        //UL L3 JCB Threshold Value 需要和案例配置相同
        emvTerminalInfo.setUnThresholdValue(2000);
        String terminalId = TopApplication.sysParam.get(SysParam.TERMINAL_ID);
        emvTerminalInfo.setAucTerminalID(terminalId);
        emvTerminalInfo.setAucIFDSerialNumber("12345678");
        emvTerminalInfo.setAucTerminalCountryCode(convert.strToBcd(terCountryCode, IConvert.EPaddingPosition.PADDING_RIGHT));
        String mercherId = TopApplication.sysParam.get(SysParam.MERCH_ID);
        emvTerminalInfo.setAucMerchantID(mercherId);
        emvTerminalInfo.setAucMerchantCategoryCode(new byte[] {0x00, 0x01});
        emvTerminalInfo.setAucMerchantNameLocation(new byte[] {0x30, 0x30, 0x30, 0x31}); //"0001"
        emvTerminalInfo.setAucTransCurrencyCode(convert.strToBcd(transCurCode, IConvert.EPaddingPosition.PADDING_RIGHT));
        emvTerminalInfo.setUcTransCurrencyExp((byte) 2);
        emvTerminalInfo.setAucTransRefCurrencyCode(convert.strToBcd(transCurCode, IConvert.EPaddingPosition.PADDING_RIGHT));
        emvTerminalInfo.setUcTransRefCurrencyExp((byte) 2);


        emvTerminalInfo.setAucTerminalAcquireID("123456");
        emvTerminalInfo.setAucAppVersion(new byte[] {0x00, 0x030});
        emvTerminalInfo.setAucDefaultDDOL(new byte[] {(byte)0x9F, 0x37, 0x04});
        emvTerminalInfo.setAucDefaultTDOL(new byte[] {(byte)0x9F, 0x37, 0x04});

        emvTerminalInfo.setAucTACDenial(new byte[] {0x00, 0x00, 0x00, 0x00, 0x00});
        emvTerminalInfo.setAucTACOnline(new byte[] {0x00, 0x00, 0x00, 0x00, 0x00});
        emvTerminalInfo.setAucTACDefault(new byte[] {0x00, 0x00, 0x00, 0x00, 0x00});

        emvTerminalInfo.setUcTerminalType((byte)0x22);  //22 印度客户也是配置这个值
        //终端是否支持脱机PIN 可以通过 setAucTerminalCapabilities 配置
        //emvTerminalInfo.setAucTerminalCapabilities(new byte[] {(byte)0xE0, (byte)0xF8, (byte)0xC8});
        emvTerminalInfo.setAucTerminalCapabilities(convert.strToBcd(terCapabilities, IConvert.EPaddingPosition.PADDING_RIGHT));  //E0F8C8
//        emvTerminalInfo.setAucAddtionalTerminalCapabilities(new byte[] {(byte)0xFF, (byte)0x00, (byte)0xF0, (byte)0xA0, 0x01});
        //paynext FF 80 F0 00 01
        emvTerminalInfo.setAucAddtionalTerminalCapabilities(new byte[] {(byte)0xFF, (byte)0x80, (byte)0xF0, (byte)0x50, 0x01});

        emvTerminalInfo.setUcTargetPercentage((byte) 20);
        emvTerminalInfo.setUcMaxTargetPercentage((byte) 50);
        emvTerminalInfo.setUcAccountType((byte) 0);
        emvTerminalInfo.setUcIssuerCodeTableIndex((byte) 0);
        return emvTerminalInfo;
    }

    /**
     * set init EmvKernelConfig
     * @return
     */
    public static EmvKernelConfig setEmvKernelConfig() {
        if (emvKernelConfig != null) {
            return emvKernelConfig;
        }
        emvKernelConfig = new EmvKernelConfig();
        emvKernelConfig.setbPSE((byte) 1);
        emvKernelConfig.setbCardHolderConfirm((byte) 1);
        emvKernelConfig.setbPreferredDisplayOrder((byte) 0);
        emvKernelConfig.setbLanguateSelect((byte) 1);
        emvKernelConfig.setbDefaultDDOL((byte) 1);
        emvKernelConfig.setbRevocationOfIssuerPublicKey((byte) 1);

        String byPassStatus = TopApplication.sysParam.get(SysParam.APP_PARAM_SUP_BYPASS);
        if ("Y".equals(byPassStatus)) {
            emvKernelConfig.setbBypassPINEntry((byte) 1);
        } else {
            emvKernelConfig.setbBypassPINEntry((byte) 0);
        }
        emvKernelConfig.setbSubBypassPINEntry((byte) 1);
        emvKernelConfig.setbGetdataForPINTryCounter((byte) 1);
        emvKernelConfig.setbFloorLimitCheck((byte) 1);
        emvKernelConfig.setbRandomTransSelection((byte) 1);
        emvKernelConfig.setbVelocityCheck((byte) 1);
        emvKernelConfig.setbTransactionLog((byte) 1);
        emvKernelConfig.setbExceptionFile((byte) 1);
        emvKernelConfig.setbTerminalActionCode((byte) 1);
        emvKernelConfig.setbDefaultActionCodeMethod((byte) EmvErrorCode.EMV_DEFAULT_ACTION_CODE_AFTER_GAC1);
        emvKernelConfig.setbTACIACDefaultSkipedWhenUnableToGoOnline((byte) 0);
        emvKernelConfig.setbCDAFailureDetectedPriorTerminalActionAnalysis((byte) 1);
        emvKernelConfig.setbCDAMethod((byte) EmvErrorCode.EMV_CDA_MODE1);
        emvKernelConfig.setbForcedOnline((byte) 0);
        emvKernelConfig.setbForcedAcceptance((byte) 0);
        emvKernelConfig.setbAdvices((byte) 0);
        emvKernelConfig.setbIssuerReferral((byte) 1);
        emvKernelConfig.setbBatchDataCapture((byte) 0);
        emvKernelConfig.setbOnlineDataCapture((byte) 1);
        emvKernelConfig.setbDefaultTDOL((byte) 1);
        emvKernelConfig.setbTerminalSupportAccountTypeSelection((byte) 1);
//        if ((boolean)SysParam.get(SysParam.PCI_MODE, false)) {
//            emvKernelConfig.setbPCIPINEntry((byte) 1);
//        }
        return emvKernelConfig;
    }
}
