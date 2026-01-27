package com.topwise.premierpay;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import androidx.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.bumptech.glide.Glide;
import com.topwise.kdialog.DialogSureCancel;
import com.topwise.kdialog.IkeyListener;
import com.topwise.manager.AppLog;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.app.BaseActivity;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.bean.BannerBean;
import com.topwise.premierpay.daoutils.DaoUtlis;
import com.topwise.premierpay.mdb.activity.MDBTestActivity;
import com.topwise.premierpay.menu.AuthMenuActivity;
import com.topwise.premierpay.menu.SettingMenuActivity;
import com.topwise.premierpay.menu.TestMenuActivity;
import com.topwise.premierpay.menu.VoidMenuActivity;
import com.topwise.premierpay.mpesa.MpesaStkActivity; // Added M-Pesa Activity Import
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.thirdcall.ThirdCall;
import com.topwise.premierpay.trans.TransBalance;
import com.topwise.premierpay.trans.TransCashOnly;
import com.topwise.premierpay.trans.TransQrCode;
import com.topwise.premierpay.trans.TransQrSave;
import com.topwise.premierpay.trans.TransRefund;
import com.topwise.premierpay.trans.TransSale;
import com.topwise.premierpay.trans.action.ActionInputpwd;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ATransaction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.trans.model.EUIParamKeys;

import com.topwise.premierpay.trans.TransSaleWithCash;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.trans.record.DetailActivity;
import com.topwise.premierpay.transmit.TransProcessListenerImpl;
import com.topwise.premierpay.transmit.iso8583.Transmit;
import com.topwise.premierpay.utils.NetWorkUtils;
import com.topwise.premierpay.utils.PermissionsTool;
import com.topwise.premierpay.utils.SmallScreenUtil;
import com.topwise.premierpay.utils.Utils;
import com.topwise.premierpay.view.InputPwdDialog;
import com.topwise.premierpay.view.MenuPage;
import com.topwise.premierpay.view.TopToast;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.RectangleIndicator;

