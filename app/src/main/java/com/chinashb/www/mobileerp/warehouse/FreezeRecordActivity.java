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
import com.chinashb.www.mobileerp.adapter.CommonSingleTextViewAdapter;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.FreezeRecordBean;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 11/15/24 8:59 PM
 * @author 作者: liweifeng
 * @description 查询冻结解冻记录
 */
public class FreezeRecordActivity extends BaseActivity {


    @BindView(R.id.freeze_record_input_EditText) EditText inputEditText;
    @BindView(R.id.freeze_record_scan_button) Button scanButton;
    @BindView(R.id.freeze_record_recyclerView) CustomRecyclerView recyclerView;
    private CommonSingleTextViewAdapter adapter;
    private boolean currentTopBox;//冻结只有大箱和小箱
//    private long currentBoxID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freeze_record_layout);
        ButterKnife.bind(this);
        scanButton.setOnClickListener(v -> {
            new IntentIntegrator(FreezeRecordActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
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
        content = content.replaceAll("\n", "");
        String boxID = "";
        if (content.contains("/") || content.contains("／")) {
            if (content.contains("／")) {
                content = content.replace("／", "/");
            }

            String[] qrContent;
            qrContent = content.split("/");
            if (qrContent.length == 2) {
                String qrTitle = qrContent[0];
                if (!qrTitle.equals("")) {
                    if (qrTitle.equals("VG") || qrTitle.equals("VC")) {
                        //物品条码
                        boxID = qrContent[1];
                        currentTopBox = true;
                    } else if (qrTitle.equals("VE")) {
                        //物品条码
                        boxID = qrContent[1];
                        currentTopBox = false;
                    } else {
                        ToastUtil.showToastShort("格式不符，请在输入框中输入VG或VE开头的箱码，点回车结束！");
                    }
                }
                GetFreeRecordAsyncTask task = new GetFreeRecordAsyncTask();
                task.execute(boxID);
            }
        }
    }

    private class GetFreeRecordAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            if (strings.length > 0) {
                String boxid = strings[0];
                String sql;
                if (currentTopBox) {
                    sql = " select Freeze_UnFreeze_Record.*,HR.HR_Name " +
                            "from Freeze_UnFreeze_Record Inner Join HR on HR.HR_ID = Freeze_UnFreeze_Record.HR_ID " +
                            "where Freeze_UnFreeze_Record.SMT_ID = " + boxid;
                } else {
                    sql = " select Freeze_UnFreeze_Record.*,HR.HR_Name " +
                            "from Freeze_UnFreeze_Record Inner Join HR on HR.HR_ID = Freeze_UnFreeze_Record.HR_ID " +
                            "where Freeze_UnFreeze_Record.SMLI_ID = " + boxid;
                }
                WsResult result = WebServiceUtil.getDataTable(sql);
                System.out.println("result = " + result);
                if (result != null && result.getResult()) {
                    String jsonData = result.getErrorInfo();
                    if (!TextUtils.isEmpty(jsonData)) {
                        return jsonData;

                    }
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(String jsonData) {
            super.onPostExecute(jsonData);
            Gson gson = new Gson();
            List<FreezeRecordBean> freezeRecordBeanList = gson.fromJson(jsonData, new TypeToken<List<FreezeRecordBean>>() {
            }.getType());
            if (freezeRecordBeanList != null && freezeRecordBeanList.size() > 0) {
                adapter.setData(freezeRecordBeanList);
            }

        }
    }
}
