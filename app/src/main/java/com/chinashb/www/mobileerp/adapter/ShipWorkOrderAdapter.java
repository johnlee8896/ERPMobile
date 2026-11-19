package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.ShipWorkOrderBean;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 成品发货 - 发货指令列表 adapter
 */
public class ShipWorkOrderAdapter extends BaseRecycleAdapter<ShipWorkOrderBean, ShipWorkOrderAdapter.ShipWorkOrderViewHolder> {

    private OnViewClickListener onViewClickListener;

    public ShipWorkOrderAdapter setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
        return this;
    }

    @NonNull
    @Override
    public ShipWorkOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShipWorkOrderViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(ShipWorkOrderViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ShipWorkOrderBean bean = dataList.get(position);
        holder.itemView.setOnClickListener(v -> {
            if (onViewClickListener != null) {
                onViewClickListener.onClickAction(v, "", bean);
            }
        });
    }

    public static class ShipWorkOrderViewHolder extends BaseViewHolder {

        @BindView(R.id.item_ship_work_order_customer_textView) TextView customerTextView;
        @BindView(R.id.item_ship_work_order_track_textView) TextView trackTextView;
        @BindView(R.id.item_ship_work_order_do_textView) TextView doTextView;

        public ShipWorkOrderViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_ship_work_order);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public <T> void initUIData(T t) {
            ShipWorkOrderBean bean = (ShipWorkOrderBean) t;
            if (bean == null) {
                return;
            }
            customerTextView.setText(bean.getCustomer_Name());
            trackTextView.setText("单号 " + bean.getTrackNo());
            doTextView.setText("指令号 " + bean.getDO_ID());
        }
    }
}