import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = TopApplication.APPNANE + MainActivity.class.getSimpleName();
    private MenuPage menuPage;
    private boolean hasDoTrans;
    private final static int CHECK_PON_STATER = 0x01;
    private LinearLayout mLayout;
    Banner mBanner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermission();
        DaoUtlis.initData();
        NetWorkUtils.startMobileSignal(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        mLayout = (LinearLayout) findViewById(R.id.ll_gallery);
        mBanner = (Banner) findViewById(R.id.banner);
        BannerBean bannerBean = new BannerBean();
        initBanner(bannerBean.getBannerList());
        mLayout.post(new Runnable() {
            @Override
            public void run() {
                android.widget.LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
                menuPage = createMenu();
                mLayout.addView(menuPage, params);
            }
        });
    }



    public void initBanner(List<String> list) {
        mBanner.setAdapter(new BannerImageAdapter<String>(list) {
            @Override
            public void onBindView(BannerImageHolder bannerImageHolder, String bannerBean, int i, int i1) {
                Glide.with(bannerImageHolder.itemView)
                        .load(bannerBean.toString())
                        .into(bannerImageHolder.imageView);
            }
        });
        mBanner.setIndicator(new RectangleIndicator(this));
    }

    /**
     * 创建菜单
     */
    private MenuPage createMenu() {
        int maxItemNPerPage = 9;
        int columns = 3;
//        if (Utils.isLowPix(MainActivity.this)) {
//            maxItemNPerPage = 6;
//            columns =3;
//        }
        MenuPage.Builder builder = new MenuPage.Builder(MainActivity.this, maxItemNPerPage, columns);
        builder.addTransItem(getString(R.string.title_sale), R.mipmap.sale_2,TopApplication.parameterBean.getSaleEnable(),
                        new TransSale(MainActivity.this, handler, transEndListener))
//                .addTransItem(getString(R.string.app_qr_sale), R.drawable.scan, new TransQrSave(MainActivity.this, handler, transEndListener))
//                .addMenuItem(getString(R.string.app_void), R.drawable.void_1,TopApplication.parameterBean.getVoidEnable(), VoidMenuActivity.class)
                .addTransItem(getString(R.string.app_refund), R.drawable.refund, TopApplication.parameterBean.getRefundEnable(),new TransRefund(MainActivity.this, handler, transEndListener))
                .addMenuItem(getString(R.string.auth_trans), R.drawable.pre_auth,TopApplication.parameterBean.getAuthEnable(), AuthMenuActivity.class)
//                .addTransItem(getString(R.string.title_balance), R.drawable.balance,TopApplication.parameterBean.isBalanceEnable(),
//                        new TransBalance(MainActivity.this, handler, null))
//                .addTransItem(getString(R.string.app_cash_withdrawal), R.drawable.cashier,TopApplication.parameterBean.getTipEnable(),
//                        new TransSaleWithCash(MainActivity.this, handler, transEndListener))
//                .addTransItem(getString(R.string.app_cash_deposit), R.drawable.cash,TopApplication.parameterBean.getTipEnable(),
//                        new TransCashOnly(MainActivity.this, handler, transEndListener))
                // ADDED M-PESA OPTION HERE
                .addMenuItem("M-Pesa STK Push", R.mipmap.spp_phone_setting, MpesaStkActivity.class)
                .addMenuItem(getString(R.string.app_bill), R.mipmap.transaction_detail, DetailActivity.class)
//                .addActionItem(getString(R.string.app_shop), R.mipmap.store, toAppShopActivity())
                .addMenuItem(getString(R.string.app_setting), R.mipmap.settings, SettingMenuActivity.class);
//                 .addMenuItem(getString(R.string.stress_test), R.drawable.settle_accounts, TestMenuActivity.class)
//                 .addMenuItem(getString(R.string.test_mdb),R.mipmap.param_d,TopApplication.parameterBean.isMdbEnable(), MDBTestActivity.class)
//                .addTransItem(getString(R.string.title_qr_code), R.drawable.qr_code,TopApplication.parameterBean.isQrEnable(),
//                        new TransQrCode(MainActivity.this, handler, transEndListener));
        return builder.create();
    }

    private AAction toAppShopActivity() {
        ActionInputpwd actionInputpwd = new ActionInputpwd(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionInputpwd)action).setParam(MainActivity.this,handler,2,
                        getString(R.string.set_please_enter_supervisor_password ),
                        getString(R.string.set_please_enter_pwd));
            }
        });
        actionInputpwd.setEndListener(new AAction.ActionEndListener() {
            @Override
            public void onEnd(AAction action, ActionResult result) {
                String data = (String)result.getData();
                TopApplication.isRuning = false;
                if (TransResult.ERR_ABORTED == result.getRet()){
                    ActivityStack.getInstance().popTo(MainActivity.this);
                    return;
                }

                String sys_pwd = TopApplication.sysParam.get(SysParam.SEC_SYSPWD);
                if (sys_pwd.equals(data)) {
                    PackageManager packageManager = getPackageManager();
                    Utils.startAppshop(MainActivity.this,packageManager);
                } else {
                    TopToast.showFailToast(MainActivity.this,
                            getString(R.string.set_pwd_err));
                }
            }
        });
        return actionInputpwd;
    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected void loadParam() {
        Intent intent = getIntent();
        if (intent != null) {
            ThirdCall thirdCall = new ThirdCall(this, intent, handler);
            thirdCall.doTrans();
        }
    }

    @Override
    protected void handleMsg(Message msg) {
        switch (msg.what) {
            case CHECK_PON_STATER:
                //paynext 不需要登录界面
//                if (Controller.Constant.NO == TopApplication.controller.get(Controller.OPERATOR_LOGON_STATUS) ){
//                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                    startActivity(intent);
//                }
                break;
            case 11:
                TopToast.showNormalToast(MainActivity.this, "交易成功");
                break;
        }
    }

    private ATransaction.TransEndListener transEndListener = new ATransaction.TransEndListener() {
        @Override
        public void onEnd(ActionResult result) {
//            handler.post(new Runnable() {
//
//                @Override
//                public void run() {
//                    AppLog.d(TAG, "TransEndListener end");
//                    hasDoTrans = false;// 重置交易标志位
//                    resetUI();
//                    if (!"topwise".equals(BuildConfig.CHANNEL)) {
//                        toReversal();
//                    }
//                }
//            });
        }
    };

    /**
     * 重置MainActivity界面
     */
    private void resetUI() {
        if (menuPage != null) {
            menuPage.setCurrentPager(0);
        }
    }

    private void toReversal() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppLog.d(TAG, "MainActivity toReversal");
                // 立即冲正，在这处理
                TransProcessListenerImpl listenerImpl = new TransProcessListenerImpl();
                int ret = Transmit.getInstance().sendReversal(listenerImpl);
                listenerImpl.onHideProgress();
                Device.closeAllLed();
            }
        }).start();
    }

    private InputPwdDialog inputPwdDialog;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cashback_flow:
                if (hasDoTrans)
                    return;

                hasDoTrans = true;

                new TransSale(MainActivity.this, handler, transEndListener).execute();
                break;
            case R.id.ll_setting:
                Intent intent =new Intent(MainActivity.this, SettingMenuActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(EUIParamKeys.NAV_TITLE.toString(),getString(R.string.app_setting));
                bundle.putBoolean(EUIParamKeys.NAV_BACK.toString(), true);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.ll_shop:
                AAction appShopAction = toAppShopActivity();
                appShopAction.execute();
                break;
            case R.id.ll_bill:
                Intent intent1 = new Intent(MainActivity.this, DetailActivity.class);
                startActivity(intent1);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // --- FIX: FORCE UNLOCK GLOBAL AND LOCAL FLAGS ---
        hasDoTrans = false;
        TopApplication.isRuning = false; // <--- This line is critical!
        // ------------------------------------------------

        AppLog.d(TAG,"onResume " + TopApplication.isInstallDeviceService);
        handler.sendEmptyMessage(CHECK_PON_STATER);
        SmallScreenUtil.getInstance().showLogo("topwise.bmp");
        MenuPage createMenu = createMenu();
        AppLog.d(TAG, "initMenuPage ======================");
        if(mLayout!=null) {
            mLayout.removeAllViews();
        }
        menuPage = createMenu;
        mLayout.addView(createMenu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            DialogSureCancel dialogSureCancel = new DialogSureCancel(this);
            dialogSureCancel.setMyListener(new IkeyListener() {
                @Override
                public void onConfirm(String text) {
                    SmallScreenUtil.getInstance().destroy();
                    Utils.exit(MainActivity.this);
                }

                @Override
                public void onCancel(int ret) {

                }
            });
            dialogSureCancel.show();
            return  true ;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void requestPermission() {
        PermissionsTool.Builder builder = PermissionsTool.with(this).
                addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE).
                addPermission(Manifest.permission.READ_EXTERNAL_STORAGE).
                addPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE).
                addPermission(Manifest.permission.SYSTEM_ALERT_WINDOW).
                addPermission(Manifest.permission.READ_PHONE_STATE).
                addPermission(Manifest.permission.ACCESS_FINE_LOCATION).
                addPermission(Manifest.permission.ACCESS_COARSE_LOCATION).
                addPermission(Manifest.permission.READ_PHONE_STATE).
                addPermission(Manifest.permission.SYSTEM_ALERT_WINDOW).
                addPermission(Manifest.permission.CAMERA);

        builder.addPermission(Manifest.permission.BLUETOOTH).
                addPermission(Manifest.permission.BLUETOOTH_ADMIN).
                addPermission(Manifest.permission.BLUETOOTH_SCAN).
                addPermission(Manifest.permission.BLUETOOTH_CONNECT);
        builder.initPermission();
    }
}