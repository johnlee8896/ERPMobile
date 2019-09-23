package com.chinashb.www.mobileerp.warehouse;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.QueryAsyncTask;
import com.chinashb.www.mobileerp.basicobject.UserInfoEntity;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.OnLoadDataListener;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.talk.ShbTcpTest;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;

public class StockPartMainActivity extends BaseActivity implements View.OnClickListener {
    RecyclerView mRecyclerView;
    private TextView tvTitle;
    private TextView tvusername;
    private Button scanToStockButton;//扫描入库
    private Button moveStockAreaButton;//移动库位
    private Button freezeStockButton;//冻结库存
    private Button productSupplyButton;//生产投料
    private Button returnWCButton;//返工出库
    private Button departMentInButton;//部门领料
    private Button partStockCheckButton;//零部件盘点
    private Button makingProductButton;//在制品盘存
    private Button selfProductButton;//自制车间成品盘点
    private Button completeProductButton;//成品库存
    private Button partStockInButton;//零部件库存
    private Button lookQRButton;

    private FloatingActionButton floatButton;
    //    private ProgressBar pbScan;
    private UserInfoEntity userInfo;
    public static Bitmap userpic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_part_main_layout);
        tvTitle = (TextView) findViewById(R.id.tv_stock_system_title);

        scanToStockButton = (Button) findViewById(R.id.btn_scan_to_stock_in);
        productSupplyButton = (Button) findViewById(R.id.btn_product_supply);
        moveStockAreaButton = (Button) findViewById(R.id.btn_move_stock_area);
        freezeStockButton = (Button) findViewById(R.id.btn_freeze_inv);
        returnWCButton = (Button) findViewById(R.id.btn_product_return);
        departMentInButton = (Button) findViewById(R.id.btn_stock_out_dep);
        lookQRButton = (Button) findViewById(R.id.look_qr_button);
        makingProductButton = (Button) findViewById(R.id.btn_check_inv_worksite);
        partStockCheckButton = (Button) findViewById(R.id.btn_check_part_inv);
        selfProductButton = (Button) findViewById(R.id.btn_check_pro_part_inv);
        completeProductButton = (Button) findViewById(R.id.btn_query_product_inv);
        partStockInButton = (Button) findViewById(R.id.btn_query_part_inv);

