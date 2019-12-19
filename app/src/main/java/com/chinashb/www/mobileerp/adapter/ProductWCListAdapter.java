package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.entity.WcIdNameEntity;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2019/12/19 14:25
 * @author 作者: xxblwf
 * @description 成品产线（工作中心）adapter
 */

public class ProductWCListAdapter extends BaseRecycleAdapter<WcIdNameEntity, ProductWCListAdapter.ProductWCListViewHolder> {

    private OnViewClickListener onViewClickListener;

    public ProductWCListAdapter(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
    }

    @NonNull @Override public ProductWCListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductWCListViewHolder(parent);
    }

    @Override public void onBindViewHolder(@NonNull ProductWCListViewHolder holder, int position) {
        super.onBindViewHolder(holder,position);
        holder.itemView.setOnClickListener(v -> {
            if (onViewClickListener != null){
                onViewClickListener.onClickAction(v,"",dataList.get(position));
            }
        });
    }

    public static class ProductWCListViewHolder extends BaseViewHolder {

        @BindView(R.id.item_wclist_name_textView) TextView nameTextView;

        public ProductWCListViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.product_wclist_item_layout);
            ButterKnife.bind(this,itemView);
        }

        @Override public <T> void initUIData(T t) {
            if (t != null) {
                WcIdNameEntity entity = (WcIdNameEntity) t;
                nameTextView.setText(entity.getWcName());
            }
        }
    }
}
