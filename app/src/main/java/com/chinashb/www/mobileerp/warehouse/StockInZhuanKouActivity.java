package com.chinashb.www.mobileerp.warehouse;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.InBoxItemAdapter;
import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.basicobject.IstPlaceEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CommAlertDialog;
import com.chinashb.www.mobileerp.widget.OnDialogViewClickListener;
import com.chinashb.www.mobileerp.widget.ScanInputDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 * @date 创建时间 2026/5/16
 * @author 作者: code-x John
 * @description 转口入库页面，逻辑仿照 StockInCompany29Activity
 */
public class StockInZhuanKouActivity extends BaseActivity {

    @BindView(R.id.zhuankou_scan_item_button)
    Button scanItemButton;
    @BindView(R.id.zhuankou_scan_location_button)
    Button scanLocationButton;
    @BindView(R.id.zhuankou_commit_button)
    Button commitButton;
    @BindView(R.id.zhuankou_box_recycler_view)
    RecyclerView boxRecyclerView;
    @BindView(R.id.zhuankou_scan_input_edit_text)
    EditText scanInputEditText;
    @BindView(R.id.zhuankou_suggest_stock_layout)
    RelativeLayout suggestStockLayout;
    @BindView(R.id.zhuankou_suggest_stock_switch)
    Switch suggestStockSwitch;

