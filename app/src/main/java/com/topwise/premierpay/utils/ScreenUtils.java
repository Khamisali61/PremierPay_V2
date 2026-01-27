package com.topwise.premierpay.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;

public class ScreenUtils {

    public static int getScreenWidth() {
        Resources resources = Resources.getSystem();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight() {
        Resources resources = Resources.getSystem();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        return displayMetrics.heightPixels;
    }
}
