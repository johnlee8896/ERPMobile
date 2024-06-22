package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.GoodsPurchaseOrderBean;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.utils.UnitFormatUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2024/4/15 6:04 PM
 * @author 作者: liweifeng
 * @description
 */
public class GoodsOrderAdapter extends BaseRecycleAdapter<GoodsPurchaseOrderBean, GoodsOrderAdapter.GoodsOrderItemViewHolder> {

    @NonNull
    @Override
    public GoodsOrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GoodsOrderItemViewHolder(parent);
    }

    public static class GoodsOrderItemViewHolder extends BaseViewHolder {


        @BindView(R.id.order_first_name_textView) TextView firstNameTextView;
        @BindView(R.id.order_second_name_textView) TextView secondNameTextView;
        @BindView(R.id.order_third_name_textView) TextView thirdNameTextView;
        @BindView(R.id.order_fourth_name_textView) TextView fourthNameTextView;
        @BindView(R.id.order_fifth_name_textView) TextView fifthNameTextView;
        //        @BindView(R.id.order_sixth_name_textView) TextView sixthNameTextView;
        @BindView(R.id.order_sixth_name_textView) EditText sixthNameTextView;
        @BindView(R.id.order_select_checkBox) CheckBox checkBox;

        public GoodsOrderItemViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_goods_order_list_layout);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public <T> void initUIData(T t) {
            GoodsPurchaseOrderBean entity = (GoodsPurchaseOrderBean) t;
            if (entity != null) {

                firstNameTextView.setText(entity.getPoiID() + "");
                secondNameTextView.setText(entity.getPONO());
//                thirdNameTextView.setText(entity.getPoiDueDate());
                String originalDateString = entity.getPoiDueDate();
//                Date(1686844800000+0800)
                if (!TextUtils.isEmpty(originalDateString)) {
                    String dateMillSecondString = originalDateString.substring(6, 19);
                    thirdNameTextView.setText(UnitFormatUtil.formatTimeToDayChinese(Long.parseLong(dateMillSecondString)));
                }
                fourthNameTextView.setText(entity.getPoiQuantity() + "");
                fifthNameTextView.setText(entity.getUnReachQuantity() + "");
                sixthNameTextView.setText(entity.getLeftQuantity() + "");

                sixthNameTextView.addTextChangedListener(new TextWatcherImpl() {
                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (TextUtils.isDigitsOnly(editable)) {
                            if (!(editable.toString().equals("0.") || editable.toString().endsWith("."))) {

                                try{
                                    entity.setLeftQuantity(Float.parseFloat(editable.toString()));
                                }catch (Exception e){
//                                    ToastUtil.showToastShort("类型转换错误");
                                    //// TODO: 2024/4/17
                                }
                            }


                        } else {
                            ToastUtil.showToastShort("只能输入数字！");
                        }
                    }
                });

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        checkBox.setVisibility(View.VISIBLE);
                        return true;
                    }
                });

            }
        }
    }
}

