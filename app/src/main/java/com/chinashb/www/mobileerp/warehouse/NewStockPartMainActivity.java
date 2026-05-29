package com.chinashb.www.mobileerp.warehouse;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.BuPlanGoodsActivity;
import com.chinashb.www.mobileerp.GetPhotoFromServerActivity;
import com.chinashb.www.mobileerp.PartWorkLinePutActivity;
import com.chinashb.www.mobileerp.PhotoCameraActivity;
import com.chinashb.www.mobileerp.PickGoodsNewShowActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.SendGoodsManagerActivity;
import com.chinashb.www.mobileerp.SupplierOrSelfReturnActivity;
import com.chinashb.www.mobileerp.basicobject.QueryAsyncTask;
import com.chinashb.www.mobileerp.basicobject.UserInfoEntity;
import com.chinashb.www.mobileerp.bean.WarehouseMenuPermissionBean;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.OnLoadDataListener;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.logistics.StockLogisticsInActivity;
import com.chinashb.www.mobileerp.shipment.NewAllShipmentAccountDeliveryManagement;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.talk.ShbTcpTest;
import com.chinashb.www.mobileerp.utils.AutoI18nUtil;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.warehouse.menu.WarehouseMenuAdapter;
import com.chinashb.www.mobileerp.warehouse.menu.WarehouseMenuItem;
import com.chinashb.www.mobileerp.warehouse.menu.WarehouseMenuRegistry;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2026/5/2 16:02
 * @author 作者: code-x John
 * @description 零件仓权限驱动网格主页
 */
public class NewStockPartMainActivity extends BaseActivity implements WarehouseMenuAdapter.OnMenuClickListener {
    public static Bitmap userpic;

    @BindView(R.id.new_part_menu_title_textView)
    TextView titleTextView;
    @BindView(R.id.new_part_menu_summary_textView)
    TextView summaryTextView;
    @BindView(R.id.new_part_menu_empty_textView)
    TextView emptyTextView;
    @BindView(R.id.new_part_menu_recyclerView)
    CustomRecyclerView menuRecyclerView;

    private final List<WarehouseMenuItem> allPartMenuItems = new ArrayList<>();
    private final List<WarehouseMenuItem> visiblePartMenuItems = new ArrayList<>();
    private final Map<String, WarehouseMenuPermissionBean> permissionMap = new LinkedHashMap<>();

