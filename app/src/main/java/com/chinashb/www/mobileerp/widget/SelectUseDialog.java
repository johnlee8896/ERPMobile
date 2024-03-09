package com.chinashb.www.mobileerp.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.chinashb.www.mobileerp.APP;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.ScreenUtil;
import com.chinashb.www.mobileerp.utils.StringUtils;
import com.chinashb.www.mobileerp.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2019/7/15 2:45 PM
 * @author 作者: liweifeng
 * @description 部门领料中选择用途
 */
public class SelectUseDialog extends BaseDialog {
    @BindView(R.id.dialog_select_use_recyclerView) CustomRecyclerView recyclerView;
    @BindView(R.id.department_input_use_EditText) EditText UseEditText;
    @BindView(R.id.dialog_use_confirm_Button) Button confirmButton;
    @BindView(R.id.dialog_usecancel_Button) Button cancelButton;
    private OnViewClickListener onViewClickListener;
    private SelectUseAdapter adapter;

    public SelectUseDialog(@NonNull Context context) {
        super(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_use_layout);
        ButterKnife.bind(this);
        configDialog(Gravity.CENTER);
        setCanceledOnTouchOutside(false);
        adapter = new SelectUseAdapter();
        recyclerView.setAdapter(adapter);
        String[] useArray = APP.get().getResources().getStringArray(R.array.select_use_array);
        List<String> useList = new ArrayList<>();
        for (int i = 0; i < useArray.length; i++) {
            useList.add(useArray[i]);
        }
        adapter.setData(useList);

        confirmButton.setOnClickListener(v -> {
            if (!StringUtils.isStringValid(UseEditText.getText().toString())){
                ToastUtil.showToastShort("您未选择或输入用途！");
            }else{
                if (onViewClickListener != null){
                    onViewClickListener.onClickAction(v,"",UseEditText.getText().toString());
                }
            }
        });

        cancelButton.setOnClickListener(v ->{
            dismiss();
        });

    }

    public void refreshContent(List<String> stringList) {
        adapter.setData(stringList);
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
        adapter.setOnViewClickListener(onViewClickListener);
    }


}

