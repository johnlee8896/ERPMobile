package com.chinashb.www.mobileerp.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.WindowManager;

import com.chinashb.www.mobileerp.R;

/***
 * @date 创建时间 2018/3/23 09:40
 * @author 作者: liweifeng
 * @description Dialog的基类
 */
public class BaseDialog extends Dialog {

    public BaseDialog(@NonNull Context context) {
        super(context);
    }

    protected void configDialogLayout(int gravity) {
        configDialog(gravity, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    protected void configDialog(int gravity, int width, int height) {
        WindowManager.LayoutParams wl = getWindow().getAttributes();
        wl.gravity = gravity;

        if ((gravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
            getWindow().setWindowAnimations(R.style.bottomDialogWindowAnim);
        } else if (gravity == Gravity.CENTER) {
            getWindow().setWindowAnimations(R.style.centerDialogWindowAnim);
        }
        wl.width = width;
        wl.height = height;
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setAttributes(wl);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
