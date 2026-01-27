package com.topwise.premierpay.trans.receipt;

import android.graphics.Bitmap;
import android.os.ConditionVariable;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;

import com.topwise.cloudpos.aidl.printer.AidlPrinter;
import com.topwise.cloudpos.aidl.printer.AidlPrinterListener;
import com.topwise.manager.AppLog;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.model.TransResult;

/**
 * 创建日期：2021/4/2 on 14:03
 * 描述:
 * 作者:  wangweicheng
 */
public abstract class AReceiptPrint extends AidlPrinterListener.Stub {
    protected PrintListener listener;


    public static final int SUCC_PRINT = 0x99;
    /** 缺纸 */
    public static final int ERROR_PRINT_NOPAPER = 0x01;
    /*** ⾼温 */
    public static final int ERROR_PRINT_HOT = 0x02;
    /*** 未知错误 */
    public static final int ERROR_PRINT_UNKNOWN = 0x03;
    /** 设备未打开 */
    public static final int ERROR_DEV_NOT_OPEN = 0x04;
    /** 设备忙 */
    public static final int ERROR_DEV_IS_BUSY = 0x05;
    /** 打印位图宽度溢出 */
    public static final int ERROR_PRINT_BITMAP_WIDTH_OVERFLOW = 0x06;
    /** 打印位图错误 */
    public static final int ERROR_PRINT_BITMAP_OTHER = 0x07;
    /** 打印条码错误 */
    public static final int ERROR_PRINT_BARCODE_OTHER = 0x08;
    /** 参数错误 */
    public static final int ERROR_PRINT_ILLIGALARGUMENT = 0x09;
    /*** 打印⽂本错误 */
    public static final int ERROR_PRINT_TEXT_OTHER = 0x0A;
    /*** mac 校验错误(当要求对打印数据进⾏防串改校验时) */
    public static final int ERROR_PRINT_DATA_MAC = 0x0B;

    private int errcodr;
    protected int receiptNum;
    public int currrentReceiptNum = 1;
    protected boolean isNext = true;
    private ConditionVariable cv;


