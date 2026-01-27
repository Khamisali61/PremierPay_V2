package com.topwise.premierpay.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.ConditionVariable;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

/**
 * 创建日期：2021/6/22 on 15:46
 * 描述:
 * 作者:wangweicheng
 */
public class NetWorkUtils {
    public static boolean isNet = true;

    public static enum netType {
        wifi, CMNET, CMWAP, noneNet
    }

    /**
     * @方法说明:判断WIFI网络是否可用
     * @方法名称:isWifiConnected
     * @param context
     * @return
     * @返回值:boolean
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager mgrConn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mgrTel = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
                .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
                .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }

    /**
     * @方法说明:判断MOBILE网络是否可用
     * @方法名称:isMobileConnected
     * @param context
     * @return
     * @返回值:boolean
     */
    public static boolean isMobileConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        if (networkINfo != null
                && networkINfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    /**
     * @方法说明:获取当前网络连接的类型信息
     * @方法名称:getConnectedType
     * @param context
     * @return
     * @返回值:int
     */
    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }

    /**
     * @方法说明:获取当前的网络状态 -1：没有网络 1：WIFI网络2：wap 网络3：net网络
     * @方法名称:getAPNType
     * @param context
     * @return
     * @返回值:netType
     */
    public static netType getAPNType(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType.noneNet;
        }

        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
                return netType.CMNET;
            } else {
                return netType.CMWAP;
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            return netType.wifi;
        }
        return netType.noneNet;
    }

    /**
     * @方法说明:判断是否有网络连接
     * @方法名称:isNetworkConnected
     * @param context
     * @return
     * @返回值:boolean
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * @方法说明:网络是否可用
     * @方法名称:isNetworkAvailable
     * @param context
     * @return
     * @返回值:boolean
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager mgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = mgr.getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            return activeNetworkInfo.isConnected();
        }
        return false;
    }


    public static void switchNet(Context context,int type) {
        if (type == 0) {
            type =NetworkCapabilities.TRANSPORT_WIFI;
        } else {
            type =NetworkCapabilities.TRANSPORT_CELLULAR;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            final ConditionVariable cv = new ConditionVariable();
            final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            builder.addTransportType(type);
            NetworkRequest request = builder.build();

            ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    if (Build.VERSION.SDK_INT >= 23) {
                        connectivityManager.bindProcessToNetwork(network);
                    }
                    connectivityManager.unregisterNetworkCallback(this);
                    cv.open();
                };

            };
            connectivityManager.requestNetwork(request, callback);
            cv.block();
        }
    }

    /**
     * @方法说明:判断是否是手机网络
     * @方法名称:is3GNet
     * @param context
     * @return
     * @返回值:boolean
     */
    public static boolean is3GNet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    private static String mobileNetworkSignal = "";

    public static void startMobileSignal(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephonyManager != null) {
            mTelephonyManager.listen(new PhoneStateListener() {

                @Override
                public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                    super.onSignalStrengthsChanged(signalStrength);
                    int asu = signalStrength.getGsmSignalStrength();
                    int lastSignal = -113 + 2 * asu;
                    mobileNetworkSignal = lastSignal + "dBm";
                }
            }, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        }
    }

    public static String getMobileSignal(){
        return mobileNetworkSignal;
    }

    public static String getWifSignal(Context context) {
        WifiManager wifi_service = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifi_service.getConnectionInfo();
        return wifiInfo.getRssi()+"dBm";
    }
}
