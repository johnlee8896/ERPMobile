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
import com.chinashb.www.mobileerp.bean.entity.WCSubProductEntity;
import com.chinashb.www.mobileerp.bean.entity.WcIdNameEntity;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.SPSingleton;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.JsonUtil;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.SPDefine;
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
 * @author 作者: xxblwf
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

    private OnViewClickListener onViewClickListener = new OnViewClickListener() {
        @Override public <T> void onClickAction(View v, String tag, T t) {
            if (t != null) {
                remark = (String) t;
                NOTextView.setText((CharSequence) t);
            }
            if (commonSelectInputDialog != null && commonSelectInputDialog.isShowing()) {
                commonSelectInputDialog.dismiss();
            }
        }
    };
    private String remark;
    private String listNo;//单据号
    private String lotNO;
    private int boxId;
    private Date manuDate = new Date();
    private boolean hasScanIst = false;
    //对于多台设备同时操作，这个判断没有用
//    private ArrayList<Integer> boxIDList;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_product_scan_code_box_in_layout);
        //保存所有已扫过的boxID，去重复判断
        String boxIDListString = SPSingleton.get().getString(SPDefine.KEY_code_box_id_List);
        Type type = new TypeToken<ArrayList<Integer>>() {
        }.getType();
//        boxIDList = JsonUtil.parseJsonToObject(boxIDListString,type);
//        if (boxIDList == null ){
//            boxIDList = new ArrayList<>();
//        }

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

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            @Override public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                parseContent(editable.toString());
            }
        });

        NOTextView.addTextChangedListener(new TextWatcherImpl() {
            @Override public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                listNo = editable.toString();
            }
        });
    }

    private void parseContent(String content) {
        certainWCSubProductEntity = null;
        if (TextUtils.isEmpty(content)) {
            return;
        }
        System.out.println("============ scan content = " + content);
        if (content.contains("/")) {
            String[] qrContent;
            qrContent = content.split("/");
            if (qrContent.length >= 2) {

                if (content.startsWith("Pallet") && qrContent.length == 8) {
                    boxId = Integer.parseInt(qrContent[1]);
//                    if (boxIDList.contains(boxId)){
//                        ToastUtil.showToastShort("该托盘已入库，请勿重复入库！");
//                    }else{
                        itemInfoTextView.setText(String.format("托盘ID:%s,托盘序列号：%s,客户图号：%s,箱子数量:%s",qrContent[1],qrContent[3],qrContent[5],qrContent[7]));
                        inputEditText.setText("");
                        hasScanItem = true;
//                    }
                } else if (content.startsWith("/SUB_IST_ID/") || content.startsWith("/IST_ID/")) {
                    //仓库位置码
                    scanContent = content;
                    GetProductIstAsyncTask task = new GetProductIstAsyncTask();
                    task.execute();
                }
            }
        }
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


    @Override public void onClick(View view) {
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
            handleIntoWareHouse();
//            wcNameTextView.setText("");
////            boxItemEntityList = new ArrayList<>()
//            if (boxItemEntityList != null && boxItemEntityList.size() > 0) {
//                boxItemEntityList.clear();
//            }


//            getWCList();

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
        if (hasScanItem){
            new IntentIntegrator(this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
            inputEditText.setText("");
        }else{
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

    /**
     * 获取产线（工作中心）
     */
    private void getWCList() {
        Intent intent = new Intent(this, SelectProductWCListActivity.class);
        intent.putExtra(IntentConstant.Intent_Extra_work_line_from, IntentConstant.Intent_Extra_work_line_from_product);
        startActivityForResult(intent, 200);
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
        protected void onPostExecute(Void result) {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    private class ParseProductCartonAsyncTask extends AsyncTask<String, Void, Void> {

        @Override protected Void doInBackground(String... strings) {
            return null;
        }
    }

    private class GetProductIstAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            IstPlaceEntity istPlaceEntity = WebServiceUtil.op_Check_Commit_IST_Barcode(scanContent);
            if (istPlaceEntity.getResult()) {
                thePlace = istPlaceEntity;
                if (istPlaceEntity.getResult()) {

                    if (boxItemEntityList != null && boxItemEntityList.size() > 0) {
                        boxItemEntityList.get(0).setIstName(istPlaceEntity.getIstName());
                        boxItemEntityList.get(0).setIst_ID(istPlaceEntity.getIst_ID());
                        boxItemEntityList.get(0).setSub_Ist_ID(istPlaceEntity.getSub_Ist_ID());
                    }
                }
            } else {
//                Toast.makeText(StockInActivity.this, bi.getErrorInfo(), Toast.LENGTH_LONG).show();
                ToastUtil.showToastLong(istPlaceEntity.getErrorInfo());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");

            recyclerView.setAdapter(adapter);
            //pbScan.setVisibility(View.INVISIBLE);
            inputEditText.setText("");
            hasScanIst = true;
            System.out.println("区域信息 大：" + thePlace.getBuName() + " " + thePlace.getIstName() + " " + "id" + thePlace.getIst_ID() + ":" + thePlace.getSub_Ist_ID());
            istInfoTextView.setText("区域信息 大：" + thePlace.getBuName() + " " + thePlace.getIstName() + " " + "id" + thePlace.getIst_ID() + ":" + thePlace.getSub_Ist_ID());
//            ToastUtil.showToastShort("区域信息 大：" + thePlace.getBuName() + " " + thePlace.getIstName() + " " + thePlace.getIst_ID() + ":" + thePlace.getSub_Ist_ID());
            //todo 直接执行入库登帐
            handleIntoWareHouse();

        }

        @Override
        protected void onPreExecute() {
            //pbScan.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    private void handleIntoWareHouse() {
        if (UserSingleton.get().getHRID() > 0 && !TextUtils.isEmpty(UserSingleton.get().getHRName())) {
            if (hasScanIst){
                ExeWarehouseProductInCodeBoxAsyncTask task = new ExeWarehouseProductInCodeBoxAsyncTask();
                task.execute();
            }else{
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

    int workLineId = 0;

    private class ExeWarehouseProductInCodeBoxAsyncTask extends AsyncTask<String, Void, Void> {
        WsResult ws_result;

        @Override
        protected Void doInBackground(String... params) {

            ws_result = WebServiceUtil.op_Product_Manu_In_Pallet(boxId, thePlace.getIst_ID(),remark);

            return null;
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
                    CommonUtil.ShowToast(ProductInScanCodeBoxActivity.this, ws_result.getErrorInfo(), R.mipmap.warning);

                } else {
                    //Toast.makeText(StockInActivity.this,"入库完成",Toast.LENGTH_LONG).show();
//                    CommonUtil.ShowToast(ProductScanBoxInActivity.this, "入库完成" + ws_result.getErrorInfo(), R.mipmap.smiley);
                    CommonUtil.ShowToast(ProductInScanCodeBoxActivity.this, "入库完成", R.mipmap.smiley);
                    hasScanItem = false;
                    inputEditText.setText("");
                    itemInfoTextView.setText("物料信息");
                    istInfoTextView.setText("入库区域");
                    hasScanIst = false;
//                    if (boxIDList != null){
//                        boxIDList.add(boxId);
//                    }

                }

            }

//            boxItemAdapter = new ItemProductNonTrayAdapter(ProductScanBoxInActivity.this, subProductItemEntityList);
//            mRecyclerView.setAdapter(boxItemAdapter);
            //pbScan.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPreExecute() {
            //pbScan.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

//    @Override protected void onDestroy() {
//        super.onDestroy();
//        SPSingleton.get().putString(SPDefine.KEY_code_box_id_List,JsonUtil.objectToJson(boxIDList));
//    }
}