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
 * 成品发货 P10 - 发货区逐托复扫。
 */
public class ProductShipWorkShipScanActivity extends BaseActivity implements View.OnClickListener {

    private long workId;
    private TextView summaryTextView;
    private EditText scanEditText;
    private Button cameraButton;
    private Button scanButton;
    private TextView statusTextView;
    private CustomRecyclerView recyclerView;
    private Button returnButton;
    private Button postButton;
    private ShipWorkSimpleAdapter<ShipWorkPalletBean> adapter;
    private boolean busy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_ship_work_ship_scan_layout);

        workId = getIntent().getLongExtra(ProductShipWorkDetailActivity.EXTRA_WORK_ID, 0);
        summaryTextView = findViewById(R.id.product_ship_work_ship_scan_summary_textView);
        scanEditText = findViewById(R.id.product_ship_work_ship_scan_editText);
        cameraButton = findViewById(R.id.product_ship_work_ship_scan_camera_button);
        scanButton = findViewById(R.id.product_ship_work_ship_scan_button);
        statusTextView = findViewById(R.id.product_ship_work_ship_scan_status_textView);
        recyclerView = findViewById(R.id.product_ship_work_ship_scan_recyclerView);
        returnButton = findViewById(R.id.product_ship_work_ship_scan_return_button);
        postButton = findViewById(R.id.product_ship_work_ship_scan_post_button);

        adapter = new ShipWorkSimpleAdapter<>(this::palletText);
        recyclerView.setAdapter(adapter);
        cameraButton.setOnClickListener(this);
        scanButton.setOnClickListener(this);
        returnButton.setOnClickListener(this);
        postButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDetail();
    }

    @Override
    public void onClick(View v) {
        if (v == cameraButton) {
            new IntentIntegrator(this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
        } else if (v == scanButton) {
            scan();
        } else if (v == returnButton) {
            open(ProductShipWorkReturnActivity.class);
        } else if (v == postButton) {
            open(ProductShipWorkPostActivity.class);
        }
    }

    private void open(Class<?> target) {
        startActivity(new Intent(this, target)
                .putExtra(ProductShipWorkDetailActivity.EXTRA_WORK_ID, workId));
        finish();
    }

    private void scan() {
        String barcode = scanEditText.getText().toString().trim();
        if (barcode.isEmpty()) {
            ToastUtil.showToastShort("请输入托盘码");
            return;
        }
        if (busy) {
            return;
        }
        busy = true;
        new ShipScanAsyncTask().execute(workId, barcode);
    }

    private void loadDetail() {
        new LoadDetailAsyncTask().execute(workId);
    }

    private String palletText(ShipWorkPalletBean pallet) {
        return "托盘 " + pallet.getCode_No_Box()
                + "\n原位置 " + safe(pallet.getOriginal_Location())
                + "  数量 " + format(pallet.getQuantity())
                + "\n状态 " + ShipWorkWorkflow.palletStatusText(pallet.getPallet_Status());
    }

    private void render(WorkDetail detail) {
        WorkHeader header = ShipWorkWorkflow.header(detail);
        if (header == null) {
            summaryTextView.setText("作业 " + workId);
            return;
        }
        if (!ShipWorkWorkflow.isOqcPassed(header)) {
            statusTextView.setText("当前作业尚未通过整单OQC，不能执行发货复扫");
            scanEditText.setEnabled(false);
            scanButton.setEnabled(false);
            cameraButton.setEnabled(false);
        } else {
            scanEditText.setEnabled(true);
            scanButton.setEnabled(true);
            cameraButton.setEnabled(true);
        }
        List<ShipWorkPalletBean> list = new ArrayList<>();
        if (detail.pallets != null) {
            for (ShipWorkPalletBean pallet : detail.pallets) {
                if ("Staged".equalsIgnoreCase(pallet.getPallet_Status())
                        || "OQCPassed".equalsIgnoreCase(pallet.getPallet_Status())
                        || "OQCFailed".equalsIgnoreCase(pallet.getPallet_Status())
                        || "ShipScanned".equalsIgnoreCase(pallet.getPallet_Status())) {
                    list.add(pallet);
                }
            }
        }
        adapter.setData(list);
        int scanned = ShipWorkWorkflow.countStatus(list, "ShipScanned");
        int pending = list.size() - scanned;
        summaryTextView.setText("作业 " + header.workId
                + (header.customerName == null ? "" : "  客户 " + header.customerName)
                + "\n待复扫 " + pending + "  已复扫 " + scanned);
        postButton.setVisibility(pending == 0 && scanned > 0 ? View.VISIBLE : View.GONE);
    }

    private class LoadDetailAsyncTask extends AsyncTask<Long, Void, ShipWorkApi.Result<WorkDetail>> {
        @Override
        protected ShipWorkApi.Result<WorkDetail> doInBackground(Long... params) {
            return ShipWorkApi.getWorkDetail(params[0]);
        }

        @Override
        protected void onPostExecute(ShipWorkApi.Result<WorkDetail> result) {
            if (result.success && result.data != null) {
                render(result.data);
                statusTextView.setText("");
            } else {
                adapter.setData(new ArrayList<ShipWorkPalletBean>());
                statusTextView.setText(result.errorInfo);
            }
        }
    }

    private class ShipScanAsyncTask extends AsyncTask<Object, Void, ShipWorkApi.Result<Void>> {
        @Override
        protected ShipWorkApi.Result<Void> doInBackground(Object... params) {
            return ShipWorkApi.shipScan((Long) params[0], (String) params[1]);
        }

        @Override
        protected void onPostExecute(ShipWorkApi.Result<Void> result) {
            busy = false;
            scanEditText.setText("");
            if (result.success) {
                ToastUtil.showToastShort("发货复扫成功");
            } else {
                ToastUtil.showToastShort(result.errorInfo);
            }
            loadDetail();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {
            scanEditText.setText(result.getContents());
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
