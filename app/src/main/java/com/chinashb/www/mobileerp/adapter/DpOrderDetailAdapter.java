package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.DpOrderDetailBean;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2020/4/7 15:30
 * @author 作者: xxblwf
 * @description 发货指令item详情
 */

public class DpOrderDetailAdapter extends BaseRecycleAdapter<DpOrderDetailBean, DpOrderDetailAdapter.DpOrderDetailViewHolder> {

    private OnViewClickListener onViewClickListener;

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
    }

    @NonNull @Override public DpOrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DpOrderDetailViewHolder(parent);
    }

    @Override public void onBindViewHolder(DpOrderDetailViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
//        holder.itemView.setOnClickListener();
        holder.itemView.setOnClickListener(v -> {
            notifyDataSetChanged();
            holder.itemView.setSelected(!holder.itemView.isSelected());
            if (onViewClickListener != null) {
                onViewClickListener.onClickAction(v, "", holder.itemView.isSelected() ? dataList.get(position) : null);
            }
        });
    }

    public static class DpOrderDetailViewHolder extends BaseViewHolder {

        @BindView(R.id.item_dp_order_detail_project_textView) TextView projectTextView;
        @BindView(R.id.item_dp_order_detail_product_id_textView) TextView productIdTextView;
        @BindView(R.id.item_dp_order_detail_common_name_textView) TextView commonNameTextView;
        @BindView(R.id.item_dp_order_detail_part_number_textView) TextView partNumberTextView;
        @BindView(R.id.item_dp_order_detail_version_textView) TextView versionTextView;
//        @BindView(R.id.item_dp_order_detail_qty_EditText) EditText qtyEditText;
        @BindView(R.id.item_dp_order_detail_qty_TextView) TextView qtyTextView;

        public DpOrderDetailViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_dp_order_detail_layout);
            ButterKnife.bind(this, itemView);
        }

        @Override public <T> void initUIData(T t) {
            DpOrderDetailBean bean = (DpOrderDetailBean) t;
            if (bean != null) {
                projectTextView.setText(bean.getProgramName());
                productIdTextView.setText(bean.getProductID() + "");
                commonNameTextView.setText(bean.getProductChineseName());
                partNumberTextView.setText(bean.getProductPartNo());
                versionTextView.setText(bean.getProductVersion());
//                qtyEditText.setText(bean.getDPIQuantity());
                qtyTextView.setText(bean.getDPIQuantity());
            }
//            qtyEditText.addTextChangedListener(new TextWatcherImpl() {
//                @Override public void afterTextChanged(Editable editable) {
//                    super.afterTextChanged(editable);
//                    if (TextUtils.isDigitsOnly(editable.toString())) {
//                        bean.setDPIQuantity(editable.toString());
//                        System.out.println("==============qty =" + bean.getDPIQuantity());
//                    }
//                }
//            });
        }
    }
}
