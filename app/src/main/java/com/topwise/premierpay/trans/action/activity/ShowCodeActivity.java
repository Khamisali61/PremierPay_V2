package com.topwise.premierpay.trans.action.activity;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.trans.model.EUIParamKeys;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.utils.QRCodePrintUtil;
import com.topwise.premierpay.utils.SmallScreenUtil;
import com.topwise.premierpay.utils.Utils;

/**
 * @author Victor(xiedianxin)
 * @brief description
 * @date 2023-08-08
 */
public class ShowCodeActivity extends BaseActivityWithTickForAction {

    private ImageView imageViewQRCode;



    private String navTitle;

    private String amount;

    private TextView mTextAmount;


    private TextView textViewTitle;

    private TextView textTime;

    private Button confirm;

    private ActionResult result;

    Handler handler = new Handler();

    Runnable mTicker = new Runnable() {
        @Override
        public void run() {
            generateQRCode(amount);
        }
    };


    @Override
    protected void initViews() {

        textViewTitle = (TextView) findViewById(R.id.header_title);
        textViewTitle.setText(navTitle.toUpperCase());

        mTextAmount = (TextView) findViewById(R.id.trad_amount);
        if (!TextUtils.isEmpty(amount)) {
            ((RelativeLayout) findViewById(R.id.rl_amount)).setVisibility(View.VISIBLE);
            String curr = TopApplication.sysParam.get(SysParam.APP_PARAM_TRANS_CURRENCY_SYMBOL);
            mTextAmount.setText("Amount  "+ curr + Utils.ftoYuan(amount));
        } else {
            ((RelativeLayout) findViewById(R.id.rl_amount)).setVisibility(View.GONE);
        }

        tickTimerStop();

        textTime = (TextView) findViewById(R.id.header_time);
        imageViewQRCode = findViewById(R.id.imageViewQRCode);

        generateQRCode(amount);

        confirm = (Button) findViewById(R.id.confirm_turn_screen);
        confirm.requestFocus();
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result = new ActionResult(TransResult.ERR_ABORTED, null);
                finish(result);
            }
        });
    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected void loadParam() {
        navTitle = getIntent().getStringExtra(EUIParamKeys.NAV_TITLE.toString());
        amount = getIntent().getStringExtra(EUIParamKeys.CONTENT.toString());

    }
    private void generateQRCode(String amount) {
        try {
            String data = "https://www.topwisesz.com/" + Device.getPosDateTime() + amount;
            Bitmap bitmap = QRCodePrintUtil.createQRImage(data, 708,  604);
            imageViewQRCode.setImageBitmap(bitmap);
            SmallScreenUtil.getInstance().showBitmap(bitmap);
            handler.postDelayed(mTicker, 10 * 1000);
        } catch (Exception e) {
            e.printStackTrace();        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_qr_code;
    }

    @Override    protected void handleMsg(Message msg) {
        if (msg.what == TIP_TIME) {
            String time = (String) msg.obj;
            if (!TextUtils.isEmpty(time)) {
                textTime.setText(time);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(mTicker);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            result = new ActionResult(TransResult.ERR_ABORTED,null);
            finish(result);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
