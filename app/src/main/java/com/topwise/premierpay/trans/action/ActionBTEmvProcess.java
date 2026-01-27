package com.topwise.premierpay.trans.action;

import android.content.Context;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.topwise.cloudpos.aidl.emv.level2.EmvTerminalInfo;
import com.topwise.cloudpos.aidl.pinpad.AidlPinpad;
import com.topwise.cloudpos.aidl.pinpad.GetPinListener;
import com.topwise.cloudpos.aidl.pinpad.PinParam;
import com.topwise.cloudpos.data.PinpadConstant;
import com.topwise.manager.AppLog;
import com.topwise.manager.TopUsdkManage;
import com.topwise.manager.emv.api.IEmv;
import com.topwise.manager.emv.entity.EinputType;
import com.topwise.manager.emv.entity.EmvOnlineResp;
import com.topwise.manager.emv.entity.EmvOutCome;
import com.topwise.manager.emv.entity.EmvTransPraram;
import com.topwise.manager.emv.enums.EOnlineResult;
import com.topwise.manager.emv.enums.ETransStatus;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.emv.EmvResultUtlis;
import com.topwise.premierpay.emv.EmvTags;
import com.topwise.premierpay.emv.EmvTransProcessImpl;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.transmit.TransProcessListener;
import com.topwise.premierpay.transmit.TransProcessListenerImpl;
import com.topwise.premierpay.utils.TrackUtils;

import java.util.Calendar;

/**
 * 创建日期：2021/4/27 on 13:56
 * 描述:
 * 作者:wangweicheng
 */
public class ActionBTEmvProcess extends AAction {
    private static final String TAG = ActionBTEmvProcess.class.getSimpleName();
    /**
     * 子类构造方法必须调用super设置ActionStartListener
     *
     * @param listener {@link ActionStartListener}
     */
    public ActionBTEmvProcess(ActionStartListener listener) {
        super(listener);
    }
    private Context context;
    private TransData transData;
    private Handler handler;
    private boolean isContact;
    private IEmv emv;

    public void setParam(Context context,Handler handler, TransData transData) {
        this.context = context;
        this.handler = handler;
        this.transData = transData;
        this.isContact = false;
    }

    private EinputType einputType;
    private EmvTransPraram emvTransPraram;
    private ConditionVariable cv;
    private  boolean bPinok =true;
    @Override
    protected void process() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TransProcessListener transProcessListener = new TransProcessListenerImpl();

                transProcessListener.onShowProgress("On EMV Processing...",180);
                ETransType eTransType = ETransType.valueOf(transData.getTransType());
                EmvTerminalInfo emvTerminalInfo = EmvResultUtlis.setEmvTerminalInfo();
                if (transData.getEnterMode() ==  Component.EnterMode.INSERT){
                    einputType = EinputType.CT;
                    emvTerminalInfo.setUcTerminalEntryMode((byte) 0x05);
                }else {
                    einputType =EinputType.CTL;
                    emvTerminalInfo.setUcTerminalEntryMode((byte) 0x07);
                }

                emv = TopApplication.usdkManage.getEmvHelper();
                emv.init(einputType);
                EmvTransProcessImpl  emvTransProcess =  new EmvTransProcessImpl(handler,transData,emv);
                emv.setProcessListener(emvTransProcess);

                emvTransPraram = new EmvTransPraram(EmvTags.checkKernelTransType(transData));

                if(transData.isStressTest()){
                    emvTransPraram.setbSup2GAC(true);
                }

                Calendar calender = Calendar.getInstance();
                String year = String.format("%04d", calender.get(Calendar.YEAR));
                emvTransPraram.setAucTransDate(year.substring(2) + transData.getDate());
                emvTransPraram.setAucTransTime(transData.getTime());
                emvTransPraram.setTransNo(transData.getTransNo());
                String amount = transData.getAmount();
                if (TextUtils.isEmpty(amount)){
                    amount = "0";
                }
                String amountOther = transData.getCashAmount();
                if (TextUtils.isEmpty(amountOther)){
                    amountOther = "0";
                }
                emvTransPraram.setAmount(Long.parseLong(amount));
                emvTransPraram.setAmountOther(Long.parseLong(amountOther));
                emvTransPraram.setAucUnNumber(transData.getUnNum());
                String transCurCode = TopApplication.sysParam.get(SysParam.APP_PARAM_TRANS_CURRENCY_CODE);
                emvTransPraram.setAucTransCurCode(transCurCode);

