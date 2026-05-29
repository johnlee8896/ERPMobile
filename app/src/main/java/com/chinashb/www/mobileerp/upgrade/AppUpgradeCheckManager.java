package com.chinashb.www.mobileerp.upgrade;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;

import com.chinashb.www.mobileerp.APP;
import com.chinashb.www.mobileerp.basicobject.QueryAsyncTask;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.funs.OnLoadDataListener;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.utils.FileUtil;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * created by code-x John
 * start: 2026-05-02 16:00
 * end: 2026-05-02 16:00
 * 全局 App 升级检查管理器
 */
public class AppUpgradeCheckManager {

    public interface CheckStateListener {
        void onCheckFinished(boolean netReady);
    }

    private static final long CHECK_INTERVAL_MS = 5 * 60 * 1000L;
    private static final String VERSION_SQL =
            "select top 1 VerID,Version,Convert(nvarchar(100),UpdateDate,23) As UpdateDate, Des " +
                    " from ERP_Mobile_Ver Where RequireUpdate=1 Order By VerID Desc";

    private static final AppUpgradeCheckManager INSTANCE = new AppUpgradeCheckManager();

    private final List<CheckStateListener> pendingListeners = new ArrayList<>();
    private Context appContext;
    private boolean checking;
    private boolean downloadUrlLoading;
    private boolean netReady;
    private long lastCheckTime;
    private int lastPromptVersionCode = -1;
    private int latestServerVersionCode = -1;
    private String latestUpdateLog = "";
    private String latestDownloadUrl = "";
    private boolean pendingUpgradePrompt;

    private AppUpgradeCheckManager() {
    }

    public static AppUpgradeCheckManager get() {
        return INSTANCE;
    }

    public synchronized void init(Context context) {
        if (context != null) {
            appContext = context.getApplicationContext();
        }
    }

    public synchronized boolean isNetReady() {
        return netReady;
    }

    public void onActivityResumed(Activity activity) {
        tryShowPendingPrompt(activity);
        startCheck(activity, false, null);
    }

    public void forceCheck(Activity activity, CheckStateListener listener) {
        startCheck(activity, true, listener);
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
            if (!force && lastCheckTime > 0 && System.currentTimeMillis() - lastCheckTime < CHECK_INTERVAL_MS) {
                notifyPendingListeners(netReady);
                return;
            }
            if (checking) {
                return;
            }
            checking = true;
        }

        QueryAsyncTask queryAsyncTask = new QueryAsyncTask();
        queryAsyncTask.setLoadDataCompleteListener(new OnLoadDataListener() {
            @Override
            public void loadComplete(List<JsonObject> result) {
                handleVersionQueryResult(activity, result);
            }
        });
        queryAsyncTask.execute(VERSION_SQL);
    }

    private void handleVersionQueryResult(Activity activity, List<JsonObject> result) {
        boolean localNetReady = result != null && result.size() == 1;
        String versionText = null;
        String updateLog = "";
        if (localNetReady) {
            JsonObject jsonObject = result.get(0);
            if (jsonObject.has("Version") && !jsonObject.get("Version").isJsonNull()) {
                versionText = jsonObject.get("Version").getAsString();
            }
            if (jsonObject.has("Des") && !jsonObject.get("Des").isJsonNull()) {
                updateLog = jsonObject.get("Des").getAsString();
            }
        }

        int serverVersionCode = parseVersionCode(versionText);
        int currentVersionCode = getVersionCode(appContext);

        synchronized (this) {
            netReady = localNetReady;
            checking = false;
            lastCheckTime = System.currentTimeMillis();
            latestServerVersionCode = serverVersionCode;
            latestUpdateLog = updateLog == null ? "" : updateLog;
            if (currentVersionCode >= serverVersionCode) {
                pendingUpgradePrompt = false;
                latestDownloadUrl = "";
            }
            if (currentVersionCode >= lastPromptVersionCode) {
                lastPromptVersionCode = -1;
            }
        }

        notifyPendingListeners(localNetReady);

        if (!localNetReady) {
            return;
        }

        if (currentVersionCode < serverVersionCode && shouldPromptVersion(serverVersionCode)) {
            synchronized (this) {
                pendingUpgradePrompt = true;
            }
            if (!TextUtils.isEmpty(latestDownloadUrl)) {
                promptUpgradeIfPossible(activity);
            } else {
                loadDownloadUrlAndPrompt(activity, updateLog, serverVersionCode);
            }
        }
    }

    private synchronized boolean shouldPromptVersion(int serverVersionCode) {
        if (serverVersionCode <= 0) {
            return false;
        }
        if (downloadUrlLoading) {
            return false;
        }
        return lastPromptVersionCode != serverVersionCode;
    }

    private void loadDownloadUrlAndPrompt(Activity activity, String updateLog, int serverVersionCode) {
        synchronized (this) {
            if (downloadUrlLoading) {
                return;
            }
            downloadUrlLoading = true;
        }

        new AsyncTask<Void, Void, WsResult>() {
            @Override
            protected WsResult doInBackground(Void... voids) {
                return WebServiceUtil.getDownloadUrl();
            }

            @Override
            protected void onPostExecute(WsResult result) {
                synchronized (AppUpgradeCheckManager.this) {
                    downloadUrlLoading = false;
                }

                if (result == null || !result.getResult() || TextUtils.isEmpty(result.getErrorInfo())) {
                    return;
                }

                synchronized (AppUpgradeCheckManager.this) {
                    latestServerVersionCode = serverVersionCode;
                    latestUpdateLog = updateLog == null ? "" : updateLog;
                    latestDownloadUrl = result.getErrorInfo();
                    pendingUpgradePrompt = true;
                }
                promptUpgradeIfPossible(activity);
            }
        }.execute();
    }

    private void tryShowPendingPrompt(Activity activity) {
        if (!isActivityUsable(activity)) {
            return;
        }
        promptUpgradeIfPossible(activity);
    }

    private void promptUpgradeIfPossible(Activity fallbackActivity) {
        final int serverVersionCode;
        final String updateLog;
        final String downloadUrl;
        synchronized (this) {
            serverVersionCode = latestServerVersionCode;
            updateLog = latestUpdateLog;
            downloadUrl = latestDownloadUrl;
        }

        if (serverVersionCode <= 0 || TextUtils.isEmpty(downloadUrl)) {
            return;
        }

        int currentVersionCode = getVersionCode(appContext);
        if (currentVersionCode >= serverVersionCode) {
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
            if (lastPromptVersionCode == serverVersionCode) {
                pendingUpgradePrompt = false;
                return;
            }
            lastPromptVersionCode = serverVersionCode;
            pendingUpgradePrompt = false;
        }

        APPUpgradeManager.with(dialogActivity)
                .setNeedShowToast(true)
                .setApkDownloadedPath(FileUtil.getCachePath())
                .builder()
                .showForceUpdateDialog(updateLog, downloadUrl);
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

    private int getVersionCode(Context context) {
        if (context == null) {
            return 0;
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int parseVersionCode(String versionText) {
        if (TextUtils.isEmpty(versionText)) {
            return 0;
        }
        try {
            return Integer.parseInt(versionText.trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
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
}
