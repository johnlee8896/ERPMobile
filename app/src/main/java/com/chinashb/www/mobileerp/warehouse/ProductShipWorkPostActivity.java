package com.chinashb.www.mobileerp.warehouse;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.ShipWorkModels.WorkDetail;
import com.chinashb.www.mobileerp.bean.ShipWorkModels.WorkHeader;
import com.chinashb.www.mobileerp.bean.ShipWorkPalletBean;
import com.chinashb.www.mobileerp.funs.ShipWorkApi;
import com.chinashb.www.mobileerp.utils.ToastUtil;

import java.util.List;

/**
 * 成品发货 P12 - 发运确认与销售出库记账。
 */
public class ProductShipWorkPostActivity extends BaseActivity implements View.OnClickListener {

    private long workId;
    private TextView summaryTextView;
    private CheckBox confirmCheckBox;
    private Button returnButton;
    private Button submitButton;
    private TextView statusTextView;
    private boolean eligible;
    private boolean busy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_ship_work_post_layout);

        workId = getIntent().getLongExtra(ProductShipWorkDetailActivity.EXTRA_WORK_ID, 0);
        summaryTextView = findViewById(R.id.product_ship_work_post_summary_textView);
        confirmCheckBox = findViewById(R.id.product_ship_work_post_confirm_checkBox);
        returnButton = findViewById(R.id.product_ship_work_post_return_button);
        submitButton = findViewById(R.id.product_ship_work_post_submit_button);
        statusTextView = findViewById(R.id.product_ship_work_post_status_textView);

        returnButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDetail();
    }

    @Override
    public void onClick(View v) {
        if (v == returnButton) {
            startActivity(new Intent(this, ProductShipWorkReturnActivity.class)
                    .putExtra(ProductShipWorkDetailActivity.EXTRA_WORK_ID, workId));
            finish();
        } else if (v == submitButton) {
            post();
        }
    }

    private void post() {
        if (busy || !eligible) {
            return;
        }
        if (!confirmCheckBox.isChecked()) {
            ToastUtil.showToastShort("请先确认本车托盘、OQC结果和物流单");
            return;
        }
        busy = true;
        submitButton.setEnabled(false);
        new PostAsyncTask().execute(workId);
    }

    private void loadDetail() {
        new LoadDetailAsyncTask().execute(workId);
    }

    private void render(WorkDetail detail) {
        WorkHeader header = ShipWorkWorkflow.header(detail);
        if (header == null) {
            summaryTextView.setText("作业 " + workId);
            eligible = false;
            updateButtons();
            return;
        }
        List<ShipWorkPalletBean> active = ShipWorkWorkflow.activePallets(detail);
        int scanned = ShipWorkWorkflow.countStatus(active, "ShipScanned");
        eligible = !active.isEmpty() && scanned == active.size();
        String customer = header.customerName == null ? "" : "\n客户 " + header.customerName;
        String stage = header.stageName == null ? "" : "\n发货区 " + header.stageName;
        summaryTextView.setText("作业 " + header.workId + customer + stage
                + "\n有效托盘 " + active.size()
                + "  复扫完成 " + scanned
                + "\n物流单ID " + header.deliveryId);
        updateButtons();
        if ("Posted".equalsIgnoreCase(header.workStatus)) {
            statusTextView.setText("销售出库已完成");
            submitButton.setVisibility(View.GONE);
            returnButton.setVisibility(View.GONE);
            return;
        }
        statusTextView.setText("");
    }

    private void updateButtons() {
        submitButton.setEnabled(eligible);
        submitButton.setVisibility(eligible ? View.VISIBLE : View.GONE);
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
                eligible = false;
                updateButtons();
            }
        }
    }

    private class PostAsyncTask extends AsyncTask<Long, Void, ShipWorkApi.Result<Void>> {
        @Override
        protected ShipWorkApi.Result<Void> doInBackground(Long... params) {
            return ShipWorkApi.post(params[0]);
        }

        @Override
        protected void onPostExecute(ShipWorkApi.Result<Void> result) {
            busy = false;
            submitButton.setEnabled(eligible);
            if (result.success) {
                String warning = result.info == null || result.info.isEmpty()
                        ? "" : "\n" + result.info;
                ToastUtil.showToastLong("销售出库完成，SID=" + result.id + warning);
                finish();
            } else if (result.networkFailure) {
                statusTextView.setText("网络请求未确认，正在重新核对作业状态");
                loadDetail();
            } else {
                statusTextView.setText(result.errorInfo);
            }
        }
    }
}
