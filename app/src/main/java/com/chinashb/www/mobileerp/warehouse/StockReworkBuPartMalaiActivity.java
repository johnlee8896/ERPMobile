package com.chinashb.www.mobileerp.warehouse;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.ReturnItemAdapter;
import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.basicobject.IstPlaceEntity;
import com.chinashb.www.mobileerp.basicobject.MpiWcBean;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.StringUtils;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CommonSelectInputDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

/***
 * @date 创建时间 5/1/26 4:57 PM
 * @author 作者: liweifeng
 * @description
 */
public class StockReworkBuPartMalaiActivity extends BaseActivity {

    private MpiWcBean themw;
    private Button btnAddTray;
    private Button btnWarehouseOut;
    private Button btnRemark;
    private RecyclerView mRecyclerView;
    private TextView remarkTextView;
    private EditText inputEditText;

    private ReturnItemAdapter returnItemAdapter;
    private List<BoxItemEntity> newissuelist;
    private IstPlaceEntity thePlace;
    private String scanstring;
    private String remark = "";
    private CommonSelectInputDialog remarkDialog;

    private OnViewClickListener onRemarkViewClickListener = new OnViewClickListener() {
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
        setContentView(R.layout.activity_stock_out_return_bu_part_malai_layout);

//        tv = (TextView)findViewById(R.id.tv_stock_system_title);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_return_items);
        btnAddTray = (Button) findViewById(R.id.btn_return_item_add_extra);
        btnWarehouseOut = (Button) findViewById(R.id.btn_exe_warehouse_out);
        btnRemark = (Button) findViewById(R.id.btn_add_remark);
        remarkTextView = findViewById(R.id.tv_remark);
        inputEditText = findViewById(R.id.stock_out_return_wc_input_EditeText);

        newissuelist = new ArrayList<>();
        if (savedInstanceState != null) {
            newissuelist = (List<BoxItemEntity>) savedInstanceState.getSerializable("BoxItemList");
        }
        returnItemAdapter = new ReturnItemAdapter(StockReworkBuPartMalaiActivity.this, newissuelist);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        mRecyclerView.setAdapter(returnItemAdapter);

        Intent intent = getIntent();
        themw = (MpiWcBean) intent.getSerializableExtra("mw");
        if (themw != null) {
        }

        btnAddTray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //StockOutMoreActivity.AsyncDirectGetBox task = new StockOutMoreActivity.AsyncDirectGetBox();
                //task.execute();
                new IntentIntegrator(StockReworkBuPartMalaiActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
            }

        });

        btnWarehouseOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!StringUtils.isStringValid(remark)){
                    ToastUtil.showToastShort("请添加备注！");
                    return;
                }

                if (newissuelist.size() > 0) {
                    StockReworkBuPartMalaiActivity.AsyncExeWarehouseOut task = new StockReworkBuPartMalaiActivity.AsyncExeWarehouseOut();
                    task.execute();
                }
            }
        });

        inputEditText.addTextChangedListener(new TextWatcherImpl(){
            @Override
protected void onTextChangedSafe(CharSequence text) {
                //                if (text.toString().endsWith("\n")){
                if (text.toString().length() > 0) {
//                    ToastUtil.showToastLong("扫描结果:" + text.toString());
                    System.out.println("========================扫描结果:" + text.toString());
                    parseScanResult(text.toString());
                }
            }
        });

        btnRemark.setOnClickListener(v -> {
            if (remarkDialog == null){
                remarkDialog = new CommonSelectInputDialog(StockReworkBuPartMalaiActivity.this);
            }
//            remarkDialog.setSelectOnly(true);
            remarkDialog.show();
            //这句必须放在show之后
//            remarkDialog.setSelectOnly(true);
            remarkDialog.setInputOnly(true);
            remarkDialog.setOnViewClickListener(onRemarkViewClickListener);
        });

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
                        StockReworkBuPartMalaiActivity.GetReturnBoxAsyncTask task = new StockReworkBuPartMalaiActivity.GetReturnBoxAsyncTask();
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


    private class GetReturnBoxAsyncTask extends AsyncTask<String, Void, Void> {
        BoxItemEntity scanresult;
        @Override
        protected Void doInBackground(String... params) {
            BoxItemEntity bi = WebServiceUtil.op_Check_Commit_WC_Return_Item_Barcode(scanstring);
            scanresult = bi;
            if (bi.getResult() ) {
                if (!is_box_existed(bi)) {
                    bi.setSelect(true);
                    newissuelist.add(bi);
                } else {
                    bi.setResult(false);
                    bi.setErrorInfo("该包装已经在装载列表中");
                }

            } else {

            }

            return null;
        }

        protected boolean is_box_existed(BoxItemEntity box_item) {
            boolean result = false;
            if (newissuelist != null) {
                for (int i = 0; i < newissuelist.size(); i++) {
                    if (newissuelist.get(i).getDIII_ID() == box_item.getDIII_ID()) {
                        return true;
                    }
                }
            }

            return result;
        }


        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");
            //// TODO: 2019/7/24 返工出库处理
            if (scanresult != null) {
                if (!scanresult.getResult() ) {
                    Toast.makeText(StockReworkBuPartMalaiActivity.this, scanresult.getErrorInfo(), Toast.LENGTH_LONG).show();
                }
            }

            returnItemAdapter = new ReturnItemAdapter(StockReworkBuPartMalaiActivity.this, newissuelist);
            mRecyclerView.setAdapter(returnItemAdapter);
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
            //2023-04-20 数量修改后要保存
            newissuelist = returnItemAdapter.getDataList();

            while (count < 10 && newissuelist.size() > 0) {
                BoxItemEntity bi = newissuelist.get(0);
                ws_result = WebServiceUtil.op_Commit_Return_Item(bi,remark);


                if (ws_result.getResult() ) {
                    newissuelist.remove(bi);
                    newissuelist.remove(bi);
                }

                count++;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");

            if (ws_result != null) {
                if (!ws_result.getResult() ) {
                    CommonUtil.ShowToast(StockReworkBuPartMalaiActivity.this, ws_result.getErrorInfo(), R.mipmap.warning);

                } else {
                    CommonUtil.ShowToast(StockReworkBuPartMalaiActivity.this, "出库完成", R.mipmap.smiley);
                }

            }

            returnItemAdapter = new ReturnItemAdapter(StockReworkBuPartMalaiActivity.this, newissuelist);
            mRecyclerView.setAdapter(returnItemAdapter);
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

