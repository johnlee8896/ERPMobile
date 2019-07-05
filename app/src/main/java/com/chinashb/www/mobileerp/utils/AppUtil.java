package com.chinashb.www.mobileerp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.view.inputmethod.InputMethodManager;

import com.chinashb.www.mobileerp.APP;

import java.util.List;

/***
 * @date 创建时间 2019/6/18 3:47 PM
 * @author 作者: liweifeng
 * @description
 */
public class AppUtil {
    /**
     * 判断当前应用是否是debug状态
     */
    public static boolean isApkInDebug() {
        try {
            ApplicationInfo info = APP.get().getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 通用的判断某个list是否为空
     */
    public static boolean isListEmpty(List<?> list) {
        return list == null || list.size() == 0;
    }

    public static void forceHideInputMethod(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null && activity.getCurrentFocus().getWindowToken() != null) {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
