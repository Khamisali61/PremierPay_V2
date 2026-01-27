package com.topwise.premierpay.trans;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;

import com.topwise.kdialog.DialogSure;
import com.topwise.manager.AppLog;
import com.topwise.manager.TopUsdkManage;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.mdb.mode.MDBCallTrans;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.thirdcall.ThirdCallTrans;
import com.topwise.premierpay.trans.action.ActionSearchCard.CardInformation;
import com.topwise.premierpay.trans.action.ActionSearchCard.SearchMode;
import com.topwise.premierpay.trans.action.ActionTransPreDeal;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ATransaction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.core.TransContext;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransResult;

import android.content.DialogInterface.OnDismissListener;
import android.text.TextUtils;

public abstract class BaseTrans extends ATransaction {
    private static final String TAG = TopApplication.APPNANE + BaseTrans.class.getSimpleName();

    protected Context context;
    protected Handler handler;
    // 当前交易类型
    protected ETransType transType;
    protected TransData transData;
    protected ThirdCallTrans thirdCallTrans;
    protected MDBCallTrans mdbCallTrans;
    protected byte mode = -1;
    /**
     * 交易监听器
     */
    protected TransEndListener transListener;

    public BaseTrans(Context context, Handler handler, ETransType transType,
                     TransEndListener transListener) {
        super();
        this.context = context;
        this.handler = handler;
        this.transType = transType;
        this.transListener = transListener;
    }

    public BaseTrans(Context context, Handler handler,
                     TransEndListener transListener) {
        super();
        this.context = context;
        this.handler = handler;
        this.transListener = transListener;
    }

    public TransData getTransData() {
        return transData;
    }

    public BaseTrans() {

    }

    @Override
    public void preExecute() {
        if (thirdCallTrans != null) {
            transData.setAmount(thirdCallTrans.getAmt());
            if (!TextUtils.isEmpty(thirdCallTrans.getOrderNo())) {
                transData.setOrigTransNo(Long.valueOf(thirdCallTrans.getOrderNo()));
            }
            transData.setNeedPrint(thirdCallTrans.getIsNeedPrintReceipt());
        }
        if (mdbCallTrans!=null){
            transData.setAmount(mdbCallTrans.getAmt());
            if (!TextUtils.isEmpty(mdbCallTrans.getOrderNo())) {
                transData.setOrigTransNo(Long.valueOf(mdbCallTrans.getOrderNo()));
            }
        }
    }

    /**
     * 设置交易类型
     *
     * @param transType
     */
    public void setTransType(ETransType transType) {
        this.transType = transType;
    }

    protected void setTransListener(TransEndListener transListener) {
        this.transListener = transListener;
    }

