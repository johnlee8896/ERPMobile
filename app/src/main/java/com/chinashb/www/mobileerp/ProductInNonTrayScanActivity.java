package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chinashb.www.mobileerp.basicobject.IstPlaceEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.entity.MESDataEntity;
import com.chinashb.www.mobileerp.bean.entity.MESInnerDataEntity;
import com.chinashb.www.mobileerp.bean.entity.WCSubProductEntity;
import com.chinashb.www.mobileerp.bean.entity.WCSubProductItemEntity;
import com.chinashb.www.mobileerp.bean.entity.WcIdNameEntity;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.JsonUtil;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.utils.UnitFormatUtil;
import com.chinashb.www.mobileerp.widget.CommonSelectInputDialog;
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
 * @date 创建时间 2019/12/18 15:35
 * @author 作者: xxblwf
 * @description 成品非托盘入库,扫描箱号二维码
 */

public class ProductInNonTrayScanActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.product_non_tray_scan_button) Button scanItemButton;
    @BindView(R.id.product_non_tray_scan_area_button) Button scanAreaButton;
    @BindView(R.id.product_non_tray_warehouse_in_button) Button warehouseInButton;
    @BindView(R.id.product_non_tray_input_EditText) EditText inputEditText;
    //    @BindView(R.id.product_non_tray_tv_item_name_col) TextView TvItemNameCol;
