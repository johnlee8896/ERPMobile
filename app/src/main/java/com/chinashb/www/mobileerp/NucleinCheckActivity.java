package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2022/5/10 9:48 AM
 * @author 作者: liweifeng
 * @description 核酸检测登记查询
 */
public class NucleinCheckActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.scan_button) Button scanButton;
    @BindView(R.id.nuclein_input_EditText) EditText inputEditText;
    @BindView(R.id.check_unrigister_button) Button checkButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuclein_layout);
        ButterKnife.bind(this);
        setViewsListener();
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

    private void parseScanResult(String contents) {
        if (contents != null && contents.length() > 10 && contents.startsWith("/")) {
            String[] splits = contents.split("/");
            if (splits.length == 5) {
                String hrId = splits[2];
                String hrNO = splits[4];
                verifyCorrect(hrId, hrNO);
            } else {
                ToastUtil.showToastShort("识别失败，格式错误！");
            }
        } else {
            ToastUtil.showToastShort("扫描识别失败，格式错误！");
        }
    }

    private void verifyCorrect(String hrId, String hrNO) {
        VerifyHRTask task = new VerifyHRTask();
        task.execute(hrId, hrNO);

    }

    private void setViewsListener() {
        scanButton.setOnClickListener(this);
        checkButton.setOnClickListener(this);

        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                if (editable.toString().length() > 5) {
                    parseScanResult(editable.toString());
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == scanButton) {
            new IntentIntegrator(NucleinCheckActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();

        } else if (v == checkButton) {

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private class VerifyHRTask extends AsyncTask<String, Void, Void> {
        private WsResult ws_result = null;

        @Override
        protected Void doInBackground(String... strings) {
            String hrId = strings[0];
            String hrNO = strings[1];
            ws_result = WebServiceUtil.verifyHRAndRegisterNuclein(Integer.parseInt(hrId), hrNO);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (ws_result != null) {
                if (!ws_result.getResult()) {
                    //Toast.makeText(StockInActivity.this,ws_result.getErrorInfo(),Toast.LENGTH_LONG).show();
                    CommonUtil.ShowToast(NucleinCheckActivity.this, ws_result.getErrorInfo(), R.mipmap.warning);

                } else {
                    //Toast.makeText(StockInActivity.this,"入库完成",Toast.LENGTH_LONG).show();
                    CommonUtil.ShowToast(NucleinCheckActivity.this, ws_result.getErrorInfo(), R.mipmap.smiley);
                }

                inputEditText.setText("");

            }
        }
    }
}
