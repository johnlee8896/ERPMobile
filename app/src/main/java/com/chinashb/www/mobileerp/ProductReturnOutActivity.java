package com.chinashb.www.mobileerp;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chinashb.www.mobileerp.adapter.CommonItemBarCodeAdapter;
import com.chinashb.www.mobileerp.basicobject.IstPlaceEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.WCProductBean;
import com.chinashb.www.mobileerp.bean.entity.WcIdNameEntity;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CommAlertDialog;
import com.chinashb.www.mobileerp.widget.CommonSelectInputDialog;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.chinashb.www.mobileerp.widget.OnDialogViewClickListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/***
 * @date 创建时间 7/16/25 2:32 PM
 * @author 作者: liweifeng
 * @description 成品返工出库（物控出给返修间，包装组或产线） 
 */
public class ProductReturnOutActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.product_return_out_select_wc_button) Button selectWcButton;
    @BindView(R.id.product_return_out_wc_name_textView) TextView wcNameTextView;
    //    @BindView(R.id.product_return_out_select_NO_button) Button selectNOButton;
//    @BindView(R.id.product_return_out_NO_textView) TextView NOTextView;
    @BindView(R.id.product_return_out_scan_button) Button scanBoxButton;
    @BindView(R.id.product_return_out_warehouse_out_button) Button returnOutButton;
    @BindView(R.id.product_return_out_input_EditText) EditText inputEditText;
    @BindView(R.id.product_return_out_recyclerView) CustomRecyclerView recyclerView;
    @BindView(R.id.product_return_out_product_info_textView) TextView itemInfoTextView;

    private WcIdNameEntity wcIdNameEntity;
    private String scanContent;
    private CommonItemBarCodeAdapter adapter;

    private List<String> noList;
    private IstPlaceEntity thePlace;
    private CommonSelectInputDialog commonSelectInputDialog;
    private String currentWCName = "";
    private int currentWCID;
    private List<Integer> boxIDList;
    private List<Integer> palletIDList;
    private List<String> psIDLotIDStringList;
    private List<ManuNotPalletEntity> manuNotPalletEntityList;
    private int CODE_BOX = 0;
    private int MANU_PALLET = 1;
    private int MANU_PALLET_NOT = 2;
    private int CURRENT_PRODUCT_LABEL = CODE_BOX;
    private boolean hasClear = true;
    private boolean hasEntityIDSelect = false;
    private OnViewClickListener onViewClickListener = new OnViewClickListener() {
        @Override
        public <T> void onClickAction(View v, String tag, T t) {
            if (t != null) {
//                remark = (String) t;
//                NOTextView.setText((CharSequence) t);
                if (t instanceof WCProductBean) {
                    currentWCID = ((WCProductBean) t).getWCId();
                    currentWCName = ((WCProductBean) t).get生产线();
                    hasEntityIDSelect = true;
                }
                wcNameTextView.setText(((WCProductBean) t).get生产线());
            }
            if (commonSelectInputDialog != null && commonSelectInputDialog.isShowing()) {
                commonSelectInputDialog.dismiss();
            }
        }
    };
    private String listNo;//单据号
    private String lotNO;
    private int boxId;
    private Date manuDate = new Date();
    private boolean hasScanItem = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_product_box_return_out_layout);
        ButterKnife.bind(this);
        setViewsListener();
        initView();
        boxIDList = new ArrayList<>();
        palletIDList = new ArrayList<>();
        psIDLotIDStringList = new ArrayList<>();
        manuNotPalletEntityList = new ArrayList<>();
    }

    private void initView() {
        adapter = new CommonItemBarCodeAdapter();
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
//            if (data != null) {
//                wcIdNameEntity = data.getParcelableExtra(IntentConstant.Intent_product_wc_id_name_entity);
//                wcNameTextView.setText(wcIdNameEntity.getWcName());
//                getProductItemList();
//
//            }
        } else {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (!TextUtils.isEmpty(result.getContents())) {
                    parseContent(result.getContents());
                }
            } else {
                // This is important, otherwise the result will not be passed to the fragment
                super.onActivityResult(requestCode, resultCode, data);
            }
        }

    }


    private void setViewsListener() {
        selectWcButton.setOnClickListener(this);
        scanBoxButton.setOnClickListener(this);
//        scanAreaButton.setOnClickListener(this);
        returnOutButton.setOnClickListener(this);
//        selectNOButton.setOnClickListener(this);
        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                parseContent(editable.toString());
            }
        });
    }

    private void parseContent(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }


        System.out.println("============ scan content = " + content);
        if (content.contains("/")) {
            String[] qrContent;
            qrContent = content.split("/");
            if (qrContent.length >= 2) {

                if (content.startsWith("Pallet") && qrContent.length == 8) {

                    if (!hasClear && (CURRENT_PRODUCT_LABEL != CODE_BOX)){
                        ToastUtil.showToastShort("成品标签不可与补打标签托盘/非托盘一起操作");
                    }else {

                        int boxId = Integer.parseInt(qrContent[1]);
                        if (boxIDList.contains(boxId)) {
                            ToastUtil.showToastShort("该托盘已在列表中，请勿重复扫描！");
                        } else {

                            if (boxIDList.size() > 2) {
                                ToastUtil.showToastShort("成品连续扫描一次不超过3托");
                            } else {
                                StringBuilder tempBoxInfoSBuilder = new StringBuilder();
    //                            tempBoxInfoSBuilder.append("\n\n");
                                if (!TextUtils.isEmpty(itemInfoTextView.getText())) {
                                    tempBoxInfoSBuilder.append(itemInfoTextView.getText());
                                } else {
                                    //  第一行换行
                                    tempBoxInfoSBuilder.append("物料信息\n");
                                }

                                tempBoxInfoSBuilder.append(String.format("托盘ID:%s,托盘序列号：%s,客户图号：%s,箱子数量:%s\n\n", qrContent[1], qrContent[3], qrContent[5], qrContent[7]));
                                //                        itemInfoTextView.setText(String.format("托盘ID:%s,托盘序列号：%s,客户图号：%s,箱子数量:%s", qrContent[1], qrContent[3], qrContent[5], qrContent[7]));
                                itemInfoTextView.setText(tempBoxInfoSBuilder.toString());
                                inputEditText.setText("");
                                hasScanItem = true;
                                boxIDList.add(boxId);
                                CURRENT_PRODUCT_LABEL = CODE_BOX;
                                hasClear = false;
                            }
                        }
                    }
                }if (content.startsWith("OldNoPallet") && qrContent.length > 8) {
                    if (!hasClear && (CURRENT_PRODUCT_LABEL != MANU_PALLET_NOT )){
                        ToastUtil.showToastShort("成品标签不可与补打标签托盘/非托盘一起操作");
                    }else{
//                        tvItemCode.setText("PS_ID/" + qrContent[1] + " LotID:" + qrContent[3] );
//                        eachBoxQtyEditText.setText(qrContent[11]);
//                        currentLotID = Integer.parseInt(qrContent[3]);
//                        currentPSID = Integer.parseInt(qrContent[1]);
                        if (psIDLotIDStringList.contains(qrContent[1] + ""+ qrContent[3])) {
                            ToastUtil.showToastShort("该标签已在列表中，请勿重复扫描！");
                        } else {
                            //                        tvItemName.setText("PS_ID： " + qrContent[6]);
//                            tvItemCode.setText("Pallet_ID/" + qrContent[1] + " " + qrContent[3] + "/" + qrContent[4]);
//                            eachBoxQtyEditText.setText(qrContent[8]);

                            if (psIDLotIDStringList.size() > 2) {
                                ToastUtil.showToastShort("连续扫描一次不超过3托");
                            }else{
                                StringBuilder tempBoxInfoSBuilder = new StringBuilder();
                                if (!TextUtils.isEmpty(itemInfoTextView.getText())) {
                                    tempBoxInfoSBuilder.append(itemInfoTextView.getText());
                                } else {
                                    //  第一行换行
                                    tempBoxInfoSBuilder.append("物料信息\n");
                                }

                                tempBoxInfoSBuilder.append("PS_ID/" + qrContent[1] + " LotID:" + qrContent[3] + " 数量:" + qrContent[11] + "\n\n");
                                itemInfoTextView.setText(tempBoxInfoSBuilder.toString());


                                try{
                                    psIDLotIDStringList.add(qrContent[1] + ""+ qrContent[3]);
                                    ManuNotPalletEntity entity = new ManuNotPalletEntity();
                                    entity.setPsID(Integer.parseInt(qrContent[1]));
                                    entity.setLotID(Long.parseLong(qrContent[3]));
                                    entity.setIstID(Integer.parseInt(qrContent[5]));
                                    entity.setSubIstID(Integer.parseInt(qrContent[7]));
                                    manuNotPalletEntityList.add(entity);
                                }catch (Exception e){
                                    ToastUtil.showToastShort("解析错误:" + e.getMessage());
                                }

                                inputEditText.setText("");
                                CURRENT_PRODUCT_LABEL = MANU_PALLET_NOT;
                                hasClear = false;
                                hasScanItem = true;
                            }
//                            currentPalletID = palletID;

                        }

    //                    tvItemName.setText("PS_ID： " + qrContent[1]);



                    }

                }else if (content.startsWith("OldPallet") && qrContent.length > 8) {
                    if (!hasClear && (CURRENT_PRODUCT_LABEL != MANU_PALLET)){
                        ToastUtil.showToastShort("成品标签不可与补打标签托盘/非托盘一起操作");
                    }else {

                        int palletID = Integer.parseInt(qrContent[1]);
                        if (palletIDList.contains(palletID)) {
                            ToastUtil.showToastShort("该托盘已在列表中，请勿重复扫描！");
                        } else {
    //                        tvItemName.setText("PS_ID： " + qrContent[6]);
//                            tvItemCode.setText("Pallet_ID/" + qrContent[1] + " " + qrContent[3] + "/" + qrContent[4]);
//                            eachBoxQtyEditText.setText(qrContent[8]);

                            if (palletIDList.size() > 2) {
                                ToastUtil.showToastShort("连续扫描一次不超过3托");
                            }else{
                                StringBuilder tempBoxInfoSBuilder = new StringBuilder();
                                if (!TextUtils.isEmpty(itemInfoTextView.getText())) {
                                    tempBoxInfoSBuilder.append(itemInfoTextView.getText());
                                } else {
                                    //  第一行换行
                                    tempBoxInfoSBuilder.append("物料信息\n");
                                }

                                tempBoxInfoSBuilder.append("Pallet_ID/" + qrContent[1] + " " + qrContent[3] + "/" + qrContent[4] + "\n\n");
                                itemInfoTextView.setText(tempBoxInfoSBuilder.toString());


                                inputEditText.setText("");
                                CURRENT_PRODUCT_LABEL = MANU_PALLET;
                                hasClear = false;
                                palletIDList.add(palletID);
                                hasScanItem = true;
                            }
//                            currentPalletID = palletID;

                        }
                    }
                }
            }
        }

    }

    private void handleWCEntityListDialog(List<WCProductBean> productWCBeanList) {
        if (commonSelectInputDialog == null) {
            commonSelectInputDialog = new CommonSelectInputDialog(ProductReturnOutActivity.this);
        }
        commonSelectInputDialog.show();
        commonSelectInputDialog.setOnViewClickListener(onViewClickListener);
        commonSelectInputDialog.setTitle("请选择要退回的产线或组别");
        commonSelectInputDialog.setSelectOnly(true);
        commonSelectInputDialog.refreshContent(productWCBeanList);
    }


    @Override
    public void onClick(View view) {
        if (view == selectWcButton) {
//            getWCList();
            GetWCProductWorkListsAsyncTask task = new GetWCProductWorkListsAsyncTask();
            task.execute();
        } else if (view == scanBoxButton) {
//            if (TextUtils.isEmpty(wcNameTextView.getText())){
//                ToastUtil.showToastShort("请先选择产线");
//                return;
//            }
            new IntentIntegrator(this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
        } else if (view == returnOutButton) {
            handleProductReturnOut();

        }
    }

    private void handleProductReturnOut() {
        if (!hasEntityIDSelect){
            ToastUtil.showToastShort("还未选择要出库的发生方！");
        }
        if (!hasScanItem){
            ToastUtil.showToastShort("请先扫描标签！");
        }
        if (hasEntityIDSelect && hasScanItem){
            if (UserSingleton.get().getHRID() > 0 && !TextUtils.isEmpty(UserSingleton.get().getHRName())) {
                ExeWarehouseProductReturnOutAsyncTask task = new ExeWarehouseProductReturnOutAsyncTask();
                task.execute();

            } else {
                CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(this)
                        .setTitle("").setMessage("您当前程序账号有误，需重新登录！")
                        .setLeftText("确定");


                builder.setOnViewClickListener(new OnDialogViewClickListener() {
                    @Override
                    public void onViewClick(Dialog dialog, View v, int tag) {
                        switch (tag) {
                            case CommAlertDialog.TAG_CLICK_LEFT:
                                CommonUtil.doLogout(ProductReturnOutActivity.this);
                                dialog.dismiss();
                                break;
                        }
                    }
                });
                builder.create().show();
            }
        }

    }


    private class GetWCProductWorkListsAsyncTask extends AsyncTask<String, Void, List<WCProductBean>> {

        @Override
        protected List<WCProductBean> doInBackground(String... params) {
//            String sql = String.format("Select WC_Id,WC_Name As 生产线, bu.BU_Name as 车间 \n" +
//                    "            From P_WC inner join bu on p_wc.bu_id=bu.Bu_ID\n" +
//                    "             inner join Company on Company.Company_ID=bu.Company_ID " +
//                    "Where Deleted=0 And (P_WC.Bu_ID=%d )  and (WC_Name like 'D%' or WC_Name like '包装%' or WC_Name like '返修%') " +
//                    "order by 公司, " +
//                    "CASE WHEN WC_Name LIKE '包装%' THEN 1  WHEN WC_Name LIKE '返修%' THEN 2  ELSE 3 END, " +
//                    "生产线", UserSingleton.get().getUserInfo().getBu_ID());
//            String sql = String.format("Select WC_Id,WC_Name As 生产线, bu.BU_Name as 车间 \n" +
//                    "            From P_WC inner join bu on p_wc.bu_id=bu.Bu_ID\n" +
//                    "             inner join Company on Company.Company_ID=bu.Company_ID " +
//                    "Where Deleted=0 And (P_WC.Bu_ID=" + UserSingleton.get().getUserInfo().getBu_ID() +" )  and (WC_Name like 'D%' or WC_Name like '包装%' or WC_Name like '返修%') " +
//                    "order by 公司, " +
//                    "CASE WHEN WC_Name LIKE '包装%' THEN 1  WHEN WC_Name LIKE '返修%' THEN 2  ELSE 3 END, " +
//                    "生产线", UserSingleton.get().getUserInfo().getBu_ID());
            String sql = "Select WC_Id,WC_Name As 生产线, bu.BU_Name as 车间 \n" +
                    "            From P_WC inner join bu on p_wc.bu_id=bu.Bu_ID\n" +
                    "             inner join Company on Company.Company_ID=bu.Company_ID " +
                    "Where Deleted=0 And (P_WC.Bu_ID=" + UserSingleton.get().getUserInfo().getBu_ID() +" )  and (WC_Name like 'D%' or WC_Name like '包装%' or WC_Name like '返修%') " +
                    "order by " +
                    "CASE WHEN WC_Name LIKE '包装%' THEN 1  WHEN WC_Name LIKE '返修%' THEN 2  ELSE 3 END, " +
                    "生产线";
            WsResult result = WebServiceUtil.getDataTable(sql);
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                Gson gson = new Gson();
                List<WCProductBean> productWCBeanList = gson.fromJson(jsonData, new TypeToken<List<WCProductBean>>() {
                }.getType());
                return productWCBeanList;



            }

            return null;
        }

        @Override
        protected void onPostExecute(List<WCProductBean> productWCBeanList) {
            handleWCEntityListDialog(productWCBeanList);
        }

    }

    private class ExeWarehouseProductReturnOutAsyncTask extends AsyncTask<String, Void, Void> {
        WsResult ws_result = null;

        @Override
        protected Void doInBackground(String... params) {
            if (CURRENT_PRODUCT_LABEL == CODE_BOX){
                if (boxIDList != null && boxIDList.size() > 0) {
                    for (int i = 0; i < boxIDList.size(); i++) {
                        ws_result = WebServiceUtil.opProductManuOutReturn(boxIDList.get(i), currentWCID, currentWCName, "");

                    }
                }
            }else if (CURRENT_PRODUCT_LABEL == MANU_PALLET){
                if (palletIDList != null && palletIDList.size() > 0) {
                    for (int i = 0; i < palletIDList.size(); i++) {
                        ws_result = WebServiceUtil.opProductManualLabelManuOutReturn(palletIDList.get(i), currentWCID, currentWCName, "");

                    }
                }
            }else if (CURRENT_PRODUCT_LABEL == MANU_PALLET_NOT){
                if (psIDLotIDStringList != null && psIDLotIDStringList.size() > 0) {
                    for (int i = 0; i < psIDLotIDStringList.size(); i++) {
                        ManuNotPalletEntity entity = manuNotPalletEntityList.get(i);
                        ws_result = WebServiceUtil.opProductManualLabelNotPalletManuOutReturn(entity.getPsID(),entity.getLotID(),entity.getIstID(),entity.getSubIstID(), currentWCID, currentWCName, "");

                    }
                }
            }




            return null;


        }


        @Override
        protected void onPostExecute(Void result) {
            if (ws_result != null) {
                if (!ws_result.getResult()) {
                    //Toast.makeText(StockInActivity.this,ws_result.getErrorInfo(),Toast.LENGTH_LONG).show();
                    CommonUtil.ShowToast(ProductReturnOutActivity.this, ws_result.getErrorInfo(), R.mipmap.warning);

                } else {
                    CommonUtil.ShowToast(ProductReturnOutActivity.this, "操作成功！",R.mipmap.smiley);

                }

            }
            hasClear = true;
            hasScanItem = false;
            CURRENT_PRODUCT_LABEL = CODE_BOX;
            if (boxIDList.size() > 0){
                boxIDList.clear();
            }
            if (palletIDList.size() > 0){
                palletIDList.clear();
            }
            if (psIDLotIDStringList.size() > 0){
                psIDLotIDStringList.clear();
            }
            itemInfoTextView.setText("");
            inputEditText.setText("");
        }


    }

    private class ManuNotPalletEntity{
        private int psID;
        private long lotID;
        private int istID;
        private int subIstID;

        public int getPsID() {
            return psID;
        }

        public ManuNotPalletEntity setPsID(int psID) {
            this.psID = psID;
            return this;
        }

        public long getLotID() {
            return lotID;
        }

        public ManuNotPalletEntity setLotID(long lotID) {
            this.lotID = lotID;
            return this;
        }

        public int getIstID() {
            return istID;
        }

        public ManuNotPalletEntity setIstID(int istID) {
            this.istID = istID;
            return this;
        }

        public int getSubIstID() {
            return subIstID;
        }

        public ManuNotPalletEntity setSubIstID(int subIstID) {
            this.subIstID = subIstID;
            return this;
        }
    }

}



