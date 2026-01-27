package com.topwise.iamge;

import android.content.Context;
import android.util.Log;

import com.topwise.iamge.api.IImgProcessing;
import com.topwise.iamge.impl.ImgProcessing;

/**
 * 创建日期：2021/5/20 on 10:19
 * 描述:
 * 作者:wangweicheng
 */
public class TopImage implements IImage {
    private static final String VERSION = "V1.00";

    static {
        Log.e("TopImage", "TopImage version: " + VERSION);
    }

    private static TopImage instance;
    private Context context;

    private TopImage(Context context) {
        if (context != null) {
            this.context = context.getApplicationContext();
        }
    }

    public synchronized static TopImage getInstance(Context context) {
        if (instance == null) {
            instance = new TopImage(context);
        }
        return instance;
    }
    @Override
    public IImgProcessing getImgProcessing() {
        ImgProcessing imgProcessing = ImgProcessing.getInstance();
        imgProcessing.setContext(context);
        return imgProcessing;
    }
}
