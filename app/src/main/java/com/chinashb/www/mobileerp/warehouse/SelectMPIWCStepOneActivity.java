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

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.chinashb.www.mobileerp.adapter.WCListAdapter;
import com.chinashb.www.mobileerp.basicobject.Issued_Item;
import com.chinashb.www.mobileerp.basicobject.Mpi_Wc;
import com.chinashb.www.mobileerp.basicobject.s_WCList;
import com.chinashb.www.mobileerp.funs.OnItemClickListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SelectMPIWCStepOneActivity extends AppCompatActivity {

    RecyclerView rvSelectWCList;

    private List<s_WCList> wcLists;
    private WCListAdapter wclAdapter;

    private s_WCList selected_list;
    private Mpi_Wc select_mw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mpi_wc_step1);

        rvSelectWCList =(RecyclerView)findViewById(R.id.rv_select_wc_list);

        setHomeButton();

        AsyncGetWCLists t = new AsyncGetWCLists();
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
                //startScanHR();
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




    private class AsyncGetWCLists extends AsyncTask<String, Void, Void> {
        Mpi_Wc scanresult;
        List<Issued_Item> li;

        @Override
        protected Void doInBackground(String... params) {

            String sql="Select Case When Ac_Type=1 Then '部件' When Ac_Type=2 Then '成品' Else '' End As WCL_Type, LID, Bu_ID, ListName From WC_List Where Bu_ID="
                    + StockMainActivity.userInfo.getBu_ID() + " Order By Case When Ac_Type=1 Then '部件' When Ac_Type=2 Then '成品' Else '' End";


            WsResult r = WebServiceUtil.getDataTable(sql);

            if(r!=null && r.getResult()==true)
            {
                String js=r.getErrorInfo();
                ArrayList<s_WCList> us = new ArrayList<s_WCList>();
                Gson gson= new Gson();
                us= gson.fromJson(js,new TypeToken<List<s_WCList>>(){}.getType());

                wcLists= us;

            }

            return null;
        }



        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");

            wclAdapter= new WCListAdapter( SelectMPIWCStepOneActivity.this, wcLists);
            rvSelectWCList.setLayoutManager( new LinearLayoutManager(SelectMPIWCStepOneActivity.this));
            rvSelectWCList.setAdapter(wclAdapter);

            wclAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void OnItemClick(View view, int position) {

                    selected_list= wcLists.get(position);

                    //存下来,重选了wcs_list, 需要清除已选生产线
                    StockOutActivity.selected_list=selected_list;
                    StockOutActivity.selected_wc=null;

                    NextStep_SelectWc();

                }
            });

            //如果出库主页上的生产线组曾选过，就直接往下点，模拟到下一步
            if(StockOutActivity.selected_list !=null)
            {
                selected_list = StockOutActivity.selected_list;
                NextStep_SelectWc();
            }

            //pbScan.setVisibility(View.INVISIBLE);
        }

        protected void NextStep_SelectWc()
        {
            Intent intent = new Intent(SelectMPIWCStepOneActivity.this, SelectMPIWCStepTwoActivity.class);
            intent.putExtra("wclist", (Serializable) selected_list);
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
