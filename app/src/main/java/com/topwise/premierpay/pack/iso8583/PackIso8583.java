package com.topwise.premierpay.pack.iso8583;

import android.text.TextUtils;

import com.topwise.manager.AppLog;
import com.topwise.manager.utlis.DataUtils;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.utils.Utils;
import com.topwise.toptool.api.convert.IConvert;
import com.topwise.toptool.api.packer.IIso8583;
import com.topwise.toptool.api.packer.Iso8583Exception;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.pack.IPacker;
import com.topwise.premierpay.pack.PackListener;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransResult;

import com.topwise.premierpay.utils.ConfiUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;

public abstract class PackIso8583 implements IPacker<TransData, byte[]> {
    private static final String TAG =  TopApplication.APPNANE + PackIso8583.class.getSimpleName();
    private IIso8583 iso8583 ;
    protected IIso8583.IIso8583Entity entity;
    protected PackListener listener;

    public PackIso8583(PackListener listener) {
        this.listener = listener;
        initEntity();
    }

    /**
     * 获取打包entity
     *
     * @return
     */
    private void initEntity() {
        try {
            iso8583 = TopApplication.packer.getIso8583();
            entity = iso8583.getEntity();
            long l = System.currentTimeMillis();
            // 平均 90 ms
            entity.loadTemplate(TopApplication.mApp.getResources().getAssets().open("cup8583.xml"));
            AppLog.e(TAG,"iso8583 loadTemplate xml time: " +(System.currentTimeMillis() -l));
        } catch (Iso8583Exception e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    protected byte[] pack(boolean isNeedMac) {
        try {
            if (isNeedMac) {
                entity.setFieldValue("64", new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 });
            } else {
                if (entity.hasField("53"))
                    entity.resetFieldValue("53");
            }

            // for debug
            if (AppLog.DEBUG_D) {
                AppLog.i(TAG,"8583 debug data======== ");
                entity.dump();
            }

            byte[] packData = iso8583.pack();

            if (isNeedMac) {
                if (packData == null || packData.length == 0) {
                    return null;
                }

                int len = packData.length;

                byte[] calMacBuf = new byte[len - 22 - 8]; // 去掉header和mac
                System.arraycopy(packData, 22, calMacBuf, 0, len - 22 - 8);
                byte[] mac = listener.onCalcMac(calMacBuf);
                if (mac == null) {
                    return null;
                }
                System.arraycopy(mac, 0, packData, len - 8, 8);
            }

            return packData;
        } catch (Iso8583Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置冲正公共类数据
     * 设置域
     * <p>
     * filed 2, field 4, field 11, field 14, field 22, field 23, field 38,
     * <p>
     * field 39, field 49, field 55, field 61
     * @param transData
     * @return
     */
    protected int setRevCommonData(TransData transData) {
        try {
            String temp = "";
            int ret = 0;
            ret = setMandatoryData(transData);
            if (ret != TransResult.SUCC) {
                return ret;
            }

            int enterMode = transData.getEnterMode();

            ETransType transType = ETransType.valueOf(transData.getTransType());
                // field 2 主账号
                temp = transData.getPan();
                if (temp != null && temp.length() > 0) {
                    entity.setFieldValue("2", temp);
                }

            temp = transType.getProcCode();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("3", temp);
                // 为后续解包比较，做准备
                transData.setField3(temp);
            }

            // field 4 交易金額
            temp = transData.getAmount();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("4", temp);
            }

            // field 7 MMDDhhmmss
            temp = transData.getDate() + transData.getTime();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("7", temp);
            }

            // field 11 流水号
            temp = String.valueOf(transData.getTransNo());
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("11", temp);//
            }

            temp = transData.getTime();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("12", temp);
            }
            temp = transData.getDate();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("13", temp);
            }

            // field 14 有效期
//            if (transType != ETransType.QR_SALE && transType != ETransType.QR_VOID
//                    && transType != ETransType.QR_REFUND
//                    && transType != ETransType.FQ_SALE
//                    && transType != ETransType.ZHUNONG_CASH_WITHDRAWAL) {
//                temp = transData.getExpDate();
//                if (temp != null && temp.length() > 0) {
//                    entity.setFieldValue("14", temp);
//                }
//            }
            temp = transData.getExpDate();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("14", temp);
            }
            // field 22 服务点输入方式码
            if (transType == ETransType.TRANS_QR_SALE || transType == ETransType.TRANS_QR_VOID
                    || transType == ETransType.TRANS_QR_REFUND) {
                entity.setFieldValue("22", getInputMethod(Component.EnterMode.QR, transData.getHasPin()));
            } else {
                entity.setFieldValue("22", getInputMethod(enterMode, transData.getHasPin()));
            }

            // field 23 卡片序列号
            if (enterMode == Component.EnterMode.INSERT || enterMode == Component.EnterMode.QPBOC
                    || enterMode == Component.EnterMode.CLSS_PBOC) {
                temp = transData.getCardSerialNo();
                if (temp != null && temp.length() > 0) {
                    entity.setFieldValue("23", temp);
                }
            }

            //field 24 NII
            temp = transType.getNetCode();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("24", temp);
            }

            // [35]二磁道,[36]三磁道
            temp = transData.getTrack2();
            if (temp != null && temp.length() > 0) {
//                    if (transData.getIsEncTrack()) {// 加密
//                        temp = new String(listener.onEncTrack(temp.getBytes()));
//                    }
                entity.setFieldValue("35", temp);
            }

            temp = transData.getRefNo();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("37", temp);
            }

            // field 38
            temp = transData.getOrigAuthCode();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("38", temp);
            }

            // filed 39
            temp = transData.getReason();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("39", temp);
            }

            // field 49
            String curCode = TopApplication.sysParam.get(SysParam.APP_PARAM_TRANS_CURRENCY_CODE);
            if (curCode != null && curCode.length() == 4 && curCode.charAt(0) == '0') {
                curCode = curCode.substring(1);
            }
            AppLog.d(TAG,"curCode=== " + curCode);
            entity.setFieldValue("49", curCode);
