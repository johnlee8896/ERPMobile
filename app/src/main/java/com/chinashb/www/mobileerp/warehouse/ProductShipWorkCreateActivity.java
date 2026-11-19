package com.chinashb.www.mobileerp.warehouse;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.ShipWorkOrderGroupAdapter;
import com.chinashb.www.mobileerp.bean.ShipWorkLineBean;
import com.chinashb.www.mobileerp.bean.ShipWorkOrderBean;
import com.chinashb.www.mobileerp.bean.ShipWorkStageAreaBean;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.AppUtil;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 成品发货管理 H5OA 旧流程，保留用于对照和过渡测试。
 */
public class ProductShipWorkCreateActivity extends BaseActivity implements View.OnClickListener {

    private static final long DEFAULT_WAREHOUSE_ID = 132L;
    private static final long DEFAULT_DELIVERY_ID = 235747L;

    private EditText searchEditText;
    private Button searchButton;
    private CustomRecyclerView orderRecyclerView;
    private Button stageButton;
    private TextView stageTextView;
    private Button createButton;

    private ShipWorkOrderGroupAdapter orderGroupAdapter;

    private List<ShipWorkOrderBean> allOrderList = new ArrayList<>();
    private ShipWorkOrderBean selectedOrder;
    private ShipWorkStageAreaBean selectedStageArea;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_ship_work_create_layout);

        searchEditText = findViewById(R.id.product_ship_work_search_editText);
        searchButton = findViewById(R.id.product_ship_work_search_button);
        orderRecyclerView = findViewById(R.id.product_ship_work_order_recyclerView);
        stageButton = findViewById(R.id.product_ship_work_stage_button);
        stageTextView = findViewById(R.id.product_ship_work_stage_textView);
        createButton = findViewById(R.id.product_ship_work_create_button);

        orderGroupAdapter = new ShipWorkOrderGroupAdapter();
        orderGroupAdapter.setOnQueryInventoryClickListener((bean, statusTextView) -> {
            if (bean.getAvailable_Quantity() > 0) {
                statusTextView.setText("库存足够");
            } else {
                statusTextView.setText("库存查询中");
            }
        });
        orderGroupAdapter.setOnCustomerClickListener(customer -> {
            orderGroupAdapter.toggleCustomer(customer.cfId);
            ShipWorkOrderBean firstOrder = orderGroupAdapter.getFirstOrder(customer);
            if (firstOrder == null) {
                return;
            }
            selectedOrder = firstOrder;
            if (!orderGroupAdapter.isLineLoaded(firstOrder.getDO_ID())) {
                loadOrderLines(firstOrder.getDO_ID());
            } else {
                orderGroupAdapter.setExpandedOrder(firstOrder.getDO_ID());
            }
        });
        orderGroupAdapter.setOnOrderClickListener(order -> {
            selectedOrder = order;
            if (!orderGroupAdapter.isLineLoaded(order.getDO_ID())) {
                loadOrderLines(order.getDO_ID());
            } else {
                orderGroupAdapter.toggleOrder(order.getDO_ID());
            }
        });
        orderRecyclerView.setAdapter(orderGroupAdapter);

        searchButton.setOnClickListener(this);
        stageButton.setOnClickListener(this);
        createButton.setOnClickListener(this);

        loadOrders();
        AppUtil.forceHideInputMethod(this);
    }

    @Override
    public void onClick(View v) {
        if (v == searchButton) {
            filterOrders(searchEditText.getText().toString());
        } else if (v == stageButton) {
            if (selectedOrder == null) {
                ToastUtil.showToastShort("请先选择发货指令");
                return;
            }
            loadStageAreas();
        } else if (v == createButton) {
            createWork();
        }
    }

    private void loadOrders() {
        new LoadOrdersAsyncTask().execute();
    }

    private void filterOrders(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            orderGroupAdapter.setOrders(allOrderList);
        } else {
            List<ShipWorkOrderBean> filtered = new ArrayList<>();
            for (ShipWorkOrderBean bean : allOrderList) {
                if ((bean.getCustomer_Name() != null && bean.getCustomer_Name().contains(keyword))
                        || (bean.getTrackNo() != null && bean.getTrackNo().contains(keyword))
                        || String.valueOf(bean.getDO_ID()).contains(keyword)) {
                    filtered.add(bean);
                }
            }
            orderGroupAdapter.setOrders(filtered);
        }
        expandFirstGroup();
    }

    private void loadOrderLines(long doId) {
        new LoadLinesAsyncTask().execute(doId);
    }

    private void expandFirstGroup() {
        ShipWorkOrderGroupAdapter.CustomerGroup firstCustomer = orderGroupAdapter.getFirstCustomer();
        if (firstCustomer == null) {
            return;
        }
        ShipWorkOrderBean firstOrder = orderGroupAdapter.getFirstOrder(firstCustomer);
        orderGroupAdapter.setExpandedCustomer(firstCustomer.cfId);
        if (firstOrder != null) {
            selectedOrder = firstOrder;
            loadOrderLines(firstOrder.getDO_ID());
        }
    }

    private void loadStageAreas() {
        new LoadStageAreasAsyncTask().execute(DEFAULT_WAREHOUSE_ID, (long) UserSingleton.get().getUserInfo().getBu_ID());
    }

    private void createWork() {
        if (selectedOrder == null) {
            ToastUtil.showToastShort("请先选择发货指令");
            return;
        }
        long stageAreaId = selectedStageArea != null ? selectedStageArea.getStage_Area_ID() : 0L;
        new CreateWorkAsyncTask().execute(
                selectedOrder.getDO_ID(),
                DEFAULT_DELIVERY_ID,
                DEFAULT_WAREHOUSE_ID,
                stageAreaId);
    }

    private class LoadOrdersAsyncTask extends AsyncTask<Void, Void, List<ShipWorkOrderBean>> {
        @Override
        protected List<ShipWorkOrderBean> doInBackground(Void... params) {
            return WebServiceUtil.getShipWorkOrderList();
        }

        @Override
        protected void onPostExecute(List<ShipWorkOrderBean> list) {
            allOrderList = list == null ? new ArrayList<ShipWorkOrderBean>() : list;
            orderGroupAdapter.setOrders(allOrderList);
            expandFirstGroup();
        }
    }

    private class LoadLinesAsyncTask extends AsyncTask<Long, Void, Object[]> {
        @Override
        protected Object[] doInBackground(Long... params) {
            long doId = params[0];
            List<ShipWorkLineBean> lines = WebServiceUtil.getShipWorkLineList(doId);
            return new Object[]{doId, lines};
        }

        @Override
        protected void onPostExecute(Object[] result) {
            long doId = (Long) result[0];
            @SuppressWarnings("unchecked")
            List<ShipWorkLineBean> lines = (List<ShipWorkLineBean>) result[1];
            orderGroupAdapter.setLines(doId, lines == null ? new ArrayList<ShipWorkLineBean>() : lines);
            if (selectedOrder != null && selectedOrder.getDO_ID() == doId) {
                orderGroupAdapter.setExpandedOrder(doId);
            }
        }
    }

    private class LoadStageAreasAsyncTask extends AsyncTask<Long, Void, List<ShipWorkStageAreaBean>> {
        @Override
        protected List<ShipWorkStageAreaBean> doInBackground(Long... params) {
            return WebServiceUtil.getShipWorkStageAreaList(params[0], params[1].intValue());
        }

        @Override
        protected void onPostExecute(List<ShipWorkStageAreaBean> list) {
            if (list == null || list.isEmpty()) {
                ToastUtil.showToastShort("未获取到发货区");
                return;
            }
            final String[] names = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                names[i] = list.get(i).getStage_Name();
            }
            final List<ShipWorkStageAreaBean> stageList = list;
            new android.app.AlertDialog.Builder(ProductShipWorkCreateActivity.this)
                    .setTitle("选择发货区")
                    .setItems(names, (dialog, which) -> {
                        selectedStageArea = stageList.get(which);
                        stageTextView.setText(selectedStageArea.getStage_Name());
                    })
                    .show();
        }
    }

    private class CreateWorkAsyncTask extends AsyncTask<Long, Void, WsResult> {
        @Override
        protected WsResult doInBackground(Long... params) {
            return WebServiceUtil.opDpShipWorkCreate(params[0], params[1], params[2], params[3], UserSingleton.get().getHRID(), UserSingleton.get().getUserInfo().getBu_ID());
        }

        @Override
        protected void onPostExecute(WsResult result) {
            if (result != null && result.getResult()) {
                ToastUtil.showToastShort("建立发货作业成功 Work_ID=" + result.getID());
                finish();
            } else {
                String err = result != null ? result.getErrorInfo() : "建立失败";
                ToastUtil.showToastShort(err);
            }
        }
    }
}
