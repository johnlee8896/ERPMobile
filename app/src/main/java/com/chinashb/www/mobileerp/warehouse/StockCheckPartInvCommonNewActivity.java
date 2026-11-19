package com.chinashb.www.mobileerp.warehouse;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.TitleLayoutManagerView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * 零件盘点（新）
 *
 * 2026-10-29 john
 * 按《仓库盘点Android-WebService接口与开发流程.md》实现：
 * 1. 范围先定 V2 _New：草稿作业、扫描范围、查看范围。
 * 2. 现场盘点 V3 _New：作业/盘点表、扫描库位、解析物料、保存、完成。
 */
public class StockCheckPartInvCommonNewActivity extends BaseActivity {

    private TitleLayoutManagerView titleLayoutManagerView;
    private Button btnSelectCheckFile;
    private Button btnScanIst;
    private Button btnScanItem;
    private Button btnCommit;
    private Button btnCal;
    private EditText inputEditText;
    private EditText etN;
    private EditText etPN;
    private EditText etDQ;
    private EditText etRemark;
    private EditText etManualLotNo;
    private TextView realQtyTextView;
    private TextView tvIst;
    private TextView tvERPIst;
    private TextView tvItemCode;
    private TextView tvItemName;
    private TextView tvManuLotno;

    private Button btnSelectDraftSession;
    private Button btnScanScope;
    private Button btnViewScope;
    private Button btnCompleteLocation;
    private Button btnRefreshLocation;
    private TextView tvScopeSession;
    private TextView tvInventorySession;
    private TextView tvTaskInfo;
    private TextView tvRecords;

    private int acType = 1;
    private int buId;
    private int warehouseId = 2;

    private long scopeSessionId;
    private String scopeSessionName = "";
    private boolean scopeEditable;

    private long countSessionId;
    private String countSessionName = "";
    private long ciId;
    private int purpose;

    private long countUnitId;
    private int compareMode;
    private boolean canInput;
    private long istId;
    private long subIstId;
    private String locationBarcode = "";
    private String locationStateVersion = "";
    private String sessionStateVersion = "";
    private long labelCiId;
    private int taskNo;
    private int countCategory;
    private int sessionStatus;
    private List<JSONObject> currentRecords = new ArrayList<>();
    private List<JSONObject> currentItems = new ArrayList<>();
    private final Set<String> savedItemScanSet = new HashSet<>();
    private String pendingSaveRequestId;
    private String pendingCompleteRequestId;

    private long itemId;
    private long ivId;
    private long psId;
    private long productId;
    private long lotId;
    private long statusId;
    private String itemScanX = "";

