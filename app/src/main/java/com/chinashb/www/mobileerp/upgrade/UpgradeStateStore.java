package com.chinashb.www.mobileerp.upgrade;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.chinashb.www.mobileerp.utils.JsonUtil;

import java.util.UUID;

/**
 * codex by john 2026-11-12 - 升级任务状态与设备 ID 本地存储
 */
public class UpgradeStateStore {
    private static final String PREFS_NAME = "mobile_upgrade_v2";
    private static final String KEY_DEVICE_ID = "upgrade_device_id";
    private static final String KEY_DOWNLOAD_STATE = "upgrade_download_state";

    private static SharedPreferences preferences;

    private UpgradeStateStore() {
    }

    public static void init(Context context) {
        if (context != null) {
            preferences = context.getApplicationContext()
                    .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
    }

    private static SharedPreferences getPreferences() {
        if (preferences == null) {
            throw new IllegalStateException("UpgradeStateStore.init(Context) must be called first");
        }
        return preferences;
    }

    public static synchronized String getOrCreateDeviceId() {
        SharedPreferences sp = getPreferences();
        String deviceId = sp.getString(KEY_DEVICE_ID, null);
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = UUID.randomUUID().toString();
            sp.edit().putString(KEY_DEVICE_ID, deviceId).apply();
        }
        return deviceId;
    }

    public static synchronized void saveState(UpgradeDownloadState state) {
        if (state == null) {
            return;
        }
        getPreferences().edit()
                .putString(KEY_DOWNLOAD_STATE, JsonUtil.objectToJson(state))
                .apply();
    }

    public static synchronized UpgradeDownloadState loadState() {
        String json = getPreferences().getString(KEY_DOWNLOAD_STATE, null);
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            UpgradeDownloadState state = JsonUtil.parseJsonToObject(
                    json, UpgradeDownloadState.class);
            return state == null ? new UpgradeDownloadState() : state;
        } catch (Exception e) {
            return null;
        }
    }

    public static synchronized void clearState() {
        getPreferences().edit().remove(KEY_DOWNLOAD_STATE).apply();
    }
}
