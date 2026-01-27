package com.topwise.premierpay.trans.action;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.topwise.premierpay.trans.action.activity.InputData2Activity;
import com.topwise.premierpay.trans.action.activity.InputDataActivity;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.model.EUIParamKeys;

/**
 * 创建日期：2021/4/12 on 16:22
 * 描述:
 * 作者:  wangweicheng
 */
public class ActionInputData extends AAction {
    /**
     * 子类构造方法必须调用super设置ActionStartListener
     *
     * @param listener {@link ActionStartListener}
     */
    public ActionInputData(ActionStartListener listener,Handler handler) {
        super(listener);
        this.lineNum = 1;
        this.handler = handler;
    }

    public ActionInputData(ActionStartListener listener,Handler handler, int lineNum) {
        super(listener);
        this.lineNum = lineNum;
        this.handler = handler;
    }

    private Context context;
    private String title;

    private int lineNum;
    private Handler handler;

    private String prompt1;
    private int inputType1;
    private int maxLen1;
    private int minLen1;

    private String prompt2;
    private int inputType2;
    private int maxLen2;
    private int minLen2;
    private boolean supScan;

    public ActionInputData setParam(Context context, String title) {
        this.context = context;
        this.title = title;
        return this;
    }

    public ActionInputData setInputLine1(String prompt,  int inTpye, int maxLen, int minLen) {
        this.prompt1 = prompt;
        this.inputType1 = inTpye;
        this.maxLen1 = maxLen;
        this.minLen1 = minLen;
        return this;
    }

    public ActionInputData setInputLine1(String prompt,  int inTpye, int maxLen, int minLen,boolean supScan) {
        this.prompt1 = prompt;
        this.inputType1 = inTpye;
        this.maxLen1 = maxLen;
        this.minLen1 = minLen;
        this.supScan = supScan;
        return this;
    }

    public ActionInputData setInputLine2(String prompt,  int inTpye, int maxLen, int minLen) {
        this.prompt2 = prompt;
        this.inputType2 = inTpye;
        this.maxLen2 = maxLen;
        this.minLen2 = minLen;
        return this;
    }

    @Override
    protected void process() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (1 == lineNum) {
                    Intent intent = new Intent(context, InputDataActivity.class);
                    intent.putExtra(EUIParamKeys.NAV_TITLE.toString(), title);
                    intent.putExtra(EUIParamKeys.NAV_BACK.toString(), true);

                    intent.putExtra(EUIParamKeys.SUPPORT_SCAN.toString(), supScan);

                    intent.putExtra(EUIParamKeys.PROMPT_1.toString(), prompt1);
                    intent.putExtra(EUIParamKeys.INPUT_TYPE_1.toString(), inputType1);

                    intent.putExtra(EUIParamKeys.INPUT_MIN_LEN_1.toString(), minLen1);
                    intent.putExtra(EUIParamKeys.INPUT_MAX_LEN_1.toString(), maxLen1);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, InputData2Activity.class);
                    intent.putExtra(EUIParamKeys.NAV_TITLE.toString(), title);
                    intent.putExtra(EUIParamKeys.NAV_BACK.toString(), true);

                    intent.putExtra(EUIParamKeys.PROMPT_1.toString(), prompt1);
                    intent.putExtra(EUIParamKeys.INPUT_TYPE_1.toString(), inputType1);
                    intent.putExtra(EUIParamKeys.INPUT_MIN_LEN_1.toString(), minLen1);
                    intent.putExtra(EUIParamKeys.INPUT_MAX_LEN_1.toString(), maxLen1);

                    intent.putExtra(EUIParamKeys.PROMPT_2.toString(), prompt2);
                    intent.putExtra(EUIParamKeys.INPUT_TYPE_2.toString(), inputType1);
                    intent.putExtra(EUIParamKeys.INPUT_MIN_LEN_2.toString(), minLen2);
                    intent.putExtra(EUIParamKeys.INPUT_MAX_LEN_2.toString(), maxLen2);
                    context.startActivity(intent);
                }
            }
        });
    }
}