//            entity.setFieldValue("49", "404");

            // 扫码的时候26和52,53不上送
//            if (transType != ETransType.QR_SALE && transType != ETransType.QR_VOID
//                    && transType != ETransType.QR_REFUND
//                    && transType != ETransType.FQ_SALE
//                    && transType != ETransType.ZHUNONG_CASH_WITHDRAWAL) {
//                if (transData.getHasPin()) {
//                    entity.setFieldValue("26", "12");
//                    if (transData.isSM()) {
//                        entity.setFieldValue("52", new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 });
//                    } else {
//                        if (!DataUtils.isNullString(transData.getPin())){
//                            entity.setFieldValue("52", TopApplication.convert.strToBcd(transData.getPin(),
//                                    IConvert.EPaddingPosition.PADDING_LEFT));
//                        }
//                    }
//                }

                // field 53
//                if (transData.getHasPin() || entity.hasField("35") || entity.hasField("36")) {
//                    temp = "2600000000000000";
//                    char[] data = temp.toCharArray();
//                    if (!transData.getHasPin()) {
//                        data[0] = '0';
//                    }
//                    if (transData.isSM()) {
//                        data[1] = '3';
//                    }
//                    if (transData.getIsEncTrack() && (entity.hasField("35") || entity.hasField("36"))) {
//                        data[2] = '1';
//                    }
//
//                    temp = new String(data);
//                    entity.setFieldValue("53", temp);
//                } else {
//                    if (transData.isSM()) { // 国密类交易必须要有53域，不需要64域时，再算mac处统一去掉53域
//                        entity.setFieldValue("53", "2300000000000000");
//                    }
//                }
//            }

            // [55]IC卡数据域if
//            if (transType != ETransType.QR_SALE && transType != ETransType.QR_VOID
//                    && transType != ETransType.QR_REFUND) {
                temp = transData.getICPositiveData();
                if (temp != null && temp.length() > 0) {
                    entity.setFieldValue("55",
                            TopApplication.convert.strToBcd(temp, IConvert.EPaddingPosition.PADDING_LEFT));
                }
//            }

//            if (transType == ETransType.TRANS_VOID
////                    || transType == ETransType.AUTHVOID || transType == ETransType.AUTHCMVOID
////                    || transType == ETransType.AUTHCM || transType == ETransType.QR_VOID
//            ) {
//
//                String f61 = "";
//                temp = String.format("%06d", transData.getBatchNo());
//                if (temp != null && temp.length() > 0) {
//                    f61 += temp;
//                } else {
//                    f61 += "000000";
//                }
//                temp = String.format("%06d", transData.getTransNo());
//                if (temp != null && temp.length() > 0) {
//                    f61 += temp;
//                } else {
//                    f61 += "000000";
//                }
//
////                if (transType == ETransType.AUTHVOID || transType == ETransType.AUTHCMVOID
////                        || transType == ETransType.AUTHCM) {
////                    temp = transData.getOrigDate();
////                    if (temp != null && temp.length() > 0 && transData.getReason() != null
////                            && !transData.getReason().equals(TransData.REASON_NO_RECV)) {
////                        f61 += temp;
////                    } else {
////                        f61 += "0000";
////                    }
////                }
//                entity.setFieldValue("61", f61);
//            }

            // TODO: 2025/6/5 INF Data
            //field 62 INF Data
            String infData = TopApplication.sysParam.get(SysParam.INF_DATA);
            AppLog.d(TAG,"infData=== " + infData);
            entity.setFieldValue("62", infData);

            // DEMTI MTI
            String f90_1 = transType.getMsgType();
            // DE11 System Trace Number
            String f90_2 = String.valueOf(transData.getTransNo());
            // DE7 Transmission Date And Time
            String f90_3 = transData.getDate() + transData.getTime();
            // DE32 Acquiring Institution ID Code
            String f90_4 = transData.getAcqCenterCode();
            // DE33 Forwarding Institution ID Code(转发机构的代码，如果没有可以使用受理机构代码)
//            String f90_5 = transData.getF33
            String f90 = f90_1 + f90_2 + f90_3 + f90_4 + f90_4;
