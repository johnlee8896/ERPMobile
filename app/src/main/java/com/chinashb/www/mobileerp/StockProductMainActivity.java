package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chinashb.www.mobileerp.warehouse.StockDepartmentInActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2019/7/24 16:42
 * @author 作者: xxblwf
 * @description 成品库管理主界面
 */

public class StockProductMainActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.stock_product_scan_into_tray_Button) Button scanIntoTrayButton;
    @BindView(R.id.stock_product_send_goods_tray_Button) Button sendGoodsTrayButton;
    @BindView(R.id.stock_product_send_goods_non_tray_Button) Button sendGoodsNonTrayButton;
    @BindView(R.id.stock_product_send_goods_command_Button) Button sendGoodsCommandButton;
    @BindView(R.id.pb_scan_progressbar) ProgressBar pbScanProgressbar;
    @BindView(R.id.fab_test_tcp_net) FloatingActionButton fabTestTcpNet;
    @BindView(R.id.product_department_in_button) Button departmentInButton;

    @BindView(R.id.stock_product_scan_into_non_tray_Button) Button scanIntoNonTrayButton;
    @BindView(R.id.product_main_other_in_textView) TextView otherInTextView;
    @BindView(R.id.product_main_sale_out_textView) TextView saleOutTextView;
    @BindView(R.id.product_main_other_out_textView) TextView otherOutTextView;
    @BindView(R.id.product_main_scan_box_textView) TextView scanBoxInTextView;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_product_main_layout);
        ButterKnife.bind(this);

        setViewsListener();
    }

    private void setViewsListener() {
        scanIntoTrayButton.setOnClickListener(this);
        sendGoodsCommandButton.setOnClickListener(this);
        sendGoodsTrayButton.setOnClickListener(this);
        sendGoodsNonTrayButton.setOnClickListener(this);
        departmentInButton.setOnClickListener(this);

        scanIntoNonTrayButton.setOnClickListener(this);
        otherOutTextView.setOnClickListener(this);
        otherInTextView.setOnClickListener(this);
        saleOutTextView.setOnClickListener(this);
        scanBoxInTextView.setOnClickListener(this);
    }

    @Override public void onClick(View v) {
        if (v == scanIntoTrayButton) {

        } else if (v == scanIntoNonTrayButton) {
            Intent intent = new Intent(this, ProductInNonTrayScanActivity.class);
            startActivity(intent);
        } else if (v == sendGoodsTrayButton) {

        } else if (v == sendGoodsNonTrayButton) {

        } else if (v == sendGoodsCommandButton) {

        } else if (v == departmentInButton) {
            Intent intent = new Intent(this, StockDepartmentInActivity.class);
            //// TODO: 2019/9/6 区分是成品还是零件 
            startActivity(intent);
        } else if (v == saleOutTextView) {
            Intent intent = new Intent(this, ProductSaleOutActivity.class);
            startActivity(intent);
        } else if (v == otherInTextView) {

        } else if (v == otherOutTextView) {

        } else if (v == scanBoxInTextView){
            Intent intent = new Intent(this,ProductScanBoxInActivity.class);
            startActivity(intent);
        }
    }
}
