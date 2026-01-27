package com.topwise.premierpay.param;

import android.content.Context;
import android.os.Parcelable;

import com.tencent.mmkv.MMKV;
import com.topwise.manager.AppLog;
import com.topwise.premierpay.BuildConfig;
import com.topwise.premierpay.app.TopApplication;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SysParam {
    private static final String TAG = TopApplication.APPNANE + SysParam.class.getSimpleName();
    private static SysParam mSysParam;
    private static Context mContext;

    /**
     * 系统管理员ID
     */
    public static final String OPER_SYS = "99";
    /**
     * 主管ID
     */
    public static final String OPER_MANAGE = "00";

    /******************************************************************
     * 通讯参数
     ******************************************************************/
    public static final String APP_TPDU = "TPDU";
    public static final String APP_COMM_TYPE_SSL = "APP_COMM_TYPE_SSL";
    public static final String COMM_TIMEOUT = "COMM_TIMEOUT";

    public static final String HOSTIP = "HOSTIP";
    public static final String HOSTPORT = "HOSTPORT";

    public static final String APP_COMM_ENABLE_BACKUPS = "APP_COMM_ENABLE_BACKUPS";

    public static final String HOSTIP_BACKUPS = "HOSTIP_BACKUPS";
    public static final String HOSTPORT_BACKUPS = "HOSTPORT_BACKUPS";

    public static final String MK_INDEX = "MK_INDEX";
    public static final String KEY_ALGORITHM = "KEY_ALGORITHM";
    /******************************************************************
     * 密码管理
     ******************************************************************/
    public static final String SEC_SYSPWD = "SEC_SYSPWD";
    public static final String SEC_MNGPWD = "SEC_MNGPWD";
    public static final String SEC_SECPWD = "SEC_SECPWD";
    /******************************************************************
     * 系统参数
     ******************************************************************/
    public static final String TRANS_NO = "TRANS_NO";
    public static final String BATCH_NO = "BATCH_NO";

    /******************************************************************
     * 商户参数
     ******************************************************************/
    public static final String MERCH_ID = "MERCH_ID";
    public static final String PARAM_MCC = "PARAM_MCC";
    public static final String TERMINAL_ID = "TERMINAL_ID";
    public static final String MERCH_NAME = "MERCH_NAME";

    /*****************************************************************
     * 其他参数
     ******************************************************************/
    public static final String LOG_ON_PUBKICKEY = "PUBKICKEY";
    public static final String TICHET_ID = "Ticket_ID";
    public static final String OTHTC_TRACK_ENCRYPT = "OTHTC_TRACK_ENCRYPT";

    //测试
//    public static final String TEST_PIN = "测试密码";

    public static final String APP_PRINT = "APP_PRINT";
    public static final String APP_PRINT_GRAY = "APP_PRINT_GRAY";
    /**
     * 交易输密控制
     */
    public static final String IPTC = "IPTC";
    public static final String IPTC_VOID = "IPTC_VOID";
    public static final String IPTC_PAVOID = "IPTC_PAVOID";
    public static final String IPTC_PACVOID = "IPTC_PACVOID";
    public static final String IPTC_PAC = "IPTC_PAC";

    public static final String QUICK_PASS_TRANS_PIN_FREE_SWITCH = "QUICK_PASS_TRANS_PIN_FREE_SWITCH";
    public static final String QUICK_PASS_TRANS_PIN_FREE_AMOUNT = "QUICK_PASS_TRANS_PIN_FREE_AMOUNT";

    /**
     * 交易刷卡控制
     */
    public static final String UCTC = "UCTC";
    public static final String UCTC_VOID = "UCTC_VOID";
    public static final String UCTC_PAVOID = "UCTC_PAVOID";
    public static final String UCTC_PACVOID = "UCTC_PACVOID";

    public static final String REVERSL_CTRL = "REVERSL_CTRL";

    //TEST Cvm limit, Contactless limit, Floor Limit
    public static final String CVM_LIMIT = "CVM_LIMIT";
    public static final String CONTACTLESS_LIMIT = "CONTACTLESS_LIMIT";
    public static final String FLOOR_LIMIT = "FLOOR_LIMIT";
    /**
     * tga l   val
     * 001 008 DEMO.jpg //Logo Name
     * 002 012 RBL Bank Ltd //Receipt Header 1
     * 003 012 TOPWISE Test //Receipt Header 2
     * 004 002 MH  //Receipt Header 3
     * 005 002 10  //Tip Percentage
     * 006 008 10017458  //Terminal ID
     * 007 015 107112000076119 //Merchant ID
     * 008 001 1 //Last four Digit Prompt (DefaultEnabled)
     * 009 012 000005000000 //contactless transaction limit
     * 010 012 000000000000  //contactless floor limit
     * 011 012 000000200000 //contactless cvm limit
     * 012 012 000000000000  //Floor limit
     * 013 001 0  //line encryption
     * 014 015 111111111111111  menu
     * 015 057 https://europa-sandbox.perseuspay.com/io/v1.0/h2hpayments //ip
     * */
    public static final String PARAM_LOGO_NAME = "LogoName";
    public static final String PARAM_RECEIPT_HEADER_1 = "ReceiptHeader1";
    public static final String PARAM_RECEIPT_HEADER_2 = "ReceiptHeader2";
    public static final String PARAM_RECEIPT_HEADER_3 = "ReceiptHeader3";
    public static final String PARAM_TIP_PERCENTAGE= "TipPercentage";
    public static final String PARAM_LFDP= "LastFourDigitPrompt";
    public static final String PARAM_CTRANS_LIMIT = "contactlessTransactionLimit";
    public static final String PARAM_CFLOOR_LIMIT = "contactlessFloorLimit";
    public static final String PARAM_CCVM_LIMIT = "contactlessCvmLimit";
    public static final String PARAM_FLOOR_LIMIT = "Floorlimit";
    public static final String PARAM_URL = "HostUrl";
    public static final String PARAM_URL_PORT = "HostUrlPort";
    public static final String PARAM_MENU_CONTROL = "PARAM_MENU_CONTROL";
    public static final String PARAM_LINE_ENCERYPTION = "lineEncryption"; //加密快关

    public static final String PARAM_ELEC_SIGN = "PARAM_ELEC_SIGN";
    public static final String PARAM_FINGERPRINT = "PARAM_FINGERPRINT";

    public static final String APP_VERSION = "APP_VERSION";
    public static final String APP_TEST_CONTROL = "APP_TEST_CONTROL"; //Y 是测试环境 N 是生产环境参数
    public static final String TMS_PARAM_URL = "TMS_PARAM_URL";

    public static final String COMMUNICATION_MODE = "COMMUNICATION_MODE";

    public static final String PCI_MODE = "PCI_MODE";
    public static final String APP_PARAM_TRANS_CURRENCY_NAME = "APP_PARAM_TRANS_CURRENCY_NAME"; //(5F2A)Transaction Currency Code
    public static final String APP_PARAM_TRANS_CURRENCY_SYMBOL = "APP_PARAM_TRANS_CURRENCY_SYMBOL"; //

    public static final String APP_PARAM_TER_COUNTRY_CODE = "APP_PARAM_TER_COUNTRY_CODE"; //(9F1A)Terminal Country Code
    public static final String APP_PARAM_TRANS_CURRENCY_CODE = "APP_PARAM_TRANS_CURRENCY_CODE"; //(5F2A)Transaction Currency Code
    public static final String APP_PARAM_TER_CAP = "APP_PARAM_TER_CAP"; //(9F33)Terminal Capabilities

    public static final String APP_PARAM_SUP_BYPASS = "APP_PARAM_SUP_BYPASS";
    public static final String BLUETOOTH_MODE = "BLUETOOTH_MODE";
    public static final String DEVICE_MODE = "DEVICE_MODE";   // 0： "POS" ,1： "BT",2："USB"
    public static final String CAMERA_MODE = "CAMERA_MODE";   // 0： main back  ,1： second or front

    public static final String AUTO_IN_MDB = "AUTO_IN_MDB";
    public static final String INF_DATA = "INF_DATA";
    public static final String LOCATION_INFO = "LOCATION_INFO";
    public static final String CITY_INFO = "CITY_INFO";
    public static final String STATE_CODE = "STATE_CODE";
    public static final String COUNTRY_CODE = "COUNTRY_CODE";
    public static final String TMS_PARAM_STR = "TMS_PARAM_STR";

    private static MMKV mkv;

    private SysParam() {
//      mkv = MMKV.defaultMMKV();
        mkv = MMKV.mmkvWithID("SysParam");
        load();
    }

    public static SysParam getInstance(Context context) {
        mContext = context;
        return SingletonHolder.sInstance;
    }

    // 静态内部类
    private static class SingletonHolder {
        private static final SysParam sInstance = new SysParam();
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key
     * @param object
     */
    public static void set(String key, Object object) {
        if (object instanceof String) {
            mkv.encode(key, (String) object);
        } else if (object instanceof Integer) {
            mkv.encode(key, (Integer) object);
        } else if (object instanceof Boolean) {
            mkv.encode(key, (Boolean) object);
        } else if (object instanceof Float) {
            mkv.encode(key, (Float) object);
        } else if (object instanceof Long) {
            mkv.encode(key, (Long) object);
        } else if (object instanceof Double) {
            mkv.encode(key, (Double) object);
        } else if (object instanceof byte[]) {
            mkv.encode(key, (byte[]) object);
        } else {
            mkv.encode(key, object.toString());
        }
    }

    public static void set(String key, Set<String> sets) {
        mkv.encode(key, sets);
    }

    public static void set(String key, Parcelable obj) {
        mkv.encode(key, obj);
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     */
    public static Integer GetInteger(String key) {
        return mkv.decodeInt(key, 0);
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object get(String key, Object defaultObject) {
        if (defaultObject instanceof String) {
            return mkv.decodeString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return mkv.decodeInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return mkv.decodeBool(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return mkv.decodeFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return mkv.decodeLong(key, (Long) defaultObject);
        } else if (defaultObject instanceof Double) {
            return mkv.decodeDouble(key, (Double) defaultObject);
        } else if (defaultObject instanceof byte[]) {
            return mkv.decodeBytes(key, (byte[]) defaultObject);
        }
        return defaultObject;
    }

    public static Double GetDouble(String key) {
        return mkv.decodeDouble(key, 0.00);
    }

    public static Long GetLong(String key) {
        return mkv.decodeLong(key, 0L);
    }

    public static Boolean GetBoolean(String key) {
        return mkv.decodeBool(key, false);
    }

    public static Float GetFloat(String key) {
        return mkv.decodeFloat(key, 0F);
    }

    public static byte[] GetBytes(String key) {
        return mkv.decodeBytes(key);
    }

    public static String GetString(String key) {
        return mkv.decodeString(key, "");
    }

    public static Set<String> GetStringSet(String key) {
        return mkv.decodeStringSet(key, Collections.<String>emptySet());
    }

    public static Parcelable GetParcelable(String key, Class clz) {
        return mkv.decodeParcelable(key, clz);
    }

    /**
     * 移除某个key对
     *
     * @param key
     */
    public static void removeKey(String key) {
        mkv.removeValueForKey(key);
    }

    /**
     * 移除多个key对
     *
     * @param key
     */
    public static void removeKeys(String[] key) {
        mkv.removeValuesForKeys(key);
    }

    /**
     * 获取全部key对
     */
    public static String[] getAllKeys() {
        return mkv.allKeys();
    }

    /**
     * 含有某个key
     *
     * @param key
     * @return
     */
    public static boolean hasKey(String key) {
        return mkv.containsKey(key);
    }

    /**
     * 含有某个key
     *
     * @param key
     * @return
     */
    public static boolean have(String key) {
        return mkv.contains(key);
    }

    /**
     * 清除所有key
     */
    public static void clearAll() {
        mkv.clearAll();
    }

    /**
     * 获取操作对象
     *
     * @return
     */
    public static MMKV getMkv() {
        return mkv;
    }

    /**
     * 如果指定的数与参数相等返回0。
     * 如果指定的数小于参数返回 -1。
     * 如果指定的数大于参数返回 1。
     */
    private static void checkNewVersion() {
        String AppVersion = mkv.decodeString(APP_VERSION,"");
        AppLog.d(TAG,"sysparm checkNewVersion ==");
        String[] sVersion = TopApplication.version.split("_");
        if (sVersion.length >= 2) { // 根据版本号，确定是否更新参数
            if (sVersion[0].compareTo(AppVersion) > 0) {
                AppLog.d(TAG,"sysparm checkNewVersion update");
                mkv.encode(SysParam.APP_VERSION, sVersion[0]); // 同步版本号
            }
        }
    }

    // First load default parameters, and save APP version.
    private static void load() {
        // 比较版本号 更新版本，只更新对应的key
        Boolean version = mkv.containsKey(APP_VERSION);
        // 如果存在版本号，则直接return
        if (version) {
            AppLog.d(TAG,"sysparm haved ==");
            checkNewVersion();
            return;
        }

        // If no version stored, then load initial parameters.
        AppLog.d(TAG,"sysparm load default parameters===");

        mkv.encode(HOSTIP,BuildConfig.HOST);
        mkv.encode(HOSTPORT,BuildConfig.PROT);
        mkv.encode(MERCH_ID, BuildConfig.MERCHANT); // 商户号898650170110218
        mkv.encode(TERMINAL_ID, BuildConfig.TERMINAL); // 终端号65546881
        mkv.encode(PARAM_MCC, "0081"); // MCC
        mkv.encode(MERCH_NAME, "Premier Bank"); // 中文商户名
        mkv.encode(APP_PRINT, "2"); //打印
        mkv.encode(APP_PRINT_GRAY, "3"); //打印灰度
        mkv.encode(APP_PARAM_TER_COUNTRY_CODE, "0404"); //
        mkv.encode(APP_PARAM_TRANS_CURRENCY_CODE, "0404"); //
        mkv.encode(APP_PARAM_TRANS_CURRENCY_NAME, "Kenyan Shilling"); //
        mkv.encode(APP_PARAM_TRANS_CURRENCY_SYMBOL, "KSh"); //
        mkv.encode(IPTC_PAVOID, "Y");//PIN for Void
        mkv.encode(UCTC_PAVOID, "Y");//Check card for Void
        mkv.encode(PARAM_ELEC_SIGN, "N"); //
        mkv.encode(APP_PARAM_TER_CAP, "E0F8C8"); //

        mkv.encode(INF_DATA, "62000000");
        mkv.encode(LOCATION_INFO, "NAIROBIEXPRESS TERMINAL");
        mkv.encode(CITY_INFO, "NAIROBI");
        mkv.encode(STATE_CODE, "KE");
        mkv.encode(COUNTRY_CODE, "KE");
        mkv.encode(TMS_PARAM_STR, "");
        mkv.encode(SEC_SYSPWD, "88888888"); // 系统管理员密码
        mkv.encode(SEC_MNGPWD, "888888");// 主管密码
        mkv.encode(SEC_SECPWD,  "88888888"); // 安全密码
        mkv.encode(APP_TPDU, "6006010000");
        mkv.encode(COMMUNICATION_MODE, true);
        mkv.encode(COMM_TIMEOUT, "60");
        mkv.encode(REVERSL_CTRL, "3");
        mkv.encode(HOSTIP_BACKUPS,BuildConfig.HOST);
        mkv.encode(HOSTPORT_BACKUPS,BuildConfig.PROT);
        mkv.encode(APP_COMM_ENABLE_BACKUPS, "N");
        mkv.encode(MK_INDEX, "1");
        mkv.encode(KEY_ALGORITHM,Constant.TRIP_DES);
        mkv.encode(TICHET_ID, "0000000000");

        // 系统参数
        mkv.encode(TRANS_NO, "000001"); // 流水号
        mkv.encode(BATCH_NO, "000001"); // 批次号

        mkv.encode(OTHTC_TRACK_ENCRYPT, "N"); // 磁道加密

        mkv.encode(IPTC_VOID, "Y"); //PIN for Void
        mkv.encode(IPTC_PACVOID, "Y");

        mkv.encode(UCTC_VOID, "Y");//Check card for Void
        mkv.encode(UCTC_PACVOID, "Y");

        mkv.encode(QUICK_PASS_TRANS_PIN_FREE_SWITCH,"N");
        mkv.encode(QUICK_PASS_TRANS_PIN_FREE_AMOUNT,"7500");

        mkv.encode(CVM_LIMIT,"5000");
        mkv.encode(CONTACTLESS_LIMIT,"9999999999");
        mkv.encode(FLOOR_LIMIT,"0");

        mkv.encode(APP_TEST_CONTROL,"Y");
        mkv.encode(TMS_PARAM_URL,BuildConfig.TMSURL);
        mkv.encode(APP_VERSION,"00");
        mkv.encode(PARAM_FINGERPRINT, "N"); //

        mkv.encode(PCI_MODE, false);


        mkv.encode(APP_PARAM_SUP_BYPASS, "Y"); //
        mkv.encode(DEVICE_MODE,0); //
        mkv.encode(CAMERA_MODE,0); //
        mkv.encode(AUTO_IN_MDB, "N"); //
    }

    private static Set<String> booleanKeyMap = new HashSet<String>() {
        private static final long serialVersionUID = 1L;
        {
            add(COMMUNICATION_MODE);
            add(PCI_MODE);
        }
    };

    private static Set<String> intKeyMap = new HashSet<String>() {
        private static final long serialVersionUID = 1L;
        {
            add(DEVICE_MODE); //
            add(CAMERA_MODE); //
        }
    };

    private static Set<String> stringKeyMap = new HashSet<String>() {
        private static final long serialVersionUID = 1L;
        {
            add(APP_TPDU); //
            add(COMM_TIMEOUT); //
            add(MK_INDEX); //
            add(KEY_ALGORITHM); //
            add(SEC_SYSPWD); //
            add(SEC_MNGPWD); //
            add(SEC_SECPWD); //
            add(MERCH_ID); //
            add(PARAM_MCC); //
            add(MERCH_NAME); //
            add(TERMINAL_ID); //
            add(TRANS_NO); // 流水号
            add(BATCH_NO); // 批次号
            add(HOSTIP); //
            add(HOSTPORT); //

            add(LOG_ON_PUBKICKEY);
            add(TICHET_ID);
            add(OTHTC_TRACK_ENCRYPT);

//            add(TEST_PIN);

            add(PARAM_LOGO_NAME);
            add(PARAM_TIP_PERCENTAGE);
            add(PARAM_CCVM_LIMIT);
            add(PARAM_CFLOOR_LIMIT);
            add(PARAM_CTRANS_LIMIT);
            add(PARAM_FLOOR_LIMIT);
            add(PARAM_LFDP);
            add(PARAM_RECEIPT_HEADER_1);
            add(PARAM_RECEIPT_HEADER_2);
            add(PARAM_RECEIPT_HEADER_3);
            add(PARAM_URL);
            add(PARAM_URL_PORT);
            add(PARAM_MENU_CONTROL);
            add(PARAM_LINE_ENCERYPTION);
            add(APP_PRINT);

            add(APP_PRINT_GRAY);

            add(IPTC_VOID);
            add(IPTC_PAVOID);
            add(IPTC_PACVOID);

            add(QUICK_PASS_TRANS_PIN_FREE_SWITCH);
            add(QUICK_PASS_TRANS_PIN_FREE_AMOUNT);

            add(UCTC_VOID);
            add(UCTC_PACVOID);
            add(UCTC_PAVOID);

            add(HOSTIP_BACKUPS);
            add(HOSTPORT_BACKUPS);

            add(APP_COMM_ENABLE_BACKUPS);

            add(REVERSL_CTRL);


            add(CVM_LIMIT);
            add(CONTACTLESS_LIMIT);
            add(FLOOR_LIMIT);

            add(APP_VERSION);
            add(APP_TEST_CONTROL);
            add(TMS_PARAM_URL);

            add(PARAM_ELEC_SIGN);
            add(PARAM_FINGERPRINT);

            add(APP_PARAM_TER_COUNTRY_CODE);
            add(APP_PARAM_TER_CAP);
            add(APP_PARAM_TRANS_CURRENCY_CODE);
            add(APP_PARAM_SUP_BYPASS);
            add(APP_PARAM_TRANS_CURRENCY_NAME);
            add(APP_PARAM_TRANS_CURRENCY_SYMBOL);
            add(INF_DATA);
            add(LOCATION_INFO);
            add(CITY_INFO);
            add(STATE_CODE);
            add(COUNTRY_CODE);
            add(TMS_PARAM_STR);
        }
    };

    public synchronized String get(String name) {
        String value = null;
        if (stringKeyMap.contains(name)) {
            value = mkv.decodeString(name, "");
        } else if (booleanKeyMap.contains(name)) {
            boolean b = mkv.decodeBool(name, false);
            value = (b ? "Y" : "N");
        } else {
            value = mkv.decodeString(name, "");
        }
        return value;
    }

    public synchronized boolean getBool(String name) {
        String value = null;
        if (stringKeyMap.contains(name)) {
            value = mkv.decodeString(name, "");
            return  "Y".equals(value);
        } else if (booleanKeyMap.contains(name)) {
            return mkv.decodeBool(name, false);
        }
        return false;
    }

    public synchronized int getInt(String name){
        return mkv.decodeInt(name, 0);
    }

    public static boolean isParamMMKVFileExist() {
        String dir = "/data/data/" + mContext.getPackageName() + File.separator + "files/mmkv/mmkv.default";
        File file = new File(dir);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    public static class Constant {
        /**
         * 对应于肯定值, 是\支持\等
         */
        public static final String YES = "Y";
        // 对应于否定值, 否\不支持\等
        public static final String NO = "N";
        /**
         * des算法
         */
        public static final String DES = "des";
        public static final String TRIP_DES = "3des";
    }
}
