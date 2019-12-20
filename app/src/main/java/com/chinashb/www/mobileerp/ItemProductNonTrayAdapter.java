package com.chinashb.www.mobileerp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.chinashb.www.mobileerp.bean.entity.WCSubProductItemEntity;
import java.text.DecimalFormat;
import java.util.List;

/***
 * @date 创建时间 2019/12/20 15:52
 * @author 作者: xxblwf
 * @description 成品非托盘物料二维码adapter
 */

public class ItemProductNonTrayAdapter extends RecyclerView.Adapter<ItemProductNonTrayAdapter.BoxItemViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<WCSubProductItemEntity> dataSoure;

    public ItemProductNonTrayAdapter(Context context, List<WCSubProductItemEntity> Box_ItemList) {
        dataSoure = Box_ItemList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public List<WCSubProductItemEntity> getDataList() {
        return dataSoure;
    }

    @Override
    public ItemProductNonTrayAdapter.BoxItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater
                .inflate(R.layout.listview_box_items, parent, false);
        ItemProductNonTrayAdapter.BoxItemViewHolder vh = new ItemProductNonTrayAdapter.BoxItemViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(final ItemProductNonTrayAdapter.BoxItemViewHolder holder, int position) {
        final WCSubProductItemEntity wcSubProductItemEntity = dataSoure.get(position);
        holder.tvItem.setText(wcSubProductItemEntity.getWcSubProductEntity().getProductName());
        DecimalFormat DF = new DecimalFormat("####.####");
        holder.tvQty.setText(DF.format(wcSubProductItemEntity.getQty()));
        holder.tvBu.setText(wcSubProductItemEntity.getBuName());
        holder.tvIst.setText(wcSubProductItemEntity.getIstName());
        holder.cbSelect.setChecked(wcSubProductItemEntity.isSelect());

//        String LotWithBox;
        //// TODO: 2019/12/20
//        LotWithBox = wcSubProductItemEntity.getLotNo() + "@" + wcSubProductItemEntity.getBoxName() + wcSubProductItemEntity.getBoxNo();

//        holder.tvLotNo.setText(LotWithBox);
        holder.tvLotNo.setText(wcSubProductItemEntity.getLotNo());

        //holder.tvIst.setText(WCSubProductItemEntity.get());
        holder.cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                wcSubProductItemEntity.setSelect(compoundButton.isChecked());

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSoure == null ? 0 : dataSoure.size();
    }

    public static class BoxItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvItem;
        TextView tvQty;
        TextView tvBu;
        TextView tvLotNo;

        TextView tvIst;
        CheckBox cbSelect;

        BoxItemViewHolder(View view) {
            super(view);

            tvItem = (TextView) view.findViewById(R.id.tv_item_name);
            tvQty = (TextView) view.findViewById(R.id.tv_qty);
            tvBu = (TextView) view.findViewById(R.id.tv_bu_name);
            tvLotNo = (TextView) view.findViewById(R.id.tv_inv_in_lotno);
            tvIst = (TextView) view.findViewById(R.id.tv_ist_name);
            cbSelect = (CheckBox) view.findViewById(R.id.cb_select_ist);


        }
    }
}
