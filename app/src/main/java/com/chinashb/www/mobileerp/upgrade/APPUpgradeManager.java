package com.chinashb.www.mobileerp.upgrade;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.chinashb.www.mobileerp.APP;
import com.chinashb.www.mobileerp.permission.PermissionsUtil;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CommAlertDialog;
import com.chinashb.www.mobileerp.widget.OnDialogViewClickListener;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.List;
import java.util.Locale;

/**
 * codex by john 2026-11-12 - 无 CDN 升级下载、校验、安装与事件上报
 */
public class APPUpgradeManager {
    private static volatile boolean isDownloading;
    private static volatile String activeSessionId;
    private static volatile long activeVersionCode;
    private static volatile String activePartPath;
    private static volatile int activeTaskId = -1;
    private static volatile APPUpgradeManager activeManager;

    private final Builder builder;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private DownloadingProgressDialog downloadingProgressDialog;
    private MobileUpgradeInfo upgradeInfo;
    private UpgradeDownloadState state;
    private BaseDownloadTask downloadTask;
    private Runnable connectTimeoutRunnable;
    private Runnable readTimeoutRunnable;
    private boolean connected;
    private boolean terminalHandled;
    private long lastProgressBytes;
    private long lastProgressTime;

    private APPUpgradeManager(Builder builder) {
        this.builder = builder;
        this.downloadingProgressDialog = new DownloadingProgressDialog(builder.context);
    }

    public static Builder with(Context context) {
        return new Builder(context);
    }

    public static boolean isDownloading() {
        return isDownloading;
    }

    public static boolean isDownloading(String sessionId, long versionCode, String partPath) {
        return isDownloading
                && TextUtils.equals(activeSessionId, sessionId)
                && activeVersionCode == versionCode
                && TextUtils.equals(activePartPath, partPath);
    }

    public static void cancelIfVersionInstalled(long versionCode) {
        if (!isVersionInstalled(APP.get(), versionCode)) {
            return;
        }
        UpgradeDownloadState savedState = loadSavedStateQuietly();
        if (savedState != null && savedState.versionCode == versionCode) {
            UpgradeStateStore.clearState();
        }
        APPUpgradeManager manager = activeManager;
        if (manager != null && activeVersionCode == versionCode) {
            manager.cancelForInstalledVersion();
        }
    }

    public static boolean isVersionInstalled(Context context, long versionCode) {
        if (context == null || versionCode <= 0) {
            return false;
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo =
                    packageManager.getPackageInfo(context.getPackageName(), 0);
            long installedCode = packageInfo.versionCode;
            return installedCode >= versionCode;
        } catch (Exception e) {
            return false;
        }
    }

    public void showUpgradeDialog(MobileUpgradeInfo info) {
        if (info == null || !info.isShouldUpgrade() || TextUtils.isEmpty(info.getApkUrl())) {
            return;
        }
        if (isVersionInstalled(builder.context, info.getVersionCode())) {
            cancelIfVersionInstalled(info.getVersionCode());
            return;
        }
        this.upgradeInfo = info;
        showUpdateDialog(info.getMessage(), info.isForceUpgrade());
    }

    public void showForceUpdateDialog(String updateLog, String downloadUrl,
                                      int versionCode, long apkSize, String sha256) {
        MobileUpgradeInfo info = new MobileUpgradeInfo();
        info.setShouldUpgrade(true);
        info.setForceUpgrade(true);
        info.setMessage(updateLog == null ? "" : updateLog);
        info.setSessionId("legacy-" + versionCode);
        info.setVersionCode(versionCode);
        info.setApkUrl(downloadUrl == null ? "" : downloadUrl);
        info.setApkSize(apkSize);
        info.setSha256(sha256 == null ? "" : sha256);
        showUpgradeDialog(info);
    }

    public void showForceUpdateDialog(String updateLog, String downloadUrl, int versionCode) {
        showForceUpdateDialog(updateLog, downloadUrl, versionCode, 0L, null);
    }

    public void showForceUpdateDialog(String updateLog, String downloadUrl) {
        int versionCode = parseVersionCode(builder.versionCode);
        showForceUpdateDialog(updateLog, downloadUrl, versionCode, 0L, null);
    }

