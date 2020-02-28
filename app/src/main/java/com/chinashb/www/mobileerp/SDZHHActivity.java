package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinashb.www.mobileerp.adapter.SDZHOrderBoxDetailAdapter;
import com.chinashb.www.mobileerp.adapter.SDZHOrderDetailAdapter;
import com.chinashb.www.mobileerp.adapter.SDZHSinglePartDetailAdapter;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.SDZHBoxDetailBean;
import com.chinashb.www.mobileerp.bean.SDZHDeliveryOrderNumberBean;
import com.chinashb.www.mobileerp.bean.SDZHDeliveryOrderNumberDetailBean;
import com.chinashb.www.mobileerp.bean.SDZHSinglePartBean;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CommProgressDialog;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.chinashb.www.mobileerp.widget.EmptyLayoutManageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2020/2/25 9:38 PM
 * @author 作者: liweifeng
 * @description 三点照合的页面
 */
public class SDZHHActivity extends BaseActivity implements View.OnClickListener {
    public static final int SCAN_ORDER_NUMBER = 1;
    public static final int SCAN_BOX_CODE = 2;
    public static final int SCAN_SINGLE_PART_CODE = 3;
    @BindView(R.id.sdzh_scan_order_number_button) TextView orderNumberButton;
    @BindView(R.id.sdzh_scan_box_bar_button) TextView boxBarButton;
    @BindView(R.id.sdzh_scan_single_part_bar_button) TextView singlePartBarButton;
    @BindView(R.id.sdzh_remove_single_part_bar_button) TextView removeSinglePartBarButton;
    @BindView(R.id.sdzh_send_order_number_textView) TextView orderNumberTextView;
    @BindView(R.id.sdzh_root_recyclerView) CustomRecyclerView rootRecyclerView;
    @BindView(R.id.sdzh_data_layout) LinearLayout dataLayout;
    @BindView(R.id.sdzh_empty_layoutView) EmptyLayoutManageView emptyLayoutView;
    @BindView(R.id.sdzh_inner_empty_layoutView) EmptyLayoutManageView innerEmptyLayoutView;
    @BindView(R.id.sdzh_current_info_textView) TextView infoTextView;
    @BindView(R.id.sdzh_root_data_layout) LinearLayout rootDataLayout;
    @BindView(R.id.sdzh_root_empty_layoutView) EmptyLayoutManageView rootEmptyLayoutView;
    @BindView(R.id.sdzh_box_title_textView) TextView boxTitleTextView;
    @BindView(R.id.sdzh_box_recyclerView) CustomRecyclerView boxRecyclerView;
    @BindView(R.id.sdzh_single_part_title_textView) TextView singlePartTitleTextView;
    @BindView(R.id.sdzh_single_part_recyclerView) CustomRecyclerView singlePartRecyclerView;
    @BindView(R.id.sdzh_order_No_data_layout) LinearLayout orderNoDataLayout;
    private String do_ID;
    private SDZHOrderDetailAdapter sdzhOrderDetailAdapter;
    private SDZHOrderBoxDetailAdapter boxDetailAdapter;
    private SDZHSinglePartDetailAdapter singlePartDetailAdapter;
    private int currentScanState = 1;

    private String selectBoxNo;
    private SDZHBoxDetailBean boxDetailBean;
    private String selectOrderNO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdzh_layout);
        ButterKnife.bind(this);

        do_ID = getIntent().getStringExtra(IntentConstant.Intent_Extra_do_id);

        do_ID = "6023";
        if (!TextUtils.isEmpty(do_ID)) {
            new GetDeliveryOrderAsyncTask().execute(do_ID);
        }

        //// TODO: 2020/2/26 放这里报错，还没加载？没有
        sdzhOrderDetailAdapter = new SDZHOrderDetailAdapter();
        rootRecyclerView.setAdapter(sdzhOrderDetailAdapter);

        boxDetailAdapter = new SDZHOrderBoxDetailAdapter();
        boxRecyclerView.setAdapter(boxDetailAdapter);

        singlePartDetailAdapter = new SDZHSinglePartDetailAdapter();
        singlePartRecyclerView.setAdapter(singlePartDetailAdapter);

        setButtonsNotEnable();
        setViewsListener();
        refreshCurrentInfo("", "", 0, 0, "");


