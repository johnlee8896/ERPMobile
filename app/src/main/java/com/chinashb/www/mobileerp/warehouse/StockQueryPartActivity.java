package com.chinashb.www.mobileerp.warehouse;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.PartItemMiddleActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.PartInvQueryAdapter;
import com.chinashb.www.mobileerp.basicobject.PartsEntity;
import com.chinashb.www.mobileerp.basicobject.UserInfoEntity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.AppUtil;
import com.chinashb.www.mobileerp.widget.EmptyLayoutManageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockQueryPartActivity extends BaseActivity {

    @BindView(R.id.tv_stock_query_part) TextView tvStockQueryPart;
    @BindView(R.id.et_stock_query_filter) EditText etFilter;
    @BindView(R.id.btn_stock_query) Button btnQuery;
    @BindView(R.id.btn_stock_query_prepage) Button btnQueryPrePage;
    @BindView(R.id.btn_stock_query_nextpage) Button btnQueryNextPage;
    @BindView(R.id.tv_part_inv_item_id_col) TextView tvPartInvItemIdCol;
    @BindView(R.id.tv_part_inv_item_col) TextView tvPartInvItemCol;
    @BindView(R.id.tv_part_inv_name_col) TextView tvPartInvNameCol;
    @BindView(R.id.tv_part_inv_spec_col) TextView tvPartInvSpecCol;
    @BindView(R.id.tv_part_inv_inv_qty_col) TextView tvPartInvInvQtyCol;
    @BindView(R.id.tv_part_inv_unit_col) TextView tvPartInvUnitCol;
    @BindView(R.id.rv_query_product_inv) RecyclerView mRecyclerView;
    @BindView(R.id.part_query_emptyManagerView) EmptyLayoutManageView emptyManagerView;
    @BindView(R.id.part_query_data_layout) LinearLayout dataLayout;

    private UserInfoEntity user;
    private PartInvQueryAdapter partsAdapter;
    private List<PartsEntity> partsEntityList;//零部件

    private PartsEntity selected_item;
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_query_part_layout);
        ButterKnife.bind(this);

