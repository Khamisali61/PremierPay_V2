package com.topwise.premierpay.setting.country;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.action.activity.BaseActivityWithTickForAction;
import com.topwise.premierpay.view.TopToast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CurrencyActivity extends BaseActivityWithTickForAction {
    private CurrencyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((TextView)findViewById(R.id.header_title)).setText(getString(R.string.set_transcation_currency));
        tickTimerStart(120);

        // 初始化测试数据
        List<CurrencyBean> countries = readCurrencyDataFromAssets(this);

        // 设置RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new CurrencyAdapter(countries);
        adapter.setItemClickListener(new CurrencyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CurrencyBean data) {
                Log.v("Jeremy",data.toString());
                TopApplication.sysParam.set(SysParam.APP_PARAM_TRANS_CURRENCY_CODE,String.format("%04d",Integer.valueOf(data.code)));
                TopApplication.sysParam.set(SysParam.APP_PARAM_TER_COUNTRY_CODE,String.format("%04d",Integer.valueOf(data.code)));
                TopApplication.sysParam.set(SysParam.APP_PARAM_TRANS_CURRENCY_NAME,data.name);
                TopApplication.sysParam.set(SysParam.APP_PARAM_TRANS_CURRENCY_SYMBOL,data.symbol+" ");
                TopToast.showFailToast(CurrencyActivity.this, "Currency Code Change to "+ data.name);
                finish();
            }
        });
        recyclerView.setAdapter(adapter);

        // 设置地区筛选Spinner
        Spinner spinner = findViewById(R.id.spinnerRegion);
        ArrayAdapter<CharSequence> regionAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.regions_array,
                android.R.layout.simple_spinner_item
        );
        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(regionAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String region = parent.getItemAtPosition(position).toString();
                adapter.setSelectedRegion(region);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 设置搜索功能
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }
    public List<CurrencyBean> readCurrencyDataFromAssets(Context context) {
        List<CurrencyBean> currencyList = new ArrayList<>();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open("currency.txt");

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }

        // 由于currency.txt的内容是一个JSON数组，我们可以直接解析它
        JSONArray jsonArray = new JSONArray(stringBuilder.toString());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            CurrencyBean currencyData = new CurrencyBean();
            currencyData.code = jsonObject.getString("Currency Code");
            currencyData.name = jsonObject.getString("Country");
            currencyData.symbol = jsonObject.getString("Symbol");
            currencyData.region = jsonObject.getString("Region");

            currencyList.add(currencyData);
        }

    } catch (IOException | JSONException e) {
        e.printStackTrace();
    }

        return currencyList;
}

    @Override
    protected void initViews() {

    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected void loadParam() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_currency;
    }

    @Override
    protected void handleMsg(Message msg) {

    }
}