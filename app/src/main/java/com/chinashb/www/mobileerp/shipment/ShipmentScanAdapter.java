package com.chinashb.www.mobileerp.shipment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2026/5/2 14:12
 * @author 作者: code-x John
 * @description 外贸发运扫码展示适配器
 */
public class ShipmentScanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private final Context context;
    private final List<Object> displayList = new ArrayList<>();

    public ShipmentScanAdapter(Context context, List<ShipmentDisplayEntity> list) {
        this.context = context;
        buildDisplayList(list);
    }

    private void buildDisplayList(List<ShipmentDisplayEntity> list) {
        displayList.clear();

        Map<String, List<ShipmentDisplayEntity>> groupedMap = new LinkedHashMap<>();
        for (ShipmentDisplayEntity entity : list) {
            List<ShipmentDisplayEntity> groupList = groupedMap.get(entity.getGroupTitle());
            if (groupList == null) {
                groupList = new ArrayList<>();
                groupedMap.put(entity.getGroupTitle(), groupList);
            }
            groupList.add(entity);
        }

        for (Map.Entry<String, List<ShipmentDisplayEntity>> entry : groupedMap.entrySet()) {
            displayList.add(entry.getKey());
            displayList.addAll(entry.getValue());
        }
    }

    public void refresh(List<ShipmentDisplayEntity> list) {
        buildDisplayList(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return displayList.get(position) instanceof String ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_section_header, parent, false);
            return new HeaderHolder(view);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_scan_code, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).headerText.setText((String) displayList.get(position));
            return;
        }

        ShipmentDisplayEntity entity = (ShipmentDisplayEntity) displayList.get(position);
        ((ItemHolder) holder).codeText.setText(entity.getCode());
        if (TextUtils.isEmpty(entity.getDetail())) {
            ((ItemHolder) holder).detailText.setVisibility(View.GONE);
        } else {
            ((ItemHolder) holder).detailText.setVisibility(View.VISIBLE);
            ((ItemHolder) holder).detailText.setText(entity.getDetail());
        }
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_header)
        TextView headerText;

        HeaderHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_code)
        TextView codeText;
        @BindView(R.id.tv_detail)
        TextView detailText;

        ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
