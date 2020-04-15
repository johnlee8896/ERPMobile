package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.InnerSelectBuBean;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2020/4/14 14:46
 * @author 作者: xxblwf
 * @description 内部调拨 选择车间adapter
 */

public class InnerSaleSelectBuAdapter extends BaseRecycleAdapter<InnerSelectBuBean, InnerSaleSelectBuAdapter.InnerBuViewHolder>{
    private OnViewClickListener onViewClickListener;

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
    }

    @NonNull @Override public InnerBuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InnerBuViewHolder(parent);
    }

    @Override public void onBindViewHolder(InnerBuViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.itemView.setOnClickListener(v -> {
            if (onViewClickListener != null){
                onViewClickListener.onClickAction(v,"",dataList.get(position));
            }
        });
    }

    public static class InnerBuViewHolder extends BaseViewHolder {

        @BindView(R.id.item_company_name_textView) TextView companyNameTextView;
        @BindView(R.id.item_bu_name_textView) TextView buNameTextView;

        public InnerBuViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_bu_inner_sale_layout);
            ButterKnife.bind(this,itemView);
        }

        @Override public <T> void initUIData(T t) {
            InnerSelectBuBean bean = (InnerSelectBuBean) t;
            if (bean != null){
                companyNameTextView.setText(bean.getCompanyName());
                buNameTextView.setText(bean.getBuName());
            }
        }
    }
}
