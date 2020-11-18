package com.chinashb.www.mobileerp.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.utils.AppUtil;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2018/6/29 1:48 PM
 * @author 作者: liweifeng
 * @description 外勤的备注
 */
public class AttendanceOutsideRemarkDialog extends BaseDialog implements View.OnClickListener {
    @BindView(R.id.dialog_input_reason_EditText) EditText reasonEditText;
    @BindView(R.id.dialog_input_reason_ok_Button) Button okButton;
    @BindView(R.id.dialog_input_cancel_Button) Button cancelButton;
    private OnViewClickListener onViewClickListener;

    public AttendanceOutsideRemarkDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_atendance_out_remark_layout);
        ButterKnife.bind(this);
        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        configDialog(Gravity.CENTER);
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

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
    }

    @Override
    public void onClick(View v) {
        if (v == okButton) {
            if (onViewClickListener != null) {
                String remark = reasonEditText.getText().toString();
                if (remark.length() == 0){
                    ToastUtil.showToastShort("请填写外勤原因");
                }else if (remark.length() < 5){
                    ToastUtil.showToastShort("请填写5~60个字符");
                }else if (remark.length() <= 60){
                    onViewClickListener.onClickAction(okButton, "", remark);
                    dismiss();
                }else{
                    ToastUtil.showToastShort("您输入的字符过多，请填写5~60个字符");
                }

            }
        }else if (v == cancelButton){
            dismiss();
        }
    }


}
