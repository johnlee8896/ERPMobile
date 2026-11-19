package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.ShipWorkLineBean;
import com.chinashb.www.mobileerp.bean.ShipWorkOrderBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 成品发货 - 发货指令（按客户分组 + 指令 + 明细）adapter
 */
public class ShipWorkOrderGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CUSTOMER = 0;
    private static final int TYPE_ORDER = 1;
    private static final int TYPE_LINE_HEADER = 2;
    private static final int TYPE_LINE = 3;

    public interface OnQueryInventoryClickListener {
        void onQueryInventoryClick(ShipWorkLineBean bean, TextView statusTextView);
    }

    public interface OnCustomerClickListener {
        void onCustomerClick(CustomerGroup customer);
    }

    public interface OnOrderClickListener {
        void onOrderClick(ShipWorkOrderBean order);
    }

    public static class CustomerGroup {
        public final long cfId;
        public final String customerName;
        public final List<ShipWorkOrderBean> orders = new ArrayList<>();

        public CustomerGroup(long cfId, String customerName) {
            this.cfId = cfId;
            this.customerName = customerName == null ? "" : customerName;
        }
    }

    private final List<CustomerGroup> customerGroups = new ArrayList<>();
    private final Map<Long, List<ShipWorkLineBean>> lineMap = new HashMap<>();
    private final Map<Long, Boolean> lineLoadedMap = new HashMap<>();
    private final List<Row> rows = new ArrayList<>();

    private long expandedCustomerCfId = -1L;
    private long expandedOrderDoId = -1L;

    private OnQueryInventoryClickListener queryListener;
    private OnCustomerClickListener customerListener;
    private OnOrderClickListener orderListener;

    public ShipWorkOrderGroupAdapter setOnQueryInventoryClickListener(OnQueryInventoryClickListener listener) {
        this.queryListener = listener;
        return this;
    }

    public ShipWorkOrderGroupAdapter setOnCustomerClickListener(OnCustomerClickListener listener) {
        this.customerListener = listener;
        return this;
    }

    public ShipWorkOrderGroupAdapter setOnOrderClickListener(OnOrderClickListener listener) {
        this.orderListener = listener;
        return this;
    }

    public void setOrders(List<ShipWorkOrderBean> orders) {
        customerGroups.clear();
        lineMap.clear();
        lineLoadedMap.clear();
        expandedCustomerCfId = -1L;
        expandedOrderDoId = -1L;

        LinkedHashMap<Long, CustomerGroup> groupMap = new LinkedHashMap<>();
        if (orders != null) {
            for (ShipWorkOrderBean order : orders) {
                long cfId = order.getCF_ID();
                CustomerGroup group = groupMap.get(cfId);
                if (group == null) {
                    group = new CustomerGroup(cfId, order.getCustomer_Name());
                    groupMap.put(cfId, group);
                }
                group.orders.add(order);
            }
        }
        customerGroups.addAll(groupMap.values());
        rebuildRows();
    }

    public void setLines(long doId, List<ShipWorkLineBean> lines) {
        lineMap.put(doId, lines == null ? new ArrayList<ShipWorkLineBean>() : lines);
        lineLoadedMap.put(doId, true);
        rebuildRows();
    }

    public boolean isLineLoaded(long doId) {
        return lineLoadedMap.containsKey(doId) && lineLoadedMap.get(doId);
    }

    public void toggleCustomer(long cfId) {
        if (expandedCustomerCfId == cfId) {
            expandedCustomerCfId = -1L;
        } else {
            expandedCustomerCfId = cfId;
        }
        expandedOrderDoId = -1L;
        rebuildRows();
    }

    public void setExpandedCustomer(long cfId) {
        expandedCustomerCfId = cfId;
        expandedOrderDoId = -1L;
        rebuildRows();
    }

    public void toggleOrder(long doId) {
        if (expandedOrderDoId == doId) {
            expandedOrderDoId = -1L;
        } else {
            expandedOrderDoId = doId;
        }
        rebuildRows();
    }

    public void setExpandedOrder(long doId) {
        expandedOrderDoId = doId;
        rebuildRows();
    }

    public List<CustomerGroup> getCustomerGroups() {
        return customerGroups;
    }

    public CustomerGroup getFirstCustomer() {
        return customerGroups.isEmpty() ? null : customerGroups.get(0);
    }

    public ShipWorkOrderBean getFirstOrder(CustomerGroup customer) {
        return customer == null || customer.orders.isEmpty() ? null : customer.orders.get(0);
    }

    private void rebuildRows() {
        rows.clear();
        for (CustomerGroup customer : customerGroups) {
            rows.add(Row.customer(customer));
            if (customer.cfId == expandedCustomerCfId) {
                for (ShipWorkOrderBean order : customer.orders) {
                    rows.add(Row.order(customer, order));
                    if (order.getDO_ID() == expandedOrderDoId && isLineLoaded(order.getDO_ID())) {
                        rows.add(Row.lineHeader(customer, order));
                        List<ShipWorkLineBean> lines = lineMap.get(order.getDO_ID());
                        if (lines != null) {
                            for (ShipWorkLineBean line : lines) {
                                rows.add(Row.line(customer, order, line));
                            }
                        }
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
        if (viewType == TYPE_CUSTOMER) {
            View v = View.inflate(parent.getContext(), R.layout.item_ship_work_customer, null);
            return new CustomerViewHolder(v);
        } else if (viewType == TYPE_ORDER) {
            View v = View.inflate(parent.getContext(), R.layout.item_ship_work_order, null);
            return new OrderViewHolder(v);
        } else if (viewType == TYPE_LINE_HEADER) {
            View v = View.inflate(parent.getContext(), R.layout.item_ship_work_detail_header, null);
            return new LineHeaderViewHolder(v);
        } else {
            View v = View.inflate(parent.getContext(), R.layout.item_ship_work_line, null);
            return new LineViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Row row = rows.get(position);
        if (holder instanceof CustomerViewHolder) {
            ((CustomerViewHolder) holder).bind(row.customer, row.customer.cfId == expandedCustomerCfId);
        } else if (holder instanceof OrderViewHolder) {
            ((OrderViewHolder) holder).bind(row.order, row.order.getDO_ID() == expandedOrderDoId);
        } else if (holder instanceof LineViewHolder) {
            ((LineViewHolder) holder).bind(row.line, queryListener);
        }
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    private static class Row {
        final int type;
        final CustomerGroup customer;
        final ShipWorkOrderBean order;
        final ShipWorkLineBean line;

        private Row(int type, CustomerGroup customer, ShipWorkOrderBean order, ShipWorkLineBean line) {
            this.type = type;
            this.customer = customer;
            this.order = order;
            this.line = line;
        }

        static Row customer(CustomerGroup customer) {
            return new Row(TYPE_CUSTOMER, customer, null, null);
        }

        static Row order(CustomerGroup customer, ShipWorkOrderBean order) {
            return new Row(TYPE_ORDER, customer, order, null);
        }

        static Row lineHeader(CustomerGroup customer, ShipWorkOrderBean order) {
            return new Row(TYPE_LINE_HEADER, customer, order, null);
        }

        static Row line(CustomerGroup customer, ShipWorkOrderBean order, ShipWorkLineBean line) {
            return new Row(TYPE_LINE, customer, order, line);
        }
    }

    private class CustomerViewHolder extends RecyclerView.ViewHolder {
        final TextView customerTextView;
        final ImageView indicator;

        CustomerViewHolder(View itemView) {
            super(itemView);
            customerTextView = itemView.findViewById(R.id.item_ship_work_customer_textView);
            indicator = itemView.findViewById(R.id.item_ship_work_customer_indicator);
        }

        void bind(final CustomerGroup customer, boolean expanded) {
            customerTextView.setText(customer.orders.size() + "个待发指令 " + customer.customerName);
            indicator.setImageResource(expanded
                    ? R.drawable.expander_ic_mini_expanded
                    : R.drawable.expander_ic_mini_collapsed);
            itemView.setOnClickListener(v -> {
                if (customerListener != null) {
                    customerListener.onCustomerClick(customer);
                }
            });
        }
    }

    private class OrderViewHolder extends RecyclerView.ViewHolder {
        final TextView customerTextView;
        final TextView trackTextView;
        final TextView doTextView;
        final ImageView indicator;

        OrderViewHolder(View itemView) {
            super(itemView);
            customerTextView = itemView.findViewById(R.id.item_ship_work_order_customer_textView);
            trackTextView = itemView.findViewById(R.id.item_ship_work_order_track_textView);
            doTextView = itemView.findViewById(R.id.item_ship_work_order_do_textView);
            indicator = itemView.findViewById(R.id.item_ship_work_order_indicator);
        }

        void bind(final ShipWorkOrderBean order, boolean expanded) {
            customerTextView.setText(order.getCustomer_Name());
            trackTextView.setText("单号 " + order.getTrackNo());
            doTextView.setText("指令号 " + order.getDO_ID());
            indicator.setImageResource(expanded
                    ? R.drawable.expander_ic_mini_expanded
                    : R.drawable.expander_ic_mini_collapsed);
            itemView.setOnClickListener(v -> {
                if (orderListener != null) {
                    orderListener.onOrderClick(order);
                }
            });
        }
    }

    private class LineHeaderViewHolder extends RecyclerView.ViewHolder {
        LineHeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class LineViewHolder extends RecyclerView.ViewHolder {
        final TextView productTextView;
        final TextView qtyTextView;
        final Button queryButton;
        final TextView statusTextView;

        LineViewHolder(View itemView) {
            super(itemView);
            productTextView = itemView.findViewById(R.id.item_ship_work_line_product_textView);
            qtyTextView = itemView.findViewById(R.id.item_ship_work_line_qty_textView);
            queryButton = itemView.findViewById(R.id.item_ship_work_line_query_button);
            statusTextView = itemView.findViewById(R.id.item_ship_work_line_status_textView);
        }

        void bind(final ShipWorkLineBean bean, final OnQueryInventoryClickListener listener) {
            productTextView.setText("物料 " + bean.getProduct_ID() + " / PS_ID " + bean.getPS_ID());
            qtyTextView.setText("需求 " + bean.getDPI_Quantity()
                    + " 可发 " + bean.getAvailable_Quantity()
                    + " 已预留 " + bean.getReserved_Quantity());
            statusTextView.setText("");
            queryButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onQueryInventoryClick(bean, statusTextView);
                }
            });
        }
    }
}
