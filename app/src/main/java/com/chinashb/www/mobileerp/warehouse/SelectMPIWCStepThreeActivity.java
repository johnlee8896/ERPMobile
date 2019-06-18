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
import com.chinashb.www.mobileerp.basicobject.Ws_Result;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.chinashb.www.mobileerp.adapter.Mpi_WcAdapter;
import com.chinashb.www.mobileerp.basicobject.Mpi_Wc;
import com.chinashb.www.mobileerp.basicobject.WorkCenter;
import com.chinashb.www.mobileerp.funs.OnItemClickListener;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.funs.CommonUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.chinashb.www.mobileerp.warehouse.StockMainActivity.selected_mws;

public class SelectMPIWCStepThreeActivity extends AppCompatActivity {

    String Action="";

    RecyclerView rvSelectMW;

    private WorkCenter select_wc;

    private Mpi_Wc select_mw;

    private TextView tvTitle;

    private List<Mpi_Wc> mws;

    private Mpi_WcAdapter adapter;
    private ProgressBar pbScan;
    private Date showdate= Calendar.getInstance().getTime();

    Button btnPreDay;
    Button btnPreSeven;
    Button btnNextDay;
    Button btnNextSeven;
    TextView txtDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mpi_wc_step3);

        tvTitle=(TextView)findViewById(R.id.tv_select_mpi_wc_show_wc);
        rvSelectMW=(RecyclerView)findViewById(R.id.rv_select_mpi_wc_byday);

        txtDay=(TextView)findViewById(R.id.tv_select_mpi_wc_show_day);

        btnPreDay=(Button)findViewById(R.id.btn_select_mpi_wc_pre_day);
        btnPreSeven =(Button)findViewById(R.id.btn_select_mpi_wc_pre_seven_days);
        btnNextDay=(Button)findViewById(R.id.btn_select_mpi_wc_next_day);
        btnNextSeven =(Button)findViewById(R.id.btn_select_mpi_wc_next_seven_days);

        pbScan=(ProgressBar)findViewById(R.id.progressbar2);

        setHomeButton();

        Intent who = getIntent();

        select_wc= (WorkCenter) who.getSerializableExtra("wc");
        if(select_wc !=null)
        {
            Action="日计划选择";

            action_daily_plan();

        }


        mws=(List<Mpi_Wc>)who.getSerializableExtra("mws");
        if(mws!=null)
        {
            action_existed_plans();
        }


    }

    protected  void action_daily_plan()
    {
        tvTitle.setText(select_wc.getWC_Name() + "的生产计划");

        showplan();

        btnPreDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showdate= CommonUtil.DateAdd(showdate,-1);

                showplan();

            }
        });



        btnPreSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showdate= CommonUtil.DateAdd(showdate,-7);

                showplan();

            }
        });


        btnNextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdate= CommonUtil.DateAdd(showdate,1);

                showplan();

            }
        });

        btnNextSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdate= CommonUtil.DateAdd(showdate,7);

                showplan();

            }
        });
    }

    protected  void action_existed_plans()
    {
        btnPreDay.setEnabled(false);
        btnPreSeven.setEnabled(false);
        btnNextDay.setEnabled(false);
        btnNextSeven.setEnabled(false);

        tvTitle.setText( "已经选过的生产计划");
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

    protected void setHomeButton(){
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    protected  void showplan()
    {
        String r;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        r=sdf.format(showdate);
        txtDay.setText(r);


        SelectMPIWCStepThreeActivity.AsyncGetMW t = new SelectMPIWCStepThreeActivity.AsyncGetMW();
        t.execute();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                //new IntentIntegrator(MainActivity.this).initiateScan();
                //start_scan_HR();
            }
            else
            {

            }
        }
        else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    private class AsyncGetMW extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            String sql="Select M.MPIWC_ID,dbo.get_mw_plan_show_name(mpiwc_ID) As MwName,dbo.get_mw_plan_show_name_html(mpiwc_ID) As HtmlMwName, M.MPI_Remark " +
                    "From MPI_WC As M " +
                    "Where M.Deleted=0 And M.WC_ID=" + select_wc.getWC_ID() + " And MPI_Date=" + CommonUtil.SqlDate(showdate) + " " +
                    " Order By PShift_ID, Shift_No";

            Ws_Result r = WebServiceUtil.getDataTable(sql);

            if(r!=null && r.getResult())
            {
                String js=r.getErrorInfo();

                ArrayList<Mpi_Wc> us = new ArrayList<Mpi_Wc>();
                Gson gson= new Gson();
                us= gson.fromJson(js,new TypeToken<List<Mpi_Wc>>(){}.getType());
                mws= us;
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


    protected  void  show_mws(Boolean showdeletebutton)
    {
        //绑定到不同的计划列表
        if (showdeletebutton==true)
        {
            adapter= new Mpi_WcAdapter( SelectMPIWCStepThreeActivity.this, selected_mws);
            adapter.showdeletebutton=showdeletebutton;

            rvSelectMW.setLayoutManager( new LinearLayoutManager(SelectMPIWCStepThreeActivity.this));
            rvSelectMW.setAdapter(adapter);

            adapter.setOnItemClickListener(new OnItemClickListener(){
                                               @Override
                                               public void OnItemClick(View view, int position)
                                               {
                                                   //Toast.makeText(SelectMPIWCStepTwoActivity.this,position+"",Toast.LENGTH_LONG).show();
                                                   if(selected_mws!=null)
                                                   {
                                                       select_mw= selected_mws.get(position);

                                                       Intent result = new Intent();
                                                       result.putExtra("mw",select_mw);
                                                       setResult(1,result);
                                                       finish();

                                                   }
                                               }
                                           }
            );
        }
        else
        {
            adapter= new Mpi_WcAdapter( SelectMPIWCStepThreeActivity.this, mws);
            adapter.showdeletebutton=showdeletebutton;

            rvSelectMW.setLayoutManager( new LinearLayoutManager(SelectMPIWCStepThreeActivity.this));
            rvSelectMW.setAdapter(adapter);

            adapter.setOnItemClickListener(new OnItemClickListener(){
               @Override
               public void OnItemClick(View view, int position)
               {
                   //Toast.makeText(SelectMPIWCStepTwoActivity.this,position+"",Toast.LENGTH_LONG).show();
                   if(mws!=null)
                   {
                       select_mw= mws.get(position);

                       Intent result = new Intent();
                       result.putExtra("mw",select_mw);
                       setResult(1,result);
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
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT )
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        }

        super.onResume();
    }


}
