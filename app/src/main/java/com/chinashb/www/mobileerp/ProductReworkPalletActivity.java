package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;

import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2024/7/9 3:58 PM
 * @author 作者: liweifeng
 * @description 手机操作整托返工
 */
public class ProductReworkPalletActivity extends BaseActivity {

    @BindView(R.id.btn_rework_pallet_scan) Button scanReworkButton;
    @BindView(R.id.rework_pallet_not_input_EditText) EditText inputEditText;

    private int boxId = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_rework_layout);
        ButterKnife.bind(this);

        setViewsListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
            } else {
                parseScanResult(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setViewsListener() {
        scanReworkButton.setOnClickListener(v -> {
            new IntentIntegrator(ProductReworkPalletActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();

        });

        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                if (editable.toString().length() > 0) {
                    parseScanResult(editable.toString());
                }
            }
        });
    }

    private void parseScanResult(String content) {
        if (content.contains("/")) {
            String[] qrContent;
            qrContent = content.split("/");
            if (qrContent.length >= 2) {

                if (content.startsWith("Pallet") && qrContent.length == 8) {
                   boxId = Integer.parseInt(qrContent[1]);
                   if (boxId > 0){
                       ReworkProductPalletByBoxIDAsyncTask task = new ReworkProductPalletByBoxIDAsyncTask();
                       task.execute();
                   }
                }
            }
        }
    }

    private class ReworkProductPalletByBoxIDAsyncTask extends AsyncTask<Void, Void, Void> {
        WsResult wsResult;
        @Override
        protected Void doInBackground(Void... voids) {
            wsResult = WebServiceUtil.reworkWholeProductPallet(boxId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (wsResult != null && wsResult.getResult()){
                ToastUtil.showToastShort("返工操作成功！");
            }else{
                ToastUtil.showToastShort("返工操作失败，原因:" + wsResult.getErrorInfo());

            }

            inputEditText.setText("");






        }
    }
}
