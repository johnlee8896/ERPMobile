package com.chinashb.www.mobileerp.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.chinashb.www.mobileerp.widget.EmptyLayoutManageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/***
 * @date 创建时间 2021/1/14 22:04
 * @author 作者: xxblwf
 * @description 每部分任务的fragment
 */

public class TaskFragment extends BaseFragment {

    @BindView(R.id.fragment_task_recyclerView) CustomRecyclerView recyclerView;
    Unbinder unbinder;
    @BindView(R.id.fragment_task_emptyManager) EmptyLayoutManageView emptyManager;

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
