package com.topwise.premierpay.trans.action.activity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.topwise.cloudpos.aidl.emv.level2.AidlEmvL2;
import com.topwise.kdialog.DialogSureCancel;
import com.topwise.kdialog.IkeyListener;
import com.topwise.manager.AppLog;
import com.topwise.manager.TopUsdkManage;
import com.topwise.manager.card.api.ICardReader;
import com.topwise.manager.card.entity.CardData;
import com.topwise.manager.card.impl.CardReader;
import com.topwise.manager.emv.entity.EmvErrorCode;
import com.topwise.manager.emv.impl.TransProcess;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.param.AppCombinationHelper;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.action.ActionSearchCard;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.trans.model.EUIParamKeys;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.utils.SmallScreenUtil;
import com.topwise.premierpay.utils.Utils;

/**
 * 创建日期：2021/6/9 on 16:46
 * 描述:
 * 作者:wangweicheng
 */
public class SearchCardManagerNewActivity extends BaseActivityWithTickForAction {
    private static final String TAG = TopApplication.APPNANE + SearchCardManagerNewActivity.class.getSimpleName();
    private int SEARCH_CARD_TIME = 60000;
    private TextView mTextAmount;
    private TextView mTextCashAmount;
    private TextView tVtitle;
    private TextView tVtime;
    private String mAmount;
    private AidlEmvL2 emv;
   // private RelativeLayout rlTap;
   // private RelativeLayout rlInsert;
  //  private RelativeLayout rlSwipe;
    private RelativeLayout rlInputPan;
    private EditText editTextPan;
    private boolean issecond;

    private String navTitle;
    private String amount;
    private String amount_cash;
    private byte mode;
    private boolean canBack = true;
    private ImageView ivImgAction;
    private ICardReader cardReader = TopUsdkManage.getInstance().getCardReader();

