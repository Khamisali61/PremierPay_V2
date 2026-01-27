package com.topwise.premierpay.trans.action;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.EUIParamKeys;

public class ActionInputTransData extends AAction {
    private Context context;
    private Handler handler;
    private String title;
    private String prompt1;

    private int maxLen1;
    private int minLen1;
    private boolean isVoidLastTrans;
    private int lineNum;

    private boolean isStressTest =false;

    public ActionInputTransData setParam(Context context, Handler handler, String title, int lineNum) {
        this.context = context;
        this.handler = handler;
        this.title = title;
        this.lineNum = lineNum;
        return this;
    }

    public ActionInputTransData setInputLine1(String prompt,  int maxLen, boolean isVoidLastTrans) {
        return setInputLine1(prompt,  maxLen, 0, isVoidLastTrans);
    }

    public ActionInputTransData setInputLine1(String prompt,  int maxLen, int minLen,
                                              boolean isVoidLastTrans) {
        this.prompt1 = prompt;
        this.maxLen1 = maxLen;
        this.minLen1 = minLen;
        this.isVoidLastTrans = isVoidLastTrans;
        return this;
    }

    public boolean isStressTest() {
        return isStressTest;
    }

    public ActionInputTransData setStressTest(boolean stressTest) {
        isStressTest = stressTest;
        return this;
    }

    /**
     * 子类构造方法必须调用super设置ActionStartListener
     *
     * @param listener {@link ActionStartListener}
     */
    public ActionInputTransData(ActionStartListener listener) {
        super(listener);
    }

    @Override
    protected void process() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (lineNum == 1) {
                    Intent intent = new Intent(context, InputAmountActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(EUIParamKeys.NAV_TITLE.toString(), title);
                    bundle.putString(EUIParamKeys.PROMPT_1.toString(), prompt1);
                    bundle.putInt(EUIParamKeys.INPUT_MAX_LEN_1.toString(), maxLen1);
                    bundle.putInt(EUIParamKeys.INPUT_MIN_LEN_1.toString(), minLen1);
                    bundle.putBoolean(EUIParamKeys.STRESS_TEST.toString(), isStressTest);
                    bundle.putBoolean(EUIParamKeys.VOID_LAST_TRANS_UI.toString(), isVoidLastTrans);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public void setResult(ActionResult result) {
        super.setResult(result);
        context = null;
    }
}
