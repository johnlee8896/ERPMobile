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
import com.chinashb.www.mobileerp.basicobject.PartsEntity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.chinashb.www.mobileerp.adapter.AdapterPartInv;
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
    private AdapterPartInv partsAdpater;
    private List<PartsEntity> partsEntityList;//零部件

    private PartsEntity selected_item;
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_query_part);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_query_product_inv);
        etFilter = (EditText) findViewById(R.id.et_stock_query_filter);
        btnQuery = (Button) findViewById(R.id.btn_stock_query);
        btnQueryNextPage = (Button) findViewById(R.id.btn_stock_query_nextpage);
        btnQueryPrePage = (Button) findViewById(R.id.btn_stock_query_prepage);

        user = UserSingleton.get().getUserInfo();
        partsEntityList = new ArrayList<>();
        partsAdpater = new AdapterPartInv(StockQueryPartActivity.this, partsEntityList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        mRecyclerView.setAdapter(partsAdpater);

        setHomeButton();

        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPage = 1;
                AsyncQueryProductInv t = new AsyncQueryProductInv();
                t.execute();
            }
        });

        btnQueryNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (partsEntityList != null) {
                    if (partsEntityList.size() >= 20) {
                        currentPage++;
                        AsyncQueryProductInv t = new AsyncQueryProductInv();
                        t.execute();
                    }
                }
            }
        });

        btnQueryPrePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage > 1) {
                    currentPage--;
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

    protected void setHomeButton() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private class AsyncQueryProductInv extends AsyncTask<String, Void, Void> {
        //ArrayList<PartsEntity> us = new ArrayList<PartsEntity>();
        @Override
        protected Void doInBackground(String... params) {
            String keyWord = etFilter.getText().toString();
            String js = WebServiceUtil.getQueryInv(user.getBu_ID(), 1, keyWord, currentPage, 20);
            Gson gson = new Gson();
            partsEntityList = gson.fromJson(js, new TypeToken<List<PartsEntity>>() {
            }.getType());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");b
            partsAdpater = new AdapterPartInv(StockQueryPartActivity.this, partsEntityList);
            mRecyclerView.setAdapter(partsAdpater);
            partsAdpater.setOnItemClickListener((view, position) -> {
                        if (partsEntityList != null) {
                            selected_item = partsEntityList.get(position);
                            Intent intent = new Intent(StockQueryPartActivity.this, StockQueryPartItemActivity.class);
                            intent.putExtra("selected_item", (Serializable) selected_item);
                            startActivityForResult(intent, 100);
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
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        super.onResume();
    }


}
