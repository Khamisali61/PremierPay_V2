package com.topwise.premierpay.menu;

import android.text.InputType;
import android.text.TextUtils;


import com.topwise.kdialog.DialogSure;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.daoutils.DaoUtilsStore;
import com.topwise.premierpay.trans.action.ActionInputData;
import com.topwise.premierpay.trans.action.ActionTransPrintDetail;
import com.topwise.premierpay.trans.action.ActionTransPrintTotal;
import com.topwise.premierpay.trans.action.ActionTransRePrint;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.trans.receipt.PrintListenerImpl;
import com.topwise.premierpay.trans.receipt.ReceiptPrintTrans;
import com.topwise.premierpay.view.MenuPage;

/**
 * 创建日期：2021/4/7 on 16:27
 * 描述:
 * 作者:  wangweicheng
 */
public class ReprintMenuActivity extends BaseMenuActivity {
    @Override
    public MenuPage createMenuPage() {
        MenuPage.Builder builder = new MenuPage.Builder(ReprintMenuActivity.this, 5, 2)
                .addActionItem("Last Print", R.mipmap.app_prt, toReprint())
                .addActionItem("Reprint INV NR ", R.mipmap.app_prtany, RePrintOntransNo())
                .addActionItem("Print Detail ", R.mipmap.app_detail, PrintDetail())
                .addActionItem("Print Total ", R.mipmap.app_ptotal, PrintTotal())
                .addActionItem("Reprint Settlement", R.mipmap.app_prt, RePrintTotal());
        return builder.create();
    }

    private AAction toReprint() {
        ActionTransRePrint actionTransPrint = new ActionTransRePrint(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionTransRePrint)action).setParam(ReprintMenuActivity.this,handler);
            }
        });

        actionTransPrint.setEndListener(new AAction.ActionEndListener() {
            @Override
            public void onEnd(AAction action,final ActionResult result) {
                TopApplication.isRuning = false;
                if (TransResult.SUCC != result.getRet()){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String message = TransResult.getMessage(ReprintMenuActivity.this, result.getRet());

                            DialogSure dialogSure = new DialogSure(ReprintMenuActivity.this);
                            dialogSure.setTitle("Last Print");
                            dialogSure.setContent(message);
                            dialogSure.tickTimerStart( Component.FAILED_DIALOG_SHOW_TIME);
                            dialogSure.show();
                        }
                    });
                }
            }
        });
        return actionTransPrint;
    }

    private AAction PrintDetail() {
        ActionTransPrintDetail actionTransPrintDetail = new ActionTransPrintDetail(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionTransPrintDetail)action).setParam(ReprintMenuActivity.this,handler);
            }
        });
        actionTransPrintDetail.setEndListener(new AAction.ActionEndListener() {
            @Override
            public void onEnd(AAction action, ActionResult result) {
                TopApplication.isRuning = false;
            }
        });
        return actionTransPrintDetail;
    }

    private AAction RePrintTotal() {
        ActionTransPrintTotal  actionTransPrintTotal = new ActionTransPrintTotal(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionTransPrintTotal)action).setParam(ReprintMenuActivity.this,handler,true);
            }
        });
        actionTransPrintTotal.setEndListener(new AAction.ActionEndListener() {
            @Override
            public void onEnd(AAction action, ActionResult result) {
                TopApplication.isRuning = false;
            }
        });
        return actionTransPrintTotal;
    }

    private AAction PrintTotal() {
        ActionTransPrintTotal actionTransPrintTotal = new ActionTransPrintTotal(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionTransPrintTotal)action).setParam(ReprintMenuActivity.this,handler);
            }
        });
        actionTransPrintTotal.setEndListener(new AAction.ActionEndListener() {
            @Override
            public void onEnd(AAction action, ActionResult result) {
                TopApplication.isRuning = false;
            }
        });
        return actionTransPrintTotal;
    }

    private AAction RePrintOntransNo() {
        ActionInputData actionInputData = new ActionInputData(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionInputData)action).setParam(ReprintMenuActivity.this,"Reprint INV NR")
                .setInputLine1("",InputType.TYPE_CLASS_NUMBER,6,6);
            }
        },handler);

        actionInputData.setEndListener(new AAction.ActionEndListener() {
            @Override
            public void onEnd(AAction action, final ActionResult result) {
                ActivityStack.getInstance().pop();
                if (TransResult.SUCC == result.getRet()){
                    String transNo= (String)result.getData();
                    if (!TextUtils.isEmpty(transNo)) {
                        Long aLong = Long.valueOf(transNo);
                        final TransData transData = DaoUtilsStore.getInstance().getmTransDaoUtils().queryByTransNo(TransData.class, aLong.toString());
                        if (transData != null) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    ReceiptPrintTrans receiptPrintTrans = ReceiptPrintTrans.getInstance();
                                    PrintListenerImpl listener = new PrintListenerImpl( handler);
                                    receiptPrintTrans.print(transData, true, listener);
                                }
                            }).start();

                        } else { //无记录提示
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    String message = TransResult.getMessage(ReprintMenuActivity.this, -15);

                                    DialogSure dialogSure = new DialogSure(ReprintMenuActivity.this);
                                    dialogSure.setTitle("Reprint INV NR");
                                    dialogSure.setContent(message);
                                    dialogSure.tickTimerStart( Component.FAILED_DIALOG_SHOW_TIME);
                                    dialogSure.show();

                                }
                            });
                        }
                    }
                }
                TopApplication.isRuning = false;
            }
        });
        return actionInputData;
    }
}
