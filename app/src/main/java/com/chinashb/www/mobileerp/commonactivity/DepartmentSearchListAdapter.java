package com.chinashb.www.mobileerp.commonactivity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.BaseRecycleAdapter;
import com.chinashb.www.mobileerp.adapter.BaseViewHolder;
import com.chinashb.www.mobileerp.bean.BUItemBean;
import com.chinashb.www.mobileerp.bean.DepartmentBean;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2019/7/3 15:49
 * @author 作者: xxblwf
 * @description 部门列表，之后会与Bu车间合并
 */

public class DepartmentSearchListAdapter extends BaseRecycleAdapter<DepartmentBean, DepartmentSearchListAdapter.DepartmentSearchViewHolder> {
    @Override
    public DepartmentSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DepartmentSearchViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(DepartmentSearchViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("SelectItem", dataList.get(position));
                Activity activity = (Activity) holder.itemView.getContext();
                activity.setResult(1, intent);
                activity.finish();

            }
        });
    }


    public static class DepartmentSearchViewHolder extends BaseViewHolder {

        @BindView(R.id.item_department_id_value_textView) TextView departmentIdTextView;
        @BindView(R.id.item_department_higher_name_textView) TextView departmentHigherNameTextView;
        @BindView(R.id.item_department_dep_name_textView) TextView departmentNameTextView;

        public DepartmentSearchViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_department_search_list_layout);
            ButterKnife.bind(this, itemView);
        }

        @Override public <T> void initUIData(T t) {
            DepartmentBean bean = (DepartmentBean) t;
            if (bean != null) {
                departmentIdTextView.setText(bean.getDepartmentID() + "");
                departmentHigherNameTextView.setText(bean.getPDN());
                departmentNameTextView.setText(bean.getDepartmentName());
            }
        }
    }

}

