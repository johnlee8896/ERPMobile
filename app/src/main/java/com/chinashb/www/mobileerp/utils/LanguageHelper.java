package com.chinashb.www.mobileerp.utils;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

public class LanguageHelper {

    private static final String PREFS_NAME = "AppSettings";
    private static final String KEY_LANGUAGE = "user_language";

    // 支持的语言代码（与 values-xx 文件夹对应）
    public static final String LANG_ZH = "zh";          // 简体中文
    public static final String LANG_ZH_TW = "zh-rTW";  // 繁体中文
    public static final String LANG_EN = "en";         // 英语
    public static final String LANG_MS = "ms";         // 马来西亚语

    // 保存用户选的语言
    public static void saveLanguage(Context context, String languageCode) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_LANGUAGE, normalizeLanguageCode(languageCode)).apply();
    }

    // 获取用户选的语言，如果用户没选过，则返回 null
    public static String getUserSelectedLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (!prefs.contains(KEY_LANGUAGE)) {
            return null;
        }
        return normalizeLanguageCode(prefs.getString(KEY_LANGUAGE, null));
    }

    //deep seek添加解决
    public static String getCurrentLanguage(Context context) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = context.getResources()
                    .getConfiguration()
                    .getLocales()
                    .get(0);
        } else {
            locale = context.getResources()
                    .getConfiguration()
                    .locale;
        }
        return locale.getLanguage();
    }

    // 🎯 核心方法：获取最终使用的语言代码
    public static String getFinalLanguageCode(Context context) {
        // 1. 先看用户是否手动选过语言
        String userSelected = getUserSelectedLanguage(context);

        if (userSelected != null) {
            // 用户选过，优先用用户选的
            return userSelected;
        } else {
            // 用户没选过，使用系统语言
            return getSystemLanguageCode(context);
        }
    }

    // 根据系统语言返回对应我们支持的语言代码，比如 "zh-rTW", "en", "ms"
    public static String getSystemLanguageCode(Context context) {
        Locale systemLocale = context.getResources().getConfiguration().locale;
        String language = systemLocale.getLanguage(); // 如 "zh", "en", "ms"
        String country = systemLocale.getCountry();   // 如 "TW", "US", "MY"

        if (language.equals("zh")) {
            // 中文：判断是否繁体（常见：TW / HK / MO）
            if (country.equalsIgnoreCase("TW") || country.equalsIgnoreCase("HK") || country.equalsIgnoreCase("MO")) {
                return LANG_ZH_TW; // 繁体中文
            } else {
                return LANG_ZH; // 简体中文
            }
        } else if (language.equals("en")) {
            return LANG_EN; // 英语
        } else if (language.equals("ms")) {
            return LANG_MS; // 马来西亚语
        }

        // 默认返回简体中文
        return LANG_ZH;
    }

    // 根据语言代码返回对应的 Locale 对象
    public static Locale getLocale(String languageCode) {
        String normalizedLanguageCode = normalizeLanguageCode(languageCode);
        if (LANG_ZH.equals(normalizedLanguageCode)) {
            return Locale.SIMPLIFIED_CHINESE;
        } else if (LANG_ZH_TW.equals(normalizedLanguageCode)) {
            return Locale.TRADITIONAL_CHINESE;
        } else if (LANG_EN.equals(normalizedLanguageCode)) {
            return Locale.ENGLISH;
        } else if (LANG_MS.equals(normalizedLanguageCode)) {
            return new Locale("ms");
        }
        return Locale.SIMPLIFIED_CHINESE;
    }

    // 为传入的 Context 设置语言，并返回一个新的 Context（用于加载正确的资源）
    public static Context setLocale(Context context, String languageCode) {
        Locale locale = getLocale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
            config.setLayoutDirection(locale);
            return context.createConfigurationContext(config);
        } else {
            // 低版本兼容
            config.locale = locale;
            resources.updateConfiguration(config, resources.getDisplayMetrics());
            return context;
        }
    }

    public static String normalizeLanguageCode(String languageCode) {
        if (languageCode == null) {
            return null;
        }
        String trimmedLanguageCode = languageCode.trim();
        if (trimmedLanguageCode.isEmpty()
                || "zh-cn".equalsIgnoreCase(trimmedLanguageCode)
                || "zh_cn".equalsIgnoreCase(trimmedLanguageCode)
                || LANG_ZH.equalsIgnoreCase(trimmedLanguageCode)) {
            return LANG_ZH;
        }
        if (LANG_ZH_TW.equalsIgnoreCase(trimmedLanguageCode)
                || "zh-tw".equalsIgnoreCase(trimmedLanguageCode)
                || "zh_tw".equalsIgnoreCase(trimmedLanguageCode)
                || "zh-hk".equalsIgnoreCase(trimmedLanguageCode)
                || "zh_hk".equalsIgnoreCase(trimmedLanguageCode)) {
            return LANG_ZH_TW;
        }
        if (LANG_EN.equalsIgnoreCase(trimmedLanguageCode)) {
            return LANG_EN;
        }
        if (LANG_MS.equalsIgnoreCase(trimmedLanguageCode)) {
            return LANG_MS;
        }
        return trimmedLanguageCode;
    }
}
