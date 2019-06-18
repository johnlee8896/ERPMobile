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
import android.widget.EditText;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.chinashb.www.mobileerp.adapter.AdapterPartInv;
import com.chinashb.www.mobileerp.basicobject.Part_Inv;
import com.chinashb.www.mobileerp.basicobject.UserInfoEntity;
import com.chinashb.www.mobileerp.funs.OnItemClickListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StockQueryPartActivity extends AppCompatActivity {

    private UserInfoEntity user;

    private EditText etFilter;
    private Button btnQuery;
    private Button btnQueryNextPage;
    private Button btnQueryPrePage;

    private RecyclerView mRecyclerView;

    private AdapterPartInv partnvadpater;
    private List<Part_Inv> part_invs;

    private Part_Inv selected_item;
    private Integer PageNi=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_query_part);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_query_product_inv);

        etFilter = (EditText)findViewById(R.id.et_stock_query_filter);

        btnQuery = (Button)findViewById(R.id.btn_stock_query);
        btnQueryNextPage =(Button)findViewById(R.id.btn_stock_query_nextpage);
        btnQueryPrePage=(Button)findViewById(R.id.btn_stock_query_prepage);

        user=StockMainActivity.userInfo;

        part_invs =  new ArrayList<>();

        partnvadpater = new AdapterPartInv(StockQueryPartActivity.this, part_invs);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        mRecyclerView.setAdapter(partnvadpater);

        setHomeButton();


        btnQuery.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                PageNi=1;

                StockQueryPartActivity.AsyncQueryProductInv t = new StockQueryPartActivity.AsyncQueryProductInv();
                t.execute();

            }

        });

        btnQueryNextPage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                if(part_invs!=null)
                {
                    if (part_invs.size()>=20)
                    {
                        PageNi++;
                        StockQueryPartActivity.AsyncQueryProductInv t = new StockQueryPartActivity.AsyncQueryProductInv();
                        t.execute();
                    }
                }


            }

        });


        btnQueryPrePage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                if (PageNi>1)
                {
                    PageNi--;
                    StockQueryPartActivity.AsyncQueryProductInv t = new StockQueryPartActivity.AsyncQueryProductInv();
                    t.execute();
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

    private class AsyncQueryProductInv extends AsyncTask<String, Void, Void> {
        //ArrayList<Part_Inv> us = new ArrayList<Part_Inv>();

        @Override
        protected Void doInBackground(String... params) {

            String F=etFilter.getText().toString();

            String js = WebServiceUtil.getQueryInv(user.getBu_ID(),1,F, PageNi, 20);


            Gson gson= new Gson();
            part_invs= gson.fromJson(js,new TypeToken<List<Part_Inv>>(){}.getType());


            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");b

            partnvadpater = new AdapterPartInv(StockQueryPartActivity.this, part_invs);
            mRecyclerView.setAdapter(partnvadpater);


            partnvadpater.setOnItemClickListener(new OnItemClickListener()
            {
               @Override
               public void OnItemClick(View view, int position)
               {
                   if(part_invs!=null)
                   {
                        selected_item= part_invs.get(position);

                       Intent intent = new Intent(StockQueryPartActivity.this, StockQueryPartItemActivity.class);
                       intent.putExtra("selected_item", (Serializable) selected_item);
                       startActivityForResult(intent,100);
                   }

               }
           }
            );
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
//设置为横屏幕
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT )
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        }

        super.onResume();
    }


}
