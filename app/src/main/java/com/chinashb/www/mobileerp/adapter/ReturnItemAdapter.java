package com.chinashb.www.mobileerp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Caleb on 2018/9/21.
 */


public class ReturnItemAdapter extends RecyclerView.Adapter<ReturnItemAdapter.IssueMoreItemViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<BoxItemEntity>  dataSoure;

    public ReturnItemAdapter(Context context, List<BoxItemEntity> Box_ItemList) {
        dataSoure = Box_ItemList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public List<BoxItemEntity> getDataList(){
        return  dataSoure;
    }
    @Override
    public IssueMoreItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater
                .inflate(R.layout.listview_return_items, parent, false);
        IssueMoreItemViewHolder vh = new IssueMoreItemViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(final IssueMoreItemViewHolder holder, int position) {
        final BoxItemEntity Box_Item = dataSoure.get(position);
        holder.tvItem.setText(Box_Item.getItemName());


        holder.tvLotNo.setText(Box_Item.getLotBox());
        DecimalFormat dfs= new DecimalFormat("####.####");
        holder.tvQty.setText(dfs.format(Box_Item.getQty()));

        if(position % 2==0)
        {
            holder.tvItem.setBackgroundColor(Color.WHITE);
            holder.tvLotNo.setBackgroundColor(Color.WHITE);
            holder.tvQty.setBackgroundColor(Color.WHITE);

        }
        else
        {
            int color= Color.argb(120,0,200,200);
            holder.tvItem.setBackgroundColor(color);
            holder.tvLotNo.setBackgroundColor(color);
            holder.tvQty.setBackgroundColor(color);
        }

    }

   @Override
    public int getItemCount() {
        return dataSoure == null ? 0 : dataSoure.size();
    }





    public static class IssueMoreItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvItem;
        TextView tvLotNo;
        TextView tvQty;



        IssueMoreItemViewHolder(View view) {
            super(view);

            tvItem = (TextView)view.findViewById(R.id.tv_issue_more_item_name);
            tvLotNo = (TextView)view.findViewById(R.id.tv_issue_more_item_lotno);
            tvQty = (TextView)view.findViewById(R.id.tv_return_item_qty);


        }
    }
}