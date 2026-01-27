package com.topwise.premierpay.trans.action;

import android.os.Handler;

import com.topwise.kdialog.DialogSureCancel;
import com.topwise.kdialog.IkeyListener;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.TransResult;

public class ActionShowMessage extends AAction {
    /**
     * 子类构造方法必须调用super设置ActionStartListener
     *
     * @param listener {@link ActionStartListener}
     */
    public ActionShowMessage(ActionStartListener listener) {
        super(listener);
    }
    private Handler handler;
    private String seePhone;
    private int chooseIndex =0;

    private String title ="";

    public void setParam(Handler handler,String title, String seePhone) {
        this.handler = handler;
        this.seePhone = seePhone;
        this.title = title;
    }
    @Override
    protected void process() {
        handler.post(new Runnable() {
            @Override
            public void run() {

                DialogSureCancel dialogSureCancel = new DialogSureCancel(ActivityStack.getInstance().top());
               dialogSureCancel.setTitle(title);
                dialogSureCancel.setContent(seePhone);
                dialogSureCancel.setMyListener(new IkeyListener() {
                    @Override
                    public void onConfirm(String text) {
                        setResult(new ActionResult(TransResult.SUCC,text));
                    }

                    @Override
                    public void onCancel(int ret) {
                        setResult(new ActionResult(TransResult.ERR_CANCEL,null));
                    }
                });
                dialogSureCancel.show();
            }
        }) ;
    }
}
