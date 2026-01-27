package com.topwise.premierpay.menu;

import android.annotation.SuppressLint;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.topwise.premierpay.R;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.app.BaseActivity;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.trans.BaseTrans;
import com.topwise.premierpay.trans.model.EUIParamKeys;
import com.topwise.premierpay.view.MenuPage;

public abstract class BaseMenuActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 9宫格菜单容器
     */
    private LinearLayout llContainer;

    /**
     * 抬头
     */
    private TextView tvTitle;

    /**
     * 返回按钮
     */
    private ImageView IvBack;

    /**
     * 显示的抬头
     */
    private String navTitle;

    /**
     * 是否显示返回按钮
     */
    private boolean navBack;

    @Override
    protected int getLayoutId() {
        return R.layout.menu_layout_a;
    }

    @Override
    protected void loadParam() {
        navTitle = getIntent().getStringExtra(EUIParamKeys.NAV_TITLE.toString());
        navBack = getIntent().getBooleanExtra(EUIParamKeys.NAV_BACK.toString(), false);
    }

    @Override
    protected void initViews() {
        tvTitle = (TextView) findViewById(R.id.header_title);
        tvTitle.setText(navTitle);
        IvBack = (ImageView) findViewById(R.id.header_back);
        llContainer = (LinearLayout) findViewById(R.id.ll_container);
    }

    @Override
    protected void onResume() {
        super.onResume();
        createMenuPage();
        android.widget.LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llContainer.removeAllViews();
        llContainer.addView(createMenuPage(), params);
    }

    public abstract MenuPage createMenuPage();

    @Override
    protected void setListeners() {
        TopApplication.isRuning = false;
        if (!navBack) {
            IvBack.setVisibility(View.GONE);
        } else {
            IvBack.setOnClickListener(this);
        }
    }

    @Override
    protected void handleMsg(Message msg) {

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.header_back:
                // 连续多次点击未处理
                if (BaseTrans.isTransRunning()) { // || MenuQuickClickProtection.getInstance().isTransClicked()
                    return;
                }
//              MenuQuickClickProtection.getInstance().start();

                finish();
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TopApplication.isRuning = false;
        ActivityStack.getInstance().removeActivity(this);
    }
}
