package com.chinashb.www.mobileerp;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinashb.www.mobileerp.adapter.ItemTextViewAdapter;
import com.chinashb.www.mobileerp.adapter.SDZHOrderBoxDetailAdapter;
import com.chinashb.www.mobileerp.adapter.SDZHOrderDetailAdapter;
import com.chinashb.www.mobileerp.adapter.SDZHSinglePartDetailAdapter;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.DeliveryOrderBean;
import com.chinashb.www.mobileerp.bean.SDZHBoxDetailBean;
import com.chinashb.www.mobileerp.bean.SDZHDeliveryOrderNumberBean;
import com.chinashb.www.mobileerp.bean.SDZHDeliveryOrderNumberDetailBean;
import com.chinashb.www.mobileerp.bean.SDZHSinglePartBean;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.AppUtil;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CommAlertDialog;
import com.chinashb.www.mobileerp.widget.CommProgressDialog;
import com.chinashb.www.mobileerp.widget.CommonSelectInputDialog;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.chinashb.www.mobileerp.widget.EmptyLayoutManageView;
import com.chinashb.www.mobileerp.widget.OnDialogViewClickListener;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2020/4/26 14:12
 * @author 作者: liweifeng
 * @description 三点照合的页面
 */
public class SDZHScanPalletCodeActivity extends BaseActivity implements View.OnClickListener {
    public static final int SCAN_ORDER_NUMBER = 1;
    public static final int SCAN_BOX_CODE = 2;
    //    public static final int SCAN_PALLET_CODE = 3;
    public static final int SCAN_PALLET_CODE = 3;
    @BindView(R.id.sdzh_scan_pallet_scan_order_number_button) TextView orderNumberButton;
    @BindView(R.id.sdzh_scan_pallet_scan_box_bar_button) TextView boxBarButton;
    @BindView(R.id.sdzh_scan_pallet_scan_single_part_bar_button) TextView singlePartBarButton;
    @BindView(R.id.sdzh_scan_pallet_remove_single_part_bar_button) TextView removeSinglePartBarButton;
    @BindView(R.id.sdzh_scan_pallet_send_order_number_textView) TextView orderNumberTextView;
    @BindView(R.id.sdzh_scan_pallet_order_number_recyclerView) CustomRecyclerView orderNumberRecyclerView;
    @BindView(R.id.sdzh_scan_pallet_data_layout) LinearLayout dataLayout;
    @BindView(R.id.sdzh_scan_pallet_empty_layoutView) EmptyLayoutManageView emptyLayoutView;
    @BindView(R.id.sdzh_scan_pallet_inner_empty_layoutView) EmptyLayoutManageView innerEmptyLayoutView;
    @BindView(R.id.sdzh_scan_pallet_current_info_textView) TextView infoTextView;
    @BindView(R.id.sdzh_scan_pallet_root_data_layout) LinearLayout rootDataLayout;
    @BindView(R.id.sdzh_scan_pallet_root_empty_layoutView) EmptyLayoutManageView rootEmptyLayoutView;
    @BindView(R.id.sdzh_scan_pallet_box_title_textView) TextView boxTitleTextView;
    @BindView(R.id.sdzh_scan_pallet_box_recyclerView) CustomRecyclerView boxRecyclerView;
    @BindView(R.id.sdzh_scan_pallet_single_part_title_textView) TextView singlePartTitleTextView;
    @BindView(R.id.sdzh_scan_pallet_single_part_recyclerView) CustomRecyclerView singlePartRecyclerView;
    @BindView(R.id.sdzh_scan_pallet_order_No_data_layout) LinearLayout orderNoDataLayout;
    @BindView(R.id.sdzh_scan_pallet_input_EditeText) EditText inputEditText;
    @BindView(R.id.sdzh_scan_pallet_scan_order_out_button) TextView outButton;
    @BindView(R.id.sdzh_scan_pallet_scan_order_logistics_button) TextView logisticsButton;
    @BindView(R.id.sdzh_scan_pallet_logistics_customer_company_textView) TextView customerCompanyTextView;
    @BindView(R.id.sdzh_scan_pallet_logistics_logistics_company_textView) TextView logisticsCompanyTextView;
    @BindView(R.id.sdzh_scan_pallet_logistics_transport_type_textView) TextView transportTypeTextView;
    @BindView(R.id.sdzh_scan_pallet_logistics_address_textView) TextView addressTextView;
    @BindView(R.id.sdzh_scan_pallet_logistics_remark_textView) TextView remarkTextView;
    @BindView(R.id.sdzh_scan_pallet_logistics_data_layout) LinearLayout logisticsDataLayout;
    @BindView(R.id.sdzh_scan_pallet_orderNo_outer_scroll_RecyclerView) CustomRecyclerView orderNoOuterScrollRecyclerView;
    @BindView(R.id.sdzh_scan_pallet_scan_pallet_bar_button) TextView scanPalletBarButton;
    private String do_ID;
    private SDZHOrderDetailAdapter sdzhOrderDetailAdapter;
    private SDZHOrderBoxDetailAdapter boxDetailAdapter;
    private SDZHSinglePartDetailAdapter singlePartDetailAdapter;
    private int currentScanState = 1;

    private String selectBoxNo;
    private SDZHBoxDetailBean boxDetailBean;
    private String selectOrderNO;
    private boolean hasSingPartSaved = false;
    private boolean isSinglePartScanning = false;


    private SDZHDeliveryOrderNumberDetailBean sdzhDeliveryOrderNumberDetailBean;
    //    private SDZHBoxDetailBean sdzhBoxDetailBean;
    private SDZHSinglePartBean sdzhSinglePartBean;

    private boolean hasClickSelect = false;
    private int selectRemovePosition = 0;
    private DeliveryOrderBean deliveryOrderBean;
    private ItemTextViewAdapter itemTextViewAdapter;

    private SDZHDeliveryOrderNumberBean orderNumberBean;

    private OnViewClickListener onViewClickListener = new OnViewClickListener() {
        @Override
        public <T> void onClickAction(View v, String tag, T t) {
            if (!TextUtils.isEmpty(tag)) {
                selectRemovePosition = Integer.parseInt(tag);
            } else {
                selectRemovePosition = 0;
            }
            if (t != null) {
                hasClickSelect = true;
                if (t instanceof SDZHDeliveryOrderNumberDetailBean) {
                    sdzhDeliveryOrderNumberDetailBean = (SDZHDeliveryOrderNumberDetailBean) t;
                    selectOrderNO = sdzhDeliveryOrderNumberDetailBean.getOrderNo();
                    refreshCurrentInfo(sdzhDeliveryOrderNumberDetailBean.getOrderNo(), "", 0, 0, 0);
                    new GetOrderDetailAsyncTask().execute(sdzhDeliveryOrderNumberDetailBean.getOrderNo());
                } else if (t instanceof SDZHBoxDetailBean) {
//                    sdzhBoxDetailBean = (SDZHBoxDetailBean) t;
                    boxDetailBean = (SDZHBoxDetailBean) t;
                    selectBoxNo = boxDetailBean.getBoxCode();
                    new GetSinglePartDetailAsyncTask().execute(selectBoxNo);
                } else if (t instanceof SDZHSinglePartBean) {
                    sdzhSinglePartBean = (SDZHSinglePartBean) t;
                }
            } else {
                //说明 是取消选中
                hasClickSelect = false;
            }
        }
    };
    private long logisticsDeliveryId = 0;
    private long customerFacilityId = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdzh_scan_pallet_layout);
        ButterKnife.bind(this);

        deliveryOrderBean = getIntent().getParcelableExtra(IntentConstant.Intent_Extra_do_delivery_bean);
