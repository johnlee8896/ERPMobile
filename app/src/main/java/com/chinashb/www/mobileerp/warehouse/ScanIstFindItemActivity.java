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
import com.chinashb.www.mobileerp.adapter.ScanIstItemAdapter;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.ScanISTItemPartBean;
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
 * @date 创建时间 2023/7/4 10:28 AM
 * @author 作者: liweifeng
 * @description
 */
public class ScanIstFindItemActivity extends BaseActivity {
    @BindView(R.id.scan_ist_input_EditText) EditText inputEditText;
    @BindView(R.id.scan_ist_scan_button) Button scanButton;
    @BindView(R.id.scan_ist_recyclerView) CustomRecyclerView recyclerView;

    private ScanIstItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_ist_item_layout);
        ButterKnife.bind(this);
        scanButton.setOnClickListener(v -> {
            new IntentIntegrator(ScanIstFindItemActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();

        });

        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                if (editable.toString().length() > 7 ) {
                    parseScanResult(editable.toString());
                }
            }
        });

        adapter = new ScanIstItemAdapter();
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

                    GetItemListByISTAsyncTask task = new GetItemListByISTAsyncTask();
                    task.execute(content);

                }
            }
        }
    }

    @SuppressLint("NewApi")
    private class GetItemListByISTAsyncTask extends AsyncTask<String, Void, List<ScanISTItemPartBean>> {
        WsResult ws_result;

        @Override
        protected List<ScanISTItemPartBean> doInBackground(String... params) {
            if (params.length > 0){
                String content = params[0];
                ws_result = WebServiceUtil.Get_Item_List_By_Scan_Ist_Subist(content);
                if (ws_result.getResult()) {
                    //添加库位与manuLot的关联
//                    addIstSubIstManuLotRelation(boxItemEntity);
//                    boxItemEntityList.remove(boxItemEntity);
//                    SelectList.remove(boxItemEntity);
                    String jsonData = ws_result.getErrorInfo();
                    Gson gson = new Gson();
                    List<ScanISTItemPartBean>  scanISTItemBeanList = gson.fromJson(jsonData, new TypeToken<List<ScanISTItemPartBean>>() {
                    }.getType());
                    return scanISTItemBeanList;
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(List<ScanISTItemPartBean> result) {
            //tv.setText(fahren + "∞ F");

            if (ws_result != null) {
                if (!ws_result.getResult()) {
                    //Toast.makeText(StockInActivity.this,ws_result.getErrorInfo(),Toast.LENGTH_LONG).show();
                    CommonUtil.ShowToast(ScanIstFindItemActivity.this, ws_result.getErrorInfo(), R.mipmap.warning);

                } else {
                    //Toast.makeText(StockInActivity.this,"入库完成",Toast.LENGTH_LONG).show();
//                    CommonUtil.ShowToast(ScanIstFindItemActivity.this, "入库完成", R.mipmap.smiley);
                    adapter.setData(result);
                }

            }
        }

    }
}
