package com.topwise.premierpay.menu;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import com.topwise.manager.AppLog;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.setting.activity.SettingActivity;
import com.topwise.premierpay.setting.activity.SettingCommManageActivity;
import com.topwise.premierpay.trans.PosLogon;
import com.topwise.premierpay.trans.TransDownloadParamer;
import com.topwise.premierpay.trans.action.ActionInputpwd;
import com.topwise.premierpay.trans.action.ActionTransShowVersion;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ATransaction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.EUIParamKeys;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.view.MenuPage;
import com.topwise.premierpay.view.TopToast;

public class SettingMenuActivity extends BaseMenuActivity {

    @Override
    public MenuPage createMenuPage() {
        int mode = TopApplication.sysParam.getInt(SysParam.DEVICE_MODE);
        int maxItemNum = 6;
        MenuPage.Builder builder =new MenuPage.Builder(SettingMenuActivity.this, maxItemNum, 3);
        builder
//                .addTransItem(getString(R.string.log_in), R.mipmap.logon,
//                        new PosLogon(SettingMenuActivity.this, handler,null))
//                .addTransItem(getString(R.string.log_out),R.mipmap.logout
//                        ,new PosLogout(SettingMenuActivity.this,handler,null))
/*                .addTransItem(getString(R.string.echo), R.mipmap.hear_h,
                        new TransEcho(SettingMenuActivity.this,handler,null))*/
               .addTransItem("Parameter download", R.mipmap.param_d,
                       new TransDownloadParamer(SettingMenuActivity.this, handler,null))
//                .addActionItem("Parameter download", R.mipmap.param_d,netParamDownload())
//                .addActionItem("Application download", R.mipmap.param_d,ApplicationDownload())
                .addActionItem("Device info", R.mipmap.about, showVersion())
//                .addActionItem("Communication parameters",R.mipmap.app_com, setAppcom())
                .addActionItem(getString(R.string.set_setting_device), R.mipmap.spp_phone_setting, setPhoneSetting())
//                .addActionItem(getString(R.string.oper_manage), R.mipmap.oper_manage, toOperMenuActivity())
                .addActionItem(getString(R.string.terminal_pararam_setting), R.mipmap.setting_m, toTermianalParamActivity());
        return builder.create();
    }

    private ATransaction.TransEndListener transEndListener = new ATransaction.TransEndListener() {
        @Override
        public void onEnd(ActionResult result) {
            handler.post(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    };

    private AAction toTermianalParamActivity() {
        ActionInputpwd actionInputpwd = new ActionInputpwd(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionInputpwd)action).setParam(SettingMenuActivity.this,handler,2,
                        getString(R.string.set_please_enter_system_administrator_password ),
                        getString(R.string.set_please_enter_pwd));
            }
        });
        actionInputpwd.setEndListener(new AAction.ActionEndListener() {
            @Override
            public void onEnd(AAction action, ActionResult result) {
                String data = (String)result.getData();
                TopApplication.isRuning = false;
                if (TransResult.ERR_ABORTED == result.getRet()) {
                    ActivityStack.getInstance().pop();
                    return;
                }

                String sys_pwd = TopApplication.sysParam.get(SysParam.SEC_SYSPWD);
                if (sys_pwd.equals(data)) {
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(SettingMenuActivity.this, SettingActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    TopToast.showFailToast(SettingMenuActivity.this,
                            getString(R.string.set_pwd_err));
                }
            }
        });
        return actionInputpwd;
    }

    private AAction toOperMenuActivity() {
        final String sys_pwd = TopApplication.sysParam.get(SysParam.SEC_MNGPWD);
        ActionInputpwd actionInputpwd = new ActionInputpwd(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionInputpwd)action).setParam(SettingMenuActivity.this,handler,1,
                        getString(R.string.set_please_enter_supervisor_password ),
                        getString(R.string.set_please_enter_pwd));
            }
        });
        actionInputpwd.setEndListener(new AAction.ActionEndListener() {
            @Override
            public void onEnd(AAction action, ActionResult result) {
                String data = (String)result.getData();
                TopApplication.isRuning = false;

                if (TransResult.ERR_HOST_REJECT == result.getRet()) {
                    return;
                }
                if (sys_pwd.equals(data)) {
                    Bundle bundle = new Bundle();
                    bundle.putString(EUIParamKeys.NAV_TITLE.toString(), getString(R.string.oper_manage));
                    Intent intent = new Intent(SettingMenuActivity.this, OperMenuActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);

                } else {
                    TopToast.showFailToast(SettingMenuActivity.this,
                            getString(R.string.set_pwd_err));
                }
            }
        });
        return actionInputpwd;
    }

    private AAction setAppcom() {
        ActionInputpwd actionInputpwd = new ActionInputpwd(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionInputpwd)action).setParam(SettingMenuActivity.this,handler,2,
                        getString(R.string.set_please_enter_system_administrator_password ),
                        getString(R.string.set_please_enter_pwd));
            }
        });
        actionInputpwd.setEndListener(new AAction.ActionEndListener() {
            @Override
            public void onEnd(AAction action, ActionResult result) {
                String data = (String)result.getData();
                TopApplication.isRuning = false;
                AppLog.d("SettingMenuActivity","getRet " + result.getRet());
                if (TransResult.ERR_ABORTED == result.getRet()) {
                    ActivityStack.getInstance().pop();
                    return;
                }
                String sys_pwd = TopApplication.sysParam.get(SysParam.SEC_SYSPWD);
                if (sys_pwd.equals(data)) {
                    Intent intent =  new Intent(SettingMenuActivity.this,SettingCommManageActivity.class);
                    startActivity(intent);
                } else {
                    TopToast.showFailToast(SettingMenuActivity.this,
                            getString(R.string.set_pwd_err));
                }
            }
        });

        return actionInputpwd;
    }

    private AAction showVersion() {
        ActionTransShowVersion actionTransShowVersion = new ActionTransShowVersion(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionTransShowVersion)action).setParam(SettingMenuActivity.this);
            }
        });
        actionTransShowVersion.setEndListener(new AAction.ActionEndListener() {
            @Override
            public void onEnd(AAction action, ActionResult result) {
                TopApplication.isRuning = false;
                ActivityStack.getInstance().pop();
            }
        });

        return actionTransShowVersion;
    }

    private AAction setPhoneSetting() {
        ActionInputpwd actionInputpwd = new ActionInputpwd(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionInputpwd)action).setParam(SettingMenuActivity.this,handler,2,
                        getString(R.string.set_please_enter_system_administrator_password ),
                        getString(R.string.set_please_enter_pwd));
            }
        });
        actionInputpwd.setEndListener(new AAction.ActionEndListener() {
            @Override
            public void onEnd(AAction action, ActionResult result) {
                String data = (String)result.getData();
                TopApplication.isRuning = false;
                if (TransResult.ERR_ABORTED == result.getRet()) {
                    return;
                }
                String sys_pwd = TopApplication.sysParam.get(SysParam.SEC_SYSPWD);
                if (sys_pwd.equals(data)) {
                    Intent intent =  new Intent(Settings.ACTION_SETTINGS);
                    startActivity(intent);
                } else {
                    TopToast.showFailToast(SettingMenuActivity.this,
                            getString(R.string.set_pwd_err));
                }

            }
        });

        return actionInputpwd;
    }
}
