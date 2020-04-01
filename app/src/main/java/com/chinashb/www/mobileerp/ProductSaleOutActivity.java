package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private String customerFacilityName = "";

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
                logisticsDeliveryId = data.getLongExtra(IntentConstant.Intent_Extra_logistics_delivery_id, 0);
                customerFacilityId = data.getLongExtra(IntentConstant.Intent_Extra_logistics_cf_id, 0);

                if (logisticsDeliveryId > 0) {
                    customerCompanyTextView.setText(data.getCharSequenceExtra(IntentConstant.Intent_Extra_logistics_customer_company_name));
                    logisticsCompanyTextView.setText(data.getCharSequenceExtra(IntentConstant.Intent_Extra_logistics_logistics_company));
                    addressTextView.setText(data.getCharSequenceExtra(IntentConstant.Intent_Extra_logistics_address));
                    remarkTextView.setText(data.getCharSequenceExtra(IntentConstant.Intent_Extra_logistics_remark));
                    transportTypeTextView.setText(data.getCharSequenceExtra(IntentConstant.Intent_Extra_logistics_transport_type));

                    customerFacilityName = data.getStringExtra(IntentConstant.Intent_Extra_logistics_cf_name);
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
                intent.putExtra(IntentConstant.Intent_Extra_do_delivery_bean,deliveryOrderBean);
                startActivity(intent);
            } else {
                ToastUtil.showToastShort("请先选择发货指令");
            }
        }
    }

    private void handleProductOut() {
        if (TextUtils.equals(customerFacilityName, deliveryOrderBean.getCFChineseName())) {
            HandleProductOutAsyncTask outAsyncTask = new HandleProductOutAsyncTask();
            outAsyncTask.execute();
        } else {
            ToastUtil.showToastLong("发货指令中客户名称与物流信息中客户名称不一致，请检查！");
        }
    }

    private class HandleProductOutAsyncTask extends AsyncTask<Void, Void, Void> {
        private WsResult result;

        @Override protected Void doInBackground(Void... voids) {
//            long cfID = 0;
            long dpi_id = 0;
//            2020-01-03T00:00:00
//            new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss").parse("2019-01-03 10:59:27")
            Date deliveryDate = null;
            try {
                 deliveryDate = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss").parse(deliveryOrderBean.getDeliveryDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (deliveryDate == null){
                deliveryDate = new Date();
            }
            result = WebServiceUtil.op_Product_Manu_Out_Not_Pallet(deliveryDate, customerFacilityId, deliveryOrderBean.getCFChineseName(), deliveryOrderBean.getTrackNo(), logisticsDeliveryId, dpi_id, deliveryOrderBean.getDOID());
//            result = WebServiceUtil.op_Product_Manu_Out_Not_Pallet(new Date(), 68, "一汽奔腾轿车有限公司", "20200328", 102838, 0, 7426);
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
