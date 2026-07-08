package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.chinashb.www.mobileerp.basicobject.QueryAsyncTask;
import com.chinashb.www.mobileerp.funs.OnLoadDataListener;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.warehouse.BuWarehouseAccountOutActivity;
import com.chinashb.www.mobileerp.warehouse.BuWarehouseAccountTailInActivity;
import com.chinashb.www.mobileerp.warehouse.ScanIstFindProductActivity;
import com.chinashb.www.mobileerp.warehouse.StockProductDepartmentInActivity;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2019/7/24 16:42
 * @author 作者: xxblwf
 * @description 成品库管理主界面
 */

public class StockProductMainActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.product_main_sale_out_textView) TextView saleOutTextView;
    @BindView(R.id.product_main_sale_out_code_box_textView) TextView saleOutCodeBoxTextView;
    @BindView(R.id.product_main_other_out_textView) TextView otherOutTextView;
    @BindView(R.id.product_main_scan_box_textView) TextView scanBoxInTextView;
    @BindView(R.id.product_main_scan_ist_get_item_textView) TextView scanIstGetItemTextView;
    @BindView(R.id.product_main_bu_warehouse_textView) TextView buWarehouseInTextView;//车间仓
    @BindView(R.id.product_main_bu_warehouse_out_textView) TextView buWarehouseOutTextView;//车间仓出库
    @BindView(R.id.product_main_scan_code_box_textView) TextView codeBoxTextView;//成品标签
    @BindView(R.id.product_main_scan_code_box_manu_textView) TextView codeBoxManuTextView;//成品补打标签
    @BindView(R.id.product_main_product_dp_order_textView) TextView dpOrderTextView;//发货指令查询
    @BindView(R.id.product_main_product_rework_pallet_textView) TextView reworkPalletTextView;//成品整托返工
    @BindView(R.id.product_main_product_move_record_textView) TextView moveRecordTextView;//成品移库记录查询
    @BindView(R.id.product_main_check_inventory_common_all_box_textView) TextView checkInventoryCommonAllTextView;//成品标签统一盘点
    @BindView(R.id.product_main_product_department_in_textView) TextView departmentInTextView;//成品部门领料

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_product_main_layout);
        ButterKnife.bind(this);

        setViewsListener();
        handleGetDepartment();
    }

    private void setViewsListener() {

        otherOutTextView.setOnClickListener(this);
        saleOutTextView.setOnClickListener(this);
        saleOutCodeBoxTextView.setOnClickListener(this);
        scanBoxInTextView.setOnClickListener(this);
        scanIstGetItemTextView.setOnClickListener(this);
        buWarehouseInTextView.setOnClickListener(this);
        buWarehouseOutTextView.setOnClickListener(this);
        codeBoxTextView.setOnClickListener(this);
        codeBoxManuTextView.setOnClickListener(this);
        dpOrderTextView.setOnClickListener(this);
        reworkPalletTextView.setOnClickListener(this);
        moveRecordTextView.setOnClickListener(this);
        departmentInTextView.setOnClickListener(this);
        checkInventoryCommonAllTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == saleOutTextView) {
            Intent intent = new Intent(this, ProductSaleOutMESActivity.class);
            startActivity(intent);
        } else if (v == saleOutCodeBoxTextView) {
            Intent intent = new Intent(this, ProductSaleOutCodeBoxActivity.class);
            startActivity(intent);
        } else if (v == scanBoxInTextView) {
            Intent intent = new Intent(this, ProductScanBoxInActivity.class);
            startActivity(intent);
        } else if (v == scanIstGetItemTextView) {
            Intent intent = new Intent(this, ScanIstFindProductActivity.class);
            startActivity(intent);
        } else if (v == buWarehouseInTextView) {
            Intent intent = new Intent(this, BuWarehouseAccountTailInActivity.class);
            startActivity(intent);
        } else if (v == buWarehouseOutTextView) {
//            ToastUtil .showToastShort("暂不可用");
            Intent intent = new Intent(this, BuWarehouseAccountOutActivity.class);
//            Intent intent = new Intent(this, BuWarehouseTailReleaseOutActivity.class);
            startActivity(intent);

        } else if (v == codeBoxTextView) {
            Intent intent = new Intent(this, ProductCodeBoxManagementActivity.class);
            startActivity(intent);
        } else if (v == codeBoxManuTextView) {
            Intent intent = new Intent(this, ProductManuCodeBoxManagementActivity.class);
            startActivity(intent);
        } else if (v == dpOrderTextView) {
            Intent intent = new Intent(this, DeliveryOrderActivity.class);
            startActivity(intent);
        } else if (v == reworkPalletTextView) {
            Intent intent = new Intent(this, ProductReworkPalletActivity.class);
            startActivity(intent);
        } else if (v == moveRecordTextView) {
            Intent intent = new Intent(this, ProductMoveRecordActivity.class);
            startActivity(intent);

        } else if (v == departmentInTextView) {
            Intent intent = new Intent(this, StockProductDepartmentInActivity.class);
            startActivity(intent);

        } else if (v == checkInventoryCommonAllTextView) {
            Intent intent = new Intent(this, ProductCheckInventoryCommonAllActivity.class);
            startActivity(intent);

        }
    }

    private void handleGetDepartment() {
        String sql = "select department_id,department_name from department";
        if (!(UserSingleton.get().getDepartmentMap() != null && UserSingleton.get().getDepartmentMap().keySet().size() > 0)) {
            QueryAsyncTask query = new QueryAsyncTask();
            query.execute(sql);
            query.setLoadDataCompleteListener(new OnLoadDataListener() {
                @Override
                public void loadComplete(List<JsonObject> jsonObjectList) {
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
}
