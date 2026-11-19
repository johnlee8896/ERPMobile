package com.chinashb.www.mobileerp.upgrade;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * codex by john 2026-11-12 - 无 CDN 升级方案 CheckMobileUpgradeV2 返回模型
 */
public class MobileUpgradeInfo {
    @SerializedName("shouldUpgrade") private boolean shouldUpgrade;
    @SerializedName("forceUpgrade") private boolean forceUpgrade;
    @SerializedName("message") private String message;
    @SerializedName("sessionId") private String sessionId;
    @SerializedName("versionCode") private long versionCode;
    @SerializedName("versionName") private String versionName;
    @SerializedName("apkUrl") private String apkUrl;
    @SerializedName("apkSize") private long apkSize;
    @SerializedName("sha256") private String sha256;
    @SerializedName("connectionCount") private int connectionCount;
    @SerializedName("connectTimeoutSeconds") private int connectTimeoutSeconds;
    @SerializedName("readTimeoutSeconds") private int readTimeoutSeconds;
    @SerializedName("retryDelaysSeconds") private List<Integer> retryDelaysSeconds;
    @SerializedName("checkAfterSeconds") private int checkAfterSeconds;

    public boolean isShouldUpgrade() {
        return shouldUpgrade;
    }

    public boolean isForceUpgrade() {
        return forceUpgrade;
    }

    public String getMessage() {
        return message == null ? "" : message;
    }

    public String getSessionId() {
        return sessionId == null ? "" : sessionId;
    }

    public long getVersionCode() {
        return versionCode;
    }

    public String getVersionName() {
        return versionName == null ? "" : versionName;
    }

    public String getApkUrl() {
        return apkUrl == null ? "" : apkUrl;
    }

    public long getApkSize() {
        return apkSize;
    }

    public String getSha256() {
        return sha256 == null ? "" : sha256;
    }

    public int getConnectionCount() {
        return connectionCount > 0 ? connectionCount : 1;
    }

    public int getConnectTimeoutSeconds() {
        return connectTimeoutSeconds > 0 ? connectTimeoutSeconds : 15;
    }

    public int getReadTimeoutSeconds() {
        return readTimeoutSeconds > 0 ? readTimeoutSeconds : 60;
    }

    public List<Integer> getRetryDelaysSeconds() {
        if (retryDelaysSeconds == null || retryDelaysSeconds.isEmpty()) {
            List<Integer> defaults = new ArrayList<>();
            defaults.add(5);
            defaults.add(15);
            defaults.add(30);
            return defaults;
        }
        return retryDelaysSeconds;
    }

    public int getCheckAfterSeconds() {
        return checkAfterSeconds;
    }

    public void setShouldUpgrade(boolean shouldUpgrade) {
        this.shouldUpgrade = shouldUpgrade;
    }

    public void setForceUpgrade(boolean forceUpgrade) {
        this.forceUpgrade = forceUpgrade;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setVersionCode(long versionCode) {
        this.versionCode = versionCode;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public void setApkSize(long apkSize) {
        this.apkSize = apkSize;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public void setConnectionCount(int connectionCount) {
        this.connectionCount = connectionCount;
    }

    public void setConnectTimeoutSeconds(int connectTimeoutSeconds) {
        this.connectTimeoutSeconds = connectTimeoutSeconds;
    }

    public void setReadTimeoutSeconds(int readTimeoutSeconds) {
        this.readTimeoutSeconds = readTimeoutSeconds;
    }

    public void setRetryDelaysSeconds(List<Integer> retryDelaysSeconds) {
        this.retryDelaysSeconds = retryDelaysSeconds;
    }

    public void setCheckAfterSeconds(int checkAfterSeconds) {
        this.checkAfterSeconds = checkAfterSeconds;
    }
}
