package com.chinashb.www.mobileerp;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinashb.www.mobileerp.adapter.CommonItemBarCodeAdapter;
import com.chinashb.www.mobileerp.adapter.DeliveryOrderAdapter;
import com.chinashb.www.mobileerp.adapter.DpOrderDetailAdapter;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.DeliveryOrderBean;
import com.chinashb.www.mobileerp.bean.DpOrderDetailBean;
import com.chinashb.www.mobileerp.bean.entity.LogisticsDeliveryEntity;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CommAlertDialog;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.chinashb.www.mobileerp.widget.OnDialogViewClickListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2020/10/30 10:21 PM
 * @author 作者: liweifeng
 * @description 扫描托盘标签关联发货指令出库
 */
public class ProductSaleOutCodeBoxActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.product_out_code_box_select_plan_button) Button selectPlanButton;
    @BindView(R.id.product_out_code_box_order_textView) TextView productNonTrayWcNameTextView;
    //    @BindView(R.id.product_non_tray_scan_button) Button productNonTrayScanButton;
    @BindView(R.id.product_out_code_box_out_button) Button outButton;
    @BindView(R.id.product_out_code_box_input_EditText) EditText inputEditText;
    @BindView(R.id.product_out_code_box_delivery_order_customRecyclerView) CustomRecyclerView recyclerView;
    @BindView(R.id.product_out_code_box_select_logistics_button) Button logisticsButton;
    @BindView(R.id.product_out_code_box_sdzh_out_button) Button sdzhOutButton;
    @BindView(R.id.product_out_code_box_logistics_customer_company_textView) TextView customerCompanyTextView;
    @BindView(R.id.product_out_code_box_logistics_logistics_company_textView) TextView logisticsCompanyTextView;
    @BindView(R.id.product_out_code_box_logistics_transport_type_textView) TextView transportTypeTextView;
    @BindView(R.id.product_out_code_box_logistics_address_textView) TextView addressTextView;
    @BindView(R.id.product_out_code_box_logistics_remark_textView) TextView remarkTextView;
    @BindView(R.id.product_out_code_box_logistics_data_layout) LinearLayout logisticsDataLayout;
    @BindView(R.id.product_out_code_box_delivery_order_detail_customRecyclerView) CustomRecyclerView orderDetailRecyclerView;
    @BindView(R.id.product_out_code_box_delivery_order_info_textView) TextView orderInfoTextView;
    @BindView(R.id.product_out_code_box_logistics_info_textView) TextView logisticsInfoTextView;
    @BindView(R.id.product_out_code_box_scan_box_out_button) Button scanBoxOutButton;
    @BindView(R.id.product_out_code_box_scan_box_item_detail_customRecyclerView) CustomRecyclerView boxItemRecyclerView;
    @BindView(R.id.product_out_code_box_sdzh_scan_pallet_out_button) Button scanPalletOutButton;
    @BindView(R.id.product_out_check_product_same_button) Button checkProductSameButton;

    private DeliveryOrderBean deliveryOrderBean;
    private DeliveryOrderAdapter deliveryOrderAdapter;
    private DpOrderDetailAdapter dpOrderDetailAdapter;
    private CommonItemBarCodeAdapter commonItemBarCodeAdapter;
    private long logisticsDeliveryId = 0;

    private LogisticsDeliveryEntity logisticsDeliveryEntity;
    private long customerFacilityId = 0;
    private String customerFacilityName = "";
    private DpOrderDetailBean tempDpOrderDetailBean;
    private int boxId = 0;
    private ArrayList<Integer> box_IDList;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_out_code_box_layout);
        ButterKnife.bind(this);

        box_IDList = new ArrayList<>();
        initViews();
        setViewsListener();
    }

    private void initViews() {
        deliveryOrderAdapter = new DeliveryOrderAdapter();
        recyclerView.setAdapter(deliveryOrderAdapter);

        dpOrderDetailAdapter = new DpOrderDetailAdapter();
        orderDetailRecyclerView.setAdapter(dpOrderDetailAdapter);
        dpOrderDetailAdapter.setOnViewClickListener(new OnViewClickListener() {
            @Override public <T> void onClickAction(View v, String tag, T t) {
                if (t != null) {
                    tempDpOrderDetailBean = (DpOrderDetailBean) t;
                } else {
                    tempDpOrderDetailBean = null;
                }
            }
        });

        commonItemBarCodeAdapter = new CommonItemBarCodeAdapter();
        boxItemRecyclerView.setAdapter(commonItemBarCodeAdapter);

    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentConstant.Intent_Request_Code_Product_Out_And_Delivery_Order) {
            if (data != null) {
                deliveryOrderBean = data.getParcelableExtra(IntentConstant.Intent_product_delivery_order_bean);
                if (deliveryOrderBean != null) {
                    deliveryOrderAdapter.setOneData(deliveryOrderBean);
                    recyclerView.setVisibility(View.GONE);
                    orderInfoTextView.setText(String.format("跟踪号:%s, 精确到时:%s, 出厂日期:%s ,到达日期:%s, 客户:%s ,接收信息:%s ,特殊说明:%s ", deliveryOrderBean.getTrackNo(),
                            deliveryOrderBean.isSpecificTime() ? "是" : "否", deliveryOrderBean.getDeliveryDate(), deliveryOrderBean.getArriveDate(),
                            deliveryOrderBean.getCFChineseName(), deliveryOrderBean.getDesInfo(), deliveryOrderBean.getSpecial()));

                    GetDpOrderDetailListsAsyncTask asyncTask = new GetDpOrderDetailListsAsyncTask();
                    asyncTask.execute();
                }
            }
        } else if (requestCode == IntentConstant.Intent_Request_Code_Product_To_Logistics) {
            //                logisticsDeliveryEntity = data.getParcelableExtra(IntentConstant.Intent_Extra_logistics_entity);
            if (data != null) {
                logisticsDeliveryId = data.getLongExtra(IntentConstant.Intent_Extra_logistics_delivery_id, 0);
//                customerFacilityId = data.getLongExtra(IntentConstant.Intent_Extra_logistics_cf_id, 0);
                customerFacilityId = data.getIntExtra(IntentConstant.Intent_Extra_logistics_cf_id, 0);

                if (logisticsDeliveryId > 0) {
                    customerCompanyTextView.setText(data.getCharSequenceExtra(IntentConstant.Intent_Extra_logistics_customer_company_name));
                    logisticsCompanyTextView.setText(data.getCharSequenceExtra(IntentConstant.Intent_Extra_logistics_logistics_company));
                    addressTextView.setText(data.getCharSequenceExtra(IntentConstant.Intent_Extra_logistics_address));
                    remarkTextView.setText(data.getCharSequenceExtra(IntentConstant.Intent_Extra_logistics_remark));
                    transportTypeTextView.setText(data.getCharSequenceExtra(IntentConstant.Intent_Extra_logistics_transport_type));

                    customerFacilityName = data.getStringExtra(IntentConstant.Intent_Extra_logistics_cf_name);
//                    logisticsDataLayout.setVisibility(View.VISIBLE);
                    logisticsDataLayout.setVisibility(View.GONE);

                    logisticsInfoTextView.setText(String.format("客户公司:%s ,物流公司:%s ,收货地址:%s ,备注:%s ,运输方式:%s ,客户名称:%s ",
                            data.getCharSequenceExtra(IntentConstant.Intent_Extra_logistics_customer_company_name),
                            data.getCharSequenceExtra(IntentConstant.Intent_Extra_logistics_logistics_company),
                            data.getCharSequenceExtra(IntentConstant.Intent_Extra_logistics_address),
                            data.getCharSequenceExtra(IntentConstant.Intent_Extra_logistics_remark),
                            data.getCharSequenceExtra(IntentConstant.Intent_Extra_logistics_transport_type),
                            data.getStringExtra(IntentConstant.Intent_Extra_logistics_cf_name)));
                }
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

    private void setViewsListener() {
        selectPlanButton.setOnClickListener(this);
        logisticsButton.setOnClickListener(this);
        outButton.setOnClickListener(this);
        sdzhOutButton.setOnClickListener(this);
        scanBoxOutButton.setOnClickListener(this);
        scanPalletOutButton.setOnClickListener(this);
        checkProductSameButton.setOnClickListener(this);
    }

    @Override public void onClick(View v) {
        if (v == selectPlanButton) {
            Intent intent = new Intent(this, DeliveryOrderActivity.class);
            startActivityForResult(intent, IntentConstant.Intent_Request_Code_Product_Out_And_Delivery_Order);
//            startActivity(intent);
        } else if (v == logisticsButton) {
            Intent intent = new Intent(this, LogisticsManageActivity.class);
            intent.putExtra(IntentConstant.Intent_Extra_logistics_from,IntentConstant.Intent_Request_Code_Logistics_from_product_sale_out);
            startActivityForResult(intent, IntentConstant.Intent_Request_Code_Product_To_Logistics);
        } else if (v == outButton) {
            if (tempDpOrderDetailBean != null) {
                handleProductOut();
            } else {
                ToastUtil.showToastShort("请选择要出库的项目产品！");
            }
        } else if (v == sdzhOutButton) {
            if (deliveryOrderBean != null) {
                Intent intent = new Intent(this, SDZHHActivity.class);
                intent.putExtra(IntentConstant.Intent_Extra_do_delivery_bean, deliveryOrderBean);
                startActivity(intent);
            } else {
                ToastUtil.showToastShort("请先选择发货指令");
            }
        } else if (v == scanBoxOutButton) {
            new IntentIntegrator(this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
        }else if (v == scanPalletOutButton){
            if (deliveryOrderBean != null) {
                Intent intent = new Intent(this, SDZHScanPalletCodeActivity.class);
                intent.putExtra(IntentConstant.Intent_Extra_do_delivery_bean, deliveryOrderBean);
                startActivity(intent);
            } else {
                ToastUtil.showToastShort("请先选择发货指令");
            }
        }else if (v == checkProductSameButton){
            Intent intent = new Intent(this, ProductCheckSameActivity.class);
//            startActivityForResult(intent, IntentConstant.Intent_Request_Code_Product_Out_And_Check_Same);
            startActivity(intent);
        }
    }

    private void parseContent(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        System.out.println("============ scan content = " + content);
//        if (content.contains(",")) {
////            MB3202004010002,GD2020033100031,C00082917,ITEM2019100918157,5,20200401
//            String[] qrContent = content.split(",");
//            if (qrContent != null && qrContent.length > 5) {
//                String cartonNO = qrContent[0];
////                lotNO = qrContent[5];
//                GetMesDataAsyncTask asyncTask = new GetMesDataAsyncTask();
//                asyncTask.execute(cartonNO);
//            } else {
//                ToastUtil.showToastShort("箱码格式错误！");
//            }
//        }

        if (content.contains("/")) {
            String[] qrContent;
            qrContent = content.split("/");
            if (qrContent.length >= 2) {

                if (content.startsWith("Pallet") && qrContent.length == 8) {
                    boxId = Integer.parseInt(qrContent[1]);
//                    itemInfoTextView.setText(String.format("托盘ID:%s,托盘序列号：%s,客户图号：%s,箱子数量:%s",qrContent[1],qrContent[3],qrContent[5],qrContent[7]));
                    inputEditText.setText("");
//                    hasScanItem = true;
                    box_IDList.add(boxId);
                    handleProductOut();
                }
            }
        }
    }

    private void handleProductOut() {
//        if (TextUtils.equals(customerFacilityName, deliveryOrderBean.getCFChineseName())) {

        if (UserSingleton.get().getHRID() > 0 && !TextUtils.isEmpty(UserSingleton.get().getHRName())) {

            HandleProductOutAsyncTask outAsyncTask = new HandleProductOutAsyncTask();
//            outAsyncTask.execute(tempDpOrderDetailBean.getPSID() + "", tempDpOrderDetailBean.getDPIQuantity());
            outAsyncTask.execute();
        } else {
            CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(this)
                    .setTitle("").setMessage("您当前程序账号有误，需重新登录！")
                    .setLeftText("确定");


            builder.setOnViewClickListener(new OnDialogViewClickListener() {
                @Override
                public void onViewClick(Dialog dialog, View v, int tag) {
                    switch (tag) {
                        case CommAlertDialog.TAG_CLICK_LEFT:
                            CommonUtil.doLogout(ProductSaleOutCodeBoxActivity.this);
                            dialog.dismiss();
                            break;
                    }
                }
            });
            builder.create().show();
        }

//        } else {
//            ToastUtil.showToastLong("发货指令中客户名称与物流信息中客户名称不一致，请检查！");
//        }
    }

    private class HandleProductOutAsyncTask extends AsyncTask<String, Void, Void> {
        private WsResult result;

        @Override protected Void doInBackground(String... strings) {
//            long cfID = 0;
//            long dpi_id = 0;
////            2020-01-03T00:00:00
//            int psId = Integer.parseInt(strings[0]);
//            String qty = strings[1];
////            new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss").parse("2019-01-03 10:59:27")
//            Date deliveryDate = null;
//            try {
//                String tempDateString = deliveryOrderBean.getDeliveryDate();
//                if (tempDateString.contains("T")) {
//                    tempDateString = tempDateString.replace("T", " ");
//                }
//                deliveryDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tempDateString);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            if (deliveryDate == null) {
//                deliveryDate = new Date();
//            }
//            result = WebServiceUtil.op_Product_Manu_Out_Not_Pallet(deliveryDate, customerFacilityId, deliveryOrderBean.getCFChineseName(), deliveryOrderBean.getTrackNo(), logisticsDeliveryId, dpi_id, deliveryOrderBean.getDOID(), psId, qty);
            result = WebServiceUtil.op_Pro_Pallet_Sale_Out_Mobile(deliveryOrderBean.getDOID(),logisticsDeliveryId,box_IDList,deliveryOrderBean.getTrackNo(),deliveryOrderBean.getCFChineseName());
//            result = WebServiceUtil.op_Product_Manu_Out_Not_Pallet(new Date(), 68, "一汽奔腾轿车有限公司", "20200328", 102838, 0, 7426);
            return null;
        }

        @Override protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (result != null) {
                if (result.getResult()) {
                    logisticsDeliveryId = result.getID();
                    ToastUtil.showToastShort("出库成功！");
                    //如果是 同PC端的出库，一般不用
                    if (outButton.getVisibility() == View.VISIBLE && tempDpOrderDetailBean != null) {
                        dpOrderDetailBeanList.remove(tempDpOrderDetailBean);
                        dpOrderDetailAdapter.setData(dpOrderDetailBeanList);
                        tempDpOrderDetailBean = null;
                    } else {
                        //这是扫描箱码出库的模式,需要作些箱号相关的数据库更新

                        //同时刷新显示列表
                        GetDpOrderDetailListsAsyncTask asyncTask = new GetDpOrderDetailListsAsyncTask();
                        asyncTask.execute();

                    }
                } else {
                    ToastUtil.showToastShort("出库失败 : " + result.getErrorInfo());
                }
            }
        }
    }

    private List<DpOrderDetailBean> dpOrderDetailBeanList;

    private class GetDpOrderDetailListsAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            //// TODO: 2019/12/20  注意这里的第二个参数%s有所修改
//            String sql = String.format("Select D.* FROM DPI As D With (NoLock) Where D.Deleted=0 And D.Do_ID= %s", deliveryOrderBean.getDOID());
            String sql = String.format("  Select Program.Program_Name, D.Product_ID,D.PS_ID,product.Abb,product.Product_PartNo,product.Product_Version," +
                    "D.DPI_Quantity FROM DPI As D With (NoLock) left join product on product.product_id = D.Product_ID " +
                    "Left Join Program On Program.Program_ID = Dbo.Get_Program_ByProduct(D.CF_ID,D.Product_ID) " +
                    "Where D.Deleted=0 And D.Do_ID=%s", deliveryOrderBean.getDOID());
            WsResult result = WebServiceUtil.getDataTable(sql);
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                Gson gson = new Gson();
                dpOrderDetailBeanList = gson.fromJson(jsonData, new TypeToken<List<DpOrderDetailBean>>() {
                }.getType());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (dpOrderDetailBeanList != null && dpOrderDetailBeanList.size() > 0) {
                dpOrderDetailAdapter.setData(dpOrderDetailBeanList);
            }
        }

    }

