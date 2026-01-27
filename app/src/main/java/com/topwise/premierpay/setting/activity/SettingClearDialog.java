package com.topwise.premierpay.setting.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.topwise.premierpay.R;

import java.util.List;

/**
 * 创建日期：2021/3/31 on 14:55
 * 描述:
 * 作者:  wangweicheng
 */
public class SettingClearDialog extends Dialog {
    private String sTitle;
    private Context context;
    private List<selectBean> selectList;
    private int maxlenth;
    private Handler handler;

    private TextView tVtitle;
    private ListView lVselect;
    private SelectAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View convertView = getLayoutInflater().inflate(R.layout.clear_dialog_layout, null);

        setContentView(convertView);
        getWindow().setGravity(Gravity.BOTTOM); // 显示在底部
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);

        initViews(convertView);
    }

    public SettingClearDialog(Context context, Handler handler, String sTitle, List<selectBean> selectList) {
        //        super(context);
        this(context, R.style.popup_dialog);
        this.context = context;
        this.handler = handler;
        this.sTitle = sTitle;
        this.selectList = selectList;
    }

    public SettingClearDialog(Context context, int theme) {
        super(context, theme);
    }

    private void initViews(View convertView) {
        tVtitle = (TextView)findViewById(R.id.tv_title);
        if (!TextUtils.isEmpty(sTitle)) {
            tVtitle.setText(sTitle);
        }
        setCancelable(false);
        lVselect = (ListView)findViewById(R.id.lv_select);
        mAdapter = new SelectAdapter(context,selectList);
        lVselect.setAdapter(mAdapter);

        lVselect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.select(position);
            }
        });

        ((Button)findViewById(R.id.bt_confirm)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean[] backList = mAdapter.getBackList();
                if (listener != null)
                    listener.onSucc(backList);

                dismiss();
            }
        });

        ((Button)findViewById(R.id.bt_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onCancel();
                dismiss();
            }
        });
    }

    public interface OnListener {
        public void onSucc(boolean []  data);
        public void onCancel();
    }

    private OnListener listener;

    public void setListener(OnListener listener) {
        this.listener = listener;
    }
}