//        do_ID = getIntent().getStringExtra(IntentConstant.Intent_Extra_do_id);
        do_ID = deliveryOrderBean.getDOID() + "";

//        do_ID = "6023";
//        do_ID = "7321";
//        do_ID = "7649";
//        do_ID = "7651";
        if (!TextUtils.isEmpty(do_ID)) {
            new GetDeliveryOrderAsyncTask().execute(do_ID);
        }

        //// TODO: 2020/2/26 放这里报错，还没加载？没有
        sdzhOrderDetailAdapter = new SDZHOrderDetailAdapter();
        orderNumberRecyclerView.setAdapter(sdzhOrderDetailAdapter);
        sdzhOrderDetailAdapter.setOnViewClickListener(onViewClickListener);

        boxDetailAdapter = new SDZHOrderBoxDetailAdapter();
        boxRecyclerView.setAdapter(boxDetailAdapter);
        boxDetailAdapter.setOnViewClickListener(onViewClickListener);

        singlePartDetailAdapter = new SDZHSinglePartDetailAdapter();
        singlePartRecyclerView.setAdapter(singlePartDetailAdapter);
        singlePartDetailAdapter.setOnViewClickListener(onViewClickListener);

        itemTextViewAdapter = new ItemTextViewAdapter();
        orderNoOuterScrollRecyclerView.setAdapter(itemTextViewAdapter);
        itemTextViewAdapter.setOnViewClickListener(new OnViewClickListener() {
            @Override public <T> void onClickAction(View v, String tag, T t) {
                if (t != null) {
                    orderNumberBean = (SDZHDeliveryOrderNumberBean) t;
                    if (orderNumberBean != null) {
                        new GetOrderDetailAsyncTask().execute(orderNumberBean.getOrderNo());
                    }
                }

            }
        });

        setButtonsNotEnable();
        setViewsListener();
        refreshCurrentInfo("", "", 0, 0, 0);
        AppUtil.forceHideInputMethod(SDZHScanPalletCodeActivity.this);

//            new GetDeliveryOrderAsyncTask().execute("6777");
//        new GetOrderDetailAsyncTask().execute("CLD0A252A12I1A");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentConstant.Intent_Request_Code_Product_To_Logistics) {
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

