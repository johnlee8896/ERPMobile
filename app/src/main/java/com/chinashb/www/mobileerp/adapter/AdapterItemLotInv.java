package com.chinashb.www.mobileerp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.Item_Lot_Inv;
import com.chinashb.www.mobileerp.funs.OnItemClickListener;
import com.chinashb.www.mobileerp.funs.CommonUtil;

import java.util.List;

/**
 * Created by Caleb on 2018/10/19.
 */


public class AdapterItemLotInv extends RecyclerView.Adapter<AdapterItemLotInv.ProInvViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<Item_Lot_Inv>  dataSoure;
    private OnItemClickListener mClickListener;

    public AdapterItemLotInv(Context context, List<Item_Lot_Inv> Part_InvList) {
        dataSoure = Part_InvList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);



    }

    public List<Item_Lot_Inv> getDataList(){
        return  dataSoure;
    }
    @Override
    public ProInvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater
                .inflate(R.layout.listview_part_item_inv, parent, false);
        ProInvViewHolder vh = new ProInvViewHolder(v,mClickListener);
        return vh;

    }

    @Override
    public void onBindViewHolder(final ProInvViewHolder holder, int position) {
        final Item_Lot_Inv lot = dataSoure.get(position);

        holder.tvVer.setText(lot.getItem_Version());
        holder.tvIstName.setText(lot.getIstName());
        holder.tvLotDate.setText(CommonUtil.DateYMD(lot.getLotDate()));
        holder.tvLotNo.setText(lot.getLotNo());
        holder.tvLotID.setText(String.valueOf(lot.getLotID()));
        holder.tvManuLotNo.setText(lot.getManuLotNo());
        holder.tvStauts.setText(lot.getLotStatus());
        holder.tvLotTag.setText(lot.getLotDescription());

        holder.tvInvQty.setText(CommonUtil.DecimalFormat(lot.getInvQty()));
        if (lot.getLotStatus().equals("正常使用"))
        {
            holder.tvInvQty.setBackgroundResource(R.drawable.textviewbluebackground);
            holder.tvInvQty.setTextColor(Color.WHITE);
        }


    }

    @Override
    public int getItemCount() {
        return dataSoure == null ? 0 : dataSoure.size();
    }

    public void setOnItemClickListener(OnItemClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public static class ProInvViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener
    {

        TextView tvVer;
        TextView tvIstName;
        TextView tvLotDate;
        TextView tvLotNo;
        TextView tvLotID;
        TextView tvManuLotNo;
        TextView tvStauts;
        TextView tvInvQty;
        TextView tvLotTag;

        ImageView ivLogTag;

        private  OnItemClickListener mListener;

       public ProInvViewHolder(View view, OnItemClickListener listener) {
            super(view);

           bindview(view);

            mListener=listener;
            ivLogTag.setOnClickListener((View.OnClickListener) this);


        }

        protected  void bindview(View view)
        {
            tvVer = (TextView)view.findViewById(R.id.tv_part_inv_item_version);
            tvIstName = (TextView)view.findViewById(R.id.tv_part_inv_item_ist);
            tvLotDate = (TextView)view.findViewById(R.id.tv_part_inv_item_indate);
            tvLotNo = (TextView)view.findViewById(R.id.tv_part_inv_item_lotno);
            tvLotID = (TextView)view.findViewById(R.id.tv_part_inv_item_lotid);
            tvManuLotNo = (TextView)view.findViewById(R.id.tv_part_inv_item_manulot);
            tvStauts = (TextView)view.findViewById(R.id.tv_part_inv_item_status);
            tvInvQty = (TextView)view.findViewById(R.id.tv_part_inv_item_qty);
            tvLotTag=(TextView)view.findViewById(R.id.tv_part_inv_item_lot_tag);
            ivLogTag=(ImageView)view.findViewById(R.id.iv_inv_part_item_lot_tag_edit);
        }

        ProInvViewHolder(View view) {
            super(view);
            bindview(view);
        }

        @Override
        public void onClick(View v) {
            Integer p = getPosition();
            if (mListener != null)
            {mListener.OnItemClick(v,p);}

        }
    }
}