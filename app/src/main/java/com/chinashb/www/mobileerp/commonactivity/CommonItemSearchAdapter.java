package com.chinashb.www.mobileerp.commonactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.BaseRecycleAdapter;
import com.chinashb.www.mobileerp.adapter.BaseViewHolder;
import com.chinashb.www.mobileerp.bean.BUItemBean;
import com.chinashb.www.mobileerp.bean.DepartmentBean;
import com.chinashb.www.mobileerp.bean.ResearchItemBean;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2019/7/3 16:39
 * @author 作者: xxblwf
 * @description common Adapter用来处理显示车间、部门等
 */

public class CommonItemSearchAdapter<TY> extends BaseRecycleAdapter<TY, CommonItemSearchAdapter.CommonItemViewHolder> {

    @NonNull @Override public CommonItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommonItemViewHolder(parent);
    }

    @Override public void onBindViewHolder(CommonItemViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        super.onBindViewHolder(holder, position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("SelectItem", (Parcelable) dataList.get(position));
                Activity activity = (Activity) holder.itemView.getContext();
                activity.setResult(1, intent);
                activity.finish();

            }
        });
    }

    public static class CommonItemViewHolder extends BaseViewHolder {

        @BindView(R.id.item_search_common_first_info_textView) TextView firstInfoTextView;
        @BindView(R.id.item_search_first_name_textView) TextView firstNameTextView;
        @BindView(R.id.item_search_second_info_textView) TextView secondInfoTextView;
        @BindView(R.id.item_search_second_name_textView) TextView secondNameTextView;
        @BindView(R.id.item_search_third_info_textView) TextView thirdInfoTextView;
        @BindView(R.id.item_search_third_name_textView) TextView thirdNameTextView;
        @BindView(R.id.item_search_fourth_info_textView) TextView fourthInfoTextView;
        @BindView(R.id.item_search_fourth_name_textView) TextView fourthNameTextView;

        public CommonItemViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_common_search_layout);
            ButterKnife.bind(this, itemView);
        }

        @Override public <T> void initUIData(T t) {
            if (t instanceof BUItemBean) {
                BUItemBean buItemBean = (BUItemBean) t;
                if (buItemBean != null) {
                    firstInfoTextView.setText("公司名称:");
                    secondInfoTextView.setText("公司简称:");
                    thirdInfoTextView.setText("车间号:");
                    fourthInfoTextView.setText("车间名称");
                    firstNameTextView.setText(buItemBean.getCompanyChineseName());
                    secondNameTextView.setText(buItemBean.getBrief());
                    thirdNameTextView.setText(buItemBean.getBUId() + "");
                    fourthNameTextView.setText(buItemBean.getBUName());
                }
            } else if (t instanceof DepartmentBean) {
                DepartmentBean bean = (DepartmentBean) t;
                if (bean != null) {
//                    departmentIdTextView.setText(bean.getDepartmentID() + "");
//                    departmentHigherNameTextView.setText(bean.getPDN());
//                    departmentNameTextView.setText(bean.getDepartmentName());
                    firstInfoTextView.setText("部门ID:");
                    secondInfoTextView.setText("上级部门:");
                    thirdInfoTextView.setText("部门名称:");
                    firstNameTextView.setText(bean.getDepartmentID() + "");
                    secondNameTextView.setText(bean.getPDN());
                    thirdNameTextView.setText(bean.getDepartmentName());
                }
            }else if (t instanceof ResearchItemBean){
                ResearchItemBean bean = (ResearchItemBean) t;
                if (bean != null){

                    firstInfoTextView.setText("产品通称:");
                    secondInfoTextView.setText("研发项目:");
                    thirdInfoTextView.setText("研发状态:");
                    firstNameTextView.setText(bean.getAbb());
                    secondNameTextView.setText(bean.getProgram());
                    thirdNameTextView.setText(bean.getStatus());
                }
            }
        }
    }
}