    private WarehouseMenuAdapter adapter;
    private UserInfoEntity userInfo;
    private boolean permissionLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stock_part_main_layout);
        ButterKnife.bind(this);

        userInfo = UserSingleton.get().getUserInfo();
        userpic = CommonUtil.pictureBitmap;
        allPartMenuItems.addAll(WarehouseMenuRegistry.getPartMenuItems());

        initRecyclerView();
        setHomeButton();
        bindHeader();
        handleGetDepartment();
        handleGetBuAcAndInvTable();
        applyVisibleMenus(allPartMenuItems, false);
        new LoadWarehouseMenuPermissionTask().execute();
    }

    @Override
    protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }

    private void initRecyclerView() {
        adapter = new WarehouseMenuAdapter(this);
        adapter.setOnMenuClickListener(this);
        menuRecyclerView.setAdapter(adapter);
    }

    private void bindHeader() {
        if (userInfo != null) {
            titleTextView.setText(userInfo.getBu_Name() + ":" + AutoI18nUtil.translate(this, "仓库管理"));
        } else {
            titleTextView.setText(AutoI18nUtil.translate(this, "零件仓功能"));
        }
        updateSummaryText();
    }

    private void updateSummaryText() {
        String summary;
        if (!permissionLoaded) {
            summary = "权限未加载完成，先显示默认菜单";
        } else {
            summary = "当前显示 " + visiblePartMenuItems.size() + " / " + allPartMenuItems.size() + " 个功能";
        }
        summaryTextView.setText(AutoI18nUtil.translate(this, summary));
        emptyTextView.setVisibility(visiblePartMenuItems.isEmpty() ? View.VISIBLE : View.GONE);
        menuRecyclerView.setVisibility(visiblePartMenuItems.isEmpty() ? View.GONE : View.VISIBLE);
        if (visiblePartMenuItems.isEmpty()) {
            emptyTextView.setText(AutoI18nUtil.translate(this, "当前账号暂无零件仓功能权限"));
        }
    }

    private void handleGetBuAcAndInvTable() {
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

    @Override
    public void onMenuClick(WarehouseMenuItem item) {
        if (item == null) {
            return;
        }
        if (item.isRequiresLogin() && !ensureLogin(item.getTitle())) {
            return;
        }

        String menuCode = item.getMenuCode();
        if (WarehouseMenuRegistry.MENU_PART_STOCK_IN.equals(menuCode)) {
            startActivity(new Intent(this, StockInActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PART_PRODUCT_SUPPLY.equals(menuCode)) {
            startActivity(new Intent(this, StockPutActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PART_REWORK_OUT.equals(menuCode)) {
            startActivity(new Intent(this, StockReworkWCActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PART_REWORK_OUT_MALAI.equals(menuCode)) {
            startActivity(new Intent(this, StockReworkWCActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PART_MOVE.equals(menuCode)) {
            startActivity(new Intent(this, StockPartMoveActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PART_FREEZE.equals(menuCode)) {
            startActivity(new Intent(this, StockFreezeActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PART_DEPARTMENT_PICK.equals(menuCode)) {
            startActivity(new Intent(this, StockDepartmentInActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PART_PRODUCT_QUERY.equals(menuCode)) {
            startActivity(new Intent(this, StockQueryProductActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PART_QUERY.equals(menuCode)) {
            startActivity(new Intent(this, StockQueryPartActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PART_WIP_CHECK.equals(menuCode)) {
            partStockCheck(true);
        } else if (WarehouseMenuRegistry.MENU_PART_CHECK.equals(menuCode)) {
            partStockCheck(false);
        } else if (WarehouseMenuRegistry.MENU_PART_SELF_PRODUCT_CHECK.equals(menuCode)) {
            selfProduct();
        } else if (WarehouseMenuRegistry.MENU_PART_LOOK_QR.equals(menuCode)) {
            ToastUtil.showToastShort("暂未开放");
        } else if (WarehouseMenuRegistry.MENU_PART_WORKLINE_PICK.equals(menuCode)) {
            startActivity(new Intent(this, PartWorkLinePutActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PART_INNER_SALE_OUT.equals(menuCode)) {
            startActivity(new Intent(this, InnerSaleOutActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PART_PLAN_GOODS.equals(menuCode)) {
            startActivity(new Intent(this, BuPlanGoodsActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PART_SUPPLIER_RETURN.equals(menuCode)) {
            startActivity(new Intent(this, SupplierOrSelfReturnActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PART_SELF_RETURN.equals(menuCode)) {
            Intent intent = new Intent(this, SupplierOrSelfReturnActivity.class);
            intent.putExtra(IntentConstant.Intent_Extra_supplier_or_self_return_boolean, true);
            startActivity(intent);
        } else if (WarehouseMenuRegistry.MENU_PART_SEND_GOODS.equals(menuCode)) {
            startActivity(new Intent(this, SendGoodsManagerActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PART_LOGISTICS_RECEIVE.equals(menuCode)) {
            startActivity(new Intent(this, StockLogisticsInActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PART_STOCK_IN_WITH_DATE.equals(menuCode)) {
            startActivity(new Intent(this, StockInWithDateActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PART_PACK.equals(menuCode)) {
            startActivity(new Intent(this, PartPackPackageActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PART_UNPACK.equals(menuCode)) {
            ToastUtil.showToastShort("暂未开放");
        } else if (WarehouseMenuRegistry.MENU_PART_PHOTO.equals(menuCode)) {
            startActivity(new Intent(this, PhotoCameraActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PART_SCAN_IST.equals(menuCode)) {
            startActivity(new Intent(this, ScanIstFindItemActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PART_MOVE_RECORD.equals(menuCode)) {
            startActivity(new Intent(this, PartMoveRecordActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PART_ALLOCATE_INNER_COMPANY.equals(menuCode)) {
            startActivity(new Intent(this, PartAllocateTransferInnerCompanyActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PART_GET_PICTURE.equals(menuCode)) {
            startActivity(new Intent(this, GetPhotoFromServerActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PART_PICK_GOODS.equals(menuCode)) {
            startActivity(new Intent(this, PickGoodsNewShowActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PART_FREEZE_RECORD.equals(menuCode)) {
            startActivity(new Intent(this, FreezeRecordActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PART_SEND_INFO.equals(menuCode)) {
            ToastUtil.showToastShort("暂未开放");
        } else if (WarehouseMenuRegistry.MENU_PART_COMPANY29.equals(menuCode)) {
            startActivity(new Intent(this, StockInZhuanKouActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PART_SHIPMENT.equals(menuCode)) {
            startActivity(new Intent(this, NewAllShipmentAccountDeliveryManagement.class));
        } else {
            startActivity(new Intent(this, ShbTcpTest.class));
        }
    }

    private boolean ensureLogin(String functionName) {
        if (userInfo != null) {
            return true;
        }
        showLocalizedToast("请先登录后再进入" + functionName);
        return false;
    }

    private void selfProduct() {
        Intent intent = new Intent(this, StockCheckPartInvActivity.class);
        intent.putExtra(IntentConstant.Intent_Extra_check_self_product, true);
        intent.putExtra("Ac_Type", 2);
        startActivity(intent);
    }

    private void partStockCheck(boolean fromZaiZhiPin) {
        Intent intent = new Intent(this, StockCheckPartInvActivity.class);
        intent.putExtra("Ac_Type", 1);
        intent.putExtra(IntentConstant.Intent_Extra_check_from_zaizhipin, fromZaiZhiPin);
        intent.putExtra(IntentConstant.Intent_Extra_check_part, true);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
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

    private void showLocalizedToast(String message) {
        Toast.makeText(this, AutoI18nUtil.translate(this, message), Toast.LENGTH_LONG).show();
    }

    private void applyVisibleMenus(List<WarehouseMenuItem> list, boolean fromPermission) {
        visiblePartMenuItems.clear();
        if (list != null) {
            visiblePartMenuItems.addAll(list);
        }
        adapter.setData(visiblePartMenuItems);
        permissionLoaded = fromPermission;
        updateSummaryText();
    }

    private List<WarehouseMenuItem> filterMenusByPermission(List<WarehouseMenuItem> sourceList) {
        if (permissionMap.isEmpty()) {
            return sourceList;
        }

        List<WarehouseMenuItem> resultList = new ArrayList<>();
        for (WarehouseMenuItem item : sourceList) {
            WarehouseMenuPermissionBean bean = permissionMap.get(buildPermissionKey(item.getModuleCode(), item.getMenuCode()));
            // 默认全权限：接口只对明确返回 Permit=false 的菜单做隐藏
            if (bean == null || bean.isPermit()) {
                resultList.add(item);
            }
        }
        return resultList;
    }

    private String buildPermissionKey(String moduleCode, String menuCode) {
        return moduleCode + "#" + menuCode;
    }

    private class LoadWarehouseMenuPermissionTask extends AsyncTask<String, Void, List<WarehouseMenuPermissionBean>> {
        @Override
        protected List<WarehouseMenuPermissionBean> doInBackground(String... strings) {
            return WebServiceUtil.getWarehouseMenuPermissionList(
                    WebServiceUtil.buildWarehouseMenuPermissionSql(
                            UserSingleton.get().getHRID(),
                            WarehouseMenuRegistry.MODULE_PART
                    )
            );
        }

        @Override
        protected void onPostExecute(List<WarehouseMenuPermissionBean> beanList) {
            permissionMap.clear();
            if (beanList != null) {
                for (WarehouseMenuPermissionBean bean : beanList) {
                    if (bean == null || TextUtils.isEmpty(bean.getMenuCode())) {
                        continue;
                    }
                    permissionMap.put(buildPermissionKey(bean.getModuleCode(), bean.getMenuCode()), bean);
                }
            }

            if (permissionMap.isEmpty()) {
                applyVisibleMenus(allPartMenuItems, true);
                return;
            }

            applyVisibleMenus(filterMenusByPermission(allPartMenuItems), true);
        }
    }
}
