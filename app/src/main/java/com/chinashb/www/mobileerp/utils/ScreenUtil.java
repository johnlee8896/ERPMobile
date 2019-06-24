package com.chinashb.www.mobileerp.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.chinashb.www.mobileerp.APP;

/***
 * @date 创建时间 2018/8/13 12:22 PM
 * @author 作者: liweifeng
 * @description 处理屏幕相关的参数
 */
public class ScreenUtil {
    private static int screenWidth, screenHeight;

    public static int getScreenHeight() {
        if (screenHeight <= 0) {
            initScreenSize();
        }
        return screenHeight;
    }

    public static int getScreenWidth() {
        if (screenWidth <= 0) {
            initScreenSize();
        }
        return screenWidth;
    }


    private static void initScreenSize() {
        WindowManager manager = (WindowManager) APP.get().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        screenWidth = outMetrics.widthPixels;
        screenHeight = outMetrics.heightPixels;
    }
}