    /**
     * 打印 Bitmap
     * @param bitmap
     * @return
     */
    protected int printBitmap(Bitmap bitmap) {
        AidlPrinter printer = TopApplication.usdkManage.getPrinter();
        if (printer == null ) {
            return -1;
        }
        try {
            int printerState = printer.getPrinterState();
            AppLog.d("AReceiptPrint", " printBitmap getPrinterState =" +printerState);

            //打印灰度需要用参数控制 0x01,0x02,0x03,0x04，值越⼤，灰度越深
            int printGray = getPrintGray();
            printer.setPrinterGray(printGray);
            return start(printer,bitmap);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    protected int printStr(String str){
        return TransResult.SUCC;
    }

    private int start(AidlPrinter printer, Bitmap bitmap) throws RemoteException {

        int result;
        while (true){
            cv = new ConditionVariable();
            printer.addRuiImage(bitmap,0);
            printer.printRuiQueue(this);
            cv.block();
            if (SUCC_PRINT == errcodr){
                SystemClock.sleep(200);
                //判断是否再打印下一联
                if (isNext && listener != null && currrentReceiptNum < receiptNum ){
                    result = listener.onConfirmNext(null, "Whether to print the next ?");
                    currrentReceiptNum++;
                    if (result == PrintListener.CONTINUE) {
                        return 0;
                    } else {
                        currrentReceiptNum = 1;
                        return -1;
                    }
                }
                currrentReceiptNum = 1;
                return 0;
            }else if(ERROR_PRINT_NOPAPER == errcodr){
                if (listener != null ){
                    result = listener.onConfirm(null, TopApplication.mApp.getString(R.string.print_paper) +"\n errCode: "+errcodr);
                    if (result == PrintListener.CONTINUE) {
                        continue;
                    }
                }
                return -1;
            }else if(ERROR_PRINT_HOT == errcodr){
                if (listener != null ){
                    result = listener.onConfirm(null, TopApplication.mApp.getString(R.string.print_hot)+"\n errCode: "+errcodr);
                    if (result == PrintListener.CONTINUE) {
                        continue;
                    }
                }
                return -1;
            }else if(ERROR_PRINT_UNKNOWN == errcodr){
                if (listener != null ){
                    result = listener.onConfirm(null, TopApplication.mApp.getString(R.string.print_nukown)+"\n errCode: "+errcodr);
                    if (result == PrintListener.CONTINUE) {
                        continue;
                    }
                }
                return -1;
            }else if(ERROR_DEV_NOT_OPEN == errcodr){
                if (listener != null ){
                    result = listener.onConfirm(null, TopApplication.mApp.getString(R.string.print_device_not_open)+"\n errCode: "+errcodr);
                    if (result == PrintListener.CONTINUE) {
                        continue;
                    }
                }
                return -1;
            }else if(ERROR_DEV_IS_BUSY == errcodr){
                SystemClock.sleep(200);
                continue;
            }else if(ERROR_PRINT_BITMAP_WIDTH_OVERFLOW == errcodr){
                if (listener != null ){
                    result = listener.onConfirm("", "Bitmap width overflow"+"\n errCode: "+errcodr);
                    if (result == PrintListener.CONTINUE) {
                        continue;
                    }
                }
                return -1;
            }else if(ERROR_PRINT_BITMAP_OTHER == errcodr){
                if (listener != null ){
                    result = listener.onConfirm(null, "Bitmap err"+"\n errCode: "+errcodr);
                    if (result == PrintListener.CONTINUE) {
                        continue;
                    }
                }
                return -1;
            }else if(ERROR_PRINT_BARCODE_OTHER == errcodr){
                if (listener != null ){
                    result = listener.onConfirm(null, "Barcode err"+"\n errCode: "+errcodr);
                    if (result == PrintListener.CONTINUE) {
                        continue;
                    }
                }
                return -1;
            }else if(ERROR_PRINT_ILLIGALARGUMENT == errcodr){
                if (listener != null ){
                    result = listener.onConfirm(null, "Parameter err"+"\n errCode: "+errcodr);
                    if (result == PrintListener.CONTINUE) {
                        continue;
                    }
                }
                return -1;
            }else if(ERROR_PRINT_TEXT_OTHER == errcodr){
                if (listener != null ){
                    result = listener.onConfirm(null, "Text err"+"\n errCode: "+errcodr);
                    if (result == PrintListener.CONTINUE) {
                        continue;
                    }
                }
                return -1;
            }else if(ERROR_PRINT_DATA_MAC == errcodr){
                if (listener != null ){
                    result = listener.onConfirm(null, "Data mac err"+"\n errCode: "+errcodr);
                    if (result == PrintListener.CONTINUE) {
                        continue;
                    }
                }
                return -1;
            }else {
                return -1;
            }
        }
    }
    @Override
    public void onError(int i) throws RemoteException {
        errcodr = i;
        if (cv != null) {
            cv.open();
        }
    }

    @Override
    public void onPrintFinish() throws RemoteException {
        errcodr = SUCC_PRINT;
        if (cv != null) {
            cv.open();
        }
    }

    private int getPrintGray(){
        String gray = TopApplication.sysParam.get(SysParam.APP_PRINT_GRAY);
        if (TextUtils.isEmpty(gray))
            gray = "2";

        return Integer.valueOf(gray);
    }
    /**
     * 获取打印
     * @return
     */
    protected int getVoucherNum() {
        int receiptNum = 0;
        String temp = TopApplication.sysParam.get(SysParam.APP_PRINT);
        if (temp != null)
            receiptNum = Integer.parseInt(temp);
        if (receiptNum < 1 || receiptNum > 3) // 打印联数只能为1-3
            receiptNum = 2;

        return receiptNum;
    }

    public void close() {
        listener = null;
    }
}
