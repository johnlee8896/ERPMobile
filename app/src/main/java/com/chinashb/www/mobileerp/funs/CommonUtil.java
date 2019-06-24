package com.chinashb.www.mobileerp.funs;

import android.content.Context;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.chinashb.www.mobileerp.basicobject.UserInfoEntity;
import com.chinashb.www.mobileerp.permission.PermissionsUtil;
import com.chinashb.www.mobileerp.utils.PermissionGroupDefine;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CommonUtil {
    public static String StringWithSpace(String s, Integer l) {
        String r = s;

        if (s.isEmpty()) {
            r = "";
        }

        for (int i = s.length(); i < l; i++) {
            r = r + "  ";
        }
        return r;
    }

    public static String SqlDate(Date d) {
        String r;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        r = sdf.format(d);


        r = " '" + r + "' ";

        return r;
    }

    public static Date DateAdd(Date date, Integer moredays) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, moredays);
        return cal.getTime();
    }


    public static String DecimalFormat(Float q) {
        DecimalFormat df = new DecimalFormat("####.####");
        String s = df.format(q);
        return s;
    }

    public static String DecimalFormat(double q) {
        DecimalFormat df = new DecimalFormat("####.####");
        String s = df.format(q);
        return s;
    }

    public static String DateYMD(String d) {
        if (d == null) {
            return "";
        }

        if (d.isEmpty()) {
            return "";
        } else {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date lotDate = format.parse(d);
                if (lotDate != null) {
                    return format.format(lotDate);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static void ShowToast(Context context, String Message, Integer image) {
        Toast toast = Toast.makeText(context, Message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        //创建图片视图对象
        ImageView imageView = new ImageView(context);
        //设置图片
        imageView.setImageResource(image);
        //获得toast的布局 8
        LinearLayout toastView = (LinearLayout) toast.getView();
        //设置此布局为横向的 10
        toastView.setOrientation(LinearLayout.HORIZONTAL);
        //将ImageView在加入到此布局中的第一个位置
        toastView.addView(imageView, 0);
        toast.show();
    }

    public static void ShowToast(Context context, String Message, Integer image, Integer length) {
        Toast toast = Toast.makeText(context, Message, length);
        toast.setGravity(Gravity.CENTER, 0, 0);
        //创建图片视图对象
        ImageView imageView = new ImageView(context);
        //设置图片
        imageView.setImageResource(image);
        //获得toast的布局 8
        LinearLayout toastView = (LinearLayout) toast.getView();
        //设置此布局为横向的 10
        toastView.setOrientation(LinearLayout.HORIZONTAL);
        //将ImageView在加入到此布局中的第一个位置
        toastView.addView(imageView, 0);
        toast.show();
    }


    /* @author suncat
     * @category 判断是否有外网连接（普通方法不能判断外网的网络是否连接，比如连接上局域网）
     * @return
     */
    public static final boolean ping() {

        String result = null;
        try {
            String ip = "172.16.1.10";// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping网址3次
            // 读取ping的内容，可以不加
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }
            Log.d("------ping-----", "result content : " + stringBuffer.toString());
            // ping的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {
            Log.d("----result---", "result = " + result);
        }
        return false;
    }


    //将JsonObject转化为HashMap<ColName，ColValue>
    public static HashMap<String, String> Convert_JsonObject_HashMap(JsonObject Selected_Object) {
        HashMap<String, String> Result = new HashMap<String, String>();
        if (Selected_Object != null) {
            Set<String> cols = Selected_Object.keySet();
            Iterator<String> iterator = cols.iterator();
            while (iterator.hasNext()) {
                String colname = iterator.next();
                String colvalue = "";
                if (!Selected_Object.get(colname).isJsonNull()) {
                    colvalue = Selected_Object.get(colname).getAsString();
                }
                Result.put(colname, colvalue);
            }
        }
        return Result;
    }


    public static String getIDSqlStringFromJsonList(List<JsonObject> list, String Member) {
        if (list == null) {
            return " (-9999) ";
        }
        if (list.size() == 0) {
            return " (-9999) ";
        }
        JsonObject o = list.get(0);

        Set<String> cols = o.keySet();
        if (cols.contains(Member) == false) {
            return " (-9999) ";
        }

        String ID;
        String om = o.get(Member).getAsString();
        if (om.isEmpty() || om.equals("null")) {
            ID = "0";
        } else {
            ID = om;
        }

        if (list.size() == 1) {
            return " (" + ID + ")";
        }


        String Result = " (" + ID;
        for (int i = 1; i < list.size(); i++) {
            o = list.get(i);
            om = o.get(Member).getAsString();
            if (om.isEmpty() || om.equals("null")) {
                ID = "0";
            } else {
                ID = om;
            }


            Result = Result + "," + ID;
        }
        Result = Result + ") ";

        return Result;
    }


    public static List<Integer> getIDListFromJsonList(List<JsonObject> list, String Member) {
        List<Integer> l = new ArrayList<Integer>();

        if (list == null) {
            return l;
        }
        if (list.size() == 0) {
            return l;
        }
        JsonObject o = list.get(0);

        Set<String> cols = o.keySet();
        if (cols.contains(Member) == false) {
            return l;
        }


        for (int i = 0; i < list.size(); i++) {
            o = list.get(i);
            String om = o.get(Member).getAsString();
            if (om.isEmpty() || om.equals("null")) {
            } else {
                Integer iID = Integer.valueOf(om);
                if (l.contains(iID) == false) {
                    l.add(iID);
                }
            }
        }
        return l;


    }

    public static void getUsersPic(Context mContext, HashMap<Integer, Bitmap> userPics, List<Integer> HR_IDS) {
        for (int i = 0; i < HR_IDS.size(); i++) {
            getUserPic(mContext, userPics, HR_IDS.get(i));
        }
    }

    public static Bitmap getUserPic(Context mContext, HashMap<Integer, Bitmap> userPics, Integer HR_ID) {
        if (userPics.containsKey(HR_ID)) {
            return userPics.get(HR_ID);
        } else {
            String picFileName = "pic_" + HR_ID + ".png";

            //read from local storage
            Bitmap bi = CommonUtil.getBitmap(mContext, picFileName);

            if (bi == null) {
                bi = WebServiceUtil.getHRPhoto(HR_ID);
                CommonUtil.saveBitmap(mContext, picFileName, bi);
            }

            CommonUtil.updateUserPics(userPics, HR_ID, bi);

            return bi;
        }

    }

    public static void updateUserPics(HashMap<Integer, Bitmap> userPics, Integer HR_ID, Bitmap UserPic) {
        if (userPics == null) {
            userPics = new HashMap<Integer, Bitmap>();
        }
        if (userPics.containsKey(HR_ID) == false) {
            userPics.put(HR_ID, UserPic);
        }
    }

    public static void saveBitmap(final Context mContext, final String mFileName, final Bitmap bitmap) {
        // 首先保存图片
        PermissionsUtil.requestPermission(mContext, PermissionGroupDefine.PERMISSION_SD_CARD,new PermissionsUtil.OnPermissionCallbackImpl(){
            @Override
            public void onSuccess(String[] permission) {
                File appDir = new File(mContext.getExternalCacheDir(), "user_pic");
                if (!appDir.exists()) {
                    appDir.mkdir();
                }
                String fileName = mFileName;
                File file = new File(appDir, fileName);
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 把文件插入到系统图库
                try {
                    MediaStore.Images.Media.insertImage(mContext.getContentResolver(), file.getAbsolutePath(), fileName, null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                // 通知图库更新
                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + mContext.getExternalCacheDir().getPath())));
            }
        });

    }


    public static Bitmap getBitmap(Context mContext, String mFileName) {
        File appDir = new File(mContext.getExternalCacheDir(), "user_pic");
        if (!appDir.exists()) {
            return null;
        }
        String fileName = mFileName;
        File file = new File(appDir, fileName);
        if (!file.exists()) {
            return null;
        }

        String fullfilename = appDir + "/" + fileName;
        try {
            FileInputStream fis = new FileInputStream(fullfilename);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            return bitmap;
        } catch (Exception ex) {
            return null;
        }

    }


    public static String isNothing2String(Object x, String V) {
        if (x == null) {
            return V;
        }
        if (x.equals("anyType{}")) {
            return V;
        }
        return x.toString();

    }

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

                UserInfoEntity.IP = ip;

                if (ip.startsWith("172.16.6.") ||
                        ip.startsWith("172.16.7.") ||
                        ip.startsWith("172.16.8.") ||
                        ip.startsWith("172.16.9.") ||
                        ip.startsWith("172.17.6") ||
                        ip.startsWith("172.17.7") ||
                        ip.startsWith("172.17.8") ||
                        ip.startsWith("172.17.9")) {

                    WebServiceUtil.set_net_link_to_intranet();
                } else {
                    WebServiceUtil.set_net_link_to_internet();
                }

            }

        }
    }

    public static Bitmap pictureBitmap;
    public static HashMap<Integer, Bitmap> userPictureMap = new HashMap<Integer, Bitmap>();

}
