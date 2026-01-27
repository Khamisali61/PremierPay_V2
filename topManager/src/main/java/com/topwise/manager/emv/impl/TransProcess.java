package com.topwise.manager.emv.impl;

import android.os.RemoteException;

import com.topwise.cloudpos.aidl.emv.level2.AidlEntry;
import com.topwise.cloudpos.aidl.emv.level2.Combination;
import com.topwise.cloudpos.aidl.emv.level2.EmvKernelConfig;
import com.topwise.cloudpos.aidl.emv.level2.EmvTerminalInfo;
import com.topwise.manager.AppLog;
import com.topwise.manager.TopUsdkManage;
import com.topwise.manager.emv.ABaseTransProcess;
import com.topwise.manager.emv.ContactEmvProcess;
import com.topwise.manager.emv.ContactLessProcess;
import com.topwise.manager.emv.api.ITransProcessListener;
import com.topwise.manager.emv.api.IEmv;
import com.topwise.manager.emv.entity.EinputType;
import com.topwise.manager.emv.entity.EmvErrorCode;
import com.topwise.manager.emv.entity.EmvTransPraram;
import com.topwise.manager.emv.entity.EmvOutCome;

import java.util.List;

/**
 * 创建日期：2021/6/10 on 16:26
 * 描述: EMV读卡流程封装实现类
 * 作者:wangweicheng
 */
public class TransProcess implements IEmv {
    private static final String TAG = TransProcess.class.getSimpleName();
    private static TransProcess instance;
    private final static String version = "version V2.0.00_20221102";
    private ABaseTransProcess baseTransProcess; // Contact or contactless EMV transaction processor
    private EinputType curInputType; // Card type

    public TransProcess() {
        AppLog.d(TAG, version);
    }

    public static synchronized TransProcess getInstance() {
        if (instance == null) {
            instance = new TransProcess();
        }
        return instance;
    }

    @Override
    public void init(EinputType einputType) {
        this.curInputType = einputType;
        AppLog.d(TAG, "init===: EinputType" + einputType.toString());

        // Generate EMV processor according to einputType
        if (EinputType.CT == einputType) {
            baseTransProcess = new ContactEmvProcess();
        } else {
            baseTransProcess = new ContactLessProcess();
        }
    }

    public void preInit(List<Combination> combinations) {
        try {
            AidlEntry entryL2 = TopUsdkManage.getInstance().getEntry();
            int emvRest = -1;
            byte[] version = new byte[64];
            /*  int ret = entryL2.getVersion(version, 64);
                if (ret == EmvErrorCode.CLSS_OK) {
                AppLog.d(TAG, "EntryLib Version: " + new String(version).trim());
            }*/

            emvRest = entryL2.initialize();
            AppLog.d(TAG, "init initialize: " + emvRest);
            if (emvRest != EmvErrorCode.CLSS_OK) {
                return ;
            }

            entryL2.delAllCombination();

            for (Combination combination : combinations) {
                emvRest = entryL2.addCombination(combination);
                AppLog.d(TAG, "init combination: " + emvRest);
            }

            AppLog.d(TAG, "enid" );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void setProcessListener(ITransProcessListener ITransProcessListener) {
        AppLog.d(TAG, "setProcessListener ===" );
        if (baseTransProcess != null) {
            baseTransProcess.setEmvProcessListener(ITransProcessListener);
        }
    }

    @Override
    public void setKernelConfig(EmvKernelConfig emvKernelConfig) {
        if (baseTransProcess != null) {
            baseTransProcess.setEmvKernelConfig(emvKernelConfig);
      //      AppLog.d(TAG, "setKernelConfig :" + emvKernelConfig.toString());
        }
    }

    @Override
    public void setTerminalInfo(EmvTerminalInfo emvTerminalInfo) {
        if (baseTransProcess != null) {
            baseTransProcess.setEmvTerminalInfo(emvTerminalInfo);
          //  AppLog.d(TAG, "setTerminalInfo :" + emvTerminalInfo.toString());
        }
    }

    @Override
    public void setTransPraram(EmvTransPraram emvTransData) {
        if (baseTransProcess != null) {
            baseTransProcess.setEmvTransData(emvTransData);
         //   AppLog.d(TAG, "setTransPraram :" + emvTransData.toString());
        }
    }

    @Override
    public byte[] getTlv(int paramInt) {
        if (baseTransProcess != null){
            return baseTransProcess.getTLV(paramInt);
        }
        return new byte[0];
    }

    @Override
    public boolean setTlv(int paramInt, byte[] paramArrayOfbyte) {
        if (paramArrayOfbyte == null) {
            return false;
        }
        AppLog.e(TAG,"setTlv TAG= " + paramInt);
        if (baseTransProcess != null) {
            return baseTransProcess.setTLV(paramInt,paramArrayOfbyte);
        }
        return false;
    }

    @Override
    public boolean setTlvList(byte[] paramArrayOfbyte) {
        return false;
    }

    @Override
    public byte[] getTlvList(int[] tagList) {
        return new byte[0];
    }

    @Override
    public EmvOutCome StartEmvProcess() {
        if (baseTransProcess != null) {
            EmvOutCome emvOutCome = baseTransProcess.StartTransProcess();
            getDebugInfo();
            AppLog.d(TAG, "emvProcess emvResult = " + emvOutCome.toString());
//          AppClose();
            return emvOutCome;
        }
        return new EmvOutCome(EmvErrorCode.EMV_OTHER_ERROR);
    }

    @Override
    public EmvOutCome CompleteEmvProcess(boolean online, String respCode, String icc55) {
        return null;
    }

	@Override
    public void getDebugInfo() {
        if (baseTransProcess != null) {
            baseTransProcess.getDebugInfo();
        }
    }

    @Override
    public void EndEmvProcess() {
        //AppClose();
    }

    private void AppClose() {
        try {
            if (baseTransProcess != null) {
                baseTransProcess = null;
            }

            boolean closeRFstatus = TopUsdkManage.getInstance().getRf().close();
            boolean closeICstatus = TopUsdkManage.getInstance().getIcc().close();
            AppLog.d(TAG, "closeRFstatus= " + closeRFstatus + " closeICstatus: " + closeICstatus);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean updateAID(int cmd, String data) {
        return false;
    }

    @Override
    public boolean updateCAPK(int cmd, String data) {
        return false;
    }
}
