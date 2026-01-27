package com.topwise.premierpay.setting.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.topwise.premierpay.R;
import com.topwise.premierpay.view.BaseViewHolder;

import java.util.List;

/**
 * 创建日期：2021/3/31 on 15:07
 * 描述:
 * 作者:  wangweicheng 多选
 */
public class SelectAdapter extends BaseAdapter {
    private Context context;
    private List<selectBean> list;
    private boolean [] backList;

    public void select(int position) {
        for (int i = 0; i < list.size(); i++) {
            if (i != position) {
                list.get(i).setSelect(false);
            }
        }
        notifyDataSetChanged();
    }

    private void initBackList(int len) {
        backList = new boolean[len];
//        for (int i = 0; i < backList.length ; i++) {
//            backList[i] = f;
//        }
    }

    public boolean[] getBackList() {
        return backList;
    }

    public SelectAdapter(Context context, List<selectBean> list) {
        super();
        this.context = context;
        this.list = list;
        initBackList(list.size());
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
        selectBean sBean = list.get(position);
        textView.setText(sBean.getTitle());
        if (sBean.isSelect()) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    backList[position] = true;
                } else {
                    backList[position] = false;
                }
            }
        });

//        checkBox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean checked = checkBox.isChecked();
//
//
//            }
//        });
        return convertView;
    }
}
