package com.chinashb.www.mobileerp.warehouse;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.PartItemMiddleActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.IssueMoreItemAdapter;
import com.chinashb.www.mobileerp.adapter.PartInvQueryAdapter;
import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.basicobject.IstPlaceEntity;
import com.chinashb.www.mobileerp.basicobject.MpiWcBean;
import com.chinashb.www.mobileerp.basicobject.PartsEntity;
import com.chinashb.www.mobileerp.basicobject.PlanInnerDetailEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.AppUtil;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CommAlertDialog;
import com.chinashb.www.mobileerp.widget.OnDialogViewClickListener;
import com.chinashb.www.mobileerp.widget.TitleLayoutManagerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 继续投料页面
 */

public class StockOutMoreActivity extends BaseActivity {

    private MpiWcBean mpiWcBean;
    private List<PlanInnerDetailEntity> planInnerDetailEntityList;
    private TextView txtMwTitleTextView;
    private Button addTrayButton;
    private Button btnScanWC;
    private Button btnWarehouseOut;
    private RecyclerView recyclerView;
    private IssueMoreItemAdapter issueMoreItemAdapter;
    private List<BoxItemEntity> boxItemEntityList;
    private IstPlaceEntity thePlace;
    private String scanedString;
    private ProgressBar pbScan;
    private EditText inputEditText;
    private boolean isDirect;
    private TitleLayoutManagerView titleLayoutManagerView;
    private long currentItemId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_out_more_layout);

        recyclerView = (RecyclerView) findViewById(R.id.rv_issue_more);
        txtMwTitleTextView = (TextView) findViewById(R.id.tv_issue_more_mw_title);
        addTrayButton = (Button) findViewById(R.id.btn_issue_more_add);
        //btnScanWC = (Button) findViewById(R.id.btn_issue_more_wc);
        btnWarehouseOut = (Button) findViewById(R.id.btn_exe_warehouse_out);
        titleLayoutManagerView = findViewById(R.id.supply_product_put_titleLayout);

        pbScan = (ProgressBar) findViewById(R.id.pb_scan_progressbar);
        inputEditText = findViewById(R.id.stock_out_more_input_EditeText);

        boxItemEntityList = new ArrayList<>();

        if (savedInstanceState != null) {
            boxItemEntityList = (List<BoxItemEntity>) savedInstanceState.getSerializable("BoxItemList");
        }

        issueMoreItemAdapter = new IssueMoreItemAdapter(StockOutMoreActivity.this, boxItemEntityList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        recyclerView.setAdapter(issueMoreItemAdapter);

        Intent intent = getIntent();
        mpiWcBean = (MpiWcBean) intent.getSerializableExtra("mw");
        String title = intent.getStringExtra(IntentConstant.Intent_supplier_input_title);
        titleLayoutManagerView.setTitle(title);
        if (mpiWcBean != null) {
            mpiWcBean.setMwNameTextView(txtMwTitleTextView);
        }
        planInnerDetailEntityList = (List<PlanInnerDetailEntity>) intent.getSerializableExtra("IssuedItemList");
        isDirect = intent.getBooleanExtra(IntentConstant.Intent_continue_put_directly, false);
        setHomeButton();

        addTrayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(StockOutMoreActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
            }
        });

        btnWarehouseOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleStockOut();
            }

        });

        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
