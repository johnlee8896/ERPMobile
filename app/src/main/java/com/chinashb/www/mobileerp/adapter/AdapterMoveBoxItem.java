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
import com.chinashb.www.mobileerp.funs.CommonUtil;

import java.util.List;

/**
 * Created by Caleb on 2018/9/21.
 */


public class AdapterMoveBoxItem extends RecyclerView.Adapter<AdapterMoveBoxItem.BoxItemViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<BoxItemEntity>  dataSoure;

    public AdapterMoveBoxItem(Context context, List<BoxItemEntity> Box_ItemList) {
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
                .inflate(R.layout.listview_box_items_tomove, parent, false);
        BoxItemViewHolder vh = new BoxItemViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(final BoxItemViewHolder holder, int position) {
        final BoxItemEntity Box_Item = dataSoure.get(position);
        holder.tvItem.setText(Box_Item.getItemName());
        holder.tvBox.setText(Box_Item.getBoxNameNo());
        holder.tvQty.setText(CommonUtil.DecimalFormat(Box_Item.getQty()));
        holder.tvOld.setText(Box_Item.getOldIstName());
        holder.tvIst.setText(Box_Item.getIstName());

        holder.cbSelect.setChecked(Box_Item.getSelect());

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
        TextView tvBox;
        TextView tvQty;
        TextView tvOld;
        TextView tvIst;
        CheckBox cbSelect;

        BoxItemViewHolder(View view) {
            super(view);

            tvItem = (TextView)view.findViewById(R.id.tv_tomove_item_name);
            tvBox = (TextView)view.findViewById(R.id.tv_tomove_box_name);
            tvQty = (TextView)view.findViewById(R.id.tv_tomove_qty);
            tvOld= (TextView)view.findViewById(R.id.tv_tomove_old_ist);
            tvIst = (TextView)view.findViewById(R.id.tv_tomove_new_ist);
            cbSelect=(CheckBox)view.findViewById(R.id.cb_tomove_select_ist );

        }
    }
}