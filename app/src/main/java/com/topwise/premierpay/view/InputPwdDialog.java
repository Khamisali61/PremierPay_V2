package com.topwise.premierpay.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.topwise.manager.AppLog;
import com.topwise.premierpay.R;

/**
 * 创建日期：2021/3/29 on 18:59
 * 描述:
 * 作者:  wangweicheng
 */
public class InputPwdDialog extends Dialog implements View.OnClickListener {

    private String expInput = "", disInput = ""; // 储存需要运算的表达式
    private String sHint;
    private String sTitle;
    private TextView tvTotal,tvTitle;
    private int MAX_LEN = 12;
    private Context context;
    private Handler handler;
    private boolean iscipher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View convertView = getLayoutInflater().inflate(R.layout.soft_pwd_board, null);
        setContentView(convertView);

        getWindow().setGravity(Gravity.BOTTOM); // 显示在底部
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);

        initViews(convertView);
    }

    /**
     *
     * @param context
     * @param handler
     * @param sHint
     * @param sTitle
     * @param type 1 主管密码6 位;2 系统管理员
     */
    public InputPwdDialog(@NonNull Context context, Handler handler, String sHint, String sTitle, int type) {
//        super(context);
        this(context, R.style.popup_dialog);
        this.sHint = sHint;
        this.sTitle = sTitle;
        this.context = context;
        this.handler = handler;
        this.iscipher = true;
        if (type == 1) {
            MAX_LEN = 6;
        } else if (type == 2) {
            MAX_LEN = 8;
        } else {
            MAX_LEN = 10;
        }
    }

    public InputPwdDialog(@NonNull Context context, Handler handler, String sHint, String sTitle, int tpye, boolean isCipher) {
//        super(context);
        this(context, R.style.popup_dialog);
        this.sHint = sHint;
        this.sTitle = sTitle;
        this.context = context;
        this.handler = handler;
        if (tpye == 1) {
            MAX_LEN = 6;
        } else if (tpye == 2 || tpye == 4) {
            MAX_LEN = 8;
        } else {
            MAX_LEN = 10;
        }
        this.iscipher = isCipher;
    }

    private void initViews(View convertView) {
//        mInputAmtView = (SmartInputPwdView)findViewById(R.id.viewInputAmount);
//        mInputAmtView.init();

        this.findViewById(R.id.btn0).setOnClickListener(this);
        this.findViewById(R.id.btn1).setOnClickListener(this);
        this.findViewById(R.id.btn2).setOnClickListener(this);
        this.findViewById(R.id.btn3).setOnClickListener(this);
        this.findViewById(R.id.btn4).setOnClickListener(this);
        this.findViewById(R.id.btn5).setOnClickListener(this);
        this.findViewById(R.id.btn6).setOnClickListener(this);
        this.findViewById(R.id.btn7).setOnClickListener(this);
        this.findViewById(R.id.btn8).setOnClickListener(this);
        this.findViewById(R.id.btn9).setOnClickListener(this);

        this.findViewById(R.id.btn_del).setOnClickListener(this);
        this.findViewById(R.id.btn_ccc).setOnClickListener(this);
        this.findViewById(R.id.button_ok).setOnClickListener(this);

        tvTitle = (TextView) findViewById(R.id.tv_title);
        if (!TextUtils.isEmpty(sTitle))
            tvTitle.setText(sTitle);

        tvTotal = (TextView) findViewById(R.id.et_input);
        if (!TextUtils.isEmpty(sHint))
            tvTotal.setHint(sHint);

        this.findViewById(R.id.btn_del).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                expInput = "";
                disInput = "";
                return false;
            }
        });

        setCancelable(false);
    }


    // 清除显示信息
    protected void clear(boolean clearTotal) {
//        tv_third.setText("");
//
//        if (clearTotal) {
//            tv_total.setText(zeroAmount);
//        }
    }

    public InputPwdDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn0) {
            changeText('0');
        } else if (viewId == R.id.btn1) {
            changeText('1');
        } else if (viewId == R.id.btn2) {
            changeText('2');
        } else if (viewId == R.id.btn3) {
            changeText('3');
        } else if (viewId == R.id.btn4) {
            changeText('4');
        } else if (viewId == R.id.btn5) {
            changeText('5');
        } else if (viewId == R.id.btn6) {
            changeText('6');
        } else if (viewId == R.id.btn7) {
            changeText('7');
        } else if (viewId == R.id.btn8) {
            changeText('8');
        } else if (viewId == R.id.btn9) {
            changeText('9');
        } else if (viewId == R.id.btn_del) {
            // 修改显示的有空格的  删除的时候前面有空格就trim掉
            if (expInput.length() > 0) {
                expInput = expInput.substring(0, expInput.length() - 1).trim();
                disInput = disInput.substring(0, disInput.length() - 1).trim();
            } else {
                expInput = "";
                disInput = "";
            }

            changeText((char) 0x0f);
        } else if (viewId == R.id.btn_ccc) {
            expInput = "";
            disInput = "";
            changeText((char) 0x18);
        }else if ( viewId == R.id.button_ok){
            if (TextUtils.isEmpty(expInput)){
                TopToast.showFailToast(context,context.getString(R.string.set_please_enter_pwd));
                return;
            }

            if (listener != null){
                listener.onSucc(expInput);
                dismiss();
            }

        }
    }

    protected void changeText(char newChar) {
        if (expInput.length() >= MAX_LEN)
            return;

        if (!dealNewDigitalChar(newChar)) {
            return;
        }
        AppLog.e("InputPwdDialog","输入的表达式：" + expInput);

        if (expInput.isEmpty()) {
            clear(true);
            return;
        }

        tvTotal.setText(disInput);
//        tvTotal.setText(expInput);
    }


    // 处理新收入的数字
    // 返回： true - 需要继续处理, false - 文本没有改变，不需要继续处理
    protected boolean dealNewDigitalChar(char newChar) {
//        if (!isDigital(newChar)) {  // 运算符或者控制字符
//            return true;
//        }
//
//        String[] expArray = expInput.split(EXP_DELIMITER);
//        if (isOnlyOneZero(expArray[expArray.length - 1])) { // 最后一个表达式(或只有一个表达式)只有一个'0'
//            if (newChar == '0') {   // 不能输入多个0
//                return false;
//            }
//
//            // 替换'0'成非零的数字（最高位的0没有意义）
//            expInput = expInput.substring(0, expInput.length() - 1) + newChar;
//            return true;
//        }
        if (0x18 == newChar) {
            expInput = "";
            disInput = "";
//            tvTotal.setText(expInput);
            tvTotal.setText(disInput);
            return false;
        } else if (0x0f == newChar ) {
            tvTotal.setText(disInput);
//            tvTotal.setText(expInput);
            return false;
        }

        // 把数字附加在表达式
        expInput += newChar;
        if (iscipher) {
            disInput += "*";
        } else {
            disInput += newChar;
        }

        return true;
    }

    public interface OnPwdListener {
        public void onSucc(String data);

        public void onErr();
    }

    private OnPwdListener listener;

    public void setPwdListener(OnPwdListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (listener != null){
                listener.onErr();
                dismiss();
            }

            return true;
        } else if (keyCode == KeyEvent.KEYCODE_0) {
            changeText('0');
        } else if (keyCode == KeyEvent.KEYCODE_1) {
            changeText('1');
        } else if (keyCode == KeyEvent.KEYCODE_2) {
            changeText('2');
        } else if (keyCode == KeyEvent.KEYCODE_3) {
            changeText('3');
        } else if (keyCode == KeyEvent.KEYCODE_4) {
            changeText('4');
        } else if (keyCode == KeyEvent.KEYCODE_5) {
            changeText('5');
        } else if (keyCode == KeyEvent.KEYCODE_6) {
            changeText('6');
        } else if (keyCode == KeyEvent.KEYCODE_7) {
            changeText('7');
        } else if (keyCode == KeyEvent.KEYCODE_8) {
            changeText('8');
        } else if (keyCode == KeyEvent.KEYCODE_9) {
            changeText('9');
        } else if (keyCode == KeyEvent.KEYCODE_DEL) {
            // 修改显示的有空格的  删除的时候前面有空格就trim掉
            if (expInput.length() > 0) {
                expInput = expInput.substring(0, expInput.length() - 1).trim();
                disInput = disInput.substring(0, disInput.length() - 1).trim();
            } else {
                expInput = "";
                disInput = "";
            }
            changeText((char) 0x0f);
        } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
            if (listener != null){
                listener.onSucc(expInput);
                dismiss();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
