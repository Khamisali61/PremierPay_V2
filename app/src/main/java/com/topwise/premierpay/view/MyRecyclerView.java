package com.topwise.premierpay.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.topwise.premierpay.R;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ATransaction;

import java.util.ArrayList;
import java.util.List;

public class MyRecyclerView extends RecyclerView.Adapter<MyRecyclerView.MyViewHolder> {

    private List<MyRecyclerView.GridItem> itemList;

    private Context context;
    private int pageIndex;
    private int maxItemNumPerPage;

    private OnItemClickListener mOnItemClickListener;

    public MyRecyclerView(Context context, List<?> list, int pageIndex, int maxItemNumPerPage) {
        this.context = context;
        this.pageIndex = pageIndex;
        this.maxItemNumPerPage = maxItemNumPerPage;
        itemList = new ArrayList<MyRecyclerView.GridItem>();
        int list_index = pageIndex * maxItemNumPerPage;
        for (int i = list_index; i < list.size(); i++) {
            itemList.add((MyRecyclerView.GridItem) list.get(i));
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_rv_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (position < itemList.size()) {
            holder.tv.setText(getViewText(position));
            holder.iv.setImageResource(getViewIcon(position));
        }
        holder.itemView.setOnClickListener(v -> {
            // Handle click event
            // Change background or other visual indicators
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v,position);
            }
        });
    }

    private int columns = 3;

    public void setColumns(int columns) {
        this.columns = columns;
    }

    @Override
    public int getItemCount() {
        if (maxItemNumPerPage % columns == 0) {
            return maxItemNumPerPage;
        } else {
            return maxItemNumPerPage + (columns - maxItemNumPerPage % columns);
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv;
        public ImageView iv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
             iv = itemView.findViewById(R.id.iv_item);
             tv = itemView.findViewById(R.id.tv_item);
        }
    }

    private Integer getViewIcon(int position) {
        Integer resId = 0;
        MyRecyclerView.GridItem holder = itemList.get(position);
        resId = holder.getIcon();
        return resId;
    }

    private String getViewText(int position) {
        String result = null;
        MyRecyclerView.GridItem holder = itemList.get(position);
        result = holder.getName();
        return result;
    }

    public static class GridItem {
        private String name;
        private int icon;
        private ATransaction trans;
        private Class<?> activity;
        private AAction action;
        private Intent intent;

        public GridItem(String name, int icon, ATransaction trans) {
            this.name = name;
            this.icon = icon;
            this.trans = trans;
        }

        public GridItem(String name, int icon, Class<?> act) {
            this.name = name;
            this.icon = icon;
            this.activity = act;
        }

        public GridItem(String name, int icon, AAction action) {
            this.name = name;
            this.icon = icon;
            this.action = action;
        }

        public GridItem(String name, int icon, Intent intent) {
            this.name = name;
            this.icon = icon;
            this.intent = intent;
        }

        public GridItem(String name, int icon) {
            this.name = name;
            this.icon = icon;
        }

        public int getIcon() {
            return icon;
        }

        public void setIcon(int icon) {
            this.icon = icon;
        }

        public ATransaction getTrans() {
            return trans;
        }

        public void setTrans(ATransaction trans) {
            this.trans = trans;
        }

        public Class<?> getActivity() {
            return activity;
        }

        public void setActivity(Class<?> act) {
            this.activity = act;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public AAction getAction() {
            return action;
        }

        public void setAction(AAction action) {
            this.action = action;
        }

        public void setIntent(Intent intent) {
            this.intent = intent;
        }

        public Intent getIntent() {
            return intent;
        }

    }

    interface OnItemClickListener {
        public void onItemClick(View view,int position);
    }



}