//            new GetDeliveryOrderAsyncTask().execute("6777");
//        new GetOrderDetailAsyncTask().execute("CLD0A252A12I1A");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                    case SCAN_SINGLE_PART_CODE:
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

    private void handleSinglePartScan(String outCode) {
//        List<SDZHSinglePartBean> singOriginalList = singlePartDetailAdapter.getList();
        List<SDZHSinglePartBean> singleOriginalList;
        SDZHSinglePartDetailAdapter adapter = (SDZHSinglePartDetailAdapter) singlePartRecyclerView.getAdapter();
        if (adapter != null) {
            if (adapter.getList() != null) {
                singleOriginalList = adapter.getList();
            } else {
                singleOriginalList = new ArrayList<>();
            }
            if (singleOriginalList.size() == boxDetailBean.getBoxQty()) {
                ToastUtil.showToastLong("该箱已满，请扫描下一箱！");
                return;
            }
            String[] strings = outCode.split(",");
            boolean hasSinglePart = false;
            if (strings.length == 5) {
                String productId = strings[3];
                for (SDZHBoxDetailBean boxDetailBean : boxDetailAdapter.getList()) {
                    if (productId.equals(boxDetailBean.getProductId())) {
                        hasSinglePart = true;
                        break;
                    }
                }

                if (!hasSinglePart) {
                    SDZHSinglePartBean model = new SDZHSinglePartBean();
                    model.setBoxCode(selectBoxNo);
                    model.setDoiNO(strings[0]);
                    model.setProductNo(strings[1]);
                    model.setWorkNo(strings[2]);
                    model.setProductId(strings[3]);
                    model.setLineId(Integer.parseInt(strings[4]));
                    model.setDOI_Code(outCode);

                    singleOriginalList.add(model);
                    singlePartDetailAdapter.setData(singleOriginalList);
                    if (singleOriginalList.size() > 0) {
                        singlePartRecyclerView.setVisibility(View.VISIBLE);
                        innerEmptyLayoutView.setVisibility(View.GONE);
                    }
                    //// TODO: 2020/2/28  ? 最后一参数
                    refreshCurrentInfo(selectOrderNO, selectBoxNo, boxDetailBean.getBoxQty(), singleOriginalList.size(), model.getWorkNo());

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
                        String resultSql = stringBuilder.toString().substring(0,stringBuilder.toString().length() - 1);
                        SaveSinglePartAsyncTask saveSinglePartAsyncTask = new SaveSinglePartAsyncTask();
                        saveSinglePartAsyncTask.execute(resultSql);
                        return;
                    }
//            ToastUtil.showToastShort("该包装箱号不存在！");
//                String sql = "INSERT INTO DeliveryOrder_Item (BoxCode, DOI_NO, CustomerProductNo," +
//                        "DOI_Code, WorkNo, ProductId, LineId, IsDelete) VALUES('" + model.getBoxCode() + "','" + model.getDoiNO() + "','" +
//                        model.getProductNo() + "','" + model.getDOI_Code() + "','" +
//                        model.getWorkNo() + "'," + model.getProductId()+ "," + String.valueOf(model.getLineId())+ ",0)";

//                handleInsert

                } else {
                    ToastUtil.showToastShort("该单机条码已存在，请勿重复扫描！");

                }

            } else {
                ToastUtil.showToastShort("单机条码格式不正确！");
            }
        }


