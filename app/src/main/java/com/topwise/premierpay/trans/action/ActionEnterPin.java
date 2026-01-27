package com.topwise.premierpay.trans.action;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.topwise.manager.emv.enums.EPinType;
import com.topwise.premierpay.trans.action.activity.PinpadActivity;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.EUIParamKeys;

public class ActionEnterPin extends AAction {
    private Context context;
    private String title;
    private String pan;
    private String amount;
    private String cashAmount;
    private EPinType enterPinType;
    private int  lastTime;

    public final static int ONLINE_PIN = 0x00;//, // 联机pin
    public final static int OFFLINE_PLAIN_PIN= 0x01;//, // 脱机明文pin
    public final static int OFFLINE_CIPHER_PIN= 0x02;//, // 脱机密文pin

    /**
     * 子类构造方法必须调用super设置ActionStartListener
     *
     * @param listener {@link ActionStartListener}
     */
    public ActionEnterPin(ActionStartListener listener) {
        super(listener);
    }

    /**
     *
     * @param context
     * @param title
     * @param pan
     * @param amount
     * @param enterPinType
     */
    public void setParam(Context context, String title, String pan, String amount, EPinType enterPinType) {
        this.context = context;
        this.title = title;
        this.pan = pan;
        this.amount = amount;
        this.enterPinType = enterPinType;
        this.cashAmount = "";
    }

    public void setParam(Context context, String title, String pan, String amount, String cashAmount, EPinType enterPinType, int lastTime) {
        this.context = context;
        this.title = title;
        this.pan = pan;
        this.amount = amount;
        this.cashAmount = cashAmount;
        this.enterPinType = enterPinType;
        this.lastTime = lastTime;
    }

    @Override
    protected void process() {
        Intent intent = new Intent(context, PinpadActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(EUIParamKeys.NAV_TITLE.toString(), title);
        bundle.putBoolean(EUIParamKeys.NAV_BACK.toString(), true);
        bundle.putString(EUIParamKeys.TRANS_AMOUNT.toString(), amount);
        bundle.putString(EUIParamKeys.TRANS_AMOUNT_CASH.toString(), cashAmount);
        bundle.putString(EUIParamKeys.PANBLOCK.toString(), pan);
        bundle.putInt(EUIParamKeys.OFFLINE_PIN_LEFT_TIME.toString(), lastTime);
        bundle.putSerializable(EUIParamKeys.ENTERPINTYPE.toString(), enterPinType);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void setResult(ActionResult result) {
        super.setResult(result);
        context = null;
    }
}
