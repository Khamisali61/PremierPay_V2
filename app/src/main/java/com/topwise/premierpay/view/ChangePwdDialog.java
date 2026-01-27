package com.topwise.premierpay.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.topwise.premierpay.R;

/**
 * 创建日期：2021/3/30 on 17:06
 * 描述:
 * 作者:  wangweicheng
 */
public class ChangePwdDialog extends Dialog {
    public ChangePwdDialog(@NonNull Context context) {
        super(context);
    }
    private TextView tVtitle;
    private EditText eDfirst;
    private EditText eDsenond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View convertView = getLayoutInflater().inflate(R.layout.change_pwd_layout, null);

        setContentView(convertView);
        getWindow().setGravity(Gravity.BOTTOM); // 显示在底部
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);

        initViews(convertView);
    }

    private String sTitle;
    private Context context;
    private int maxlenth;
    private Handler handler;

    private void initViews(View convertView) {
        tVtitle = (TextView)findViewById(R.id.tv_title);
        if (!TextUtils.isEmpty(sTitle)){
            tVtitle.setText(sTitle);
        }
        eDfirst = (EditText)findViewById(R.id.et_first);
        eDsenond = (EditText)findViewById(R.id.et_second);

        showSoftInputFromWindow(eDfirst);

        eDfirst.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxlenth)});
        eDsenond.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxlenth)});
        setCancelable(false);
        ((Button)findViewById(R.id.bt_cnacel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onCancel();
                dismiss();
            }
        });
        ((Button)findViewById(R.id.bt_confirm)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String first = eDfirst.getText().toString();
                String senond = eDsenond.getText().toString();

                if (TextUtils.isEmpty(first)) {
                    TopToast.showFailToast(context,"please enter new password");
                    return;
                }
                if (first.length() != maxlenth) {
                    TopToast.showFailToast(context,"new password length is not equal to " +maxlenth);
                    return;
                }
                if (TextUtils.isEmpty(senond)) {
                    TopToast.showFailToast(context,"please enter retype new password");
                    return;
                }
                if (senond.length() != maxlenth) {
                    TopToast.showFailToast(context,"retype new password length is not equal to " +maxlenth);
                    return;
                }

                if (!first.equals(senond)) {
                    TopToast.showFailToast(context,"new password not equal to retype new password" +maxlenth);
                    return;
                }

                if (listener != null){
                    listener.onSucc(senond);
                }

                dismiss();
            }
        });
    }

    public ChangePwdDialog(Context context, int theme) {
        super(context, theme);
    }

    public ChangePwdDialog(Context context, Handler handler, String sTitle, int maxlenth) {
        //        super(context);
        this(context, R.style.popup_dialog);
        this.context = context;
        this.handler = handler;
        this.sTitle = sTitle;
        this.maxlenth = maxlenth;
    }

    public interface OnListener {
        public void onSucc(String  data);

        public void onCancel();
    }

    private OnListener listener;

    public void setListener(OnListener listener) {
        this.listener = listener;
    }

    public void showSoftInputFromWindow(EditText editText){
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (listener != null)
                listener.onCancel();

            dismiss();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}