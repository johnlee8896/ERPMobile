package com.chinashb.www.mobileerp.warehouse;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.ShipWorkSimpleAdapter;
import com.chinashb.www.mobileerp.bean.ShipWorkModels.WorkDetail;
import com.chinashb.www.mobileerp.bean.ShipWorkModels.WorkHeader;
import com.chinashb.www.mobileerp.bean.ShipWorkPalletBean;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.ShipWorkApi;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 成品发货 P08 - 备料初扫与实际库位移库。
 */
public class ProductShipWorkPrepareActivity extends BaseActivity implements View.OnClickListener {

    private long workId;
    private TextView summaryTextView;
    private EditText palletEditText;
    private Button palletCameraButton;
    private Button palletScanButton;
    private LinearLayout locationRow;
    private EditText locationEditText;
    private Button locationCameraButton;
    private Button moveButton;
    private TextView statusTextView;
    private CustomRecyclerView recyclerView;
    private Button lockButton;
    private Button returnButton;
    private Button oqcButton;
    private ShipWorkSimpleAdapter<ShipWorkPalletBean> adapter;

    private WorkDetail workDetail;
    private boolean busy;
    private boolean palletScanMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_ship_work_prepare_layout);

        workId = getIntent().getLongExtra(ProductShipWorkDetailActivity.EXTRA_WORK_ID, 0);
        summaryTextView = findViewById(R.id.product_ship_work_prepare_summary_textView);
        palletEditText = findViewById(R.id.product_ship_work_prepare_pallet_editText);
        palletCameraButton = findViewById(R.id.product_ship_work_prepare_pallet_camera_button);
        palletScanButton = findViewById(R.id.product_ship_work_prepare_pallet_scan_button);
        locationRow = findViewById(R.id.product_ship_work_prepare_location_row);
        locationEditText = findViewById(R.id.product_ship_work_prepare_location_editText);
        locationCameraButton = findViewById(R.id.product_ship_work_prepare_location_camera_button);
        moveButton = findViewById(R.id.product_ship_work_prepare_move_button);
        statusTextView = findViewById(R.id.product_ship_work_prepare_status_textView);
        recyclerView = findViewById(R.id.product_ship_work_prepare_recyclerView);
        lockButton = findViewById(R.id.product_ship_work_prepare_lock_button);
        returnButton = findViewById(R.id.product_ship_work_prepare_return_button);
        oqcButton = findViewById(R.id.product_ship_work_prepare_oqc_button);

        adapter = new ShipWorkSimpleAdapter<>(this::palletText);
        recyclerView.setAdapter(adapter);

        palletScanButton.setOnClickListener(this);
        palletCameraButton.setOnClickListener(this);
        locationCameraButton.setOnClickListener(this);
        moveButton.setOnClickListener(this);
        lockButton.setOnClickListener(this);
        returnButton.setOnClickListener(this);
        oqcButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onClick(View v) {
        if (v == palletScanButton) {
            pickScan(palletEditText.getText().toString());
        } else if (v == palletCameraButton) {
            palletScanMode = true;
            new IntentIntegrator(this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
        } else if (v == locationCameraButton) {
            palletScanMode = false;
            new IntentIntegrator(this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
        } else if (v == moveButton) {
            moveToStage();
        } else if (v == lockButton) {
            open(ProductShipWorkFifoActivity.class);
        } else if (v == returnButton) {
            open(ProductShipWorkReturnActivity.class);
        } else if (v == oqcButton) {
            open(ProductShipWorkOqcActivity.class);
        }
    }

    private void open(Class<?> target) {
        startActivity(new Intent(this, target)
                .putExtra(ProductShipWorkDetailActivity.EXTRA_WORK_ID, workId));
        finish();
    }

    private void pickScan(String barcode) {
        if (barcode.trim().isEmpty()) {
            ToastUtil.showToastShort("请输入托盘码");
            return;
        }
        if (busy) {
            return;
        }
        busy = true;
        new PickScanAsyncTask().execute(workId, barcode.trim());
    }

    private void moveToStage() {
        String location = locationEditText.getText().toString().trim();
        if (location.isEmpty()) {
            ToastUtil.showToastShort("请扫描实际待发库位");
            return;
        }
        if (busy) {
            return;
        }
        busy = true;
        new MoveToStageAsyncTask().execute(workId, location);
    }

    private void loadData() {
        new LoadDataAsyncTask().execute(workId);
    }

    private String palletText(ShipWorkPalletBean pallet) {
        return "托盘 " + pallet.getCode_No_Box()
                + "\n原位置 " + safe(pallet.getOriginal_Location())
                + "  数量 " + format(pallet.getQuantity())
                + "\n状态 " + ShipWorkWorkflow.palletStatusText(pallet.getPallet_Status())
                + (pallet.getPick_Scan_Name() == null || pallet.getPick_Scan_Name().isEmpty()
                        ? "" : "  初扫人 " + pallet.getPick_Scan_Name());
    }

    private void renderDetail(WorkDetail detail) {
        workDetail = detail;
        List<ShipWorkPalletBean> active = ShipWorkWorkflow.activePallets(detail);
        List<ShipWorkPalletBean> display = new ArrayList<>();
        if (detail.pallets != null) {
            for (ShipWorkPalletBean pallet : detail.pallets) {
                if (!ShipWorkWorkflow.isExcluded(pallet.getPallet_Status())) {
                    display.add(pallet);
                }
            }
        }
        adapter.setData(display);
        WorkHeader header = ShipWorkWorkflow.header(detail);
        int locked = ShipWorkWorkflow.countStatus(active, "Locked");
        int pickScanned = ShipWorkWorkflow.countStatus(active, "PickScanned");
        int staged = ShipWorkWorkflow.countStatus(active, "Staged")
                + ShipWorkWorkflow.countStatus(active, "OQCPassed")
                + ShipWorkWorkflow.countStatus(active, "OQCFailed")
                + ShipWorkWorkflow.countStatus(active, "ShipScanned");
        summaryTextView.setText(header == null ? "作业 " + workId : buildSummary(header, locked, pickScanned, staged));
        locationRow.setVisibility(pickScanned > 0 ? View.VISIBLE : View.GONE);
        oqcButton.setVisibility(locked == 0 && pickScanned == 0 && staged > 0 ? View.VISIBLE : View.GONE);
        statusTextView.setText("");
    }

    private String buildSummary(WorkHeader header, int locked, int pickScanned, int staged) {
        String customer = header.customerName == null ? "" : "\n客户 " + header.customerName;
        return "作业 " + header.workId + "  " + ShipWorkWorkflow.statusText(header.workStatus) + customer
                + "\n待初扫 " + locked + "  待移库 " + pickScanned + "  已入发货区 " + staged;
    }

    private class LoadDataAsyncTask extends AsyncTask<Long, Void, ShipWorkApi.Result<WorkDetail>> {
        @Override
        protected ShipWorkApi.Result<WorkDetail> doInBackground(Long... params) {
            return ShipWorkApi.getWorkDetail(params[0]);
        }

        @Override
        protected void onPostExecute(ShipWorkApi.Result<WorkDetail> result) {
            if (result.success && result.data != null) {
                renderDetail(result.data);
            } else {
                adapter.setData(new ArrayList<ShipWorkPalletBean>());
                statusTextView.setText(result.errorInfo);
            }
        }
    }

    private class PickScanAsyncTask extends AsyncTask<Object, Void, ShipWorkApi.Result<Void>> {
        @Override
        protected ShipWorkApi.Result<Void> doInBackground(Object... params) {
            return ShipWorkApi.pickScan((Long) params[0], (String) params[1]);
        }

        @Override
        protected void onPostExecute(ShipWorkApi.Result<Void> result) {
            busy = false;
            palletEditText.setText("");
            if (result.success) {
                ToastUtil.showToastShort("备料初扫成功");
            } else {
                ToastUtil.showToastShort(result.errorInfo);
            }
            loadData();
        }
    }

    private class MoveToStageAsyncTask extends AsyncTask<Object, Void, ShipWorkApi.Result<Void>> {
        @Override
        protected ShipWorkApi.Result<Void> doInBackground(Object... params) {
            return ShipWorkApi.moveToStage((Long) params[0], (String) params[1]);
        }

        @Override
        protected void onPostExecute(ShipWorkApi.Result<Void> result) {
            busy = false;
            locationEditText.setText("");
            if (result.success) {
                ToastUtil.showToastShort("移库成功，共 " + result.id + " 托");
            } else {
                ToastUtil.showToastShort(result.errorInfo);
            }
            loadData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {
            if (palletScanMode) {
                palletEditText.setText(result.getContents());
            } else {
                locationEditText.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    private static String format(double value) {
        return value == Math.rint(value) ? String.valueOf((long) value) : String.valueOf(value);
    }
}