//                    customerFacilityName = data.getStringExtra(IntentConstant.Intent_Extra_logistics_cf_name);
                    logisticsDataLayout.setVisibility(View.VISIBLE);
                }
            }
        } else {

            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (!TextUtils.isEmpty(result.getContents())) {
                    switch (currentScanState) {
                        case SCAN_ORDER_NUMBER:
                            handleOrderNOScan(result.getContents());
                            break;
                        case SCAN_BOX_CODE:
                            handleBoxScan(result.getContents());
                            break;
                        case SCAN_PALLET_CODE:
                            handleSinglePartScan(result.getContents());
                            break;
                    }

//                parseScanResult(result.getContents());
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
                ToastUtil.showToastShort("扫描内容为空！");
            }
        }

    }

    private CommonSelectInputDialog inputDialog;
    private String inputNumber;
    private OnViewClickListener onInputDialogViewClickListener = new OnViewClickListener() {
        @Override public <T> void onClickAction(View v, String tag, T t) {
            if (t != null) {
                inputNumber = (String) t;
                int number = 0;
                try {
                    number = Integer.parseInt(inputNumber);
                } catch (Exception e) {
                    number = 0;
                }
                if (number == totalRetNumber){
//                    ToastUtil.showToastShort("该箱已出满");
                    refreshCurrentInfo(selectOrderNO,selectBoxNo,boxDetailBean.getBoxQty(),number,number);
                }else if (number > totalRetNumber){
                    ToastUtil.showToastShort("当前出库数大于剩余数");
                }else{
                    refreshCurrentInfo(selectOrderNO, selectBoxNo, boxDetailBean.getBoxQty(), singleOriginalList.size(), number );
                    //// TODO: 2020/4/26
//                    HandleSDZHProductOutAsyncTask outAsyncTask = new HandleSDZHProductOutAsyncTask();
//                    outAsyncTask.execute(number + "");
                }
//                remarkTextView.setText((CharSequence) t);
            }
            if (inputDialog != null && inputDialog.isShowing()) {
                inputDialog.dismiss();
            }
        }
    };

    private void showInputDialog() {
        if (inputDialog == null) {
            inputDialog = new CommonSelectInputDialog(SDZHScanPalletCodeActivity.this);
        }
        inputDialog.show();
        inputDialog.setRecyclerViewGone();
        inputDialog.setInputTextHint("请填写出库数量！");
        inputDialog.setInputDialogTitle("提示");
        inputDialog.setOnViewClickListener(onInputDialogViewClickListener);
    }


    private String[] splitStringArray;
    private List<SDZHSinglePartBean> singleOriginalList;

    private void handleSinglePartScan(String outCode) {
//        outCode = new String(outCode,"gb2312");
        System.out.println("========= outCode = " + outCode);
//        List<SDZHSinglePartBean> singOriginalList = singlePartDetailAdapter.getList();
//        outCode = outCode.replace("\n","");
        if (outCode.contains("\r\n")) {
            outCode = outCode.replace("\r\n", "");
        }
        SDZHSinglePartDetailAdapter adapter = (SDZHSinglePartDetailAdapter) singlePartRecyclerView.getAdapter();
        if (adapter != null) {
            if (adapter.getList() != null) {
                singleOriginalList = adapter.getList();
            } else {
                singleOriginalList = new ArrayList<>();
            }
            if (singleOriginalList.size() != 0 && singleOriginalList.size() == boxDetailBean.getBoxQty()) {
                ToastUtil.showToastLong("该箱已满，请扫描下一箱！");
                return;
            }
            boolean isRepeat = false;
            //判断重复扫描
            for (SDZHSinglePartBean bean : singleOriginalList) {
                if (TextUtils.equals(bean.getDOI_Code().toLowerCase(), outCode.toLowerCase())) {
                    ToastUtil.showToastShort("该托标码已存在，请勿重复扫描！");
                    isRepeat = true;
                    break;
                }
            }
            if (isRepeat) {
                return;
            }


            splitStringArray = outCode.split(",");
            int productId = 0;
            if (splitStringArray.length > 5) {
//            boolean hasSinglePart = false;
//                String productId = splitStringArray[3];
                //普通单机是5，这里按箱标 是7
                //b12电机特殊处理
                String customerProductNo = "";
                if(!TextUtils.isEmpty(splitStringArray[6]) ){
                    try{
                        productId = Integer.parseInt(splitStringArray[6]);
                    }catch (Exception e){
                        productId = 0;
                    }

                }
                if (productId > 0){
                    customerProductNo = splitStringArray[3];
                    boolean hasSinglePart = false;
                    for (SDZHBoxDetailBean boxDetailBean : boxDetailAdapter.getList()) {
                        if (productId == boxDetailBean.getProductId()) {
                            hasSinglePart = true;
                            break;
                        }
                    }

                    if (hasSinglePart) {
                        SDZHSinglePartBean model = new SDZHSinglePartBean();
                        model.setBoxCode(selectBoxNo);
                        model.setDoiNO(splitStringArray[0]);
                        model.setProductNo(splitStringArray[1]);
                        model.setWorkNo(splitStringArray[2]);
//                        model.setProductId(splitStringArray[3]);
                        model.setProductId(productId + "");
                        if (splitStringArray[4].length() <= 3) {
                            try {
                                model.setLineId(Integer.parseInt(splitStringArray[4]));
                            } catch (Exception e) {
                                model.setLineId(0);//表示这个产线 返回的是 20200422 这种格式
                            }

                        } else {
                            model.setLineId(0);
                        }
                        model.setDOI_Code(outCode);

                        singleOriginalList.add(model);
                        singlePartDetailAdapter.setData(singleOriginalList);
                        if (singleOriginalList.size() > 0) {
                            singlePartRecyclerView.setVisibility(View.VISIBLE);
                            innerEmptyLayoutView.setVisibility(View.GONE);
                        }
                        //// TODO: 2020/2/28  ? 最后一参数
                        refreshCurrentInfo(selectOrderNO, selectBoxNo, boxDetailBean.getBoxQty(), singleOriginalList.size(), 0);
                        showInputDialog() ;
                        String sqlHead = "INSERT INTO DeliveryOrder_Item (BoxCode, DOI_NO, CustomerProductNo,DOI_Code, WorkNo, ProductId, LineId, IsDelete) VALUES";
                        StringBuilder stringBuilder = new StringBuilder(sqlHead);
                        if (singleOriginalList.size() == boxDetailBean.getBoxQty()) {
                            ToastUtil.showToastLong("该箱已满，请扫描下一箱！");
                            //// TODO: 2020/2/28  保存到数据库


                            for (SDZHSinglePartBean bean : singleOriginalList) {
                                String temp = String.format("('%s','%s','%s','%s','%s','%s','%s','%s')", bean.getBoxCode(), bean.getDoiNO(), bean.getProductNo(),
                                        bean.getDOI_Code(), bean.getWorkNo(), bean.getProductId(), String.valueOf(bean.getLineId()), "0");
                                stringBuilder.append(temp);
                                stringBuilder.append(",");
                            }
                            String resultSql = stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
                            SaveSinglePartAsyncTask saveSinglePartAsyncTask = new SaveSinglePartAsyncTask();
                            saveSinglePartAsyncTask.execute(resultSql);
                            return;
                        }

                    } else {
                        ToastUtil.showToastShort("该托标Product_ID不在该发货计划内！");
                    }
                }else{

                }
//                if (!TextUtils.isEmpty(splitStringArray[3]) && splitStringArray[3].startsWith("B12LeH-ZD1221C")) {
//                    int productId = 19336;
//
//                    customerProductNo = splitStringArray[3];
//                    boolean hasSinglePart = false;
//                    for (SDZHBoxDetailBean boxDetailBean : boxDetailAdapter.getList()) {
//                        if (productId == boxDetailBean.getProductId()) {
//                            hasSinglePart = true;
//                            break;
//                        }
//                    }
//
//                    if (hasSinglePart) {
//                        SDZHSinglePartBean model = new SDZHSinglePartBean();
//                        model.setBoxCode(selectBoxNo);
//                        model.setDoiNO(splitStringArray[0]);
//                        model.setProductNo(splitStringArray[1]);
//                        model.setWorkNo(splitStringArray[2]);
////                        model.setProductId(splitStringArray[3]);
//                        model.setProductId(productId + "");
//                        if (splitStringArray[4].length() <= 3) {
//                            try {
//                                model.setLineId(Integer.parseInt(splitStringArray[4]));
//                            } catch (Exception e) {
//                                model.setLineId(0);//表示这个产线 返回的是 20200422 这种格式
//                            }
//
//                        } else {
//                            model.setLineId(0);
//                        }
//                        model.setDOI_Code(outCode);
//
//                        singleOriginalList.add(model);
//                        singlePartDetailAdapter.setData(singleOriginalList);
//                        if (singleOriginalList.size() > 0) {
//                            singlePartRecyclerView.setVisibility(View.VISIBLE);
//                            innerEmptyLayoutView.setVisibility(View.GONE);
//                        }
//                        //// TODO: 2020/2/28  ? 最后一参数
//                        refreshCurrentInfo(selectOrderNO, selectBoxNo, boxDetailBean.getBoxQty(), singleOriginalList.size(), "");
//                        showInputDialog() ;
//                        String sqlHead = "INSERT INTO DeliveryOrder_Item (BoxCode, DOI_NO, CustomerProductNo,DOI_Code, WorkNo, ProductId, LineId, IsDelete) VALUES";
//                        StringBuilder stringBuilder = new StringBuilder(sqlHead);
//                        if (singleOriginalList.size() == boxDetailBean.getBoxQty()) {
//                            ToastUtil.showToastLong("该箱已满，请扫描下一箱！");
//                            //// TODO: 2020/2/28  保存到数据库
//
//
//                            for (SDZHSinglePartBean bean : singleOriginalList) {
//                                String temp = String.format("('%s','%s','%s','%s','%s','%s','%s','%s')", bean.getBoxCode(), bean.getDoiNO(), bean.getProductNo(),
//                                        bean.getDOI_Code(), bean.getWorkNo(), bean.getProductId(), String.valueOf(bean.getLineId()), "0");
//                                stringBuilder.append(temp);
//                                stringBuilder.append(",");
//                            }
//                            String resultSql = stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
//                            SaveSinglePartAsyncTask saveSinglePartAsyncTask = new SaveSinglePartAsyncTask();
//                            saveSinglePartAsyncTask.execute(resultSql);
//                            return;
//                        }
//
//                    } else {
//                        ToastUtil.showToastShort("该托标Product_ID不在该发货计划内！");
//                    }
//                } else {
//                    customerProductNo = splitStringArray[2];
//                    GetProductIdAsyncTask task = new GetProductIdAsyncTask();
//                    task.execute(customerProductNo, outCode);
//                }


                //// TODO: 2020/4/18 这里要通过图号获取productId
//                for (SDZHBoxDetailBean boxDetailBean : boxDetailAdapter.getList()) {
//                    if (productId.equals(boxDetailBean.getProductId())) {
//                        hasSinglePart = true;
//                        break;
//                    }
//                }
//
//                if (!hasSinglePart) {
//                    SDZHSinglePartBean model = new SDZHSinglePartBean();
//                    model.setBoxCode(selectBoxNo);
//                    model.setDoiNO(splitStringArray[0]);
//                    model.setProductNo(splitStringArray[1]);
//                    model.setWorkNo(splitStringArray[2]);
//                    model.setProductId(splitStringArray[3]);
//                    model.setLineId(Integer.parseInt(splitStringArray[4]));
//                    model.setDOI_Code(outCode);
//
//                    singleOriginalList.add(model);
//                    singlePartDetailAdapter.setData(singleOriginalList);
//                    if (singleOriginalList.size() > 0) {
//                        singlePartRecyclerView.setVisibility(View.VISIBLE);
//                        innerEmptyLayoutView.setVisibility(View.GONE);
//                    }
//                    //// TODO: 2020/2/28  ? 最后一参数
//                    refreshCurrentInfo(selectOrderNO, selectBoxNo, boxDetailBean.getBoxQty(), singleOriginalList.size(), model.getWorkNo());
//
//                    String sqlHead = "INSERT INTO DeliveryOrder_Item (BoxCode, DOI_NO, CustomerProductNo,DOI_Code, WorkNo, ProductId, LineId, IsDelete) VALUES";
//                    StringBuilder stringBuilder = new StringBuilder(sqlHead);
//                    if (singleOriginalList.size() == boxDetailBean.getBoxQty()) {
//                        ToastUtil.showToastLong("该箱已满，请扫描下一箱！");
//                        //// TODO: 2020/2/28  保存到数据库
//                        for (SDZHSinglePartBean bean : singleOriginalList) {
//                            String temp = String.format("('%s','%s','%s','%s','%s','%s','%s','%s')", bean.getBoxCode(), bean.getDoiNO(), bean.getProductNo(),
//                                    bean.getDOI_Code(), bean.getWorkNo(), bean.getProductId(), String.valueOf(bean.getLineId()), "0");
//                            stringBuilder.append(temp);
//                            stringBuilder.append(",");
//                        }
//                        String resultSql = stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
//                        SaveSinglePartAsyncTask saveSinglePartAsyncTask = new SaveSinglePartAsyncTask();
//                        saveSinglePartAsyncTask.execute(resultSql);
//                        return;
//                    }
////            ToastUtil.showToastShort("该包装箱号不存在！");
////                String sql = "INSERT INTO DeliveryOrder_Item (BoxCode, DOI_NO, CustomerProductNo," +
////                        "DOI_Code, WorkNo, ProductId, LineId, IsDelete) VALUES('" + model.getBoxCode() + "','" + model.getDoiNO() + "','" +
////                        model.getProductNo() + "','" + model.getDOI_Code() + "','" +
////                        model.getWorkNo() + "'," + model.getProductId()+ "," + String.valueOf(model.getLineId())+ ",0)";
//
////                handleInsert
//
//                } else {
//                    ToastUtil.showToastShort("该单机条码已存在，请勿重复扫描！");
//                }

            } else {
                ToastUtil.showToastShort("托标条码格式不正确！");
                inputEditText.setText("");
            }
        }

    }