    /**
     * 交易结果提示
     */
    protected void transEnd(final ActionResult result) {
        AppLog.d(TAG, " TRANS--END-- " + result.getRet());

        // 交易结束，停止交易处理
        // TopApplication.amapLbsUtils.stopLocation();
        dispResult(transType.getTransName(), result, new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                new Thread(new Runnable() {
                   @Override
                    public void run() {
                        //check card
                        if (!transData.isStressTest()) {
                           checkRemoveCard();
                        }
                        closeReader();
                        TopApplication.isRuning = false;
                        TransContext.getInstance().setCurrentAction(null);
                        TransContext.getInstance().setCurrentContext(null);
                        TransContext.close();
                        Device.closeAllLed();
                        AppLog.d(TAG, " TRANS--END--transListener " + transListener);
                        if (transData.isStressTest()) {
                                transListener.onEnd(result);
                            setTransRunning(false);
                        } else {
                            // 防止第三方自动签到后无法做交易,将标志位及时设置为false
                            setTransRunning(false);
                            if ("Y".equals(TopApplication.sysParam.get(SysParam.AUTO_IN_MDB))) {
                                ActivityStack.getInstance().popMDB();
                                //TransContext.getInstance().setCurrentAction(null);
                            } else {
                                //TransContext.getInstance().setCurrentAction(null);
                                ActivityStack.getInstance().popAllButBottom();
                            }
                            if (transListener != null) {
                                transListener.onEnd(result);
                            }
                        }
                        transListener = null;
                   }
              }).start();
            }
        });
    }

    /**
     * 提示移卡
     */
    protected void checkRemoveCard()  {
        String localClassName = ((Activity) getCurrentContext()).getLocalClassName();
        AppLog.d(TAG, transType.toString() + " TRANS--checkRemoveCard-- " + localClassName);
        while (true) {
            if (TopApplication.sysParam.getInt(SysParam.DEVICE_MODE) != 0) {
               break;
            }

            AppLog.d(TAG, transType.toString() + " checkRemoveCard ");
            try {
                boolean iccExist = TopUsdkManage.getInstance().getIcc().isExist();
                boolean rfExist  = TopUsdkManage.getInstance().getRf().isExist();
                if (!iccExist && !rfExist) {
                    break;
                } else {
                    Device.beepNormal();
                    showWarning("Please Remove Card!");
                }
                SystemClock.sleep(300);
            } catch (RemoteException e) {
                e.printStackTrace();
                break;
            }
       }

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (dialogSure != null){
                    dialogSure.dismiss();
                    dialogSure = null;
                }
            }
        });
        SystemClock.sleep(100);
    }

    public void closeReader() {
        try {
            if (TopApplication.sysParam.getInt(SysParam.DEVICE_MODE) != 1 && !(Build.MANUFACTURER.equalsIgnoreCase("topwise") || Build.MANUFACTURER.equalsIgnoreCase("Gertec"))){
                return;
            }
            //Equipment under electric
            TopUsdkManage.getInstance().getCardReader().cancel();
            boolean iccClose = TopUsdkManage.getInstance().getIcc().close();
            boolean rfClose = TopUsdkManage.getInstance().getRf().close();
            AppLog.d(TAG,"close icc= " +iccClose+"and rf= " +rfClose);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重写父类的execute， 增加交易是否已运行检查和交易预处理
     */
    @Override
    public synchronized void execute() {
        AppLog.d(TAG,  " TRANS--START-- " + isTransRunning);

        if (isTransRunning()) {
            return;
        }
        setTransRunning(true);
        Device.closeAllLed();

        // 初始化transData
        transData = Component.transInit();

        // 第三方调用时要保存的订单号
        transData.setOrderNo(TopApplication.mOrderNo);
        TopApplication.mOrderNo = null;

        // 设置当前context
        TransContext.getInstance().setCurrentContext(context);
        // 在交易预处理的时候开启定位获取
        // TopApplication.amapLbsUtils.startLocation();

        ActionTransPreDeal preDealAction = new ActionTransPreDeal(
                new AAction.ActionStartListener() {

                    @Override
                    public void onStart(AAction action) {
                        ((ActionTransPreDeal) action).setParam(
                                getCurrentContext(), transType);
                    }
                });
        preDealAction.setEndListener(new AAction.ActionEndListener() {

            @Override
            public void onEnd(AAction action, ActionResult result) {
                if (result.getRet() != TransResult.SUCC) {
                    transEnd(result);
                    return;
                }
                transData.setTransType(transType.toString());
                // 执行交易的时候才 ,管理类交易 不想需要初始化 ksn
//                if (transType != ETransType.LOGON &&
//                        transType != ETransType.TRANS_HANDSHAKE &&
//                        transType != ETransType.LOGOUT &&
//                        transType != ETransType.TRANS_NETWORK &&
//                        transType != ETransType.PARAM_DOWNLOAD){
//                    transData.setPinKsn(Device.autoAddPinKsn());
//                    transData.setDataKsn(Device.autoAddDataKsn());
//                }
                exe();
            }
        });
        preDealAction.execute();
    }

    /**
     * 执行父类的execute方法
     *
     */
    private void exe() {
        super.execute();
    }

    /**
     * 交易是否已执行， 此标准是全局性的， 真的所有交易， 如果某个交易中间需要插入其他交易时， 自己控制此状态。
     */
    private static boolean isTransRunning = false;

    /**
     * 获取交易执行状态
     *
     * @return
     */
    public static boolean isTransRunning() {
        return isTransRunning;
    }

    /**
     * 设置交易执行状态
     *
     * @param isTransRunning
     */
    public static void setTransRunning(boolean isTransRunning) {
        BaseTrans.isTransRunning = isTransRunning;
    }

    /**
     * 外置签名板显示logo
     */
    private void dispLogoOnPad() {

        // 签名板选择
//		String signPadStr = FinancialApplication.sysParam
//				.get(SysParam.SIGNATURE_SELECTOR);
        // 密码键盘选择
//		String pinPadStr = FinancialApplication.sysParam
//				.get(SysParam.EX_PINPAD);
    }

    /**
     * 交易结果提示，及拔卡处理
     *
     * @param transName
     * @param result
     * @param dismissListener
     */
    protected void dispResult(final String transName, final ActionResult result,
                         final OnDismissListener dismissListener) {
        if (result.getRet() != TransResult.SUCC &&
                result.getRet() != TransResult.ERR_ABORTED &&
                result.getRet() != TransResult.CONTINUE) {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    String message = TransResult.getMessage(getCurrentContext(), result.getRet());
                    DialogSure dialogSure = new DialogSure(getCurrentContext());
                    dialogSure.setTitle(transName);
                    dialogSure.setContent(message);
                    dialogSure.tickTimerStart(Component.FAILED_DIALOG_SHOW_TIME);
                    dialogSure.setFailLogo();
                    dialogSure.setOnDismissListener(dismissListener);
                    dialogSure.show();

                    Device.beepFail();
                }
            });
        } else if (result.getRet() == TransResult.SUCC) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String message = TransResult.getMessage(getCurrentContext(), result.getRet());
                    DialogSure dialogSure = new DialogSure(getCurrentContext());
                    dialogSure.setTitle(transName);
                    dialogSure.setContent(message);
                    dialogSure.tickTimerStart(Component.FAILED_DIALOG_SHOW_TIME);
                    dialogSure.setSucessLogo();
                    dialogSure.setOnDismissListener(dismissListener);
                    dialogSure.show();

                    Device.beepSucc();
                }
            });
        } else {
            dismissListener.onDismiss(null);
        }
    }

    private DialogSure dialogSure;

    /**
     * 提示警告信息
     *
     * @param warning
     */
    private void showWarning(final String warning) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (dialogSure == null) {
                    dialogSure = new DialogSure(ActivityStack.getInstance().top()) ;
                    dialogSure.setTitle(transType.getTransName());
                    dialogSure.setContent(warning);
                    dialogSure.setFailLogo();
                    dialogSure.show();
                    AppLog.d(TAG, transType.toString() + " dialogSure show " );
                }
            }
        });
    }

    @Override
    protected void bind(String state, AAction action) {
        super.bind(state, action);
        if (action != null) {
            action.setEndListener(new AAction.ActionEndListener() {

                @Override
                public void onEnd(AAction action, final ActionResult result) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                AppLog.d(TAG, transType.toString()
                                        + " ACTION--" + currentState + "--end");
                                onActionResult(currentState, result);
                            } catch (Exception e) {
                                e.printStackTrace();
                                transEnd(new ActionResult(
                                        TransResult.ERR_ABORTED, null));
                            }
                        }
                    });
                }
            });
        }
    }

    private String currentState;

    @Override
    public void gotoState(String state) {
        this.currentState = state;
        AppLog.i(TAG, transType.toString() + " ACTION--" + currentState
                + "--start");
        super.gotoState(state);
    }

    /**
     * action结果处理
     *
     * @param currentState
     *            ：当前State
     * @param result
     *            ：当前Action执行的结果
     */
    public abstract void onActionResult(String currentState, ActionResult result);

    protected Context getCurrentContext() {
        return TransContext.getInstance().getCurrentContext();
    }

    /**
     * 保存寻卡后的卡信息及输入方式
     * @param cardInfo
     * @param transData
     * @param isPreDialFlg //预拨号
     */
    public void saveCardInfo(CardInformation cardInfo, TransData transData, boolean isPreDialFlg) {
        // 手输卡号处理
        byte mode = cardInfo.getSearchMode();
        if (mode == SearchMode.KEYIN) {
            transData.setPan(cardInfo.getPan());
            transData.setExpDate(cardInfo.getExpDate());
            transData.setEnterMode(Component.EnterMode.MANAUL);
        } else if (mode == SearchMode.SWIPE) {
            transData.setTrack1(cardInfo.getTrack1());
            transData.setTrack2(cardInfo.getTrack2());
            transData.setTrack3(cardInfo.getTrack3());
            transData.setPan(cardInfo.getPan());
            transData.setExpDate(cardInfo.getExpDate());
            transData.setEnterMode(Component.EnterMode.SWIPE);
        } else if (mode == SearchMode.INSERT || mode == SearchMode.TAP) {
            transData.setEnterMode(mode == SearchMode.INSERT ? Component.EnterMode.INSERT : Component.EnterMode.QPBOC);
        }
    }
}
