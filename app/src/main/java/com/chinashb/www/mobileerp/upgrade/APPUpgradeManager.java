package com.chinashb.www.mobileerp.upgrade;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;

import com.chinashb.www.mobileerp.libapi.bean.BaseBean;
import com.chinashb.www.mobileerp.permission.PermissionsUtil;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CommAlertDialog;
import com.chinashb.www.mobileerp.widget.OnDialogViewClickListener;
import com.google.gson.reflect.TypeToken;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * @date 创建时间 2018/4/19 11:25
 * @author 作者: liweifeng
 * @description APP 升级下载APK相关的处理，使用单例模式
 */
public class APPUpgradeManager {
    /*1, '华瑞物联钰翔版*/
    public static final String NAME_YuXiang = "1";
    /*2, '华瑞物联通用版'*/
    public static final String NAME_logistics = "2";
    /*3, '危化品'*/
    public static final String NAME_hazardous = "3";
    /*4, '银企直连'*/
    public static final String NAME_BCLink = "4";
    /*5, '华瑞物联OA'*/
    public static final String NAME_OA = "5";
    /*6, '.NET客户端'*/
    public static final String NAME_NET = "6";
    /*7, '智能地磅'*/
    public static final String NAME_SmartPound = "7";
    /*8, '材料管家'*/
    public static final String NAME_MaterialsManager = "1";
    /*10, '其它'*/
    public static final String NAME_Other = "10";

    private static boolean hasShowedUpdateDialog = false;
    private DownloadingProgressDialog downloadingProgressDialog;
    private Builder builder;

    private APPUpgradeManager(Builder builder) {
        this.builder = builder;
        downloadingProgressDialog = new DownloadingProgressDialog(builder.context);
    }

    public static Builder with(Context context) {
        return new Builder(context);
    }

    public void checkNewVersion(String nameCode) {
        Map<String, String> map = new HashMap<>();
        //app_name: 钰翔版 - 1，通用版 - 2，危化品 - 3 ，银企直联 -4，其他 - 10
        map.put("app_name", nameCode);
        //app_client : ios - 1, Android - 2
        map.put("app_client", "2");

        map.put("version_name", builder.versionName);
        map.put("app_code", builder.versionCode);

        Type type = new TypeToken<BaseBean<VersionBean>>() {
        }.getType();

        //todo
//        PresenterSingleton.get(builder.APIService).doGetData(null, builder.APIUrl,
//                map, type, new BaseViewInterface() {
//                    @Override
//                    public void onFail(String api, BaseBean msg) {
//                    }
//
//                    @Override
//                    public <T> void initData(String api, T t) {
//                        if (t == null) {
//                            return;
//                        }
//                        VersionBean bean = (VersionBean) t;
//                        switch (bean.getUpdateTag()) {
//                            case 99: //强制更新
//                                showUpdateDialog(bean, true);
//                                break;
//                            case 0: //普通更新
//                                if (!hasShowedUpdateDialog || builder.needShowToast) {
//                                    showUpdateDialog(bean, false);
//                                    hasShowedUpdateDialog = true;
//                                }
//                                break;
//                            case 10: //无更新
//                                if (builder.needShowToast) {
//                                    ToastUtil.showToastShort("已经是最新版了");
//                                }
//                                break;
//                        }
//                    }
//                });
    }

    private void showUpdateDialog(VersionBean appBean, boolean isForceUpdate) {
        CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(this.builder.context)
                .setTitle("版本更新").setMessage(appBean.getDescription())
                .setLeftText("立即升级");

        if (isForceUpdate) {
            builder.setCancelAble(false).setTouchOutsideCancel(false);
        } else {
            builder.setRightText("下次再说");
        }

        builder.setOnViewClickListener(new OnDialogViewClickListener() {
            @Override
            public void onViewClick(Dialog dialog, View v, int tag) {
                switch (tag) {
                    case CommAlertDialog.TAG_CLICK_LEFT:
                        executeUpdateAction(appBean);
                        dialog.dismiss();
                        break;
                    case CommAlertDialog.TAG_CLICK_MIDDLE:
                        dialog.dismiss();
                        break;
                    case CommAlertDialog.TAG_CLICK_RIGHT:
                        dialog.dismiss();
                        break;
                }
            }
        });
        builder.create().show();
    }





    public void showForceUpdateDialog() {
        VersionBean appBean = new VersionBean();
        appBean.setDescription("我就是想更新");
        appBean.setUrl("http://www.chinashb.com/Download/Shberp.apk");
       showUpdateDialog(appBean,true);
    }

