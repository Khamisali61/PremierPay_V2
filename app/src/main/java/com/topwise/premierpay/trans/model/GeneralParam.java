package com.topwise.premierpay.trans.model;

import com.tencent.mmkv.MMKV;
import com.topwise.premierpay.app.TopApplication;

/**
 * 创建日期：2021/5/15 on 16:15
 * 描述:
 * 作者:wangweicheng
 */
public class GeneralParam {
    private static final String TAG =  TopApplication.APPNANE + GeneralParam.class.getSimpleName();
    // PIN密钥
    public static final String TPK = "TPK";
    // MAC密钥
    public static final String TAK = "TAK";
    // DES密钥
    public static final String TDK = "TDK";

    private static MMKV mkv;

    private GeneralParam() {
        mkv = MMKV.mmkvWithID("GeneralParam");
    }

    public static GeneralParam getInstance() {
        return GeneralParam.SingletonHolder.sInstance;
    }

    //静态内部类
    private static class SingletonHolder {
        private static final GeneralParam sInstance = new GeneralParam();
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
    public static String GetString(String key) {
        return mkv.decodeString(key, "");
    }

}
