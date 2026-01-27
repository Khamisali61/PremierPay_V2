package com.topwise.premierpay.mdb.activity;

import android.os.Message;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.widget.TextView;

import com.topwise.cloudpos.aidl.mdb.TransRequestCallback;
import com.topwise.cloudpos.data.SerialportConstant;
import com.topwise.kdialog.DialogSureCancel;
import com.topwise.kdialog.IkeyListener;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.BaseActivity;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.mdb.MDBCall;
import com.topwise.premierpay.param.SysParam;

public class MDBTestActivity extends BaseActivity {
    @Override
    protected void initViews() {
        ((TextView)findViewById(R.id.header_title)).setText(getString(R.string.test_mdb));
        TopApplication.sysParam.set(SysParam.AUTO_IN_MDB,"Y");
        try {
            TopApplication.usdkManage.getMDBSerial(SerialportConstant.PORT_ONE).open();
        }catch (Exception e){

        }

    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected void loadParam() {
        try {

            TopApplication.usdkManage.getMDBSerial(SerialportConstant.PORT_ONE).setTransRequestCallback(new TransRequestCallback.Stub() {
                @Override
                public void onReceived(String s) throws RemoteException {
                    MDBCall mdbCall = new MDBCall(MDBTestActivity.this, handler);
                    mdbCall.doTrans(s);
                }
            });
        }catch (Exception e){

        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            exitMDBActivity();
            return true;
        }
        return true;
    }

    private void exitMDBActivity() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                DialogSureCancel dialogSureCancel =  new DialogSureCancel(MDBTestActivity.this);
                dialogSureCancel.setTitle("MDB");
                dialogSureCancel.setContent("EXIT MDB MODE?");
                dialogSureCancel.tickTimerStart(5);
                dialogSureCancel.show();
                dialogSureCancel.setMyLietener(new IkeyListener() {
                    @Override
                    public void onConfirm(String text) {

                        TopApplication.sysParam.set(SysParam.AUTO_IN_MDB,"N");
                        //TransContext.getInstance().setCurrentAction(null);
                        try {
                            TopApplication.usdkManage.getMDBSerial(SerialportConstant.PORT_ONE).close();
                        }catch (Exception e){

                        }
                        finish();
//                        ActivityStack.getInstance().popAllButBottom();
//                        resetFinish();
//                        ActionResult result = new ActionResult(TransResult.ERR_ABORTED, null);
//                        finish(result);
                        return;
                    }
                    @Override
                    public void onCancel(int res) {
                    }
                });
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.mdb_layout;
    }

    @Override
    protected void handleMsg(Message msg) {

    }
}
