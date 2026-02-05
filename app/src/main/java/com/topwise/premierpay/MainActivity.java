package com.topwise.premierpay;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;

import com.topwise.kdialog.DialogSureCancel;
import com.topwise.kdialog.IkeyListener;
import com.topwise.manager.AppLog;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.app.BaseActivity;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.daoutils.DaoUtlis;
import com.topwise.premierpay.menu.AuthMenuActivity;
import com.topwise.premierpay.menu.SettingMenuActivity;
import com.topwise.premierpay.mpesa.MpesaStkActivity;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.thirdcall.ThirdCall;
import com.topwise.premierpay.trans.TransRefund;
import com.topwise.premierpay.trans.TransSale;
import android.widget.TextView;
import android.graphics.Color;
import java.net.InetSocketAddress;
import java.net.Socket;
import com.topwise.premierpay.trans.core.ATransaction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.trans.model.EUIParamKeys;
import com.topwise.premierpay.trans.record.DetailActivity;
import com.topwise.premierpay.transmit.TransProcessListenerImpl;
import com.topwise.premierpay.transmit.iso8583.Transmit;
import com.topwise.premierpay.utils.NetWorkUtils;
import com.topwise.premierpay.utils.PermissionsTool;
import com.topwise.premierpay.utils.SmallScreenUtil;
import com.topwise.premierpay.utils.Utils;
import com.topwise.premierpay.view.TopToast;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = TopApplication.APPNANE + MainActivity.class.getSimpleName();
    private boolean hasDoTrans;
    private final static int CHECK_PON_STATER = 0x01;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Device.isT3Device()) {
             setContentView(R.layout.activity_main);
        }
        super.onCreate(savedInstanceState);
        requestPermission();
        DaoUtlis.initData();
        NetWorkUtils.startMobileSignal(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        // Bind new buttons
        View btnSale = findViewById(R.id.card_sale);
        View btnRefund = findViewById(R.id.card_refund);
        View btnPreAuth = findViewById(R.id.card_preauth);
        View btnReports = findViewById(R.id.card_reports);
        View btnMpesa = findViewById(R.id.card_mpesa);
        View btnSettings = findViewById(R.id.card_settings);

        if (btnSale != null) btnSale.setOnClickListener(this);
        if (btnRefund != null) btnRefund.setOnClickListener(this);
        if (btnPreAuth != null) btnPreAuth.setOnClickListener(this);
        if (btnReports != null) btnReports.setOnClickListener(this);
        if (btnMpesa != null) btnMpesa.setOnClickListener(this);
        if (btnSettings != null) btnSettings.setOnClickListener(this);
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
//                    if (!"topwise".equals(BuildConfig.CHANNEL)) {
//                        toReversal();
//                    }
//                }
//            });
        }
    };

    private void toReversal() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppLog.d(TAG, "MainActivity toReversal");
                TransProcessListenerImpl listenerImpl = new TransProcessListenerImpl();
                int ret = Transmit.getInstance().sendReversal(listenerImpl);
                listenerImpl.onHideProgress();
                Device.closeAllLed();
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        if (TopApplication.isRuning) return;

        Intent intent;
        Bundle bundle;

        switch (v.getId()) {
            case R.id.card_sale:
                if (hasDoTrans) return;
                hasDoTrans = true;
                TopApplication.isRuning = true;
                if (TopApplication.parameterBean.getSaleEnable()){
                     new TransSale(MainActivity.this, handler, transEndListener).execute();
                } else {
                     TopToast.showFailToast(MainActivity.this, getString(R.string.permission_not_allowed));
                     hasDoTrans = false;
                     TopApplication.isRuning = false;
                }
                break;
            case R.id.card_refund:
                if (hasDoTrans) return;
                hasDoTrans = true;
                TopApplication.isRuning = true;
                if (TopApplication.parameterBean.getRefundEnable()){
                    new TransRefund(MainActivity.this, handler, transEndListener).execute();
                } else {
                    TopToast.showFailToast(MainActivity.this, getString(R.string.permission_not_allowed));
                    hasDoTrans = false;
                    TopApplication.isRuning = false;
                }
                break;
            case R.id.card_preauth:
                TopApplication.isRuning = true;
                if (TopApplication.parameterBean.getAuthEnable()) {
                    intent = new Intent(MainActivity.this, AuthMenuActivity.class);
                    bundle = new Bundle();
                    bundle.putString(EUIParamKeys.NAV_TITLE.toString(), getString(R.string.auth_trans));
                    bundle.putBoolean(EUIParamKeys.NAV_BACK.toString(), true);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    TopToast.showFailToast(MainActivity.this, getString(R.string.permission_not_allowed));
                    TopApplication.isRuning = false;
                }
                break;
            case R.id.card_reports:
                TopApplication.isRuning = true;
                intent = new Intent(MainActivity.this, DetailActivity.class);
                bundle = new Bundle();
                bundle.putString(EUIParamKeys.NAV_TITLE.toString(), getString(R.string.app_bill));
                bundle.putBoolean(EUIParamKeys.NAV_BACK.toString(), true);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.card_mpesa:
                TopApplication.isRuning = true;
                intent = new Intent(MainActivity.this, MpesaStkActivity.class);
                bundle = new Bundle();
                bundle.putString(EUIParamKeys.NAV_TITLE.toString(), "M-Pesa STK Push");
                bundle.putBoolean(EUIParamKeys.NAV_BACK.toString(), true);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.card_settings:
                TopApplication.isRuning = true;
                intent = new Intent(MainActivity.this, SettingMenuActivity.class);
                bundle = new Bundle();
                bundle.putString(EUIParamKeys.NAV_TITLE.toString(), getString(R.string.app_setting));
                bundle.putBoolean(EUIParamKeys.NAV_BACK.toString(), true);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hasDoTrans = false;
        TopApplication.isRuning = false;
        AppLog.d(TAG,"onResume " + TopApplication.isInstallDeviceService);
        handler.sendEmptyMessage(CHECK_PON_STATER);
        SmallScreenUtil.getInstance().showLogo("topwise.bmp");
        updateHeaderInfo();
        checkHostConnection();
    }

    private void updateHeaderInfo() {
        TextView tvMerchName = findViewById(R.id.tv_merch_name);
        TextView tvTermId = findViewById(R.id.tv_term_id);
        TextView tvLocation = findViewById(R.id.tv_location);

        SysParam sysParam = SysParam.getInstance(this);
        String merchName = sysParam.get(SysParam.MERCH_NAME);
        String termId = sysParam.get(SysParam.TERMINAL_ID);
        String city = sysParam.get(SysParam.CITY_INFO);

        if (tvMerchName != null) tvMerchName.setText(merchName);
        if (tvTermId != null) tvTermId.setText("TID: " + termId);
        if (tvLocation != null) tvLocation.setText(city);
    }

    private void checkHostConnection() {
        final TextView tvSystemStatus = findViewById(R.id.tv_system_status);
        if (tvSystemStatus == null) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean isConnected;
                String hostIp = SysParam.getInstance(MainActivity.this).get(SysParam.HOSTIP);
                String hostPortStr = SysParam.getInstance(MainActivity.this).get(SysParam.HOSTPORT);
                int hostPort = 0;
                try {
                    hostPort = Integer.parseInt(hostPortStr);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                boolean connected = false;
                if (hostIp != null && !hostIp.isEmpty() && hostPort > 0) {
                    Socket socket = null;
                    try {
                        socket = new Socket();
                        socket.connect(new InetSocketAddress(hostIp, hostPort), 3000);
                        connected = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        connected = false;
                    } finally {
                        if (socket != null) {
                            try { socket.close(); } catch (Exception e) {}
                        }
                    }
                }
                isConnected = connected;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         if (isConnected) {
                            tvSystemStatus.setText("SYSTEM READY");
                            tvSystemStatus.setTextColor(Color.GREEN);
                            tvSystemStatus.setAlpha(1.0f);
                        } else {
                            tvSystemStatus.setText("CONNECTION DOWN");
                            tvSystemStatus.setTextColor(Color.RED);
                            tvSystemStatus.setAlpha(1.0f);
                        }
                    }
                });
            }
        }).start();
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