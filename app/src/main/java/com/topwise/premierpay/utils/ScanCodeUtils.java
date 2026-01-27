package com.topwise.premierpay.utils;

import android.hardware.camera2.CameraAccessException;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.topwise.cloudpos.aidl.camera.AidlCameraScanCode;
import com.topwise.cloudpos.aidl.camera.AidlCameraScanCodeListener;
import com.topwise.cloudpos.data.AidlScanParam;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.param.SysParam;

import android.hardware.camera2.CameraManager;
import android.content.Context;

/**
 * 创建日期：2021/4/22 on 16:10
 * 描述:
 * 作者:wangweicheng
 */
public class ScanCodeUtils extends AidlCameraScanCodeListener.Stub {
    private static final String TAG =  "Jeremy";//TopApplication.APPNANE + ScanCodeUtils.class.getSimpleName();

    public final static int SCAN_CANCEL = -1;
    public final static int SCAN_OUTTIME = -2;
    public final static int SCAN_BUNDLE_ISNULL = -3;
    public final static int SCAN_CAMERA_ISNULL = -4;
    private Bundle bundle;
    private AidlCameraScanCode iScanner;
    private onScanListener onScanListener;
    private boolean hasStop = false;

    private void init() {
        bundle = new Bundle();
        int mode  = TopApplication.sysParam.getInt(SysParam.CAMERA_MODE);

        AidlScanParam param = new AidlScanParam(mode,60, "","","");
        bundle.putSerializable(AidlScanParam.SCAN_CODE, param);
        iScanner = TopApplication.usdkManage.getCameraScan();
    }

    private void init(int outTime) {
        bundle = new Bundle();
        int mode  = TopApplication.sysParam.getInt(SysParam.CAMERA_MODE);
        AidlScanParam param = new AidlScanParam(mode,outTime, "","","");
        bundle.putSerializable(AidlScanParam.SCAN_CODE, param);
        iScanner = TopApplication.usdkManage.getCameraScan();
    }

    private void init(int outTime, String Title, String tip, String hint) {
        bundle = new Bundle();
        int mode  = TopApplication.sysParam.getInt(SysParam.CAMERA_MODE);

        AidlScanParam param = new AidlScanParam(mode,outTime, Title,tip,hint);
        bundle.putSerializable(AidlScanParam.SCAN_CODE, param);
        iScanner = TopApplication.usdkManage.getCameraScan();
    }

    private void init(String Title, String hint) {
        bundle = new Bundle();
        int mode  = TopApplication.sysParam.getInt(SysParam.CAMERA_MODE);
        AidlScanParam param = new AidlScanParam(mode,60, Title,"",hint);
        bundle.putSerializable(AidlScanParam.SCAN_CODE, param);
        iScanner = TopApplication.usdkManage.getCameraScan();
    }

    public static int getNumberOfCameras() {
        CameraManager manager = (CameraManager) TopApplication.mApp.getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] cameraIdList = manager.getCameraIdList();
            return cameraIdList.length;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void startScan() {
        Log.d(TAG,"startScan " );
        if (bundle == null) {
            if (onScanListener != null) {
                onScanListener.onCancel(SCAN_BUNDLE_ISNULL);
            }
            return;
        }
        if (iScanner == null) {
            if (onScanListener != null) {
                onScanListener.onCancel(SCAN_CAMERA_ISNULL);
            }
            return;
        }
        try {
            iScanner.scanCode(bundle,this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public ScanCodeUtils() {
        init();
    }

    public ScanCodeUtils(int outTime) {
        init(outTime);
    }

    public ScanCodeUtils(String Title,String hint) {
        init(Title,hint);
    }

    public ScanCodeUtils(boolean isPreview) {

    }

    public ScanCodeUtils(int outTime,String Title,String tip,String hint) {
        init(outTime,Title,tip,hint);
    }

    @Override
    public void onResult(String s) throws RemoteException {
        Log.d(TAG,"onResult:"+s);
        if (onScanListener != null) {
            onScanListener.onResult(s);
        }
    }

    @Override
    public void onCancel() throws RemoteException {
        Log.d(TAG,"onCancel:");
        if (onScanListener != null) {
            onScanListener.onCancel(SCAN_CANCEL);
        }
    }

    @Override
    public void onError(int i) throws RemoteException {
        Log.d(TAG,"onError:");
        if (onScanListener != null) {
            onScanListener.onCancel(i);
        }
    }

    @Override
    public void onTimeout() throws RemoteException {
        Log.d(TAG,"onTimeout:");
        if (onScanListener != null) {
            onScanListener.onCancel(SCAN_OUTTIME);
        }
    }

    @Override
    public void onPreview(byte[] bytes, int i, int i1) throws RemoteException {

    }

    private void stopScan() {
        Log.d(TAG,"stopScan");
        try {
            if (iScanner != null)
                iScanner.stopScan();

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setOnScanListener(onScanListener onScanListener) {
        this.onScanListener = onScanListener;
    }

    public interface onScanListener{
        void onCancel(int r);
        void onResult(String s);
    }
}
