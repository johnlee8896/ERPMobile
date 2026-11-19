package com.chinashb.www.mobileerp.logistics;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.LogisticsIncomeCheckAdapter;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.LogisticsIncomeBean;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.AppUtil;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * codex by john 2026-09-10 10:52 - 物流来料检索
 */
public class LogisticsIncomeCheckActivity extends BaseActivity implements View.OnClickListener {
    private Button todayButton;
    private Button weekButton;
    private Button monthButton;
    private Button threeMonthButton;
    private EditText keywordEditText;
    private Button searchButton;
    private CustomRecyclerView recyclerView;
    private LogisticsIncomeCheckAdapter adapter;
    // codex by john 2026-09-10 14:54 - 过程状态筛选
    private Spinner processStatusSpinner;
    private ProgressBar loadingProgressBar;
    private String currentProcessStatus = "在途";
    private List<LogisticsIncomeBean> fullList = new ArrayList<>();

    // codex by john 2026-09-10 14:38 - DateOptionID 映射：12=今日 13=本周 1=本月 10=最近三个月
    private int currentDateOptionID = 12;
    // codex by john 2026-10-27 - 关键字检索默认最近三个月
    private static final int DATE_OPTION_THREE_MONTHS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logistics_income_check_layout);

        todayButton = findViewById(R.id.btn_date_today);
        weekButton = findViewById(R.id.btn_date_week);
        monthButton = findViewById(R.id.btn_date_month);
        threeMonthButton = findViewById(R.id.btn_date_three_month);
        keywordEditText = findViewById(R.id.et_logistics_keyword);
        searchButton = findViewById(R.id.btn_logistics_search);
        recyclerView = findViewById(R.id.rv_logistics_income_check);
        loadingProgressBar = findViewById(R.id.pb_logistics_loading);

        adapter = new LogisticsIncomeCheckAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        todayButton.setOnClickListener(this);
        weekButton.setOnClickListener(this);
        monthButton.setOnClickListener(this);
        threeMonthButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);

        // codex by john 2026-09-10 14:54 - 过程状态筛选
        processStatusSpinner = findViewById(R.id.spinner_process_status);
        final String[] statuses = {"待检验", "待评审", "待入库", "待退货", "在途"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        processStatusSpinner.setAdapter(statusAdapter);
        processStatusSpinner.setSelection(4); // 默认在途
        processStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentProcessStatus = statuses[position];
                filterAndDisplay();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        selectDateOption(12);
    }

    @Override
    public void onClick(View v) {
        if (v == todayButton) {
            selectDateOption(12);
        } else if (v == weekButton) {
            selectDateOption(13);
        } else if (v == monthButton) {
            selectDateOption(1);
        } else if (v == threeMonthButton) {
            selectDateOption(10);
        } else if (v == searchButton) {
            // codex by john 2026-09-10 15:24 - 点击检索后收起输入法
            AppUtil.forceHideInputMethod(this);
            searchByKeyword();
        }
    }

    private void selectDateOption(int optionID) {
        currentDateOptionID = optionID;
        todayButton.setSelected(optionID == 12);
        weekButton.setSelected(optionID == 13);
        monthButton.setSelected(optionID == 1);
        threeMonthButton.setSelected(optionID == 10);
        loadData();
    }

    private void loadData() {
        loadDataInternal(currentDateOptionID, true);
    }

    // codex by john 2026-10-27 - 关键字检索：不按当前时间/状态筛选，固定最近三个月且显示所有状态
    private void searchByKeyword() {
        loadDataInternal(DATE_OPTION_THREE_MONTHS, false);
    }

    private void loadDataInternal(int dateOptionID, boolean filterByStatus) {
        String keys = keywordEditText.getText().toString().trim();
        GetLogisticsInfoAsyncTask task = new GetLogisticsInfoAsyncTask(dateOptionID, filterByStatus);
        task.execute(keys);
    }

    private class GetLogisticsInfoAsyncTask extends AsyncTask<String, Void, String> {
        private final int dateOptionID;
        private final boolean filterByStatus;

        GetLogisticsInfoAsyncTask(int dateOptionID, boolean filterByStatus) {
            this.dateOptionID = dateOptionID;
            this.filterByStatus = filterByStatus;
        }

        @Override
        protected String doInBackground(String... params) {
            String keys = params.length > 0 ? params[0] : "";
            // codex by john 2026-09-10 14:38 - MaterialType=0(供应商来料/默认) OnlyCurrentCompany=true(只查当前车间) SourceID=0(普通)
            WsResult wsResult = WebServiceUtil.getLogisticsInfoByMobile(
                    UserSingleton.get().getUserInfo().getBu_ID(),
                    UserSingleton.get().getHRID(),
                    dateOptionID,
                    0,
                    keys,
                    true,
                    0);
            System.out.println("==== LogisticsIncome result=" + (wsResult != null ? wsResult.getResult() : "null")
                    + " errorInfo=" + (wsResult != null ? wsResult.getErrorInfo() : "null")
                    + " info=" + (wsResult != null ? wsResult.getInfo() : "null"));
            if (wsResult != null && wsResult.getResult()) {
                return wsResult.getErrorInfo();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            loadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String json) {
            loadingProgressBar.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(json) && !json.trim().equals("[]")) {
                Gson gson = new Gson();
                List<LogisticsIncomeBean> list = gson.fromJson(json, new TypeToken<List<LogisticsIncomeBean>>() {
                }.getType());
                fullList = (list != null) ? list : new ArrayList<LogisticsIncomeBean>();
            } else {
                fullList = new ArrayList<>();
            }
            if (filterByStatus) {
                filterAndDisplay();
            } else {
                if (fullList.size() > 0) {
                    adapter.setData(fullList);
                } else {
                    showNoData();
                }
            }
        }
    }

    // codex by john 2026-09-10 14:54 - 按过程状态筛选
    private void filterAndDisplay() {
        List<LogisticsIncomeBean> filtered = new ArrayList<>();
        for (LogisticsIncomeBean bean : fullList) {
            if (TextUtils.equals(bean.getProcessStatus(), currentProcessStatus)) {
                filtered.add(bean);
            }
        }
        if (filtered.size() > 0) {
            adapter.setData(filtered);
        } else {
            showNoData();
        }
    }

    private void showNoData() {
        adapter.setData(new ArrayList<LogisticsIncomeBean>());
        Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
        // codex by john 2026-09-10 16:00 - 进入页面收起输入法
        AppUtil.forceHideInputMethod(this);
    }
}
