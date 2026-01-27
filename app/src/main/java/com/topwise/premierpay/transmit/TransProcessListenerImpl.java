package com.topwise.premierpay.transmit;

import android.graphics.Bitmap;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;

import com.topwise.cloudpos.aidl.shellmonitor.AidlShellMonitor;
import com.topwise.cloudpos.aidl.shellmonitor.InstructionSendDataCallback;

import com.topwise.kdialog.DialogLoading;
import com.topwise.kdialog.DialogSure;
import com.topwise.kdialog.IkeyListener;

import com.topwise.manager.AppLog;
import com.topwise.toptool.api.convert.IConvert;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.utils.ConfiUtils;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class TransProcessListenerImpl implements TransProcessListener {
//    private TopAlertDialog topAlertDialog;
//    private TopComfirmDialog topComfirmDialog;
    private DialogSure dialogSure;
    private DialogLoading dialogLoading;
    private Handler locHandler;

    private String message;
    private String title;
    private int time;

    public TransProcessListenerImpl() {
        locHandler = createHandler();
    }

    @Override
    public void onShowProgress(String imessage, int itimeout) {
        message = imessage;
        time = itimeout;
        locHandler.sendEmptyMessage(I_SHOW);
    }

    @Override
    public void onUpdateProgressTitle(String ititle) {
        title = ititle;
    }

    @Override
    public void onUpdateMsg(String tip) {
        Message msg = locHandler.obtainMessage();
        msg.what =I_CHANGE_MESS;
        msg.obj = tip;
        locHandler.sendMessage(msg);
    }


    @Override
    public void onHideProgress() {
        locHandler.sendEmptyMessage(I_HIDE);
    }

    @Override
    public int onShowMessageWithConfirm(String imessage, int itimeout) {
        cv = new ConditionVariable();
        message = imessage;
        time = itimeout;
        locHandler.sendEmptyMessage(I_CONFIRM);
        cv.block();
        return 0;
    }

    @Override
    public int onInputOnlinePin(TransData transData) {
        return 0;
    }

    /**
     * 银联mac
     * @param data
     * @return
     */
    @Override
    public byte[] onCalcMac(byte[] data) {
        byte[] tmpbuf = new byte[8];

        /**
         * No need for ANSIx9.19
         */
//        int len;
//        byte[] dataIn = new byte[data.length + tmpbuf.length];
//        len = data.length / tmpbuf.length + 1;
//
//        System.arraycopy(data, 0, dataIn, 0, data.length);

//        for (int i = 0; i < len; i++) {
//            for (int k = 0; k < tmpbuf.length; k++) {
//                tmpbuf[k] ^= dataIn[i * tmpbuf.length + k];
//            }
//        }

        String beforeCalcMacData = TopApplication.convert.bcdToStr(data);
        AppLog.d("onCalcMac", "onCalcMac origin === "+ beforeCalcMacData);
        try {
            byte[] mac = Device.calcMac(data);
            AppLog.d("onCalcMac", "onCalcMac calculated === "+ TopApplication.convert.bcdToStr(mac));
            return mac;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] onEncTrack(byte[] track) {

        byte[] block = null;
        String trackStr = new String();
        int len = track.length;
        int isDouble = 0;
        if (len % 2 > 0) {
            isDouble = 1;
            trackStr = new String(track) + "F";
        } else {
            trackStr = new String(track);
        }

        int ii = len / 16;
        int iii = len % 8 ;
        if (iii > 0){
            ii += 1;
        }

        byte[] trackData = new byte[ii * 8];

        Arrays.fill(trackData, (byte) 0xff);

        byte[] bTrack = TopApplication.convert.strToBcd(trackStr, IConvert.EPaddingPosition.PADDING_LEFT);

        System.arraycopy(bTrack, 0, trackData, 0, bTrack.length );

//        if (bTrack.length - 1 < trackData.length) {
//            System.arraycopy(bTrack, 0, trackData, 0, bTrack.length - 1);
//
//        } else {
//            System.arraycopy(bTrack, bTrack.length - trackData.length - 1, trackData, 0, trackData.length);
//
//        }
        AppLog.d("onEncTrack", "calcByCMD indata === "+ TopApplication.convert.bcdToStr(trackData));
//        block = Device.calcByTdk(trackData);
        block = calcByCMD(trackData);
        if (block == null) return  null;
//        return block;
        return TopApplication.convert.bcdToStr(block).getBytes();
//        if (bTrack.length - 1 < trackData.length) {
//            byte[] data = new byte[trackData.length + 1];
//            System.arraycopy(block, 0, data, 0, trackData.length);
//            System.arraycopy(bTrack, bTrack.length - 1, data, trackData.length, 1);
//
//            if (isDouble == 1) {
//                return topApplication.convert.bcdToStr(data).substring(0, topApplication.convert.bcdToStr(data).length() - 1).getBytes();
//
//            }
//            return topApplication.convert.bcdToStr(data).getBytes();
//        } else {
//            System.arraycopy(block, 0, bTrack, bTrack.length - block.length - 1, block.length);
//            return topApplication.convert.bcdToStr(bTrack).substring(0, len).getBytes();
//
//        }
    }

    @Override
    public int onShowErrorBitmap(Bitmap bitmap, String message, int timeout) {
        return 0;
    }

    @Override
    public int onShowSuccessMsgWithConfirm(String imessage, int itimeout) {
        cv = new ConditionVariable();
        message = imessage;
        time = itimeout;
        locHandler.sendEmptyMessage(I_CONFIRM_SUCESS);
        cv.block();
        return 0;
    }

    @Override
    public int onShowFailWithConfirm(String imessage, int itimeout) {
        cv = new ConditionVariable();
        message = imessage;
        time = itimeout;
        locHandler.sendEmptyMessage(I_CONFIRM_FAIL);
        cv.block();
        return 0;
    }

    private static final int I_SHOW = 0x01;
    private static final int I_HIDE = 0x02;
    private static final int I_CONFIRM = 0x03;
    private static final int I_CONFIRM_FAIL = 0x04;
    private static final int I_CONFIRM_SUCESS = 0x05;
    private static final int I_CHANGE_MESS = 0x06;

    private ConditionVariable cv;

    // 创建handler
    private Handler createHandler() {
        return new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case I_CONFIRM_SUCESS:
                        AppLog.d("TopAlertDialog","I_CONFIRM_SUCESS" );
                        if (dialogLoading != null) {
                            dialogLoading.dismiss();
                            dialogLoading = null;
                        }
                        if (dialogSure != null) {
                            dialogSure.dismiss();
                            dialogSure = null;
                        }

                        dialogSure = new DialogSure(ActivityStack.getInstance().top());
                        dialogSure.setContent(message);
                        if (!TextUtils.isEmpty(title))
                            dialogSure.setTitle(title);
                        dialogSure.setSucessLogo();
                        dialogSure.tickTimerStart(time);
                        dialogSure.show();
                        dialogSure.setMyLietener(new IkeyListener() {
                            @Override
                            public void onConfirm(String text) {
                                if (cv != null)
                                    cv.open();
                            }

                            @Override
                            public void onCancel(int res) {
                                if (cv != null)
                                    cv.open();
                            }
                        });
                        break;
                    case I_CONFIRM_FAIL:
                        AppLog.d("TopAlertDialog","I_CONFIRM_FAIL" );
                        if (dialogLoading != null) {
                            dialogLoading.dismiss();
                            dialogLoading = null;
                        }
                        if (dialogSure != null) {
                            dialogSure.dismiss();
                            dialogSure = null;
                        }

                        dialogSure = new DialogSure(ActivityStack.getInstance().top());
                        dialogSure.setContent(message);
                        if (!TextUtils.isEmpty(title))
                            dialogSure.setTitle(title);
                        dialogSure.setFailLogo();
                        dialogSure.tickTimerStart(time);
                        dialogSure.show();
                        dialogSure.setMyLietener(new IkeyListener() {
                            @Override
                            public void onConfirm(String text) {
                                if (cv != null)
                                    cv.open();
                            }

                            @Override
                            public void onCancel(int res) {
                                if (cv != null)
                                    cv.open();
                            }
                        });
                        break;
                    case I_CONFIRM:
                        AppLog.d("TopAlertDialog","I_CONFIRM" );
                        if (dialogLoading != null) {
                            dialogLoading.dismiss();
                            dialogLoading = null;
                        }
                        AppLog.d("TopComfirmDialog","title " +title+" message " +message + " " +  time);
                        if (dialogSure != null){
                            dialogSure.dismiss();
                            dialogSure = null;
                        }

                        dialogSure = new DialogSure(ActivityStack.getInstance().top());
                        dialogSure.setContent(message);
                        if (!TextUtils.isEmpty(title))
                            dialogSure.setTitle(title);
                        dialogSure.tickTimerStart(time);
                        dialogSure.show();
                        dialogSure.setMyLietener(new IkeyListener() {
                            @Override
                            public void onConfirm(String text) {
                                if (cv != null)
                                    cv.open();
                            }

                            @Override
                            public void onCancel(int res) {
                                if (cv != null)
                                    cv.open();
                            }
                        });
                        break;
                    case I_SHOW:
                        AppLog.d("TopAlertDialog","I_SHOW" );
                        if (dialogSure != null) {
                            dialogSure.dismiss();
                            dialogSure = null;
                        }
                        if (dialogLoading != null) {
                            dialogLoading.dismiss();
                            dialogLoading = null;
                        }
                        if (dialogLoading == null) {
                            AppLog.d("DialogLoading","ActivityStack.getInstance().top() " +ActivityStack.getInstance().top().getLocalClassName() );
                            dialogLoading = new DialogLoading(ActivityStack.getInstance().top());


                            dialogLoading.show();
                        }
                        AppLog.d("TopAlertDialog","title " +title+" message " +message );
//                        topAlertDialog.setIcon(R.mipmap.ic_launcher);
                        if (!TextUtils.isEmpty(title))
                            dialogLoading.setTitle(title);

                        if (!TextUtils.isEmpty(message))
                            dialogLoading.setContent(message);

                            dialogLoading.tickTimerStart(time);
                        break;
                    case I_HIDE:
                        AppLog.d("TopAlertDialog","I_HIDE" );
                        if (dialogLoading != null) {
                            dialogLoading.dismiss();
                            dialogLoading = null;
                        }
                        if (dialogSure != null) {
                            dialogSure.dismiss();
                            dialogSure = null;
                        }
                        break;
                    case I_CHANGE_MESS:
                        AppLog.d("TopAlertDialog","I_CHANGE_MESS" );
                        if (dialogLoading != null){
                            dialogLoading.setContent((String) msg.obj);
                        }
                        break;
                }
            }
        };
    }

    int ret = -1;
    byte [] outData;

    /**
     *#define ALG_NULL                    0
     * #define ALG_ENCRYPT_DES_CBC         1
     * #define ALG_DECRYPT_DES_CBC         2
     * #define ALG_ENCRYPT_DES_ECB         3
     * #define ALG_DECRYPT_DES_ECB         4
     * #define ALG_ENCRYPT_AES_CBC         5
     * #define ALG_DECRYPT_AES_CBC         6
     * #define ALG_ENCRYPT_AES_ECB         7
     * #define ALG_DECRYPT_AES_ECB         8
     * #define ALG_ENCRYPT_SM4_CBC         9
     * #define ALG_DECRYPT_SM4_CBC         10
     * #define ALG_ENCRYPT_SM4_ECB         11
     *
     * #define ALG_DECRYPT_SM4_ECB         12
     * @param data
     * @return
     */
    public byte[] calcByCMD(byte[] data) {
        AidlShellMonitor shellMonitor = TopApplication.usdkManage.getShellMonitor();

        int out = 60;
//        byte mode = 0x03; //ecb
        byte mode = 0x01; //CBC
        byte keyIndex =  22;
//        int randomLen = random.length;
        byte[] iv = new byte[8];
        byte [] data1 = new byte[data.length];
        byte [] dataBuf = new byte[4  + 8 +data.length];

        Arrays.fill(iv, (byte) 0x00);

        dataBuf[0] = mode;
        dataBuf[1] = (byte) ConfiUtils.tdkIndex;
        dataBuf[2] = keyIndex;
        dataBuf[3] = 0;

        System.arraycopy(iv, 0, dataBuf, 4, 8);
        System.arraycopy(data, 0, dataBuf, 4 + 8, data.length);

//        System.arraycopy(data, 0, dataBuf, 4 , data.length);
//        cv = new ConditionVariable();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            shellMonitor.sendIns(out, (byte)0x62, (byte)0x14, (byte) dataBuf.length,dataBuf , new InstructionSendDataCallback.Stub() {

                @Override
                public void onReceiveData(byte resultCode, byte[] tlvArray) throws RemoteException {
                    AppLog.emvd("calcByCMD","resultCode = " + resultCode
                            + TopApplication.convert.bcdToStr(tlvArray));
                    outData = tlvArray;
                    ret = resultCode;
                    countDownLatch.countDown();
//                    if (cv != null) {
//                        cv.open();
//                    }
                }

            });
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            if (cv != null) {
//                cv.block();
//            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (ret != 0){
            return null;
        }
        int len = 0;
        if (outData!= null){
            len = outData.length > data1.length ? data1.length : outData.length;
            System.arraycopy(outData, 0, data1, 0, len);
        }
        AppLog.emvd("calcByCMD == ",len +"   "+ TopApplication.convert.bcdToStr(data1));
        return data1;
    }
}
