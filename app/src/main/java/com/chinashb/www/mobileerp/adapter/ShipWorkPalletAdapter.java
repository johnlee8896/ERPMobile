package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.ShipWorkPalletBean;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 成品发货 - 作业托盘 adapter
 */
public class ShipWorkPalletAdapter extends BaseRecycleAdapter<ShipWorkPalletBean, ShipWorkPalletAdapter.ShipWorkPalletViewHolder> {

    public interface OnPalletActionClickListener {
        void onReturnClick(ShipWorkPalletBean bean);
        void onPassClick(ShipWorkPalletBean bean);
        void onFailClick(ShipWorkPalletBean bean);
    }

    private OnPalletActionClickListener onPalletActionClickListener;

    public ShipWorkPalletAdapter setOnPalletActionClickListener(OnPalletActionClickListener listener) {
        this.onPalletActionClickListener = listener;
        return this;
    }

    @NonNull
    @Override
    public ShipWorkPalletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShipWorkPalletViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(ShipWorkPalletViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ShipWorkPalletBean bean = dataList.get(position);
        holder.returnButton.setOnClickListener(v -> {
            if (onPalletActionClickListener != null) {
                onPalletActionClickListener.onReturnClick(bean);
            }
        });
        holder.passButton.setOnClickListener(v -> {
            if (onPalletActionClickListener != null) {
                onPalletActionClickListener.onPassClick(bean);
            }
        });
        holder.failButton.setOnClickListener(v -> {
            if (onPalletActionClickListener != null) {
                onPalletActionClickListener.onFailClick(bean);
            }
        });
    }

    public static class ShipWorkPalletViewHolder extends BaseViewHolder {

        @BindView(R.id.item_ship_work_pallet_code_textView) TextView codeTextView;
        @BindView(R.id.item_ship_work_pallet_info_textView) TextView infoTextView;
        @BindView(R.id.item_ship_work_pallet_return_button) Button returnButton;
        @BindView(R.id.item_ship_work_pallet_pass_button) Button passButton;
        @BindView(R.id.item_ship_work_pallet_fail_button) Button failButton;

        public ShipWorkPalletViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_ship_work_pallet);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public <T> void initUIData(T t) {
            ShipWorkPalletBean bean = (ShipWorkPalletBean) t;
            if (bean == null) {
                return;
            }
            codeTextView.setText("箱码 " + bean.getCode_No_Box());
            String oqc = bean.getOQC_Result() == null || bean.getOQC_Result().isEmpty() ? "未检" : bean.getOQC_Result();
            infoTextView.setText("状态 " + bean.getPallet_Status() + "  OQC " + oqc + "  数量 " + bean.getQuantity());
        }
    }
}
