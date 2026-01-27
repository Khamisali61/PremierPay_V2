package com.topwise.premierpay.trans.action;

import android.content.Context;
import android.content.Intent;

import com.topwise.premierpay.trans.action.activity.CheckTurnScreenActivity;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.EUIParamKeys;

/**
 * @author Victor(xiedianxin)
 * @brief description
 * @date 2023-08-08
 */
public class ActionCheckTurnScreen extends AAction {
    /**
     * 子类构造方法必须调用super设置ActionStartListener
     *
     * @param listener {@link ActionStartListener}
     */

    Context context;

    String title;

    String message;

    String message2;

    public ActionCheckTurnScreen(ActionStartListener listener) {
        super(listener);
    }

    public void setParam(Context context, String title, String message, String message2) {
        this.context = context;
        this.title = title;
        this.message = message;
        this.message2 = message2;
    }

    @Override
    protected void process() {
        Intent intent = new Intent(context, CheckTurnScreenActivity.class);
        intent.putExtra(EUIParamKeys.NAV_TITLE.toString(), title);
        intent.putExtra(EUIParamKeys.PROMPT_1.toString(), message);
        intent.putExtra(EUIParamKeys.PROMPT_2.toString(), message2);
        context.startActivity(intent);
    }

    @Override
    public void setResult(ActionResult result) {
        super.setResult(result);
    }
}
