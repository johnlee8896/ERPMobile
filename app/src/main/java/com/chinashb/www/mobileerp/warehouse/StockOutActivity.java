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
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.chinashb.www.mobileerp.adapter.AdapterIssuedItem;
import com.chinashb.www.mobileerp.basicobject.Issued_Item;
import com.chinashb.www.mobileerp.basicobject.Ist_Place;
import com.chinashb.www.mobileerp.basicobject.Mpi_Wc;
import com.chinashb.www.mobileerp.basicobject.WorkCenter;
import com.chinashb.www.mobileerp.basicobject.s_WCList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.chinashb.www.mobileerp.warehouse.StockMainActivity.selected_mws;

public class StockOutActivity extends AppCompatActivity {

    private TextView txtActivityTitle;
    private TextView txtMW;
    private Button btnSelectMWHis            ;
    private Button btnSelectMW;
    private Button btnContinueOut;
    private Button btnContinueOut2;
    private Mpi_Wc themw;

    private RecyclerView mrv_issued_items;

    private AdapterIssuedItem IssueditemAdapter;
    private List<Issued_Item> IssuedItemList;

    //用来保存先前选过的产线组
    public static s_WCList selected_list;
    public static WorkCenter selected_wc;
    private Ist_Place thePlace;
    private String scanstring;

    private List<Mpi_Wc> mws ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bindView();

        setHomeButton();

