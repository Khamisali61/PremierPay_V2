package com.topwise.premierpay.trans.action;

import android.content.Context;
import android.content.Intent;

import com.topwise.premierpay.trans.action.activity.CardConfirmActivity;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.model.EUIParamKeys;

public class ActionCardConfirm extends AAction {
    private Context context;
    private String title;
    private String pan;
    private String amount;

    /**
     * 子类构造方法必须调用super设置ActionStartListener
     *
     * @param listener {@link ActionStartListener}
     */
    public ActionCardConfirm(ActionStartListener listener) {
        super(listener);
    }

    public void setParam(Context context, String title, String pan, String amount) {
        this.context = context;
        this.title = title;
        this.pan = pan;
        this.amount = amount;
    }

    @Override
    protected void process() {
        Intent intent = new Intent(context, CardConfirmActivity.class);
        intent.putExtra(EUIParamKeys.NAV_TITLE.toString(), title);
        intent.putExtra(EUIParamKeys.NAV_BACK.toString(), true);
        intent.putExtra(EUIParamKeys.TRANS_AMOUNT.toString(), amount);
        intent.putExtra(EUIParamKeys.PANBLOCK.toString(), pan);
        context.startActivity(intent);
    }
}
