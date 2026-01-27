package com.topwise.lbs;

/**
 * 创建日期：2021/5/19 on 15:37
 * 描述:
 * 作者:wangweicheng
 */
public class ALocation {
    private double Latitude;
    private double Longitude;
    private String Address;

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public ALocation(double latitude, double longitude, String address) {
        Latitude = latitude;
        Longitude = longitude;
        Address = address;
    }

    @Override
    public String toString() {
        return "ALocation{" +
                "纬度 Latitude=" + Latitude +
                ", 经度 Longitude=" + Longitude +
                ", Address='" + Address + '\'' +
                '}';
    }
}
