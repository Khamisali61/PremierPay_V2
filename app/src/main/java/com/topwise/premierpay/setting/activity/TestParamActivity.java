package com.topwise.premierpay.setting.activity;

import android.os.Bundle;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.topwise.kdialog.DialogEditSureCancel;
import com.topwise.kdialog.DialogSureCancel;
import com.topwise.kdialog.IkeyListener;
import com.topwise.manager.AppLog;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.daoutils.DaoUtilsStore;
import com.topwise.premierpay.trans.action.ActionSelectMode;
import com.topwise.premierpay.trans.action.activity.BaseActivityWithTickForAction;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.core.TransContext;
import com.topwise.premierpay.trans.model.TestParam;
import com.topwise.premierpay.trans.model.TransResult;

/**
 * 创建日期：2021/3/31 on 9:48
 * 描述:
 * 作者:  wangweicheng
 */
public class TestParamActivity extends BaseActivityWithTickForAction implements View.OnClickListener {
    private static final String TAG = TopApplication.APPNANE + TestParamActivity.class.getSimpleName();
    private TextView tVtime;
    private TextView tVTotalNum;
    private TextView tVCommType;
    private TextView tVIntervalMode;
    private TextView tVIntervalTime;
    private TextView tVDelayTime;
    private TextView tVEachTime;


    private DialogEditSureCancel dialogEditSureCancel;
    private TestParam testParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tickTimerStart(120);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_set_comm_type:
                 setCommType();
                break;
            case R.id.rl_set_interval_mode:
                setIntervalMode();
                break;
            case R.id.rl_set_interval_time:
                setParam(new OnListener() {
                             @Override
                             public void onCallBack(String data) {
                                 AppLog.i(TAG,"url " + data);
                                 if (!TextUtils.isEmpty(data)) {
                                     tVIntervalTime.setText(data);
                                     testParam.setIntervalTime(Integer.parseInt(data));
                                     DaoUtilsStore.getInstance().saveTestParam(testParam);
                                 }
                             }
                         },getString(R.string.interval_time),
                        getString(R.string.interval_time),
                        testParam.getIntervalTime()+"",
                        InputType.TYPE_CLASS_NUMBER,10);
                break;
            case R.id.rl_set_total_num:
                String totolNum = tVTotalNum.getText().toString();
                setParam(new OnListener() {
                             @Override
                             public void onCallBack(String data) {
                                 AppLog.i(TAG,"url " + data);
                                 if (!TextUtils.isEmpty(data)) {
                                     tVTotalNum.setText(data);
                                     testParam.setTotalNum(Integer.parseInt(data));
                                     DaoUtilsStore.getInstance().saveTestParam(testParam);
                                 }
                             }
                         },getString(R.string.total_test_number),
                        getString(R.string.total_test_number),
                        totolNum,
                        InputType.TYPE_CLASS_NUMBER,10);
                break;
            case R.id.rl_set_delay_time:
                setParam(new OnListener() {
                             @Override
                             public void onCallBack(String data) {
                                 AppLog.i(TAG,"url " + data);
                                 if (!TextUtils.isEmpty(data)) {
                                     tVDelayTime.setText(data);
                                     testParam.setDelayTime(Integer.parseInt(data));
                                     DaoUtilsStore.getInstance().saveTestParam(testParam);
                                 }
                             }
                         },getString(R.string.delay_time),
                        getString(R.string.delay_time),
                        testParam.getDelayTime()+"",
                        InputType.TYPE_CLASS_NUMBER,10);
                break;
            case R.id.rl_clear:
                clearData();
                break;
            case R.id.rl_set_each_time:
                String eachTransTime = tVEachTime.getText().toString();
                setParam(new OnListener() {
                             @Override
                             public void onCallBack(String data) {
                                 AppLog.i(TAG,"each transaction time  " + data);
                                 if (!TextUtils.isEmpty(data)) {
                                     tVEachTime.setText(data);
                                     testParam.setEachTransTime(Integer.parseInt(data));
                                     DaoUtilsStore.getInstance().saveTestParam(testParam);
                                 }
                             }
                         },getString(R.string.each_transaction_number),
                        getString(R.string.each_transaction_number),
                        eachTransTime,
                        InputType.TYPE_CLASS_NUMBER,2);
                break;


