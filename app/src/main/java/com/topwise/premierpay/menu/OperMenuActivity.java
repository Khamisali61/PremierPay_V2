package com.topwise.premierpay.menu;

import android.text.TextUtils;

import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.operator.OperAddActivity;
import com.topwise.premierpay.operator.OperDelActivity;
import com.topwise.premierpay.operator.OperQueryActivity;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.view.TopToast;
import com.topwise.premierpay.trans.action.ActionInputChangePwd;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.view.MenuPage;

/**
 * 创建日期：2021/3/31 on 17:19
 * 描述:
 * 作者:  wangweicheng
 */
public class OperMenuActivity extends BaseMenuActivity {

    /**
     * 查询操作员， 添加操作员， 删除操作员， 修改操作员密码， 主管改密
     */
    @Override
    public MenuPage createMenuPage() {
        MenuPage.Builder builder = new MenuPage.Builder(OperMenuActivity.this, 4, 2)
                .addMenuItem(getString(R.string.query_oper), R.mipmap.query_operator, OperQueryActivity.class)
                .addMenuItem(getString(R.string.add_oper), R.mipmap.add_operator, OperAddActivity.class)
                .addMenuItem(getString(R.string.delete_oper), R.mipmap.del_operator, OperDelActivity.class)
//                .addMenuItem(getString(R.string.modify_oper_pwd), R.mipmap.modify_passwd, OperChgPwdActivity.class)
                .addActionItem(getString(R.string.modify_manger_pwd), R.mipmap.modify_mag_passwd, toChangePwd());
        return builder.create();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TopApplication.isRuning = false;
    }

    private AAction toChangePwd() {
        ActionInputChangePwd actionInputChangePwd = new ActionInputChangePwd(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionInputChangePwd)action).setParam(OperMenuActivity.this,handler,getString(R.string.modify_manger_pwd));
            }
        });

        actionInputChangePwd.setEndListener(new AAction.ActionEndListener() {
            @Override
            public void onEnd(AAction action, ActionResult result) {
                TopApplication.isRuning = false;
                if (TransResult.SUCC == result.getRet()) {
                    String data = (String)result.getData();
                    if (!TextUtils.isEmpty(data)) {
                        TopApplication.sysParam.set(SysParam.SEC_MNGPWD, data);
                        TopToast.showScuessToast("Modify the success");
                    }
                }
            }
        });

        return actionInputChangePwd;
    }
}
