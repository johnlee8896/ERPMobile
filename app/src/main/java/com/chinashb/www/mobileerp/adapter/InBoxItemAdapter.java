package com.chinashb.www.mobileerp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Caleb on 2018/9/21.
 */


public class InBoxItemAdapter extends RecyclerView.Adapter<InBoxItemAdapter.BoxItemViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<BoxItemEntity> dataSoure;

    public InBoxItemAdapter(Context context, List<BoxItemEntity> Box_ItemList) {
        dataSoure = Box_ItemList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public List<BoxItemEntity> getDataList() {
        return dataSoure;
    }

    @Override
    public BoxItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater
                .inflate(R.layout.listview_box_items, parent, false);
        BoxItemViewHolder vh = new BoxItemViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(final BoxItemViewHolder holder, int position) {
        final BoxItemEntity Box_Item = dataSoure.get(position);
        holder.tvItem.setText(Box_Item.getItemName());
        DecimalFormat DF = new DecimalFormat("####.####");
        holder.tvQty.setText(DF.format(Box_Item.getQty()));
        holder.tvBu.setText(Box_Item.getBuName());
        holder.tvIst.setText(Box_Item.getIstName());
        holder.cbSelect.setChecked(Box_Item.getSelect());

        String LotWithBox;
        LotWithBox = Box_Item.getLotNo() + "@" + Box_Item.getBoxName() + Box_Item.getBoxNo();
        holder.tvLotNo.setText(LotWithBox);

        //holder.tvIst.setText(BoxItemEntity.get());
        holder.cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Box_Item.setSelect(compoundButton.isChecked());

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSoure == null ? 0 : dataSoure.size();
    }

    public static class BoxItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvItem;
        TextView tvQty;
        TextView tvBu;
        TextView tvLotNo;

        TextView tvIst;
        CheckBox cbSelect;

        BoxItemViewHolder(View view) {
            super(view);

            tvItem = (TextView) view.findViewById(R.id.tv_item_name);
            tvQty = (TextView) view.findViewById(R.id.tv_qty);
            tvBu = (TextView) view.findViewById(R.id.tv_bu_name);
            tvLotNo = (TextView) view.findViewById(R.id.tv_inv_in_lotno);
            tvIst = (TextView) view.findViewById(R.id.tv_ist_name);
            cbSelect = (CheckBox) view.findViewById(R.id.cb_select_ist);


        }
    }
}