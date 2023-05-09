package com.chinashb.www.mobileerp.warehouse;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.InBoxItemAdapter;
import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.PackDepackBoxBean;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.JsonUtil;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2023/4/12 9:58 AM
 * @author 作者: liweifeng
 * @description 零件包装拆解或增加界面，先处理拆解
 */
public class PartPackPackageActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.pack_scan_top_middle_box_Button) Button scanTopMiddleBoxButton;
    @BindView(R.id.pack_confirm_button) Button confirmButton;
    @BindView(R.id.pack_input_EditText) EditText inputEditText;
    @BindView(R.id.pack_box_recyclerView) RecyclerView recyclerView;
    @BindView(R.id.pack_scan_little_middle_box_button) Button scanLittleMiddleBoxButton;
    @BindView(R.id.pack_top_middle_box_info_TextView) TextView topMiddleBoxInfoTextView;
    @BindView(R.id.pack_middle_smli_box_info_TextView) TextView middleSmliBoxInfoTextView;

    private String scanContent;
    private InBoxItemAdapter boxItemAdapter;
    private List<BoxItemEntity> boxItemEntityList = new ArrayList<>();
    private ArrayList<String> scanCodeList = new ArrayList<>();
    private String scanCode = "";
    private boolean hasScanTopMiddle = false;
    private boolean isChildSMM = false;
    private List<Long> childBoxIDList = new ArrayList<>();
    private String outBoxID = "";
    private StringBuilder stringBuilder = new StringBuilder();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                ToastUtil.showToastLong("您当前公司与来料入库公司不符合，请确认来料是否入到该公司！");
            } else if (msg.what == 1) {
//                ToastUtil.showToastLong("建议仓库存放:" + boxItemEntity.getIstName());
                Bundle bundle = msg.getData();
                if (bundle != null) {
                    ToastUtil.showToastLong("建议仓库存放:" + bundle.getString("suggest_ist"));
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pack_package_layout);
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
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setViewsListener() {
        scanTopMiddleBoxButton.setOnClickListener(this);
        scanLittleMiddleBoxButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                if (editable.toString().length() > 7) {
                    System.out.println("========================扫描结果:" + editable.toString());
                    parseScanResult(editable.toString());
                }
            }
        });
    }

    private void showTopMiddleBoxInfo(PackDepackBoxBean boxItem) {
        topMiddleBoxInfoTextView.setText(String.format("箱号:%s,物料:%s,数量:%s,批次:%s,子箱数目:%s,子箱加载数目:%s,备注:%s",
                boxItem.getSMTID(),
                boxItem.getItemID(), boxItem.getIQty(), (boxItem.getManuLot() + "@" + boxItem.getBoxName()), boxItem.getChildCount(), boxItem.getChildLoaded(), boxItem.getRemark()));
    }

    private void parseScanResult(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        if (content.contains("\n")) {
            content = content.replace("\n", "");
        }
        if (content.contains("/") || content.contains("／")) {
            if (content.contains("／")) {
                content = content.replace("／", "/");
            }

            String[] qrContent;
            qrContent = content.split("/");
            if (qrContent.length >= 2) {
                String qrTitle = qrContent[0];
                String boxID = qrContent[1];
                if (!qrTitle.equals("")) {
                    if (qrTitle.equals("VE") || qrTitle.equals("VF") || qrTitle.equals("VG") || qrTitle.equals("V9") || qrTitle.equals("VA") || qrTitle.equals("VB") || qrTitle.equals("VC")) {
                        //物品条码
                        if (!hasScanTopMiddle) {
                            if (qrTitle.equals("VE") || qrTitle.equals("V9")) {
                                ToastUtil.showToastShort("请首先扫描待拆解的外箱或中箱！");
                                return;
                            }
                        } else {
                            if (qrTitle.equals("VG") || qrTitle.equals("VB")) {
                                ToastUtil.showToastShort("请扫描要拆解出的小箱或中箱！");
                                return;
                            }
                        }
                        scanContent = content;
                        scanCodeList.add(content);
                        scanCode = content;
                        if (!hasScanTopMiddle) {

                            String outSql = "";
                            if (qrTitle.equals("VG") || qrTitle.equals("VB")) {

                                outSql = "Select * from supplier_manu_top_box where smt_id = " + boxID;
                            } else if (qrTitle.equals("VA") || qrTitle.equals("VF")) {

                                outSql = "Select * from supplier_manu_middle_box where smm_id = " + boxID;
                            }
                            if (!TextUtils.isEmpty(outSql)) {
                                GetOutterBoxAsyncTask task = new GetOutterBoxAsyncTask();
                                task.execute(outSql);
                            }
                            outBoxID = boxID;
                        } else {
                            //开始处理子箱
                            if (qrTitle.equals("VA") || qrTitle.equals("VF")) {
                                isChildSMM = true;
                            }
                            stringBuilder.append("VE/" + boxID).append("\n");
                            middleSmliBoxInfoTextView.setText(stringBuilder.toString());
                            if (!childBoxIDList.contains(Long.parseLong(boxID))) {
                                childBoxIDList.add(Long.parseLong(boxID));
                            }
                        }

//                        GetBoxAsyncTask task = new GetBoxAsyncTask();
//                        task.execute();
                    }
                }

                if (content.startsWith("/SUB_IST_ID/") || content.startsWith("/IST_ID/") ||
                        content.startsWith("/SUB——IST——ID/") || content.startsWith("/IST——ID/")) {
                    if (content.startsWith("/SUB——IST——ID/")) {
                        content = content.replace("/SUB——IST——ID/", "/SUB_IST_ID/");
                    }

                    if (content.startsWith("/IST——ID/")) {
                        content = content.replace("/IST——ID/", "/IST_ID/");
                    }
                    //仓库位置码
                    scanContent = content;


                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view == scanTopMiddleBoxButton) {
            new IntentIntegrator(PartPackPackageActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();

        } else if (view == scanLittleMiddleBoxButton) {
            if (!hasScanTopMiddle) {
                ToastUtil.showToastShort("请先扫描待拆解的箱码！");
            } else {
                new IntentIntegrator(PartPackPackageActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
            }

        } else if (view == confirmButton) {
//            hasScanTopMiddle=
            if (!hasScanTopMiddle) {
                ToastUtil.showToastShort("您还未进行扫描！");
                return;
            }
            if (childBoxIDList.size() == 0) {
                ToastUtil.showToastShort("您未扫描要拆解出的子箱！");
                return;
            }
//            if (isChildSMM) {
//
//            } else {
//
//            }
            CommitPackAsyncTask task = new CommitPackAsyncTask();
            task.execute();

        }
    }

    private class GetBoxAsyncTask extends AsyncTask<String, Void, Void> {
        BoxItemEntity scanBoxItemEntity;

        @Override
        protected Void doInBackground(String... params) {
            BoxItemEntity boxItemEntity = WebServiceUtil.op_Check_Commit_DS_Item_Income_Barcode(scanContent);
            String s = JsonUtil.objectToJson(boxItemEntity);

//            //// TODO: 2020/1/9 这里先处理，为避免因供应商选错，而导致入错账的问题
//            if (boxItemEntity != null) {
//                //如果来料里设置的公司与该操作员的公司不符
//                if (boxItemEntity.getBu_ID() != UserSingleton.get().getUserInfo().getBu_ID()) {
////                    ToastUtil.showToastLong("您当前公司与来料入库公司不符合，请确认来料是否入到该公司！");
//                    Message message = new Message();
//                    message.what = 0;
//                    handler.sendMessage(message);
//                    return null;
//                }
//            }

            scanBoxItemEntity = boxItemEntity;
            if (boxItemEntity.getResult()) {
                //// TODO: 2020/1/9 这里先处理，为避免因供应商选错，而导致入错账的问题
//                if (boxItemEntity != null) {
                //如果来料里设置的公司与该操作员的公司不符
                //再加一个判断 boxItemEntity.getBu_ID()==0 表示解析出错
                //// TODO: 2020/4/17 这里应该是companyID
//                if (boxItemEntity.getBu_ID() != 0 && boxItemEntity.getBu_ID() != UserSingleton.get().getUserInfo().getBu_ID()) {
////                    ToastUtil.showToastLong("您当前公司与来料入库公司不符合，请确认来料是否入到该公司！");
//                    Message message = new Message();
//                    message.what = 0;
//                    handler.sendMessage(message);
//                    return null;
//                }


                if (boxItemEntity.getCompany_ID() != 0 && boxItemEntity.getCompany_ID() != UserSingleton.get().getUserInfo().getCompany_ID()) {
//                    ToastUtil.showToastLong("您当前公司与来料入库公司不符合，请确认来料是否入到该公司！");
                    Message message = new Message();
                    message.what = 0;
                    handler.sendMessage(message);
                    return null;
                }
//                }


                if (hasScanTopMiddle) {

                    if (!is_box_existed(boxItemEntity)) {
                        boxItemEntity.setSelect(true);
                        boxItemEntityList.add(boxItemEntity);
                    } else {
                        boxItemEntity.setResult(false);
                        boxItemEntity.setErrorInfo("该包装已经在装载列表中");
                    }
                }


            } else {

            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            //pbScan.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");
            if (scanBoxItemEntity != null) {
                if (!scanBoxItemEntity.getResult()) {
                    Toast.makeText(PartPackPackageActivity.this, scanBoxItemEntity.getErrorInfo(), Toast.LENGTH_LONG).show();
                    hasScanTopMiddle = false;
                    return;
                }
            }

            if (!hasScanTopMiddle) {
//                showTopMiddleBoxInfo(scanBoxItemEntity);
            } else {

                boxItemAdapter = new InBoxItemAdapter(PartPackPackageActivity.this, boxItemEntityList);
                recyclerView.setAdapter(boxItemAdapter);
            }

            inputEditText.setText("");
            inputEditText.setHint("请继续使用扫描枪");
            hasScanTopMiddle = true;
//            if (inputDialog != null && inputDialog.isShowing()) {
//                inputDialog.dismiss();
//            }
            //pbScan.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

        protected Boolean is_box_existed(BoxItemEntity box_item) {
            Boolean result = false;
            if (boxItemEntityList != null) {
                for (int i = 0; i < boxItemEntityList.size(); i++) {
                    if (boxItemEntityList.get(i).getDIII_ID() == box_item.getDIII_ID()) {
                        return true;
                    }
                }
            }

            return result;
        }

    }


    private class GetOutterBoxAsyncTask extends AsyncTask<String, Void, PackDepackBoxBean> {

        @Override
        protected PackDepackBoxBean doInBackground(String... params) {
            if (params != null && params.length > 0) {

                String sql = params[0];
                WsResult result = WebServiceUtil.getDataTable(sql);
                if (result != null && result.getResult()) {
                    String jsonData = result.getErrorInfo();
                    Gson gson = new Gson();
                    List<PackDepackBoxBean> boxItemEntityList = gson.fromJson(jsonData, new TypeToken<List<PackDepackBoxBean>>() {
                    }.getType());
                    return boxItemEntityList.get(0);
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(PackDepackBoxBean result) {
            if (result != null) {
                showTopMiddleBoxInfo(result);
                hasScanTopMiddle = true;
            } else {
                ToastUtil.showToastShort("解析失败！");
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    private class CommitPackAsyncTask extends AsyncTask<String, Void, Void> {
//        private WsResult wsResult = null;
        private boolean result;

        @Override
        protected Void doInBackground(String... params) {
//            if (params != null && params.length > 0) {
//
//                String SMT_ID = params[0];
            result = WebServiceUtil.op_TopBox_Depack(outBoxID, childBoxIDList, isChildSMM);

//            }

            return null;
        }


        @Override
        protected void onPostExecute(Void a) {
            if (result) {
                isChildSMM = false;
                hasScanTopMiddle = false;
                childBoxIDList = new ArrayList<>();
                outBoxID = "";
                stringBuilder = new StringBuilder();
                ToastUtil.showToastLong("包装拆解成功！");
            } else {
                ToastUtil.showToastLong("包装拆解失败！");
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

}
