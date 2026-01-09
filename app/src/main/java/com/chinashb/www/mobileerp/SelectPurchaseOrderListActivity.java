package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.chinashb.www.mobileerp.adapter.StockInPurchaseOrderAdapter;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.StockInPurchaseOrderBean;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.warehouse.StockInActivity;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.chinashb.www.mobileerp.widget.EmptyLayoutManageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 1/7/26 3:41 PM
 * @author 作者: liweifeng
 * @description 零部件及马来采购订单由金蝶生成，所以在入库的时候先选金蝶，避免重复创建
 */
public class SelectPurchaseOrderListActivity extends BaseActivity {
    @BindView(R.id.purchase_order_title_confirm_Button) Button confirmButton;
    //    @BindView(R.id.select_purchase_order_title_manageView) TitleLayoutManagerView titleManageView;
    @BindView(R.id.purchase_order_recyclerView) CustomRecyclerView orderRecyclerView;
    @BindView(R.id.purchase_order_empty_layoutView) EmptyLayoutManageView emptyLayoutView;
    private int toBu_ID;
    private long itemID;
    private StockInPurchaseOrderAdapter adapter;
    private StockInPurchaseOrderBean tempDpOrderDetailBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_purchase_order_list_layout);
        ButterKnife.bind(this);
        toBu_ID = getIntent().getIntExtra(IntentConstant.Intent_Extra_MY_Purchase_Order_Bu_ID, -1);
        itemID = getIntent().getLongExtra(IntentConstant.Intent_Extra_MY_Purchase_Order_Item_ID, -1);
        adapter = new StockInPurchaseOrderAdapter();
        orderRecyclerView.setAdapter(adapter);
        adapter.setOnViewClickListener(new OnViewClickListener() {
            @Override
            public <T> void onClickAction(View v, String tag, T t) {
                if (t != null) {
                    tempDpOrderDetailBean = (StockInPurchaseOrderBean) t;
                    jumpBackToStockInActivity();
                } else {
                    tempDpOrderDetailBean = null;
                    ToastUtil.showToastShort("获取采购订单失败！");
                    finish();
                }
            }
        });
        if (toBu_ID > 0 && itemID > 0) {
            getPOList(itemID, toBu_ID);
        } else {
            ToastUtil.showToastShort("参数有误，未能获取采购订单数据！");
        }
        confirmButton.setOnClickListener(v -> {

            jumpBackToStockInActivity();
        });

    }

    private void jumpBackToStockInActivity() {
        Intent intent = new Intent(this, StockInActivity.class);
        intent.putExtra(IntentConstant.Intent_Extra_MY_Purchase_Order_bean, tempDpOrderDetailBean);
        setResult(IntentConstant.Intent_Request_Stock_in_To_Purchase_Order_Activity, intent);
        finish();
    }

    private void getPOList(long item_id, int buID) {
        GetPOOrderListAsyncTask task = new GetPOOrderListAsyncTask();
        task.execute(item_id + "", buID + "");
    }


    private class GetPOOrderListAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String sitem_ID = strings[0];
            String sBu_ID = strings[1];
            String sql = String.format("SELECT POI.POI_ID,POI.PO_ID\n" +
                    "  ,Case When PO.Ver=1 Then PO_No Else POR.Release_No End As PO_No\n" +
                    "  ,Case When PO.Ver=1 Then PO_Date Else POR.Release_Date End As 下单日期\n" +
                    "  ,Item.KisCode,Item.Item_ID,Item.Item As 物料编码, Item.Item_Name As 物料,Item.Item_Spec2 As 规格,Item_Version.Item_Version As 版本,Item.Item_Unit As 单位\n" +
                    "  ,POI.POI_Quantity AS 采购数量,POI.POI_In_Qty AS 已关联数量,POI.POI_Quantity-ISNULL(POI.POI_In_Qty,0) AS 未关联数量,POI.ML_Kis_BillNo AS 金蝶采购单号\n" +
                    "  FROM Purchase_Order_Item AS POI\n" +
                    "  INNER JOIN Purchase_Order AS PO ON PO.PO_ID=POI.PO_ID\n" +
                    "  Inner join Item_Version On Item_Version.IV_ID=POI.IV_ID \n" +
                    "  Inner Join Item On Item_Version.Item_ID=Item.Item_ID\n" +
                    "  Left Join Purchase_Order_Release As POR On POR.POR_ID = POI.POR_ID\n" +
                    "  WHERE PO.BU_ID=%s AND POI.Item_ID=%s AND POI.PO_Status_ID IN(1,2)\n" +
                    "   AND POI.ML_Kis_FID>0 AND POI_Quantity<>POI_In_Qty AND PO_Date>='2025-12-01'", sBu_ID, sitem_ID);

            WsResult result = WebServiceUtil.getDataTable(sql);
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                System.out.println("============================jsonData = " + jsonData);
                if (!TextUtils.isEmpty(jsonData)) {
                    return jsonData;

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String json) {
            if (!TextUtils.isEmpty(json) && !json.trim().equals("[]")) {
                Gson gson = new Gson();
                List<StockInPurchaseOrderBean> orderBeanList = gson.fromJson(json, new TypeToken<List<StockInPurchaseOrderBean>>() {
                }.getType());
//                return orderBeanList;
                if (orderBeanList != null && orderBeanList.size() > 0) {
                    adapter.setData(orderBeanList);
                    orderRecyclerView.setVisibility(View.VISIBLE);
                    emptyLayoutView.setVisibility(View.GONE);
                } else {
                    ToastUtil.showToastShort("没有获取到相关数据！");
                    emptyLayoutView.setVisibility(View.VISIBLE);
                    orderRecyclerView.setVisibility(View.GONE);
                }
            }
        }
    }


}

