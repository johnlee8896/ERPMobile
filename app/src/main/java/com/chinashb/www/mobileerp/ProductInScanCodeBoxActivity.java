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
import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.basicobject.IstPlaceEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.ProductInAlarmBeanSimple;
import com.chinashb.www.mobileerp.bean.entity.WCSubProductEntity;
import com.chinashb.www.mobileerp.bean.entity.WcIdNameEntity;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.JsonUtil;
import com.chinashb.www.mobileerp.utils.OnAsyncTaskCompleteListener;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.utils.UnitFormatUtil;
import com.chinashb.www.mobileerp.widget.CommAlertDialog;
import com.chinashb.www.mobileerp.widget.CommonSelectInputDialog;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.chinashb.www.mobileerp.widget.OnDialogViewClickListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2020/10/29 15:05
 * @author 作者: xxblwf 最新沿用版
 * @description 扫描ERP程序生成的托盘标签入库，成品
 */

public class ProductInScanCodeBoxActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.product_in_scan_code_box_scan_button) Button scanBoxButton;
    @BindView(R.id.product_in_scan_code_box_scan_area_button) Button scanAreaButton;
    @BindView(R.id.product_in_scan_code_box_warehouse_in_button) Button warehouseInButton;
    @BindView(R.id.product_in_scan_code_box_input_EditText) EditText inputEditText;
    @BindView(R.id.product_in_scan_code_box_recyclerView) CustomRecyclerView recyclerView;
    @BindView(R.id.product_in_scan_code_box_select_wc_button) Button selectWcButton;
    @BindView(R.id.product_in_scan_code_box_wc_name_textView) TextView wcNameTextView;
    @BindView(R.id.product_in_scan_code_box_select_NO_button) Button selectNOButton;
    @BindView(R.id.product_in_scan_code_box_NO_textView) TextView NOTextView;
    @BindView(R.id.product_in_scan_code_box_item_info_textview) TextView itemInfoTextView;
    @BindView(R.id.product_in_scan_code_box_ist_info_textview) TextView istInfoTextView;
    int workLineId = 0;
    private WcIdNameEntity wcIdNameEntity;
    private String scanContent;
    private List<WCSubProductEntity> subProductEntityList;
    //    private List<WCSubProductItemEntity> boxItemEntityList;
    private List<BoxItemEntity> boxItemEntityList;
    private WCSubProductEntity certainWCSubProductEntity;
    //    private ItemProductNonTrayAdapter adapter;
    private CommonItemBarCodeAdapter adapter;
    private List<String> noList;
    private IstPlaceEntity thePlace;
    private CommonSelectInputDialog commonSelectInputDialog;
    private String currentCartonNo;
    private boolean hasScanItem = false;
    private String remark = "";
    private List<Integer> boxIDList;
    private List<Integer> errorBoxIDList;
    private List<String> errorBoxResultInfoStringList;
    //标记连续扫描模式
    private boolean isInContinuousModeAndHasPossibleError = false;
//    private boolean isLastScanExecute = false;
//    private boolean isCurrentScanInTaskStart = false;
    private boolean isCurrentScanInTaskEnd= false;
    private int currentAsyncTaskIndex = 0;
    private OnAsyncTaskCompleteListener onAsyncTaskCompleteListener = new OnAsyncTaskCompleteListener() {
        @Override
        public void onAsyncTaskComplete() {

        }
    };

    private OnViewClickListener onViewClickListener = new OnViewClickListener() {
        @Override
        public <T> void onClickAction(View v, String tag, T t) {
            if (t != null) {
                remark = (String) t;
                NOTextView.setText((CharSequence) t);
            }
            if (commonSelectInputDialog != null && commonSelectInputDialog.isShowing()) {
                commonSelectInputDialog.dismiss();
            }
        }
    };
    private String listNo;//单据号
    private String lotNO;
    //    private int boxId;
    private Date manuDate = new Date();
    //对于多台设备同时操作，这个判断没有用
//    private ArrayList<Integer> boxIDList;
    private boolean hasScanIst = false;
    private String month = "";
//    private int tempBoxID = 0;
    private int modifiedMonthBoxID = 0 ;
    private OnViewClickListener onMonthViewClickListener = new OnViewClickListener() {
        @Override
        public <T> void onClickAction(View v, String tag, T t) {
            if (t != null) {
                month = (String) t;
//                NOTextView.setText((CharSequence) t);
                //这里是数字1到12
                if (modifiedMonthBoxID > 0){
                    handleProductInAfterModifyMonth(modifiedMonthBoxID);
                }
            }
            if (commonSelectInputDialog != null && commonSelectInputDialog.isShowing()) {
                commonSelectInputDialog.dismiss();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_product_scan_code_box_in_layout);
        //保存所有已扫过的boxID，去重复判断
//        String boxIDListString = SPSingleton.get().getString(SPDefine.KEY_code_box_id_List);
        Type type = new TypeToken<ArrayList<Integer>>() {
        }.getType();
//        boxIDList = JsonUtil.parseJsonToObject(boxIDListString,type);
//        if (boxIDList == null ){
//            boxIDList = new ArrayList<>();
//        }
        boxIDList = new ArrayList<>();
        errorBoxIDList = new ArrayList<>();
        errorBoxResultInfoStringList = new ArrayList<>();
        ButterKnife.bind(this);
        setViewsListener();
        initView();

    }

    private void initView() {
        boxItemEntityList = new ArrayList<>();
//        adapter = new ItemProductNonTrayAdapter(this, boxItemEntityList);
        adapter = new CommonItemBarCodeAdapter();
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            if (data != null) {
                wcIdNameEntity = data.getParcelableExtra(IntentConstant.Intent_product_wc_id_name_entity);
                wcNameTextView.setText(wcIdNameEntity.getWcName());
                getProductItemList();

            }
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

    private void getProductItemList() {
        GetWCProductWorkListsAsyncTask task = new GetWCProductWorkListsAsyncTask();
        task.execute();
    }

    private void setViewsListener() {
        selectWcButton.setOnClickListener(this);
        scanBoxButton.setOnClickListener(this);
        scanAreaButton.setOnClickListener(this);
        warehouseInButton.setOnClickListener(this);
        selectNOButton.setOnClickListener(this);
        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                parseContent(editable.toString());
            }
        });

        NOTextView.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                listNo = editable.toString();
            }
        });
    }

