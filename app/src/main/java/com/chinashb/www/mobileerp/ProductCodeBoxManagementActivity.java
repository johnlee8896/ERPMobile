package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2024/6/26 2:24 PM
 * @author 作者: liweifeng
 * @description 正常标签的成品操作管理，入库、移库，托盘，非托盘
 */
public class ProductCodeBoxManagementActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.product_main_code_box_scan_code_box_in_textView) TextView scanInPalletTextView;
    @BindView(R.id.product_main_code_box_scan_code_box_in_Not_Pallet_textView) TextView scanInNotPalletTextView;
    @BindView(R.id.product_main_code_box_sale_out_code_box_textView) TextView saleOutCodeBoxTextView;
    @BindView(R.id.product_main_code_box_move_pallet_textView) TextView movePalletTextView;
    @BindView(R.id.product_main_code_box_move_not_pallet_textView) TextView moveNotPalletTextView;
    @BindView(R.id.product_main_code_box_product_check_inv_textView) TextView checkInvTextView;
    @BindView(R.id.product_main_code_box_product_return_out_textView) TextView returnOutTextView;
    @BindView(R.id.product_main_code_box_product_out_to_fa_textView) TextView outToFATextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_code_box_layout);
        ButterKnife.bind(this);
        setViewsListener();
    }

    private void setViewsListener() {
        scanInPalletTextView.setOnClickListener(this);
        scanInNotPalletTextView.setOnClickListener(this);
        saleOutCodeBoxTextView.setOnClickListener(this);
        movePalletTextView.setOnClickListener(this);
        moveNotPalletTextView.setOnClickListener(this);
        checkInvTextView.setOnClickListener(this);
        returnOutTextView.setOnClickListener(this);
        outToFATextView .setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == scanInPalletTextView) {
            Intent intent = new Intent(this, ProductInScanCodeBoxActivity.class);
            startActivity(intent);
        } else if (v == scanInNotPalletTextView) {
            Intent intent = new Intent(this, ProductNotPalletInActivity.class);
            startActivity(intent);
        } else if (v == saleOutCodeBoxTextView) {
//            Intent intent = new Intent(this, ProductInScanCodeBoxActivity.class);
//            startActivity(intent);
        } else if (v == movePalletTextView) {
            Intent intent = new Intent(this, MoveProductPalletActivity.class);
            startActivity(intent);
        } else if (v == moveNotPalletTextView) {
            Intent intent = new Intent(this, MoveProductNotPalletActivity.class);
            startActivity(intent);
        } else if (v == checkInvTextView) {
            Intent intent = new Intent(this, ProductCheckInventoryActivity.class);
            startActivity(intent);
        }else if (v == returnOutTextView) {
            Intent intent = new Intent(this, ProductReturnOutActivity.class);
            startActivity(intent);
        }else if(v == outToFATextView ){
            Intent intent = new Intent(this, ProductOutToFAWarehouseActivity .class);
            startActivity(intent);
        }

    }
}
