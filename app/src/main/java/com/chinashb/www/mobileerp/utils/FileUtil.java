package com.chinashb.www.mobileerp.utils;

import android.os.Environment;
import android.text.TextUtils;

import com.chinashb.www.mobileerp.APP;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/***
 * @date 创建时间 2018/4/2 19:09
 * @author 作者: yulong
 * @description 文件相关的操作
 */
public class FileUtil {
    public final static String UPLOAD_SUFFIX = "_upload";
    //定位信息的缓存文件夹
    public static String locationCachePath;
    private static String rootPath;
    private static String cachePath;
    private static String ocrPath;
    private static String picturePath;
    private static String tempFilePath;
    private static String downloadPath;

    public static String getRootPath() {
        if (rootPath == null) {
            if (AppUtil.isApkInDebug()) {
                rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Shb/";
            } else {
                rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.shb/";
            }
            checkOrMakeDir(rootPath);
        }
        return rootPath;
    }


    public static boolean checkOrMakeDir(String path) {
        File file = new File(path);
        boolean flag = true;
        if (!file.exists()) {
            flag = file.mkdirs();
            if (!flag) {
//                CLog.e("新建文件夹失败:" + path);
            }
        }
        return flag;
    }



    private static String appFilePath;
    public static String getAPPFilePath(){
        if (appFilePath == null) {
            appFilePath = APP.get().getFilesDir().getAbsolutePath() + "/app/";
            checkOrMakeDir(appFilePath);
        }
        return appFilePath;
    }

    public static String getLocationDataFilePath(String transportId) {
        return getLocationCachePath() + transportId;
    }

    public static String getLocationCachePath() {
        if (locationCachePath == null) {
            locationCachePath = APP.get().getFilesDir() + "gps/";
            checkOrMakeDir(locationCachePath);
        }
        return locationCachePath;
    }


