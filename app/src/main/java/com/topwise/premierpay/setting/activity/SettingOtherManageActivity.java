package com.topwise.premierpay.setting.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.topwise.kdialog.DialogEditSureCancel;
import com.topwise.kdialog.DialogMoreChoice;
import com.topwise.kdialog.DialogSure;
import com.topwise.kdialog.ISelectListener;
import com.topwise.kdialog.IkeyListener;
import com.topwise.kdialog.adapter.SingleBean;
import com.topwise.kdialog.adapter.SingleChoiceAdapter;
import com.topwise.manager.AppLog;
import com.topwise.manager.TopUsdkManage;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.daoutils.DaoUtilsStore;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.tms.TmsParamDownload;
import com.topwise.premierpay.trans.action.ActionMultiSelect;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.transmit.TransProcessListenerImpl;
import com.topwise.premierpay.utils.ScanCodeUtils;
import com.topwise.premierpay.view.TopToast;
import com.topwise.premierpay.trans.action.activity.BaseActivityWithTickForAction;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：2021/3/31 on 9:43
 * 描述:
 * 作者:  wangweicheng
 */
public class SettingOtherManageActivity extends BaseActivityWithTickForAction implements View.OnClickListener {
    private static final String TAG = TopApplication.APPNANE + SettingOtherManageActivity.class.getSimpleName();
    private TextView tVtime;

    private TextView tvMode,camera_mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tickTimerStart(120);
    }

    private SettingClearDialog clearDialog;
    protected static final int CLEAR_TIP = 0x01;
    private static final int CHEXK_PASS = 0x02;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_set_clear:
                if (clearDialog != null) {
                    clearDialog.dismiss();
                    clearDialog = null;
                }
                final String [] selectItem = {getString(R.string.om_cleartrade_menu_reserval),
                        getString(R.string.om_cleartrade_menu_trade_voucher),
//                        getString(R.string.om_cleartrade_menu_script),
//                        getString(R.string.om_cleartrade_menu_black_list)
                };
                List<SingleBean> slist = new ArrayList<>();
                for (int i = 0; i < selectItem.length ; i++) {
                    SingleBean s = new SingleBean(false, selectItem[i]);
                    slist.add(s);
                }
                DialogMoreChoice dialogMoreChoice = new DialogMoreChoice(this);
                dialogMoreChoice.setTitle(getString(R.string.set_other_clear));
                dialogMoreChoice.setListdata(slist);
                dialogMoreChoice.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        SingleChoiceAdapter adapter = (SingleChoiceAdapter) dialogMoreChoice.getListView().getAdapter();
                        SingleBean item = (SingleBean) adapter.getItem(i);
                        adapter.getListData().get(i).setSelect(!item.isSelect());
                        adapter.notifyDataSetChanged();
                    }
                });
                dialogMoreChoice.setSelectLietener(new ISelectListener() {
                    @Override
                    public void onConfirm(Boolean[] text) {
                        for (int i = 0; i < text.length; i++) {
                            AppLog.d(TAG,"ssss " + text[i]);
                            switch (i) {
                                case 0:
                                    if (text[i]) {
                                        Message m = new Message();
                                        m.what = CHEXK_PASS;
                                        m.obj = text;
                                        m.arg1 =0;
                                        handler.handleMessage(m);
                                    }
                                    break;
                                case 1:
                                    if (text[i]) {
                                        Message m = new Message();
                                        m.what = CHEXK_PASS;
                                        m.obj = text;
                                        m.arg1 =1;
                                        handler.handleMessage(m);}
                                    break;
//                                case 2:
//                                    if (text[i]) {
//                                        AppLog.d(TAG,"ssss onConfirm " + selectItem[i] );
//                                    }
//                                    break;
//                                case 3:
//                                    if (text[i]) {
//                                        AppLog.d(TAG,"ssss onConfirm " + selectItem[i] );
//                                    }
//                                    break;
                            }
                        }
                    }
                });
                dialogMoreChoice.show();

