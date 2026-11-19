package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.ShipWorkLineBean;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 成品发货 - 发货指令明细 adapter
 */
public class ShipWorkLineAdapter extends BaseRecycleAdapter<ShipWorkLineBean, ShipWorkLineAdapter.ShipWorkLineViewHolder> {

    private OnQueryInventoryClickListener onQueryInventoryClickListener;

    public interface OnQueryInventoryClickListener {
        void onQueryInventoryClick(ShipWorkLineBean bean, TextView statusTextView);
    }

    public ShipWorkLineAdapter setOnQueryInventoryClickListener(OnQueryInventoryClickListener listener) {
        this.onQueryInventoryClickListener = listener;
        return this;
    }

    @NonNull
    @Override
    public ShipWorkLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShipWorkLineViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(ShipWorkLineViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ShipWorkLineBean bean = dataList.get(position);
        holder.queryButton.setOnClickListener(v -> {
            if (onQueryInventoryClickListener != null) {
                onQueryInventoryClickListener.onQueryInventoryClick(bean, holder.statusTextView);
            }
        });
    }

    public static class ShipWorkLineViewHolder extends BaseViewHolder {

        @BindView(R.id.item_ship_work_line_product_textView) TextView productTextView;
        @BindView(R.id.item_ship_work_line_qty_textView) TextView qtyTextView;
        @BindView(R.id.item_ship_work_line_query_button) Button queryButton;
        @BindView(R.id.item_ship_work_line_status_textView) TextView statusTextView;

        public ShipWorkLineViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_ship_work_line);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public <T> void initUIData(T t) {
            ShipWorkLineBean bean = (ShipWorkLineBean) t;
            if (bean == null) {
                return;
            }
            productTextView.setText("物料 " + bean.getProduct_ID() + " / PS_ID " + bean.getPS_ID());
            qtyTextView.setText("需求 " + bean.getDPI_Quantity()
                    + " 可发 " + bean.getAvailable_Quantity()
                    + " 已预留 " + bean.getReserved_Quantity());
            statusTextView.setText("");
        }
    }
}
