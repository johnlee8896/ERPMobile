package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.LogisticsIncomeBean;
import com.chinashb.www.mobileerp.utils.UnitFormatUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * codex by john 2026-09-10 13:37 - 物流来料检索列表 adapter
 */
public class LogisticsIncomeCheckAdapter extends BaseRecycleAdapter<LogisticsIncomeBean, LogisticsIncomeCheckAdapter.LogisticsIncomeViewHolder> {

    @NonNull
    @Override
    public LogisticsIncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LogisticsIncomeViewHolder(parent);
    }

    public static class LogisticsIncomeViewHolder extends BaseViewHolder {
        @BindView(R.id.first_text_textView) TextView processStatusTextView;
        @BindView(R.id.second_text_textView) TextView sendDateTextView;
        @BindView(R.id.third_text_textView) TextView receiveTimeTextView;
        @BindView(R.id.fourth_text_textView) TextView supplierNameTextView;
        @BindView(R.id.fifth_text_textView) TextView itemIdTextView;
        @BindView(R.id.sixth_text_textView) TextView itemNameTextView;
        @BindView(R.id.seventh_text_textView) TextView itemCodeTextView;
        @BindView(R.id.eighth_text_textView) TextView versionTextView;
        @BindView(R.id.ninth_text_textView) TextView productionBatchTextView;
        @BindView(R.id.tenth_text_textView) TextView qtyTextView;
        @BindView(R.id.eleventh_text_textView) TextView packageQtyTextView;
        @BindView(R.id.twelfth_text_textView) TextView checkRemarkTextView;

        public LogisticsIncomeViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_logistics_income_layout);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public <T> void initUIData(T t) {
            LogisticsIncomeBean bean = (LogisticsIncomeBean) t;
            if (bean != null) {
                processStatusTextView.setText(bean.getProcessStatus());
                sendDateTextView.setText(formatDate(bean.getSendDate(), false));
                receiveTimeTextView.setText(formatDate(bean.getReceiveTime(), true));
                supplierNameTextView.setText(bean.getSupplierName());
                itemIdTextView.setText(bean.getItemId() + "");
                itemNameTextView.setText(bean.getItemName());
                itemCodeTextView.setText(bean.getItemCode());
                versionTextView.setText(bean.getVersion());
                productionBatchTextView.setText(bean.getProductionBatch());
                qtyTextView.setText(formatQty(bean.getQty()));
                packageQtyTextView.setText(formatQty(bean.getPackageQty()));
                checkRemarkTextView.setText(bean.getCheckRemark());
            }
        }

        // codex by john 2026-09-10 15:00 - 小数点后是0时显示整数，如 2160.0 -> 2160
        private String formatQty(double value) {
            if (value == (long) value) {
                return String.valueOf((long) value);
            }
            return String.valueOf(value);
        }

        // codex by john 2026-09-10 14:49 - 把服务端 /Date(毫秒+0800)/ 转成可读时间，includeTime=true 时带时分秒
        private String formatDate(String original, boolean includeTime) {
            if (TextUtils.isEmpty(original)) {
                return "";
            }
            try {
                int start = original.indexOf('(');
                int end = original.indexOf('+');
                if (end < 0) {
                    end = original.indexOf(')');
                }
                if (start >= 0 && end > start) {
                    long millis = Long.parseLong(original.substring(start + 1, end));
                    return includeTime
                            ? UnitFormatUtil.formatTimeToSecond(millis)
                            : UnitFormatUtil.formatTimeToDayChinese(millis);
                }
            } catch (Exception ignored) {
            }
            return original;
        }
    }
}
