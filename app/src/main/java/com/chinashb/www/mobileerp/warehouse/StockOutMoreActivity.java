package com.chinashb.www.mobileerp.warehouse;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.IssueMoreItemAdapter;
import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.basicobject.Issued_Item;
import com.chinashb.www.mobileerp.basicobject.IstPlaceEntity;
import com.chinashb.www.mobileerp.basicobject.MpiWcBean;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 继续投料页面
 */

public class StockOutMoreActivity extends AppCompatActivity {

    private MpiWcBean themw;
    private List<Issued_Item> issuedItemList;
    private TextView txtMw_Title;
    private Button btnAddTray;
    private Button btnScanWC;
    private Button btnWarehouseOut;
    private RecyclerView recyclerView;
    private IssueMoreItemAdapter issueMoreItemAdapter;
    private List<BoxItemEntity> boxItemEntityList;
    private IstPlaceEntity thePlace;
    private String scanedItem;
    private ProgressBar pbScan;
    private EditText inputEditText;
    private boolean isDirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_out_more_layout);

        recyclerView = (RecyclerView) findViewById(R.id.rv_issue_more);
        txtMw_Title = (TextView) findViewById(R.id.tv_issue_more_mw_title);
        btnAddTray = (Button) findViewById(R.id.btn_issue_more_add);
        //btnScanWC = (Button) findViewById(R.id.btn_issue_more_wc);
        btnWarehouseOut = (Button) findViewById(R.id.btn_exe_warehouse_out);

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
        themw = (MpiWcBean) intent.getSerializableExtra("mw");
        if (themw != null) {
            themw.setMwNameTextView(txtMw_Title);
        }
        issuedItemList = (List<Issued_Item>) intent.getSerializableExtra("IssuedItemList");
        isDirect = intent.getBooleanExtra(IntentConstant.Intent_continue_put_directly, false);
        setHomeButton();

        btnAddTray.setOnClickListener(new View.OnClickListener() {
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
                if (editable.toString().endsWith("\n")) {
//                    ToastUtil.showToastLong("扫描结果:" + editable.toString());
                    System.out.println("========================扫描结果:" + editable.toString());
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
            AsyncExeWarehouseOut task = new AsyncExeWarehouseOut();
            task.execute();
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
        //简单分析判别错误条码
        if (content.contains("/")) {
            String[] qrContent;
            qrContent = content.split("/");
            if (qrContent.length >= 2) {
                String qrTitle = qrContent[0];
                if (!qrTitle.equals("")) {
                    if (qrTitle.equals("VE") || qrTitle.equals("VF") || qrTitle.equals("VG") || qrTitle.equals("V9") || qrTitle.equals("VA") || qrTitle.equals("VB") || qrTitle.equals("VC")) {
                        //物品条码
                        scanedItem = content;
                        GetIssueMoreBoxAsyncTask task = new GetIssueMoreBoxAsyncTask();
                        task.execute();
                    }
                }
            }
        }
    }

    private class GetIssueMoreBoxAsyncTask extends AsyncTask<String, Void, Void> {
        BoxItemEntity boxItemEntity;

        @Override
        protected Void doInBackground(String... params) {
            Long MW_ID = themw.getMPIWC_ID();
            BoxItemEntity boxItemEntity = WebServiceUtil.op_Check_Commit_MW_Issue_Item_Barcode(MW_ID, scanedItem);
            this.boxItemEntity = boxItemEntity;
            if (boxItemEntity.getResult()) {
                if (!isBoxExist(boxItemEntity)) {
                    boxItemEntity.setSelect(true);
                    setNeedQty(boxItemEntity);
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
            if (box_item != null && issuedItemList != null) {
                for (int i = 0; i < issuedItemList.size(); i++) {
                    Issued_Item issued_item = issuedItemList.get(i);
                    if (issued_item.getItem_ID() == box_item.getItem_ID()) {
                        box_item.setNeedMoreQty(issued_item.getMoreQty());
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Void result) {
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

            if (bi.getResult() == true) {
                if (!is_box_existed(bi)) {
                    bi.setSelect(true);
                    boxItemEntityList.add(bi);
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
                if (scanresult.getResult() == false) {
                    Toast.makeText(StockOutMoreActivity.this, scanresult.getErrorInfo(), Toast.LENGTH_LONG).show();
                }
            }

            issueMoreItemAdapter = new IssueMoreItemAdapter(StockOutMoreActivity.this, boxItemEntityList);
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

    private class AsyncGetIst extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {


            IstPlaceEntity bi = WebServiceUtil.op_Check_Commit_IST_Barcode(scanedItem);

            if (bi.getResult() == true) {
                thePlace = bi;

                if (bi.getResult() == true) {
                    for (int i = 0; i < boxItemEntityList.size(); i++) {
                        if (boxItemEntityList.get(i).getSelect() == true) {
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

        protected void UpdateNeedQty(BoxItemEntity box_item) {
            if (box_item != null && issuedItemList != null) {
                for (int i = 0; i < issuedItemList.size(); i++) {
                    Issued_Item issued_item = issuedItemList.get(i);
                    if (issued_item.getItem_ID() == box_item.getItem_ID()) {
                        float oldmoreqty = issued_item.getMoreQty();
                        float newmoreqty = oldmoreqty - box_item.getQty();
                        issued_item.setMoreQty(newmoreqty);

                    }
                }
            }
        }

        @Override
        protected Void doInBackground(String... params) {

            int count = 0;

            int newissuesize = boxItemEntityList.size();
            ;
            while (count < newissuesize && boxItemEntityList.size() > 0) {
                BoxItemEntity bi = boxItemEntityList.get(0);

                ws_result = WebServiceUtil.op_Commit_MW_Issue_Item(themw.getMPIWC_ID(), bi);

                if (ws_result.getResult() == true) {
                    boxItemEntityList.remove(bi);
                    UpdateNeedQty(bi);
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
                if (ws_result.getResult() == false) {
                    CommonUtil.ShowToast(StockOutMoreActivity.this, ws_result.getErrorInfo(), R.mipmap.warning, Toast.LENGTH_LONG);
                } else {
                    CommonUtil.ShowToast(StockOutMoreActivity.this, "成功出库", R.mipmap.smiley, Toast.LENGTH_SHORT);
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

}
