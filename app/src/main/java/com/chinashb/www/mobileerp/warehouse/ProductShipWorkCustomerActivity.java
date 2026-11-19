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
import com.chinashb.www.mobileerp.adapter.ShipWorkSimpleAdapter;
import com.chinashb.www.mobileerp.bean.ShipWorkModels.Customer;
import com.chinashb.www.mobileerp.funs.ShipWorkApi;
import com.chinashb.www.mobileerp.utils.AppUtil;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 成品发货 P03 - 选择客户。
 */
public class ProductShipWorkCustomerActivity extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_CF_ID = "extra_cf_id";
    public static final String EXTRA_CUSTOMER_NAME = "extra_customer_name";

    private EditText searchEditText;
    private Button searchButton;
    private TextView statusTextView;
    private CustomRecyclerView recyclerView;
    private ShipWorkSimpleAdapter<Customer> adapter;
    private boolean loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_ship_work_customer_layout);

        searchEditText = findViewById(R.id.product_ship_work_customer_search_editText);
        searchButton = findViewById(R.id.product_ship_work_customer_search_button);
        statusTextView = findViewById(R.id.product_ship_work_customer_status_textView);
        recyclerView = findViewById(R.id.product_ship_work_customer_recyclerView);

        adapter = new ShipWorkSimpleAdapter<>(item -> item.orderCount + "个待发指令  " + item.customerName
                + "\n客户ID " + item.cfId);
        adapter.setOnItemClickListener(this::openOrders);
        recyclerView.setAdapter(adapter);

        searchButton.setOnClickListener(this);
        loadCustomers("");
        AppUtil.forceHideInputMethod(this);
    }

    @Override
    public void onClick(View v) {
        if (v == searchButton) {
            loadCustomers(searchEditText.getText().toString());
        }
    }

    private void openOrders(Customer customer) {
        Intent intent = new Intent(this, ProductShipWorkOrderActivity.class);
        intent.putExtra(ProductShipWorkOrderActivity.EXTRA_CF_ID, customer.cfId);
        intent.putExtra(ProductShipWorkOrderActivity.EXTRA_CUSTOMER_NAME, customer.customerName);
        startActivity(intent);
    }

    private void loadCustomers(String keyword) {
        if (loading) {
            return;
        }
        loading = true;
        statusTextView.setText("正在读取待发货客户");
        new LoadCustomersAsyncTask().execute(keyword);
    }

    private class LoadCustomersAsyncTask extends AsyncTask<String, Void, ShipWorkApi.Result<List<Customer>>> {
        @Override
        protected ShipWorkApi.Result<List<Customer>> doInBackground(String... params) {
            return ShipWorkApi.getCustomers(params[0]);
        }

        @Override
        protected void onPostExecute(ShipWorkApi.Result<List<Customer>> result) {
            loading = false;
            if (result.success) {
                adapter.setData(result.data == null ? new ArrayList<Customer>() : result.data);
                statusTextView.setText(adapter.getRealCount() == 0 ? "没有找到待发货客户" : "");
            } else {
                adapter.setData(new ArrayList<Customer>());
                statusTextView.setText(result.errorInfo);
            }
        }
    }
}
