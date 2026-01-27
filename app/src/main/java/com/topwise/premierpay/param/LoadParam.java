package com.topwise.premierpay.param;

import android.text.TextUtils;

import com.topwise.cloudpos.struct.BytesUtil;

import com.topwise.manager.AppLog;
import com.topwise.manager.emv.entity.EmvAidParam;
import com.topwise.manager.emv.entity.EmvCapkParam;
import com.topwise.tmslibrary.util.HexUtil;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.tms.bean.AidBean;
import com.topwise.premierpay.tms.bean.CapkBean;

import java.util.Locale;

/**
 * 创建日期：2021/4/21 on 10:02
 * 描述: 逻辑 可以从xml 读取文件，到数据库，也可以单独加载到数据库
 * 作者:wangweicheng
 */
public abstract class LoadParam<T> {
    protected static final String TAG = LoadParam.class.getSimpleName();



    public abstract boolean DelectAll();

    /**
     * 保存到数据库
     * @return
     */
    public abstract void saveAll();

    /**
     * 单独保存记录
     * @param inData
     * @return
     */
    public abstract boolean save(String inData);


    protected EmvAidParam saveAid(AidBean aid) {

        EmvAidParam aidParam;
        try {
            aidParam = new EmvAidParam();
            // 9f06 AID
            if(aid.getAID()!=null&& !aid.getAID().isEmpty()) {
                aidParam.setAid(aid.getAID());
            }
            // DF01
            aidParam.setbPartMatch(aid.isPartMatch());
            // 9F08
            if(aid.getAppVersion()!=null&& !aid.getAppVersion().isEmpty()) {
                aidParam.setAucAPPVer(aid.getAppVersion());
                aidParam.setbAPPVerFlg(true);
            }

            //DF810C
            if(aid.getKernelType()!=null&& !aid.getKernelType().isEmpty()) {
                aidParam.setAucKernType(aid.getKernelType());
            }
            // DF11
            if(aid.getTACDefault()!=null&& !aid.getTACDefault().isEmpty()) {
                aidParam.setAucTACDefault(aid.getTACDefault());
                aidParam.setbTACDefaultFlg(true);
            }

            // DF12
            if(aid.getTACOnline()!=null&& !aid.getTACOnline().isEmpty()) {
                aidParam.setAucTACOnline(aid.getTACOnline());
                aidParam.setbTACOnlineFlg(true);
            }

            // DF13
            if(aid.getTACDenial()!=null&& !aid.getTACDenial().isEmpty()) {
                aidParam.setAucTACDenail(aid.getTACDenial());
                aidParam.setbTACDenailFlg(true);
            }
            // 9F1B
            if(aid.getTermFLmt()!=null&& !aid.getTermFLmt().isEmpty()) {
                aidParam.setAucFloorLimit(aid.getTermFLmt());
                aidParam.setbFloorLimitFlg(true);
            }

            // DF15 byte 4
            if(aid.getThresHold()!=null&& !aid.getThresHold().isEmpty()) {
                aidParam.setAucThreshold(aid.getThresHold());
                aidParam.setbThresholdFlg(true);
            }
            // DF16
            if(aid.getMaxTargetPercent()!=null&& !aid.getMaxTargetPercent().isEmpty()) {
                aidParam.setUcMaxTP(Byte.parseByte(aid.getMaxTargetPercent()));
                aidParam.setbMaxTPFlg(true);
            }

            // DF17
            if(aid.getTargetPercent()!=null&& !aid.getTargetPercent().isEmpty()) {
                aidParam.setUcTP(Byte.parseByte(aid.getTargetPercent()));
                aidParam.setbTPFlg(true);
            }

            // DF14
            if(aid.getDefTDOL()!=null&& !aid.getDefTDOL().isEmpty()) {
                aidParam.setAucTermDDOL(aid.getDefTDOL());
                aidParam.setbTermDDOLFlg(true);
            }

            if(aid.getDefDDOL()!=null&& !aid.getDefDDOL().isEmpty()) {
                aidParam.setAucTermDDOL(aid.getDefDDOL());
                aidParam.setbTermDDOLFlg(true);
            }


            // 9F7B
            if(aid.getRdClssTxnOnDeviceLmt()!=null&& !aid.getRdClssTxnOnDeviceLmt().isEmpty()) {
                aidParam.setAucRdClssTxnLmtOnDevice(aid.getRdClssTxnOnDeviceLmt());
                aidParam.setbRdClssTxnLmtOnDeviceFlg(true);
            }

            // DF19
            if(aid.getRdClssFLmt()!=null&& !aid.getRdClssFLmt().isEmpty()) {
                aidParam.setAucRdClssFLmt(aid.getRdClssFLmt());
                aidParam.setbRdClssFLmtFlg(true);
            }

            // DF20
            if(aid.getRdClssTxnOnDeviceLmt()!=null&& !aid.getRdClssTxnOnDeviceLmt().isEmpty()) {
                aidParam.setAucRdClssTxnLmt(aid.getRdClssTxnOnDeviceLmt());
                aidParam.setbRdClssTxnLmtFlg(true);
            }

            // DF21
            if(aid.getCVMLmt()!=null&& !aid.getCVMLmt().isEmpty()) {
                aidParam.setAucRdCVMLmt(aid.getCVMLmt());
                aidParam.setbRdCVMLmtFlg(true);
            }

            //9F1C s termId
            if(aid.getTerminalAcquireId()!=null&& !aid.getTerminalAcquireId().isEmpty()) {
                aidParam.setAucTermID(aid.getTerminalAcquireId());
                aidParam.setbTermIDFlg(true);
            }

            //5F2A s transCurrCode
            if(aid.getTransCurrencyCode()!=null&& !aid.getTransCurrencyCode().isEmpty()) {
                aidParam.setAucCurrencyCode(aid.getTransCurrencyCode());
                aidParam.setbCurrencyCodeFlg(true);
            }

            //5F36 i transCurrExp
            if(aid.getTransCurrencyExp()!=null&& !aid.getTransCurrencyExp().isEmpty()) {
                aidParam.setUcCurrencyExp(Byte.parseByte(aid.getTransCurrencyExp()));
                aidParam.setbCurrencyExpFlg(true);
            }

            //9F3C s referCurrCode
            if(aid.getTransRefCurrencyCode()!=null&& !aid.getTransRefCurrencyCode().isEmpty()) {
                aidParam.setAucRefCurrencyCode(aid.getTransRefCurrencyCode());
                aidParam.setbRefCurrencyCodeExt(true);
            }

            //9F3D byte referCurrExp
            if(aid.getTransRefCurrencyExp()!=null&& !aid.getTransRefCurrencyExp().isEmpty()) {
                aidParam.setUcRefCurrencyExp(Byte.parseByte(aid.getTransRefCurrencyExp()));
                aidParam.setbRefCurrencyExpExt(true);
            }
            String terCountryCode = TopApplication.sysParam.get(SysParam.APP_PARAM_TER_COUNTRY_CODE);
            if (!terCountryCode.isEmpty()) {
                aidParam.setAucCountryCode(terCountryCode);
                aidParam.setbCountryCodeFlg(true);
            }

            AppLog.e(TAG, "mUAidDaoUtils uAid  ==" + aidParam.toString());

            return aidParam;
        } catch(Exception e){
            e.printStackTrace();
            AppLog.e(TAG, "TlvException ==");
            return null;
        }
    }

