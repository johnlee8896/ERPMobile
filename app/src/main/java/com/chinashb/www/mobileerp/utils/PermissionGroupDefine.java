package com.chinashb.www.mobileerp.utils;

import android.Manifest;

/***
 *@date 创建时间 2019/1/29 19:10
 *@author 作者:liweifeng
 *@description  
 */
public class PermissionGroupDefine {

    public final static String[] PERMISSION_RECORD_VIDEO= {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    public final static String[] PERMISSION_LOCATION_AND_WIF = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE,
    };

    public final static String[] PERMISSION_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,

    };

    public final static String[] PERMISSION_DIAL_PHONE = {
            Manifest.permission.CALL_PHONE,
    };

    public final static String[] PERMISSION_CAMERA = {
            Manifest.permission.CAMERA,
    };


    public final static String[] PERMISSION_SD_CARD = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    public final static String[] PERMISSION_SD_CARD_AND_CAMERA = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };

    public final static String[] PERMISSION_SD_CARD_AND_PHONE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
    };

    public final static String[] PERMISSION_LOCATION_AND_WRITE = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    public final static String[] PERMISSION_LOCATION_AND_WRITE_AND_CAMERA = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public final static String[] PERMISSION_AI_AUDIO = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE

    };

    public final static String[] PERMISSION_ALL_ACTIVITY_FLOAT_WINDOW = {
            Manifest.permission.SYSTEM_ALERT_WINDOW
    };


    public final static String[] PERMISSION_LOCATION_AND_WIFI = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE,
    };


}