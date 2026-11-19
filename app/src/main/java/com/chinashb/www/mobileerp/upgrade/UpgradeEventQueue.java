package com.chinashb.www.mobileerp.upgrade;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.utils.JsonUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * codex by john 2026-11-12 - 升级事件本地补报队列
 */
public class UpgradeEventQueue {
    public static final String EVENT_DOWNLOAD_STARTED = "DOWNLOAD_STARTED";
    public static final String EVENT_DOWNLOAD_PROGRESS = "DOWNLOAD_PROGRESS";
    public static final String EVENT_DOWNLOAD_SUCCESS = "DOWNLOAD_SUCCESS";
    public static final String EVENT_DOWNLOAD_FAILED = "DOWNLOAD_FAILED";
    public static final String EVENT_INSTALL_STARTED = "INSTALL_STARTED";
    public static final String EVENT_INSTALL_SUCCESS = "INSTALL_SUCCESS";

    private static final String PREFS_NAME = "mobile_upgrade_v2";
    private static final String KEY_EVENTS = "upgrade_event_queue";
    private static final int MAX_EVENT_COUNT = 50;
    private static final UpgradeEventQueue INSTANCE = new UpgradeEventQueue();

    private Context appContext;
    private boolean flushing;

    private UpgradeEventQueue() {
    }

    public static UpgradeEventQueue get() {
        return INSTANCE;
    }

    public void init(Context context) {
        if (context != null) {
            appContext = context.getApplicationContext();
        }
    }

    public synchronized void enqueue(String sessionId, long versionCode,
                                     String eventType, String message) {
        if (appContext == null || TextUtils.isEmpty(eventType)) {
            return;
        }
        List<UpgradeEvent> events = loadEvents();
        if (events.size() >= MAX_EVENT_COUNT) {
            events.remove(0);
        }
        events.add(new UpgradeEvent(
                TextUtils.isEmpty(sessionId) ? "" : sessionId,
                versionCode,
                eventType,
                sanitizeMessage(message),
                System.currentTimeMillis()));
        saveEvents(events);
        flushAsync();
    }

    public void flushAsync() {
        if (appContext == null) {
            return;
        }
        synchronized (this) {
            if (flushing) {
                return;
            }
            flushing = true;
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                flush();
            }
        }, "upgrade-event-queue");
        thread.start();
    }

    public void reportInstallSuccessIfNeeded() {
        if (appContext == null) {
            return;
        }
        UpgradeDownloadState state = UpgradeStateStore.loadState();
        if (state == null || !UpgradeDownloadState.STATUS_VERIFIED.equals(state.status)) {
            return;
        }
        if (getVersionCode(appContext) != state.versionCode) {
            return;
        }
        enqueue(state.sessionId, state.versionCode, EVENT_INSTALL_SUCCESS, "");
    }

    private synchronized void setFlushing(boolean flushing) {
        this.flushing = flushing;
    }

    private void flush() {
        if (appContext == null) {
            return;
        }
        try {
            while (true) {
                UpgradeEvent event;
                synchronized (this) {
                    List<UpgradeEvent> events = loadEvents();
                    if (events.isEmpty()) {
                        break;
                    }
                    event = events.get(0);
                }

                WsResult result;
                try {
                    result = WebServiceUtil.reportMobileUpgradeEventV2(
                            event.sessionId,
                            event.versionCode,
                            event.eventType,
                            event.message);
                } catch (Exception e) {
                    break;
                }
                if (result == null || !result.getResult()) {
                    break;
                }

                synchronized (this) {
                    List<UpgradeEvent> events = loadEvents();
                    for (Iterator<UpgradeEvent> it = events.iterator(); it.hasNext(); ) {
                        UpgradeEvent item = it.next();
                        if (item.createdAt == event.createdAt
                                && TextUtils.equals(item.sessionId, event.sessionId)
                                && TextUtils.equals(item.eventType, event.eventType)) {
                            it.remove();
                            break;
                        }
                    }
                    saveEvents(events);
                    if (EVENT_INSTALL_SUCCESS.equals(event.eventType)) {
                        UpgradeDownloadState state = UpgradeStateStore.loadState();
                        if (state != null && state.versionCode == event.versionCode
                                && getVersionCode(appContext) == state.versionCode) {
                            UpgradeStateStore.clearState();
                        }
                    }
                }
            }
        } finally {
            setFlushing(false);
        }
    }

    private List<UpgradeEvent> loadEvents() {
        SharedPreferences sp = getPreferences();
        String json = sp.getString(KEY_EVENTS, "[]");
        try {
            Type type = new TypeToken<List<UpgradeEvent>>() {
            }.getType();
            List<UpgradeEvent> events = JsonUtil.parseJsonToObject(json, type);
            return events == null ? new ArrayList<UpgradeEvent>() : events;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void saveEvents(List<UpgradeEvent> events) {
        getPreferences().edit().putString(KEY_EVENTS, JsonUtil.objectToJson(events)).apply();
    }

    private SharedPreferences getPreferences() {
        return appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private String sanitizeMessage(String message) {
        if (TextUtils.isEmpty(message)) {
            return "";
        }
        String value = message.replaceAll("[\\r\\n]+", " ");
        if (value.length() > 200) {
            value = value.substring(0, 200);
        }
        return value;
    }

    private long getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            return 0;
        }
    }

    public static class UpgradeEvent {
        public String sessionId;
        public long versionCode;
        public String eventType;
        public String message;
        public long createdAt;

        public UpgradeEvent() {
        }

        UpgradeEvent(String sessionId, long versionCode, String eventType,
                     String message, long createdAt) {
            this.sessionId = sessionId;
            this.versionCode = versionCode;
            this.eventType = eventType;
            this.message = message;
            this.createdAt = createdAt;
        }
    }
}
