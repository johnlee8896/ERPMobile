package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chinashb.www.mobileerp.adapter.WCListAdapter;
import com.chinashb.www.mobileerp.basicobject.PlanInnerDetailEntity;
import com.chinashb.www.mobileerp.basicobject.MpiWcBean;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.basicobject.s_WCList;
import com.chinashb.www.mobileerp.funs.OnItemClickListener;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.StaticVariableUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2019/7/19 16:20
 * @author 作者: xxblwf
 * @description 计划管理页面
 */

public class PlanManagerActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.plan_part_product_switch_textView) TextView switchTextView;
    private WCListAdapter wclAdapter;
    @BindView(R.id.plan_main_recyclerView) RecyclerView selectWCListRecyclerView;
    private boolean isCurrentPart = true;//默认是部件列表
    private List<s_WCList> wcLists;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_manage_layout);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        switchTextView.setOnClickListener(this);
        GetWCListsAsyncTask getWCListsAsyncTask = new GetWCListsAsyncTask();
        getWCListsAsyncTask.execute();
    }

    @Override public void onClick(View v) {
        if (v == switchTextView) {
            if (TextUtils.equals("切换为成品", switchTextView.getText())) {
                switchTextView.setText("切换为部件");
                isCurrentPart = false;
            } else {
                switchTextView.setText("切换为成品");
                isCurrentPart = true;
            }
            List<s_WCList> tempList = getCurrentShowList();
//            wclAdapter = new WCListAdapter(PlanManagerActivity.this, wcLists);
            wclAdapter = new WCListAdapter(PlanManagerActivity.this, tempList);
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
//            wclAdapter = new WCListAdapter(PlanManagerActivity.this, wcLists);
            wclAdapter = new WCListAdapter(PlanManagerActivity.this, tempList);
            selectWCListRecyclerView.setLayoutManager(new LinearLayoutManager(PlanManagerActivity.this));
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

    private void doNextStepSelectWc(s_WCList wcEntity) {
//        Intent intent = new Intent(PlanManagerActivity.this, SelectMPIWCStepTwoActivity.class);
        Intent intent = new Intent(PlanManagerActivity.this, PlanShowListActivity.class);
        intent.putExtra("wclist", (Serializable) wcEntity);
//        startActivityForResult(intent, 100);
        startActivity(intent);
    }

    private List<s_WCList> getCurrentShowList() {
        List<s_WCList> tempList = new ArrayList<>();
        for (s_WCList bean : wcLists) {
            if (isCurrentPart) {
                if (bean.getWCL_Type().equals("部件")){
                    tempList.add(bean);
                }
            }else{
                if (bean.getWCL_Type().equals("成品")){
                    tempList.add(bean);
                }
            }
        }
        return tempList;
    }
}


