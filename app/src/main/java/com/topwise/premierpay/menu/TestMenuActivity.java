package com.topwise.premierpay.menu;

import android.os.Message;

import com.topwise.premierpay.R;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.app.UIHandler;
import com.topwise.premierpay.setting.activity.TestParamActivity;
import com.topwise.premierpay.setting.activity.TestReportActivity;
import com.topwise.premierpay.trans.TransAutoSale;
import com.topwise.premierpay.trans.core.ATransaction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.view.MenuPage;

/**
 * 创建日期：2021/4/7 on 16:27
 * 描述:
 * 作者:  wangweicheng
 */
public class TestMenuActivity extends BaseMenuActivity {
    protected UIHandler handler = new UIHandler(this);
    private  String TAG ="TestMenuActivity";
    private final int NEXT_TEST = 0;
    private MenuPage menuPage;
    private TransAutoSale transSale;
    private MenuPage.Builder builder;

    @Override
    protected void loadParam() {
        super.loadParam();
        transSale = new TransAutoSale(this, handler, transEndListener);
    }

    @Override
    protected void handleMsg(Message msg) {
        super.handleMsg(msg);
    }

    @Override
    public MenuPage createMenuPage() {
        builder = new MenuPage.Builder(TestMenuActivity.this, 3, 2)
                .addTransItem(getString(R.string.stress_test), R.mipmap.start_test, transSale)
                .addMenuItem(getString(R.string.test_param_config), R.mipmap.test_param_config, TestParamActivity.class)
                .addMenuItem(getString(R.string.test_report), R.mipmap.test_report, TestReportActivity.class);
        menuPage =builder.create();
        return menuPage;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        builder.setContext(null);
    }

    private ATransaction.TransEndListener transEndListener = new ATransaction.TransEndListener() {
        @Override
        public void onEnd(ActionResult result) {
            ActivityStack.getInstance().popTo(TestMenuActivity.this);
        }
    };
}
