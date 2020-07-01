package com.chinashb.www.mobileerp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.PlanInnerDetailEntity;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Caleb on 2018/9/21.
 */


public class IssuedItemAdapter extends RecyclerView.Adapter<IssuedItemAdapter.IssuedItemViewHolder> {
    private final LayoutInflater inflater;
    private final Context mContext;
    private List<PlanInnerDetailEntity> planInnerDetailEntityList;

    public IssuedItemAdapter(Context context, List<PlanInnerDetailEntity> Issued_ItemList) {
        planInnerDetailEntityList = Issued_ItemList;
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    public List<PlanInnerDetailEntity> getDataList(){
        return planInnerDetailEntityList;
    }
    @Override
    public IssuedItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_plan_inner_detail_layout, parent, false);
        IssuedItemViewHolder viewHolder = new IssuedItemViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final IssuedItemViewHolder holder, int position) {
        final PlanInnerDetailEntity Issued_Item = planInnerDetailEntityList.get(position);
        holder.tvItem.setText(Issued_Item.getItemName());
        holder.tvNextLocation.setText(Issued_Item.getNextLocation());
        //holder.tvNextLotNo.setText(PlanInnerDetailEntity.getNextLotNo());

        DecimalFormat df4=new DecimalFormat("#####.####");


        holder.tvSingleQty.setText(df4.format(Issued_Item.getSingleQty()));
        holder.tvNeedQty.setText(df4.format(Issued_Item.getNeedQty()));
        holder.tvIssuedQty.setText(df4.format(Issued_Item.getIssuedQty()));
        holder.tvMoreQty.setText(df4.format(Issued_Item.getMoreQty()));

        holder.tvIssuedLastMoment.setText(Issued_Item.getLastIssueMoment());

        holder.itemIDTextView.setText(Issued_Item.getItem_ID() + "");

    }

    @Override
    public int getItemCount() {
        return planInnerDetailEntityList == null ? 0 : planInnerDetailEntityList.size();
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
        TextView itemIDTextView;


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

            itemIDTextView = view.findViewById(R.id.tv_item_id_name_textView);


        }
    }
}