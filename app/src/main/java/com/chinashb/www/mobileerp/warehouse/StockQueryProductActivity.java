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
import com.chinashb.www.mobileerp.funs.OnItemClickListener;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.chinashb.www.mobileerp.adapter.AdapterProInv;
import com.chinashb.www.mobileerp.basicobject.ProductsEntity;
import com.chinashb.www.mobileerp.basicobject.UserInfoEntity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StockQueryProductActivity extends AppCompatActivity {

    private UserInfoEntity user;
    private EditText etFilter;
    private Button btnQuery;
    private Button btnQueryNextPage;
    private Button btnQueryPrePage;
    private RecyclerView mRecyclerView;

    private AdapterProInv productAdapter;
    private List<ProductsEntity> productsEntityList;

    private String scanstring;
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_query_product);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_query_product_inv);
        etFilter = (EditText) findViewById(R.id.et_stock_query_filter);
        btnQuery = (Button) findViewById(R.id.btn_stock_query);
        btnQueryNextPage = (Button) findViewById(R.id.btn_stock_query_nextpage);
        btnQueryPrePage = (Button) findViewById(R.id.btn_stock_query_prepage);

        user = UserSingleton.get().getUserInfo();
        productsEntityList = new ArrayList<>();
        productAdapter = new AdapterProInv(StockQueryProductActivity.this, productsEntityList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        mRecyclerView.setAdapter(productAdapter);

//        productAdapter.setOnItemClickListener((view, position) -> {
//                    if (productsEntityList != null && productsEntityList.size() > 0) {
//                        Intent intent = new Intent(StockQueryProductActivity.this, StockQueryProductItemActivity.class);
//                        intent.putExtra("selected_item", (Serializable) productsEntityList.get(position));
//                        startActivityForResult(intent, 100);
//                    }
////                    ToastUtil.showToastLong("正在开发！");
//
//                }
//        );

        setHomeButton();
        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPage = 1;
                QueryProductStockAsyncTask t = new QueryProductStockAsyncTask();
                t.execute();

            }

        });

        btnQueryNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productsEntityList != null) {
                    if (productsEntityList.size() >= 20) {
                        currentPage++;
                        QueryProductStockAsyncTask t = new QueryProductStockAsyncTask();
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
                    QueryProductStockAsyncTask t = new QueryProductStockAsyncTask();
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

    private class QueryProductStockAsyncTask extends AsyncTask<String, Void, Void> {
        //ArrayList<ProductsEntity> us = new ArrayList<ProductsEntity>();

        @Override
        protected Void doInBackground(String... params) {

            String keyWord = etFilter.getText().toString();
            String js = WebServiceUtil.getQueryInv(user.getBu_ID(), 2, keyWord, currentPage, 20);
            Gson gson = new Gson();
            productsEntityList = gson.fromJson(js, new TypeToken<List<ProductsEntity>>() {
            }.getType());

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");

            productAdapter = new AdapterProInv(StockQueryProductActivity.this, productsEntityList);
            mRecyclerView.setAdapter(productAdapter);
            productAdapter.setOnItemClickListener((view, position) -> {
                        if (productsEntityList != null && productsEntityList.size() > 0) {
                            Intent intent = new Intent(StockQueryProductActivity.this, StockQueryProductItemActivity.class);
                            intent.putExtra("selected_item", (Serializable) productsEntityList.get(position));
                            startActivityForResult(intent, 100);
                        }
//                    ToastUtil.showToastLong("正在开发！");

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
