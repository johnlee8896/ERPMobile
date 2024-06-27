package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2024/6/26 3:47 PMc
 * @author 作者: liweifeng
 * @description 手工标签的成品操作管理，移库，托盘，非托盘，这里没有入库，因已在库中
 */
public class ProductManuCodeBoxManagementActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.product_main_manu_code_box_code_check_pallet_textView) TextView checkPalletTextView;
    @BindView(R.id.product_main_manu_code_box_code_check_Not_Pallet_textView) TextView checkNotPalletTextView;
    @BindView(R.id.product_main_manu_code_box_move_pallet_textView) TextView movePalletTextView;
    @BindView(R.id.product_main_manu_code_box_move_not_pallet_textView) TextView moveNotPalletTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_manu_code_box_layout);
        ButterKnife.bind(this);
        setViewsListener();
    }

    private void setViewsListener() {
        checkPalletTextView.setOnClickListener(this);
        checkNotPalletTextView.setOnClickListener(this);
        movePalletTextView.setOnClickListener(this);
        moveNotPalletTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == checkPalletTextView){
            Intent intent = new Intent(this, ProductCheckInventoryManuPalletActivity.class);
            startActivity(intent);
        }else if (v == checkNotPalletTextView){
            Intent intent = new Intent(this, ProductCheckInventoryManuPalletNotActivity.class);
            startActivity(intent);
        }else if (v == movePalletTextView){
            Intent intent = new Intent(this, MoveManuProductPalletActivity.class);
            startActivity(intent);
        }else if (v == moveNotPalletTextView){
            Intent intent = new Intent(this, MoveManuProductPalletNotActivity.class);
            startActivity(intent);
        }
    }
}
