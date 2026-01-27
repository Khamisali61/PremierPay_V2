package com.topwise.premierpay.trans.receipt;

import android.graphics.Bitmap;

import com.topwise.premierpay.trans.model.Component;

/**
 * 创建日期：2021/4/2 on 14:07
 * 描述:
 * 作者:  wangweicheng
 */
public interface IReceiptGenerator {
    public static final String TYPE_FACE = Component.FONT_PATH + Component.FONT_NAME;
    public static final int SMALL = 16;
    public static final int NORMAL = 18;
    public static final int LARGE = 32;
    public static final int XLARGE = 48;

    public static final int FONT_BIG = 30;
    public static final int FONT_NORMAL = 24;
    public static final int FONT_SMALL = 22;
    public static final int FONT_SSMALL = 16;
    /**
     * 生成凭单
     *
     * @return
     */
    public Bitmap generateBinmap();
    public Bitmap generateBitmap();
    public String generateStr();
}
