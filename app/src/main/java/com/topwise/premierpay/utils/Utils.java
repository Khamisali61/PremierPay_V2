package com.topwise.premierpay.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.EditText;

import com.topwise.cloudpos.aidl.pinpad.AidlPinpad;
import com.topwise.manager.AppLog;
import com.topwise.manager.utlis.DataUtils;
import com.topwise.premierpay.param.AidParam;
import com.topwise.premierpay.param.AppCombinationHelper;
import com.topwise.premierpay.param.CapkParam;
import com.topwise.premierpay.param.LoadParam;
import com.topwise.premierpay.param.SysParam;
import com.topwise.toptool.api.convert.IConvert;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.tms.TmsParamDownload;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.view.TopToast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Utils {
    public static String signPhotoPath = Environment.getExternalStorageDirectory() + "/topwise/esign/";

    /**
     * 得到设备屏幕的宽度
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 得到设备屏幕的高度
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 得到设备的密度
     */
    public static float getScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static String Yuntof(String yuan) {
        if (TextUtils.isEmpty(yuan)) return "";
        return TopApplication.convert.amountMajorToMinUnit( yuan, IConvert.ECurrencyExponent.CURRENCY_EXPONENT_2 );
    }

    public static String ftoYuan(String fen) {
        if (TextUtils.isEmpty(fen)) return "";
        return TopApplication.convert.amountMinUnitToMajor ( fen, IConvert.ECurrencyExponent.CURRENCY_EXPONENT_2,true );
    }

    public static String ftoYuan(Long fen) {
        if (fen == null || fen == 0  ) return "0.00";
        if (fen < 0) return "-"+TopApplication.convert.amountMinUnitToMajor ( String.valueOf(Math.abs(fen)), IConvert.ECurrencyExponent.CURRENCY_EXPONENT_2,true );
        return TopApplication.convert.amountMinUnitToMajor ( String.valueOf(fen), IConvert.ECurrencyExponent.CURRENCY_EXPONENT_2,true );
    }

    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

    public static boolean isLowPix(Context context) {
        float width = getScreenWidth(context);
        if (width < 480) {
            return true;
        }
        return false;
    }

    public static  boolean isLargePix(Context context) {
        float height = getScreenHeight(context);
        if (height > 1280) {
            return true;
        }
        return false;
    }

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
     * 解析
     * @param indata
     * @return
     */
    public static Map<String, String> getTlvF48(String indata) {
        Map<String, String> map = new HashMap<String, String>();
        String key = "",lenth = "",vale = "";
        int index = 0,vlen = 0;
        while (index < indata.length()) {
            key = indata.substring(index,index + 3);
            index += 3;
            AppLog.d("getTlvF48","key = " + key + " index= " + index);
            lenth = indata.substring(index,index + 3);
            index += 3;
            AppLog.d("getTlvF48","lenth = " + lenth + " index= " + index);
            vlen = Integer.valueOf(lenth);
            vale = indata.substring(index,index + vlen);
            index += vlen;
            AppLog.d("getTlvF48","vale = " + vale + " index= " + index);
            map.put(key,vale);
        }
        AppLog.d("getTlvF48","map = " + map.toString());
        return map;
    }

    /**
     * TCC Must be a valid TCC or a space
     * SubElement ID: from 00 to 99 (22 is SingleTap)
     * SubElement Length: from 01 to 99
     * data: R220504011
     * @param field48
     * @return
     */
    public static boolean isSingleTapAndPinRequest(String field48) {
        boolean isRequestPin = false;
        Map<String, String> map = new HashMap<String, String>();
        String tcc="", seId = "",seLength = "",seValue = "";
        int index = 0,vLen = 0;
        if (field48 == null || field48.length() < 5) {
            AppLog.d("isSingleTapAndPinRequest","Invalid field48 = ");
            return false;
        }
        while (index < field48.length()) {
            tcc = field48.substring(index, index + 1);
            index += 1;
            AppLog.d("isSingleTapAndPinRequest","tcc = " + tcc + " index= " + index);
            seId = field48.substring(index, index + 2);
            index += 2;
            AppLog.d("isSingleTapAndPinRequest","seId = " + seId + " index= " + index);
            seLength = field48.substring(index, index + 2);
            index += 2;
            AppLog.d("isSingleTapAndPinRequest","seLength = " + seLength + " index= " + index);
            vLen = Integer.parseInt(seLength);
            seValue = field48.substring(index, index + vLen);
            index += vLen;
            AppLog.d("isSingleTapAndPinRequest","seValue = " + seValue + " index= " + index);
            map.put(seId, seValue);
        }
        String singleTapTag = map.get("22");
        AppLog.d("isSingleTapAndPinRequest","singleTapTag = " + singleTapTag);
        Map<String, String> sfMap = new HashMap<String, String>();
        if (!TextUtils.isEmpty(singleTapTag)){
            String sfId = "", sfLength = "", sfValue = "";
            int sfIndex = 0, sfLen = 0, pinRequestInt = 0;
            while (sfIndex < singleTapTag.length()) {
                sfId = singleTapTag.substring(sfIndex, sfIndex + 2);
                sfIndex += 2;
                AppLog.d("isSingleTapAndPinRequest","sfId = " + sfId + " index= " + sfIndex);
                sfLength = singleTapTag.substring(sfIndex, sfIndex + 2);
                sfIndex += 2;
                AppLog.d("isSingleTapAndPinRequest","sfLength = " + sfLength + " index= " + sfIndex);
                sfLen = Integer.parseInt(sfLength);
                sfValue = singleTapTag.substring(sfIndex, sfIndex + sfLen);
                sfIndex += sfLen;
                AppLog.d("isSingleTapAndPinRequest","sfValue = " + sfValue + " index= " + sfIndex);
                sfMap.put(sfId, sfValue);
            }
            String pinRequest = sfMap.get("04");
            AppLog.d("isSingleTapAndPinRequest","pinRequest = " + pinRequest);
            if (!TextUtils.isEmpty(pinRequest)) {
                pinRequestInt = Integer.parseInt(pinRequest);
                isRequestPin = (pinRequestInt == 1);
            }
            AppLog.d("isSingleTapAndPinRequest","isRequestPin = " + isRequestPin + " ,pinRequestInt: " + pinRequestInt);
        }
        return isRequestPin;
    }

    /**
     * TLV Format
     * tcc
     * subElement 22: Multi-Purpose Merchant Indicator
     * tag 02: Single Tap Indicator(Merchant capable of single tap processing)-    1
     * tag 03: Response to PIN Request-    1
     * @param oriField48
     * @return
     */
    public static String packRspField48Str(String oriField48) {
        String field48 = "";
        String tag1 = "02", tag2 = "03", se = "22";
        String tcc = oriField48.substring(0, 1);
        String tag1Value = "1", tag2Value = "1";
        String  tag1Len = "", tag2Len = "", seLen = "";
        tag1Len = String.format("%02d", tag1Value.length());
        AppLog.d("field48Str","tag1Len = " + tag1Len);
        tag2Len = String.format("%02d", tag2Value.length());
        AppLog.d("field48Str","tag2Len = " + tag2Len);
        String seValue = tag1 + tag1Len + tag1Value + tag2 + tag2Len + tag2Value;
        AppLog.d("field48Str","seValue = " + seValue);
        seLen = String.format("%02d", seValue.length());
        AppLog.d("field48Str","seLen = " + seLen);
        field48 = tcc + se + seLen + seValue;
        AppLog.d("field48Str","field48 = " + field48);

        return field48;
    }

    /**
     * 从磁道2数据中获取主帐号
     *
     * @param track
     * @return
     * @date 2015年5月22日下午3:28:14
     * @example
     */
    public static String getPan(String track) {
        if (track == null)
            return null;

        int len = track.indexOf('=');
        if (len < 0) {
            len = track.indexOf('D');
            if (len < 0)
                return null;
        }

        if ((len < 13) || (len > 19))
            return null;
        return track.substring(0, len);
    }

    /**
     * SD卡是否可用.
     */
    public static boolean sdCardIsAvailable() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sd = new File(Environment.getExternalStorageDirectory().getPath());
            return sd.canWrite();
        } else {
            return false;
        }
    }

    /**
     * 得到SD卡根目录.
     */
    public static File getRootPath() {
        File path = null;
        if (sdCardIsAvailable()) {
            path = Environment.getExternalStorageDirectory(); // 取得sdcard文件路径
        } else {
            path = Environment.getDataDirectory();
        }
        return path;
    }

    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/CrashLog/";

    public static void saveBitmapAsPng(Bitmap bmp) {
        try {
            long current = System.currentTimeMillis();
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(current));
            File file = new File(PATH + time);
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 前6后4， 其他显示“*”
     *
     * @param cardNo
     * @return
     */
    public static String maskedCardNo(String cardNo) {
        if (TextUtils.isEmpty(cardNo))
            return null;
        char[] tempNum = cardNo.toCharArray();
        int cardLength = tempNum.length;
        // 验证：16-20位数字
        if (cardLength < 13)
            return null;

        for (int i = 0; i < cardLength; i++) {
            if ((i + 1 > 6) && (i < cardLength - 4)) {
                tempNum[i] = '*';
            }
        }
        return new String(tempNum);
    }

    /**
     * 应用市场跳转
     * @param context
     * @param packageManager
     */
    public static  void startAppshop(Context context, PackageManager packageManager) {
        Intent intent = new Intent();
        intent = packageManager.getLaunchIntentForPackage("com.topwise.myappstore");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(intent == null) {
            TopToast.showFailToast(context,"");
        } else {
            context.startActivity(intent);
        }
    }

    /**
     * 获取外网的IP(要访问Url，要放到后台线程里处理)
     *
     * @param @return
     * @return String
     * @throws
     * @Title: GetNetIp
     * @Description:
     */
    public static String getNetIp() {
        URL infoUrl = null;
        InputStream inStream = null;
        String ipLine = "";
        HttpURLConnection httpConnection = null;
        try {
//          infoUrl = new URL("http://ip168.com/");
            infoUrl = new URL("http://pv.sohu.com/cityjson?ie=utf-8");
            URLConnection connection = infoUrl.openConnection();
            httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inStream, "utf-8"));
                StringBuilder strber = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null){
                    strber.append(line + "\n");
                }
                Pattern pattern = Pattern
                        .compile("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))");
                Matcher matcher = pattern.matcher(strber.toString());
                if (matcher.find()) {
                    ipLine = matcher.group();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inStream.close();
                httpConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        AppLog.e("getNetIp", ipLine);
        return ipLine;
    }

    /**
     * 转换时间格式
     * @param transData
     * @return
     */
    public static String getTransDataTime(TransData transData) {
        String year = transData.getDatetime();
        String date = transData.getDate();
        String time = transData.getTime();
        //yyyyMMdd
        return date.substring(2,4)  + "/"+date.substring(0,2)  + "/"+year.substring(0,4) + " "+
                time.substring(0,2) + ":" + time.substring(2,4) + ":" + time.substring(4,6) ;
    }

    /**
     * 弹虚拟键盘
     * @param editText
     * @param context
     */
    public static void showSoftInputFromWindow(EditText editText, Activity context) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    /**
     * 从aid或者卡号 区分卡片类别
     * @param enterMode
     * @param pan
     * @param aid
     * @return
     */
    public static String fromAidGetCardType(int enterMode,String pan,String aid) {
        if(enterMode == Component.EnterMode.SWIPE || enterMode == Component.EnterMode.MANAUL) {
            if (pan.startsWith("60") || pan.startsWith("62") || pan.startsWith("9")) {
                return "UNIONPAY";
            } else if (pan.startsWith("4")) {
                return "VISA";
            } else if (pan.startsWith("5")) {
                return "MASTERCARD";
            } else if (pan.startsWith("35")) {
                return "JCB";
            } else if (pan.startsWith("30")||pan.startsWith("36")||pan.startsWith("38")||pan.startsWith("3")) {
                return "Discover/Diner";
            } else if (pan.startsWith("37")) {
                return "AMEX";
            }
        } else {
            if (aid != null) {
                if (aid.startsWith("A000000003")) {
                    return "VISA";
                } else if (aid.startsWith("A000000004")) {
                    return "MASTERCARD";
                } else if (aid.startsWith("A000000333")) {
                    return "UNIONPAY";
                } else if (aid.startsWith("A000000065")) {
                    return "JCB";
                } else if (aid.startsWith("A000000025")) {
                    return "AMEX";
                } else if (aid.startsWith("A000000324") || aid.startsWith("A000000152")) {
                    return "DISCOVER";
                } else if (aid.startsWith("A000000524")) {
                    return "RUPAY";
                }
            }
        }
        return "Other";
    }

    /**
     * <uses-permission android:name="android.permission.READ_PHONE_STATE" />
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getIMEI(Context context) {
        String id;
        // android.telephony.TelephonyManager
        TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            TopToast.showFailToast(context,"Please check permission.READ_PHONE_STATE!");
            return null;
        }
        if (mTelephony.getDeviceId() != null) {
            id = mTelephony.getDeviceId();
        } else {
            //android.provider.Settings;
            id = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return id;
    }

    /**
     * 大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数
     * 此方法中前三位格式有：
     * 13+任意数
     * 15+除4的任意数
     * 18+除1和4的任意数
     * 17+除9的任意数
     * 147
     */
    public static boolean isChinaPhoneLegal(String str) throws PatternSyntaxException {
        String regExp = "^((13[0-9])|(15[^4])|(18[0,1,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static String convertorVale(String indata ,int len, char c) {
        if (DataUtils.isNullString(indata) || len == 0) return null;
        StringBuffer stringBuffer = new StringBuffer();
        if (indata.length() >= len) return indata;
        int l = len - indata.length();
        for (int i = 0; i < l; i++) {
            stringBuffer.append(c);
        }
        stringBuffer.append(indata);
        return stringBuffer.toString();
    }

    public static Bitmap getImageLogoFile() {
        Bitmap image = null;
        File file = new File(TmsParamDownload.LOGO_PATH);
        if (!file.exists()) {
            return null;
        }
        try {
            InputStream is = new FileInputStream(file);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    public static Bitmap getImageFromAssetsFile(String filename) {
        Bitmap image = null;
        AssetManager am = TopApplication.mApp.getResources().getAssets();
        try {
            InputStream is = am.open(filename);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    /**
     * 退出进程，到桌面
     * @param context
     */
    public static void exit(Context context) {
        //应用退出，就销毁
     //   TopApplication.amapLbsUtils.destroyLocation();

//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        intent.setPackage("com.topwise.toplauncher");
//                    Intent intent = new Intent(Intent.ACTION_MAIN);
//                    intent.addCategory(Intent.CATEGORY_HOME);
       // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       // context.startActivity(intent);
        Device.enableHomeAndRecent(false);
       // android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

  /*   public static String getLocationTagA5(){
        String tagA5T1 = "";// 经度
        String tagA5T2 = "";// 纬度
        String tagA5T5 = "";// 坐标系

        String tagA5T3 = "";// MCC
        String tagA5T4 = "";// MNC

        // 1, CDMA 电信
        String tagA5T15 = "";// SID
        String tagA5T16 = "";// NID
        String tagA5T17 = "";// BID
        String tagA5T18 = "";// SIG

        // 2, GSM 移动，联通
        String tagA5T6 = "";// LAC
        String tagA5T7 = "";// CID
        String tagA5T8 = "";// SIG

        // MNC（00、02、04、07-移动 01、06、09-联通 03、05、11-电信）

        StringBuilder sb = new StringBuilder();
        // 1,GPS/WIFI定位
        // 定位信息 消费和预授权需要上送tagA5 必填信息: 经度 纬度 坐标系 (高德地图返回的是 GCJ02)
//        北纬为正数，南纬为负数；
//        东经为正数，西经为负数。

        ALocation aLocation = TopApplication.amapLbsUtils.getaMapLocation();
        String longitude = "";
        String latitude = "";
        double longitudedouble = aLocation.getLongitude();
        double latitudedouble = aLocation.getLatitude();
        if (longitude != null && longitude != null) {

            String type = "GCJ02";
            // 经度
            if (longitudedouble >= 0) {
                longitude = "+" + String.valueOf(longitudedouble);
            }else {
                longitude = "-" + String.valueOf(longitudedouble);
            }
            if (longitude.length() > 10) {
                longitude = longitude.substring( 0, 10 );
            }
            // 纬度
            if (latitudedouble >= 0) {
                latitude = "+" + String.valueOf(latitudedouble);
            }else {
                latitude = "-" + String.valueOf(latitudedouble);
            }
            if (latitude.length() > 10) {
                latitude = latitude.substring( 0, 10 );
            }

            tagA5T1 = getTlv( sb, "01", longitude );
            tagA5T2 = getTlv( sb, "02", latitude );
            tagA5T5 = getTlv( sb, "05", type );
        }

        // 2,基站信息 定位和基站信息都有就都上送
        BaseStationInfo baseStationInfo = TopApplication.amapLbsUtils.getBaseStationInfo();
        if (baseStationInfo != null) {

            // 共有 MCC MNC SIG
            String mcc = baseStationInfo.getMcc() + "";
            String mnc = String.format( "%02d", baseStationInfo.getMnc() );
            String sig = baseStationInfo.getSig();

            tagA5T3 = getTlv( sb, "03", mcc );
            tagA5T4 = getTlv( sb, "04", mnc );

            if (baseStationInfo.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
                // 1，CDMA 必选 NID BID SID
                String sid = baseStationInfo.getSID();
                String nid = baseStationInfo.getNID();
                String bid = baseStationInfo.getBID();

                tagA5T15 = getTlv( sb, "15", sid );// SID
                tagA5T16 = getTlv( sb, "16", nid );// NID
                tagA5T17 = getTlv( sb, "17", bid );// BID
                tagA5T18 = getTlv( sb, "18", sig );// SIG

            } else {
                // 2，GSM 必选LAC CID

                String lac = baseStationInfo.getLac();
                String cid = baseStationInfo.getCid();

                tagA5T6 = getTlv( sb, "06", lac );// LAC
                tagA5T7 = getTlv( sb, "07", cid );// CID
                tagA5T8 = getTlv( sb, "08", sig );// SIG
            }

        }

        // A5 value整合
        sb.delete( 0, sb.length() );
        sb.append( tagA5T1 );
        sb.append( tagA5T2 );
        sb.append( tagA5T3 );
        sb.append( tagA5T4 );
        sb.append( tagA5T5 );
        sb.append( tagA5T6 );
        sb.append( tagA5T7 );
        sb.append( tagA5T8 );
        sb.append( tagA5T15 );
        sb.append( tagA5T16 );
        sb.append( tagA5T17 );
        sb.append( tagA5T18 );
        String tagA5 = sb.toString();

        if (!TextUtils.isEmpty( tagA5 )) {
            // A5 TLV
            tagA5 = getTlv( sb, "A5", tagA5 );

        }
        return tagA5;
    }*/

    private static String getTlv(StringBuilder sb, String tag, String value) {
        sb.delete( 0, sb.length() );
        sb.append( tag );
        sb.append( String.format( "%03d", value.length() ) );
        sb.append( value );
        return sb.toString();
    }

    public static boolean checkYYMM(String yyMMdata) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
        try {
            dateFormat.setLenient(false);
            dateFormat.parse("20" + yyMMdata);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean checkMMdd(String MMdddata) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMdd");
        try {
            dateFormat.setLenient(false);
            dateFormat.parse(MMdddata);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void install(Context context, String name, String path) {
        InputStream in = null;
        FileOutputStream out = null;
        try {
            in = context.getAssets().open(name);
            File file = new File(path + name);
            out = new FileOutputStream(file);
            int count = 0;
            byte[] tmp = new byte[1024];
            while ((count = in.read(tmp)) != -1) {
                out.write(tmp, 0, count);
            }
            Runtime.getRuntime().exec("chmod 777 " + path + name);
        } catch (IOException e) {
            // log.e(e);
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getEsignPath(Context context) {
        String path = context.getFilesDir().getAbsolutePath()+"/topwise/esign/";
        return  path;
    }

    /**
     *
     * @return
     */
    public static String getRandom() {
        try {
            AidlPinpad aidlPinpad = TopApplication.usdkManage.getPinpad(0);
            if(aidlPinpad== null){
                return "";
            }

            byte [] random = aidlPinpad.getRandom();
            byte[] unpredictableNum = new byte[4];
            System.arraycopy(random, 0, unpredictableNum, 0, 4);
            return TopApplication.convert.bcdToStr(unpredictableNum);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String amountAddComma(String str) {
        if (TextUtils.isEmpty(str)) {
            str = "";
        }
        String addCommaStr = ""; // 需要添加逗号的字符串(整数)
        String tmpCommaStr = ""; // 小数，等逗号添加完后，最后在末尾补上
        if (str.contains(".")) {
            addCommaStr = str.substring(0, str.indexOf("."));
            tmpCommaStr = str.substring(str.indexOf("."), str.length());
        } else {
            addCommaStr = str;
        }

        // 将传进数字反转
        String reverseStr = new StringBuilder(addCommaStr).reverse().toString();
        String strTemp = "";
        for (int i = 0; i < reverseStr.length(); i++) {
            if (i * 3 + 3 > reverseStr.length()) {
                strTemp += reverseStr.substring(i * 3, reverseStr.length());
                break;
            }
            strTemp += reverseStr.substring(i * 3, i * 3 + 3) + ",";
        }
        // 将 "5,000,000," 中最后一个","去除
        if (strTemp.endsWith(",")) {
            strTemp = strTemp.substring(0, strTemp.length() - 1);
        }
        String test = new StringBuilder(strTemp).reverse().toString();
        // 将数字重新反转,并将小数拼接到末尾
        String resultStr = test + tmpCommaStr;
        return resultStr;
    }

    public static String getSystemReferenceNumber(TransData transData) {
        String systemRefNo = "";
        String sn = Device.getSn();
        if (sn != null) {
            if (sn.length() > 6) {
                sn = sn.substring(sn.length() - 5, sn.length());
            }
        }
        systemRefNo = sn + String.format("%06d", transData.getTransNo());
        return systemRefNo;
    }
    public static void loadAidCapk() {
        long startTime = System.currentTimeMillis();
        AppLog.d("loadAidCapk","loadAidCapk start: " + startTime);

        // Load all aid param
        LoadParam aidParam = new AidParam();
        aidParam.saveAll();
        AppLog.d("loadAidCapk","init AidParam====");

        // Load all public key param
        LoadParam capkParam = new CapkParam();
        capkParam.saveAll();
        AppLog.d("loadAidCapk","init CapkParam====");

        reloadCombinationList();

        long useTime = System.currentTimeMillis() - startTime;
        AppLog.d("loadAidCapk","loadAidCapk time use: " + useTime);
    }

    public static void reloadCombinationList() {
        AppLog.d("reloadAidParam","start");
        AppCombinationHelper.getInstance().deleteAll();
        // Load all combinations from aids
        AppCombinationHelper.getInstance().getAppCombinationList();
    }

    public static void loadTmsOtherParams() {
        long startTime = System.currentTimeMillis();
        AppLog.d("loadTmsOtherParams","loadTmsOtherParams start: " + startTime);
        String temp = "";
        boolean isEnable = false;

        //Merchant ID
        temp = TopApplication.parameterBean.getMerchantID();
        AppLog.d("loadTmsOtherParams","merchantID = " + temp);
        if (temp != null && !TextUtils.isEmpty(temp)) {
            TopApplication.sysParam.set(SysParam.MERCH_ID, temp);
        }

        //Terminal ID
        temp = TopApplication.parameterBean.getTerminalID();
        AppLog.d("loadTmsOtherParams","terminalID = " + temp);
        if (temp != null && !TextUtils.isEmpty(temp)) {
            TopApplication.sysParam.set(SysParam.TERMINAL_ID, temp);
        }

        //Country Code
        temp = TopApplication.parameterBean.getCountryCode();
        AppLog.d("loadTmsOtherParams","countryCode = " + temp);
        if (temp != null && !TextUtils.isEmpty(temp)) {
            TopApplication.sysParam.set(SysParam.APP_PARAM_TER_COUNTRY_CODE, temp);
        }

        //Currency Code
        temp = TopApplication.parameterBean.getCurrencyCode();
        AppLog.d("loadTmsOtherParams","currencyCode = " + temp);
        if (temp != null && !TextUtils.isEmpty(temp)) {
            TopApplication.sysParam.set(SysParam.APP_PARAM_TRANS_CURRENCY_CODE, temp);
        }

        //Merchant Name
        temp = TopApplication.parameterBean.getMerchantName();
        AppLog.d("loadTmsOtherParams","merchantName = " + temp);
        if (temp != null && !TextUtils.isEmpty(temp)) {
            TopApplication.sysParam.set(SysParam.MERCH_NAME, temp);
        }

        //Merchant Location
        temp = TopApplication.parameterBean.getMerchantLocation();
        AppLog.d("loadTmsOtherParams","merchantLocation = " + temp);
        if (temp != null && !TextUtils.isEmpty(temp)) {
            TopApplication.sysParam.set(SysParam.LOCATION_INFO, temp);
        }

        //Merchant City
        temp = TopApplication.parameterBean.getMerchantCity();
        AppLog.d("loadTmsOtherParams","merchantCity = " + temp);
        if (temp != null && !TextUtils.isEmpty(temp)) {
            TopApplication.sysParam.set(SysParam.CITY_INFO, temp);
        }

        //Merchant State
        temp = TopApplication.parameterBean.getMerchantState();
        AppLog.d("loadTmsOtherParams","merchantState = " + temp);
        if (temp != null && !TextUtils.isEmpty(temp)) {
            TopApplication.sysParam.set(SysParam.STATE_CODE, temp);
        }

        //Currency Country
        temp = TopApplication.parameterBean.getMerchantCountry();
        AppLog.d("loadTmsOtherParams","merchantCountry = " + temp);
        if (temp != null && !TextUtils.isEmpty(temp)) {
            TopApplication.sysParam.set(SysParam.COUNTRY_CODE, temp);
        }

        //MCC
        temp = TopApplication.parameterBean.getMcc();
        AppLog.d("loadTmsOtherParams","mcc = " + temp);
        if (temp != null && !TextUtils.isEmpty(temp)) {
            TopApplication.sysParam.set(SysParam.PARAM_MCC, temp);
        }

        //Sale Enable
        isEnable = TopApplication.parameterBean.getSaleEnable();
        AppLog.d("loadTmsOtherParams","saleEnable = " + isEnable);

        //Auth Enable
        isEnable = TopApplication.parameterBean.getAuthEnable();
        AppLog.d("loadTmsOtherParams","authEnable = " + isEnable);

        //Refund Enable
        isEnable = TopApplication.parameterBean.getRefundEnable();
        AppLog.d("loadTmsOtherParams","refundEnable = " + isEnable);

        //Check pin for pre-auth-void Enable
        isEnable = TopApplication.parameterBean.isPinPreAuthVoid();
        AppLog.d("loadTmsOtherParams","pinPreAuthVoid = " + isEnable);
        if (isEnable) {
            TopApplication.sysParam.set(SysParam.IPTC_PAVOID, SysParam.Constant.YES);
        } else {
            TopApplication.sysParam.set(SysParam.IPTC_PAVOID, SysParam.Constant.NO);
        }

        //Check pin for pre-auth-void Enable
        isEnable = TopApplication.parameterBean.isCardPreAuthVoid();
        AppLog.d("loadTmsOtherParams","cardPreAuthVoid = " + isEnable);
        if (isEnable) {
            TopApplication.sysParam.set(SysParam.UCTC_PAVOID, SysParam.Constant.YES);
        } else {
            TopApplication.sysParam.set(SysParam.UCTC_PAVOID, SysParam.Constant.NO);
        }

        //Manual Key Enable
        isEnable = TopApplication.parameterBean.getManualKeyEnable();
        AppLog.d("loadTmsOtherParams","manualKeyEnable = " + isEnable);

        //Magnetic Stripe Card Enable
        isEnable = TopApplication.parameterBean.getMagStripeEnable();
        AppLog.d("loadTmsOtherParams","magStripeEnable = " + isEnable);

        //Electronic Signature Enable
        isEnable = TopApplication.parameterBean.isSignature();
        AppLog.d("loadTmsOtherParams","signature = " + isEnable);

        //Security Password
        temp = TopApplication.parameterBean.getSecurityPassword();
        AppLog.d("loadTmsOtherParams","securityPassword = " + temp);
        if (temp != null && !TextUtils.isEmpty(temp)) {
            TopApplication.sysParam.set(SysParam.SEC_SECPWD, temp);
        }

        //Security Password
        temp = TopApplication.parameterBean.getAdminPassword();
        AppLog.d("loadTmsOtherParams","adminPassword = " + temp);
        if (temp != null && !TextUtils.isEmpty(temp)) {
            TopApplication.sysParam.set(SysParam.SEC_SYSPWD, temp);
        }

        //Security Password
        temp = TopApplication.parameterBean.getSupervisorPassword();
        AppLog.d("loadTmsOtherParams","supervisorPassword = " + temp);
        if (temp != null && !TextUtils.isEmpty(temp)) {
            TopApplication.sysParam.set(SysParam.SEC_MNGPWD, temp);
        }

        //ip
        temp = TopApplication.parameterBean.getIp();
        AppLog.d("loadTmsOtherParams","ip = " + temp);
        if (temp != null && !TextUtils.isEmpty(temp)) {
            TopApplication.sysParam.set(SysParam.HOSTIP, temp);
        }

        //port
        temp = TopApplication.parameterBean.getPort();
        AppLog.d("loadTmsOtherParams","port = " + temp);
        if (temp != null && !TextUtils.isEmpty(temp)) {
            TopApplication.sysParam.set(SysParam.HOSTPORT, temp);
        }

        //Reversal Times Control
        temp = TopApplication.parameterBean.getReversalControl();
        AppLog.d("loadTmsOtherParams","reversalControl = " + temp);
        if (temp != null && !TextUtils.isEmpty(temp)) {
            TopApplication.sysParam.set(SysParam.REVERSL_CTRL, temp);
        }

        //Terminal CVM Limit
        temp = TopApplication.parameterBean.getTerminalCvmLmt();
        AppLog.d("loadTmsOtherParams","TerminalCvmLmt = " + temp);
        if (temp != null && !TextUtils.isEmpty(temp)) {
            TopApplication.sysParam.set(SysParam.CVM_LIMIT, temp);
        }

        //Terminal Transaction Limit
        temp = TopApplication.parameterBean.getTerminalTransactionLmt();
        AppLog.d("loadTmsOtherParams","TerminalTransactionLmt = " + temp);
        if (temp != null && !TextUtils.isEmpty(temp)) {
            TopApplication.sysParam.set(SysParam.CONTACTLESS_LIMIT, temp);
        }

        //Terminal Floor Limit
        temp = TopApplication.parameterBean.getTerminalFloorLmt();
        AppLog.d("loadTmsOtherParams","TerminalFloorLmt = " + temp);
        if (temp != null && !TextUtils.isEmpty(temp)) {
            TopApplication.sysParam.set(SysParam.FLOOR_LIMIT, temp);
        }

        //Transaction Number
        temp = TopApplication.parameterBean.getSerialNo();
        AppLog.d("loadTmsOtherParams","SerialNo = " + temp);
        if (temp != null && !TextUtils.isEmpty(temp)) {
            TopApplication.sysParam.set(SysParam.TRANS_NO, temp);
        }

        //Batch Number
        temp = TopApplication.parameterBean.getBatchNo();
        AppLog.d("loadTmsOtherParams","BatchNo = " + temp);
        if (temp != null && !TextUtils.isEmpty(temp)) {
            TopApplication.sysParam.set(SysParam.BATCH_NO, temp);
        }

        long useTime = System.currentTimeMillis() - startTime;
        AppLog.d("loadTmsOtherParams","loadTmsOtherParams time use: " + useTime);
    }
}
