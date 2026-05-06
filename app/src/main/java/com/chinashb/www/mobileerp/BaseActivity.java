package com.chinashb.www.mobileerp;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;

import com.chinashb.www.mobileerp.utils.AutoI18nUtil;
import com.chinashb.www.mobileerp.utils.LanguageHelper;
import com.umeng.analytics.MobclickAgent;

/***
 * @date 创建时间 2019/8/27 13:11
 * @author 作者: xxblwf
 * @description base基类，便于统计等
 */

public class BaseActivity  extends AppCompatActivity {
    private boolean autoI18nInstalled;
    private final ViewTreeObserver.OnGlobalLayoutListener autoI18nLayoutListener =
            new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    AutoI18nUtil.applyToActivity(BaseActivity.this);
                }
            };

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    @Override
//    protected void attachBaseContext(@NonNull Context newBase) {
//        String lang = LanguageHelper.getSavedLanguage(newBase);
//        super.attachBaseContext(LanguageHelper.setLocale(newBase, lang));
//    }
//
//    // 切换语言并重启当前 Activity 以生效
//    protected void changeLanguage(String languageCode) {
//        LanguageHelper.saveLanguage(this, languageCode);
//        recreate(); // 重新创建 Activity，会触发 attachBaseContext 使用新语言
//    }

    @Override
    protected void attachBaseContext(@NonNull Context newBase) {
        String langCode = LanguageHelper.getFinalLanguageCode(newBase);
        super.attachBaseContext(LanguageHelper.setLocale(newBase, langCode));
    }

    // 提供给页面调用的：切换语言并重启 Activity
    protected void changeLanguage(String languageCode) {
        LanguageHelper.saveLanguage(this, languageCode); // 保存用户选择
        recreate(); // 重启当前页面以应用新语言
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        installAutoI18n();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        installAutoI18n();
    }

    @Override
    public void setContentView(View view, android.view.ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        installAutoI18n();
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(AutoI18nUtil.translate(this, title));
    }

    @Override protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        AutoI18nUtil.applyToActivity(this);
    }

    @Override protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDestroy() {
        removeAutoI18nListener();
        super.onDestroy();
    }

    private void installAutoI18n() {
        final View decorView = getWindow() == null ? null : getWindow().getDecorView();
        if (decorView == null) {
            return;
        }
        decorView.post(new Runnable() {
            @Override
            public void run() {
                AutoI18nUtil.applyToActivity(BaseActivity.this);
            }
        });

        if (!autoI18nInstalled && decorView.getViewTreeObserver().isAlive()) {
            decorView.getViewTreeObserver().addOnGlobalLayoutListener(autoI18nLayoutListener);
            autoI18nInstalled = true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void removeAutoI18nListener() {
        View decorView = getWindow() == null ? null : getWindow().getDecorView();
        if (!autoI18nInstalled || decorView == null) {
            return;
        }
        ViewTreeObserver observer = decorView.getViewTreeObserver();
        if (observer.isAlive()) {
            observer.removeOnGlobalLayoutListener(autoI18nLayoutListener);
        }
        autoI18nInstalled = false;
    }
}