        setButtonListener();
    }

    protected  void bindView()
    {
        setContentView(R.layout.activity_stock_out);
        mrv_issued_items = (RecyclerView) findViewById(R.id.rv_issed_items);
        txtActivityTitle=(TextView)findViewById(R.id.tv_stock_out_title);
        txtMW= (TextView)findViewById(R.id.tv_mpi_wc_title);
        btnSelectMWHis= (Button)findViewById(R.id.btn_select_mpi_wc_from_selected);
        btnSelectMW = (Button)findViewById(R.id.btn_select_mpiwc);
        btnContinueOut = (Button) findViewById(R.id.btn_continue_stock_out);
        btnContinueOut2 = (Button) findViewById(R.id.btn_continue_stock_out_extra);

        mws=selected_mws;
        IssuedItemList =  new ArrayList<>();
        IssueditemAdapter= new AdapterIssuedItem(StockOutActivity.this, IssuedItemList);
        mrv_issued_items.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        mrv_issued_items.setAdapter(IssueditemAdapter);

    }

    protected void setButtonListener()
    {

        btnSelectMWHis.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                //StockOutActivity.AsyncGetMWItems task = new StockOutActivity.AsyncGetMWItems();
                //task.execute();

                //new IntentIntegrator( StockOutActivity.this).initiateScan();

                if(mws!=null )
                {
                    if (mws.size()>=1)
                    {
                        Intent intent = new Intent(StockOutActivity.this, SelectMPIWCStepThreeActivity.class);
                        intent.putExtra("mws",(Serializable) mws);
                        startActivityForResult(intent,200 );
                    }

                }

            }

        });

        btnSelectMW.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){


                Intent intent = new Intent(StockOutActivity.this, SelectMPIWCStepOneActivity.class);
                intent.putExtra("mw",  themw);
                startActivityForResult(intent,100);

            }

        });


        btnContinueOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){


                if (themw!=null)
                {
                    Intent intent = new Intent(StockOutActivity.this, StockOutMoreActivity.class);
                    intent.putExtra("mw",  themw);
                    intent.putExtra("IssuedItemList", (Serializable) IssuedItemList);
                    startActivityForResult(intent,300);
                }

            }

        });

        btnContinueOut2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                if (themw!=null)
                {
                    Intent intent = new Intent(StockOutActivity.this, StockOutMoreExtraActivity.class);
                    intent.putExtra("mw",  themw);
                    startActivityForResult(intent,400);
                }

            }

        });

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

        if ((requestCode==100 || requestCode==200) && resultCode==1)
        {
            Mpi_Wc select_mw;
            select_mw= (Mpi_Wc) data.getSerializableExtra("mw");

            if(select_mw !=null)
            {
                themw=select_mw;

                if(!Exist_mws())
                {mws.add(themw);}

                txtActivityTitle.setText("投料出库 #" + themw.getMPIWC_ID().toString()) ;
                themw.setMwNameTextView(txtMW);

                StockOutActivity.AsyncShowIssuedMW task = new StockOutActivity.AsyncShowIssuedMW();
                task.execute();
            }
        }

        //投料之后，总刷新已投清单
        if(requestCode==300||requestCode==400)
        {
            StockOutActivity.AsyncShowIssuedMW task = new StockOutActivity.AsyncShowIssuedMW();
            task.execute();
        }

        if(result != null) {
            if(result.getContents() == null) {
                //new IntentIntegrator(StockOutActivity.this).initiateScan();

            } else {

                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

                String X = result.getContents();
                if (! X.equals(""))
                {
                    if(X.startsWith(("MI")))
                    {
                        scanstring= X;

                        StockOutActivity.AsyncGetMW task = new StockOutActivity.AsyncGetMW();
                        task.execute();

                    }
                }
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private Boolean Exist_mws()
    {
        Boolean ex = false ;
        Long ID = themw.getMPIWC_ID();

        for(int i =0 ; i<mws.size();i++)
        {
            Long IDi = mws.get(i).getMPIWC_ID();

            if (ID.equals(IDi))
            {
                ex=true;
            }
        }
        return ex;
    }


    private class AsyncGetMW extends AsyncTask<String, Void, Void> {
        Mpi_Wc scanresult;
        List<Issued_Item> li;

        @Override
        protected Void doInBackground(String... params) {

            Mpi_Wc bi = WebServiceUtil.op_Check_Commit_MW_Barcode(scanstring);


            scanresult=bi;

            if (bi.getResult()==true)
            {
                themw=bi;

                li = WebServiceUtil.op_Get_MW_Issed_Items(bi.getMPIWC_ID());

            }
            else
            {

            }

            return null;
        }



        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");

            if ( scanresult!=null)
            {
                if(scanresult.getResult()==false)
                {
                    Toast.makeText(StockOutActivity.this,scanresult.getErrorInfo(),Toast.LENGTH_LONG).show();
                }
                else
                {
                    txtMW.setText(scanresult.getMwName());

                    IssuedItemList=li;
                    IssueditemAdapter= new AdapterIssuedItem(StockOutActivity.this, IssuedItemList);

                    mrv_issued_items.setAdapter(IssueditemAdapter);
                }


            }



            //pbScan.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPreExecute() {
            //pbScan.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }


    private class AsyncShowIssuedMW extends AsyncTask<String, Void, Void> {
        List<Issued_Item> li;

        @Override
        protected Void doInBackground(String... params) {


            if (themw !=null)
            {

                li = WebServiceUtil.op_Get_MW_Issed_Items(themw.getMPIWC_ID());

            }
            else
            {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");

            if ( li!=null)
            {
                IssuedItemList=li;
                IssueditemAdapter= new AdapterIssuedItem(StockOutActivity.this, IssuedItemList);

                mrv_issued_items.setAdapter(IssueditemAdapter);

            }


            //pbScan.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPreExecute() {
            //pbScan.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    private class AsyncGetMWItems extends AsyncTask<String, Void, List<Issued_Item> > {
        Mpi_Wc scanresult;

        @Override
        protected List<Issued_Item>  doInBackground(String... params) {

            List<Issued_Item> li = WebServiceUtil.op_Get_MW_Issed_Items((long) 471058);


            return li;
        }



        @Override
        protected void onPostExecute(List<Issued_Item>  result) {
            //tv.setText(fahren + "∞ F");

            if ( result!=null)
            {
                IssuedItemList = result;
                IssueditemAdapter= new AdapterIssuedItem(StockOutActivity.this, IssuedItemList);
                mrv_issued_items.setAdapter(IssueditemAdapter);
            }


            //pbScan.setVisibility(View.INVISIBLE);
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
//设置为屏幕
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT )
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );

        }

        super.onResume();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("MWList", (Serializable) mws);

    }
}
