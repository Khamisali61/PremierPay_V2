package com.topwise.premierpay.emv;

import com.topwise.cloudpos.struct.BytesUtil;
import com.topwise.manager.emv.api.IEmv;

import com.topwise.manager.emv.enums.EKernelType;
import com.topwise.toptool.api.packer.ITlv;
import com.topwise.toptool.api.packer.TlvException;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.utils.ConfiUtils;

/**
 * 创建日期：2021/4/16 on 15:15
 * 描述:
 * 作者:wangweicheng
 */
public class EmvTags {
    /**
     * 消费 //paynext 说不要57  Abhishek，15:16 Can you please remove Tag 57 from DE55 //TlvTagUtil.TAG_57,
     */
    public static final int[] TAGS_SALE_BYTE = { 0x9F26, 0x9F27, 0x9F10, 0x9F37, 0x9F36, 0x95, 0x9A, 0x9C, 0x9F02, 0x5F2A,
            0x82, 0x9F1A, 0x9F03, 0x9F33, 0x9F34, 0x9F35, 0x9F1E, 0x84, 0x9F09, 0x9F41, 0x9F63 };
    public static final int[] TAGS_SALE_BYTE_VISA = { 0x9F26, 0x9F10, 0x9F37, 0x9F36, 0x95, 0x9A, 0x9C, 0x9F02, 0x5F2A,
            0x82, 0x9F1A, 0x9F03, 0x9F33, 0x84,0x9F6E};
    public static final int[] TAGS_SALE_BYTE_MC = { 0x9F26, 0x9F27, 0x9F10, 0x9F37, 0x9F36, 0x95, 0x9A, 0x9C, 0x9F02, 0x5F2A,
            0x82, 0x9F1A, 0x9F03, 0x9F33, 0x9F34, 0x9F35, 0x9F1E, 0x84, 0x9F09, 0x9F41, 0x9F6E};


    public static final int[]
            TAGS_CERT_BYTE = {0x57,0x5A, 0x5F2A,0x5F34, 0x82,0x84,0x8A,0x95,0x9A,0x9B,0x9C,0x9F02,0x9F03,0x9F10,0x9F1A,0x9F21,0x9F26,0x9F27,0x9F33,
             0x9F34, 0x9F36, 0x9F37,0xC0,0xF7};

    /**
     * 查余额55域EMV标签
     */
    public static final int[] TAGS_QUE = { 0x9F26, 0x9F27, 0x9F10, 0x9F37, 0x9F36, 0x95, 0x9A, 0x9C, 0x9F02, 0x5F2A,
            0x82, 0x9F1A, 0x9F03, 0x9F33, 0x9F34, 0x9F35, 0x9F1E, 0x84, 0x9F09, 0x9F41, 0x9F63 };
    /**
     * 上送
     *         postive55Tag = new String[]{TlvTagUtil.TAG_95, TlvTagUtil.TAG_9F1E,
     *                 TlvTagUtil.TAG_9F10, TlvTagUtil.TAG_9F36, TlvTagUtil.TAG_DF31};
     */
    public static final int[] TAGS_DUP_BYTE ={0x95, 0x9F1E, 0x9F10, 0x9F36, 0xDF31};

    /**
     * 冲正
     */
    public static final int[] TAGS_DUP = { 0x95, 0x9F10, 0x9F1E, 0xDF31 };
    //========================end

