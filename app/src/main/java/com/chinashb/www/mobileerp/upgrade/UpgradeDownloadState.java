package com.chinashb.www.mobileerp.upgrade;

import java.util.ArrayList;
import java.util.List;

/**
 * codex by john 2026-11-12 - 升级下载任务持久化状态
 */
public class UpgradeDownloadState {
    public static final String STATUS_IDLE = "IDLE";
    public static final String STATUS_DOWNLOADING = "DOWNLOADING";
    public static final String STATUS_VERIFIED = "VERIFIED";
    public static final String STATUS_FAILED = "FAILED";

    public String sessionId = "";
    public long versionCode;
    public String versionName = "";
    public String apkUrl = "";
    public long apkSize;
    public String sha256 = "";
    public int connectionCount = 1;
    public int connectTimeoutSeconds = 15;
    public int readTimeoutSeconds = 60;
    public List<Integer> retryDelaysSeconds = new ArrayList<>();
    public int checkAfterSeconds;
    public String message = "";
    public boolean forceUpgrade;
    public String targetPath = "";
    public String partPath = "";
    public int retryIndex;
    public String status = STATUS_IDLE;
    public int downloadTaskId = -1;
    public long lastReportedProgressPercent = -1;
    public long lastProgressReportTime;

    public String taskKey() {
        return sessionId + "|" + versionCode + "|" + targetPath;
    }
}
