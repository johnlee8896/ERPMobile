package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chinashb.www.mobileerp.adapter.DeliveryOrderAdapter;
import com.chinashb.www.mobileerp.bean.DeliveryOrderBean;
import com.chinashb.www.mobileerp.bean.entity.LogisticsDeliveryEntity;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2020/1/2 19:41
 * @author 作者: xxblwf
 * @description 成品出库
 */

public class ProductSaleOutActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.product_out_select_plan_button) Button selectPlanButton;
    @BindView(R.id.product_out_order_textView) TextView productNonTrayWcNameTextView;
    //    @BindView(R.id.product_non_tray_scan_button) Button productNonTrayScanButton;
    @BindView(R.id.product_out_out_button) Button outButton;
    @BindView(R.id.product_out_input_EditText) EditText inputEditText;
    @BindView(R.id.product_out_delivery_order_customRecyclerView) CustomRecyclerView recyclerView;
    @BindView(R.id.product_out_select_logistics_button) Button logisticsButton;
    @BindView(R.id.product_out_sdzh_out_button) Button sdzhOutButton;

    private DeliveryOrderBean deliveryOrderBean;
    private DeliveryOrderAdapter adapter;

    private LogisticsDeliveryEntity logisticsDeliveryEntity;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_out_layout);
        ButterKnife.bind(this);

        initViews();
        setViewsListener();
    }

    private void initViews() {
        adapter = new DeliveryOrderAdapter();
        recyclerView.setAdapter(adapter);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentConstant.Intent_Request_Code_Product_Out_And_Delivery_Order) {
            if (data != null) {
                deliveryOrderBean = data.getParcelableExtra(IntentConstant.Intent_product_delivery_order_bean);
                if (deliveryOrderBean != null) {
                    adapter.setOneData(deliveryOrderBean);
                }
            }
        }else if (requestCode == IntentConstant.Intent_Request_Code_Product_To_Logistics){
            if (data != null){
                logisticsDeliveryEntity = data.getParcelableExtra(IntentConstant.Intent_Extra_logistics_entity);

            }
        }
    }

    private void setViewsListener() {
        selectPlanButton.setOnClickListener(this);
        logisticsButton.setOnClickListener(this);
        outButton.setOnClickListener(this);
        sdzhOutButton.setOnClickListener(this);
    }

    @Override public void onClick(View v) {
        if (v == selectPlanButton) {
            Intent intent = new Intent(this, DeliveryOrderActivity.class);
            startActivityForResult(intent, IntentConstant.Intent_Request_Code_Product_Out_And_Delivery_Order);
            startActivity(intent);
        } else if (v == logisticsButton) {
            Intent intent = new Intent(this, LogisticsManageActivity.class);
            startActivityForResult(intent, IntentConstant.Intent_Request_Code_Product_To_Logistics);
        }else if (v == outButton){

        }else if (v == sdzhOutButton){
            Intent intent = new Intent(this,SDZHHActivity.class);
            startActivity(intent);
        }
    }
}
