package com.chinashb.www.mobileerp.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.ShipWorkLineBean;
import com.chinashb.www.mobileerp.bean.ShipWorkOrderBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 成品发货 - 发货指令(可展开明细) adapter
 */
public class ShipWorkOrderExpandableAdapter extends BaseExpandableListAdapter {

    private static final int CHILD_TYPE_HEADER = 0;
    private static final int CHILD_TYPE_LINE = 1;

    private List<ShipWorkOrderBean> groupList = new ArrayList<>();
    private Map<Long, List<ShipWorkLineBean>> lineMap = new HashMap<>();
    private Map<Long, Boolean> loadedMap = new HashMap<>();

    private OnQueryInventoryClickListener onQueryInventoryClickListener;

    public interface OnQueryInventoryClickListener {
        void onQueryInventoryClick(ShipWorkLineBean bean, TextView statusTextView);
    }

    public ShipWorkOrderExpandableAdapter setOnQueryInventoryClickListener(OnQueryInventoryClickListener listener) {
        this.onQueryInventoryClickListener = listener;
        return this;
    }

    public void setGroupList(List<ShipWorkOrderBean> list) {
        groupList = list == null ? new ArrayList<ShipWorkOrderBean>() : list;
        lineMap.clear();
        loadedMap.clear();
        notifyDataSetChanged();
    }

    public void setLines(long doId, List<ShipWorkLineBean> lines) {
        lineMap.put(doId, lines == null ? new ArrayList<ShipWorkLineBean>() : lines);
        loadedMap.put(doId, true);
        notifyDataSetChanged();
    }

    public boolean isLineLoaded(long doId) {
        return loadedMap.containsKey(doId) && loadedMap.get(doId);
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ShipWorkOrderBean order = groupList.get(groupPosition);
        List<ShipWorkLineBean> lines = lineMap.get(order.getDO_ID());
        return lines == null ? 0 : lines.size() + 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (childPosition == 0) {
            return null;
        }
        ShipWorkOrderBean order = groupList.get(groupPosition);
        List<ShipWorkLineBean> lines = lineMap.get(order.getDO_ID());
        return lines == null ? null : lines.get(childPosition - 1);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupList.get(groupPosition).getDO_ID();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        return childPosition == 0 ? CHILD_TYPE_HEADER : CHILD_TYPE_LINE;
    }

    @Override
    public int getChildTypeCount() {
        return 2;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.item_ship_work_order, null);
            holder = new GroupViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }
        ShipWorkOrderBean bean = groupList.get(groupPosition);
        holder.bind(bean, isExpanded);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (childPosition == 0) {
            if (convertView == null) {
                convertView = View.inflate(parent.getContext(), R.layout.item_ship_work_detail_header, null);
            }
            return convertView;
        }
        ChildViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.item_ship_work_line, null);
            holder = new ChildViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        ShipWorkLineBean bean = (ShipWorkLineBean) getChild(groupPosition, childPosition);
        holder.bind(bean, onQueryInventoryClickListener);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return childPosition != 0;
    }

    private static class GroupViewHolder {
        private TextView customerTextView;
        private TextView trackTextView;
        private TextView doTextView;
        private ImageView indicatorImageView;

        GroupViewHolder(View view) {
            customerTextView = view.findViewById(R.id.item_ship_work_order_customer_textView);
            trackTextView = view.findViewById(R.id.item_ship_work_order_track_textView);
            doTextView = view.findViewById(R.id.item_ship_work_order_do_textView);
            indicatorImageView = view.findViewById(R.id.item_ship_work_order_indicator);
        }

        void bind(ShipWorkOrderBean bean, boolean isExpanded) {
            customerTextView.setText(bean.getCustomer_Name());
            trackTextView.setText("跟踪号 " + bean.getTrackNo());
            doTextView.setText("DO_ID " + bean.getDO_ID());
            indicatorImageView.setImageResource(isExpanded
                    ? R.drawable.expander_ic_mini_expanded
                    : R.drawable.expander_ic_mini_collapsed);
        }
    }

    private static class ChildViewHolder {
        private TextView productTextView;
        private TextView qtyTextView;
        private Button queryButton;
        private TextView statusTextView;

        ChildViewHolder(View view) {
            productTextView = view.findViewById(R.id.item_ship_work_line_product_textView);
            qtyTextView = view.findViewById(R.id.item_ship_work_line_qty_textView);
            queryButton = view.findViewById(R.id.item_ship_work_line_query_button);
            statusTextView = view.findViewById(R.id.item_ship_work_line_status_textView);
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
