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
import com.chinashb.www.mobileerp.bean.ShipWorkModels.WorkDetail;
import com.chinashb.www.mobileerp.bean.ShipWorkModels.WorkHeader;
import com.chinashb.www.mobileerp.bean.ShipWorkPalletBean;
import com.chinashb.www.mobileerp.funs.ShipWorkApi;

import java.util.List;

/**
 * 成品发货 - 作业恢复路由。
 */
public class ProductShipWorkDetailActivity extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_WORK_ID = "extra_work_id";
    public static final String EXTRA_DO_ID = "extra_do_id";

    private long workId;
    private TextView statusTextView;
    private TextView summaryTextView;
    private Button retryButton;
    private boolean loading;
    private boolean navigated;
    private boolean completed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_ship_work_detail_layout);

        workId = getIntent().getLongExtra(EXTRA_WORK_ID, 0);
        statusTextView = findViewById(R.id.product_ship_work_detail_status_textView);
        summaryTextView = findViewById(R.id.product_ship_work_detail_summary_textView);
        retryButton = findViewById(R.id.product_ship_work_detail_retry_button);
        retryButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 已从详情页进入过子流程，返回详情时只允许用户手动返回，不再自动重复跳转。
        if (navigated) {
            return;
        }
        navigated = false;
        completed = false;
        loadDetail();
    }

    @Override
    public void onClick(View v) {
        if (v == retryButton) {
            if (completed) {
                finish();
            } else {
                loadDetail();
            }
        }
    }

    private void loadDetail() {
        if (loading) {
            return;
        }
        loading = true;
        retryButton.setVisibility(View.GONE);
        statusTextView.setText("正在读取作业详情");
        summaryTextView.setText("");
        new LoadDetailAsyncTask().execute(workId);
    }

    private void route(WorkDetail detail) {
        WorkHeader header = ShipWorkWorkflow.header(detail);
        ShipWorkWorkflow.Step step = ShipWorkWorkflow.nextStep(detail);
        summaryTextView.setText(buildSummary(header, detail));
        if (step == ShipWorkWorkflow.Step.INVALID) {
            statusTextView.setText("作业不可用");
            retryButton.setVisibility(View.VISIBLE);
            return;
        }
        if (step == ShipWorkWorkflow.Step.COMPLETED) {
            completed = true;
            statusTextView.setText("该发货作业已完成");
            retryButton.setText("返回任务列表");
            retryButton.setVisibility(View.VISIBLE);
            return;
        }
        if (navigated) {
            return;
        }
        navigated = true;
        Intent intent = null;
        switch (step) {
            case FIFO:
                intent = new Intent(this, ProductShipWorkFifoActivity.class);
                break;
            case PREPARE:
                intent = new Intent(this, ProductShipWorkPrepareActivity.class);
                break;
            case OQC:
                intent = new Intent(this, ProductShipWorkOqcActivity.class);
                break;
            case SHIP_SCAN:
                intent = new Intent(this, ProductShipWorkShipScanActivity.class);
                break;
            case POST:
                intent = new Intent(this, ProductShipWorkPostActivity.class);
                break;
            default:
                break;
        }
        if (intent != null) {
            intent.putExtra(EXTRA_WORK_ID, workId);
            startActivity(intent);
        }
    }

    private String buildSummary(WorkHeader header, WorkDetail detail) {
        if (header == null) {
            return "";
        }
        String title = "作业 " + header.workId
                + "  " + ShipWorkWorkflow.statusText(header.workStatus);
        String customer = header.customerName == null ? "" : "\n客户 " + header.customerName;
        String stage = header.stageName == null ? "" : "\n发货区 " + header.stageName;
        List<ShipWorkPalletBean> pallets = detail == null ? null : detail.pallets;
        String counts = pallets == null ? "" : "\n托盘 " + pallets.size() + " 托";
        return title + customer + stage + counts;
    }

    private class LoadDetailAsyncTask extends AsyncTask<Long, Void, ShipWorkApi.Result<WorkDetail>> {
        @Override
        protected ShipWorkApi.Result<WorkDetail> doInBackground(Long... params) {
            return ShipWorkApi.getWorkDetail(params[0]);
        }

        @Override
        protected void onPostExecute(ShipWorkApi.Result<WorkDetail> result) {
            loading = false;
            if (result.success && result.data != null) {
                statusTextView.setText("正在进入下一步");
                route(result.data);
            } else {
                statusTextView.setText(result.errorInfo == null ? "读取作业详情失败" : result.errorInfo);
                retryButton.setText("重新查询");
                retryButton.setVisibility(View.VISIBLE);
            }
        }
    }
}
