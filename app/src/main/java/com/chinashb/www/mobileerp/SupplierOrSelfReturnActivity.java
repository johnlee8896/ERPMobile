package com.chinashb.www.mobileerp;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chinashb.www.mobileerp.adapter.CommonItemBarCodeAdapter;
import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CommAlertDialog;
import com.chinashb.www.mobileerp.widget.CommonSelectInputDialog;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.chinashb.www.mobileerp.widget.OnDialogViewClickListener;
import com.chinashb.www.mobileerp.widget.TitleLayoutManagerView;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2020/5/13 14:56
 * @author 作者: xxblwf
 * @description 供应商退货, 及自制车间退货
 */

public class SupplierOrSelfReturnActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.supplier_return_scan_camera_button) Button scanCameraButton;
    @BindView(R.id.supplier_return_input_EditText) EditText inputEditText;
    @BindView(R.id.supplier_return_confirm_button) Button confirmButton;
    @BindView(R.id.supplier_return_recyclerView) CustomRecyclerView recyclerView;
    @BindView(R.id.supplier_return_remark_button) Button remarkButton;
    @BindView(R.id.supplier_return_remark_TextView) TextView remarkTextView;
    @BindView(R.id.supplier_return_titleLayoutView) TitleLayoutManagerView titleLayoutView;

    private CommonItemBarCodeAdapter adapter;
    private List<BoxItemEntity> boxItemEntityArrayList = new ArrayList<>();
    private CommonSelectInputDialog remarkDialog;
    private String remark;
    private String scanContent;
    private BoxItemEntity tempBoxItemEntity;
    private boolean currentSelfProductReturn = false;//默认是供应商退货，目前只有供应商和自制车间

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_return_layout);
        ButterKnife.bind(this);


        currentSelfProductReturn = getIntent().getBooleanExtra(IntentConstant.Intent_Extra_supplier_or_self_return_boolean, false);
        if (currentSelfProductReturn) {
            titleLayoutView.setTitle("自制车间退货");
        }


        scanCameraButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        remarkButton.setOnClickListener(this);

        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