    /**
     * 优先申请SD卡的读写权限
     */
    private void executeUpdateAction(VersionBean appBean) {
        if (TextUtils.isEmpty(appBean.getUrl())){
            ToastUtil.showToastShort("下载链接为空");
            return;
        }

        PermissionsUtil.PermissionTipInfo tipsInfo = PermissionsUtil.PermissionTipInfo.newInstance()
                .setMessage("APP升级需要您的SD卡读写权限，请确认允许SD卡读写权限");

        PermissionsUtil.requestPermission(builder.context, tipsInfo, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                },
                true, new PermissionsUtil.OnPermissionCallback() {
                    @Override
                    public void onSuccess(String[] permission) {
                        downloadAPK(appBean);
                    }

                    @Override
                    public void onRefused(String[] permission) {
                        ToastUtil.showToastLong("您拒绝了SD卡的读写权限");
                    }
                });

    }

    public static boolean isApkFile(Context context, String filePath) {
        String pkg;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo info = packageManager.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
            if (info != null) {
                ApplicationInfo appInfo = info.applicationInfo;
                pkg = appInfo.packageName;//得到安装包名称       
            } else {
                pkg = "";
            }
        } catch (Exception e) {
            pkg = "";
        }
        return !TextUtils.isEmpty(pkg);
    }

    private String getAPKPath(VersionBean bean) {
        String path;
        //这个算法主要是为了兼容之前版本直接传的文件路径名，
        // 现在改用这个文件所在的文件
        File file = new File(builder.apkDownloadedPath);
        if (file.exists() && file.isFile()) {
            path = file.getParent();
        } else {
            path = builder.apkDownloadedPath;
        }

        return String.format("%s%s_%s_%s.apk", path,
                builder.appName, bean.getVersionName(), bean.getCode());
    }

    private void downloadAPK(VersionBean bean) {
        downloadingProgressDialog.show();
        downloadingProgressDialog.setCancelable(bean.getUpdateTag() != 99);

        //如果存在这个和APK同名的文件，判断是不是APK，不是的话就删除这个文件
        File apkFile = new File(getAPKPath(bean));
        if (apkFile.exists()) {
            if (!isApkFile(builder.context, getAPKPath(bean))) {
                apkFile.delete();
            }
        }

        FileDownloader.setup(builder.context);
        FileDownloader.getImpl().create(bean.getUrl())
                .setPath(getAPKPath(bean))
                .setListener(new FileDownloadListenerImpl() {
                    @Override
                    protected void started(BaseDownloadTask task) {
                        super.started(task);
                        ToastUtil.showToastLong("正在后台下载更新...");
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        if (downloadingProgressDialog != null && downloadingProgressDialog.isShowing()) {
                            downloadingProgressDialog.updateProgress(soFarBytes, totalBytes);
                        }
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        installAPK(getAPKPath(bean));
//                        KLog.d("下载完成");
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        ToastUtil.showToastShort("下载出错了");
                        e.printStackTrace();
//                        KLog.e("下载出错了:" + task.getEtag(), task.getErrorCause().getMessage());
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
//                        KLog.e(task.getErrorCause().getMessage());
                    }
                }).start();
    }



    private void installAPK(String apkPath) {
        if (!isForeground()) {
            return;
        }
        File apkFile = new File(apkPath);
        if (apkFile == null || !apkFile.exists()) {
            ToastUtil.showToastLong("安装包文件不存在");
            return;
        } else if (!apkFile.exists()) {
            ToastUtil.showToastLong("安装包文件错误");
            return;
        }

        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri apkUri;
        //判读版本是否在7.0以上,解决7.0系统以上的安装apk问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            apkUri = FileProvider.getUriForFile(builder.context, builder.context.getPackageName() + ".fileprovider", apkFile);
        } else {
            apkUri = Uri.fromFile(apkFile);
        }
        installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        builder.context.startActivity(installIntent);
    }

    public boolean isForeground() {
        ActivityManager am = (ActivityManager) builder.context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (builder.context.getPackageName().equals(cpn.getPackageName())) {
                return true;
            }
        }
        return false;
    }


    /***
     *@date 创建时间 2018/11/15 18:01
     *@author 作者: W.YuLong
     *@description 建造者模式的构建Builder
     */
    public static class Builder {
        private Context context;

        @Deprecated
        private String versionName;
        @Deprecated
        private String versionCode;

        private String APIService;
        private String APIUrl;

        private String apkDownloadedPath;
        private String appName;


        private boolean needShowToast;

        public Builder(Context activity) {
            this.context = activity;
        }


        public String getAppName() {
            return appName;
        }

        public Builder setAppName(String appName) {
            this.appName = appName;
            return this;
        }

        public Builder setNeedShowToast(boolean needShowToast) {
            this.needShowToast = needShowToast;
            return this;
        }

        public Builder setVersionName(String versionName) {
            this.versionName = versionName;
            return this;
        }

        public Builder setVersionCode(String versionCode) {
            this.versionCode = versionCode;
            return this;
        }

        public Builder setAPIService(String APIService) {
            this.APIService = APIService;
            return this;
        }

        public Builder setAPIUrl(String APIUrl) {
            this.APIUrl = APIUrl;
            return this;
        }

        public Builder setApkDownloadedPath(String apkDownloadedPath) {
            this.apkDownloadedPath = apkDownloadedPath;
            return this;
        }

        public APPUpgradeManager builder() {
            return new APPUpgradeManager(this);
        }
    }
}
