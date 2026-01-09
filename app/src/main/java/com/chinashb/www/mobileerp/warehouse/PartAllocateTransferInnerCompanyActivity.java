package com.chinashb.www.mobileerp.warehouse;

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

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.CommonItemBarCodeAdapter;
import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.InnerSelectBuBean;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CommAlertDialog;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.chinashb.www.mobileerp.widget.OnDialogViewClickListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2023/7/21 9:59 AM
 * @author 作者: liweifeng
 * @description 零件同公司调拨
 */
public class PartAllocateTransferInnerCompanyActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.part_allocate_select_bu_button) Button selectBuButton;
    @BindView(R.id.part_allocate_bu_name_textView) TextView buNameTextView;
    @BindView(R.id.part_allocate_scan_button) Button scanButton;
    @BindView(R.id.part_allocate_input_EditText) EditText inputEditText;
    @BindView(R.id.part_allocate_remark_button) Button remarkButton;
    @BindView(R.id.part_allocate_remark_TextView) TextView remarkTextView;
    @BindView(R.id.part_allocate_warehouse_in_button) Button outWarehouseInButton;
    @BindView(R.id.part_allocate_recyclerView) CustomRecyclerView recyclerView;

    private CommonItemBarCodeAdapter adapter;
    private String scanContent;
    private List<BoxItemEntity> boxItemEntityArrayList = new ArrayList<>();
    private String remark;
    private InnerSelectBuBean innerSelectBuBean;


    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bu_part_allocate_layout);
        ButterKnife.bind(this);

        adapter = new CommonItemBarCodeAdapter();
        recyclerView.setAdapter(adapter);

        setViewsLisener();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentConstant.Intent_Request_Code_Part_Allocate_Transfer_to_Bu) {
            innerSelectBuBean = data.getParcelableExtra(IntentConstant.Intent_Extra_select_bu_bean);
            buNameTextView.setText(innerSelectBuBean.getBuName());

        } else {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (!TextUtils.isEmpty(result.getContents())) {
                    parseContent(result.getContents());
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void setViewsLisener() {
        selectBuButton.setOnClickListener(this);
        remarkButton.setOnClickListener(this);
        outWarehouseInButton.setOnClickListener(this);
        scanButton.setOnClickListener(this);

        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                //2025-04-14 john这里添加手机 pda的扫描
                if (editable.toString().length() > 7 ) {
//                    ToastUtil.showToastLong("扫描结果:" + editable.toString());
                    System.out.println("========================扫描结果:" + editable.toString());
                    parseContent(editable.toString());
                }
            }
        });
    }

