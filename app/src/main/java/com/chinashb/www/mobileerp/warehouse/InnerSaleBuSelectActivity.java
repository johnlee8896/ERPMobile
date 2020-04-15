package com.chinashb.www.mobileerp.warehouse;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.InnerSaleSelectBuAdapter;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.InnerSelectBuBean;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2020/4/14 14:20
 * @author 作者: xxblwf
 * @description 内部调拨，车间选择
 */

public class InnerSaleBuSelectActivity extends BaseActivity {

    @BindView(R.id.inner_sale_out_bu_customerRecyclerView) CustomRecyclerView recyclerView;
    private InnerSaleSelectBuAdapter adapter;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_sale_bu_select_layout);
        ButterKnife.bind(this);

        adapter = new InnerSaleSelectBuAdapter();
        adapter.setOnViewClickListener(new OnViewClickListener() {
            @Override public <T> void onClickAction(View v, String tag, T t) {
                InnerSelectBuBean bean = (InnerSelectBuBean) t;
                if (bean != null) {
                    Intent intent = new Intent(InnerSaleBuSelectActivity.this, InnerSaleOutActivity.class);
                    intent.putExtra(IntentConstant.Intent_Extra_select_bu_bean, bean);
                    setResult(IntentConstant.Intent_Request_Code_Sale_Out_to_Bu, intent);
                    finish();
                }
            }
        });
        recyclerView.setAdapter(adapter);
//        int buId = getIntent().getIntExtra(IntentConstant.Intent_Extra_current_bu_id,0);
//        if (buId != 0){
        GetBuListAsyncTask task = new GetBuListAsyncTask();
        task.execute();
//        }


    }

    private class GetBuListAsyncTask extends AsyncTask<String, Void, Void> {
        List<InnerSelectBuBean> buBeanList;

        @Override
        protected Void doInBackground(String... params) {
//            String buId = params[0];
            String sql = String.format("Select CF_ID,Company.Company_Chinese_Name , Bu_Name From Bu  Inner Join [Company]  With (NoLock)  On [Bu].[Company_ID]=[Company].[Company_ID] " +
                            " Inner Join [Customer_Facility] With (NoLock)  On [Bu].[ID_Customer]=[Customer_Facility].[CF_ID] " +
                            "Where Bu.Enabled=1 And Bu.Company_ID<>%s And (Bu.Has_Part_Account=1 Or Bu.Has_Product_WareHouse_Account=1)  And Isnull(Bu.Is_Wujin,0)=0  Order By Bu.Company_ID, Bu.Bu_ID ",
                    UserSingleton.get().getUserInfo().getBu_ID());
            WsResult result = WebServiceUtil.getDataTable(sql);
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                Gson gson = new Gson();
                buBeanList = gson.fromJson(jsonData, new TypeToken<List<InnerSelectBuBean>>() {
                }.getType());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (buBeanList != null && buBeanList.size() > 0) {
                adapter.setData(buBeanList);
            }
        }

    }
}
