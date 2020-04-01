package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.SDZHBoxDetailBean;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2020/2/26 3:51 PM
 * @author 作者: liweifeng
 * @description 三点照合 发货单中 发货箱明细adapter
 */
public class SDZHOrderBoxDetailAdapter extends BaseRecycleAdapter<SDZHBoxDetailBean, SDZHOrderBoxDetailAdapter.SDZHBoxDetailViewHolder> {

    private OnViewClickListener onViewClickListener;

    public SDZHOrderBoxDetailAdapter setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
        return this;
    }

    @NonNull
    @Override
    public SDZHBoxDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SDZHBoxDetailViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(SDZHBoxDetailViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);


        holder.itemView.setOnClickListener(v -> {
            notifyDataSetChanged();
            holder.itemView.setSelected(!holder.itemView.isSelected());
            if (onViewClickListener != null) {
                onViewClickListener.onClickAction(v, "", holder.itemView.isSelected() ? dataList.get(position) : null);
            }
        });
    }

    public static class SDZHBoxDetailViewHolder extends BaseViewHolder {

        @BindView(R.id.item_sdzh_box_order_number_textView) TextView orderNumberTextView;
        @BindView(R.id.item_sdzh_box_part_number_textView) TextView partNumberTextView;
        @BindView(R.id.item_sdzh_box_capacity_number_textView) TextView capacityNumberTextView;
        @BindView(R.id.item_sdzh_box_box_bar_textView) TextView boxBarTextView;
        @BindView(R.id.item_sdzh_box_is_full_textView) TextView isFullTextView;

        public SDZHBoxDetailViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_sdzh_box_detail_layout);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public <T> void initUIData(T t) {
            SDZHBoxDetailBean entity = (SDZHBoxDetailBean) t;
            if (entity != null) {
                orderNumberTextView.setText(entity.getOrderNo());
                partNumberTextView.setText(entity.getProductNo());
                capacityNumberTextView.setText(entity.getBoxQty() + "");
                boxBarTextView.setText(entity.getBoxCode());
                isFullTextView.setText(entity.getIsOK()  ? "否" : "是");
            }
        }
    }
}
