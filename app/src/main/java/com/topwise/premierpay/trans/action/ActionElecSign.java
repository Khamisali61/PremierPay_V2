package com.topwise.premierpay.trans.action;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.topwise.premierpay.trans.action.activity.ElecSignActivity;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.model.EUIParamKeys;
import com.topwise.premierpay.trans.model.TransData;

public class ActionElecSign extends AAction {

    /**
     * 子类构造方法必须调用super设置ActionStartListener
     *
     * @param listener {@link ActionStartListener}
     */
    public ActionElecSign(ActionStartListener listener) {
        super(listener);
    }

    private Context context;
    private TransData transData;
    private String title;

    public void setParam(Context context,String title, TransData transData) {
        this.context = context;
        this.title = title;
        this.transData = transData;
    }

    @Override
    protected void process() {
        Intent intent = new Intent(context, ElecSignActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(EUIParamKeys.NAV_TITLE.toString(), title);
        bundle.putSerializable(EUIParamKeys.CONTENT.toString(), transData);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