//        boolean isHas = false;
//        For index = 0 To dgv_Box.Rows.Count - 1
//        If dgv_Box.Rows(index).Cells("Product_ID").Value.ToString() = model.ProductId Then
//                isHas = True
//        End If
//        Next
//        If isHas Then
//        If sqlHelp.SelectIsHaveData(" COUNT(*) ", " DeliveryOrder_item ", " DOI_Code='" + model.DOI_Code + "' And IsDelete=0 ") Then
//        MessageBox.Show("【单机条码：" + model.DOI_NO + "】已存在！", "提示", MessageBoxButtons.OK, MessageBoxIcon.Warning)
//        Else
//        If sqlHelp.InsertDeliveryOrder_Item(model) Then
//        lbl_IsIn.Text = (CInt(lbl_IsIn.Text.Trim()) + 1).ToString()
//        BindOrderItem()
//        If lbl_Qty.Text.Trim() = lbl_IsIn.Text.Trim() Then
//        If sqlHelp.UpdateDeliveryOrder_Box(" IsOk=0 ", " BoxCode='" + model.BoxCode + "'") Then
//        BindOrderBox()
//        MessageBox.Show("【包装箱：" + selectBoxNo + "】已满，请扫描下一箱子！", "提示", MessageBoxButtons.OK, MessageBoxIcon.Warning)
//        Else
//        MessageBox.Show("【包装箱：" + selectBoxNo + "】【已满】状态修改失败！", "提示", MessageBoxButtons.OK, MessageBoxIcon.Warning)
//        End If
//        End If
//        Else
//        MessageBox.Show("【单机条码：" + model.DOI_NO + "】保存失败！", "提示", MessageBoxButtons.OK, MessageBoxIcon.Error)
//        End If
//        End If
//        Else
//        MessageBox.Show("该产品不在【预设发货单产品明细】中！", "提示", MessageBoxButtons.OK, MessageBoxIcon.Warning)
//        End If
//        Else
//        MessageBox.Show("【产品条码】格式不正确！", "提示", MessageBoxButtons.OK, MessageBoxIcon.Warning)
//        End If


    }

    private void handleBoxScan(String content) {
        boolean hasBox = false;
        for (SDZHBoxDetailBean boxDetailBean : boxDetailAdapter.getList()) {
            if (content.equals(boxDetailBean.getBoxCode())) {
                hasBox = true;
                break;
            }
        }

        if (!hasBox) {
            ToastUtil.showToastShort("该包装箱号不存在！");
        } else {
            singlePartBarButton.setEnabled(true);
            ToastUtil.showToastShort("请扫描单机条码！");

        }

    }

    private void handleOrderNOScan(String content) {
        boolean hasOrderNO = false;
//        for (int i = 0; i < orderNoDataLayout.getChildCount(); i++) {
//            TextView textView = (TextView) orderNoDataLayout.getChildAt(i);
//            if (content.equals(textView.getText().toString())) {
//                textView.setBackgroundColor(Color.RED);
//                hasOrderNO = true;
//                break;
//            }
//        }

        if (content.equals(orderNumberTextView.getText().toString())) {
            hasOrderNO = true;
        }


        if (!hasOrderNO) {
            ToastUtil.showToastShort("该发货单号不存在！");
        } else {
            boxBarButton.setEnabled(true);
            ToastUtil.showToastShort("请扫描包装箱！");

        }
    }


    private void parseScanResult(String content) {
        ToastUtil.showToastLong(content);
    }

    private void refreshCurrentInfo(String orderNumber, String boxNumber, int capacityNumber, int scannedNumber, String waterNumber) {
        infoTextView.setText(String.format("当前发货单号: %s    当前包装箱号: %s   \n " +
                        "收容量: %s      已扫描:  %s \n  当前产品流水号:  %s   ",
                orderNumber, boxNumber, capacityNumber, scannedNumber, waterNumber));
    }

    private void setButtonsNotEnable() {
        orderNumberButton.setEnabled(false);
        boxBarButton.setEnabled(false);
        singlePartBarButton.setEnabled(false);
        removeSinglePartBarButton.setEnabled(false);
    }

    private void setViewsListener() {
        orderNumberButton.setOnClickListener(this);
        boxBarButton.setOnClickListener(this);
        singlePartBarButton.setOnClickListener(this);
        removeSinglePartBarButton.setOnClickListener(this);
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
            currentScanState = SCAN_SINGLE_PART_CODE;
        } else if (view == removeSinglePartBarButton) {

        }
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

