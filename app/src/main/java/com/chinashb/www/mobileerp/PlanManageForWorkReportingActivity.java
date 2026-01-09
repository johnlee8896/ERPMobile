package com.chinashb.www.mobileerp;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chinashb.www.mobileerp.adapter.WCListAdapter;
import com.chinashb.www.mobileerp.basicobject.MpiWcBean;
import com.chinashb.www.mobileerp.basicobject.PlanInnerDetailEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.basicobject.s_WCList;
import com.chinashb.www.mobileerp.bean.PlanItemDetailBean;
import com.chinashb.www.mobileerp.funs.OnItemClickListener;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.StaticVariableUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 7/17/25 2:39 PM
 * @author 作者: liweifeng
 * @description 计划管理-给报工专用
 */
public class PlanManageForWorkReportingActivity extends BaseActivity implements View.OnClickListener {
    //    @BindView(R.id.plan_for_reportingtitle_layoutManager) TitleLayoutManagerView planForReportingtitleLayoutManager;
    @BindView(R.id.plan_for_reportingpart_product_switch_textView) TextView switchTextView;
    @BindView(R.id.plan_for_reportingmain_recyclerView) RecyclerView selectWCListRecyclerView;
    @BindView(R.id.plan_for_reportingpart_select_plan_textView) TextView selectPlanTextView;
    @BindView(R.id.plan_for_reportingpart_plan_show_textView) TextView planShowTextView;
    private WCListAdapter wclAdapter;
//    private boolean isCurrentPart = true;//默认是部件列表
    private boolean isCurrentPart = false;//默认是成品列表
    private List<s_WCList> wcLists;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_manage_for_reporting_layout);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentConstant.Intent_Request_Code_Plan_Select_to_Work_Reporting_Activity){
            if (data != null) {
//                if (!TextUtils.isEmpty(result.getContents())) {
//                    parseContent(result.getContents());
//                }
                PlanItemDetailBean planItemDetailBean = data.getParcelableExtra(IntentConstant.Intent_PlanItemDetailBean);
                if (planItemDetailBean != null){
                    planShowTextView.setText(Html.fromHtml(planItemDetailBean.getHtmlMwName()));
                    selectWCListRecyclerView.setVisibility(View.GONE);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initView() {
        switchTextView.setOnClickListener(this);
        selectPlanTextView.setOnClickListener(this);
        GetWCListsAsyncTask getWCListsAsyncTask = new GetWCListsAsyncTask();
        getWCListsAsyncTask.execute();
    }

    @Override
    public void onClick(View v) {
        if (v == switchTextView) {
            if (TextUtils.equals("切换为成品", switchTextView.getText())) {
                switchTextView.setText("切换为部件");
                isCurrentPart = false;
            } else {
                switchTextView.setText("切换为成品");
                isCurrentPart = true;
            }
            List<s_WCList> tempList = getCurrentShowList();
//            wclAdapter = new WCListAdapter(PlanManageForWorkReporting.this, wcLists);
            wclAdapter = new WCListAdapter(PlanManageForWorkReportingActivity.this, tempList);
            selectWCListRecyclerView.setAdapter(wclAdapter);
            wclAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void OnItemClick(View view, int position) {
//                    s_WCList wcEntity = wcLists.get(position);
                    s_WCList wcEntity = wclAdapter.getDataList().get(position);
                    //存下来,重选了wcs_list, 需要清除已选生产线
                    StaticVariableUtils.selected_list = wcEntity;
                    StaticVariableUtils.selectedWorkCenter = null;
                    doNextStepSelectWc(wcEntity);

                }
            });
        }else if(v == selectPlanTextView){
            if (selectWCListRecyclerView.getVisibility() == View.GONE){
                selectWCListRecyclerView.setVisibility(View.VISIBLE);
            }
        }

    }

    private class GetWCListsAsyncTask extends AsyncTask<String, Void, Void> {
        MpiWcBean scanresult;
        List<PlanInnerDetailEntity> li;

        @Override
        protected Void doInBackground(String... params) {
            String sql = "Select Case When Ac_Type=1 Then '部件' When Ac_Type=2 Then '成品' Else '' End As WCL_Type, LID, Bu_ID, ListName From WC_List Where Bu_ID="
                    + UserSingleton.get().getUserInfo().getBu_ID() + " Order By Case When Ac_Type=1 Then '部件' When Ac_Type=2 Then '成品' Else '' End";
            WsResult result = WebServiceUtil.getDataTable(sql);
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                ArrayList<s_WCList> wcList = new ArrayList<s_WCList>();
                Gson gson = new Gson();
                wcList = gson.fromJson(jsonData, new TypeToken<List<s_WCList>>() {
                }.getType());
                wcLists = wcList;

            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");
            List<s_WCList> tempList = getCurrentShowList();
//            wclAdapter = new WCListAdapter(PlanManageForWorkReporting.this, wcLists);
            wclAdapter = new WCListAdapter(PlanManageForWorkReportingActivity.this, tempList);
            selectWCListRecyclerView.setLayoutManager(new LinearLayoutManager(PlanManageForWorkReportingActivity.this));
            selectWCListRecyclerView.setAdapter(wclAdapter);

            wclAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void OnItemClick(View view, int position) {
//                    s_WCList wcEntity = wcLists.get(position);
                    s_WCList wcEntity = wclAdapter.getDataList().get(position);
                    //存下来,重选了wcs_list, 需要清除已选生产线
                    StaticVariableUtils.selected_list = wcEntity;
                    StaticVariableUtils.selectedWorkCenter = null;
                    doNextStepSelectWc(wcEntity);

                }
            });

            //如果出库主页上的生产线组曾选过，就直接往下点，模拟到下一步
            //// TODO: 2019/7/22 这个与另外一个计划是否会有冲突
//            if (StaticVariableUtils.selected_list != null) {
//                s_WCList wcEntity = StaticVariableUtils.selected_list;
//                doNextStepSelectWc(wcEntity);
//            }

        }

    }

    private void doNextStepSelectWc(s_WCList wcEntity) {
//        Intent intent = new Intent(PlanManageForWorkReporting.this, SelectMPIWCStepTwoActivity.class);
        Intent intent = new Intent(PlanManageForWorkReportingActivity.this, PlanShowListActivity.class);
        intent.putExtra("wclist", (Serializable) wcEntity);
        intent.putExtra(IntentConstant.Intent_Extra_Plan_Select_to_Work_Reporting_boolean, true);
        startActivityForResult(intent, IntentConstant.Intent_Request_Code_Plan_Select_to_Work_Reporting_Activity);
//        startActivity(intent);
    }

    private List<s_WCList> getCurrentShowList() {
        List<s_WCList> tempList = new ArrayList<>();
        for (s_WCList bean : wcLists) {
            if (isCurrentPart) {
                if (bean.getWCL_Type().equals("部件")) {
                    tempList.add(bean);
                }
            } else {
                if (bean.getWCL_Type().equals("成品")) {
                    tempList.add(bean);
                }
            }
        }
        return tempList;
    }
}



