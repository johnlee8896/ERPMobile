package com.chinashb.www.mobileerp.warehouse;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.ScanIstItemProductAdapter;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.ScanIstItemProductBean;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2023/8/12 10:51 AM
 * @author 作者: liweifeng
 * @description
 */
public class ScanIstFindProductActivity extends BaseActivity {
    @BindView(R.id.scan_ist_product_input_EditText) EditText inputEditText;
    @BindView(R.id.scan_ist_product_scan_button) Button scanButton;
    @BindView(R.id.scan_ist_product_recyclerView) CustomRecyclerView recyclerView;

    private ScanIstItemProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_ist_item_product_layout);
        ButterKnife.bind(this);
        scanButton.setOnClickListener(v -> {
            new IntentIntegrator(ScanIstFindProductActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();

        });

        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                if (editable.toString().length() > 7) {
                    parseScanResult(editable.toString());
                }
            }
        });

        adapter = new ScanIstItemProductAdapter();
        recyclerView.setAdapter(adapter);

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

    @SuppressLint("NewApi")
    private void parseScanResult(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        if (content.contains("/") || content.contains("／")) {
            if (content.contains("／")) {
                content = content.replace("／", "/");
            }

            String[] qrContent;
            qrContent = content.split("/");
            if (qrContent.length >= 2) {
                String qrTitle = qrContent[0];

                if (content.startsWith("/SUB_IST_ID/") || content.startsWith("/IST_ID/") ||
                        content.startsWith("/SUB——IST——ID/") || content.startsWith("/IST——ID/")) {
                    if (content.startsWith("/SUB——IST——ID/")) {
                        content = content.replace("/SUB——IST——ID/", "/SUB_IST_ID/");
                    }

                    GetItemProductListByISTAsyncTask task = new GetItemProductListByISTAsyncTask();
                    task.execute(content);

                }
            }
        }
    }

    @SuppressLint("NewApi")
    private class GetItemProductListByISTAsyncTask extends AsyncTask<String, Void, List<ScanIstItemProductBean>> {
        WsResult ws_result;

        @Override
        protected List<ScanIstItemProductBean> doInBackground(String... params) {
            if (params.length > 0) {
                String content = params[0];
                ws_result = WebServiceUtil.Get_Item_List_By_Scan_Ist_Subist_Product(content);
                if (ws_result.getResult()) {
                    //添加库位与manuLot的关联
//                    addIstSubIstManuLotRelation(boxItemEntity);
//                    boxItemEntityList.remove(boxItemEntity);
//                    SelectList.remove(boxItemEntity);
                    String jsonData = ws_result.getErrorInfo();
                    Gson gson = new Gson();
                    List<ScanIstItemProductBean> scanISTItemBeanList = gson.fromJson(jsonData, new TypeToken<List<ScanIstItemProductBean>>() {
                    }.getType());
                    return scanISTItemBeanList;
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(List<ScanIstItemProductBean> result) {
            //tv.setText(fahren + "∞ F");

            if (ws_result != null) {
                if (!ws_result.getResult()) {
                    //Toast.makeText(StockInActivity.this,ws_result.getErrorInfo(),Toast.LENGTH_LONG).show();
                    CommonUtil.ShowToast(ScanIstFindProductActivity.this, ws_result.getErrorInfo(), R.mipmap.warning);

                } else {
                    //Toast.makeText(StockInActivity.this,"入库完成",Toast.LENGTH_LONG).show();
//                    CommonUtil.ShowToast(ScanIstFindProductActivity.this, "入库完成", R.mipmap.smiley);
                    adapter.setData(result);
                }

            }
            inputEditText.setText("");
        }

    }
}

