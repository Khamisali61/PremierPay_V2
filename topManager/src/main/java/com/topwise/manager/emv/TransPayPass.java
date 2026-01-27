package com.topwise.manager.emv;

import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.topwise.cloudpos.aidl.emv.level2.AidlPaypass;
import com.topwise.cloudpos.aidl.emv.level2.EmvCapk;
import com.topwise.cloudpos.struct.BytesUtil;
import com.topwise.cloudpos.struct.TlvList;
import com.topwise.manager.AppLog;
import com.topwise.manager.TopUsdkManage;
import com.topwise.manager.emv.entity.ClssOutComeData;
import com.topwise.manager.emv.entity.ClssUserInterRequestData;
import com.topwise.manager.emv.entity.EmvErrorCode;
import com.topwise.manager.emv.entity.EmvOnlineResp;
import com.topwise.manager.emv.entity.EmvOutCome;

import com.topwise.manager.emv.enums.EAuthRespCode;
import com.topwise.manager.emv.enums.ECVMStatus;
import com.topwise.manager.emv.enums.EKernelType;
import com.topwise.manager.emv.enums.EOnlineResult;
import com.topwise.manager.emv.enums.EPinType;
import com.topwise.manager.emv.enums.ETransStatus;
import com.topwise.manager.emv.enums.ETransStep;
import com.topwise.manager.emv.utlis.TAGUtlis;
import com.topwise.manager.utlis.DataUtils;

/**
 * 创建日期：2021/6/18 on 16:54
 * 描述:PayPass Process
 * 作者:wangweicheng
 */
public class TransPayPass extends AClssKernelBaseTrans {
    private static final String TAG = TransPayPass.class.getSimpleName();
    private AidlPaypass paypass = TopUsdkManage.getInstance().getPaypass();

    public TransPayPass() {

    }

    @Override
    public EmvOutCome StartKernelTransProc() {
        try {
            AppLog.d(TAG, "start TransPayPass =========== ");
//          PreProcResult preProcResult = clssTransParam.getPreProcResult();

            // Pass kernel type to payment app
            AppUpdateKernelType(EKernelType.KERNTYPE_MC);

            // Initialize PayPass kernel
            int nRet = paypass.initialize(1);
            AppLog.d(TAG, "initialize nRet: " + nRet);

            // Set final select data
            nRet = paypass.setFinalSelectData(clssTransParam.getAucFinalSelectFCIdata(), clssTransParam.getnFinalSelectFCIdataLen());
            AppLog.d(TAG, "setFinalSelectData res: " + nRet);
            if (nRet != EmvErrorCode.CLSS_OK) { // ERROR Occurs
                if (nRet == EmvErrorCode.EMV_SELECT_NEXT_AID) {
                    nRet = entryL2.delCandListCurApp();
                    AppLog.d(TAG, "entryL2 delCandListCurApp nRet: " + nRet);
                    if (nRet == EmvErrorCode.CLSS_OK) {
                        return new EmvOutCome(EmvErrorCode.CLSS_RESELECT_APP, ETransStatus.SELECT_NEXT_AID, ETransStep.CLSS_KERNEL_SET_FINAL_SELECT_DATA);
                    } else {
                        return new EmvOutCome(EmvErrorCode.CLSS_USE_CONTACT, ETransStatus.TRY_ANOTHER_INTERFACE, ETransStep.CLSS_KERNEL_SET_FINAL_SELECT_DATA);
                    }
                } else {
                    return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_SET_FINAL_SELECT_DATA);
                }
            }

            // Get current aid and notice payment app
            String currentAid = getCurrentAid();
            if (TextUtils.isEmpty(currentAid)) {
                return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_SET_AID_PARAMS_TO_KERNEL);
            }

