package com.topwise.premierpay.emv;

import android.os.ConditionVariable;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;

import com.topwise.cloudpos.aidl.emv.level2.Combination;
import com.topwise.cloudpos.aidl.emv.level2.EmvCandidateItem;
import com.topwise.cloudpos.aidl.emv.level2.EmvCapk;
import com.topwise.cloudpos.aidl.shellmonitor.AidlShellMonitor;
import com.topwise.cloudpos.aidl.shellmonitor.InstructionSendDataCallback;
import com.topwise.cloudpos.struct.BytesUtil;
import com.topwise.kdialog.DialogSure;
import com.topwise.kdialog.DialogSureCancel;
import com.topwise.manager.AppLog;

import com.topwise.manager.TopUsdkManage;
import com.topwise.manager.emv.api.IEmv;
import com.topwise.manager.emv.entity.EmvAidParam;
import com.topwise.manager.emv.entity.EmvOnlineResp;
import com.topwise.manager.emv.entity.EmvPinEnter;
import com.topwise.manager.emv.enums.ECVMStatus;
import com.topwise.manager.emv.enums.EKernelType;
import com.topwise.manager.emv.enums.EOnlineResult;
import com.topwise.manager.emv.enums.EPinType;
import com.topwise.manager.emv.impl.ETransProcessListenerImpl;

import com.topwise.toptool.api.convert.IConvert;
import com.topwise.toptool.api.packer.ITlv;
import com.topwise.toptool.api.packer.TlvException;
import com.topwise.premierpay.BuildConfig;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.daoutils.DaoUtilsStore;
import com.topwise.premierpay.param.AidParam;
import com.topwise.premierpay.param.AppCombinationHelper;
import com.topwise.premierpay.param.CapkParam;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.action.ActionCardConfirm;
import com.topwise.premierpay.trans.action.ActionEnterPin;
import com.topwise.premierpay.trans.action.ActionFingerprint;
import com.topwise.premierpay.trans.action.ActionMultiSelect;
import com.topwise.premierpay.trans.action.ActionSearchCard;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.TestParam;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.transmit.TransProcessListener;
import com.topwise.premierpay.transmit.TransProcessListenerImpl;
import com.topwise.premierpay.transmit.iso8583.Online;
import com.topwise.premierpay.utils.ConfiUtils;
import com.topwise.premierpay.utils.ThreadPoolUtils;
import com.topwise.premierpay.utils.TrackUtils;
import com.topwise.premierpay.view.TopToast;

import java.util.List;

/**
 * 创建日期：2021/6/11 on 15:19
 * 描述:emv没有用 锁的操作，耗时的操作应用 处理
 * 作者:wangweicheng
 */
public class EmvTransProcessImpl extends ETransProcessListenerImpl {
    private static final String TAG = EmvTransProcessImpl.class.getSimpleName();

    private TransData transData;
    private ConditionVariable cv;
    private boolean isConfirm =false;
    private EmvOnlineResp emvOnlineResp;
    private EmvPinEnter emvPinEnter;
    private int intResult;
    private IEmv emv;
    private IConvert convert = TopApplication.convert;
    private TransProcessListener transProcessListener;
    private Handler mhandler;
    private int nMuitiAppSelectcIndex = -1;
    private boolean b2SeachCardStatus;
    boolean isSure = false;

    public EmvTransProcessImpl(Handler mhandler, TransData transData, IEmv emv) {
        this.mhandler = mhandler;
        this.transData = transData;
        this.emv = emv;
    }