    private AnimationDrawable anim;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_card_new;
    }

    @Override
    protected void initViews() {
        tVtitle = (TextView)findViewById(R.id.header_title);
        tVtitle.setText(navTitle);

        mTextAmount = (TextView) findViewById(R.id.trad_amount);
        mTextCashAmount = (TextView) findViewById(R.id.cash_trad_amount);

       // rlTap =  (RelativeLayout) findViewById(R.id.layout_rf);
        rlInputPan =  (RelativeLayout) findViewById(R.id.rl_input_pan);
       // rlInsert =  (RelativeLayout) findViewById(R.id.layout_ic);
       // rlSwipe = (RelativeLayout) findViewById(R.id.layout_msr);

        ivImgAction = (ImageView) findViewById(R.id.img_actions);
        AnimationDrawable animationDrawable = (AnimationDrawable) ivImgAction.getBackground();
        this.anim = animationDrawable;
        animationDrawable.start();

        if (isKeyin()) {
            rlInputPan.setVisibility(View.VISIBLE);
            editTextPan = (EditText) findViewById(R.id.ed_indata);

            editTextPan.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    AppLog.i(TAG,"onKey " + keyCode);
                    if (keyCode == KeyEvent.KEYCODE_ENTER &&
                            event.getAction() == KeyEvent.ACTION_UP) {
                        String sPan = editTextPan.getText().toString();
                        if (!TextUtils.isEmpty(sPan) && sPan.length() > 12) {
                            ActionSearchCard.CardInformation cardInformation = new ActionSearchCard.CardInformation();
                            cardInformation.setSearchMode(ActionSearchCard.SearchMode.KEYIN);
                            cardInformation.setPan(sPan);

                            finish(new ActionResult(TransResult.SUCC, cardInformation));
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
        String curr = TopApplication.sysParam.get(SysParam.APP_PARAM_TRANS_CURRENCY_SYMBOL);
        if (!TextUtils.isEmpty(amount)) {
            ((RelativeLayout) findViewById(R.id.rl_amount)).setVisibility(View.VISIBLE);
            mTextAmount.setText(curr + Utils.ftoYuan(amount));
            SmallScreenUtil.getInstance().showSearchCard(curr + Utils.ftoYuan(amount));
        } else {
            ((RelativeLayout) findViewById(R.id.rl_amount)).setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(amount_cash)) {
            ((RelativeLayout) findViewById(R.id.rl_cash_amount)).setVisibility(View.VISIBLE);
            mTextCashAmount.setText(curr+Utils.ftoYuan(amount_cash));
            SmallScreenUtil.getInstance().showSearchCard(getString(R.string.trans_amount_cash)+curr+Utils.ftoYuan(amount_cash));
        } else {
            ((RelativeLayout) findViewById(R.id.rl_cash_amount)).setVisibility(View.GONE);
        }

        tVtime = (TextView)findViewById(R.id.header_time);

        emv = TopApplication.usdkManage.getEmv();

       /* if (isSwipe()) {
            rlSwipe.setVisibility(View.VISIBLE);
        } else {
            rlSwipe.setVisibility(View.GONE);
        }
        if (isInsert()) {
            rlInsert.setVisibility(View.VISIBLE);
        } else {
            rlInsert.setVisibility(View.GONE);
        }
        if (isTap()) {
            rlTap.setVisibility(View.VISIBLE);
        } else {
            rlTap.setVisibility(View.GONE);
        }*/
    }

    @Override
    protected void handleMsg(Message msg) {
        switch (msg.what){
            case TIP_TIME:
                String time = (String)msg.obj;
                if (!TextUtils.isEmpty(time))
                    tVtime.setText(time);
                break;
        }
    }

    @Override
    protected void setListeners() {
        if (cardReader != null) {
            Device.openBlueLed();
        }
        TransProcess.getInstance().preInit(AppCombinationHelper.getInstance().getAppCombinationList());
        cardReader.startFindCard(isSwipe(), isInsert(), isTap(), SEARCH_CARD_TIME / 1000,
                new CardReader.onReadCardListener() {
                    @Override
                    public void getReadState(CardData cardData) {
                        AppLog.d(TAG,"cardData" +cardData.toString());

                        if (cardData!= null && CardData.EReturnType.OK == cardData.geteReturnType()) {
                            switch (cardData.geteCardType()) {
                                case IC:
                                    Device.openYellowLed();
                                    Device.beepNormal();
                                    finish(new ActionResult(TransResult.SUCC, new ActionSearchCard.CardInformation(ActionSearchCard.SearchMode.INSERT)));
                                    break;
                                case RF:
                                    Log.d("jeremy"," ===========  find RF card 111 =============" );
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Device.beepNormal();
                                            Device.openYellowLed();
                                        }
                                    }).start();
                                    //TopApplication.curTime =  System.currentTimeMillis();
                                    AppLog.d("jeremy"," ===========  find RF card =============" );
                                    finish(new ActionResult(TransResult.SUCC, new ActionSearchCard.CardInformation(ActionSearchCard.SearchMode.TAP)));
                                    break;
                                case MAG:
                                    Device.openYellowLed();
                                    Device.beepNormal();
                                    if (cardData.getTrack2() == null || cardData.getTrack2().length() < 12) {
                                        finish(new ActionResult(TransResult.ERR_CHECK_CARD,null));
                                    } else {
                                        finish(new ActionResult(TransResult.SUCC, new ActionSearchCard.CardInformation(ActionSearchCard.SearchMode.SWIPE, cardData)));
                                    }
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            // finish(new ActionResult(TransResult.ERR_CHECK_CARD, null));
                            if (cardData.geteCardType() == CardData.ECardType.IC) {
                                finish(new ActionResult(EmvErrorCode.EMV_FALLBACK, null));
                            } else {
                                finish(new ActionResult(TransResult.ERR_CHECK_CARD, null));
                            }
                        }
                    }

                    @Override
                    public void onNotification(CardData.EReturnType eReturnType) {
                        if (eReturnType == CardData.EReturnType.TIMEOUT) {
                            finish(new ActionResult(TransResult.ERR_ABORTED, null));
                        } else if (eReturnType == CardData.EReturnType.RF_MULTI_CARD) {
                            finish(new ActionResult(TransResult.ERR_RF_MULTI_CARD, null));
                        }
                    }
                });
    }

    @Override
    protected void loadParam() {
        Bundle bundle = getIntent().getExtras();
        navTitle = getIntent().getStringExtra(EUIParamKeys.NAV_TITLE.toString());
        canBack = bundle.getBoolean(EUIParamKeys.NAV_BACK.toString(),true);
        amount = bundle.getString(EUIParamKeys.TRANS_AMOUNT.toString());
        amount_cash = bundle.getString(EUIParamKeys.TRANS_AMOUNT_CASH.toString());
        issecond = bundle.getBoolean(EUIParamKeys.TIKE_TIME.toString(),false);
        mode = bundle.getByte(EUIParamKeys.CARD_SEARCH_MODE.toString(), ActionSearchCard.SearchMode.SWIPE);
        AppLog.d(TAG,"CARD_SEARCH_MODE isSwipe " +isSwipe() + " isInsert " + isInsert()+ " isTap " + isTap());
        if (issecond) {
            SEARCH_CARD_TIME = 29000;
            tickTimerStart(30);
        }
    }

    /**
     * Manual input PAN
    * */
    private boolean isKeyin() {
        AppLog.d(TAG,"isKeyin TopApplication.parameterBean.getManualKeyEnable(): " + TopApplication.parameterBean.getManualKeyEnable());
        return ((mode & ActionSearchCard.SearchMode.KEYIN) == ActionSearchCard.SearchMode.KEYIN) && TopApplication.parameterBean.getManualKeyEnable();
    }

    private boolean isSwipe() {
        AppLog.d(TAG,"isKeyin TopApplication.parameterBean.getMagStripeEnable(): " + TopApplication.parameterBean.getMagStripeEnable());
        return ((mode & ActionSearchCard.SearchMode.SWIPE) == ActionSearchCard.SearchMode.SWIPE) && TopApplication.parameterBean.getMagStripeEnable();
    }

    private boolean isInsert() {
        return ((mode & ActionSearchCard.SearchMode.INSERT) == ActionSearchCard.SearchMode.INSERT);
    }

    private boolean isTap() {
        return ((mode & ActionSearchCard.SearchMode.TAP) == ActionSearchCard.SearchMode.TAP);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        AppLog.i(TAG,"onKeyDown " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (canBack) {
                if (cardReader != null) {
                    cardReader.cancel();
                }
                ActionResult result = new ActionResult(TransResult.ERR_ABORTED, null);
                finish(result);
            } else {
                DialogSureCancel dialogSureCancel = new DialogSureCancel(this);
                dialogSureCancel.setMyListener(new IkeyListener() {
                    @Override
                    public void onConfirm(String text) {
                        if (cardReader != null) {
                            cardReader.cancel();
                        }
                        ActionResult result = new ActionResult(TransResult.ERR_CANCEL, null);
                        finish(result);
                    }

                    @Override
                    public void onCancel(int ret) {

                    }
                });
                dialogSureCancel.show();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onPause() {
        super.onPause();
        this.anim.stop();
        SmallScreenUtil.getInstance().showMessage("Emv process");
    }
}