            // Load AID TLV list by aid
            TlvList kernalList = AppGetKernalDataFromAidParam(currentAid);
            if (kernalList == null) {
                return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_SET_AID_PARAMS_TO_KERNEL);
            }

            // Set tlv data list into kernel
            kernalList.addTlv("9F1D",new byte[]{0x6C,0x00,(byte)0x80,0x00,0x00,0x00,0x00,0x00});
            kernalList.addTlv("DF811B",new byte[]{(byte) 0xB0});

            AppLog.d(TAG, "entryL2 delCandListCurApp nRet: " + nRet);
            //caixh-add
            if (currentAid.startsWith("A0000000041010")) {
                kernalList.addTlv("DF8117",new byte[]{(byte) 0xE0});
                kernalList.addTlv("DF811B",new byte[]{(byte) 0xB0});
                kernalList.addTlv("DF8118",new byte[]{(byte) 0x60});
                kernalList.addTlv("DF8119",new byte[]{(byte) 0x08});
                kernalList.addTlv("DF811F",new byte[]{(byte) 0x08});
                kernalList.addTlv("9F1D",new byte[]{0x6C,0x00,(byte)0x80,0x00,0x00,0x00,0x00,0x00});
                if (clssTransParam.isbSupSimpleProc()) {
                    kernalList.addTlv("0xDF8120", "0000000000");
                    kernalList.addTlv("0xDF8122", "0000000000");
                    kernalList.addTlv("0xDF8121", "FFFFFFFFFF");
                } else {
                    kernalList.addTlv("0xDF8120", "F45084800C");
                    kernalList.addTlv("0xDF8122", "F45084800C");
                    kernalList.addTlv("0xDF8121", "0000000000");
                }
                // 测试
//                kernalList.addTlv("0xDF8123", "000000000000");
//                kernalList.addTlv("0xDF8124", "000001000000");
//                kernalList.addTlv("0xDF8125", "000001000000");
//                kernalList.addTlv("0xDF8126", "000000100000");
            }else if (currentAid.startsWith("A0000000043060")){
                kernalList.addTlv("DF8117",new byte[]{(byte) 0xE0});
                kernalList.addTlv("DF811B",new byte[]{(byte) 0xB0});
                kernalList.addTlv("DF8118",new byte[]{(byte) 0x40});
                kernalList.addTlv("DF8119",new byte[]{(byte) 0x08});
                kernalList.addTlv("DF811F",new byte[]{(byte) 0x08});
                kernalList.addTlv("9F1D",new byte[]{0x4C,0x00,(byte)0x80,0x00,0x00,0x00,0x00,0x00});
                if (clssTransParam.isbSupSimpleProc()) {
                    kernalList.addTlv("0xDF8120", "0000000000");
                    kernalList.addTlv("0xDF8122", "0000000000");
                    kernalList.addTlv("0xDF8121", "FFFFFFFFFF");
                } else {
                    kernalList.addTlv("0xDF8120", "F45004800C");
                    kernalList.addTlv("0xDF8122", "F45004800C");
                    kernalList.addTlv("0xDF8121", "0000800000");
                }
                // 测试
//                kernalList.addTlv("0xDF8123", "000000000001");
//                kernalList.addTlv("0xDF8124", "000001000000");
//                kernalList.addTlv("0xDF8125", "000001000000");
//                kernalList.addTlv("0xDF8126", "000000100000");
            }



            AppLog.d(TAG, "entryL2 setKernalData ==== ");
            byte [] kernalData = kernalList.getBytes();
            if (kernalData != null) {
                AppLog.d(TAG, "setTLVDataList nRet: " + convert.bcdToStr(kernalData));

                nRet = paypass.setTLVDataList(kernalData, kernalData.length);
                AppLog.d(TAG, "setTLVDataList nRet: " + nRet);
                if (nRet != EmvErrorCode.CLSS_OK) {
                    return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_SET_AID_PARAMS_TO_KERNEL);
                }
            }

            //Test case:MCD19_T01_S01 9F1D Terminal Risk Management Data need set  0x6C 0x00 0x80 0x00 0x00 0x00 0x00 0x00
          /*  setTLV(0x9F1D,new byte[]{0x6C,0x00,(byte)0x80,0x00,0x00,0x00,0x00,0x00});
            setTLV(0xDF811B,new byte[]{(byte) 0xB0});
*/

            // Callback app final Select aid
            if (!AppFinalSelectAid()) {
                return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_SET_AID_PARAMS_TO_KERNEL);
            }

            // GPO
            byte[] dataBuf = new byte[1];
            Log.d("Jeremy", "gpoProc begin");
            nRet = paypass.gpoProc(dataBuf);
            Log.d("Jeremy", "gpoProc end");
            AppLog.d(TAG, "gpoProc nRet: " + nRet);
            AppLog.d(TAG, "gpoProc Transaction Path: " + dataBuf[0]);
            if (nRet != EmvErrorCode.CLSS_OK) {
                if (nRet == EmvErrorCode.CLSS_RESELECT_APP) {
                    nRet = entryL2.delCandListCurApp();
                    AppLog.d(TAG, "entryL2 delCandListCurApp nRet: " + nRet);
                    if (nRet == EmvErrorCode.CLSS_OK) {
                        return new EmvOutCome(EmvErrorCode.CLSS_RESELECT_APP, ETransStatus.SELECT_NEXT_AID, ETransStep.CLSS_KERNEL_GPO_PROC);
                    } else {
                        return new EmvOutCome(EmvErrorCode.CLSS_USE_CONTACT, ETransStatus.TRY_ANOTHER_INTERFACE, ETransStep.CLSS_KERNEL_GPO_PROC);
                    }
                } else {
                    return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_GPO_PROC);
                }
            }

            // Read card record data
            Log.d("Jeremy", "readData nRet: " + nRet);
            nRet = paypass.readData();
            Log.d("Jeremy", "readData nRet: " + nRet);
            Log.d("Jeremy", "============== end ========================" );
            if (nRet != EmvErrorCode.CLSS_OK ) {
                if (nRet == EmvErrorCode.CLSS_RESELECT_APP) {
                    nRet = entryL2.delCandListCurApp();
                    AppLog.d(TAG, "entryL2 delCandListCurApp nRet: " + nRet);
                    if (nRet == EmvErrorCode.CLSS_OK) {
                        return new EmvOutCome(EmvErrorCode.CLSS_RESELECT_APP, ETransStatus.SELECT_NEXT_AID, ETransStep.CLSS_KERNEL_READ_DATA_PROC);
                    } else {
                        return new EmvOutCome(EmvErrorCode.CLSS_USE_CONTACT, ETransStatus.TRY_ANOTHER_INTERFACE, ETransStep.CLSS_KERNEL_READ_DATA_PROC);
                    }
                } else {
                    return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_READ_DATA_PROC);
                }
            }

            // Read card info
            byte[] aucTrack2 = getTLV(0x57);
            if (aucTrack2 != null) {
                String cardNo =  getPan(BytesUtil.bytes2HexString(aucTrack2).split("F")[0]);
                if (!DataUtils.isNullString(cardNo)) {
                    if (!AppConfirmPan(cardNo)) {
                        return new EmvOutCome(EmvErrorCode.EMV_USER_CANCEL, ETransStatus.END_APPLICATION, ETransStep.CLSS_APP_CONFIRM_PAN);
                    }
                }
            }

            // If it's a simple process, return with CLSS_OK here.
