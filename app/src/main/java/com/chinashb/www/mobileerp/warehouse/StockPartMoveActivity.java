package com.chinashb.www.mobileerp.warehouse;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.MWBackUpListActivity;
import com.chinashb.www.mobileerp.PickGoodsNewShowActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.BoxMoveItemAdapter;
import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.basicobject.IstPlaceEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.IssueOutBackUpBean;
import com.chinashb.www.mobileerp.bean.PickGoodsBean;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CommAlertDialog;
import com.chinashb.www.mobileerp.widget.OnDialogViewClickListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/***
 * 移动库位页面
 */
public class StockPartMoveActivity extends BaseActivity implements View.OnClickListener {
    private Button btnAddTray;
    private Button btnScanArea;
    private Button btnWarehouseMove;
    private EditText inputEditText;
    private RecyclerView mRecyclerView;
    private BoxMoveItemAdapter boxitemAdapter;
    private List<BoxItemEntity> boxitemList;
    private IstPlaceEntity thePlace;
    private String scanstring;
    private RelativeLayout switchLayout;
    private Switch stockSwitch;
    private boolean isOpenSuggestStock = true;
    private boolean fromPickGoods ;
    private PickGoodsBean pickGoodsBean;
    private String backUpScanCode = "";//从备料来的标签，自动作移库提示
    private IssueOutBackUpBean backUpBean;
    private boolean isProcessingBoxScan = false;
    private boolean isProcessingIstScan = false;
    private boolean isExecutingWarehouseMove = false;
    private String pendingScanText = "";
    private final ArrayList<String> pendingBoxScanQueue = new ArrayList<>();
    private final Runnable parseInputRunnable = new Runnable() {
        @Override
        public void run() {
            if (!TextUtils.isEmpty(pendingScanText)) {
                parseScanResult(pendingScanText);
            }
        }
    };
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
        setContentView(R.layout.activity_stock_part_move_layout);

        fromPickGoods = getIntent().getBooleanExtra(IntentConstant.Intent_Extra_Send_Goods_Move_from,false);
        if (fromPickGoods){
            pickGoodsBean = getIntent().getParcelableExtra(IntentConstant.Intent_Extra_to_pick_goods_bean);
        }


//        tv = (TextView)findViewById(R.id.tv_stock_system_title);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_move_box);
        btnAddTray = (Button) findViewById(R.id.btn_move_add_tray);
        btnScanArea = (Button) findViewById(R.id.btn_move_scan_new_place);
        btnWarehouseMove = (Button) findViewById(R.id.btn_move_execute);
        inputEditText = findViewById(R.id.stock_move_input_EditeText);
        stockSwitch = findViewById(R.id.stock_move_suggest_stock_Switch);
        switchLayout = findViewById(R.id.stock_move_suggest_stock_Layout);

        boxitemList = new ArrayList<>();
        if (savedInstanceState != null) {
            boxitemList = (List<BoxItemEntity>) savedInstanceState.getSerializable("BoxItemList");
        }
        boxitemAdapter = new BoxMoveItemAdapter(StockPartMoveActivity.this, boxitemList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        mRecyclerView.setAdapter(boxitemAdapter);
        setHomeButton();

        btnAddTray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (boxitemList.size() < 10) {
                    new IntentIntegrator(StockPartMoveActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
                } else {
                    Toast.makeText(StockPartMoveActivity.this, "移动清单不超过10个 ", Toast.LENGTH_LONG).show();
                }
            }

        });

        btnScanArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (boxitemList.size() > 0) {
                    int selectedcount = 0;
                    for (int i = 0; i < boxitemList.size(); i++) {
                        if (boxitemList.get(i).getSelect()) {
                            selectedcount++;
                        }
                    }
                    if (selectedcount > 0) {
                        new IntentIntegrator(StockPartMoveActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
                    }

                }

            }

        });

        btnWarehouseMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleMoveStockArea();
            }
        });

        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
