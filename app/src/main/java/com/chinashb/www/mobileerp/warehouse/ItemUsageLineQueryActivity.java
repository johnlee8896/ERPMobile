package com.chinashb.www.mobileerp.warehouse;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.chinashb.www.mobileerp.adapter.ItemUsageLineQueryAdapter;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.ItemUsageLineQueryBean;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.AppUtil;
import com.chinashb.www.mobileerp.utils.JsonUtil;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * codex by john 2026-11-12 - 物料使用线别查询
 */
public class ItemUsageLineQueryActivity extends BaseActivity implements View.OnClickListener {
    private EditText itemIdEditText;
    private Button searchButton;
    private TextView summaryTextView;
    private ProgressBar loadingProgressBar;
    private CustomRecyclerView recyclerView;
    private ItemUsageLineQueryAdapter adapter;
    private long requestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_usage_line_query);
        setHomeButton();

        itemIdEditText = findViewById(R.id.et_item_usage_item_id);
        searchButton = findViewById(R.id.btn_item_usage_search);
        summaryTextView = findViewById(R.id.tv_item_usage_summary);
        loadingProgressBar = findViewById(R.id.pb_item_usage_loading);
        recyclerView = findViewById(R.id.rv_item_usage_line);

        adapter = new ItemUsageLineQueryAdapter();
        recyclerView.setAdapter(adapter);
        searchButton.setOnClickListener(this);
        itemIdEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_DOWN)) {
                search();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
        AppUtil.forceHideInputMethod(this);
    }

    @Override
    public void onClick(View v) {
        if (v == searchButton) {
            search();
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

    private void search() {
        AppUtil.forceHideInputMethod(this);
        String text = itemIdEditText.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "请输入 Item_ID", Toast.LENGTH_SHORT).show();
            return;
        }
        long itemId;
        try {
            itemId = Long.parseLong(text);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Item_ID 只能是数字", Toast.LENGTH_SHORT).show();
            return;
        }
        if (itemId <= 0) {
            Toast.makeText(this, "Item_ID 必须大于 0", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        final long currentRequestId = ++requestId;
        new QueryTask(currentRequestId).execute(itemId);
    }

    private void setLoading(boolean loading) {
        loadingProgressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        searchButton.setEnabled(!loading);
    }

    private void showResult(List<ItemUsageLineQueryBean> list) {
        adapter.setData(list);
        summaryTextView.setText("共 " + list.size() + " 行");
        if (list.isEmpty()) {
            Toast.makeText(this, "没有查询到物料使用线别", Toast.LENGTH_SHORT).show();
        }
    }

    private class QueryTask extends AsyncTask<Long, Void, QueryResult> {
        private final long currentRequestId;

        QueryTask(long currentRequestId) {
            this.currentRequestId = currentRequestId;
        }

        @Override
        protected QueryResult doInBackground(Long... params) {
            long itemId = params.length > 0 ? params[0] : 0L;
            int buId = UserSingleton.get().getUserInfo().getBu_ID();
            String mpiDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(new Date()) + " 00:00:00.000";

            String sql = String.format(Locale.US,
                    "SELECT CASE WHEN GROUPING(m.wc_id) = 1 THEN N'合计' ELSE w.wc_name END AS 产线 "
                            + ",SUM(CASE WHEN m.PShift_ID = 1 THEN m.MPI_Quantity ELSE 0 END) AS 白班需求数 "
                            + ",SUM(CASE WHEN m.PShift_ID = 2 THEN m.MPI_Quantity ELSE 0 END) AS 夜班需求数 "
                            + ",SUM(m.MPI_Quantity) AS 合计需求数 "
                            + ",COUNT(*) AS 需求计划数 "
                            + "FROM mpi_wc m "
                            + "INNER JOIN abom b ON b.abv_id = m.abv_id AND b.item_id = %d "
                            + "LEFT JOIN p_wc w ON w.wc_id = m.wc_id "
                            + "WHERE m.bu_id = %d AND m.actived = 1 "
                            + "AND m.MPI_Date = '%s' "
                            + "GROUP BY GROUPING SETS ((m.wc_id, w.wc_name),()) "
                            + "ORDER BY GROUPING(m.wc_id) DESC, m.wc_id",
                    itemId, buId, mpiDate);

            WsResult wsResult;
            try {
                wsResult = WebServiceUtil.getDataTable(sql);
            } catch (Exception e) {
                return QueryResult.failure("网络请求失败");
            }
            if (wsResult == null) {
                return QueryResult.failure("网络请求失败");
            }
            if (!wsResult.getResult()) {
                return QueryResult.failure(wsResult.getErrorInfo());
            }
            try {
                Type listType = new TypeToken<List<ItemUsageLineQueryBean>>() {
                }.getType();
                List<ItemUsageLineQueryBean> list =
                        JsonUtil.parseJsonToObject(wsResult.getErrorInfo(), listType);
                return QueryResult.success(list == null
                        ? new ArrayList<ItemUsageLineQueryBean>() : list);
            } catch (Exception e) {
                return QueryResult.failure("查询结果解析失败：" + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(QueryResult result) {
            if (currentRequestId != requestId) {
                return;
            }
            setLoading(false);
            if (result == null) {
                adapter.setData(new ArrayList<ItemUsageLineQueryBean>());
                summaryTextView.setText("查询失败");
                return;
            }
            if (!result.success) {
                adapter.setData(new ArrayList<ItemUsageLineQueryBean>());
                summaryTextView.setText("查询失败");
                Toast.makeText(ItemUsageLineQueryActivity.this,
                        TextUtils.isEmpty(result.message) ? "查询失败" : result.message,
                        Toast.LENGTH_LONG).show();
                return;
            }
            showResult(result.items);
        }
    }

    private static class QueryResult {
        final boolean success;
        final String message;
        final List<ItemUsageLineQueryBean> items;

        private QueryResult(boolean success, String message,
                            List<ItemUsageLineQueryBean> items) {
            this.success = success;
            this.message = message;
            this.items = items;
        }

        static QueryResult success(List<ItemUsageLineQueryBean> items) {
            return new QueryResult(true, "", items);
        }

        static QueryResult failure(String message) {
            return new QueryResult(false, message,
                    new ArrayList<ItemUsageLineQueryBean>());
        }
    }
}
