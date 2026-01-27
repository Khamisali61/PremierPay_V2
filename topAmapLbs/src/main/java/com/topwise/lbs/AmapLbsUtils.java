package com.topwise.lbs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.util.List;

/**
 * 创建日期：2021/5/19 on 14:51
 * 描述:
 * 作者:wangweicheng
 */
public class AmapLbsUtils{

    private static Context mContext;
    private AMapLocationClient locationClient;
    private AMapLocationClientOption clientOption;
    private ALocation aMapLocation;
    private BaseStationInfo baseStationInfo;
    private AmapLbsUtils() {
        Log.d("onLocationChanged: ", "AmapLbsUtils AmapLbsUtils" );
        initLocation();
    }
    public static AmapLbsUtils getInstance(Context context) {
        mContext = context;
        Log.d("onLocationChanged: ", "AmapLbsUtils getInstance" );
        return SingletonHolder.sInstance;
    }

    public static AmapLbsUtils getInstance() {
        return SingletonHolder.sInstance;
    }
    //静态内部类
    private static class SingletonHolder {

        private static final AmapLbsUtils sInstance = new AmapLbsUtils();
    }
    private void initLocation() {
        Log.d("onLocationChanged: ", "AmapLbsUtils initLocation" );
        locationClient = new AMapLocationClient(mContext);
        locationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation Location) {
                if (Location == null) {
                    return;
                }
                if (Location.getErrorCode() != 0) {
                    // 定位失败：显示错误信息ErrCode是错误码，errInfo是错误信息。
                    Log.e("onLocationChanged",
                            "wwc ErrCode:" + Location.getErrorCode() + ", errInfo:" + Location.getErrorInfo());
                    return;
                }
                aMapLocation = new ALocation(Location.getLatitude(),Location.getLongitude(),Location.getAddress());

                Log.d("onLocationChanged: ", "wwc aMapLocation " +aMapLocation.toString());
            }
        });

        clientOption = new AMapLocationClientOption();
        clientOption.setInterval(30*10*1000);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        clientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置定位间隔,单位毫秒,默认为2000ms 10 000-- 1800000 半个小时
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        //设置定位参数
        locationClient.setLocationOption(clientOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位

    }
    public void startLocation(){
        if (locationClient != null){
            aMapLocation = null;
            baseStationInfo = null;
            Log.e("onLocationChanged",  " 开始 startLocation");
            locationClient.startLocation();
            startStationInfo();
        }

    }
    public void stopLocation(){
        Log.e("onLocationChanged",  " 停止lbs stopLocation");

        if (null != locationClient){
            locationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
        }
    }

    public void destroyLocation() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的， 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            Log.e("onLocationChanged: ", "wwc AmapLbsUtils 销毁了哦");
            locationClient.onDestroy();
            locationClient = null;
            clientOption = null;
        }
    }

    public ALocation getaMapLocation() {
        return aMapLocation;
    }

    private void startStationInfo() {
        try {
            Log.e("onLocationChanged",  " startStationInfo");
            TelephonyManager tel = (TelephonyManager) mContext.getSystemService( Context.TELEPHONY_SERVICE );
            int simState = tel.getSimState();
            if (simState == TelephonyManager.SIM_STATE_ABSENT || simState == TelephonyManager.SIM_STATE_UNKNOWN) {
                // 没有sim卡或者sim不可用
                Log.e("onLocationChanged",  " No sim card or sim is not available");
                return ;
            }
            baseStationInfo = new BaseStationInfo();
            @SuppressLint("MissingPermission")
            CellLocation cel = tel.getCellLocation();
            int phoneType = tel.getPhoneType();
            // MyPhoneStateListener phoneStateListener = new MyPhoneStateListener();
            // tel.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
            baseStationInfo.setPhoneType( phoneType );
            // CDMA 和 GSM 对应不同获取方式
            Log.d( "test", "phoneType: " + phoneType );

            if (cel instanceof CdmaCellLocation) {
                baseStationInfo.setPhoneType( TelephonyManager.PHONE_TYPE_CDMA );
                CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) cel;
                String bid = Integer.toHexString( cdmaCellLocation.getBaseStationId() );
                String nid = Integer.toHexString( cdmaCellLocation.getNetworkId() );
                String sid = Integer.toHexString( cdmaCellLocation.getSystemId() );
                baseStationInfo.setBID( bid );
                baseStationInfo.setNID( nid );
                baseStationInfo.setSID( sid );
                Log.d( "onLocationChanged", "bid:" + bid + " ,nid: " + nid + " sid: " + sid );
            } else {// 移动联通一致
                GsmCellLocation gsmCellLocation = (GsmCellLocation) cel;
                String cid = Integer.toHexString( gsmCellLocation.getCid() );
                String lac = Integer.toHexString( gsmCellLocation.getLac() );
                baseStationInfo.setCid( cid );
                baseStationInfo.setLac( lac );
                Log.d( "onLocationChanged", "cid:" + cid + " ,lac: " + lac );
            }

            // MCC MNC
            String operator = tel.getNetworkOperator();
            int mcc = Integer.parseInt( operator.substring( 0, 3 ) );
            int mnc = Integer.parseInt( operator.substring( 3 ) );

            baseStationInfo.setMcc( mcc );
            baseStationInfo.setMnc( mnc );

            // sig
            int sig = getMobileDbm( mContext );
            String sigStr = "";
            if (sig < 0) {
                String hexString = Integer.toHexString( (sig + 65536) );
                sigStr = hexString.substring( hexString.length() - 4 );
            } else {
                sigStr = Integer.toHexString( sig );
            }
            baseStationInfo.setSig( sigStr );

//            Log.d( "test", "mcc:" + mcc + " ,mnc: " + mnc + " ,sig: " + sig + " ,sigStr: " + sigStr );
            Log.d("onLocationChanged"," baseStationInfo " + baseStationInfo.toString());
        } catch (Exception e) {
            e.printStackTrace();// 没有SIM卡等出错情况
        }

        return ;
    }

    private static final int DBM_DEFAULT = -102;

    /**
     * 获取手机信号强度，需添加权限 android.permission.ACCESS_COARSE_LOCATION <br>
     * API要求不低于17 <br>
     *
     * @return 当前手机主卡信号强度, 单位 dBm（-102是默认值，表示获取失败）
     */
    @SuppressLint({"NewApi", "MissingPermission"})
    public static int getMobileDbm(Context context) {
        int dbm = DBM_DEFAULT;
        TelephonyManager tm = (TelephonyManager) context.getSystemService( Context.TELEPHONY_SERVICE );
        List<CellInfo> cellInfoList;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

            cellInfoList = tm.getAllCellInfo();
            if (null != cellInfoList) {
                for (CellInfo cellInfo : cellInfoList) {
                    if (!cellInfo.isRegistered()) {
                        continue;
                    }
                    if (cellInfo instanceof CellInfoGsm) {
                        CellSignalStrengthGsm cellSignalStrengthGsm = ((CellInfoGsm) cellInfo).getCellSignalStrength();
                        dbm = cellSignalStrengthGsm.getDbm();
                    } else if (cellInfo instanceof CellInfoCdma) {
                        CellSignalStrengthCdma cellSignalStrengthCdma = ((CellInfoCdma) cellInfo)
                                .getCellSignalStrength();
                        dbm = cellSignalStrengthCdma.getDbm();
                    } else if (cellInfo instanceof CellInfoWcdma) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            CellSignalStrengthWcdma cellSignalStrengthWcdma = ((CellInfoWcdma) cellInfo)
                                    .getCellSignalStrength();
                            dbm = cellSignalStrengthWcdma.getDbm();
                        }
                    } else if (cellInfo instanceof CellInfoLte) {
                        CellSignalStrengthLte cellSignalStrengthLte = ((CellInfoLte) cellInfo).getCellSignalStrength();
                        int asuLevel = cellSignalStrengthLte.getAsuLevel();
                        int level = cellSignalStrengthLte.getLevel();
                        dbm = cellSignalStrengthLte.getDbm();
                    }
                }
            }
        }
        dbm = (113+dbm)/2;
        Log.d("onLocationChanged", "getMobileDbm>>dbm:" + dbm);
        if (dbm == DBM_DEFAULT) {

        }
        return dbm;
    }

    public BaseStationInfo getBaseStationInfo() {
        return baseStationInfo;
    }
}
