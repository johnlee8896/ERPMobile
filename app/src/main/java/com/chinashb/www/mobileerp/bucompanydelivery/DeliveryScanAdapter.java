package com.chinashb.www.mobileerp.bucompanydelivery;

/***
 * @date 创建时间 4/25/26 3:11 PM
 * @author 作者: liweifeng
 * @description
 */

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

public class DeliveryScanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private Context context;
    private List<Object> displayList = new ArrayList<>();
    public DeliveryScanAdapter(
            Context context,
            List<DeliveryScanEntity> list
    ) {
        this.context = context;
        buildDisplayList(list);
    }

    private void buildDisplayList(List<DeliveryScanEntity> list) {
        displayList.clear();

        Map<String, List<DeliveryScanEntity>> map = new LinkedHashMap<>();

        for (DeliveryScanEntity e : list) {
            List<DeliveryScanEntity> groupList = map.get(e.getGroupTitle());
            if (groupList == null) {
                groupList = new ArrayList<>();
                map.put(e.getGroupTitle(), groupList);
            }
            groupList.add(e);
        }

        for (Map.Entry<String, List<DeliveryScanEntity>> entry : map.entrySet()) {
            displayList.add(entry.getKey());        // Header
            displayList.addAll(entry.getValue());   // Items
        }
    }

    public void refresh(List<DeliveryScanEntity> list) {
        buildDisplayList(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return displayList.get(position) instanceof String
                ? TYPE_HEADER
                : TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(context)
                    .inflate(R.layout.item_section_header, parent, false);
            return new HeaderHolder(v);
        } else {
            View v = LayoutInflater.from(context)
                    .inflate(R.layout.item_scan_code, parent, false);
            return new ItemHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).headerText.setText(
                    (String) displayList.get(position)
            );
        } else {
            DeliveryScanEntity entity =
                    (DeliveryScanEntity) displayList.get(position);

            ((ItemHolder) holder).codeText.setText(entity.getCode());
            if (TextUtils.isEmpty(entity.getDetail())) {
                ((ItemHolder) holder).detailText.setVisibility(View.GONE);
            } else {
                ((ItemHolder) holder).detailText.setVisibility(View.VISIBLE);
                ((ItemHolder) holder).detailText.setText(entity.getDetail());
            }
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
