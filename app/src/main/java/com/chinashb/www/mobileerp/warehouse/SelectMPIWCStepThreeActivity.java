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

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.BuPlanGoodsActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.StaticVariableUtils;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
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

public class SelectMPIWCStepThreeActivity extends BaseActivity {
    private String action = "";
    private RecyclerView selectMWRecyclerView;
    private WorkCenter selectWorkCenter;
    private MpiWcBean selectMpiWcBean;
    private TextView subTitleTextView;
    private List<MpiWcBean> mpiWcBeanList;
    private Mpi_WcAdapter adapter;
    private ProgressBar pbScan;
    private Date showdate = Calendar.getInstance().getTime();

    private Button preDayButton;
    private Button preSevenButton;
    private Button nextDayButton;
    private Button nextSevenButton;
    private TextView txtDay;
    private boolean bu_option_mp_bom = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mpi_wc_step3_layout);
        subTitleTextView = (TextView) findViewById(R.id.tv_select_mpi_wc_show_wc);
        selectMWRecyclerView = (RecyclerView) findViewById(R.id.rv_select_mpi_wc_byday);
        txtDay = (TextView) findViewById(R.id.tv_select_mpi_wc_show_day);
        preDayButton = (Button) findViewById(R.id.btn_select_mpi_wc_pre_day);
        preSevenButton = (Button) findViewById(R.id.btn_select_mpi_wc_pre_seven_days);
        nextDayButton = (Button) findViewById(R.id.btn_select_mpi_wc_next_day);
        nextSevenButton = (Button) findViewById(R.id.btn_select_mpi_wc_next_seven_days);

        pbScan = (ProgressBar) findViewById(R.id.progressbar2);
        setHomeButton();
        Intent intent = getIntent();
        selectWorkCenter = (WorkCenter) intent.getSerializableExtra("wc");
        if (selectWorkCenter != null) {
            action = "日计划选择";
            initActionDailyPlan();
        }

        mpiWcBeanList = (List<MpiWcBean>) intent.getSerializableExtra("mws");
        if (mpiWcBeanList != null) {
            actionExistedPlans();
        }

    }

    protected void initActionDailyPlan() {
        subTitleTextView.setText(selectWorkCenter.getWC_Name() + "的生产计划");
        showPlan();
        preDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdate = CommonUtil.DateAdd(showdate, -1);
                showPlan();
            }
        });

        preSevenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdate = CommonUtil.DateAdd(showdate, -7);
                showPlan();
            }
        });

        nextDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdate = CommonUtil.DateAdd(showdate, 1);
                showPlan();
            }
        });

        nextSevenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdate = CommonUtil.DateAdd(showdate, 7);
                showPlan();
            }
        });
    }

    protected void actionExistedPlans() {
        preDayButton.setEnabled(false);
        preSevenButton.setEnabled(false);
        nextDayButton.setEnabled(false);
        nextSevenButton.setEnabled(false);

        subTitleTextView.setText("已经选过的生产计划");
        txtDay.setText("");
        showMpiList(true);
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

    protected void showPlan() {
        String dateString;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateString = sdf.format(showdate);
        txtDay.setText(dateString);
        if (!bu_option_mp_bom){
            GetBuOptionAsyncTask task = new GetBuOptionAsyncTask();
            task.execute();
        }else{
            GetMWAsyncTask t = new GetMWAsyncTask();
            t.execute();
        }
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
                    "Where M.Deleted=0 And M.WC_ID=" + selectWorkCenter.getWC_ID() + " And MPI_Date=" + CommonUtil.SqlDate(showdate) + (bu_option_mp_bom  ? " and allow_iss=1" : " ") +
//                    "Where M.Deleted=0 And M.WC_ID=" + selectWorkCenter.getWC_ID() + " And MPI_Date=" + CommonUtil.SqlDate(showdate) + "  and allow_iss=1" +
                    " Order By PShift_ID, Shift_No";
            WsResult result = WebServiceUtil.getDataTable(sql);
            if (result != null && result.getResult()) {
                String js = result.getErrorInfo();
                ArrayList<MpiWcBean> mpiWcBeanList = new ArrayList<MpiWcBean>();
                Gson gson = new Gson();
                mpiWcBeanList = gson.fromJson(js, new TypeToken<List<MpiWcBean>>() {
                }.getType());
                SelectMPIWCStepThreeActivity.this.mpiWcBeanList = mpiWcBeanList;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");
            showMpiList(false);
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

    protected void showMpiList(Boolean showdeletebutton) {
        //绑定到不同的计划列表
        if (showdeletebutton) {
            adapter = new Mpi_WcAdapter(SelectMPIWCStepThreeActivity.this, StaticVariableUtils.selectMpiWcBeanList);
            adapter.showdeletebutton = showdeletebutton;
            selectMWRecyclerView.setLayoutManager(new LinearLayoutManager(SelectMPIWCStepThreeActivity.this));
            selectMWRecyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener((view, position) -> {
                //Toast.makeText(SelectMPIWCStepTwoActivity.this,position+"",Toast.LENGTH_LONG).show();
                if (StaticVariableUtils.selectMpiWcBeanList != null) {
                    selectMpiWcBean = StaticVariableUtils.selectMpiWcBeanList.get(position);
                    Intent intent = new Intent();
                    intent.putExtra("mw", selectMpiWcBean);
                    setResult(1, intent);
                    finish();
                }
            }
            );
        } else {
            adapter = new Mpi_WcAdapter(SelectMPIWCStepThreeActivity.this, mpiWcBeanList);
            adapter.showdeletebutton = showdeletebutton;
            selectMWRecyclerView.setLayoutManager(new LinearLayoutManager(SelectMPIWCStepThreeActivity.this));
            selectMWRecyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(new OnItemClickListener() {
                                               @Override
                                               public void OnItemClick(View view, int position) {
                                                   //Toast.makeText(SelectMPIWCStepTwoActivity.this,position+"",Toast.LENGTH_LONG).show();
                                                   if (mpiWcBeanList != null) {
                                                       selectMpiWcBean = mpiWcBeanList.get(position);
                                                       Intent result = new Intent();
                                                       result.putExtra("mw", selectMpiWcBean);
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


    class GetBuOptionAsyncTask extends AsyncTask<String ,Void ,Void>{

        @Override protected Void doInBackground(String... strings) {
//            String sql = strings[0];
            String sql = "select Option_MP_BOM from bu where bu_id = " + UserSingleton.get().getUserInfo().getBu_ID();
            WsResult result = WebServiceUtil.getDataTable(sql);
            List<OptionMPBoxISSBean> commonDataList = new ArrayList<>();
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                Gson gson = new Gson();
                commonDataList = gson.fromJson(jsonData, new TypeToken<List<OptionMPBoxISSBean>>() {
                }.getType());
                if (commonDataList != null && commonDataList.size() > 0) {
                    if (commonDataList.get(0) instanceof OptionMPBoxISSBean) {
                      bu_option_mp_bom = commonDataList.get(0).optionMPBoM;
                    }
                }
            }
            return null;
        }

        @Override protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            GetMWAsyncTask t = new GetMWAsyncTask();
            t.execute();
        }
    }

    /**
     * 该车间是否有可投料的开关
     */
    private class OptionMPBoxISSBean {
        @SerializedName("Option_MP_BOM") boolean optionMPBoM;

        public boolean isOptionMPBoM() {
            return optionMPBoM;
        }

        public void setOptionMPBoM(boolean optionMPBoM) {
            this.optionMPBoM = optionMPBoM;
        }
    }

}