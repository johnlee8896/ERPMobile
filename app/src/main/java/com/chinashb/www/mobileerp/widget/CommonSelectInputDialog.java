package com.chinashb.www.mobileerp.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chinashb.www.mobileerp.APP;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2019/11/20 14:42
 * @author 作者: xxblwf
 * @description 选择或添加备注
 */

public class CommonSelectInputDialog extends BaseDialog {
    @BindView(R.id.stock_out_more_remark_EditText) EditText remarkEditText;
    @BindView(R.id.dialog_select_remark_recyclerView) CustomRecyclerView recyclerView;
    @BindView(R.id.dialog_remark_confirm_Button) Button confirmButton;
    @BindView(R.id.dialog_remark_cancel_Button) Button cancelButton;
    @BindView(R.id.dialog_remark_title_textView) TextView titleTextView;
    private OnViewClickListener onViewClickListener;
    private SelectUseAdapter adapter;

    public CommonSelectInputDialog(@NonNull Context context) {
        super(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_add_remark_layout);
        ButterKnife.bind(this);
        configDialog(Gravity.CENTER);
        setCanceledOnTouchOutside(false);
        adapter = new SelectUseAdapter();
        recyclerView.setAdapter(adapter);
        String[] useArray = APP.get().getResources().getStringArray(R.array.select_remark_array);
        List<String> useList = new ArrayList<>();
        for (int i = 0; i < useArray.length; i++) {
            useList.add(useArray[i]);
        }
        adapter.setData(useList);

        cancelButton.setOnClickListener(v -> {
            dismiss();
        });

        confirmButton.setOnClickListener(v -> {
            if (onViewClickListener != null) {
                onViewClickListener.onClickAction(v, null, remarkEditText.getText().toString());
            }
//            dismiss();
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

    public void setTitle(String  title) {
        titleTextView.setText(title);
    }

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
        adapter.setOnViewClickListener(onViewClickListener);
    }


}

