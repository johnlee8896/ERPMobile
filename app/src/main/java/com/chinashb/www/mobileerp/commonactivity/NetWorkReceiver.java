package com.chinashb.www.mobileerp.commonactivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.chinashb.www.mobileerp.basicobject.UserInfoEntity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;

public class NetWorkReceiver extends BroadcastReceiver {


    public static void initNetWorkLink(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //3G/4G
        NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (dataNetworkInfo.isConnected()) {
            WebServiceUtil.set_net_link_to_internet();

        } else {
            //WIFI
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (wifiNetworkInfo.isConnected()) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                String ip;
                int ipAddress = wifiInfo.getIpAddress();
                ip = (ipAddress & 0xFF) + "." +
                        ((ipAddress >> 8) & 0xFF) + "." +
                        ((ipAddress >> 16) & 0xFF) + "." +
                        (ipAddress >> 24 & 0xFF);

//                UserInfoEntity.IP = ip;

                if (ip.startsWith("172.16.6.") ||
                        ip.startsWith("172.16.7.") ||
                        ip.startsWith("172.16.8.") ||
                        ip.startsWith("172.16.9.") ||
                        ip.startsWith("172.17.6") ||
                        ip.startsWith("172.17.7") ||
                        ip.startsWith("172.17.8") ||
                        ip.startsWith("172.17.9")||
                        ip.startsWith("172.17.26")) {

                    WebServiceUtil.set_net_link_to_intranet();
                } else {
                    WebServiceUtil.set_net_link_to_internet();
                }

            }

        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        initNetWorkLink(context);
    }

}

    /*
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("网络状态发生变化");
            //检测API是不是小于23,因为到了API23之后getNetworkInfo(int networkType)方法被弃用
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                //获得ConnectivityManager对象
                ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                //获取ConnectivityManager对象对应的NetworkInfo对象
                //获取WIFI连接的信息
                NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                //获取移动数据连接的信息
                NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                    Toast.makeText(context, "WIFI已连接,移动数据已连接", Toast.LENGTH_SHORT).show();
                } else if (wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
                    Toast.makeText(context, "WIFI已连接,移动数据已断开", Toast.LENGTH_SHORT).show();
                } else if (!wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                    Toast.makeText(context, "WIFI已断开,移动数据已连接", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "WIFI已断开,移动数据已断开", Toast.LENGTH_SHORT).show();
                }
            //API大于23时使用下面的方式进行网络监听
            }else {
                System.out.println("API level 大于23");
                //获得ConnectivityManager对象
                ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                //获取所有网络连接的信息
                Network[] networks = connMgr.getAllNetworks();
                //用于存放网络连接信息
                StringBuilder sb = new StringBuilder();
                //通过循环将网络信息逐个取出来
                for (int i=0; i < networks.length; i++){
                //获取ConnectivityManager对象对应的NetworkInfo对象
                    NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
                    sb.append(networkInfo.getTypeName() + " connect is " + networkInfo.isConnected());
                }
                Toast.makeText(context, sb.toString(),Toast.LENGTH_SHORT).show();
            }
        }

    }
*/




