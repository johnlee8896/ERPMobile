package com.chinashb.www.mobileerp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * created by code-x John
 * start: 2026-05-01 17:18:32 CST
 * end: 2026-05-01 17:18:32 CST
 *
 * 对历史页面中大量硬编码中文做运行时翻译，优先覆盖 XML 文字、Hint、标题和常见提示语。
 */
public final class AutoI18nUtil {

    private static final Map<String, List<PhraseItem>> PHRASE_CACHE = new HashMap<>();

    private AutoI18nUtil() {
    }

    public static void applyToActivity(Activity activity) {
        if (activity == null || shouldSkip(activity)) {
            return;
        }
        View decorView = activity.getWindow() == null ? null : activity.getWindow().getDecorView();
        if (decorView != null) {
            applyToView(activity, decorView);
        }
    }

    public static void applyToView(Context context, View view) {
        if (context == null || view == null || shouldSkip(context)) {
            return;
        }
        applySingleView(context, view);
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyToView(context, group.getChildAt(i));
            }
        }
    }

    public static CharSequence translate(Context context, CharSequence source) {
        if (context == null || source == null) {
            return source;
        }

        String text = source.toString();
        if (!containsHan(text)) {
            return source;
        }

        String languageCode = LanguageHelper.getFinalLanguageCode(context);
        if (TextUtils.isEmpty(languageCode) || LanguageHelper.LANG_ZH.equals(languageCode)) {
            return source;
        }

        List<PhraseItem> items = getPhraseItems(context, languageCode);
        for (int i = 0; i < items.size(); i++) {
            PhraseItem item = items.get(i);
            if (text.equals(item.source)) {
                return item.target;
            }
        }

        String translated = text;
        for (int i = 0; i < items.size(); i++) {
            PhraseItem item = items.get(i);
            if (translated.contains(item.source)) {
                translated = translated.replace(item.source, item.target);
            }
        }

        if (!LanguageHelper.LANG_ZH_TW.equals(languageCode)) {
            translated = translated
                    .replace("：", ": ")
                    .replace("，", ", ")
                    .replace("。", ". ")
                    .replace("！", "!")
                    .replace("？", "?")
                    .replace("（", "(")
                    .replace("）", ")");
            translated = translated.replaceAll(" {2,}", " ").trim();
        }
        return translated;
    }

    private static void applySingleView(Context context, View view) {
        // ✅ 关键：永远不要碰 EditText
        if (view instanceof android.widget.EditText) {
            return;
        }

        CharSequence contentDescription = view.getContentDescription();
        if (!TextUtils.isEmpty(contentDescription)) {
            view.setContentDescription(translate(context, contentDescription));
        }

        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            CharSequence text = textView.getText();
            if (!TextUtils.isEmpty(text)) {
                textView.setText(translate(context, text));
            }
            CharSequence hint = textView.getHint();
            if (!TextUtils.isEmpty(hint)) {
                textView.setHint(translate(context, hint));
            }
        }
    }

    private static List<PhraseItem> getPhraseItems(Context context, String languageCode) {
        List<PhraseItem> cached = PHRASE_CACHE.get(languageCode);
        if (cached != null) {
            return cached;
        }

        String[] sources = context.getResources().getStringArray(R.array.auto_i18n_source_phrases);
        String[] targets = context.getResources().getStringArray(R.array.auto_i18n_target_phrases);
        int count = Math.min(sources.length, targets.length);
        List<PhraseItem> items = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            if (!TextUtils.isEmpty(sources[i]) && !TextUtils.isEmpty(targets[i])) {
                items.add(new PhraseItem(sources[i], targets[i]));
            }
        }
        Collections.sort(items, new Comparator<PhraseItem>() {
            @Override
            public int compare(PhraseItem left, PhraseItem right) {
                return right.source.length() - left.source.length();
            }
        });
        PHRASE_CACHE.put(languageCode, items);
        return items;
    }

    private static boolean containsHan(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            Character.UnicodeBlock block = Character.UnicodeBlock.of(text.charAt(i));
            if (block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                    || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                    || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                    || block == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS) {
                return true;
            }
        }
        return false;
    }

    private static boolean shouldSkip(Context context) {
        Context current = context;
        while (current instanceof ContextWrapper) {
            if (current.getClass().getSimpleName().equals("LoginActivity")) {
                return true;
            }
            Context baseContext = ((ContextWrapper) current).getBaseContext();
            if (baseContext == null || baseContext == current) {
                break;
            }
            current = baseContext;
        }
        return current != null && current.getClass().getSimpleName().equals("LoginActivity");
    }

    private static final class PhraseItem {
        private final String source;
        private final String target;

        private PhraseItem(String source, String target) {
            this.source = source;
            this.target = target;
        }
    }
}
