package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.chinashb.www.mobileerp.adapter.GoodsOrderAdapter;
import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.GoodsPurchaseOrderBean;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.AppUtil;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.JsonUtil;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.chinashb.www.mobileerp.widget.EmptyLayoutManageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2024/4/15 2:55 PM
 * @author 作者: liweifeng
 * @description 发货管理中的订单管理
 */
public class GoodsOrderManageActivity extends BaseActivity {
    @BindView(R.id.order_recyclerView) CustomRecyclerView orderRecyclerView;
    @BindView(R.id.order_empty_layoutView) EmptyLayoutManageView emptyLayoutView;
    @BindView(R.id.title_confirm_Button) Button confirmButton;
    private int toBu_ID;
    private long ivID;
    private GoodsOrderAdapter adapter;
//    private List<HashMap<Integer ,Float>> poiQuantityMapList;
    private HashMap<Long ,Float> poiQuantityMap;
    private BoxItemEntity boxItemEntity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_order_manage_layout);
        ButterKnife.bind(this);
        toBu_ID = getIntent().getIntExtra(IntentConstant.Intent_Extra_goods_order_to_bu_id, -1);
        ivID = getIntent().getLongExtra(IntentConstant.Intent_Extra_goods_order_iv_id, -1);
//        boxItemEntity = (BoxItemEntity) getIntent().getSerializableExtra(IntentConstant.Intent_Extra_send_goods_box_item_bean);
//        setContentView(R.layout.activity_goods_order_manage_layout);
//        poiQuantityMapList = new ArrayList<>();
        poiQuantityMap = new HashMap<>();
        adapter = new GoodsOrderAdapter();
        orderRecyclerView.setAdapter(adapter);
        if (toBu_ID > 0 && ivID > 0) {
            GetOrderListAsynTask task = new GetOrderListAsynTask();
            task.execute();
        } else {
            ToastUtil.showToastShort("参数有误，未能获取订单数据！");
        }
        confirmButton.setOnClickListener(v ->{
            AppUtil.forceHideInputMethod(GoodsOrderManageActivity.this);

            List<GoodsPurchaseOrderBean> beanDataList = adapter.getList();
//            poiQuantityMapList.clear();
            poiQuantityMap.clear();
            for (GoodsPurchaseOrderBean bean : beanDataList){
                if (bean != null){
//                    HashMap<Integer ,Float> map = new HashMap<>();
                    poiQuantityMap.put(Long.parseLong(bean.getPoiID() + ""),bean.getLeftQuantity());
//                    poiQuantityMapList.add(map);
                }
            }
            //// TODO: 2024/5/22 这里作一判断，如果没有填数量或者数量和标签上的不对，则给出提示
            Intent intent = new Intent(this,SendGoodsManagerActivity.class);
//            String json = JsonUtil.objectToJson(poiQuantityMapList);
            String json = JsonUtil.objectToJson(poiQuantityMap);
            intent.putExtra(IntentConstant.Intent_Extra_goods_poi_map_string,json);
            intent.putExtra(IntentConstant.Intent_Extra_goods_poi_map_order_boolean,poiQuantityMap.size() > 0);
            setResult(IntentConstant.Intent_Request_Code_Goods_Send_To_Goods_Order_Activity,intent);
            finish();
        });

    }

    private class GetOrderListAsynTask extends AsyncTask<String, Void, List<GoodsPurchaseOrderBean>> {

        @Override
        protected List<GoodsPurchaseOrderBean> doInBackground(String... strings) {

            WsResult result = WebServiceUtil.getPOISelfBuForSendGoods(toBu_ID, ivID, UserSingleton.get().getUserInfo().getBu_ID());
            List<GoodsPurchaseOrderBean> orderBeanList = null;
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                Gson gson = new Gson();
                orderBeanList = gson.fromJson(jsonData, new TypeToken<List<GoodsPurchaseOrderBean>>() {
                }.getType());
                return orderBeanList;
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<GoodsPurchaseOrderBean> goodsPurchaseOrderBeans) {
            super.onPostExecute(goodsPurchaseOrderBeans);
            if (goodsPurchaseOrderBeans != null && goodsPurchaseOrderBeans.size() > 0) {
                adapter.setData(goodsPurchaseOrderBeans);
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
