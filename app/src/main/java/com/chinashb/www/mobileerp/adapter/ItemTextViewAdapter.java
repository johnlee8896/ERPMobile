package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.Button;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.SDZHDeliveryOrderNumberBean;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2020/4/18 15:58
 * @author 作者: xxblwf
 * @description 只有一个textview的adapter
 */

public class ItemTextViewAdapter extends BaseRecycleAdapter<SDZHDeliveryOrderNumberBean, ItemTextViewAdapter.ItemTextViewViewHolder> {

    private OnViewClickListener onViewClickListener;

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
    }

    @NonNull @Override public ItemTextViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemTextViewViewHolder(parent);
    }

    @Override public void onBindViewHolder(ItemTextViewViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (position == selectPosition){
            holder.itemView.setSelected(true);
        }
        holder.itemView.setOnClickListener(v -> {
            holder.itemView .setSelected(!holder.itemView.isSelected());
            if (onViewClickListener != null){
                onViewClickListener.onClickAction(v,"",dataList.get(position));
            }
//            holder.itemTextView.setBackgroundColor(Color.RED);
        });
//        if (selectPosition == position){
//            holder.itemTextView.setBackgroundColor(null);
//        }
    }

    public static class ItemTextViewViewHolder extends BaseViewHolder {


        @BindView(R.id.item_textview_textView) Button itemTextView;

        public ItemTextViewViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_textview_layout);
            ButterKnife.bind(this, itemView);
        }

        @Override public <T> void initUIData(T t) {
            SDZHDeliveryOrderNumberBean bean = (SDZHDeliveryOrderNumberBean) t;
            if (bean != null) {
                itemTextView.setText(bean.getOrderNo());
            }
        }
    }
}