//                if (editable.toString().length() > 7 && editable.toString().endsWith("\n")) {
                if (editable.toString().length() > 7 ) {
//                    ToastUtil.showToastLong("扫描结果:" + editable.toString());
                    System.out.println("========================扫描结果:" + editable.toString());
                    parseContent(editable.toString());
                }
            }
        });
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (!TextUtils.isEmpty(result.getContents())) {
                parseContent(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void parseContent(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        System.out.println("============ scan content = " + content);
        // V5/B/54/PS/10475/L/191217/LQ/0/Qty/120
        if (content.contains("/")) {
            String[] qrContent;
            qrContent = content.split("/");
            if (qrContent.length >= 2) {
                String qrTitle = qrContent[0];
                if (!qrTitle.equals("")) {
                    if (qrTitle.equals("VE") || qrTitle.equals("VF") || qrTitle.equals("VG") || qrTitle.equals("V9") || qrTitle.equals("VA") || qrTitle.equals("VB") || qrTitle.equals("VC")) {
                        //物品条码
                        scanContent = content;
                        GetItemQRCodeAsyncTask task = new GetItemQRCodeAsyncTask();
                        task.execute();
                    }
                }
            }
        }
    }

    private void handleIntoWareHouse() {

        if (boxItemEntityArrayList.size() > 0) {
            if (UserSingleton.get().getHRID() > 0 && !TextUtils.isEmpty(UserSingleton.get().getHRName())) {
                GetEntityAsyncTask task = new GetEntityAsyncTask();
                task.execute(boxItemEntityArrayList.get(0).getLotID() + "");

            } else {
                CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(SupplierOrSelfReturnActivity.this)
                        .setTitle("").setMessage("您当前程序账号有误，需重新登录！")
                        .setLeftText("确定");


                builder.setOnViewClickListener(new OnDialogViewClickListener() {
                    @Override
                    public void onViewClick(Dialog dialog, View v, int tag) {
                        switch (tag) {
                            case CommAlertDialog.TAG_CLICK_LEFT:
                                CommonUtil.doLogout(SupplierOrSelfReturnActivity.this);
                                dialog.dismiss();
                                break;
                        }
                    }
                });
                builder.create().show();
            }

        }
    }

    private OnViewClickListener remarkOnViewClickListener = new OnViewClickListener() {
        @Override public <T> void onClickAction(View v, String tag, T t) {
            if (t != null) {
                remark = (String) t;
                remarkTextView.setText((CharSequence) t);
            }
            if (remarkDialog != null && remarkDialog.isShowing()) {
                remarkDialog.dismiss();
            }
        }
    };

    @Override public void onClick(View v) {
        if (v == scanCameraButton) {
            new IntentIntegrator(this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
        } else if (v == confirmButton) {
            handleIntoWareHouse();
        } else if (v == remarkButton) {
            if (remarkDialog == null) {
                remarkDialog = new CommonSelectInputDialog(SupplierOrSelfReturnActivity.this);
            }
            remarkDialog.show();
            remarkDialog.setOnViewClickListener(remarkOnViewClickListener);
        }
    }

    private class GetItemQRCodeAsyncTask extends AsyncTask<String, Void, BoxItemEntity> {
        @Override
        protected BoxItemEntity doInBackground(String... params) {
            BoxItemEntity boxItemEntity;
            boxItemEntity = WebServiceUtil.op_Check_Work_Line_Scan_Item_Barcode(scanContent);
            return boxItemEntity;
        }

        @Override
        protected void onPostExecute(BoxItemEntity boxItemEntity) {
            if (boxItemEntity.getResult()) {
                if (!is_box_existed(boxItemEntity)) {
                    boxItemEntity.setSelect(true);
                    String nullType = "anyType{}";
                    if (TextUtils.isEmpty(boxItemEntity.getIstName()) || boxItemEntity.getIstName().contains(nullType)) {
                        boxItemEntity.setIstName("");
                    }

                    //// TODO: 2020/3/9
                    if (TextUtils.isEmpty(boxItemEntity.getBuName()) || boxItemEntity.getBuName().contains(nullType)) {
                        boxItemEntity.setBuName(UserSingleton.get().getUserInfo().getBu_Name());
                    }
                    tempBoxItemEntity = boxItemEntity;
                    JudgeIsSupplierAsyncTask task = new JudgeIsSupplierAsyncTask();
                    task.execute(boxItemEntity.getLotID() + "");


                } else {
                    boxItemEntity.setResult(false);
                    boxItemEntity.setErrorInfo("该包装已经在装载列表中");
                }
            }

            if (boxItemEntity != null) {
                if (!boxItemEntity.getResult()) {
                    ToastUtil.showToastShort(boxItemEntity.getErrorInfo());
                }
            }

//            recyclerView.setAdapter(adapter);

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    private boolean is_box_existed(BoxItemEntity box_item) {
        Boolean result = false;

        if (boxItemEntityArrayList != null) {
            for (int i = 0; i < boxItemEntityArrayList.size(); i++) {
                if (boxItemEntityArrayList.get(i).getSMLI_ID() == box_item.getSMLI_ID() && box_item.getSMLI_ID() > 0) {
                    return true;
                }
                if (boxItemEntityArrayList.get(i).getSMM_ID() == box_item.getSMM_ID() && box_item.getSMM_ID() > 0) {
                    return true;
                }
                if (boxItemEntityArrayList.get(i).getSMT_ID() == box_item.getSMT_ID() && box_item.getSMT_ID() > 0) {
                    return true;
                }
                if (boxItemEntityArrayList.get(i).getSMT_ID() == box_item.getSMT_ID() && box_item.getSMT_ID() == 0
                        && boxItemEntityArrayList.get(i).getSMM_ID() == box_item.getSMM_ID() && box_item.getSMM_ID() == 0
                        && boxItemEntityArrayList.get(i).getSMLI_ID() == box_item.getSMLI_ID() && box_item.getSMLI_ID() == 0
                        && boxItemEntityArrayList.get(i).getLotID() == box_item.getLotID()) {
                    return true;
                }


            }
        }

        return result;
    }

    private class JudgeIsSupplierAsyncTask extends AsyncTask<String, Void, Void> {
        WsResult ws_result;
        int wssCase = 0;

        @Override
        protected Void doInBackground(String... params) {
            String lotID = params[0];
            //// TODO: 2019/12/20  注意pc是取top 100,这里取的是全部
            String sql = String.format("select top 1 wss_case from supplier_manu_lot inner join lot on lot.Source_ID = Supplier_Manu_Lot .Supplier_ID where lot.lotid = %s", lotID);
            WsResult result = WebServiceUtil.getDataTable(sql);
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
//                Gson gson = new Gson();
//                workLineItemEntityList = gson.fromJson(jsonData, new TypeToken<>() {
//                }.getType());
                if (jsonData.contains("wss_case")) {
                    String[] items = jsonData.trim().split("wss_case");
                    if (items != null && items.length > 0) {
                        String wssCaseString = items[1].trim().replace(":", "");
                        if (!TextUtils.isEmpty(wssCaseString)) {
                            try {
                                wssCase = Integer.parseInt(wssCaseString);
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            boxItemEntityArrayList.add(tempBoxItemEntity);
            adapter.setData(boxItemEntityArrayList);
            inputEditText.setText("");
            inputEditText.setHint("请继续扫描");
            if (currentSelfProductReturn) {
                if (wssCase == 8 || wssCase == 11) {
                    //采购入库，供应商
                } else {
                    ToastUtil.showToastShort("当前物料不属于供应商，不能执行供应商退货！");
                }
            } else {
                if (wssCase == 1 || wssCase == 4) {
//                    //采购入库，供应商
//                    boxItemEntityArrayList.add(tempBoxItemEntity);
//                    adapter.setData(boxItemEntityArrayList);
//                    inputEditText.setText("");
//                    inputEditText.setHint("请继续扫描");
                } else {
                    ToastUtil.showToastShort("当前物料不属于供应商，不能执行供应商退货！");
                }
            }
        }

    }

    private class GetEntityAsyncTask extends AsyncTask<String, Void, LotSourceNameIDBean> {
        @Override
        protected LotSourceNameIDBean doInBackground(String... params) {
            LotSourceNameIDBean lotSourceNameIDBean = null;
            //// TODO: 2019/12/20  注意pc是取top 100,这里取的是全部
            String sql = String.format("select Source_ID,Source_Name from lot where LotID = %s", params[0]);
            WsResult result = WebServiceUtil.getDataTable(sql);
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                Gson gson = new Gson();
                lotSourceNameIDBean = gson.fromJson(jsonData, LotSourceNameIDBean.class);
            }

            return lotSourceNameIDBean;
        }

        @Override protected void onPostExecute(LotSourceNameIDBean lotSourceNameIDBean) {
//            super.onPostExecute(lotSourceNameIDBean);
            if (lotSourceNameIDBean != null) {
                SupplierReturnAsyncTask task = new SupplierReturnAsyncTask();
                task.execute(lotSourceNameIDBean.getSourceName(), lotSourceNameIDBean.getSourceID() + "");
            }
        }
    }

    private class SupplierReturnAsyncTask extends AsyncTask<String, Void, Void> {
        WsResult ws_result;

        @Override
        protected Void doInBackground(String... params) {
            String entityName = params[0];
            String entityId = params[1];
            int entityID = 0;
            try {
                if (!TextUtils.isEmpty(entityId)) {
                    entityID = Integer.parseInt(entityId);
                }
            } catch (Exception e) {
            }
            int count = 0;
            int newissuesize = boxItemEntityArrayList.size();
            while (count < newissuesize && boxItemEntityArrayList.size() > 0) {
                BoxItemEntity boxItemEntity = boxItemEntityArrayList.get(0);
                //// TODO: 2020/5/14 这里的 boxItemEntity.getEntityName() 科目 应是从lot表中读取，sourceName 和sourceID
                ws_result = WebServiceUtil.op_Commit_Work_line_Item_Non_Plan(boxItemEntity.getItem_ID(), boxItemEntity.getIV_ID(), boxItemEntity.getLotID(),
                        boxItemEntity.getLotNo(), boxItemEntity.getIst_ID(), boxItemEntity.getSub_Ist_ID(), boxItemEntity.getSMLI_ID(), boxItemEntity.getSMM_ID(), boxItemEntity.getSMT_ID(),
                        String.valueOf(boxItemEntity.getQty()), entityName, "", remark, entityID);

                count++;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            remark = "";
            if (ws_result != null) {
                if (!ws_result.getResult()) {
                    ToastUtil.showToastShort(ws_result.getErrorInfo());
                } else {
                    ToastUtil.showToastShort("成功退货！");
                    boxItemEntityArrayList.clear();
                    adapter.notifyDataSetChanged();
                }
            }
        }

    }

    /**
     * 根据lot表中获取该lotID对应的sourceID，和sourceName其实是供应商的id和name
     */
    private class LotSourceNameIDBean {
        @SerializedName("Source_ID") private int sourceID;
        @SerializedName("Source_Name") private String sourceName;

        public int getSourceID() {
            return sourceID;
        }

        public void setSourceID(int sourceID) {
            this.sourceID = sourceID;
        }

        public String getSourceName() {
            return sourceName;
        }

        public void setSourceName(String sourceName) {
            this.sourceName = sourceName;
        }
    }
}
