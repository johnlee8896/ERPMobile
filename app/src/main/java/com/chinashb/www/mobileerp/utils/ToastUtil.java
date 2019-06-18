package com.chinashb.www.mobileerp.utils;

import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.Toast;

import com.chinashb.www.mobileerp.APP;

/***
 * @date 创建时间 2019/6/18 3:43 PM
 * @author 作者: liweifeng
 * @description Toast工具类
 */
public class ToastUtil {
    private static Toast sToast;

    public ToastUtil() {
    }

    private static Toast getsToast() {
        if (sToast == null) {
            sToast = Toast.makeText(APP.get(), "", Toast.LENGTH_SHORT);
            sToast.setGravity(17, 0, 0);
        }

        return sToast;
    }

    public static void showToastShort(@StringRes int textId) {
        getsToast().setDuration(Toast.LENGTH_SHORT);
        getsToast().setText(textId);
        getsToast().show();
    }

    public static void showToastShort(CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            getsToast().setDuration(Toast.LENGTH_SHORT);
            getsToast().setText(text);
            getsToast().show();
        }
    }

    public static void showToastLong(CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            getsToast().setDuration(Toast.LENGTH_LONG);
            getsToast().setText(text);
            getsToast().show();
        }
    }

    public static void showToastLong(@StringRes int textId) {
        getsToast().setDuration(Toast.LENGTH_LONG);
        getsToast().setText(textId);
        getsToast().show();
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

}
