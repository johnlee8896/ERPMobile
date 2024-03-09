package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.BigAreaSumBean;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2024/2/28 6:54 PM
 * @author 作者: liweifeng
 * @description 大区查询
 */

public class BigAreaQtyAdapter extends BaseRecycleAdapter<BigAreaSumBean, BigAreaQtyAdapter.SelectStorageAreaViewHolder> {

    private OnViewClickListener onViewClickListener;
    private List<String> selectAreaEntityList;

    public BigAreaQtyAdapter setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
        return this;
    }

    public List<String> getSelectAreaEntityList() {
        return selectAreaEntityList;
    }

//    @Override
//    public void setData(List<BigAreaSumBean> list) {
//        super.setData(list);
//    }

    @NonNull
    @Override
    public BigAreaQtyAdapter.SelectStorageAreaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BigAreaQtyAdapter.SelectStorageAreaViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(BigAreaQtyAdapter.SelectStorageAreaViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.itemView.setOnClickListener(v -> {
//            if (holder.imageView.getVisibility() == View.GONE){
//                holder.imageView.setVisibility(View.VISIBLE);
//            }else{
//                holder.imageView.setVisibility(View.GONE);
//            }
//            dataList.get(position).setSelect(holder.imageView.getVisibility() == View.VISIBLE);
            if (onViewClickListener != null) {
                onViewClickListener.onClickAction(v, "", dataList.get(position));
            }
        });
    }

    public static class SelectStorageAreaViewHolder extends BaseViewHolder {
        @BindView(R.id.item_select_storage_area_textView) TextView areaTextView;
        @BindView(R.id.item_select_storage_area_imageView) ImageView imageView;

        public SelectStorageAreaViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_select_storage_area_layout);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public <T> void initUIData(T t) {
            BigAreaSumBean entity = (BigAreaSumBean) t;
            if (entity != null) {
                areaTextView.setText(String.format("%S %S",entity.getLayoutName(),entity.getSumQty()));
            }
        }
    }
}