//                if (sendGoodsOrderNumberBeanList.size() > 1) {

                orderNoDataLayout.removeAllViews();
                for (SDZHDeliveryOrderNumberBean bean : sendGoodsOrderNumberBeanList) {
                    TextView textView = new TextView(SDZHHActivity.this);
                    textView.setText("发货单号 " + bean.getOrderNo());
                    orderNoDataLayout.addView(textView);
                    textView.setOnClickListener(v -> {
                        v.setBackgroundColor(Color.RED);
                    });
                }
                TextView firstView = (TextView) orderNoDataLayout.getChildAt(0);
                firstView.setBackgroundColor(Color.RED);
//                }


                //// TODO: 2020/2/26 这里暂时先取一个
                SDZHDeliveryOrderNumberBean bean = sendGoodsOrderNumberBeanList.get(0);
                selectOrderNO = bean.getOrderNo();
                orderNumberTextView.setText(bean.getOrderNo());

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
//                rootRecyclerView.setAdapter(sdzhOrderDetailAdapter);

                sdzhOrderDetailAdapter.setData(sendGoodsOrderNumberDetailBeanList);
                if (innerEmptyLayoutView.getVisibility() == View.VISIBLE) {
                    innerEmptyLayoutView.setVisibility(View.GONE);
                    rootRecyclerView.setVisibility(View.VISIBLE);
                }

                new GetBoxDetailAsyncTask().execute(orderNo);

            } else {
                rootRecyclerView.setVisibility(View.GONE);
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
//            String order_id = strings[0];
//            String orderNo = strings[1];

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
//                rootRecyclerView.setAdapter(sdzhOrderDetailAdapter);

                boxDetailAdapter.setData(boxBeanList);
                boxTitleTextView.setVisibility(View.VISIBLE);
                boxRecyclerView.setVisibility(View.VISIBLE);

                boxDetailBean = boxBeanList.get(0);
                selectBoxNo = boxBeanList.get(0).getBoxCode();
                new GetSinglePartDetailAsyncTask().execute(boxBeanList.get(0).getBoxCode());

                if (innerEmptyLayoutView.getVisibility() == View.VISIBLE) {
                    innerEmptyLayoutView.setVisibility(View.GONE);
                    rootRecyclerView.setVisibility(View.VISIBLE);
                }
            } else {
                rootRecyclerView.setVisibility(View.GONE);
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
//                rootRecyclerView.setAdapter(sdzhOrderDetailAdapter);

                singlePartDetailAdapter.setData(singlePartBeanList);
                if (innerEmptyLayoutView.getVisibility() == View.VISIBLE) {
                    innerEmptyLayoutView.setVisibility(View.GONE);
                    rootRecyclerView.setVisibility(View.VISIBLE);
                }

                singlePartTitleTextView.setVisibility(View.VISIBLE);
                singlePartRecyclerView.setVisibility(View.VISIBLE);
            } else {
                rootRecyclerView.setVisibility(View.GONE);
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
            progressDialog = new CommProgressDialog.Builder(SDZHHActivity.this)
                    .setTitle("正在保存单机数据..").create();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(WsResult result) {
            super.onPostExecute(result);
            if (result != null && result.getResult()){
                ToastUtil.showToastLong("保存成功！");
            }else{
                ToastUtil.showToastLong("保存失败，错误信息是 " + result.getErrorInfo());
            }

            progressDialog.dismiss();
            //isok = 0表示已经装满，不可再操作
            String updateSql = String.format("UPDATE DeliveryOrder_Box SET IsOk = 0 where BoxCode = '%s'",selectBoxNo);
            UpdateBoxAsyncTask updateBoxAsyncTask = new UpdateBoxAsyncTask();
            updateBoxAsyncTask.execute(updateSql);

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
            progressDialog = new CommProgressDialog.Builder(SDZHHActivity.this)
                    .setTitle("正在更新包装箱数据..").create();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(WsResult result) {
            super.onPostExecute(result);
            if (result != null && result.getResult()){
                ToastUtil.showToastLong("更新成功！");
            }else{
                ToastUtil.showToastLong("更新失败，错误信息是 " + result.getErrorInfo());
            }

            progressDialog.dismiss();
            singlePartBarButton.setEnabled(false);

        }
    }
}
