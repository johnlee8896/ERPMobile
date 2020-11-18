package com.chinashb.www.mobileerp.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.utils.AppUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2018/7/19 11:13 AM
 * @author 作者: liweifeng
 * @description 操作成功如打卡成功的dialog
 */
public class SuccessNoticeDialog extends BaseDialog {
    @BindView(R.id.dialog_success_imageView) ImageView imageView;

    public SuccessNoticeDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_success_notice_layout);
        ButterKnife.bind(this);
        configDialog(Gravity.CENTER);
        imageView.setOnClickListener(v -> dismiss());
    }

    private void configDialog(int gravity) {
        WindowManager.LayoutParams wl = getWindow().getAttributes();
        wl.gravity = gravity;// 设置重力
        if (gravity != Gravity.CENTER) {
            getWindow().setWindowAnimations(R.style.bottomDialogWindowAnim);
        }
        getWindow().setAttributes(wl);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCancelable(true);
        getWindow().setLayout((int) (AppUtil.getScreenWidth() * 0.75),
                WindowManager.LayoutParams.WRAP_CONTENT);

    }

}