    /**
     * 获取文件流 assert下文件
     */
    public static InputStream getFileFromAsset(String localAsset) {
        InputStream open = null;
        try {
            open = APP.get().getAssets().open(localAsset);
            return open;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                open.close();
            } catch (IOException io) {
                io.printStackTrace();
            }
            return null;
        }
    }

    /**
     * 复制assert文件下js打包文件
     * sourceName为文件名
     * dstName为文件夹名
     */
    public static boolean copyFileFromAssets(String sourceName, String dstDirName) {
        if (TextUtils.isEmpty(sourceName) || TextUtils.isEmpty(dstDirName)) {
            return false;
        }
        File file = new File(dstDirName, sourceName);
        if (file.exists() && file.length() > 0) {
            return true;
        }
        InputStream inputStream = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            inputStream = APP.get().getAssets().open(sourceName);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            byte[] bytes = new byte[1024];
            while (inputStream.read(bytes) > 0) {
                bos.write(bytes, 0, bytes.length);
            }
            inputStream.close();
            bos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (bos != null) {
                    bos.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /*读取asset中的文件内容*/
    public static String getTextFromAssets(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStreamReader inputReader = new InputStreamReader(APP.get().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            while ((line = bufReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    /**
     * 解压缩
     * 将zipFile文件解压到folderPath目录下.
     *
     * @param zipFile    zip文件
     * @param folderPath 解压到的地址
     */
//    public static void upZipFile(File zipFile, String folderPath) throws Exception {
//        ZipFile zFile = new ZipFile(zipFile);
//        Enumeration zList = zFile.entries();
//        ZipEntry ze = null;
//        byte[] buf = new byte[1024];
//        while (zList.hasMoreElements()) {
//            ze = (ZipEntry) zList.nextElement();
//            if (ze.isDirectory()) {
//                String dirstr = folderPath + ze.getName();
//                dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
//                File f = new File(dirstr);
//                f.mkdir();
//                continue;
//            }
//            File realFileName = getRealFileName(folderPath, ze.getName());
//
//            //LocalResSingleton.get().putLocalAssetFile(JsConstants.JS_ASSET_ROOT_PATH + ze.getName(), realFileName.getAbsolutePath());
//            CLog.d(String.format("zip拷贝输出：%s, path = %s", JsConstants.JS_ASSET_ROOT_PATH + ze.getName(),
//                    realFileName.getAbsoluteFile()));
//
//            OutputStream os = new BufferedOutputStream(new FileOutputStream(realFileName));
//            InputStream is = new BufferedInputStream(zFile.getInputStream(ze));
//            int readLen = 0;
//            while ((readLen = is.read(buf, 0, 1024)) != -1) {
//                os.write(buf, 0, readLen);
//            }
//            is.close();
//            os.close();
//        }
//        zFile.close();
//    }

    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     *
     * @param baseDir     指定根目录
     * @param absFileName 相对路径名，来自于ZipEntry中的name
     */
    public static File getRealFileName(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        String substr = null;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                try {
                    substr = new String(substr.getBytes("8859_1"), "GB2312");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                ret = new File(ret, substr);

            }
            if (!ret.exists()) {
                ret.mkdirs();
            }
            substr = dirs[dirs.length - 1];
            try {
                substr = new String(substr.getBytes("8859_1"), "GB2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ret = new File(ret, substr);
            return ret;
        } else {
            if (dirs != null && dirs[0] != null) {
                ret = new File(baseDir, dirs[0]);
            }
        }
        return ret;
    }

    public static String getPicturePath() {
        if (picturePath == null) {
            picturePath = getRootPath().concat("picture/");
            checkOrMakeDir(picturePath);
        }
        return picturePath;
    }


    public static String getTempFilePath() {
        if (tempFilePath == null) {
            tempFilePath = getRootPath().concat("tempFilePath/");
            checkOrMakeDir(tempFilePath);
        }
        return tempFilePath;
    }

    public static String getDownloadPath() {
        if (downloadPath == null) {
            downloadPath = getRootPath().concat("downloadPath/");
            checkOrMakeDir(downloadPath);
        }
        return downloadPath;
    }


    public static String getOCRTransportLicenseFilePath() {
        return getOCRPath() + "transportLicense.jpg";
    }

    /*行驶证的识别的文件路径*/
    public static String getOCRVehicleLicenseFilePath() {
        return getOCRPath() + "VehicleLicense.jpg";
    }

    /*行驶证副页的OCR识别*/
    public static String getOCRVehicleLicenseBackFilePath() {
        return getOCRPath() + "VehicleLicenseBack.jpg";
    }

    /*身份证背面的OCR识别*/
    public static String getOCRIDCardBackFilePath() {
        return getOCRPath() + "idCardBack.jpg";
    }

    /*身份证背面的OCR识别temp*/
    public static String getTempBackFilePath() {
        return getOCRPath() + "idCardBackTemp.jpg";
    }

    /**
     * OCR识别的身份证正面图片路径
     */
    public static String getOCRIDCardFrontFilePath() {
        return getOCRPath() + "idCardFront.jpg";
    }

    /***
     * OCR的营业执照的识别路径
     */
    public static String getOCRBusinessLicenseFilePath() {
        return getOCRPath() + "BusinessLicense.jpg";
    }

    /***
     * OCR的道路许可证
     */
    public static String getOCRRoadPermitFilePath() {
        return getOCRPath() + "RoadPermit.jpg";
    }

    /***
     * OCR识别的驾驶证
     */
    public static String getOCRDriverLicenseFilePath() {
        return getOCRPath() + "DriverLicense.jpg";
    }

    /***
     * OCR识别的车牌号
     */
    public static String getOCRCarLicenseFilePath() {
        return getOCRPath() + "CarLicense.jpg";
    }

    /***
     * OCR银行卡识别的
     */
    public static String getOCRBankCardFilePath() {
        return getOCRPath() + "BankCard.jpg";
    }


    private static String getOCRPath() {
        if (ocrPath == null) {
            ocrPath = getPicturePath().concat("ocr/");
            checkOrMakeDir(ocrPath);
        }
        return ocrPath;
    }


    public static String getCachePath() {
        if (cachePath == null) {
            cachePath = getRootPath().concat("cache/");
            checkOrMakeDir(cachePath);
        }
        return cachePath;
    }

    public static String getUploadFilePath(String transportId) {
        return getLocationCachePath() + transportId + UPLOAD_SUFFIX;
    }

    public static String readTextFromFile(String path) {
        StringBuilder sb = new StringBuilder("");
        FileInputStream inputStream = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                CLog.e("文件不存在:" + path);
                return "";
            }

            inputStream = new FileInputStream(new File(path));
            byte[] temp = new byte[8192];
            int len = 0;
            //读取文件内容:
            while ((len = inputStream.read(temp)) > 0) {
                sb.append(new String(temp, 0, len));
            }
            //关闭输入流
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return sb.toString();
    }

    /*** 将String 写入文件**/
    public static void writeStringToFile(String path, String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }

            //后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            FileWriter filerWriter = new FileWriter(path, true);
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(text);
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File fileCopy(String oldFilePath, String newFilePath) {
        //如果原文件不存在
        if (!new File(oldFilePath).exists()) {
            return null;
        }
        File outFile = null;
        //获得原文件流
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(oldFilePath));
            byte[] data = new byte[4096];
            //输出流
            outFile = new File(newFilePath);
            FileOutputStream outputStream = new FileOutputStream(outFile);
            //开始处理流
            while (inputStream.read(data) != -1) {
                outputStream.write(data);
            }
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return outFile;
    }

    /*删除整个文件夹*/
    public static void deleteFolder(String folderPath) {
        deleteFolder(new File(folderPath));
    }
    /*删除整个文件夹*/
    public static void deleteFolder(File folder) {
        if (folder == null || !folder.exists()) {
            return;
        }
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files == null) {
                return;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteFolder(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
    }

    /***
     * 删除文件
     */
    public static boolean deleteFile(File file) {
        if (file != null && file.exists()) {
            return file.delete();
        }
        return false;
    }

    public static boolean deleteFile(String path) {
        File file = new File(path);
        return deleteFile(file);
    }


}
