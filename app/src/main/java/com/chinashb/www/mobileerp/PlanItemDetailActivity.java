package com.chinashb.www.mobileerp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.widget.TextView;

import com.chinashb.www.mobileerp.adapter.IssuedItemAdapter;
import com.chinashb.www.mobileerp.basicobject.PlanInnerDetailEntity;
import com.chinashb.www.mobileerp.bean.PlanItemDetailBean;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.utils.IntentConstant;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2019/7/24 15:17
 * @author 作者: xxblwf
 * @description 计划条目的详情
 */

public class PlanItemDetailActivity extends BaseActivity {

    @BindView(R.id.plan_item_detail_stock_out_title_textView) TextView stockOutTextView;
    @BindView(R.id.plan_item_detail_mpi_wc_title_textView) TextView mpiWcTextView;
    @BindView(R.id.plan_item_detail_recyclerView) RecyclerView recyclerView;
//    private MpiWcBean mpiWcBean;
    private PlanItemDetailBean planItemDetailBean;
    private IssuedItemAdapter issuedItemAdapter;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_item_detail_layout);
        ButterKnife.bind(this);

//        mpiWcBean = (MpiWcBean) getIntent().getSerializableExtra(IntentConstant.Intent_Extra_mpiWcBean);
        planItemDetailBean = (PlanItemDetailBean) getIntent().getParcelableExtra(IntentConstant.Intent_PlanItemDetailBean);
        stockOutTextView.setText("投料出库 #" + planItemDetailBean.getMPIWCID());
        //// TODO: 2019/7/23
//        mpiWcBean.setMwNameTextView(mpiWcTextView);
        //// TODO: 2019/7/24 这句话影响显示
        recyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview

        mpiWcTextView.setText(Html.fromHtml(planItemDetailBean.getHtmlMwName()));
        AsyncShowIssuedMW task = new AsyncShowIssuedMW();
        task.execute();
    }

    private class AsyncShowIssuedMW extends AsyncTask<String, Void, List<PlanInnerDetailEntity>> {
        List<PlanInnerDetailEntity> planInnerDetailEntityList;
        @Override
        protected List<PlanInnerDetailEntity> doInBackground(String... params) {
            if (planItemDetailBean != null) {
                planInnerDetailEntityList = WebServiceUtil.opGetMWIssedItems((long) planItemDetailBean.getMPIWCID());
                return planInnerDetailEntityList;
            } else {

            }

            return null;
        }

        @Override
        protected void onPostExecute(List<PlanInnerDetailEntity> planInnerDetailEntityList) {
            //tv.setText(fahren + "∞ F");
            if (planInnerDetailEntityList != null) {
                issuedItemAdapter = new IssuedItemAdapter(PlanItemDetailActivity.this, planInnerDetailEntityList);
                recyclerView.setAdapter(issuedItemAdapter);
            }
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
}