    protected EmvCapkParam saveCapk(CapkBean capk) {
        EmvCapkParam capkParam;
        capkParam = new EmvCapkParam();
        // 9f06 RID
        if (capk.getRID() != null && !capk.getRID().isEmpty()) {
            capkParam.setRID(capk.getRID());
        }
        // 9F2201
        if (capk.getKeyID() != null && !capk.getKeyID().isEmpty()) {
            AppLog.d("keyID", capk.getKeyID());
            byte[] bytes = BytesUtil.hexString2Bytes(String.format(Locale.ENGLISH, "%2s", capk.getKeyID()).replace(" ", "0"));
            AppLog.d("keyID", new String(bytes));
            capkParam.setKeyID(bytes[0] & 0xFF);
        }

        // DF02
        if (capk.getModulus() != null && !capk.getModulus().isEmpty()) {
            capkParam.setModul(capk.getModulus());
        }
        // DF03
        if (capk.getCheckSum() != null && !capk.getCheckSum().isEmpty()) {
            capkParam.setCheckSum(capk.getCheckSum());
        }

        // DF06
        if (capk.getHashIndex() != null && !capk.getHashIndex().isEmpty()) {
            capkParam.setHashInd(HexUtil.StringToByte(capk.getHashIndex())[0]);
        }
        // DF04
        if (capk.getExponent() != null && !capk.getExponent().isEmpty()) {
            capkParam.setExponent(capk.getExponent());
        }

        // DF05
        if (capk.getExpDate() != null && !capk.getExpDate().isEmpty()) {
            capkParam.setExpDate(capk.getExpDate());
        }

        // DF07
        if (capk.getArithIndex() != null && !capk.getArithIndex().isEmpty()) {
            capkParam.setArithInd(HexUtil.StringToByte(capk.getArithIndex())[0]);
        }

        if (!TextUtils.isEmpty(capkParam.getRID()) && capkParam.getKeyID() >= 0) {
            String sKeyId = BytesUtil.byte2HexString((byte) capkParam.getKeyID());
            String ridindex = capkParam.getRID() + sKeyId;
            capkParam.setRIDKeyID(ridindex);
        }
        return capkParam;
    }
}
