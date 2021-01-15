package com.chinashb.www.mobileerp;

/***
 * @date 创建时间 2021/1/14 21:48
 * @author 作者: xxblwf
 * @description 任务管理页面
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;


import com.chinashb.www.mobileerp.adapter.TaskJsonAdapter;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.TaskBean;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.task.TaskDetailActivity;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.widget.CommProgressDialog;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.chinashb.www.mobileerp.widget.EmptyLayoutManageView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskMainNewActivity extends BaseActivity {

    @BindView(R.id.navigation_task) BottomNavigationView navigation;
    @BindView(R.id.fab_new_task_button) FloatingActionButton fabNewTaskButton;
    @BindView(R.id.rv_task_list) CustomRecyclerView recyclerView;
    @BindView(R.id.task_emptyManager) EmptyLayoutManageView emptyManageView;
    //    public List<Integer> ColWidth;
//    public List<String> ColCaption;
//    public List<String> HiddenCol;
//    RecyclerView recyclerView;
//    private EmptyLayoutManageView emptyManageView;
    private int tasks_type = 0; //0:综合 1:mysent   2:myexe 3:mydep
    //    private List<JsonObject> beanList;
    private List<TaskBean> beanList;
    //    private TaskJsonListAdapter ObjectAdapter;
    private JsonObject Selected_Object;
    private HashMap<String, String> Result;
    private TaskJsonAdapter adapter;

//    int HR_ID;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_task_myall:
                    tasks_type = 0;
                    LoadTasks();
                    return true;
                case R.id.navigation_task_mysent:
                    tasks_type = 1;
                    LoadTasks();
                    return true;
                case R.id.navigation_task_myexe:
                    tasks_type = 2;
                    LoadTasks();
                    return true;
                case R.id.navigation_task_mydep:
                    tasks_type = 3;
                    LoadTasks();
                    return true;

            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_layout);
        ButterKnife.bind(this);

//        HR_ID = UserSingleton.get().getHRID();


        setHomeButton();

//        recyclerView = (RecyclerView) findViewById(R.id.rv_task_list);
//        emptyManageView = (EmptyLayoutManageView) findViewById(R.id.task_emptyManager);
        beanList = new ArrayList<>();
//        TaskJsonListAdapter jsonListAdapter = new TaskJsonListAdapter(this, beanList);
//        recyclerView.setAdapter(jsonListAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskJsonAdapter();
        recyclerView.setAdapter(adapter);

//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation_task);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        tasks_type = 0;
        LoadTasks();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setHomeButton() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    protected void LoadTasks() {

        GetTaskAsyncTask task = new GetTaskAsyncTask();
        task.execute();

    }

    private CommProgressDialog progressDialog;

    private class GetTaskAsyncTask extends AsyncTask<String, Void, List<TaskBean>> {

        @Override
        protected List<TaskBean> doInBackground(String... params) {


            switch (tasks_type) {
                case 0:
                    beanList = loadTasksMyAllData();
                    break;
                case 1:
                    beanList = loadTasksMySentData();
                    break;
                case 2:
                    beanList = loadTasksMyExeData();
                    break;
                case 3:
                    beanList = loadTasksMyDepOnGoingData();
                    break;

            }

            return beanList;
        }

        @Override
        protected void onPreExecute() {
            //pbScan.setVisibility(View.VISIBLE);
            if (progressDialog == null) {
                progressDialog = new CommProgressDialog.Builder(TaskMainNewActivity.this).setTitle("正在获取数据").create();
            }
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(List<TaskBean> beanList) {


            //pbScan.setVisibility(View.INVISIBLE);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            bindObjectListsToAdapter(beanList);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

        protected List<TaskBean> loadTasksMyAllData() {

            String sqlSelect = getSqlTaskSelectField();

            String sqlFrom = getSqlTaskFrom();

            String sqlWhere = getSqlTaskMyConcernWhere();

            String Sql = sqlSelect + sqlFrom + sqlWhere;

//            beanList = WebServiceUtil.getJsonList(Sql);


            WsResult result = WebServiceUtil.getDataTable(Sql);
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
//                ArrayList<WcIdNameEntity> us;
                Gson gson = new Gson();
                beanList = gson.fromJson(jsonData, new TypeToken<List<TaskBean>>() {
                }.getType());
//                beanList = us;
//                adapter.setData(beanList);

            }

            getUserPics();
            if (beanList != null && beanList.size() > 0) {
                beanList.subList(0, beanList.size() > 20 ? 19 : beanList.size());
            }
            return beanList;

        }

        //todo 不理解
        private String getSqlTaskSelectField() {
            String sql = " Select (Case When BigJob=0 Then 0 Else TTask_Ver.TTP_ID End) As TTP_ID, " +
                    "~ Isnull(TTask_Exer.Confirmed,1) As New, " +
                    "Case When TTPlan.Is_From_Business_System=1 And Business_System_ID=4 Then PlanName + char(10) +LTrim(TTask_Ver.Title) Else LTrim(TTask_Ver.Title) End As TaskTitle, " +
                    "TTask.TID As TID, TTask_Ver.TVID, " +
                    "        Convert(nvarchar(100),TTask_Ver.Create_Time,20) As Create_Time, " +
                    "Convert(nvarchar(100),TTask_Ver.Start_Time,23) As Start_Time, " + "" +
                    "Convert(nvarchar(100),TTask_Ver.End_Time,23) As End_Time , " +
                    "        TTask.Creater as Creater_HRID,TTAsk_Ver.Owner As CreaterName, TTask_Ver.Exer As Responder, TTask_Ver.Auditor As Auditor, " +
                    "Isnull(TTask_Ver.Percentage,0) As Percentage, TTask_Ver.Remark As Remark, " +
                    "        (Select Max(access_time) From TTask_Access Where Visitor=" + UserSingleton.get().getHRID() + " Group by Tid having Tid=TTask.Tid) As LastAccessTime ";
            return sql;
        }

        private String getSqlTaskFrom() {
            String From = " From TTask_Ver " +
                    "inner join ttask On ttask_ver.tid= ttask.tid And ttask_ver.tvid= ttask.current_tvid " +
                    "inner Join ttplan On ttplan.ttp_id= ttask_ver.ttp_id " +
                    "inner Join ttask_priority On ttask_ver.priority= ttask_priority.ttpr_Id " +
                    "inner join ttask_status On ttask_ver.status = ttask_status.tts_id " +
                    "Left Join TTask_Check On TTask_Ver.LastAuditID=TTask_Check.TTCR_ID " +
                    "Left Join [TTask_Exer] On [TTask].[TID]=[TTask_Exer].[TID] " +
                    " And TTask_Exer.Exer=" + UserSingleton.get().getHRID() + " And Isnull(TTask_Exer.Quit,0)=0 ";
            return From;
        }

        private String getSqlTaskMyConcernWhere() {
            int HR_ID = UserSingleton.get().getHRID();
            String sql_get_task_finished_but_i_not_know = getSqlDoneINotKnow();

            List<JsonObject> tasks_finished_i_not_know = WebServiceUtil.getJsonList(sql_get_task_finished_but_i_not_know);

            String IDs = CommonUtil.getIDSqlStringFromJsonList(tasks_finished_i_not_know, "TID");

            String Where = " Where (Not Isnull(TTPlan.Closed,0)=1 And " +
                    "((TTask.TID In (Select tk.TID from ttask As tk inner join TTPlan As tn On tk.ttp_id=tn.ttp_id where tk.Creater =" + HR_ID + " And tn.BigJob =0)) " +
                    " Or  (TTask_Ver.Exer_Dep_ID In (Select Distinct Department_ID From HR_Adm Where Hr_ID= " + HR_ID + ") And isnull(TTask_Ver.Exer,'')='') " +
                    "Or  (TTask.Tid in (Select Tid from TTask_Exer Where Quit=0 And Exer=" + HR_ID + ")) or " +
                    " (ttask.tid in (Select tid from ttask_care where who=" + HR_ID + "))  ) And TTask_Ver.Release=1  And  TTask_Ver.Status In (1,2,12) ) " + "" +
                    " Or TTask.TID In  " + IDs;
            return Where;
        }

        private String getSqlDoneINotKnow() {
            int HR_ID = UserSingleton.get().getHRID();
            String sql_get_task_finished_but_i_not_know = " Select TTask_Ver.TID, " +
                    " (Select max(access_time) from TTask_Access " +
                    " Where Visitor = " + HR_ID + " group by tid having tid=ttask_ver.tid) As LastAccess " +
                    " From TTask_Ver " +
                    " inner join ttask on ttask_ver.tid= ttask.tid and ttask_ver.tvid= ttask.current_tvid " +
                    " Where TTask_Ver.Finished=1 And " +
                    " ( (TTask.TID In (" +
                    "Select tk.TID from ttask As tk inner join TTPlan As tn On tk.ttp_id=tn.ttp_id where tk.Creater =" + HR_ID + " And tn.BigJob =0)) " +
                    " Or  (TTask_Ver.Exer_Dep_ID In (Select Distinct Department_ID From HR_Adm Where Hr_ID= " + HR_ID + ") And isnull(TTask_Ver.Exer,'')='')" +
                    " Or  (TTask.Tid in (" +
                    "Select Tid from TTask_Exer Where Quit=0 And Exer=" + HR_ID + ")) " +
                    " or  (ttask.tid in (Select tid from ttask_care where who=" + HR_ID + "))) " +
                    " And Datediff(Minute,Isnull((" +
                    "Select max(access_time) from TTask_Access Where Visitor = " + HR_ID + " group by tid having tid=ttask_ver.tid" +
//                    "),'1900-1-1'),TTask_Ver.ActualFinish_Time)> 5  And Datediff(Day, TTask_Ver.ActualFinish_Time,Getdate()) < 365 ";//// TODO: 2021/1/14 添加order by
                    "),'1900-1-1'),TTask_Ver.ActualFinish_Time)> 5  And Datediff(Day, TTask_Ver.ActualFinish_Time,Getdate()) < 365 order by Ttask_ver.create_time desc";

            return sql_get_task_finished_but_i_not_know;
        }

        private String getSqlCommonWhere(Integer Who) {
            String Cw = " Where Not Isnull(TTPlan.Closed,0)=1 And " + getSqlAboutWho(Who);
            return Cw;
        }

        private String getSqlAboutWho(Integer Who) {
            String S = " (ttask.tid In (Select tid from ttask_exer where Quit=0 And exer=" + Who + ") Or " +
                    " ttask.tid In (Select tid from ttask_Owner where Owner=" + Who + ") Or " +
                    " TTask.tid In (Select tid from ttask_auditor where auditor = " + Who + ") Or " +
                    " TTask.TID In (Select Tid From TTask_Care Where Who=" + Who + ") " +
                    ") ";
            return S;
        }

        protected void getUserPics() {
            if (beanList == null) {
                return;
            }

//            List<Integer> Hr_IDs = CommonUtil.getIDListFromJsonList(beanList, "Creater");
//            if (Hr_IDs == null) {
//                return;
//            }
//            if (Hr_IDs.size() == 0) {
//                return;
//            }
            List<Integer> hrIDList = new ArrayList<>();
            for (TaskBean bean : beanList) {
                hrIDList.add(bean.getCreaterHRID());
            }

            for (int i = 0; i < hrIDList.size(); i++) {
                Integer x = hrIDList.get(i);
                if (x != 0) {
                    Bitmap bitmap = CommonUtil.getUserPic(TaskMainNewActivity.this, CommonUtil.userPictureMap, x);

                }
            }

        }

        private String getSqlPlanOnGoing() {
            String S = " Isnull(TTPlan.Closed,0)=0 And Isnull(TTPlan.Deleted,0)=0 And Isnull(TTPlan.Released,0)=1 And Isnull(TTPlan.Finished,0)=0 ";
            return S;
        }

        private String getSqlTaskOnGoing() {
            String S = " TTask_Ver.Status In (1,2,3,4,5) ";
            return S;
        }

        private String getSqlTasksMySentWhere() {
            int HR_ID = UserSingleton.get().getHRID();
            //独立任务，非计划内的任务 BigJob=0
            String Where = getSqlCommonWhere(HR_ID) +
                    " And TTask_Ver.Release=1 " +
                    " And " + getSqlPlanOnGoing() +
                    " And " + getSqlTaskOnGoing() +
                    " And ttask.tid in (Select tid from ttask_owner where owner=" + HR_ID + ")  " +
                    " And TTPlan.BigJob=0 " +
                    " Order By TTPlan.TTP_ID Desc, TTask_Ver.SubTaskNo Asc ";
            return Where;
        }

        protected List<TaskBean> loadTasksMySentData() {

            String Where = getSqlTaskMyConcernWhere();

            String sqlSelect = getSqlTaskSelectField();

            String sqlFrom = getSqlTaskFrom();

            String sqlWhere = getSqlTasksMySentWhere();

//            beanList = WebServiceUtil.getJsonList(sqlSelect + sqlFrom + sqlWhere);

            WsResult result = WebServiceUtil.getDataTable(sqlSelect + sqlFrom + sqlWhere);
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                if (!TextUtils.isEmpty(jsonData)) {
                    //// TODO: 2020/1/3 放这里会出错，刷新UI线程问题
//                    return jsonData;
                    Gson gson = new Gson();
                    beanList = gson.fromJson(jsonData, new TypeToken<List<TaskBean>>() {
                    }.getType());

                }
            }


            getUserPics();
            return beanList;
        }

        private String getSqlTasksMyExeWhere() {
            int HR_ID = UserSingleton.get().getHRID();
            String Where = getSqlCommonWhere(HR_ID) +
                    " And TTask_Ver.Release=1 " +
                    " And " + getSqlPlanOnGoing() +
                    " And " + getSqlTaskOnGoing() +
                    "  And TTask.Tid in (Select Tid from TTask_Exer Where Quit=0 And Exer=" + HR_ID + ")  " +
                    " Order By TTPlan.TTP_ID Desc, TTask_Ver.SubTaskNo Asc ";
            return Where;
        }

        protected List<TaskBean> loadTasksMyExeData() {

            String sqlSelect = getSqlTaskSelectField();

            String sqlFrom = getSqlTaskFrom();

            String sqlWhere = getSqlTasksMyExeWhere();
            //这个已经在asyntask中，doInBackground
//            beanList = WebServiceUtil.getJsonList(sqlSelect + sqlFrom + sqlWhere);
            WsResult result = WebServiceUtil.getDataTable(sqlSelect + sqlFrom + sqlWhere);
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                if (!TextUtils.isEmpty(jsonData)) {
                    //// TODO: 2020/1/3 放这里会出错，刷新UI线程问题
//                    return jsonData;
                    Gson gson = new Gson();
                    beanList = gson.fromJson(jsonData, new TypeToken<List<TaskBean>>() {
                    }.getType());

                }
            }

            getUserPics();
            return beanList;
        }

        private String getSqlCommonWhereDep() {
            String s = " Where Not Isnull(TTPlan.Closed,0)=1 And TTask_Ver.Exer_Dep_ID In (Select Distinct Department_ID From HR_Adm Where Hr_ID= " + UserSingleton.get().getHRID() + ") ";
            return s;
        }

        private String getSqlTasksMyDepOnGoingWhere() {

            String Where = getSqlCommonWhereDep() +
                    " And TTask_Ver.Release=1 " +
                    " And " + getSqlPlanOnGoing() +
                    " And " + getSqlTaskOnGoing() +
                    " Order By TTPlan.TTP_ID Desc, TTask_Ver.SubTaskNo Asc ";
            return Where;
        }

        protected List<TaskBean> loadTasksMyDepOnGoingData() {

            String sqlSelect = getSqlTaskSelectField();

            String sqlFrom = getSqlTaskFrom();

            String sqlWhere = getSqlTasksMyDepOnGoingWhere();

//            beanList = WebServiceUtil.getJsonList(sqlSelect + sqlFrom + sqlWhere);
            WsResult result = WebServiceUtil.getDataTable(sqlSelect + sqlFrom + sqlWhere);
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                if (!TextUtils.isEmpty(jsonData)) {
                    //// TODO: 2020/1/3 放这里会出错，刷新UI线程问题
//                    return jsonData;
                    Gson gson = new Gson();
                    beanList = gson.fromJson(jsonData, new TypeToken<List<TaskBean>>() {
                    }.getType());

                }
            }

            getUserPics();
            return beanList;
        }

        protected void bindObjectListsToAdapter(List<TaskBean> beanList) {
//            ObjectAdapter = new TaskJsonListAdapter(TaskMainNewActivity.this, beanList);
            //赋值 列宽度
            //ObjectAdapter.ColWidthList = ColWidthList;
            //ObjectAdapter.hiddenColList =hiddenColList;
//            recyclerView.setLayoutManager(new LinearLayoutManager(TaskMainNewActivity.this));
//            recyclerView.setAdapter(ObjectAdapter);
            adapter.setData(beanList);
            if (beanList != null && beanList.size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                emptyManageView.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                emptyManageView.setVisibility(View.VISIBLE);
            }


            adapter.setOnViewClickListener(new OnViewClickListener() {
                @Override public <T> void onClickAction(View v, String tag, T t) {
                    if (t != null) {
                        TaskBean bean = (TaskBean) t;
                        Intent intent = new Intent(TaskMainNewActivity.this, TaskDetailActivity.class);
                        intent.putExtra(IntentConstant.Intent_Extra_task_bean, bean);
                        startActivity(intent);
                    }
                }
            });
        }

    }

}
