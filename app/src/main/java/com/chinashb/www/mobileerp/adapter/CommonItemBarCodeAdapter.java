package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2020/3/18 9:56
 * @author 作者: xxblwf
 * @description common 二维码扫物料后的解析adapter
 */

public class CommonItemBarCodeAdapter extends BaseRecycleAdapter<BoxItemEntity, CommonItemBarCodeAdapter.ItemBarCodeViewHolder> {
    private boolean isISTVisible ;

    public void setISTVisible(boolean ISTVisible) {
        isISTVisible = ISTVisible;
    }

    @NonNull @Override public ItemBarCodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemBarCodeViewHolder(parent);
    }



    public static class ItemBarCodeViewHolder extends BaseViewHolder {

        @BindView(R.id.item_common_in_tv_item_name_col) TextView itemNameTextView;
        @BindView(R.id.item_common_in_tv_bu_name_col) TextView buTextView;
        @BindView(R.id.item_common_in_tv_inv_in_lotno) TextView lotTextView;
        @BindView(R.id.item_common_in_tv_qty_col) EditText qtyEditText;
        @BindView(R.id.item_common_in_tv_ist_name_col) TextView istTextView;
        @BindView(R.id.item_common_cb_select_ist) CheckBox checkBox;

        public ItemBarCodeViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_scan_bar_code_layout);
            ButterKnife.bind(this,itemView);
        }

        @Override public <T> void initUIData(T t) {
            BoxItemEntity entity = (BoxItemEntity) t;

            itemNameTextView.setText(entity.getItemName());
            DecimalFormat DF = new DecimalFormat("####.####");
            qtyEditText.setText(DF.format(entity.getQty()));
            buTextView.setText(entity.getBuName());
            istTextView.setText(entity.getIstName());
            checkBox.setChecked(entity.getSelect());

            String LotWithBox;
            LotWithBox = entity.getLotNo() + "@" + entity.getBoxName() + entity.getBoxNo();
            lotTextView.setText(LotWithBox);

            //istTextView.setText(BoxItemEntity.get());
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    entity.setSelect(compoundButton.isChecked());

                }
            });

            qtyEditText.addTextChangedListener(new TextWatcherImpl(){
                @Override public void afterTextChanged(Editable editable) {
                    super.afterTextChanged(editable);
                        String q = editable.toString();
                        if (!q.isEmpty()) {
                            if (Float.parseFloat(q) > 0) {
                                entity.setQty(Float.parseFloat(q));
                            } else {
                                entity.setQty(0);
                            }
                        } else {
                            entity.setQty(0);
                        }
                }
            });



        }
    }
}
