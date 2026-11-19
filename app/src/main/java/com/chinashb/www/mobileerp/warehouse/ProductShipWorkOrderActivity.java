package com.chinashb.www.mobileerp.warehouse;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.ShipWorkOrderFlowAdapter;
import com.chinashb.www.mobileerp.bean.ShipWorkModels.InventoryLine;
import com.chinashb.www.mobileerp.bean.ShipWorkOrderBean;
import com.chinashb.www.mobileerp.funs.ShipWorkApi;
import com.chinashb.www.mobileerp.utils.AppUtil;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 成品发货 P04 - 选择发货指令及查看库存。
 */
public class ProductShipWorkOrderActivity extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_CF_ID = "extra_cf_id";
    public static final String EXTRA_CUSTOMER_NAME = "extra_customer_name";

    private long cfId;
    private TextView customerTextView;
    private EditText searchEditText;
    private Button searchButton;
    private TextView statusTextView;
    private CustomRecyclerView recyclerView;
    private ShipWorkOrderFlowAdapter adapter;
    private boolean loadingOrders;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_ship_work_order_layout);

        cfId = getIntent().getLongExtra(EXTRA_CF_ID, 0);
        customerTextView = findViewById(R.id.product_ship_work_order_customer_textView);
        searchEditText = findViewById(R.id.product_ship_work_order_search_editText);
        searchButton = findViewById(R.id.product_ship_work_order_search_button);
        statusTextView = findViewById(R.id.product_ship_work_order_status_textView);
        recyclerView = findViewById(R.id.product_ship_work_order_recyclerView);

        customerTextView.setText(getIntent().getStringExtra(EXTRA_CUSTOMER_NAME));
        adapter = new ShipWorkOrderFlowAdapter();
        adapter.setOnOrderActionListener(this::onOrderAction);
        adapter.setOnInventoryToggleListener(this::loadInventoryIfNeeded);
        recyclerView.setAdapter(adapter);

        searchButton.setOnClickListener(this);
        loadOrders("");
        AppUtil.forceHideInputMethod(this);
    }

    @Override
    public void onClick(View v) {
        if (v == searchButton) {
            loadOrders(searchEditText.getText().toString());
        }
    }

    private void onOrderAction(ShipWorkOrderBean order) {
        if (order.getWork_ID() != null && order.getWork_ID() > 0) {
            Intent intent = new Intent(this, ProductShipWorkDetailActivity.class);
            intent.putExtra(ProductShipWorkDetailActivity.EXTRA_WORK_ID, order.getWork_ID());
            startActivity(intent);
            return;
        }
        Intent intent = new Intent(this, ProductShipWorkSetupActivity.class);
        intent.putExtra(ProductShipWorkSetupActivity.EXTRA_DO_ID, order.getDO_ID());
        startActivity(intent);
    }

    private void loadOrders(String keyword) {
        if (loadingOrders) {
            return;
        }
        loadingOrders = true;
        statusTextView.setText("正在读取发货指令");
        new LoadOrdersAsyncTask().execute(keyword);
    }

    private void loadInventory(long doId) {
        new LoadInventoryAsyncTask().execute(doId);
    }

    private void loadInventoryIfNeeded(long doId) {
        if (!adapter.isInventoryLoaded(doId)) {
            loadInventory(doId);
        }
    }

    private class LoadOrdersAsyncTask extends AsyncTask<String, Void, ShipWorkApi.Result<List<ShipWorkOrderBean>>> {
        @Override
        protected ShipWorkApi.Result<List<ShipWorkOrderBean>> doInBackground(String... params) {
            return ShipWorkApi.getOrders(cfId, params[0]);
        }

        @Override
        protected void onPostExecute(ShipWorkApi.Result<List<ShipWorkOrderBean>> result) {
            loadingOrders = false;
            if (result.success) {
                adapter.setOrders(result.data == null ? new ArrayList<ShipWorkOrderBean>() : result.data);
                statusTextView.setText(adapter.getItemCount() == 0 ? "没有找到发货指令" : "");
            } else {
                adapter.setOrders(new ArrayList<ShipWorkOrderBean>());
                statusTextView.setText(result.errorInfo);
            }
        }
    }

    private class LoadInventoryAsyncTask extends AsyncTask<Long, Void, Object[]> {
        @Override
        protected Object[] doInBackground(Long... params) {
            long doId = params[0];
            ShipWorkApi.Result<List<InventoryLine>> result = ShipWorkApi.getOrderInventory(doId);
            return new Object[]{doId, result};
        }

        @Override
        protected void onPostExecute(Object[] values) {
            long doId = (Long) values[0];
            @SuppressWarnings("unchecked")
            ShipWorkApi.Result<List<InventoryLine>> result = (ShipWorkApi.Result<List<InventoryLine>>) values[1];
            if (result.success) {
                adapter.setInventory(doId, result.data == null ? new ArrayList<InventoryLine>() : result.data);
            }
        }
    }
}
