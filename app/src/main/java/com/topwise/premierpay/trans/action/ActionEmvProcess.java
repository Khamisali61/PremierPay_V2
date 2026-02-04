package com.topwise.premierpay.trans.action;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.topwise.cloudpos.aidl.emv.level2.EmvTerminalInfo;
import com.topwise.manager.AppLog;
import com.topwise.manager.emv.api.IEmv;
import com.topwise.manager.emv.entity.EinputType;
import com.topwise.manager.emv.entity.EmvErrorCode;
import com.topwise.manager.emv.entity.EmvTransPraram;
import com.topwise.manager.emv.entity.EmvOutCome;
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
import com.topwise.premierpay.utils.ThreadPoolUtils;

import java.util.Calendar;

/**
 * 创建日期：2021/4/27 on 13:56
 * 描述:
 * 作者:wangweicheng
 */
public class ActionEmvProcess extends AAction {
    private static final String TAG = "Jeremy";
    /**
     * 子类构造方法必须调用super设置ActionStartListener
     *
     * @param listener {@link ActionStartListener}
     */
    public ActionEmvProcess(ActionStartListener listener) {
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

    @Override
    protected void process() {
        ThreadPoolUtils.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                {
                    AppLog.d("jeremy","  process init begin " );
                    ETransType eTransType = ETransType.valueOf(transData.getTransType());
                    EmvTerminalInfo emvTerminalInfo = EmvResultUtlis.setEmvTerminalInfo();
                    if (transData.getEnterMode() ==  Component.EnterMode.INSERT) {
                        einputType = EinputType.CT;
                        emvTerminalInfo.setUcTerminalEntryMode((byte) 0x05);
                    } else {
                        einputType =EinputType.CTL;
                        emvTerminalInfo.setUcTerminalEntryMode((byte) 0x07);
                    }
                AppLog.d("jeremy"," getAucTerminalCountryCode --"+  TopApplication.convert.bcdToStr(emvTerminalInfo.getAucTerminalCountryCode()));

                emv = TopApplication.usdkManage.getEmvHelper();
                emv.init(einputType);
                emv.setProcessListener(new EmvTransProcessImpl(handler, transData, emv));

                emvTransPraram = new EmvTransPraram(EmvTags.checkKernelTransType(transData));

                if (transData.isStressTest()) {
                    emvTransPraram.setbSup2GAC(false);
                }

                Calendar calender = Calendar.getInstance();
                String year = String.format("%04d", calender.get(Calendar.YEAR));
                emvTransPraram.setAucTransDate(year.substring(2) + transData.getDate());
                emvTransPraram.setAucTransTime(transData.getTime());
                emvTransPraram.setTransNo(transData.getTransNo());
                AppLog.d("jeremy"," getMcc --"+ transData.getMcc());
                emvTransPraram.setMcc(transData.getMcc());
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
//                     TODO: 2025/6/4 modified from true to false
//                     TODO: 2025/9/30 modified from true to false
                    emvTransPraram.setbSupSimpleProc(true);
                }

//                if (transData.getEnterMode() ==  Component.EnterMode.INSERT) {
//                    emvTransPraram.setbSup2GAC(false);
//                } else {
//                    //emvTransPraram.setClssForceOnlinePin(true);
//                }
                    emv.setTerminalInfo(emvTerminalInfo);
                    emv.setTransPraram(emvTransPraram);
                    emv.setKernelConfig(EmvResultUtlis.setEmvKernelConfig());
                    AppLog.d("jeremy","  process init end " );
                    EmvOutCome emvOutCome = emv.StartEmvProcess();
  //                final EmvResult emvResult = emv.emvProcess(emvTransPraram);
                    AppLog.d(TAG,"EmvOutCome: " + emvOutCome.toString());
                    emv.EndEmvProcess();

                    if (ETransStatus.ONLINE_APPROVE == emvOutCome.geteTransStatus() ||
                            ETransStatus.OFFLINE_APPROVE == emvOutCome.geteTransStatus()) {
                        if(ETransStatus.OFFLINE_APPROVE==emvOutCome.geteTransStatus()){
                            transData.setResponseCode("00");
                        }
                        setResult(new ActionResult(TransResult.SUCC, transData));
                    } else if (ETransStatus.ONLINE_REQUEST == emvOutCome.geteTransStatus()) { //eg rufund
                        setResult(new ActionResult(TransResult.SUCC, transData));
                    } else if (EmvErrorCode.EMV_FALLBACK == emvOutCome.getnErrorCodeL2()) {
                        setResult(new ActionResult(EmvErrorCode.EMV_FALLBACK, transData));
                    } else if (EmvErrorCode.EMV_TRY_OTHER_INTERFACE == emvOutCome.getnErrorCodeL2()) {
                        setResult(new ActionResult(EmvErrorCode.CLSS_CARD_NOT_SUPPORT, transData));
                    } else if (EmvErrorCode.CLSS_REFER_CONSUMER_DEVICE == emvOutCome.getnErrorCodeL2()) {
                        setResult(new ActionResult(EmvErrorCode.EMV_SEE_PHONE, transData));
                    }else if (ETransStatus.OFFLINE_DECLINED==emvOutCome.geteTransStatus()){
                        setResult(new ActionResult(EmvErrorCode.CLSS_CARD_NOT_SUPPORT, transData));
                    }else if (ETransStatus.TRY_ANOTHER_INTERFACE==emvOutCome.geteTransStatus()){
                        setResult(new ActionResult(EmvErrorCode.CLSS_USE_CONTACT, transData));
                    } else if (ETransStatus.NEED_PWD == emvOutCome.geteTransStatus()) {
                        setResult(new ActionResult(EmvErrorCode.CLSS_NEED_PWD, transData));
                    } else if (ETransStatus.NEED_INSERT == emvOutCome.geteTransStatus()) {
                        setResult(new ActionResult(EmvErrorCode.CLSS_NEED_CONTACT, transData));
                    } else if(EmvErrorCode.EMV_DECLINED== emvOutCome.getnErrorCodeL2()){
                        setResult(new ActionResult(EmvErrorCode.EMV_DECLINED, transData));
                    } else {
                        Device.openRedLed();
                        Device.beepFail();
                        if ("N".equals(TopApplication.sysParam.get(SysParam.AUTO_IN_MDB))) {
                            TransProcessListener transProcessListener = new TransProcessListenerImpl();
                            transProcessListener.onUpdateProgressTitle(eTransType.getTransName().toUpperCase());
                            // Suppress technical popup
                            // transProcessListener.onShowFailWithConfirm(emvOutCome.toString(), 8);
                        }
                        setResult(new ActionResult(TransResult.ERR_ABORTED, transData));
                    }
                }
            }
        });
    }

}
