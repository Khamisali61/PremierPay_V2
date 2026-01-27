package com.topwise.premierpay.pack.json;

import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.pack.PackListener;

/**
 * 创建日期：2021/4/9 on 9:18
 * 描述:
 * 作者:  wangweicheng
 */
public class JsonPackReveral extends PackJson {
    public JsonPackReveral(PackListener listener) {
        super(listener);
    }

    @Override
    public String pack(TransData transData) {
        setRevCommonData(transData);
        return pack(false);
    }
}