//            if (clssTransParam.isbSupSimpleProc()) {
//                AppLog.d(TAG, "Simple process return : " + nRet);
//                return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.ONLINE_REQUEST, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
//            }

            // Start transaction
            byte[] ucAcType = new byte[1];
            AppLog.d(TAG, "Transaction Path: " + dataBuf[0]);
            if (dataBuf[0] == EmvErrorCode.CLSS_TRANSPATH_EMV) {
                // Add current capk param to kernel
                addCapk(currentAid);
                AppLog.d(TAG, "Start transProcMChip");
                nRet = paypass.transProcMChip(ucAcType);
                AppLog.d(TAG, "End transProcMChip res: " +nRet+ "ucAcType= " + ucAcType[0]);

            } else if (dataBuf[0] == EmvErrorCode.CLSS_TRANSPATH_MAG) {
                AppLog.d(TAG, "Start transProcMag");
                nRet = paypass.transProcMag(ucAcType);
                AppLog.d(TAG, "End transProcMag res: " +nRet +" ucAcType= "+ ucAcType[0]);
            } else {
                nRet = EmvErrorCode.CLSS_TERMINATE;
            }

            getDebugInfo();

            if (nRet != EmvErrorCode.ICC_CMD_ERR) {
                AppRemovrCard();
            }

            // Refund process
            if (clssTransParam.getTransParam().getUcTransType() == EmvErrorCode.EMV_TRANS_TYPE_REFUND &&
                    (ucAcType[0] == EmvErrorCode.AC_TC || ucAcType[0] == EmvErrorCode.AC_AAC)) {
                AppLog.d(TAG, "TransType is Refund and appoval");
                EmvOnlineResp refundOnlineResp = AppReqOnlineProc();
                AppLog.d(TAG, "AppReqOnlineProc refundOnlineResp :" + refundOnlineResp.toString());
                if (EOnlineResult.ONLINE_APPROVE == refundOnlineResp.geteOnlineResult()) {
                    return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.ONLINE_APPROVE, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                }
                // ONLINE_DECLINED
                return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.ONLINE_DECLINED, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
            }

            // PayPass see phone processing, read DF8116
            ClssUserInterRequestData userInterRequestData = getUserInterRequestData();
            AppLog.d(TAG, "PayPass userInterRequestData: " + userInterRequestData.toString());
            if (userInterRequestData.getUcUIMessageID() == EmvErrorCode.CLSS_UI_MSGID_SEE_PHONE) {
                AppLog.d(TAG, "SEE PHONE");
                return new EmvOutCome(EmvErrorCode.CLSS_REFER_CONSUMER_DEVICE, ETransStatus.SEE_PHONE_TRY_AGAIN, ETransStep.CLSS_KERNEL_TRANS_PROC);
            }

            // DF8129 tag  - If it needs pwd
            clssOutComeData = getOutcomeData();
            AppLog.d(TAG, "PayPass clssOutComeData: " + clssOutComeData.toString());
            if (clssOutComeData.getUcOCCVM() == EmvErrorCode.CLSS_OC_ONLINE_PIN) { // CVM control force online pin
                // Online enciphered PIN
                emvPinEnter = AppReqImportPin(EPinType.ONLINE_PIN_REQ);
                if (ECVMStatus.ENTER_OK != emvPinEnter.getEcvmStatus()) {
                    return new EmvOutCome(EmvErrorCode.EMV_USER_CANCEL, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_TRANS_CVM);
                }
            } else if (clssTransParam.isClssForceOnlinePin()) { // App control force online pin
                // Online enciphered PIN
                emvPinEnter = AppReqImportPin(EPinType.ONLINE_PIN_REQ);
                if (ECVMStatus.ENTER_OK != emvPinEnter.getEcvmStatus()) {
                    return new EmvOutCome(EmvErrorCode.EMV_USER_CANCEL, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_TRANS_CVM);
                }
            }

            switch (clssOutComeData.getUcOCStatus()) {
                case EmvErrorCode.CLSS_OC_APPROVED: // OFFLINE_APPROVE
                    // offline success
                    AppLog.d(TAG, " OFFLINE_APPROVE");
                    return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.OFFLINE_APPROVE, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                case EmvErrorCode.CLSS_OC_ONLINE_REQUEST:
                    // online success
                    AppLog.d(TAG, " ONLINE_REQUEST");

                    // Request for online processing and get online response data from issue bank
                    emvOnlineResp = AppReqOnlineProc();
                    AppLog.d(TAG, "emvOnlineResp " +emvOnlineResp.toString());

                    // Check response data and script
                    if (EOnlineResult.ONLINE_APPROVE == emvOnlineResp.geteOnlineResult()) {
                        if (emvOnlineResp.isExistAuthRespCode()) {
                            // Check 8A Authorisation Response Code
                            boolean transResSatus = EAuthRespCode.checkTransResSatus(convert.bcdToStr(emvOnlineResp.getAuthRespCode()), EKernelType.KERNTYPE_AMEX);
                            AppLog.d(TAG, "ARQC checkTransResSatus transResSatus " + transResSatus);
                            if (transResSatus) { // ONLINE_APPROVE
                                return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.ONLINE_APPROVE, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                            }
                        }
                    } else if (EOnlineResult.NEED_INSERT == emvOnlineResp.geteOnlineResult()) {
                        return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.NEED_INSERT, ETransStep.EMV_APP_CON_2GAC);
                    } else if (EOnlineResult.NEED_PWD == emvOnlineResp.geteOnlineResult()) {
                        return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.NEED_PWD, ETransStep.EMV_APP_CON_2GAC);
                    }
                    // ONLINE_DECLINED
                    return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.ONLINE_DECLINED, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                case EmvErrorCode.CLSS_OC_DECLINED: // OFFLINE_DECLINED
                    // transaction reject
                    AppLog.d(TAG, "AAC transaction reject");
                    return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.OFFLINE_DECLINED, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                case EmvErrorCode.CLSS_OC_END_APPLICATION:
                    AppLog.d(TAG, "END APPLICATION");
                   if (clssOutComeData.getUcOCStart()==0x10&& userInterRequestData.getUcUIMessageID() == EmvErrorCode.CLSS_UI_MSGID_SEE_PHONE){
                       AppLog.d(TAG, "SEE PHONE");
                       return new EmvOutCome(EmvErrorCode.CLSS_REFER_CONSUMER_DEVICE,ETransStatus.SEE_PHONE_TRY_AGAIN,ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                   }
                   return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                case EmvErrorCode.CLSS_OC_TRY_ANOTHER_INTERFACE:
                    AppLog.d(TAG, "TRY ANOTHER INTERFACE");
                    return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.TRY_ANOTHER_INTERFACE, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                default:
                    return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
            }
        } catch(Exception e) {
            e.getMessage();
        }
        return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
    }

    @Override
    public byte[] getTLV(int tag) {
        AppLog.d(TAG, "getTLV tag : " + tag);
        byte [] aucVale = new byte[256];
        int [] aucLen  = new int[2];
        byte[] aucTag = TAGUtlis.tagFromInt(tag);
        AppLog.d(TAG, "getTLV aucTag : " + convert.bcdToStr(aucTag));
        try {
            if (EmvErrorCode.CLSS_OK == paypass.getTLVDataList(aucTag, aucTag.length,256, aucVale, aucLen)) {
                byte [] aucResp = new byte[aucLen[0]];
                System.arraycopy(aucVale, 0, aucResp, 0, aucLen[0]);
                AppLog.d(TAG, "getTLV aucResp : " + convert.bcdToStr(aucResp));
                return aucResp;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public boolean setTLV(int tag, byte[] datas) {
        if (datas == null) {
            AppLog.d(TAG, "vale data is null " );
            return false;
        }
        AppLog.d(TAG, "setTLV T : " + tag);
        byte[] aucTag = TAGUtlis.tagFromInt(tag);
        AppLog.d(TAG, "setTLV T : " + convert.bcdToStr(aucTag));
        byte[] aucLen = TAGUtlis.genLen(datas.length);
        AppLog.d(TAG, "setTLV L : " + convert.bcdToStr(aucLen));
        AppLog.d(TAG, "setTLV V : " + convert.bcdToStr(datas));
//        System.arraycopy(aid, 0, rid, 0, 5);
        byte[] aucTLV = new byte[aucTag.length + aucLen.length + datas.length];
        System.arraycopy(aucTag,0, aucTLV,0, aucTag.length);
        System.arraycopy(aucLen,0, aucTLV, aucTag.length, aucLen.length);
        System.arraycopy(datas,0, aucTLV, (aucTag.length + aucLen.length), datas.length);

        AppLog.d(TAG, "setTLV TLV : " + convert.bcdToStr(aucTLV));
        try {
            int nRet = paypass.setTLVDataList(aucTLV, aucTLV.length);
            AppLog.d(TAG, "setTLVDataList nRet " + nRet);
            if (nRet == EmvErrorCode.CLSS_OK) {
                return true;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取当前rid
     *
     * @return 当前应用rid
     */
    @Override
    protected String getCurrentAid() {
        byte[] tlvVale = getTLV(0x4F);
        if (tlvVale != null) {
            AppLog.d(TAG, "getCurrentAid TAG 4F: " + convert.bcdToStr(tlvVale));
            return convert.bcdToStr(tlvVale);
        }
        return null;
    }

    @Override
    protected ClssOutComeData getOutcomeData() {
        byte[] tlvOutCome = getTLV(0xDF8129);
        if (tlvOutCome != null) {
            AppLog.d(TAG, "getOutcomeData : " + convert.bcdToStr(tlvOutCome));
            return new ClssOutComeData(tlvOutCome);
        }
        return new ClssOutComeData();
    }

    private ClssUserInterRequestData getUserInterRequestData() {
        byte[] tlvUserInterRequestData = getTLV(0xDF8116);
        if (tlvUserInterRequestData != null) {
            AppLog.d(TAG, "ClssUserInterRequestData : " + convert.bcdToStr(tlvUserInterRequestData));
            return new ClssUserInterRequestData(tlvUserInterRequestData);
        }
        return new ClssUserInterRequestData();
    }

    /**
     * 添加CAPK到交易库
     */
    @Override
    protected  boolean addCapk(String aid) {
        try{
            int res;
            AppLog.d(TAG, "addCapk ======== aid: " + aid);
            paypass.delAllRevocList();
            paypass.delAllCAPK();
            byte[] tlvCapkIndex = getTLV(0x8F);
            if (tlvCapkIndex != null && tlvCapkIndex.length == 0x01) {
                EmvCapk emvCapk = AppFindCapkParamsProc(BytesUtil.hexString2Bytes(aid), tlvCapkIndex[0]);
                if (emvCapk != null) {
                    res = paypass.addCAPK(emvCapk);
                    AppLog.d(TAG, "amexPay addCAPK res: " + res);
                    if (res == EmvErrorCode.CLSS_OK) {
                        return true;
                    }
                }
            }
        } catch(RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void getDebugInfo() {
        byte aucAssistInfo[] = new byte[4096];
        int nErrcode[] = new int[1];
        try {
            int nRet = paypass.getDebugInfo(aucAssistInfo.length, aucAssistInfo, nErrcode);
            AppLog.e(TAG, "getDebugInfo nRet: " + nRet);
            AppLog.e(TAG, "getDebugInfo nErrcode: " + nErrcode[0]);
            AppLog.e(TAG, "getDebugInfo aucAssistInfo: " + new String(aucAssistInfo).trim());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
