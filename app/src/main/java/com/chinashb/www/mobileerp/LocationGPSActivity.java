package com.chinashb.www.mobileerp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.chinashb.www.mobileerp.basicobject.QueryAsyncTask;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.funs.OnLoadDataListener;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.upgrade.APPUpgradeManager;
import com.chinashb.www.mobileerp.utils.FileUtil;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.group.GroupImageTextLayout;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/***
 * @date 创建时间 2022/10/26 2:40 PM
 * @author 作者: liweifeng
 * @description 获取物流相关信息
 */
public class LocationGPSActivity extends Activity {

    private GroupImageTextLayout tv;

    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logistics_tracking_layout);
        tv = (GroupImageTextLayout) findViewById(R.id.logistics_tracking_location_layout);
        //求俩个经纬度的距离
        /*float[] results=new float[3];
        Location.distanceBetween(100, 200, 200, 400, results);
        tv.setText(results[0]+"米");*/
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location mlocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        updateLocation(mlocation);

        //每隔5秒  2000米的距离
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 2000, new LocationListener() {

            //位置改变
//在设备的位置改变时被调用
            @Override
            public void onLocationChanged(Location location) {
// TODO Auto-generated method stub
                updateLocation(location);
            }

            //在用户禁用具有定位功能的硬件时被调用
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // TODO Auto-generated method stub

            }

            //位置服务可用
            //在用户启动具有定位功能的硬件是被调用
            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub
                if (ActivityCompat.checkSelfPermission(LocationGPSActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationGPSActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                updateLocation(mLocationManager.getLastKnownLocation(provider));
            }

            //在提供定位功能的硬件状态改变是被调用
            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub

            }
        });

        UploadGPSDataTask task = new UploadGPSDataTask();
        task.execute(mlocation);
        checkReaderVersionOk();
    }

    private void updateLocation(Location mlocation) {
        String info;
// TODO Auto-generated method stub
        if (mlocation != null) {
            Geocoder geocoder = new Geocoder(LocationGPSActivity.this, Locale.getDefault());
            try {
                // 获取经纬度对于的位置
                // getFromLocation(纬度, 经度, 最多获取的位置数量)
                List<Address> addresses = geocoder.getFromLocation(mlocation.getLatitude(), mlocation.getLongitude(), 1);
                // 得到第一个经纬度位置解析信息
                Address address = addresses.get(0);
                // 获取到详细的当前位置
                // Address里面还有很多方法你们可以自行实现去尝试。比如具体省的名称、市的名称...
                info = address.getAddressLine(0) + // 获取国家名称
                        address.getAddressLine(1) + // 获取省市县(区)
                        address.getAddressLine(2);  // 获取镇号(地址名称)
                // 赋值
//                nowAddress.setText(info);

                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("经度:" + mlocation.getLongitude());
                stringBuffer.append("纬度:" + mlocation.getLatitude());
                stringBuffer.append("海拔:" + mlocation.getAltitude());
                stringBuffer.append("速度:" + mlocation.getSpeed());
                stringBuffer.append("方向:" + mlocation.getBearing());
                stringBuffer.append("位置:" + info);
                tv.setText(stringBuffer.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 移除位置管理器
            // 需要一直获取位置信息可以去掉这个


        } else {
            tv.setText("正在获取位置信息");
        }
    }

    private class UploadGPSDataTask extends AsyncTask<Location, Void, WsResult> {

        @Override
        protected WsResult doInBackground(Location... locations) {
            Location mLocation = locations[0];
            if (mLocation != null) {
                Geocoder geocoder = new Geocoder(LocationGPSActivity.this, Locale.getDefault());
                try {
                    // 获取经纬度对于的位置
                    // getFromLocation(纬度, 经度, 最多获取的位置数量)
                    List<Address> addresses = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
                    // 得到第一个经纬度位置解析信息
                    Address address = addresses.get(0);
                    // 获取到详细的当前位置
                    // Address里面还有很多方法你们可以自行实现去尝试。比如具体省的名称、市的名称...
                    String info = address.getAddressLine(0) + // 获取国家名称
                            address.getAddressLine(1) + // 获取省市县(区)
                            address.getAddressLine(2);  // 获取镇号(地址名称)

                    WsResult wsResult = WebServiceUtil.get_Logistics_Info_From_Mobile("YD768768787878", "M00800999", "韵达快递", "李先生", "张师傅", "18210443308", 0, new Date(), String.valueOf(mLocation.getLongitude()), String.valueOf(mLocation.getLatitude()),
                            "张经理", "13535353531", new Date(), "", "", info);
                    return wsResult;

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
            return null;

        }

        @Override
        protected void onPostExecute(WsResult result) {
            if (result != null) {
                if (!result.getResult()) {
                    ToastUtil.showToastShort("数据上传成功");

                } else {
                    ToastUtil.showToastShort("数据上传失败，原因 ：" + result.getErrorInfo());
                }

            }
        }


    }


    private void checkReaderVersionOk() {
        String sql = "select top 1 VerID,Version,Convert(nvarchar(100),UpdateDate,23) As UpdateDate, Des " +
                " from Enjoy_Word_Read Where RequireUpdate=1 Order By VerID Desc";
        QueryAsyncTask query = new QueryAsyncTask();
        query.execute(sql);
        query.setLoadDataCompleteListener(new OnLoadDataListener() {
            @Override
            public void loadComplete(List<JsonObject> result) {
                if (result != null && result.size() == 1) {
                    //返回结果，说明网络访问没有问题
//                    isNetReady = true;
                    JsonObject o = result.get(0);
                    Integer ErpVerID = o.get("VerID").getAsInt();
                    //// TODO: 2019/7/19 这里根据数据库中Version 的值来判断 Version为实际versionCode值
//                    String Version = o.get("Version").getAsString();
                    String Version = o.get("Version").getAsString();
                    String UpdateDate = o.get("UpdateDate").getAsString();
                    String updateLog = o.get("Des").getAsString();

                    if (getVersionCode(LocationGPSActivity.this) < Integer.parseInt(Version)) {
                        GetDownloadUrlTask task = new GetDownloadUrlTask();
                        task.execute(updateLog);
                    }

                } else {

                }
            }
        });

    }

    private int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private class GetDownloadUrlTask extends AsyncTask<String, Void, WsResult> {
        //Image hr_photo;
        String updateLog = "";
        @Override
        protected WsResult doInBackground(String... params) {
            if (params != null && params.length > 0 ){
                updateLog = params[0];
            }
            WsResult result = WebServiceUtil.getDownloadUrl();
            return result;
        }

        @Override
        protected void onPreExecute() {
//            scanProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(WsResult result) {
//
            if (result != null && result.getResult() && !TextUtils.isEmpty(result.getErrorInfo())){
                APPUpgradeManager.with(LocationGPSActivity.this)
                        .setNeedShowToast(true)
                        .setApkDownloadedPath(FileUtil.getCachePath())
                        .builder().showForceUpdateDialog(updateLog,result.getErrorInfo());

            }else{
                ToastUtil.showToastShort("获取下载链接失败！");
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }


}
