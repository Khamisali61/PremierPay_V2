package com.topwise.premierpay.menu;

import com.topwise.manager.AppLog;
import com.topwise.premierpay.BuildConfig;
import com.topwise.premierpay.R;
import com.topwise.premierpay.trans.TransAuth;
import com.topwise.premierpay.trans.TransAuthCmd;
import com.topwise.premierpay.trans.TransAuthVoid;
import com.topwise.premierpay.trans.core.ATransaction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.transmit.TransProcessListenerImpl;
import com.topwise.premierpay.transmit.iso8583.Transmit;
import com.topwise.premierpay.view.MenuPage;

/**
 * 创建日期：2021/4/6 on 16:59
 * 描述:
 * 作者:  wangweicheng
 */
public class AuthMenuActivity extends BaseMenuActivity {
    private static final String TAG = AuthMenuActivity.class.getSimpleName();
    @Override
    public MenuPage createMenuPage() {
        MenuPage.Builder builder = new MenuPage.Builder(AuthMenuActivity.this, 2, 2)
                .addTransItem(getString(R.string.auth_trans),R.mipmap.app_auth
                        ,new TransAuth(AuthMenuActivity.this, handler, authTransEndListener))
                .addTransItem(getString(R.string.auth_cm),R.mipmap.app_auth_cmd
                        ,new TransAuthCmd(AuthMenuActivity.this, handler, authTransEndListener))
                .addTransItem(getString(R.string.auth_void),R.mipmap.app_auth_void
                        ,new TransAuthVoid(AuthMenuActivity.this,handler, authTransEndListener))
//                .addTransItem(getString(R.string.auth_cm_void),R.mipmap.app_auth_cmd_void
//                        ,new TransAuthCmdVoid(AuthMenuActivity.this,handler,null))
                ;
        return builder.create();
    }

    public ATransaction.TransEndListener authTransEndListener = new ATransaction.TransEndListener() {
        @Override
        public void onEnd(ActionResult result) {
            AppLog.d(TAG, "AuthMenuActivity TransEndListener onEnd");
            AppLog.d(TAG, "AuthMenuActivity TransEndListener handler: " + handler);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    AppLog.d(TAG, "AuthMenuActivity TransEndListener post");
                    if (!"topwise".equals(BuildConfig.CHANNEL)) {
                        toReversal();
                    }
                }
            }).start();
//            handler.post(new Runnable() {
//
//                @Override
//                public void run() {
//                    AppLog.d(TAG, "AuthMenuActivity TransEndListener post");
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
                AppLog.d(TAG, "AuthMenuActivity toReversal");
                // 立即冲正，在这处理
                TransProcessListenerImpl listenerImpl = new TransProcessListenerImpl();
                int ret = Transmit.getInstance().sendReversal(listenerImpl);
                listenerImpl.onHideProgress();
                Device.closeAllLed();
            }
        }).start();
    }

}
