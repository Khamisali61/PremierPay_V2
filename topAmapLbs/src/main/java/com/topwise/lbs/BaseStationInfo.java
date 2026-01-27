package com.topwise.lbs;

/**
 * 创建日期：2021/5/19 on 15:45
 * 描述:
 * 作者:wangweicheng
 */
public class BaseStationInfo {

    private int phoneType;//网络类型 CDMA / GSM

    /***************** GSM 网络 *********************/
    private String lac;// 位置区域码
    private String cid;// 基站编号

    /**************** CDMA 网络 ********************/
    private String SID;// 系统识别码：每个地级市只有一个SID
    private String NID;// 网络识别码：由各本地网管理，也就是由地级分公司分配。每个地级市可能有1到3个NID
    private String BID;// BID：表示的是网络中的某一个小区，可以理解为基站

    // 共有
    private int mcc;// 移动国家代码
    private int mnc;// 移动网络号码

    private String sig;// 基站信号

    public String getLac() {
        return lac;
    }

    public void setLac(String lac) {
        this.lac = lac;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getSID() {
        return SID;
    }

    public void setSID(String sID) {
        SID = sID;
    }

    public String getNID() {
        return NID;
    }

    public void setNID(String nID) {
        NID = nID;
    }

    public String getBID() {
        return BID;
    }

    public void setBID(String bID) {
        BID = bID;
    }

    public int getMcc() {
        return mcc;
    }

    public void setMcc(int mcc) {
        this.mcc = mcc;
    }

    public int getMnc() {
        return mnc;
    }

    public void setMnc(int mnc) {
        this.mnc = mnc;
    }

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    public int getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(int phoneType) {
        this.phoneType = phoneType;
    }

    @Override
    public String toString() {
        return "BaseStationInfo{" +
                "phoneType=" + phoneType +
                ", lac='" + lac + '\'' +
                ", cid='" + cid + '\'' +
                ", SID='" + SID + '\'' +
                ", NID='" + NID + '\'' +
                ", BID='" + BID + '\'' +
                ", mcc=" + mcc +
                ", mnc=" + mnc +
                ", sig='" + sig + '\'' +
                '}';
    }
}
