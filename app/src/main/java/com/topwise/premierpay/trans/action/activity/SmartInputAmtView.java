package com.topwise.premierpay.trans.action.activity;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.topwise.manager.AppLog;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.view.TopToast;

import net.sourceforge.jeval.Evaluator;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmartInputAmtView extends FrameLayout implements View.OnClickListener {
    private static final String TAG = TopApplication.APPNANE +SmartInputAmtView.class.getSimpleName();
    private static final String INTEGER_ZERO_AMOUNT = "0";      // 0
    private static final String FLOAT_ZERO_AMOUNT = "0.00";     // 0.00
    private static final String DECIMAL_POINT = ".";            // 小数点
    private static final String REGEX_POINT = "\\.";            // 规则表达式小数点
    private static final double MAX_AMOUNT = 999999999.00;      // 最大金额
    private static final String EXP_DELIMITER = " ";            // 表达式分隔符
    private static final int MAX_DIGITS = 9;                    // 最大位数

    private Context parentContext;
    private TextView tv_total,tv_third, tv_hint; //  tv_first, tv_second,
    private DecimalFormat df = new DecimalFormat(FLOAT_ZERO_AMOUNT);
    private Evaluator evaluator = new Evaluator();
    private String zeroAmount;
    private String moneyPrefix;
    private String expInput = ""; // 储存需要运算的表达式,格式：运算符前面含空格，运算符为+-x÷，eg：1 +2.00 x1 ÷ 2

    private Boolean quickPay;

    public SmartInputAmtView(Context context) {
        super(context);
        parentContext = context;
    }

    public SmartInputAmtView(Context context, AttributeSet attrs) {
        super(context, attrs);
        parentContext = context;
    }

    public String getAmount() {
        return tv_total.getText().toString();//.replace(moneyPrefix, "");
    }

    /**
     * 快速收银 quickPay
     * @param quick
     */
    public void init(boolean quick) {
        this.quickPay = quick;
        LayoutInflater.from(parentContext).inflate(R.layout.soft_calc_board, this);
//        moneyPrefix = parentContext.getString(R.string.prefix_money); moneyPrefix +
//        zeroAmount = INTEGER_ZERO_AMOUNT;

        tv_total = (TextView) findViewById(R.id.tv_total);
//        tv_first = (TextView) findViewById(R.id.tv_first);
//        tv_second = (TextView) findViewById(R.id.tv_second);
        tv_third = (TextView) findViewById(R.id.tv_third);
        tv_hint = (TextView) findViewById(R.id.textview_amount_consume);

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
        this.findViewById(R.id.btn_point).setOnClickListener(this);
        this.findViewById(R.id.btn_del).setOnClickListener(this);
        this.findViewById(R.id.btn_clear).setOnClickListener(this);
        this.findViewById(R.id.btn_add).setOnClickListener(this);
        this.findViewById(R.id.btn_min).setOnClickListener(this);
        this.findViewById(R.id.btn_mul).setOnClickListener(this);
        this.findViewById(R.id.btn_div).setOnClickListener(this);

        if (quickPay){ //快速收银
//            this.findViewById(R.id.btn_scanpay).setOnClickListener(this);
//            this.findViewById(R.id.btn_unionpay).setOnClickListener(this);
//            this.findViewById(R.id.button_ok).setVisibility(GONE);

        }else { //显示确定
//            this.findViewById(R.id.button_ok).setVisibility(VISIBLE);
//            this.findViewById(R.id.btn_scanpay).setVisibility(GONE);
//            this.findViewById(R.id.btn_unionpay).setVisibility(GONE);
        }


        tv_total.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                String data = s.toString().replace(moneyPrefix, "");
                String data = s.toString();
                AppLog.e("SmartInputAmtView","总金额文本:" + data);
                if (data.equals(INTEGER_ZERO_AMOUNT)
                        || data.equals(FLOAT_ZERO_AMOUNT)
                        || TextUtils.isEmpty(data)
//                        || data.charAt(0) == '-'
                ) {
                    //灰色
                    setEnterStatus(false);
                } else {//蓝色
                    setEnterStatus(true);
                }
            }
        });

        this.findViewById(R.id.btn_del).setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                expInput = "";
                return false;
            }
        });

        clear(true);
    }

    public void setAmountHint(String hintStr) {
        tv_hint.setText(hintStr);
    }

    public void clearAmount() {
        expInput = "";
        clear(true);
    }

    // 设置确认键状态
    protected void setEnterStatus(boolean enable) {
        if (quickPay){
//            View btnEnterscanpay = findViewById(R.id.btn_scanpay);
//            View btnEnterunionpay = findViewById(R.id.btn_unionpay);
//            if (enable) {
//                btnEnterscanpay.setBackgroundResource(R.color.text_pay_type_color);
//                btnEnterunionpay.setBackgroundResource(R.color.text_pay_type_color);
//            } else {
//                btnEnterscanpay.setBackgroundResource(R.color.text_default_light);
//                btnEnterunionpay.setBackgroundResource(R.color.text_default_light);
//            }
//            btnEnterscanpay.setClickable(enable);
//            btnEnterunionpay.setClickable(enable);
        }else {
            View btnEnter = findViewById(R.id.button_ok);
            if (enable) {
                btnEnter.setBackgroundResource(R.color.text_pay_type_color);
            } else {
                btnEnter.setBackgroundResource(R.color.text_default_light);
            }
            btnEnter.setClickable(enable);
        }

    }

    // 清除显示信息
    protected void clear(boolean clearTotal) {
//        tv_first.setText("");
//        tv_second.setText("");
        tv_third.setText("");

        if (clearTotal) {
            tv_total.setText(zeroAmount);
        }
    }

    // 检验数据串里面是否有+-x÷(减号在规则表达式中间需要转义)
    protected boolean isMathOperator(String str) {
        Pattern p = Pattern.compile("[-+x÷]");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    // 检验需要计算的公式的最后一位是不是运算符
    protected boolean isEndWithDigital(String str) {
        Pattern p = Pattern.compile(".*[0-9]$");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    // 表达式是否以运算符(+-*/x÷)结尾(已经转换后的表达式)
    protected boolean isEndWithOperator(String str) {
        Pattern p = Pattern.compile(".+[-+*/x÷]$");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    // 是否是金额0
    protected boolean isOnlyOneZero(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }

        String localStr = str;

        String header = String.valueOf(str.charAt(0));
        if (isMathOperator(header)) {
            localStr = str.replace(header, "");
        }

        String[] partInteger = localStr.split(REGEX_POINT);
        // 只有整数部分并且是0
        return (partInteger.length == 1) && INTEGER_ZERO_AMOUNT.equals(localStr);
    }

    // 判断是否是数字字符
    protected boolean isDigital(char newChar) {
        return (newChar >= '0') && (newChar <= '9');
    }

    // 处理新收入的数字
    // 返回： true - 需要继续处理, false - 文本没有改变，不需要继续处理
    protected boolean dealNewDigitalChar(char newChar) {
        if (!isDigital(newChar)) {  // 运算符或者控制字符
            return true;
        }

        String[] expArray = expInput.split(EXP_DELIMITER);
        if (isOnlyOneZero(expArray[expArray.length - 1])) { // 最后一个表达式(或只有一个表达式)只有一个'0'
            if (newChar == '0') {   // 不能输入多个0
                return false;
            }

            // 替换'0'成非零的数字（最高位的0没有意义）
            expInput = expInput.substring(0, expInput.length() - 1) + newChar;
            return true;
        }

        // 把数字附加在表达式
        expInput += newChar;

        return true;
    }

    // 是否允许输入小数点
    protected boolean allowEnterPoint() {
        String[] expArray = expInput.split(EXP_DELIMITER);
        return !expArray[expArray.length - 1].contains(DECIMAL_POINT);
    }

    // 是否允许输入运算符号
    protected boolean allowEnterOperator() {
        if (TextUtils.isEmpty(expInput)) {
            return false;
        }

        // 小数点后面至少要有1位数字才能接着输入运算符
        if (expInput.endsWith(DECIMAL_POINT)) {
            return false;
        }

        // 如果以运算符结尾了，这时候再输入运算符，则先删除旧的
        if (isEndWithOperator(expInput)) {
            expInput = expInput.substring(0, expInput.length() - 1).trim();
        }

        // 判断是否是第一段表达式（前面没有运算符了）
        String[] expArray = expInput.split(EXP_DELIMITER);
        String header = String.valueOf(expArray[expArray.length - 1].charAt(0));
        if (!isMathOperator(header)) {
            return true;
        }

        // 非第一段表达式，判断是否为：运算符+"."这种形式
        String body = expArray[expArray.length - 1].replace(header, "");
        return !DECIMAL_POINT.equals(body);
    }

    protected void changeText(char newChar) {
        if (!dealNewDigitalChar(newChar)) {
            return;
        }

        if (expInput.isEmpty()) {
            clear(true);
            return;
        }

        //将存储的待计算公式通过【空格】分隔
        String[] expArray = expInput.split(EXP_DELIMITER);
        String headerOfLastItem = String.valueOf(expArray[expArray.length - 1].charAt(0));
        if (isMathOperator(headerOfLastItem)) {  // 有运算符
            String bodyOfLastItem = expArray[expArray.length - 1].replace(headerOfLastItem, "");
            if (DECIMAL_POINT.equals(bodyOfLastItem)) {  // 只有一个"."
                bodyOfLastItem = FLOAT_ZERO_AMOUNT; // 设置成浮点的0金额,以便后续继续输入小数部分
            }

            if (!TextUtils.isEmpty(bodyOfLastItem)) {
                if (Double.valueOf(bodyOfLastItem) > MAX_AMOUNT) {
                    bodyOfLastItem = bodyOfLastItem.substring(0, MAX_DIGITS);
                }

                if (bodyOfLastItem.contains(DECIMAL_POINT)) {         // 有小数点
                    if (!bodyOfLastItem.endsWith(DECIMAL_POINT)) {    // 不以小数点结尾
                        String partInteger = bodyOfLastItem.split(REGEX_POINT)[0];    // 整数部分
                        String partDecimal = bodyOfLastItem.split(REGEX_POINT)[1];    // 小数部分
                        if (partDecimal.length() > 2) {        // 小数点大于2位
                            partDecimal = partDecimal.substring(0, 2);
                            expArray[expArray.length - 1] = headerOfLastItem + partInteger + DECIMAL_POINT + partDecimal;
                            int offset = expInput.lastIndexOf(headerOfLastItem);
                            expInput = expInput.substring(0, offset);
                            expInput = expInput + expArray[expArray.length - 1];
                        }
                    } else {    // 以小数点结尾  即为刚输入小数点
                        String partInteger = bodyOfLastItem.split(REGEX_POINT)[0];
                        if (partInteger.length() > MAX_DIGITS) {
                            expInput = expInput.substring(0, expInput.length() - 1);
                        }
                    }
                } else {    // 没有小数点，仍然要重新拼接(尾部可能已经超限被截断)！
                    int offset = expInput.lastIndexOf(headerOfLastItem);
                    expInput = expInput.substring(0, offset) + headerOfLastItem + bodyOfLastItem;
                }
            }
        } else {
            // 没有运算符的时候(单个表达式)
            if (expInput.contains(DECIMAL_POINT)) { // 有小数点
                if (expInput.endsWith(DECIMAL_POINT)) {
                    String[] expSplit = expInput.split(REGEX_POINT);
                    if (expSplit.length > 0) {
                        String partInteger = expSplit[0];

                        if (partInteger.length() > MAX_DIGITS) {
                            partInteger = partInteger.substring(0, MAX_DIGITS);
                        }

                        expInput = partInteger + DECIMAL_POINT;
                    }
                } else {
                    String partInteger = expInput.split(REGEX_POINT)[0];
                    String partDecimal = expInput.split(REGEX_POINT)[1];
                    if (partDecimal.length() > 2) {
                        partDecimal = partDecimal.substring(0, 2);
                    }

                    expInput = partInteger + DECIMAL_POINT + partDecimal;
                }
            } else {    // 无小数点
                if (expInput.length() > MAX_DIGITS) {
                    expInput = expInput.substring(0, MAX_DIGITS);
                }
            }
        }

        AppLog.e( "expInput ","wwc expInput " +  expInput);
        // 设置1-3部分的显示信息(从下往上倒序显示最末3个表达式)
        String[] displayArray = expInput.split(EXP_DELIMITER);
        clear(false);
        tv_third.setText(expInput.replaceAll( " ","" ));
//        tv_third.setText(displayArray[displayArray.length - 1]);
//        if (displayArray.length >= 2) {
//            tv_third.setText( displayArray[displayArray.length - 2] + displayArray[displayArray.length - 1] );
////            tv_second.setText(displayArray[displayArray.length - 2]);
//        }
//        if (displayArray.length >= 3) {
//            tv_third.setText( displayArray[displayArray.length - 3] + displayArray[displayArray.length - 1] );
////            tv_first.setText(displayArray[displayArray.length - 3]);
//        }

        // 将带空格，x，÷的公式转化为不带空格、*、/的表达式
        String expFinal = expInput.replaceAll(EXP_DELIMITER, "");
        expFinal = expFinal.replaceAll("x", "*");
        expFinal = expFinal.replaceAll("÷", "/");
        AppLog.e("SmartInputAmtView","格式化后的表达式：" + expFinal);

        // 计算总计，并显示
        try {
            String expResult;

            if (isEndWithDigital(expFinal)) {       // 以数字结尾
                expResult = evaluator.evaluate(expFinal);
            } else if (expFinal.endsWith(DECIMAL_POINT)) {    // 以"."结尾
                // 去掉末尾的"."
                String header = expFinal.substring(0, expFinal.length() - 1);
                if (TextUtils.isEmpty(header)) {
                    expResult = INTEGER_ZERO_AMOUNT;
                } else {
                    if (isEndWithOperator(header)) {    // 运算符号(+,-,*,/)紧接着小数点
                        expResult = evaluator.evaluate(header.substring(0, header.length() - 1));
                    } else {
                        expResult = evaluator.evaluate(header);
                    }
                }
            } else {    // 以运算符号(+,-,*,/)结尾
                expResult = evaluator.evaluate(expFinal.substring(0, expFinal.length() - 1));
            }

            Double aDouble = Double.valueOf(expResult);
            if (df.format(Double.valueOf(expResult)).equals("∞")) {
                tv_total.setText(zeroAmount);
            } else if (aDouble > MAX_AMOUNT) {
                TopToast.showFailToast(parentContext,"金额超限");
                tv_total.setText(zeroAmount);
            } else {
                String amt =  df.format(Double.valueOf(expResult)); //moneyPrefix +
                tv_total.setText(amt);
            }
        } catch (Exception e) {
            AppLog.e("SmartInputAmtView","处理格式化后的表达式发生异常:" + expInput + "," + expFinal);
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        //  Device.beepPromt();

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
        } else if (viewId == R.id.btn_point) {
            if (allowEnterPoint()) {
                expInput += DECIMAL_POINT;
                changeText('.');
            }
        } else if (viewId == R.id.btn_del) {
            // 修改显示的有空格的  删除的时候前面有空格就trim掉
            if (expInput.length() > 0) {
                expInput = expInput.substring(0, expInput.length() - 1).trim();
            } else {
                expInput = "";
            }

            changeText((char) 0x0f);
        } else if (viewId == R.id.btn_clear) {
            expInput = "";
            changeText((char) 0x18);
        } else if (viewId == R.id.btn_add) {
            if (allowEnterOperator()) {
                expInput += " +";
                changeText("+".charAt(0));
            }
        } else if (viewId == R.id.btn_min) {
            if (allowEnterOperator()) {
                expInput += " -";
                changeText("-".charAt(0));
            }
        } else if (viewId == R.id.btn_mul) {
            if (allowEnterOperator()) {
                expInput += " x";
                changeText("*".charAt(0));
            }
        } else if (viewId == R.id.btn_div) {
            if (allowEnterOperator()) {
                expInput += " ÷";
                changeText("÷".charAt(0));
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_0) {
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
            changeText((char) 0x0f);
        } else if (keyCode == KeyEvent.KEYCODE_CLEAR) {
            changeText((char) 0x18);
        }

        return super.onKeyDown(keyCode, event);
    }

}
