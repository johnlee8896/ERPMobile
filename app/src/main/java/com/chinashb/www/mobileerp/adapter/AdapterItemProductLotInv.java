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
import com.chinashb.www.mobileerp.bean.ProductItemBean;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.OnItemClickListener;

import java.util.List;

/***
 * @date 创建时间 2019/6/28 13:16
 * @author 作者: xxblwf
 * @description 成品库item详情
 */

public class AdapterItemProductLotInv extends RecyclerView.Adapter<AdapterItemProductLotInv.ProductInvViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<ProductItemBean> dataSoure;
    private OnItemClickListener mClickListener;

    public AdapterItemProductLotInv(Context context, List<ProductItemBean> Part_InvList) {
        dataSoure = Part_InvList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public List<ProductItemBean> getDataList() {
        return dataSoure;
    }

    @Override
    public ProductInvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater
                .inflate(R.layout.listview_product_item_inv, parent, false);
        ProductInvViewHolder vh = new ProductInvViewHolder(v, mClickListener);
        return vh;

    }

    @Override
    public void onBindViewHolder(final ProductInvViewHolder holder, int position) {
        final ProductItemBean productItemBean = dataSoure.get(position);

//        holder.tvVer.setText(lot.getItem_Version());
//        holder.tvIstName.setText(lot.getIstName());
        //todo
        holder.tvLotDate.setText(CommonUtil.DateYMD(productItemBean.get入库日期()));
//        holder.tvLotNo.setText(productItemBean.getLotID());
        holder.tvLotID.setText(String.valueOf(productItemBean.getLotID()));
        holder.tvManuLotNo.setText(productItemBean.get批次号());
        holder.tvStauts.setText(productItemBean.get状态());
        holder.tvLotTag.setText(productItemBean.get标注());

        holder.tvInvQty.setText(CommonUtil.DecimalFormat(productItemBean.get数量()));
        if (productItemBean!= null && productItemBean.get状态().equals("正常使用")) {
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

    public static class ProductInvViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

//        TextView tvVer;
//        TextView tvIstName;
        TextView tvLotDate;
        TextView tvLotNo;
        TextView tvLotID;
        TextView tvManuLotNo;
        TextView tvStauts;
        TextView tvInvQty;
        TextView tvLotTag;
        ImageView ivLogTag;

        private OnItemClickListener mListener;

        public ProductInvViewHolder(View view, OnItemClickListener listener) {
            super(view);
            bindview(view);
            mListener = listener;
            ivLogTag.setOnClickListener((View.OnClickListener) this);

        }

        protected void bindview(View view) {
//            tvVer = (TextView) view.findViewById(R.id.tv_product_inv_item_version);
//            tvIstName = (TextView) view.findViewById(R.id.tv_product_inv_item_ist);
            tvLotDate = (TextView) view.findViewById(R.id.tv_product_inv_item_indate);
//            tvLotNo = (TextView) view.findViewById(R.id.tv_product_inv_item_lotno);
            tvLotID = (TextView) view.findViewById(R.id.tv_product_inv_item_lotid);
            tvManuLotNo = (TextView) view.findViewById(R.id.tv_product_inv_item_manulot);
            tvStauts = (TextView) view.findViewById(R.id.tv_product_inv_item_status);
            tvInvQty = (TextView) view.findViewById(R.id.tv_product_inv_item_qty);
            tvLotTag = (TextView) view.findViewById(R.id.tv_product_inv_item_lot_tag);
            ivLogTag = (ImageView) view.findViewById(R.id.iv_inv_product_item_lot_tag_edit);
        }

        ProductInvViewHolder(View view) {
            super(view);
            bindview(view);
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
