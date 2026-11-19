package com.chinashb.www.mobileerp.warehouse;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.ShipWorkModels.WorkDetail;
import com.chinashb.www.mobileerp.bean.ShipWorkModels.WorkHeader;
import com.chinashb.www.mobileerp.funs.ShipWorkApi;
import com.chinashb.www.mobileerp.utils.ToastUtil;

/**
 * 成品发货 P09 - 整单OQC终检。
 */
public class ProductShipWorkOqcActivity extends BaseActivity implements View.OnClickListener {

    private long workId;
    private TextView summaryTextView;
    private TextView resultTextView;
    private CheckBox documentCheckBox;
    private CheckBox palletCountCheckBox;
    private CheckBox batchCheckBox;
    private CheckBox appearanceCheckBox;
    private EditText remarkEditText;
    private Button passButton;
    private Button failButton;
    private Button returnButton;
    private Button shipScanButton;
    private TextView statusTextView;
    private WorkHeader header;
    private boolean busy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_ship_work_oqc_layout);

        workId = getIntent().getLongExtra(ProductShipWorkDetailActivity.EXTRA_WORK_ID, 0);
        summaryTextView = findViewById(R.id.product_ship_work_oqc_summary_textView);
        resultTextView = findViewById(R.id.product_ship_work_oqc_result_textView);
        documentCheckBox = findViewById(R.id.product_ship_work_oqc_document_checkBox);
        palletCountCheckBox = findViewById(R.id.product_ship_work_oqc_pallet_count_checkBox);
        batchCheckBox = findViewById(R.id.product_ship_work_oqc_batch_checkBox);
        appearanceCheckBox = findViewById(R.id.product_ship_work_oqc_appearance_checkBox);
        remarkEditText = findViewById(R.id.product_ship_work_oqc_remark_editText);
        passButton = findViewById(R.id.product_ship_work_oqc_pass_button);
        failButton = findViewById(R.id.product_ship_work_oqc_fail_button);
        returnButton = findViewById(R.id.product_ship_work_oqc_return_button);
        shipScanButton = findViewById(R.id.product_ship_work_oqc_ship_scan_button);
        statusTextView = findViewById(R.id.product_ship_work_oqc_status_textView);

        passButton.setOnClickListener(this);
        failButton.setOnClickListener(this);
        returnButton.setOnClickListener(this);
        shipScanButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDetail();
    }

    @Override
    public void onClick(View v) {
        if (v == passButton) {
            record(true);
        } else if (v == failButton) {
            record(false);
        } else if (v == returnButton) {
            open(ProductShipWorkReturnActivity.class);
        } else if (v == shipScanButton) {
            open(ProductShipWorkShipScanActivity.class);
        }
    }

    private void open(Class<?> target) {
        startActivity(new Intent(this, target)
                .putExtra(ProductShipWorkDetailActivity.EXTRA_WORK_ID, workId));
        finish();
    }

    private void record(boolean passed) {
        if (busy) {
            return;
        }
        String remark = remarkEditText.getText().toString().trim();
        if (passed) {
            if (!documentCheckBox.isChecked() || !palletCountCheckBox.isChecked()
                    || !batchCheckBox.isChecked() || !appearanceCheckBox.isChecked()) {
                ToastUtil.showToastShort("四项核对必须全部完成");
                return;
            }
        } else if (remark.isEmpty()) {
            ToastUtil.showToastShort("不合格时必须填写原因");
            return;
        }
        busy = true;
        new RecordOqcAsyncTask().execute(workId, passed, remark);
    }

    private void loadDetail() {
        new LoadDetailAsyncTask().execute(workId);
    }

    private void render(WorkDetail detail) {
        header = ShipWorkWorkflow.header(detail);
        if (header == null) {
            summaryTextView.setText("作业 " + workId);
            return;
        }
        String customer = header.customerName == null ? "" : "\n客户 " + header.customerName;
        String stage = header.stageName == null ? "" : "\n发货区 " + header.stageName;
        summaryTextView.setText("作业 " + header.workId + customer + stage
                + "\n有效托盘 " + ShipWorkWorkflow.activePallets(detail).size() + " 托");

        boolean pass = ShipWorkWorkflow.isOqcPassed(header);
        documentCheckBox.setChecked(pass);
        palletCountCheckBox.setChecked(pass);
        batchCheckBox.setChecked(pass);
        appearanceCheckBox.setChecked(pass);
        remarkEditText.setText(header.oqcRemark == null ? "" : header.oqcRemark);
        resultTextView.setText(buildResultText(header));
        shipScanButton.setVisibility(pass ? View.VISIBLE : View.GONE);
        statusTextView.setText("");
    }

    private String buildResultText(WorkHeader header) {
        String result;
        if (ShipWorkWorkflow.isOqcPassed(header)) {
            result = "整单合格";
        } else if ("Fail".equalsIgnoreCase(header.oqcResult)) {
            result = "整单不合格";
        } else {
            result = "待终检";
        }
        StringBuilder builder = new StringBuilder("终检结果：" + result);
        if (header.oqcRevision > 0) {
            builder.append("\n第 ").append(header.oqcRevision).append(" 次终检");
        }
        if (header.oqcByName != null && !header.oqcByName.isEmpty()) {
            builder.append("\n终检人 ").append(header.oqcByName);
        }
        if (header.oqcPalletCount != null) {
            builder.append("\n快照托盘 ").append(header.oqcPalletCount);
        }
        if (header.oqcQuantity != null) {
            builder.append("  快照件数 ").append(header.oqcQuantity);
        }
        return builder.toString();
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
                statusTextView.setText(result.errorInfo);
            }
        }
    }

    private class RecordOqcAsyncTask extends AsyncTask<Object, Void, ShipWorkApi.Result<Void>> {
        @Override
        protected ShipWorkApi.Result<Void> doInBackground(Object... params) {
            return ShipWorkApi.recordOqc((Long) params[0], (Boolean) params[1], (String) params[2]);
        }

        @Override
        protected void onPostExecute(ShipWorkApi.Result<Void> result) {
            busy = false;
            if (result.success) {
                ToastUtil.showToastShort("OQC记录成功");
                loadDetail();
            } else {
                statusTextView.setText(result.errorInfo);
            }
        }
    }
}