//    @BindView(R.id.product_non_tray_tv_bu_name_col) TextView TvBuNameCol;
//    @BindView(R.id.product_non_tray_tv_inv_in_lotno) TextView TvInvInLotno;
//    @BindView(R.id.product_non_tray_tv_qty_col) TextView TvQtyCol;
//    @BindView(R.id.product_non_tray_tv_ist_name_col) TextView TvIstNameCol;
//    @BindView(R.id.product_non_tray_tv_inv_in_selected) TextView TvInvInSelected;
    @BindView(R.id.product_non_tray_recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.product_non_tray_select_wc_button) Button selectWcButton;
    @BindView(R.id.product_non_tray_wc_name_textView) TextView wcNameTextView;
    @BindView(R.id.product_non_tray_select_NO_button) Button selectNOButton;
    @BindView(R.id.product_non_tray_NO_textView) TextView NOTextView;

    private WcIdNameEntity wcIdNameEntity;
    private String scanContent;
    private List<WCSubProductEntity> subProductEntityList;
    private List<WCSubProductItemEntity> subProductItemEntityList;
    private WCSubProductEntity certainWCSubProductEntity;
    private ItemProductNonTrayAdapter boxItemAdapter;

    private List<String> noList;
    private IstPlaceEntity thePlace;
    private CommonSelectInputDialog commonSelectInputDialog;
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

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_in_no_tray_scan_layout);
        ButterKnife.bind(this);
        setViewsListener();
        initView();
    }

    private void initView() {
        subProductItemEntityList = new ArrayList<>();
        boxItemAdapter = new ItemProductNonTrayAdapter(this, subProductItemEntityList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        mRecyclerView.setAdapter(boxItemAdapter);
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
        scanItemButton.setOnClickListener(this);
        scanAreaButton.setOnClickListener(this);
        warehouseInButton.setOnClickListener(this);
        selectNOButton.setOnClickListener(this);
        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                parseContent(editable.toString());
            }
        });

        NOTextView.addTextChangedListener(new TextWatcherImpl(){
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
        // V5/B/54/PS/10475/L/191217/LQ/0/Qty/120
        if (content.contains("/")) {
            String[] qrContent;
            qrContent = content.split("/");
            if (qrContent.length >= 2) {
//                    String qrTitle = qrContent[0];
//                    if (!qrTitle.equals("")) {
//                        if (qrTitle.equals("V5")) {
//                            //成品 物料码
//                            scanContent = content;
////                            GetProductItemQRCodeAsyncTask task = new GetProductItemQRCodeAsyncTask;
////                            task.execute();
//                            if (qrContent[1].startsWith("B")){
//                                String BContent = qrContent[1].split("/")
//                            }
//                        }
//                    }
                if (content.startsWith("XH")){
                    if (content.contains(",")){
                        String cartNO = content.split(",")[0];

                    }
                }else if (content.startsWith("V5")) {
                    int buId = Integer.parseInt(getParsedString(content, "/B/", "/PS/"));
                    if (buId == UserSingleton.get().getUserInfo().getBu_ID()) {
                        int psId = Integer.parseInt(getParsedString(content, "/PS/", "/L/"));
                        String lotNo = getParsedString(content, "/L/", "/LQ/");
                        int qty = Integer.parseInt(getParsedString(content, "/Qty/", ""));
                        boolean hasThisItem = false;
                        if (subProductItemEntityList == null ){
                            return;
                        }
                        for (WCSubProductEntity entity : subProductEntityList) {
                            if (entity.getPsId() == psId) {
                                hasThisItem = true;
                                certainWCSubProductEntity = entity;
                                WCSubProductItemEntity itemEntity = new WCSubProductItemEntity();
                                itemEntity.setWcSubProductEntity(entity);
                                itemEntity.setSelect(true);
                                itemEntity.setLotNo(lotNo);
                                itemEntity.setQty(qty);
                                itemEntity.setBuName(UserSingleton.get().getUserInfo().getBu_Name());

                                subProductItemEntityList.add(itemEntity);
                                boxItemAdapter = new ItemProductNonTrayAdapter(ProductInNonTrayScanActivity.this, subProductItemEntityList);
                                mRecyclerView.setAdapter(boxItemAdapter);
                                inputEditText.setText("");
                                //// TODO: 2019/12/20 去掉扫描枪几个字
                                inputEditText.setHint("请继续扫描");
                                break;
                            }
                        }
                        if (!hasThisItem) {
                            ToastUtil.showToastShort("该产品不属于此产线！");
                        }
                    } else {
                        ToastUtil.showToastShort("该物料码不属于本车间！");
                        return;
                    }
                }

                if (content.startsWith("/SUB_IST_ID/") || content.startsWith("/IST_ID/")) {
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

    private  String getParsedString(String code,String part,String nextPart){
        if (!nextPart.isEmpty()){
            int p = code.indexOf(part) + part.length();
            int q = code.indexOf(nextPart);
            return code.substring(p,q );
        }else{
            int p = code.indexOf(part) + part.length();
            return code.substring(p);
        }
    }

    private void getMESData(){
        GetMesDataAsyncTask asyncTask = new GetMesDataAsyncTask();
        asyncTask.execute();
    }

    @Override public void onClick(View view) {
        if (view == selectWcButton) {
            getWCList();
        } else if (view == scanItemButton) {
            if (TextUtils.isEmpty(wcNameTextView.getText())){
                ToastUtil.showToastShort("请先选择产线");
                return;
            }
            new IntentIntegrator(this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
        } else if (view == scanAreaButton) {
            handleScanArea();
        } else if (view == warehouseInButton) {
//            handleIntoWareHouse();
//            wcNameTextView.setText("");
////            subProductItemEntityList = new ArrayList<>()
//            if (subProductItemEntityList != null && subProductItemEntityList.size() > 0) {
//                subProductItemEntityList.clear();
//            }


            getWCList();

        } else if (view == selectNOButton) {
            handleSelectNO();
        }
    }

    private void handleSelectNO() {
        if (commonSelectInputDialog == null) {
            commonSelectInputDialog = new CommonSelectInputDialog(ProductInNonTrayScanActivity.this);
        }
        commonSelectInputDialog.show();
        commonSelectInputDialog.setOnViewClickListener(onViewClickListener);
        commonSelectInputDialog.setTitle("请选择或添加单据号");
        commonSelectInputDialog.refreshContent(getNOList());
    }

    private void handleScanArea() {
        if (subProductItemEntityList.size() > 0) {
            int selectedcount = 0;
            for (int i = 0; i < subProductItemEntityList.size(); i++) {
                if (subProductItemEntityList.get(i).isSelect()) {
                    selectedcount++;
                }
            }
            if (selectedcount > 0) {
                new IntentIntegrator(ProductInNonTrayScanActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
                inputEditText.setText("");
            } else {
                ToastUtil.showToastShort("请选择成品箱条目！");
            }

        } else {
            ToastUtil.showToastShort("没有物品条码或仓库位置码没有成功，请重新扫描！");
        }
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
        intent.putExtra(IntentConstant.Intent_Extra_work_line_from,IntentConstant.Intent_Extra_work_line_from_product);
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
                    //// TODO: 2019/12/27
//                    for (int i = 0; i < boxItemEntityList.size(); i++) {
//                        if (boxItemEntityList.get(i).getSelect()) {
//                            boxItemEntityList.get(i).setIstName(istPlaceEntity.getIstName());
//                            boxItemEntityList.get(i).setIst_ID(istPlaceEntity.getIst_ID());
//                            boxItemEntityList.get(i).setSub_Ist_ID(istPlaceEntity.getSub_Ist_ID());
//                        }
//                    }
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

            mRecyclerView.setAdapter(boxItemAdapter);
            //pbScan.setVisibility(View.INVISIBLE);
            inputEditText.setText("");
            System.out.println("区域信息 大：" + thePlace.getBuName() + " " + thePlace.getIstName() + " " + "id" + thePlace.getIst_ID() + ":" + thePlace.getSub_Ist_ID());
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
        if (subProductItemEntityList.size() > 0) {
            int selectedcount = 0;
            for (int i = 0; i < subProductItemEntityList.size(); i++) {
                if (subProductItemEntityList.get(i).isSelect()) {
                    //// TODO: 2019/12/27
//                    if (subProductItemEntityList.get(i).getIst_ID() == 0) {
                    if (thePlace.getIst_ID() == 0) {
//                            CommonUtil.ShowToast(StockInActivity.this, "还没有扫描库位", R.mipmap.warning, Toast.LENGTH_SHORT);
                        ToastUtil.showToastLong("还没有扫描库位");
                        return;
                    }
                    selectedcount++;
                }

            }
            if (selectedcount > 0) {
                ExeWarehouseProductInAsyncTask task = new ExeWarehouseProductInAsyncTask();
                task.execute();
            }

        } else {
            //// TODO: 2019/7/10  这里应控件按钮的可用性
            ToastUtil.showToastShort("没有物品条码或仓库位置码没有成功，请重新扫描！");
        }
    }

    private class ExeWarehouseProductInAsyncTask extends AsyncTask<String, Void, Void> {
        WsResult ws_result;

        @Override
        protected Void doInBackground(String... params) {

            List<WCSubProductItemEntity> SelectList;
            SelectList = new ArrayList<>();

            for (int i = 0; i < subProductItemEntityList.size(); i++) {
                if (subProductItemEntityList.get(i).isSelect()) {
                    SelectList.add(subProductItemEntityList.get(i));
                }
            }

            int count = 0;
            int selectedCount = SelectList.size();
            while (count < selectedCount && SelectList.size() > 0) {
                //todo  这里取的是0，验证多个是否成功
                WCSubProductItemEntity boxItemEntity = SelectList.get(0);
//                String sql = String.format("insert into Ist_SubIst_ManuLot (IST_ID,Sub_IST_ID,Item_ID,IV_ID,LotID,Company_ID,Bu_ID,ManuLotNo，SendToWarehouseTime) values (%d,%d,%d,%d,%d,%d,%d,%s,%s)",
//                String sql = String.format("insert into Ist_SubIst_ManuLot (IST_ID,Sub_IST_ID,Item_ID,IV_ID,LotID,Company_ID,Bu_ID,ManuLotNo) values (%d,%d,%d,%d,%d,%d,%d,%s)",
//                        boxItemEntity.getIst_ID() ,boxItemEntity.getSub_Ist_ID(),boxItemEntity.getItem_ID(),boxItemEntity.getIV_ID(),boxItemEntity.getLotID(),
//                        UserSingleton.get().getUserInfo().getCompany_ID(),UserSingleton.get().getUserInfo().getBu_ID(),
////                        !TextUtils.isEmpty(boxItemEntity.getManuLotNo()) ? boxItemEntity.getManuLotNo() : boxItemEntity.getLotNo());
////                        boxItemEntity.getLotNo(),UnitFormatUtil.formatTimeToSecond(System.currentTimeMillis()));
//                        boxItemEntity.getLotNo());

//                ws_result = WebServiceUtil.op_Commit_DS_Item_Income_To_Warehouse(boxItemEntity,sql);

//                ws_result = WebServiceUtil.op_Product_Manu_In_Not_Pallet(wcIdNameEntity,boxItemEntity,new Date(),
//                        listNo,"李伟锋成品入库测试",
//                        13269,"lwf",
//                        thePlace.getIst_ID(),thePlace.getSub_Ist_ID());

                ws_result = WebServiceUtil.op_Product_Manu_In_Not_Pallet(wcIdNameEntity,boxItemEntity,new Date(),
                        listNo,new Date() ,"李伟锋成品入库测试",
                        13269,"lwf",
                        thePlace.getIst_ID(),thePlace.getSub_Ist_ID(),boxItemEntity.getQty());
                if (ws_result.getResult()) {
                    //添加库位与manuLot的关联
//                    addIstSubIstManuLotRelation(boxItemEntity);
                    subProductItemEntityList.remove(boxItemEntity);
                    SelectList.remove(boxItemEntity);
                }
//                else{
////                    ToastUtil.showToastLong("入库失败！");
//                    System.out.println("==========================入库失败！");
//                }

                count++;
            }

            return null;
        }

//        private void addIstSubIstManuLotRelation(BoxItemEntity boxItemEntity) {
//            if (boxItemEntity != null) {
//                String sql = String.format("insert into Ist_SubIst_ManuLot (IST_ID,Sub_IST_ID,Item_ID,IV_ID,LotID,Company_ID,Bu_ID,ManuLotNo) values (%d,%d,%d,%d,%d,%d,%d,%s)",
//                        boxItemEntity.getIst_ID() ,boxItemEntity.getSub_Ist_ID(),boxItemEntity.getItem_ID(),boxItemEntity.getIV_ID(),boxItemEntity.getLotID(),
//                        UserSingleton.get().getUserAllInfoEntity().getCompanyID(),UserSingleton.get().getUserInfo().getBu_ID(),boxItemEntity.getManuLotNo());
//
//
//            }
//        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");

            if (ws_result != null) {
                if (!ws_result.getResult()) {
                    //Toast.makeText(StockInActivity.this,ws_result.getErrorInfo(),Toast.LENGTH_LONG).show();
                    CommonUtil.ShowToast(ProductInNonTrayScanActivity.this, ws_result.getErrorInfo(), R.mipmap.warning);

                } else {
                    //Toast.makeText(StockInActivity.this,"入库完成",Toast.LENGTH_LONG).show();
                    CommonUtil.ShowToast(ProductInNonTrayScanActivity.this, "入库完成" + ws_result.getErrorInfo(), R.mipmap.smiley);
                }

            }

            boxItemAdapter = new ItemProductNonTrayAdapter(ProductInNonTrayScanActivity.this, subProductItemEntityList);
            mRecyclerView.setAdapter(boxItemAdapter);
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

    private class GetMesDataAsyncTask extends AsyncTask<String, Void, String> {

        @Override protected String doInBackground(String... strings) {
            WsResult result =
//            MESWebServiceUtil.GetSaveFinishedProductCodeDataByMes("XH1910130001");
                    WebServiceUtil.GetSaveFinishedProductCodeDataByMes("XH1910130001");
            return result.getErrorInfo();
        }

        @Override protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println("=============================== result = " + result);
            MESDataEntity mesDataEntity = JsonUtil.parseJsonToObject(result, MESDataEntity.class);

            if (mesDataEntity.getCode() == 0){//表示成功
                System.out.println(mesDataEntity.getCode());
                String tempJson = mesDataEntity.getMessage().replace("[","").replace("]","");
                MESInnerDataEntity mesInnerDataEntity = JsonUtil.parseJsonToObject(tempJson, MESInnerDataEntity.class);
                System.out.println("=============================== " + mesInnerDataEntity.getItemID() + " " +mesInnerDataEntity.getItemUnit());
            }else{
                ToastUtil.showToastLong("接口请求数据错误！");
            }
        }
    }




}

