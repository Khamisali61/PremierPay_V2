package com.topwise.premierpay.trans.action.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.topwise.premierpay.R;

import java.util.List;


public class TransAdapter extends BaseAdapter {

    private Context context;
    private List<String[]> list;

    public TransAdapter(Context context, List<String[]> list) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.trans_item_layout, parent, false);
        }
        TextView textViewid = (TextView) convertView.findViewById(R.id.tv_title);
        TextView textViewname = (TextView) convertView.findViewById(R.id.tv_content);
        String[] strings = list.get(position);

        if (String.valueOf(strings[0]).equals(context.getString(R.string.title_balance))) {
            textViewid.setText(strings[0]);
            textViewname.setText(strings[1]);
            textViewname.setTextColor(context.getResources().getColor(R.color.trans_amount_color));
            textViewname.setTextSize(28f);
        } else {
            textViewid.setText(strings[0]);
            textViewname.setText(strings[1]);
        }
        return convertView;
    }
}

