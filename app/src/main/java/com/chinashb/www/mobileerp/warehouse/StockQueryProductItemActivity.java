package com.chinashb.www.mobileerp.warehouse;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.ProductLotInvAdapter;
import com.chinashb.www.mobileerp.basicobject.ProductsEntity;
import com.chinashb.www.mobileerp.basicobject.UserInfoEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.ProductPackBean;
import com.chinashb.www.mobileerp.commonactivity.InputBoxActivity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.widget.CommProgressDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

/***
 * @date 创建时间 2019/6/28 8:37
 * @author 作者: xxblwf
 * @description 成品库查询item详情
 */

public class StockQueryProductItemActivity extends BaseActivity implements View.OnClickListener {

    private UserInfoEntity user;

    private RecyclerView recyclerView;
    //    private RecyclerView unPalletRecyclerView;
    private ProductLotInvAdapter palletProductItemAdpater;
    //    private AdapterItemProductLotInv unPalletProductItemAdpater;
    //    private PartsEntity selected_item;
    private ProductsEntity selected_item;
    //    private List<Item_Lot_Inv> productItemBeanList;
//    private List<ProductItemBean> productItemBeanList;
    private List<ProductPackBean> productItemBeanList;
    private TextView txtItem;
    //    private ProductItemBean selectInnerItem;
    private ProductPackBean selectInnerItem;
    private String NewLotDes = "";
    private Button packButton, unPackButton;
    private boolean isUnPack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_stock_query_part_item);
        setContentView(R.layout.activity_stock_query_product_item);

        recyclerView = (RecyclerView) findViewById(R.id.rv_query_product_product_inv);
