package com.topwise.premierpay.trans.action;

import android.content.Context;
import android.os.Handler;

import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.view.InputPwdDialog;

/**
 * 创建日期：2021/3/30 on 9:55
 * 描述:
 * 作者:  wangweicheng
 */
public class ActionInputpwd extends AAction {

    /**
     * 子类构造方法必须调用super设置ActionStartListener
     *
     * @param listener {@link ActionStartListener}
     */
    public ActionInputpwd(ActionStartListener listener) {
        super(listener);
    }
    private Handler handler;
    private Context context;
    private int type;
    private String title;
    private String subTitle;
    private InputPwdDialog dialog;

    /**
     *
     * @param context
     * @param handler
     * @param type  1 主管密码6 位;2 系统管理员
     * @param title
     * @param subTitle
     */
    public void setParam(Context context, Handler handler, int type, String title, String subTitle) {
        this.context = context;
        this.handler = handler;
        this.type = type;
        this.title = title;
        this.subTitle = subTitle;
    }
    @Override
    protected void process() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (type == 4) {
                    dialog = new InputPwdDialog(context, handler, subTitle, title, type,false);
                    dialog.setPwdListener(new InputPwdDialog.OnPwdListener() {
                        @Override
                        public void onSucc(String data) {
                            ActionResult result = new ActionResult(TransResult.SUCC, data);
                            setResult(result);
                        }

                        @Override
                        public void onErr() {
                            ActionResult result = new ActionResult(TransResult.ERR_ABORTED, null);
                            setResult(result);
                        }
                    });
                } else {
                    dialog = new InputPwdDialog(context, handler, subTitle, title, type);
                    dialog.setPwdListener(new InputPwdDialog.OnPwdListener() {
                        @Override
                        public void onSucc(String data) {
                            ActionResult result = new ActionResult(TransResult.SUCC, data);
                            setResult(result);
                        }

                        @Override
                        public void onErr() {
                            ActionResult result = new ActionResult(TransResult.ERR_ABORTED, null);
                            setResult(result);
                        }
                    });
                }

                dialog.show();
            }
        });
    }

    @Override
    public void setResult(ActionResult result) {
        super.setResult(result);
        context = null;
        dialog =null;
    }
}
