package com.topwise.premierpay.trans.action;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.topwise.premierpay.trans.action.activity.TransOnlineActivity;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.model.EUIParamKeys;
import com.topwise.premierpay.trans.model.TransData;

/**
 * 创建日期：2021/6/15 on 17:25
 * 描述:
 * 作者:wangweicheng
 */
public class ActionOnlyOnline  extends AAction {
    /**
     * 子类构造方法必须调用super设置ActionStartListener
     *
     * @param listener {@link ActionStartListener}
     */
    public ActionOnlyOnline(ActionStartListener listener) {
        super(listener);
    }
    private String title;
    private Context context;
    private TransData transData;

    public void setParam(Context context, TransData transData,String title) {
        this.context = context;
        this.title = title;
        this.transData = transData;
    }

    @Override
    protected void process() {
        Intent intent = new Intent(context, TransOnlineActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(EUIParamKeys.NAV_TITLE.toString(), title);
        bundle.putSerializable(EUIParamKeys.CONTENT.toString(), transData);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