    private void showUpdateDialog(String message, boolean forceUpgrade) {
        CommAlertDialog.DialogBuilder dialogBuilder =
                new CommAlertDialog.DialogBuilder(builder.context)
                        .setTitle("版本更新")
                        .setMessage(TextUtils.isEmpty(message) ? "发现新版本" : message)
                        .setLeftText("立即升级");

        if (forceUpgrade) {
            dialogBuilder.setCancelAble(false).setTouchOutsideCancel(false);
        } else {
            dialogBuilder.setRightText("下次再说");
        }

        dialogBuilder.setOnViewClickListener(new OnDialogViewClickListener() {
            @Override
            public void onViewClick(Dialog dialog, View v, int tag) {
                switch (tag) {
                    case CommAlertDialog.TAG_CLICK_LEFT:
                        dialog.dismiss();
                        executeUpdateAction(upgradeInfo);
                        break;
                    case CommAlertDialog.TAG_CLICK_RIGHT:
                        dialog.dismiss();
                        break;
                    case CommAlertDialog.TAG_CLICK_MIDDLE:
                        dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
        dialogBuilder.create().show();
    }

    private void executeUpdateAction(final MobileUpgradeInfo info) {
        if (info == null || TextUtils.isEmpty(info.getApkUrl())) {
            ToastUtil.showToastShort("下载链接为空");
            return;
        }

        PermissionsUtil.PermissionTipInfo tipsInfo =
                PermissionsUtil.PermissionTipInfo.newInstance()
                        .setMessage("APP升级需要您的SD卡读写权限，请确认允许SD卡读写权限");

        PermissionsUtil.requestPermission(builder.context, tipsInfo, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                }, true, new PermissionsUtil.OnPermissionCallback() {
                    @Override
                    public void onSuccess(String[] permission) {
                        startDownload(info);
                    }

                    @Override
                    public void onRefused(String[] permission) {
                        ToastUtil.showToastLong("您拒绝了SD卡的读写权限");
                    }
                });
    }

    private void startDownload(MobileUpgradeInfo info) {
        if (info == null || TextUtils.isEmpty(info.getApkUrl())) {
            ToastUtil.showToastShort("下载链接为空");
            return;
        }
        if (isVersionInstalled(builder.context, info.getVersionCode())) {
            cancelIfVersionInstalled(info.getVersionCode());
            return;
        }
        this.upgradeInfo = info;

        UpgradeDownloadState newState = createState(info);
        UpgradeDownloadState savedState = loadSavedState();
        UpgradeDownloadState targetState = savedState;
        if (savedState == null || !TextUtils.equals(savedState.taskKey(), newState.taskKey())) {
            targetState = newState;
        } else {
            targetState.retryIndex = 0;
            targetState.status = UpgradeDownloadState.STATUS_IDLE;
            targetState.downloadTaskId = -1;
            targetState.lastReportedProgressPercent = -1;
            targetState.lastProgressReportTime = 0L;
        }
        startDownloadInternal(info, targetState);
    }

    private void startDownloadInternal(final MobileUpgradeInfo info,
                                       final UpgradeDownloadState targetState) {
        if (isDownloading) {
            if (isDownloading(info.getSessionId(), info.getVersionCode(),
                    targetState.partPath)) {
                showActiveProgressDialog();
                ToastUtil.showToastShort("正在下载升级包，请稍候...");
                return;
            }
            ToastUtil.showToastShort("已有升级下载任务，请稍候...");
            return;
        }

        this.upgradeInfo = info;
        this.state = targetState;
        this.connected = false;
        this.terminalHandled = false;
        this.lastProgressBytes = 0L;
        this.lastProgressTime = 0L;

        targetState.sessionId = info.getSessionId();
        targetState.versionCode = info.getVersionCode();
        targetState.versionName = info.getVersionName();
        targetState.apkUrl = info.getApkUrl();
        targetState.apkSize = info.getApkSize();
        targetState.sha256 = info.getSha256();
        targetState.connectionCount = info.getConnectionCount();
        targetState.connectTimeoutSeconds = info.getConnectTimeoutSeconds();
        targetState.readTimeoutSeconds = info.getReadTimeoutSeconds();
        targetState.retryDelaysSeconds = info.getRetryDelaysSeconds();
        targetState.checkAfterSeconds = info.getCheckAfterSeconds();
        targetState.message = info.getMessage();
        targetState.forceUpgrade = info.isForceUpgrade();
        targetState.targetPath = getAPKPath(info);
        targetState.partPath = getAPKPartPath(info);
        targetState.status = UpgradeDownloadState.STATUS_DOWNLOADING;
        targetState.downloadTaskId = -1;

        isDownloading = true;
        activeSessionId = targetState.sessionId;
        activeVersionCode = targetState.versionCode;
        activePartPath = targetState.partPath;
        activeTaskId = -1;
        activeManager = this;
        UpgradeStateStore.saveState(targetState);

        showDownloadDialog();
        downloadingProgressDialog.showConnecting();
        cleanupLocalFiles(info);
        Log.i("APP_UPDATE", "downloadUrl=" + info.getApkUrl()
                + " targetVersion=" + info.getVersionCode()
                + " partFile=" + targetState.partPath
                + " expectedSize=" + info.getApkSize());

        try {
            FileDownloader.setup(builder.context);
            FileDownloader.getImpl().pauseAll();
            FileDownloader.getImpl().clearAllTaskData();
            FileDownloader.getImpl().setMaxNetworkThreadCount(1);

            downloadTask = FileDownloader.getImpl()
                    .create(appendVersionParam(info.getApkUrl(), info.getVersionCode()))
                    .setPath(targetState.partPath)
                    .setAutoRetryTimes(0)
                    .setListener(new UpgradeDownloadListener());

            UpgradeEventQueue.get().enqueue(
                    targetState.sessionId,
                    targetState.versionCode,
                    UpgradeEventQueue.EVENT_DOWNLOAD_STARTED,
                    "start");

            scheduleConnectTimeout();
            activeTaskId = downloadTask.start();
            targetState.downloadTaskId = activeTaskId;
            UpgradeStateStore.saveState(targetState);
        } catch (Exception e) {
            handleDownloadFailure(
                    UpgradeEventQueue.EVENT_DOWNLOAD_FAILED,
                    classifyFailure(e, false),
                    e);
        }
    }

    private UpgradeDownloadState createState(MobileUpgradeInfo info) {
        UpgradeDownloadState state = new UpgradeDownloadState();
        state.sessionId = info.getSessionId();
        state.versionCode = info.getVersionCode();
        state.versionName = info.getVersionName();
        state.apkUrl = info.getApkUrl();
        state.apkSize = info.getApkSize();
        state.sha256 = info.getSha256();
        state.connectionCount = info.getConnectionCount();
        state.connectTimeoutSeconds = info.getConnectTimeoutSeconds();
        state.readTimeoutSeconds = info.getReadTimeoutSeconds();
        state.retryDelaysSeconds = info.getRetryDelaysSeconds();
        state.checkAfterSeconds = info.getCheckAfterSeconds();
        state.message = info.getMessage();
        state.forceUpgrade = info.isForceUpgrade();
        state.targetPath = getAPKPath(info);
        state.partPath = getAPKPartPath(info);
        return state;
    }

    private UpgradeDownloadState loadSavedState() {
        try {
            return UpgradeStateStore.loadState();
        } catch (Exception e) {
            return null;
        }
    }

    private static UpgradeDownloadState loadSavedStateQuietly() {
        try {
            return UpgradeStateStore.loadState();
        } catch (Exception e) {
            return null;
        }
    }

    private void cleanupLocalFiles(MobileUpgradeInfo info) {
        String apkPath = getAPKPath(info);
        String partPath = getAPKPartPath(info);
        File apkFile = new File(apkPath);
        File partFile = new File(partPath);
        if (apkFile.exists()) {
            apkFile.delete();
        }
        if (partFile.exists()) {
            partFile.delete();
        }

        File dir = getUpgradeDirectory(builder.context);
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            String name = file.getName();
            if (file.getAbsolutePath().equals(apkPath)
                    || file.getAbsolutePath().equals(partPath)) {
                continue;
            }
            if ((name.startsWith("Shberp-") || name.equalsIgnoreCase("SHBERP.apk"))
                    && (name.endsWith(".apk") || name.endsWith(".part")
                    || name.endsWith(".temp"))) {
                file.delete();
            }
        }
    }

    private String appendVersionParam(String url, long versionCode) {
        if (TextUtils.isEmpty(url) || versionCode <= 0 || url.contains("?")) {
            return url;
        }
        return url + "?v=" + versionCode;
    }

    private void scheduleConnectTimeout() {
        cancelConnectTimeout();
        final String key = state.taskKey();
        connectTimeoutRunnable = () -> {
            if (!connected && isDownloading(state.sessionId, state.versionCode, state.partPath)) {
                handleDownloadFailure("CONNECT_TIMEOUT",
                        "CONNECT_TIMEOUT", new java.net.SocketTimeoutException("connect timeout"));
            }
        };
        handler.postDelayed(connectTimeoutRunnable,
                state.connectTimeoutSeconds * 1000L);
    }

    private void cancelConnectTimeout() {
        if (connectTimeoutRunnable != null) {
            handler.removeCallbacks(connectTimeoutRunnable);
            connectTimeoutRunnable = null;
        }
    }

    private void scheduleReadTimeout() {
        cancelReadTimeout();
        if (!connected) {
            return;
        }
        readTimeoutRunnable = () -> {
            if (connected && isDownloading(state.sessionId, state.versionCode, state.partPath)) {
                handleDownloadFailure("READ_TIMEOUT",
                        "READ_TIMEOUT", new java.net.SocketTimeoutException("read timeout"));
            }
        };
        handler.postDelayed(readTimeoutRunnable, state.readTimeoutSeconds * 1000L);
    }

    private void cancelReadTimeout() {
        if (readTimeoutRunnable != null) {
            handler.removeCallbacks(readTimeoutRunnable);
            readTimeoutRunnable = null;
        }
    }

    private void handleDownloadFailure(String eventType, String errorCode, Throwable e) {
        if (terminalHandled) {
            return;
        }
        terminalHandled = true;
        cancelConnectTimeout();
        cancelReadTimeout();
        stopCurrentDownloadTask(true);

        if (state != null) {
            state.status = UpgradeDownloadState.STATUS_FAILED;
            state.downloadTaskId = -1;
            UpgradeStateStore.saveState(state);
        }
        UpgradeEventQueue.get().enqueue(
                state == null ? "" : state.sessionId,
                state == null ? 0L : state.versionCode,
                UpgradeEventQueue.EVENT_DOWNLOAD_FAILED,
                errorCode);

        List<Integer> retryDelays = upgradeInfo == null
                ? java.util.Arrays.asList(5, 15, 30)
                : upgradeInfo.getRetryDelaysSeconds();
        boolean canRetry = isRetryableFailure(errorCode)
                && state != null
                && state.retryIndex < retryDelays.size();

        if (canRetry) {
            int delaySeconds = retryDelays.get(state.retryIndex);
            state.retryIndex++;
            state.status = UpgradeDownloadState.STATUS_IDLE;
            UpgradeStateStore.saveState(state);
            if (downloadingProgressDialog != null && downloadingProgressDialog.isShowing()) {
                downloadingProgressDialog.showMessage(
                        "下载失败：" + errorCode + "，" + delaySeconds + "秒后重试");
            }
            final MobileUpgradeInfo retryInfo = upgradeInfo;
            final UpgradeDownloadState retryState = state;
            handler.postDelayed(() -> {
                clearActive(false);
                terminalHandled = false;
                startDownloadInternal(retryInfo, retryState);
            }, delaySeconds * 1000L);
            return;
        }

        if (state != null) {
            state.retryIndex = retryDelays.size();
            UpgradeStateStore.saveState(state);
        }
        ToastUtil.showToastLong("升级包下载失败：" + errorCode);
        clearActive(true);
    }

    private boolean isRetryableFailure(String errorCode) {
        return !"USER_CANCELLED".equals(errorCode)
                && !"SIZE_MISMATCH".equals(errorCode)
                && !"SHA256_MISMATCH".equals(errorCode)
                && !"NO_SPACE".equals(errorCode);
    }

    private void stopCurrentDownloadTask(boolean deletePartFile) {
        cancelConnectTimeout();
        cancelReadTimeout();
        BaseDownloadTask task = downloadTask;
        downloadTask = null;
        if (task != null) {
            try {
                task.pause();
                if (task.getId() > 0 && state != null) {
                    FileDownloader.getImpl().clear(task.getId(), state.partPath);
                }
            } catch (Exception ignored) {
            }
        }
        if (deletePartFile && state != null) {
            new File(state.partPath).delete();
        }
    }

    private void verifyAndInstall(final MobileUpgradeInfo info,
                                  final UpgradeDownloadState downloadState,
                                  final File partFile) {
        if (downloadingProgressDialog != null && downloadingProgressDialog.isShowing()) {
            downloadingProgressDialog.showVerifying();
        }

        new Thread(() -> {
            String failureCode = null;
            if (info.getApkSize() > 0 && partFile.length() != info.getApkSize()) {
                failureCode = "SIZE_MISMATCH";
            } else if (!TextUtils.isEmpty(info.getSha256())) {
                String actualSha256 = calculateSha256(partFile);
                if (actualSha256 == null
                        || !info.getSha256().equalsIgnoreCase(actualSha256)) {
                    failureCode = "SHA256_MISMATCH";
                }
            }
            if (failureCode == null) {
                failureCode = verifyApkIdentity(partFile, info.getVersionCode());
            }

            final String resultCode = failureCode;
            handler.post(() -> finishVerification(info, downloadState,
                    partFile, resultCode));
        }, "upgrade-apk-verify").start();
    }

    private void finishVerification(MobileUpgradeInfo info,
                                    UpgradeDownloadState downloadState,
                                    File partFile,
                                    String failureCode) {
        if (!TextUtils.isEmpty(failureCode)) {
            partFile.delete();
            downloadState.status = UpgradeDownloadState.STATUS_FAILED;
            downloadState.downloadTaskId = -1;
            UpgradeStateStore.saveState(downloadState);
            UpgradeEventQueue.get().enqueue(
                    downloadState.sessionId,
                    downloadState.versionCode,
                    UpgradeEventQueue.EVENT_DOWNLOAD_FAILED,
                    failureCode);
            ToastUtil.showToastLong("升级包校验失败：" + failureCode);
            clearActive(true);
            return;
        }

        File apkFile = new File(getAPKPath(info));
        if (apkFile.exists()) {
            apkFile.delete();
        }
        if (!partFile.renameTo(apkFile)) {
            partFile.delete();
            downloadState.status = UpgradeDownloadState.STATUS_FAILED;
            UpgradeStateStore.saveState(downloadState);
            UpgradeEventQueue.get().enqueue(
                    downloadState.sessionId,
                    downloadState.versionCode,
                    UpgradeEventQueue.EVENT_DOWNLOAD_FAILED,
                    "UNKNOWN");
            ToastUtil.showToastLong("升级包处理失败，请重试");
            clearActive(true);
            return;
        }

        downloadState.targetPath = apkFile.getAbsolutePath();
        downloadState.partPath = getAPKPartPath(info);
        downloadState.status = UpgradeDownloadState.STATUS_VERIFIED;
        downloadState.retryIndex = 0;
        downloadState.downloadTaskId = -1;
        UpgradeStateStore.saveState(downloadState);
        UpgradeEventQueue.get().enqueue(
                downloadState.sessionId,
                downloadState.versionCode,
                UpgradeEventQueue.EVENT_DOWNLOAD_SUCCESS,
                "");

        clearActive(false);
        installAPK(apkFile, downloadState);
    }

    private String verifyApkIdentity(File apkFile, long expectedVersionCode) {
        try {
            PackageManager packageManager = builder.context.getPackageManager();
            PackageInfo archiveInfo =
                    packageManager.getPackageArchiveInfo(apkFile.getAbsolutePath(), 0);
            if (archiveInfo == null
                    || !TextUtils.equals(builder.context.getPackageName(), archiveInfo.packageName)) {
                return "PACKAGE_MISMATCH";
            }
            long downloadedVersionCode = archiveInfo.versionCode;
            if (downloadedVersionCode != expectedVersionCode) {
                Log.i("APP_UPDATE", "APK版本错误 target=" + expectedVersionCode
                        + " file=" + downloadedVersionCode);
                return "VERSION_MISMATCH";
            }
            return null;
        } catch (Exception e) {
            Log.w("APP_UPDATE", "APK身份校验失败", e);
            return "PACKAGE_MISMATCH";
        }
    }

    private void installAPK(File apkFile, UpgradeDownloadState downloadState) {
        if (!isForeground()) {
            return;
        }
        if (!apkFile.exists()) {
            ToastUtil.showToastLong("安装包文件不存在");
            return;
        }
        Log.i("APP_UPDATE", "installFile=" + apkFile.getAbsolutePath()
                + " length=" + apkFile.length());

        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri apkUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            apkUri = FileProvider.getUriForFile(
                    builder.context,
                    builder.context.getPackageName() + ".fileprovider",
                    apkFile);
        } else {
            apkUri = Uri.fromFile(apkFile);
        }
        installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");

        UpgradeEventQueue.get().enqueue(
                downloadState.sessionId,
                downloadState.versionCode,
                UpgradeEventQueue.EVENT_INSTALL_STARTED,
                "");
        try {
            builder.context.startActivity(installIntent);
        } catch (Exception e) {
            ToastUtil.showToastLong("无法调起安装界面");
        }
        dismissDownloadDialog();
    }

    private String calculateSha256(File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        InputStream in = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            in = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            byte[] hash = digest.digest();
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format(Locale.US, "%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    private String classifyFailure(Throwable e, boolean connectedNow) {
        if (e == null) {
            return "UNKNOWN";
        }
        String name = e.getClass().getName();
        String message = e.getMessage() == null ? "" : e.getMessage();
        if (name.contains("OutOfSpace") || message.toLowerCase(Locale.US).contains("no space")) {
            return "NO_SPACE";
        }
        if (name.contains("SocketTimeout") || message.contains("timeout")) {
            return connectedNow ? "READ_TIMEOUT" : "CONNECT_TIMEOUT";
        }
        if (message.matches(".*\\b4[0-9]{2}\\b.*")) {
            return "HTTP_4XX";
        }
        if (message.matches(".*\\b5[0-9]{2}\\b.*")) {
            return "HTTP_5XX";
        }
        if (isNotEnoughSpace()) {
            return "NO_SPACE";
        }
        return "UNKNOWN";
    }

    private boolean isNotEnoughSpace() {
        if (state == null || state.apkSize <= 0) {
            return false;
        }
        try {
            File target = new File(state.partPath);
            StatFs statFs = new StatFs(target.getParentFile().getAbsolutePath());
            long available = statFs.getAvailableBytes();
            return available < state.apkSize;
        } catch (Exception e) {
            return false;
        }
    }

    private void clearActive(boolean dismissDialog) {
        isDownloading = false;
        activeSessionId = "";
        activeVersionCode = 0L;
        activePartPath = "";
        activeTaskId = -1;
        activeManager = null;
        if (dismissDialog) {
            dismissDownloadDialog();
        }
    }

    private void cancelForInstalledVersion() {
        terminalHandled = true;
        cancelConnectTimeout();
        cancelReadTimeout();
        stopCurrentDownloadTask(true);
        if (state != null && state.versionCode == activeVersionCode) {
            UpgradeStateStore.clearState();
        }
        clearActive(true);
    }

    private void showActiveProgressDialog() {
        APPUpgradeManager manager = activeManager;
        if (manager != null && manager.downloadingProgressDialog != null
                && !manager.downloadingProgressDialog.isShowing()) {
            manager.downloadingProgressDialog.show();
        }
    }

    private void showDownloadDialog() {
        if (downloadingProgressDialog != null && !downloadingProgressDialog.isShowing()) {
            downloadingProgressDialog.show();
        }
        downloadingProgressDialog.setCancelable(false);
    }

    private void dismissDownloadDialog() {
        if (downloadingProgressDialog != null && downloadingProgressDialog.isShowing()) {
            downloadingProgressDialog.dismiss();
        }
    }

    private boolean isForeground() {
        Activity topActivity = APP.getTopActivity();
        return topActivity != null && !topActivity.isFinishing();
    }

    private String getAPKPath(MobileUpgradeInfo info) {
        File dir = getUpgradeDirectory(builder.context);
        String fileName = info.getVersionCode() > 0
                ? String.format(Locale.US, "Shberp-%d.apk", info.getVersionCode())
                : "SHBERP.apk";
        return new File(dir, fileName).getAbsolutePath();
    }

    private String getAPKPartPath(MobileUpgradeInfo info) {
        return getAPKPath(info) + ".part";
    }

    private static File getUpgradeDirectory(Context context) {
        File externalFilesDir = context.getExternalFilesDir(null);
        if (externalFilesDir == null) {
            externalFilesDir = context.getFilesDir();
        }
        File upgradeDir = new File(externalFilesDir, "upgrade");
        if (!upgradeDir.exists() && !upgradeDir.mkdirs()) {
            throw new IllegalStateException("无法创建升级目录");
        }
        return upgradeDir;
    }

    private int parseVersionCode(String value) {
        if (TextUtils.isEmpty(value)) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private class UpgradeDownloadListener extends FileDownloadListener {
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            scheduleConnectTimeout();
        }

        @Override
        protected void started(BaseDownloadTask task) {
            scheduleConnectTimeout();
        }

        @Override
        protected void connected(BaseDownloadTask task, String etag, boolean isContinue,
                                 int soFarBytes, int totalBytes) {
            connected = true;
            cancelConnectTimeout();
            int total = totalBytes > 0 ? totalBytes : (int) state.apkSize;
            if (downloadingProgressDialog != null && downloadingProgressDialog.isShowing()) {
                downloadingProgressDialog.updateProgress(soFarBytes, total);
            }
            scheduleReadTimeout();
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            lastProgressBytes = soFarBytes;
            lastProgressTime = System.currentTimeMillis();
            int total = totalBytes > 0 ? totalBytes : (int) state.apkSize;
            if (downloadingProgressDialog != null && downloadingProgressDialog.isShowing()) {
                downloadingProgressDialog.updateProgress(soFarBytes, total);
            }
            reportProgressIfNeeded(soFarBytes, total);
            scheduleReadTimeout();
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            if (terminalHandled) {
                return;
            }
            cancelConnectTimeout();
            cancelReadTimeout();
            downloadTask = null;
            state.downloadTaskId = task.getId();
            File downloadedFile = new File(task.getTargetFilePath());
            Log.i("APP_UPDATE", "taskId=" + task.getId()
                    + " downloadedFile=" + downloadedFile.getAbsolutePath()
                    + " exists=" + downloadedFile.exists()
                    + " length=" + downloadedFile.length());
            if (!TextUtils.equals(downloadedFile.getAbsolutePath(), state.partPath)) {
                state.partPath = downloadedFile.getAbsolutePath();
            }
            UpgradeStateStore.saveState(state);
            verifyAndInstall(upgradeInfo, state, downloadedFile);
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            handleDownloadFailure("USER_CANCELLED",
                    "USER_CANCELLED", new Exception("user cancelled"));
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            handleDownloadFailure("DOWNLOAD_FAILED",
                    classifyFailure(e, connected), e);
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            // Same URL/path task already exists; keep the existing single task.
        }
    }

    private void reportProgressIfNeeded(long soFarBytes, long totalBytes) {
        if (state == null || totalBytes <= 0) {
            return;
        }
        long percent = soFarBytes * 100L / totalBytes;
        long now = System.currentTimeMillis();
        if (state.lastReportedProgressPercent >= 0
                && percent - state.lastReportedProgressPercent < 5
                && now - state.lastProgressReportTime < 5000L) {
            return;
        }
        state.lastReportedProgressPercent = percent;
        state.lastProgressReportTime = now;
        UpgradeStateStore.saveState(state);
        UpgradeEventQueue.get().enqueue(
                state.sessionId,
                state.versionCode,
                UpgradeEventQueue.EVENT_DOWNLOAD_PROGRESS,
                soFarBytes + "/" + totalBytes + " " + percent + "%");
    }

    public static class Builder {
        private Context context;
        private String versionName;
        private String versionCode;
        private String APIService;
        private String APIUrl;
        private String apkDownloadedPath;
        private String appName;
        private boolean needShowToast;

        public Builder(Context context) {
            this.context = context;
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

        public Builder setAppName(String appName) {
            this.appName = appName;
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
