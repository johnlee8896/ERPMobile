package com.chinashb.www.mobileerp.singleton;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.chinashb.www.mobileerp.APP;
import com.chinashb.www.mobileerp.permission.PermissionsUtil;
import com.chinashb.www.mobileerp.utils.CLog;
import com.chinashb.www.mobileerp.utils.PermissionGroupDefine;


/***
 * @date 创建时间 2018/5/14 20:43
 * @author 作者: liweifeng
 * @description 高德地图定位相关的处理
 */
public class GPSLocationSingleton {
    private final static int LOCATION_INTERVAL_TIME = 2000;
    private final static double EARTH_RADIUS = 6378137.0;
    private AMapLocationClient locationClient;
    private boolean hasRequiredSuccessPermission = false;


    private GPSLocationSingleton() {
    }

    public static GPSLocationSingleton get() {
        return GPSLocationSingletonHelper.instance;
    }

    public double getDistanceByLatLng(double lantitude1, double longitude1, double lantitude2, double longitude2) {
        double radiusLantitude1 = (lantitude1 * Math.PI / 180.0);
        double radiusLantitude2 = (lantitude2 * Math.PI / 180.0);
        double distanceLantitude = radiusLantitude1 - radiusLantitude2;
        double distanceLongitude = (longitude1 - longitude2) * Math.PI / 180.0;
        double angleDistance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(distanceLantitude / 2), 2)
                + Math.cos(radiusLantitude1) * Math.cos(radiusLantitude2)
                * Math.pow(Math.sin(distanceLongitude / 2), 2)));
        angleDistance = angleDistance * EARTH_RADIUS;
        angleDistance = Math.round(angleDistance * 10000) / 10000 ;
        return angleDistance;
    }


    private AMapLocationClient getLocationClient() {
        if (locationClient == null) {
            locationClient = new AMapLocationClient(APP.get());
        }
        return locationClient;
    }

    /*停止定位*/
    public void stopLocationLoop() {
        getLocationClient().stopLocation();
    }

    /*
     * 连续定位
     * */
    public void startLocationLoop(AMapLocationListener aMapLocationListener) {
        startLocationLoop(aMapLocationListener, true);
    }

    public void startLocationLoop(AMapLocationListener aMapLocationListener, boolean isNeedAddress) {
        if (!hasRequiredSuccessPermission){
//            PermissionsUtil.requestPermission(APP.get(), new PermissionListener() {
//                @Override
//                public void permissionAccept(@NonNull String[] permission) {
//                    hasRequiredSuccessPermission = true;
//                    beginLocationWithPermission(isNeedAddress, aMapLocationListener);
//                }
//
//                @Override
//                public void permissionRefused(@NonNull String[] permission) {
//
//                }
//            }, PermissionGroupDefine.PERMISSION_LOCATION_AND_WIFI);

            PermissionsUtil.requestPermission(APP.get(), PermissionGroupDefine.PERMISSION_LOCATION_AND_WIFI, new PermissionsUtil.OnPermissionCallback() {
                @Override
                public void onSuccess(String[] permission) {
                    hasRequiredSuccessPermission = true;
                    beginLocationWithPermission(isNeedAddress, aMapLocationListener);
                }

                @Override
                public void onRefused(String[] permission) {

                }
            });

        }

    }

//    public void startRequestPermission(PermissionListener permissionListener){
//        PermissionsUtil.requestPermission(APP.get(), permissionListener, PermissionGroupDefine.PERMISSION_LOCATION_AND_WIFI);
//    }

    public void startRequestPermission(PermissionsUtil.OnPermissionCallbackImpl onPermissionCallbackIpl){
        PermissionsUtil.requestPermission(APP.get(),PermissionGroupDefine.PERMISSION_LOCATION_AND_WIFI,onPermissionCallbackIpl);
    }

    public void beginLocationWithPermission(boolean isNeedAddress, AMapLocationListener aMapLocationListener) {
        AMapLocationClientOption locationOption = new AMapLocationClientOption();
        //定位精度:高精度模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy)
                //设置定位缓存策略
                .setGpsFirst(false).setSensorEnable(true)
                .setInterval(LOCATION_INTERVAL_TIME).setNeedAddress(isNeedAddress)//可选，设置是否返回逆地理地址信息。默认是ture
                .setOnceLocation(false).setOnceLocationLatest(false);
        locationOption.setLocationCacheEnable(true);

        getLocationClient().setLocationOption(locationOption);
        //设置定位回调监听
        getLocationClient().setLocationListener(aMapLocationListener);
        getLocationClient().startLocation();
    }

    /*获取定位的地址*/
    public void getLocationFormatAddress(final GeocodeSearch.OnGeocodeSearchListener geocodeSearchListener) {
        PermissionsUtil.requestPermission(APP.get(),PermissionGroupDefine.PERMISSION_LOCATION_AND_WIFI, new PermissionsUtil.OnPermissionCallbackImpl(){
            @Override
            public void onSuccess(String[] permission) {
                super.onSuccess(permission);
                AMapLocationListener aMapLocationListener = new AMapLocationListener() {
                    @Override
                    public void onLocationChanged(AMapLocation aMapLocation) {
                        CLog.debug(aMapLocation.toStr());
                        GeocodeSearch geocodeSearch = new GeocodeSearch(APP.get());
                        geocodeSearch.setOnGeocodeSearchListener(geocodeSearchListener);
                        LatLonPoint latLng = new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude());

                        RegeocodeQuery regeocodeQuery = new RegeocodeQuery(latLng, 200, GeocodeSearch.AMAP);
                        geocodeSearch.getFromLocationAsyn(regeocodeQuery);

                    }
                };
                getLocationOnce(aMapLocationListener);
            }
        });
    }

    public void getLocationFormatAddress(final LatLonPoint latLonPoint, final GeocodeSearch.OnGeocodeSearchListener geocodeSearchListener) {


        PermissionsUtil.requestPermission(APP.get(),PermissionGroupDefine.PERMISSION_LOCATION_AND_WIFI,new PermissionsUtil.OnPermissionCallbackImpl(){
            @Override
            public void onSuccess(String[] permission) {
                super.onSuccess(permission);
                GeocodeSearch geocodeSearch = new GeocodeSearch(APP.get());
                geocodeSearch.setOnGeocodeSearchListener(geocodeSearchListener);
                RegeocodeQuery regeocodeQuery = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);
                geocodeSearch.getFromLocationAsyn(regeocodeQuery);
            }
        });
    }

    public void getLocationOnce(AMapLocationListener locationListener) {
        AMapLocationClient locationClient = new AMapLocationClient(APP.get());
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy)
                .setGpsFirst(false).setLocationCacheEnable(false)
                .setNeedAddress(true)//可选，设置是否返回逆地理地址信息。默认是ture
                .setOnceLocation(true)//可选，设置是否单次定位。默认是false
                .setOnceLocationLatest(true);//true表示获取最近3s内精度最高的一次定位结果；false表示使用默认的连续定位策略。

        locationClient.setLocationOption(option);
        locationClient.setLocationListener(locationListener);
        locationClient.startLocation();
    }

    private static class GPSLocationSingletonHelper {
        private static GPSLocationSingleton instance = new GPSLocationSingleton();
    }

}