//                List<selectBean> slist = new ArrayList<>();
//                for (int i = 0; i < selectItem.length ; i++) {
//                    selectBean s = new selectBean(selectItem[i],false);
//                    slist.add(s);
//                }
//                clearDialog = new SettingClearDialog(this,handler,
//                        getString(R.string.set_other_clear),slist);
//                clearDialog.setListener(new SettingClearDialog.OnListener() {
//                    @Override
//                    public void onSucc(boolean [] data) {
//                        for (int i = 0; i < data.length; i++) {
//                            AppLog.d(TAG,"ssss " +data[i]);
//                        }
//                        //to do
//
//                    }
//
//                    @Override
//                    public void onCancel() {
//
//                    }
//                });
//                clearDialog.show();
//                TopToast.showNormalToast(this,"清除");
                break;
            case R.id.rl_set_downlod:
                TopToast.showNormalToast(this,"Downlod");
                break;
            case R.id.rl_set_print_param:
                TopToast.showNormalToast(this,"print_param");
                break;
            case R.id.rl_set_tms_downdload:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        TransProcessListenerImpl transProcessListener = new TransProcessListenerImpl();
                        transProcessListener.onUpdateProgressTitle("TMS parameter download");
                        transProcessListener.onShowProgress("Receiving...",30);
                        TmsParamDownload tmsParamDownload = new TmsParamDownload();
                       String result = tmsParamDownload.downloadParam();
                        transProcessListener.onHideProgress();
                        if (!TextUtils.isEmpty(result)) {
                            Device.beepSucc();
                            transProcessListener.onShowSuccessMsgWithConfirm("Success",8);
                        } else {
                            Device.beepFail();
                            transProcessListener.onShowFailWithConfirm("Failure",8);
                        }
                        transProcessListener = null;
                    }
                }).start();
                break;
            case R.id.rl_set_tms_url:
                String url = tvTmsUrl.getText().toString();
                setParam(new SettingCommManageActivity.OnListener() {
                             @Override
                             public void onCallBack(String data) {
                                 AppLog.i(TAG,"url " + data);
                                 if (!TextUtils.isEmpty(data)) {
                                     TopApplication.sysParam.set(SysParam.TMS_PARAM_URL,data);
                                     tvTmsUrl.setText(data);
                                 }
                             }
                         },getString(R.string.tms_url),
                        getString(R.string.tms_url),
                        url,
                        InputType.TYPE_CLASS_TEXT,80);
                break;
            case R.id.rl_switch_bluetooth:
                chooseMode();
                break;
            case R.id.rl_switch_camera:
                switchCamera();
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_othet_manage_layout;
    }

    private TextView tvTmsUrl;
    @Override
    protected void initViews() {
        ((TextView)findViewById(R.id.header_title)).setText(getString(R.string.set_other_manage));
        tVtime = (TextView)findViewById(R.id.header_time);

        String temp = TopApplication.sysParam.get(SysParam.TMS_PARAM_URL);
        tvTmsUrl = (TextView)findViewById(R.id.tv_set_tms_url);
        if (!TextUtils.isEmpty(temp)) {
            tvTmsUrl.setText(temp);
        }

        //mode
        tvMode = (TextView) findViewById(R.id.tv_mode);
        int mode  = TopApplication.sysParam.getInt(SysParam.DEVICE_MODE);
        tvMode.setText(getShowMode(mode));

        findViewById(R.id.rl_switch_bluetooth).setOnClickListener(this);
        if(!(Build.MANUFACTURER.equalsIgnoreCase("topwise") ||
                Build.MANUFACTURER.equalsIgnoreCase("gertec"))) {
            findViewById(R.id.rl_switch_bluetooth).setVisibility(View.GONE);
        }
        //camera switch
        camera_mode = (TextView) findViewById(R.id.camera_mode);
        int cameraMode  = TopApplication.sysParam.getInt(SysParam.CAMERA_MODE);
        camera_mode.setText(getCameraMode(cameraMode));
        findViewById(R.id.rl_switch_camera).setOnClickListener(this);
        if (ScanCodeUtils.getNumberOfCameras() < 2) {
            findViewById(R.id.rl_switch_camera).setVisibility(View.GONE);
        }
    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected void loadParam() {

    }

    @Override
    protected void handleMsg(Message msg) {
        switch (msg.what){
            case TIP_TIME:
                String time = (String)msg.obj;
                if (!TextUtils.isEmpty(time))
                    tVtime.setText(time);

                if (Integer.valueOf(time) == 0) {
                    if (clearDialog != null)
                        clearDialog.dismiss();
                    ActivityStack.getInstance().pop();
                }
                break;
            case CLEAR_TIP:
                DialogSure dialogSure = new DialogSure(this);
                dialogSure.setSucessLogo();
                dialogSure.setTitle("Clear Record");
                dialogSure.setContent("Successful");
                dialogSure.tickTimerStart(8);
                dialogSure.show();
                break;
            case CHEXK_PASS:
                final int type  =  msg.arg1;

                if (dialogEditSureCancel != null) {
                    dialogEditSureCancel.dismiss();
                    dialogEditSureCancel = null;
                }
                dialogEditSureCancel = new DialogEditSureCancel(SettingOtherManageActivity.this);
                dialogEditSureCancel.setHint(getString(R.string.set_security_code_please));
                dialogEditSureCancel.setTitle(getString(R.string.set_security_code));
                dialogEditSureCancel.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                dialogEditSureCancel.setMaxlenth(8);
                dialogEditSureCancel.setMyListener(new IkeyListener() {
                    @Override
                    public void onConfirm(String text) {
                        if (!TextUtils.isEmpty(text)) {
                            String sec_pwd = TopApplication.sysParam.get(SysParam.SEC_SECPWD);
                            if (text.equals(sec_pwd)) {
                                if (type == 0) {
                                    boolean b = DaoUtilsStore.getInstance().getmDupTransDaoUtils().deleteAll();
                                    AppLog.d(TAG,"ssss onConfirm ,getmDupTransDaoUtils().deleteAll(): " + b);
                                } else {
                                    boolean b1 = DaoUtilsStore.getInstance().getmTransDaoUtils().deleteAll();
                                    boolean b2 = DaoUtilsStore.getInstance().getmTotaTransdata().deleteAll();
                                    AppLog.d(TAG,"ssss onConfirm ,getmTransDaoUtils().deleteAll()： " + b1  + " ,getmTotaTransdata().deleteAll()： " + b2);
                                }
                                handler.sendEmptyMessage(CLEAR_TIP);
                            }else {
                                TopToast.showFailToast(SettingOtherManageActivity.this, getString(R.string.set_security_code_err));
                            }
                        }
                    }

                    @Override
                    public void onCancel(int res) {

                    }
                });

                dialogEditSureCancel.show();

                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            tickTimerStop();
            ActivityStack.getInstance().pop();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private  void switchCamera() {
        ActionMultiSelect actionModSelect = new ActionMultiSelect(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                int mode  = TopApplication.sysParam.getInt(SysParam.CAMERA_MODE);
                ((ActionMultiSelect)action).setParam(handler,getString(R.string.select_camera_mode),new String[]{"Main Camera","Second Camera"},mode);
            }
        }) ;
        actionModSelect.setEndListener(new AAction.ActionEndListener() {
            @Override
            public void onEnd(AAction action, ActionResult result) {
                if (result.getRet() != TransResult.SUCC) {
                    return;
                }
                int oldMode  = TopApplication.sysParam.getInt(SysParam.CAMERA_MODE);
                final int mode = Integer.valueOf((String) result.getData()) ;
                if (oldMode == mode) {
                    return;
                }
                TopApplication.sysParam.set(SysParam.CAMERA_MODE,mode);
                camera_mode.setText(getCameraMode(mode));
            }
        });
        actionModSelect.execute();
    }

    private  void chooseMode() {
        ActionMultiSelect actionModSelect = new ActionMultiSelect(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                int mode = TopApplication.sysParam.getInt(SysParam.DEVICE_MODE);
                ((ActionMultiSelect)action).setParam(handler,getString(R.string.select_mode), new String[]{"POS","BT","USB"},mode);
            }
        }) ;
        actionModSelect.setEndListener(new AAction.ActionEndListener() {
            @Override
            public void onEnd(AAction action, ActionResult result) {
                    if (result.getRet() != TransResult.SUCC) {
                        return;
                    }
                    int oldMode  = TopApplication.sysParam.getInt(SysParam.DEVICE_MODE);
                    final int mode = Integer.valueOf((String) result.getData()) ;
                    if (oldMode == mode) {
                        return;
                    }
                    TopUsdkManage.getInstance().close();
                    TopApplication.usdkManage.setMode(mode);
                    TopApplication.sysParam.set(SysParam.DEVICE_MODE,mode);
                    TopUsdkManage.getInstance().init(TopApplication.mApp,(ret) -> {
                        tvMode.setText(getShowMode(mode));
                        TopApplication.switchModeInit();
                        TopApplication.injectKeys();
                    });

            }
        });
        actionModSelect.execute();
    }

    private DialogEditSureCancel dialogEditSureCancel;

    /**
     *
     * @param listener
     * @param title
     * @param hint
     * @param connet
     * @param type InputType.TYPE_CLASS_NUMBER
     * @param maxlen
     */
    private void setParam(final SettingCommManageActivity.OnListener listener, String title, String hint, String connet, int type, int maxlen) {
        if (dialogEditSureCancel != null) {
            dialogEditSureCancel.dismiss();
            dialogEditSureCancel = null;
        }
        dialogEditSureCancel = new DialogEditSureCancel(SettingOtherManageActivity.this);
        dialogEditSureCancel.setMaxlenth(maxlen);
        dialogEditSureCancel.setTitle(title);
        dialogEditSureCancel.setInputType(type);
        dialogEditSureCancel.setHint(hint);
        dialogEditSureCancel.setConnent(connet);

        dialogEditSureCancel.setMyListener(new IkeyListener() {
            @Override
            public void onConfirm(String text) {
                if (!TextUtils.isEmpty(text)) {
                    listener.onCallBack(text);
                }
            }

            @Override
            public void onCancel(int res) {
                listener.onCallBack("");
            }
        } );
        dialogEditSureCancel.show();
    }

    private  String getShowMode(int mode) {
        switch (mode) {
            case 1:
                return "BT";
            case 2:
                return "USB";
            case 0:
            default:
                return "POS";
        }
    }

    private String getCameraMode(int mode) {
        switch (mode) {
            case 1:
                return "Second Camera";
            case 0:
            default:
                return "Main Camera";
        }
    }
}
