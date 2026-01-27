package com.topwise.premierpay.setting.country;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.topwise.premierpay.R;

import java.util.ArrayList;
import java.util.List;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.ViewHolder> implements Filterable {
    private List<CurrencyBean> originalList;
    private List<CurrencyBean> filteredList;
    private String selectedRegion = "All";
    private OnItemClickListener listener;



    public CurrencyAdapter(List<CurrencyBean> countries) {
        this.originalList = countries;
        this.filteredList = new ArrayList<>(countries);
    }
    public  void setItemClickListener(OnItemClickListener listener){
       this.listener =  listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_country, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CurrencyBean CurrencyBean = filteredList.get(position);
        holder.name.setText(CurrencyBean.getName());
        holder.region.setText(String.format("%04d",Integer.valueOf(CurrencyBean.getCode())));

    }

    @Override
    public int getItemCount() { return filteredList.size(); }

    public void setSelectedRegion(String region) {
        selectedRegion = region;
        getFilter().filter("");
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<CurrencyBean> filtered = new ArrayList<>();
                String query = constraint.toString().toLowerCase().trim();
                FilterResults results = new FilterResults();


                for (CurrencyBean CurrencyBean : originalList) {
                    boolean matchesRegion = selectedRegion.equals("All")||
                            CurrencyBean.getRegion().equalsIgnoreCase(selectedRegion);

                    boolean matchesSearch = CurrencyBean.getName().toLowerCase().contains(query);

                    if (matchesRegion && matchesSearch) {
                        filtered.add(CurrencyBean);
                    }
                }


                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList.clear();
                filteredList.addAll((List<CurrencyBean>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name, region;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
            region = itemView.findViewById(R.id.tv_region);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (listener != null) {
                listener.onItemClick(filteredList.get(position));
            }
        }
    }

    // 定义一个接口用于回调
    public    interface OnItemClickListener {
        void onItemClick(CurrencyBean data);
    }

}