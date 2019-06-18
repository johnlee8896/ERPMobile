package com.chinashb.www.mobileerp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.Issued_Item;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Caleb on 2018/9/21.
 */


public class AdapterIssuedItem extends RecyclerView.Adapter<AdapterIssuedItem.IssuedItemViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<Issued_Item>  dataSoure;

    public AdapterIssuedItem(Context context, List<Issued_Item> Issued_ItemList) {
        dataSoure = Issued_ItemList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public List<Issued_Item> getDataList(){
        return  dataSoure;
    }
    @Override
    public IssuedItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater
                .inflate(R.layout.listview_issued_items, parent, false);
        IssuedItemViewHolder vh = new IssuedItemViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(final IssuedItemViewHolder holder, int position) {
        final Issued_Item Issued_Item = dataSoure.get(position);
        holder.tvItem.setText(Issued_Item.getItemName());
        holder.tvNextLocation.setText(Issued_Item.getNextLocation());
        //holder.tvNextLotNo.setText(Issued_Item.getNextLotNo());

        DecimalFormat df4=new DecimalFormat("#####.####");


        holder.tvSingleQty.setText(df4.format(Issued_Item.getSingleQty()));
        holder.tvNeedQty.setText(df4.format(Issued_Item.getNeedQty()));
        holder.tvIssuedQty.setText(df4.format(Issued_Item.getIssuedQty()));
        holder.tvMoreQty.setText(df4.format(Issued_Item.getMoreQty()));

        holder.tvIssuedLastMoment.setText(Issued_Item.getLastIssueMoment());

    }

    @Override
    public int getItemCount() {
        return dataSoure == null ? 0 : dataSoure.size();
    }

    public static class IssuedItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvItem;
        TextView tvNextLocation;
        //TextView tvNextLotNo;
        TextView tvSingleQty;
        TextView tvNeedQty;
        TextView tvIssuedQty;
        TextView tvIssuedLastMoment;
        TextView tvMoreQty;


        IssuedItemViewHolder(View view) {
            super(view);

            tvItem = (TextView)view.findViewById(R.id.tv_issued_item_name);
            tvNextLocation=(TextView)view.findViewById(R.id.tv_issued_nextlocation);
            //tvNextLotNo=(TextView)view.findViewById(R.id.tv_issued_nextlotno);
            tvSingleQty=(TextView)view.findViewById(R.id.tv_singleqty);
            tvNeedQty=(TextView)view.findViewById(R.id.tv_needqty);
            tvIssuedQty=(TextView)view.findViewById(R.id.tv_issuedqty);
            tvIssuedLastMoment=(TextView)view.findViewById(R.id.tv_issued_last_moment);
            tvMoreQty=(TextView)view.findViewById(R.id.tv_moreqty);


        }
    }
}