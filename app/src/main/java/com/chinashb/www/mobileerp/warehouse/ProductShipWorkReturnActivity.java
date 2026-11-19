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
 * 成品发货 P11 - 异常托盘退回与撤回。
 */
public class ProductShipWorkReturnActivity extends BaseActivity implements View.OnClickListener {

    private long workId;
    private TextView summaryTextView;
    private EditText barcodeEditText;
    private Button cameraButton;
    private Button returnButton;
    private TextView statusTextView;
    private CustomRecyclerView recyclerView;
    private Button backButton;
    private ShipWorkSimpleAdapter<ShipWorkPalletBean> adapter;
    private boolean busy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_ship_work_return_layout);

        workId = getIntent().getLongExtra(ProductShipWorkDetailActivity.EXTRA_WORK_ID, 0);
        summaryTextView = findViewById(R.id.product_ship_work_return_summary_textView);
        barcodeEditText = findViewById(R.id.product_ship_work_return_editText);
        cameraButton = findViewById(R.id.product_ship_work_return_camera_button);
        returnButton = findViewById(R.id.product_ship_work_return_button);
        statusTextView = findViewById(R.id.product_ship_work_return_status_textView);
        recyclerView = findViewById(R.id.product_ship_work_return_recyclerView);
        backButton = findViewById(R.id.product_ship_work_return_back_button);

        adapter = new ShipWorkSimpleAdapter<>(this::palletText);
        recyclerView.setAdapter(adapter);
        cameraButton.setOnClickListener(this);
        returnButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
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
        } else if (v == returnButton) {
            String barcode = barcodeEditText.getText().toString().trim();
            if (barcode.isEmpty()) {
                ToastUtil.showToastShort("请输入托盘码");
                return;
            }
            new android.app.AlertDialog.Builder(this)
                    .setTitle("确认退回")
                    .setMessage("退回会生成反向移库流水并释放该托盘；若已完成整单OQC，既有OQC同时失效。")
                    .setPositiveButton("确认退回", (dialog, which) -> returnPallet(barcode))
                    .setNegativeButton("取消", null)
                    .show();
        } else if (v == backButton) {
            finish();
        }
    }

    private void returnPallet(String barcode) {
        if (busy) {
            return;
        }
        busy = true;
        new ReturnPalletAsyncTask().execute(workId, barcode);
    }

    private void loadDetail() {
        new LoadDetailAsyncTask().execute(workId);
    }

    private String palletText(ShipWorkPalletBean pallet) {
        String remark = pallet.getOQC_Remark() == null || pallet.getOQC_Remark().isEmpty()
                ? "" : "\nOQC备注 " + pallet.getOQC_Remark();
        return "托盘 " + pallet.getCode_No_Box()
                + "\n原位置 " + safe(pallet.getOriginal_Location())
                + "  数量 " + format(pallet.getQuantity())
                + "\n状态 " + ShipWorkWorkflow.palletStatusText(pallet.getPallet_Status()) + remark;
    }

    private void render(WorkDetail detail) {
        WorkHeader header = ShipWorkWorkflow.header(detail);
        summaryTextView.setText(header == null ? "作业 " + workId
                : "作业 " + header.workId + "  可退回托盘 "
                        + returnable(detail).size() + " 托");
        adapter.setData(returnable(detail));
        statusTextView.setText("");
    }

    private List<ShipWorkPalletBean> returnable(WorkDetail detail) {
        List<ShipWorkPalletBean> list = new ArrayList<>();
        if (detail == null || detail.pallets == null) {
            return list;
        }
        for (ShipWorkPalletBean pallet : detail.pallets) {
            String status = pallet.getPallet_Status();
            if ("Staged".equalsIgnoreCase(status)
                    || "OQCPassed".equalsIgnoreCase(status)
                    || "OQCFailed".equalsIgnoreCase(status)
                    || "ShipScanned".equalsIgnoreCase(status)) {
                list.add(pallet);
            }
        }
        return list;
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
            } else {
                adapter.setData(new ArrayList<ShipWorkPalletBean>());
                statusTextView.setText(result.errorInfo);
            }
        }
    }

    private class ReturnPalletAsyncTask extends AsyncTask<Object, Void, ShipWorkApi.Result<Void>> {
        @Override
        protected ShipWorkApi.Result<Void> doInBackground(Object... params) {
            return ShipWorkApi.returnPallet((Long) params[0], (String) params[1]);
        }

        @Override
        protected void onPostExecute(ShipWorkApi.Result<Void> result) {
            busy = false;
            barcodeEditText.setText("");
            if (result.success) {
                ToastUtil.showToastShort("托盘已退回，OQC已失效");
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
            barcodeEditText.setText(result.getContents());
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