protected void onTextChangedSafe(CharSequence text) {
                String textValue = text == null ? "" : text.toString();
                if (textValue.length() > 7) {
                    pendingScanText = textValue;
                    inputEditText.removeCallbacks(parseInputRunnable);
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

        backUpScanCode = getIntent().getStringExtra(IntentConstant.Intent_Extra_Backup_ScanCode);
        backUpBean = getIntent().getParcelableExtra(IntentConstant.Intent_Extra_Backup_bean);
        if (!TextUtils.isEmpty(backUpScanCode)){
            parseScanResult(backUpScanCode);
        }

    }

    @Override
    protected void onResume() {
//设置为屏幕
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        super.onResume();
    }

    private void handleMoveStockArea() {
        if (isExecutingWarehouseMove) {
            return;
        }
        if (boxitemList.size() > 0) {
            int selectedcount = 0;
            for (int i = 0; i < boxitemList.size(); i++) {
                if (boxitemList.get(i).getSelect()) {
                    if (boxitemList.get(i).getIst_ID() == 0) {
                        ToastUtil.showToastShort("请先扫描新库位！");
                        return;
                    }
                    selectedcount++;
                }
            }
            if (selectedcount > 0) {
                if (UserSingleton.get().getHRID() > 0 && !TextUtils.isEmpty(UserSingleton.get().getHRName())) {
                    isExecutingWarehouseMove = true;
                    btnWarehouseMove.setEnabled(false);
                    AsyncExeWarehouseMove task = new AsyncExeWarehouseMove(buildSelectedBoxItemList());
                    task.execute();
                } else {
                    CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(StockPartMoveActivity.this)
                            .setTitle("").setMessage("您当前程序账号有误，需重新登录！")
                            .setLeftText("确定");


                    builder.setOnViewClickListener(new OnDialogViewClickListener() {
                        @Override
                        public void onViewClick(Dialog dialog, View v, int tag) {
                            switch (tag) {
                                case CommAlertDialog.TAG_CLICK_LEFT:
                                    CommonUtil.doLogout(StockPartMoveActivity.this);
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    });
                    builder.create().show();
                }
            }
        }
    }

    private void refreshMoveList() {
        if (boxitemAdapter == null) {
            boxitemAdapter = new BoxMoveItemAdapter(StockPartMoveActivity.this, boxitemList);
            mRecyclerView.setAdapter(boxitemAdapter);
            return;
        }
        boxitemAdapter.notifyDataSetChanged();
    }

    private List<BoxItemEntity> buildSelectedBoxItemList() {
        List<BoxItemEntity> selectedList = new ArrayList<>();
        for (int i = 0; i < boxitemList.size(); i++) {
            if (boxitemList.get(i).getSelect()) {
                selectedList.add(boxitemList.get(i));
            }
        }
        return selectedList;
    }

    private void enqueueBoxScan(String result) {
        if (TextUtils.isEmpty(result)) {
            return;
        }
        pendingBoxScanQueue.add(result);
        processNextBoxScan();
    }

    private void processNextBoxScan() {
        if (isProcessingBoxScan || pendingBoxScanQueue.isEmpty()) {
            return;
        }
        isProcessingBoxScan = true;
        scanstring = pendingBoxScanQueue.remove(0);
        GetBoxAsyncTask task = new GetBoxAsyncTask(scanstring);
        task.execute();
    }

    private void finishBoxScan() {
        isProcessingBoxScan = false;
        processNextBoxScan();
    }

    private void startIstScan(String result) {
        if (TextUtils.isEmpty(result) || isProcessingIstScan || isExecutingWarehouseMove) {
            return;
        }
        isProcessingIstScan = true;
        scanstring = result;
        GetIstAsyncTask task = new GetIstAsyncTask(scanstring);
        task.execute();
    }

    private void parseScanResult(String result) {
        System.out.println("===== result = " + result);
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
                        enqueueBoxScan(result);
                    }
                }

                if (result.startsWith("/SUB_IST_ID/") || result.startsWith("/IST_ID/")) {
                    //仓库位置码
                    startIstScan(result);
                }

            }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                //不要重复启动扫码
                //new IntentIntegrator(StockMoveActivity.this).initiateScan();
            } else {
                parseScanResult(result.getContents());
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == switchLayout) {
            stockSwitch.performClick();
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (fromPickGoods){
            Intent intent = new Intent(StockPartMoveActivity.this, PickGoodsNewShowActivity.class);
            intent.putExtra(IntentConstant.Intent_Extra_to_pick_goods_bean_back,pickGoodsBean);
//            setResult(IntentConstant.Intent_Request_Code_Pick_Goods_To_Stock_Move_Activity,intent);
            setResult(-1,intent);
            finish();
        }else{
            super.onBackPressed();
        }
//        finish();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("BoxItemList", (Serializable) boxitemList);

    }

    private class GetBoxAsyncTask extends AsyncTask<String, Void, Void> {
        private final String requestScanString;
        BoxItemEntity boxItemEntity;

        GetBoxAsyncTask(String requestScanString) {
            this.requestScanString = requestScanString;
        }

        @Override
        protected Void doInBackground(String... params) {
            BoxItemEntity bi;
            if (!TextUtils.isEmpty(backUpScanCode)){
                bi = WebServiceUtil.op_Check_Commit_Move_Item_Barcode_For_BackUp(requestScanString) ;
            }else{
                bi = WebServiceUtil.op_Check_Commit_Move_Item_Barcode(requestScanString);
            }
            boxItemEntity = bi;

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");
            if (boxItemEntity != null) {
                if (!boxItemEntity.getResult()) {
                    Toast.makeText(StockPartMoveActivity.this, boxItemEntity.getErrorInfo(), Toast.LENGTH_LONG).show();
                } else if (is_box_existed(boxItemEntity)) {
                    Toast.makeText(StockPartMoveActivity.this, "该包装已经在装载列表中", Toast.LENGTH_LONG).show();
                } else {
                    if (isOpenSuggestStock) {
                        Message message = new Message();
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("suggest_ist", boxItemEntity.getIstName());
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                    boxItemEntity.setSelect(true);
                    boxitemList.add(boxItemEntity);
                    refreshMoveList();
                }
            }
//            2024-08-08 拣货物料判断
            if (fromPickGoods && pickGoodsBean != null && boxItemEntity != null){
                if (boxItemEntity.getItem_ID() != pickGoodsBean.getItemID()){
                    CommonUtil.ShowToast(StockPartMoveActivity.this, "所选拣货物料与扫描物料不一致！", R.mipmap.monster_mike, Toast.LENGTH_SHORT);

                }
            }
            inputEditText.setText("");
            inputEditText.setHint("请继续扫描");
            pendingScanText = "";
            finishBoxScan();


        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

        protected boolean is_box_existed(BoxItemEntity box_item) {
            boolean result = false;
            if (boxitemList != null) {
                for (int i = 0; i < boxitemList.size(); i++) {
                    if (boxitemList.get(i).getDIII_ID() == box_item.getDIII_ID()) {
                        return true;
                    }
                }
            }
            return result;
        }

    }

    private class GetIstAsyncTask extends AsyncTask<String, Void, Void> {
        private final String requestScanString;
        private IstPlaceEntity bi;

        GetIstAsyncTask(String requestScanString) {
            this.requestScanString = requestScanString;
        }
        @Override
        protected Void doInBackground(String... params) {
            bi = WebServiceUtil.op_Check_Commit_IST_Barcode(requestScanString);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if ((bi != null) && bi.getResult()) {
                thePlace = bi;
                if (bi.getResult()) {
                    for (int i = 0; i < boxitemList.size(); i++) {
                        if (boxitemList.get(i).getSelect()) {
                            boxitemList.get(i).setIstName(bi.getIstName());
                            boxitemList.get(i).setIst_ID(bi.getIst_ID());
                            boxitemList.get(i).setSub_Ist_ID(bi.getSub_Ist_ID());
                        }
                    }
                }
                refreshMoveList();
                inputEditText.setText("");
                inputEditText.setHint("请继续扫描");
                pendingScanText = "";
                isProcessingIstScan = false;
                handleMoveStockArea();

            } else {
                Toast.makeText(StockPartMoveActivity.this, bi.getErrorInfo(), Toast.LENGTH_LONG).show();
                isProcessingIstScan = false;
            }


        }


    }

    private class AsyncExeWarehouseMove extends AsyncTask<String, Void, WsResult> {
        private final List<BoxItemEntity> selectedList;
        private final List<BoxItemEntity> successList = new ArrayList<>();

        AsyncExeWarehouseMove(List<BoxItemEntity> selectedList) {
            this.selectedList = selectedList;
        }

        @Override
        protected WsResult doInBackground(String... params) {
            WsResult lastResult = null;
            for (int i = 0; i < selectedList.size(); i++) {
                BoxItemEntity bi = selectedList.get(i);
                lastResult = WebServiceUtil.op_Commit_Move_Item(bi);
                if (lastResult == null || !lastResult.getResult()) {
                    return lastResult;
                }
                successList.add(bi);
            }
            return lastResult;
        }


        @Override
        protected void onPostExecute(WsResult result) {
            if (!successList.isEmpty()) {
                boxitemList.removeAll(successList);
            }
            if (result != null && result.getResult()) {
                CommonUtil.ShowToast(StockPartMoveActivity.this,
                        successList.size() > 1 ? "全部移库完成" : "移库完成",
                        R.mipmap.smiley,
                        Toast.LENGTH_SHORT);
            } else {
                String errorInfo = result == null ? "接口无返回结果" : result.getErrorInfo();
                ToastUtil.showToastShort("移库失败：" + errorInfo);
            }

            refreshMoveList();
            isExecutingWarehouseMove = false;
            btnWarehouseMove.setEnabled(true);
            if (!TextUtils.isEmpty(backUpScanCode)){
                backUpScanCode = "";
                Intent intent = new Intent(StockPartMoveActivity.this, MWBackUpListActivity.class);
                intent.putExtra(IntentConstant.Intent_Extra_backup_rework_move_result_boolean,result != null && result.getResult());
                intent.putExtra(IntentConstant.Intent_Extra_backup_rework_move_ist_id,thePlace != null ? thePlace.getIst_ID() : 0) ;
                intent.putExtra(IntentConstant.Intent_Extra_backup_rework_move_sub_ist_id,thePlace != null ? thePlace.getSub_Ist_ID() : 0);
                intent.putExtra(IntentConstant.Intent_Extra_Backup_bean,backUpBean);
                setResult(IntentConstant.Intent_Request_Backup_To_PartMove_Activity,intent);
                finish();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

}
