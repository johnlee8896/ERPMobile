package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chinashb.www.mobileerp.adapter.DeliveryOrderAdapter;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.DeliveryOrderBean;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.UnitFormatUtil;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.chinashb.www.mobileerp.widget.EmptyLayoutManageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2020/1/3 9:19
 * @author 作者: xxblwf
 * @description 发货指令页面
 */

public class DeliveryOrderActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.delivery_order_week_ago_button) TextView weekAgoButton;
    @BindView(R.id.delivery_order_before_yesterday_button) TextView beforeYesterdayButton;
    @BindView(R.id.delivery_order_yesterday_button) TextView yesterdayButton;
    @BindView(R.id.delivery_order_today_button) TextView todayButton;
    @BindView(R.id.delivery_order_tomorrow_button) TextView tomorrowButton;
    @BindView(R.id.delivery_order_customRecyclerView) CustomRecyclerView recyclerView;
    @BindView(R.id.delivery_order_emptyLayoutView) EmptyLayoutManageView emptyLayoutView;
    @BindView(R.id.delivery_order_sdzh_button) TextView sdzhButton;

    private DeliveryOrderAdapter adapter;
    private DeliveryOrderBean deliveryOrderBean;
    private OnViewClickListener onViewClickListener = new OnViewClickListener() {
        @Override
        public <T> void onClickAction(View v, String tag, T t) {
            if (t != null && t instanceof DeliveryOrderBean) {
                Intent intent = new Intent(DeliveryOrderActivity.this, ProductSaleOutActivity.class);
                intent.putExtra(IntentConstant.Intent_product_delivery_order_bean, (DeliveryOrderBean) t);
                setResult(IntentConstant.Intent_Request_Code_Product_Out_And_Delivery_Order, intent);
                finish();

                //// TODO: 2020/2/26
//                deliveryOrderBean = (DeliveryOrderBean) t;

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_order_layout);
        ButterKnife.bind(this);

        initView();
        setViewsListener();

        long currentTime = System.currentTimeMillis();
        String todayYMD = UnitFormatUtil.formatTimeToDay(currentTime);
        GetDeliveryOrderAsyncTask task = new GetDeliveryOrderAsyncTask();
        task.execute(todayYMD, todayYMD);
        todayButton.setSelected(true);
        todayButton.setTextColor(getResources().getColor(R.color.color_orange_FF6A00));
    }

    private void initView() {
        adapter = new DeliveryOrderAdapter();
        adapter.setOnViewClickListener(onViewClickListener);
        recyclerView.setAdapter(adapter);
    }

    private void setViewsListener() {
        weekAgoButton.setOnClickListener(this);
        beforeYesterdayButton.setOnClickListener(this);
        yesterdayButton.setOnClickListener(this);
        todayButton.setOnClickListener(this);
        tomorrowButton.setOnClickListener(this);
        sdzhButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        String todayYMD = UnitFormatUtil.formatTimeToDay(currentTime);
        GetDeliveryOrderAsyncTask task = new GetDeliveryOrderAsyncTask();
        weekAgoButton.setTextColor(Color.DKGRAY);
        beforeYesterdayButton.setTextColor(Color.DKGRAY);
        yesterdayButton.setTextColor(Color.DKGRAY);
        todayButton.setTextColor(Color.DKGRAY);
        tomorrowButton.setTextColor(Color.DKGRAY);
        //// TODO: 2020/1/3 有待检查和优化
        if (v == weekAgoButton) {//pc端处理是-3，-7
//            long startTime = currentTime - UnitFormatUtil.ONE_DAY_TIME_IN_MILL_SECOND * 8;
//            long endTime = currentTime - UnitFormatUtil.ONE_DAY_TIME_IN_MILL_SECOND;

            long startTime = currentTime - UnitFormatUtil.ONE_DAY_TIME_IN_MILL_SECOND * 7;
            long endTime = currentTime - UnitFormatUtil.ONE_DAY_TIME_IN_MILL_SECOND * 3;
//            task.execute(UnitFormatUtil.formatTimeToDay(startTime), UnitFormatUtil.formatTimeToDay(endTime));
            //// TODO: 2020/4/12 对于范围，这里真的处理不同寻常，先end，后start
            task.execute(UnitFormatUtil.formatTimeToDay(endTime), UnitFormatUtil.formatTimeToDay(startTime));
            weekAgoButton.setTextColor(getResources().getColor(R.color.color_orange_FF6A00));
        } else if (v == beforeYesterdayButton) {
//            long startTime = currentTime - UnitFormatUtil.ONE_DAY_TIME_IN_MILL_SECOND * 3;
//            long endTime = currentTime - UnitFormatUtil.ONE_DAY_TIME_IN_MILL_SECOND * 2;
////            task.execute(UnitFormatUtil.formatTimeToDay(startTime), UnitFormatUtil.formatTimeToDay(endTime));
//            task.execute(UnitFormatUtil.formatTimeToDay(startTime), UnitFormatUtil.formatTimeToDay(startTime));



            long startTime = currentTime - UnitFormatUtil.ONE_DAY_TIME_IN_MILL_SECOND * 2;
            long endTime = currentTime - UnitFormatUtil.ONE_DAY_TIME_IN_MILL_SECOND;
//            task.execute(UnitFormatUtil.formatTimeToDay(startTime), UnitFormatUtil.formatTimeToDay(endTime));
            task.execute(UnitFormatUtil.formatTimeToDay(startTime), UnitFormatUtil.formatTimeToDay(startTime));


            beforeYesterdayButton.setTextColor(getResources().getColor(R.color.color_orange_FF6A00));
        } else if (v == yesterdayButton) {
            long startTime = currentTime - UnitFormatUtil.ONE_DAY_TIME_IN_MILL_SECOND ;
            long endTime = currentTime - UnitFormatUtil.ONE_DAY_TIME_IN_MILL_SECOND;
//            task.execute(UnitFormatUtil.formatTimeToDay(startTime), UnitFormatUtil.formatTimeToDay(endTime));
            task.execute(UnitFormatUtil.formatTimeToDay(startTime), UnitFormatUtil.formatTimeToDay(startTime));
            yesterdayButton.setTextColor(getResources().getColor(R.color.color_orange_FF6A00));
        } else if (v == todayButton) {
            task.execute(todayYMD, todayYMD);
            todayButton.setTextColor(getResources().getColor(R.color.color_orange_FF6A00));
        } else if (v == tomorrowButton) {
//            long startTime = currentTime - UnitFormatUtil.ONE_DAY_TIME_IN_MILL_SECOND * 8;
            long endTime = currentTime + UnitFormatUtil.ONE_DAY_TIME_IN_MILL_SECOND;
            task.execute(UnitFormatUtil.formatTimeToDay(endTime), UnitFormatUtil.formatTimeToDay(endTime));
            tomorrowButton.setTextColor(getResources().getColor(R.color.color_orange_FF6A00));
        } else if (v == sdzhButton) {
//            if (deliveryOrderBean != null && deliveryOrderBean.getDOID() != 0) {
//                Intent intent = new Intent(this, SDZHHActivity.class);
//                intent.putExtra(IntentConstant.Intent_Extra_do_id, deliveryOrderBean.getDOID() + "");
//                startActivity(intent);
//            }
        }
    }

    /**
     * 根据时间获取发货指令列表
     */
    private class GetDeliveryOrderAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String startDate = strings[0];
            String endDate = strings[1];
            String sql = String.format("Select D.DO_ID,  D.TrackNo, D.SpecificTime , D.Delivery_Date , D.Arrive_Date, F.CF_Chinese_Name , Des_Info ,  Special , D.Done ,D.Part_Done A FROM DP_Order As D\n" +
                    "Inner Join Customer_Facility As F On D.CF_ID=F.CF_ID \n" +
                    "Where D.Bu_ID=%s And D.Deleted=0 And Datediff(day,D.Delivery_Date,'%s') >= 0 And Datediff(day,D.Delivery_Date,'%s') <= 0 And Isnull(D.Done,0)=0 \n" +
                    "Order By D.Delivery_Date, D.DO_ID", UserSingleton.get().getUserInfo().getBu_ID(), startDate, endDate);
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
            List<DeliveryOrderBean> deliveryOrderBeanList = gson.fromJson(jsonData, new TypeToken<List<DeliveryOrderBean>>() {
            }.getType());
//                    DeliveryOrderBean bean = JsonUtil.parseJsonToObject(jsonData,new TypeToken<List<DeliveryOrderBean>>(){});
            if (deliveryOrderBeanList != null && deliveryOrderBeanList.size() > 0) {
                //// TODO: 2020/2/26
                deliveryOrderBean = deliveryOrderBeanList.get(0);



                adapter.setData(deliveryOrderBeanList);
                if (emptyLayoutView.getVisibility() == View.VISIBLE) {
                    emptyLayoutView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            } else {
                recyclerView.setVisibility(View.GONE);
                emptyLayoutView.setVisibility(View.VISIBLE);
            }

        }
    }

    /**
     * 获取发货指令详情
     */
//    private class GetDeliveryOrderItemAsyncTask extends AsyncTask<String, String, String> {
//
//        @Override
//        protected String doInBackground(String... doId) {
//            String sql = String.format("Select D.* FROM DPI As D With (NoLock) Where D.Deleted=0 And D.Do_ID = %s", doId);
//            WsResult result = WebServiceUtil.getDataTable(sql);
//            if (result != null && result.getResult()) {
//                String jsonData = result.getErrorInfo();
//                if (!TextUtils.isEmpty(jsonData)) {
//                    return jsonData;
//                }
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String jsonData) {
//            super.onPostExecute(jsonData);
//            Gson gson = new Gson();
//            List<DeliveryOrderBean> deliveryOrderBeanList = gson.fromJson(jsonData, new TypeToken<List<DeliveryOrderBean>>() {
//            }.getType());
////                    DeliveryOrderBean bean = JsonUtil.parseJsonToObject(jsonData,new TypeToken<List<DeliveryOrderBean>>(){});
//            if (deliveryOrderBeanList != null && deliveryOrderBeanList.size() > 0) {
//                adapter.setData(deliveryOrderBeanList);
//            }
//
//        }
//    }


}
