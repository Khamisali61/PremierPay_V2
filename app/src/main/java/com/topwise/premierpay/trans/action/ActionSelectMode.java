package com.topwise.premierpay.trans.action;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.topwise.kdialog.DialogSingleChoice;
import com.topwise.kdialog.IkeyListener;
import com.topwise.kdialog.adapter.SingleBean;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.TransResult;


import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：2021/6/1 on 16:00
 * 描述:
 * 作者:wangweicheng
 */
public class ActionSelectMode extends AAction {
    /**
     * 子类构造方法必须调用super设置ActionStartListener
     *
     * @param listener {@link ActionStartListener}
     */
    public ActionSelectMode(ActionStartListener listener) {
        super(listener);
    }
    private Context context;
    private Handler handler;
    private String [] contents;
    private String title ="";
    private int index ;
    public void setParam(Context context, Handler handler,String [] contents) {
        this.context = context;
        this.handler = handler;
        this.contents = contents;
    }

    public void setParam(Context context, Handler handler,String title ,String [] contents) {
        this.context = context;
        this.handler = handler;
        this.contents = contents;
        this.title = title;
    }

    public void setParam(Context context, Handler handler,String title ,String [] contents,int index) {
        this.context = context;
        this.handler = handler;
        this.contents = contents;
        this.title = title;
        this.index =  index;
    }

    @Override
    protected void process() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                List<SingleBean> selects = new ArrayList<>();
                for (int i = 0; i < contents.length; i++){
                    boolean isSelect = false;
                    if(i==index){
                        isSelect =  true;
                    }

                    SingleBean singleBean = new SingleBean(isSelect,contents[i]);
                    selects.add(singleBean);
                }
                DialogSingleChoice dialogSingleChoice =  new DialogSingleChoice(context);
                if(TextUtils.isEmpty(title)) {
                    dialogSingleChoice.setTitle("Please Select Type");
                }else{
                    dialogSingleChoice.setTitle(title);
                }
                dialogSingleChoice.setListdata(selects);
//                dialogSingleChoice.setCancelable(false);
                dialogSingleChoice.setMyLietener(new IkeyListener() {
                    @Override
                    public void onConfirm(String text) {
                        ActionResult result = new ActionResult(TransResult.SUCC, Integer.valueOf(text));
                        setResult(result);
                    }

                    @Override
                    public void onCancel(int res) {
                        ActionResult result = new ActionResult(TransResult.ERR_ABORTED, null);
                        setResult(result);
                    }
                });
                dialogSingleChoice.show();
            }
        });
    }
}
