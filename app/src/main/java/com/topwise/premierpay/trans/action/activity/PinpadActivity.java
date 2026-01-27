package com.topwise.premierpay.trans.action.activity;

import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.topwise.cloudpos.aidl.pinpad.AidlPinpad;
import com.topwise.cloudpos.aidl.pinpad.GetPinListener;
import com.topwise.cloudpos.aidl.pinpad.PinParam;
import com.topwise.cloudpos.data.PinpadConstant;
import com.topwise.manager.AppLog;
import com.topwise.manager.emv.enums.EPinType;
import com.topwise.manager.utlis.DataUtils;
import com.topwise.premierpay.BuildConfig;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.utils.PanUtils;
import com.topwise.premierpay.view.PasswordView;
import com.topwise.premierpay.view.TopToast;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.EUIParamKeys;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.utils.Utils;

public class PinpadActivity extends BaseActivityWithTickForAction {
    private static final String TAG = TopApplication.APPNANE + PinpadActivity.class.getSimpleName();

    private TextView mTestCardNo;
    private TextView mTestAmount;
    private TextView mTextCashAmount;
    private TextView mPin;
    private TextView tVtitle;
    private TextView tVtitleTip;

    private TextView tVtime;

    private TextView tvOfflineWarning;
    private String navTitle;
    private String panBlock;
    private String amount;
    private String cashAmount;
    private int offlineLastTimes;

