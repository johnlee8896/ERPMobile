package com.chinashb.www.mobileerp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.DepartmentBean;
import com.chinashb.www.mobileerp.bean.PickGoodsBean;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.warehouse.StockPartMoveActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 7/25/24 4:50 PM
 * @author 作者: liweifeng
 * @description 6个item的列表common
 */
public class CommonItemCommonSixAdapter<TY> extends BaseRecycleAdapter<TY, CommonItemCommonSixAdapter.CommonItemViewHolder> {

    @NonNull
    @Override
    public CommonItemCommonSixAdapter.CommonItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommonItemCommonSixAdapter.CommonItemViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(CommonItemCommonSixAdapter.CommonItemViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里 拣货的话，点击进入移库页面
//                Intent intent = new Intent();
//                intent.putExtra("SelectItem", (Parcelable) dataList.get(position));
//                Activity activity = (Activity) holder.itemView.getContext();
//                activity.setResult(1, intent);
//                activity.finish();
                Activity activity = (Activity) holder.itemView.getContext();
                Intent intent = new Intent(activity, StockPartMoveActivity.class);
                intent.putExtra(IntentConstant.Intent_Extra_Send_Goods_Move_from,true);
                activity.startActivityForResult(intent, IntentConstant.Intent_Request_Code_Pick_Goods_To_Stock_Move_Activity);
            }
        });
    }

    public static class CommonItemViewHolder extends BaseViewHolder {

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

        public CommonItemViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_common_list_six_item);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public <T> void initUIData(T t) {
            if (t instanceof PickGoodsBean) {
                PickGoodsBean pickGoodsBean = (PickGoodsBean) t;
                if (pickGoodsBean != null) {
                    firstInfoTextView.setText("大区:");
                    secondInfoTextView.setText("存储单元:");
                    thirdInfoTextView.setText("批次号:");
                    fourthInfoTextView.setText("Item_ID");
                    fifthInfoTextView.setText("规格型号");
                    sixthInfoTextView.setText("物料名称");
                    seventhInfoTextView.setText("数量");
                    firstNameTextView.setText(pickGoodsBean.getBigArea());
                    secondNameTextView.setText(pickGoodsBean.getAreaUnit());
                    thirdNameTextView.setText(pickGoodsBean.getLotNo());
                    fourthNameTextView.setText(pickGoodsBean.getItemID() + "");
                    fifthNameTextView.setText(pickGoodsBean.getSpec());
                    sixthNameTextView.setText(pickGoodsBean.getItemName());
                    seventhNameTextView.setText(pickGoodsBean.getQty() + "");
                }
            } else if (t instanceof DepartmentBean) {
                DepartmentBean bean = (DepartmentBean) t;
                if (bean != null) {
//                    departmentIdTextView.setText(bean.getDepartmentID() + "");
//                    departmentHigherNameTextView.setText(bean.getPDN());
//                    departmentNameTextView.setText(bean.getDepartmentName());
                    firstInfoTextView.setText("部门ID:");
                    secondInfoTextView.setText("上级部门:");
                    thirdInfoTextView.setText("部门名称:");
                    firstNameTextView.setText(bean.getDepartmentID() + "");
                    secondNameTextView.setText(bean.getPDN());
                    thirdNameTextView.setText(bean.getDepartmentName());
                }
            }
        }
    }
}

