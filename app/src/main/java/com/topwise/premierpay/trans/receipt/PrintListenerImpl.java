package com.topwise.premierpay.trans.receipt;

import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Message;

import com.topwise.kdialog.DialogSure;
import com.topwise.kdialog.DialogSureCancel;
import com.topwise.kdialog.IkeyListener;
import com.topwise.premierpay.app.ActivityStack;

/**
 * 创建日期：2021/4/2 on 14:08
 * 描述:
 * 作者:  wangweicheng
 */
public class PrintListenerImpl implements PrintListener {
    private Handler handler;


    private boolean isRun;

    private DialogSure dialogSure;
    private DialogSureCancel dialogSureCancel;
    private ConditionVariable cv;
    private int result = -1;
    private boolean isStressTest;

    public PrintListenerImpl( Handler handler,boolean isStressTest) {
        this.handler = handler;
        this.isRun = true;
        this.isStressTest = isStressTest;

    }
    public PrintListenerImpl( Handler handler) {
        this.handler = handler;
        this.isRun = true;
    }
    @Override
    public void onShowMessage(String title, String message) {

    }

    @Override
    public int onConfirm(final String title, final String message) {
        if(isStressTest){
            return -1;
        }
        cv = new ConditionVariable();
        result = -1;
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (dialogSureCancel != null) {
                    dialogSureCancel.dismiss();
                    dialogSureCancel = null;
                }
                dialogSureCancel = new DialogSureCancel(ActivityStack.getInstance().top());
                dialogSureCancel.setContent(message);
//                dialogSureCancel.setTitle(title);
                dialogSureCancel.setMyListener(new IkeyListener() {
                    @Override
                    public void onConfirm(String text) {
                        result = 0;
                        if (cv != null) {
                            cv.open();
                        }
                    }

                    @Override
                    public void onCancel(int res) {
                        result = 1;
                        if (cv != null) {
                            cv.open();
                        }
                    }
                });

                dialogSureCancel.show();
            }
        });
        cv.block();
        return result;
    }

    @Override
    public int onConfirmNext(final String title, final String message) {
        if (!isRun) {
            return -1;
        }
        if(isStressTest){
            return 0;
        }
        cv = new ConditionVariable();
        result = -1;
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (dialogSureCancel != null) {
                    dialogSureCancel.dismiss();
                    dialogSureCancel = null;
                }
                dialogSureCancel = new DialogSureCancel(ActivityStack.getInstance().top());
//                dialogSureCancel.setTitle(title);
                dialogSureCancel.setContent(message);
                dialogSureCancel.tickTimerStart(8);
                dialogSureCancel.setMyListener(new IkeyListener() {
                    @Override
                    public void onConfirm(String text) {
                        result = 0;
                        if (cv != null) {
                            cv.open();
                        }
                    }

                    @Override
                    public void onCancel(int res) {
                        result = 1;
                        if (cv != null) {
                            cv.open();
                        }
                    }
                });

                dialogSureCancel.show();
            }
        });
        cv.block();
        return result;
    }

    @Override
    public void onEnd() {
      Message message = Message.obtain();
      message.what = 2;
      handler.sendMessage(message);
    }

    public void setRun(boolean run) {
        isRun = run;
    }
}
