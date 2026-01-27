package com.topwise.premierpay.trans.action;

import android.os.Handler;

import com.topwise.kdialog.DialogSingleChoice;
import com.topwise.kdialog.IkeyListener;
import com.topwise.kdialog.adapter.SingleBean;
import com.topwise.manager.AppLog;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.TransResult;

import java.util.ArrayList;
import java.util.List;

public class ActionMultiSelect extends AAction {
    /**
     * 子类构造方法必须调用super设置ActionStartListener
     *
     * @param listener {@link ActionStartListener}
     */
    public ActionMultiSelect(ActionStartListener listener) {
        super(listener);
    }
    private Handler handler;
    private String [] aids;
    private int chooseIndex = 0;

    private String title ="";

    public void setParam(Handler handler,String title, String [] aids) {
        this.handler = handler;
        this.aids = aids;
        this.title = title;
    }

    public void setParam(Handler handler, String title,String [] aids, int chooseIndex) {
        this.handler = handler;
        this.aids = aids;
        this.chooseIndex = chooseIndex;
        this.title = title;
    }

    @Override
    protected void process() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                List<SingleBean> singles = new ArrayList<>();
                for (int i = 0; i < aids.length; i++) {
                    SingleBean s = new SingleBean(aids[i]);
                    if (i == chooseIndex) {
                        s.setSelect(true);
                    }
                    singles.add(s);
                }

                DialogSingleChoice dialogSingleChoice = new DialogSingleChoice(ActivityStack.getInstance().top());
                dialogSingleChoice.setListdata(singles);
                dialogSingleChoice.setTitle(title);
                dialogSingleChoice.setCancelable(false);
                dialogSingleChoice.setMyLietener(new IkeyListener() {
                    @Override
                    public void onConfirm(String text) {
                        AppLog.d("ActionMultiSelect","onConfirm======== " +text);
                        setResult(new ActionResult(TransResult.SUCC, text));
                    }

                    @Override
                    public void onCancel(int res) {
                        AppLog.d("ActionMultiSelect","onCancel======== " +res);
                        setResult(new ActionResult(TransResult.ERR_CANCEL,null));
                    }
                });
                dialogSingleChoice.show();
            }
        }) ;
    }
}
