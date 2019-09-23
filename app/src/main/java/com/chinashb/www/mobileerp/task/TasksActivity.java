package com.chinashb.www.mobileerp.task;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.MobileMainActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.TaskJsonListAdapter;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.OnItemClickListener;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.widget.CommProgressDialog;
import com.chinashb.www.mobileerp.widget.EmptyLayoutManageView;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TasksActivity extends BaseActivity {

    public List<Integer> ColWidth;
    public List<String> ColCaption;
    public List<String> HiddenCol;
    RecyclerView recyclerView;
    private EmptyLayoutManageView emptyManageView;
    int tasks_type = 0; //0:综合 1:mysent   2:myexe 3:mydep
    List<JsonObject> tasks;
    TaskJsonListAdapter ObjectAdapter;
    JsonObject Selected_Object;
    HashMap<String, String> Result;

//    int HR_ID;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_task_myall:
                    tasks_type = 0;
                    AsynLoadTasks();
                    return true;
                case R.id.navigation_task_mysent:
                    tasks_type = 1;
                    AsynLoadTasks();
                    return true;
                case R.id.navigation_task_myexe:
                    tasks_type = 2;
                    AsynLoadTasks();
                    return true;
                case R.id.navigation_task_mydep:
                    tasks_type = 3;
                    AsynLoadTasks();
                    return true;

            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_layout);

//        HR_ID = UserSingleton.get().getHRID();


        setHomeButton();

        recyclerView = (RecyclerView) findViewById(R.id.rv_task_list);
        emptyManageView = (EmptyLayoutManageView) findViewById(R.id.task_emptyManager);
        tasks = new ArrayList<>();
        TaskJsonListAdapter jsonListAdapter = new TaskJsonListAdapter(this, tasks);
        recyclerView.setAdapter(jsonListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation_task);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        tasks_type = 0;
        AsynLoadTasks();

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
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    protected void AsynLoadTasks() {

        AsyncGetTasks t = new AsyncGetTasks();
        t.execute();

    }

    private CommProgressDialog progressDialog;

    private class AsyncGetTasks extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {


            switch (tasks_type) {
                case 0:
                    loadTasksMyAllData();
                    break;
                case 1:
                    loadTasksMySentData();
                    break;
                case 2:
                    loadTasksMyExeData();
                    break;
                case 3:
                    loadTasksMyDepOnGoingData();
                    break;

            }


            return null;
        }

        @Override
        protected void onPreExecute() {
            //pbScan.setVisibility(View.VISIBLE);
            if (progressDialog == null) {
                progressDialog = new CommProgressDialog.Builder(TasksActivity.this).setTitle("正在获取数据").create();
            }
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {

            bindObjectListsToAdapter(tasks);


            //pbScan.setVisibility(View.INVISIBLE);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

        protected void loadTasksMyAllData() {

            String sqlSelect = getSqlTaskSelectField();

            String sqlFrom = getSqlTaskFrom();

            String sqlWhere = getSqlTaskMyConcernWhere();

            String Sql = sqlSelect + sqlFrom + sqlWhere;

            tasks = WebServiceUtil.getJsonList(Sql);


            getUserPics();

        }

        //todo 不理解
        private String getSqlTaskSelectField() {
            String sql = " Select (Case When BigJob=0 Then 0 Else TTask_Ver.TTP_ID End) As TTP_ID, " +
                    "~ Isnull(TTask_Exer.Confirmed,1) As New, " +
                    "Case When TTPlan.Is_From_Business_System=1 And Business_System_ID=4 Then PlanName + char(10) +LTrim(TTask_Ver.Title) Else LTrim(TTask_Ver.Title) End As 任务, " +
                    "TTask.TID As TID, TTask_Ver.TVID, " +
                    "        Convert(nvarchar(100),TTask_Ver.Create_Time,20) As Create_Time, " +
                    "Convert(nvarchar(100),TTask_Ver.Start_Time,23) As Start_Time, " + "" +
                    "Convert(nvarchar(100),TTask_Ver.End_Time,23) As End_Time , " +
                    "        TTask.Creater,TTAsk_Ver.Owner As 制定人, TTask_Ver.Exer As 责任人, TTask_Ver.Auditor As 审核人, " +
                    "Isnull(TTask_Ver.Percentage,0) As 完成度, TTask_Ver.Remark As 备注, " +
                    "        (Select Max(access_time) From TTask_Access Where Visitor=" + UserSingleton.get().getHRID() + " Group by Tid having Tid=TTask.Tid) As 最近访问 ";
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
                    "),'1900-1-1'),TTask_Ver.ActualFinish_Time)> 5  And Datediff(Day, TTask_Ver.ActualFinish_Time,Getdate()) < 365 ";

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
            if (tasks == null) {
                return;
            }

            List<Integer> Hr_IDs = CommonUtil.getIDListFromJsonList(tasks, "Creater");
            if (Hr_IDs == null) {
                return;
            }
            if (Hr_IDs.size() == 0) {
                return;
            }

            for (int i = 0; i < Hr_IDs.size(); i++) {
                Integer x = Hr_IDs.get(i);
                if (x != 0) {
                    Bitmap bitmap = CommonUtil.getUserPic(TasksActivity.this, CommonUtil.userPictureMap, x);

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

        protected void loadTasksMySentData() {

            String Where = getSqlTaskMyConcernWhere();

            String sqlSelect = getSqlTaskSelectField();

            String sqlFrom = getSqlTaskFrom();

            String sqlWhere = getSqlTasksMySentWhere();

            tasks = WebServiceUtil.getJsonList(sqlSelect + sqlFrom + sqlWhere);


            getUserPics();

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

        protected void loadTasksMyExeData() {

            String sqlSelect = getSqlTaskSelectField();

            String sqlFrom = getSqlTaskFrom();

            String sqlWhere = getSqlTasksMyExeWhere();

            tasks = WebServiceUtil.getJsonList(sqlSelect + sqlFrom + sqlWhere);


            getUserPics();

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

        protected void loadTasksMyDepOnGoingData() {

            String sqlSelect = getSqlTaskSelectField();

            String sqlFrom = getSqlTaskFrom();

            String sqlWhere = getSqlTasksMyDepOnGoingWhere();

            tasks = WebServiceUtil.getJsonList(sqlSelect + sqlFrom + sqlWhere);


            getUserPics();

        }

        protected void bindObjectListsToAdapter(final List<JsonObject> JList) {
            ObjectAdapter = new TaskJsonListAdapter(TasksActivity.this, tasks);
            //赋值 列宽度
            //ObjectAdapter.ColWidthList = ColWidthList;
            //ObjectAdapter.hiddenColList =hiddenColList;
            recyclerView.setLayoutManager(new LinearLayoutManager(TasksActivity.this));
            recyclerView.setAdapter(ObjectAdapter);
            if (tasks != null && tasks.size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                emptyManageView.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                emptyManageView.setVisibility(View.VISIBLE);
            }

            ObjectAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void OnItemClick(View view, int position) {
                    if (JList != null) {
                        Selected_Object = JList.get(position);

                        //Selected_Object转换成HashMap：Result
                        Result = CommonUtil.Convert_JsonObject_HashMap(Selected_Object);

                        Intent Conversation = new Intent(TasksActivity.this, TaskViewActivity.class);
                        Conversation.putExtra("Task", (Serializable) Result);

                        startActivity(Conversation);

                    }

                }
            });
        }

    }

}
