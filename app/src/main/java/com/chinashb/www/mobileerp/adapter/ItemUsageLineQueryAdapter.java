package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.ItemUsageLineQueryBean;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * codex by john 2026-11-12 - 物料使用线别查询 Adapter
 */
public class ItemUsageLineQueryAdapter
        extends BaseRecycleAdapter<ItemUsageLineQueryBean, ItemUsageLineQueryAdapter.ItemUsageLineViewHolder> {

    @NonNull
    @Override
    public ItemUsageLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemUsageLineViewHolder(parent);
    }

    public static class ItemUsageLineViewHolder extends BaseViewHolder {
        @BindView(R.id.item_usage_line_name_text) TextView lineNameTextView;
        @BindView(R.id.item_usage_shift_demand_text) TextView shiftDemandTextView;
        @BindView(R.id.item_usage_total_demand_text) TextView totalDemandTextView;
        @BindView(R.id.item_usage_plan_count_text) TextView planCountTextView;

        ItemUsageLineViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_item_usage_line_query);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public <T> void initUIData(T t) {
            ItemUsageLineQueryBean bean = (ItemUsageLineQueryBean) t;
            if (bean == null) {
                return;
            }
            String lineName = emptyToText(bean.getLineName());
            lineNameTextView.setText(lineName);
            lineNameTextView.setTextColor("合计".equals(lineName)
                    ? itemView.getResources().getColor(R.color.color_orange_FF6A00)
                    : itemView.getResources().getColor(R.color.login_title_color));

            shiftDemandTextView.setText("白班：" + emptyToText(bean.getDayShiftQty())
                    + "    夜班：" + emptyToText(bean.getNightShiftQty()));
            totalDemandTextView.setText("合计需求：" + emptyToText(bean.getTotalQty()));
            planCountTextView.setText("需求计划：" + emptyToText(bean.getPlanCount()));
        }

        private String emptyToText(String value) {
            return TextUtils.isEmpty(value) ? "0" : value;
        }
    }
}
