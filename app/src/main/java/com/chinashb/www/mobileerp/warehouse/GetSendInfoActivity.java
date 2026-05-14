package com.chinashb.www.mobileerp.warehouse;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.CommonSingleTextViewAdapter;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 12/28/24 10:04 AM
 * @author 作者: liweifeng
 * @description 扫描标签获取相关发货及入库信息
 */
public class GetSendInfoActivity extends BaseActivity {


    @BindView(R.id.send_info_input_EditText) EditText inputEditText;
    @BindView(R.id.send_info_scan_button) Button scanButton;
    @BindView(R.id.send_info_recyclerView) CustomRecyclerView recyclerView;
    private CommonSingleTextViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_send_info_layout);
        ButterKnife.bind(this);
        scanButton.setOnClickListener(v -> {
            new IntentIntegrator(GetSendInfoActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
        });

        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
protected void onTextChangedSafe(CharSequence text) {
                                if (text.toString().length() > 7) {
                    parseScanResult(text.toString());
                }
            }
        });

        adapter = new CommonSingleTextViewAdapter();
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
            if (qrContent.length > 1) {
                String qrTitle = qrContent[0];
                if (!qrTitle.equals("")) {
                    if (qrTitle.equals("VE") || qrTitle.equals("VF") || qrTitle.equals("VG") || qrTitle.equals("V9") || qrTitle.equals("VA") || qrTitle.equals("VB") || qrTitle.equals("VC")) {
                        //物品条码
//                        GetBoxAllInfoAsyncTask task = new GetBoxAllInfoAsyncTask();
//                        task.execute(content);
                    }else{
                        ToastUtil.showToastShort("标签格式错误！");
                    }
                }
            }
        }else{
            ToastUtil.showToastShort("标签格式错误！");

        }
    }

//    private class GetBoxAllInfoAsyncTask extends AsyncTask<String, String, String> {
//        private WsResult wsResult;
//
//        @Override
//        protected String doInBackground(String... strings) {
//            wsResult = WebServiceUtil.getBoxPartAllInfo("");
//            if (wsResult != null && wsResult.getResult()) {
//                Type type = new TypeToken<List<BoxPartInfoBean>>() {
//                }.getType();
//                List<BoxPartInfoBean> beanList = JsonUtil.parseJsonToObject(wsResult.getErrorInfo(), type);
//                return beanList;
////            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String jsonData) {
//            super.onPostExecute(jsonData);
////            Gson gson = new Gson();
////            List<FreezeRecordBean> freezeRecordBeanList = gson.fromJson(jsonData, new TypeToken<List<FreezeRecordBean>>() {
////            }.getType());
////            if (freezeRecordBeanList != null && freezeRecordBeanList.size() > 0) {
////                adapter.setData(freezeRecordBeanList);
////            }
//
//        }
//    }
}

