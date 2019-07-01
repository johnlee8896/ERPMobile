package com.chinashb.www.mobileerp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.ProductsEntity;
import com.chinashb.www.mobileerp.funs.OnItemClickListener;

import java.util.List;

/**
 * Created by Caleb on 2018/9/21.
 */


public class AdapterProInv extends RecyclerView.Adapter<AdapterProInv.ProInvViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<ProductsEntity> dataSoure;
    private OnItemClickListener mClickListener;

    public AdapterProInv(Context context, List<ProductsEntity> Product_InvList) {
        dataSoure = Product_InvList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(OnItemClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public List<ProductsEntity> getDataList() {
        return dataSoure;
    }

    @Override
    public ProInvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater
                .inflate(R.layout.listview_pro_inv, parent, false);
        ProInvViewHolder vh = new ProInvViewHolder(v, mClickListener);
        return vh;

    }

    @Override
    public void onBindViewHolder(final ProInvViewHolder holder, int position) {
        final ProductsEntity Product_Inv = dataSoure.get(position);
        holder.tvItem.setText(String.valueOf(Product_Inv.getItem_ID()));
        holder.tvPS.setText(String.valueOf(Product_Inv.getPS_ID()));
        holder.tvAbb.setText(Product_Inv.get产品通称());
        holder.tvPartNo.setText(Product_Inv.get客户零件编号());
        holder.tvName.setText(Product_Inv.get名称());
        holder.tvVer.setText(Product_Inv.get版本());
        holder.tvQty.setText(String.valueOf(Product_Inv.get库存()));
        //holder.tvIst.setText(ProductsEntity.get存储区域());

    }

    @Override
    public int getItemCount() {
        return dataSoure == null ? 0 : dataSoure.size();
    }

    public static class ProInvViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvItem;
        TextView tvPS;
        TextView tvAbb;
        TextView tvPartNo;
        TextView tvName;
        TextView tvVer;
        TextView tvQty;
        TextView tvIst;
        private OnItemClickListener mListener;

        ProInvViewHolder(View view, OnItemClickListener listener) {
            super(view);
            this.mListener = listener;
            tvItem = (TextView) view.findViewById(R.id.tv_pro_inv_item_ID);
            tvPS = (TextView) view.findViewById(R.id.tv_pro_inv_ps_ID);
            tvAbb = (TextView) view.findViewById(R.id.tv_pro_inv_abb);
            tvPartNo = (TextView) view.findViewById(R.id.tv_pro_inv_partno);
            tvName = (TextView) view.findViewById(R.id.tv_pro_inv_name);
            tvVer = (TextView) view.findViewById(R.id.tv_pro_inv_version);
            //tvIst = (TextView)view.findViewById(R.id.tv_pro_inv_storage);
            tvQty = (TextView) view.findViewById(R.id.tv_pro_inv_qty);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.OnItemClick(view, getPosition());
            }
        }
    }
}