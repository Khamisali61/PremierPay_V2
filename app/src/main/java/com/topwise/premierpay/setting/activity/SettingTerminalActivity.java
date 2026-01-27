package com.topwise.premierpay.setting.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.topwise.kdialog.DialogEditSureCancel;
import com.topwise.kdialog.IkeyListener;
import com.topwise.manager.AppLog;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.setting.MCCDataManager;
import com.topwise.premierpay.setting.MCCItem;
import com.topwise.premierpay.view.MCCAdapter;
import com.topwise.premierpay.view.TopToast;
import com.topwise.premierpay.trans.action.activity.BaseActivityWithTickForAction;

import java.util.Arrays;
import java.util.List;

/**
 * 创建日期：2021/3/29 on 16:22
 * 描述:
 * 作者:  wangweicheng
 */
public class SettingTerminalActivity extends BaseActivityWithTickForAction implements View.OnClickListener {
    private static final String TAG = TopApplication.APPNANE + SettingTerminalActivity.class.getSimpleName();
    private TextView tVtime;
    private TextView tVmerchantNumber;
    private TextView tVterminalNumber;
    private TextView tVmerchantName;
    private TextView tVmerchantLocation;
    private TextView tVmerchantCity;
    private TextView tVmerchantState;
    private TextView tVmerchantCountry;
    private TextView tVMcc;
    private Spinner spinner;
    private TextView tVcallTicketid;
    private DialogEditSureCancel dialogEditSureCancel;
    private List<MCCItem> mccList;
    private MCCAdapter adapter;
    private boolean isFirstSelection = true; // 添加标志防止初始自动触发
    private boolean isProgrammaticSelection = false; // 新增标志，标记是否是程序触发的选择

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tickTimerStart(120);
    }

    private static final int CHEXK_PASS = 0x01;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_set_merchant:
                if (dialogEditSureCancel != null){
                    dialogEditSureCancel.dismiss();
                    dialogEditSureCancel = null;
                }
                final String mNumber= tVmerchantNumber.getText().toString();
                setTerminalParam(v.getId(),
                        getString(R.string.set_merchant_num),
                        mNumber,
                        getString(R.string.set_merchant_num_please),
                        InputType.TYPE_CLASS_TEXT,
                        15,
                        tVmerchantNumber);
                break;
            case R.id.rl_set_terminal:
                if (dialogEditSureCancel != null){
                    dialogEditSureCancel.dismiss();
                    dialogEditSureCancel = null;
                }
                final String tNumber= tVterminalNumber.getText().toString();
                setTerminalParam(v.getId(),
                        getString(R.string.set_terminal_num),
                        tNumber,
                        getString(R.string.set_merchant_num_please),
                        InputType.TYPE_CLASS_TEXT,
                        8,
                        tVterminalNumber);
                break;
            case R.id.rl_set_merchant_name:
                final String nNumber= tVmerchantName.getText().toString();
                setTerminalParam(v.getId(),
                        getString(R.string.set_merchant_name),
                        nNumber,
                        getString(R.string.set_merchant_num_please),
                        InputType.TYPE_CLASS_TEXT,
                        40,
                        tVmerchantName);
                break;
            case R.id.rl_set_call_ticket_id:
                final String callTicketId= tVcallTicketid.getText().toString();
                setTerminalParam(v.getId(),
                        getString(R.string.set_call_ticket_id),
                        callTicketId,
                        getString(R.string.set_merchant_num_please),
                        InputType.TYPE_CLASS_NUMBER,
                        10,
                        tVcallTicketid);
                break;
            case R.id.rl_set_merchant_location:
                final String location= tVmerchantLocation.getText().toString();
                setTerminalParam(v.getId(),
                        getString(R.string.set_merchant_location),
                        location,
                        getString(R.string.set_merchant_num_please),
                        InputType.TYPE_CLASS_TEXT,
                        24,
                        tVmerchantLocation);
                break;
            case R.id.rl_set_merchant_city:
                final String city= tVmerchantCity.getText().toString();
                setTerminalParam(v.getId(),
                        getString(R.string.set_merchant_city),
                        city,
                        getString(R.string.set_merchant_num_please),
                        InputType.TYPE_CLASS_TEXT,
                        12,
                        tVmerchantCity);
                break;
            case R.id.rl_set_merchant_state:
                final String state= tVmerchantState.getText().toString();
                setTerminalParam(v.getId(),
                        getString(R.string.set_merchant_state),
                        state,
                        getString(R.string.set_merchant_num_please),
                        InputType.TYPE_CLASS_TEXT,
                        2,
                        tVmerchantState);
                break;
            case R.id.rl_set_merchant_country:
                final String country= tVmerchantCountry.getText().toString();
                setTerminalParam(v.getId(),
                        getString(R.string.set_merchant_country),
                        country,
                        getString(R.string.set_merchant_num_please),
                        InputType.TYPE_CLASS_TEXT,
                        2,
                        tVmerchantCountry);
                break;
            case R.id.rl_set_merchant_mcc:
                final String mcc= tVMcc.getText().toString();
                setTerminalParam(v.getId(),
                        getString(R.string.set_merchant_mcc),
                        mcc,
                        getString(R.string.set_merchant_num_please),
                        InputType.TYPE_CLASS_NUMBER,
                        4,
                        tVMcc);
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_terminal_layout;
    }

    @Override
    protected void initViews() {
        ((TextView)findViewById(R.id.header_title)).setText(getString(R.string.set_terminal_param));
        tVtime = (TextView)findViewById(R.id.header_time);
        spinner = findViewById(R.id.spinner);

        loadMCCData();
        setupSpinner();

        String sysMcc = TopApplication.sysParam.get(SysParam.PARAM_MCC);
        AppLog.d(TAG, "SysParam.PARAM_MCC: " + sysMcc);
        int index = MCCDataManager.findPositionByCodeIncludingRanges(mccList, sysMcc);
        AppLog.d(TAG, "mccList index: " + index);
        spinner.setSelection(index);

        tVmerchantNumber = (TextView)findViewById(R.id.tv_merchant_num);
        tVterminalNumber = (TextView)findViewById(R.id.tv_terminal_num);
        tVmerchantName   = (TextView)findViewById(R.id.tv_merchant_name);
        tVcallTicketid = (TextView)findViewById(R.id.tv_call_ticket_id);
        tVmerchantLocation   = (TextView)findViewById(R.id.tv_merchant_location);
        tVmerchantCity   = (TextView)findViewById(R.id.tv_merchant_city);
        tVmerchantState   = (TextView)findViewById(R.id.tv_merchant_state);
        tVmerchantCountry   = (TextView)findViewById(R.id.tv_merchant_country);
        tVMcc = (TextView)findViewById(R.id.tv_merchant_mcc);
        String temp = TopApplication.sysParam.get(SysParam.MERCH_ID);
        if (!TextUtils.isEmpty(temp)) {
            AppLog.i(TAG,temp);
            tVmerchantNumber.setText(temp);
        }

        temp = TopApplication.sysParam.get(SysParam.TERMINAL_ID);
        if (!TextUtils.isEmpty(temp)) {
            AppLog.i(TAG,temp);
            tVterminalNumber.setText(temp);
        }

        temp = TopApplication.sysParam.get(SysParam.MERCH_NAME);
        if (!TextUtils.isEmpty(temp)) {
            AppLog.i(TAG,temp);
            tVmerchantName.setText(temp);
        }

        temp = TopApplication.sysParam.get(SysParam.TICHET_ID);
        if (!TextUtils.isEmpty(temp)) {
            AppLog.i(TAG,temp);
            tVcallTicketid.setText(temp);
        }

        temp = TopApplication.sysParam.get(SysParam.LOCATION_INFO);
        if (!TextUtils.isEmpty(temp)) {
            AppLog.i(TAG,temp);
            tVmerchantLocation.setText(temp);
        }
        temp = TopApplication.sysParam.get(SysParam.CITY_INFO);
        if (!TextUtils.isEmpty(temp)) {
            AppLog.i(TAG,temp);
            tVmerchantCity.setText(temp);
        }
        temp = TopApplication.sysParam.get(SysParam.STATE_CODE);
        if (!TextUtils.isEmpty(temp)) {
            AppLog.i(TAG,temp);
            tVmerchantState.setText(temp);
        }
        temp = TopApplication.sysParam.get(SysParam.COUNTRY_CODE);
        if (!TextUtils.isEmpty(temp)) {
            AppLog.i(TAG,temp);
            tVmerchantCountry.setText(temp);
        }
        temp = TopApplication.sysParam.get(SysParam.PARAM_MCC);
        if (!TextUtils.isEmpty(temp)) {
            AppLog.i(TAG,temp);
            tVMcc.setText(temp);
        }
    }

    public void loadMCCData() {
        mccList = MCCDataManager.loadMCCData(this);
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
                    if (dialogEditSureCancel != null)
                        dialogEditSureCancel.dismiss();

                    ActivityStack.getInstance().pop();
                }

                break;
            case CHEXK_PASS:
                final String merchant_n = (String) msg.obj;
                final int type  =  msg.arg1;

                if (dialogEditSureCancel != null) {
                    dialogEditSureCancel.dismiss();
                    dialogEditSureCancel = null;
                }
                dialogEditSureCancel = new DialogEditSureCancel(SettingTerminalActivity.this);
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
                                    TopApplication.sysParam.set(SysParam.MERCH_ID, merchant_n);
                                    tVmerchantNumber.setText(merchant_n);
                                } else if (type == 1){
                                    TopApplication.sysParam.set(SysParam.TERMINAL_ID, merchant_n);
                                    tVterminalNumber.setText(merchant_n);
                                } else if (type == 2) {
                                    TopApplication.sysParam.set(SysParam.PARAM_MCC,merchant_n);
                                    tVMcc.setText(merchant_n);
                                }
                            }else {
                                TopToast.showFailToast(SettingTerminalActivity.this, getString(R.string.set_security_code_err));
                            }
                        }
                    }

                    @Override
                    public void onCancel(int res) {
                        resetSpinner();
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

    private void setTerminalParam(final int viewId, String title, final String content, String hint, int intype, final int maxlen, TextView textView) {
        if (dialogEditSureCancel != null) {
            dialogEditSureCancel.dismiss();
            dialogEditSureCancel = null;
        }
        dialogEditSureCancel = new DialogEditSureCancel(SettingTerminalActivity.this);
        dialogEditSureCancel.setHint(hint);
        dialogEditSureCancel.setTitle(title);
        dialogEditSureCancel.setInputType(intype);
        dialogEditSureCancel.setMaxlenth(maxlen);
        dialogEditSureCancel.setConnent(content);

        dialogEditSureCancel.setMyListener(new IkeyListener() {
            @Override
            public void onConfirm(String text) {
                AppLog.i(TAG,text);

                if (content.equals(text))
                    return;

                if (maxlen < text.length())
                    return;

                switch (viewId) {
                    case R.id.rl_set_merchant_name:
                        if (!TextUtils.isEmpty(text)) {
                            TopApplication.sysParam.set(SysParam.MERCH_NAME,text);
                            tVmerchantName.setText(text);
                        }
                        break;
                    case R.id.rl_set_merchant_location:
                        if (!TextUtils.isEmpty(text)) {
                            TopApplication.sysParam.set(SysParam.LOCATION_INFO,text);
                            tVmerchantLocation.setText(text);
                        }
                        break;
                    case R.id.rl_set_merchant_city:
                        if (!TextUtils.isEmpty(text)) {
                            TopApplication.sysParam.set(SysParam.CITY_INFO,text);
                            tVmerchantCity.setText(text);
                        }
                        break;
                    case R.id.rl_set_merchant_state:
                        if (!TextUtils.isEmpty(text)) {
                            TopApplication.sysParam.set(SysParam.STATE_CODE,text);
                            tVmerchantState.setText(text);
                        }
                        break;
                    case R.id.rl_set_merchant_country:
                        if (!TextUtils.isEmpty(text)) {
                            TopApplication.sysParam.set(SysParam.COUNTRY_CODE,text);
                            tVmerchantCountry.setText(text);
                        }
                        break;
                    case R.id.rl_set_call_ticket_id:
                        if (!TextUtils.isEmpty(text)) {
                            TopApplication.sysParam.set(SysParam.TICHET_ID,text);
                            tVcallTicketid.setText(text);
                        }
                        break;
                    case R.id.rl_set_terminal:
                        if (!TextUtils.isEmpty(text)) {
                            Message m = new Message();
                            m.what = CHEXK_PASS;
                            m.obj = text;
                            m.arg1 = 1;
                            handler.handleMessage(m);
                        }
                        break;
                    case R.id.rl_set_merchant:
                        if (!TextUtils.isEmpty(text)) {
                            Message m = new Message();
                            m.what = CHEXK_PASS;
                            m.obj = text;
                            m.arg1 =0;
                            handler.handleMessage(m);
                        }
                        break;
                    case R.id.rl_set_merchant_mcc:
                        if (!TextUtils.isEmpty(text)) {
                            Message m = new Message();
                            m.what = CHEXK_PASS;
                            m.obj = text;
                            m.arg1 =2;
                            handler.handleMessage(m);
                        }
                        break;
                }
            }

            @Override
            public void onCancel(int res) {

            }
        });
        dialogEditSureCancel.show();
    }

    private void setupSpinner() {
        adapter = new MCCAdapter(this, android.R.layout.simple_spinner_item, mccList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AppLog.d(TAG, "position: " + position + " ,mccItem: " + parent.getItemAtPosition(position).toString());
                // 防止初始化的自动选择触发事件
                if (isFirstSelection) {
                    isFirstSelection = false;
                    return;
                }

                MCCItem selectedItem = (MCCItem) parent.getItemAtPosition(position);

                if (selectedItem.isRange()) {
                    // 如果是范围，弹出输入选择对话框
                    showRangeInputDialog(selectedItem);
                } else {
                    // 如果是具体代码，直接使用
                    isProgrammaticSelection = true;
                    handleMCCSelection(selectedItem.getCode(), selectedItem.getDescription());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 什么都不选时的处理
            }
        });
    }
    private void showRangeInputDialog(MCCItem rangeItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter the specific MCC code");
        builder.setMessage("Range: " + rangeItem.getCode() + " - " + rangeItem.getDescription() +
                "\nPlease enter the 4-digit MCC code (" + rangeItem.getMinCode() + " to " + rangeItem.getMaxCode() + "):");

        // 创建输入框
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("For Example: " + rangeItem.getMinCode());
        builder.setView(input);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            String inputCode = input.getText().toString().trim();
            if (isValidMCCCode(inputCode, rangeItem)) {
                String description = MCCDataManager.getDescriptionByCode(mccList, inputCode);
                if ("Unknown MCC".equals(description)) {
                    description = rangeItem.getDescription() + "(Specific code)";
                }
                isProgrammaticSelection = true;
                handleMCCSelection(inputCode, description);
            } else {
                AppLog.d(TAG, "isInvalidMCCCode: ");
                Toast.makeText(SettingTerminalActivity.this, "Invalid MCC code, please select it again", Toast.LENGTH_SHORT).show();
                resetSpinner();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            AppLog.d(TAG, "setNegativeButton: ");
            dialog.cancel();
            resetSpinner();
        });

        builder.setOnCancelListener(dialog -> {
            AppLog.d(TAG, "setOnCancelListener: ");
            resetSpinner();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void resetSpinner() {
        isProgrammaticSelection = false;
        // 对话框取消时重置Spinner
        spinner.setSelection(0);

    }
    private boolean isValidMCCCode(String code, MCCItem rangeItem) {
        if (code.length() != 4) {
            return false;
        }

        try {
            int codeValue = Integer.parseInt(code);
            return codeValue >= rangeItem.getMinCode() && codeValue <= rangeItem.getMaxCode();
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private void handleMCCSelection(String code, String description) {
        // 这里处理最终选择的MCC代码
        Toast.makeText(this, "Selected : " + code + " - " + description, Toast.LENGTH_SHORT).show();

        // 可以在这里保存选择结果或执行其他操作
        saveSelectedMCC(code, description);
    }

    private void saveSelectedMCC(String code, String description) {
        AppLog.d(TAG, "setNegativeButton code: " + code+  " ,description:" + description + " ,isProgrammaticSelection: " +isProgrammaticSelection);
        // 保存选择的MCC代码
        // 例如保存到SharedPreferences或数据库
        if (!TextUtils.isEmpty(code)) {
//            if (isProgrammaticSelection) {
//                Message m = new Message();
//                m.what = CHEXK_PASS;
//                m.obj = code;
//                m.arg1 =2;
//                handler.handleMessage(m);
//            } else {
                TopApplication.sysParam.set(SysParam.PARAM_MCC,code);
//            }
        }
    }

}
