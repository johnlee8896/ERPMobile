package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.SDZHSinglePartBean;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2020/2/26 4:03 PM
 * @author 作者: liweifeng
 * @description 三点照合 发货单中 发货箱中单机详情 adapter
 */
public class SDZHSinglePartDetailAdapter extends BaseRecycleAdapter<SDZHSinglePartBean, SDZHSinglePartDetailAdapter.SDZHSinglePartViewHolder> {


    @NonNull
    @Override
    public SDZHSinglePartDetailAdapter.SDZHSinglePartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SDZHSinglePartDetailAdapter.SDZHSinglePartViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(SDZHSinglePartDetailAdapter.SDZHSinglePartViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
//        holder.itemView
    }

    public static class SDZHSinglePartViewHolder extends BaseViewHolder {


        @BindView(R.id.item_sdzh_single_serial_number_textView) TextView serialNumberTextView;
        @BindView(R.id.item_sdzh_single_customer_number_textView) TextView customerNumberTextView;
        @BindView(R.id.item_sdzh_single_bar_code_textView) TextView barCodeTextView;

        public SDZHSinglePartViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_sdzh_single_part_layout);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public <T> void initUIData(T t) {
            SDZHSinglePartBean entity = (SDZHSinglePartBean) t;
            if (entity != null) {
                //// TODO: 2020/2/27  ? 
                serialNumberTextView.setText(entity.getDoiNO());
                customerNumberTextView.setText(entity.getProductNo());
                barCodeTextView.setText(entity.getDOI_Code());
            }
        }
    }
}

