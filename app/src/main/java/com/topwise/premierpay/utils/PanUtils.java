package com.topwise.premierpay.utils;

import android.text.TextUtils;

/**
 * 创建日期：2021/6/22 on 15:46
 * 描述:
 * 作者:wangweicheng
 */
public class PanUtils {
    /**
     * 前6后4， 其他显示“*”
     *
     * @param cardNo
     * @return
     */
    public static String maskedCardNo(String cardNo) {
        if (TextUtils.isEmpty(cardNo))
            return "";

        char[] tempNum = cardNo.toCharArray();
        int cardLength = tempNum.length;
        // 验证：16-20位数字
        if (cardLength < 13)
            return null;

        for (int i = 0; i < cardLength; i++) {
//            if ((i + 1 > 6) && (i < cardLength - 4)) {
//                tempNum[i] = '*';
//            }
            //Support mask last four digits
            if ( (i < cardLength - 4)) {
                tempNum[i] = '*';
            }
        }
        return new String(tempNum);
    }
}
