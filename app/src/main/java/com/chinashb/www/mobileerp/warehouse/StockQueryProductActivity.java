package com.chinashb.www.mobileerp.warehouse;

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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.chinashb.www.mobileerp.adapter.AdapterProInv;
import com.chinashb.www.mobileerp.basicobject.Product_Inv;
import com.chinashb.www.mobileerp.basicobject.UserInfoEntity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;

import java.util.ArrayList;
import java.util.List;

public class StockQueryProductActivity extends AppCompatActivity {

    private UserInfoEntity user;

    private EditText etFilter;
    private Button btnQuery;
    private Button btnQueryNextPage;
    private Button btnQueryPrePage;

    private RecyclerView mRecyclerView;

    private AdapterProInv proinvadpater;
    private List<Product_Inv> product_invs;

    private String scanstring;
    private Integer PageNi=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_query_product);


        mRecyclerView = (RecyclerView) findViewById(R.id.rv_query_product_inv);

        etFilter = (EditText)findViewById(R.id.et_stock_query_filter);

        btnQuery = (Button)findViewById(R.id.btn_stock_query);
        btnQueryNextPage =(Button)findViewById(R.id.btn_stock_query_nextpage);
        btnQueryPrePage=(Button)findViewById(R.id.btn_stock_query_prepage);

        user=StockMainActivity.userInfo;

        product_invs =  new ArrayList<>();

        proinvadpater = new AdapterProInv(StockQueryProductActivity.this, product_invs);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        mRecyclerView.setAdapter(proinvadpater);


        setHomeButton();

        btnQuery.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                PageNi=1;

                StockQueryProductActivity.AsyncQueryProductInv t = new StockQueryProductActivity.AsyncQueryProductInv();
                t.execute();

            }

        });

        btnQueryNextPage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                if( product_invs!=null)
                {
                    if (product_invs.size()>=20)
                    {
                        PageNi++;
                        StockQueryProductActivity.AsyncQueryProductInv t = new StockQueryProductActivity.AsyncQueryProductInv();
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
                    StockQueryProductActivity.AsyncQueryProductInv t = new StockQueryProductActivity.AsyncQueryProductInv();
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
        //ArrayList<Product_Inv> us = new ArrayList<Product_Inv>();

        @Override
        protected Void doInBackground(String... params) {

            String F=etFilter.getText().toString();

            String js = WebServiceUtil.getQueryInv(user.getBu_ID(),2,F, PageNi, 20);


            Gson gson= new Gson();
            product_invs= gson.fromJson(js,new TypeToken<List<Product_Inv>>(){}.getType());


            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");

            proinvadpater = new AdapterProInv(StockQueryProductActivity.this, product_invs);
            mRecyclerView.setAdapter(proinvadpater);
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
