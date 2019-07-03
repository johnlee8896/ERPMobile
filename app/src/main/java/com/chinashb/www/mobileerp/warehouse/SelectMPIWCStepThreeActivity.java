package com.chinashb.www.mobileerp.warehouse;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.utils.StaticVariableUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.chinashb.www.mobileerp.adapter.Mpi_WcAdapter;
import com.chinashb.www.mobileerp.basicobject.MpiWcBean;
import com.chinashb.www.mobileerp.basicobject.WorkCenter;
import com.chinashb.www.mobileerp.funs.OnItemClickListener;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SelectMPIWCStepThreeActivity extends AppCompatActivity {
    private String action = "";
    private RecyclerView selectMWRecyclerView;
    private WorkCenter select_wc;
    private MpiWcBean select_mw;
    private TextView tvTitle;
    private List<MpiWcBean> mws;
    private Mpi_WcAdapter adapter;
    private ProgressBar pbScan;
    private Date showdate = Calendar.getInstance().getTime();

    private Button preDayButton;
    private Button preSevenButton;
    private Button nextDayButton;
    private Button nextSevenButton;
    private TextView txtDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mpi_wc_step3);
        tvTitle = (TextView) findViewById(R.id.tv_select_mpi_wc_show_wc);
        selectMWRecyclerView = (RecyclerView) findViewById(R.id.rv_select_mpi_wc_byday);
        txtDay = (TextView) findViewById(R.id.tv_select_mpi_wc_show_day);
        preDayButton = (Button) findViewById(R.id.btn_select_mpi_wc_pre_day);
        preSevenButton = (Button) findViewById(R.id.btn_select_mpi_wc_pre_seven_days);
        nextDayButton = (Button) findViewById(R.id.btn_select_mpi_wc_next_day);
        nextSevenButton = (Button) findViewById(R.id.btn_select_mpi_wc_next_seven_days);

        pbScan = (ProgressBar) findViewById(R.id.progressbar2);
        setHomeButton();
        Intent intent = getIntent();
        select_wc = (WorkCenter) intent.getSerializableExtra("wc");
        if (select_wc != null) {
            action = "日计划选择";
            initActionDailyPlan();
        }

        mws = (List<MpiWcBean>) intent.getSerializableExtra("mws");
        if (mws != null) {
            actionExistedPlans();
        }

    }

    protected void initActionDailyPlan() {
        tvTitle.setText(select_wc.getWC_Name() + "的生产计划");
        showplan();
        preDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdate = CommonUtil.DateAdd(showdate, -1);
                showplan();
            }
        });

        preSevenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdate = CommonUtil.DateAdd(showdate, -7);
                showplan();
            }
        });

        nextDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdate = CommonUtil.DateAdd(showdate, 1);
                showplan();
            }
        });

        nextSevenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdate = CommonUtil.DateAdd(showdate, 7);
                showplan();
            }
        });
    }

    protected void actionExistedPlans() {
        preDayButton.setEnabled(false);
        preSevenButton.setEnabled(false);
        nextDayButton.setEnabled(false);
        nextSevenButton.setEnabled(false);

        tvTitle.setText("已经选过的生产计划");
        txtDay.setText("");
        show_mws(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
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

    protected void showplan() {
        String dateString;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateString = sdf.format(showdate);
        txtDay.setText(dateString);
        GetMWAsyncTask t = new GetMWAsyncTask();
        t.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                //new IntentIntegrator(MainActivity.this).initiateScan();
                //startScanHR();
            } else {

            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class GetMWAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String sql = "Select M.MPIWC_ID,dbo.get_mw_plan_show_name(mpiwc_ID) As MwName,dbo.get_mw_plan_show_name_html(mpiwc_ID) As HtmlMwName, M.MPI_Remark " +
                    "From MPI_WC As M " +
                    "Where M.Deleted=0 And M.WC_ID=" + select_wc.getWC_ID() + " And MPI_Date=" + CommonUtil.SqlDate(showdate) + " " +
                    " Order By PShift_ID, Shift_No";
            WsResult result = WebServiceUtil.getDataTable(sql);
            if (result != null && result.getResult()) {
                String js = result.getErrorInfo();
                ArrayList<MpiWcBean> us = new ArrayList<MpiWcBean>();
                Gson gson = new Gson();
                us = gson.fromJson(js, new TypeToken<List<MpiWcBean>>() {
                }.getType());
                mws = us;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");
            show_mws(false);
            pbScan.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPreExecute() {
            pbScan.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    protected void show_mws(Boolean showdeletebutton) {
        //绑定到不同的计划列表
        if (showdeletebutton) {
            adapter = new Mpi_WcAdapter(SelectMPIWCStepThreeActivity.this, StaticVariableUtils.selected_mws);
            adapter.showdeletebutton = showdeletebutton;
            selectMWRecyclerView.setLayoutManager(new LinearLayoutManager(SelectMPIWCStepThreeActivity.this));
            selectMWRecyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(new OnItemClickListener() {
                                               @Override
                                               public void OnItemClick(View view, int position) {
                                                   //Toast.makeText(SelectMPIWCStepTwoActivity.this,position+"",Toast.LENGTH_LONG).show();
                                                   if (StaticVariableUtils.selected_mws != null) {
                                                       select_mw = StaticVariableUtils.selected_mws.get(position);
                                                       Intent intent = new Intent();
                                                       intent.putExtra("mw", select_mw);
                                                       setResult(1, intent);
                                                       finish();
                                                   }
                                               }
                                           }
            );
        } else {
            adapter = new Mpi_WcAdapter(SelectMPIWCStepThreeActivity.this, mws);
            adapter.showdeletebutton = showdeletebutton;
            selectMWRecyclerView.setLayoutManager(new LinearLayoutManager(SelectMPIWCStepThreeActivity.this));
            selectMWRecyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(new OnItemClickListener() {
                                               @Override
                                               public void OnItemClick(View view, int position) {
                                                   //Toast.makeText(SelectMPIWCStepTwoActivity.this,position+"",Toast.LENGTH_LONG).show();
                                                   if (mws != null) {
                                                       select_mw = mws.get(position);
                                                       Intent result = new Intent();
                                                       result.putExtra("mw", select_mw);
                                                       setResult(1, result);
                                                       finish();
                                                   }
                                               }
                                           }
            );
        }
    }

    @Override
    protected void onResume() {
        //设置为竖屏幕
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }

}