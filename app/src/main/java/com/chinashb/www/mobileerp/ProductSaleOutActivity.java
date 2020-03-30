package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinashb.www.mobileerp.adapter.DeliveryOrderAdapter;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.DeliveryOrderBean;
import com.chinashb.www.mobileerp.bean.entity.LogisticsDeliveryEntity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;

import java.security.PolicySpi;
import java.util.Date;

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
    @BindView(R.id.product_out_logistics_customer_company_textView) TextView customerCompanyTextView;
    @BindView(R.id.product_out_logistics_logistics_company_textView) TextView logisticsCompanyTextView;
    @BindView(R.id.product_out_logistics_transport_type_textView) TextView transportTypeTextView;
    @BindView(R.id.product_out_logistics_address_textView) TextView addressTextView;
    @BindView(R.id.product_out_logistics_remark_textView) TextView remarkTextView;
    @BindView(R.id.product_out_logistics_data_layout) LinearLayout logisticsDataLayout;

    private DeliveryOrderBean deliveryOrderBean;
    private DeliveryOrderAdapter adapter;
    private long logisticsDeliveryId = 0;

    private LogisticsDeliveryEntity logisticsDeliveryEntity;
    private long customerFacilityId = 0;

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
        } else if (requestCode == IntentConstant.Intent_Request_Code_Product_To_Logistics) {
            //                logisticsDeliveryEntity = data.getParcelableExtra(IntentConstant.Intent_Extra_logistics_entity);
            if (data != null) {
                logisticsDeliveryId = (long) data.getLongExtra(IntentConstant.Intent_Extra_logistics_delivery_id, 0);
                customerFacilityId = (long) data.getLongExtra(IntentConstant.Intent_Extra_logistics_cf_id, 0);

                if (logisticsDeliveryId > 0) {
                    customerCompanyTextView.setText(data.getCharSequenceExtra(IntentConstant.Intent_Extra_logistics_customer_company_name));
                    logisticsCompanyTextView.setText(data.getCharSequenceExtra(IntentConstant.Intent_Extra_logistics_logistics_company));
                    addressTextView.setText(data.getCharSequenceExtra(IntentConstant.Intent_Extra_logistics_address));
                    remarkTextView.setText(data.getCharSequenceExtra(IntentConstant.Intent_Extra_logistics_remark));
                    transportTypeTextView.setText(data.getCharSequenceExtra(IntentConstant.Intent_Extra_logistics_transport_type));
                    logisticsDataLayout.setVisibility(View.VISIBLE);
                }
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
//            startActivity(intent);
        } else if (v == logisticsButton) {
            Intent intent = new Intent(this, LogisticsManageActivity.class);
            startActivityForResult(intent, IntentConstant.Intent_Request_Code_Product_To_Logistics);
        } else if (v == outButton) {
            handleProductOut();
        } else if (v == sdzhOutButton) {
            if (deliveryOrderBean != null) {
                Intent intent = new Intent(this, SDZHHActivity.class);
                startActivity(intent);
            } else {
                ToastUtil.showToastShort("请先选择发货指令");
            }
        }
    }

    private void handleProductOut() {
        HandleProductOutAsyncTask outAsyncTask = new HandleProductOutAsyncTask();
        outAsyncTask.execute();
    }


    private class HandleProductOutAsyncTask extends AsyncTask<Void, Void, Void> {
        private WsResult result;

        @Override protected Void doInBackground(Void... voids) {
//            long cfID = 0;
            long dpi_id = 0;
            result = WebServiceUtil.op_Product_Manu_Out_Not_Pallet(new Date(), customerFacilityId, deliveryOrderBean.getCFChineseName(), deliveryOrderBean.getTrackNo(), logisticsDeliveryId, dpi_id, deliveryOrderBean.getDOID());
            return null;
        }

        @Override protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (result != null) {
                if (result.getResult()) {
                    logisticsDeliveryId = result.getID();
                    ToastUtil.showToastShort("出库成功！");
                } else {
                    ToastUtil.showToastShort("出库失败 : " + result.getErrorInfo());
                }
            }
        }
    }
}
