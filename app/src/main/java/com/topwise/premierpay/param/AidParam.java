package com.topwise.premierpay.param;

import android.text.TextUtils;

import com.topwise.manager.AppLog;
import com.topwise.manager.emv.entity.EmvAidParam;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.tms.bean.AidBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：2021/4/21 on 10:03
 * 描述:
 * 作者:wangweicheng
 */
public class AidParam extends LoadParam<EmvAidParam> {
    public static List<EmvAidParam> aidList = new ArrayList<>();

    @Override
    public boolean DelectAll() {
        aidList.clear();
        EmvAidParam.clearTlvMap();
        return true;
    }

    public static List<EmvAidParam> getEmvAidParamList() {
        return aidList;
    }

    /**
     * 保存全部
     */
    @Override
    public void saveAll() {
        DelectAll();
        //check the TMS whether exist AIDs
        if( TopApplication.parameterBean.getAids()!=null){
           for (AidBean aidBean:TopApplication.parameterBean.getAids()){
               EmvAidParam aidParam = saveAid(aidBean);
               AppLog.d("*EmvAidParam*",aidParam.toString());
               aidList.add(aidParam);
               EmvAidParam.putTlvMap(aidParam.getAid(), aidParam);
           }
        }
    }

    /**
     * 保存单条
     * @param inData
     * @return
     */
    @Override
    public boolean save(String inData) {
        if (TextUtils.isEmpty(inData))
            return false;
        return true;
    }

    public static EmvAidParam getAidFromList(String aid) {
        if (aidList== null || aidList.isEmpty()) {
            return null;
        }
        if (TextUtils.isEmpty(aid)) {
            return null;
        }

        for (EmvAidParam aidParam:aidList) {
            if(aid.equals(aidParam.getAid())) {
                return aidParam;
            }
        }
        return null;
    }

    /**
     * 支持部分匹配，长度长，优先级高
     * @param aid
     * @return
     */
    public static EmvAidParam getCurrentAidParam(String aid) {
        AppLog.e(TAG,"getCurrentAidParam  aid: " + aid);

        EmvAidParam emvAidParam = null;
        for (int i = aid.length(); i >= 10; i= i-2) {
            AppLog.e(TAG,"getCurrentAidParam  i: " + i);
            String subAid = aid.substring(0, i);
            AppLog.e(TAG,"getCurrentAidParam  subAid: " + subAid);
            emvAidParam = getAidFromList(subAid);
            if (emvAidParam != null) {
                break;
            }
        }
        if (emvAidParam == null) {
           AppLog.e(TAG,"getCurrentAidParam  Unable to match AID parameters ====" + aid);
            return null;
        }
        AppLog.e(TAG,"getCurrentAidParam  " + emvAidParam.toString());

        return emvAidParam;
    }
}
