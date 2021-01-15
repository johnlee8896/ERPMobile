package com.chinashb.www.mobileerp.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.ScreenUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2020/12/28 14:13
 * @author 作者: xxblwf
 * @description 在制品盘点选择
 */

public class SwitchPanDianTypeDialog extends BaseDialog {
    @BindView(R.id.switch_type_dairuku) TextView DairukuTextView;
    @BindView(R.id.switch_type_touliaoqu) TextView TouliaoquTextView;
    private OnViewClickListener onViewClickListener;

    public SwitchPanDianTypeDialog(@NonNull Context context) {
        super(context);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_switch_pandian_type_layout);
        ButterKnife.bind(this);
        configDialog(Gravity.CENTER);
        setCanceledOnTouchOutside(false);
    }

    protected void configDialog(int gravity) {
        WindowManager.LayoutParams wl = getWindow().getAttributes();
        wl.gravity = gravity;// 设置重力
        if (gravity != Gravity.CENTER) {
            getWindow().setWindowAnimations(R.style.bottomDialogWindowAnim);
        }
        getWindow().setAttributes(wl);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCancelable(true);
        getWindow().setLayout((int) (ScreenUtil.getScreenWidth() * 0.75),
                WindowManager.LayoutParams.WRAP_CONTENT);

    }

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
        DairukuTextView.setOnClickListener(v -> {
            if (onViewClickListener != null){
                onViewClickListener.onClickAction(v,"",1);
            }
        });

        TouliaoquTextView.setOnClickListener(v -> {
            if (onViewClickListener != null){
                onViewClickListener.onClickAction(v,"",2);
            }
        });
    }
}
