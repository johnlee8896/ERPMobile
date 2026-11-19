package com.chinashb.www.mobileerp.upgrade;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.chinashb.www.mobileerp.APP;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.FileUtil;
import com.chinashb.www.mobileerp.utils.JsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * codex by john 2026-11-12 - 无 CDN 升级检查管理器
 */
public class AppUpgradeCheckManager {
    public interface CheckStateListener {
        void onCheckFinished(boolean netReady);
    }

    private static final String TAG = "UpgradeCheckV2";
    private static final long DEFAULT_CHECK_INTERVAL_MS = 5 * 60 * 1000L;
    private static final long PROMPT_INTERVAL_MS = 60 * 1000L;
    private static final long MIN_CHECK_AFTER_MS = 60 * 1000L;
    private static final long MAX_CHECK_AFTER_MS = 30 * 60 * 1000L;

    private static final AppUpgradeCheckManager INSTANCE = new AppUpgradeCheckManager();

    private final List<CheckStateListener> pendingListeners = new ArrayList<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable periodicCheckTask = new Runnable() {
        @Override
        public void run() {
            Activity topActivity = APP.getTopActivity();
            if (topActivity != null && !topActivity.isFinishing()
                    && !APPUpgradeManager.isDownloading()) {
                forceCheck(topActivity, null);
            }
            handler.postDelayed(this, nextCheckIntervalMs());
        }
    };

    private Context appContext;
    private boolean checking;
    private boolean netReady;
    private long lastCheckTime;
    private long lastPromptTime;
    private long nextCheckIntervalMs = DEFAULT_CHECK_INTERVAL_MS;
    private MobileUpgradeInfo latestUpgradeInfo;
    private boolean pendingUpgradePrompt;

    private AppUpgradeCheckManager() {
    }

    public static AppUpgradeCheckManager get() {
        return INSTANCE;
    }

    public synchronized void init(Context context) {
        if (context != null) {
            appContext = context.getApplicationContext();
            UpgradeStateStore.init(appContext);
            UpgradeEventQueue.get().init(appContext);
        }
    }

    public synchronized boolean isNetReady() {
        return netReady;
    }

    public void onActivityResumed(Activity activity) {
        UpgradeEventQueue.get().flushAsync();
        tryShowPendingPrompt(activity);
        startCheck(activity, false, null);
    }

    public void startPeriodicCheck() {
        handler.removeCallbacks(periodicCheckTask);
        handler.post(periodicCheckTask);
    }

    public void stopPeriodicCheck() {
        handler.removeCallbacks(periodicCheckTask);
    }

    public void forceCheck(Activity activity, CheckStateListener listener) {
        startCheck(activity, true, listener);
    }

    public void reportInstallSuccessIfNeeded() {
        UpgradeEventQueue.get().reportInstallSuccessIfNeeded();
        UpgradeEventQueue.get().flushAsync();
    }

    private void startCheck(Activity activity, boolean force, CheckStateListener listener) {
        if (listener != null) {
            synchronized (this) {
                pendingListeners.add(listener);
            }
        }

        if (!isActivityUsable(activity)) {
            notifyPendingListeners(false);
            return;
        }

        synchronized (this) {
            if (APPUpgradeManager.isDownloading()) {
                notifyPendingListeners(netReady);
                return;
            }
            if (checking) {
                notifyPendingListeners(netReady);
                return;
            }
            if (!force && lastCheckTime > 0
                    && System.currentTimeMillis() - lastCheckTime < nextCheckIntervalMs) {
                notifyPendingListeners(netReady);
                return;
            }
            checking = true;
        }

        new UpgradeCheckTask().execute();
    }

    private void tryShowPendingPrompt(Activity activity) {
        if (isActivityUsable(activity)) {
            promptUpgradeIfPossible(activity);
        }
    }

    private void handleCheckResult(CheckResult checkResult) {
        boolean localNetReady;
        MobileUpgradeInfo info;

        synchronized (this) {
            checking = false;
            lastCheckTime = System.currentTimeMillis();
            localNetReady = checkResult.networkAvailable;
            netReady = localNetReady;
            info = checkResult.info;
            latestUpgradeInfo = info;

            if (info != null && info.getCheckAfterSeconds() > 0) {
                long configuredMs = info.getCheckAfterSeconds() * 1000L;
                nextCheckIntervalMs = Math.max(MIN_CHECK_AFTER_MS,
                        Math.min(configuredMs, MAX_CHECK_AFTER_MS));
            } else {
                nextCheckIntervalMs = DEFAULT_CHECK_INTERVAL_MS;
            }
            if (!localNetReady || info == null || !info.isShouldUpgrade()) {
                pendingUpgradePrompt = false;
            }
        }

        notifyPendingListeners(localNetReady);

        if (!localNetReady || info == null || !info.isShouldUpgrade()) {
            return;
        }
        if (APPUpgradeManager.isDownloading()) {
            return;
        }

        long currentVersionCode = getVersionCode(appContext);
        Log.i("APP_UPDATE", "check currentVersion=" + currentVersionCode
                + " serverVersion=" + info.getVersionCode()
                + " shouldUpgrade=" + info.isShouldUpgrade());
        if (currentVersionCode >= info.getVersionCode()) {
            APPUpgradeManager.cancelIfVersionInstalled(info.getVersionCode());
            synchronized (this) {
                pendingUpgradePrompt = false;
            }
            return;
        }

        Activity topActivity = APP.getTopActivity();
        if (shouldPromptVersion(info.isForceUpgrade())) {
            synchronized (this) {
                pendingUpgradePrompt = true;
            }
            promptUpgradeIfPossible(topActivity);
        }
    }

