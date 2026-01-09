package com.chinashb.www.mobileerp.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.StockInPurchaseOrderBean;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.UnitFormatUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 1/7/26 6:08 PM
 * @author 作者: liweifeng
 * @description
 */
public class StockInPurchaseOrderAdapter extends BaseRecycleAdapter<StockInPurchaseOrderBean, StockInPurchaseOrderAdapter.PurchaseOrderItemViewHolder> {

    private OnViewClickListener onViewClickListener;


    public StockInPurchaseOrderAdapter setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
        return this;
    }

    @NonNull
    @Override
    public StockInPurchaseOrderAdapter.PurchaseOrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StockInPurchaseOrderAdapter.PurchaseOrderItemViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(PurchaseOrderItemViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.itemView.setOnClickListener(v -> {
                holder.itemView.setSelected(!holder.itemView.isSelected());
                if (onViewClickListener != null) {
                    onViewClickListener.onClickAction(v, "", holder.itemView.isSelected() ? dataList.get(position) : null);
                }
        });
    }

    public static class PurchaseOrderItemViewHolder extends BaseViewHolder {

        /**
         * POI_ID : 1338971
         * PO_ID : 226056
         * PO_No : D0901202512270001
         * 下单日期 : /Date(1766764800000+0800)/
         * KisCode : 4.01.026.0001
         * Item_ID : 48327
         * 物料编码 : P-SE00600-8001
         * 物料 : 托盘
         * 规格 : 1115X870X620
         * 版本 : A1
         * 单位 : 个
         * 采购数量 : 50.0
         * 已关联数量 : 0.0
         * 未关联数量 : 50.0
         * 金蝶采购单号 : CGDD226204
         */
        @BindView(R.id.item_common_first_info_textView) TextView firstInfoTextView;
        @BindView(R.id.item_first_name_textView) TextView firstNameTextView;
        @BindView(R.id.item_second_info_textView) TextView secondInfoTextView;
        @BindView(R.id.item_second_name_textView) TextView secondNameTextView;
        @BindView(R.id.item_third_info_textView) TextView thirdInfoTextView;
        @BindView(R.id.item_third_name_textView) TextView thirdNameTextView;
        @BindView(R.id.item_fourth_info_textView) TextView fourthInfoTextView;
        @BindView(R.id.item_fourth_name_textView) TextView fourthNameTextView;
        @BindView(R.id.item_fifth_info_textView) TextView fifthInfoTextView;
        @BindView(R.id.item_fifth_name_textView) TextView fifthNameTextView;
        @BindView(R.id.item_sixth_info_textView) TextView sixthInfoTextView;
        @BindView(R.id.item_sixth_name_textView) TextView sixthNameTextView;
        @BindView(R.id.item_seventh_info_textView) TextView seventhInfoTextView;
        @BindView(R.id.item_seventh_name_textView) TextView seventhNameTextView;

        public PurchaseOrderItemViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_common_list_six_item);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public <T> void initUIData(T t) {
            StockInPurchaseOrderBean entity = (StockInPurchaseOrderBean) t;
            if (entity != null) {

                firstInfoTextView.setText("物料名称：");
                secondInfoTextView.setText("订单编号：");
                thirdInfoTextView.setText("金蝶采购单号：");
                fourthInfoTextView.setText("物料信息：");
                fifthInfoTextView.setText("采购数量：");
                sixthInfoTextView.setText("关联数量信息：");
                seventhInfoTextView.setText("下单日期：");

                firstNameTextView.setText(entity.getItemName()+ "");
                secondNameTextView.setText(entity.getPONo());
                thirdNameTextView.setText(entity.getKisOrderNO());

                fourthNameTextView.setText(String.format("Item_ID:%s KiSCode:%s  版本:%s  单位:%s",
                        entity.getItemID() + "",entity.getKisCode(),entity.getVersion(),entity.getUnit()));
                fifthNameTextView.setText(entity.getPurchaseQty() + "");
                sixthNameTextView.setText(String.format("关联:%s 未关联:%s",
                        entity.getLinkedQty() + "",entity.getUnLinkedQty() + ""));

                fifthNameTextView .setTextSize(14);
                fifthNameTextView.setTextColor(Color.BLACK);
                sixthNameTextView .setTextSize(14);
                sixthNameTextView.setTextColor(Color.BLACK);
                seventhNameTextView .setTextSize(14);
                seventhNameTextView.setTextColor(Color.BLACK);


                String originalDateString = entity.getOrderDate();
                if (!TextUtils.isEmpty(originalDateString)) {
                    String dateMillSecondString = originalDateString.substring(6, 19);
                    seventhNameTextView.setText(UnitFormatUtil.formatTimeToDayChinese(Long.parseLong(dateMillSecondString)));
                }


//                itemView.setOnClickListener(v -> {
////                    if()
//                });



            }
        }
    }
}


