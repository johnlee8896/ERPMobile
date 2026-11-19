package com.chinashb.www.mobileerp.warehouse;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.IssueMoreItemAdapter;
import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.basicobject.IstPlaceEntity;
import com.chinashb.www.mobileerp.basicobject.MpiWcBean;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.AppUtil;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.utils.UnitFormatUtil;
import com.chinashb.www.mobileerp.widget.CommAlertDialog;
import com.chinashb.www.mobileerp.widget.CommonSelectInputDialog;
import com.chinashb.www.mobileerp.widget.OnDialogViewClickListener;
import com.chinashb.www.mobileerp.widget.TimePickerManager;
import com.chinashb.www.mobileerp.widget.TitleLayoutManagerView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StockOutMoreExtraActivity extends BaseActivity {

    private MpiWcBean themw;
    // codex by john 2026-08-30 11:25 - 是否额外领料备料模式
    private boolean isBackup;

    private TitleLayoutManagerView titleLayoutManagerView;
    private TextView txtMw_Title;
    private TextView tvIssueMoreExtraTitle;

    private Button btnAddTray;
    private Button btnRemark;
    private Button btnWarehouseOut;
    private Button selectDateButton;
    private Button btnExtraBackup;

    private RecyclerView mRecyclerView;
    private EditText inputEditText;
    private TextView remarkTextView;

    private IssueMoreItemAdapter issueMoreItemAdapter;
    private List<BoxItemEntity> newissuelist;
    private ArrayList<String> scanCodeList = new ArrayList<>();
    private IstPlaceEntity thePlace;
    private String scanstring;
    private String remark = "";
    private CommonSelectInputDialog remarkDialog;
    private Date outDate;
    private TimePickerManager timePickerManager;
    // 2026-10-29 john 额外领料防重：扫码处理锁、出库提交锁、备料提交锁、最近条码去重
    private boolean isProcessingScan;
    private boolean isExecutingOut;
    private boolean isExecutingBackup;
    private String lastProcessedBarcode;
    private long lastProcessedBarcodeTime;

    private OnViewClickListener onViewClickListener = new OnViewClickListener() {
        @Override public <T> void onClickAction(View v, String tag, T t) {
            if (t != null){
                remark = (String) t;
                remarkTextView.setText((CharSequence) t);
            }
            if (remarkDialog != null && remarkDialog.isShowing()){
                remarkDialog.dismiss();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_stock_out_more_extra_layout);

//        tv = (TextView)findViewById(R.id.tv_stock_system_title);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_issue_more_extra);

        txtMw_Title = (TextView) findViewById(R.id.tv_issue_more_mw_title);
        btnAddTray = (Button) findViewById(R.id.btn_issue_more_add_extra);
        inputEditText = findViewById(R.id.stock_out_more_extra_input_EditeText);
        remarkTextView = findViewById(R.id.stock_out_more_remark_EditText);
        btnWarehouseOut = (Button) findViewById(R.id.btn_exe_warehouse_out);
        selectDateButton = findViewById(R.id.btn_issue_extra_add_date);
        btnRemark = findViewById(R.id.btn_remark);
        titleLayoutManagerView = findViewById(R.id.title_layout);
        tvIssueMoreExtraTitle = findViewById(R.id.tv_issue_more_extra_title);
        btnExtraBackup = findViewById(R.id.btn_issue_more_backup_extra);


        newissuelist = new ArrayList<>();
        if (savedInstanceState != null) {
            newissuelist = (List<BoxItemEntity>) savedInstanceState.getSerializable("BoxItemList");
        }
        issueMoreItemAdapter = new IssueMoreItemAdapter(StockOutMoreExtraActivity.this, newissuelist);
        issueMoreItemAdapter.showNeedMore = false;
        issueMoreItemAdapter.setCanEdit(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setAdapter(issueMoreItemAdapter);

        Intent who = getIntent();
        themw = (MpiWcBean) who.getSerializableExtra("mw");
        if (themw != null) {
            themw.setMwNameTextView(txtMw_Title);

        }

        isBackup = who.getBooleanExtra(IntentConstant.Intent_continue_put_extra_from_backup, false);
        if (isBackup) {
            titleLayoutManagerView.setTitle("额外领料备料");
            tvIssueMoreExtraTitle.setText("额外领料备料");
            selectDateButton.setVisibility(View.GONE);
            btnWarehouseOut.setVisibility(View.GONE);
            btnExtraBackup.setVisibility(View.VISIBLE);
        }

        btnAddTray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //StockOutMoreActivity.AsyncDirectGetBox task = new StockOutMoreActivity.AsyncDirectGetBox();
                //task.execute();

                new IntentIntegrator(StockOutMoreExtraActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
            }

        });

        btnRemark.setOnClickListener(v ->{
            if (remarkDialog == null){
                remarkDialog = new CommonSelectInputDialog(StockOutMoreExtraActivity.this);
            }
//            remarkDialog.setSelectOnly(true);
            remarkDialog.show();
            //这句必须放在show之后
            remarkDialog.setSelectOnly(true);
            remarkDialog.setOnViewClickListener(onViewClickListener);
        });

        selectDateButton.setOnClickListener(v -> {
            selectDate();
        });


        btnWarehouseOut.setOnClickListener(v -> handleExtraWarehouseOut());
        btnExtraBackup.setOnClickListener(v -> handleExtraWarehouseBackup());

        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            protected void onTextChangedSafe(CharSequence text) {
                //                if (text.toString().endsWith("\n")) {
                if (text.toString().length() > 0) {
//                    ToastUtil.showToastLong("扫描结果:" + text.toString());
                    System.out.println("========================扫描结果:" + text.toString());
                    parseScanResult(text.toString());
                }
            }
        });


    }

    private void handleExtraWarehouseOut() {
        if (isExecutingOut) {
            return;
        }
        if (TextUtils.isEmpty(remarkTextView.getText().toString())) {
            ToastUtil.showToastShort("请为本操作添加备注");
            return;
        }

        if (newissuelist.size() <= 0) {
            return;
        }

        if (UserSingleton.get().getHRID() <= 0 || TextUtils.isEmpty(UserSingleton.get().getHRName())) {
            CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(StockOutMoreExtraActivity.this)
                    .setTitle("").setMessage("您当前程序账号有误，需重新登录！")
                    .setLeftText("确定");
            builder.setOnViewClickListener(new OnDialogViewClickListener() {
                @Override
                public void onViewClick(Dialog dialog, View v, int tag) {
                    switch (tag) {
                        case CommAlertDialog.TAG_CLICK_LEFT:
                            CommonUtil.doLogout(StockOutMoreExtraActivity.this);
                            dialog.dismiss();
                            break;
                    }
                }
            });
            builder.create().show();
            return;
        }

        if (TextUtils.isEmpty(remark)) {
            ToastUtil.showToastShort("额外领料，请选择备注！");
            return;
        }

        isExecutingOut = true;
        AsyncExeWarehouseOut task = new AsyncExeWarehouseOut();
        task.execute();
    }

    // codex by john 2026-08-31 15:49 - 额外领料备料提交，走通用备料方法（与 StockOutMoreActivity 的 HandleWarehouseBackupAsyncTask 一致）
    private void handleExtraWarehouseBackup() {
        if (isExecutingBackup) {
            return;
        }
        if (newissuelist == null || newissuelist.size() <= 0) {
            ToastUtil.showToastShort("未有待投料数据！");
            return;
        }

        if (TextUtils.isEmpty(remark)) {
            ToastUtil.showToastShort("额外领料，请选择备注！");
            return;
        }
        isExecutingBackup = true;
        AsyncExeWarehouseBackup task = new AsyncExeWarehouseBackup();
        task.execute();
    }

    private void refreshIssueMoreExtraList() {
        if (mRecyclerView == null || issueMoreItemAdapter == null) {
            return;
        }
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                issueMoreItemAdapter.notifyDataSetChanged();
            }
        });
    }

    private void selectDate() {
        showTimePickerDialog(TimePickerManager.PICK_TYPE_OUT_DATE);
    }

    private void showTimePickerDialog(String pickType) {
        if (timePickerManager == null) {
            timePickerManager = new TimePickerManager(StockOutMoreExtraActivity.this);

            Calendar todayDate = Calendar.getInstance();
//            calendar.get(Calendar.DATE) == calendar
//                    .getActualMaximum(Calendar.DAY_OF_MONTH)
            if (todayDate.get(Calendar .DATE) == todayDate.getActualMaximum(Calendar.DAY_OF_MONTH)){
                Calendar endDate = Calendar.getInstance();
                endDate.add(Calendar .DAY_OF_YEAR ,1);
                timePickerManager.setDateRange(todayDate,endDate);
            }
        }
        if (pickType == TimePickerManager.PICK_TYPE_ARRIVE_DATE || pickType == TimePickerManager.PICK_TYPE_OUT_DATE) {
            timePickerManager.setShowType(TimePickerManager.PICK_TYPE_YEAR, 3);
        } else {
            timePickerManager.setShowType(TimePickerManager.PICK_TYPE_YEAR, 5);
        }
        timePickerManager
                .setOnViewClickListener(new OnViewClickListener() {
                    @Override
                    public <T> void onClickAction(View v, String tag, T date) {
                        if (tag.equals(TimePickerManager.PICK_TYPE_OUT_DATE)) {
                            selectDateButton.setText(UnitFormatUtil.sdf_YMD_Chinese.format(date));
                            outDate = (Date) date;
                        }
                    }
                })
                .showDialog(pickType);
        AppUtil.forceHideInputMethod(StockOutMoreExtraActivity.this);
    }

    private void parseScanResult(String result) {
        if (isProcessingScan) {
            return;
        }
        if (TextUtils.isEmpty(result)) {
            return;
        }
        long now = System.currentTimeMillis();
        if (result.equals(lastProcessedBarcode) && now - lastProcessedBarcodeTime < 1500L) {
            return;
        }
//        Toast.makeText(this, "Scanned: " + result, Toast.LENGTH_LONG).show();
//        String X = result.getContents();
        if (result.contains("/")) {
            String[] qrContent;
            qrContent = result.split("/");
            if (qrContent.length >= 2) {
                String qrTitle = qrContent[0];
                if (!qrTitle.equals("")) {
                    if (qrTitle.equals("VE") || qrTitle.equals("VF") || qrTitle.equals("VG") || qrTitle.equals("V9") || qrTitle.equals("VA") || qrTitle.equals("VB") || qrTitle.equals("VC")) {
                        //物品条码
                        lastProcessedBarcode = result;
                        lastProcessedBarcodeTime = System.currentTimeMillis();
                        isProcessingScan = true;
                        scanstring = result;
                        scanCodeList.add(result);
                        GetIssueMoreExtraBoxAsyncTask task = new GetIssueMoreExtraBoxAsyncTask();
                        task.execute();
                    }
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                //new IntentIntegrator(StockOutMoreActivity.this).initiateScan();
            } else {
                parseScanResult(result.getContents());
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class GetIssueMoreExtraBoxAsyncTask extends AsyncTask<String, Void, Void> {
        BoxItemEntity scanresult;
        boolean scanNormal = true;

        @Override
        protected Void doInBackground(String... params) {

            Long MW_ID = themw.getMPIWC_ID();
            BoxItemEntity bi = WebServiceUtil.op_Check_Commit_MW_Issue_Extra_Item_Barcode(MW_ID, scanstring);
            scanresult = bi;
            if (bi.getResult() ) {
                if (!is_box_existed(bi)) {
                    if (bi.getQty() < 0){
                        scanNormal = false;

                    }
                    bi.setSelect(true);
                    newissuelist.add(bi);
                } else {
                    bi.setResult(false);
                    bi.setErrorInfo("该包装已经在装载列表中");
                }
            }

            return null;
        }


        protected Boolean is_box_existed(BoxItemEntity box_item) {
            Boolean result = false;

            if (newissuelist != null) {
                for (int i = 0; i < newissuelist.size(); i++) {
                    if (newissuelist.get(i).getSMLI_ID() == box_item.getSMLI_ID() && box_item.getSMLI_ID() > 0) {
                        return true;
                    }
                    if (newissuelist.get(i).getSMM_ID() == box_item.getSMM_ID() && box_item.getSMM_ID() > 0) {
                        return true;
                    }
                    if (newissuelist.get(i).getSMT_ID() == box_item.getSMT_ID() && box_item.getSMT_ID() > 0) {
                        return true;
                    }
                    if (newissuelist.get(i).getSMT_ID() == box_item.getSMT_ID() && box_item.getSMT_ID() == 0
                            && newissuelist.get(i).getSMM_ID() == box_item.getSMM_ID() && box_item.getSMM_ID() == 0
                            && newissuelist.get(i).getSMLI_ID() == box_item.getSMLI_ID() && box_item.getSMLI_ID() == 0
                            && newissuelist.get(i).getLotID() == box_item.getLotID()) {
                        return true;
                    }


                }
            }

            return result;
        }


        @Override
        protected void onPostExecute(Void result) {

            if (scanresult != null) {
                if (!scanresult.getResult() ) {
                    Toast.makeText(StockOutMoreExtraActivity.this, scanresult.getErrorInfo(), Toast.LENGTH_LONG).show();
                }
            }

            if (scanNormal){
                 refreshIssueMoreExtraList();
            }else{
                ToastUtil.showToastLong("数量为负数，物料码有误，请重新扫描！");
            }
            inputEditText.setText("");
            inputEditText.setHint("请继续扫描");
            isProcessingScan = false;
        }

    }


    private class AsyncExeWarehouseOut extends AsyncTask<String, Void, Void> {
        WsResult ws_result;
        private final List<BoxItemEntity> successList = new ArrayList<>();

        @Override
        protected Void doInBackground(String... params) {

            List<BoxItemEntity> selectedList = new ArrayList<>(newissuelist);
            int count = 0;
            int newissuesize = selectedList.size();
            while (count < newissuesize) {
                BoxItemEntity bi = selectedList.get(count);
                if (outDate == null){
                    outDate = new Date() ;
                }

                ws_result = WebServiceUtil.op_Commit_MW_Issue_Extra_Item(themw.getMPIWC_ID(), bi, remark, outDate, scanCodeList.size() == newissuesize ? scanCodeList.get(0):"");

                if (ws_result.getResult() ) {
                    successList.add(bi);
                } else {
                    return null;
                }

                count++;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!successList.isEmpty()) {
                newissuelist.removeAll(successList);
            }
            refreshIssueMoreExtraList();
            remarkTextView.setText("");
            remark = "";
            if (ws_result != null) {
                CommonUtil.ShowWsResultToast(StockOutMoreExtraActivity.this, ws_result, "成功出库", Toast.LENGTH_SHORT);
            }
            scanCodeList.clear();
            isExecutingOut = false;
        }

    }

    // codex by john 2026-08-31 15:49 - 额外领料备料异步提交，逐条调 op_Commit_MW_Issue_Item_To_Backup（与 HandleWarehouseBackupAsyncTask 一致）
    private class AsyncExeWarehouseBackup extends AsyncTask<String, Void, Void> {
        WsResult ws_result;
        private final List<BoxItemEntity> successList = new ArrayList<>();

        @Override
        protected Void doInBackground(String... params) {
            List<BoxItemEntity> selectedList = new ArrayList<>(newissuelist);
            int count = 0;
            int size = selectedList.size();
            while (count < size) {
                BoxItemEntity bi = selectedList.get(count);
                if (outDate == null) {
                    outDate = new Date();
                }
//                ws_result = WebServiceUtil.op_Commit_MW_Issue_Item_To_Backup(
                ws_result = WebServiceUtil.op_Commit_MW_Issue_Item_To_Backup_EXtra(
                        themw.getMPIWC_ID(), bi, outDate,
                        scanCodeList.size() == size ? scanCodeList.get(count) : "",remark);
                if (ws_result.getResult()) {
                    successList.add(bi);
                } else {
                    return null;
                }
                count++;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!successList.isEmpty()) {
                newissuelist.removeAll(successList);
            }
            refreshIssueMoreExtraList();
            if (ws_result != null) {
                CommonUtil.ShowWsResultToast(StockOutMoreExtraActivity.this, ws_result, "成功备料", Toast.LENGTH_SHORT);
            }
            scanCodeList.clear();
            remark = "";
            isExecutingBackup = false;
        }
    }


    @Override
    protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }
}
