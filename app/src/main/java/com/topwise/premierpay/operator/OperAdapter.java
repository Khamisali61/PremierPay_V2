package com.topwise.premierpay.operator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.topwise.premierpay.R;
import com.topwise.premierpay.daoutils.entity.Operator;
import com.topwise.premierpay.view.BaseViewHolder;

import java.util.List;

/**
 * 创建日期：2021/3/31 on 17:46
 * 描述:
 * 作者:  wangweicheng
 */
public class OperAdapter extends BaseAdapter {
    private Context context;
    private List<Operator> list;

    public OperAdapter(Context context, List<Operator> list) {
        super();
        this.context = context;
        this.list = list;
    }

    public void add( List<Operator> list){
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.oper_item_layout, null);
        }
        TextView textViewid =  BaseViewHolder.get(convertView, R.id.tv_oper_id);
        TextView textViewname =  BaseViewHolder.get(convertView, R.id.tv_oper_name);

        Operator operator = list.get(position);

        textViewid.setText(operator.getOperId());
        textViewname.setText(operator.getName());

        return convertView;
    }
}