                if (eTransType == ETransType.TRANS_VOID
                        ||eTransType == ETransType.TRANS_REFUND){
                    emvTransPraram.setbSupSimpleProc(true);
                }

//                if (transData.getEnterMode() ==  Component.EnterMode.INSERT){
//                    emvTransPraram.setbSup2GAC(false);
//                }else {
//                    //emvTransPraram.setClssForceOnlinePin(true);
//                }

                emv.setTerminalInfo(emvTerminalInfo);
                emv.setTransPraram(emvTransPraram);
                emv.setKernelConfig(EmvResultUtlis.setEmvKernelConfig());

                EmvOutCome emvOutCome = emv.StartEmvProcess();
//                final EmvResult emvResult = emv.emvProcess(emvTransPraram);
                AppLog.d(TAG,"EmvOutCome: " +emvOutCome.toString());
                if (!(ETransStatus.OFFLINE_APPROVE == emvOutCome.geteTransStatus()|| ETransStatus.ONLINE_REQUEST == emvOutCome.geteTransStatus()) ){
                    transProcessListener.onHideProgress();
                    setResult(new ActionResult(TransResult.ERR_ABORTED, transData));
                    return;
                }
                boolean isInputOnlinePIn = false;
                ETransType transType = ETransType.valueOf(transData.getTransType());
                saveCardInfoAndCardSeq();
                saveTvrTsi();
                byte[] f55 = EmvTags.getF55(transType, emv,false, false);
                byte[] f55Dup = EmvTags.getF55(transType, emv, true, false);
                if (f55 != null && f55.length > 0){
                    transData.setSendIccData(TopApplication.convert.bcdToStr(f55));
                }
                if (f55Dup != null && f55Dup.length > 0) {
                    transData.setDupIccData(TopApplication.convert.bcdToStr(f55Dup));
                }

