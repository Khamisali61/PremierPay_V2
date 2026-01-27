package com.topwise.premierpay.app;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;

import com.google.gson.Gson;
import com.tencent.mmkv.MMKV;
import com.topwise.iamge.TopImage;

import com.topwise.manager.AppLog;
import com.topwise.manager.ITopUsdk;
import com.topwise.manager.TopUsdkManage;
import com.topwise.toptool.api.ITool;
import com.topwise.toptool.api.comm.ICommHelper;
import com.topwise.toptool.api.convert.IConvert;
import com.topwise.toptool.api.packer.IPacker;
import com.topwise.toptool.impl.TopTool;
import com.topwise.premierpay.param.AidParam;
import com.topwise.premierpay.param.AppCombinationHelper;
import com.topwise.premierpay.param.LoadParam;
import com.topwise.premierpay.daoutils.DaoManager;

import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.tms.bean.ParameterBean;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.Controller;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.trans.model.ResponseCode;
import com.topwise.premierpay.param.CapkParam;
import com.topwise.premierpay.utils.ConfiUtils;
import com.topwise.premierpay.utils.SmallScreenUtil;
import com.topwise.premierpay.utils.ThreadPoolUtils;
import com.topwise.premierpay.utils.Utils;
import com.topwise.premierpay.view.TopToast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TopApplication extends Application {
    public static final String APPNANE = " ";
    private static final String TAG =  TopApplication.APPNANE + TopApplication.class.getSimpleName();

    private Context mContext;
    public static TopApplication mApp;
    public static IConvert convert;
    public static IPacker packer;
    public static ICommHelper iCommHelper;
    public static ITool iTool;
    public static TopImage topImage;


    // 订单号
    public static String mOrderNo;
    /**
     * 平台应答码解析
     */
    public static ResponseCode rspCode;
    // 应用版本号
    public static String version;

    public static SysParam sysParam;
    public static Controller controller;

    public static ITopUsdk usdkManage;

    public static boolean isRuning; //Click the menu flag
    public static boolean isInstallDeviceService;
    public static  ParameterBean parameterBean;
    private static final String TERMINAL_PARAMETER = "defaultParameterJson.json";
    public static long curTime = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        // log 控制
        AppLog.i(TAG,"onCreate");
        version = getVersion();
        mContext = getApplicationContext();
        mApp = this;
        SmallScreenUtil.getInstance().init(this);

        // init uDaoManager
        DaoManager.getInstance().init(mApp);
        ThreadPoolUtils.getThreadPool();
        MMKV.initialize(mApp);
        sysParam = SysParam.getInstance(mApp);

        String paramString = sysParam.get(SysParam.TMS_PARAM_STR);
        AppLog.i(TAG,"paramString: " + paramString);
        if(paramString!=null && !"".equals(paramString)) {
            Gson gson = new Gson();
            parameterBean = gson.fromJson(paramString, ParameterBean.class);
            AppLog.v(TAG, "parameterBean: " + parameterBean.toString());
        }else{
            paramString =loadParameterFile(this);
            Gson gson = new Gson();
            parameterBean = gson.fromJson(paramString, ParameterBean.class);
        }
        //init crashtool
        //CrashHandler crashHandler = CrashHandler.getInstance();
        // crashHandler.init(this);
        //init logger
       /* FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(4)         // (Optional) How many method line to show. Default 2
//                .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
         //       .logStrategy(new CustomLogCatStrategy()) // (Optional) Changes the log strategy to print out. Default LogCat
                .tag("TPW-posApp")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));*/
        //将日志输出到文件
//        FormatStrategy formatStrategy = CsvFormatStrategy.newBuilder()
////                .showThreadInfo(true)  // (Optional) Whether to show thread info or not. Default true
////                .methodCount(4)         // (Optional) How many method line to show. Default 2
////                .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
//         //       .logStrategy(new CustomLogCatStrategy()) // (Optional) Changes the log strategy to print out. Default LogCat
//                .tag("TPW-posApp")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
//                .build();
        //       Logger.addLogAdapter(new DiskLogAdapter(formatStrategy));


        //=== inir sdk manager db
//        long l = System.currentTimeMillis();
//        DBManager.getInstance().init(this);  //emv db 大约耗时 2s
//        AppLog.d("ssssssss " + (System.currentTimeMillis() -l));

        if (!(Build.MANUFACTURER.equalsIgnoreCase("topwise") || Build.MANUFACTURER.equalsIgnoreCase("gertec"))) {
            sysParam.set(SysParam.DEVICE_MODE, 1);
        }
        if (Build.MODEL.equals("SK-210") || Build.MODEL.equals("SK210")) {
            sysParam.set(SysParam.DEVICE_MODE, 2);
        }
        // sysParam.set(SysParam.DEVICE_MODE,2);

        usdkManage = TopUsdkManage.getInstance();
        usdkManage.setMode(sysParam.getInt(SysParam.DEVICE_MODE));
        usdkManage.init(this,(ret)->{
            AppLog.i(TAG,"init ret:" + ret);
            if (ret) {
                // LogUtil.restrictLog(LogUtil.ERROR);
                Device.enableHomeAndRecent(true);
                // if("topwise".startsWith(BuildConfig.CHANNEL) && !Build.MODEL.equals("SK-210")) {
                TopApplication.injectKeys();
                // }
            } else {
                TopToast.showFailToast(mApp,"SDK Bind Fail!");
                System.exit(0);
            }
            SmallScreenUtil.getInstance().setISmallScreen(usdkManage.getSmallScreen());
            SmallScreenUtil.getInstance().showLogo("topwise.bmp");
        });

        init();
    }


    private static String loadParameterFile(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = context.getResources().getAssets().open(TERMINAL_PARAMETER)){
            long startTime = System.currentTimeMillis();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            AppLog.d(TAG, "load Assets Aid File(ms) = " + (System.currentTimeMillis() - startTime));
        } catch (IOException e) {
            e.printStackTrace();
        }
        AppLog.d(TAG, "AID Json = " + stringBuilder.toString().trim());
        return stringBuilder.toString();
    }

    public static void switchModeInit(){
        usdkManage = TopUsdkManage.getInstance();
        usdkManage.setMode(sysParam.getInt(SysParam.DEVICE_MODE));
        usdkManage.init(mApp.mContext, (ret)->{
            AppLog.i(TAG,"init ret:"+ret);
            if (ret){
                Device.enableHomeAndRecent(true);
                // if("topwise".startsWith(BuildConfig.CHANNEL) && !Build.MODEL.equals("SK-210")) {
                TopApplication.injectKeys();
                // }
            }else {
                TopToast.showFailToast(mApp,"SDK Bind Fail!");
                System.exit(0);
            }
            SmallScreenUtil.getInstance().setISmallScreen(usdkManage.getSmallScreen());
            SmallScreenUtil.getInstance().showLogo("topwise.bmp");
        });
    }

    /**
     * 初始化工具类
     */
    public static void init() {
        AppLog.i(TAG,"init AidlDeviceService");

        ConfiUtils.isDebug = !(boolean)sysParam.get(SysParam.COMMUNICATION_MODE,false);
        AppLog.i(TAG,"ConfiUtils.isDebug " + ConfiUtils.isDebug);

        AppLog.i(TAG,"init controller");
        controller = Controller.getInstance();

        AppLog.i(TAG,"init TopTool");
        iTool = TopTool.getInstance();

        convert = iTool.getConvert();
        packer = iTool.getPacker();
        iCommHelper = iTool.getCommHelper();
        topImage = TopImage.getInstance(mApp);

        // amapLbsUtils =AmapLbsUtils.getInstance(TopApplication.mApp);

        initData(mApp);
    }

    /**
     * 获取软件版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            String version = info.versionName+"_"+info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void initData(final Context mContext) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 拷贝打印字体
                Utils.install(TopApplication.mApp, Component.FONT_NAME, Component.FONT_PATH);

                // 初始化平台应答码
                rspCode = ResponseCode.getInstance();
                try {
                    if (Utils.isZh(mContext)) {
                        AppLog.d(TAG,"init ch");
                        rspCode.init(mContext.getResources().getAssets().open("response_list_en.xml"));
                    } else {
                        AppLog.d(TAG,"init en");
                        rspCode.init(mContext.getResources().getAssets().open("response_list_en.xml"));
                    }

                    // Load all aid param
                    LoadParam aidParam = new AidParam();
                    aidParam.saveAll();
                    AppLog.d(TAG,"init AidParam====");

                    // Load all public key param
                    LoadParam capkParam = new CapkParam();
                    capkParam.saveAll();
                    AppLog.d(TAG,"init CapkParam====");

                    // Load all combinations from aids
                    AppCombinationHelper.getInstance().getAppCombinationList();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /*************
     * inject the key for test
     */
    public static void injectKeys() {
        // 🛑 SAFETY: Return immediately to disable test key injection
        // This prevents the app from overwriting your production keys.
        if (true) {
            return;
        }

        // Original logic below (now unreachable)
        if (TopApplication.usdkManage == null || TopApplication.usdkManage.getPinpad(0) == null) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(500);
                // Faked key value
                String tmk ="F45EDA5EC710202A79D09BC4D5452C40" ;
                String pik ="3DA449E1DE4882301A37CD0275420FD1" ;
                String mac ="D9EF4F102C237D9DC01D07868E445503" ;
                Device.writeTMK(TopApplication.convert.strToBcd(tmk, IConvert.EPaddingPosition.PADDING_LEFT));
                Device.writeTPK(TopApplication.convert.strToBcd(pik, IConvert.EPaddingPosition.PADDING_LEFT),null);
                Device.writeMAK(TopApplication.convert.strToBcd(mac, IConvert.EPaddingPosition.PADDING_LEFT),null);
            }
        }).start();
    }
}
