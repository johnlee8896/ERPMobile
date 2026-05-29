package com.chinashb.www.mobileerp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.basicobject.WsResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * created by code-x John
 * start: 2026-05-19 20:06:58 CST
 * end: 2026-05-19 20:06:58 CST
 */
public final class CrashLogUploadManager {

    private static final String PREF_NAME = "crash_upload_queue";
    private static final String KEY_PENDING_FILES = "pending_files";
    private static final int MAX_CRASH_CONTENT_LENGTH = 180000;
    private static final CrashLogUploadManager INSTANCE = new CrashLogUploadManager();

    private final Object uploadLock = new Object();
    private volatile boolean uploading;
    private Context appContext;

    private CrashLogUploadManager() {
    }

    public static CrashLogUploadManager getInstance() {
        return INSTANCE;
    }

    public void init(Context context) {
        if (context == null) {
            return;
        }
        appContext = context.getApplicationContext();
        tryUploadPending(appContext);
    }

    public void enqueueCrashFile(Context context, String crashFilePath) {
        if (context == null || TextUtils.isEmpty(crashFilePath)) {
            return;
        }
        SharedPreferences preferences = getPreferences(context);
        Set<String> pendingFiles = new HashSet<String>(preferences.getStringSet(KEY_PENDING_FILES, new HashSet<String>()));
        pendingFiles.add(crashFilePath);
        preferences.edit().putStringSet(KEY_PENDING_FILES, pendingFiles).apply();
    }

    public void tryUploadPending(Context context) {
        if (context == null) {
            return;
        }
        appContext = context.getApplicationContext();
        synchronized (uploadLock) {
            if (uploading) {
                return;
            }
            uploading = true;
        }
        Thread uploadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    uploadPendingInternal();
                } finally {
                    uploading = false;
                }
            }
        }, "CrashLogUploadThread");
        uploadThread.setPriority(Thread.MIN_PRIORITY);
        uploadThread.start();
    }

    private void uploadPendingInternal() {
        Context context = appContext;
        if (context == null) {
            return;
        }
        SharedPreferences preferences = getPreferences(context);
        Set<String> pendingFiles = new HashSet<String>(preferences.getStringSet(KEY_PENDING_FILES, new HashSet<String>()));
        if (pendingFiles.isEmpty()) {
            return;
        }

        List<String> successFiles = new ArrayList<String>();
        for (String crashFilePath : pendingFiles) {
            if (TextUtils.isEmpty(crashFilePath)) {
                successFiles.add(crashFilePath);
                continue;
            }
            File crashFile = new File(crashFilePath);
            if (!crashFile.exists() || !crashFile.isFile()) {
                successFiles.add(crashFilePath);
                continue;
            }
            String crashContent = readCrashContent(crashFile);
            if (TextUtils.isEmpty(crashContent)) {
                continue;
            }
            WsResult wsResult = WebServiceUtil.opUploadMobileCrashLog(
                    safeGetHrId(),
                    safeGetHrName(),
                    crashFile.getName(),
                    Build.MODEL,
                    Build.VERSION.SDK_INT,
                    crashContent);
            if (wsResult != null && wsResult.getResult()) {
                successFiles.add(crashFilePath);
                //noinspection ResultOfMethodCallIgnored
                crashFile.delete();
            }
        }

        if (!successFiles.isEmpty()) {
            pendingFiles.removeAll(successFiles);
            preferences.edit().putStringSet(KEY_PENDING_FILES, pendingFiles).apply();
        }
    }

    private String readCrashContent(File crashFile) {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(crashFile)));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
                if (builder.length() >= MAX_CRASH_CONTENT_LENGTH) {
                    break;
                }
            }
        } catch (Exception ignored) {
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ignored) {
                }
            }
        }
        return builder.toString();
    }

    private SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private int safeGetHrId() {
        try {
            return UserSingleton.get().getHRID();
        } catch (Exception ignored) {
            return 0;
        }
    }

    private String safeGetHrName() {
        try {
            String hrName = UserSingleton.get().getHRName();
            return hrName == null ? "" : hrName;
        } catch (Exception ignored) {
            return "";
        }
    }
}
