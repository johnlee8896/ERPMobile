package com.chinashb.www.mobileerp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/***
 * @date 创建时间 2018/11/15 14:09
 * @author 作者: liweifeng
 * @description 查看崩溃日志的类, 6.0 以上需要动态申请权限
 *              仅供内部使用
 */
public class ExceptionCatchManager implements Thread.UncaughtExceptionHandler {

    private static final String CRASH = "crash";
    private static final String CRASH_FILE_NAME = "crashFileName";
    private static final String VERSION_NAME = "版本名";
    private static final String VERSION_CODE = "版本号";
    private static final String MODEL = "手机厂商";
    private static final String SDK_INT = "SDK版本";
    private static final String PRODUCT = "型号";
    private static final String MOBILE_INFO = "手机信息";
//    private static final String SHSHB_CRASH_LOG = "A_CrashLog";
    private static final String SHSHB_CRASH_LOG = "SHB_CrashLog";
    private static final String FORMAT_TIME = "yyyy年MM月dd日HH时mm分";
    private Context context;
    private Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;

    private ExceptionCatchManager() {
    }

    public static class ExceptionHolder {
        public static ExceptionCatchManager exceptionCatchManager = new ExceptionCatchManager();
    }

    public static ExceptionCatchManager getInstance() {
        return ExceptionHolder.exceptionCatchManager;
    }


    public void init(Context context) {
        this.context = context;
        defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        String crashFileName = saveToSDCard(throwable);
        //如果需要上传文件可以把这个打开
        //cacheCrashFile(crashFileName);
        //让系统默认处理崩溃掉
        defaultUncaughtExceptionHandler.uncaughtException(thread, throwable);
    }

    private void cacheCrashFile(String crashFileName) {
        SharedPreferences sp = context.getSharedPreferences(CRASH, Context.MODE_PRIVATE);
        sp.edit().putString(CRASH_FILE_NAME, crashFileName).commit();
    }

    private String saveToSDCard(Throwable throwable) {
        String fileName = null;
        StringBuffer stringBuffer = new StringBuffer();
        /*for (Map.Entry<String, String> entry : obtainSimpleInfo(context)
                .entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            stringBuffer.append(key).append(" = ").append(value).append("\n");
        }*/
        stringBuffer.append(obtainSimpleInfo(context));
        stringBuffer.append(obtainExceptionInfo(throwable));
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File parentDir = new File(Environment.getExternalStorageDirectory() + File.separator + SHSHB_CRASH_LOG
                    + File.separator);

            if (!parentDir.exists()) {
                parentDir.mkdir();
            }
            try {
                fileName = parentDir.toString()
                        + File.separator
                        + getAppName() + getAssignTime(FORMAT_TIME).concat(".txt");
                FileOutputStream fos = new FileOutputStream(fileName);
                fos.write(stringBuffer.toString().getBytes());
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fileName;
    }

    private String getAssignTime(String dateFormatStr) {
        DateFormat dataFormat = new SimpleDateFormat(dateFormatStr);
        long currentTime = System.currentTimeMillis();
        return dataFormat.format(currentTime);
    }


    /**
     * 获取系统未捕捉的错误信息
     *
     * @param throwable
     * @return
     */
    private String obtainExceptionInfo(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        printWriter.close();
        return stringWriter.toString();
    }

    private String getAppName() {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取一些简单的信息,软件版本，手机版本，型号等信息存放在HashMap中
     *  @return map
     */
    /*private HashMap<String, String> obtainSimpleInfo(Context context) {
        HashMap<String, String> map = new HashMap<>();
        PackageManager mPackageManager = context.getPackageManager();
        PackageInfo mPackageInfo = null;
        try {
            mPackageInfo = mPackageManager.getPackageInfo(
                    context.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        map.put(VERSION_NAME, mPackageInfo.versionName);
        map.put(VERSION_CODE, "" + mPackageInfo.versionCode);
        map.put(MODEL, "" + Build.MODEL);
        map.put(SDK_INT, "" + Build.VERSION.SDK_INT);
        map.put(PRODUCT, "" + Build.PRODUCT);
        map.put(MOBILE_INFO, getMobileInfo());
        return map;
    }*/

    /**
     * 获取一些简单的信息,软件版本，手机版本，型号等信息存放在HashMap中
     * @return StringBuffer
     */
    private StringBuffer obtainSimpleInfo(Context context) {
        StringBuffer stringBuffer = new StringBuffer();
        PackageManager mPackageManager = context.getPackageManager();
        PackageInfo mPackageInfo = null;
        try {
            mPackageInfo = mPackageManager.getPackageInfo(
                    context.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        stringBuffer.append(VERSION_NAME).append(" = ").append(mPackageInfo.versionName).append("\n");
        stringBuffer.append(VERSION_CODE).append(" = ").append(mPackageInfo.versionCode).append("\n");
        stringBuffer.append(MODEL).append(" = ").append( Build.MODEL).append("\n");
        stringBuffer.append(SDK_INT).append(" = ").append( Build.VERSION.SDK_INT).append("\n");
        stringBuffer.append(PRODUCT).append(" = ").append(Build.PRODUCT).append("\n");
        stringBuffer.append(MOBILE_INFO).append(" = ").append(getMobileInfo()).append("\n");
        return stringBuffer;
    }

    /**
     * 获取手机一些信息
     */
    private String getMobileInfo() {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            // 利用反射获取 Build 的所有属性
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String name = field.getName();
                String value = field.get(null).toString();
                stringBuffer.append(name + "=" + value);
                stringBuffer.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }
}