    private synchronized boolean shouldPromptVersion(boolean forceUpgrade) {
        return forceUpgrade
                || System.currentTimeMillis() - lastPromptTime >= PROMPT_INTERVAL_MS;
    }

    private void promptUpgradeIfPossible(Activity fallbackActivity) {
        final MobileUpgradeInfo info;
        synchronized (this) {
            info = latestUpgradeInfo;
        }
        if (info == null || !info.isShouldUpgrade() || TextUtils.isEmpty(info.getApkUrl())) {
            return;
        }
        if (APPUpgradeManager.isDownloading()) {
            return;
        }
        if (getVersionCode(appContext) >= info.getVersionCode()) {
            APPUpgradeManager.cancelIfVersionInstalled(info.getVersionCode());
            synchronized (this) {
                pendingUpgradePrompt = false;
            }
            return;
        }

        Activity topActivity = APP.getTopActivity();
        Activity dialogActivity = isActivityUsable(topActivity) ? topActivity : fallbackActivity;
        if (!isActivityUsable(dialogActivity)) {
            synchronized (this) {
                pendingUpgradePrompt = true;
            }
            return;
        }

        synchronized (this) {
            lastPromptTime = System.currentTimeMillis();
            pendingUpgradePrompt = false;
        }

        APPUpgradeManager.with(dialogActivity)
                .setNeedShowToast(true)
                .setApkDownloadedPath(FileUtil.getCachePath())
                .builder()
                .showUpgradeDialog(info);
    }

    private synchronized long nextCheckIntervalMs() {
        return nextCheckIntervalMs;
    }

    private void notifyPendingListeners(boolean currentNetReady) {
        List<CheckStateListener> callbackList;
        synchronized (this) {
            if (pendingListeners.isEmpty()) {
                return;
            }
            callbackList = new ArrayList<>(pendingListeners);
            pendingListeners.clear();
        }
        for (CheckStateListener listener : callbackList) {
            if (listener != null) {
                listener.onCheckFinished(currentNetReady);
            }
        }
    }

    private long getVersionCode(Context context) {
        if (context == null) {
            return 0;
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            return 0;
        }
    }

    private String getVersionName(Context context) {
        if (context == null) {
            return "";
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName == null ? "" : packageInfo.versionName;
        } catch (Exception e) {
            return "";
        }
    }

    private String getAccountNo() {
        String accountNo = UserSingleton.get().getHRNO();
        if (TextUtils.isEmpty(accountNo)
                && UserSingleton.get().getUserInfo() != null) {
            accountNo = UserSingleton.get().getUserInfo().getHrNum();
        }
        return TextUtils.isEmpty(accountNo) ? "" : accountNo;
    }

    private String getNetworkType() {
        if (appContext == null) {
            return "UNKNOWN";
        }
        try {
            ConnectivityManager manager =
                    (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager == null ? null : manager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected()) {
                return "NONE";
            }
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return "WIFI";
            }
            return "MOBILE";
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    private boolean isActivityUsable(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return !activity.isDestroyed();
        }
        return true;
    }

    private class UpgradeCheckTask extends AsyncTask<Void, Void, CheckResult> {
        @Override
        protected CheckResult doInBackground(Void... voids) {
            try {
                WsResult result = WebServiceUtil.checkMobileUpgradeV2(
                        getAccountNo(),
                        UpgradeStateStore.getOrCreateDeviceId(),
                        Build.MANUFACTURER + " " + Build.MODEL,
                        Build.VERSION.RELEASE,
                        getVersionCode(appContext),
                        getVersionName(appContext),
                        getNetworkType());
                if (result == null || !result.getResult()) {
                    return CheckResult.failure();
                }

                String json = result.getInfo();
                if (TextUtils.isEmpty(json) || "null".equalsIgnoreCase(json)) {
                    Log.w(TAG, "CheckMobileUpgradeV2 Info is empty");
                    return CheckResult.failure();
                }
                MobileUpgradeInfo info =
                        JsonUtil.parseJsonToObject(json, MobileUpgradeInfo.class);
                if (info == null) {
                    Log.w(TAG, "CheckMobileUpgradeV2 Info parse failed");
                    return CheckResult.failure();
                }
                return CheckResult.success(info);
            } catch (Exception e) {
                Log.w(TAG, "CheckMobileUpgradeV2 failed", e);
                return CheckResult.failure();
            }
        }

        @Override
        protected void onPostExecute(CheckResult checkResult) {
            if (checkResult == null) {
                checkResult = CheckResult.failure();
            }
            handleCheckResult(checkResult);
        }
    }

    private static class CheckResult {
        final boolean networkAvailable;
        final MobileUpgradeInfo info;

        private CheckResult(boolean networkAvailable, MobileUpgradeInfo info) {
            this.networkAvailable = networkAvailable;
            this.info = info;
        }

        static CheckResult success(MobileUpgradeInfo info) {
            return new CheckResult(true, info);
        }

        static CheckResult failure() {
            return new CheckResult(false, null);
        }
    }
}
