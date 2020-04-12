package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.chinashb.www.mobileerp.adapter.LogisticsSelectAdapter;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.DpOrderDetailBean;
import com.chinashb.www.mobileerp.bean.LogisticsSelectBean;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2020/4/8 9:54
 * @author 作者: xxblwf
 * @description 物流选择页面
 */

public class LogisticsSelectActivity extends BaseActivity {

    @BindView(R.id.logistics_select_customRecyclerView) CustomRecyclerView recyclerView;

    private LogisticsSelectAdapter adapter;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logistics_select_layout);
        ButterKnife.bind(this);

        adapter = new LogisticsSelectAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setOnViewClickListener(new OnViewClickListener() {
            @Override public <T> void onClickAction(View v, String tag, T t) {
                if (t != null){
                    LogisticsSelectBean bean = (LogisticsSelectBean) t;
                    Intent intent = new Intent(LogisticsSelectActivity.this,LogisticsManageActivity.class);
                    intent.putExtra(IntentConstant.Intent_Extra_logistics_select_bean,bean);
                    setResult(IntentConstant.Intent_Request_Code_Logistics_Select_to_Logistics,intent);
                    finish();
                }
            }
        });

        GetLogisticsListsAsyncTask task = new GetLogisticsListsAsyncTask();
        task.execute();
    }


    private class GetLogisticsListsAsyncTask extends AsyncTask<String, Void, Void> {
        List<LogisticsSelectBean> beanList;
        @Override
        protected Void doInBackground(String... params) {
            //// TODO: 2019/12/20  注意这里的第二个参数%s有所修改
//            String sql = String.format("Select D.* FROM DPI As D With (NoLock) Where D.Deleted=0 And D.Do_ID= %s", deliveryOrderBean.getDOID());
            String sql = "  select top 10 Delivery_ID,Abroad,Mass,account_delivery.CF_ID,Delivery_NO,Delivery_Date,dt.delivery,account_delivery.LC_ID,lc.lc_name,AWarehouse,AW_ID,Ac_Title_ID,Arrive_Date,Shiping_Date,Company_ID,Bu_ID,account_delivery.DT_ID," +
                    " Contract_No,Replenish,TrackNo,LoadTime,DriverName,DriverTel,account_delivery.Remark,Des_Address,License_Plate_NO,IsBackUp,IsReturn,Truck_Left, customer_facility.cf_chinese_name from account_delivery " +
                    " inner join customer_facility on account_delivery.CF_ID = customer_facility.cf_id " +
                    " left join lc on account_delivery.lc_id = lc.LC_ID " +
                    " left join dt on account_delivery.dt_id = dt.dt_id " +
                    " order by Insert_Time desc";
            WsResult result = WebServiceUtil.getDataTable(sql);
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                Gson gson = new Gson();
                beanList = gson.fromJson(jsonData, new TypeToken<List<LogisticsSelectBean>>() {
                }.getType());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (beanList != null && beanList.size() > 0) {
                adapter.setData(beanList);
            }
        }

    }
}
