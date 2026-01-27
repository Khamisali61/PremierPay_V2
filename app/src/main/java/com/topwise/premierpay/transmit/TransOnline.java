package com.topwise.premierpay.transmit;

import android.content.Context;
import android.text.TextUtils;

import com.topwise.manager.AppLog;
import com.topwise.manager.utlis.DataUtils;


import com.topwise.toptool.api.convert.IConvert;
import com.topwise.toptool.api.packer.ITlv;
import com.topwise.toptool.api.packer.TlvException;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.daoutils.DaoUtilsStore;
import com.topwise.premierpay.daoutils.entity.TotaTransdata;
import com.topwise.premierpay.param.AidParam;
import com.topwise.premierpay.param.CapkParam;
import com.topwise.premierpay.param.LoadParam;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.core.TransContext;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.Controller;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.GeneralParam;
import com.topwise.premierpay.trans.model.ResponseCode;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.transmit.json.JsonOnline;
import com.topwise.premierpay.transmit.iso8583.Online;

import com.topwise.premierpay.utils.Utils;
import com.topwise.premierpay.utils.ConfiUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class TransOnline {
    private static final String TAG =  TopApplication.APPNANE +TransOnline.class.getSimpleName();

    private static void showErr(int ret, TransProcessListener listener, Context mContext) {
        listener.onHideProgress();
        Device.beepFail();
        String message = TransResult.getMessage(mContext, ret);
        listener.onShowFailWithConfirm(
                mContext.getString(R.string.emv_err_info)
                        + message,
                Component.FAILED_DIALOG_SHOW_TIME);
    }

    private static int checkRspCode(TransData transData, TransProcessListener listener, Context mContext) {
        if (!"00".equals(transData.getResponseCode())) { // Failed
            listener.onHideProgress();
            String field46 = transData.getField46();
            Device.beepFail();
            if (!DataUtils.isNullString(field46)) {
                listener.onShowFailWithConfirm(
                        field46,
                        Component.FAILED_DIALOG_SHOW_TIME);
            } else {
                ResponseCode responseCode = TopApplication.rspCode.parse(transData.getResponseCode());
                listener.onShowFailWithConfirm(
                        mContext.getString(R.string.emv_err_code)
                                + responseCode.getCode()
                                + mContext.getString(R.string.emv_err_info)
                                + responseCode.getMessage(),
                        Component.FAILED_DIALOG_SHOW_TIME);
            }
            return TransResult.ERR_ABORTED;
        }
        return TransResult.SUCC;
    }

    public static int posHandshake(TransProcessListener listener, Context mContext) {
        TransData transData = Component.transInit();
        transData.setTransType(ETransType.TRANS_HANDSHAKE.toString());

        int ret = JsonOnline.getInstance().online(transData, listener);
        if (listener != null) {
            listener.onHideProgress();
        }
        if (ret != TransResult.SUCC) {
            return ret;
        }
        if (listener != null) {
            listener.onShowMessageWithConfirm(mContext.getString(R.string.result_sucess_consume),5);
        }
        return TransResult.SUCC;
    }

    public static int posGetTmk(TransProcessListener listener, Context mContext) {
        TransData transData = Component.transInit();
        transData.setTransType(ETransType.TRANS_GET_TMK.toString());
        int ret = JsonOnline.getInstance().online(transData, listener);
        if (listener != null) {
            listener.onHideProgress();
        }
        if (ret != TransResult.SUCC) {
            return ret;
        }
        if (listener != null) {
            listener.onShowMessageWithConfirm(mContext.getString(R.string.result_sucess_consume),5);
        }
        return TransResult.SUCC;
    }

    /**
     * 终端参数下载交易
     * 从服务端下载参数后，更新终端参数，主要是MMKV存储的终端参数更新
     *
     * @param listener
     * @param mContext
     * @return
     */
    public static int posApplicationDownload(TransProcessListener listener, Context mContext) {
        TransData transData = Component.transInit();
        transData.setTransType(ETransType.PARAM_DOWNLOAD.toString());
        if (listener != null)
            listener.onUpdateProgressTitle(ETransType.PARAM_DOWNLOAD.getTransName());

        int ret = JsonOnline.getInstance().online(transData, listener);
        if (listener != null) {
            listener.onHideProgress();
        }
        if (ret != TransResult.SUCC) {
            showErr(ret, listener,mContext);
            return ret;
        }
//      transData.setResponseCode("03");
        ret = checkRspCode(transData, listener,mContext);
        if (ret != TransResult.SUCC) {
            return ret;
        }
        String field48 = transData.getField48();
        if (ret != TransResult.SUCC) {
            return TransResult.ERR_HOST_REJECT;
        }
        /**
         * tga l   val
         * 001 008 DEMO.jpg //Logo Name
         * 002 012 RBL Bank Ltd //Receipt Header 1
         * 003 012 TOPWISE Test //Receipt Header 2
         * 004 002 MH  //Receipt Header 3
         * 005 002 10  //Tip Percentage
         * 006 008 10017458  //Terminal ID
         * 007 015 107112000076119 //Merchant ID
         * 008 001 1 //Last four Digit Prompt (DefaultEnabled)
         * 009 012 000005000000 //contactless transaction limit
         * 010 012 000000000000  //contactless floor limit
         * 011 012 000000200000 //contactless cvm limit
         * 012 012 000000000000  //Floor limit
         * 013 001 0  //line encryption
         * 014 015 111111111111111  menu
         * 015 057 https://europa-sandbox.perseuspay.com/io/v1.0/h2hpayments //ip
         * */
        String tagVale = "";
        Map<String, String> tlvF48 = Utils.getTlvF48(field48);
        tagVale = tlvF48.get("001");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.PARAM_LOGO_NAME, tagVale);
        tagVale = tlvF48.get("002");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.PARAM_RECEIPT_HEADER_1, tagVale);
        tagVale = tlvF48.get("003");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.PARAM_RECEIPT_HEADER_2, tagVale);
        tagVale = tlvF48.get("004");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.PARAM_RECEIPT_HEADER_3, tagVale);
        tagVale = tlvF48.get("005");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.PARAM_TIP_PERCENTAGE, tagVale);
        tagVale = tlvF48.get("006");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.TERMINAL_ID, tagVale);
        tagVale = tlvF48.get("007");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.MERCH_ID, tagVale);
        tagVale = tlvF48.get("008");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.PARAM_LFDP, tagVale);
        tagVale = tlvF48.get("009");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.PARAM_CTRANS_LIMIT, tagVale);
        tagVale = tlvF48.get("010");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.PARAM_CFLOOR_LIMIT, tagVale);
        tagVale = tlvF48.get("011");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.PARAM_CCVM_LIMIT, tagVale);
        tagVale = tlvF48.get("012");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.PARAM_FLOOR_LIMIT, tagVale);
        tagVale = tlvF48.get("013");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.PARAM_LINE_ENCERYPTION, tagVale);
        tagVale = tlvF48.get("014");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.PARAM_MENU_CONTROL, tagVale);
        tagVale = tlvF48.get("015");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.PARAM_URL, tagVale);

        if (listener != null) {
            listener.onShowSuccessMsgWithConfirm(mContext.getString(R.string.result_sucess_consume),5);
        }
        return TransResult.SUCC;
    }

    /**
     * POS参数下载
     *
     * @param listener
     * @param mContext
     * @return
     */
    public static int posParamDownload(TransProcessListener listener, Context mContext) {
        TransData transData = Component.transInit();
        transData.setTransType(ETransType.PARAM_DOWNLOAD.toString());
        if (listener != null) {
            listener.onUpdateProgressTitle(ETransType.PARAM_DOWNLOAD.getTransName());
        }

        int ret = JsonOnline.getInstance().online(transData, listener);
        Device.closeAllLed();
        if (listener != null) {
            listener.onHideProgress();
        }
        if (ret != TransResult.SUCC) {
            showErr(ret, listener,mContext);
            return ret;
        }
//      transData.setResponseCode("03");
        ret = checkRspCode(transData, listener, mContext);
        if (ret != TransResult.SUCC) {
            return ret;
        }

        String field48 = transData.getField48();
        if (DataUtils.isNullString(field48)) {
            return TransResult.ERR_RECORD_DATA;
        }
        /**
         * tga l   val
         * 001 008 DEMO.jpg //Logo Name
         * 002 012 RBL Bank Ltd //Receipt Header 1
         * 003 012 TOPWISE Test //Receipt Header 2
         * 004 002 MH  //Receipt Header 3
         * 005 002 10  //Tip Percentage
         * 006 008 10017458  //Terminal ID
         * 007 015 107112000076119 //Merchant ID
         * 008 001 1 //Last four Digit Prompt (DefaultEnabled)
         * 009 012 000005000000 //contactless transaction limit
         * 010 012 000000000000  //contactless floor limit
         * 011 012 000000200000 //contactless cvm limit
         * 012 012 000000000000  //Floor limit
         * 013 001 0  //line encryption
         * 014 015 111111111111111  menu
         * 015 057 https://europa-sandbox.perseuspay.com/io/v1.0/h2hpayments //ip
         * */
        String tagVale = "";
        Map<String, String> tlvF48 = Utils.getTlvF48(field48);
        tagVale = tlvF48.get("001");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.PARAM_LOGO_NAME, tagVale);
        tagVale = tlvF48.get("002");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.PARAM_RECEIPT_HEADER_1, tagVale);
        tagVale = tlvF48.get("003");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.PARAM_RECEIPT_HEADER_2, tagVale);
        tagVale = tlvF48.get("004");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.PARAM_RECEIPT_HEADER_3, tagVale);
        tagVale = tlvF48.get("005");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.PARAM_TIP_PERCENTAGE, tagVale);
        tagVale = tlvF48.get("006");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.TERMINAL_ID, tagVale);
        tagVale = tlvF48.get("007");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.MERCH_ID, tagVale);
        tagVale = tlvF48.get("008");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.PARAM_LFDP, tagVale);
        tagVale = tlvF48.get("009");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.PARAM_CTRANS_LIMIT, tagVale);
        tagVale = tlvF48.get("010");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.PARAM_CFLOOR_LIMIT, tagVale);
        tagVale = tlvF48.get("011");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.PARAM_CCVM_LIMIT, tagVale);
        tagVale = tlvF48.get("012");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.PARAM_FLOOR_LIMIT, tagVale);
        tagVale = tlvF48.get("013");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.PARAM_LINE_ENCERYPTION, tagVale);
        tagVale = tlvF48.get("014");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.PARAM_MENU_CONTROL, tagVale);
        tagVale = tlvF48.get("015");
        if (!TextUtils.isEmpty(tagVale))
            TopApplication.sysParam.set(SysParam.PARAM_URL, tagVale);

        if (listener != null) {
            listener.onShowMessageWithConfirm(mContext.getString(R.string.result_sucess_consume),5);
        }
        return TransResult.SUCC;
    }

    /**
     * POS签到下发工作密钥[PEK + MAK + TDK（可选）]
     *
     * @param listener
     * @param context
     * @return
     */
    public static int posLogon(TransProcessListener listener, Context context) {
//        if (ConfiUtils.isDebug){
//            TopApplication.controller.set(Controller.POS_LOGON_STATUS,Controller.Constant.YES);
//            return  TransResult.SUCC;
//        }
        TransData transData = Component.transInit();
        transData.setTransType(ETransType.LOGON.toString());
        String desType = TopApplication.sysParam.get(SysParam.KEY_ALGORITHM);
        if (transData.getIsEncTrack() && SysParam.Constant.TRIP_DES.equals(desType)) {
            ETransType.LOGON.setNetCode("004"); // f60.3
        } else if (SysParam.Constant.TRIP_DES.equals(desType)) {
            ETransType.LOGON.setNetCode("003"); // f60.3
        } else {
            ETransType.LOGON.setNetCode("001");
//            isTripDes = false;
        }

        if (listener != null) {
            listener.onUpdateProgressTitle(ETransType.LOGON.getTransName());
        }

        int ret = Online.getInstance().online(transData, listener);
        if (listener != null) {
            listener.onHideProgress();
        }
        if (ret != TransResult.SUCC) {
            return ret;
        }
        ret = checkRspCode(transData, listener,context);
        if (ret != TransResult.SUCC) {
            return ret;
        }

        //update batch
        long batchNo = transData.getBatchNo();
        AppLog.i(TAG,"posLogon update batch " + batchNo);
        TopApplication.sysParam.set(SysParam.BATCH_NO, String.valueOf(batchNo));

        //update data time
        Calendar calender = Calendar.getInstance();
        int year = calender.get(Calendar.YEAR);
        String timestamp = String.valueOf(year) + transData.getDate() + transData.getTime();
        AppLog.i(TAG, "posLogon timestamp = " + timestamp);
        Device.updateSystemTime(timestamp);

        IConvert convert = TopApplication.convert;

        if (transData.getField62() == null) // F62判空，防止strToBcd参数异常
            return TransResult.ERR_ABORTED;

        AppLog.i(TAG, "posLogon getField62 = " + transData.getField62());

        byte[] f62 = convert.strToBcd(transData.getField62(), IConvert.EPaddingPosition.PADDING_LEFT);
        // 工作密钥，若长度域不为24或40或56或60或84,格式有误
        if (f62 != null) {
            if (f62.length != 40 && f62.length != 60) {
                return TransResult.ERR_TWK_LENGTH;
            }
        }

        // 解析密钥和KCV，下装到PINPAD并保存MMKV
        int index = 0;

        // PINKEY
        byte[] pinKey = new byte[16];
        byte[] pinKeyKCV = new byte[4];

        System.arraycopy(f62, index, pinKey, 0, pinKey.length);
        index += pinKey.length;
        System.arraycopy(f62, index, pinKeyKCV, 0, pinKeyKCV.length);
        index += pinKeyKCV.length;

        boolean tpk = false;
        if (ConfiUtils.isDebug) {
            tpk = Device.writeTPK(pinKey, null);
        } else {
            tpk = Device.writeTPK(pinKey, pinKeyKCV);
        }
        AppLog.i(TAG,"posLogon writeTPK " + tpk);
        if (!tpk) {
            return TransResult.ERR_WRITE_PIN;
        }
        AppLog.i(TAG,"posLogon writeTPK " + TopApplication.convert.bcdToStr(pinKey));
        // 保存TPK
        GeneralParam.getInstance().set(GeneralParam.TPK, TopApplication.convert.bcdToStr(pinKey));

        // MACKEY
        byte[] macKey = new byte[16];
        byte[] macKeyKCV = new byte[4];
        System.arraycopy(f62, index, macKey, 0, macKey.length);
        System.arraycopy(macKey, 0, macKey, 8, 8);
        index += 16;
        System.arraycopy(f62, index, macKeyKCV, 0, 4);
        index += 4;

        if (!ConfiUtils.isDebug) {
            boolean mak = Device.writeMAK(macKey, macKeyKCV);
            AppLog.i(TAG, "posLogon writeMAK " + mak);
            if (!mak) {
                return TransResult.ERR_WRITE_MAK;
            }
            AppLog.i(TAG, "posLogon writeMAK " + TopApplication.convert.bcdToStr(macKey));
            GeneralParam.getInstance().set(GeneralParam.TAK, TopApplication.convert.bcdToStr(macKey));

            // TDK并非一定下发，有的客户磁道不加密
            if (f62.length > 40) {
                byte[] trackKey = new byte[16];
                byte[] trackKeyKCV = new byte[4];
                System.arraycopy(f62, index, trackKey, 0, 16);
                index += 16;
                System.arraycopy(f62, index, trackKeyKCV, 0, 4);
                index += 4;

                boolean tdk = Device.writeTDK(trackKey, trackKeyKCV);
                AppLog.i(TAG, "posLogon writeTDK " + tdk);
                if (!tdk) {
                    return TransResult.ERR_WRITE_TDK;
                }
                GeneralParam.getInstance().set(GeneralParam.TDK, TopApplication.convert.bcdToStr(trackKey));
            }
        }
        TopApplication.controller.set(Controller.POS_LOGON_STATUS,Controller.Constant.YES);
            // 保存TDK
//            TopApplication.generalParam.set(GeneralParam.TDK,
//                    TopApplication.convert.bcdToStr(trackKey));
        return TransResult.SUCC;
    }

    /**
     * 检查是否需要下载参数
     * @param checkHeader 检查返回报文头
     * @param checkFirst  是否是第一次
     * @param listener
     * @return
     */
    public static int downLoadCheck(boolean checkHeader, boolean checkFirst, TransProcessListener listener) {
        int ret = 0;
        if (ConfiUtils.isDebug) { // 测试
            return TransResult.SUCC;
        }

        Controller controller = TopApplication.controller;
        if (checkHeader) {

        }

        if (checkFirst) {
            // emv公钥下载
            if (controller.get(Controller.NEED_DOWN_CAPK) == Controller.Constant.YES) {
                ret = emvCapkDl(listener);
                if (ret != 0) {
                    return ret;
                }
            }

            // AID参数下载
            if (controller.get(Controller.NEED_DOWN_AID) == Controller.Constant.YES) {
                ret = emvAidDl(listener);
                if (ret != 0) {
                    return ret;
                }
            }

            // 非接业务参数下载
            if (controller.get(Controller.NEED_DOWN_CLPARA) == Controller.Constant.YES) {
                ret = piccDownloadParam(listener);
                if (ret != 0) {
                    return ret;
                }
                controller.set(Controller.NEED_DOWN_CLPARA, Controller.Constant.NO);
            }

            // 黑名单下载
            if (controller.get(Controller.NEED_DOWN_BLACK) == Controller.Constant.YES) {
                ret = blackDl(listener);
                if (ret != 0) {
                    return ret;
                }
                controller.set(Controller.NEED_DOWN_BLACK, Controller.Constant.NO);
            }
        }
        return TransResult.SUCC;
    }

    /**
     * 黑名单下载
     *
     * @param listener
     * @return
     */
    private static int blackDl(TransProcessListener listener) {
        return TransResult.SUCC;
    }

    /**
     * 非接业务参数下载
     *
     * @param listener
     * @return
     */
    private static int piccDownloadParam(TransProcessListener listener) {
        IConvert convert = TopApplication.convert;
        TransData transData = Component.transInit();
        transData.setTransType(ETransType.PICC_DOWNLOAD_PARAM.toString());
        listener.onUpdateProgressTitle(ETransType.PICC_DOWNLOAD_PARAM.getTransName());
        int ret = Online.getInstance().online(transData, listener);
        if (listener != null) {
            listener.onHideProgress();
        }
        if (ret != TransResult.SUCC) {
            return ret;
        }

        // 平台拒绝
        ret = checkRspCode(transData, listener, TopApplication.mApp);
        if (ret != TransResult.SUCC) {
            return ret;
        }

        // 解析返回数据
        String f62 = transData.getField62();
        if (f62 != null) {
            f62 = new String(convert.strToBcd(f62, IConvert.EPaddingPosition.PADDING_LEFT));
            AppLog.d(TAG," PICC_DOWNLOAD_PARAM " + f62);

            // 非接交易通道开关
            String value = getPiccParamValue("FF805D", f62);
            if (!DataUtils.isNullString(value)) {
                AppLog.d(TAG," 非接交易通道开关 FF805D var  " + value);
//                TopApplication.sysParam.set(SysParam.QUICK_PASS_TRANS_SWITCH,
//                        "1".equals(value) ? SysParam.Constant.YES : SysParam.Constant.NO);
            }

//
            // 闪卡当笔重刷处理时间
            value = getPiccParamValue("FF803A", f62);
            if (!DataUtils.isNullString(value)) {
                AppLog.d(TAG," 闪卡当笔重刷处理时间 FF803A var  " + value);
            }
//            if (value != null)
//                TopApplication.sysParam.set(SysParam.QUICK_PASS_TRANS_BRUSH_TIMES, value);


            // 闪卡记录可处理时间
            value = getPiccParamValue("FF803C", f62);
            if (!DataUtils.isNullString(value)) {
                AppLog.d(TAG," 闪卡记录可处理时间 FF803C var  " + value);
            }
//            if (value != null)
//                TopApplication.sysParam.set(SysParam.QUICK_PASS_TRANS_TIMES, value);

            // 非接快速业务（QPS）免密限额
            value = getPiccParamValue("FF8058", f62);
            if (!DataUtils.isNullString(value)){
                AppLog.d(TAG," 非接快速业务（QPS）免密限额 FF8058 var  " + value);
            }
//            if (value != null)
//                TopApplication.sysParam.set(SysParam.QUICK_PASS_TRANS_PIN_FREE_AMOUNT,
//                        String.valueOf(Long.parseLong(value)));

            // 非接快速业务标识
            value = getPiccParamValue("FF8054", f62);
            if (!DataUtils.isNullString(value)){
                AppLog.d(TAG," 非接快速业务标识 FF8054 var  " + value);
            }
//            if (value != null)
//                TopApplication.sysParam.set(SysParam.QUICK_PASS_TRANS_FLAG,
//                        "1".equals(value) ? SysParam.Constant.YES : SysParam.Constant.NO);

            // BIN表A标识
            value = getPiccParamValue("FF8055", f62);
            if (!DataUtils.isNullString(value)){
                AppLog.d(TAG," BIN表A标识 FF8055 var  " + value);
            }
//            if (value != null)
//                TopApplication.sysParam.set(SysParam.QUICK_PASS_TRANS_BIN_A_FLAG,
//                        "1".equals(value) ? SysParam.Constant.YES : SysParam.Constant.NO);

            // BIN表B标识
            value = getPiccParamValue("FF8056", f62);
            if (!DataUtils.isNullString(value)){
                AppLog.d(TAG," BIN表B标识 FF8056 var  " + value);
            }
//            if (value != null)
//                TopApplication.sysParam.set(SysParam.QUICK_PASS_TRANS_BIN_B_FLAG,
//                        "1".equals(value) ? SysParam.Constant.YES : SysParam.Constant.NO);

            // CDCVM标识
            value = getPiccParamValue("FF8057", f62);
            if (!DataUtils.isNullString(value)){
                AppLog.d(TAG," CDCVM标识 FF8057 var  " + value);
            }
//            if (value != null)
//                TopApplication.sysParam.set(SysParam.QUICK_PASS_TRANS_CDCVM_FLAG,
//                        "1".equals(value) ? SysParam.Constant.YES : SysParam.Constant.NO);
            // 免签限额
            value = getPiccParamValue("FF8059", f62);
            if (!DataUtils.isNullString(value)){
                AppLog.d(TAG," 免签限额 FF8059 var  " + value);
            }
//            if (value != null)
//                TopApplication.sysParam.set(SysParam.QUICK_PASS_TRANS_SIGN_FREE_AMOUNT,
//                        String.valueOf(Long.parseLong(value)));

            // 免签标识
            value = getPiccParamValue("FF805A", f62);
            if (!DataUtils.isNullString(value)){
                AppLog.d(TAG," 免签标识 FF805A var  " + value);
            }
//            if (value != null)
//                TopApplication.sysParam.set(SysParam.QUICK_PASS_TRANS_SIGN_FREE_FLAG,
//                        "1".equals(value) ? SysParam.Constant.YES : SysParam.Constant.NO);
        }

        // PICC参数下载结束通知
        transData = Component.transInit();
        transData.setTransType(ETransType.PICC_DOWNLOAD_PARAM_END.toString());
        listener.onUpdateProgressTitle(ETransType.PICC_DOWNLOAD_PARAM_END.getTransName());
        ret = Online.getInstance().online(transData, listener);
        if (listener != null) {
            listener.onHideProgress();
        }
        if (ret != TransResult.SUCC) {
            return ret;
        }

        ret = checkRspCode(transData, listener,TopApplication.mApp);
        return ret;
    }

    private static String getPiccParamValue(String tag, String piccParam) {
        int index = piccParam.indexOf(tag);
        if (index != -1) {
            String lenth = piccParam.substring(index + 6, index + 9);
            int integer = Integer.valueOf(lenth);
            return piccParam.substring(index + 9, index + 9 + integer);
        }
        return null;
    }

    /**
     * EMV AID参数下载
     *
     * @param listener
     * @return
     */
    private static int emvAidDl(TransProcessListener listener) {
        TransData transData = Component.transInit();
        IConvert convert = TopApplication.convert;
        ITlv tlv = TopApplication.packer.getTlv();
        ArrayList<byte[]> paramInfo = new ArrayList<byte[]>();
        int cnt = 0;
        int ret;
        boolean needDl = true;
        LoadParam loadParam = new AidParam();
        loadParam.DelectAll();
        TopApplication.controller.set(Controller.NEED_DOWN_AID, Controller.Constant.YES);
        while (true) {
            transData = Component.transInit();
            transData.setTransType(ETransType.EMV_MON_PARAM.toString()); // IC卡参数下载状态上送

            String caMonStr = "1" + String.format("%02d", cnt);
            transData.setField62(caMonStr);
            listener.onUpdateProgressTitle(ETransType.EMV_MON_PARAM.getTransName());
            ret = Online.getInstance().online(transData, listener);
            if (ret != TransResult.SUCC) {
                if (listener != null) {
                    listener.onHideProgress();
                }
                return ret;
            }
            ret = checkRspCode(transData, listener,TopApplication.mApp);
            if (ret != TransResult.SUCC) {
                return ret;
            }

            if (transData.getField62() == null) // F62判空，防止strToBcd参数异常
                return TransResult.ERR_ABORTED;

            byte[] field62 = convert.strToBcd(transData.getField62(),
                    IConvert.EPaddingPosition.PADDING_LEFT);
            if (field62[0] == 0x30) {
                // NOTE:提示无参数下载
                needDl = false;
                break;
            } else {
                byte[] f62 = new byte[field62.length - 1];
                System.arraycopy(field62, 1, f62, 0, f62.length);
                List<ITlv.ITlvDataObj> list;
                try {
                    list = tlv.unpack(f62).getDataObjectList();
                    for (int i = 0; i < list.size(); i++) {
                        paramInfo.add(tlv.pack(list.get(i)));
                        cnt++;
                    }
                    // 还有后续参数下载
                    if (field62[0] == 0x32) {
                        continue;
                    }

                } catch (TlvException e) {

                    e.printStackTrace();
                }
                break;
            }

        }
        if (!needDl) { // 无需下载
            if (listener != null) {
                listener.onHideProgress();
            }
            return TransResult.SUCC;
        }

        AppLog.d(TAG," paramInfo.size=========== " + paramInfo.size());

        // 下载emv aid参数
        for (int i = 0; i < cnt; i++) {
            transData = Component.transInit();
            transData.setTransType(ETransType.EMV_PARAM_DOWN.toString()); // IC卡AID参数下载
            String f62 = convert.bcdToStr(paramInfo.get(i));
            transData.setField62(f62);
            if (listener != null) {
                listener.onUpdateProgressTitle(
                        ETransType.EMV_PARAM_DOWN.getTransName() + "[" + (i + 1) + "/" + cnt + "]");
            }

            ret = Online.getInstance().online(transData, listener);
            if (ret != TransResult.SUCC) {
                listener.onHideProgress();
                return ret;
            }

            ret = checkRspCode(transData, listener,TopApplication.mApp);
            if (ret != TransResult.SUCC) {
                return ret;
            }

            // 保存aid
            if (transData.getField62() == null) // F62判空，防止strToBcd参数异常
                return TransResult.ERR_ABORTED;

            byte[] bF62 = TopApplication.convert.strToBcd(transData.getField62(), IConvert.EPaddingPosition.PADDING_LEFT);

            if (bF62[0] == 0x30) // 无对应aid下载
                continue;

            byte[] aid = new byte[bF62.length - 1];
            System.arraycopy(bF62, 1, aid, 0, aid.length);

            // 保存一条AID参数
            loadParam.save(convert.bcdToStr(aid));
        }

        // 下载aid结束
        listener.onUpdateProgressTitle(ETransType.EMV_PARAM_DOWN_END.getTransName());

        transData = Component.transInit();
        transData.setTransType(ETransType.EMV_PARAM_DOWN_END.toString()); // IC卡AID参数下载结束
        ret = Online.getInstance().online(transData, listener);
        if (listener != null) {
            listener.onHideProgress();
        }
        if (ret == TransResult.SUCC) {
            ret = checkRspCode(transData, listener,TopApplication.mApp);
            if (ret == TransResult.SUCC) {
                TopApplication.controller.set(Controller.NEED_DOWN_AID, Controller.Constant.NO);
            }
            return ret;
        }
        return ret;
    }

    /**
     * EMV CAPK下载
     *
     * @param listener
     * @return
     */
    private static int emvCapkDl(TransProcessListener listener) {
        TransData transData = Component.transInit();
        IConvert convert = TopApplication.convert;
        ArrayList<byte[]> capkInfo = new ArrayList<byte[]>();
        int cnt = 0;
        int ret;
        boolean needDl = true;
        LoadParam loadParam = new CapkParam();
        loadParam.DelectAll();
        // Utils.deleteAllCapk(); //TODO:公钥存文件系统的处理
        TopApplication.controller.set(Controller.NEED_DOWN_CAPK, Controller.Constant.YES);
//        String supportSm = TopApplication.sysParam.get(SysParam.SUPPORT_SM);
        while (true) {
            transData = Component.transInit();
            ETransType.EMV_MON_CA.setNetCode("372");

            transData.setTransType(ETransType.EMV_MON_CA.toString()); // 公钥参数查询
            String caMonStr = "1" + String.format("%02d", cnt);
            transData.setField62(caMonStr);
            listener.onUpdateProgressTitle(ETransType.EMV_MON_CA.getTransName());
            ret = Online.getInstance().online(transData, listener);
            if (ret != TransResult.SUCC) {
                if (listener != null) {
                    listener.onHideProgress();
                }
                return ret;
            }
            ret = checkRspCode(transData, listener, TopApplication.mApp);
            if (ret != TransResult.SUCC) {
                return ret;
            }

            if (transData.getField62() == null) // F62判空，防止strToBcd参数异常
                return TransResult.ERR_RECORD_DATA;

            byte[] field62 = convert.strToBcd(transData.getField62(),
                    IConvert.EPaddingPosition.PADDING_LEFT);
            if (field62[0] == 0x30) {
                // 无capk下载
                needDl = false;
                break;
            } else {
                int len = 0;
                len += 1;
                while (len < field62.length - 1) {
                    byte[] capk = new byte[23];
                    System.arraycopy(field62, len, capk, 0, 23);
                    capkInfo.add(capk);
                    len += 23;
                    cnt++;
                }
                if (field62[0] == 0x32) {
                    continue;
                }
                break;
            }
        }
        AppLog.d(TAG," capkInfo.size=========== " + capkInfo.size());
        if (!needDl) {
            if (listener != null) {
                listener.onHideProgress();
            }
            return TransResult.SUCC;
        }

        // 下载capk
        for (int i = 0; i < cnt; i++) {
            transData = Component.transInit();
            transData.setTransType(ETransType.EMV_CA_DOWN.toString()); // IC卡公钥参数下载

            ITlv tlv = TopApplication.packer.getTlv();
            ITlv.ITlvDataObjList tlvList = tlv.createTlvDataObjectList();
            byte[] data = null;
            try {
                ITlv.ITlvDataObjList list = tlv.unpack(capkInfo.get(i));
                ITlv.ITlvDataObj tlv9F06 = list.getByTag(0x9f06);
                ITlv.ITlvDataObj tlv9F22 = list.getByTag(0x9f22);
                tlvList.addDataObj(tlv9F06);
                tlvList.addDataObj(tlv9F22);
                data = tlv.pack(tlvList);
            } catch (TlvException e1) {
                e1.printStackTrace();
                continue;
            }
            String f62 = convert.bcdToStr(data);
            transData.setField62(f62);
            if (listener != null) {
                listener.onUpdateProgressTitle(ETransType.EMV_CA_DOWN.getTransName() + "[" + (i + 1) + "/" + cnt + "]");
            }

            ret = Online.getInstance().online(transData, listener);
            if (ret != TransResult.SUCC) {
                listener.onHideProgress();
                return ret;
            }

            ret = checkRspCode(transData, listener,TopApplication.mApp);
            if (ret != TransResult.SUCC) {
                return ret;
            }

            // 保存capk
            if (transData.getField62() == null) // F62判空，防止strToBcd参数异常
                return TransResult.ERR_ABORTED;

            byte[] bF62 = convert.strToBcd(transData.getField62(), IConvert.EPaddingPosition.PADDING_LEFT);
            if (bF62[0] == 0x30) // 无对应公钥下载
                continue;
            byte[] capk = new byte[bF62.length - 1];
            System.arraycopy(bF62, 1, capk, 0, capk.length);

            // 保存一条公钥
            loadParam.save(convert.bcdToStr(capk));
        } // End of for

        // IC卡公钥下载结束
        listener.onUpdateProgressTitle(ETransType.EMV_CA_DOWN_END.getTransName());
        transData = Component.transInit();
        transData.setTransType(ETransType.EMV_CA_DOWN_END.toString());
        ret = Online.getInstance().online(transData, listener);
        if (listener != null) {
            listener.onHideProgress();
        }
        if (ret == TransResult.SUCC) {
            ret = checkRspCode(transData, listener, TopApplication.mApp);
            if (ret == TransResult.SUCC) {
                TopApplication.controller.set(Controller.NEED_DOWN_CAPK, Controller.Constant.NO);
            }
            return ret;
        }
        return ret;
    }

    /**
     *
     * @param totaTransdata
     * @param listener
     * @return
     */
    public static int settle(TotaTransdata totaTransdata, TransProcessListenerImpl listener) {
        int ret = TransResult.SUCC;
        if (TopApplication.controller.get(Controller.BATCH_UP_STATUS) != Controller.Constant.BATCH_UP) {
            // 上送联机交易的电子签名

            // 处理脱机交易

            // 上送脱机交易的电子签名

            // 重新上送 上送失败的电子签名

            // 处理脚本

            // 处理冲正

            // 冲正mac错则直接返回

            //结算
            ret = settleRequest(totaTransdata, listener);
            if (ret != TransResult.SUCC) {
                listener.onHideProgress();
                return ret;
            }
        }

        int transCount = DaoUtilsStore.getInstance().getmTransDaoUtils().getTransCount();
        if (transCount > 0) {
            ret = batchUp(listener);
            if (ret != TransResult.SUCC) {
                listener.onHideProgress();
                return ret;
            }
        }
        return TransResult.SUCC;
    }

    /**
     * Settlement
     * @param total
     * @param listener
     * @return
     */
    private static int settleRequest(TotaTransdata total, TransProcessListenerImpl listener) {
        TransData transData = Component.transInit();
        transData.setTransType(ETransType.TRANS_SETTLE.toString());
        listener.onUpdateProgressTitle(ETransType.TRANS_SETTLE.getTransName());

        String debitAmt;
        String debitNum;
        String creditAmt;
        String creditNum;

        String fDebitAmt;
        String fDebitNum;
        String fCreditAmt;
        String fCreditNum;

        String buf;
        //按借 贷卡方式计算
        Long bankSaleAmountTotal = total.getBankSaleAmountTotal()  != null  ?total.getBankSaleAmountTotal() : 0L;
        Long bankSaleNumberTotal = total.getBankSaleNumberTotal()!= null  ?total.getBankSaleNumberTotal() : 0L;

        Long bankVoidAmountTotal = total.getBankVoidAmountTotal()!= null  ?total.getBankVoidAmountTotal() : 0L;
        Long bankVoidNumberTotal = total.getBankVoidNumberTotal()!= null  ?total.getBankVoidNumberTotal() : 0L;

        Long bankRefundAmountTotal = total.getBankRefundAmountTotal()!= null  ?total.getBankRefundAmountTotal() : 0L;
        Long bankRefundNumberTotal = total.getBankRefundNumberTotal()!= null  ?total.getBankRefundNumberTotal() : 0L;

        Long qrSaleAmountTotal = total.getQrSaleAmountTotal() != null  ?total.getQrSaleAmountTotal() : 0L;
        Long qrSaleNumberTotal = total.getQrSaleNumberTotal()!= null  ?total.getQrSaleNumberTotal() : 0L;

        Long qrVoidAmountTotal = total.getQrVoidAmountTotal()!= null  ?total.getQrVoidAmountTotal() : 0L;
        Long qrVoidNumberTotal = total.getQrVoidNumberTotal()!= null  ?total.getQrVoidNumberTotal() : 0L;

        Long qrRefundAmountTotal = total.getQrRefundAmountTotal()!= null  ?total.getQrRefundAmountTotal() : 0L;
        Long qrRefundNumberTotal = total.getQrRefundNumberTotal()!= null  ?total.getQrRefundNumberTotal() : 0L;

        Long rmbDebitAmount = (bankSaleAmountTotal + qrSaleAmountTotal)
                -(bankVoidAmountTotal + qrVoidAmountTotal);
        Long rmbDebitNum = (bankSaleNumberTotal + qrSaleNumberTotal)
                -(bankVoidNumberTotal + qrVoidNumberTotal);

        Long rmbCreditAmount = bankRefundAmountTotal + qrRefundAmountTotal;
        Long rmbCreditNum = bankRefundNumberTotal + qrRefundNumberTotal;

        debitAmt = String.format("%012d", rmbDebitAmount);
        debitNum = String.format("%03d", rmbDebitNum);
        creditAmt = String.format("%012d", rmbCreditAmount);
        creditNum = String.format("%03d", rmbCreditNum);
        buf = debitAmt + debitNum + creditAmt + creditNum + "0";
        fDebitAmt = String.format("%012d", 0);
        fDebitNum = String.format("%03d", 0);
        fCreditAmt = String.format("%012d", 0);
        fCreditNum = String.format("%03d", 0);
        buf += fDebitAmt + fDebitNum + fCreditAmt + fCreditNum + "0";
        transData.setField48(buf);

        int ret = Online.getInstance().online(transData, listener);
        if (listener != null) {
            listener.onHideProgress();
        }
        if (ret != TransResult.SUCC) {
            return ret;
        }
        String field48 = transData.getField48();
        if (field48 == null || field48.length() < 61) // 判空F48以及长度
            return TransResult.ERR_ABORTED;

        char rmbResult = field48.charAt(30);
        char frnResult = field48.charAt(61);
        TopApplication.controller.set(Controller.RMB_RESULT, Integer.parseInt("" + rmbResult));
        TopApplication.controller.set(Controller.FRN_RESULT, Integer.parseInt("" + frnResult));
        // 存结算应答码
        if ((rmbResult == '1') && frnResult == '1') { // 对账平
            TopApplication.controller.set(Controller.BATCH_UP_TYPE, Controller.Constant.ICLOG);
        } else if (rmbResult != '1' && frnResult == '1') { // 内卡对账不平
            TopApplication.controller.set(Controller.BATCH_UP_TYPE, Controller.Constant.RMBLOG);
        } else if (rmbResult == '1' && frnResult != '1') { // 外卡对账不平
            TopApplication.controller.set(Controller.BATCH_UP_TYPE, Controller.Constant.FRNLOG);
        } else if (rmbResult != '1' && frnResult != '1') { // 内外卡对账不平
            TopApplication.controller.set(Controller.BATCH_UP_TYPE, Controller.Constant.ALLLOG);
        }
        TopApplication.controller.set(Controller.BATCH_UP_STATUS, Controller.Constant.BATCH_UP);
        TopApplication.controller.set(Controller.BATCH_NUM, 0);
        return TransResult.SUCC;
    }

    /**
     * 批上送
     * @param listener
     * @return
     */
    private static int batchUp(TransProcessListenerImpl listener) {
        int ret = 0;
        listener.onUpdateProgressTitle(ETransType.BATCH_UP.getTransName());
        // 获取交易记录条数
        long cnt = DaoUtilsStore.getInstance().getmTransDaoUtils().getTransCount();

        if (cnt <= 0) {
            TopApplication.controller.set(Controller.BATCH_UP_STATUS, Controller.Constant.WORKED);
            return TransResult.ERR_NO_TRANS;
        }
        // 获取交易重复次数
        int resendTimes = 1;
        // Integer.parseInt(TopApplication.sysParam.get(SysParam.RESEND_TIMES));
        int sendCnt = 0;
        final boolean[] left = new boolean[] { false };
        int batchUpType = TopApplication.controller.get(Controller.BATCH_UP_TYPE);
        while (sendCnt < resendTimes ){
            // 1)(对账平不送)全部磁条卡离线类交易，包括离线结算和结算调整
            // 2)(对账平不送)基于PBOC标准的借/贷记IC卡脱机消费(含小额支付)成功交易
            ret = allPbocOfflineBatch(batchUpType, listener, new BatchUpListener() {

                @Override
                public void onLeftResult(boolean l) {
                    left[0] = l;
                }
            });
            if (ret != TransResult.SUCC) {
                return ret;
            }
            // 3)(不存在)基于PBOC标准的电子钱包IC卡脱机消费成功交易 --- 不存在
            // 4)(对账平不送)全部磁条卡的请求类联机成功交易明细
            ret = allMagCardTransBatch(batchUpType, listener, new BatchUpListener() {

                @Override
                public void onLeftResult(boolean l) {
                    left[0] = l;
                }
            });
            if (ret != TransResult.SUCC) {
                return ret;
            }
            // 5)(对账平不送)磁条卡和基于PBOC借/贷记标准IC卡的通知类交易明细，包括退货和预授权完成(通知)交易
            ret = adviceTransBatchUp(batchUpType, listener, new BatchUpListener() {

                @Override
                public void onLeftResult(boolean l) {
                    left[0] = l;
                }
            });
            if (ret != TransResult.SUCC) {
                return ret;
            }
            // 6)(对账平也送)为了上送基于PBOC标准的借/贷记IC卡成功交易产生的TC值，所有成功的IC卡借贷记联机交易明细全部重新上送
            ret = allICCardTransBatchUp(batchUpType, listener, new BatchUpListener() {

                @Override
                public void onLeftResult(boolean l) {
                    left[0] = l;
                }
            });
            if (ret != TransResult.SUCC) {
                return ret;
            }
            // 7)(对账平也送)为了让发卡方了解基于PBOC标准的借/贷记IC卡脱机消费(含小额支付)交易的全部情况，上送所有失败的脱机消费交易明细
            // 8)(对账平也送)为了让发卡方防范基于PBOC标准的借/贷记IC卡风险交易，上送所有ARPC错但卡片仍然承兑的IC卡借贷记联机交易明细
            ret = allArpcErrIccTransBatchUp(batchUpType, listener, new BatchUpListener() {
                @Override
                public void onLeftResult(boolean l) {
                    left[0] = l;
                }
            });
            if (ret != TransResult.SUCC) {
                return ret;
            }
            // 9)(不存在)为了上送基于PBOC标准的电子钱包IC卡成功圈存交易产生的TAC值，上送所有圈存确认的交易明细
            if (left[0]) {
                left[0] = false;
                sendCnt++;
                continue;
            }else {
                break;
            }
        }
        // 10)(对账平也送)最后需上送批上送结束报文
        ret = batchUpEnd(batchUpType, listener);
        if (ret != TransResult.SUCC) {
            return ret;
        }
        return TransResult.SUCC;
    }
    private static int allArpcErrIccTransBatchUp(int batchUpType, TransProcessListener listener,
                                                 BatchUpListener batchUpListener) {
        int ret = TransResult.SUCC;
        int cnt = 0;


        List<TransData> allTrans = DaoUtilsStore.getInstance().getmTransDaoUtils().getallICCardTransBatchUp(TransData.class);

        /******************************** SQL优化 ***************************/
        List<ETransType> types = new ArrayList<ETransType>();
        types.add(ETransType.TRANS_SALE );

        if (allTrans == null || allTrans.size() == 0) {
            return TransResult.SUCC;
        }
        int transCnt = (int) allTrans.size();
        TransData transLog;
        int batchNum = TopApplication.controller.get(Controller.BATCH_NUM);
        for (cnt = 0; cnt < transCnt; cnt++) {
            transLog = allTrans.get(cnt);

            IConvert convert = TopApplication.convert;
            byte[] tvr = null;
            String s = transLog.getTvr();
            tvr = convert.strToBcd(s, IConvert.EPaddingPosition.PADDING_LEFT);
            if ((tvr[4] & 0x40) == 0x00) { // ARPC不错不在此送
                continue;
            }
            TransData transLogClone = transLog.clone();
            Component.transInit(transLogClone);
            transLogClone.setTransType(ETransType.IC_FAIL_BAT.toString());
            transLogClone.setTransNo(transLog.getTransNo());
            String f60 = "00" + String.format("%06d", transLog.getBatchNo());
            if (batchUpType != Controller.Constant.ICLOG) { // 对账不平
                f60 += "206";
            } else {
                f60 += "204";
            }
            transLogClone.setField60(f60);
            String f62 = "71";
            if ("CUP".equals(transLog.getInterOrgCode())) {
                f62 += "00";
            } else {
                f62 += "01";
            }
            f62 += "05";
            f62 += transLog.getAmount();
            f62 += "156";
            f62 += "22";
            transLogClone.setField62(f62);
            ret = Online.getInstance().online(transLogClone, listener);
            if (ret != TransResult.SUCC) {
                if (ret == TransResult.ERR_RECV) { // 批上送交易无应答时，终端应在本轮上送完毕后再重发，而非立即重发
                    batchUpListener.onLeftResult(true);
                    continue;
                }
                return ret;
            } else if (ret == TransResult.SUCC) {
                ResponseCode responseCode = TopApplication.rspCode.parse(transLogClone.getResponseCode());
                transLogClone.setResponseMsg(responseCode.getMessage());
                // 返回码失败处理
                if (!"00".equals(transLogClone.getResponseCode()) && !"94".equals(transLogClone.getResponseCode())) {

                    if (listener != null) {
                        listener.onShowFailWithConfirm(
                                TransContext.getInstance().getCurrentContext().getString(R.string.emv_err_code)
                                        + responseCode.getCode()
                                        + TransContext.getInstance().getCurrentContext()
                                        .getString(R.string.emv_err_info)
                                        + responseCode.getMessage(),
                               8);
                    }
                }
            }

            transLog.setIsUpload(true);
            if (!DaoUtilsStore.getInstance().getmTransDaoUtils().update(transLog)) {
                return TransResult.ERR_ABORTED;
            }
            batchNum++;
            AppLog.e("settle","allArpcErrIccTransBatchUp batchNum=== " + batchNum);
            TopApplication.controller.set(Controller.BATCH_NUM, batchNum);
        }
        return TransResult.SUCC;
    }

    private static int adviceTransBatchUp(int batchUpType, TransProcessListener listener,
                                          BatchUpListener batchUpListener){

        int ret = TransResult.SUCC;
        int cnt = 0;

        if (batchUpType == Controller.Constant.ICLOG) {
            return TransResult.SUCC;
        }

        List<TransData> allTrans = DaoUtilsStore.getInstance().getmTransDaoUtils().getadviceTransBatchUp(TransData.class,batchUpType);

        if (allTrans == null || allTrans.size() == 0) {
            return TransResult.SUCC;
        }
        int transCnt = (int) allTrans.size();
        TransData transLog;
        int batchNum = TopApplication.controller.get(Controller.BATCH_NUM);
        for (cnt = 0; cnt < transCnt; cnt++) {
            transLog = allTrans.get(cnt);

            TransData transLogClone = transLog.clone();
            ETransType transType = ETransType.valueOf(transLog.getTransType());

            transLogClone.setOrigTransType(transType.toString());

            transLogClone.setTransType(ETransType.NOTICE_TRANS_BAT.toString());

            TransData transData = Component.transInit();
            transLogClone.setHeader(transData.getHeader());
            transLogClone.setTpdu(transData.getTpdu());
            transLogClone.setMerchID(transData.getMerchID());
            transLogClone.setTermID(transData.getTermID());
            ret = Online.getInstance().online(transLogClone, listener);
            if (ret != TransResult.SUCC) {
                if (ret == TransResult.ERR_RECV) { // 批上送交易无应答时，终端应在本轮上送完毕后再重发，而非立即重发
                    batchUpListener.onLeftResult(true);
                    continue;
                }
                return ret;
            } else if (ret == TransResult.SUCC) {
                ResponseCode responseCode = TopApplication.rspCode.parse(transLogClone.getResponseCode());
                // 返回码失败处理
                if (!"00".equals(responseCode.getCode()) && !"94".equals(responseCode.getCode())) {

                    if (listener != null) {
                        listener.onShowFailWithConfirm(
                                TransContext.getInstance().getCurrentContext().getString(R.string.emv_err_code)
                                        + responseCode.getCode()
                                        + TransContext.getInstance().getCurrentContext()
                                        .getString(R.string.emv_err_info)
                                        + responseCode.getMessage(),
                                8);
                    }
                }
            }

            transLog.setIsUpload(true);
            if (!DaoUtilsStore.getInstance().getmTransDaoUtils().update(transLog)) {
                return TransResult.ERR_ABORTED;
            }
            batchNum++;
            TopApplication.controller.set(Controller.BATCH_NUM, batchNum);

        }
        return TransResult.SUCC;

    }
    private static int allICCardTransBatchUp(int batchUpType, TransProcessListener listener,
                                             BatchUpListener batchUpListener) {
        int ret = TransResult.SUCC;
        int cnt = 0;

        List<TransData> allTrans = DaoUtilsStore.getInstance().getmTransDaoUtils().getallICCardTransBatchUp(TransData.class);


        if (allTrans == null || allTrans.size() == 0) {
            return TransResult.SUCC;
        }
        int transCnt = (int) allTrans.size();
        TransData transLog;
        int batchNum = TopApplication.controller.get(Controller.BATCH_NUM);
        for (cnt = 0; cnt < transCnt; cnt++) {
            transLog = allTrans.get(cnt);

            IConvert convert = TopApplication.convert;
            byte[] tvr = null;
            String s = transLog.getTvr();
            tvr = convert.strToBcd(s, IConvert.EPaddingPosition.PADDING_LEFT);
            AppLog.e("settle","tvr=== " + tvr);
            if ((tvr[4] & 0x40) != 0x00) { // ARPC错
                continue;
            }

            TransData transLogClone = transLog.clone();
            Component.transInit(transLogClone);
            transLogClone.setTransType(ETransType.IC_TC_BAT.toString());
            transLogClone.setTransNo(transLog.getTransNo());
            String f60 = "00" + String.format("%06d", transLog.getBatchNo());
            if (batchUpType != Controller.Constant.ICLOG) { // 对账不平
                f60 += "205";
            } else {
                f60 += "203";
            }
            f60 += "60";
            transLogClone.setField60(f60);
            String f62 = "61";
            if ("CUP".equals(transLog.getInterOrgCode())) {
                f62 += "00";
            } else {
                f62 += "01";
            }
            f62 += "00";
            f62 += transLog.getAmount();
            f62 += "156";
            transLogClone.setField62(f62);
            ret = Online.getInstance().online(transLogClone, listener);
            if (ret != TransResult.SUCC) {
                if (ret == TransResult.ERR_RECV) { // 批上送交易无应答时，终端应在本轮上送完毕后再重发，而非立即重发
                    batchUpListener.onLeftResult(true);
                    continue;
                }
                return ret;
            } else if (ret == TransResult.SUCC) {
                ResponseCode responseCode = TopApplication.rspCode.parse(transLogClone.getResponseCode());
                transLogClone.setResponseMsg(responseCode.getMessage());
                // 返回码失败处理
                if (!"00".equals(transLogClone.getResponseCode()) && !"94".equals(transLogClone.getResponseCode())) {

                    if (listener != null) {
                        listener.onShowFailWithConfirm(
                                TransContext.getInstance().getCurrentContext().getString(R.string.emv_err_code)
                                        + responseCode.getCode()
                                        + TransContext.getInstance().getCurrentContext()
                                        .getString(R.string.emv_err_info)
                                        + responseCode.getMessage(),
                                8);
                    }
                }
            }

            transLog.setIsUpload(true);

            if (!DaoUtilsStore.getInstance().getmTransDaoUtils().update(transLog)) {
                return TransResult.ERR_ABORTED;
            }
            batchNum++;
            AppLog.e("settle","allICCardTransBatchUp batchNum=== " + batchNum);
            TopApplication.controller.set(Controller.BATCH_NUM, batchNum);
        }
        return TransResult.SUCC;
    }

    /**
     * 全部磁条卡的请求类联机成功交易明细上送
     *
     * @param batchUpType
     *            批上送类型
     * @param listener
     * @param batchUpListener
     * @return
     */
    private static int allMagCardTransBatch(int batchUpType, TransProcessListener listener,
                                            BatchUpListener batchUpListener) {
        boolean left = false;
        int ret = TransResult.SUCC;

        // 对账平不送
        if (batchUpType == Controller.Constant.ICLOG) {
            return TransResult.SUCC;
        }

        int[] sendLoc = new int[8];
        List<TransData> allTrans = null;
        // TransData.readAllTrans();

        /****************************
         * SQL优化
         *****************************************/
        List<TransData> magTrans = new ArrayList<TransData>();

        magTrans = DaoUtilsStore.getInstance().getmTransDaoUtils().getallMagCardTransBatch(TransData.class,batchUpType);

        allTrans = DaoUtilsStore.getInstance().getmTransDaoUtils().getallCardTransBatch(TransData.class,batchUpType);
        if (allTrans == null)
            allTrans = magTrans;
        else {
            AppLog.d("TRANSDATA", "1->allTrans.size = " + allTrans.size());
            if (magTrans != null)
                allTrans.addAll(magTrans);
            AppLog.d("TRANSDATA", "2->allTrans.size = " + allTrans.size());
        }

        if (allTrans == null || allTrans.size() == 0) {
            return TransResult.SUCC;
        }
        int transCnt = (int) allTrans.size();
        int offSendCnt = 0;
        String f48 = "";
        int batchNum = TopApplication.controller.get(Controller.BATCH_NUM);
        TransData transLog = null;

        for (int cnt = 0; cnt < transCnt; cnt++) {
            transLog = allTrans.get(cnt);
            ETransType transType = ETransType.valueOf(transLog.getTransType());
            transLog.setOrigTransType(transType.toString());

            String interOrgCode = transLog.getInterOrgCode();

            sendLoc[offSendCnt] = cnt;
            if ("CUP".equals(interOrgCode)) {
                f48 += "00";
            } else {
                f48 += "01";
            }
            f48 += String.format("%06d", transLog.getTransNo());
            if (ETransType.valueOf(transLog.getTransType()) == ETransType.TRANS_QR_VOID
                    || ETransType.valueOf(transLog.getTransType()) == ETransType.TRANS_QR_SALE) {
                String c2b = "00000000000000000000" + transLog.getQrCode();
                c2b = c2b.substring(c2b.length() - 20, c2b.length());
                f48 += c2b;
            } else {
                String pan = "00000000000000000000" + transLog.getPan();
                pan = pan.substring(pan.length() - 20, pan.length());
                f48 += pan;
            }
            String amt = "000000000000" + transLog.getAmount();
            amt = amt.substring(amt.length() - 12, amt.length());
            f48 += amt;
            offSendCnt++;
            if (offSendCnt != 8) {
                continue;
            }
            TransData transData = Component.transInit();
            f48 = String.format("%1$02d", offSendCnt) + f48;
            transData.setField48(f48);
            transData.setOrigTransType(transType.toString());
            transData.setTransType(ETransType.BATCH_UP.toString());

            ret = Online.getInstance().online(transData, listener);
            if (ret != TransResult.SUCC) {
                if (ret == TransResult.ERR_RECV) {
                    offSendCnt = 0;
                    f48 = "";
                    left = true; // 批上送交易无应答时，终端应在本轮上送完毕后再重发，而非立即重发
                    batchUpListener.onLeftResult(left);
                    f48 = "";
                    continue;
                }
                return ret;
            } else if (ret == TransResult.SUCC) {
                ResponseCode responseCode = TopApplication.rspCode.parse(transData.getResponseCode());
                transData.setResponseMsg(responseCode.getMessage());
                // 返回码失败处理
                if (!"00".equals(transData.getResponseCode()) && !"94".equals(transData.getResponseCode())) {
                    Device.beepErr();
                    if (listener != null) {
                        listener.onShowFailWithConfirm(
                                TransContext.getInstance().getCurrentContext().getString(R.string.emv_err_code)
                                        + responseCode.getCode()
                                        + TransContext.getInstance().getCurrentContext()
                                        .getString(R.string.emv_err_info)
                                        + responseCode.getMessage(),
                                8);
                    }
                }
            }

            // 更新交易状态
            for (offSendCnt = 0; offSendCnt < 8; offSendCnt++) {
                transData = allTrans.get(sendLoc[offSendCnt]);
                transData.setOrigTransType(transData.getTransType());
                transData.setIsUpload(true);
                DaoUtilsStore.getInstance().getmTransDaoUtils().update(transData);

            }
            batchNum += 8;
            f48 = "";
            TopApplication.controller.set(Controller.BATCH_NUM, batchNum);
            offSendCnt = 0;
            f48 = "";
        }

        // 最后未达8笔的
        if (offSendCnt != 0) {
            TransData transData = Component.transInit();
            f48 = String.format("%1$02d", offSendCnt) + f48;
            transData.setField48(f48);
            transData.setOrigTransType(transLog.getOrigTransType().toString());
            transData.setTransType(ETransType.BATCH_UP.toString());

            ret = Online.getInstance().online(transData, listener);
            if (ret != TransResult.SUCC) {
                if (ret == TransResult.ERR_RECV) { // 批上送交易无应答时，终端应在本轮上送完毕后再重发，而非立即重发
                    offSendCnt = 0;
                    left = true;
                    batchUpListener.onLeftResult(left);
                    return TransResult.SUCC; // 此处返回SUCC是为了流程能继续
                }
                return ret;
            } else if (ret == TransResult.SUCC) {
                ResponseCode responseCode = TopApplication.rspCode.parse(transData.getResponseCode());
                transData.setResponseMsg(responseCode.getMessage());
                // 返回码失败处理
                if (!"00".equals(transData.getResponseCode()) && !"94".equals(transData.getResponseCode())) {
                    Device.beepErr();
                    if (listener != null) {
                        listener.onShowFailWithConfirm(
                                TransContext.getInstance().getCurrentContext().getString(R.string.emv_err_code)
                                        + responseCode.getCode()
                                        + TransContext.getInstance().getCurrentContext()
                                        .getString(R.string.emv_err_info)
                                        + responseCode.getMessage(),
                                8);
                    }
                }
            }

            // 更新交易状态
            for (int cnt = 0; cnt < offSendCnt; cnt++) {
                transData = allTrans.get(sendLoc[cnt]);
                transData.setOrigTransType(transData.getTransType());
                transData.setIsUpload(true);
                DaoUtilsStore.getInstance().getmTransDaoUtils().update(transData);

            }
            batchNum += offSendCnt;
            TopApplication.controller.set(Controller.BATCH_NUM, batchNum);
        }
        return ret;
    }

    /**
     * 结算结束
     *
     * @param listener
     * @return
     */
    private static int batchUpEnd(int batchUpType, TransProcessListener listener) {
        listener.onUpdateProgressTitle(ETransType.BATCH_UP_END.getTransName());
        TransData transData = Component.transInit();
        String f60 = "00" + String.format("%06d", Long.parseLong(TopApplication.sysParam.get(SysParam.BATCH_NO)));
        if (batchUpType != Controller.Constant.ICLOG) { // 对账不平
            f60 += "202";
        } else {
            f60 += "207";
        }
        int batchUpNum = TopApplication.controller.get(Controller.BATCH_NUM);
        AppLog.e("settle","batchUpEnd batchNum=== " + batchUpNum);
        transData.setField48(String.format("%04d", batchUpNum));
        transData.setField60(f60);
        transData.setTransType(ETransType.BATCH_UP_END.toString());
        int ret = Online.getInstance().online(transData, listener);
        if (ret == TransResult.SUCC) {
            ResponseCode responseCode = TopApplication.rspCode.parse(transData.getResponseCode());
            transData.setResponseMsg(responseCode.getMessage());
            // 返回码失败处理
            if (!"00".equals(transData.getResponseCode()) && !"94".equals(transData.getResponseCode())) {
                if (listener != null) {
                    listener.onShowFailWithConfirm(
                            TransContext.getInstance().getCurrentContext().getString(R.string.emv_err_code)
                                    + responseCode.getCode()
                                    + TransContext.getInstance().getCurrentContext().getString(R.string.emv_err_info)
                                    + responseCode.getMessage(),
                            8);
                }
            }
        }

        return ret;
    }

    public interface BatchUpListener {
        void onLeftResult(boolean left);
    }

    /**
     * (对账平不送)基于PBOC标准的借/贷记IC卡脱机消费(含小额支付)成功交易
     */
    private static int allPbocOfflineBatch(int batchUpType, TransProcessListener listener,
                                           BatchUpListener batchUpListener) {

        // 对账平不送
        if (batchUpType == Controller.Constant.ICLOG) {
            return TransResult.SUCC;
        }

        List<TransData> allTrans = DaoUtilsStore.getInstance().getmTransDaoUtils().getallPbocOfflineBatch(TransData.class,batchUpType);
        if (allTrans == null || allTrans.size() == 0) {
            return TransResult.SUCC;
        }
        int batchNum = TopApplication.controller.get(Controller.BATCH_NUM);

        for (TransData transData : allTrans) {
            TransData transLogClone = transData.clone();
            TransData temptTransData = Component.transInit();
            transLogClone.setHeader(temptTransData.getHeader());
            transLogClone.setTpdu(temptTransData.getTpdu());
            transLogClone.setMerchID(temptTransData.getMerchID());
            transLogClone.setTermID(temptTransData.getTermID());
            transLogClone.setTransType(ETransType.OFFLINE_TRANS_SEND_BAT.toString());
            int ret = Online.getInstance().online(transLogClone, listener);
            if (ret != TransResult.SUCC) {
                if (ret == TransResult.ERR_RECV) { // 批上送交易无应答时，终端应在本轮上送完毕后再重发，而非立即重发
                    batchUpListener.onLeftResult(true);
                    continue;
                }
                return ret;
            } else if (ret == TransResult.SUCC) {
                ResponseCode responseCode = TopApplication.rspCode.parse(transLogClone.getResponseCode());
                transData.setResponseMsg(responseCode.getMessage());
                // 返回码失败处理
                if (!"00".equals(transLogClone.getResponseCode()) && !"94".equals(transLogClone.getResponseCode())) {
                    Device.beepErr();
                    if (listener != null) {
                        listener.onShowMessageWithConfirm(
                                TransContext.getInstance().getCurrentContext().getString(R.string.emv_err_code)
                                        + responseCode.getCode()
                                        + TransContext.getInstance().getCurrentContext()
                                        .getString(R.string.emv_err_info)
                                        + responseCode.getMessage(),
                              5);
                    }
                }
            }

            transData.setIsUpload(true);

            if (!DaoUtilsStore.getInstance().getmTransDaoUtils().update(transData)) {
                return TransResult.ERR_ABORTED;
            }
            batchNum++;
            TopApplication.controller.set(Controller.BATCH_NUM, batchNum);
        }
        return 0;
    }
}
