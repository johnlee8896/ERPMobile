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

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.MealTypeEntity;
import com.chinashb.www.mobileerp.basicobject.Ws_Result;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.chinashb.www.mobileerp.adapter.WorkCenterAdapter;
import com.chinashb.www.mobileerp.basicobject.Issued_Item;
import com.chinashb.www.mobileerp.basicobject.Mpi_Wc;
import com.chinashb.www.mobileerp.basicobject.UserInfoEntity;
import com.chinashb.www.mobileerp.basicobject.WorkCenter;
import com.chinashb.www.mobileerp.basicobject.s_WCList;
import com.chinashb.www.mobileerp.funs.OnItemClickListener;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SelectMPIWCStepTwoActivity extends AppCompatActivity {



    RecyclerView rvSelectWC;

    private s_WCList selected_wclist;
    private WorkCenter selected_wc;

    private ProgressBar pbScan;

    private String displayText;

    public UserInfoEntity userInfo;
    private List<Date> weekDates;
    private List<MealTypeEntity> mealTypes;

    private List<WorkCenter> ws;
    private WorkCenterAdapter wcAdapter;

    private TextView tvWc;

    private Mpi_Wc select_mw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mpi_wc_step2);

        tvWc=(TextView)findViewById(R.id.tv_select_wc);

        Intent who = getIntent();
        selected_wclist= (s_WCList) who.getSerializableExtra("wclist");
        if(selected_wclist !=null)
        {
            tvWc.setText(selected_wclist.getListName() + "的生产线");
        }

        rvSelectWC =(RecyclerView)findViewById(R.id.rv_select_wc);

        setHomeButton();


        SelectMPIWCStepTwoActivity.AsyncGetWC t = new SelectMPIWCStepTwoActivity.AsyncGetWC();
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

    protected void setHomeButton(){
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (requestCode==100 && resultCode==1)
        {
            select_mw= (Mpi_Wc) data.getSerializableExtra("mw");

            if(select_mw !=null)
            {

                Intent re = new Intent();
                re.putExtra("mw",select_mw);
                setResult(1,re);
                finish();
            }
        }

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



    private class AsyncGetWC extends AsyncTask<String, Void, Void> {
        Mpi_Wc scanresult;
        List<Issued_Item> li;

        @Override
        protected Void doInBackground(String... params) {

            String sql="Select Wi.WC_ID, Wi.List_No,WC_Name From WC_List_Item As Wi Inner Join P_WC As C On Wi.Wc_ID=C.WC_ID Where Wi.LID= " + selected_wclist.getLID() + " Order By Wi.List_No";

            Ws_Result r = WebServiceUtil.getDataTable(sql);

            if(r!=null && r.getResult()==true)
            {
                String js=r.getErrorInfo();
                ArrayList<WorkCenter> us = new ArrayList<WorkCenter>();
                Gson gson= new Gson();
                us= gson.fromJson(js,new TypeToken<List<WorkCenter>>(){}.getType());


                //wcLists= us;
                ws= us;

            }
                        return null;
        }



        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");

            wcAdapter= new WorkCenterAdapter( SelectMPIWCStepTwoActivity.this, ws);

            rvSelectWC.setLayoutManager( new LinearLayoutManager(SelectMPIWCStepTwoActivity.this));
            rvSelectWC.setAdapter(wcAdapter);

            wcAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void OnItemClick(View view, int position) {
                    //Toast.makeText(SelectMPIWCStepTwoActivity.this,position+"",Toast.LENGTH_LONG).show();
                    if(ws!=null)
                    {selected_wc= ws.get(position);
                        //保存下来
                        StockOutActivity.selected_wc =selected_wc;

                        NextStep_SelectWM();
                    }
                }
            });

            //直接按前面的选择，显示下一步
            if(StockOutActivity.selected_wc!=null)
            {
                selected_wc =StockOutActivity.selected_wc;
                NextStep_SelectWM();
            }
            //pbScan.setVisibility(View.INVISIBLE);
        }

        protected  void NextStep_SelectWM()
        {
            Intent intent = new Intent(SelectMPIWCStepTwoActivity.this, SelectMPIWCStepThreeActivity.class);
            intent.putExtra("wc", (Serializable) selected_wc);
            startActivityForResult(intent,100);
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
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT )
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        }

        super.onResume();
    }


}
