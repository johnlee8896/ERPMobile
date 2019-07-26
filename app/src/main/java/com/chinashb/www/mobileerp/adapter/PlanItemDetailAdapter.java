package com.chinashb.www.mobileerp.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinashb.www.mobileerp.PlanItemDetailActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.PlanItemDetailBean;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.widget.SelectUseDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2019/7/23 8:59
 * @author 作者: xxblwf
 * @description 计划item
 */

public class PlanItemDetailAdapter extends BaseRecycleAdapter<PlanItemDetailBean, PlanItemDetailAdapter.PlanItemViewHolder> {

    @NonNull @Override public PlanItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlanItemViewHolder(parent);
    }

    static class PlanItemViewHolder extends BaseViewHolder {

        @BindView(R.id.plan_list_dot_imageView) ImageView dotImageView;
        @BindView(R.id.plan_list_work_line_textView) TextView workLineTextView;
        @BindView(R.id.plan_list_type_textView) TextView typeTextView;
        @BindView(R.id.plan_list_wc_name) TextView WcNameTextView;
        @BindView(R.id.plan_list_title_layout) LinearLayout titleLayout;
        @BindView(R.id.plan_list_plan_detail_textView) TextView planDetailTextView;
        @BindView(R.id.plan_list_remark_textView) TextView remarkTextView;

        public PlanItemViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_plan_show_list_layout);
            ButterKnife.bind(this, itemView);
        }

        @Override public <T> void initUIData(T t) {
            PlanItemDetailBean bean = (PlanItemDetailBean) t;
            if (bean != null) {
                workLineTextView.setText(bean.getWCName());
//                typeTextView.setText();
                if (!TextUtils.isEmpty(bean.getHtmlMwName())) {
                    planDetailTextView.setText(Html.fromHtml(bean.getHtmlMwName()));
                } else {
                    planDetailTextView.setText(bean.getMwName());
                }
                remarkTextView.setText("备注: " + bean.getMPIRemark());
            }

            itemView.setOnClickListener(v ->{
                Intent intent = new Intent(itemView.getContext(), PlanItemDetailActivity.class);
                intent.putExtra(IntentConstant.Intent_PlanItemDetailBean,bean);
                itemView.getContext().startActivity(intent);
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override public boolean onLongClick(View v) {
                    SelectUseDialog dialog = new SelectUseDialog(itemView.getContext());
                    dialog.show();
                    return false;
                }
            });
        }
    }
}
