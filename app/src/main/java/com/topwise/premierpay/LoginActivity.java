package com.topwise.premierpay;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.topwise.kdialog.DialogSureCancel;
import com.topwise.kdialog.IkeyListener;
import com.topwise.manager.AppLog;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.app.BaseActivity;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.daoutils.CommonDaoUtils;
import com.topwise.premierpay.daoutils.DaoUtilsStore;
import com.topwise.premierpay.daoutils.entity.Operator;
import com.topwise.premierpay.menu.OperMenuActivity;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.utils.Utils;
import com.topwise.premierpay.view.TopToast;
import com.topwise.premierpay.setting.activity.SettingActivity;
import com.topwise.premierpay.trans.core.TransContext;
import com.topwise.premierpay.trans.model.Controller;
import com.topwise.premierpay.trans.model.EUIParamKeys;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = TopApplication.APPNANE + LoginActivity.class.getSimpleName();

    private TextView mTextSvn;
    private EditText edtOperId;
    private EditText edtOperPwd;
    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.oper_confirm:
                process();
                break;
            default:
                break;
        }
    }

    private void process() {
        String operId = edtOperId.getText().toString().trim();
        String operPwd = edtOperPwd.getText().toString().trim();
        if (operId == null || operId.length() == 0) {
            edtOperId.setFocusable(true);
            edtOperId.requestFocus();
            return;
        }

        if (operPwd == null || operPwd.length() == 0) {
            edtOperPwd.setFocusable(true);
            edtOperPwd.requestFocus();
            return;
        }

        // 系统管理员登录
        if (operId.equals(SysParam.OPER_SYS)) {
            if (operPwd.equals(TopApplication.sysParam.get(SysParam.SEC_SYSPWD))) {
                TransContext.getInstance().setOperID(operId);

                Bundle bundle = new Bundle();
                Intent intent = new Intent(this, SettingActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                ActivityStack.getInstance().pop();
            } else {
                setPwdEditEmpty();
                TopToast.showFailToast(LoginActivity.this, getString(R.string.oper_pwd_is_err));

            }
            return;
        }

        // 主管登录
        if (operId.equals(SysParam.OPER_MANAGE)) {
            if (operPwd.equals(TopApplication.sysParam.get(SysParam.SEC_MNGPWD))) {
                Bundle bundle = new Bundle();
                bundle.putString(EUIParamKeys.NAV_TITLE.toString(), getString(R.string.oper_manage));
                Intent intent = new Intent(this, OperMenuActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                ActivityStack.getInstance().pop();
            } else {
                setPwdEditEmpty();
                TopToast.showFailToast(LoginActivity.this, getString(R.string.oper_pwd_is_err));
            }
            return;
        }
        CommonDaoUtils<Operator> operatorCommonDaoUtils = DaoUtilsStore.getInstance().getmUOperatorDaoUtils();
        Operator operator = operatorCommonDaoUtils.queryByOper(Operator.class, operId);
        // 操作员登录
        if (operator == null) {
            setEdiEmpty();
            TopToast.showFailToast(LoginActivity.this, getString(R.string.oper_is_not_exist));
            return;
        }
        if (!operator.getPwd().equals(operPwd)) {
            setPwdEditEmpty();
            TopToast.showFailToast(LoginActivity.this, getString(R.string.oper_pwd_is_err));
            return;
        }

        TopApplication.controller.set(Controller.OPERATOR_LOGON_STATUS, Controller.Constant.YES);
        TopApplication.controller.set(Controller.CUR_OPERID, operId);
//        topApplication.generalParam.set(GeneralParam.CUR_OPERID, operId);
        ActivityStack.getInstance().pop();
    }

    private void setEdiEmpty() {
        edtOperId.setText("");
        edtOperPwd.setText("");
        edtOperId.requestFocus();
    }

    private void setPwdEditEmpty() {
        edtOperPwd.setText("");
        edtOperPwd.setFocusable(true);
        edtOperPwd.requestFocus();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViews() {
        mTextSvn = (TextView)findViewById(R.id.svn_id);

        if (!TextUtils.isEmpty(TopApplication.version))
            mTextSvn.setText(TopApplication.version);

        edtOperId = (EditText) findViewById(R.id.oper_id_edt);

        edtOperId.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                AppLog.i(TAG,"onKey " + keyCode);
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    process();
                    return true;
                }
                return false;
            }
        });
        showSoftInputFromWindow(edtOperId);
        edtOperPwd = (EditText) findViewById(R.id.oper_pwd_edt);
        edtOperPwd.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                AppLog.i(TAG,"onKey pwd " + keyCode);
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    process();
                    return true;
                }
                return false;
            }
        });
        btnConfirm = (Button) findViewById(R.id.oper_confirm);
//        mEditPW.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
//        mEditPW.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {}
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                String temp = s.toString();
//                if (mEditUserId.getText().toString().equals(UtilsParam.TEST_managerId)) {
//                    if (temp.length() > 8) {
//                        mEditPW.setText(temp.substring(0, temp.length()-1));
//                        mEditPW.setSelection(temp.length()-1);
//                    }
//                } else {
//                    if (temp.length() > 4) {
//                        mEditPW.setText(temp.substring(0, temp.length()-1));
//                        mEditPW.setSelection(temp.length()-1);
//                    }
//                }
//            }
//        });
    }

    @Override
    protected void setListeners() {
        edtOperId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 2) {
                    if (s.toString().equals(SysParam.OPER_MANAGE) || s.toString().equals(SysParam.OPER_SYS)) {
                        edtOperPwd.requestFocus();
                        return;
                    }
                    CommonDaoUtils<Operator> operatorCommonDaoUtils = DaoUtilsStore.getInstance().getmUOperatorDaoUtils();
                    Operator operator = operatorCommonDaoUtils.queryByOper(Operator.class, s.toString());
                    if (operator == null) {
                        TopToast.showFailToast(LoginActivity.this, getString(R.string.oper_is_not_exist));
                        edtOperId.setText("");
                        edtOperId.requestFocus();
                        return;
                    }
                    edtOperPwd.requestFocus();
                }
            }
        });

        edtOperPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String operId = edtOperId.getText().toString().trim();

                if (operId.equals(SysParam.OPER_SYS)) {
                    edtOperPwd.setFilters(new InputFilter[] { new InputFilter.LengthFilter(8) });
                } else if (operId.equals(SysParam.OPER_MANAGE)) {
                    edtOperPwd.setFilters(new InputFilter[] { new InputFilter.LengthFilter(6) });
                } else {
                    edtOperPwd.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4) });
                }
            }
        });
        btnConfirm.setOnClickListener(this);
    }

    @Override
    protected void loadParam() {

    }

    @Override
    protected void handleMsg(Message msg) {

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 退出当前应用
//            DialogUtils.showExitAppDialog(LoginActivity.this);
            DialogSureCancel dialogSureCancel = new DialogSureCancel(this);
            dialogSureCancel.setMyListener(new IkeyListener() {
                @Override
                public void onConfirm(String text) {
                    Utils.exit(LoginActivity.this);
                }
                @Override
                public void onCancel(int ret) {

                }
            });
            dialogSureCancel.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showSoftInputFromWindow(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
}