//        unPalletRecyclerView = (RecyclerView) findViewById(R.id.rv_query_product_product_un_pallet_inv);
        txtItem = (TextView) findViewById(R.id.tv_stock_query_product_current_item);

        packButton = findViewById(R.id.package_product_Button);
        unPackButton = findViewById(R.id.unpackage_product_Button);
        user = UserSingleton.get().getUserInfo();
        Intent intent = getIntent();
        selected_item = (ProductsEntity) intent.getSerializableExtra("selected_item");

        if (selected_item != null) {
//            txtItem.setText(String.valueOf(selected_item.getItem_ID()) + " " + selected_item.getItem()
//                    + " " + selected_item.getItem_Name() + " " + selected_item.getItem_Spec2() + " ");

            txtItem.setText(String.valueOf(selected_item.getItem_ID()) + " "
                    + " " + selected_item.get名称() + " ");

            refreshProductInvList();
        }

        palletProductItemAdpater = new ProductLotInvAdapter(StockQueryProductItemActivity.this, productItemBeanList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        recyclerView.setAdapter(palletProductItemAdpater);

        packButton.setOnClickListener(this);
        unPackButton.setOnClickListener(this);
        packButton.setEnabled(false);

//        unPalletProductItemAdpater = new AdapterItemProductLotInv(StockQueryProductItemActivity.this, productItemBeanList);
//        unPalletRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
//        unPalletRecyclerView.setAdapter(unPalletProductItemAdpater);


    }

    @Override
    protected void onResume() {
//设置为横屏幕
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        super.onResume();
    }

    private void refreshProductInvList() {
        AsyncQueryProductInvItem asyncQueryPartInvItem = new AsyncQueryProductInvItem();
        asyncQueryPartInvItem.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 1) {
            String Input = data.getStringExtra("Input");
            if (Input.isEmpty() || Input.equals("null")) {
                Input = "";
            }
            if (selectInnerItem != null) {
                NewLotDes = Input;
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

    @Override
    public void onClick(View view) {
        if (view == packButton) {
            isUnPack = false;
            refreshProductInvList();
            ;
            packButton.setEnabled(false);
            unPackButton.setEnabled(true);
        } else if (view == unPackButton) {
            isUnPack = true;
            refreshProductInvList();
            ;
            packButton.setEnabled(true);
            unPackButton.setEnabled(false);
        }
    }

    private class AsyncQueryProductInvItem extends AsyncTask<String, Void, Void> {
        private CommProgressDialog progressDialog;
        @Override
        protected Void doInBackground(String... params) {
            String sql = "select ";
//            String js = WebServiceUtil.getQueryPartInvItem(user.getBu_ID(), selected_item.getItem_ID());
            String js = "";
            if (isUnPack) {
                js = WebServiceUtil.getQueryProductUNPackInv(user.getBu_ID(), selected_item.get客户零件编号());
            } else {
                js = WebServiceUtil.getQueryProductPackInv(user.getBu_ID(), selected_item.get客户零件编号());
            }
            Gson gson = new Gson();
//            productItemBeanList = gson.fromJson(js, new TypeToken<List<Item_Lot_Inv>>() {}.getType());
            productItemBeanList = gson.fromJson(js, new TypeToken<List<ProductPackBean>>() {
            }.getType());

            return null;
        }

        @Override
        protected void onPreExecute() {
            //pbScan.setVisibility(View.VISIBLE);
            progressDialog = new CommProgressDialog.Builder(StockQueryProductItemActivity.this)
                    .setTitle("正在查询..").create();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");

            palletProductItemAdpater = new ProductLotInvAdapter(StockQueryProductItemActivity.this, productItemBeanList);
            palletProductItemAdpater.setUnPack(isUnPack);
            recyclerView.setAdapter(palletProductItemAdpater);
            palletProductItemAdpater.setOnItemClickListener((view, position) -> {
                        if (productItemBeanList != null) {
                            selectInnerItem = productItemBeanList.get(position);
                            Intent intent = new Intent(StockQueryProductItemActivity.this, InputBoxActivity.class);
                            intent.putExtra("Title", "输入批次标注，" + selectInnerItem.get标注() + "：");
                            String OldDes = "";
                            if (selectInnerItem.get标注() != null) {
                                OldDes = selectInnerItem.get标注();
                            }

                            intent.putExtra("OriText", OldDes);
                            startActivityForResult(intent, 100);
                        }

                    }
            );
            progressDialog.dismiss();

//            unPalletProductItemAdpater = new AdapterItemProductLotInv(StockQueryProductItemActivity.this, productItemBeanList);
//            unPalletRecyclerView.setAdapter(palletProductItemAdpater);
//            unPalletProductItemAdpater.setOnItemClickListener((view, position) -> {
//                        if (productItemBeanList != null) {
//                            selectInnerItem = productItemBeanList.get(position);
//                            Intent intent = new Intent(StockQueryProductItemActivity.this, InputBoxActivity.class);
//                            intent.putExtra("Title", "输入批次标注，" + selectInnerItem.get标注() + "：");
//                            String OldDes = "";
//                            if (selectInnerItem.get标注() != null) {
//                                OldDes = selectInnerItem.get标注();
//                            }
//
//                            intent.putExtra("OriText", OldDes);
//                            startActivityForResult(intent, 100);
//                        }
//
//                    }
//            );

//            ListViewUtility.setListViewHeightBasedOnChildren(unPalletRecyclerView);
//
//            ListViewUtility.setListViewHeightBasedOnChildren(palletRecyclerView);
            //pbScan.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    private class AsyncUpdateLotDescription extends AsyncTask<String, Void, Void> {
        WsResult ws_result;

        @Override
        protected Void doInBackground(String... params) {

            if (selectInnerItem != null) {
                ws_result = WebServiceUtil.op_Commit_Update_Lot_Description(UserSingleton.get().getHRID(), selectInnerItem.getLot_ID(), NewLotDes);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            //pbScan.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {

            if (ws_result != null) {
                if (ws_result.getResult()) {
                    selectInnerItem.set标注(NewLotDes);
                    palletProductItemAdpater.notifyDataSetChanged();
                }

            }


            //pbScan.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }


}