//            if (f90 != null && f90.length() > 0) {
//                entity.setFieldValue("90", f90);
//            }
            return TransResult.SUCC;
        } catch (Exception e) {
            e.printStackTrace();
            return TransResult.ERR_PACK;
        }
    }

    protected int setVoidCommonData(TransData transData) {
        try {
            String temp = "";
            int ret = 0;
            // h, m, field 3, field 25, field 41,field 42
            ret = setMandatoryData(transData);
            if (ret != TransResult.SUCC) {
                return ret;
            }
            // field 2, 4, 11, 14, 22, 23, 26, 35, 36, 49, 52, 53
            ret = setCommonData(transData);
            if (ret != TransResult.SUCC) {
                return ret;
            }

            // [37]原参考号
            temp = transData.getOrigRefNo();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("37", temp);
            }
            // [38]原授权码
            temp = transData.getOrigAuthCode();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("38", temp);
            }

            ETransType transType = ETransType.valueOf(transData.getTransType());
            // field 61
            String f61 = "";
            temp = String.format("%06d", transData.getOrigBatchNo());
            if (temp != null && temp.length() > 0) {
                f61 += temp;
            } else {
                f61 += "000000";
            }
            temp = String.format("%06d", transData.getOrigTransNo());
            if (temp != null && temp.length() > 0) {
                f61 += temp;
            } else {
                f61 += "000000";
            }
            // 预授权撤销，预授权完成请求需要原始交易日期
//            if (transType == ETransType.AUTHVOID || transType == ETransType.AUTHCMVOID) {
//                temp = transData.getOrigDate();
//                if (temp != null && temp.length() > 0) {
//                    f61 += temp;
//                } else {
//                    f61 += "0000";
//                }
//            }
            entity.setFieldValue("61", f61);

            return TransResult.SUCC;
        } catch (Exception e) {
            e.printStackTrace();
            return TransResult.ERR_PACK;
        }
    }

    protected int setBitDataF60(TransData transData) {
        try {
            String temp = "";
            ETransType transType = ETransType.valueOf(transData.getTransType());
            String f60 = transType.getFuncCode(); // f60.1
            f60 += String.format("%06d", transData.getBatchNo()); // f60.2
            f60 += transType.getNetCode(); // f60.3
            if (transType == ETransType.BALANCE) {
                temp = "60";
                f60 += temp;
            } else  if (transType == ETransType.TRANS_SALE || transType == ETransType.TRANS_VOID
                    || transType == ETransType.TRANS_REFUND) {
                temp = "60";
                temp += "0";
                f60 += temp;
            } else if (transType == ETransType.BATCH_UP_END) {
                f60 = transData.getField60();
            }
            entity.setFieldValue("60", f60);
        } catch (Iso8583Exception e) {
            e.printStackTrace();
            return TransResult.ERR_PACK;
        }

        return TransResult.SUCC;
    }

    /**
     * 设置公共数据
     *
     * 设置域： h, m, field 3, field 25, field 41, field 42， field 43
     *
     * @param transData
     * @return
     */
    protected int setMandatoryData(TransData transData) {
        try {
            String temp = "";
            // h
            String pHeader = transData.getTpdu() + transData.getHeader();
            entity.setFieldValue("h", pHeader);

            // m
            ETransType transType = ETransType.valueOf(transData.getTransType());
            if (transData.isReversal()) {
                entity.setFieldValue("m", transType.getDupMsgType());
            } else {
                entity.setFieldValue("m", transType.getMsgType());
            }

            // todo test data need host provide
            temp = transType.getProcCode();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("3", temp);
                // 为后续解包比较，做准备
                transData.setField3(temp);
            }

            temp = transData.getMcc();
            AppLog.d(TAG,"MCC=== " + temp);
            if (temp != null && !TextUtils.isEmpty(temp)) {
                entity.setFieldValue("18", temp);
            } else {
                entity.setFieldValue("18", TopApplication.sysParam.get(SysParam.PARAM_MCC));
            }

            temp = transType.getServiceCode();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("25", temp);
            }
            // field 41 终端号
            temp = transData.getTermID();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("41", temp);
            }

            // field 42 商户号
            temp = transData.getMerchID();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("42", temp);
            }

            // TODO: 7/3/2025 test data
            // field 43 商户姓名和地址
