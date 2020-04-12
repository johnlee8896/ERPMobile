package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.LogisticsSelectBean;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/***2
 * @date 创建时间 2020/4/8 10:06
 * @author 作者: xxblwf
 * @description 物流选择页面
 */

public class LogisticsSelectAdapter extends BaseRecycleAdapter<LogisticsSelectBean, LogisticsSelectAdapter.LogisticsSelectViewHolder> {
    private OnViewClickListener onViewClickListener;

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
    }

    @NonNull @Override public LogisticsSelectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LogisticsSelectViewHolder(parent);
    }

    @Override public void onBindViewHolder(LogisticsSelectViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.itemView.setOnClickListener(v -> {
            if (onViewClickListener != null){
                onViewClickListener.onClickAction(v,"",dataList.get(position));
            }
        });
    }

    public static class LogisticsSelectViewHolder extends BaseViewHolder {

        @BindView(R.id.logistics_select_customer_company_textView) TextView customerCompanyTextView;
        @BindView(R.id.logistics_select_logistics_company_textView) TextView logisticsCompanyTextView;
        @BindView(R.id.logistics_select_transport_type_textView) TextView transportTypeTextView;
        @BindView(R.id.logistics_select_address_textView) TextView addressTextView;
        @BindView(R.id.logistics_select_remark_textView) TextView remarkTextView;

        public LogisticsSelectViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_logistics_select_layout);
            ButterKnife.bind(this,itemView);
        }

        @Override public <T> void initUIData(T t) {
            if (t != null) {
                LogisticsSelectBean bean = (LogisticsSelectBean) t;
                if (bean != null){
                    customerCompanyTextView.setText(bean.getCfChineseName());
                    logisticsCompanyTextView.setText(bean.getLcName());
                    transportTypeTextView.setText(bean.getDelivery());
                    addressTextView.setText(bean.getDesAddress());
                    remarkTextView.setText(bean.getRemark());
                }
            }
        }
    }
}
