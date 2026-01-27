package com.topwise.premierpay.trans.action.activity;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import com.topwise.premierpay.R;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.EUIParamKeys;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.view.ViewUtils;

import java.util.ArrayList;

/**
 * 创建日期：2021/4/13 on 16:04
 * 描述:
 * 作者:  wangweicheng
 */
public class DispTransDetailActivity extends BaseActivityWithTickForAction implements View.OnClickListener {
    private TextView tVtitle,tVtime;
    private ImageView ivBack;
    private Button btnConfirm;
    private Button btnConcel;
    private String navTitle;
    private boolean navBack;
    private LinearLayout llDetailContainer;

    private ArrayList<String> leftColumns = new ArrayList<String>();
    private ArrayList<String> rightColumns = new ArrayList<String>();

    @Override
    public void onClick(View v) {
        ActionResult result = null;
        switch (v.getId()){
            case R.id.confirm_btn:
                result = new ActionResult(TransResult.SUCC, null);
                finish(result);
            case R.id.concel_btn:
                result = new ActionResult(TransResult.ERR_ABORTED, null);
                finish(result);
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail_layout;
    }

    @Override
    protected void initViews() {
        tVtitle = (TextView)findViewById(R.id.header_title);
        tVtitle.setText(navTitle.toUpperCase());

        tVtime = (TextView)findViewById(R.id.header_time);
        llDetailContainer = (LinearLayout) findViewById(R.id.detail_layout);



        btnConfirm  = (Button) findViewById(R.id.confirm_btn);
        btnConcel  = (Button) findViewById(R.id.concel_btn);
        if (navBack){
            btnConcel.setVisibility(View.VISIBLE);
            btnConcel.setOnClickListener(this);
        }else {
            btnConcel.setVisibility(View.GONE);
        }
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.bottomMargin = 15;
        for (int i = 0; i < leftColumns.size(); i++) {
            RelativeLayout layer = ViewUtils.genSingleLineLayout(DispTransDetailActivity.this, leftColumns.get(i),
                    rightColumns.get(i));
            llDetailContainer.addView(layer, params);
        }
    }

    @Override
    protected void setListeners() {
        btnConfirm.setOnClickListener(this);
    }

    @Override
    protected void loadParam() {
        Bundle bundle = getIntent().getExtras();
        navTitle = getIntent().getStringExtra(EUIParamKeys.NAV_TITLE.toString());
        leftColumns = bundle.getStringArrayList(EUIParamKeys.ARRAY_LIST_1.toString());
        rightColumns = bundle.getStringArrayList(EUIParamKeys.ARRAY_LIST_2.toString());
        navBack = getIntent().getBooleanExtra(EUIParamKeys.NAV_BACK.toString(), false);

    }

    @Override
    protected void handleMsg(Message msg) {
        switch (msg.what){
            case TIP_TIME:
                String time = (String)msg.obj;
                if (!TextUtils.isEmpty(time))
                    tVtime.setText(time);
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ActionResult result = new ActionResult(TransResult.ERR_ABORTED, null);
            finish(result);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
