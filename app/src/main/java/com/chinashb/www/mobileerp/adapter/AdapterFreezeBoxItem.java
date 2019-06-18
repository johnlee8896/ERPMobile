package com.chinashb.www.mobileerp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.funs.CommonUtil;

import java.util.List;

/**
 * Created by Caleb on 2018/9/21.
 */


public class AdapterFreezeBoxItem extends RecyclerView.Adapter<AdapterFreezeBoxItem.BoxItemViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<BoxItemEntity>  dataSoure;

    public AdapterFreezeBoxItem(Context context, List<BoxItemEntity> Box_ItemList) {
        dataSoure = Box_ItemList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public List<BoxItemEntity> getDataList(){
        return  dataSoure;
    }
    @Override
    public BoxItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater
                .inflate(R.layout.listview_box_items_freeze, parent, false);
        BoxItemViewHolder vh = new BoxItemViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(final BoxItemViewHolder holder, int position) {
        final BoxItemEntity Box_Item = dataSoure.get(position);
        holder.tvItem.setText(Box_Item.getItemName());
        holder.tvQty.setText(CommonUtil.DecimalFormat(Box_Item.getQty()));
        holder.tvBox.setText(Box_Item.getBoxNameNo());
        holder.tvLot.setText(Box_Item.getLotNo());
        holder.tvIst.setText(Box_Item.getIstName());

        String fs =Box_Item.getFreezeStatus();

        String htmlstr="<font color='#0000FF'>" + fs + "</font>";

        if (fs.contains("正常"))
        {
            htmlstr="<font color='#0000FF'>" + fs + "</font>";
           //holder.tvStatus.setTextColor(Color.BLUE);
        }
        if (fs.contains("冻结"))
        {
            htmlstr="<font color='#FF0000'>" + fs + "</font>";
        }

        holder.tvStatus.setText(Html.fromHtml(htmlstr));


    }

    @Override
    public int getItemCount() {
        return dataSoure == null ? 0 : dataSoure.size();
    }

    public static class BoxItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvItem;
        TextView tvBox;
        TextView tvLot;
        TextView tvQty;
        TextView tvStatus;
        TextView tvIst;


        BoxItemViewHolder(View view) {
            super(view);

            tvItem = (TextView)view.findViewById(R.id.tv_item_name);
            tvQty = (TextView)view.findViewById(R.id.tv_qty);

            tvLot = (TextView)view.findViewById(R.id.tv_item_lotno);
            tvIst = (TextView)view.findViewById(R.id.tv_item_ist_name);

            tvBox = (TextView)view.findViewById(R.id.tv_item_box_name);
            tvStatus = (TextView)view.findViewById(R.id.tv_freezestatus);




        }
    }
}