//    private String getParsedString(String code,String part,String nextPart){
//        if (!TextUtils.isEmpty(nextPart)){
//            int p = code.indexOf(part) + part.length();
//            int q = code.indexOf(nextPart);
//            return code.substring(p,q - p);
//        }else{
//            int p = code.indexOf(part) + part.length();
//            return code.substring(p,code.length() - p);
//        }
//    }

    private void parseContent(String content) {
        certainWCSubProductEntity = null;

        if (TextUtils.isEmpty(content)) {
            return;
        }
        if (content.contains("/") && content.length() > 7){
            GetTransferScanContentAsyncTask transferScanContentAsyncTask = new GetTransferScanContentAsyncTask();
            transferScanContentAsyncTask.execute(content);
        }


        System.out.println("============ scan content = " + content);
        if (content.contains("/")) {
            String[] qrContent;
            qrContent = content.split("/");
            if (qrContent.length >= 2) {

                if (content.startsWith("Pallet") && qrContent.length == 8) {
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

                            tempBoxInfoSBuilder.append(String.format("托盘ID:%s,托盘序列号：%s,客户图号：%s,箱子数量:%s\n", qrContent[1], qrContent[3], qrContent[5], qrContent[7]));
                            //                        itemInfoTextView.setText(String.format("托盘ID:%s,托盘序列号：%s,客户图号：%s,箱子数量:%s", qrContent[1], qrContent[3], qrContent[5], qrContent[7]));
                            itemInfoTextView.setText(tempBoxInfoSBuilder.toString());
                            inputEditText.setText("");
                            hasScanItem = true;
                            boxIDList.add(boxId);
                        }
                    }
                    GetProductSuggestAreaAsyncTask task = new GetProductSuggestAreaAsyncTask();
                    task.execute(boxId);

                    GetProductInJudgeAlarmAsyncTask alarmAsyncTask = new GetProductInJudgeAlarmAsyncTask();
                    alarmAsyncTask.execute(boxId);
                } else if (content.startsWith("/SUB_IST_ID/") || content.startsWith("/IST_ID/")) {
                    //仓库位置码
                    scanContent = content;
                    GetProductIstAsyncTask task = new GetProductIstAsyncTask();
                    task.execute();
                }
            }
        }
    }

    private String getParsedString(String code, String part, String nextPart) {
        if (!nextPart.isEmpty()) {
            int p = code.indexOf(part) + part.length();
            int q = code.indexOf(nextPart);
            return code.substring(p, q);
        } else {
            int p = code.indexOf(part) + part.length();
            return code.substring(p);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == selectWcButton) {
            getWCList();
        } else if (view == scanBoxButton) {
//            if (TextUtils.isEmpty(wcNameTextView.getText())){
//                ToastUtil.showToastShort("请先选择产线");
//                return;
//            }
            new IntentIntegrator(this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
        } else if (view == scanAreaButton) {
            handleScanArea();
        } else if (view == warehouseInButton) {
//            2024-03-20 john成品扫描串库位的问题很可能是扫了箱标后，直接点了入库按钮，目前先禁掉此手动点击
//            handleIntoWareHouse();


//            handleIntoWareHouse();

        } else if (view == selectNOButton) {
            handleSelectNO();
        }
    }

    private void handleSelectNO() {
        if (commonSelectInputDialog == null) {
            commonSelectInputDialog = new CommonSelectInputDialog(ProductInScanCodeBoxActivity.this);
        }
        commonSelectInputDialog.show();
        commonSelectInputDialog.setOnViewClickListener(onViewClickListener);
        commonSelectInputDialog.setTitle("请选择或添加单据号");
        commonSelectInputDialog.refreshContent(getNOList());
    }

    private void handleScanArea() {
        if (hasScanItem) {
            new IntentIntegrator(this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
            inputEditText.setText("");
        } else {
            ToastUtil.showToastShort("没有扫描箱码或仓库位置码没有成功，请重新扫描！");
        }


//        if (boxItemEntityList.size() > 0) {
//            int selectedcount = 0;
//            for (int i = 0; i < boxItemEntityList.size(); i++) {
////                if (boxItemEntityList.get(i).isSelect()) {
////                    selectedcount++;
////                }
//            }
//            selectedcount = boxItemEntityList.size();
//            if (selectedcount > 0) {
//                new IntentIntegrator(this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
//                inputEditText.setText("");
//            } else {
//                ToastUtil.showToastShort("请选择成品箱条目！");
//            }
//
//        } else {
//            ToastUtil.showToastShort("没有物品条码或仓库位置码没有成功，请重新扫描！");
//        }
    }

    private List<String> getNOList() {
        noList = new ArrayList<>();
//        C-20191226-01
        for (int i = 0; i < 15; i++) {
            noList.add(String.format("C-%s-%s", UnitFormatUtil.formatTimeToDayWithoutLine(System.currentTimeMillis()), String.format("%02d", i + 1)));
        }
        return noList;
    }

    private List<String> getMonthList() {
        List monthList = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            monthList.add(String.valueOf(i));
        }
        return monthList;
    }

    /**
     * 获取产线（工作中心）
     */
    private void getWCList() {
        Intent intent = new Intent(this, SelectProductWCListActivity.class);
        intent.putExtra(IntentConstant.Intent_Extra_work_line_from, IntentConstant.Intent_Extra_work_line_from_product);
        startActivityForResult(intent, 200);
    }

    private void handleIntoWareHouse() {
//        boxIDList = new ArrayList<>();
//        boxIDList.add(788227);
//        boxIDList.add(790798);
//        boxIDList.add(790803);
//        hasScanIst = true;
//        thePlace = new IstPlaceEntity();



        if (UserSingleton.get().getHRID() > 0 && !TextUtils.isEmpty(UserSingleton.get().getHRName())) {
            if (hasScanIst && (thePlace != null)) {
                ExeWarehouseProductInCodeBoxAsyncTask task = new ExeWarehouseProductInCodeBoxAsyncTask();
                task.execute();
            } else {
                ToastUtil.showToastShort("没有扫描仓库位置码，请重新扫描！");
            }

        } else {
            CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(this)
                    .setTitle("").setMessage("您当前程序账号有误，需重新登录！")
                    .setLeftText("确定");


            builder.setOnViewClickListener(new OnDialogViewClickListener() {
                @Override
                public void onViewClick(Dialog dialog, View v, int tag) {
                    switch (tag) {
                        case CommAlertDialog.TAG_CLICK_LEFT:
                            CommonUtil.doLogout(ProductInScanCodeBoxActivity.this);
                            dialog.dismiss();
                            break;
                    }
                }
            });
            builder.create().show();
        }


//        if (boxItemEntityList.size() > 0) {
//            int selectedcount = 0;
//            for (int i = 0; i < boxItemEntityList.size(); i++) {
//                if (boxItemEntityList.get(i) != null) {
//                    //// TODO: 2019/12/27
////                    if (boxItemEntityList.get(i).getIst_ID() == 0) {
//                    if (thePlace.getIst_ID() == 0) {
////                            CommonUtil.ShowToast(StockInActivity.this, "还没有扫描库位", R.mipmap.warning, Toast.LENGTH_SHORT);
//                        ToastUtil.showToastLong("还没有扫描库位");
//                        return;
//                    }
//                    selectedcount++;
//                }
//
//            }
//            if (selectedcount > 0) {
//
//                if (UserSingleton.get().getHRID() > 0 && !TextUtils.isEmpty(UserSingleton.get().getHRName())) {
//
//                    ExeWarehouseProductInCodeBoxAsyncTask task = new ExeWarehouseProductInCodeBoxAsyncTask();
//                    task.execute();
//                } else {
//                    CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(this)
//                            .setTitle("").setMessage("您当前程序账号有误，需重新登录！")
//                            .setLeftText("确定");
//
//
//                    builder.setOnViewClickListener(new OnDialogViewClickListener() {
//                        @Override
//                        public void onViewClick(Dialog dialog, View v, int tag) {
//                            switch (tag) {
//                                case CommAlertDialog.TAG_CLICK_LEFT:
//                                    CommonUtil.doLogout(ProductScanCodeBoxInActivity.this);
//                                    dialog.dismiss();
//                                    break;
//                            }
//                        }
//                    });
//                    builder.create().show();
//                }
//            }
//
//        } else {
//            //// TODO: 2019/7/10  这里应控件按钮的可用性
//            ToastUtil.showToastShort("没有物品条码或仓库位置码没有成功，请重新扫描！");
//        }
    }

    private void modifyPalletMonth() {
        if (commonSelectInputDialog == null) {
            commonSelectInputDialog = new CommonSelectInputDialog(ProductInScanCodeBoxActivity.this);
        }
        commonSelectInputDialog.show();
//        commonSelectInputDialog.setOnViewClickListener(onViewClickListener);
        commonSelectInputDialog.setOnViewClickListener(onMonthViewClickListener);
        commonSelectInputDialog.setTitle("请选择修改后的托盘号月份");
        commonSelectInputDialog.refreshContent(getMonthList());
    }

    private void handleProductInAfterModifyMonth(int boxID) {
        ModifyMonthExeWarehouseProductInCodeBoxAsyncTask task = new ModifyMonthExeWarehouseProductInCodeBoxAsyncTask();
        task.execute(boxID);
    }

    private void handleProductInNeglectMonth(int boxID) {
        DirectExeWarehouseProductInCodeBoxAsyncTask task = new DirectExeWarehouseProductInCodeBoxAsyncTask();
        task.execute(boxID);
    }

    private void handleAllInCorrect() {
        CommonUtil.ShowToast(ProductInScanCodeBoxActivity.this, boxIDList.size() > 1 ? "全部入库成功！" : "入库成功！", R.mipmap.smiley);
        hasScanItem = false;
        inputEditText.setText("");
        itemInfoTextView.setText("物料信息");
        istInfoTextView.setText("入库区域");
        hasScanIst = false;
        thePlace = null;
        //2024-03-21 这个是导致扫完第一个库后再扫第二个提示重复入库的问题，因没有重置boxIDList
        boxIDList.clear();
    }

    private void handleFinishIfError(){
        hasScanItem = false;
        inputEditText.setText("");
        itemInfoTextView.setText("物料信息");
        istInfoTextView.setText("入库区域");
        hasScanIst = false;
        thePlace = null;
        //2024-03-21 这个是导致扫完第一个库后再扫第二个提示重复入库的问题，因没有重置boxIDList
        boxIDList.clear();
    }

    private class GetWCProductWorkListsAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            //// TODO: 2019/12/20  注意这里的第二个参数%s有所修改
            String sql = String.format("Select distinct Product.Item_ID As Item_ID,Ps_Version.IV_ID,Product.Product_ID,PS_Version.PS_ID," +
                            "Product.Product_Chinese_Name As Product_Name," +
                            "Product.Abb As Product_Common_Name,Ps_Version.PS_Version As Version,  Product.Product_Version As Newest_Version, " +
                            " Case When Product.Audit=1 Then '' Else '未审' End As Approval_detail  " +
                            "From Item Inner Join Product On Product.Item_ID=Item.Item_ID  Inner Join PS_Version On Product.Product_ID=Ps_Version.Product_ID " +
                            "And PS_Version.Active=1  Left Join [P_PWC]  With (NoLock)  On [Item].[Item_ID]=[P_PWC].[Item_ID] " +
                            " Where (Product.Bu_ID=%s Or Product.Bu_ID=%s)  And P_PWC.WC_ID=%s", UserSingleton.get().getUserInfo().getBu_ID(), UserSingleton.get().getUserInfo().getBu_ID()
                    , wcIdNameEntity.getWcId());
            WsResult result = WebServiceUtil.getDataTable(sql);
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                Gson gson = new Gson();
                subProductEntityList = gson.fromJson(jsonData, new TypeToken<List<WCSubProductEntity>>() {
                }.getType());
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Void result) {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    private class ParseProductCartonAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            return null;
        }
    }

    private class GetProductIstAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            IstPlaceEntity istPlaceEntity = WebServiceUtil.op_Check_Commit_IST_Barcode(scanContent);
            thePlace = istPlaceEntity;

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {



            if (thePlace != null ) {
                if (thePlace.getResult()){
                    if (boxItemEntityList != null && boxItemEntityList.size() > 0) {
                        boxItemEntityList.get(0).setIstName(thePlace.getIstName());
                        boxItemEntityList.get(0).setIst_ID(thePlace.getIst_ID());
                        boxItemEntityList.get(0).setSub_Ist_ID(thePlace.getSub_Ist_ID());
                    }

                    hasScanIst = true;
                    System.out.println("区域信息 大：" + thePlace.getBuName() + " " + thePlace.getIstName() + " " + "id" + thePlace.getIst_ID() + ":" + thePlace.getSub_Ist_ID());
                    istInfoTextView.setText("区域信息 大：" + thePlace.getBuName() + " " + thePlace.getIstName() + " " + "id" + thePlace.getIst_ID() + ":" + thePlace.getSub_Ist_ID());
//            ToastUtil.showToastShort("区域信息 大：" + thePlace.getBuName() + " " + thePlace.getIstName() + " " + thePlace.getIst_ID() + ":" + thePlace.getSub_Ist_ID());
                    //todo 直接执行入库登帐
                    handleIntoWareHouse();
                }else{
                    ToastUtil.showToastLong(thePlace.getErrorInfo());
                }

            } else {
                ToastUtil.showToastShort("地址码获取错误，请重新扫描！");
            }
            //tv.setText(fahren + "∞ F");

//            recyclerView.setAdapter(adapter);
//            //pbScan.setVisibility(View.INVISIBLE);
//            inputEditText.setText("");
//            //2024-07-11 john 有报 thePlace.getBuName() 空指针，故加一判断
//            if (thePlace != null){
//                hasScanIst = true;
//                System.out.println("区域信息 大：" + thePlace.getBuName() + " " + thePlace.getIstName() + " " + "id" + thePlace.getIst_ID() + ":" + thePlace.getSub_Ist_ID());
//                istInfoTextView.setText("区域信息 大：" + thePlace.getBuName() + " " + thePlace.getIstName() + " " + "id" + thePlace.getIst_ID() + ":" + thePlace.getSub_Ist_ID());
////            ToastUtil.showToastShort("区域信息 大：" + thePlace.getBuName() + " " + thePlace.getIstName() + " " + thePlace.getIst_ID() + ":" + thePlace.getSub_Ist_ID());
//                //todo 直接执行入库登帐
//                handleIntoWareHouse();
//            }else{
//                ToastUtil.showToastShort("地址码获取错误，请重新扫描！");
//            }


        }

    }

    private class ExeWarehouseProductInCodeBoxAsyncTask extends AsyncTask<String, Void, Void> {
        //        WsResult ws_result;
        List<WsResult> wsResultList = new ArrayList<>();
        List<WsResult> errorWSResultList = new ArrayList<>();

        @Override
        protected Void doInBackground(String... params) {
            wsResultList.clear();
            errorBoxIDList.clear();
            errorBoxResultInfoStringList.clear();
//            ws_result = WebServiceUtil.op_Product_Manu_In_Pallet(boxId, thePlace.getIst_ID(), thePlace.getSub_Ist_ID(), remark);
//            //循环中已经执行完了todo
//            for (int i = 0; i < boxIDList.size(); i++) {
//                WsResult ws_result = WebServiceUtil.op_Product_Manu_In_Pallet(boxIDList.get(i), thePlace.getIst_ID(), thePlace.getSub_Ist_ID(), remark);
//                wsResultList.add(ws_result);
//            }

            //初始化这个

            isCurrentScanInTaskEnd = false;

            int count = 0;
            List<Integer> tempBoxIDList = new ArrayList<>();
            for (int i = 0; i < boxIDList.size(); i++) {
                tempBoxIDList.add(boxIDList.get(i));
            }

            int selectedCount = tempBoxIDList.size();
            while (count < selectedCount && tempBoxIDList.size() > 0) {
                //// TODO: 2024/5/8 因为remove，故每次取第0个
                int scanInBoxID = tempBoxIDList.get(0);
                WsResult result = WebServiceUtil.op_Product_Manu_In_Pallet(scanInBoxID, thePlace.getIst_ID(), thePlace.getSub_Ist_ID(), remark);
//                WsResult result = WebServiceUtil.op_Product_Manu_In_Pallet(scanInBoxID, 10013, 48248, remark);
                wsResultList.add(result);
                if (result.getResult()) {
                    //// TODO: 2024/5/8  这里会被理解为remove index而非object
                } else {
                    errorWSResultList.add(result);
                }
                //// TODO: 2024/5/8 不论成功与否都要移除，否则每次执行的都是同一个
                tempBoxIDList.remove(0);
                count++;
            }




            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            //这里接收所有的返回值
            //循环中已经执行完了,这里处理所有的返回，不需要再考虑某一个复杂的异步执行问题
            if (wsResultList.size() == 1) {
                WsResult ws_result = null;
                ws_result = wsResultList.get(0);

                int tempBoxID = boxIDList.get(0);

                if (ws_result != null) {
                    if (!ws_result.getResult()) {
                        String errorInfo = ws_result.getErrorInfo();
                        if (!TextUtils.isEmpty(errorInfo)) {
                            if (errorInfo.contains("月份与批次") && errorInfo.contains("不一致")) {
                                CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(ProductInScanCodeBoxActivity.this)
                                        .setTitle("").setMessage("当前托盘号月份与批次月份不一致，是否修改月份，点确定为修改，点取消则直接入库！")
                                        .setLeftText("确定修改")
                                        .setRightText("直接入库");


                                builder.setOnViewClickListener(new OnDialogViewClickListener() {
                                    @Override
                                    public void onViewClick(Dialog dialog, View v, int tag) {
                                        switch (tag) {
                                            case CommAlertDialog.TAG_CLICK_LEFT:
//                                            //修改托盘月份
                                                modifiedMonthBoxID = tempBoxID;
                                                modifyPalletMonth();
                                                dialog.dismiss();
                                                break;
                                            case CommAlertDialog.TAG_CLICK_RIGHT:
//                                            //直接入库
                                                handleProductInNeglectMonth(tempBoxID);
                                                dialog.dismiss();
                                                break;
                                        }
                                    }
                                });
                                builder.create().show();
                            } else {
//                                CommonUtil.ShowToast(ProductInScanCodeBoxActivity.this, ws_result.getErrorInfo(), R.mipmap.warning);
                                CommonUtil.ShowToast(ProductInScanCodeBoxActivity.this, ws_result.getErrorInfo(), R.mipmap.warning);
                                ToastUtil.showToastShort(ws_result.getErrorInfo());
                                handleFinishIfError();
                            }
                        }

                    } else {
                        handleAllInCorrect();
                    }

                }
            } else {
                //判断是否全部成功
                boolean allCorrect = true;
                for (int i = 0; i < wsResultList.size(); i++) {
                    if (!wsResultList.get(i).getResult()) {
                        allCorrect = false;
                        break;
                    }
                }

//                isInContinuousMode = true;
                if (allCorrect) {
                    handleAllInCorrect();
                } else {
                    //处理有部分错的
                    isInContinuousModeAndHasPossibleError = true;
//                    for (int i = 0; i < wsResultList.size(); i++) {
//                    int index = 0;
//                    currentAsyncTaskIndex = index;
                    currentAsyncTaskIndex = 0;
                    isCurrentScanInTaskEnd = true;
//                    while(isCurrentScanInTaskEnd && index < wsResultList.size()){
                    while(isCurrentScanInTaskEnd && currentAsyncTaskIndex < wsResultList.size()){
                        isCurrentScanInTaskEnd = false;
//                        WsResult wsResult = wsResultList.get(index);
                        WsResult wsResult = wsResultList.get(currentAsyncTaskIndex);
//                        int tempBoxID = boxIDList.get(index);
                        int tempBoxID = boxIDList.get(currentAsyncTaskIndex);
                        if (wsResult != null) {
                            if (!wsResult.getResult()) {
                                errorBoxIDList.add(tempBoxID);
                                String errorInfo = wsResult.getErrorInfo();
                                if (!TextUtils.isEmpty(errorInfo)) {
                                    if (errorInfo.contains("月份与批次") && errorInfo.contains("不一致")) {
                                        CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(ProductInScanCodeBoxActivity.this)
                                                .setTitle("").setMessage("当前托盘号月份与批次月份不一致，是否修改月份，点确定为修改，点取消则直接入库！")
                                                .setLeftText("确定修改")
                                                .setRightText("直接入库");


                                        builder.setOnViewClickListener(new OnDialogViewClickListener() {
                                            @Override
                                            public void onViewClick(Dialog dialog, View v, int tag) {
                                                switch (tag) {
                                                    case CommAlertDialog.TAG_CLICK_LEFT:
//                                                      //修改托盘月份
                                                        modifyPalletMonth();
                                                        dialog.dismiss();
                                                        break;
                                                    case CommAlertDialog.TAG_CLICK_RIGHT:
//                                                      //直接入库
                                                        handleProductInNeglectMonth(tempBoxID);
                                                        dialog.dismiss();
                                                        break;
                                                }
                                            }
                                        });
                                        builder.create().show();
                                    } else {
//                                        CommonUtil.ShowToast(ProductInScanCodeBoxActivity.this, wsResult.getErrorInfo(), R.mipmap.warning);
                                        errorBoxResultInfoStringList.add(errorInfo);
                                        isCurrentScanInTaskEnd = true;
                                        currentAsyncTaskIndex++;
                                    }
                                }

                            }//循环里的如果成功就暂不提示
//                            else {
//                                handleAllInCorrect();
//                            }

                        }
//                        index++;
                    }
//                    for (int i = 0; i < wsResultList.size(); i++) {
//                        WsResult wsResult = wsResultList.get(i);
//                        int tempBoxID = boxIDList.get(i);
//                        if (wsResult != null) {
//                            if (!wsResult.getResult()) {
//                                errorBoxIDList.add(tempBoxID);
//                                String errorInfo = wsResult.getErrorInfo();
//                                if (!TextUtils.isEmpty(errorInfo)) {
//                                    if (errorInfo.contains("月份与批次") && errorInfo.contains("不一致")) {
//                                        CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(ProductInScanCodeBoxActivity.this)
//                                                .setTitle("").setMessage("当前托盘号月份与批次月份不一致，是否修改月份，点确定为修改，点取消则直接入库！")
//                                                .setLeftText("确定修改")
//                                                .setRightText("直接入库");
//
//
//                                        builder.setOnViewClickListener(new OnDialogViewClickListener() {
//                                            @Override
//                                            public void onViewClick(Dialog dialog, View v, int tag) {
//                                                switch (tag) {
//                                                    case CommAlertDialog.TAG_CLICK_LEFT:
////                                            //修改托盘月份
//                                                        modifyPalletMonth();
//                                                        dialog.dismiss();
//                                                        break;
//                                                    case CommAlertDialog.TAG_CLICK_RIGHT:
////                                            //直接入库
//                                                        handleProductInNeglectMonth(tempBoxID);
//                                                        dialog.dismiss();
//                                                        break;
//                                                }
//                                            }
//                                        });
//                                        builder.create().show();
//                                    } else {
//                                        CommonUtil.ShowToast(ProductInScanCodeBoxActivity.this, wsResult.getErrorInfo(), R.mipmap.warning);
//                                    }
//                                }
//
//                            }//循环里的如果成功就暂不提示
////                            else {
////                                handleAllInCorrect();
////                            }
//
//                        }
//                    }
                }
            }

            //在全部执行完后做一个总结提示
            //// TODO: 2024/5/10 因为是异步，所以下面的执行也许上面的还没有执行完
            if (isInContinuousModeAndHasPossibleError && isCurrentScanInTaskEnd) {
                if (errorBoxIDList != null && errorBoxIDList.size() > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < errorBoxIDList.size(); i++) {
                        if (errorBoxResultInfoStringList.size() > 0) {
                            stringBuilder.append(String.format("箱号为VG/%d的入库有错误，错误信息为：", errorBoxIDList.get(i)));
                            stringBuilder.append(errorBoxResultInfoStringList.get(i)).append("\n\n");
                        }
                    }
                    if (stringBuilder.toString().trim().length() > 0) {
                        CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(ProductInScanCodeBoxActivity.this)
                                .setTitle("此入库信息提示").setMessage(stringBuilder.toString())
                                .setMiddleText("确定");


                        builder.setOnViewClickListener(new OnDialogViewClickListener() {
                            @Override
                            public void onViewClick(Dialog dialog, View v, int tag) {
                                switch (tag) {
                                    case CommAlertDialog.TAG_CLICK_MIDDLE:
                                        dialog.dismiss();
//                                        handleAllInCorrect();
                                        hasScanItem = false;
                                        inputEditText.setText("");
                                        itemInfoTextView.setText("物料信息\n");
                                        istInfoTextView.setText("入库区域");
                                        hasScanIst = false;
                                        thePlace = null;
                                        boxIDList.clear();
                                        errorBoxIDList.clear();
                                        errorBoxResultInfoStringList.clear();
                                        break;

                                }
                            }
                        });
                        builder.create().show();
                    }

                }
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    private class DirectExeWarehouseProductInCodeBoxAsyncTask extends AsyncTask<Integer, Void, Void> {
        WsResult ws_result;

        int tempBoxID = 0;
        @Override
        protected Void doInBackground(Integer... params) {
//            try{
//
//            }catch (Exception e){
//
//            }
            if (params.length > 0){
                tempBoxID = params[0];
                if (tempBoxID > 0){
                    ws_result = WebServiceUtil.op_Product_Manu_In_Pallet_Neglect_Month(tempBoxID, thePlace.getIst_ID(), thePlace.getSub_Ist_ID(), remark);
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            //pbScan.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");KXCA252A1200415F0002002
            if (ws_result.getResult()) {
                //添加库位与manuLot的关联
//                    addIstSubIstManuLotRelation(boxItemEntity);
//                boxItemEntityList.remove(entity);
//                adapter.setData(boxItemEntityList);
            }
            if (ws_result != null) {
                if (!ws_result.getResult()) {
                    //Toast.makeText(StockInActivity.this,ws_result.getErrorInfo(),Toast.LENGTH_LONG).show();
//                    String errorInfo = ws_result.getErrorInfo();
//                    if (!TextUtils.isEmpty(errorInfo)) {
//                        if (errorInfo.contains("月份与批次") && errorInfo.contains("不一致")) {
//                            CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(ProductInScanCodeBoxActivity.this)
//                                    .setTitle("").setMessage("当前托盘号月份与批次月份不一致，是否修改月份，点确定为修改，点取消则直接入库！")
//                                    .setLeftText("确定修改")
//                                    .setRightText("直接入库");
//
//
//                            builder.setOnViewClickListener(new OnDialogViewClickListener() {
//                                @Override
//                                public void onViewClick(Dialog dialog, View v, int tag) {
//                                    switch (tag) {
//                                        case CommAlertDialog.TAG_CLICK_LEFT:
////                                            //修改托盘月份
//                                            modifyPalletMonth();
//                                            dialog.dismiss();
//                                            break;
//                                        case CommAlertDialog.TAG_CLICK_RIGHT:
////                                            //直接入库
//                                            handleProductInNeglectMonth();
//                                            dialog.dismiss();
//                                            break;
//                                    }
//                                }
//                            });
//                            builder.create().show();
//                        } else {
//                            CommonUtil.ShowToast(ProductInScanCodeBoxActivity.this, ws_result.getErrorInfo(), R.mipmap.warning);
//                        }
//                    }
                    if (isInContinuousModeAndHasPossibleError) {
                        errorBoxResultInfoStringList.add(ws_result.getErrorInfo());
                    } else {
                        CommonUtil.ShowToast(ProductInScanCodeBoxActivity.this, ws_result.getErrorInfo(), R.mipmap.warning);
                    }
                    isCurrentScanInTaskEnd = true;
                    currentAsyncTaskIndex++;


                } else {
//                    //Toast.makeText(StockInActivity.this,"入库完成",Toast.LENGTH_LONG).show();
////                    CommonUtil.ShowToast(ProductScanBoxInActivity.this, "入库完成" + ws_result.getErrorInfo(), R.mipmap.smiley);
//                    handleAllInCorrect();
////                    if (boxIDList != null){
////                        boxIDList.add(boxId);
////                    }


                    if (isInContinuousModeAndHasPossibleError) {
//                        CommonUtil.ShowToast(ProductInScanCodeBoxActivity.this, "入库完成", R.mipmap.smiley);
                        //持续模式中如果其中一个执行正确，则不给提示，因为最终全部结束后会有整体提示
                        if (errorBoxIDList != null && errorBoxIDList.size() > 0) {
                            //这里默认是remove index
//                            if (errorBoxIDList.contains(tempBoxID)) {
//                                errorBoxIDList.remove((Integer) tempBoxID);
//
//                            }
                            //// TODO: 2024/5/9 上面的remove容易有问题
                            for (int i = 0 ; i< errorBoxIDList.size() ;i++){
                                if (errorBoxIDList.get(i) == tempBoxID){
                                    errorBoxIDList.remove(i);
                                }
                            }
                        }
                    } else {
                        handleAllInCorrect();
                    }

                }
                isCurrentScanInTaskEnd = true;
                currentAsyncTaskIndex++;

            }

//            boxItemAdapter = new ItemProductNonTrayAdapter(ProductScanBoxInActivity.this, subProductItemEntityList);
//            mRecyclerView.setAdapter(boxItemAdapter);
            //pbScan.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    private class ModifyMonthExeWarehouseProductInCodeBoxAsyncTask extends AsyncTask<Integer, Void, Void> {
        WsResult ws_result;
        int tempBoxID = 0;

        @Override
        protected Void doInBackground(Integer... params) {
            if (params.length > 0){
                tempBoxID = params[0];
                modifiedMonthBoxID = params[0];
                if (tempBoxID > 0){
                    ws_result = WebServiceUtil.op_Product_Manu_In_Pallet_Modify_Month(tempBoxID, thePlace.getIst_ID(), thePlace.getSub_Ist_ID(), remark, month);

                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            //pbScan.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");KXCA252A1200415F0002002
            if (ws_result.getResult()) {
                //添加库位与manuLot的关联
//                    addIstSubIstManuLotRelation(boxItemEntity);
//                boxItemEntityList.remove(entity);
//                adapter.setData(boxItemEntityList);
            }
            if (ws_result != null) {
                if (!ws_result.getResult()) {
                    //Toast.makeText(StockInActivity.this,ws_result.getErrorInfo(),Toast.LENGTH_LONG).show();
                    String errorInfo = ws_result.getErrorInfo();
                    if (!TextUtils.isEmpty(errorInfo)) {
                        if (errorInfo.contains("月份与批次") && errorInfo.contains("不一致")) {
                            CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(ProductInScanCodeBoxActivity.this)
                                    .setTitle("").setMessage("当前托盘号月份与批次月份不一致，是否修改月份，点确定为修改，点取消则直接入库！")
                                    .setLeftText("确定修改")
                                    .setRightText("直接入库");


                            builder.setOnViewClickListener(new OnDialogViewClickListener() {
                                @Override
                                public void onViewClick(Dialog dialog, View v, int tag) {
                                    switch (tag) {
                                        case CommAlertDialog.TAG_CLICK_LEFT:
//                                            //修改托盘月份
                                            modifyPalletMonth();
                                            dialog.dismiss();
                                            break;
                                        case CommAlertDialog.TAG_CLICK_RIGHT:
//                                            //直接入库
                                            handleProductInNeglectMonth(modifiedMonthBoxID);
                                            dialog.dismiss();
                                            break;
                                    }
                                }
                            });
                            builder.create().show();
                        } else {
                            if (isInContinuousModeAndHasPossibleError) {
                                errorBoxResultInfoStringList.add(ws_result.getErrorInfo());

                            } else {
                                CommonUtil.ShowToast(ProductInScanCodeBoxActivity.this, ws_result.getErrorInfo(), R.mipmap.warning);
                            }
                        }
                    }

                } else {
                    //Toast.makeText(StockInActivity.this,"入库完成",Toast.LENGTH_LONG).show();
//                    CommonUtil.ShowToast(ProductScanBoxInActivity.this, "入库完成" + ws_result.getErrorInfo(), R.mipmap.smiley);
                    if (isInContinuousModeAndHasPossibleError) {
//                        CommonUtil.ShowToast(ProductInScanCodeBoxActivity.this, "入库完成", R.mipmap.smiley);
                        //持续模式中如果其中一个执行正确，则不给提示，因为最终全部结束后会有整体提示
//                        if (errorBoxIDList != null && errorBoxIDList.size() > 0) {
////                            errorBoxIDList.remove(tempBoxID);
//                            //这里默认是remove index
////                            if (errorBoxIDList.contains(tempBoxID)) {
////                                errorBoxIDList.remove((Integer) tempBoxID);
////
////                            }
//
//                        }

                        if (errorBoxIDList != null && errorBoxIDList.size() > 0) {

                            //// TODO: 2024/5/9 上面的remove容易有问题
                            for (int i = 0 ; i< errorBoxIDList.size() ;i++){
                                if (errorBoxIDList.get(i) == tempBoxID){
                                    errorBoxIDList.remove(i);
                                }
                            }
                        }
                    } else {
                        handleAllInCorrect();
                    }
//                    if (boxIDList != null){
//                        boxIDList.add(boxId);
//                    }

                }
                isCurrentScanInTaskEnd = true;
                currentAsyncTaskIndex++;

            }

//            boxItemAdapter = new ItemProductNonTrayAdapter(ProductScanBoxInActivity.this, subProductItemEntityList);
//            mRecyclerView.setAdapter(boxItemAdapter);
            //pbScan.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

//    @Override protected void onDestroy() {
//        super.onDestroy();
//        SPSingleton.get().putString(SPDefine.KEY_code_box_id_List,JsonUtil.objectToJson(boxIDList));
//    }

    private class GetProductSuggestAreaAsyncTask extends AsyncTask<Integer,Void,Void>{
        WsResult ws_result;

        @Override
        protected Void doInBackground(Integer... integers) {
            int boxID = integers[0];
            ws_result = WebServiceUtil.getProductSuggestAreaName(UserSingleton.get().getUserInfo().getBu_ID(),boxID);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (ws_result != null && ws_result.getResult()){
                ToastUtil.showToastLong("建议库位:" + ws_result.getErrorInfo());
            }else{
                ToastUtil.showToastShort("未能获取建议库位");

            }
        }
    }


    private class GetProductInJudgeAlarmAsyncTask extends AsyncTask<Integer,Void,Void>{
        WsResult ws_result;

        @Override
        protected Void doInBackground(Integer... integers) {
            int boxID = integers[0];
            ws_result = WebServiceUtil.getProductInJudgeAlarm(UserSingleton.get().getUserInfo().getBu_ID(),boxID);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (ws_result != null && ws_result.getResult()){
//                ToastUtil.showToastLong("建议库位:" + ws_result.getErrorInfo());
                Type type = new TypeToken<List<ProductInAlarmBeanSimple>>() {
                }.getType();
//                List<ProductInAlarmBean> beanList = JsonUtil.parseJsonToObject(ws_result.getErrorInfo(), type);
                List<ProductInAlarmBeanSimple> beanList = JsonUtil.parseJsonToObject(ws_result.getErrorInfo(), type);
//                return beanList;
                boolean isAlarm = false;
                for (ProductInAlarmBeanSimple bean : beanList){
                    //// TODO: 5/30/25 遍历查询，如果是排版，且有一个是不够就提示是紧急品
                    if (bean.is排版()){
                        if (!bean.is库存够发货()){
                            isAlarm = true;
                            break;
                        }
                    }
                }
                if (isAlarm){
                    CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(ProductInScanCodeBoxActivity.this)
                            .setTitle("").setMessage("该产品是发货缺料产品！")
                            .setLeftText("确定");


                    builder.setOnViewClickListener(new OnDialogViewClickListener() {
                        @Override
                        public void onViewClick(Dialog dialog, View v, int tag) {
                            switch (tag) {
                                case CommAlertDialog.TAG_CLICK_LEFT:
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    });
                    builder.create().show();
                }
            }else{
//                ToastUtil.showToastShort("未能获取建议库位");

            }
        }
    }

    private class GetTransferScanContentAsyncTask extends AsyncTask<String,Void,Void>{
        WsResult ws_result;

        @Override
        protected Void doInBackground(String... strings) {
            String content = strings[0];
            ws_result = WebServiceUtil.getTransferScanContent(UserSingleton.get().getUserInfo().getBu_ID(),UserSingleton.get().getHRID(),content);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (ws_result != null && ws_result.getResult()){
//                ToastUtil.showToastShort("建议库位:" + ws_result.getErrorInfo());
                System.out.println("上传成功");
            }else{
                System.out.println("上传失败");
//                ToastUtil.showToastShort("未能获取建议库位");

            }
        }
    }

}