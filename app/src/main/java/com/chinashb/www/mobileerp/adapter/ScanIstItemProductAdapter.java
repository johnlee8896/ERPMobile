package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.ScanIstItemProductBean;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2023/8/12 2:42 PM
 * @author 作者: liweifeng
 * @description
 */
public class ScanIstItemProductAdapter extends BaseRecycleAdapter<ScanIstItemProductBean, ScanIstItemProductAdapter.ScanISTItemProductViewHolder> {

    @NonNull
    @Override
    public ScanISTItemProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ScanISTItemProductViewHolder(parent);
    }

    public static class ScanISTItemProductViewHolder extends BaseViewHolder {


        @BindView(R.id.item_scan_product_ist_product_name_textView) TextView NameTextView;
        @BindView(R.id.item_scan_product_ist_product_no_textView) TextView productNoTextView;
        @BindView(R.id.item_scan_product_ist_area_textView) TextView areaTextView;
        @BindView(R.id.item_scan_product_ist_lotid_textView) TextView lotidTextView;
        @BindView(R.id.item_scan_product_ist_lotno_textView) TextView lotnoTextView;
        @BindView(R.id.item_scan_product_ist_qty_textView) TextView qtyTextView;
        @BindView(R.id.item_scan_product_ist_in_pallet_textView) TextView isInPalletTextView;
        @BindView(R.id.item_scan_product_status_textView) TextView statusTextView;
        @BindView(R.id.item_scan_product_ist_serialNo_textView) TextView serialNoTextView;

        public ScanISTItemProductViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_scan_ist_product_item_detail_layout);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public <T> void initUIData(T t) {
            ScanIstItemProductBean entity = (ScanIstItemProductBean) t;
            if (entity != null) {
                NameTextView.setText(entity.getProductChineseName());
                productNoTextView.setText(entity.getProductPartNo());
                areaTextView.setText(entity.get单元());
                lotidTextView.setText(String.valueOf(entity.getLotID()));
                lotnoTextView.setText(entity.get批次号());
                qtyTextView.setText(String.valueOf(entity.get库存()));
                isInPalletTextView.setText(entity.is在托盘() ? "是" : "否");
                statusTextView.setText(entity.get状态());
                serialNoTextView.setText(entity.get托盘序号());

            }
        }
    }
}