//            temp = "Premier Bank";
//            temp = TopApplication.sysParam.get(SysParam.MERCH_NAME);
            // 1-23 location information exclusive of city, state and country
            // 24-36 The city in which the point-of-Service is located
            // 37-38 The state in which the point-of-Service is located
            // 39-40 The country in which the Point-of-Service is located
            String locationInfo = TopApplication.sysParam.get(SysParam.LOCATION_INFO);
            String city = TopApplication.sysParam.get(SysParam.CITY_INFO);
            String stateCode = TopApplication.sysParam.get(SysParam.STATE_CODE);
            String countryCode = TopApplication.sysParam.get(SysParam.COUNTRY_CODE);
            temp = String.format("%23s", locationInfo) + String.format("%13s", city) + stateCode + countryCode;
            AppLog.d(TAG,"merchant and location=== " + temp);
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("43", temp);
            }

            return TransResult.SUCC;
        } catch (Iso8583Exception e) {
            e.printStackTrace();
        }
        return TransResult.ERR_PACK;
    }

    /**
     * 设置field 2, 4, 7，11，12，13, 14, 22, 23, 24, 35, 37, 49, 52, 54, 62
     *
     * @param transData
     * @return
     */
    private int setCommonData(TransData transData) {
        String temp = "";
        try {
            ETransType transType = ETransType.valueOf(transData.getTransType());
            int enterMode = transData.getEnterMode();
            AppLog.d(TAG,"enterMode=== " + enterMode);
            if (enterMode == Component.EnterMode.MANAUL) {
                // 手工输入
                // [2]主账号,[14]有效期
                temp = transData.getPan();
                if (temp != null && temp.length() > 0) {
                    entity.setFieldValue("2", temp);
                }

                temp = transData.getExpDate();
                if (temp != null && temp.length() > 0) {
                    entity.setFieldValue("14", temp);
                }

            } else if (enterMode == Component.EnterMode.SWIPE) {
                // 刷卡
                temp = transData.getPan();
                if (!TextUtils.isEmpty(temp)) {
                    entity.setFieldValue("2", temp);
                }
                // [35]二磁道,[36]三磁道
                temp = transData.getTrack2();
                if (temp != null && temp.length() > 0) {
//                    if (transData.getIsEncTrack()) {// 加密
//                        temp = new String(listener.onEncTrack(temp.getBytes()));
//                    }
                    entity.setFieldValue("35", temp);
                }

                temp = transData.getTrack3();
                if (temp != null && temp.length() > 0) {
//                    if (transData.getIsEncTrack()) {// 加密
//                        temp = new String(listener.onEncTrack(temp.getBytes()));
//                    }
                    entity.setFieldValue("36", temp);
                }

            } else if (enterMode == Component.EnterMode.INSERT || enterMode == Component.EnterMode.QPBOC
                    || enterMode == Component.EnterMode.CLSS_PBOC) {
                // [2]主账号
                temp = transData.getPan();
                if (temp != null && temp.length() > 0) {
                    entity.setFieldValue("2", temp);
                }
                // [14]有效期
                temp = transData.getExpDate();
                AppLog.d(TAG,"expire date=== " + temp);
                if (temp != null && temp.length() > 0) {
                    entity.setFieldValue("14", temp);
                }
                // [23]卡序列号
                temp = transData.getCardSerialNo();
                if (temp != null && temp.length() > 0) {
                    entity.setFieldValue("23", temp);
                }

                // [35]二磁道
                temp = transData.getTrack2();
                if (temp != null && temp.length() > 0) {
                    if (transData.isEncTrack()) {// 加密
                      //  temp = new String(listener.onEncTrack(temp.getBytes()));
                    }
                    entity.setFieldValue("35", temp);
                }
            }

            // field 7 MMDDhhmmss
            temp = transData.getDate() + transData.getTime();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("7", temp);
            }

            temp = transData.getTime();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("12", temp);
            }
            temp = transData.getDate();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("13", temp);
            }

            // 非指定账户圈存转入卡的输入方式
            // [23]卡序列号
            temp = transData.getCardSerialNo();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("23", temp);
            }
            // field 4
            temp = transData.getAmount();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("4", temp);
            }

            // todo test data need host provide
            //field 24 NII
            temp = transType.getNetCode();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("24", temp);
            }

            // field 11 流水号
            temp = String.valueOf(transData.getTransNo());
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("11", temp);
            }
            // field 22 服务点输入方式码
            entity.setFieldValue("22", getInputMethod(enterMode, transData.isHasPin()));

            // [26]服务点PIN获取码,[52]PIN,[53]安全控制信息
            // 扫码的时候26和52,53不上送
            if (transData.isHasPin()) {
//                    entity.setFieldValue("52", TopApplication.convert.strToBcd(transData.getPin(),
//                            IConvert.EPaddingPosition.PADDING_LEFT));
                entity.setFieldValue("52", transData.getPin());
            }

            // Field 48
            String field48 = transData.getField48();
            AppLog.d(TAG,"field48=== " + field48);
            if (field48 != null) {
                boolean isRequestPin = Utils.isSingleTapAndPinRequest(field48);
                AppLog.d(TAG,"isRequestPin=== " + isRequestPin);
                if (isRequestPin) {
                    temp = Utils.packRspField48Str(field48);
                    AppLog.d(TAG,"temp=== " + temp);
                    entity.setFieldValue("48", temp);
                }
            }
            // field 49
            String curCode = TopApplication.sysParam.get(SysParam.APP_PARAM_TRANS_CURRENCY_CODE);
            if (curCode != null && curCode.length() == 4 && curCode.charAt(0) == '0') {
                curCode = curCode.substring(1);
            }
            AppLog.d(TAG,"curCode=== " + curCode);
            entity.setFieldValue("49", curCode);
