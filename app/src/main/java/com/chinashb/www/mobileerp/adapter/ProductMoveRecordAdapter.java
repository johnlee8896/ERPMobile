package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.ProductBoxMoveRecordBean;
import com.chinashb.www.mobileerp.utils.UnitFormatUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 8/20/24 10:55 AM
 * @author 作者: liweifeng
 * @description 成品移库记录查询
 */
public class ProductMoveRecordAdapter extends BaseRecycleAdapter<ProductBoxMoveRecordBean, ProductMoveRecordAdapter.ProductBoxMoveRecordViewHolder> {
    @NonNull
    @Override
    public ProductMoveRecordAdapter.ProductBoxMoveRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductMoveRecordAdapter.ProductBoxMoveRecordViewHolder(parent);
    }

    public static class ProductBoxMoveRecordViewHolder extends BaseViewHolder {


        @BindView(R.id.item_product_move_record_to_sub_ist_textView) TextView toSubIstTextView;
        @BindView(R.id.item_product_move_record_bu_name_textView) TextView buNameTextView;
        @BindView(R.id.item_product_move_record_box_code_textView) TextView boxCodeTextView;
        @BindView(R.id.item_product_move_record_operator_textView) TextView operatorTextView;
        @BindView(R.id.item_product_move_record_date_textView) TextView dateTextView;

        public ProductBoxMoveRecordViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_product_move_record_detail_layout);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public <T> void initUIData(T t) {
            if (t != null) {
                ProductBoxMoveRecordBean bean = (ProductBoxMoveRecordBean) t;
                if (bean != null) {
                    String buName = "车间名称";
                    if (bean.getBuID() == 81){
                        buName = "滁州座椅";
                    }else if (bean.getBuID() == 1){
                        buName = "上海座椅";
                    }
                    buNameTextView.setText(buName);
                    boxCodeTextView.setText("BoxID/" + bean.getBoxID());
                    toSubIstTextView.setText(bean.getToAreaName());
                    operatorTextView.setText(bean.get移库人());

                    String originalDateString = bean.get移库时间();
                    if (!TextUtils.isEmpty(originalDateString) && originalDateString.contains("Date")) {
                        String dateMillSecondString = originalDateString.substring(6, 19);
                        dateTextView.setText(UnitFormatUtil.formatTimeToSecond(Long.parseLong(dateMillSecondString)));
                    }
                }

            }

        }
    }
}
