package com.topwise.premierpay.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.topwise.premierpay.setting.MCCItem;

import java.util.List;

public class MCCAdapter extends ArrayAdapter<MCCItem> {
    private final LayoutInflater inflater;
    private final int resource;

    public MCCAdapter(Context context, int resource, List<MCCItem> items) {
        super(context, resource, items);
        this.inflater = LayoutInflater.from(context);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(resource, parent, false);
        }

        MCCItem item = getItem(position);
        if (item != null) {
            TextView textView = view.findViewById(android.R.id.text1);
            if (item.isRange()) {
                textView.setText(item.getCode() + " - " + item.getDescription() + " (" + item.getCategory() + ")");
            } else {
                textView.setText(item.getCode() + " - " + item.getDescription() + " (" + item.getCategory() + ")");
            }
        }

        return view;
    }
}