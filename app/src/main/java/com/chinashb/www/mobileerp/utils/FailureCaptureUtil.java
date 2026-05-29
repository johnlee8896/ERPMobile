package com.chinashb.www.mobileerp.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;

import com.chinashb.www.mobileerp.APP;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * created by code-x John
 * start: 2026-05-02 12:35:58 CST
 * end: 2026-05-02 12:35:58 CST
 */
public final class FailureCaptureUtil {

    private static final String SCREENSHOT_DIR = "shb/screen";
    private static final long MIN_CAPTURE_INTERVAL_MS = 1500L;
    private static long lastCaptureAt;

    private FailureCaptureUtil() {
    }

    public static void captureIfPossible() {
        Activity activity = APP.getTopActivity();
        if (activity == null || activity.isFinishing() || activity.getWindow() == null) {
            return;
        }
        long now = System.currentTimeMillis();
        synchronized (FailureCaptureUtil.class) {
            if (now - lastCaptureAt < MIN_CAPTURE_INTERVAL_MS) {
                return;
            }
            lastCaptureAt = now;
        }
        try {
            View rootView = activity.getWindow().getDecorView().getRootView();
            if (rootView == null || rootView.getWidth() <= 0 || rootView.getHeight() <= 0) {
                return;
            }
            Bitmap bitmap = Bitmap.createBitmap(rootView.getWidth(), rootView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            rootView.draw(canvas);
            final Bitmap bitmapForSave = bitmap;
            final Activity currentActivity = activity;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        saveBitmap(currentActivity, bitmapForSave);
                    } finally {
                        bitmapForSave.recycle();
                    }
                }
            }, "FailureCaptureSaveThread").start();
        } catch (Exception ignored) {
        }
    }

    private static void saveBitmap(Activity activity, Bitmap bitmap) {
        List<File> baseDirs = resolveBaseDirs(activity);
        for (int i = 0; i < baseDirs.size(); i++) {
            File targetFile = createTargetFile(baseDirs.get(i));
            if (targetFile == null) {
                continue;
            }
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(targetFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                return;
            } catch (Exception ignored) {
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    private static File createTargetFile(File baseDir) {
        File screenDir = new File(baseDir, SCREENSHOT_DIR);
        if (!screenDir.exists() && !screenDir.mkdirs()) {
            return null;
        }
        String fileName = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(new Date());
        File targetFile = new File(screenDir, fileName + ".png");
        int index = 1;
        while (targetFile.exists()) {
            targetFile = new File(screenDir, fileName + "_" + index + ".png");
            index++;
        }
        return targetFile;
    }

    private static List<File> resolveBaseDirs(Activity activity) {
        List<File> baseDirs = new ArrayList<>();
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File externalStorageDirectory = Environment.getExternalStorageDirectory();
            if (externalStorageDirectory != null) {
                baseDirs.add(externalStorageDirectory);
            }
        }
        File appExternalDir = activity.getExternalFilesDir(null);
        if (appExternalDir != null) {
            baseDirs.add(appExternalDir);
        }
        String fallbackPath = activity.getFilesDir() == null ? null : activity.getFilesDir().getAbsolutePath();
        if (!TextUtils.isEmpty(fallbackPath)) {
            baseDirs.add(new File(fallbackPath));
        }
        return baseDirs;
    }
}
