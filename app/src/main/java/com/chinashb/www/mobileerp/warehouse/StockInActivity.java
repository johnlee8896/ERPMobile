package com.chinashb.www.mobileerp.warehouse;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
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
import com.chinashb.www.mobileerp.utils.JsonUtil;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CommAlertDialog;
import com.chinashb.www.mobileerp.widget.OnDialogViewClickListener;
import com.chinashb.www.mobileerp.widget.ScanInputDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 扫描入库
 */
public class StockInActivity extends BaseActivity implements View.OnClickListener {
    BroadcastReceiver mFoundReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //找到设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 添加进一个设备列表，进行显示。
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
//                    Log.v(TAG, "find device:" + device.getName() + device.getAddress());
                }
            }
            //搜索完成
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//                cancelDiscovery();
            }
        }
    };
    private Button addTrayScannerButton;
    private Button addTrayPhotoButton;
    private Button scanAreaButton;
    //    private Button stockInButton;
//    private Button btnStartMoving;
    private Button warehouseInButton;
    private RecyclerView mRecyclerView;
    private EditText inputEditText;
    private InBoxItemAdapter boxItemAdapter;
    private List<BoxItemEntity> boxItemEntityList = new ArrayList<>();
    private IstPlaceEntity thePlace;
    private ArrayList<String> scanCodeList = new ArrayList<>();
    private String scanContent;
    private String scanCode = "";
    private ScanInputDialog inputDialog;
    private RelativeLayout switchLayout;
    private Switch stockSwitch;
    private boolean isOpenSuggestStock = true;
    private String pendingScanText = "";
    private boolean isProcessingBoxScan = false;
    private boolean isProcessingIstScan = false;
    private boolean isExecutingWarehouseIn = false;
    private final ArrayList<String> pendingBoxScanQueue = new ArrayList<>();
    private final ArrayList<Long> trackedScanDiiiIdList = new ArrayList<>();
    private final Runnable parseInputRunnable = new Runnable() {
        @Override
        public void run() {
            if (!TextUtils.isEmpty(pendingScanText)) {
                parseScanResult(pendingScanText);
            }
        }
    };
    //// TODO: 2020/1/9 以后要优化，暂时先解决Can't toast on a thread that has not called Looper.prepare() 的问题
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                ToastUtil.showToastLong("您当前公司与来料入库公司不符合，请确认来料是否入到该公司！");
            } else if (msg.what == 1) {
//                ToastUtil.showToastLong("建议仓库存放:" + boxItemEntity.getIstName());
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
        setContentView(R.layout.activity_stock_in_layout);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_box_item);
        addTrayScannerButton = (Button) findViewById(R.id.btn_add_tray_scanner);
        addTrayPhotoButton = findViewById(R.id.btn_add_tray_photo);
        scanAreaButton = (Button) findViewById(R.id.btn_scan_area);
        warehouseInButton = (Button) findViewById(R.id.btn_exe_warehouse_in);
        inputEditText = (EditText) findViewById(R.id.input_EditText);

        stockSwitch = findViewById(R.id.setting_open_suggest_stock_Switch);
        switchLayout = findViewById(R.id.setting_open_suggest_stock_Layout);

        setHomeButton();

        if (savedInstanceState != null) {
            boxItemEntityList = (List<BoxItemEntity>) savedInstanceState.getSerializable("BoxItemList");
        }

        boxItemAdapter = new InBoxItemAdapter(StockInActivity.this, boxItemEntityList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setAdapter(boxItemAdapter);
        setViewsListener();
        if (UserSingleton.get().getUserInfo().getCompany_ID() == 29){
            ToastUtil.showToastShort("您当前车间是零部件公司，请用零部件公司入库界面！");
            finish();
        }

    }

    @Override
    protected void onResume() {
        //设置为竖屏
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        super.onResume();
    }

    private void setViewsListener() {
        addTrayScannerButton.setOnClickListener(this);
        addTrayPhotoButton.setOnClickListener(this);
        scanAreaButton.setOnClickListener(this);
        warehouseInButton.setOnClickListener(this);
        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
protected void onTextChangedSafe(CharSequence text) {
                String textValue = text == null ? "" : text.toString();
                if (textValue.length() > 7) {
                    pendingScanText = textValue;
                    inputEditText.removeCallbacks(parseInputRunnable);
                    // 扫码枪通常是连续灌入字符，这里短暂防抖后只解析一次完整内容。
                    inputEditText.postDelayed(parseInputRunnable, 80);
                }
            }
        });
        switchLayout.setOnClickListener(this);

        stockSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = stockSwitch.isChecked();
                if (isChecked) {
                    ToastUtil.showToastShort("您已打开建议仓库！");
                } else {
                    ToastUtil.showToastShort("您已关闭建议仓库！");
                }
                isOpenSuggestStock = isChecked;
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
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (!TextUtils.isEmpty(result.getContents())) {
                parseScanResult(result.getContents());
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == addTrayScannerButton) {
            //scanContent= "VG/404731";
            //StockInActivity.GetBoxAsyncTask task = new StockInActivity.GetBoxAsyncTask();
            //task.execute();
//            CommAlertDialog.with(StockInActivity.this).setTitle("请点击扫描枪")
//                    .setMessage("请确保蓝牙已连接，若未连接请打开设置--蓝牙--连接QScanner")
//                    .setMiddleText("确定")
//                    .setCancelAble(false).setTouchOutsideCancel(false)
//                    .setClickButtonDismiss(true)
//                    .create().show();
            //todo 这时用dialog的edittext，会直接走ondestroy方法
//            if (inputDialog == null) {
//                inputDialog = new ScanInputDialog(StockInActivity.this);
//            }
//            if (!inputDialog.isShowing()) {
//                inputDialog.show();
//            }
//            inputDialog.setOnViewClickListener(new OnViewClickListener() {
//                @Override
//                public <T> void onClickAction(View v, String tag, T t) {
////                    parseScanResult((String) t);
//                    ToastUtil.showToastLong("Scanned: " + ((String) t));
//                    inputDialog.dismiss();
//                }
//            });
//            inputDialog.setOnEditTextInputCompleteListener(new OnEditTextInputCompleteListener() {
//                @Override
//                public void onEditTextInputComplete(String content) {
//                    parseScanResult(content);
//                }
//            });


//            new IntentIntegrator(StockInActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();

            //todo通过扫描枪 当控制editText显示隐藏时，这个方法会随着扫描反复调用 ，故需要做些处理
//            if (inputEditText.getVisibility() == View.GONE) {
//
//                inputEditText.setVisibility(View.VISIBLE);
//                addTrayScannerButton.setText("扫描增加托盘");
//            } else {
//                addTrayScannerButton.setText("开始扫描");
//                parseScanResult(inputEditText.getText().toString());
//
//            }
            ToastUtil.showToastLong("请直接用扫码枪进行扫描，确保蓝牙已连接配对！");
        } else if (view == addTrayPhotoButton) {
            new IntentIntegrator(StockInActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
//            parseScanResult("");
        } else if (view == scanAreaButton) {
            if (boxItemEntityList.size() > 0) {
                int selectedcount = 0;
                for (int i = 0; i < boxItemEntityList.size(); i++) {
                    if (boxItemEntityList.get(i).getSelect()) {
                        selectedcount++;
                    }
                }
                if (selectedcount > 0) {
                    new IntentIntegrator(StockInActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
                    inputEditText.setText("");
                } else {
                    ToastUtil.showToastShort("请选择条目！");
                }

            } else {
                ToastUtil.showToastShort("没有物品条码或仓库位置码没有成功，请重新扫描！");
            }
        } else if (view == warehouseInButton) {
            handleIntoWareHouse();
        } else if (view == switchLayout) {
            stockSwitch.performClick();
        }
    }

    private void parseScanResult(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
//        content = "VB/MT/579807/S/3506/IV/38574/P/T17-1130-1 A0/D/20190619/L/19061903/N/49/Q/114";






//        content = content.trim();
//        content = content.replace(" ",""); 不能随便去空格，因有些 D00 A5样式
        System.out.println("============ scan content = " + content);
        // VB/MT/579807/S/3506/IV/38574/P/T17-1130-1 A0/D/20190619/L/19061903/N/49/Q/114
        if (content.contains("\n")) {
            content = content.replace("\n", "");
        }
        if (content.contains("/") || content.contains("／")) {
            if (content.contains("／")) {
                content = content.replace("／", "/");
            }

            String[] qrContent;
            qrContent = content.split("/");
            if (qrContent.length >= 2) {
                String qrTitle = qrContent[0];
                if (!qrTitle.equals("")) {
                    if (qrTitle.equals("VE") || qrTitle.equals("VF") || qrTitle.equals("VG") || qrTitle.equals("V9") || qrTitle.equals("VA") || qrTitle.equals("VB") || qrTitle.equals("VC")) {
                        //物品条码
                        enqueueBoxScan(content);
                    }
                }

                if (content.startsWith("/SUB_IST_ID/") || content.startsWith("/IST_ID/") ||
                        content.startsWith("/SUB——IST——ID/") || content.startsWith("/IST——ID/")) {
                    if (content.startsWith("/SUB——IST——ID/")) {
                        content = content.replace("/SUB——IST——ID/", "/SUB_IST_ID/");
                    }

                    if (content.startsWith("/IST——ID/")) {
                        content = content.replace("/IST——ID/", "/IST_ID/");
                    }
                    //仓库位置码
                    startIstScan(content);
                }
            }
        }
    }

    private void handleIntoWareHouse() {
        if (isExecutingWarehouseIn) {
            return;
        }
        if (boxItemEntityList.size() > 0) {
            int selectedcount = 0;
            for (int i = 0; i < boxItemEntityList.size(); i++) {
                if (boxItemEntityList.get(i).getSelect()) {
                    if (boxItemEntityList.get(i).getIst_ID() == 0) {
//                            CommonUtil.ShowToast(StockInActivity.this, "还没有扫描库位", R.mipmap.warning, Toast.LENGTH_SHORT);
                        ToastUtil.showToastLong("还没有扫描库位");
                        return;
                    }
                    selectedcount++;
                }

            }
            if (selectedcount > 0) {
                if (UserSingleton.get().getHRID() > 0 && !TextUtils.isEmpty(UserSingleton.get().getHRName())){
                    isExecutingWarehouseIn = true;
                    warehouseInButton.setEnabled(false);
                    AsyncExeWarehouseIn task = new AsyncExeWarehouseIn(buildSelectedBoxItemList(), new ArrayList<>(scanCodeList));
                    task.execute();
                }else{
                    CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(StockInActivity.this)
                            .setTitle("").setMessage("您当前程序账号有误，需重新登录！")
                            .setLeftText("确定");


                    builder.setOnViewClickListener(new OnDialogViewClickListener() {
                        @Override
                        public void onViewClick(Dialog dialog, View v, int tag) {
                            switch (tag) {
                                case CommAlertDialog.TAG_CLICK_LEFT:
                                    CommonUtil.doLogout(StockInActivity.this);
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    });
                    builder.create().show();


//                    CommonUtil.doLogout(StockInActivity.this);
//                    EventBus.getDefault().post(new LogoutEvent());
                }
            }

        } else {
            //// TODO: 2019/7/10  这里应控件按钮的可用性
            ToastUtil.showToastShort("没有物品条码或仓库位置码没有成功，请重新扫描！");
        }
    }

    private void refreshBoxItemList() {
        if (boxItemAdapter == null) {
            boxItemAdapter = new InBoxItemAdapter(StockInActivity.this, boxItemEntityList);
            mRecyclerView.setAdapter(boxItemAdapter);
            return;
        }
        if (mRecyclerView == null) {
            boxItemAdapter.notifyDataSetChanged();
            return;
        }
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                if (boxItemAdapter != null) {
                    boxItemAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void enqueueBoxScan(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        pendingBoxScanQueue.add(content);
        processNextBoxScan();
    }

    private void processNextBoxScan() {
        if (isProcessingBoxScan || pendingBoxScanQueue.isEmpty()) {
            return;
        }
        isProcessingBoxScan = true;
        scanContent = pendingBoxScanQueue.remove(0);
        scanCode = scanContent;
        GetBoxAsyncTask task = new GetBoxAsyncTask(scanContent);
        task.execute();
    }

    private void finishBoxScan() {
        isProcessingBoxScan = false;
        processNextBoxScan();
    }

    private void startIstScan(String content) {
        if (TextUtils.isEmpty(content) || isProcessingIstScan || isExecutingWarehouseIn) {
            return;
        }
        isProcessingIstScan = true;
        scanContent = content;
        GetIstAsyncTask task = new GetIstAsyncTask(content);
        task.execute();
    }

    private void trackScanCode(BoxItemEntity boxItemEntity, String content) {
        if (boxItemEntity == null || TextUtils.isEmpty(content)) {
            return;
        }
        scanCodeList.add(content);
        trackedScanDiiiIdList.add(boxItemEntity.getDIII_ID());
    }

    private void removeTrackedScanCode(BoxItemEntity boxItemEntity) {
        if (boxItemEntity == null) {
            return;
        }
        for (int i = 0; i < trackedScanDiiiIdList.size(); i++) {
            if (trackedScanDiiiIdList.get(i) == boxItemEntity.getDIII_ID()) {
                trackedScanDiiiIdList.remove(i);
                if (i < scanCodeList.size()) {
                    scanCodeList.remove(i);
                }
                break;
            }
        }
    }

    private List<BoxItemEntity> buildSelectedBoxItemList() {
        List<BoxItemEntity> selectedList = new ArrayList<>();
        for (int i = 0; i < boxItemEntityList.size(); i++) {
            if (boxItemEntityList.get(i).getSelect()) {
                selectedList.add(boxItemEntityList.get(i));
            }
        }
        return selectedList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("===========onDestroy");
        inputEditText.removeCallbacks(parseInputRunnable);
        if (inputDialog != null && inputDialog.isShowing()) {
            inputDialog.dismiss();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("BoxItemList", (Serializable) boxItemEntityList);

    }

    private class GetIsAlarmItemAsyncTask extends AsyncTask<String, Void, Void> {
        WsResult result;

        @Override
        protected Void doInBackground(String... ids) {
            if (ids.length > 0){
                int itemID = Integer.parseInt(ids[0]);
                result = WebServiceUtil.getIsAlarmItem(UserSingleton.get().getUserInfo().getBu_ID(),itemID);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (result != null) {
                if (result.getResult()) {
                    String  info = result.getErrorInfo();
                    if (info.contains("紧急物料")) {
                        CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(StockInActivity.this)
                                .setTitle("").setMessage("此物料为紧急物料！")
                                .setLeftText("确定");


                        builder.setOnViewClickListener(new OnDialogViewClickListener() {
                            @Override
                            public void onViewClick(Dialog dialog, View v, int tag) {
                                switch (tag) {
                                    case CommAlertDialog.TAG_CLICK_LEFT:
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        });
                        builder.create().show();
                    } else {

                    }

                }
            }

        }
    }

    private class GetBoxAsyncTask extends AsyncTask<String, Void, Void> {
        private final String requestScanContent;
        BoxItemEntity scanBoxItemEntity;

        GetBoxAsyncTask(String requestScanContent) {
            this.requestScanContent = requestScanContent;
        }

        @Override
        protected Void doInBackground(String... params) {

            BoxItemEntity boxItemEntity = null;
            if(UserSingleton.get().getUserInfo().getBu_ID() ==149 || UserSingleton.get().getUserInfo().getBu_ID() == 155){

                boxItemEntity = WebServiceUtil.op_Check_Commit_DS_Item_Income_Barcode_ML(requestScanContent);
            }else {
                boxItemEntity = WebServiceUtil.op_Check_Commit_DS_Item_Income_Barcode(requestScanContent);
            }

            //// TODO: 2020/10/19 test
            String s = JsonUtil.objectToJson(boxItemEntity);

            scanBoxItemEntity = boxItemEntity;
            return null;
        }

        protected Boolean is_box_existed(BoxItemEntity box_item) {
            Boolean result = false;
            if (boxItemEntityList != null) {
                for (int i = 0; i < boxItemEntityList.size(); i++) {
                    if (boxItemEntityList.get(i).getDIII_ID() == box_item.getDIII_ID()) {
                        return true;
                    }
                }
            }

            return result;
        }


        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");
            if (scanBoxItemEntity != null) {
                if (!scanBoxItemEntity.getResult()) {
                    Toast.makeText(StockInActivity.this, scanBoxItemEntity.getErrorInfo(), Toast.LENGTH_LONG).show();
                } else if (scanBoxItemEntity.getCompany_ID() != 0
                        && scanBoxItemEntity.getCompany_ID() != UserSingleton.get().getUserInfo().getCompany_ID()) {
                    Message message = new Message();
                    message.what = 0;
                    handler.sendMessage(message);
                } else if (is_box_existed(scanBoxItemEntity)) {
                    Toast.makeText(StockInActivity.this, "该包装已经在装载列表中", Toast.LENGTH_LONG).show();
                } else {
                    if (isOpenSuggestStock) {
                        Message message = new Message();
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("suggest_ist", scanBoxItemEntity.getIstName());
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                    scanBoxItemEntity.setSelect(true);
                    boxItemEntityList.add(scanBoxItemEntity);
                    trackScanCode(scanBoxItemEntity, requestScanContent);
                    refreshBoxItemList();
                }
            }

            inputEditText.setText("");
            inputEditText.setHint("请继续使用扫描枪");
            pendingScanText = "";
            if (scanBoxItemEntity != null && scanBoxItemEntity.getResult() && scanBoxItemEntity.getItem_ID() > 0) {
                GetIsAlarmItemAsyncTask task = new GetIsAlarmItemAsyncTask();
                task.execute(scanBoxItemEntity.getItem_ID() + "");
            }
            finishBoxScan();
        }

        @Override
        protected void onPreExecute() {
            //pbScan.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

//    private void jumpToSelectPurchaseOrderActivity(int item_ID ,int toBu_ID) {
//        Intent intent = new Intent(StockInActivity.this, SelectPurchaseOrderListActivity.class);
//        intent.putExtra(IntentConstant.Intent_Extra_MY_Purchase_Order_Bu_ID,toBu_ID);
//        intent.putExtra(IntentConstant.Intent_Extra_MY_Purchase_Order_Item_ID,item_ID);
//        startActivityForResult(intent,IntentConstant.Intent_Request_Stock_in_To_Purchase_Order_Activity);
//
//    }
//
//    private void getPOList(long item_id) {
//        GetPOOrderListAsyncTask task = new GetPOOrderListAsyncTask();
//        task.execute(item_id + "");
//    }


    private class GetPOOrderListAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String sitem_ID = strings[0];
            String sql = String.format("SELECT POI.POI_ID,POI.PO_ID\n" +
                    "  ,Case When PO.Ver=1 Then PO_No Else POR.Release_No End As PO_No\n" +
                    "  ,Case When PO.Ver=1 Then PO_Date Else POR.Release_Date End As 下单日期\n" +
                    "  ,Item.KisCode,Item.Item_ID,Item.Item As 物料编码, Item.Item_Name As 物料,Item.Item_Spec2 As 规格,Item_Version.Item_Version As 版本,Item.Item_Unit As 单位\n" +
                    "  ,POI.POI_Quantity AS 采购数量,POI.POI_In_Qty AS 已关联数量,POI.POI_Quantity-ISNULL(POI.POI_In_Qty,0) AS 未关联数量,POI.ML_Kis_BillNo AS 金蝶采购单号\n" +
                    "  FROM Purchase_Order_Item AS POI\n" +
                    "  INNER JOIN Purchase_Order AS PO ON PO.PO_ID=POI.PO_ID\n" +
                    "  Inner join Item_Version On Item_Version.IV_ID=POI.IV_ID \n" +
                    "  Inner Join Item On Item_Version.Item_ID=Item.Item_ID\n" +
                    "  Left Join Purchase_Order_Release As POR On POR.POR_ID = POI.POR_ID\n" +
                    "  WHERE PO.BU_ID=%d AND POI.Item_ID=%s AND POI.PO_Status_ID IN(1,2)\n" +
                    "   AND POI.ML_Kis_FID>0 AND POI_Quantity<>POI_In_Qty AND PO_Date>='2025-12-01'",UserSingleton.get().getUserInfo().getBu_ID(),sitem_ID );

            WsResult result = WebServiceUtil.getDataTable(sql);
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                System.out.println("============================jsonData = " + jsonData);
                if (!TextUtils.isEmpty(jsonData)) {
                    return jsonData;

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String json) {
            if (!TextUtils.isEmpty(json) && !json.trim().equals("[]")) {

            }
        }
    }


    private class GetIstAsyncTask extends AsyncTask<String, Void, Void> {
        private final String requestScanContent;
        private IstPlaceEntity scanIstPlaceEntity;

        GetIstAsyncTask(String requestScanContent) {
            this.requestScanContent = requestScanContent;
        }

        @Override
        protected Void doInBackground(String... params) {
            scanIstPlaceEntity = WebServiceUtil.op_Check_Commit_IST_Barcode(requestScanContent);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");
            inputEditText.setText("");
            pendingScanText = "";
            if (scanIstPlaceEntity != null && scanIstPlaceEntity.getResult()) {
                thePlace = scanIstPlaceEntity;
                for (int i = 0; i < boxItemEntityList.size(); i++) {
                    if (boxItemEntityList.get(i).getSelect()) {
                        boxItemEntityList.get(i).setIstName(scanIstPlaceEntity.getIstName());
                        boxItemEntityList.get(i).setIst_ID(scanIstPlaceEntity.getIst_ID());
                        boxItemEntityList.get(i).setSub_Ist_ID(scanIstPlaceEntity.getSub_Ist_ID());
                    }
                }
                refreshBoxItemList();
                //todo 直接执行入库登帐
                handleIntoWareHouse();
            } else if (scanIstPlaceEntity != null) {
                ToastUtil.showToastLong(scanIstPlaceEntity.getErrorInfo());
            }
            isProcessingIstScan = false;

        }


    }

    private class AsyncExeWarehouseIn extends AsyncTask<String, Void, Void> {
        WsResult ws_result;
        private final List<BoxItemEntity> selectedList;
        private final ArrayList<String> committedScanCodeList;
        private final List<BoxItemEntity> successList = new ArrayList<>();

        AsyncExeWarehouseIn(List<BoxItemEntity> selectedList, ArrayList<String> committedScanCodeList) {
            this.selectedList = selectedList;
            this.committedScanCodeList = committedScanCodeList;
        }

        @Override
        protected Void doInBackground(String... params) {
            int selectedCount = selectedList.size();
            for (int count = 0; count < selectedCount; count++) {
                BoxItemEntity boxItemEntity = selectedList.get(count);
//                String sql = String.format("insert into Ist_SubIst_ManuLot (IST_ID,Sub_IST_ID,Item_ID,IV_ID,LotID,Company_ID,Bu_ID,ManuLotNo，SendToWarehouseTime) values (%d,%d,%d,%d,%d,%d,%d,%s,%s)",
                String sql = String.format("insert into Ist_SubIst_ManuLot (IST_ID,Sub_IST_ID,Item_ID,IV_ID,LotID,Company_ID,Bu_ID,ManuLotNo) values (%d,%d,%d,%d,%d,%d,%d,%s)",
                        boxItemEntity.getIst_ID(), boxItemEntity.getSub_Ist_ID(), boxItemEntity.getItem_ID(), boxItemEntity.getIV_ID(), boxItemEntity.getLotID(),
                        UserSingleton.get().getUserInfo().getCompany_ID(), UserSingleton.get().getUserInfo().getBu_ID(),
//                        !TextUtils.isEmpty(boxItemEntity.getManuLotNo()) ? boxItemEntity.getManuLotNo() : boxItemEntity.getLotNo());
//                        boxItemEntity.getLotNo(),UnitFormatUtil.formatTimeToSecond(System.currentTimeMillis()));
                        boxItemEntity.getLotNo());

//                ws_result = WebServiceUtil.op_Commit_DS_Item_Income_To_Warehouse(boxItemEntity,sql);
                ws_result = WebServiceUtil.op_Commit_DS_Item_Income_To_Warehouse(boxItemEntity,
                        committedScanCodeList.size() == selectedCount ? committedScanCodeList.get(0) : "");
                if (ws_result.getResult()) {
                    successList.add(boxItemEntity);
                } else {
                    break;
                }
            }

            return null;
        }

//        private void addIstSubIstManuLotRelation(BoxItemEntity boxItemEntity) {
//            if (boxItemEntity != null) {
//                String sql = String.format("insert into Ist_SubIst_ManuLot (IST_ID,Sub_IST_ID,Item_ID,IV_ID,LotID,Company_ID,Bu_ID,ManuLotNo) values (%d,%d,%d,%d,%d,%d,%d,%s)",
//                        boxItemEntity.getIst_ID() ,boxItemEntity.getSub_Ist_ID(),boxItemEntity.getItem_ID(),boxItemEntity.getIV_ID(),boxItemEntity.getLotID(),
//                        UserSingleton.get().getUserAllInfoEntity().getCompanyID(),UserSingleton.get().getUserInfo().getBu_ID(),boxItemEntity.getManuLotNo());
//
//
//            }
//        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");
            if (!successList.isEmpty()) {
                for (int i = 0; i < successList.size(); i++) {
                    removeTrackedScanCode(successList.get(i));
                }
                boxItemEntityList.removeAll(successList);
            }

            if (ws_result != null) {
                CommonUtil.ShowWsResultToast(StockInActivity.this, ws_result, "入库完成");
            }
            warehouseInButton.setEnabled(true);
            isExecutingWarehouseIn = false;

            refreshBoxItemList();
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


}
