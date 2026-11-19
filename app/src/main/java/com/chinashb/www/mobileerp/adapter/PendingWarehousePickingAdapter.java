package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.PendingWarehousePickingBean;
import com.chinashb.www.mobileerp.utils.UnitFormatUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * codex by john 2026-11-12 - 来料待入库拣货列表 Adapter
 */
public class PendingWarehousePickingAdapter
        extends BaseRecycleAdapter<PendingWarehousePickingBean, PendingWarehousePickingAdapter.PickingViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(PendingWarehousePickingBean bean);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PickingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PickingViewHolder(parent);
    }

    public class PickingViewHolder extends BaseViewHolder {
        @BindView(R.id.pending_picking_location_text) TextView locationTextView;
        @BindView(R.id.pending_picking_shortage_text) TextView shortageTextView;
        @BindView(R.id.pending_picking_item_text) TextView itemTextView;
        @BindView(R.id.pending_picking_spec_lot_text) TextView specLotTextView;
        @BindView(R.id.pending_picking_supplier_text) TextView supplierTextView;
        @BindView(R.id.pending_picking_qty_text) TextView qtyTextView;

        PickingViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_pending_warehouse_picking);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public <T> void initUIData(T t) {
            final PendingWarehousePickingBean bean = (PendingWarehousePickingBean) t;
            if (bean == null) {
                return;
            }
            String location = bean.getDisplayLocation();
            locationTextView.setText(TextUtils.isEmpty(location) ? "未指定储位" : location);
            bindShortage(bean);
            itemTextView.setText(bean.getItemCode() + "  " + bean.getItemName());

            StringBuilder specLot = new StringBuilder();
            if (!TextUtils.isEmpty(bean.getItemSpec())) {
                specLot.append("规格：").append(bean.getItemSpec());
            }
            if (!TextUtils.isEmpty(bean.getLotNo())) {
                if (specLot.length() > 0) {
                    specLot.append("  ");
                }
                specLot.append("批次：").append(bean.getLotNo());
            }
            specLotTextView.setText(specLot.toString());

            supplierTextView.setText("供应商：" + bean.getSupplierName());
            qtyTextView.setText("数量：" + formatQty(bean.getQty())
                    + "  件数：" + bean.getPackageQty()
                    + "  接收日期：" + formatReceiveDate(bean));

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(bean);
                }
            });
        }

        private String formatQty(double value) {
            return value == (long) value
                    ? String.valueOf((long) value)
                    : String.valueOf(value);
        }

        // codex by john 2026-11-14 - 当天紧缺红色、次日紧缺黄色，颜色优先使用服务端返回值
        private void bindShortage(PendingWarehousePickingBean bean) {
            int level = bean.getShortageLevel();
            if (level <= 0) {
                shortageTextView.setVisibility(View.GONE);
                return;
            }
            double balance = level == 1
                    ? Math.abs(bean.getTodayTheoreticalBalance())
                    : Math.abs(bean.getTomorrowTheoreticalBalance());
            String label = TextUtils.isEmpty(bean.getShortageLabel())
                    ? (level == 1 ? "当天紧缺" : "次日紧缺")
                    : bean.getShortageLabel();
            shortageTextView.setText(label + "  缺 " + formatQty(balance));
            shortageTextView.setBackgroundColor(resolveColor(
                    bean.getShortageColor(), level == 1 ? 0xFFFF8080 : 0xFFFFF59D));
            shortageTextView.setTextColor(Color.BLACK);
            shortageTextView.setVisibility(View.VISIBLE);
        }

        private int resolveColor(String color, int fallback) {
            if (!TextUtils.isEmpty(color)) {
                try {
                    return Color.parseColor(color);
                } catch (Exception ignored) {
                }
            }
            return fallback;
        }

        // codex by john 2026-11-12 - 兼容 /Date(毫秒)、/Date(毫秒+0800)/ 和带 T 的日期
        private String formatReceiveDate(PendingWarehousePickingBean bean) {
            String raw = bean.getReceiveDate();
            if (TextUtils.isEmpty(raw)) {
                raw = bean.getReceiveTime();
            }
            if (TextUtils.isEmpty(raw)) {
                return "";
            }

            if (raw.startsWith("/Date(")) {
                try {
                    int start = raw.indexOf('(') + 1;
                    int end = raw.indexOf('+');
                    if (end < 0 || end >= raw.indexOf(')')) {
                        end = raw.indexOf(')');
                    }
                    if (end > start) {
                        long millis = Long.parseLong(raw.substring(start, end));
                        return UnitFormatUtil.formatTimeToDay(millis);
                    }
                } catch (Exception ignored) {
                }
                return raw;
            }

            return UnitFormatUtil.getFormatDateStringRemoveTYMD(raw);
        }
    }
}
