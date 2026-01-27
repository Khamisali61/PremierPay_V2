package com.topwise.premierpay.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.topwise.premierpay.R;
import com.topwise.premierpay.setting.activity.selectBean;

import java.util.List;

/**
 * 创建日期：2021/4/26 on 19:08
 * 描述:
 * 作者:wangweicheng
 */
public class SelectAidDialog extends Dialog {
    private String sTitle;
    private Context context;
    private List<selectBean> selectList;
    private int maxlenth;
    private Handler handler;

    private TextView tVtitle;
    private ListView lVselect;
    private SelectAidAdapter mAdapter;
    private boolean isf;
    
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

    private void initViews(View convertView) {
        tVtitle = (TextView)findViewById(R.id.tv_title);
        if (!TextUtils.isEmpty(sTitle)){
            tVtitle.setText(sTitle);
        }
        setCancelable(false);
        lVselect = (ListView)findViewById(R.id.lv_select);
        mAdapter = new SelectAidAdapter(context,selectList);
        lVselect.setAdapter(mAdapter);


        ((Button)findViewById(R.id.bt_confirm)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectBean selectBean = mAdapter.getBackList();
                if (listener != null)
                    listener.onSucc(selectBean);

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

    public SelectAidDialog(Context context, int theme) {
        super(context, theme);
    }

    public SelectAidDialog(Context context,  String sTitle, List<selectBean> selectList) {
        //        super(context);
        this(context, R.style.popup_dialog);
        this.context = context;
        this.sTitle = sTitle;
        this.selectList = selectList;
    }

    public interface OnListener {
        public void onSucc(selectBean selectBean);
        public void onCancel();
    }

    private OnListener listener;

    public void setListener(OnListener listener) {
        this.listener = listener;
    }
}


class SelectAidAdapter extends BaseAdapter {
    private Context context;
    private List<selectBean> list;


    public void select(int position) {
        for (int i = 0; i < list.size(); i++) {
            if (i != position) {
                list.get(i).setSelect(false);
            } else {
                list.get(i).setSelect(true);
            }
        }
        notifyDataSetChanged();
    }

    public selectBean getBackList() {
        for (selectBean s:list) {
            if (s.isSelect())
                return s;
        }
        return  null;
    }

    public SelectAidAdapter(Context context, List<selectBean> list) {
        super();
        this.context = context;
        this.list = list;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.select_list_layout, null);
        }
        TextView textView =  BaseViewHolder.get(convertView, R.id.tv_title);
        final CheckBox checkBox =  BaseViewHolder.get(convertView, R.id.cb_setect);
//      checkBox.setClickable(false);
        selectBean sBean = list.get(position);
        textView.setText(sBean.getTitle());

        if (sBean.isSelect()) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select(position);
            }
        });

        return convertView;
    }
}