                if (einputType ==  EinputType.CT){
                    byte[] value =  emv.getTlv(0x9f34);
                    if( value!=null && (value[0] & 0x3F) == 0x02){
                        isInputOnlinePIn = true;
                    }
                }else {
                    byte[] value =  emv.getTlv(0xDF8129);
                    if( (value !=null) && (value[3] & 0xF0) == 0x02 ) {
                        isInputOnlinePIn = true;
                    }
                }
                if(isInputOnlinePIn) {
                    cv = new ConditionVariable();
                    AidlPinpad mPinpad = TopApplication.usdkManage.getPinpad(0);
                    PinParam pinParam =  new PinParam(Device.INDEX_TPK,0,transData.getPan(),
                            PinpadConstant.KeyType.KEYTYPE_PEK,"0,4,5,6,7,8,9,10,11,12");
                    transProcessListener.onUpdateMsg("On Input Password...");

                    pinParam.setTimeOut(60000);
                    try {
                        mPinpad.getPin(pinParam.getParam(), new GetPinListener() {
                            @Override
                            public void onInputKey(int i, String s) throws RemoteException {

                            }
                            @Override
                            public void onError(int i) throws RemoteException {
                                AppLog.d(TAG,"onError: "  +i);
                                bPinok =false;
                                cv.open();
                            }

                            @Override
                            public void onConfirmInput(byte[] bytes) throws RemoteException {
                                if(bytes == null || bytes.length ==0){
                                    AppLog.d(TAG, "onConfirmInput bypass");
                                }else {
                                    AppLog.d(TAG, "onConfirmInput: " + TopApplication.convert.bcdToStr(bytes));
                                    transData.setHasPin(true);
                                    transData.setPin(TopApplication.convert.bcdToStr(bytes));
                                }
                                cv.open();
                            }

                            @Override
                            public void onCancelKeyPress() throws RemoteException {

                            }

                            @Override
                            public void onStopGetPin() throws RemoteException {

                            }

                            @Override
                            public void onTimeout() throws RemoteException {

                            }

                            @Override
                            public IBinder asBinder() {
                                return null;
                            }
                        });
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    AppLog.d(TAG, "cv block");
                    cv.block();
                }

                if(!bPinok){
                    transProcessListener.onHideProgress();
                    setResult(new ActionResult(TransResult.ERR_ABORTED, transData));
                    return;
                }

                if (ETransStatus.OFFLINE_APPROVE == emvOutCome.geteTransStatus() ){
                    AppLog.d(TAG,"offline approve force to online : " +emvOutCome.toString());
                }

                transProcessListener.onUpdateMsg("Online...");
                EmvOnlineResp emvOnlineResp = emvTransProcess.onReqOnlineProc();
                emvOutCome  = emv.CompleteEmvProcess(emvOnlineResp.geteOnlineResult()==EOnlineResult.ONLINE_APPROVE,new String(emvOnlineResp.getAuthRespCode()),"");
                AppLog.d(TAG,"online EmvOutCome: " +emvOutCome.toString());
                transProcessListener.onHideProgress();
                if(emvOutCome.geteTransStatus()==ETransStatus.ONLINE_APPROVE){
                    setResult(new ActionResult(TransResult.SUCC, transData));
                }else{
                    setResult(new ActionResult(TransResult.ERR_ABORTED, transData));
                }

            }
        }).start();
    }


    public void closeReader(){
        try {
            //Equipment under electric
            boolean iccClose = TopUsdkManage.getInstance().getIcc().close();
            boolean rfClose = TopUsdkManage.getInstance().getRf().close();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private void saveCardInfoAndCardSeq() {
        byte[] track2 = emv.getTlv(0x57);
        String strTrack2 = TopApplication.convert.bcdToStr(track2);
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
            String temp = TopApplication.convert.bcdToStr(expDate);
            transData.setExpDate(temp.substring(0, 4));
            AppLog.d(TAG," saveCardInfoAndCardSeq 有效期 expDate =" + temp);
        }
        // 获取卡片序列号
        byte[] cardSeq = emv.getTlv(0x5f34);
        if (cardSeq != null && cardSeq.length > 0) {
            String temp = TopApplication.convert.bcdToStr(cardSeq);
            transData.setCardSerialNo(temp.substring(0, 2));
           AppLog.d(TAG," saveCardInfoAndCardSeq 卡片序列号 cardSeq =" + temp);
        }

    }


    /**
     AID	    M	Tag 84
     App Name	M	Tag 9F12
     TC     	M	Tag 9F26 (2GAC)
     TVR	    M	Tag 95 (2GAC)
     TSI	    M	Tag 9B (2GAC)
     Card Holder Name	M	Tag 5F20
     */
    private void saveTvrTsi(){
        IEmv emv = TopApplication.usdkManage.getEmvHelper();
        String temp = "";
        try {
            byte[] valueTVR = emv.getTlv(0x95);
            if (valueTVR != null && valueTVR.length > 0){
                temp = TopApplication.convert.bcdToStr(valueTVR);
                if (!TextUtils.isEmpty(temp)) transData.setTvr(temp);
                AppLog.emvd("EmvResultUtlis setTVR(): " + temp);
            }
            byte[] valueATC = emv.getTlv(0x9F36);
            if (valueATC != null && valueATC.length > 0){
                temp = TopApplication.convert.bcdToStr(valueATC);
                if (!TextUtils.isEmpty(temp)) transData.setAtc(temp);
                AppLog.emvd("EmvResultUtlis setATC(): " + temp);
            }
            //0x9B
            byte[] valueTis  = emv.getTlv(0x9B);
            if (valueTis != null && valueTis.length > 0){
                temp = TopApplication.convert.bcdToStr(valueTis);
                if (!TextUtils.isEmpty(temp)) transData.setTsi(temp);
                AppLog.emvd("EmvResultUtlis setTsi(): " + temp);
            }
            //0x9F26
            byte[] valueTc  = emv.getTlv(0x9F26);
            if (valueTc != null && valueTc.length > 0){
                temp = TopApplication.convert.bcdToStr(valueTc);
                if (!TextUtils.isEmpty(temp)) transData.setTc(temp);
                AppLog.emvd("EmvResultUtlis setTc(): " + temp);
            }
            //0x9F12
            byte[] valueAppname  = emv.getTlv(0x9F12);
            if (valueAppname != null && valueAppname.length > 0){
                temp = new String(valueAppname);
                if (!TextUtils.isEmpty(temp)) transData.setEmvAppName(temp);
                AppLog.emvd("EmvResultUtlis setEmvAppName(): " + temp);
            }
            //0x84
            byte[] valueAid  = emv.getTlv(0x84);
            if (valueAid != null && valueAid.length > 0){
                temp = TopApplication.convert.bcdToStr(valueAid);
                if (!TextUtils.isEmpty(temp)) transData.setAid(temp);
               AppLog.emvd("EmvResultUtlis setAid(): " + temp);
            }
            byte[] valuehname  = emv.getTlv(0x5F20);
            if (valuehname != null && valuehname.length > 0){
//                AppLog.emvd("EmvResultUtlis Card Holder Name(): " + TopApplication.convert.bcdToStr(valuehname));
                temp = new String(valuehname);
                if (!TextUtils.isEmpty(temp)) transData.setCardHolderName(temp);
                AppLog.emvd("EmvResultUtlis Card Holder Name(): " + temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
