package com.hwariot.lib;

import android.app.Application;

/***
 * @date 创建时间 2018/5/7 20:03
 * @author 作者: YuLong
 * @description Lib的Application，从上层模块传入
 */
public class APILibAPP {

    private static Application application;

    public static Application get() {
        return application;
    }

    public static void init(Application app) {
        application = app;
    }

    private static IBridgeInterface iBridgeInterface;

    public static void setBridgeInterface(IBridgeInterface bridgeInterface) {
        iBridgeInterface = bridgeInterface;
    }

    public static String getToken(){
        return iBridgeInterface.getToken();
    }

    public static String getBaseAPIUrl(String serviceType){
        return iBridgeInterface.getBaseAPIUrl(serviceType);
    }

    public static String getAPIVersion(){
        return iBridgeInterface.getAPIVersion();
    }

    public static String getDefaultService(){
        return iBridgeInterface.getDefaultService();
    }

    public static String getSerialNumberId(){
        return iBridgeInterface.getSerialNumberId();
    }

    public static void onTokenExpired(){
        iBridgeInterface.tokenExpired();
    }


    public interface IBridgeInterface{
        String getToken();
        String getBaseAPIUrl(String serviceType);
        String getDefaultService();
        String getAPIVersion();
        String getSerialNumberId();
        void tokenExpired();
    }

}