    /**
     * Multi application selection function
     *
     * @param aids
     * @return
     */
    @Override
    public int onReqAppAidSelect(final String[] aids) {
        AppLog.d(TAG,"requestAidSelect aid size " + aids.length);
        if (aids == null || aids.length == 0) {
            return -1;
        }
        if (aids.length == 1) {
            return 0;
        }
        nMuitiAppSelectcIndex = -1;

        cv = new ConditionVariable();
        ActionMultiSelect actionMultiSelect = new ActionMultiSelect(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionMultiSelect)action).setParam(mhandler, TopApplication.mApp.getString(R.string.select_aid), aids);
            }
        }) ;
        actionMultiSelect.setEndListener(new AAction.ActionEndListener() {
            @Override
            public void onEnd(AAction action, ActionResult result) {
                if (result.getRet() == TransResult.SUCC) {
                    nMuitiAppSelectcIndex = Integer.valueOf((String) result.getData());
                }
                if (cv != null) {
                    cv.open();
                    cv = null;
                }
            }
        });
        actionMultiSelect.execute();

        if (cv != null) {
            cv.block();
        }

        AppLog.d(TAG,"requestAidSelect nMuitiAppSelectcIndex " + nMuitiAppSelectcIndex);
        return nMuitiAppSelectcIndex;
    }

    @Override
    public void onUpToAppEmvCandidateItem(EmvCandidateItem emvCandidateItem) {
        if (emvCandidateItem != null) {
            AppLog.d(TAG,"onUpdateEmvCandidateItem  DisplayName:" + new String(emvCandidateItem.getAucDisplayName()).trim());
        }
    }

    @Override
    public void onUpToAppKernelType(EKernelType kernelType) {
        AppLog.d(TAG,"onUpdateKernelType = " + kernelType.toString());
        transData.setKernelType(kernelType.getKernelID());
    }

    /**
     * app can get 9F06 SELECTED AID
     * @return
     */
    @Override
    public boolean onReqFinalAidSelect() {
        AppLog.d(TAG,"finalAidSelect = ");
        return true;
    }

    /**
     * 由应用确认 是否确认卡号，通常非接触不需要
     * Confirmed by the application Whether to confirm the card number, usually contactLess does not need
     * @param cardNo
     * @return
     */
    @Override
    public boolean onConfirmCardInfo(String cardNo) {
        AppLog.d(TAG,"onConfirmCardInfo = " + cardNo);
        transData.setPan(cardNo);
       // if contactLess return
        if (transData.getEnterMode() == Component.EnterMode.QPBOC || transData.isStressTest()) {
            return true;
        }
        return checkCardPan();
    }

    /**
     *
     * @param pinType
     * @param leftTimes
     * @return
     */
    @Override
    public EmvPinEnter onReqGetPinProc(EPinType pinType, int leftTimes) {
        AppLog.d(TAG,"onReqGetPinProc = " + pinType.toString() + " leftTimes= " + leftTimes);

        if (transData.isStressTest()) {
            String pinblock ="AD03432FAC3C7D8B";
            EmvPinEnter emvPinEnter = new EmvPinEnter();
            emvPinEnter.setEcvmStatus(ECVMStatus.ENTER_OK);
            emvPinEnter.setPlainTextPin(pinblock);

            transData.setHasPin(true);
            transData.setPin(pinblock);
            TestParam testParam = DaoUtilsStore.getInstance().getTestParam();
            final int delayTime = testParam.getDelayTime();
            if (delayTime != 0) {
                final AidlShellMonitor shellMonitor = TopUsdkManage.getInstance().getShellMonitor();
                ThreadPoolUtils.getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            shellMonitor.sendIns(60, (byte) 0x6C, (byte)0x24, (byte) 0x02, new byte[]{0x01, 0x10}, new InstructionSendDataCallback.Stub() {
                                @Override
                                public void onReceiveData(byte b, byte[] bytes) throws RemoteException {
                                    AppLog.d(TAG, "lightOn = " + b);
                                }
                            });
                            SystemClock.sleep(5 * 1000);
                            shellMonitor.sendIns(60, (byte) 0x6C, (byte) 0x24, (byte) 0x02, new byte[]{0x02, 0x10}, new InstructionSendDataCallback.Stub() {
                                @Override
                                public void onReceiveData(byte b, byte[] bytes) throws RemoteException {
                                    AppLog.d(TAG, "lightAuto = " + b);
                                }
                            });
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            return emvPinEnter;
        }

        if ("Y".equals(TopApplication.sysParam.get(SysParam.PARAM_FINGERPRINT))) {
            return checkFingerprint();
        } else {
            return checkPin(pinType,leftTimes);
        }
    }

    /**
     * 联机处理
     * a.脚本数据 有app 设置
     * b.非接不会走这个流程
     * @return
     */
    @Override
    public EmvOnlineResp onReqOnlineProc() {
        AppLog.d(TAG,"onRequestOnline = ");
        emvOnlineResp = new EmvOnlineResp();

        emvOnlineResp.seteOnlineResult(EOnlineResult.ONLINE_ABORT);
        try {
            ETransType transType = ETransType.valueOf(transData.getTransType());
            //online
            // read ARQC
            byte[] arqc = emv.getTlv(0x9f26);
            if (arqc != null && arqc.length > 0) {
                transData.setArqc(convert.bcdToStr(arqc));
            }
            // //IT26卡 纯电子现金 “消费” 消费金额超过余额时转联机
            byte[] aid = emv.getTlv(0x4f);
            saveCardInfoAndCardSeq(transData);
            saveTvrTsi();
            AppLog.w(TAG, "onReqOnlineProc transData.getKernelType()：" + transData.getKernelType());
            AppLog.w(TAG, "onReqOnlineProc EKernelType.KERNTYPE_MC：" + EKernelType.KERNTYPE_MC.getKernelID());
            AppLog.w(TAG, "onReqOnlineProc USE TAG SALE MC：" + (transData.getKernelType() == EKernelType.KERNTYPE_MC.getKernelID()));
            byte[] mccByte = emv.getTlv(0x9F15);
            if (mccByte != null && mccByte.length > 0) {
                AppLog.w(TAG, "mccByte: " + convert.bcdToStr(mccByte));
                transData.setMcc(convert.bcdToStr(mccByte));
            }
            // 生成联机的55域数据
//            byte[] f55 = EmvTags.getF55(transType, emv,false, false);
//            byte[] f55Dup = EmvTags.getF55(transType, emv, true, false);
            byte[] f55 = EmvTags.getF55(transType, emv,false, false, transData);
            byte[] f55Dup = EmvTags.getF55(transType, emv, true, false, transData);
            if (f55 != null && f55.length > 0) {
                transData.setSendIccData(convert.bcdToStr(f55));
                AppLog.w(TAG, "genLen setSendIccData：" + convert.bcdToStr(f55));
            }
            if (f55Dup != null && f55Dup.length > 0) {
                transData.setDupIccData(convert.bcdToStr(f55Dup));
            }
            transData.setOnlineTrans(true);
            int commResult; //1 success e faild
            transProcessListener = new TransProcessListenerImpl();
            transProcessListener.onUpdateProgressTitle(transType.getTransName().toUpperCase());

            int online = Online.getInstance().online(transData, transProcessListener);
            transData.getTransStatusSum().setResult(online);
            DaoUtilsStore.getInstance().updateStatus(transData);

            AppLog.d(TAG,"onRequestOnline online = " + online);
            if (online == TransResult.SUCC) {
                if ("00".equals(transData.getResponseCode())) {
                    commResult = 1;
                } else { //联机拒绝
                    commResult = 2;
                }
            } else {
                if (online == TransResult.ERR_NEED_ENTER_PWD){
                    emvOnlineResp.seteOnlineResult(EOnlineResult.NEED_PWD);
                } else if (online == TransResult.ERR_NEED_INSTER_CARD) {
                    emvOnlineResp.seteOnlineResult(EOnlineResult.NEED_INSERT);
                } else {
                    //can show tip
                    emvOnlineResp.seteOnlineResult(EOnlineResult.ONLINE_ABORT);
                }
                return emvOnlineResp;
            }
            AppLog.d(TAG,"onRequestOnline commResult = " + commResult);
            String rspF55 = transData.getRecvIccData();
            AppLog.d(TAG,"rspF55 = " + rspF55);
            ITlv tlv = TopApplication.packer.getTlv();
            if (rspF55 != null && rspF55.length() > 0) {
                // 设置授权数据
                byte[] resp55 = convert.strToBcd(rspF55, IConvert.EPaddingPosition.PADDING_LEFT);
                ITlv.ITlvDataObjList list = null;

                list = tlv.unpack(resp55);
                byte[] value91 = list.getValueByTag(0x91);
                if (value91 != null && value91.length > 0) {
                    emvOnlineResp.setIssueAuthData(value91);
                    emvOnlineResp.setExistIssAuthData(true);
                }
                // 设置脚本 71
                byte[] value71 = list.getValueByTag(0x71);
                if (value71 != null && value71.length > 0) {
                    emvOnlineResp.setIssueScript71(value71);
                    emvOnlineResp.setExistIssScr71(true);
                }

                // 设置脚本 72
                byte[] value72 = list.getValueByTag(0x72);
                if (value72 != null && value72.length > 0) {
                    ITlv.ITlvDataObj obj = tlv.createTlvDataObject();
                    obj.setTag(0x72);
                    obj.setValue(value72);
                    emvOnlineResp.setIssueScript72(value72);
                    emvOnlineResp.setExistIssScr72(true);
                }
                // 设置脚本 89
                byte[] value89 = list.getValueByTag(0x89);
                if (value89 != null && value89.length > 0) {
                    emv.setTlv(0x89, value89);
                    emvOnlineResp.setAuthCode(value89);

                    emvOnlineResp.setExistAuthCode(true);
                }
                //  set script  8A
                byte[] value8A = list.getValueByTag(0x8A);
                if (value8A != null && value8A.length > 0) {
                    emv.setTlv(0x8A, value8A);
                    emvOnlineResp.setAuthRespCode(value8A);
                    emvOnlineResp.setExistAuthRespCode(true);
                } else if (!TextUtils.isEmpty(transData.getResponseCode())) {
                    emvOnlineResp.setExistAuthRespCode(true);
//                    transData.getResponseCode().getBytes();
                    emvOnlineResp.setAuthRespCode(transData.getResponseCode().getBytes());
                }
            } else if (!TextUtils.isEmpty(transData.getResponseCode())) {
                emvOnlineResp.setExistAuthRespCode(true);
//                emvOnlineResp.setAuthRespCode(convert.strToBcd(transData.getResponseCode(), IConvert.EPaddingPosition.PADDING_RIGHT));
                emvOnlineResp.setAuthRespCode(transData.getResponseCode().getBytes());
            }
            if (commResult != 1) {
                //delect dup
                Device.beepFail();
//                transProcessListener.onShowMessageWithConfirm(
//                        context.getString(R.string.emv_err_code) + transData.getResponseCode()
//                                + context.getString(R.string.emv_err_info) + transData.getResponseMsg(),
//                        Constants.FAILED_DIALOG_SHOW_TIME);
                emvOnlineResp.seteOnlineResult(EOnlineResult.ONLINE_REFER);
                return emvOnlineResp;
            }
            if (ConfiUtils.isDebug || "topwise".startsWith(BuildConfig.CHANNEL)) {
                emvOnlineResp.setExistAuthRespCode(true);
                emvOnlineResp.setAuthRespCode(TopApplication.convert.strToBcd("3030", IConvert.EPaddingPosition.PADDING_RIGHT));
            }
            emvOnlineResp.seteOnlineResult(EOnlineResult.ONLINE_APPROVE);
            return emvOnlineResp;
        } catch (TlvException e) {
            e.printStackTrace();

        } finally {
            if (transProcessListener != null){
                transProcessListener.onHideProgress();
                transProcessListener = null;
            }
        }

        return emvOnlineResp;
    }

    /**
     * check card pan
     */
    private boolean checkCardPan() {
        AppLog.d(TAG,"checkCardPan=========00= " + isConfirm);
        cv = new ConditionVariable();

        ActionCardConfirm actionCardConfirm = new ActionCardConfirm(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ETransType eTransType = ETransType.valueOf(transData.getTransType());
                String amount = "";
                if (ETransType.TRANS_SALE_WITH_CASH == eTransType) {
                    amount = transData.getCardAmount();
                } else {
                    amount = transData.getAmount();
                }
                ((ActionCardConfirm)action).setParam(ActivityStack.getInstance().top(), eTransType.getTransName(), transData.getPan(), amount);
            }
        });

        actionCardConfirm.setEndListener(new AAction.ActionEndListener() {
            @Override
            public void onEnd(AAction action, ActionResult result) {
                if (TransResult.SUCC == result.getRet()) {
                    isConfirm = true;
                } else {
                    isConfirm = false;
                }
                AppLog.d(TAG,"checkCardPan=========11= " +isConfirm);
//                ActivityStack.getInstance().pop();
                cv.open();
            }
        });

        actionCardConfirm.execute();

        if (cv != null)
            cv.block();

        AppLog.d(TAG,"checkCardPan=========22= " +isConfirm);
        return isConfirm;
    }

    /**
     * Check PIN
     * @param ePinType
     * @return
     */
    private EmvPinEnter checkPin(final EPinType ePinType, final int leftTimes) {
        cv = new ConditionVariable();
        emvPinEnter = new EmvPinEnter();

        ActionEnterPin actionEnterPin = new ActionEnterPin(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ETransType eTransType = ETransType.valueOf(transData.getTransType());
                String amount = "", cashAmout = "";
                if (ETransType.TRANS_SALE_WITH_CASH == eTransType) {
                    amount = transData.getCardAmount();
                    cashAmout = transData.getCashAmount();
                } else {
                    amount = transData.getAmount();
                }
                if ((boolean)SysParam.get(SysParam.PCI_MODE, false)) {
                    AppLog.i(TAG,"PCI_MODE Input PIN...");
                    ((ActionEnterPin) action).setParam(ActivityStack.getInstance().top(),
                            eTransType.getTransName(),
                            transData.getPan(),
                            amount, cashAmout,
                            EPinType.PCI_MODE_REQ, leftTimes);
                } else {
                    AppLog.i(TAG,"COMM_MODE Input PIN...");
                    ((ActionEnterPin) action).setParam(ActivityStack.getInstance().top(),
                            eTransType.getTransName(),
                            transData.getPan(),
                            amount, cashAmout,
                            ePinType, leftTimes);
                }
            }
        });
        actionEnterPin.setEndListener(new AAction.ActionEndListener() {
            @Override
            public void onEnd(AAction action, ActionResult result) {
                    if (result.getRet() == TransResult.SUCC) {
                        String pinblock = (String)result.getData();
                        String byPassStatus = TopApplication.sysParam.get(SysParam.APP_PARAM_SUP_BYPASS);
                        boolean bPciMode = (boolean)TopApplication.sysParam.get(SysParam.PCI_MODE, false);

                        AppLog.i(TAG,"byPassStatus: " + byPassStatus);
                        AppLog.i(TAG,"bPciMode: " + bPciMode);
                        AppLog.i(TAG,"pinblock: " + pinblock);
                        AppLog.i(TAG,"ePinType: " + ePinType);

                        // PCI PIN模式得到值为"00",表示没有输入PIN直接按了确认. -by wxz 20230418
                        if (pinblock.equals("00") && bPciMode == true) {
                            pinblock = "";
                        }

                        if ("Y".equals(byPassStatus) && TextUtils.isEmpty(pinblock)) { //ter sup bypass
                            emvPinEnter.setEcvmStatus(ECVMStatus.ENTER_BYPASS);
                            transData.setHasPin(false);
                        } else {
                            emvPinEnter.setEcvmStatus(ECVMStatus.ENTER_OK);
                            emvPinEnter.setPlainTextPin(pinblock);
                            if (ePinType == EPinType.ONLINE_PIN_REQ) {
                                transData.setHasPin(true);
                                transData.setPin(pinblock);
                            } else {
                                transData.setHasPin(false);
                                transData.setPin("");
                            }
                        }
                    } else {
                        emvPinEnter.setEcvmStatus(ECVMStatus.ENTER_CANCEL);
                        transData.setHasPin(false);
                    }

                if (cv != null) {
                    cv.open();
                }
                AppLog.i(TAG,"ActionEnterPin onEnd ======" + result.getRet());
            }
        });
        actionEnterPin.execute();
        if (cv != null) {
            cv.block();
        }
        AppLog.d(TAG,"checkPin=========22= " + emvPinEnter.toString());
        return emvPinEnter;
    }

    /**
     * Check fingerPrint
     * @return
     */
    private EmvPinEnter checkFingerprint() {
        cv = new ConditionVariable();
        emvPinEnter = new EmvPinEnter();
        ActionFingerprint actionFingerprint = new ActionFingerprint(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ETransType eTransType = ETransType.valueOf(transData.getTransType());
                ((ActionFingerprint) action).setParam(ActivityStack.getInstance().top(), eTransType.getTransName(), transData);
            }
        });
        actionFingerprint.setEndListener(new AAction.ActionEndListener() {
            @Override
            public void onEnd(AAction action, ActionResult result) {
                if (result.getRet() == TransResult.SUCC) {
                    String fingerprint = (String) result.getData();
                    emvPinEnter.setEcvmStatus(ECVMStatus.ENTER_OK);
                    emvPinEnter.setPlainTextPin("");
                    transData.setPin("");
                    transData.setFingerprint(fingerprint);
                } else {
                    emvPinEnter.setEcvmStatus(ECVMStatus.ENTER_CANCEL);
                    transData.setHasPin(false);
                }

                if (cv != null) {
                    cv.open();
                }
                AppLog.i(TAG,"actionFingerprint onEnd ======" + result.getRet());
            }
        });
        actionFingerprint.execute();
        if (cv != null) {
            cv.block();
        }
        return emvPinEnter;
    }

    /**
     AID	    M	Tag 84
     App Name	M	Tag 9F12
     TC     	M	Tag 9F26 (2GAC)
     TVR	    M	Tag 95 (2GAC)
     TSI	    M	Tag 9B (2GAC)
     Card Holder Name	M	Tag 5F20
     */
    private void saveTvrTsi() {
        IEmv emv = TopApplication.usdkManage.getEmvHelper();
        String temp = "";
        try {
            byte[] valueTVR = emv.getTlv(0x95);
            if (valueTVR != null && valueTVR.length > 0) {
                temp = TopApplication.convert.bcdToStr(valueTVR);
                if (!TextUtils.isEmpty(temp)) transData.setTvr(temp);
                AppLog.emvd("EmvResultUtlis setTVR(): " + temp);
            }
            byte[] valueATC = emv.getTlv(0x9F36);
            if (valueATC != null && valueATC.length > 0) {
                temp = TopApplication.convert.bcdToStr(valueATC);
                if (!TextUtils.isEmpty(temp)) transData.setAtc(temp);
                AppLog.emvd("EmvResultUtlis setATC(): " + temp);
            }
            //0x9B
            byte[] valueTis  = emv.getTlv(0x9B);
            if (valueTis != null && valueTis.length > 0) {
                temp = TopApplication.convert.bcdToStr(valueTis);
                if (!TextUtils.isEmpty(temp)) transData.setTsi(temp);
                AppLog.emvd("EmvResultUtlis setTsi(): " + temp);
            }
            //0x9F26
            byte[] valueTc  = emv.getTlv(0x9F26);
            if (valueTc != null && valueTc.length > 0) {
                temp = TopApplication.convert.bcdToStr(valueTc);
                if (!TextUtils.isEmpty(temp)) transData.setTc(temp);
                AppLog.emvd("EmvResultUtlis setTc(): " + temp);
            }
            //0x9F12
            byte[] valueAppname  = emv.getTlv(0x9F12);
            if (valueAppname != null && valueAppname.length > 0) {
                temp = new String(valueAppname);
                if (!TextUtils.isEmpty(temp)) transData.setEmvAppName(temp);
                AppLog.emvd("EmvResultUtlis setEmvAppName(): " + temp);
            }
            //0x84
            byte[] valueAid  = emv.getTlv(0x84);
            if (valueAid != null && valueAid.length > 0) {
                temp = TopApplication.convert.bcdToStr(valueAid);
                if (!TextUtils.isEmpty(temp)) transData.setAid(temp);
               AppLog.emvd("EmvResultUtlis setAid(): " + temp);
            }
            byte[] valuehname  = emv.getTlv(0x5F20);
            if (valuehname != null && valuehname.length > 0) {
//                AppLog.emvd("EmvResultUtlis Card Holder Name(): " + TopApplication.convert.bcdToStr(valuehname));
                temp = new String(valuehname);
                if (!TextUtils.isEmpty(temp)) transData.setCardHolderName(temp);
                AppLog.emvd("EmvResultUtlis Card Holder Name(): " + temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存磁道信息， 卡号， 有效期， 卡片序列号
     *
     * @param transData
     */
    private void saveCardInfoAndCardSeq(TransData transData) {
        byte[] track2 = emv.getTlv(0x57);
        String strTrack2 = convert.bcdToStr(track2);
        strTrack2 = strTrack2.split("F")[0];
        transData.setTrack2(strTrack2);
        AppLog.d(TAG," saveCardInfoAndCardSeq  strTrack2 =" + strTrack2);
        // 卡号
        String pan = TrackUtils.getPan(strTrack2);
        transData.setPan(pan);
        AppLog.d(TAG," saveCardInfoAndCardSeq  pan =" + pan);
        // 有效期
        byte[] expDate = emv.getTlv(0x5f24);
        if (expDate != null && expDate.length > 0) {
            String temp = convert.bcdToStr(expDate);
            transData.setExpDate(temp.substring(0, 4));
            AppLog.d(TAG," saveCardInfoAndCardSeq 有效期 expDate =" + temp);
        }
        // 获取卡片序列号
        byte[] cardSeq = emv.getTlv(0x5f34);
        if (cardSeq != null && cardSeq.length > 0) {
            String temp = convert.bcdToStr(cardSeq);
            temp = String.format("%03d", Integer.parseInt(temp.substring(0, 2)));
            transData.setCardSerialNo(temp);
            AppLog.d(TAG," saveCardInfoAndCardSeq 卡片序列号 cardSeq =" + temp);
        }
    }

    //check
    @Override
    public boolean onSecCheckCardProc() {
        b2SeachCardStatus  =false;
        cv = new ConditionVariable();
        mhandler.post(new Runnable() {
            @Override
            public void run() {
                ActionSearchCard actionSearchCard = new ActionSearchCard(new AAction.ActionStartListener() {
                    @Override
                    public void onStart(AAction action) {
                        ((ActionSearchCard) action).setParam(
                                ActivityStack.getInstance().top(),
                                ETransType.valueOf(transData.getTransType()).getTransName(),
                                ActionSearchCard.SearchMode.TAP,
                                transData.getAmount());
                    }
                });
                actionSearchCard.setEndListener(new AAction.ActionEndListener() {
                    @Override
                    public void onEnd(AAction action, ActionResult result) {
                        if (result.getRet() == TransResult.SUCC) {
                            b2SeachCardStatus  =true;
                        } else {
                            b2SeachCardStatus  =false;
                        }
                        if (cv != null) {
                            cv.open();
                        }
                    }
                });
                actionSearchCard.execute();
            }
        });

        if (cv != null) {
            cv.block();
        }

        AppLog.d(TAG," onSecCheckCardProc =========" +b2SeachCardStatus);

        return b2SeachCardStatus;
    }

    /**
     * Notice card holder to remove card
     */
    @Override
    public void onRemoveCardProc() {
        mhandler.post(new Runnable() {
            @Override
            public void run() {
                TopToast.showNormalToast(ActivityStack.getInstance().top(),"PLS REMOVE CARD");
            }
        });
    }

    @Override
    public List<Combination> onLoadCombinationParam() {
        AppLog.d(TAG," onLoadCombinationParam =========");
        return AppCombinationHelper.getInstance().getAppCombinationList();
    }

    @Override
    public EmvCapk onFindIssCapkParamProc(String sRid, byte bCapkIndex) {
        AppLog.d(TAG," onFindIssCapkParamProc =========" +sRid +" bCapkIndex = "+ BytesUtil.byte2HexString(bCapkIndex));
        return CapkParam.getEmvCapkParam(sRid,bCapkIndex);
    }

    @Override
    public EmvAidParam onFindCurAidParamProc(String sAid) {
        AppLog.d(TAG," onFindCurAidParamProc =========" +sAid );
        return AidParam.getCurrentAidParam(sAid);
    }

    /**
     * 显示脱机密码次数
     * @param PinTryCounter
     * @return
     */
    @Override
    public boolean onDisplayPinVerifyStatus(final int PinTryCounter) {
        AppLog.d(TAG," onDisplayPinVerifyStatus =========" + PinTryCounter);
        isSure = false;
        cv = new ConditionVariable();
        mhandler.post(new Runnable() {
            @Override
            public void run() {
                if (PinTryCounter <= 0x00) {
                    final DialogSure dialogSure = new DialogSure(ActivityStack.getInstance().top());
                    dialogSure.setTitle("PIN Verify Status");
                    dialogSure.setContent("PIN WAS LOCKED");
                    dialogSure.setSureListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogSure.dismiss();
                            if (cv != null) {
                                cv.open();
                            }
                            isSure = false;
                        }
                    });
                    dialogSure.show();
                } else {
                    final DialogSureCancel dialogSureCancel = new DialogSureCancel(ActivityStack.getInstance().top());
                    dialogSureCancel.setTitle("PIN Verify Status");
                    dialogSureCancel.setContent(String.format("Remain %d Times,Continue?", PinTryCounter));
                    dialogSureCancel.show();
                    dialogSureCancel.setSureListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogSureCancel.dismiss();
                            if (cv != null){
                                cv.open();
                            }
                            isSure = true;
                        }
                    });
                    dialogSureCancel.setCancelListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogSureCancel.dismiss();
                            if (cv != null) {
                                cv.open();
                            }
                            isSure = false;
                        }
                    });
                }
            }
        });

        if (cv != null) {
            cv.block();
        }
        return isSure;
    }
}
