package com.hwariot.lib.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.hwariot.lib.APILibAPP;


/***
 *@date 创建时间 2018/5/8 09:52
 *@author 作者: YuLong
 *@description  网络相关的工具类
 */
public final class NetWorkUtils {
    /**
     * 手机网络类型
     */
    public static final int NET_TYPE_WIFI = 0x01;
    public static final int NET_TYPE_WAP = 0x02;
    public static final int NET_TYPE_NET = 0x03;

    public static boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) APILibAPP.get()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            @SuppressLint("MissingPermission") NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取当前网络类型
     *
     * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
     */
    public static int getNetworkType() {
        int netType = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) APILibAPP
                .get().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (!TextUtils.isEmpty(extraInfo)) {
                if ("cmnet".equals(extraInfo.toLowerCase())) {
                    netType = NET_TYPE_NET;
                } else {
                    netType = NET_TYPE_WAP;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NET_TYPE_WIFI;
        }
        return netType;
    }

}