package com.topwise.premierpay.operator;

import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.topwise.premierpay.R;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.daoutils.DaoUtilsStore;
import com.topwise.premierpay.daoutils.entity.Operator;
import com.topwise.premierpay.view.TopToast;
import com.topwise.premierpay.trans.action.activity.BaseActivityWithTickForAction;

import java.util.List;

/**
 * 创建日期：2021/4/1 on 11:16
 * 描述:
 * 作者:  wangweicheng
 */
public class OperAddActivity extends BaseActivityWithTickForAction implements View.OnClickListener {
    private static final String TAG = TopApplication.APPNANE + OperAddActivity.class.getSimpleName();
    private TextView tVtime;
    private EditText editTextName;
    private EditText editTextId;
    private EditText editTextPwd;
    private Button btnConfirm;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_confirm:
                String operID = editTextId.getText().toString().trim();
                String operPwd = editTextPwd.getText().toString().trim();
                String operName = editTextName.getText().toString().trim();
                if (!TextUtils.isEmpty(operID) && !TextUtils.isEmpty(operPwd)) {
                    List<Operator> operators = DaoUtilsStore.getInstance().getmUOperatorDaoUtils().queryAll();
                    if (operators != null) {
                        for (int i = 0; i < operators.size(); i++) {
                            if (operID.equals(operators.get(i).getOperId())) {
                                TopToast.showFailToast(this, getString(R.string.oper_exist));
                                return;
                            }
                        }
                    }
                    Operator oper = new Operator(operID, operPwd, operName);
                    boolean save = DaoUtilsStore.getInstance().getmUOperatorDaoUtils().save(oper);
                    if (save) {
                        TopToast.showScuessToast(getString(R.string.add_success_end));
                    }
                    tickTimerStop();
                    ActivityStack.getInstance().pop();
                }
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.add_oper_layout;
    }

    @Override
    protected void initViews() {
        ((TextView)findViewById(R.id.header_title)).setText(getString(R.string.add_oper));
        tVtime = (TextView)findViewById(R.id.header_time);
        editTextName = (EditText)findViewById(R.id.et_input_name);
        editTextId   = (EditText)findViewById(R.id.et_input_operid);
        editTextPwd  = (EditText)findViewById(R.id.et_input_pwd);
        btnConfirm  = (Button) findViewById(R.id.bt_confirm);
        btnConfirm.setEnabled(false);
        setEditListener(editTextName);
        setEditListener(editTextId);
        setEditListener(editTextPwd);

        editTextId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String operID = editTextId.getText().toString().trim();
                String operPwd = editTextPwd.getText().toString().trim();
                if (!TextUtils.isEmpty(operID) && !TextUtils.isEmpty(operPwd) ){
                    btnConfirm.setEnabled(true);
                } else {
                    btnConfirm.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String operID = editTextId.getText().toString().trim();
                String operPwd = editTextPwd.getText().toString().trim();
                if (!TextUtils.isEmpty(operID) && !TextUtils.isEmpty(operPwd)) {
                    btnConfirm.setEnabled(true);
                } else {
                    btnConfirm.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void setListeners() {
        btnConfirm.setOnClickListener(this);
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
                    ActivityStack.getInstance().pop();
                }

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

    private void setEditListener(EditText editText) {
        editText.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                    return true;
                }
                return false;
            }
        });
    }
}
