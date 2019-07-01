package com.chinashb.www.mobileerp.utils;

import android.content.pm.ApplicationInfo;

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
}
