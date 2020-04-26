package com.chinashb.www.mobileerp.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinashb.www.mobileerp.APP;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.CompanyBean;
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

public class CommonSelectInputDialog<T> extends BaseDialog {
    @BindView(R.id.stock_out_more_remark_EditText) EditText remarkEditText;
    @BindView(R.id.dialog_select_remark_recyclerView) CustomRecyclerView recyclerView;
    @BindView(R.id.dialog_remark_confirm_Button) Button confirmButton;
    @BindView(R.id.dialog_remark_cancel_Button) Button cancelButton;
    @BindView(R.id.dialog_remark_title_textView) TextView titleTextView;
    @BindView(R.id.dialog_remark_bottom_button_layout) LinearLayout buttonLayout;
    private OnViewClickListener onViewClickListener;
    private SelectUseAdapter adapter;

    private String title;
    private boolean selectOnly;

    public CommonSelectInputDialog(@NonNull Context context) {
        super(context);
    }

    public CommonSelectInputDialog setSelectOnly(boolean selectOnly) {
        this.selectOnly = selectOnly;
        if (selectOnly) {
            remarkEditText.setVisibility(View.GONE);
            buttonLayout.setVisibility(View.GONE);
        }
        return this;
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
        setContentList();

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

    private void setContentList() {
        String[] useArray = APP.get().getResources().getStringArray(R.array.select_remark_array);
        List<String> useList = new ArrayList<>();
        for (int i = 0; i < useArray.length; i++) {
            useList.add(useArray[i]);
        }
        adapter.setData(useList);
    }

    public void refreshContentList(List<CompanyBean> list){
        adapter.setData(list);
    }

    public void setRecyclerViewGone(){
        recyclerView.setVisibility(View.GONE);
    }

    public void refreshCommonContentList(List<T> list){
        adapter.setData(list);
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

    public CommonSelectInputDialog setTitle(String title) {
        titleTextView.setText(title);
        return this;
    }

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
        adapter.setOnViewClickListener(onViewClickListener);
    }


    public void setInputTextHint(String hint) {
        remarkEditText.setHint(hint);
    }

    public void setInputDialogTitle(String title) {
        titleTextView.setText(title);
    }
}