    private boolean isConfirm = false;
    private boolean isShow = false;
    private EPinType enterPinType;
    private AidlPinpad mPinpad;
    private PasswordView mPasswordView;
    private TextView mPasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPinpad = TopApplication.usdkManage.getPinpad(0);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pinpad;
    }

    @Override
    protected void initViews() {
        String curr =TopApplication.sysParam.get(SysParam.APP_PARAM_TRANS_CURRENCY_SYMBOL) ;
        mPasswordView = (PasswordView) findViewById(R.id.passwordView);
        mPasswordText = (TextView)findViewById(R.id.passwordText);
        mTestCardNo = (TextView) findViewById(R.id.card_num);
        mTestAmount = (TextView) findViewById(R.id.trad_amount);
        mTextCashAmount = (TextView) findViewById(R.id.trad_cash_amount);
//      mPin = (TextView) findViewById(R.id.pin_num);

        tVtitle = (TextView)findViewById(R.id.header_title);
        tVtitle.setText(navTitle.toUpperCase());

        tVtime = (TextView)findViewById(R.id.header_time);
        tvOfflineWarning = (TextView)findViewById(R.id.lastPwdWarning);

        mTestCardNo.setText(getString(R.string.pin_tip_card_num) + PanUtils.maskedCardNo(panBlock));
        if (!TextUtils.isEmpty(amount)) {
            mTestAmount.setText(getString(R.string.trans_amount)+ curr+ Utils.ftoYuan(amount));
        }

        if (!TextUtils.isEmpty(cashAmount)) {
            mTextCashAmount.setVisibility(View.VISIBLE);
            mTextCashAmount.setText(getString(R.string.trans_amount_cash)+curr+ Utils.ftoYuan(cashAmount));
        }

//      0x00:联机 PIN
//      0x01:脱机 PIN 必
        tVtitleTip = (TextView)findViewById(R.id.input_amount_tip);
        if (EPinType.ONLINE_PIN_REQ == enterPinType) {
            tVtitleTip.setText("ONLINE PASSWORD:");
        } else if (EPinType.OFFLINE_PLAIN_TEXT_PIN_REQ == enterPinType) {
            tVtitleTip.setText("OFFLINE PASSWORD:");
        } else {
            tVtitleTip.setText("PCI MODE PASSWORD");
        }
        tVtitleTip.setText("READ CARD PIN");

        if (offlineLastTimes > 0) {
            tvOfflineWarning.setVisibility(View.VISIBLE);
            tvOfflineWarning.setText("Offline Pin Left Times:" + offlineLastTimes);
        }
        initialOnListenerStyle();
    }

    private void initialOnListenerStyle() {
        mPasswordView.setOnResultListener(new PasswordView.OnResultListener() {
            @Override
            public void finish(String result) {
                mPasswordText.setText(result);
            }

            @Override
            public void typing(String result) {
                mPasswordText.setText(result);
            }
        });
    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        AppLog.i(TAG," setListeners　onStart " ); // 需要界面起来后
        if (mPinpad != null && !isShow) {
            AppLog.i(TAG," setListeners　" ); // 需要界面起来后
            isShow = true;
            getPinBlock(panBlock, amount);
        }
    }

    @Override
    protected void loadParam() {
        Bundle bundle = getIntent().getExtras();
        navTitle = getIntent().getStringExtra(EUIParamKeys.NAV_TITLE.toString());
        panBlock = getIntent().getStringExtra(EUIParamKeys.PANBLOCK.toString());
        amount = getIntent().getStringExtra(EUIParamKeys.TRANS_AMOUNT.toString());
        cashAmount = getIntent().getStringExtra(EUIParamKeys.TRANS_AMOUNT_CASH.toString());
        enterPinType = (EPinType) bundle.getSerializable(EUIParamKeys.ENTERPINTYPE.toString());
        offlineLastTimes = getIntent().getIntExtra(EUIParamKeys.OFFLINE_PIN_LEFT_TIME.toString(),0);
    }

    private static final int ONKEY_SHOW = 0x01;
    private static final int ONKEY_ONERR = 0x02;
    private static final int ONKEY_COMFIRM = 0x03;
    private static final int ONKEY_CANCEL = 0x04;
    private static final int ONKEY_COMFIRM_NULL = 0x05;

    @Override
    protected void handleMsg(Message msg) {
        ActionResult result;
        switch (msg.what){
            case ONKEY_SHOW:
                String showPin = (String)msg.obj;
                if (!DataUtils.isNullString(showPin)) {
                    //mPin.setText(showPin);
                    mPasswordView.setTextValue(showPin);
                } else {
                    mPasswordView.setTextValue(showPin);
                }
                mPasswordText.setText(""+showPin.length());
                break;
            case ONKEY_ONERR:
                int errorCode = (int)msg.obj;
                TopToast.showFailToast(PinpadActivity.this,errorCode + "");
                result = new ActionResult(TransResult.ERR_ABORTED, null);
                finishPinpad(result);
                break;
            case ONKEY_COMFIRM:
                byte[] pin = ( byte[])msg.obj;
                result = new ActionResult(TransResult.SUCC, TopApplication.convert.bcdToStr(pin));
                finishPinpad(result);
                break;
            case ONKEY_COMFIRM_NULL:
                result = new ActionResult(TransResult.SUCC, "");
                finishPinpad(result);
                break;
            case ONKEY_CANCEL:
                result = new ActionResult(TransResult.ERR_ABORTED, null);
                finishPinpad(result);
                break;
            case TIP_TIME:
                String time = (String)msg.obj;
                if (!TextUtils.isEmpty(time)) {
                    tVtime.setText(time);

                    if ( 1 == Integer.valueOf(time)) // 提前1s 关闭
                        finishPinpad(new ActionResult(TransResult.ERR_ABORTED, null));
                }
                break;
        }
    }

    public void getPinBlock(final String cardNo, final String amount) {
        AppLog.i(TAG, "getPinBlock(), cardNo = " + cardNo);

        new Thread() {
            @Override
            public void run() {
                try {
                    mPinpad.setPinKeyboardMode(1);

                    PinParam pinParam =  new PinParam(Device.INDEX_TPK, enterPinType.getType(), cardNo,
                            PinpadConstant.KeyType.KEYTYPE_PEK,"0,4,5,6,7,8,9,10,11,12");
                    pinParam.setTimeOut(60000);

                    mPinpad.getPin(pinParam.getParam(), new GetPinListener.Stub() {
                        @Override
                        public void onInputKey(int len, String msg) throws RemoteException {
                            AppLog.i(TAG, "getPinBlock onInputKey");
//                            AudioManager mAudioMgr = (AudioManager) getSystemService(AUDIO_SERVICE);
//                            mAudioMgr.playSoundEffect(AudioManager.FX_KEY_CLICK);

                            if (isConfirm) {
                                return;
                            }

                            Device.beepNormal();
                            Message message = Message.obtain();
                            message.what = ONKEY_SHOW;
                            if (EPinType.ONLINE_PIN_REQ == enterPinType) {
                                message.obj = msg;
                            } else {
                                message.obj = filling(msg,"*");
                            }
                            handler.sendMessage(message);
                        }

                        @Override
                        public void onError(int errorCode) throws RemoteException {
                            AppLog.i(TAG, "getPinBlock onError " + errorCode);
                            Message message = Message.obtain();
                            message.obj = errorCode;
                            message.what = ONKEY_ONERR;
                            handler.sendMessage(message);
                        }

                        @Override
                        public void onConfirmInput(byte[] pin) throws RemoteException {
                            isConfirm = true;
                            if (pin == null || pin.length == 0) {
                                handler.sendEmptyMessage(ONKEY_COMFIRM_NULL);
                            } else {
                                AppLog.i(TAG, "getPinBlock onConfirmInput " + TopApplication.convert.bcdToStr(pin));
                                Message message = Message.obtain();
                                message.obj = pin;
                                message.what = ONKEY_COMFIRM;
                                handler.sendMessage(message);
                            }
                        }

                        @Override
                        public void onCancelKeyPress() throws RemoteException {
                            AppLog.i(TAG, "getPinBlock onCancelKeyPress ");
                            handler.sendEmptyMessage(ONKEY_CANCEL);
                        }

                        @Override
                        public void onStopGetPin() throws RemoteException {
                            AppLog.i(TAG, "getPinBlock onStopGetPin ");
                        }

                        @Override
                        public void onTimeout() throws RemoteException {

                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ActionResult result = new ActionResult(TransResult.ERR_ABORTED, null);
            finishPinpad(result);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void finishPinpad(ActionResult result) {

        finish(result);
    }

    private void closePinpad() {
        if (mPinpad != null) {
        if (BuildConfig.POS_MODE ==1) {
              try {
                  mPinpad.stopGetPin();
               } catch (RemoteException e) {
                   e.printStackTrace();
               }
               AppLog.v(TAG, "closePinpad stopGetPin");
               SystemClock.sleep(30);
            }
       }
    }

    private String filling(String msg, String f) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < msg.length(); i++) {
            stringBuffer.append(f);
        }
        return stringBuffer.toString();
    }
}