    private InBoxItemAdapter boxItemAdapter;
    private final List<BoxItemEntity> boxItemEntityList = new ArrayList<>();
    private IstPlaceEntity thePlace;
    private final ArrayList<String> scanCodeList = new ArrayList<>();
    private String scanContent;
    private ScanInputDialog inputDialog;
    private boolean isOpenSuggestStock = true;
    private BoxItemEntity currentBoxItemEntity;
    private boolean isProcessingBoxScan;
    private boolean isProcessingIstScan;
    private boolean isExecutingWarehouseIn;

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                ToastUtil.showToastLong("该入库标签不属于28或29公司，请确认！");
            } else if (msg.what == 1) {
                Bundle bundle = msg.getData();
                if (bundle != null) {
                    ToastUtil.showToastLong("建议仓库存放:" + bundle.getString("suggest_ist"));
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_stock_in_zhuankou_layout);
        ButterKnife.bind(this);

        boxItemAdapter = new InBoxItemAdapter(this, boxItemEntityList);
        boxRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        boxRecyclerView.setItemAnimator(null);
        boxRecyclerView.setAdapter(boxItemAdapter);
        setHomeButton();
        setViewsListener();

        int currentCompanyId = UserSingleton.get().getUserInfo().getCompany_ID();
        if (currentCompanyId != 28 && currentCompanyId != 29) {
            ToastUtil.showToastShort("该界面目前只适用于28或29公司的转扣扫描入库！");
            finish();
        }
    }

    @Override
    protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }

    private void setViewsListener() {
        scanInputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            protected void onTextChangedSafe(CharSequence text) {
                if (text != null && text.length() > 7) {
                    parseScanResult(text.toString());
                }
            }
        });

        suggestStockSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = suggestStockSwitch.isChecked();
                ToastUtil.showToastShort(isChecked ? "您已打开建议仓库！" : "您已关闭建议仓库！");
                isOpenSuggestStock = isChecked;
            }
        });
    }

    @OnClick({
            R.id.zhuankou_scan_item_button,
            R.id.zhuankou_scan_location_button,
            R.id.zhuankou_commit_button,
            R.id.zhuankou_suggest_stock_layout
    })
    void onViewClick(View view) {
        if (view.getId() == R.id.zhuankou_scan_item_button) {
            new IntentIntegrator(this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
        } else if (view.getId() == R.id.zhuankou_scan_location_button) {
            if (boxItemEntityList.size() > 0) {
                int selectedCount = 0;
                for (int i = 0; i < boxItemEntityList.size(); i++) {
                    if (boxItemEntityList.get(i).getSelect()) {
                        selectedCount++;
                    }
                }
                if (selectedCount > 0) {
                    new IntentIntegrator(this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
                    scanInputEditText.setText("");
                } else {
                    ToastUtil.showToastShort("请选择条目！");
                }
            } else {
                ToastUtil.showToastShort("没有物品条码或仓库位置码没有成功，请重新扫描！");
            }
        } else if (view.getId() == R.id.zhuankou_commit_button) {
            handleIntoWareHouse();
        } else if (view.getId() == R.id.zhuankou_suggest_stock_layout) {
            suggestStockSwitch.performClick();
        }
    }

    private void parseScanResult(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        if (content.contains("\n")) {
            content = content.replace("\n", "");
        }
        if (content.contains("/") || content.contains("／")) {
            if (content.contains("／")) {
                content = content.replace("／", "/");
            }

            String[] qrContent = content.split("/");
            if (qrContent.length >= 2) {
                String qrTitle = qrContent[0];
                if (!TextUtils.isEmpty(qrTitle)) {
                    if (qrTitle.equals("VE") || qrTitle.equals("VF") || qrTitle.equals("VG")
                            || qrTitle.equals("V9") || qrTitle.equals("VA") || qrTitle.equals("VB")
                            || qrTitle.equals("VC")) {
                        if (isProcessingBoxScan) {
                            return;
                        }
                        scanContent = content;
                        isProcessingBoxScan = true;
                        new GetBoxAsyncTask().execute();
                    }
                }

                if (content.startsWith("/SUB_IST_ID/") || content.startsWith("/IST_ID/")
                        || content.startsWith("/SUB——IST——ID/") || content.startsWith("/IST——ID/")) {
                    if (isProcessingIstScan) {
                        return;
                    }
                    if (content.startsWith("/SUB——IST——ID/")) {
                        content = content.replace("/SUB——IST——ID/", "/SUB_IST_ID/");
                    }
                    if (content.startsWith("/IST——ID/")) {
                        content = content.replace("/IST——ID/", "/IST_ID/");
                    }
                    scanContent = content;
                    isProcessingIstScan = true;
                    new GetIstAsyncTask().execute();
                }
            }
        }
    }

    private void handleIntoWareHouse() {
        if (isExecutingWarehouseIn) {
            return;
        }
        commitButton.setEnabled(false);
        if (boxItemEntityList.size() > 0) {
            int selectedCount = 0;
            for (int i = 0; i < boxItemEntityList.size(); i++) {
                if (boxItemEntityList.get(i).getSelect()) {
                    if (boxItemEntityList.get(i).getIst_ID() == 0) {
                        ToastUtil.showToastLong("还没有扫描库位");
                        commitButton.setEnabled(true);
                        return;
                    }
                    selectedCount++;
                }
            }
            if (selectedCount > 0) {
                if (UserSingleton.get().getHRID() > 0 && !TextUtils.isEmpty(UserSingleton.get().getHRName())) {
                    new AsyncExeWarehouseIn().execute();
                } else {
                    CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(this)
                            .setTitle("")
                            .setMessage("您当前程序账号有误，需重新登录！")
                            .setLeftText("确定");

                    builder.setOnViewClickListener(new OnDialogViewClickListener() {
                        @Override
                        public void onViewClick(Dialog dialog, View v, int tag) {
                            if (tag == CommAlertDialog.TAG_CLICK_LEFT) {
                                CommonUtil.doLogout(StockInZhuanKouActivity.this);
                                dialog.dismiss();
                            }
                        }
                    });
                    builder.create().show();
                    commitButton.setEnabled(true);
                }
            } else {
                commitButton.setEnabled(true);
            }
        } else {
            ToastUtil.showToastShort("没有物品条码或仓库位置码没有成功，请重新扫描！");
            commitButton.setEnabled(true);
        }
    }

    private void refreshBoxItemList() {
        if (boxRecyclerView == null || boxItemAdapter == null) {
            return;
        }
        boxRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                boxItemAdapter.notifyDataSetChanged();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (inputDialog != null && inputDialog.isShowing()) {
            inputDialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
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
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (!TextUtils.isEmpty(result.getContents())) {
                parseScanResult(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class GetIsAlarmItemAsyncTask extends AsyncTask<String, Void, Void> {
        WsResult result;

        @Override
        protected Void doInBackground(String... ids) {
            if (ids.length > 0) {
                int itemID = Integer.parseInt(ids[0]);
                result = WebServiceUtil.getIsAlarmItem(UserSingleton.get().getUserInfo().getBu_ID(), itemID);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (result != null && result.getResult()) {
                String info = result.getErrorInfo();
                if (info.contains("紧急物料")) {
                    CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(StockInZhuanKouActivity.this)
                            .setTitle("")
                            .setMessage("此物料为紧急物料！")
                            .setLeftText("确定");

                    builder.setOnViewClickListener(new OnDialogViewClickListener() {
                        @Override
                        public void onViewClick(Dialog dialog, View v, int tag) {
                            if (tag == CommAlertDialog.TAG_CLICK_LEFT) {
                                dialog.dismiss();
                            }
                        }
                    });
                    builder.create().show();
                }
            }
        }
    }

    private class GetBoxAsyncTask extends AsyncTask<String, Void, Void> {
        BoxItemEntity scanBoxItemEntity;
        boolean shouldTrackScanCode;

        @Override
        protected Void doInBackground(String... params) {
            BoxItemEntity boxItemEntity;
            if (UserSingleton.get().getUserInfo().getBu_ID() == 149
                    || UserSingleton.get().getUserInfo().getBu_ID() == 155) {
                boxItemEntity = WebServiceUtil.op_Check_Commit_DS_Item_Income_Barcode_ML(scanContent);
            } else {
                boxItemEntity = WebServiceUtil.op_Check_Commit_DS_Item_Income_Barcode(scanContent);
            }

            scanBoxItemEntity = boxItemEntity;
            if (boxItemEntity.getResult()) {
                if (boxItemEntity.getCompany_ID() != 0
                        && boxItemEntity.getCompany_ID() != 28
                        && boxItemEntity.getCompany_ID() != 29) {
                    Message message = new Message();
                    message.what = 0;
                    handler.sendMessage(message);
                    return null;
                }

                if (!isBoxExisted(boxItemEntity)) {
                    currentBoxItemEntity = boxItemEntity;
                    shouldTrackScanCode = true;
                } else {
                    boxItemEntity.setResult(false);
                    boxItemEntity.setErrorInfo("该包装已经在装载列表中");
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            handleCurrentBoxStatus(currentBoxItemEntity);

            if (scanBoxItemEntity != null && !scanBoxItemEntity.getResult()) {
                Toast.makeText(StockInZhuanKouActivity.this, scanBoxItemEntity.getErrorInfo(), Toast.LENGTH_LONG).show();
            }

            if (scanBoxItemEntity != null && scanBoxItemEntity.getResult() && shouldTrackScanCode) {
                scanCodeList.add(scanContent);
            }
            refreshBoxItemList();
            scanInputEditText.setText("");
            scanInputEditText.setHint("请继续使用扫描枪");
            isProcessingBoxScan = false;

            if (scanBoxItemEntity != null && scanBoxItemEntity.getResult()) {
                GetIsAlarmItemAsyncTask task = new GetIsAlarmItemAsyncTask();
                task.execute(scanBoxItemEntity.getItem_ID() + "");
            }
        }

        private boolean isBoxExisted(BoxItemEntity boxItem) {
            if (boxItemEntityList != null) {
                for (int i = 0; i < boxItemEntityList.size(); i++) {
                    if (boxItemEntityList.get(i).getDIII_ID() == boxItem.getDIII_ID()) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private void handleCurrentBoxStatus(BoxItemEntity boxItemEntity) {
        if (boxItemEntity == null) {
            return;
        }
        if (isOpenSuggestStock) {
            Message message = new Message();
            message.what = 1;
            Bundle bundle = new Bundle();
            bundle.putString("suggest_ist", boxItemEntity.getIstName());
            message.setData(bundle);
            handler.sendMessage(message);
        }
        boxItemEntity.setSelect(true);
        boxItemEntityList.add(boxItemEntity);
        currentBoxItemEntity = null;
    }

    private class GetIstAsyncTask extends AsyncTask<String, Void, Void> {
        private IstPlaceEntity istPlaceEntity;

        @Override
        protected Void doInBackground(String... params) {
            istPlaceEntity = WebServiceUtil.op_Check_Commit_IST_Barcode(scanContent);
            if (istPlaceEntity.getResult()) {
                thePlace = istPlaceEntity;
                for (int i = 0; i < boxItemEntityList.size(); i++) {
                    if (boxItemEntityList.get(i).getSelect()) {
                        boxItemEntityList.get(i).setIstName(istPlaceEntity.getIstName());
                        boxItemEntityList.get(i).setIst_ID(istPlaceEntity.getIst_ID());
                        boxItemEntityList.get(i).setSub_Ist_ID(istPlaceEntity.getSub_Ist_ID());
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            refreshBoxItemList();
            scanInputEditText.setText("");
            if (istPlaceEntity != null && !istPlaceEntity.getResult()) {
                ToastUtil.showToastLong(istPlaceEntity.getErrorInfo());
            } else {
                handleIntoWareHouse();
            }
            isProcessingIstScan = false;
        }
    }

    private class AsyncExeWarehouseIn extends AsyncTask<String, Void, Void> {
        WsResult wsResult;
        private final List<BoxItemEntity> successList = new ArrayList<>();

        @Override
        protected Void doInBackground(String... params) {
            List<BoxItemEntity> selectList = new ArrayList<>();
            for (int i = 0; i < boxItemEntityList.size(); i++) {
                if (boxItemEntityList.get(i).getSelect()) {
                    selectList.add(boxItemEntityList.get(i));
                }
            }

            int selectedCount = selectList.size();
            int count = 0;
            while (count < selectedCount) {
                BoxItemEntity boxItemEntity = selectList.get(count);
                wsResult = WebServiceUtil.opCommitDsItemIncomeToWarehouseZhuanKou(
                        boxItemEntity,
                        scanCodeList.size() == selectedCount ? scanCodeList.get(0) : ""
                );
                if (wsResult != null && wsResult.getResult()) {
                    successList.add(boxItemEntity);
                } else {
                    break;
                }
                count++;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!successList.isEmpty()) {
                boxItemEntityList.removeAll(successList);
            }
            if (wsResult != null) {
                CommonUtil.ShowWsResultToast(StockInZhuanKouActivity.this, wsResult, "入库完成");
            }
            commitButton.setEnabled(true);
            isExecutingWarehouseIn = false;
            refreshBoxItemList();
            scanCodeList.clear();
        }

        @Override
        protected void onPreExecute() {
            isExecutingWarehouseIn = true;
        }
    }
}
