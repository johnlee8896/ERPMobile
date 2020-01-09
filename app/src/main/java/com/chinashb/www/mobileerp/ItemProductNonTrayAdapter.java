package com.chinashb.www.mobileerp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.chinashb.www.mobileerp.bean.entity.WCSubProductItemEntity;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;

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
//        View v = mLayoutInflater.inflate(R.layout.listview_box_items, parent, false);
        View v = mLayoutInflater.inflate(R.layout.item_product_scan_item, parent, false);
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

        holder.tvQty.addTextChangedListener(new TextWatcherImpl(){
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                try {
                    String q = editable.toString();
                    if (!q.isEmpty()) {
//                        if (Float.parseFloat(q) > 0) {
                        if (Integer.parseInt(q) > 0) {
//                            wcSubProductItemEntity.setQty(Float.parseFloat(q));
                            wcSubProductItemEntity.setQty(Integer.parseInt(q) );
                        } else {
                            wcSubProductItemEntity.setQty(0);
                        }
                    } else {
                        wcSubProductItemEntity.setQty(0);
                    }
                } finally {

                }
            }
        });

//        holder.tvIst.setText(WCSubProductItemEntity.get());
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
//        TextView tvQty;
        EditText tvQty;
        TextView tvBu;
        TextView tvLotNo;

        TextView tvIst;
        CheckBox cbSelect;

        BoxItemViewHolder(View view) {
            super(view);

            tvItem = (TextView) view.findViewById(R.id.item_product_item_name_textView);
            tvQty = (EditText) view.findViewById(R.id.item_product_item_qty_editText);
            tvBu = (TextView) view.findViewById(R.id.item_product_item_bu_textView);
            tvLotNo = (TextView) view.findViewById(R.id.item_product_item_lot_textView);
            tvIst = (TextView) view.findViewById(R.id.tv_ist_name);
            cbSelect = (CheckBox) view.findViewById(R.id.cb_select_ist);




        }
    }
}
