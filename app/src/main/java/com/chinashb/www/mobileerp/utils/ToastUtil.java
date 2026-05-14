package com.chinashb.www.mobileerp.utils;

import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.Toast;

import com.chinashb.www.mobileerp.APP;
import com.chinashb.www.mobileerp.R;

/***
 * @date 创建时间 2019/6/18 3:43 PM
 * @author 作者: liweifeng
 * @description Toast工具类
 */
public class ToastUtil {
    public ToastUtil() {
    }

    private static Toast buildToast(CharSequence text, int duration, Integer iconRes) {
        Toast toast = Toast.makeText(APP.get(), text, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        if (iconRes != null) {
            ImageView imageView = new ImageView(APP.get());
            imageView.setImageResource(iconRes);
            LinearLayout toastView = (LinearLayout) toast.getView();
            if (toastView != null) {
                toastView.setOrientation(LinearLayout.HORIZONTAL);
                toastView.addView(imageView, 0);
            }
        }
        return toast;
    }

    public static void showToastShort(@StringRes int textId) {
        showToastShort(APP.get().getString(textId));
    }

    public static void showToastShort(CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            showAutoStateToast(text, Toast.LENGTH_SHORT);
        }
    }

    public static void showWrongToastShort(CharSequence text) {
        Integer iconRes = null;
        iconRes = R.mipmap.warning;
        buildToast(text, Toast.LENGTH_SHORT, iconRes).show();
    }

    public static void showToastLong(CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            showAutoStateToast(text, Toast.LENGTH_LONG);
        }
    }

    public static void showToastCertainTime(CharSequence text,int seconds) {
        if (!TextUtils.isEmpty(text)) {
            showAutoStateToast(text, seconds);
        }
    }

    public static void showToastLong(@StringRes int textId) {
        showToastLong(APP.get().getString(textId));
    }

    public static void showApiError(String api, String msg, String requestApi) {
        if (AppUtil.isApkInDebug()) {
            if (api.equals(requestApi)) {
                showToastShort(api + msg);
            }
        } else {
            showToastShort(msg);
        }

    }

    private static void showAutoStateToast(CharSequence rawText, int duration) {
        CharSequence translatedText = AutoI18nUtil.translate(APP.get(), rawText);
        String sourceText = rawText == null ? "" : rawText.toString();
        Integer iconRes = null;
        if (isFailureMessage(sourceText)) {
            iconRes = R.mipmap.warning;
            FailureCaptureUtil.captureIfPossible();
        } else if (isSuccessMessage(sourceText)) {
            iconRes = R.mipmap.smiley;
        }
        buildToast(translatedText, duration, iconRes).show();
    }

    private static boolean isSuccessMessage(String text) {
        String lowerText = text == null ? "" : text.toLowerCase();
        if (isFailureMessage(lowerText)) {
            return false;
        }
        return lowerText.contains("成功")
                || lowerText.contains("完成")
                || lowerText.contains("berjaya")
                || lowerText.contains("success");
    }

    private static boolean isFailureMessage(String text) {
        String lowerText = text == null ? "" : text.toLowerCase();
        return lowerText.contains("不成功")
                || lowerText.contains("未成功")
                || lowerText.contains("不完成")
                || lowerText.contains("未完成")
                || lowerText.contains("失败")
                || lowerText.contains("错误")
                || lowerText.contains("异常")
                || lowerText.contains("警告")
                || lowerText.contains("不可")
                || lowerText.contains("不能")
                || lowerText.contains("不符")
                || lowerText.contains("不一致")
                || lowerText.contains("未")
                || lowerText.contains("没有")
                || lowerText.contains("为空")
                || lowerText.contains("请先")
                || lowerText.contains("超时")
                || lowerText.contains("unsuccess")
                || lowerText.contains("not success")
                || lowerText.contains("not completed")
                || lowerText.contains("gagal")
                || lowerText.contains("error")
                || lowerText.contains("fail");
    }

}
