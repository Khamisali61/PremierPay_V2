package com.topwise.premierpay.trans;

import android.content.Context;
import android.os.Handler;

import com.topwise.kdialog.DialogSureCancel;
import com.topwise.kdialog.IkeyListener;

import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.action.ActionInputTransData;
import com.topwise.premierpay.trans.action.ActionInputpwd;
import com.topwise.premierpay.trans.action.ActionTransOnline;
import com.topwise.premierpay.trans.action.ActionTransState;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.State;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.utils.Utils;

/**
 * 创建日期：2021/4/6 on 17:38
 * 描述:
 * 作者:  wangweicheng
 */
public class TransQrRefund extends BaseTrans {
    private String title = "";

    public TransQrRefund(Context context, Handler handler, TransEndListener transListener) {
        super(context, handler, ETransType.TRANS_QR_REFUND, transListener);
        title = ETransType.TRANS_QR_REFUND.getTransName().toUpperCase();
    }

    @Override
    public void onActionResult(String currentState, ActionResult result) {
        State state = State.valueOf(currentState);
        int ret = result.getRet();
        transData.setTransresult(ret);

        if (ret != TransResult.SUCC && state != State.ONLINE) {
            transEnd(result);
            return;
        }
        switch (state){
            case CHECK_PWD:
                String data = (String)result.getData();
                String sys_pwd = TopApplication.sysParam.get(SysParam.SEC_MNGPWD);
                if (sys_pwd.equals(data)){
                    gotoState(State.ENTER_DATA.toString());
                }else {
                    transEnd(new ActionResult(TransResult.ERR_SUPPWD_WRONG,null));
                }
                break;
            case ENTER_DATA:
                String res = (String) result.getData();
                transData.setEnterMode(Component.EnterMode.QR);
                transData.setOrigQrVoucher(res);
                transData.setOrigDate(res.substring(4, 8));
                // 扫码过后，提示输入金额
                gotoState(State.ENTER_AMOUNT.toString());
                break;
            case ENTER_AMOUNT:
                String amount = (String) result.getData();
                transData.setAmount(amount);
                checkAmount(amount);
                break;
            case ONLINE:
                gotoState(State.TRANS_STATE.toString());
                break;
            default:
                transEnd(result);
                break;
        }
    }

    @Override
    protected void bindStateOnAction() {
        bind(State.CHECK_PWD.toString(),  new ActionInputpwd(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionInputpwd)action).setParam(getCurrentContext(),handler,1,
                        getCurrentContext().getString(R.string.set_please_enter_supervisor_password ),
                        getCurrentContext().getString(R.string.set_please_enter_pwd));
            }
        }));


        bind(State.ENTER_AMOUNT.toString(), new ActionInputTransData(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionInputTransData) action).setParam(getCurrentContext(), handler, title,1)
                        .setInputLine1("",  9, false);

            }
        }));

        bind(State.ONLINE.toString(), new ActionTransOnline(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionTransOnline) action).setParam(getCurrentContext(),transData);
            }
        }));

        bind(State.TRANS_STATE.toString(),  new ActionTransState(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {

                ((ActionTransState) action).setParam(getCurrentContext(),title,transData);
            }
        }));

        gotoState(State.CHECK_PWD.toString());
    }

    private void checkAmount(String amontStr){

        final String AmontStr = getCurrentContext().getString(R.string.trans_amount_info, Utils.ftoYuan(amontStr));
        handler.post(new Runnable() {
            @Override
            public void run() {
                DialogSureCancel dialogSureCancel = new DialogSureCancel(getCurrentContext());
                dialogSureCancel.setTitle(title);
                dialogSureCancel.setContent(AmontStr);
                dialogSureCancel.show();
                dialogSureCancel.setMyLietener(new IkeyListener() {
                    @Override
                    public void onConfirm(String text) {
                        gotoState(State.ONLINE.toString());
                    }

                    @Override
                    public void onCancel(int res) {
                        gotoState(State.ENTER_AMOUNT.toString());
                    }
                });
            }
        });
    }
}