            default:
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test_param_layout;
    }

    @Override
    protected void initViews() {
        ((TextView)findViewById(R.id.header_title)).setText("Test Setting");
        tVtime = (TextView)findViewById(R.id.header_time);

        tVTotalNum = (TextView)findViewById(R.id.total_test_number);
        tVTotalNum.setText(testParam.getTotalNum()+"");

        tVCommType = (TextView)findViewById(R.id.tv_comm_type);
        tVCommType.setText(getCommType(testParam.getCommType()));

        tVIntervalMode = (TextView)findViewById(R.id.tv_set_interval_mode);
        tVIntervalMode.setText(getIntervalMode(testParam.getIntervalMode()));

        tVIntervalTime = (TextView)findViewById(R.id.tv_set_interval_time);
        tVIntervalTime.setText(testParam.getIntervalTime()+"");

        tVDelayTime = (TextView) findViewById(R.id.tv_set_delay_time);
        tVDelayTime.setText(testParam.getDelayTime() + "");


        tVEachTime = (TextView) findViewById(R.id.tv_set_each_time);
        tVEachTime.setText(testParam.getEachTransTime() + "");
    }

    public interface OnListener {
        public void onCallBack(String data);
    }

    /**
     *
     * @param listener
     * @param title
     * @param hint
     * @param connet
     * @param type InputType.TYPE_CLASS_NUMBER
     * @param maxlen
     */
    private void setParam(final OnListener listener, String title, String hint, String connet, int type, int maxlen) {
        if (dialogEditSureCancel != null) {
            dialogEditSureCancel.dismiss();
            dialogEditSureCancel = null;
        }
        dialogEditSureCancel = new DialogEditSureCancel(TestParamActivity.this);
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

    @Override
    protected void setListeners() {

    }

    @Override
    protected void loadParam() {
       testParam = DaoUtilsStore.getInstance().getTestParam();
    }

    private final static int UP_BACKS = 0x01;

    @Override
    protected void handleMsg(Message msg) {
        switch (msg.what){
            case TIP_TIME:
                String time = (String)msg.obj;
                if (!TextUtils.isEmpty(time))
                    tVtime.setText(time);

                if (Integer.valueOf(time) == 0) {
                    if (dialogEditSureCancel != null)
                        dialogEditSureCancel.dismiss();
                    ActivityStack.getInstance().pop();
                }

                break;
            case UP_BACKS:

                break;
        }
    }

    private void setIntervalMode() {
        ActionSelectMode  actionSelectMode = new ActionSelectMode(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                String[] selectType = new String[]{"Fixed Interval","Random"};
                ((ActionSelectMode) action).setParam(TestParamActivity.this, handler,"Interval Mode",selectType,testParam.getIntervalMode());

            }
        });
        actionSelectMode.setEndListener(new AAction.ActionEndListener() {
            @Override
            public void onEnd(AAction action, ActionResult result) {
                if (result.getRet()== TransResult.SUCC) {
                    int type = (int)result.getData();
                    tVIntervalMode.setText(getIntervalMode(type));
                    testParam.setIntervalMode(type);
                    DaoUtilsStore.getInstance().saveTestParam(testParam);
                }
            }
        });
        actionSelectMode.execute();
    }

    private void setCommType(){
        ActionSelectMode  actionSelectMode = new ActionSelectMode(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                String[] selectType = new String[]{"Wifi","Mobile","Random"};
               ((ActionSelectMode) action).setParam(TestParamActivity.this, handler,"Communication Type",selectType,testParam.getCommType() );

           }
        });
        actionSelectMode.setEndListener(new AAction.ActionEndListener() {
            @Override
            public void onEnd(AAction action, ActionResult result) {
                if(result.getRet()== TransResult.SUCC) {
                    int type = (int)result.getData();
                    tVCommType.setText(getCommType(type));
                    testParam.setCommType(type);
                    DaoUtilsStore.getInstance().saveTestParam(testParam);
                }
           }
        });
        actionSelectMode.execute();
    }

    private void clearData(){
        DialogSureCancel dialogSureCancel = new DialogSureCancel(this);
        dialogSureCancel.setContent("Clear The Data?");
        dialogSureCancel.setMyListener(new IkeyListener() {
            @Override
            public void onConfirm(String text) {
                DaoUtilsStore.getInstance().getmTransDaoUtils().deleteAll();
                DaoUtilsStore.getInstance().getmDupTransDaoUtils().deleteAll();
                DaoUtilsStore.getInstance().getmTransStatusDaoUtils().deleteAll();
            }
            @Override
            public void onCancel(int ret) {

            }
        });
        dialogSureCancel.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            tickTimerStop();
            ActivityStack.getInstance().pop();
            TopApplication.isRuning =false;
            TransContext.getInstance().setCurrentContext(null);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TopApplication.isRuning = false;
    }

    private String getCommType(int type) {
        switch (type) {
            case 1:
                return "Mobile";
            case 2:
                return "Random";
            case 0:
            default:
                return "Wifi";
        }

    }

    private String getIntervalMode(int type) {
        switch (type) {
            case 1:
                return "Random";
            case 0:
            default:
                return "Fixed Time";
        }
    }
}
