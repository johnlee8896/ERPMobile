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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.MealTypeEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.utils.StaticVariableUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.chinashb.www.mobileerp.adapter.WorkCenterAdapter;
import com.chinashb.www.mobileerp.basicobject.PlanInnerDetailEntity;
import com.chinashb.www.mobileerp.basicobject.MpiWcBean;
import com.chinashb.www.mobileerp.basicobject.UserInfoEntity;
import com.chinashb.www.mobileerp.basicobject.WorkCenter;
import com.chinashb.www.mobileerp.basicobject.s_WCList;
import com.chinashb.www.mobileerp.funs.OnItemClickListener;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SelectMPIWCStepTwoActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private s_WCList selectedWCEntity;
    private WorkCenter selectWorkCenter;
    private ProgressBar pbScan;
    private String displayText;
    public UserInfoEntity userInfo;
    private List<Date> weekDates;
    private List<MealTypeEntity> mealTypes;
    private List<WorkCenter> workCenterList;
    private WorkCenterAdapter wcAdapter;
    private TextView subTitleTextView;
    private MpiWcBean select_mw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mpi_wc_step2);
        subTitleTextView = (TextView) findViewById(R.id.tv_select_wc);
        Intent intent = getIntent();
        selectedWCEntity = (s_WCList) intent.getSerializableExtra("wclist");
        if (selectedWCEntity != null) {
            subTitleTextView.setText(selectedWCEntity.getListName() + "的生产线");
        }

        recyclerView = (RecyclerView) findViewById(R.id.rv_select_wc);
        setHomeButton();

        GetWCAsyncTask t = new GetWCAsyncTask();
        t.execute();

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 1) {
            select_mw = (MpiWcBean) data.getSerializableExtra("mw");

            if (select_mw != null) {

                Intent re = new Intent();
                re.putExtra("mw", select_mw);
                setResult(1, re);
                finish();
            }
        }

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

    private class GetWCAsyncTask extends AsyncTask<String, Void, Void> {
        MpiWcBean mpiWcBean;
        List<PlanInnerDetailEntity> li;
        @Override
        protected Void doInBackground(String... params) {

            String sql = "Select Wi.WC_ID, Wi.List_No,WC_Name From WC_List_Item As Wi Inner Join P_WC As C On Wi.Wc_ID=C.WC_ID Where Wi.LID= " + selectedWCEntity.getLID() + " and c.deleted = 0 Order By Wi.List_No";
            WsResult r = WebServiceUtil.getDataTable(sql);
            if (r != null && r.getResult() ) {
                String js = r.getErrorInfo();
                ArrayList<WorkCenter> workCenterList = new ArrayList<WorkCenter>();
                Gson gson = new Gson();
                workCenterList = gson.fromJson(js, new TypeToken<List<WorkCenter>>() {
                }.getType());
                //wcLists= us;
                SelectMPIWCStepTwoActivity.this.workCenterList = workCenterList;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");
            wcAdapter = new WorkCenterAdapter(SelectMPIWCStepTwoActivity.this, workCenterList);
            recyclerView.setLayoutManager(new LinearLayoutManager(SelectMPIWCStepTwoActivity.this));
            recyclerView.setAdapter(wcAdapter);
            wcAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void OnItemClick(View view, int position) {
                    //Toast.makeText(SelectMPIWCStepTwoActivity.this,position+"",Toast.LENGTH_LONG).show();
                    if (workCenterList != null) {
                        selectWorkCenter = workCenterList.get(position);
                        //保存下来
                        StaticVariableUtils.selectedWorkCenter = selectWorkCenter;
                        goNextStepSelectWM();
                    }
                }
            });

            //直接按前面的选择，显示下一步
            if (StaticVariableUtils.selectedWorkCenter != null) {
                selectWorkCenter = StaticVariableUtils.selectedWorkCenter;
                goNextStepSelectWM();
            }
            //pbScan.setVisibility(View.INVISIBLE);
        }

        protected void goNextStepSelectWM() {
            Intent intent = new Intent(SelectMPIWCStepTwoActivity.this, SelectMPIWCStepThreeActivity.class);
            intent.putExtra("wc", (Serializable) selectWorkCenter);
            startActivityForResult(intent, 100);
        }

        @Override
        protected void onPreExecute() {
            //pbScan.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
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
