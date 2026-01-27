package com.topwise.manager;

import android.app.Application;
import android.content.Context;

import com.topwise.cloudpos.aidl.card.AidlCheckCard;
import com.topwise.cloudpos.aidl.mdb.AidlMdbSerialport;
import com.topwise.cloudpos.aidl.smallscreen.AidlSmallScreen;
import com.topwise.cloudpos.aidl.tm.AidlTM;
import com.topwise.cloudpos.aidl.buzzer.AidlBuzzer;
import com.topwise.cloudpos.aidl.camera.AidlCameraScanCode;
import com.topwise.cloudpos.aidl.cpucard.AidlCPUCard;
import com.topwise.cloudpos.aidl.emv.level2.AidlAmex;
import com.topwise.cloudpos.aidl.emv.level2.AidlDpas;
import com.topwise.cloudpos.aidl.emv.level2.AidlEmvL2;
import com.topwise.cloudpos.aidl.emv.level2.AidlEntry;
import com.topwise.cloudpos.aidl.emv.level2.AidlJcb;
import com.topwise.cloudpos.aidl.emv.level2.AidlMir;
import com.topwise.cloudpos.aidl.emv.level2.AidlPaypass;
import com.topwise.cloudpos.aidl.emv.level2.AidlPaywave;
import com.topwise.cloudpos.aidl.emv.level2.AidlPure;
import com.topwise.cloudpos.aidl.emv.level2.AidlQpboc;
import com.topwise.cloudpos.aidl.emv.level2.AidlRupay;
import com.topwise.cloudpos.aidl.fingerprint.AidlFingerprint;
import com.topwise.cloudpos.aidl.iccard.AidlICCard;
import com.topwise.cloudpos.aidl.led.AidlLed;
import com.topwise.cloudpos.aidl.magcard.AidlMagCard;
import com.topwise.cloudpos.aidl.pedestal.AidlPedestal;
import com.topwise.cloudpos.aidl.pinpad.AidlPinpad;
import com.topwise.cloudpos.aidl.printer.AidlPrinter;
import com.topwise.cloudpos.aidl.psam.AidlPsam;
import com.topwise.cloudpos.aidl.rfcard.AidlRFCard;
import com.topwise.cloudpos.aidl.serialport.AidlSerialport;
import com.topwise.cloudpos.aidl.shellmonitor.AidlShellMonitor;
import com.topwise.cloudpos.aidl.system.AidlSystem;
import com.topwise.manager.card.api.ICardReader;
import com.topwise.manager.emv.api.IEmv;


/**
 * 创建日期：2021/4/12 on 10:04
 * 描述:
 * 作者:  wangweicheng
 */
public class TopUsdkManage implements ITopUsdk {
    private static ITopUsdk mService;
    private static TopUsdkManage topUsdkManage;
    /******* Mode = 0  Pos Mode; Mode = 1  BT  Mode;Mode = 2  USB Mode; **********/
    private static int mMode = 0;

    public static TopUsdkManage getInstance() {
        if (topUsdkManage == null) {
            synchronized (TopUsdkManage.class) {
                if (topUsdkManage == null) {
                    topUsdkManage = new TopUsdkManage();
                }
            }
        }
        return topUsdkManage;
    }

    @Override
    public String getVersion() {
        return mService.getVersion();
    }

    @Override
    public void init(Context mContext, TopUsdkManage.InitListener initListener) {
        switch (mMode){
            case 0:
            default:
                mService = TopLocalManage.getInstance();
                break;
        }
        mService.init(mContext,initListener);
    }

    @Override
    public AidlEmvL2 getEmv() {
        return mService.getEmv();
    }

    @Override
    public AidlPure getPurePay() {
        return mService.getPurePay();

    }

    @Override
    public AidlPaypass getPaypass() {
        return mService.getPaypass();
    }

    @Override
    public AidlPaywave getPaywave() {
        return mService.getPaywave();
    }

    @Override
    public AidlEntry getEntry() {
        return mService.getEntry();
    }

    @Override
    public AidlAmex getAmexPay() {
        return mService.getAmexPay();
    }

    @Override
    public AidlQpboc getUnionPay() {
        return mService.getUnionPay();
    }

    @Override
    public AidlRupay getRupay() {
        return mService.getRupay();
    }

    @Override
    public AidlMir getMirPay() {
        return mService.getMirPay();
    }

    @Override
    public AidlDpas getDpasPay() {
        return mService.getDpasPay();
    }

    @Override
    public AidlJcb getJcbPay() {
        return mService.getJcbPay();
    }

    @Override
    public AidlSystem getSystem() {
        return mService.getSystem();
    }

    @Override
    public AidlCameraScanCode getCameraScan() {
        return mService.getCameraScan();
    }

    @Override
    public AidlPinpad getPinpad(int type) {
        return mService.getPinpad(type);
    }

    @Override
    public AidlLed getLed() {
        return mService.getLed();
    }

    @Override
    public AidlBuzzer getBuzzer() {
        return mService.getBuzzer();
    }

    @Override
    public AidlPrinter getPrinter() {
        return mService.getPrinter();
    }

    @Override
    public AidlShellMonitor getShellMonitor() {
        return mService.getShellMonitor();
    }

    public AidlSmallScreen getSmallScreen() {
        return mService.getSmallScreen();
    }

    @Override
    public AidlICCard getIcc() {
        return mService.getIcc();
    }

    @Override
    public AidlRFCard getRf() {
        return mService.getRf();
    }

    @Override
    public AidlMagCard getMag() {
        return mService.getMag();
    }

    @Override
    public AidlPsam getPsam(int devid) {
        return mService.getPsam(devid);
    }

    @Override
    public AidlSerialport getSerialport(int port) {
        return mService.getSerialport(port);
    }

    @Override
    public AidlPedestal getPedestal() {
        return mService.getPedestal();
    }

    @Override
    public AidlCPUCard getCpu() {
        return mService.getCpu();
    }

    @Override
    public ICardReader getCardReader() {
        return mService.getCardReader();
    }

    @Override
    public IEmv getEmvHelper() {
        return mService.getEmvHelper();
    }

    @Override
    public AidlFingerprint getFingerprint() {
        return mService.getFingerprint();
    }

    @Override
    public AidlTM getTmsManager() {
        return mService.getTmsManager();
    }

    @Override
    public AidlCheckCard getCheckCard() {
        return mService.getCheckCard();
    }


    @Override
    public AidlMdbSerialport getMDBSerial(int port) {
        return mService.getMDBSerial(port);
    }


    @Override
    public void setMode(int mode) {
        mMode = mode;
    }

    @Override
    public void close() {

    }

    public interface InitListener{
        void OnConnection(boolean ret);
    }
}