    public static byte[] getF55(ETransType transType, IEmv iEmv, boolean isDup, boolean isEC, TransData transData) {
        switch (transType) {
            case TRANS_SALE:
            case TRANS_SALE_WITH_CASH:
            case TRANS_CASH:
            case TRANS_REFUND:
            case TRANS_PRE_AUTH:
            case TRANS_PRE_AUTH_CMP:
            case TRANS_PRE_AUTH_VOID:
                if (isDup) {
                    return getValueList(TAGS_DUP, iEmv);
                }
                if (ConfiUtils.isToCert){
                    return getValueList(TAGS_CERT_BYTE, iEmv);
                }else{
                    if (transData.getKernelType() == EKernelType.KERNTYPE_MC.getKernelID()) {
                        return getValueList(TAGS_SALE_BYTE_MC, iEmv);
                    } else if (transData.getKernelType() == EKernelType.KERNTYPE_VISAAP.getKernelID() || transData.getKernelType() == EKernelType.KERNTYPE_VISA.getKernelID()) {
                        return getValueList(TAGS_SALE_BYTE_VISA, iEmv);
                    } else {
                        return getValueList(TAGS_SALE_BYTE, iEmv);
                    }
                }

            case BALANCE:
                return getValueList(TAGS_QUE, iEmv);
            default:
                break;
        }
        return null;
    }
    public static byte[] getF55(ETransType transType, IEmv iEmv, boolean isDup, boolean isEC) {
        switch (transType) {
            case TRANS_SALE:
            case TRANS_SALE_WITH_CASH:
            case TRANS_CASH:
            case TRANS_REFUND:
            case TRANS_PRE_AUTH:
            case TRANS_PRE_AUTH_CMP:
            case TRANS_PRE_AUTH_VOID:
                if (isDup) {
                    return getValueList(TAGS_DUP, iEmv);
                }
                if (ConfiUtils.isToCert){
                    return getValueList(TAGS_CERT_BYTE, iEmv);
                }else{
                    return getValueList(TAGS_SALE_BYTE, iEmv);
                }

            case BALANCE:
                return getValueList(TAGS_QUE, iEmv);
            default:
                break;
        }
        return null;
    }

    private static byte[] getValueList(int[] tags, IEmv emv) {
        if (tags == null || tags.length == 0) {
            return null;
        }

        ITlv tlv = TopApplication.packer.getTlv();
        ITlv.ITlvDataObjList tlvList = tlv.createTlvDataObjectList();
        for (int tag : tags) {
            try {
                byte[] value = emv.getTlv(tag);
                if (value == null || value.length == 0) {
                    if (tag == 0x9f03) {
                        value = new byte[6];
                    }else if(tag==0x5A){
                        byte[] aucTrack2 =  emv.getTlv(0x57);
                        if (aucTrack2 != null) {
                            String cardNo = getPan(BytesUtil.bytes2HexString(aucTrack2).split("F")[0]);
                            value =BytesUtil.hexString2Bytes(cardNo);
                        }
                    } else {
                        continue;
                    }
                }
                ITlv.ITlvDataObj obj = tlv.createTlvDataObject();
                obj.setTag(tag);
                obj.setValue(value);
                tlvList.addDataObj(obj);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        try {
            return tlv.pack(tlvList);
        } catch (TlvException e) {
            e.printStackTrace();
        }
        return null;
    }


    protected static String getPan(String track) {
        if (track == null)
            return null;

        int len = track.indexOf('=');
        if (len < 0) {
            len = track.indexOf('D');
            if (len < 0)
                return null;
        }

        if ((len < 10) || (len > 19))
            return null;
        return track.substring(0, len);
    }
    /**
     * transtype:交易类型，定义
     * 如下：
     * 消费 0x00
     * 查询 0x31
     * 预授权 0x03
     * 指定账户圈存 0x60
     * ⾮指定账户圈存 0x62
     * 现⾦圈存 0x63
     * 现⾦充值撤销 0x17
     * 退货 0x20
     * 消费撤销 0x20
     * ⾮指定账户圈存读转⼊卡 0xF1
     * 卡⽚余额查询 0xF2
     * 卡⽚交易日志志查询 0xF3
     * 卡⽚圈存日志查询 0xF4
     * @param transData
     * @return
     */
    public static byte checkKernelTransType(TransData transData) {
        ETransType eTransType = ETransType.valueOf(transData.getTransType());
        switch (eTransType){
            case TRANS_SALE:
            case TRANS_SALE_WITH_CASH:
            case TRANS_CASH:
                return 0x00;
            case TRANS_PRE_AUTH:
                return 0x03;
            case TRANS_VOID:
            case TRANS_REFUND:
                return 0x20;
            case BALANCE:
                return 0x30;

            default:
                return 0x00;
        }
    }
}
