package com.topwise.premierpay.pack.json;

import android.text.TextUtils;

import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.pack.PackListener;

/**
 * > MTI = 0300
 * >> [DE003] = [000000]
 * >> [DE024] = [1]
 * >> [DE041] = [10017458]
 * >> [DE047] = [00005604YP890000000031]
 * >> [DE061] = [1800008367]
 */
public class JsonPackParamDownload extends PackJson {
    public JsonPackParamDownload(PackListener listener) {
        super(listener);
    }

    @Override
    public String pack(TransData transData) {
        String temp = "";
        try {
            ETransType transType = ETransType.valueOf(transData.getTransType());
            temp = transType.getMsgType();
            if (!TextUtils.isEmpty(temp))
                sendData.setMsgType(temp);

            temp = transType.getProcCode();
            if (!TextUtils.isEmpty(temp))
                sendData.setF003(temp);

            temp = String.format("%06d",transData.getTransNo());
            if (!TextUtils.isEmpty(temp))
                sendData.setF011(temp);

            sendData.setF024("1");

            temp = transData.getTermID();
            if (!TextUtils.isEmpty(temp))
                sendData.setF041(temp);
            temp = transData.getMerchID();
            if (!TextUtils.isEmpty(temp))
                sendData.setF042(temp);

//            jsonBody.put("F060","v1.0");
            temp = Device.getSn();
            if (!TextUtils.isEmpty(temp))
                sendData.setF047(temp);

            temp = TopApplication.sysParam.get(SysParam.TICHET_ID);
            if (!TextUtils.isEmpty(temp))
                sendData.setF061(temp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pack(false);
    }
}