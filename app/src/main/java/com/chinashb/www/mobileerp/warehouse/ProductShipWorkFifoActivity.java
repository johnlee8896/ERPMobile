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
import com.chinashb.www.mobileerp.adapter.ShipWorkFifoAdapter;
import com.chinashb.www.mobileerp.bean.ShipWorkModels.FifoLine;
import com.chinashb.www.mobileerp.bean.ShipWorkModels.WorkDetail;
import com.chinashb.www.mobileerp.funs.ShipWorkApi;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 成品发货 P07 - FIFO整托锁定。
 */
public class ProductShipWorkFifoActivity extends BaseActivity implements View.OnClickListener {

    private long workId;
    private TextView summaryTextView;
    private TextView statusTextView;
    private CustomRecyclerView recyclerView;
    private ShipWorkFifoAdapter adapter;
    private Button prepareButton;
    private boolean busy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_ship_work_fifo_layout);

        workId = getIntent().getLongExtra(ProductShipWorkDetailActivity.EXTRA_WORK_ID, 0);
        summaryTextView = findViewById(R.id.product_ship_work_fifo_summary_textView);
        statusTextView = findViewById(R.id.product_ship_work_fifo_status_textView);
        recyclerView = findViewById(R.id.product_ship_work_fifo_recyclerView);
        prepareButton = findViewById(R.id.product_ship_work_fifo_prepare_button);

        adapter = new ShipWorkFifoAdapter();
        adapter.setOnLockFifoClickListener(this::lockFifo);
        recyclerView.setAdapter(adapter);
        prepareButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onClick(View v) {
        if (v == prepareButton) {
            Intent intent = new Intent(this, ProductShipWorkPrepareActivity.class);
            intent.putExtra(ProductShipWorkDetailActivity.EXTRA_WORK_ID, workId);
            startActivity(intent);
            finish();
        }
    }

    private void loadData() {
        statusTextView.setText("正在读取FIFO数据");
        new LoadDataAsyncTask().execute(workId);
    }

    private void lockFifo(FifoLine line, double quantity) {
        if (busy) {
            return;
        }
        if (quantity <= 0 || quantity > line.lockablePalletQuantity) {
            ToastUtil.showToastShort("请输入有效锁定数量");
            return;
        }
        new android.app.AlertDialog.Builder(this)
                .setTitle("确认锁定")
                .setMessage("确认锁定 " + quantity + " 件？")
                .setPositiveButton("确认", (dialog, which) -> submitLock(line, quantity))
                .setNegativeButton("取消", null)
                .show();
    }

    private void submitLock(FifoLine line, double quantity) {
        busy = true;
        new LockFifoAsyncTask().execute(workId, line.dpiId, quantity);
    }

    private class LoadDataAsyncTask extends AsyncTask<Long, Void, Object[]> {
        @Override
        protected Object[] doInBackground(Long... params) {
            ShipWorkApi.Result<WorkDetail> detail = ShipWorkApi.getWorkDetail(params[0]);
            ShipWorkApi.Result<List<FifoLine>> lines = ShipWorkApi.getOrderLines(params[0]);
            return new Object[]{detail, lines};
        }

        @Override
        protected void onPostExecute(Object[] values) {
            @SuppressWarnings("unchecked")
            ShipWorkApi.Result<WorkDetail> detail = (ShipWorkApi.Result<WorkDetail>) values[0];
            @SuppressWarnings("unchecked")
            ShipWorkApi.Result<List<FifoLine>> lines = (ShipWorkApi.Result<List<FifoLine>>) values[1];
            if (!detail.success || detail.data == null) {
                statusTextView.setText(detail.errorInfo);
                return;
            }
            WorkDetail data = detail.data;
            com.chinashb.www.mobileerp.bean.ShipWorkModels.WorkHeader header = ShipWorkWorkflow.header(data);
            summaryTextView.setText(header == null ? "作业 " + workId : buildSummary(header));
            if (lines.success) {
                adapter.setData(lines.data == null ? new ArrayList<FifoLine>() : lines.data);
            } else {
                adapter.setData(new ArrayList<FifoLine>());
                statusTextView.setText(lines.errorInfo);
                return;
            }
            boolean hasActivePallet = !ShipWorkWorkflow.activePallets(data).isEmpty();
            prepareButton.setVisibility(hasActivePallet ? View.VISIBLE : View.GONE);
            statusTextView.setText(adapter.getRealCount() == 0 ? "没有产品明细" : "");
        }
    }

    private String buildSummary(com.chinashb.www.mobileerp.bean.ShipWorkModels.WorkHeader header) {
        String customer = header.customerName == null ? "" : "\n客户 " + header.customerName;
        String stage = header.stageName == null ? "" : "\n发货区 " + header.stageName;
        return "作业 " + header.workId + "  " + ShipWorkWorkflow.statusText(header.workStatus)
                + customer + stage;
    }

    private class LockFifoAsyncTask extends AsyncTask<Object, Void, ShipWorkApi.Result<Void>> {
        @Override
        protected ShipWorkApi.Result<Void> doInBackground(Object... params) {
            long id = (Long) params[0];
            long dpiId = (Long) params[1];
            double quantity = (Double) params[2];
            return ShipWorkApi.lockFifo(id, dpiId, quantity);
        }

        @Override
        protected void onPostExecute(ShipWorkApi.Result<Void> result) {
            busy = false;
            if (result.success) {
                ToastUtil.showToastShort("锁定成功，共 " + result.id + " 托");
                loadData();
            } else {
                ToastUtil.showToastShort(result.errorInfo);
                loadData();
            }
        }
    }
}