//        pbScan = (ProgressBar) findViewById(R.id.pb_scan_progressbar);
        floatButton = (FloatingActionButton) findViewById(R.id.fab_test_tcp_net);
        userInfo = UserSingleton.get().getUserInfo();
        userpic = CommonUtil.pictureBitmap;
        tvTitle.setText(userInfo.getBu_Name() + ":" + "仓库管理");
        String Test;
        setHomeButton();
        setViewsListener();
        handleGetDepartment();

    }

    private void handleGetDepartment() {
        String sql = "select department_id,department_name from department";
        if (!(UserSingleton.get().getDepartmentMap() != null && UserSingleton.get().getDepartmentMap().keySet().size() > 0)) {
            QueryAsyncTask query = new QueryAsyncTask();
            query.execute(sql);
            query.setLoadDataCompleteListener(new OnLoadDataListener() {
                @Override public void loadComplete(List<JsonObject> jsonObjectList) {
                    if (jsonObjectList != null && jsonObjectList.size() > 0) {
                        HashMap<Integer, String> departmentIDNameMap = new HashMap<>();
                        for (JsonObject jsonObject : jsonObjectList) {
                            int departmentID = jsonObject.get("department_id").getAsInt();
                            String departmentName = jsonObject.get("department_name").getAsString();
                            departmentIDNameMap.put(departmentID, departmentName);
                        }
                        UserSingleton.get().setDepartmentMap(departmentIDNameMap);
                    }
                }
            });
        }
    }

    private void setViewsListener() {
        scanToStockButton.setOnClickListener(this);
        productSupplyButton.setOnClickListener(this);
        returnWCButton.setOnClickListener(this);
        departMentInButton.setOnClickListener(this);
        moveStockAreaButton.setOnClickListener(this);
        freezeStockButton.setOnClickListener(this);
        completeProductButton.setOnClickListener(this);
        partStockInButton.setOnClickListener(this);
        partStockCheckButton.setOnClickListener(this);
        selfProductButton.setOnClickListener(this);
        makingProductButton.setOnClickListener(this);
        floatButton.setOnClickListener(this);
    }

    private void productSupply() {
        if (userInfo == null) {
            Toast.makeText(StockPartMainActivity.this, "请先扫描职工二维码登录", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(StockPartMainActivity.this, StockPutActivity.class);
            startActivity(intent);
        }
    }

    private void scanToStock() {
        if (userInfo == null) {
            Toast.makeText(StockPartMainActivity.this, "请先扫描职工二维码登录", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(StockPartMainActivity.this, StockInActivity.class);
            startActivity(intent);
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


    @Override
    protected void onResume() {
        //设置为竖屏幕
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }


        super.onResume();
    }


    @Override
    public void onClick(View view) {
        if (view == scanToStockButton) {
            scanToStock();
        } else if (view == productSupplyButton) {
            productSupply();
        } else if (view == returnWCButton) {
            returnToWC();
        } else if (view == moveStockAreaButton) {
            moveStockArea();
        } else if (view == freezeStockButton) {
            freezeStock();
        } else if (view == departMentInButton) {
            departMentIn();
        } else if (view == completeProductButton) {
            completeProduct();
        } else if (view == partStockInButton) {
            partStockIn();
        } else if (view == makingProductButton) {
            makingProductCheck();
        } else if (view == partStockCheckButton) {
            partStockCheck();
        } else if (view == selfProductButton) {
            selfProduct();
        } else if (view == lookQRButton) {
//            scanToStock();
        } else if (view == floatButton) {
            Intent intent = new Intent(StockPartMainActivity.this, ShbTcpTest.class);
            startActivity(intent);
        }
    }

    private void selfProduct() {
        if (userInfo == null) {
            Toast.makeText(StockPartMainActivity.this, "请先登录", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(StockPartMainActivity.this, StockCheckPartInvActivity.class);
            intent.putExtra("Ac_Type", 2);

            startActivity(intent);
        }
    }

    private void partStockCheck() {
        if (userInfo == null) {
            Toast.makeText(StockPartMainActivity.this, "请先登录", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(StockPartMainActivity.this, StockCheckPartInvActivity.class);
            intent.putExtra("Ac_Type", 1);
            startActivity(intent);
        }
    }

    private void partStockIn() {
        if (userInfo == null) {
            Toast.makeText(StockPartMainActivity.this, "请先登录", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(StockPartMainActivity.this, StockQueryPartActivity.class);
            startActivity(intent);
        }
    }

    private void completeProduct() {
        if (userInfo == null) {
            Toast.makeText(StockPartMainActivity.this, "请先登录", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(StockPartMainActivity.this, StockQueryProductActivity.class);
            startActivity(intent);
        }
    }

    private void departMentIn() {
        if (userInfo == null) {
            Toast.makeText(StockPartMainActivity.this, "请先登录", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(StockPartMainActivity.this, StockDepartmentInActivity.class);
            startActivity(intent);
        }
    }

    private void freezeStock() {
        if (userInfo == null) {
            Toast.makeText(StockPartMainActivity.this, "请先扫描职工二维码登录", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(StockPartMainActivity.this, StockFreezeActivity.class);
            startActivity(intent);
        }
    }

    private void moveStockArea() {
        if (userInfo == null) {
            Toast.makeText(StockPartMainActivity.this, "请先扫描职工二维码登录", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(StockPartMainActivity.this, StockMoveActivity.class);
            startActivity(intent);
        }
    }

    private void makingProductCheck() {

    }

    private void returnToWC() {
        if (userInfo == null) {
            Toast.makeText(StockPartMainActivity.this, "请先登录", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(StockPartMainActivity.this, StockReworkWCActivity.class);
            startActivity(intent);
        }
    }
}
