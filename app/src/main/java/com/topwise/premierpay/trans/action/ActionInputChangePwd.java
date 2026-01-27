package com.topwise.premierpay.trans.action;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.view.ChangePwdDialog;

/**
 * 创建日期：2021/4/13 on 11:37
 * 描述:
 * 作者:  wangweicheng
 */
public class ActionInputChangePwd extends AAction {
    /**
     * 子类构造方法必须调用super设置ActionStartListener
     *
     * @param listener {@link ActionStartListener}
     */
    public ActionInputChangePwd(ActionStartListener listener) {
        super(listener);
    }
    private ChangePwdDialog changePwdDialog;

    private Handler handler;
    private Context context;
    private String title;
    /**
     *
     * @param context
     * @param handler
     * @param title
     */
    public void setParam(Context context, Handler handler, String title) {
        this.context = context;
        this.handler = handler;
        this.title = title;
    }

    @Override
    protected void process() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (changePwdDialog != null) {
                    changePwdDialog.dismiss();
                    changePwdDialog = null;
                }
                changePwdDialog = new ChangePwdDialog(context, handler,
                        title,6);
                changePwdDialog.setListener(new ChangePwdDialog.OnListener() {
                    @Override
                    public void onSucc(String data) {
                        if (!TextUtils.isEmpty(data)) {
                            ActionResult result = new ActionResult(TransResult.SUCC, data);
                            setResult(result);
                        }
                    }

                    @Override
                    public void onCancel() {
                        ActionResult result = new ActionResult(TransResult.ERR_HOST_REJECT, null);
                        setResult(result);
                    }
                });
                changePwdDialog.show();
            }
        });
    }
}