//                if (editable.toString().endsWith("\n")) {
                if (editable.toString().length() > 0) {
//                    ToastUtil.showToastLong("扫描结果:" + editable.toString());

//                    parseScanResult(editable.toString());
                    AfterGetItemBarcode(editable.toString());
                }
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("BoxItemList", (Serializable) boxItemEntityList);

    }

    private void handleStockOut() {
        if (boxItemEntityList.size() > 0) {
            if (UserSingleton.get().getHRID() > 0 && !TextUtils.isEmpty(UserSingleton.get().getHRName())){

                AsyncExeWarehouseOut task = new AsyncExeWarehouseOut();
                task.execute();
            }else{
                CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(StockOutMoreActivity.this)
                        .setTitle("").setMessage("您当前程序账号有误，需重新登录！")
                        .setLeftText("确定");


                builder.setOnViewClickListener(new OnDialogViewClickListener() {
                    @Override
                    public void onViewClick(Dialog dialog, View v, int tag) {
                        switch (tag) {
                            case CommAlertDialog.TAG_CLICK_LEFT:
                                CommonUtil.doLogout(StockOutMoreActivity.this);
                                dialog.dismiss();
                                break;
                        }
                    }
                });
                builder.create().show();
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
                //new IntentIntegrator(StockOutMoreActivity.this).initiateScan();
            } else {
                String contents = result.getContents();
//                Toast.makeText(this, "Scanned: " + contents, Toast.LENGTH_LONG).show();
                AfterGetItemBarcode(contents);

            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        //设置为横屏幕
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        super.onResume();
    }

    void AfterGetItemBarcode(String content) {
        System.out.println("========================扫描结果:" + content);
        //简单分析判别错误条码
        if (content.contains("/")) {
            String[] qrContent;
            qrContent = content.split("/");
            if (qrContent.length >= 2) {
                String qrTitle = qrContent[0];
                if (!qrTitle.equals("")) {
                    if (qrTitle.equals("VE") || qrTitle.equals("VF") || qrTitle.equals("VG") || qrTitle.equals("V9") || qrTitle.equals("VA") || qrTitle.equals("VB") || qrTitle.equals("VC")) {
                        //物品条码
                        scanedString = content;
                        GetIssueMoreBoxAsyncTask task = new GetIssueMoreBoxAsyncTask();
                        task.execute();
                    }
                }
            }
        }
    }

    private class GetIssueMoreBoxAsyncTask extends AsyncTask<String, Void, Void> {
        BoxItemEntity boxItemEntity;
        boolean scanNormal = true;

        @Override
        protected Void doInBackground(String... params) {
            Long MW_ID = mpiWcBean.getMPIWC_ID();
            BoxItemEntity boxItemEntity = WebServiceUtil.op_Check_Commit_MW_Issue_Item_Barcode(MW_ID, scanedString);
            this.boxItemEntity = boxItemEntity;
            if (boxItemEntity.getResult()) {
                if (!isBoxExist(boxItemEntity)) {
                    boxItemEntity.setSelect(true);
                    setNeedQty(boxItemEntity);

                    currentItemId = boxItemEntity.getItem_ID();
                    if (boxItemEntity.getQty() < 0){
                        scanNormal = false;

                    }

                    boxItemEntityList.add(boxItemEntity);
                } else {
                    boxItemEntity.setResult(false);
                    boxItemEntity.setErrorInfo("该包装已经在列表中");
                }
            }
            return null;
        }

        private boolean isBoxExist(BoxItemEntity boxItemEntity) {
            if (boxItemEntityList != null) {
                for (int i = 0; i < boxItemEntityList.size(); i++) {
                    if (boxItemEntity.getSMT_ID() > 0) {
                        return boxItemEntityList.get(i).getSMT_ID() == boxItemEntity.getSMT_ID();
                    }
                    if (boxItemEntity.getSMM_ID() > 0) {
                        return boxItemEntityList.get(i).getSMM_ID() == boxItemEntity.getSMM_ID();
                    }
                    if (boxItemEntity.getSMLI_ID() > 0) {
                        return boxItemEntityList.get(i).getSMLI_ID() == boxItemEntity.getSMLI_ID();
                    }

                }
            }
            return false;
        }

        //获取还需求数
        protected void setNeedQty(BoxItemEntity box_item) {
            if (box_item != null && planInnerDetailEntityList != null) {
                for (int i = 0; i < planInnerDetailEntityList.size(); i++) {
                    PlanInnerDetailEntity issued_item = planInnerDetailEntityList.get(i);
                    if (issued_item.getItem_ID() == box_item.getItem_ID()) {
                        box_item.setNeedMoreQty(issued_item.getMoreQty());
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            if (scanNormal){
                if (boxItemEntity != null) {
                    if (!boxItemEntity.getResult()) {
                        CommonUtil.ShowToast(StockOutMoreActivity.this, boxItemEntity.getErrorInfo(), R.mipmap.warning, Toast.LENGTH_LONG);
                    }
                }
                issueMoreItemAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(issueMoreItemAdapter);
                pbScan.setVisibility(View.INVISIBLE);
                if (isDirect) {
                    handleStockOut();
                }

            }else{
                ToastUtil.showToastLong("数量为负数，物料码有误，请重新扫描！");
            }
            inputEditText.setText("");
            inputEditText.setHint("请继续扫描");
        }

        @Override
        protected void onPreExecute() {
            pbScan.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //测试从服务器扫码，查看条码是否合规
    private class AsyncDirectGetBox extends AsyncTask<String, Void, Void> {
        BoxItemEntity scanresult;

        protected Boolean is_box_existed(BoxItemEntity box_item) {
            Boolean result = false;

            if (boxItemEntityList != null) {
                for (int i = 0; i < boxItemEntityList.size(); i++) {
                    if (boxItemEntityList.get(i).getSMLI_ID() == box_item.getSMLI_ID() && box_item.getSMLI_ID() > 0) {
                        return true;
                    }
                    if (boxItemEntityList.get(i).getSMM_ID() == box_item.getSMM_ID() && box_item.getSMM_ID() > 0) {
                        return true;
                    }
                    if (boxItemEntityList.get(i).getSMT_ID() == box_item.getSMT_ID() && box_item.getSMT_ID() > 0) {
                        return true;
                    }
                    if (boxItemEntityList.get(i).getSMT_ID() == box_item.getSMT_ID() && box_item.getSMT_ID() == 0
                            && boxItemEntityList.get(i).getSMM_ID() == box_item.getSMM_ID() && box_item.getSMM_ID() == 0
                            && boxItemEntityList.get(i).getSMLI_ID() == box_item.getSMLI_ID() && box_item.getSMLI_ID() == 0
                            && boxItemEntityList.get(i).getLotID() == box_item.getLotID()) {
                        return true;
                    }


                }
            }

            return result;
        }

        @Override
        protected Void doInBackground(String... params) {

            BoxItemEntity bi = WebServiceUtil.op_Check_Commit_MW_Issue_Item_Barcode((long) 471059, "VE/3655118");

            scanresult = bi;

            if (bi.getResult() ) {
                if (!is_box_existed(bi)) {
                    if (bi.getQty() > 0){
                        bi.setSelect(true);
                        boxItemEntityList.add(bi);
                    }
                } else {
                    bi.setResult(false);
                    bi.setErrorInfo("该包装已经在装载列表中");
                }


            } else {

            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");

            if (scanresult != null) {
                if (!scanresult.getResult() ) {
                    ToastUtil.showToastShort( scanresult.getErrorInfo());
                }
                if (scanresult.getQty() < 0){
                    ToastUtil.showToastShort("投料数量不能为负");
                }else{
                    issueMoreItemAdapter = new IssueMoreItemAdapter(StockOutMoreActivity.this, boxItemEntityList);
                    recyclerView.setAdapter(issueMoreItemAdapter);
                }
            }


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

    private class AsyncGetIst extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {


            IstPlaceEntity bi = WebServiceUtil.op_Check_Commit_IST_Barcode(scanedString);

            if (bi.getResult() ) {
                thePlace = bi;

                if (bi.getResult() ) {
                    for (int i = 0; i < boxItemEntityList.size(); i++) {
                        if (boxItemEntityList.get(i).getSelect() ) {
                            boxItemEntityList.get(i).setIstName(bi.getIstName());
                            boxItemEntityList.get(i).setIst_ID(bi.getIst_ID());
                            boxItemEntityList.get(i).setSub_Ist_ID(bi.getSub_Ist_ID());


                        }
                    }
                }
            } else {
                Toast.makeText(StockOutMoreActivity.this, bi.getErrorInfo(), Toast.LENGTH_LONG).show();
            }
            /*
            List<Boolean> result;
            MealTypeEntity mealType;
            for (Date d:weekDates) {
                mealType = new MealTypeEntity();
                mealType.setDate(d);
                result = WebServiceUtil.getFoodOrderDay(userInfo.getHrID(), d);
                if (result.size()==4)
                {
                    mealType.setBreakfast(result.get(0));
                    mealType.setLunch(result.get(1));
                    mealType.setDinner(result.get(2));
                    mealType.setSnack(result.get(3));
                }
                mealTypes.add(mealType);
            } */
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");

            recyclerView.setAdapter(issueMoreItemAdapter);
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

    private class AsyncExeWarehouseOut extends AsyncTask<String, Void, Void> {
        WsResult ws_result;

        protected void updateNeedQty(BoxItemEntity boxItemEntity) {
            if (boxItemEntity != null && planInnerDetailEntityList != null) {
                for (int i = 0; i < planInnerDetailEntityList.size(); i++) {
                    PlanInnerDetailEntity issued_item = planInnerDetailEntityList.get(i);
                    if (issued_item.getItem_ID() == boxItemEntity.getItem_ID()) {
                        float oldmoreqty = issued_item.getMoreQty();
                        float newmoreqty = oldmoreqty - boxItemEntity.getQty();
                        issued_item.setMoreQty(newmoreqty);

                    }
                }
            }
        }

        @Override
        protected Void doInBackground(String... params) {

            int count = 0;
            int size = boxItemEntityList.size();
            while (count < size && boxItemEntityList.size() > 0) {
                BoxItemEntity bi = boxItemEntityList.get(0);
                ws_result = WebServiceUtil.op_Commit_MW_Issue_Item(mpiWcBean.getMPIWC_ID(), bi);
                if (ws_result.getResult() ) {
                    boxItemEntityList.remove(bi);
                    updateNeedQty(bi);
                } else {
                    //遇到错误，停止
                    return null;
                }

                count++;
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {

            issueMoreItemAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(issueMoreItemAdapter);
            pbScan.setVisibility(View.INVISIBLE);

            if (ws_result != null) {
                if (!ws_result.getResult() ) {
                    CommonUtil.ShowToast(StockOutMoreActivity.this, ws_result.getErrorInfo(), R.mipmap.warning, Toast.LENGTH_LONG);
                } else {
//                    CommonUtil.ShowToast(StockOutMoreActivity.this, "成功出库", R.mipmap.smiley, Toast.LENGTH_SHORT);
                    ItemInvQueryAsyncTask task = new ItemInvQueryAsyncTask();
                    task.execute(currentItemId + "");




                }
            }
        }

        @Override
        protected void onPreExecute() {
            pbScan.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

private List<PartsEntity> partsEntityList;
    private class ItemInvQueryAsyncTask extends AsyncTask<String, Void, Void> {
        //ArrayList<PartsEntity> us = new ArrayList<PartsEntity>();
        @Override
        protected Void doInBackground(String... params) {
//            String keyWord = filterEditText.getText().toString();
            if (params != null && params.length > 0){
                String itemId = params[0];
                String js = WebServiceUtil.getQueryInv(UserSingleton.get().getUserInfo().getBu_ID(), 1, itemId, 1, 20);
                Gson gson = new Gson();
                partsEntityList = gson.fromJson(js, new TypeToken<List<PartsEntity>>() {
                }.getType());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");b
            if (partsEntityList == null || partsEntityList.size() == 0) {
//                dataLayout.setVisibility(View.GONE);
                ToastUtil.showToastShort("未找到当前物料的库存!");
            }else{
                double totalInv = 0;
               for (PartsEntity entity : partsEntityList){
                   totalInv += entity.getInv();
               }
                CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(StockOutMoreActivity.this)
                        .setTitle("提示").setMessage(String.format("出库成功，该物料当前库存为%s！",totalInv + ""))
                        .setLeftText("确定");


                builder.setOnViewClickListener(new OnDialogViewClickListener() {
                    @Override
                    public void onViewClick(Dialog dialog, View v, int tag) {
                        switch (tag) {
                            case CommAlertDialog.TAG_CLICK_LEFT:
                                currentItemId = 0;
                                dialog.dismiss();
                                break;
                        }
                    }
                });
                builder.create().show();
            }

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
