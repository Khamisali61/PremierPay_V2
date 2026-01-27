package com.topwise.premierpay.trans.action;

import android.content.Context;
import android.content.Intent;

import com.topwise.premierpay.trans.action.activity.ShowCodeActivity;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.EUIParamKeys;

/**
 * @author Victor(xiedianxin)
 * @brief description
 * @date 2023-08-08
 */
public class ActionShowCode extends AAction {
    /**
     * 子类构造方法必须调用super设置ActionStartListener
     *
     * @param listener {@link ActionStartListener}
     */

    private Context context;
    private String title;
    private String amount;

    public ActionShowCode(ActionStartListener listener) {
        super(listener);
    }

    public void setParam(Context context, String title, String amount) {
        this.context = context;
        this.title = title;
        this.amount = amount;
    }

    @Override
    protected void process() {
        Intent intent = new Intent(context, ShowCodeActivity.class);
        intent.putExtra(EUIParamKeys.NAV_TITLE.toString(), title);
        intent.putExtra(EUIParamKeys.CONTENT.toString(), amount);
        context.startActivity(intent);
    }

    @Override
    public void setResult(ActionResult result) {
        super.setResult(result);
    }
}
