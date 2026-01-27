package com.topwise.premierpay.trans.action;

import android.content.Context;
import android.os.Handler;


import com.topwise.kdialog.DialogSure;
import com.topwise.kdialog.IkeyListener;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.TransResult;

/**
 * 创建日期：2021/4/22 on 9:42
 * 描述:
 * 作者:wangweicheng
 */
public class ActionComFiromSecondTap extends AAction {
    /**
     * 子类构造方法必须调用super设置ActionStartListener
     *
     * @param listener {@link ActionStartListener}
     */
    public ActionComFiromSecondTap(ActionStartListener listener) {
        super(listener);
    }
    private Context context;
    private String title;
    private String tip;
    private Handler handler;


    public void setParam(Context context, String title, String tip,Handler handler) {
        this.context = context;
        this.handler = handler;
        this.title = title;
        this.tip = tip;
    }
    @Override
    protected void process() {
        handler.post(new Runnable() {
            @Override
            public void run() {

                DialogSure dialogSure = new DialogSure(context);
                dialogSure.setTitle("Rupay");
                dialogSure.setContent("Please Second Tap!");
                dialogSure.setMyLietener(new IkeyListener() {
                    @Override
                    public void onConfirm(String text) {
                        ActionResult result = new ActionResult(TransResult.SUCC, null);
                        setResult(result);
                    }

                    @Override
                    public void onCancel(int res) {
                        ActionResult result = new ActionResult(TransResult.SUCC, null);
                        setResult(result);
                    }
                });

                dialogSure.show();

            }
        });
    }
}
