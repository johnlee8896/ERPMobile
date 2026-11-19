package com.chinashb.www.mobileerp.warehouse;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.ShipWorkSimpleAdapter;
import com.chinashb.www.mobileerp.bean.ShipWorkModels.Delivery;
import com.chinashb.www.mobileerp.bean.ShipWorkModels.Warehouse;
import com.chinashb.www.mobileerp.bean.ShipWorkStageAreaBean;
import com.chinashb.www.mobileerp.funs.ShipWorkApi;
import com.chinashb.www.mobileerp.utils.ShipWorkFormatUtil;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 成品发货 P05 - 物流单、发货区及建立作业。
 */
public class ProductShipWorkSetupActivity extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_DO_ID = "extra_do_id";
    private static final int REQUEST_DELIVERY = 3101;
    private static final int REQUEST_STAGE_AREA = 3102;

    private long doId;
    private TextView doTextView;
    private TextView warehouseTextView;
    private TextView deliveryTextView;
    private Button newDeliveryButton;
    private CustomRecyclerView deliveryRecyclerView;
    private ShipWorkSimpleAdapter<Delivery> deliveryAdapter;
    private TextView stageTextView;
    private Button stageButton;
    private Button createButton;

    private long selectedDeliveryId;
    private long selectedStageAreaId;
    private boolean busy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_ship_work_setup_layout);

        doId = getIntent().getLongExtra(EXTRA_DO_ID, 0);
        doTextView = findViewById(R.id.product_ship_work_setup_do_textView);
        warehouseTextView = findViewById(R.id.product_ship_work_setup_warehouse_textView);
        deliveryTextView = findViewById(R.id.product_ship_work_setup_delivery_textView);
        newDeliveryButton = findViewById(R.id.product_ship_work_new_delivery_button);
        deliveryRecyclerView = findViewById(R.id.product_ship_work_setup_delivery_recyclerView);
        stageTextView = findViewById(R.id.product_ship_work_setup_stage_textView);
        stageButton = findViewById(R.id.product_ship_work_setup_stage_button);
        createButton = findViewById(R.id.product_ship_work_setup_create_button);

        doTextView.setText("发货指令 " + doId);
        deliveryAdapter = new ShipWorkSimpleAdapter<>(this::deliveryText);
        deliveryAdapter.setOnItemClickListener(this::selectDelivery);
        deliveryRecyclerView.setAdapter(deliveryAdapter);

        newDeliveryButton.setOnClickListener(this);
        stageButton.setOnClickListener(this);
        createButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSetupData();
    }

    @Override
    public void onClick(View v) {
        if (v == newDeliveryButton) {
            startActivityForResult(new Intent(this, ProductShipWorkDeliveryCreateActivity.class)
                    .putExtra(ProductShipWorkDeliveryCreateActivity.EXTRA_DO_ID, doId), REQUEST_DELIVERY);
        } else if (v == stageButton) {
            startActivityForResult(new Intent(this, ProductShipWorkStageAreaSettingsActivity.class)
                    .putExtra(ProductShipWorkStageAreaSettingsActivity.EXTRA_DO_ID, doId), REQUEST_STAGE_AREA);
        } else if (v == createButton) {
            createWork();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DELIVERY && resultCode == RESULT_OK && data != null) {
            long deliveryId = data.getLongExtra(ProductShipWorkDeliveryCreateActivity.EXTRA_DELIVERY_ID, 0);
            if (deliveryId > 0) {
                selectedDeliveryId = deliveryId;
                deliveryTextView.setText("物流单：已选择 ID " + deliveryId);
                loadDeliveries(deliveryId);
            }
        }
    }

    private String deliveryText(Delivery item) {
        String track = item.trackNo == null || item.trackNo.isEmpty() ? "无跟踪号" : item.trackNo;
        return track + "\n物流单ID " + item.deliveryId
                + "\n发货日期 " + ShipWorkFormatUtil.date(item.deliveryDate)
                + "  车牌 " + safe(item.licensePlateNo)
                + "  司机 " + safe(item.driverName);
    }

    private void selectDelivery(Delivery item) {
        selectedDeliveryId = item.deliveryId;
        deliveryTextView.setText("物流单：" + (item.trackNo == null ? "" : item.trackNo));
    }

    private void loadSetupData() {
        loadDeliveries(0);
        loadWarehouse();
        loadStageAreas();
    }

    private void loadWarehouse() {
        new LoadWarehouseAsyncTask().execute(doId);
    }

    private void loadDeliveries(final long preferredId) {
        new LoadDeliveriesAsyncTask().execute(preferredId);
    }

    private void loadStageAreas() {
        new LoadStageAreasAsyncTask().execute();
    }

    private void createWork() {
        if (busy) {
            return;
        }
        if (selectedDeliveryId <= 0) {
            ToastUtil.showToastShort("请选择或新建物流单");
            return;
        }
        if (selectedStageAreaId <= 0) {
            ToastUtil.showToastShort("请先设置唯一发货区");
            return;
        }
        busy = true;
        createButton.setEnabled(false);
        new CreateWorkAsyncTask().execute(doId, selectedDeliveryId, selectedStageAreaId);
    }

    private class LoadDeliveriesAsyncTask extends AsyncTask<Long, Void, Object[]> {
        @Override
        protected Object[] doInBackground(Long... params) {
            long preferredId = params[0];
            ShipWorkApi.Result<List<Delivery>> result = ShipWorkApi.getDeliveries(doId, "");
            return new Object[]{preferredId, result};
        }

        @Override
        protected void onPostExecute(Object[] values) {
            long preferredId = (Long) values[0];
            @SuppressWarnings("unchecked")
            ShipWorkApi.Result<List<Delivery>> result = (ShipWorkApi.Result<List<Delivery>>) values[1];
            if (!result.success) {
                ToastUtil.showToastShort(result.errorInfo);
                return;
            }
            List<Delivery> list = result.data == null ? new ArrayList<Delivery>() : result.data;
            deliveryAdapter.setData(list);
            if (list.size() == 1) {
                selectDelivery(list.get(0));
            } else if (preferredId > 0) {
                for (Delivery delivery : list) {
                    if (delivery.deliveryId == preferredId) {
                        selectDelivery(delivery);
                        break;
                    }
                }
            }
        }
    }

    private class LoadStageAreasAsyncTask extends AsyncTask<Void, Void, ShipWorkApi.Result<List<ShipWorkStageAreaBean>>> {
        @Override
        protected ShipWorkApi.Result<List<ShipWorkStageAreaBean>> doInBackground(Void... params) {
            return ShipWorkApi.getStageAreas(doId);
        }

        @Override
        protected void onPostExecute(ShipWorkApi.Result<List<ShipWorkStageAreaBean>> result) {
            if (!result.success) {
                stageTextView.setText("发货区：" + result.errorInfo);
                return;
            }
            List<ShipWorkStageAreaBean> list = result.data == null ? new ArrayList<ShipWorkStageAreaBean>() : result.data;
            if (list.isEmpty()) {
                selectedStageAreaId = 0;
                stageTextView.setText("发货区：未设置，请先设置");
            } else if (list.size() == 1) {
                selectedStageAreaId = list.get(0).getStage_Area_ID();
                stageTextView.setText("发货区：" + list.get(0).getStage_Name());
            } else {
                selectedStageAreaId = 0;
                stageTextView.setText("发货区：存在 " + list.size() + " 个，请指定唯一发货区");
            }
        }
    }

    private class LoadWarehouseAsyncTask extends AsyncTask<Long, Void, ShipWorkApi.Result<List<Warehouse>>> {
        @Override
        protected ShipWorkApi.Result<List<Warehouse>> doInBackground(Long... params) {
            return ShipWorkApi.getWarehouses(params[0]);
        }

        @Override
        protected void onPostExecute(ShipWorkApi.Result<List<Warehouse>> result) {
            if (!result.success || result.data == null || result.data.isEmpty()) {
                warehouseTextView.setText("仓库：" + (result.success ? "未获取到" : result.errorInfo));
                return;
            }
            if (result.data.size() == 1) {
                Warehouse warehouse = result.data.get(0);
                warehouseTextView.setText("仓库：" + warehouse.warehouseName);
            } else {
                warehouseTextView.setText("仓库：匹配到 " + result.data.size() + " 个，请检查配置");
            }
        }
    }

    private class CreateWorkAsyncTask extends AsyncTask<Long, Void, ShipWorkApi.Result<Void>> {
        @Override
        protected ShipWorkApi.Result<Void> doInBackground(Long... params) {
            return ShipWorkApi.createWork(params[0], params[1], params[2]);
        }

        @Override
        protected void onPostExecute(ShipWorkApi.Result<Void> result) {
            busy = false;
            createButton.setEnabled(true);
            if (result.success) {
                ToastUtil.showToastShort("建立发货作业成功 Work_ID=" + result.id);
                Intent intent = new Intent(ProductShipWorkSetupActivity.this, ProductShipWorkDetailActivity.class);
                intent.putExtra(ProductShipWorkDetailActivity.EXTRA_WORK_ID, result.id);
                startActivity(intent);
                finish();
            } else {
                ToastUtil.showToastShort(result.errorInfo);
            }
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
