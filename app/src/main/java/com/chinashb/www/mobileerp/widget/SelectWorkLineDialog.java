package com.chinashb.www.mobileerp.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.SelectWorkLineAdapter;
import com.chinashb.www.mobileerp.basicobject.WorkLineSelectEntity;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.ScreenUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2019/7/24 9:29
 * @author 作者: xxblwf
 * @description 选择生产线别(A1 、 A2等)dialog
 */

public class SelectWorkLineDialog extends BaseDialog implements View.OnClickListener {
    private final List<WorkLineSelectEntity> workLineSelectEntityList;
    @BindView(R.id.dialog_select_work_line_recyclerView) CustomRecyclerView recyclerView;
    @BindView(R.id.dialog_work_line_confirm_Button) Button confirmButton;
    @BindView(R.id.dialog_work_line_cancel_Button) Button cancelButton;
    private OnViewClickListener onViewClickListener;
    private SelectWorkLineAdapter adapter;

    public SelectWorkLineDialog(@NonNull Context context, List<WorkLineSelectEntity> workLineSelectEntityList) {
        super(context);
        this.workLineSelectEntityList = workLineSelectEntityList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_work_line_layout);
        ButterKnife.bind(this);
        configDialog(Gravity.CENTER);
        setCanceledOnTouchOutside(false);
        adapter = new SelectWorkLineAdapter();
        recyclerView.setAdapter(adapter);
//        String[] useArray = APP.get().getResources().getStringArray(R.array.select_use_array);
//        List<WorkLineSelectEntity> useList = new ArrayList<>();
//        for (int i = 0; i < useArray.length; i++) {
//            useList.add(useArray[i]);
//        }
        if (workLineSelectEntityList != null && workLineSelectEntityList.size() > 0) {
            adapter.setData(workLineSelectEntityList);
        }
        confirmButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

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


    @Override public void onClick(View v) {
        if (v == confirmButton){
            if (onViewClickListener != null){
//                onViewClickListener.onClickAction(v,null,adapter.getList());
                onViewClickListener.onClickAction(v,null,adapter.getSelectWorkLIneStringList());
            }
            dismiss();
        }else if (v == cancelButton){
            if (onViewClickListener != null){
//                onViewClickListener.onClickAction(v,null,adapter.getList());
                onViewClickListener.onClickAction(v,null,null);
            }
            dismiss();
        }
    }
}