//    private boolean isCurrentSmallPackage = false;
    private boolean isCurrentSmallPackage = false;
    private void parseContent(String content) {
        isCurrentSmallPackage = false;
        if (TextUtils.isEmpty(content)) {
            return;
        }
        if (content.contains("/")) {
            String[] qrContent;
            qrContent = content.split("/");
            if (qrContent.length >= 2) {
                String qrTitle = qrContent[0];
                if (!qrTitle.equals("")) {
                    if (qrTitle.equals("VE") || qrTitle.equals("VF") || qrTitle.equals("VG") || qrTitle.equals("V9") || qrTitle.equals("VA") || qrTitle.equals("VB") || qrTitle.equals("VC")) {
                        if (qrTitle.equals("VE") || qrTitle.equals("V9")){
                            isCurrentSmallPackage = true;
                        }
                        //物品条码
                        scanContent = content;
                        GetItemQRCodeAsyncTask task = new GetItemQRCodeAsyncTask();
                        task.execute();
                    }
                }
            }
        }
    }

    @Override public void onClick(View v) {
        if (v == selectBuButton) {
            Intent intent = new Intent(PartAllocateTransferInnerCompanyActivity.this,InnerSaleBuSelectActivity.class);
            intent.putExtra(IntentConstant.Intent_Extra_to_inner_company_bu_from,IntentConstant.Intent_Request_Code_Part_Allocate_Transfer_to_Bu);
            startActivityForResult(intent,IntentConstant.Intent_Request_Code_Part_Allocate_Transfer_to_Bu);
        } else if (v == remarkButton) {

        } else if (v == outWarehouseInButton) {
            if (UserSingleton.get().getHRID() > 0 && !TextUtils.isEmpty(UserSingleton.get().getHRName())){

                AllocateTransferAsyncTask task = new AllocateTransferAsyncTask();
                task.execute();
            }else{
                CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(PartAllocateTransferInnerCompanyActivity.this)
                        .setTitle("").setMessage("您当前程序账号有误，需重新登录！")
                        .setLeftText("确定");


                builder.setOnViewClickListener(new OnDialogViewClickListener() {
                    @Override
                    public void onViewClick(Dialog dialog, View v, int tag) {
                        switch (tag) {
                            case CommAlertDialog.TAG_CLICK_LEFT:
                                CommonUtil.doLogout(PartAllocateTransferInnerCompanyActivity.this);
                                dialog.dismiss();
                                break;
                        }
                    }
                });
                builder.create().show();
            }
        } else if (v == scanButton) {
            new IntentIntegrator(this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
        }
    }

    private class GetItemQRCodeAsyncTask extends AsyncTask<String, Void, BoxItemEntity> {
        @Override
        protected BoxItemEntity doInBackground(String... params) {
            BoxItemEntity boxItemEntity;
//            boxItemEntity = WebServiceUtil.op_Check_Work_Line_Scan_Item_Barcode(scanContent);
            boxItemEntity = WebServiceUtil.op_Check_Allocate_Transfer_Box(scanContent);
            return boxItemEntity;
//            return null;
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

                    if (TextUtils.isEmpty(boxItemEntity.getBuName()) || boxItemEntity.getBuName().contains(nullType)) {
                        boxItemEntity.setBuName(UserSingleton.get().getUserInfo().getBu_Name());
                    }
                    boxItemEntity.setCanNotEdit(true);
                    if (isCurrentSmallPackage){
                        boxItemEntity.setCanNotEdit(false);
                    }
                    boxItemEntityArrayList.add(boxItemEntity);

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
            adapter.setData(boxItemEntityArrayList);
            inputEditText.setText("");
            inputEditText.setHint("请继续扫描");
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

    private class AllocateTransferAsyncTask extends AsyncTask<String, Void, Void> {
        WsResult ws_result;

        @Override
        protected Void doInBackground(String... params) {
            //version空了 unit,
            int count = 0;
            int newissuesize = boxItemEntityArrayList.size();
            while (count < newissuesize && boxItemEntityArrayList.size() > 0) {
                BoxItemEntity boxItemEntity = boxItemEntityArrayList.get(0);
//                ws_result = WebServiceUtil.op_Allocate_Transfer_In_Same_Company( innerSelectBuBean.getCfID(), scanContent, boxItemEntity.getItem_ID(), boxItemEntity.getIV_ID(), String.valueOf(boxItemEntity.getQty()),
                ws_result = WebServiceUtil.op_Allocate_Transfer_In_Same_Company( innerSelectBuBean.getBuID(), scanContent, boxItemEntity.getItem_ID(), boxItemEntity.getIV_ID(), String.valueOf(boxItemEntity.getQty()),
                        boxItemEntity.getLotID(),boxItemEntity.getIst_ID(),boxItemEntity.getSub_Ist_ID(),boxItemEntity.getItemName(),boxItemEntity.getLotNo(),
                        "","",String.valueOf(boxItemEntity.getBoxQty())
                       );

//                ws_result = WebServiceUtil.op_Product_Manu_In_Not_Pallet(wcIdNameEntity,boxItemEntity,new Date(),
//                        listNo,new Date() ,"李伟锋成品入库测试",
//                        13269,"lwf",
//                        thePlace.getIst_ID(),thePlace.getSub_Ist_ID(),boxItemEntity.getQty());

//                if (ws_result.getResult() ) {
//                    boxItemEntityArrayList.remove(boxItemEntity);
//                } else {
//                    return null;
//                }

                count++;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
//            adapter.notifyDataSetChanged();
//            recyclerView.setAdapter(adapter);
            //pbScan.setVisibility(View.INVISIBLE);
//            remarkTextView.setText("");
            remark = "";
            if (ws_result != null) {
                if (!ws_result.getResult()) {
                    ToastUtil.showToastShort(ws_result.getErrorInfo());
                } else {
                    ToastUtil.showToastShort("成功出库");
                    boxItemEntityArrayList.clear();
//                    boxItemEntityArrayList.remove(boxItemen)
                    adapter.notifyDataSetChanged();
                }
            }
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