//    private class GetMesDataAsyncTask extends AsyncTask<String, Void, String> {
//
//        @Override protected String doInBackground(String... strings) {
//            if (strings == null || strings.length == 0) {
//                return null;
//            }
//            String cartonNO = strings[0];
//            WsResult result =
////            MESWebServiceUtil.GetSaveFinishedProductCodeDataByMes("XH1910130001");
////                    WebServiceUtil.GetSaveFinishedProductCodeDataByMes("XH1910130001");
//                    WebServiceUtil.GetSaveFinishedProductCodeDataByMes(cartonNO);
//            return result.getErrorInfo();
//        }
//
//        @Override protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            System.out.println("=============================== result = " + result);
//            MESDataEntity mesDataEntity = JsonUtil.parseJsonToObject(result, MESDataEntity.class);
//            MESInnerDataEntity mesInnerDataEntity;
//            if (mesDataEntity.getCode() == 0) {//表示成功
//                String tempJson = mesDataEntity.getMessage().replace("[", "").replace("]", "");
//                mesInnerDataEntity = JsonUtil.parseJsonToObject(tempJson, MESInnerDataEntity.class);
//                System.out.println("=============================== " + mesInnerDataEntity.getItemID() + " " + mesInnerDataEntity.getItemUnit());
//
//                boxId = mesInnerDataEntity.getBoxId();
//
////                result = WebServiceUtil.op_Product_Manu_Out_Not_Pallet(deliveryDate, customerFacilityId, deliveryOrderBean.getCFChineseName(), deliveryOrderBean.getTrackNo(), logisticsDeliveryId, dpi_id, deliveryOrderBean.getDOID(), tempDpOrderDetailBean.getPSID(), tempDpOrderDetailBean.getDPIQuantity());
//
//                if (logisticsDeliveryId == 0) {
//                    ToastUtil.showToastShort("未选择物流信息！");
//                    return;
//                }
//
////                Date deliveryDate = null;
////                try {
////                    String tempDateString = deliveryOrderBean.getDeliveryDate();
////                    if (tempDateString.contains("T")) {
////                        tempDateString = tempDateString.replace("T", " ");
////                    }
////                    deliveryDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tempDateString);
////                } catch (ParseException e) {
////                    e.printStackTrace();
////                }
////                if (deliveryDate == null) {
////                    deliveryDate = new Date();
////                }
////                 WebServiceUtil.op_Product_Manu_Out_Not_Pallet(deliveryDate, customerFacilityId, deliveryOrderBean.getCFChineseName(), deliveryOrderBean.getTrackNo(), logisticsDeliveryId, 0, deliveryOrderBean.getDOID(), mesInnerDataEntity.getPSID(), mesInnerDataEntity.getQty() + "");
//
//                if (UserSingleton.get().getHRID() > 0 && !TextUtils.isEmpty(UserSingleton.get().getHRName())) {
//                    HandleProductOutAsyncTask outAsyncTask = new HandleProductOutAsyncTask();
////                    outAsyncTask.execute(mesInnerDataEntity.getPSID() + "", mesInnerDataEntity.getQty() + "");
//                    outAsyncTask.execute();
//                } else {
//                    CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(ProductSaleOutCodeBoxActivity.this)
//                            .setTitle("").setMessage("您当前程序账号有误，需重新登录！")
//                            .setLeftText("确定");
//
//
//                    builder.setOnViewClickListener(new OnDialogViewClickListener() {
//                        @Override
//                        public void onViewClick(Dialog dialog, View v, int tag) {
//                            switch (tag) {
//                                case CommAlertDialog.TAG_CLICK_LEFT:
//                                    CommonUtil.doLogout(ProductSaleOutCodeBoxActivity.this);
//                                    dialog.dismiss();
//                                    break;
//                            }
//                        }
//                    });
//                    builder.create().show();
//                }
//
//
////                if (mesInnerDataEntity.getPSID() != )
//
//
////                String tempString;
////                if (!TextUtils.isEmpty(mesInnerDataEntity.getDateString())){
////                    if (mesInnerDataEntity.getDateString().contains(".")){
////                        tempString = mesInnerDataEntity.getDateString().split(".")[0];
////                    }
////                    tempString = mesInnerDataEntity.getDateString();
////                    try {
////                        manuDate = UnitFormatUtil.sdf_YMDHMS.parse(tempString);
////                    } catch (ParseException e) {
////                        manuDate = new Date();
////                        e.printStackTrace();
////                    }
////
////                }
//
//
////                BoxItemEntity boxItemEntity = new BoxItemEntity();
////                boxItemEntity.setItemName(mesInnerDataEntity.getProductChineseName() + "@" + mesInnerDataEntity.getProductDrawNo() + "@" + mesInnerDataEntity.getProductVersion());
////                boxItemEntity.setQty(mesInnerDataEntity.getQty());
////                boxItemEntity.setBuName(UserSingleton.get().getUserInfo().getBu_Name());
////
////                boxItemEntity.setLotNo(lotNO);
////                boxItemEntity.set
//
//
////                        WCSubProductItemEntity itemEntity = new WCSubProductItemEntity();
////                        itemEntity.setWcSubProductEntity(entity);
////                        itemEntity.setSelect(true);
////                        itemEntity.setLotNo(lotNo);
////                        itemEntity.setQty(qty);
////                        itemEntity.setBuName(UserSingleton.get().getUserInfo().getBu_Name());
//
////                boxItemEntityList.add(boxItemEntity);
//////                        deliveryOrderAdapter = new ItemProductNonTrayAdapter(ProductScanBoxInActivity.this, boxItemEntityList);
////                deliveryOrderAdapter.setData(boxItemEntityList);
//                inputEditText.setText("");
//                //// TODO: 2019/12/20 去掉扫描枪几个字
//                inputEditText.setHint("请继续扫描");
//
//
//            } else {
//                ToastUtil.showToastLong("接口请求数据错误！");
//            }
//        }
//    }
}
