package com.topwise.premierpay.trans.model;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;

import com.topwise.cloudpos.aidl.led.AidlLed;
import com.topwise.cloudpos.aidl.pinpad.AidlPinpad;
import com.topwise.cloudpos.aidl.system.AidlSystem;
import com.topwise.cloudpos.data.PinpadConstant;
import com.topwise.manager.AppLog;
import com.topwise.manager.utlis.DataUtils;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.beep.BeepHelper;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.utils.ConfiUtils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Device {
    private static final String TAG =  TopApplication.APPNANE + Device.class.getSimpleName();

    /**
     * mac密钥索引
     */
    public final static byte INDEX_TAK = 0x01;

    /**
     * pin密钥索引
     */
    public static final byte INDEX_TPK = 0x03;

    /**
     * des密钥索引
     */
    public static final byte INDEX_TDK = 0x05;

    public static byte TYPE_X919 = 0x0;

    public static byte TYPE_CUP_ECB = 0x01;


    public static void enableHomeAndRecent(boolean i) {
        try {
            AppLog.e(TAG,"enableHomeAndRecent i = " + i);
            AidlSystem systemManager = TopApplication.usdkManage.getSystem();
            boolean b = systemManager.enableHomeButton(i);
            AppLog.e(TAG,"enableHomeButton " + b);
            b = systemManager.enableRecentAppButton(i);
            AppLog.e(TAG,"enableRecentAppButton " + b);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static String getPosDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        return dateFormat.format(date);
    }

    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return dateFormat.format(date);
    }

    public static long timeConverter(String timeString) {
        if(TextUtils.isEmpty(timeString)){
            return  0l;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            Date date = formatter.parse(timeString);
            long milliseconds = date.getTime();
            return  milliseconds/1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0l;
    }

    /**
     * 获取日期YYYYMMDD
     *
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date(System.currentTimeMillis());
        return dateFormat.format(date);
    }

    /**
     * 获取时间HHMMSS
     *
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss");
        Date date = new Date(System.currentTimeMillis());
        return dateFormat.format(date);
    }

    private static boolean isValidDate(String str) {
        boolean convertSuccess = true;                        //2021 0515 16 09 37
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }

    /**
     * 设置系统时间
     *
     * @param time
     */
    public static void updateSystemTime(String time) {
        if (isValidDate(time)) {
            try {
                boolean b = TopApplication.usdkManage.getSystem().updateSysTime(time);
                AppLog.i(TAG, "posLogon updateSystemTime = " + b);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * write tmk
     * @param tmkValue
     * @return
     */
    public static boolean writeTMK( byte[] tmkValue) {
        String tIndex = TopApplication.sysParam.get(SysParam.MK_INDEX);
        int tmkIndex = Integer.valueOf(tIndex); // Master key index
//        int tmkdex = tmkIndex * 2 + 1;
        int tmkdex = tmkIndex;
        final AidlPinpad pinpadManager = TopApplication.usdkManage.getPinpad(0);
        try {
            return pinpadManager.loadMainkey(tmkdex, tmkValue,null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * PIN KEY
     * @param tpkValue
     * @param tpkKcv
     * @return
     */
    public static boolean writeTPK(byte[] tpkValue, byte[] tpkKcv) {
        String index = TopApplication.sysParam.get(SysParam.MK_INDEX);
//        int tmkdex = Integer.valueOf(index) * 2 + 1; // Master key index
        int tmkdex = Integer.valueOf(index); // Master key index
        final AidlPinpad pinpadManager = TopApplication.usdkManage.getPinpad(0);
        try {
           return pinpadManager.loadWorkKey(PinpadConstant.KeyType.KEYTYPE_PEK, tmkdex, INDEX_TPK, tpkValue, tpkKcv);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * mac KEY
     * @param makValue
     * @param makKcv
     * @return
     */
    public static boolean writeMAK(byte[] makValue, byte[] makKcv){
        String index = TopApplication.sysParam.get(SysParam.MK_INDEX);
//        int tmkdex = Integer.valueOf(index) * 2 + 1;
        int tmkdex = Integer.valueOf(index);
        final AidlPinpad pinpadManager =  TopApplication.usdkManage.getPinpad(0);
        try {
            return pinpadManager.loadWorkKey(PinpadConstant.KeyType.KEYTYPE_MAK, tmkdex, INDEX_TAK, makValue, makKcv);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * tdk KEY
     * @param tdkValue
     * @param tdkKcv
     * @return
     */
    public static boolean writeTDK(byte[] tdkValue, byte[] tdkKcv){
        String index = TopApplication.sysParam.get(SysParam.MK_INDEX);
//        int tmkdex = Integer.valueOf(index) * 2 + 1;
        int tmkdex = Integer.valueOf(index);
        final AidlPinpad pinpadManager = TopApplication.usdkManage.getPinpad(0);
        try {
            return pinpadManager.loadWorkKey(PinpadConstant.KeyType.KEYTYPE_TDK, tmkdex, INDEX_TDK, tdkValue, tdkKcv);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 注入 PIN BDK
     * @param pinValue
     * @param ksn
     * @return
     */
    public static boolean writeBdkPin(int index,byte[] pinValue,byte[] ksn){
        final AidlPinpad pinpadManager = TopApplication.usdkManage.getPinpad(0);
        try {
            return pinpadManager.loadDukptBDK(index,pinValue,ksn);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *  IPEK
     * @param index
     * @param pinValue
     * @param ksn
     * @return
     */
    public static boolean writeIPEKPin(int index,byte[] pinValue,byte[] ksn){
        final AidlPinpad pinpadManager = TopApplication.usdkManage.getPinpad(0);
        try {
            return pinpadManager.loadDukptIPEK(index,pinValue,ksn);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * writeBdkData
     * @param dataValue
     * @param ksn
     * @return
     */
    public static boolean writeBdkData(int index,byte[] dataValue,byte[] ksn){
        final AidlPinpad pinpadManager = TopApplication.usdkManage.getPinpad(0);
//                UsdkContext.getInstance().getUsdkServiceManager().getPinpad(0);
        try {

            return pinpadManager.loadDukptBDK(index,dataValue,ksn);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * writeIPEKData
     * @param dataValue
     * @param ksn
     * @return
     */
    public static boolean writeIPEKData(int index,byte[] dataValue,byte[] ksn){
        final AidlPinpad pinpadManager = TopApplication.usdkManage.getPinpad(0);
        try {

            return pinpadManager.loadDukptIPEK(index,dataValue,ksn);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getVersion(){
        String[] s = TopApplication.version.split("_");
        if (s.length > 1)
            return s[0];
        else
            return "";
    }

    /**
     *      serialNo = "YTEST00000007";//测试
     * @return
     */
    public static String getSn(){
        String serialNo ="";
        try {
            serialNo = TopApplication.usdkManage.getSystem().getSerialNo();
            AppLog.d("getSn","serialNo="+ serialNo);
            if (DataUtils.isNullString(serialNo)) return "";
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return serialNo;
    }

    public static byte []  getHardwareSNCiphertext(byte [] encRdmFactors){
        try {
            return TopApplication.usdkManage.getShellMonitor().getHardwareSNCiphertext(encRdmFactors);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String gerModel(){
        String model = "";
        String manufacture = "";
        try {
            model = TopApplication.usdkManage.getSystem().getModel();
            manufacture = TopApplication.usdkManage.getSystem().getManufacture();

        } catch (RemoteException e) {
            e.printStackTrace();
            model = "";
            manufacture = "";
        }
        return manufacture +" "+model+" ";
    }

    /**
     * Check if the current device has a physical keypad (MP45P or T3)
     * @return true if it's MP45P or T3
     */
    public static boolean isPhysicalKeyDevice() {
        String model = Build.MODEL;
        return "MP45P".equalsIgnoreCase(model) || "T3".equalsIgnoreCase(model);
    }

    /**
     * 获取安全固件版本号
     * @return
     */
    public static String getSecurityDriverVersion(){
        String model = "";
        try {
            model =  TopApplication.usdkManage.getSystem().getSecurityDriverVersion();
        } catch (RemoteException e) {
            e.printStackTrace();
            model = "";
        }

        return model;
    }

    /**
     *获取 ROM 版本(AP 版本号)
     * @return
     */
    public static String getRomVersion(){
        String model = "";
        try {
            model = TopApplication.usdkManage.getSystem().getRomVersion();
        } catch (RemoteException e) {
            e.printStackTrace();
            model = "";
        }

        return model;
    }

    /**
     * 获取 Android 内核版本
     * @return
     */
    public static String getAndroidOsVersion(){
        String model = "";
        try {
            model = TopApplication.usdkManage.getSystem().getAndroidOsVersion();
        } catch (RemoteException e) {
            e.printStackTrace();
            model = "";
        }

        return model;
    }

    public static byte[] calcByTdk(byte[] data){
        byte[]  outputDate = new byte[16];

        final AidlPinpad pinpadManager = TopApplication.usdkManage.getPinpad(0);
        try {
            int ret = pinpadManager.encryptByTdk(ConfiUtils.tdkIndex, (byte) 11, null, data, outputDate);
            AppLog.d("calcDes","encryptByTdk " +ret);
            if (ret != 0){
                return null;
            }
            return outputDate;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 每次使用前，先geu ksn
     * @return
     */
    public static String autoAddPinKsn() {
        final AidlPinpad pinpadManager = TopApplication.usdkManage.getPinpad(0);
        try {
            byte[] dukptKsn = pinpadManager.getDUKPTKsn(ConfiUtils.pinIndex, true);
            if (dukptKsn == null || dukptKsn.length == 0) return null;
            return TopApplication.convert.bcdToStr(dukptKsn);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 每次使用前，先geu ksn
     * @return
     */
    public static String autoAddDataKsn() {
        final AidlPinpad pinpadManager = TopApplication.usdkManage.getPinpad(0);
        try {
            byte[] dukptKsn = pinpadManager.getDUKPTKsn(ConfiUtils.tdkIndex, true);
            if (dukptKsn == null || dukptKsn.length == 0) return null;
            return TopApplication.convert.bcdToStr(dukptKsn);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前SDK版本信息
     *
     * @param
     * @createtor：Administrator
     * @date:2015-8-4 上午9:25:34
     */
    public static String getCurrentSdkVersion() {
        String curSdkVesion = "";
        try {
            AidlSystem aidlSystem = TopApplication.usdkManage.getSystem();
            curSdkVesion = aidlSystem.getCurSdkVersion();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return curSdkVesion;
    }

    //led 操作===========================================================

    /**
     * 所有led灭
     */
    public static void closeAllLed(){
        try {
            //关灯
            AidlLed aidlLed =TopApplication.usdkManage.getLed();
            if(aidlLed == null){
                return;
            }
            boolean led = aidlLed.setLed(Component.ALL, false);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public static void closeRedLed() {
        try {
            //开灯
            boolean led = TopApplication.usdkManage.getLed().setLed(Component.RED, false);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    /**
     *
     */
    public static void closeGreenLed() {
        try {
            //开灯
            boolean led = TopApplication.usdkManage.getLed().setLed(Component.GREEN, false);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void closeYellowLed() {
        try {
            //开灯
            boolean led = TopApplication.usdkManage.getLed().setLed(Component.YELLOW, false);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public static void closeBlueLed() {
        try {
            boolean led = TopApplication.usdkManage.getLed().setLed(Component.BLUE, false);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /**
     * 开灯 all
     */
    public static void openAllLed() {
        try {
            //开灯
            boolean led = TopApplication.usdkManage.getLed().setLed(Component.ALL, true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public static void openRedLed(){
        try {
            //开灯
            boolean led = TopApplication.usdkManage.getLed().setLed(Component.RED, true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public static void openGreenLed(){
        try {
            //开灯
            boolean led = TopApplication.usdkManage.getLed().setLed(Component.GREEN, true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void openYellowLed(){
        try {
            //开灯
            boolean led = TopApplication.usdkManage.getLed().setLed(Component.YELLOW, true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void openBlueLed(){
        try {
            //开灯
            boolean led = TopApplication.usdkManage.getLed().setLed(Component.BLUE, true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //led 操作===========================================================end

    //蜂鸣器
    public static void beepSucc(){
//        try {
//            TopApplication.usdkManage.getBuzzer().beep(1,300);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
        int[] beef = new int[2];
        beef[0] = 1500; //HZ
        beef[1] = 600; //ms
        BeepHelper.getInstance().beef(beef);
    }

    public static void beepFail(){
//        try {
//            AppLog.d(TAG,"beepFail");
//            TopApplication.usdkManage.getBuzzer().beep(2,100);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
        int[] beef = new int[2];
        beef[0] = 750; //HZ
        beef[1] = 200; //ms
        BeepHelper.getInstance().beef(beef);
        SystemClock.sleep(200);
        BeepHelper.getInstance().beef(beef);
    }

    public static void beepErr(){
//        try {
//            AppLog.d(TAG,"beepErr");
//            TopApplication.usdkManage.getBuzzer().beep(4,100);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
        int[] beef = new int[2];
        beef[0] = 750; //HZ
        beef[1] = 200; //ms
        BeepHelper.getInstance().beef(beef);
    }

    public static void beepNormal(){
//        try {
//            AppLog.d(TAG,"beepNormal");
//            TopApplication.usdkManage.getBuzzer().beep(0,100);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
        try {
            BeepHelper.getInstance().beep();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] calcMac(byte[] bytes) {
        byte[] outData = new byte[8];
        final AidlPinpad pinpadManager = TopApplication.usdkManage.getPinpad(0);
        try {
            Bundle mbundle = new Bundle();

            mbundle.putInt("wkeyid",INDEX_TAK);
            mbundle.putByteArray("data",bytes);
//            mbundle.putInt("type", TYPE_CUP_ECB);
            mbundle.putInt("type", PinpadConstant.MacAlg.ANSIX919);

            int mac = pinpadManager.getMac(mbundle, outData);
            if(mac != 0){
                return null;
            }
            return outData;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

}
