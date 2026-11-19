package com.chinashb.www.mobileerp.warehouse;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.PendingWarehousePickingAdapter;
import com.chinashb.www.mobileerp.bean.PendingWarehousePickingBean;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.AppUtil;
import com.chinashb.www.mobileerp.utils.JsonUtil;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * codex by john 2026-11-12 - 来料待入库拣货列表
 */
public class PendingWarehousePickingActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_STOCK_IN = 0x1501;

    private EditText keywordEditText;
    private Button searchButton;
    private Button refreshButton;
    private Button enterScanInButton;
    private TextView summaryTextView;
    private ProgressBar loadingProgressBar;
    private CustomRecyclerView recyclerView;
    private PendingWarehousePickingAdapter adapter;
    private long dataRequestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_warehouse_picking);
        setHomeButton();

        keywordEditText = findViewById(R.id.et_pending_picking_keyword);
        searchButton = findViewById(R.id.btn_pending_picking_search);
        refreshButton = findViewById(R.id.btn_pending_picking_refresh);
        enterScanInButton = findViewById(R.id.btn_pending_enter_scan_in);
        summaryTextView = findViewById(R.id.tv_pending_picking_summary);
        loadingProgressBar = findViewById(R.id.pb_pending_picking_loading);
        recyclerView = findViewById(R.id.rv_pending_warehouse_picking);

        adapter = new PendingWarehousePickingAdapter();
        adapter.setOnItemClickListener(this::openStockInActivity);
        recyclerView.setAdapter(adapter);

        searchButton.setOnClickListener(this);
        refreshButton.setOnClickListener(this);
        enterScanInButton.setOnClickListener(this);
        keywordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_DOWN)) {
                AppUtil.forceHideInputMethod(this);
                loadData(keywordEditText.getText().toString().trim());
                return true;
            }
            return false;
        });

        loadData("");
    }

    @Override
    public void onClick(View v) {
        if (v == searchButton) {
            AppUtil.forceHideInputMethod(this);
            loadData(keywordEditText.getText().toString().trim());
        } else if (v == refreshButton) {
            AppUtil.forceHideInputMethod(this);
            loadData(keywordEditText.getText().toString().trim());
        } else if (v == enterScanInButton) {
            // 普通进入扫描入库页面，不带待入库拣货参数，不自动扫描标签。
            startActivity(new Intent(this, StockInActivity.class));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setHomeButton() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void loadData(String keys) {
        final long requestId = ++dataRequestId;
        setLoading(true);
        new GetPickingDataTask(requestId).execute(keys);
    }

    private void setLoading(boolean loading) {
        loadingProgressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        searchButton.setEnabled(!loading);
        refreshButton.setEnabled(!loading);
        enterScanInButton.setEnabled(!loading);
    }

    private void openStockInActivity(PendingWarehousePickingBean item) {
        if (item == null) {
            return;
        }
        if (item.getDiiiId() <= 0 || TextUtils.isEmpty(item.getCode())) {
            Toast.makeText(this, "该来料明细缺少入库标签，请刷新后重试。", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, StockInActivity.class);
        intent.putExtra(StockInActivity.EXTRA_PENDING_DIII_ID, item.getDiiiId());
        intent.putExtra(StockInActivity.EXTRA_PENDING_SCAN_CODE, item.getCode());
        intent.putExtra(StockInActivity.EXTRA_PENDING_ITEM_ID, item.getItemId());
        intent.putExtra(StockInActivity.EXTRA_PENDING_SOURCE_BU_ID, item.getBuId());
        intent.putExtra(StockInActivity.EXTRA_PENDING_ITEM_CODE, item.getItemCode());
        intent.putExtra(StockInActivity.EXTRA_PENDING_ITEM_NAME, item.getItemName());
        intent.putExtra(StockInActivity.EXTRA_PENDING_LOT_NO, item.getLotNo());
        startActivityForResult(intent, REQUEST_STOCK_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_STOCK_IN) {
            loadData(keywordEditText.getText().toString().trim());
        }
    }

    private class GetPickingDataTask extends AsyncTask<String, Void, PickingLoadResult> {
        private final long requestId;

        GetPickingDataTask(long requestId) {
            this.requestId = requestId;
        }

        @Override
        protected PickingLoadResult doInBackground(String... params) {
            String keys = params.length > 0 ? params[0] : "";
            WsResult result = WebServiceUtil.op_Get_Logistics_Pending_Warehouse_Picking_Data(
                    UserSingleton.get().getUserInfo().getBu_ID(),
                    UserSingleton.get().getHRID(),
                    keys);
            if (result == null) {
                return PickingLoadResult.failure("网络请求失败");
            }
            if (!result.getResult()) {
                return PickingLoadResult.failure(result.getErrorInfo());
            }

            try {
                Type listType = new TypeToken<List<PendingWarehousePickingBean>>() {
                }.getType();
                List<PendingWarehousePickingBean> list =
                        JsonUtil.parseJsonToObject(result.getErrorInfo(), listType);
                PickingSummary summary = JsonUtil.parseJsonToObject(
                        result.getInfo(), PickingSummary.class);
                return PickingLoadResult.success(
                        list == null ? new ArrayList<PendingWarehousePickingBean>() : list,
                        summary == null ? 0 : summary.total);
            } catch (Exception e) {
                return PickingLoadResult.failure("列表数据解析失败：" + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(PickingLoadResult loadResult) {
            if (requestId != dataRequestId) {
                return;
            }
            setLoading(false);
            if (loadResult == null) {
                showFailure("加载失败");
                return;
            }
            if (!loadResult.success) {
                showFailure(loadResult.message);
                return;
            }

            adapter.setData(loadResult.items);
            summaryTextView.setText("共 " + loadResult.items.size() + " 件");
            if (loadResult.items.isEmpty()) {
                Toast.makeText(PendingWarehousePickingActivity.this,
                        "没有找到待入库来料", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showFailure(String message) {
        adapter.setData(new ArrayList<PendingWarehousePickingBean>());
        summaryTextView.setText("加载失败");
        Toast.makeText(this, TextUtils.isEmpty(message) ? "加载失败" : message,
                Toast.LENGTH_LONG).show();
    }

    private static class PickingLoadResult {
        final boolean success;
        final String message;
        final List<PendingWarehousePickingBean> items;

        private PickingLoadResult(boolean success, String message,
                                  List<PendingWarehousePickingBean> items) {
            this.success = success;
            this.message = message;
            this.items = items;
        }

        static PickingLoadResult success(List<PendingWarehousePickingBean> items, int total) {
            return new PickingLoadResult(true, "", items);
        }

        static PickingLoadResult failure(String message) {
            return new PickingLoadResult(false, message,
                    new ArrayList<PendingWarehousePickingBean>());
        }
    }

    private static class PickingSummary {
        @SerializedName("Total") int total;
    }
}
