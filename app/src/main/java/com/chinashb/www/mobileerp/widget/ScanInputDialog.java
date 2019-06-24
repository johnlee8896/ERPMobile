package com.chinashb.www.mobileerp.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.utils.OnEditTextInputCompleteListener;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.ScreenUtil;
import com.chinashb.www.mobileerp.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2018/4/20 21:31
 * @author 作者: liweifeng
 * @description 密码输入的对话框
 */
public class ScanInputDialog extends BaseDialog implements View.OnClickListener {

    @BindView(R.id.dialog_input_password_EditeText)
    EditText inputEditeText;
    @BindView(R.id.dialog_input_ok_Button)
    Button okButton;
    @BindView(R.id.dialog_input_cancel_Button)
    Button cancelButton;
    private String password = "2019";
    private OnViewClickListener onViewClickListener;
    private OnEditTextInputCompleteListener onEditTextInputCompleteListener;

    public void setOnEditTextInputCompleteListener(OnEditTextInputCompleteListener onEditTextInputCompleteListener) {
        this.onEditTextInputCompleteListener = onEditTextInputCompleteListener;
    }

    public ScanInputDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_input_layout);
        ButterKnife.bind(this);
        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        configDialog(Gravity.CENTER);
        setCanceledOnTouchOutside(false);
//        inputEditeText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                System.out.println("===================== edtable = " + editable.toString());
//                if (onEditTextInputCompleteListener != null){
//                    onEditTextInputCompleteListener.onEditTextInputComplete(editable.toString());
//                }
//            }
//        });
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
    }

    @Override
    public void onClick(View v) {
        if (v == okButton) {
            String text = inputEditeText.getText().toString();
            if (TextUtils.isEmpty(text)) {
//                ToastUtil.showToastShort("请输入密码切换");
                ToastUtil.showToastShort("没有信息，请输入或扫描！");
            } else {
                if (onViewClickListener != null) {
                    onViewClickListener.onClickAction(okButton, "", inputEditeText.getText().toString());
                }
            }

        } else if (v == cancelButton) {
            dismiss();
        }
    }


}