//            entity.setFieldValue("49", "404");

            // TODO: 7/3/2025 test data need host provide field specification
            //field 62 INF Data
            String infData = TopApplication.sysParam.get(SysParam.INF_DATA);
            AppLog.d(TAG,"infData=== " + infData);
            entity.setFieldValue("62", infData);

       //     setBitDataF59(transData); // 59域数据含国密的密钥，扫码交易信息以及硬件序列号上送
            return TransResult.SUCC;


        } catch (Iso8583Exception e) {

            e.printStackTrace();
        }

        return TransResult.ERR_PACK;
    }

    // 设置 field 48
    protected int setBitDataF48(TransData transData) {
        try {
            ETransType transType = ETransType.valueOf(transData.getTransType());
            switch (transType) {
                case TRANS_SETTLE:
                case BATCH_UP:
                case BATCH_UP_END:
                    entity.setFieldValue("48", transData.getField48());
                    break;
//                case EC_TRANSFER_LOAD:
//                    int enterMode = transData.getTransferEnterMode();
//                    String temp = getInputMethod(enterMode, false);
//                    entity.setFieldValue("48", temp + "0");
//                    break;

                default:
                    break;
            }
        } catch (Iso8583Exception e) {
            e.printStackTrace();
            return TransResult.ERR_PACK;
        }
        return TransResult.SUCC;
    }

    public static final String TopNo = "000056";
    public static String devType = "04";

    /**
     * A2 067
     * 01 002 04
     * 02 018 000002041170270297
     * 03 006 141319
     * 04 008 558740F9
     * 05 008 320001
     * A5 040
     * 01 010 +113.84363
     * 02 010 +22.616641
     * 05 005 GCJ0
     * @param transData
     */
  /*  protected int setBitDataF59(TransData transData){
        if(transData.isStressTest()){
            return 0;
        }

        ETransType transType = ETransType.valueOf(transData.getTransType());
        byte[] f59Temp = new byte[512];
        int f59Length = 0;
        if ((transType == ETransType.TRANS_SALE
                || transType == ETransType.TRANS_QR_SALE) && (!transData.isReversal())){
            String sn = Device.getSn();
            String tag2Value2 = TopNo + devType + sn;
            String encRdmFactors = "";
            if (transType == ETransType.TRANS_QR_SALE) {
                String c2bMessage = transData.getQrCode();
                if (c2bMessage.length() < 6) {
                    return TransResult.ERR_PACK;
                }
                encRdmFactors = c2bMessage.substring(c2bMessage.length() - 6, c2bMessage.length());
            } else {
                String cardNo = transData.getPan();
                encRdmFactors = cardNo.substring(cardNo.length() - 6, cardNo.length());
            }
            byte[] hardwareSNCiphertext = Device.getHardwareSNCiphertext(TopApplication.convert.strToBcd(encRdmFactors, IConvert.EPaddingPosition.PADDING_RIGHT));
            String snCiphData  =  "";//
            if (hardwareSNCiphertext != null){
                snCiphData = TopApplication.convert.bcdToStr(hardwareSNCiphertext);
            }

            String ver = "320001  ";

            String tagA2T1 = "01" + String.format("%03d", devType.length()) + devType;
            String tagA2T2 = "02" + String.format("%03d", tag2Value2.length()) + tag2Value2;

            String tagA2T3 = "";
            String tagA2T4 = "";
            if (!TextUtils.isEmpty(snCiphData)) {
                tagA2T3 = "03" + String.format("%03d", encRdmFactors.length()) + encRdmFactors;
                tagA2T4 = "04" + String.format("%03d", snCiphData.length()) + snCiphData;
            } else {
                tagA2T2 = "02" + String.format("%03d", sn.length()) + sn;
            }
            String tagA2T5 = "05" + String.format("%03d", ver.length()) + ver;
            String tagA2 = tagA2T1 + tagA2T2 + tagA2T3 + tagA2T4 + tagA2T5;

            AppLog.v(TAG, " tagA2T1 = " + tagA2T1 + " tagA2T2 = " + tagA2T2 + " tagA2T3 = " + tagA2T3 + " tagA2T4 = "
                    + tagA2T4 + " tagA2T5 = " + tagA2T5);
            tagA2 = "A2" + String.format("%03d", tagA2.length()) + tagA2;
            System.arraycopy(tagA2.getBytes(), 0, f59Temp, f59Length, tagA2.length());
            f59Length += tagA2.length();
        }

        if (transType == ETransType.TRANS_QR_SALE && !DataUtils.isNullString(transData.getQrCode())) {// tagA3
            String c2bMessage = transData.getQrCode();
            String tagA3 = "A3" + String.format("%03d", c2bMessage.length()) + c2bMessage;
            System.arraycopy(tagA3.getBytes(), 0, f59Temp, f59Length, tagA3.length());
            f59Length += tagA3.length();

        } else if (transType == ETransType.TRANS_QR_VOID || transType == ETransType.TRANS_QR_REFUND) {// tag
            // A4
            String temp = transData.getOrigQrVoucher();
            if (temp != null) {
                String tagA4 = "A4" + String.format("%03d", temp.length()) + temp;
                System.arraycopy(tagA4.getBytes(), 0, f59Temp, f59Length, tagA4.length());
                f59Length += tagA4.length();
            }
        }

        if ((transType == ETransType.TRANS_SALE ||transType == ETransType.TRANS_QR_SALE)
                && (!transData.isReversal())) {
            // "A504001010+113.8436002010+22.61667905005GCJ02";//
            String tagA5 = Utils.getLocationTagA5();
            if (!TextUtils.isEmpty(tagA5)) {
                System.arraycopy(tagA5.getBytes(), 0, f59Temp, f59Length, tagA5.length());
                f59Length += tagA5.length();
            }
        }
        byte[] f59 = new byte[f59Length];
        System.arraycopy(f59Temp, 0, f59, 0, f59Length);
        try {
            if (f59 != null && f59.length > 0)
                entity.setFieldValue("59", f59);
        } catch (Iso8583Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return TransResult.SUCC;
    }*/


    protected int setFinancialData(TransData transData) {
        try {
            String temp = "";
            int ret = 0;
            // h,m, field 3, field 25, field 41,field 42
            // add field 43
            ret = setMandatoryData(transData);
            if (ret != TransResult.SUCC) {
                return ret;
            }
            //field 2, 4, 7, 11, 12, 13, 14, 22, 23, 24, 35, 37, 49, 52, 54, 62
            //remove field 26, field 36
            ret = setCommonData(transData);
            if (ret != TransResult.SUCC) {
                return ret;
            }
            // field 55
            temp = transData.getSendIccData();
            if (temp != null && temp.length() > 0) {
//                entity.setFieldValue("55", TopApplication.convert.strToBcd(temp, IConvert.EPaddingPosition.PADDING_LEFT));
                entity.setFieldValue("55", temp);
            }
            return TransResult.SUCC;
        } catch (Exception e) {
            e.printStackTrace();
            return TransResult.ERR_PACK;
        }
    }


    protected byte[] setCertData(TransData transData) {
        try {
            ETransType transType = ETransType.valueOf(transData.getTransType());
            String messageType = transType.getMsgType();
            String tlvData = transData.getSendIccData()+"F700";
            AppLog.d("messageType",messageType);
            AppLog.d("tlvData",tlvData);
            String dataLength = bytesToHex(intToTwoBytes(tlvData.length()/2));
            AppLog.d("length",dataLength);
            String data =messageType+dataLength+tlvData;
            AppLog.d("data",data);
            if (data != null && data.length() > 0) {
                return TopApplication.convert.strToBcd(data, IConvert.EPaddingPosition.PADDING_LEFT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    public static byte[] intToTwoBytes(int value) {
        ByteBuffer buffer = ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN);
        buffer.putShort((short) value);
        return buffer.array();
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    protected String getInputMethod(int enterMode, boolean hasPin) {
        String inputMethod = "";
        switch (enterMode) {
            case Component.EnterMode.MANAUL:
                inputMethod = "01";
                break;
            case Component.EnterMode.SWIPE:
                inputMethod = "02";
                break;
            case Component.EnterMode.INSERT:
                inputMethod = "05";
                break;
            case Component.EnterMode.QPBOC:
                inputMethod = "07";
                break;
            case Component.EnterMode.FALLBACK:
                inputMethod = "80";
                break;
            case Component.EnterMode.PHONE:
                inputMethod = "92";
                break;
            case Component.EnterMode.MOBILE:
                inputMethod = "96";
                break;
            case Component.EnterMode.CLSS_PBOC:
                inputMethod = "98";
                break;
            case Component.EnterMode.QR:
                inputMethod = "03"; // 文档上要求04，实际上银联要求03
                break;
            default:
                break;
        }

        if (hasPin) {
            inputMethod += "1";
        } else {
            inputMethod += "2";
        }

        return inputMethod;
    }

    private int set59Filed(TransData transData){
        return TransResult.ERR_UNPACK;
    }

    @Override
    public int unpack(TransData transData, byte[] rsp) {
            HashMap<String, byte[]> map = null;
            try {
//                byte[] testdata = TopApplication.convert.strToBcd("7E7E7E7E7E7E7E7E7E7E7E7E7E7E7E7E7E7E7E7E7E7E30323130723C06002EF0820031363430303535323939393930303031323330303030303030303030303030303530303031303231313230303235303030303236313230303235313032313331313230373230303133323430303535323939393930303031323344333131323230313134373439343232353239343131303030303236303137363535303030303030303231393030303030303031313530343630314E4149524F424945585052455353205445524D494E414C2020202020204E4149524F42494B454B453039562020203220202032343034303330394633363032303030373931303841373436304541413030383630303030", IConvert.EPaddingPosition.PADDING_LEFT);
//                map = iso8583.unpack(testdata, true);
                map = iso8583.unpack(rsp, true);
                // 调试信息， 日志输入解包后数据
                entity.dump();
            } catch (Exception e) {
                e.printStackTrace();
                return TransResult.ERR_UNPACK;
            }

            // 报文头
            byte[] header = map.get("h");
            transData.setHeader(new String(header).substring(10));

            ETransType transType = ETransType.valueOf(transData.getTransType());
            byte[] buff = null;
            String temp = "";
            if (transType != ETransType.TRANS_SETTLE) {
                buff = map.get("39");
                if (buff == null) {
                    return TransResult.ERR_BAG;
                }
                temp = new String(buff);
                if (temp.equals("65")) {
                    buff = map.get("48");
                    if (buff != null && buff.length != 0) {
                        String field48 = new String(buff);
                        AppLog.d(TAG,"48=== " + field48);
                        transData.setField48(field48);
                        boolean isSingleTapPIN = Utils.isSingleTapAndPinRequest(field48);
                        if (isSingleTapPIN) {
                            return TransResult.ERR_NEED_ENTER_PWD;
                        }
                    }
                    return TransResult.ERR_NEED_INSTER_CARD;

                }
                if (!temp.equals("00")) {
                    transData.setResponseCode(temp);
                    return TransResult.ERR_HOST_REJECT;
                }
                AppLog.d(TAG,"39=== " + temp);
                transData.setResponseCode(temp);
            }

            // field 3 交易处理码
            buff = map.get("3");
            if (buff != null && buff.length > 0) {
                temp = new String(buff);
                AppLog.d(TAG,"3=== " + temp);
            }

            // field 4 交易金额
            buff = map.get("4");
            if (buff != null && buff.length > 0) {
                temp = new String(buff);
                AppLog.d(TAG,"4=== " + temp);
                if (!ConfiUtils.isDebug) {
                    transData.setAmount(temp);
                }
            }

            // field 7 交易时间日期
            buff = map.get("7");
            if (buff != null && buff.length > 0) {
                temp = new String(buff);
                AppLog.d(TAG,"7=== " + temp);
                String date, time;
                if (!ConfiUtils.isDebug) {
                    if (temp.length() > 4) {
                       date = temp.substring(0, 4);
                       time = temp.substring(4);
                       transData.setDate(date);
                       transData.setTime(time);
                    }
                }
            }

            // field 11 流水号
            buff = map.get("11");
            if (buff != null && buff.length > 0) {
                temp = new String(buff);
                AppLog.d(TAG,"11=== " + temp);
                if (!ConfiUtils.isDebug) {
                    transData.setTransNo(Long.parseLong(temp));
                }
            }

            // field 12 受卡方所在地时间
            buff = map.get("12");
            if (buff != null && buff.length > 0) {
                temp = new String(buff);
                AppLog.d(TAG,"12=== " + temp);
                if (!ConfiUtils.isDebug) {
                    transData.setTime(temp);
                }
            }
            // field 13 受卡方所在地日期
            // Calendar date = Calendar.getInstance();
            // String yeardate = String.valueOf(date.get(Calendar.YEAR));
            buff = map.get("13");
            if (buff != null) {
                // yeardate = yeardate + new String(buff);
                temp = new String(buff);
                AppLog.d(TAG,"13=== " + temp);
                if (!ConfiUtils.isDebug) {
                    transData.setDate(temp);
                }
            }
            // field 14 卡有效期
            buff = map.get("14");
            if (buff != null && buff.length > 0) {
                String expDate = new String(buff);
                AppLog.d(TAG,"14=== " + expDate);
                if (!expDate.equals("0000")) {
                    transData.setExpDate(expDate);
                }
            }
            // field 15清算日期
            buff = map.get("15");
            if (buff != null && buff.length > 0) {
                temp = new String(buff);
                AppLog.d(TAG,"15=== " + temp);
                transData.setSettleDate(temp);
            }
            // field 22
            buff = map.get("22");
            if (buff != null && buff.length > 0) {
                temp = new String(buff);
                AppLog.d(TAG,"22=== " + temp);
                transData.setSettleDate(temp);
            }

            // field 23 卡片序列号
            buff = map.get("23");
            if (buff != null && buff.length > 0) {
                temp = new String(buff);
                AppLog.d(TAG,"23=== " + temp);
                transData.setCardSerialNo(temp);
            }
            //field 24
            buff = map.get("24");
            if (buff != null && buff.length > 0) {
                temp = new String(buff);
                AppLog.d(TAG,"24=== " + temp);
                transData.setSettleDate(temp);
            }
            // field 25
            buff = map.get("25");
            if (buff != null && buff.length > 0) {
                temp = new String(buff);
                AppLog.d(TAG,"25=== " + temp);
                transData.setSettleDate(temp);
            }
            // field 26

            // field 32 受理方标识码
            buff = map.get("32");
            if (buff != null && buff.length > 0) {
                temp = new String(buff);
                AppLog.d(TAG,"32=== " + temp);
                transData.setAcqCenterCode(temp);
            }

            // field 35
            buff = map.get("35");
            if (buff != null && buff.length > 0) {
                temp = new String(buff);
                AppLog.d(TAG,"35=== " + temp);
                transData.setSettleDate(temp);
            }
            // field 36

            // field 37 检索参考号
            buff = map.get("37");
            if (buff != null && buff.length > 0) {
                temp = new String(buff);
                AppLog.d(TAG,"37=== " + temp);
                transData.setRefNo(temp);
            }

            // field 38 授权码
            buff = map.get("38");
            if (buff != null && buff.length > 0) {
                temp = new String(buff);
                AppLog.d(TAG,"38=== " + temp);
                transData.setAuthCode(temp);
            }

            // field 41 校验终端号
            buff = map.get("41");
            if (buff != null && buff.length > 0) {
                temp = new String(buff);
                AppLog.d(TAG,"41=== " + temp);
                transData.setTermID(temp);
            }

            // field 42 校验商户号
            buff = map.get("42");
            if (buff != null && buff.length > 0) {
                temp = new String(buff);
                AppLog.d(TAG,"42=== " + temp);
                transData.setMerchID(temp);
            }
            // field 43 校验商户姓名和地址
            buff = map.get("43");
            if (buff != null && buff.length > 0) {
                temp = new String(buff);
                AppLog.d(TAG,"43=== " + temp);
                transData.setMerchID(temp);
            }
            // field 44
            buff = map.get("44");
            if (buff != null && buff.length > 11) {
                temp = new String(buff).substring(0, 11).trim();
                transData.setIsserCode(temp);
                if (buff.length > 11) {
                    temp = new String(buff).substring(11).trim();
                    transData.setAcqCode(temp);
                }
            }
            // field 48
            buff = map.get("48");
            if (buff != null && buff.length > 0) {
                transData.setField48(new String(buff));
                AppLog.d(TAG,"48=== " + temp);
            }
            // field 49
            buff = map.get("49");
            if (buff != null && buff.length > 0) {
                temp = new String(buff);
                AppLog.d(TAG,"49=== " + temp);
//                transData.setField49(new String(buff));
            }
            // field 54
//            buff = map.get("54");
//            AppLog.d(TAG,"54=== " + temp);
//            if (buff != null && buff.length >= 20) {
//                temp = new String(buff);
//                transData.setBalanceFlag(temp.substring(7, 8));
//                transData.setBalance(temp.substring(temp.length() - 12, temp.length()));
//            }
            buff = map.get("55");
            if (buff != null && buff.length > 0) {
//                temp = TopApplication.convert.bcdToStr(buff);
                temp = new String(buff);
                AppLog.d(TAG,"55=== " + temp);
                transData.setRecvIccData(temp);
            }
            // filed59域 解tlv
            buff = map.get("59");
            if (transType == ETransType.TRANS_QR_VOID || transType == ETransType.TRANS_QR_REFUND) {
                transData.setQrVoucher("");
            } else if (transType == ETransType.TRANS_QR_SALE) {
                // all_unionpay

                if (buff != null && buff.length > 0) {
                    byte[] tmp = new byte[buff.length - 5];
                    System.arraycopy(buff, 5, tmp, 0, tmp.length); // 前面有5个字节的长度
                    transData.setQrVoucher(new String(tmp));
                }
            }
            // field 60
            buff = map.get("60");
            if (buff != null && buff.length > 0) {
                temp = new String(buff);
                AppLog.d(TAG,"60=== " + temp);
                transData.setBatchNo(Long.parseLong(temp.substring(2, 8)));
            }
            // field 61
            // field 62
            buff = map.get("62");
            if (buff != null && buff.length > 0) {
//                temp = TopApplication.convert.bcdToStr(buff);
                temp = new String(buff);
                AppLog.d(TAG,"62=== " + temp);
                transData.setField62(temp);
            }
            // field 63
            buff = map.get("63");
            if (buff != null && buff.length > 0) {
                try {
                    // 国际组织代码
                    transData.setInterOrgCode(new String(buff).substring(0, 3));
                    // 63域附加信息域
                    byte[] reserved = new byte[buff.length - 3];
                    System.arraycopy(buff, 3, reserved, 0, reserved.length);
                    transData.setReserved(new String(reserved, "GBK").trim());

                    if (buff.length > 3) {
                        // 发卡行信息
                        int len = buff.length - 3 > 20 ? 20 : buff.length - 3;
                        byte[] issuerResp = new byte[20];
                        System.arraycopy(buff, 3, issuerResp, 0, len);
                        transData.setIssuerResp(new String(issuerResp, "GBK").trim());

                        if (buff.length > 23) {
                            // 中心信息
                            len = buff.length - 23 > 20 ? 20 : buff.length - 23;
                            byte[] centerResp = new byte[20];
                            System.arraycopy(buff, 23, centerResp, 0, len);
                            transData.setCenterResp(new String(centerResp, "GBK").trim());

                            if (buff.length > 43) {
                                // 收单行信息
                                len = buff.length - 43 > 20 ? 20 : buff.length - 43;
                                byte[] recvBankResp = new byte[20];
                                System.arraycopy(buff, 43, recvBankResp, 0, len);
                                transData.setRecvBankResp(new String(recvBankResp, "GBK").trim());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // 如果内卡不返回,则设置默认值CUP
                transData.setInterOrgCode("CUP");
            }

            // field 64
            // 解包校验mac
            if (!ConfiUtils.isDebug) {
                byte[] data = new byte[rsp.length - 11 - 8];
                System.arraycopy(rsp, 11, data, 0, data.length);
                buff = map.get("64");
                if (buff != null && buff.length > 0 && listener != null) {
                    byte[] mac = listener.onCalcMac(data);
                    AppLog.d(TAG,"MAC === " + TopApplication.convert.bcdToStr(mac));
                    if (!TopApplication.iTool.getUtils().isByteArrayValueSame(buff, 0, mac, 0, 8)) {
                        return TransResult.ERR_MAC;
                    }
                }
            }

            listener = null;
        return TransResult.SUCC;
    }
}
