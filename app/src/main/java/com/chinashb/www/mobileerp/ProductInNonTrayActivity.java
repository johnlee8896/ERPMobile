package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chinashb.www.mobileerp.bean.entity.WcIdNameEntity;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2019/12/18 15:35
 * @author 作者: xxblwf
 * @description 成品非托盘入库
 */

public class ProductInNonTrayActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.product_non_tray_scan_button) Button scanButton;
    @BindView(R.id.product_non_tray_scan_area_button) Button scanAreaButton;
    @BindView(R.id.product_non_tray_warehouse_in_button) Button warehouseInButton;
    @BindView(R.id.product_non_tray_input_EditText) EditText inputEditText;
    //    @BindView(R.id.product_non_tray_tv_item_name_col) TextView TvItemNameCol;
//    @BindView(R.id.product_non_tray_tv_bu_name_col) TextView TvBuNameCol;
//    @BindView(R.id.product_non_tray_tv_inv_in_lotno) TextView TvInvInLotno;
//    @BindView(R.id.product_non_tray_tv_qty_col) TextView TvQtyCol;
//    @BindView(R.id.product_non_tray_tv_ist_name_col) TextView TvIstNameCol;
//    @BindView(R.id.product_non_tray_tv_inv_in_selected) TextView TvInvInSelected;
    @BindView(R.id.product_non_tray_recyclerView) RecyclerView recyclerView;
    @BindView(R.id.product_non_tray_select_wc_button) Button selectWcButton;
    @BindView(R.id.product_non_tray_wc_name_textView) TextView wcNameTextView;

    private WcIdNameEntity wcIdNameEntity;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_in_no_tray_layout);
        ButterKnife.bind(this);
        setViewsListener();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            wcIdNameEntity = data.getParcelableExtra(IntentConstant.Intent_product_wc_id_name_entity);
            wcNameTextView.setText(wcIdNameEntity.getWcName());
        }
    }

    private void setViewsListener() {
        selectWcButton.setOnClickListener(this);
        scanButton.setOnClickListener(this);
        scanAreaButton.setOnClickListener(this);
        warehouseInButton.setOnClickListener(this);
        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                parseContent(editable.toString());
            }
        });
    }

    private void parseContent(String content) {

    }

    @Override public void onClick(View view) {
        if (view == selectWcButton) {
            getWCList();
        } else if (view == scanButton) {

        } else if (view == scanAreaButton) {

        } else if (view == warehouseInButton) {
            wcNameTextView.setText("");
        }
    }

    /**
     * 获取产线（工作中心）
     */
    private void getWCList() {
        Intent intent = new Intent(this, SelectProductWCListActivity.class);
        startActivityForResult(intent, 200);
    }


}
