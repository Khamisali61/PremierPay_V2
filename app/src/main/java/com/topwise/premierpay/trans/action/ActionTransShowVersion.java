package com.topwise.premierpay.trans.action;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.topwise.premierpay.setting.activity.SettingAboutActivity;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.model.EUIParamKeys;

/**
 * 创建日期：2021/4/6 on 10:38
 * 描述:
 * 作者:  wangweicheng
 */
public class ActionTransShowVersion extends AAction {
    /**
     * 子类构造方法必须调用super设置ActionStartListener
     *
     * @param listener {@link ActionStartListener}
     */
    public ActionTransShowVersion(ActionStartListener listener) {
        super(listener);
    }

    private Context context;
    private int type;

    public void setParam(Context context) {
        this.context = context;
        this.type = 0;
    }

    public void setParam(Context context,int type) {
        this.context = context;
        this.type = 1;
    }

    @Override
    protected void process() {
        Intent intent = new Intent(context, SettingAboutActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(EUIParamKeys.PROMPT_1.toString(), "1");
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
