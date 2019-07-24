package com.chinashb.www.mobileerp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2019/7/24 16:42
 * @author 作者: xxblwf
 * @description 成品库管理主界面
 */

public class StockProductMainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.stock_product_scan_into_tray_Button) Button scanIntoTrayButton;
    @BindView(R.id.stock_product_scan_into_non_tray_Button) Button scanIntoNonTrayButton;
    @BindView(R.id.stock_product_send_goods_tray_Button) Button sendGoodsTrayButton;
    @BindView(R.id.stock_product_send_goods_non_tray_Button) Button sendGoodsNonTrayButton;
    @BindView(R.id.stock_product_send_goods_command_Button) Button sendGoodsCommandButton;
    @BindView(R.id.pb_scan_progressbar) ProgressBar pbScanProgressbar;
    @BindView(R.id.fab_test_tcp_net) FloatingActionButton fabTestTcpNet;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_product_main_layout);
        ButterKnife.bind(this);

        setViewsListener();
    }

    private void setViewsListener() {
        scanIntoTrayButton.setOnClickListener(this);
        scanIntoNonTrayButton.setOnClickListener(this);
        sendGoodsCommandButton.setOnClickListener(this);
        sendGoodsTrayButton.setOnClickListener(this);
        sendGoodsNonTrayButton.setOnClickListener(this);
    }

    @Override public void onClick(View v) {
        if (v == scanIntoTrayButton){

        }else if (v == scanIntoNonTrayButton){

        }else if (v == sendGoodsTrayButton){

        }else if (v == sendGoodsNonTrayButton){

        }else if (v == sendGoodsCommandButton){

        }
    }
}
