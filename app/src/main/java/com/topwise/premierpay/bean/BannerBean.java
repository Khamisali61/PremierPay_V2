package com.topwise.premierpay.bean;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BannerBean {

    private ArrayList<String> mBannerList = new ArrayList<>();

    public static final String BANNER_CUSTOM_PATH = "/custom/banner/";
    public static final String BANNER_PRODUCT_PATH = "/product/banner/";

    public static final String TOP_WISE_LOGO_MP35P = "file:///android_asset/topwise_logo_mp35p.png";
    public static final String TOP_WISE_LOGO_MP45P = "file:///android_asset/topwise_logo_mp45p.png";
    public static final String TOP_WISE_LOGO_MP35 = "file:///android_asset/topwise_logo_mp35.png";
    public static final String TOP_WISE_LOGO_M3 = "file:///android_asset/topwise_logo_m3.png";
    public static final String TOP_WISE_LOGO_T1 = "file:///android_asset/topwise_logo_t1.png";
    public static final String TOP_WISE_LOGO_T1Pro = "file:///android_asset/topwise_logo_t1p.png";
    public static final String TOP_WISE_LOGO_T3 = "file:///android_asset/topwise_logo_t3.png";

    public BannerBean() {
        mBannerList.add(TOP_WISE_LOGO_T1Pro);
        mBannerList.add(TOP_WISE_LOGO_T3);
        mBannerList.add(TOP_WISE_LOGO_MP45P);
//        mBannerList.add(TOP_WISE_LOGO_MP35);
    }

    public BannerBean(String message){
        this();
        loadBannerPath(BANNER_CUSTOM_PATH);
        loadBannerPath(BANNER_PRODUCT_PATH);
        try {
            JSONObject jsonObject = new JSONObject(message);
            JSONObject data = jsonObject.getJSONObject("data");
            String deviceId = data.getString("deviceId");
            if(deviceId != null) {
                JSONArray jsonArray = data.getJSONArray("viewpager");
                mBannerList.clear();
                for(int i = 0;i < jsonArray.length();i++) {
                    JSONObject imgJSON = jsonArray.getJSONObject(i);
                    int id = imgJSON.getInt("id");
                    String title = imgJSON.getString("title");
                    String pictureAddress = imgJSON.getString("pictureAddress");
                    mBannerList.add(pictureAddress);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadBannerPath(String path) {
        File bannerPath = new File(path);
        if(bannerPath.exists() && bannerPath.isDirectory()) {
            File[] files = bannerPath.listFiles();
            if(files.length > 0) {
                mBannerList.clear();
                for(File file : files) {
                    String name = file.getName();
                    if(name.endsWith("png") || name.endsWith("jpg")) {
                        mBannerList.add(path + name);
                    }
                }
            }
        }
    }

    public List<String> getBannerList() {
        return mBannerList;
    }

}
