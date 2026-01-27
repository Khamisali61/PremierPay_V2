package com.topwise.premierpay.trans.action.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.topwise.cloudpos.aidl.fingerprint.AidlFingerprint;
import com.topwise.cloudpos.aidl.fingerprint.FingerprintConstant;
import com.topwise.cloudpos.aidl.fingerprint.FingerprintListener;
import com.topwise.cloudpos.aidl.fingerprint.FingerprintParam;
import com.topwise.cloudpos.aidl.fingerprint.FingerprintResult;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.trans.model.EUIParamKeys;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransResult;

import pl.droidsonroids.gif.GifImageView;

public class FingerprintActivity extends BaseActivityWithTickForAction implements View.OnClickListener{
    private static final String TAG = TopApplication.APPNANE + FingerprintActivity.class.getSimpleName();
    private String navTitle;
    private TransData transData;
    private GifImageView ivFingerprint;
    private AidlFingerprint aidlFingerprint;
    private TextView tVtime,tVtitle,tvFingerprint;
    private  static  final int SUCCESS =0;
    private  static  final int FAIL =1;
    private Bitmap fpImage_bitmap =null;

    @Override
    public void onClick(View v) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fingerprint_layout;
    }

    @Override
    protected void initViews() {
        tvFingerprint =(TextView) findViewById(R.id.fingerprint_tv);
        ivFingerprint =(GifImageView) findViewById(R.id.fingerprint_im);
        tVtime = (TextView)findViewById(R.id.header_time);
        tVtitle = (TextView)findViewById(R.id.header_title);
        tVtitle.setText(navTitle.toUpperCase());
    }

    @Override
    protected void setListeners() {
        findViewById(R.id.bt_fingerprint_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivFingerprint.setImageResource(R.mipmap.fingerprint);
                start();
            }
        });

        findViewById(R.id.bt_fingerprint_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fpImage_bitmap != null) {
                    String file = System.currentTimeMillis()+"";
                    finish(new ActionResult(TransResult.SUCC, file));
                } else {
                    finish(new ActionResult(TransResult.ERR_PARAM,null));
                }
            }
        });
        start();
    }

    private void start() {
        if (aidlFingerprint != null) {
            try {
                aidlFingerprint.stop();
                aidlFingerprint.close();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        fpImage_bitmap = null;
        aidlFingerprint = TopApplication.usdkManage.getFingerprint();
        FingerprintParam fingerprintParam = new FingerprintParam();
        fingerprintParam.setTimeOut(60 * 1000);
        fingerprintParam.setThreshold(10);
        fingerprintParam.setLfdLevel(FingerprintConstant.LfdLevel.OFF);
        tvFingerprint.setText(getString(R.string.finger_input_tip));

        try {
            aidlFingerprint.open();
            Thread.sleep(100);
            aidlFingerprint.start(fingerprintParam, new FingerprintListener.Stub() {
                @Override
                public void onSuccess(FingerprintResult fingerprintResult) throws RemoteException {
                    if (fingerprintResult.getFpImgData().length == 92160) {
                        fpImage_bitmap = createBitmap(fingerprintResult.getFpImgData());
                    } else {
                        fpImage_bitmap = BitmapFactory.decodeByteArray(fingerprintResult.getFpImgData(), 0,
                                fingerprintResult.getFpImgData().length);
                    }
                    Device.beepSucc();
                    handler.sendMessage(handler.obtainMessage(SUCCESS, fpImage_bitmap)) ;
                }

                @Override
                public void onProcess(FingerprintResult fingerprintResult) throws RemoteException {

                }

                @Override
                public void onFail(int i) throws RemoteException {
                    fpImage_bitmap = null;
                    aidlFingerprint.close();
                    Device.beepFail();
                    handler.sendMessage(handler.obtainMessage(FAIL)) ;
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Bitmap createBitmap(byte[] image) {
        if (image == null) {
            return null;
        }
        int length = image.length;
        if (length == 0) {
            return null;
        }
        int[] RGBbits = new int[length];
        for (int i = 0; i < length; i++ ) {
            int v;
            int red=0;
            v = image[i] & 0xff;
            RGBbits[i] = Color.argb(255,v, v, v);

        }
        return Bitmap.createBitmap(RGBbits, 256, 360, Bitmap.Config.ARGB_8888);
    }

    @Override
    protected void loadParam() {
        Bundle bundle = getIntent().getExtras();
        navTitle = getIntent().getStringExtra(EUIParamKeys.NAV_TITLE.toString());
        transData = (TransData) bundle.getSerializable(EUIParamKeys.CONTENT.toString());
        aidlFingerprint = TopApplication.usdkManage.getFingerprint();
    }

    @Override
    protected void handleMsg(Message msg) {
        switch (msg.what) {
            case TIP_TIME:
                String time = (String)msg.obj;
                if (!TextUtils.isEmpty(time)) {
                    tVtime.setText(time);
                }

                if ("0".equals(time)) {
                    handleErr(TransResult.ERR_TIMEOUT);
                }
                break;
            case SUCCESS:
                ivFingerprint.setImageBitmap(fpImage_bitmap);
                tvFingerprint.setText("Get Fingerprint Successfully");
          //    fpImage_bitmap.recycle();
                try {
                    aidlFingerprint.close();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case FAIL:
                ivFingerprint.setImageResource(R.drawable.nofinger);
                tvFingerprint.setText("Get Fingerprint Fail");
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            handleErr(TransResult.ERR_ABORTED);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void handleErr(int error) {
        try {
            aidlFingerprint.stop();
            aidlFingerprint.close();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        ActionResult result = new ActionResult(error, null);
        finish(result);
    }
}
