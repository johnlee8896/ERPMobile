package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.BoxMoveRecordBean;
import com.chinashb.www.mobileerp.utils.UnitFormatUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2023/7/12 10:06 AM
 * @author 作者: liweifeng
 * @description
 */
public class MoveRecordAdapter extends BaseRecycleAdapter<BoxMoveRecordBean, MoveRecordAdapter.BoxMoveRecordViewHolder> {
    @NonNull
    @Override
    public BoxMoveRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BoxMoveRecordViewHolder(parent);
    }

    public static class BoxMoveRecordViewHolder extends BaseViewHolder {


        @BindView(R.id.item_move_record_from_ist_textView) TextView fromIstTextView;
        @BindView(R.id.item_move_record_from_sub_ist_textView) TextView fromSubIstTextView;
        @BindView(R.id.item_move_record_to_ist_textView) TextView toIstTextView;
        @BindView(R.id.item_move_record_to_sub_ist_textView) TextView toSubIstTextView;
        @BindView(R.id.item_move_record_box_type_textView) TextView boxTypeTextView;
        @BindView(R.id.item_move_record_bu_name_textView) TextView buNameTextView;
        @BindView(R.id.item_move_record_box_code_textView) TextView boxCodeTextView;
        @BindView(R.id.item_move_record_operator_textView) TextView operatorTextView;
        @BindView(R.id.item_move_record_date_textView) TextView dateTextView;
        @BindView(R.id.item_move_record_item_id_textView) TextView itemIdTextView;
        @BindView(R.id.item_move_record_item_name_textView) TextView itemNameTextView;
        @BindView(R.id.item_move_record_item_version_textView) TextView versionTextView;
        @BindView(R.id.item_move_record_qty_textView) TextView qtyTextView;

        public BoxMoveRecordViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_move_record_detail_layout);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public <T> void initUIData(T t) {
            if (t != null) {
                BoxMoveRecordBean bean = (BoxMoveRecordBean) t;
                if (bean != null) {
                    buNameTextView.setText(bean.getBuName());
                    boxCodeTextView.setText(bean.getBoxcode());
                    fromIstTextView.setText(bean.get移出库位());
                    fromSubIstTextView.setText(bean.get移出单元());
                    toIstTextView.setText(bean.get移到库位());
                    toSubIstTextView.setText(bean.get移到单元());
                    operatorTextView.setText(bean.get移库人());
                    boxTypeTextView.setText(bean.get移库类型());
                    itemIdTextView.setText(bean.getItem_id() + "");
                    itemNameTextView.setText(bean.getItemDrawNO());
                    versionTextView.setText(bean.getItem_version());
                    qtyTextView.setText(bean.getIqty());

                    String originalDateString = bean.get移库时间();
//                Date(1686844800000+0800)
                    if (!TextUtils.isEmpty(originalDateString) && originalDateString.contains("Date")) {
                        String dateMillSecondString = originalDateString.substring(6, 19);
                        dateTextView.setText(UnitFormatUtil.formatTimeToDayChinese(Long.parseLong(dateMillSecondString)));
                    }
//                    dateTextView.setText(bean.get移库时间());
                }

            }

        }
    }
}