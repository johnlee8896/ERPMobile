package com.chinashb.www.mobileerp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.ProductPackBean;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.OnItemClickListener;
import com.chinashb.www.mobileerp.utils.UnitFormatUtil;

import java.util.List;

/***
 * @date 创建时间 2019/6/28 13:16
 * @author 作者: xxblwf
 * @description 成品库item详情
 */

public class ProductLotInvAdapter extends RecyclerView.Adapter<ProductLotInvAdapter.ProductInvViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
//    private List<ProductItemBean> dataSoure;
    private List<ProductPackBean> dataSoure;
    private OnItemClickListener mClickListener;
    private boolean isUnPack;//是否包装  区别无包装少了 包装序列号、包装日期 另外 包装日期换成了入库日期


    public ProductLotInvAdapter(Context context, List<ProductPackBean> Part_InvList) {
        dataSoure = Part_InvList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public ProductLotInvAdapter setUnPack(boolean unPack) {
        isUnPack = unPack;
        return this;
    }

    public List<ProductPackBean> getDataList() {
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
        final ProductPackBean productItemBean = dataSoure.get(position);

//        holder.tvVer.setText(lot.getItem_Version());
//        holder.tvIstName.setText(lot.getIstName());
        //todo
//        "包装日期":"\/Date(1648396800000+0800)\/",

//        holder.lotTextView.setText(productItemBean.getLotID());
        holder.tvPSID.setText(String.valueOf(productItemBean.getPS_ID()));
        holder.tvLotID .setText(String.valueOf(productItemBean.getLot_ID()));
        holder.tvManuLotNo.setText(productItemBean.get批次());
        holder.tvStauts.setText(productItemBean.get状态());
        holder.tvLotTag.setText(productItemBean.get标注());

        holder.tvInvQty.setText(CommonUtil.DecimalFormat(productItemBean.get产品数量()));
//        if (productItemBean!= null && productItemBean.get状态().equals("正常使用")) {
//            holder.tvInvQty.setBackgroundResource(R.drawable.textviewbluebackground);
//            holder.tvInvQty.setTextColor(Color.WHITE);
//        }

        holder.palletSerialNoTextView.setText(productItemBean.get包装序列号());
        holder.istTextView.setText(productItemBean.get存储区域());

        if (!isUnPack){
            holder.palletSerialNoTextView.setVisibility(View.VISIBLE);
            holder.dateInfoTextView.setText("包装日期");

            String originalDateString = productItemBean.get包装日期();
            String dateMillSecondString = originalDateString.substring(6,19);
            holder.tvLotDate.setText(UnitFormatUtil.formatTimeToDayChinese(Long.parseLong(dateMillSecondString)));
            holder.lotInfoTextView.setVisibility(View.GONE);
            holder.tvLotID.setVisibility(View.GONE);

        }else{
            holder.palletSerialNoTextView.setVisibility(View.GONE);
            holder.serialNOInfoTextView.setVisibility(View.GONE);
            holder.dateInfoTextView.setText("入库日期");

            String originalDateString = productItemBean.get入库日期();
            if (!TextUtils.isEmpty(originalDateString)){
                String dateMillSecondString = originalDateString.substring(6,19);
                holder.tvLotDate.setText(UnitFormatUtil.formatTimeToDayChinese(Long.parseLong(dateMillSecondString)));
            }
            holder.lotInfoTextView.setVisibility(View.VISIBLE);
            holder.tvLotID.setVisibility(View.VISIBLE);

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
        TextView serialNOInfoTextView;
        TextView tvLotNo;
        TextView tvPSID;
        TextView tvLotID;
        TextView tvManuLotNo;
        TextView tvStauts;
        TextView tvInvQty;
        TextView tvLotTag;
        ImageView ivLogTag;
        TextView istTextView;
        TextView palletSerialNoTextView;
        TextView dateInfoTextView;
        TextView lotInfoTextView;

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
//            lotTextView = (TextView) view.findViewById(R.id.tv_product_inv_item_lotno);
            tvLotID = (TextView) view.findViewById(R.id.tv_product_inv_item_lotid);
            tvPSID = (TextView) view.findViewById(R.id.tv_product_inv_item_psid);
            tvManuLotNo = (TextView) view.findViewById(R.id.tv_product_inv_item_manulot);
            tvStauts = (TextView) view.findViewById(R.id.tv_product_inv_item_status);
            tvInvQty = (TextView) view.findViewById(R.id.tv_product_inv_item_qty);
            tvLotTag = (TextView) view.findViewById(R.id.tv_product_inv_item_lot_tag);
            ivLogTag = (ImageView) view.findViewById(R.id.iv_inv_product_item_lot_tag_edit);
            istTextView = (TextView) view.findViewById(R.id.tv_product_inv_item_ist);
            palletSerialNoTextView = (TextView) view.findViewById(R.id.tv_product_pallet_serialno);
            serialNOInfoTextView = view.findViewById(R.id.tv_product_pallet_serialnoinfo);
            dateInfoTextView = view.findViewById(R.id.tv_product_inv_item_indate_title);
            lotInfoTextView = view.findViewById(R.id.tv_product_inv_item_lotid_title);
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