//        mRecyclerView = (RecyclerView) findViewById(R.id.rv_query_product_inv);
//        etFilter = (EditText) findViewById(R.id.et_stock_query_filter);
//        btnQuery = (Button) findViewById(R.id.btn_stock_query);
//        btnQueryNextPage = (Button) findViewById(R.id.btn_stock_query_nextpage);
//        btnQueryPrePage = (Button) findViewById(R.id.btn_stock_query_prepage);

        user = UserSingleton.get().getUserInfo();
        partsEntityList = new ArrayList<>();
        partsAdapter = new PartInvQueryAdapter(StockQueryPartActivity.this, partsEntityList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        mRecyclerView.setAdapter(partsAdapter);

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
                    AsyncQueryProductInv t = new AsyncQueryProductInv();
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
        ActionBar actionBar = getSupportActionBar();
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
            if (partsEntityList == null || partsEntityList.size() == 0) {
                dataLayout.setVisibility(View.GONE);
                emptyManagerView.setVisibility(View.VISIBLE);
            }else{
                partsAdapter = new PartInvQueryAdapter(StockQueryPartActivity.this, partsEntityList);
                mRecyclerView.setAdapter(partsAdapter);
                partsAdapter.setOnItemClickListener((view, position) -> {
                            if (partsEntityList != null) {
                                selected_item = partsEntityList.get(position);
//                            QueryPartInvItemAsyncTask task = new QueryPartInvItemAsyncTask();
//                            task.execute(selected_item.getItem_ID());
                                Intent intent = new Intent(StockQueryPartActivity.this, PartItemMiddleActivity.class);
                                intent.putExtra("selected_item", (Serializable) selected_item);
                                startActivityForResult(intent, 100);
                            }
                        }
                );
                dataLayout.setVisibility(View.VISIBLE);
                emptyManagerView.setVisibility(View.GONE);
            }
            AppUtil.forceHideInputMethod(StockQueryPartActivity.this);
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

//    private class QueryPartInvItemAsyncTask extends AsyncTask<Integer, Void, List<Item_Lot_Inv>> {
//
//        @Override
//        protected List<Item_Lot_Inv> doInBackground(Integer... params) {
//            int itemId = params[0];
//            String js = WebServiceUtil.getQueryPartInvItem(UserSingleton.get().getUserInfo().getBu_ID(), itemId);
//            Gson gson = new Gson();
//            List<Item_Lot_Inv> itemLotInvList = gson.fromJson(js, new TypeToken<List<Item_Lot_Inv>>() {
//            }.getType());
//            return itemLotInvList;
//        }
//
//        @Override
//        protected void onPostExecute(List<Item_Lot_Inv> itemLotInvList) {
//            //tv.setText(fahren + "∞ F");
////            partItemAdapter = new ItemPartLotInvAdapter(StockQueryPartActivity.this, itemLotInvList);
////            recyclerView.setAdapter(partItemAdapter);
////            partItemAdapter.setOnItemClickListener(new OnItemClickListener() {
////                                                       @Override
////                                                       public void OnItemClick(View view, int position) {
////                                                           if (itemLotInvList != null) {
////                                                               EditingLot = itemLotInvList.get(position);
////                                                               Intent intent = new Intent(StockQueryPartItemActivity.this, InputBoxActivity.class);
////                                                               intent.putExtra("Title", "输入批次标注，" + EditingLot.getLotNo() + "：");
////                                                               String originalDescription = "";
////                                                               if (EditingLot.getLotDescription() != null) {
////                                                                   originalDescription = EditingLot.getLotDescription();
////                                                               }
////                                                               intent.putExtra("OriText", originalDescription);
////                                                               startActivityForResult(intent, 100);
////                                                           }
////
////                                                       }
////                                                   }
////            );
//            //pbScan.setVisibility(View.INVISIBLE);
//
//            if (itemLotInvList != null && itemLotInvList.size() > 0){
//                int count = 0;
//                HashMap<String,ArrayList<Item_Lot_Inv>> map = new HashMap<>();
//                List<String> stockNumberList = new ArrayList<>();
//                for (Item_Lot_Inv entity : itemLotInvList){
//                    if (!TextUtils.isEmpty(entity.getIstName())){
//                        String[] strArray = entity.getIstName().split("#");
//                        if (strArray.length > 0 && !strArray[0].contains("#")){
//                            if (!stockNumberList.contains(strArray[0])){
//                                stockNumberList.add(strArray[0]);
//                                ArrayList<Item_Lot_Inv> list = new ArrayList<>();
//                                list.add(entity);
//                                map.put(strArray[0],list);
//
//                            }else{
//                                ArrayList<Item_Lot_Inv> arrayList = map.get(strArray[0]);
//                                if (arrayList != null){
//                                    arrayList.add(entity);
//                                }
//                                map.put(strArray[0],arrayList);
//                            }
//                        }
//                    }
//                }
//
//                Intent intent = new Intent(StockQueryPartActivity.this, PartItemMiddleActivity.class);
//                intent.putExtra(IntentConstant.Intent_Part_middle_map,map);
//                intent.putExtra("selected_item", (Serializable) selected_item);
////                startActivityForResult(intent, 100);
//                startActivity(intent);
//
//
////                Intent intent = new Intent(StockQueryPartActivity.this, StockQueryPartItemActivity.class);
////                intent.putExtra("selected_item", (Serializable) selected_item);
////                startActivityForResult(intent, 100);
//            }
//        }
//
//        @Override
//        protected void onPreExecute() {
//            //pbScan.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected void onProgressUpdate(Void... values) {
//        }
//
//    }


    @Override
    protected void onResume() {
//设置为横屏幕
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        super.onResume();
    }


}
