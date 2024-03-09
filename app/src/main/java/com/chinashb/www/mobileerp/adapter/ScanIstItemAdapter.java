package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.ScanISTItemPartBean;
import com.chinashb.www.mobileerp.utils.UnitFormatUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2023/7/4 6:57 PM
 * @author 作者: liweifeng
 * @description
 */
public class ScanIstItemAdapter extends BaseRecycleAdapter<ScanISTItemPartBean, ScanIstItemAdapter.ScanISTItemViewHolder> {

    @NonNull
    @Override
    public ScanISTItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ScanISTItemViewHolder(parent);
    }

    public static class ScanISTItemViewHolder extends BaseViewHolder {


        @BindView(R.id.item_scan_ist_item_version_textView) TextView itemVersionTextView;
        @BindView(R.id.item_scan_ist_area_textView) TextView areaTextView;
        @BindView(R.id.item_scan_stock_number_textView) TextView stockNumberTextView;
        @BindView(R.id.item_scan_ist_box_in_date_stock_textView) TextView inDateStockTextView;
        @BindView(R.id.item_scan_ist_box_in_stock_lot_textView) TextView inStockLotTextView;
        @BindView(R.id.item_scan_ist_make_date_textView) TextView makeDateTextView;
        @BindView(R.id.item_scan_ist_part_make_lot_textView) TextView makeLotTextView;
        @BindView(R.id.item_scan_ist_item_state_textView) TextView stateTextView;
        @BindView(R.id.item_scan_ist_in_days_textView) TextView inDaysTextView;
        @BindView(R.id.item_scan_stock_can_use_days_textView) TextView canUseDaysTextView;
        @BindView(R.id.item_scan_ist_item_id_textView) TextView itemIdTextView;
        @BindView(R.id.item_scan_ist_item_name_textView) TextView itemNameTextView;
        @BindView(R.id.item_scan_item_spec_textView) TextView specTextView;

        public ScanISTItemViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_scan_ist_item_detail_layout);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public <T> void initUIData(T t) {
            ScanISTItemPartBean entity = (ScanISTItemPartBean) t;
            if (entity != null) {
                itemVersionTextView.setText(entity.get物料版本());
                areaTextView.setText(entity.get单元());
                stockNumberTextView.setText(entity.get库存() + "");
//                inDateStockTextView.setText(entity.get入库日期());
                String originalDateString = entity.get入库日期();
//                Date(1686844800000+0800)
                if (!TextUtils.isEmpty(originalDateString)) {
                    String dateMillSecondString = originalDateString.substring(6, 19);
                    inDateStockTextView.setText(UnitFormatUtil.formatTimeToDayChinese(Long.parseLong(dateMillSecondString)));
                }

                inStockLotTextView.setText(entity.get入库批次());
                makeDateTextView.setText(entity.get生产日期());
                makeLotTextView.setText(entity.get生产批次());
                stateTextView.setText(entity.get状态());
                inDaysTextView.setText(entity.get在库日期() + "");
                canUseDaysTextView.setText(entity.get质保天数() + "");
                itemIdTextView.setText(entity.getItem_ID() + "");
                itemNameTextView.setText(entity.getItem_Name());
                specTextView.setText(entity.getItem_Spec2());
            }
        }
    }
}
