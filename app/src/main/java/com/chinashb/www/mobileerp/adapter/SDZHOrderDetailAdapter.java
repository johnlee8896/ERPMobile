package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.SDZHDeliveryOrderNumberDetailBean;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2020/2/25 11:22 PM
 * @author 作者: liweifeng
 * @description 三点照合中发货单号中预设发货单产品明细adapter
 */
public class SDZHOrderDetailAdapter extends BaseRecycleAdapter<SDZHDeliveryOrderNumberDetailBean, SDZHOrderDetailAdapter.SDZHOrderDetailViewHolder> {

    private OnViewClickListener onViewClickListener;
    private List<String> selectWorkLIneStringList;

    public SDZHOrderDetailAdapter setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
        return this;
    }

//    public List<String> getSelectWorkLIneStringList() {
//        selectWorkLIneStringList = new ArrayList<>();
//        for (WorkLineSelectEntity entity : dataList) {
//            if (entity.isSelect()) {
//                selectWorkLIneStringList.add(entity.getWorkLinName());
//            }
//        }
//        return selectWorkLIneStringList;
//    }

    @NonNull
    @Override
    public SDZHOrderDetailAdapter.SDZHOrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SDZHOrderDetailAdapter.SDZHOrderDetailViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(SDZHOrderDetailAdapter.SDZHOrderDetailViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
//        holder.itemView.setOnClickListener(v -> {
//            if (holder.imageView.getVisibility() == View.GONE) {
//                holder.imageView.setVisibility(View.VISIBLE);
//            } else {
//                holder.imageView.setVisibility(View.GONE);
//            }
//            dataList.get(position).setSelect(holder.imageView.getVisibility() == View.VISIBLE);
////            if (onViewClickListener != null) {
////                onViewClickListener.onClickAction(v, "", dataList.get(position));
////            }
//        });
    }

    public static class SDZHOrderDetailViewHolder extends BaseViewHolder {

        @BindView(R.id.item_sdzh_program_id_textView) TextView programIdTextView;
        @BindView(R.id.item_sdzh_program_name_textView) TextView programNameTextView;
        @BindView(R.id.item_sdzh_product_id_textView) TextView productIdTextView;
        @BindView(R.id.item_sdzh_ps_id_textView) TextView psIdTextView;
        @BindView(R.id.item_sdzh_name_textView) TextView nameTextView;
        @BindView(R.id.item_sdzh_product_common_name_textView) TextView productCommonNameTextView;
        @BindView(R.id.item_sdzh_customer_part_number_textView) TextView customerPartNumberTextView;
        @BindView(R.id.item_sdzh_version_textView) TextView versionTextView;
        @BindView(R.id.item_sdzh_newest_version_textView) TextView newestVersionTextView;

        public SDZHOrderDetailViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_sdzh_order_number_layout);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public <T> void initUIData(T t) {
            SDZHDeliveryOrderNumberDetailBean entity = (SDZHDeliveryOrderNumberDetailBean) t;
            if (entity != null) {
//                workLineTextView.setText(entity.getWorkLinName());
                programIdTextView.setText(entity.getProgramID() + "");
                programNameTextView.setText(entity.getProgramName());
                productIdTextView.setText(entity.getProductID() + "");
                psIdTextView.setText(entity.getPSID() + "");
                nameTextView.setText(entity.getProductName());
                productCommonNameTextView.setText(entity.getProductCommonName());
                customerPartNumberTextView.setText(entity.getProductPartNo());
                versionTextView.setText(entity.getPSVersion());
                newestVersionTextView.setText(entity.getNewestVersion());
            }
        }
    }
}
