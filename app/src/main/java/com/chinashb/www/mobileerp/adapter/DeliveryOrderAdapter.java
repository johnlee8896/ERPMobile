package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.DeliveryOrderBean;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2020/1/3 13:22
 * @author 作者: xxblwf
 * @description 发货指令adapter
 */

public class DeliveryOrderAdapter extends BaseRecycleAdapter<DeliveryOrderBean, DeliveryOrderAdapter.DeliveryOrderViewHolder> {
    private OnViewClickListener onViewClickListener;

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
    }

    @NonNull @Override public DeliveryOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeliveryOrderViewHolder(parent);
    }

    @Override public void onBindViewHolder(DeliveryOrderViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.itemView.setOnClickListener(v -> {
            if (onViewClickListener != null) {
                onViewClickListener.onClickAction(v, "", dataList.get(position));
            }
        });
    }

    public static class DeliveryOrderViewHolder extends BaseViewHolder {

        @BindView(R.id.item_order_track_NO_textView) TextView trackNOTextView;
        @BindView(R.id.item_order_is_time_accurate_textView) TextView isTimeAccurateTextView;
        @BindView(R.id.item_order_delivery_time_textView) TextView deliveryTimeTextView;
        @BindView(R.id.item_order_arrive_time_textView) TextView arriveTimeTextView;
        @BindView(R.id.item_order_customer_textView) TextView customerTextView;
        @BindView(R.id.item_order_receive_message_textView) TextView receiveMessageTextView;
        @BindView(R.id.item_order_special_remark_textView) TextView remarkTextView;
        @BindView(R.id.item_order_do_id_textView) TextView doIdTextView;

        public DeliveryOrderViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_delivery_order_layout);
            ButterKnife.bind(this, itemView);
        }

        @Override public <T> void initUIData(T t) {
            if (t != null && t instanceof DeliveryOrderBean) {
                DeliveryOrderBean bean = (DeliveryOrderBean) t;
                if (bean != null) {
                    trackNOTextView.setText(bean.getTrackNo());
                    isTimeAccurateTextView.setText(bean.isSpecificTime() ? "是" : "否");
                    deliveryTimeTextView.setText(bean.getDeliveryDate());
                    arriveTimeTextView.setText(bean.getArriveDate());
                    customerTextView.setText(bean.getCFChineseName());
                    receiveMessageTextView.setText(bean.getDesInfo());
                    remarkTextView.setText(bean.getSpecial());
                    doIdTextView.setText(bean.getDOID() + "");
                }
            }

        }
    }
}
