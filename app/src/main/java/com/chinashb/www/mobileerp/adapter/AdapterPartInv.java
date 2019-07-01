package com.chinashb.www.mobileerp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.PartsEntity;
import com.chinashb.www.mobileerp.funs.OnItemClickListener;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Caleb on 2018/9/21.
 */


public class AdapterPartInv extends RecyclerView.Adapter<AdapterPartInv.ProInvViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<PartsEntity> dataSoure;
    private OnItemClickListener mClickListener;

    public AdapterPartInv(Context context, List<PartsEntity> Part_InvList) {
        dataSoure = Part_InvList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public List<PartsEntity> getDataList() {
        return dataSoure;
    }

    @Override
    public ProInvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater
                .inflate(R.layout.listview_part_inv, parent, false);
        ProInvViewHolder vh = new ProInvViewHolder(v, mClickListener);
        return vh;

    }

    @Override
    public void onBindViewHolder(final ProInvViewHolder holder, int position) {
        final PartsEntity Part_Inv = dataSoure.get(position);
        holder.tvItemID.setText(String.valueOf(Part_Inv.getItem_ID()));
        holder.tvItem.setText(String.valueOf(Part_Inv.getItem()));
        holder.tvName.setText(Part_Inv.getItem_Name());
        holder.tvSpec.setText(Part_Inv.getItem_Spec2());
        DecimalFormat df = new DecimalFormat("####.####");
        holder.tvQty.setText(df.format(Part_Inv.getInv()));
        holder.tvUnit.setText(Part_Inv.getItem_Unit());

    }

    @Override
    public int getItemCount() {
        return dataSoure == null ? 0 : dataSoure.size();
    }

    public void setOnItemClickListener(OnItemClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public static class ProInvViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvItemID;
        TextView tvItem;

        TextView tvName;
        TextView tvSpec;
        TextView tvQty;
        TextView tvUnit;

        private OnItemClickListener mListener;

        public ProInvViewHolder(View view, OnItemClickListener listener) {
            super(view);

            mListener = listener;
            view.setOnClickListener((View.OnClickListener) this);

            tvItemID = (TextView) view.findViewById(R.id.tv_part_inv_item_ID);
            tvItem = (TextView) view.findViewById(R.id.tv_part_inv_item);
            tvName = (TextView) view.findViewById(R.id.tv_part_inv_name);
            tvSpec = (TextView) view.findViewById(R.id.tv_part_inv_spec);
            tvQty = (TextView) view.findViewById(R.id.tv_part_inv_qty);
            tvUnit = (TextView) view.findViewById(R.id.tv_part_inv_unit);

            tvQty.setTextColor(Color.GREEN);
            tvQty.setBackgroundColor(Color.BLACK);


        }

        ProInvViewHolder(View view) {
            super(view);

            tvItemID = (TextView) view.findViewById(R.id.tv_part_inv_item_ID);
            tvItem = (TextView) view.findViewById(R.id.tv_part_inv_item);
            tvName = (TextView) view.findViewById(R.id.tv_part_inv_name);
            tvSpec = (TextView) view.findViewById(R.id.tv_part_inv_spec);
            tvQty = (TextView) view.findViewById(R.id.tv_part_inv_qty);
            tvUnit = (TextView) view.findViewById(R.id.tv_part_inv_unit);


        }

        @Override
        public void onClick(View v) {
            Integer p = getPosition();
            if (mListener != null) {
                mListener.OnItemClick(v, p);
            }

        }
    }
}