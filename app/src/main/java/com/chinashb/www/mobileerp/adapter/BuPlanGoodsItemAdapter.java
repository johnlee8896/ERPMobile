package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.BuPlanGoodsItemBean;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2020/5/13 9:53
 * @author 作者: xxblwf
 * @description 车间要货计划
 */

public class BuPlanGoodsItemAdapter extends BaseRecycleAdapter<BuPlanGoodsItemBean, BuPlanGoodsItemAdapter.BuPlanGoodsViewHolder>{
    @NonNull @Override public BuPlanGoodsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BuPlanGoodsViewHolder(parent);
    }

    public static class BuPlanGoodsViewHolder extends BaseViewHolder {

        @BindView(R.id.bu_plan_goods_first_name_textView) TextView itemIDTextView;
        @BindView(R.id.bu_plan_goods_first_right_name_textView) TextView versionTextView;
        @BindView(R.id.bu_plan_goods_second_name_textView) TextView nameTextView;
        @BindView(R.id.bu_plan_goods_third_name_textView) TextView storageTextView;

        public BuPlanGoodsViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_bu_plan_goods_list_layout);
            ButterKnife.bind(this,itemView);
        }

        @Override public <T> void initUIData(T t) {
            BuPlanGoodsItemBean bean = (BuPlanGoodsItemBean) t;
            if (bean != null){
                itemIDTextView.setText(bean.getItemID() + "");
                versionTextView.setText(bean.getVersion());
                nameTextView.setText(bean.getItemSpec());
                storageTextView.setText(bean.getInv_qty() + "");
            }
        }
    }
}