    private String scanFor = "";
    private boolean isProcessingScan;
    private boolean isSaving;
    private boolean isCompleting;
    private String lastProcessedBarcode;
    private long lastProcessedBarcodeTime;
    private int savedItemCountInLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_check_part_inv);
        setHomeButton();
        bindView();
        setButtonClick();
        getExtras();
        addTopControls();
        restoreInstanceState(savedInstanceState);
    }

    protected void setHomeButton() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void getExtras() {
        acType = getIntent().getIntExtra("Ac_Type", 1);
        buId = UserSingleton.get().getUserInfo() == null ? 0 : UserSingleton.get().getUserInfo().getBu_ID();
        warehouseId = resolveWarehouseId(buId);
        // 零件盘点（新）暂不开放成品搜索与旧搜索入口
        findViewById(R.id.check_inv_search_layout).setVisibility(View.GONE);
        findViewById(R.id.check_part_inv_product_serarch_data_layout).setVisibility(View.GONE);
    }

    // codex by john 2026-11-15 - 零件盘点仓库映射，后续 BU 可继续补充
    private int resolveWarehouseId(int buId) {
        switch (buId) {
            case 1:
                return 2;
            case 149:
                return 218;
            case 81:
                return 131;
            default:
                return 2;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("scanFor", scanFor);
        outState.putInt("acType", acType);
        outState.putLong("scopeSessionId", scopeSessionId);
        outState.putString("scopeSessionName", scopeSessionName);
        outState.putBoolean("scopeEditable", scopeEditable);
        outState.putLong("countSessionId", countSessionId);
        outState.putString("countSessionName", countSessionName);
        outState.putLong("ciId", ciId);
        outState.putInt("purpose", purpose);
        outState.putLong("countUnitId", countUnitId);
        outState.putInt("compareMode", compareMode);
        outState.putBoolean("canInput", canInput);
        outState.putLong("istId", istId);
        outState.putLong("subIstId", subIstId);
        outState.putString("locationBarcode", locationBarcode);
        outState.putString("locationStateVersion", locationStateVersion);
        outState.putString("sessionStateVersion", sessionStateVersion);
        outState.putLong("labelCiId", labelCiId);
        outState.putInt("taskNo", taskNo);
        outState.putInt("countCategory", countCategory);
        outState.putInt("sessionStatus", sessionStatus);
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        scanFor = savedInstanceState.getString("scanFor", "");
        acType = savedInstanceState.getInt("acType", acType);
        scopeSessionId = savedInstanceState.getLong("scopeSessionId", 0);
        scopeSessionName = savedInstanceState.getString("scopeSessionName", "");
        scopeEditable = savedInstanceState.getBoolean("scopeEditable", false);
        countSessionId = savedInstanceState.getLong("countSessionId", 0);
        countSessionName = savedInstanceState.getString("countSessionName", "");
        ciId = savedInstanceState.getLong("ciId", 0);
        purpose = savedInstanceState.getInt("purpose", 0);
        countUnitId = savedInstanceState.getLong("countUnitId", 0);
        compareMode = savedInstanceState.getInt("compareMode", 0);
        canInput = savedInstanceState.getBoolean("canInput", false);
        istId = savedInstanceState.getLong("istId", 0);
        subIstId = savedInstanceState.getLong("subIstId", 0);
        locationBarcode = savedInstanceState.getString("locationBarcode", "");
        locationStateVersion = savedInstanceState.getString("locationStateVersion", "");
        sessionStateVersion = savedInstanceState.getString("sessionStateVersion", "");
        labelCiId = savedInstanceState.getLong("labelCiId", 0);
        taskNo = savedInstanceState.getInt("taskNo", 0);
        countCategory = savedInstanceState.getInt("countCategory", 0);
        sessionStatus = savedInstanceState.getInt("sessionStatus", 0);

        if (!TextUtils.isEmpty(scopeSessionName)) {
            tvScopeSession.setText("已选择：" + scopeSessionName
                    + (scopeEditable ? "（可扫范围）" : "（已启用/结束，仅可查看）"));
        }
        if (!TextUtils.isEmpty(countSessionName)) {
            if (ciId > 0) {
                tvInventorySession.setText("作业：" + countSessionName + " 表：" + ciId
                        + (purpose == 0 ? " 正常盘点" : " 复核"));
            } else {
                tvInventorySession.setText("已选作业：" + countSessionName);
            }
        }
    }

    private void bindView() {
        titleLayoutManagerView = findViewById(R.id.stock_check_part_titleLayout);
        btnSelectCheckFile = findViewById(R.id.btn_select_check_inv);
        btnScanIst = findViewById(R.id.btn_select_check_inv_scan_ist);
        btnScanItem = findViewById(R.id.btn_select_check_inv_add_item);
        btnCommit = findViewById(R.id.btn_affirm_qty);
        btnCal = findViewById(R.id.btn_check_inv_cal_qty);
        inputEditText = findViewById(R.id.content_stock_check_part_input_EditeText);
        etN = findViewById(R.id.et_check_stock_box_n);
        etPN = findViewById(R.id.et_check_stock_box_pn);
        etDQ = findViewById(R.id.et_check_stock_box_dq);
        etRemark = findViewById(R.id.et_check_stock_box_remark);
        etManualLotNo = findViewById(R.id.et_check_stock_manulotno);
        realQtyTextView = findViewById(R.id.et_check_stock_box_real_qty);
        tvIst = findViewById(R.id.tv_check_stock_ist);
        tvERPIst = findViewById(R.id.tv_check_stock_ist_erp);
        tvItemCode = findViewById(R.id.tv_check_stock_item_code);
        tvItemName = findViewById(R.id.tv_check_stock_item_name);
        tvManuLotno = findViewById(R.id.tv_check_stock_manulotno);

        btnSelectCheckFile.setText("选择作业/表");
        btnScanIst.setText("扫描库位");
        btnScanItem.setText("扫描物料");
        btnCommit.setText("保存本条");
        btnCal.setText("计算数量");
    }

    private void setButtonClick() {
        btnSelectCheckFile.setOnClickListener(v -> {
            if (countSessionId <= 0) {
                new JobQueryAsyncTask("Jobs").execute();
            } else {
                new JobQueryAsyncTask("Dashboard").execute();
            }
        });

        btnScanIst.setOnClickListener(v -> {
            if (ciId <= 0) {
                ToastUtil.showToastShort("请先选择作业和盘点表");
                return;
            }
            scanFor = "Location";
            initiateScan();
        });

        btnScanItem.setOnClickListener(v -> {
            if (!canInput || istId <= 0) {
                ToastUtil.showToastShort("请先扫描库位并确认可录入");
                return;
            }
            scanFor = "Item";
            initiateScan();
        });

        btnCommit.setOnClickListener(v -> saveCurrentItem());
        btnCal.setOnClickListener(v -> updateRealQty());

        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            protected void onTextChangedSafe(CharSequence text) {
                if (!TextUtils.isEmpty(text)) {
                    onScanText(text.toString());
                }
            }
        });
    }

    private void addTopControls() {
        android.view.ViewGroup root = (android.view.ViewGroup) titleLayoutManagerView.getParent();
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(8, 4, 8, 4);

        TextView context = new TextView(this);
        context.setPadding(8, 4, 8, 4);
        context.setText("账类：" + acTypeName(acType) + "  仓库ID：" + warehouseId + "  BU：" + buId);
        panel.addView(context);

        tvInventorySession = new TextView(this);
        tvInventorySession.setPadding(8, 8, 8, 4);
        tvInventorySession.setText("未选择盘点作业/表");
        panel.addView(tvInventorySession);

        tvTaskInfo = new TextView(this);
        tvTaskInfo.setPadding(8, 0, 8, 4);
        tvTaskInfo.setText("任务码：未扫描");
        panel.addView(tvTaskInfo);

        tvRecords = new TextView(this);
        tvRecords.setPadding(8, 0, 8, 4);
        tvRecords.setText("本轮已保存：0条");
        panel.addView(tvRecords);

        // codex by john 2026-11-17 - 暂时隐藏高级快捷操作，只保留下方三个主操作按钮
        LinearLayout advancedActionsLayout = new LinearLayout(this);
        advancedActionsLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.setGravity(android.view.Gravity.CENTER);
        row1.addView(createSmallButton("新建作业", v -> showCreateJobDialog()));
        row1.addView(createSmallButton("工作台", v -> {
            if (countSessionId > 0) new JobQueryAsyncTask("Dashboard").execute();
        }));
        row1.addView(createSmallButton("结果", v -> showResultsViewDialog()));
        row1.addView(createSmallButton("结束作业", v -> showCloseJobDialog()));
        advancedActionsLayout.addView(row1);

        LinearLayout row2 = new LinearLayout(this);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        row2.setGravity(android.view.Gravity.CENTER);
        row2.addView(createSmallButton("扫任务码", v -> {
            if (ciId <= 0) {
                ToastUtil.showToastShort("请先选择盘点表");
                return;
            }
            scanFor = "Task";
            initiateScan();
        }));
        row2.addView(createSmallButton("结束贴号", v -> showFinalizeLabelsDialog()));
        row2.addView(createSmallButton("重新盘点", v -> showReopenDialog()));
        advancedActionsLayout.addView(row2);

        LinearLayout row3 = new LinearLayout(this);
        row3.setOrientation(LinearLayout.HORIZONTAL);
        row3.setGravity(android.view.Gravity.CENTER);
        row3.addView(createSmallButton("人工查物料", v -> showItemSearchDialog()));
        row3.addView(createSmallButton("更正记录", v -> showCorrectDialog()));
        row3.addView(createSmallButton("作废记录", v -> showVoidDialog()));
        advancedActionsLayout.addView(row3);

        LinearLayout row4 = new LinearLayout(this);
        row4.setOrientation(LinearLayout.HORIZONTAL);
        row4.setGravity(android.view.Gravity.CENTER);
        row4.addView(createSmallButton("刷新库位", v -> refreshLocationResult()));
        row4.addView(createSmallButton("完成库位", v -> completeLocation()));
        advancedActionsLayout.addView(row4);

        LinearLayout row5 = new LinearLayout(this);
        row5.setOrientation(LinearLayout.HORIZONTAL);
        row5.setGravity(android.view.Gravity.CENTER);
        row5.addView(createSmallButton("搜索作业", v -> showJobSearchDialog()));
        advancedActionsLayout.addView(row5);

        advancedActionsLayout.setVisibility(View.GONE);
        panel.addView(advancedActionsLayout);

        root.addView(panel, 1);
    }

    private String acTypeName(int type) {
        switch (type) {
            case 1: return "零件账";
            case 2: return "成品账";
            case 13: return "线边仓账";
            case 14: return "车间仓账";
            default: return "未知账类";
        }
    }

    private Button createSmallButton(String text, View.OnClickListener listener) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextSize(12);
        button.setAllCaps(false);
        button.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        button.setOnClickListener(listener);
        return button;
    }

    private void initiateScan() {
        new IntentIntegrator(this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
    }

    private void onScanText(String text) {
        if (isProcessingScan) {
            return;
        }
        if (TextUtils.isEmpty(text)) {
            return;
        }
        long now = System.currentTimeMillis();
        if (text.equals(lastProcessedBarcode) && now - lastProcessedBarcodeTime < 1500L) {
            return;
        }
        lastProcessedBarcode = text;
        lastProcessedBarcodeTime = now;
        isProcessingScan = true;

        if ("Scope".equals(scanFor)) {
            handleScopeScan(text);
        } else if ("Task".equals(scanFor)) {
            handleTaskScan(text);
        } else if ("Location".equals(scanFor)) {
            scanLocation(text);
        } else if ("Item".equals(scanFor)) {
            resolveItem(text);
        } else {
            isProcessingScan = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {
            onScanText(result.getContents());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleScopeScan(String barcode) {
        new ScopeScanAsyncTask().execute(barcode);
    }

    private void handleTaskScan(String barcode) {
        new TaskLabelResolveAsyncTask().execute(barcode);
    }

    private void selectItem(JSONObject item) {
        itemId = item.optLong("Item_ID", 0);
        ivId = item.optLong("IV_ID", 0);
        psId = item.optLong("PS_ID", 0);
        productId = item.optLong("Product_ID", 0);
        lotId = item.optLong("LotID", 0);
        statusId = item.optLong("Status_ID", 0);
        tvItemCode.setText(item.optString("ItemCode", item.optString("Item_Code", item.optString("Item", ""))));
        tvItemName.setText(item.optString("ItemName", item.optString("Item_Name", "")));
        String systemLot = item.optString("LotNo", "");
        tvManuLotno.setText(TextUtils.isEmpty(systemLot)
                ? "未指定系统批次" : systemLot + " / LotID " + item.optLong("LotID", 0));
        etManualLotNo.setText("");
        etN.setText("1");
        etPN.setText(item.optString("PackagingQtySuggested", ""));
        etDQ.setText("");
        etRemark.setText("");
    }

    private void showItemCandidateDialog(List<JSONObject> items) {
        String[] names = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            JSONObject item = items.get(i);
            names[i] = item.optString("ItemCode", item.optString("Item", ""))
                    + "  " + item.optString("ItemName", "")
                    + "\n版本 " + item.optString("ItemVersion", "")
                    + "  状态 " + item.optString("StatusName", String.valueOf(item.optInt("Status_ID", 0)));
        }
        new AlertDialog.Builder(this)
                .setTitle("请核对并选择物料")
                .setItems(names, (dialog, which) -> selectItem(items.get(which)))
                .setNegativeButton("取消", (dialog, which) -> resetLocationItemState())
                .show();
    }

    private void showItemSearchDialog() {
        final EditText input = new EditText(this);
        input.setHint("输入物料编码、名称或批次");
        new AlertDialog.Builder(this)
                .setTitle("人工查询物料")
                .setView(input)
                .setPositiveButton("查询", (dialog, which) -> {
                    String keyword = input.getText().toString().trim();
                    if (keyword.isEmpty()) {
                        ToastUtil.showToastShort("请输入查询内容");
                        return;
                    }
                    new ItemSearchAsyncTask().execute(keyword, 0);
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void showJobSearchDialog() {
        final EditText input = new EditText(this);
        input.setHint("输入作业名称");
        new AlertDialog.Builder(this)
                .setTitle("搜索盘点作业")
                .setView(input)
                .setPositiveButton("查询", (dialog, which) ->
                        new JobQueryAsyncTask("Jobs", input.getText().toString().trim()).execute())
                .setNegativeButton("取消", null)
                .show();
    }

    private void showCreateJobDialog() {
        final EditText nameEdit = new EditText(this);
        nameEdit.setText("仓库盘点作业" + new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(new Date()));
        final EditText dateEdit = new EditText(this);
        dateEdit.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        final EditText remarkEdit = new EditText(this);
        remarkEdit.setHint("作业说明");
        final Spinner categorySpinner = new Spinner(this);
        categorySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"大盘", "日常盘点"}));
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.addView(nameEdit);
        panel.addView(dateEdit);
        panel.addView(remarkEdit);
        panel.addView(categorySpinner);
        new AlertDialog.Builder(this)
                .setTitle("新建盘点作业")
                .setView(panel)
                .setPositiveButton("创建并启动", (dialog, which) ->
                        createJob(nameEdit.getText().toString(), dateEdit.getText().toString(),
                                categorySpinner.getSelectedItemPosition() + 1,
                                remarkEdit.getText().toString()))
                .setNegativeButton("取消", null)
                .show();
    }

    private void showCreateSheetDialog() {
        if (countSessionId <= 0) {
            ToastUtil.showToastShort("请先选择盘点作业");
            return;
        }
        final EditText nameEdit = new EditText(this);
        nameEdit.setHint("表名，必填");
        final EditText areaEdit = new EditText(this);
        areaEdit.setHint("责任区域，1-500字");
        final Spinner purposeSpinner = new Spinner(this);
        purposeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"正常盘点表", "复核表"}));
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.addView(nameEdit);
        panel.addView(areaEdit);
        panel.addView(purposeSpinner);
        new AlertDialog.Builder(this)
                .setTitle("新建盘点表")
                .setView(panel)
                .setPositiveButton("创建", (dialog, which) -> {
                    String name = nameEdit.getText().toString().trim();
                    String area = areaEdit.getText().toString().trim();
                    if (name.isEmpty()) {
                        ToastUtil.showToastShort("表名不能为空");
                        return;
                    }
                    if (area.length() < 1 || area.length() > 500) {
                        ToastUtil.showToastShort("责任区域长度必须为1-500");
                        return;
                    }
                    try {
                        JSONObject json = jobActionBase("CreateSheet", "");
                        json.put("Purpose", purposeSpinner.getSelectedItemPosition());
                        json.put("Name", name);
                        json.put("WorkAreaDescription", area);
                        new JobActionAsyncTask().execute(json.toString());
                    } catch (Exception e) {
                        ToastUtil.showToastShort("新建盘点表参数错误");
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void createJob(String name, String date, int category, String remark) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(date)) {
            ToastUtil.showToastShort("名称和日期不能为空");
            return;
        }
        try {
            JSONObject json = new JSONObject();
            json.put("Warehouse_ID", warehouseId);
            json.put("Bu_ID", buId);
            json.put("Ac_Type", acType);
            json.put("CountSession_ID", 0);
            json.put("Action", "Create");
            json.put("StateVersion", "");
            json.put("Name", name);
            json.put("Remark", remark);
            json.put("CheckDate", date);
            json.put("CountCategory", category);
            json.put("Reason", "");
            json.put("Request_ID", UUID.randomUUID().toString());
            new JobActionAsyncTask().execute(json.toString());
        } catch (Exception e) {
            ToastUtil.showToastShort("新建作业参数错误");
        }
    }

    private void showFinalizeLabelsDialog() {
        if (countSessionId <= 0 || ciId <= 0 || purpose != 0) {
            ToastUtil.showToastShort("请先选择本人正常盘点表");
            return;
        }
        final EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setHint("本表最后贴号，允许0");
        new AlertDialog.Builder(this)
                .setTitle("确认结束贴号")
                .setView(input)
                .setPositiveButton("确认", (dialog, which) -> {
                    int no;
                    try {
                        no = Integer.parseInt(input.getText().toString());
                    } catch (Exception e) {
                        ToastUtil.showToastShort("请输入非负整数");
                        return;
                    }
                    if (no < 0) {
                        ToastUtil.showToastShort("结束贴号不能为负数");
                        return;
                    }
                    try {
                        JSONObject json = jobActionBase("FinalizeSheetLabels", "");
                        json.put("CI_ID", ciId);
                        json.put("No", no);
                        new JobActionAsyncTask().execute(json.toString());
                    } catch (Exception e) {
                        ToastUtil.showToastShort("结束贴号参数错误");
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void showCloseJobDialog() {
        if (countSessionId <= 0) {
            ToastUtil.showToastShort("请先选择作业");
            return;
        }
        final EditText input = new EditText(this);
        input.setHint("结束原因");
        new AlertDialog.Builder(this)
                .setTitle("结束盘点作业")
                .setMessage("未盘点项目不会按零处理；结束前请先查看结果。")
                .setView(input)
                .setPositiveButton("确认结束", (dialog, which) -> {
                    if (TextUtils.isEmpty(input.getText().toString())) {
                        ToastUtil.showToastShort("请填写结束原因");
                        return;
                    }
                    try {
                        JSONObject json = jobActionBase("Close", input.getText().toString());
                        json.put("ConfirmIncomplete", true);
                        new JobActionAsyncTask().execute(json.toString());
                    } catch (Exception e) {
                        ToastUtil.showToastShort("结束作业参数错误");
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void showReopenDialog() {
        if (countSessionId <= 0 || ciId <= 0) {
            ToastUtil.showToastShort("请先选择作业和盘点表");
            return;
        }
        final EditText input = new EditText(this);
        input.setHint("重新盘点原因");
        new AlertDialog.Builder(this)
                .setTitle("重新盘点")
                .setMessage("新轮次将替代旧轮次；实盘重开后原复核失效。")
                .setView(input)
                .setPositiveButton("确认", (dialog, which) -> {
                    if (TextUtils.isEmpty(input.getText().toString())) {
                        ToastUtil.showToastShort("请填写原因");
                        return;
                    }
                    try {
                        JSONObject json = jobActionBase(countCategory == 1 ? "ReopenTask" : "Reopen",
                                input.getText().toString());
                        json.put("CI_ID", ciId);
                        json.put("Purpose", purpose);
                        if (countCategory == 1) {
                            json.put("LabelCI_ID", labelCiId);
                            json.put("No", taskNo);
                        } else {
                            json.put("IST_ID", istId);
                            json.put("Sub_IST_ID", subIstId);
                        }
                        new JobActionAsyncTask().execute(json.toString());
                    } catch (Exception e) {
                        ToastUtil.showToastShort("重新盘点参数错误");
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void showResultsViewDialog() {
        if (countSessionId <= 0) {
            ToastUtil.showToastShort("请先选择作业");
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("结果查询")
                .setItems(new String[]{"进度", "差异结果", "操作记录"}, (dialog, which) -> {
                    String view = which == 0 ? "Progress" : which == 1 ? "Results" : "Operations";
                    new JobQueryAsyncTask(view).execute();
                })
                .show();
    }

    private void showResultInfoDialog(String view, String json) {
        try {
            JSONObject root = new JSONObject(json);
            StringBuilder text = new StringBuilder();
            if ("Progress".equals(view)) {
                appendArray(text, root, "Progress", "任务");
                appendArray(text, root, "Locations", "库位");
            } else if ("Results".equals(view)) {
                appendArray(text, root, "Differences", "差异");
                appendArray(text, root, "ReviewDifference", "复核差异");
                appendArray(text, root, "ReviewComparison", "复核比较");
                appendArray(text, root, "UnitReviewComparison", "轮次复核比较");
                appendArray(text, root, "InventoryComparison", "库存对照");
            } else {
                appendArray(text, root, "Operations", "操作记录");
            }
            new AlertDialog.Builder(this)
                    .setTitle("结果查询 - " + view)
                    .setMessage(text.length() == 0 ? "没有查询到数据" : text.toString())
                    .setPositiveButton("关闭", null)
                    .show();
        } catch (Exception e) {
            ToastUtil.showToastShort("结果数据解析失败");
        }
    }

    private void appendArray(StringBuilder text, JSONObject root, String key, String title) {
        JSONArray array = root.optJSONArray(key);
        if (array == null) {
            return;
        }
        if (text.length() > 0) {
            text.append("\n");
        }
        text.append(title).append("：\n");
        for (int i = 0; i < array.length(); i++) {
            text.append(array.optJSONObject(i).toString()).append("\n");
        }
    }

    private void showCorrectDialog() {
        showRecordActionDialog(true);
    }

    private void showVoidDialog() {
        showRecordActionDialog(false);
    }

    private void showRecordActionDialog(final boolean correct) {
        if (currentRecords.isEmpty()) {
            ToastUtil.showToastShort("当前库位没有可操作记录");
            return;
        }
        String[] items = new String[currentRecords.size()];
        for (int i = 0; i < currentRecords.size(); i++) {
            JSONObject record = currentRecords.get(i);
            items[i] = "记录 " + record.optLong("CII_ID", 0)
                    + "  " + record.optString("ItemCode", record.optString("Item", ""))
                    + "  数量 " + record.optString("Qty", "");
        }
        new AlertDialog.Builder(this)
                .setTitle(correct ? "选择要更正的记录" : "选择要作废的记录")
                .setItems(items, (dialog, which) -> {
                    JSONObject record = currentRecords.get(which);
                    showRecordReasonDialog(correct, record);
                })
                .show();
    }

    private void showRecordReasonDialog(final boolean correct, final JSONObject record) {
        final EditText reasonEdit = new EditText(this);
        reasonEdit.setHint("必须填写原因");
        final EditText qtyEdit = new EditText(this);
        qtyEdit.setText(record.optString("Qty", realQtyTextView.getText().toString()));
        final EditText packageEdit = new EditText(this);
        packageEdit.setText(record.optString("PackageN", etN.getText().toString()));
        final EditText singleEdit = new EditText(this);
        singleEdit.setText(record.optString("SinglePackageQty", etPN.getText().toString()));
        final EditText discreteEdit = new EditText(this);
        discreteEdit.setText(record.optString("DiscreteQty", etDQ.getText().toString()));
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.addView(reasonEdit);
        if (correct) {
            panel.addView(qtyEdit);
            panel.addView(packageEdit);
            panel.addView(singleEdit);
            panel.addView(discreteEdit);
        }
        new AlertDialog.Builder(this)
                .setTitle(correct ? "更正记录" : "作废记录")
                .setView(panel)
                .setPositiveButton("确认", (dialog, which) -> {
                    String reason = reasonEdit.getText().toString().trim();
                    if (reason.isEmpty()) {
                        ToastUtil.showToastShort("原因不能为空");
                        return;
                    }
                    try {
                        JSONObject json = buildBaseInventoryJson();
                        json.put("CII_ID", record.optLong("CII_ID", 0));
                        json.put("Reason", reason);
                        json.put("Request_ID", UUID.randomUUID().toString());
                        if (correct) {
                            json.put("Qty", qtyEdit.getText().toString());
                            json.put("PackageN", packageEdit.getText().toString());
                            json.put("SinglePackageQty", singleEdit.getText().toString());
                            json.put("DiscreteQty", discreteEdit.getText().toString());
                            json.put("Remark", etRemark.getText().toString());
                            new RecordWriteAsyncTask("Correct").execute(json.toString());
                        } else {
                            new RecordWriteAsyncTask("Void").execute(json.toString());
                        }
                    } catch (Exception e) {
                        ToastUtil.showToastShort("记录操作参数错误");
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private JSONObject jobActionBase(String action, String reason) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("Warehouse_ID", warehouseId);
        json.put("Bu_ID", buId);
        json.put("Ac_Type", acType);
        json.put("CountSession_ID", countSessionId);
        json.put("Action", action);
        json.put("StateVersion", sessionStateVersion);
        json.put("Name", countSessionName);
        json.put("Remark", "");
        json.put("CheckDate", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        json.put("CountCategory", countCategory);
        json.put("Reason", reason == null ? "" : reason);
        json.put("Request_ID", UUID.randomUUID().toString());
        return json;
    }

    private void bindRecords(JSONArray records) {
        currentRecords.clear();
        if (records != null) {
            for (int i = 0; i < records.length(); i++) {
                currentRecords.add(records.optJSONObject(i));
            }
        }
        savedItemCountInLocation = currentRecords.size();
        if (tvRecords != null) {
            tvRecords.setText("本轮已保存：" + currentRecords.size() + "条");
        }
    }

    private void updateTaskInfo() {
        if (tvTaskInfo != null) {
            tvTaskInfo.setText(taskNo > 0
                    ? "任务码：表 " + labelCiId + " 第 " + taskNo + " 号"
                    : "任务码：未扫描");
        }
    }

    private void completeLargeCountTask() {
        new CompleteAsyncTask().execute(false);
    }

    private JSONObject buildBaseInventoryJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("Warehouse_ID", warehouseId);
        json.put("Bu_ID", buId);
        json.put("Ac_Type", acType);
        json.put("CountSession_ID", countSessionId);
        json.put("CI_ID", ciId);
        json.put("Purpose", purpose);
        json.put("IST_ID", istId);
        json.put("Sub_IST_ID", subIstId);
        json.put("CountUnit_ID", countUnitId);
        json.put("CompareMode", compareMode);
        json.put("StateVersion", locationStateVersion);
        if (labelCiId > 0 || taskNo > 0) {
            json.put("LabelCI_ID", labelCiId);
            json.put("No", taskNo);
        }
        return json;
    }

    private void scanLocation(String barcode) {
        resetAllAfterComplete();
        locationBarcode = barcode;
        new ScanLocationAsyncTask().execute(barcode);
    }

    private void resolveItem(String barcode) {
        // codex by john 2026-11-17 - 同一库位同一标签只允许保存一次
        if (savedItemScanSet.contains(barcode)) {
            isProcessingScan = false;
            inputEditText.setText("");
            ToastUtil.showToastShort("该标签已保存，请勿重复盘点");
            return;
        }
        itemScanX = barcode;
        new ResolveItemAsyncTask().execute(barcode);
    }

    private void saveCurrentItem() {
        if (isSaving) {
            return;
        }
        if (ciId <= 0 || istId <= 0) {
            ToastUtil.showToastShort("请先选择作业并扫描库位");
            return;
        }
        if (!canInput) {
            ToastUtil.showToastShort("当前库位不可继续录入");
            return;
        }
        if (itemId <= 0) {
            ToastUtil.showToastShort("请先扫描物料");
            return;
        }
        if (savedItemScanSet.contains(itemScanX)) {
            ToastUtil.showToastShort("该标签已保存，请勿重复保存");
            resetLocationItemState();
            return;
        }
        updateRealQty();
        String qty = realQtyTextView.getText().toString();
        if (TextUtils.isEmpty(qty)) {
            ToastUtil.showToastShort("数量为空");
            return;
        }
        new SaveAsyncTask().execute(qty);
    }

    private void refreshLocationResult() {
        if (ciId <= 0 || istId <= 0) {
            ToastUtil.showToastShort("请先扫描库位");
            return;
        }
        new LocationResultAsyncTask().execute();
    }

    private void completeLocation() {
        if (isCompleting) {
            return;
        }
        if (ciId <= 0 || istId <= 0) {
            ToastUtil.showToastShort("请先扫描库位");
            return;
        }
        boolean empty = savedItemCountInLocation <= 0;
        if (empty) {
            new AlertDialog.Builder(this)
                    .setTitle("确认空库位")
                    .setMessage("当前库位没有保存明细，确认按空库位完成吗？")
                    .setPositiveButton("确认", (dialog, which) -> new CompleteAsyncTask().execute(true))
                    .setNegativeButton("取消", null)
                    .show();
        } else {
            new CompleteAsyncTask().execute(false);
        }
    }

    private void updateRealQty() {
        BigDecimal n = parseDecimal(etN.getText().toString());
        BigDecimal pn = parseDecimal(etPN.getText().toString());
        BigDecimal dq = parseDecimal(etDQ.getText().toString());
        BigDecimal qty = n.multiply(pn).add(dq).stripTrailingZeros();
        realQtyTextView.setText(qty.toPlainString());
    }

    private BigDecimal parseDecimal(String value) {
        if (TextUtils.isEmpty(value)) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private float parseFloat(String value) {
        if (TextUtils.isEmpty(value)) {
            return 0f;
        }
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            return 0f;
        }
    }

    private long optLong(JSONObject json, String key) {
        return json == null ? 0L : json.optLong(key, 0L);
    }

    private int optInt(JSONObject json, String key) {
        return json == null ? 0 : json.optInt(key, 0);
    }

    private JSONObject firstObject(JSONObject root, String key) {
        if (root == null) {
            return null;
        }
        JSONObject obj = root.optJSONObject(key);
        if (obj != null) {
            return obj;
        }
        JSONArray arr = root.optJSONArray(key);
        if (arr != null && arr.length() > 0) {
            return arr.optJSONObject(0);
        }
        return null;
    }

    private void showError(WsResult result, String fallback) {
        String msg = result != null && !TextUtils.isEmpty(result.getErrorInfo()) ? result.getErrorInfo() : fallback;
        ToastUtil.showToastShort(msg);
    }

    private void resetLocationItemState() {
        itemId = 0;
        ivId = 0;
        psId = 0;
        productId = 0;
        lotId = 0;
        statusId = 0;
        itemScanX = "";
        tvItemCode.setText("......");
        tvItemName.setText("......");
        tvManuLotno.setText("......");
        etManualLotNo.setText("");
        etN.setText("");
        etPN.setText("");
        etDQ.setText("");
        etRemark.setText("");
        realQtyTextView.setText("");
    }

    private void resetAllAfterComplete() {
        istId = 0;
        subIstId = 0;
        countUnitId = 0;
        compareMode = 0;
        canInput = false;
        locationBarcode = "";
        locationStateVersion = "";
        savedItemCountInLocation = 0;
        currentRecords.clear();
        currentItems.clear();
        savedItemScanSet.clear();
        tvIst.setText("......");
        tvERPIst.setText("......");
        if (tvRecords != null) {
            tvRecords.setText("本轮已保存：0条");
        }
        resetLocationItemState();
    }

    private class TaskLabelResolveAsyncTask extends AsyncTask<String, Void, WsResult> {
        @Override
        protected WsResult doInBackground(String... params) {
            try {
                JSONObject json = parseTaskCode(params[0]);
                return json == null ? null
                        : WebServiceUtil.op_CheckInventory_TaskLabelResolveV3_New(json.toString());
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(WsResult result) {
            isProcessingScan = false;
            if (result == null || !result.getResult()) {
                showError(result, "解析任务码失败");
                return;
            }
            try {
                JSONObject root = new JSONObject(result.getInfo());
                JSONObject label = firstObject(root, "Label");
                if (label != null) {
                    labelCiId = label.optLong("LabelCI_ID", label.optLong("CI_ID", labelCiId));
                    taskNo = label.optInt("No", taskNo);
                }
                JSONObject status = firstObject(root, "Result");
                if (status != null) {
                    labelCiId = status.optLong("LabelCI_ID", labelCiId);
                    taskNo = status.optInt("No", taskNo);
                    countSessionId = status.optLong("CountSession_ID", countSessionId);
                    if (!status.optBoolean("Accepted", true)) {
                        ToastUtil.showToastShort(status.optString("Message", "任务码未被接受"));
                        return;
                    }
                }
                updateTaskInfo();
                confirmTaskCode();
            } catch (Exception e) {
                ToastUtil.showToastShort("任务码解析结果失败");
            }
        }
    }

    private void confirmTaskCode() {
        if (countSessionId <= 0 || ciId <= 0 || labelCiId <= 0 || taskNo <= 0) {
            ToastUtil.showToastShort("任务码缺少作业、表或序号");
            return;
        }
        try {
            JSONObject json = new JSONObject();
            json.put("CountSession_ID", countSessionId);
            json.put("Warehouse_ID", warehouseId);
            json.put("Bu_ID", buId);
            json.put("Ac_Type", acType);
            json.put("LabelCI_ID", labelCiId);
            json.put("No", taskNo);
            json.put("CI_ID", ciId);
            json.put("Purpose", purpose);
            new ConfirmTaskAsyncTask().execute(json.toString());
        } catch (Exception e) {
            ToastUtil.showToastShort("任务确认参数错误");
        }
    }

    private class ConfirmTaskAsyncTask extends AsyncTask<String, Void, WsResult> {
        @Override
        protected WsResult doInBackground(String... params) {
            return WebServiceUtil.op_CheckInventory_TaskLabelResolveV3_New(params[0]);
        }

        @Override
        protected void onPostExecute(WsResult result) {
            if (result == null || !result.getResult()) {
                showError(result, "任务确认失败");
                return;
            }
            try {
                JSONObject root = new JSONObject(result.getInfo());
                JSONObject status = firstObject(root, "Result");
                if (status == null || status.optBoolean("Accepted", true)) {
                    resetAllAfterComplete();
                    ToastUtil.showToastShort("任务确认成功，请扫描库位");
                } else {
                    ToastUtil.showToastShort(status.optString("Message", "任务确认未被接受"));
                }
            } catch (Exception e) {
                ToastUtil.showToastShort("任务确认成功，但解析失败");
            }
        }
    }

    private class ItemSearchAsyncTask extends AsyncTask<Object, Void, WsResult> {
        @Override
        protected WsResult doInBackground(Object... params) {
            try {
                JSONObject json = buildBaseInventoryJson();
                json.put("SearchText", params[0]);
                json.put("SearchPage", params[1]);
                return WebServiceUtil.op_CheckInventory_ItemSearchV3_New(json.toString());
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(WsResult result) {
            if (result == null || !result.getResult()) {
                showError(result, "查询物料失败");
                return;
            }
            try {
                JSONObject root = new JSONObject(result.getInfo());
                JSONArray items = root.optJSONArray("Items");
                currentItems.clear();
                if (items != null) {
                    for (int i = 0; i < items.length(); i++) {
                        currentItems.add(items.getJSONObject(i));
                    }
                }
                if (currentItems.isEmpty()) {
                    ToastUtil.showToastShort("没有查询到物料");
                } else if (currentItems.size() == 1) {
                    selectItem(currentItems.get(0));
                } else {
                    showItemCandidateDialog(currentItems);
                }
            } catch (Exception e) {
                ToastUtil.showToastShort("查询物料结果解析失败");
            }
        }
    }

    private class JobActionAsyncTask extends AsyncTask<String, Void, WsResult> {
        @Override
        protected WsResult doInBackground(String... params) {
            return WebServiceUtil.op_CheckInventory_JobActionV3_New(params[0]);
        }

        @Override
        protected void onPostExecute(WsResult result) {
            if (result == null || !result.getResult()) {
                showError(result, "作业操作失败");
                return;
            }
            try {
                JSONObject root = new JSONObject(result.getInfo());
                JSONObject status = firstObject(root, "Result");
                if (status != null && !status.optBoolean("Accepted", true)) {
                    ToastUtil.showToastShort(status.optString("Message", "作业操作未被接受"));
                    return;
                }
                long newSessionId = status == null ? 0 : status.optLong("CountSession_ID", 0);
                if (newSessionId > 0) {
                    countSessionId = newSessionId;
                }
                ToastUtil.showToastShort(status == null ? "作业操作成功" : status.optString("Message", "作业操作成功"));
                if (countSessionId > 0) {
                    new JobQueryAsyncTask("Dashboard").execute();
                } else {
                    new JobQueryAsyncTask("Jobs").execute();
                }
            } catch (Exception e) {
                ToastUtil.showToastShort("作业操作成功，但解析失败");
            }
        }
    }

    private class RecordWriteAsyncTask extends AsyncTask<String, Void, WsResult> {
        private final String operation;

        RecordWriteAsyncTask(String operation) {
            this.operation = operation;
        }

        @Override
        protected WsResult doInBackground(String... params) {
            if ("Correct".equals(operation)) {
                return WebServiceUtil.op_CheckInventory_CorrectV3_New(params[0]);
            }
            return WebServiceUtil.op_CheckInventory_VoidV3_New(params[0]);
        }

        @Override
        protected void onPostExecute(WsResult result) {
            if (result == null || !result.getResult()) {
                showError(result, operation + "操作失败");
                return;
            }
            ToastUtil.showToastShort(operation + "操作成功");
            refreshLocationResult();
        }
    }

    private JSONObject parseTaskCode(String value) throws JSONException {
        String text = value == null ? "" : value.trim();
        java.util.regex.Matcher path = java.util.regex.Pattern.compile("/ic/(\\d+)/(\\d+)").matcher(text);
        if (path.find()) {
            JSONObject json = new JSONObject();
            json.put("LabelCI_ID", Long.parseLong(path.group(1)));
            json.put("No", Integer.parseInt(path.group(2)));
            return json;
        }
        java.util.regex.Matcher ci = java.util.regex.Pattern.compile("CI_ID=(\\d+)").matcher(text);
        java.util.regex.Matcher no = java.util.regex.Pattern.compile("No=(\\d+)").matcher(text);
        if (ci.find() && no.find()) {
            JSONObject json = new JSONObject();
            json.put("LabelCI_ID", Long.parseLong(ci.group(1)));
            json.put("No", Integer.parseInt(no.group(1)));
            return json;
        }
        return null;
    }

    private class ScopeListAsyncTask extends AsyncTask<Void, Void, WsResult> {
        @Override
        protected WsResult doInBackground(Void... params) {
            return WebServiceUtil.op_CheckInventory_ScopeListV2(buId, warehouseId, acType,
                    UserSingleton.get().getHRName(), "");
        }

        @Override
        protected void onPostExecute(WsResult result) {
            if (result == null || !result.getResult()) {
                showError(result, "获取草稿作业失败");
                return;
            }
            showScopeSessionDialog(result.getInfo());
        }
    }

    private void showScopeSessionDialog(String json) {
        try {
            JSONObject root = new JSONObject(json);
            JSONArray sessions = root.optJSONArray("Sessions");
            if (sessions == null || sessions.length() == 0) {
                ToastUtil.showToastShort("没有可用的草稿作业");
                return;
            }
            String[] items = new String[sessions.length()];
            for (int i = 0; i < sessions.length(); i++) {
                JSONObject bean = sessions.getJSONObject(i);
                items[i] = bean.optString("CountSession_Name", "")
                        + "\n作业ID：" + bean.optLong("CountSession_ID", 0)
                        + " " + bean.optString("ScopeStatusName", "");
            }
            new AlertDialog.Builder(StockCheckPartInvCommonNewActivity.this)
                    .setTitle("选择草稿作业")
                    .setItems(items, (dialog, which) -> {
                        JSONObject bean = sessions.optJSONObject(which);
                        if (bean != null) {
                            scopeSessionId = bean.optLong("CountSession_ID", 0);
                            scopeSessionName = bean.optString("CountSession_Name", "");
                            scopeEditable = bean.optBoolean("ScopeEditable", false);
                            tvScopeSession.setText("已选择：" + scopeSessionName
                                    + (scopeEditable ? "（可扫范围）" : "（已启用/结束，仅可查看）"));
                        }
                    })
                    .show();
        } catch (Exception e) {
            tvScopeSession.setText("解析草稿作业失败");
        }
    }

    private class ScopeLocationsAsyncTask extends AsyncTask<Void, Void, WsResult> {
        @Override
        protected WsResult doInBackground(Void... params) {
            return WebServiceUtil.op_CheckInventory_ScopeLocationsV2((int) scopeSessionId, buId, warehouseId, acType,
                    UserSingleton.get().getHRName(), "");
        }

        @Override
        protected void onPostExecute(WsResult result) {
            if (result == null || !result.getResult()) {
                showError(result, "查看范围失败");
                return;
            }
            try {
                JSONObject root = new JSONObject(result.getInfo());
                JSONArray locations = root.optJSONArray("Locations");
                int count = locations == null ? 0 : locations.length();
                tvScopeSession.setText("当前范围：" + count + " 个库位");
            } catch (Exception e) {
                tvScopeSession.setText("范围信息：" + result.getInfo());
            }
        }
    }

    private class ScopeScanAsyncTask extends AsyncTask<String, Void, WsResult> {
        @Override
        protected WsResult doInBackground(String... params) {
            return WebServiceUtil.op_CheckInventory_ScopeScanV2(params[0], (int) scopeSessionId, buId, warehouseId, acType,
                    UserSingleton.get().getHRName(), "");
        }

        @Override
        protected void onPostExecute(WsResult result) {
            isProcessingScan = false;
            if (result == null || !result.getResult()) {
                showError(result, "扫描范围失败");
                return;
            }
            try {
                JSONObject root = new JSONObject(result.getInfo());
                JSONObject status = firstObject(root, "Result");
                if (status != null) {
                    tvScopeSession.setText(status.optString("Message", "扫描范围成功"));
                    if (!status.optBoolean("Accepted", true)) {
                        ToastUtil.showToastShort(status.optString("Message", "扫描范围未被接受"));
                    }
                }
            } catch (Exception e) {
                tvScopeSession.setText("扫描范围成功：" + result.getInfo());
            }
        }
    }

    private class JobQueryAsyncTask extends AsyncTask<Void, Void, WsResult> {
        private final String view;
        private final String searchText;

        JobQueryAsyncTask(String view) {
            this(view, "");
        }

        JobQueryAsyncTask(String view, String searchText) {
            this.view = view;
            this.searchText = searchText;
        }

        @Override
        protected WsResult doInBackground(Void... params) {
            try {
                JSONObject json = new JSONObject();
                json.put("Warehouse_ID", warehouseId);
                json.put("Bu_ID", buId);
                json.put("Ac_Type", acType);
                json.put("View", view);
                json.put("CountSession_ID", view.equals("Jobs") ? 0 : countSessionId);
                json.put("SearchText", searchText);
                return WebServiceUtil.op_CheckInventory_JobQueryV3_New(json.toString());
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(WsResult result) {
            if (result == null || !result.getResult()) {
                showError(result, "查询盘点作业失败");
                return;
            }
            if ("Jobs".equals(view)) {
                showJobSelectDialog(result.getInfo());
            } else if ("Dashboard".equals(view)) {
                showSheetSelectDialog(result.getInfo());
            } else {
                showResultInfoDialog(view, result.getInfo());
            }
        }
    }

    private void showJobSelectDialog(String json) {
        try {
            JSONObject root = new JSONObject(json);
            JSONArray sessions = root.optJSONArray("Sessions");
            if (sessions == null || sessions.length() == 0) {
                ToastUtil.showToastShort("没有可用的盘点作业");
                showCreateJobDialog();
                return;
            }
            String[] items = new String[sessions.length()];
            for (int i = 0; i < sessions.length(); i++) {
                JSONObject bean = sessions.getJSONObject(i);
                items[i] = bean.optString("CountSession_Name", "") + "\n作业ID：" + bean.optLong("CountSession_ID", 0);
            }
            new AlertDialog.Builder(StockCheckPartInvCommonNewActivity.this)
                    .setTitle("选择盘点作业")
                    .setItems(items, (dialog, which) -> {
                        JSONObject bean = sessions.optJSONObject(which);
                        if (bean != null) {
                            countSessionId = bean.optLong("CountSession_ID", 0);
                            countSessionName = bean.optString("CountSession_Name", "");
                            sessionStateVersion = bean.optString("StateVersion", "");
                            countCategory = bean.optInt("CountCategory", 0);
                            sessionStatus = bean.optInt("SessionStatus", 0);
                            ciId = 0;
                            purpose = 0;
                            labelCiId = 0;
                            taskNo = 0;
                            resetAllAfterComplete();
                            updateTaskInfo();
                            tvInventorySession.setText("已选作业：" + countSessionName);
                            new JobQueryAsyncTask("Dashboard").execute();
                        }
                    })
                    .setNeutralButton("新建作业", (dialog, which) -> showCreateJobDialog())
                    .show();
        } catch (Exception e) {
            ToastUtil.showToastShort("解析盘点作业失败");
        }
    }

    private void showSheetSelectDialog(String json) {
        try {
            JSONObject root = new JSONObject(json);
            JSONObject session = firstObject(root, "Session");
            if (session != null) {
                sessionStateVersion = session.optString("StateVersion", sessionStateVersion);
                countCategory = session.optInt("CountCategory", countCategory);
                sessionStatus = session.optInt("SessionStatus", sessionStatus);
            }
            JSONArray sheets = root.optJSONArray("Sheets");
            if (sheets == null || sheets.length() == 0) {
                ToastUtil.showToastShort("该作业没有可用盘点表");
                showCreateSheetDialog();
                return;
            }
            String[] items = new String[sheets.length()];
            for (int i = 0; i < sheets.length(); i++) {
                JSONObject bean = sheets.getJSONObject(i);
                int p = bean.optInt("Purpose", 0);
                items[i] = "表ID：" + bean.optLong("CI_ID", 0)
                        + " " + (p == 0 ? "正常盘点" : "复核")
                        + "\n" + bean.optString("CI_Name", "");
            }
            new AlertDialog.Builder(StockCheckPartInvCommonNewActivity.this)
                    .setTitle("选择盘点表")
                    .setItems(items, (dialog, which) -> {
                        JSONObject bean = sheets.optJSONObject(which);
                        if (bean != null) {
                            ciId = bean.optLong("CI_ID", 0);
                            purpose = bean.optInt("Purpose", 0);
                            tvInventorySession.setText("作业：" + countSessionName + " 表：" + ciId
                                    + (purpose == 0 ? " 正常盘点" : " 复核"));
                            resetAllAfterComplete();
                        }
                    })
                    .setNeutralButton("新建盘点表", (dialog, which) -> showCreateSheetDialog())
                    .show();
        } catch (Exception e) {
            ToastUtil.showToastShort("解析盘点表失败");
        }
    }

    private class ScanLocationAsyncTask extends AsyncTask<String, Void, WsResult> {
        @Override
        protected WsResult doInBackground(String... params) {
            try {
                JSONObject json = buildBaseInventoryJson();
                json.put("LocationBarcode", params[0]);
                return WebServiceUtil.op_CheckInventory_ScanLocationV3_New(json.toString());
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(WsResult result) {
            isProcessingScan = false;
            if (result == null || !result.getResult()) {
                showError(result, "扫描库位失败");
                return;
            }
            try {
                JSONObject root = new JSONObject(result.getInfo());
                JSONObject status = firstObject(root, "Result");
                if (status == null) {
                    status = new JSONObject();
                }
                if (!status.optBoolean("Accepted", true)) {
                    ToastUtil.showToastShort(status.optString("Message", "库位未被接受"));
                    return;
                }
                JSONObject location = firstObject(root, "Location");
                if (location != null) {
                    istId = location.optLong("IST_ID", 0);
                    subIstId = location.optLong("Sub_IST_ID", 0);
                    locationStateVersion = location.optString("StateVersion", "");
                    String areaName = location.optString("AreaName", location.optString("IST_Name", ""));
                    String locationName = location.optString("LocationName", location.optString("Sub_Ist_Name", ""));
                    tvIst.setText(areaName);
                    tvERPIst.setText(locationName);
                }
                JSONObject scannedLocation = firstObject(root, "ScannedLocation");
                if (scannedLocation != null) {
                    String areaName = scannedLocation.optString("AreaName", "");
                    String locationName = scannedLocation.optString("LocationName", "");
                    if (!TextUtils.isEmpty(areaName)) tvIst.setText(areaName);
                    if (!TextUtils.isEmpty(locationName)) tvERPIst.setText(locationName);
                }
                countUnitId = status.optLong("CountUnit_ID", 0);
                compareMode = status.optInt("CompareMode", 0);
                canInput = status.optBoolean("CanInput", false);
                savedItemCountInLocation = 0;
                labelCiId = status.optLong("LabelCI_ID", labelCiId);
                taskNo = status.optInt("No", taskNo);
                bindRecords(root.optJSONArray("Records"));
                resetLocationItemState();
                updateTaskInfo();
                ToastUtil.showToastShort(status.optString("Message", "库位扫描成功"));
            } catch (Exception e) {
                ToastUtil.showToastShort("解析库位结果失败");
            }
        }
    }

    private class ResolveItemAsyncTask extends AsyncTask<String, Void, WsResult> {
        @Override
        protected WsResult doInBackground(String... params) {
            try {
                JSONObject json = buildBaseInventoryJson();
                json.put("ScanX", params[0]);
                return WebServiceUtil.op_CheckInventory_ResolveItemV3_New(json.toString());
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(WsResult result) {
            isProcessingScan = false;
            if (result == null || !result.getResult()) {
                showError(result, "解析物料失败");
                return;
            }
            try {
                JSONObject root = new JSONObject(result.getInfo());
                JSONArray items = root.optJSONArray("Items");
                if (items == null || items.length() == 0) {
                    ToastUtil.showToastShort("没有解析到物料");
                    return;
                }
                currentItems.clear();
                for (int i = 0; i < items.length(); i++) {
                    currentItems.add(items.getJSONObject(i));
                }
                if (currentItems.size() == 1) {
                    selectItem(currentItems.get(0));
                } else {
                    showItemCandidateDialog(currentItems);
                }
            } catch (Exception e) {
                ToastUtil.showToastShort("解析物料结果失败");
            }
        }
    }

    private class SaveAsyncTask extends AsyncTask<String, Void, WsResult> {
        private int packageN;
        private float singlePackageQty;
        private float discreteQty;
        private String manuLotNo;
        private String remark;

        @Override
        protected void onPreExecute() {
            isSaving = true;
            packageN = (int) parseFloat(etN.getText().toString());
            singlePackageQty = parseFloat(etPN.getText().toString());
            discreteQty = parseFloat(etDQ.getText().toString());
            manuLotNo = etManualLotNo.getText().toString();
            remark = etRemark.getText().toString();
        }

        @Override
        protected WsResult doInBackground(String... params) {
            try {
                JSONObject json = buildBaseInventoryJson();
                json.put("Request_ID", UUID.randomUUID().toString());
                json.put("Item_ID", itemId);
                json.put("IV_ID", ivId);
                json.put("PS_ID", psId);
                json.put("Product_ID", productId);
                json.put("LotID", lotId);
                json.put("Status_ID", statusId);
                json.put("Qty", Double.parseDouble(params[0]));
                json.put("PackageN", packageN);
                json.put("SinglePackageQty", singlePackageQty);
                json.put("DiscreteQty", discreteQty);
                json.put("ScanX", itemScanX);
                json.put("ManuLotNo", manuLotNo);
                json.put("Remark", remark);
                return WebServiceUtil.op_CheckInventory_SaveV3_New(json.toString());
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(WsResult result) {
            isSaving = false;
            if (result == null || !result.getResult()) {
                showError(result, "保存失败");
                return;
            }
            try {
                JSONObject root = new JSONObject(result.getInfo());
                JSONObject status = firstObject(root, "Result");
                if (status == null) {
                    status = new JSONObject();
                }
                if (!status.optBoolean("Accepted", true)) {
                    ToastUtil.showToastShort(status.optString("Message", "保存未被接受"));
                    return;
                }
                countUnitId = status.optLong("CountUnit_ID", countUnitId);
                compareMode = status.optInt("CompareMode", compareMode);
                canInput = status.optBoolean("CanInput", canInput);
                locationStateVersion = status.optString("StateVersion", locationStateVersion);
                savedItemCountInLocation++;
                savedItemScanSet.add(itemScanX);
                bindRecords(root.optJSONArray("Records"));
                ToastUtil.showToastShort("保存成功");
                if (countCategory == 1) {
                    completeLargeCountTask();
                } else {
                    resetLocationItemState();
                    if (!canInput) {
                        ToastUtil.showToastShort("当前库位已完成，请点击完成库位");
                    }
                }
            } catch (Exception e) {
                ToastUtil.showToastShort("保存成功，但解析返回结果失败");
            }
        }
    }

    private class LocationResultAsyncTask extends AsyncTask<Void, Void, WsResult> {
        @Override
        protected WsResult doInBackground(Void... params) {
            try {
                return WebServiceUtil.op_CheckInventory_LocationResultV3_New(buildBaseInventoryJson().toString());
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(WsResult result) {
            if (result == null || !result.getResult()) {
                showError(result, "刷新库位失败");
                return;
            }
            try {
                JSONObject root = new JSONObject(result.getInfo());
                JSONObject status = firstObject(root, "Result");
                if (status == null) {
                    status = new JSONObject();
                }
                countUnitId = status.optLong("CountUnit_ID", countUnitId);
                compareMode = status.optInt("CompareMode", compareMode);
                canInput = status.optBoolean("CanInput", canInput);
                locationStateVersion = status.optString("StateVersion", locationStateVersion);
                bindRecords(root.optJSONArray("Records"));
                ToastUtil.showToastShort(status.optString("Message", "库位结果已刷新"));
            } catch (Exception e) {
                ToastUtil.showToastShort("刷新库位失败");
            }
        }
    }

    private class CompleteAsyncTask extends AsyncTask<Boolean, Void, WsResult> {
        @Override
        protected void onPreExecute() {
            isCompleting = true;
        }

        @Override
        protected WsResult doInBackground(Boolean... params) {
            try {
                JSONObject json = buildBaseInventoryJson();
                json.put("EmptyConfirmed", params[0]);
                json.put("Request_ID", UUID.randomUUID().toString());
                return WebServiceUtil.op_CheckInventory_CompleteV3_New(json.toString());
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(WsResult result) {
            isCompleting = false;
            if (result == null || !result.getResult()) {
                showError(result, "完成库位失败");
                return;
            }
            try {
                JSONObject root = new JSONObject(result.getInfo());
                JSONObject status = firstObject(root, "Result");
                if (status == null) {
                    status = new JSONObject();
                }
                if (!status.optBoolean("Accepted", true)) {
                    ToastUtil.showToastShort(status.optString("Message", "完成库位未被接受"));
                    return;
                }
                locationStateVersion = status.optString("StateVersion", locationStateVersion);
                ToastUtil.showToastShort(countCategory == 1 ? "本任务已完成" : "完成库位成功");
                resetAllAfterComplete();
                if (countCategory == 1) {
                    labelCiId = 0;
                    taskNo = 0;
                    updateTaskInfo();
                }
            } catch (Exception e) {
                ToastUtil.showToastShort("完成库位成功，但解析返回结果失败");
            }
        }
    }
}
