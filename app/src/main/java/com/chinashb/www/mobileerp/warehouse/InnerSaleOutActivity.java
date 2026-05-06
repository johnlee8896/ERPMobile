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
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.StringUtils;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CommAlertDialog;
import com.chinashb.www.mobileerp.widget.CommonSelectInputDialog;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.chinashb.www.mobileerp.widget.OnDialogViewClickListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2020/4/14 9:58
 * @author 作者: xxblwf
 * @description 集团内销售出库 出库给其他车间
 */

public class InnerSaleOutActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.inner_sale_out_select_bu_button) Button selectBuButton;
    @BindView(R.id.inner_sale_out_bu_name_textView) TextView buNameTextView;
    @BindView(R.id.inner_sale_out_scan_button) Button scanButton;
    @BindView(R.id.inner_sale_out_input_EditText) EditText inputEditText;
    @BindView(R.id.inner_sale_out_remark_button) Button remarkButton;
    @BindView(R.id.inner_sale_out_remark_TextView) TextView remarkTextView;
    @BindView(R.id.inner_sale_out_warehouse_in_button) Button outWarehouseInButton;
    @BindView(R.id.inner_sale_out_recyclerView) CustomRecyclerView recyclerView;

    private CommonItemBarCodeAdapter adapter;
    private String scanContent;
    private List<BoxItemEntity> boxItemEntityArrayList = new ArrayList<>();
    private String remark;
    private InnerSelectBuBean innerSelectBuBean;
    private CommonSelectInputDialog remarkDialog;
    private long lastItemID = 0;

    private OnViewClickListener remarkOnViewClickListener = new OnViewClickListener() {
        @Override public <T> void onClickAction(View v, String tag, T t) {
            if (t != null) {
                remark = (String) t;
            }
            //// TODO: 5/6/25 能调用 此方法说明是点了确定按钮，取消按钮则是直接dismiss
            if (remark.length() > 0){
                ToastUtil.showToastShort("备注添加成功！");
                remarkTextView.setText(String.format("备注：%s",remark));
                remarkTextView.setTextColor(getResources().getColor(R.color.color_orange_F58B23));
                if (remarkDialog != null && remarkDialog.isShowing()) {
                    remarkDialog.dismiss();
                }
            }else{
                ToastUtil.showToastShort("备注为空！");
            }

        }
    };


    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bu_inner_sale_out_layout);
        ButterKnife.bind(this);

        adapter = new CommonItemBarCodeAdapter();
        recyclerView.setAdapter(adapter);

        setViewsLisener();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentConstant.Intent_Request_Code_Sale_Out_to_Bu) {
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
                if (editable.toString().length() > 0){
                    parseContent(editable.toString());
                }

            }
        });
    }

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
            Intent intent = new Intent(InnerSaleOutActivity.this,InnerSaleBuSelectActivity.class);
            intent.putExtra(IntentConstant.Intent_Extra_to_inner_company_bu_from,IntentConstant.Intent_Request_Code_Sale_Out_to_Bu);
            startActivityForResult(intent,IntentConstant.Intent_Request_Code_Sale_Out_to_Bu);
        } else if (v == remarkButton) {
            showRemarkDialog();
        } else if (v == outWarehouseInButton) {
            if (UserSingleton.get().getHRID() > 0 && !TextUtils.isEmpty(UserSingleton.get().getHRName())){

                AsyncExeWarehouseOut task = new AsyncExeWarehouseOut();
                task.execute();
            }else{
                CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(InnerSaleOutActivity.this)
                        .setTitle("").setMessage("您当前程序账号有误，需重新登录！")
                        .setLeftText("确定");


                builder.setOnViewClickListener(new OnDialogViewClickListener() {
                    @Override
                    public void onViewClick(Dialog dialog, View v, int tag) {
                        switch (tag) {
                            case CommAlertDialog.TAG_CLICK_LEFT:
                                CommonUtil.doLogout(InnerSaleOutActivity.this);
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

    private void showRemarkDialog() {
        if (remarkDialog == null) {
            remarkDialog = new CommonSelectInputDialog(InnerSaleOutActivity.this);
        }
        remarkDialog.show();
        remarkDialog.setInputDialogTitle("请添加备注");
        remarkDialog.setInputOnly(true);
        remarkDialog.setOnViewClickListener(remarkOnViewClickListener);
    }

    private class GetItemQRCodeAsyncTask extends AsyncTask<String, Void, BoxItemEntity> {
        @Override
        protected BoxItemEntity doInBackground(String... params) {
            BoxItemEntity boxItemEntity;
//            boxItemEntity = WebServiceUtil.op_Check_Work_Line_Scan_Item_Barcode(scanContent);
            boxItemEntity = WebServiceUtil.op_Check_Commit_Sale_Out_Item_Barcode(scanContent);
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
                    }else{
                        //// TODO: 9/6/25 增加，针对结算中心发马来的，不论外购件还是自制件大标签 可以 修改数量
//                        if (innerSelectBuBean .getBuID() == 149 && UserSingleton.get().getUserInfo().getBu_ID() == 155){
                        if ((innerSelectBuBean .getBuName() .equals("马来座椅电机") && UserSingleton.get().getUserInfo().getBu_ID() == 155)
                                ||(innerSelectBuBean .getBuName() .contains("结算中心") )){
                            boxItemEntity .setCanNotEdit(false);
                        }
                    }

                    boxItemEntityArrayList.add(boxItemEntity);

                } else {
                    boxItemEntity.setResult(false);
                    boxItemEntity.setErrorInfo("该包装已经在装载列表中");
                }
            }

            if (boxItemEntity != null) {
                if (lastItemID != boxItemEntity.getItem_ID()){
                    emptyRemark();
                }
                lastItemID = boxItemEntity.getItem_ID();
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

    private class AsyncExeWarehouseOut extends AsyncTask<String, Void, Void> {
        WsResult ws_result;

        @Override
        protected Void doInBackground(String... params) {

            int count = 0;
            int newissuesize = boxItemEntityArrayList.size();
            while (count < newissuesize && boxItemEntityArrayList.size() > 0) {
                //// TODO: 9/6/25   这里如果改了数量如果还取boxItemEntityArrayList的话值是没有变的
//                BoxItemEntity boxItemEntity = boxItemEntityArrayList.get(0);
                List<BoxItemEntity> currentEntityList = new ArrayList<>();
                currentEntityList = adapter.getList();
                BoxItemEntity boxItemEntity = currentEntityList.get(0);
//                ws_result = WebServiceUtil.op_Commit_Sale_Out_Item(UserSingleton.get().getUserInfo().getBu_ID(), UserSingleton.get().getHRID(), innerSelectBuBean.getCfID(), innerSelectBuBean.getCompanyName(), boxItemEntity.getItem_ID(), boxItemEntity.getIV_ID(), boxItemEntity.getLotID(),
//                ws_result = WebServiceUtil.op_Commit_Sale_Out_Item(UserSingleton.get().getUserInfo().getBu_ID(), UserSingleton.get().getHRID(), innerSelectBuBean.getCfID(), innerSelectBuBean.getBuName() , boxItemEntity.getItem_ID(), boxItemEntity.getIV_ID(), boxItemEntity.getLotID(),
//                        boxItemEntity.getLotNo(), boxItemEntity.getIst_ID(), boxItemEntity.getSub_Ist_ID(), boxItemEntity.getSMLI_ID(), boxItemEntity.getSMM_ID(), boxItemEntity.getSMT_ID(),
//                        String.valueOf(boxItemEntity.getQty()));
//                2026-01-19 john 加上备注的处理
                ws_result = WebServiceUtil.op_Commit_Sale_Out_Item_Remark(UserSingleton.get().getUserInfo().getBu_ID(), UserSingleton.get().getHRID(), innerSelectBuBean.getCfID(), innerSelectBuBean.getBuName() , boxItemEntity.getItem_ID(), boxItemEntity.getIV_ID(), boxItemEntity.getLotID(),
                        boxItemEntity.getLotNo(), boxItemEntity.getIst_ID(), boxItemEntity.getSub_Ist_ID(), boxItemEntity.getSMLI_ID(), boxItemEntity.getSMM_ID(), boxItemEntity.getSMT_ID(),
                        String.valueOf(boxItemEntity.getQty()),remark);



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

            if (ws_result != null) {
                if (!ws_result.getResult()) {
                    if (StringUtils.isStringValid(ws_result.getErrorInfo())){
//                        ToastUtil.showToastLong("执行超时，未知错误！");
                        ToastUtil.showToastLong("执行失败！" + ws_result.getErrorInfo());
                    }
                    else {
                        ToastUtil.showToastLong("执行失败！" + ws_result.getErrorInfo());
                    }


                } else {
                    ToastUtil.showToastLong("成功出库");
                    boxItemEntityArrayList.clear();
//                    boxItemEntityArrayList.remove(boxItemen)
                    adapter.notifyDataSetChanged();
                }
            }
            emptyRemark();
        }

        @Override
        protected void onPreExecute() {
            //pbScan.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    private void emptyRemark() {
        remark = "";
        remarkTextView.setText(String.format("备注：%s",remark));
    }
}
