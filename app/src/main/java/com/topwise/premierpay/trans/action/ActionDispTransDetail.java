package com.topwise.premierpay.trans.action;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.topwise.premierpay.trans.action.activity.DispTransDetailActivity;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.model.EUIParamKeys;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * 创建日期：2021/4/13 on 16:17
 * 描述:
 * 作者:  wangweicheng
 */
public class ActionDispTransDetail extends AAction {
    /**
     * 子类构造方法必须调用super设置ActionStartListener
     *
     * @param listener {@link ActionStartListener}
     */
    public ActionDispTransDetail(ActionStartListener listener) {
        super(listener);
    }
    private Context context;
    private Handler handler;

    private LinkedHashMap<String, String> map;
    private String title;
    private Boolean ivCancel;
    private int inletType = 0;

    public void setParam(Context context, Handler handler, String title, LinkedHashMap<String, String> map, boolean ivCancel) {
        this.context = context;
        this.handler = handler;
        this.title = title;
        this.map = map;
        this.ivCancel = ivCancel;
    }

    @Override
    protected void process() {
        ArrayList<String> leftColumns = new ArrayList<String>();
        ArrayList<String> rightColumns = new ArrayList<String>();

        Set<String> keys = map.keySet();
        for (String key : keys) {
            leftColumns.add(key);
            Object value = map.get(key);
            if (value != null) {
                rightColumns.add((String) value);
            } else {
                rightColumns.add("");
            }
        }

        Bundle bundle = new Bundle();
        bundle.putString(EUIParamKeys.NAV_TITLE.toString(), title);
        bundle.putBoolean(EUIParamKeys.NAV_BACK.toString(), ivCancel);
        bundle.putStringArrayList(EUIParamKeys.ARRAY_LIST_1.toString(), leftColumns);
        bundle.putStringArrayList(EUIParamKeys.ARRAY_LIST_2.toString(), rightColumns);

        Intent intent = new Intent(context, DispTransDetailActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
