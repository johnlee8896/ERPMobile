package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.ShipWorkModels.InventoryLine;
import com.chinashb.www.mobileerp.bean.ShipWorkOrderBean;
import com.chinashb.www.mobileerp.utils.ShipWorkFormatUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户下的发货指令列表，可展开指令库存。
 */
public class ShipWorkOrderFlowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ORDER = 0;
    private static final int TYPE_INVENTORY = 1;
    private static final int TYPE_INVENTORY_HEADER = 2;

    public interface OnOrderActionListener {
        void onOrderAction(ShipWorkOrderBean order);
    }

    public interface OnInventoryToggleListener {
        void onInventoryToggle(long doId);
    }

    private final List<ShipWorkOrderBean> orders = new ArrayList<>();
    private final Map<Long, List<InventoryLine>> inventoryMap = new HashMap<>();
    private final Map<Long, Boolean> loadedMap = new HashMap<>();
    private final List<Row> rows = new ArrayList<>();
    private long expandedDoId = -1L;
    private OnOrderActionListener actionListener;
    private OnInventoryToggleListener inventoryToggleListener;

    public ShipWorkOrderFlowAdapter setOnOrderActionListener(OnOrderActionListener listener) {
        this.actionListener = listener;
        return this;
    }

    public ShipWorkOrderFlowAdapter setOnInventoryToggleListener(OnInventoryToggleListener listener) {
        this.inventoryToggleListener = listener;
        return this;
    }

    public void setOrders(List<ShipWorkOrderBean> list) {
        orders.clear();
        inventoryMap.clear();
        loadedMap.clear();
        expandedDoId = -1L;
        if (list != null) {
            orders.addAll(list);
        }
        rebuildRows();
    }

    public void setInventory(long doId, List<InventoryLine> list) {
        inventoryMap.put(doId, list == null ? new ArrayList<InventoryLine>() : list);
        loadedMap.put(doId, true);
        rebuildRows();
    }

    public boolean isInventoryLoaded(long doId) {
        return loadedMap.containsKey(doId) && loadedMap.get(doId);
    }

    public void toggleOrder(long doId) {
        expandedDoId = expandedDoId == doId ? -1L : doId;
        rebuildRows();
    }

    private void rebuildRows() {
        rows.clear();
        for (ShipWorkOrderBean order : orders) {
            rows.add(Row.order(order));
            if (order.getDO_ID() == expandedDoId) {
                List<InventoryLine> lines = inventoryMap.get(order.getDO_ID());
                if (lines != null) {
                    rows.add(Row.inventoryHeader());
                    for (InventoryLine line : lines) {
                        rows.add(Row.inventory(line));
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position).type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_INVENTORY) {
            return new InventoryViewHolder(View.inflate(parent.getContext(), R.layout.item_ship_work_inventory_flow, null));
        }
        if (viewType == TYPE_INVENTORY_HEADER) {
            return new HeaderViewHolder(View.inflate(parent.getContext(), R.layout.item_ship_work_detail_header, null));
        }
        return new OrderViewHolder(View.inflate(parent.getContext(), R.layout.item_ship_work_order_flow, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Row row = rows.get(position);
        if (holder instanceof OrderViewHolder) {
            ((OrderViewHolder) holder).bind(row.order);
        } else if (holder instanceof InventoryViewHolder) {
            ((InventoryViewHolder) holder).bind(row.inventory);
        }
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    private static class Row {
        final int type;
        final ShipWorkOrderBean order;
        final InventoryLine inventory;

        private Row(int type, ShipWorkOrderBean order, InventoryLine inventory) {
            this.type = type;
            this.order = order;
            this.inventory = inventory;
        }

        static Row order(ShipWorkOrderBean order) {
            return new Row(TYPE_ORDER, order, null);
        }

        static Row inventory(InventoryLine inventory) {
            return new Row(TYPE_INVENTORY, null, inventory);
        }

        static Row inventoryHeader() {
            return new Row(TYPE_INVENTORY_HEADER, null, null);
        }
    }

    private class OrderViewHolder extends RecyclerView.ViewHolder {
        final TextView trackTextView;
        final TextView customerTextView;
        final TextView infoTextView;
        final Button actionButton;

        OrderViewHolder(View itemView) {
            super(itemView);
            trackTextView = itemView.findViewById(R.id.item_ship_work_order_flow_track_textView);
            customerTextView = itemView.findViewById(R.id.item_ship_work_order_flow_customer_textView);
            infoTextView = itemView.findViewById(R.id.item_ship_work_order_flow_info_textView);
            actionButton = itemView.findViewById(R.id.item_ship_work_order_flow_action_button);
        }

        void bind(final ShipWorkOrderBean order) {
            String trackNo = order.getTrackNo() == null || order.getTrackNo().isEmpty()
                    ? "指令 " + order.getDO_ID()
                    : order.getTrackNo();
            trackTextView.setText("单号 " + trackNo);
            customerTextView.setText(order.getCustomer_Name());
            String date = ShipWorkFormatUtil.date(order.getDelivery_Date());
            infoTextView.setText("指令号 " + order.getDO_ID() + "  日期 " + date);
            if (order.getWork_ID() != null && order.getWork_ID() > 0) {
                actionButton.setText("继续作业");
            } else {
                actionButton.setText("设置");
            }
            itemView.setOnClickListener(v -> {
                toggleOrder(order.getDO_ID());
                if (inventoryToggleListener != null) {
                    inventoryToggleListener.onInventoryToggle(order.getDO_ID());
                }
            });
            actionButton.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onOrderAction(order);
                }
            });
        }
    }

    private class InventoryViewHolder extends RecyclerView.ViewHolder {
        final TextView textView;

        InventoryViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_ship_work_inventory_flow_textView);
        }

        void bind(InventoryLine line) {
            String name = line.productName == null || line.productName.isEmpty()
                    ? String.valueOf(line.productId)
                    : line.productName;
            String customerPart = line.customerPartNo == null || line.customerPartNo.isEmpty()
                    ? ""
                    : "\n客户零件号 " + line.customerPartNo;
            String status;
            if (!line.inventoryEnough) {
                status = "库存不足";
            } else {
                status = "库存足够";
            }
            textView.setText("产品 " + name + "  PS_ID " + line.psId + customerPart
                    + "\n需求 " + format(line.dpiQuantity)
                    + "  剩余 " + format(line.remainingQuantity)
                    + "  库存 " + format(line.inventoryQuantity)
                    + "\n可用 " + format(line.availableInventoryQuantity)
                    + "  已锁 " + format(line.lockedQuantity)
                    + "  " + status
                    + (line.inventoryLocations == null ? "" : "\n" + line.inventoryLocations));
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static String format(double value) {
        if (value == Math.rint(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }
}
