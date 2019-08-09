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
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.PartsEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.commonactivity.InputBoxActivity;
import com.chinashb.www.mobileerp.funs.OnItemClickListener;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.chinashb.www.mobileerp.adapter.ItemPartLotInvAdapter;
import com.chinashb.www.mobileerp.basicobject.Item_Lot_Inv;
import com.chinashb.www.mobileerp.basicobject.UserInfoEntity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

public class StockQueryPartItemActivity extends AppCompatActivity {

    private UserInfoEntity userInfoEntity;
    private RecyclerView recyclerView;
    private ItemPartLotInvAdapter partItemAdapter;
    private PartsEntity selected_item;
    private List<Item_Lot_Inv> itemLotInvList;
    private TextView titleNameTextView;

    private Item_Lot_Inv EditingLot;
    private String description = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_query_part_item);

        recyclerView = (RecyclerView) findViewById(R.id.rv_query_product_inv);
        titleNameTextView = (TextView) findViewById(R.id.tv_stock_query_current_item);
        userInfoEntity = UserSingleton.get().getUserInfo();
        Intent intent = getIntent();
//        selected_item = (PartsEntity) intent.getSerializableExtra("selected_item");





        initData();

        setHomeButton();
    }

    private void initData() {
        itemLotInvList = (List<Item_Lot_Inv>) getIntent().getSerializableExtra(IntentConstant.Intent_Part_middle_map_list);

        if (selected_item != null) {
            titleNameTextView.setText(String.valueOf(selected_item.getItem_ID()) + " " + selected_item.getItem()
                    + " " + selected_item.getItem_Name() + " " + selected_item.getItem_Spec2() + " ");

//            QueryPartInvItemAsyncTask queryPartInvItemAsyncTask = new QueryPartInvItemAsyncTask();
//            queryPartInvItemAsyncTask.execute();
        }

        partItemAdapter = new ItemPartLotInvAdapter(StockQueryPartItemActivity.this, itemLotInvList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        recyclerView.setAdapter(partItemAdapter);
//        partItemAdapter = new ItemPartLotInvAdapter(StockQueryPartItemActivity.this, itemLotInvList);
//        recyclerView.setAdapter(partItemAdapter);
        partItemAdapter.setOnItemClickListener(new OnItemClickListener() {
                                                   @Override
                                                   public void OnItemClick(View view, int position) {
                                                       if (itemLotInvList != null) {
                                                           EditingLot = itemLotInvList.get(position);
                                                           Intent intent = new Intent(StockQueryPartItemActivity.this, InputBoxActivity.class);
                                                           intent.putExtra("Title", "输入批次标注，" + EditingLot.getLotNo() + "：");
                                                           String originalDescription = "";
                                                           if (EditingLot.getLotDescription() != null) {
                                                               originalDescription = EditingLot.getLotDescription();
                                                           }
                                                           intent.putExtra("OriText", originalDescription);
                                                           startActivityForResult(intent, 100);
                                                       }

                                                   }
                                               }
        );
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 1) {
            String Input = data.getStringExtra("Input");
            if (Input.isEmpty() || Input.equals("null")) {
                Input = "";
            }
            if (EditingLot != null) {
                description = Input;
                AsyncUpdateLotDescription t = new AsyncUpdateLotDescription();
                t.execute();
            }

        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
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

//    private class QueryPartInvItemAsyncTask extends AsyncTask<String, Void, Void> {
//        @Override
//        protected Void doInBackground(String... params) {
//            String sql = "select ";
//            String js = WebServiceUtil.getQueryPartInvItem(userInfoEntity.getBu_ID(), selected_item.getItem_ID());
//            Gson gson = new Gson();
//            itemLotInvList = gson.fromJson(js, new TypeToken<List<Item_Lot_Inv>>() {
//            }.getType());
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            //tv.setText(fahren + "∞ F");
//            partItemAdapter = new ItemPartLotInvAdapter(StockQueryPartItemActivity.this, itemLotInvList);
//            recyclerView.setAdapter(partItemAdapter);
//            partItemAdapter.setOnItemClickListener(new OnItemClickListener() {
//                                                       @Override
//                                                       public void OnItemClick(View view, int position) {
//                                                           if (itemLotInvList != null) {
//                                                               EditingLot = itemLotInvList.get(position);
//                                                               Intent intent = new Intent(StockQueryPartItemActivity.this, InputBoxActivity.class);
//                                                               intent.putExtra("Title", "输入批次标注，" + EditingLot.getLotNo() + "：");
//                                                               String originalDescription = "";
//                                                               if (EditingLot.getLotDescription() != null) {
//                                                                   originalDescription = EditingLot.getLotDescription();
//                                                               }
//                                                               intent.putExtra("OriText", originalDescription);
//                                                               startActivityForResult(intent, 100);
//                                                           }
//
//                                                       }
//                                                   }
//            );
//            //pbScan.setVisibility(View.INVISIBLE);
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


    private class AsyncUpdateLotDescription extends AsyncTask<String, Void, Void> {
        WsResult ws_result;

        @Override
        protected Void doInBackground(String... params) {
            if (EditingLot != null) {
                ws_result = WebServiceUtil.op_Commit_Update_Lot_Description(UserSingleton.get().getHRID(), EditingLot.getLotID(), description);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (ws_result != null) {
                if (ws_result.getResult()) {
                    EditingLot.setLotDescription(description);
                    partItemAdapter.notifyDataSetChanged();
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

    @Override
    protected void onResume() {
        //设置为横屏幕
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        super.onResume();
    }


}
