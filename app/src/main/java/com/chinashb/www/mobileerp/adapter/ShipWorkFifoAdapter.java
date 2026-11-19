package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.ShipWorkModels.FifoLine;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 成品发货 - FIFO 备料明细 adapter
 */
public class ShipWorkFifoAdapter extends BaseRecycleAdapter<FifoLine, ShipWorkFifoAdapter.ShipWorkFifoViewHolder> {

    public interface OnLockFifoClickListener {
        void onLockFifoClick(FifoLine bean, double quantity);
    }

    private OnLockFifoClickListener onLockFifoClickListener;

    public ShipWorkFifoAdapter setOnLockFifoClickListener(OnLockFifoClickListener listener) {
        this.onLockFifoClickListener = listener;
        return this;
    }

    @NonNull
    @Override
    public ShipWorkFifoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShipWorkFifoViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(ShipWorkFifoViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        FifoLine bean = dataList.get(position);
        holder.lockButton.setOnClickListener(v -> {
            if (onLockFifoClickListener != null) {
                double quantity = parseQuantity(holder.quantityEditText.getText().toString());
                if (quantity > 0) {
                    onLockFifoClickListener.onLockFifoClick(bean, quantity);
                }
            }
        });
    }

    private static double parseQuantity(String value) {
        try {
            return Double.parseDouble(value == null ? "" : value.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public static class ShipWorkFifoViewHolder extends BaseViewHolder {

        @BindView(R.id.item_ship_work_fifo_product_textView) TextView productTextView;
        @BindView(R.id.item_ship_work_fifo_qty_textView) TextView qtyTextView;
        @BindView(R.id.item_ship_work_fifo_quantity_editText) EditText quantityEditText;
        @BindView(R.id.item_ship_work_fifo_lock_button) Button lockButton;

        public ShipWorkFifoViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_ship_work_fifo);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public <T> void initUIData(T t) {
            FifoLine bean = (FifoLine) t;
            if (bean == null) {
                return;
            }
            productTextView.setText("物料 " + bean.productId + " / PS_ID " + bean.psId);
            qtyTextView.setText("需求 " + format(bean.dpiQuantity)
                    + "  已锁 " + bean.reservedPalletCount + "托/" + format(bean.reservedQuantity)
                    + "  本次可锁 " + bean.lockablePalletCount + "托/" + format(bean.lockablePalletQuantity));
            boolean enabled = bean.availableQuantity > 0
                    && bean.lockablePalletCount > 0
                    && bean.lockablePalletQuantity > 0;
            quantityEditText.setText(format(bean.lockablePalletQuantity));
            quantityEditText.setEnabled(enabled);
            lockButton.setEnabled(enabled);
        }

        private String format(double value) {
            return value == Math.rint(value) ? String.valueOf((long) value) : String.valueOf(value);
        }
    }
}
