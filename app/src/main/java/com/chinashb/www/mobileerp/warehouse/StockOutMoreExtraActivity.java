package com.chinashb.www.mobileerp.warehouse;

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
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.DialogSelectAddRemarkDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StockOutMoreExtraActivity extends BaseActivity {

    private MpiWcBean themw;

    private TextView txtMw_Title;

    private Button btnAddTray;
    private Button btnRemark;
    private Button btnWarehouseOut;

    private RecyclerView mRecyclerView;
    private EditText inputEditText;
    private TextView remarkTextView;

    private IssueMoreItemAdapter issueMoreItemAdapter;
    private List<BoxItemEntity> newissuelist;
    private IstPlaceEntity thePlace;
    private String scanstring;
    private String remark = "";
    private DialogSelectAddRemarkDialog remarkDialog;

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
        setContentView(R.layout.activity_stock_out_more_extra_layout);

//        tv = (TextView)findViewById(R.id.tv_stock_system_title);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_issue_more_extra);

        txtMw_Title = (TextView) findViewById(R.id.tv_issue_more_mw_title);
        btnAddTray = (Button) findViewById(R.id.btn_issue_more_add_extra);
        inputEditText = findViewById(R.id.stock_out_more_extra_input_EditeText);
        remarkTextView = findViewById(R.id.stock_out_more_remark_EditText);
        btnWarehouseOut = (Button) findViewById(R.id.btn_exe_warehouse_out);
        btnRemark = findViewById(R.id.btn_remark);


        newissuelist = new ArrayList<>();
        if (savedInstanceState != null) {
            newissuelist = (List<BoxItemEntity>) savedInstanceState.getSerializable("BoxItemList");
        }
        issueMoreItemAdapter = new IssueMoreItemAdapter(StockOutMoreExtraActivity.this, newissuelist);
        issueMoreItemAdapter.showNeedMore = false;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        mRecyclerView.setAdapter(issueMoreItemAdapter);

        Intent who = getIntent();
        themw = (MpiWcBean) who.getSerializableExtra("mw");
        if (themw != null) {
            themw.setMwNameTextView(txtMw_Title);

        }

        setHomeButton();

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
                remarkDialog = new DialogSelectAddRemarkDialog(StockOutMoreExtraActivity.this);
            }
            remarkDialog.show();
            remarkDialog.setOnViewClickListener(onViewClickListener);
        });


        btnWarehouseOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(remarkTextView.getText().toString())){
                    ToastUtil.showToastShort("请为本操作添加备注");
//                    remarkTextView.requestFocus();
                    return;
                }

                if (newissuelist.size() > 0) {
                    AsyncExeWarehouseOut task = new AsyncExeWarehouseOut();
                    task.execute();

                }

            }

        });

        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                if (editable.toString().endsWith("\n")) {
//                    ToastUtil.showToastLong("扫描结果:" + editable.toString());
                    System.out.println("========================扫描结果:" + editable.toString());
                    parseScanResult(editable.toString());
                }
            }
        });

//        remarkTextView.addTextChangedListener(new TextWatcherImpl(){
//            @Override public void afterTextChanged(Editable editable) {
//                super.afterTextChanged(editable);
//                remark = editable.toString();
//            }
//        });


    }

    private void parseScanResult(String result) {
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
                        scanstring = result;
                        GetIssueMoreExtraBoxAsyncTask task = new GetIssueMoreExtraBoxAsyncTask();
                        task.execute();
                    }
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

        @Override
        protected Void doInBackground(String... params) {

            Long MW_ID = themw.getMPIWC_ID();
            BoxItemEntity bi = WebServiceUtil.op_Check_Commit_MW_Issue_Extra_Item_Barcode(MW_ID, scanstring);
            scanresult = bi;
            if (bi.getResult() ) {
                if (!is_box_existed(bi)) {
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
            //tv.setText(fahren + "∞ F");

            if (scanresult != null) {
                if (!scanresult.getResult() ) {
                    Toast.makeText(StockOutMoreExtraActivity.this, scanresult.getErrorInfo(), Toast.LENGTH_LONG).show();
                }
            }

            mRecyclerView.setAdapter(issueMoreItemAdapter);
            inputEditText.setText("");
            inputEditText.setHint("请继续扫描");
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

        @Override
        protected Void doInBackground(String... params) {

            int count = 0;
            int newissuesize = newissuelist.size();
            while (count < newissuesize && newissuelist.size() > 0) {
                BoxItemEntity bi = newissuelist.get(0);
                ws_result = WebServiceUtil.op_Commit_MW_Issue_Extra_Item(themw.getMPIWC_ID(), bi,remark);

                if (ws_result.getResult() ) {
                    newissuelist.remove(bi);
                } else {
                    return null;
                }

                count++;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            issueMoreItemAdapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(issueMoreItemAdapter);
            //pbScan.setVisibility(View.INVISIBLE);
            remarkTextView.setText("");
            remark = "";
            if (ws_result != null) {
                if (!ws_result.getResult() ) {
                    CommonUtil.ShowToast(StockOutMoreExtraActivity.this, ws_result.getErrorInfo(), R.mipmap.warning, Toast.LENGTH_LONG);
                } else {
                    CommonUtil.ShowToast(StockOutMoreExtraActivity.this, "成功出库", R.mipmap.smiley, Toast.LENGTH_SHORT);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            //pbScan.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("BoxItemList", (Serializable) newissuelist);

    }
}