//    private Bitmap ecodeAsBitmap(String content){
//        Bitmap bitmap = null;
//        BitMatrix result = null;
//        MultiFormatWriter writer = new MultiFormatWriter();
//        try{
//            Hashtable<EncodeHintType, Object> hints = new Hashtable();
//            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
//            //注意BitMatrix构造方法的实现；
//            result = writer.encode(content, BarcodeFormat.QR_CODE, 800,800, hints);
//            BarcodeEncoder encoder = new BarcodeEncoder();
//            bitmap = encoder.createBitmap(result);
//        }catch(WriterException e){
//            e.printStackTrace();
//        }
//        return bitmap;
//    }

    private class GetProductIdAsyncTask extends AsyncTask<String, String, String> {
        private String outCode;

        @Override protected String doInBackground(String... strings) {
            String customerProductNo = strings[0];
            outCode = strings[1];
            String sql = String.format("select  Product_ID from product where product_drawno = '%s'", customerProductNo);

            WsResult result = WebServiceUtil.getDataTable(sql);
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                if (!TextUtils.isEmpty(jsonData)) {
                    //// TODO: 2020/1/3 放这里会出错，刷新UI线程问题
                    return jsonData;

                }
            }
            return null;
        }

        @Override protected void onPostExecute(String json) {
            super.onPostExecute(json);
            if (!TextUtils.isEmpty(json)) {
                Gson gson = new Gson();
                List<ProductIdBean> productIdBeanList = gson.fromJson(json, new TypeToken<List<ProductIdBean>>() {
                }.getType());

                if (productIdBeanList != null && productIdBeanList.size() > 0) {
                    ProductIdBean productIdBean = productIdBeanList.get(0);
                    int productId = productIdBean.getProductID();

                    boolean hasSinglePart = false;
                    for (SDZHBoxDetailBean boxDetailBean : boxDetailAdapter.getList()) {
//                        if (String.valueOf(productId).trim().equals(boxDetailBean.getProductId())) {
//                            hasSinglePart = true;
//                            break;
//                        }
                        if (productId == boxDetailBean.getProductId()) {
                            hasSinglePart = true;
                            break;
                        }
                    }

                    if (hasSinglePart) {
                        SDZHSinglePartBean model = new SDZHSinglePartBean();
                        model.setBoxCode(selectBoxNo);
                        model.setDoiNO(splitStringArray[0]);
                        model.setProductNo(splitStringArray[1]);
                        model.setWorkNo(splitStringArray[2]);
//                        model.setProductId(splitStringArray[3]);
                        model.setProductId(productId + "");
                        if (splitStringArray[4].length() <= 3) {
                            try {
                                model.setLineId(Integer.parseInt(splitStringArray[4]));
                            } catch (Exception e) {
                                model.setLineId(0);//表示这个产线 返回的是 20200422 这种格式
                            }

                        } else {
                            model.setLineId(0);
                        }
                        model.setDOI_Code(outCode);

                        singleOriginalList.add(model);
                        singlePartDetailAdapter.setData(singleOriginalList);
                        if (singleOriginalList.size() > 0) {
                            singlePartRecyclerView.setVisibility(View.VISIBLE);
                            innerEmptyLayoutView.setVisibility(View.GONE);
                        }
                        //// TODO: 2020/2/28  ? 最后一参数
                        refreshCurrentInfo(selectOrderNO, selectBoxNo, boxDetailBean.getBoxQty(), singleOriginalList.size(), 0);

                        String sqlHead = "INSERT INTO DeliveryOrder_Item (BoxCode, DOI_NO, CustomerProductNo,DOI_Code, WorkNo, ProductId, LineId, IsDelete) VALUES";
                        StringBuilder stringBuilder = new StringBuilder(sqlHead);
                        if (singleOriginalList.size() == boxDetailBean.getBoxQty()) {
                            ToastUtil.showToastLong("该箱已满，请扫描下一箱！");
                            //// TODO: 2020/2/28  保存到数据库


                            for (SDZHSinglePartBean bean : singleOriginalList) {
                                String temp = String.format("('%s','%s','%s','%s','%s','%s','%s','%s')", bean.getBoxCode(), bean.getDoiNO(), bean.getProductNo(),
                                        bean.getDOI_Code(), bean.getWorkNo(), bean.getProductId(), String.valueOf(bean.getLineId()), "0");
                                stringBuilder.append(temp);
                                stringBuilder.append(",");
                            }
                            String resultSql = stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
                            SaveSinglePartAsyncTask saveSinglePartAsyncTask = new SaveSinglePartAsyncTask();
                            saveSinglePartAsyncTask.execute(resultSql);
                            return;
                        }

                    } else {
                        ToastUtil.showToastShort("该托标Product_ID不在该发货计划内！");
                    }
                }
                inputEditText.setText("");
            }
        }
    }

    private void handleBoxScan(String content) {
        boolean hasBox = false;
        int index = 0;
        for (SDZHBoxDetailBean bean : boxDetailAdapter.getList()) {
            //这里面有箱号有空格 不能直接trim，
            content = content.replace("\n", "");
//            if (content.equals(bean.getBoxCode().replace("\n",""))) {
            if (TextUtils.equals(content.toLowerCase(), bean.getBoxCode().replace("\n", "").toLowerCase())) {
                hasBox = true;
                boxDetailBean = bean;
                break;
            }
            index++;
        }

        if (!hasBox) {
            ToastUtil.showToastShort("该包装箱号不存在！");
        } else {
            if (!boxDetailBean.getIsOK()) {
                ToastUtil.showToastShort("该箱已装满！");
            } else {
                singlePartBarButton.setEnabled(true);
                scanPalletBarButton.setEnabled(true);
                ToastUtil.showToastShort("请扫描托标！");
                boxDetailAdapter.setSelectPosition(index);
                refreshCurrentInfo(orderNumberBean.getOrderNo(), boxDetailBean.getBoxCode(), boxDetailBean.getBoxQty(), 0, 0);
                currentScanState = SCAN_PALLET_CODE;
            }


        }
        inputEditText.setText("");

    }

    private void handleOrderNOScan(String content) {
        boolean hasOrderNO = false;
        List<SDZHDeliveryOrderNumberBean> orderNumberList = itemTextViewAdapter.getList();
        if (orderNumberList == null) {
            ToastUtil.showToastShort("该计划下没有预设发货单！");
            return;
        }
        int index = 0;
        for (SDZHDeliveryOrderNumberBean bean : orderNumberList) {
//            TextView textView = (TextView) orderNoDataLayout.getChildAt(i);
            if (content.trim().toLowerCase().equals(bean.getOrderNo().toLowerCase())) {
//                textView.setBackgroundColor(Color.RED);
                hasOrderNO = true;
                orderNumberBean = bean;
                break;
            }
            index++;
        }

//        if (content.equals(orderNumberTextView.getText().toString())) {
//            hasOrderNO = true;
//        }


        if (!hasOrderNO) {
            ToastUtil.showToastShort("该发货单号不存在！");
        } else {
            boxBarButton.setEnabled(true);
            ToastUtil.showToastShort("请扫描包装箱！");

            itemTextViewAdapter.setSelectPosition(index);
            currentScanState = SCAN_BOX_CODE;

        }
        inputEditText.setText("");
    }


    private void parseScanResult(String content) {
        ToastUtil.showToastLong(content);
    }

    private int totalRetNumber = 0;
    private void refreshCurrentInfo(String orderNumber, String boxNumber, int capacityNumber, int scannedNumber, int tobeReducedNumber) {
        try {
            if (totalRetNumber == 0){
//                totalRetNumber = Integer.parseInt(boxNumber);
                totalRetNumber = boxDetailBean.getBoxQty();
            }
            infoTextView.setText(String.format("当前发货单号: %s    当前包装箱号: %s   \n " +
                            "收容量: %s      已扫描:  %s   该箱剩余出库数:  %s   ",
                    orderNumber, boxNumber, capacityNumber, scannedNumber, (totalRetNumber - tobeReducedNumber) + ""));
            totalRetNumber = totalRetNumber - tobeReducedNumber;

        } catch (Exception e) {
            infoTextView.setText(String.format("当前发货单号: %s    当前包装箱号: %s   \n " +
                            "收容量: %s      已扫描:  %s   该箱剩余出库数:  %s   ",
                    orderNumber, boxNumber, capacityNumber, scannedNumber, 0 + ""));
        }
    }

    private void setButtonsNotEnable() {
//        orderNumberButton.setEnabled(false);
        boxBarButton.setEnabled(false);
        singlePartBarButton.setEnabled(false);
        scanPalletBarButton.setEnabled(false);
        removeSinglePartBarButton.setEnabled(false);
    }

    private void setViewsListener() {
        orderNumberButton.setOnClickListener(this);
        boxBarButton.setOnClickListener(this);
        singlePartBarButton.setOnClickListener(this);
        scanPalletBarButton.setOnClickListener(this);
        removeSinglePartBarButton.setOnClickListener(this);
        outButton.setOnClickListener(this);
        logisticsButton.setOnClickListener(this);

        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                if (editable.toString().length() > 3 && editable.toString().endsWith("\n")) {
//                    ToastUtil.showToastLong("扫描结果:" + editable.toString());
                    System.out.println("========================扫描结果:" + editable.toString());
                    if (!TextUtils.isEmpty(editable.toString())) {
                        switch (currentScanState) {
                            case SCAN_ORDER_NUMBER:
                                handleOrderNOScan(editable.toString());
                                break;
                            case SCAN_BOX_CODE:
                                handleBoxScan(editable.toString());
                                break;
                            case SCAN_PALLET_CODE:
                                handleSinglePartScan(editable.toString());
                                break;
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == orderNumberButton) {
            new IntentIntegrator(this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
            currentScanState = SCAN_ORDER_NUMBER;
        } else if (view == boxBarButton) {
            new IntentIntegrator(this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
            currentScanState = SCAN_BOX_CODE;
        } else if (view == singlePartBarButton) {
            new IntentIntegrator(this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
            currentScanState = SCAN_PALLET_CODE;
        } else if (view == removeSinglePartBarButton) {
//            removeSinglePart();
        } else if (view == outButton) {
            handleSaleOut();
        } else if (view == logisticsButton) {
            Intent intent = new Intent(this, LogisticsManageActivity.class);
            intent.putExtra(IntentConstant.Intent_Extra_logistics_from,IntentConstant.Intent_Request_Code_Logistics_from_sdzh_scan_pallet);
            startActivityForResult(intent, IntentConstant.Intent_Request_Code_Product_To_Logistics);
        } else if (view == scanPalletBarButton) {
            new IntentIntegrator(this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
            currentScanState = SCAN_PALLET_CODE;
        }
    }

    private void handleSaleOut() {

        if (UserSingleton.get().getHRID() > 0 && !TextUtils.isEmpty(UserSingleton.get().getHRName())) {
//           judgeIsFullAndHandle();
            if (logisticsDeliveryId == 0) {
                ToastUtil.showToastShort("未选择物流信息！");
                return;
            }

//            if (boxDetailBean != null && singleOriginalList != null && (singleOriginalList.size() < boxDetailBean.getBoxQty())) {
            if (boxDetailBean != null && totalRetNumber > 0 ) {
//            ToastUtil.showToastShort("当前箱子不满，确定要出库吗？");
                CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(SDZHScanPalletCodeActivity.this)
                        .setTitle("").setMessage("当前箱子不满，确定要出库吗？")
                        .setLeftText("确定").setRightText("取消");


                builder.setOnViewClickListener(new OnDialogViewClickListener() {
                    @Override
                    public void onViewClick(Dialog dialog, View v, int tag) {
                        switch (tag) {
                            case CommAlertDialog.TAG_CLICK_LEFT:
                                HandleSDZHProductOutAsyncTask outAsyncTask = new HandleSDZHProductOutAsyncTask();
//                                outAsyncTask.execute(singleOriginalList.size() + "");
                                outAsyncTask.execute(inputNumber + "");
                                dialog.dismiss();
                                break;
                            case CommAlertDialog.TAG_CLICK_RIGHT:
                                dialog.dismiss();
                                break;
                        }
                    }
                });
                builder.create().show();

            } else {
                HandleSDZHProductOutAsyncTask outAsyncTask = new HandleSDZHProductOutAsyncTask();
//                outAsyncTask.execute(singleOriginalList.size() + "");
                outAsyncTask.execute(inputNumber+ "");
            }


        } else {
            CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(SDZHScanPalletCodeActivity.this)
                    .setTitle("").setMessage("您当前程序账号有误，需重新登录！")
                    .setLeftText("确定");


            builder.setOnViewClickListener(new OnDialogViewClickListener() {
                @Override
                public void onViewClick(Dialog dialog, View v, int tag) {
                    switch (tag) {
                        case CommAlertDialog.TAG_CLICK_LEFT:
                            CommonUtil.doLogout(SDZHScanPalletCodeActivity.this);
                            dialog.dismiss();
                            break;
                    }
                }
            });
            builder.create().show();
        }
    }

//    private void judgeIsFullAndHandle(){
//        if (boxDetailBean != null && singleOriginalList != null && (singleOriginalList.size() < boxDetailBean.getBoxQty()) ){
////            ToastUtil.showToastShort("当前箱子不满，确定要出库吗？");
//            CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(SDZHScanPalletCodeActivity.this)
//                    .setTitle("").setMessage("当前箱子不满，确定要出库吗？")
//                    .setLeftText("确定").setRightText("取消");
//
//
//            builder.setOnViewClickListener(new OnDialogViewClickListener() {
//                @Override
//                public void onViewClick(Dialog dialog, View v, int tag) {
//                    switch (tag) {
//                        case CommAlertDialog.TAG_CLICK_LEFT:
//                            handleSaleOut();
//                            dialog.dismiss();
//                            break;
//                        case CommAlertDialog.TAG_CLICK_RIGHT:
//                            dialog.dismiss();
//                            break;
//                    }
//                }
//            });
//            builder.create().show();
//
//        }else{
//            handleSaleOut();
//        }
//    }

//    private void removeSinglePart() {
//        if (sdzhSinglePartBean != null) {
//            CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(SDZHScanPalletCodeActivity.this)
//                    .setTitle("").setMessage("确定移除该单机条目吗？")
//                    .setLeftText("确定").setRightText("取消");
//
//
//            builder.setOnViewClickListener(new OnDialogViewClickListener() {
//                @Override
//                public void onViewClick(Dialog dialog, View v, int tag) {
//                    switch (tag) {
//                        case CommAlertDialog.TAG_CLICK_LEFT:
//                            handleRemoveSinglePart();
//                            dialog.dismiss();
//                            break;
//                        case CommAlertDialog.TAG_CLICK_RIGHT:
//                            dialog.dismiss();
//                            break;
//                    }
//                }
//            });
//            builder.create().show();
//        } else {
//            ToastUtil.showToastShort("请选择要移除的单机条目");
//        }
//    }

    private void handleRemoveSinglePart() {
        //// TODO: 2020/2/29 本来是要用do_id来判断的，但实际扫描单机时没有时时保存，所以用boxcode和doi_no
//        String sql = String.format("delete DeliveryOrder_Item where BoxCode = '%s' and DOI_NO = '%s'", sdzhSinglePartBean.getBoxCode(),
//                sdzhSinglePartBean.getDOI_Code());

        String sql = String.format("update DeliveryOrder_Item set IsDelete = 1 where BoxCode = '%s' and DOI_NO = '%s'", sdzhSinglePartBean.getBoxCode(),
                sdzhSinglePartBean.getDoiNO());
        DeleteSinglePartAsyncTask task = new DeleteSinglePartAsyncTask();
        task.execute(sql);
//        singlePartDetailAdapter.notifyItemRemoved(selectRemovePosition);
    }


//    select * FROM DeliveryOrder_Product WHERE IsDelete=0 AND Do_Id= 6777
//SELECT distinct(OrderNo) AS 发货单号 FROM DeliveryOrder_Product WHERE IsDelete=0 AND Do_Id= 6771
//// TODO: 2020/2/26  暂时先不用distinct ?

    /**
     * 根据发货计划（指令）获取发货单号
     */

    private class GetDeliveryOrderAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String do_ID = strings[0];
            //CLD0A252A12I1A
            String sql = String.format("select * FROM DeliveryOrder_Product WHERE IsDelete=0 AND Do_Id= %s", do_ID);
            WsResult result = WebServiceUtil.getDataTable(sql);
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                if (!TextUtils.isEmpty(jsonData)) {
                    //// TODO: 2020/1/3 放这里会出错，刷新UI线程问题
                    return jsonData;

                }
            }

            return null;
        }


        @Override
        protected void onPostExecute(String jsonData) {
            super.onPostExecute(jsonData);
            Gson gson = new Gson();
            List<SDZHDeliveryOrderNumberBean> sendGoodsOrderNumberBeanList = gson.fromJson(jsonData, new TypeToken<List<SDZHDeliveryOrderNumberBean>>() {
            }.getType());
            if (sendGoodsOrderNumberBeanList != null && sendGoodsOrderNumberBeanList.size() > 0) {
                orderNumberButton.setEnabled(true);
                if (emptyLayoutView.getVisibility() == View.VISIBLE) {
                    emptyLayoutView.setVisibility(View.GONE);
//                    recyclerView.setVisibility(View.VISIBLE);
                    dataLayout.setVisibility(View.VISIBLE);

                    orderNumberButton.setEnabled(true);

                }
                itemTextViewAdapter.setData(sendGoodsOrderNumberBeanList);

//                if (sendGoodsOrderNumberBeanList.size() > 1) {

//                orderNoDataLayout.removeAllViews();
//                for (SDZHDeliveryOrderNumberBean bean : sendGoodsOrderNumberBeanList) {
//                    TextView textView = new TextView(SDZHScanPalletCodeActivity.this);
//                    textView.setText("发货单号 " + bean.getOrderNo());
//                    orderNoDataLayout.addView(textView);
//                    textView.setOnClickListener(v -> {
//                        v.setBackgroundColor(Color.RED);
//                    });
//                }
//                TextView firstView = (TextView) orderNoDataLayout.getChildAt(0);
//                firstView.setBackgroundColor(Color.RED);
//                }


                //// TODO: 2020/2/26 这里暂时先取一个
                SDZHDeliveryOrderNumberBean bean = sendGoodsOrderNumberBeanList.get(0);
                selectOrderNO = bean.getOrderNo();
                orderNumberTextView.setText(bean.getOrderNo());

                refreshCurrentInfo(bean.getOrderNo(), "", 0, 0, 0);
                new GetOrderDetailAsyncTask().execute(bean.getOrderNo());
            } else {
                rootDataLayout.setVisibility(View.GONE);
                rootEmptyLayoutView.setVisibility(View.VISIBLE);

                dataLayout.setVisibility(View.GONE);
                emptyLayoutView.setVisibility(View.VISIBLE);
//            }

            }
        }


    }

    /**
     * 通过sql获取发货单详情
     */
    private class GetOrderDetailAsyncTask extends AsyncTask<String, String, String> {

        String orderNo = "";

        @Override
        protected String doInBackground(String... strings) {
            orderNo = strings[0];
            //CLD0A252A12I1A
            String sql = String.format("SELECT DeliveryOrder_Product.DOP_Id,Program.Program_ID, Program.Program_Name  ,DeliveryOrder_Product.Product_ID ,DeliveryOrder_Product.PS_ID," +
                    "\t  DeliveryOrder_Product.OrderNo,Product.Product_Chinese_Name As Product_Name, Product.Abb As Product_Common_Name, " +
                    "                                    Product.Product_PartNo ,PS_Version.PS_Version  ,dbo.Get_Product_Version_Latest(DeliveryOrder_Product.Product_ID) As Newest_version " +
                    "                                    FROM  DeliveryOrder_Product " +
                    "                                    INNER JOIN Product ON Product.Product_ID =DeliveryOrder_Product.Product_ID " +
                    "                                    INNER JOIN PS_Version ON DeliveryOrder_Product.PS_ID =PS_Version .PS_ID " +
                    "                                    INNER JOIN Program ON Program.Program_ID =Product.Program_ID  " +
                    " WHERE  DeliveryOrder_Product.IsDelete=0 AND DeliveryOrder_Product.OrderNo='%s'", orderNo);
            WsResult result = WebServiceUtil.getDataTable(sql);
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                if (!TextUtils.isEmpty(jsonData)) {
                    //// TODO: 2020/1/3 放这里会出错，刷新UI线程问题
                    return jsonData;

                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String jsonData) {
            super.onPostExecute(jsonData);
            Gson gson = new Gson();
            List<SDZHDeliveryOrderNumberDetailBean> sendGoodsOrderNumberDetailBeanList = gson.fromJson(jsonData, new TypeToken<List<SDZHDeliveryOrderNumberDetailBean>>() {
            }.getType());
            if (sendGoodsOrderNumberDetailBeanList != null && sendGoodsOrderNumberDetailBeanList.size() > 0) {

//                sdzhOrderDetailAdapter = new SDZHOrderDetailAdapter();
//                orderNumberRecyclerView.setAdapter(sdzhOrderDetailAdapter);

                sdzhOrderDetailAdapter.setData(sendGoodsOrderNumberDetailBeanList);
                if (innerEmptyLayoutView.getVisibility() == View.VISIBLE) {
                    innerEmptyLayoutView.setVisibility(View.GONE);
                    orderNumberRecyclerView.setVisibility(View.VISIBLE);
                }

                new GetBoxDetailAsyncTask().execute(orderNo);

            } else {
                orderNumberRecyclerView.setVisibility(View.GONE);
                innerEmptyLayoutView.setVisibility(View.VISIBLE);
            }

        }
    }

    /**
     * 通过sql获取箱号详情
     */
    private class GetBoxDetailAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
//            String order_id = splitStringArray[0];
//            String orderNo = splitStringArray[1];

            //// TODO: 2020/2/27   根据参数，这个请求与上面的可同时进行
            String orderNo = strings[0];

//            String order_id = "6777";
//            String orderNo = "CLD0A252A12I1A";


//            String order_id = "6023";
//            String orderNo = "RD10A252A11E1A1";
            //CLD0A252A12I1A  6777
            //isok = 1是没有装满 其他的  是装满
            String sql = String.format("SELECT DOB_Id, Do_Id, OrderNo ,Product_Id, ProductNo , BoxQty , BoxCode , " +
                    "IsDelete, IsOK  " +
                    " FROM DeliveryOrder_Box WHERE Do_Id=%s AND OrderNo='%s' AND IsDelete=0", do_ID, orderNo);
            WsResult result = WebServiceUtil.getDataTable(sql);
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                if (!TextUtils.isEmpty(jsonData)) {
                    //// TODO: 2020/1/3 放这里会出错，刷新UI线程问题
                    return jsonData;

                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String jsonData) {
            super.onPostExecute(jsonData);
            Gson gson = new Gson();
            List<SDZHBoxDetailBean> boxBeanList = gson.fromJson(jsonData, new TypeToken<List<SDZHBoxDetailBean>>() {
            }.getType());
            if (boxBeanList != null && boxBeanList.size() > 0) {

//                sdzhOrderDetailAdapter = new SDZHOrderDetailAdapter();
//                orderNumberRecyclerView.setAdapter(sdzhOrderDetailAdapter);

                boxDetailAdapter.setData(boxBeanList);
                boxTitleTextView.setVisibility(View.VISIBLE);
                boxRecyclerView.setVisibility(View.VISIBLE);

                boxDetailBean = boxBeanList.get(0);
                selectBoxNo = boxBeanList.get(0).getBoxCode();
                new GetSinglePartDetailAsyncTask().execute(boxBeanList.get(0).getBoxCode());

                if (innerEmptyLayoutView.getVisibility() == View.VISIBLE) {
                    innerEmptyLayoutView.setVisibility(View.GONE);
                    orderNumberRecyclerView.setVisibility(View.VISIBLE);
                }
            } else {
                orderNumberRecyclerView.setVisibility(View.GONE);
                innerEmptyLayoutView.setVisibility(View.VISIBLE);

                boxTitleTextView.setVisibility(View.GONE);
                boxRecyclerView.setVisibility(View.GONE);
            }

        }
    }


    /**
     * 通过sql获取单机详情
     */
    private class GetSinglePartDetailAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            String selectBoxNo = strings[0];
//            order_id = "6777";
//            orderNo = "CLD0A252A12I1A";
            //CLD0A252A12I1A  6777
            String sql = String.format("SELECT DOI_Id, BoxCode, DOI_NO, CustomerProductNo as ProductNo,DOI_Code, WorkNo, ProductId, LineId, IsDelete FROM DeliveryOrder_Item " +
                    " WHERE BoxCode='%s' AND IsDelete=0", selectBoxNo);
            WsResult result = WebServiceUtil.getDataTable(sql);
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                if (!TextUtils.isEmpty(jsonData)) {
                    //// TODO: 2020/1/3 放这里会出错，刷新UI线程问题
                    return jsonData;

                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String jsonData) {
            super.onPostExecute(jsonData);
            Gson gson = new Gson();
            List<SDZHSinglePartBean> singlePartBeanList = gson.fromJson(jsonData, new TypeToken<List<SDZHSinglePartBean>>() {
            }.getType());
            if (singlePartBeanList != null && singlePartBeanList.size() > 0) {

//                sdzhOrderDetailAdapter = new SDZHOrderDetailAdapter();
//                orderNumberRecyclerView.setAdapter(sdzhOrderDetailAdapter);

                singlePartDetailAdapter.setData(singlePartBeanList);
                if (innerEmptyLayoutView.getVisibility() == View.VISIBLE) {
                    innerEmptyLayoutView.setVisibility(View.GONE);
                    orderNumberRecyclerView.setVisibility(View.VISIBLE);
                }

                singlePartTitleTextView.setVisibility(View.VISIBLE);
                singlePartRecyclerView.setVisibility(View.VISIBLE);
            } else {
                orderNumberRecyclerView.setVisibility(View.GONE);
                innerEmptyLayoutView.setVisibility(View.VISIBLE);

                singlePartTitleTextView.setVisibility(View.GONE);
                singlePartRecyclerView.setVisibility(View.GONE);
            }

            singlePartTitleTextView.setVisibility(View.VISIBLE);

        }
    }

    /**
     * 操作数据库保存单机信息
     */
    private class SaveSinglePartAsyncTask extends AsyncTask<String, String, WsResult> {
        private CommProgressDialog progressDialog;

        @Override
        protected WsResult doInBackground(String... strings) {

            String sql = strings[0];
            System.out.println("========== SaveSinglePartAsyncTask sql = " + sql);
            WsResult result = WebServiceUtil.getDataTable(sql);
//            if (result != null && result.getResult()) {
//                String jsonData = result.getErrorInfo();
//                if (!TextUtils.isEmpty(jsonData)) {
//                    //// TODO: 2020/1/3 放这里会出错，刷新UI线程问题
//                    return jsonData;
//
//                }
//            }
//
//            return null;
            return result;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new CommProgressDialog.Builder(SDZHScanPalletCodeActivity.this)
                    .setTitle("正在保存单机数据..").create();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(WsResult result) {
            super.onPostExecute(result);
            if (result != null && result.getResult()) {
                ToastUtil.showToastLong("保存成功！");
            } else {
                ToastUtil.showToastLong("保存失败，错误信息是 " + result.getErrorInfo());
            }

            progressDialog.dismiss();
            //isok = 0表示已经装满，不可再操作
            String updateSql = String.format("UPDATE DeliveryOrder_Box SET IsOk = 0 where BoxCode = '%s'", selectBoxNo);
            UpdateBoxAsyncTask updateBoxAsyncTask = new UpdateBoxAsyncTask();
            updateBoxAsyncTask.execute(updateSql);

            singleOriginalList = new ArrayList<>();

        }
    }

    /**
     * 当一个包装箱中单机扫描完成时更新包装箱  //// TODO: 2020/2/28 未来可用接口代码合并
     */
    private class UpdateBoxAsyncTask extends AsyncTask<String, String, WsResult> {
        private CommProgressDialog progressDialog;

        @Override
        protected WsResult doInBackground(String... strings) {

            String sql = strings[0];
            System.out.println("========== UpdateBoxAsyncTask sql = " + sql);
            WsResult result = WebServiceUtil.getDataTable(sql);
            return result;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new CommProgressDialog.Builder(SDZHScanPalletCodeActivity.this)
                    .setTitle("正在更新包装箱数据..").create();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(WsResult result) {
            super.onPostExecute(result);
            if (result != null && result.getResult()) {
                ToastUtil.showToastLong("更新成功！");


                //更新箱子状态，为已装满
                new GetBoxDetailAsyncTask().execute(selectOrderNO);

            } else {
                ToastUtil.showToastLong("更新失败，错误信息是 " + result.getErrorInfo());
            }

            progressDialog.dismiss();
            singlePartBarButton.setEnabled(false);
            scanPalletBarButton.setEnabled(false);

        }
    }

    /**
     * 当一个包装箱中单机扫描完成时更新包装箱  //// TODO: 2020/2/28 未来可用接口代码合并
     */
    private class DeleteSinglePartAsyncTask extends AsyncTask<String, String, WsResult> {
        private CommProgressDialog progressDialog;

        @Override
        protected WsResult doInBackground(String... strings) {

            String sql = strings[0];
            System.out.println("========== DeleteSinglePartAsyncTask sql = " + sql);
            WsResult result = WebServiceUtil.getDataTable(sql);
            return result;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new CommProgressDialog.Builder(SDZHScanPalletCodeActivity.this)
                    .setTitle("正在更新单机条目及包装箱数据..").create();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(WsResult result) {
            super.onPostExecute(result);
            if (result != null && result.getResult()) {
                ToastUtil.showToastLong("更新成功！");

                singlePartDetailAdapter.notifyItemRemoved(selectRemovePosition);
                if (singlePartDetailAdapter != null) {
                    if (singlePartDetailAdapter.getList() != null) {
                        int currentNumber = singlePartDetailAdapter.getList().size();
                        refreshCurrentInfo(selectOrderNO, selectBoxNo, boxDetailBean.getBoxQty(), currentNumber - 1, 0 );
                    }
                }

                //同时更新box,与扫描更新唯一不同是这里 isok = 1，表示还可操作
                String updateSql = String.format("UPDATE DeliveryOrder_Box SET IsOk = 1 where BoxCode = '%s'", selectBoxNo);
                UpdateBoxAsyncTask updateBoxAsyncTask = new UpdateBoxAsyncTask();
                updateBoxAsyncTask.execute(updateSql);

            } else {
                ToastUtil.showToastLong("更新失败，错误信息是 " + result.getErrorInfo());
            }

            progressDialog.dismiss();

        }
    }

    /**
     * 三点照合成品出库
     */
    private class HandleSDZHProductOutAsyncTask extends AsyncTask<String, Void, Void> {
        private WsResult result;

        @Override protected Void doInBackground(String... strings) {
//            long cfID = 0;
            String qty = strings[0];
            long dpi_id = 0;
            Date deliveryDate = null;
            try {
                String deliveryDateString = deliveryOrderBean.getDeliveryDate();
                if (deliveryDateString.contains("T")) {
                    deliveryDateString = deliveryDateString.replace("T", " ");
                }
                deliveryDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(deliveryDateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (customerFacilityId == 0){
                ToastUtil.showToastShort("发货未选择物流！");
            }
            if (deliveryDate == null) {
                deliveryDate = new Date();
            }
            //// TODO: 2020/4/7
            if (customerFacilityId == deliveryOrderBean.getCFID()) {
                //// TODO: 2020/4/23
            }
//            result = WebServiceUtil.op_Product_Manu_Out_Not_Pallet(deliveryDate, customerFacilityId, deliveryOrderBean.getCFChineseName(), deliveryOrderBean.getTrackNo(), logisticsDeliveryId, dpi_id, deliveryOrderBean.getDOID(), orderNumberBean.getPSId(), qty);
            result = WebServiceUtil.op_Product_Manu_Out_Not_Pallet(deliveryDate, deliveryOrderBean.getCFID(), deliveryOrderBean.getCFChineseName(), deliveryOrderBean.getTrackNo(), logisticsDeliveryId, dpi_id, deliveryOrderBean.getDOID(), orderNumberBean.getPSId(), qty);
//            result = WebServiceUtil.op_Product_Manu_Out_Not_Pallet(new Date(), 68, "一汽奔腾轿车有限公司", "20200328", 102838, 0, 7426);
            return null;
        }

        @Override protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (result != null) {
                if (result.getResult()) {
                    logisticsDeliveryId = result.getID();
                    ToastUtil.showToastShort("出库成功！");
                } else {
                    ToastUtil.showToastShort("出库失败 : " + result.getErrorInfo());
                }
            }
        }
    }

    private class ProductIdBean {
        @SerializedName("Product_ID") private int productID;

        public void setProductID(int productID) {
            this.productID = productID;
        }

        public int getProductID() {
            return productID;
        }
    }
}

