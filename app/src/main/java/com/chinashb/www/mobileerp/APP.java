package com.chinashb.www.mobileerp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import java.lang.ref.WeakReference;

import com.chinashb.www.mobileerp.upgrade.AppUpgradeCheckManager;
import com.chinashb.www.mobileerp.utils.ExceptionCatchManager;
import com.chinashb.www.mobileerp.utils.LanguageHelper;
import com.umeng.commonsdk.UMConfigure;

/***
 * @date 创建时间 2019/6/18 10:09 AM
 * @author 作者: liweifeng
 * @description
 */
public class APP extends Application {
    private static APP app;
    private static WeakReference<Activity> topActivityRef;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        ExceptionCatchManager.getInstance().init(get());
        AppUpgradeCheckManager.get().init(this);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
                topActivityRef = new WeakReference<>(activity);
                AppUpgradeCheckManager.get().onActivityResumed(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Activity topActivity = getTopActivity();
                if (topActivity == activity) {
                    topActivityRef = null;
                }
            }
        });
        //设置LOG开关，默认为false
        UMConfigure.setLogEnabled(true);
        //// TODO: 2019/8/27 s2
        UMConfigure.init(this,"5d649a6c570df39d02000754","Umeng",UMConfigure.DEVICE_TYPE_PHONE,"s2");
        //统计SDK是否支持采集在子进程中打点的自定义事件，默认不支持
        UMConfigure.setProcessEvent(true);//支持多进程打点
    }

    public static APP get(){
        if (app == null){
            app = new APP();
        }
        return app;
    }

    public static Activity getTopActivity() {
        return topActivityRef == null ? null : topActivityRef.get();
    }

//    @Override
//    protected void attachBaseContext(Context base) {
//        String lang = LanguageHelper.getSavedLanguage(base);
//        super.attachBaseContext(LanguageHelper.setLocale(base, lang));
//    }

    @Override
    protected void attachBaseContext(Context base) {
        String langCode = LanguageHelper.getFinalLanguageCode(base); // 可能是系统语言 or 用户选择
        super.attachBaseContext(LanguageHelper.setLocale(base, langCode));
    }
}
