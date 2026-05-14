package com.chinashb.www.mobileerp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.chinashb.www.mobileerp.adapter.ProductMoveRecordAdapter;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.ProductBoxMoveRecordBean;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.utils.JsonUtil;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CommProgressDialog;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.chinashb.www.mobileerp.widget.EmptyLayoutManageView;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 8/20/24 10:17 AM
 * @author 作者: liweifeng
 * @description 成品移库记录查询
 */
public class ProductMoveRecordActivity extends BaseActivity {


    @BindView(R.id.product_move_record_input_EditText) EditText inputEditText;
    @BindView(R.id.product_move_record_scan_button) Button scanButton;
    @BindView(R.id.product_move_record_recyclerView) CustomRecyclerView recyclerView;
    @BindView(R.id.product_move_record_emptyManager) EmptyLayoutManageView emptyManager;
    private ProductMoveRecordAdapter adapter;

    private CommProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_move_record_layout);
        ButterKnife.bind(this);
        scanButton.setOnClickListener(v -> {
            new IntentIntegrator(ProductMoveRecordActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
        });

        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
protected void onTextChangedSafe(CharSequence text) {
                                if (text.toString().length() > 7) {
                    parseScanResult(text.toString());
                }
            }
        });

        adapter = new ProductMoveRecordAdapter();
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
        if (content.contains("/")) {
            String[] qrContent;
            qrContent = content.split("/");
            if (qrContent.length >= 2) {

                if (content.startsWith("Pallet") && qrContent.length == 8) {
                    int boxId = Integer.parseInt(qrContent[1]);
                    inputEditText.setText("");
                    GetProductMoveRecordAsyncTask task = new GetProductMoveRecordAsyncTask();
                    task.execute(boxId);
                }
            }

        }

    }

    private class GetProductMoveRecordAsyncTask extends AsyncTask<Integer, Void, List<ProductBoxMoveRecordBean>> {
        WsResult wsResult;

        @Override
        protected List<ProductBoxMoveRecordBean> doInBackground(Integer... params) {
            int boxID = 0;
            if (params.length > 0) {
                boxID = params[0];
            }
            wsResult = WebServiceUtil.getProductMoveRecord(boxID);
            if (wsResult != null && wsResult.getResult()) {
                Type type = new TypeToken<List<ProductBoxMoveRecordBean>>() {
                }.getType();
                List<ProductBoxMoveRecordBean> beanList = JsonUtil.parseJsonToObject(wsResult.getErrorInfo(), type);
                return beanList;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            if (progressDialog == null) {
                progressDialog = new CommProgressDialog.Builder(ProductMoveRecordActivity.this)
                        .setTitle("正在加载").create();
            }
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(List<ProductBoxMoveRecordBean> resultList) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (wsResult == null) {
                ToastUtil.showToastShort("获取拣货任务失败！");
            } else {
                if (!wsResult.getResult()) {
                    ToastUtil.showToastShort("错误:原因" + wsResult.getErrorInfo());
                }
            }
            //tv.setText(fahren + "∞ F");
            if (resultList == null || resultList.size() == 0) {
                recyclerView.setVisibility(View.GONE);
                emptyManager.setVisibility(View.VISIBLE);
                return;
            }else{
                adapter.setData(resultList);
                emptyManager.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

        }


    }
}


