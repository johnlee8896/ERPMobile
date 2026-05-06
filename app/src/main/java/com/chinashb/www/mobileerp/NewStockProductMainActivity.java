package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.bean.WarehouseMenuPermissionBean;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.AutoI18nUtil;
import com.chinashb.www.mobileerp.warehouse.BuWarehouseAccountOutActivity;
import com.chinashb.www.mobileerp.warehouse.BuWarehouseAccountTailInActivity;
import com.chinashb.www.mobileerp.warehouse.ScanIstFindProductActivity;
import com.chinashb.www.mobileerp.warehouse.menu.WarehouseMenuAdapter;
import com.chinashb.www.mobileerp.warehouse.menu.WarehouseMenuItem;
import com.chinashb.www.mobileerp.warehouse.menu.WarehouseMenuRegistry;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2026/5/2 16:02
 * @author 作者: code-x John
 * @description 成品仓权限驱动网格主页
 */
public class NewStockProductMainActivity extends BaseActivity implements WarehouseMenuAdapter.OnMenuClickListener {

    @BindView(R.id.new_product_menu_title_textView)
    TextView titleTextView;
    @BindView(R.id.new_product_menu_summary_textView)
    TextView summaryTextView;
    @BindView(R.id.new_product_menu_empty_textView)
    TextView emptyTextView;
    @BindView(R.id.new_product_menu_recyclerView)
    CustomRecyclerView menuRecyclerView;

    private final List<WarehouseMenuItem> allProductMenuItems = new ArrayList<>();
    private final List<WarehouseMenuItem> visibleProductMenuItems = new ArrayList<>();
    private final Map<String, WarehouseMenuPermissionBean> permissionMap = new LinkedHashMap<>();

    private WarehouseMenuAdapter adapter;
    private boolean permissionLoaded;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stock_product_main_layout);
        ButterKnife.bind(this);

        allProductMenuItems.addAll(WarehouseMenuRegistry.getProductMenuItems());
        initRecyclerView();
        setHomeButton();
        bindHeader();
        applyVisibleMenus(allProductMenuItems, false);
        new LoadWarehouseMenuPermissionTask().execute();
    }

    private void initRecyclerView() {
        adapter = new WarehouseMenuAdapter(this);
        adapter.setOnMenuClickListener(this);
        menuRecyclerView.setAdapter(adapter);
    }

    private void bindHeader() {
        titleTextView.setText(AutoI18nUtil.translate(this, "成品仓功能"));
        updateSummaryText();
    }

    private void updateSummaryText() {
        String summary;
        if (!permissionLoaded) {
            summary = "权限未加载完成，先显示默认菜单";
        } else {
            summary = "当前显示 " + visibleProductMenuItems.size() + " / " + allProductMenuItems.size() + " 个功能";
        }
        summaryTextView.setText(AutoI18nUtil.translate(this, summary));
        emptyTextView.setVisibility(visibleProductMenuItems.isEmpty() ? View.VISIBLE : View.GONE);
        menuRecyclerView.setVisibility(visibleProductMenuItems.isEmpty() ? View.GONE : View.VISIBLE);
        if (visibleProductMenuItems.isEmpty()) {
            emptyTextView.setText(AutoI18nUtil.translate(this, "当前账号暂无成品仓功能权限"));
        }
    }

    @Override
    public void onMenuClick(WarehouseMenuItem item) {
        if (item == null) {
            return;
        }
        if (!UserSingleton.get().hasLogin()) {
            showLocalizedToast("请先登录后再进入" + item.getTitle());
            return;
        }

        String menuCode = item.getMenuCode();
        if (WarehouseMenuRegistry.MENU_PRODUCT_SALE_OUT.equals(menuCode)) {
            startActivity(new Intent(this, ProductSaleOutMESActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PRODUCT_SALE_OUT_PALLET.equals(menuCode)) {
            startActivity(new Intent(this, ProductSaleOutCodeBoxActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PRODUCT_SCAN_BOX_IN.equals(menuCode)) {
            startActivity(new Intent(this, ProductScanBoxInActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PRODUCT_SCAN_IST.equals(menuCode)) {
            startActivity(new Intent(this, ScanIstFindProductActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PRODUCT_BU_WAREHOUSE_IN.equals(menuCode)) {
            startActivity(new Intent(this, BuWarehouseAccountTailInActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PRODUCT_BU_WAREHOUSE_OUT.equals(menuCode)) {
            startActivity(new Intent(this, BuWarehouseAccountOutActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PRODUCT_CODE_BOX.equals(menuCode)) {
            startActivity(new Intent(this, ProductCodeBoxManagementActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PRODUCT_CODE_BOX_MANU.equals(menuCode)) {
            startActivity(new Intent(this, ProductManuCodeBoxManagementActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PRODUCT_DELIVERY_ORDER.equals(menuCode)) {
            startActivity(new Intent(this, DeliveryOrderActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PRODUCT_REWORK_PALLET.equals(menuCode)) {
            startActivity(new Intent(this, ProductReworkPalletActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PRODUCT_MOVE_RECORD.equals(menuCode)) {
            startActivity(new Intent(this, ProductMoveRecordActivity.class));
        } else if (WarehouseMenuRegistry.MENU_PRODUCT_CHECK_ALL.equals(menuCode)) {
            startActivity(new Intent(this, ProductCheckInventoryCommonAllActivity.class));
        }
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
        visibleProductMenuItems.clear();
        if (list != null) {
            visibleProductMenuItems.addAll(list);
        }
        adapter.setData(visibleProductMenuItems);
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
                            WarehouseMenuRegistry.MODULE_PRODUCT
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
                applyVisibleMenus(allProductMenuItems, true);
                return;
            }

            applyVisibleMenus(filterMenusByPermission(allProductMenuItems), true);
        }
    }
}
