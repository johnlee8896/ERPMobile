package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.chinashb.www.mobileerp.adapter.PlanItemDetailAdapter;
import com.chinashb.www.mobileerp.basicobject.WorkCenter;
import com.chinashb.www.mobileerp.basicobject.WorkLineSelectEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.basicobject.s_WCList;
import com.chinashb.www.mobileerp.bean.PlanItemDetailBean;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.utils.UnitFormatUtil;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.chinashb.www.mobileerp.widget.SelectWorkLineDialog;
import com.chinashb.www.mobileerp.widget.TimePickerManager;
import com.chinashb.www.mobileerp.widget.TitleLayoutManagerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2019/7/22 16:12
 * @author 作者: xxblwf
 * @description 展示计划的页面
 */

public class PlanShowListActivity extends BaseActivity implements View.OnClickListener, OnViewClickListener {
    @BindView(R.id.plan_show_list_time_select_start_textView) TextView startTimeTextView;
    @BindView(R.id.plan_show_list_work_center_select_textView) TextView workLineSelectTextView;
    @BindView(R.id.plan_main_recyclerView) CustomRecyclerView recyclerView;
    @BindView(R.id.plan_show_list_time_select_end_textView) TextView endTimeTextView;
    @BindView(R.id.plan_show_list_title_managerView) TitleLayoutManagerView titleManagerView;

    private TimePickerManager timePickerManager;
    private String currentDate = "";
    private PlanItemDetailAdapter adapter;

    private String startDateString;
    private String endDateString;
    private s_WCList selectedWCEntity;
    private ArrayList<PlanItemDetailBean> originalBeanList;
//    private ArrayList<WorkCenter> workCenterList;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_show_list_layout);
        ButterKnife.bind(this);

        currentDate = UnitFormatUtil.formatTimeToDay(System.currentTimeMillis());
        startDateString = currentDate;
        endDateString = currentDate;
        Intent intent = getIntent();
        selectedWCEntity = (s_WCList) intent.getSerializableExtra("wclist");
        if (selectedWCEntity != null) {
            titleManagerView.setTitle(selectedWCEntity.getListName() + "的生产线");
        }

        setViewListeners();
        adapter = new PlanItemDetailAdapter();
        recyclerView.setAdapter(adapter);

        timePickerManager = new TimePickerManager(PlanShowListActivity.this);
        GetMPAsyncTask task;
        task = new GetMPAsyncTask();
        task.execute();
    }

    private void setViewListeners() {
        endTimeTextView.setOnClickListener(this);
        startTimeTextView.setOnClickListener(this);
        workLineSelectTextView.setOnClickListener(this);
    }

    @Override public void onClick(View v) {
        if (v == startTimeTextView) {
            showTimePickerDialog(TimePickerManager.PICK_TYPE_START);
        } else if (v == endTimeTextView) {
            showTimePickerDialog(TimePickerManager.PICK_TYPE_END);
        } else if (v == workLineSelectTextView) {
            GetWorkLineListAsyncTask task = new GetWorkLineListAsyncTask();
            task.execute();
        }
    }

    private void showTimePickerDialog(String pickType) {
        timePickerManager
                .setOnViewClickListener(PlanShowListActivity.this)
                .showDialog(pickType);
    }

    @Override public <T> void onClickAction(View v, String tag, T date) {
        if (tag.equals(TimePickerManager.PICK_TYPE_START)) {
//            startDateString = ((Date) date).getTime();
            startDateString = getText((Date) date);
            startTimeTextView.setText(getText((Date) date));
            startTimeTextView.setTextColor(getResources().getColor(R.color.color_blue_528FFF));

        } else if (tag.equals(TimePickerManager.PICK_TYPE_END)) {
            endDateString = getText((Date) date);
            endTimeTextView.setText(getText((Date) date));
            endTimeTextView.setTextColor(getResources().getColor(R.color.color_blue_528FFF));
//            task.execute();

            GetMPAsyncTask task;
            task = new GetMPAsyncTask();
            task.execute();
        }
    }

    private String getText(Date date) {
        return UnitFormatUtil.sdf_YMD.format(date);
    }

    private class GetMPAsyncTask extends AsyncTask<String, Void, ArrayList<PlanItemDetailBean>> {
        @Override
        protected ArrayList<PlanItemDetailBean> doInBackground(String... params) {
            String sql = " Select Wi.WC_ID, Wi.List_No,WC_Name,M.MPIWC_ID,dbo.get_mw_plan_show_name(mpiwc_ID) As MwName,dbo.get_mw_plan_show_name_html(mpiwc_ID) As HtmlMwName, M.MPI_Remark From" +
                    "        WC_List_Item As Wi Inner Join P_WC As C On Wi.Wc_ID=C.WC_ID" +
                    "        Inner Join MPI_WC As M On M.WC_ID=Wi.WC_ID" +
                    "        Where Wi.LID= " + selectedWCEntity.getLID() +
//                    "        Where Wi.LID= 1" +
                    "        And M.Deleted=0 And  MPI_Date>= '" + startDateString + "' And  MPI_Date <= '" + endDateString +
                    "   '     Order By Wi.List_No, PShift_ID, Shift_No";
//            String sql = "Select Case When Ac_Type=1 Then '部件' When Ac_Type=2 Then '成品' Else '' End As WCL_Type, LID, Bu_ID, ListName From WC_List Where Bu_ID="
//                    + UserSingleton.get().getUserInfo().getBu_ID() + " Order By Case When Ac_Type=1 Then '部件' When Ac_Type=2 Then '成品' Else '' End";
            WsResult result = WebServiceUtil.getDataTable(sql);
            ArrayList<PlanItemDetailBean> planItemBeanList = new ArrayList<>();
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                Gson gson = new Gson();
                planItemBeanList = gson.fromJson(jsonData, new TypeToken<List<PlanItemDetailBean>>() {
                }.getType());

            }

            return planItemBeanList;
        }


        @Override
        protected void onPostExecute(ArrayList<PlanItemDetailBean> planBeanList) {
            //tv.setText(fahren + "∞ F");
//            List<s_WCList> tempList = getCurrentShowList();
////            wclAdapter = new WCListAdapter(PlanManagerActivity.this, wcLists);
//            wclAdapter = new WCListAdapter(PlanManagerActivity.this, tempList);
//            selectWCListRecyclerView.setLayoutManager(new LinearLayoutManager(PlanManagerActivity.this));
//            selectWCListRecyclerView.setAdapter(wclAdapter);
//
//            wclAdapter.setOnItemClickListener(new OnItemClickListener() {
//                @Override
//                public void OnItemClick(View view, int position) {
////                    s_WCList wcEntity = wcLists.get(position);
//                    s_WCList wcEntity = wclAdapter.getDataList().get(position);
//                    //存下来,重选了wcs_list, 需要清除已选生产线
//                    StaticVariableUtils.selected_list = wcEntity;
//                    StaticVariableUtils.selectedWorkCenter = null;
//                    doNextStepSelectWc(wcEntity);
//
//                }
//            });

            //如果出库主页上的生产线组曾选过，就直接往下点，模拟到下一步
            //// TODO: 2019/7/22 这个与另外一个计划是否会有冲突
//            if (StaticVariableUtils.selected_list != null) {
//                s_WCList wcEntity = StaticVariableUtils.selected_list;
//                doNextStepSelectWc(wcEntity);
//            }

            //pbScan.setVisibility(View.INVISIBLE);
            if (planBeanList != null) {
                adapter.setData(planBeanList);
                originalBeanList = planBeanList;
            }
        }


        @Override
        protected void onPreExecute() {
            //pbScan.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }


    private class GetWorkLineListAsyncTask extends AsyncTask<String, Void, ArrayList<WorkCenter>> {
        @Override
        protected ArrayList<WorkCenter> doInBackground(String... params) {

            String sql = "Select Wi.WC_ID, Wi.List_No,WC_Name From WC_List_Item As Wi Inner Join P_WC As C On Wi.Wc_ID=C.WC_ID Where Wi.LID= " + selectedWCEntity.getLID() + " Order By Wi.List_No";
            WsResult r = WebServiceUtil.getDataTable(sql);
            ArrayList<WorkCenter> workCenterList = new ArrayList<WorkCenter>();
            if (r != null && r.getResult()) {
                String js = r.getErrorInfo();
                Gson gson = new Gson();
                workCenterList = gson.fromJson(js, new TypeToken<List<WorkCenter>>() {
                }.getType());
                //wcLists= us;
//                PlanShowListActivity.this.workCenterList = workCenterList;
            }
            return workCenterList;
        }

        @Override
        protected void onPostExecute(ArrayList<WorkCenter> workCenterList) {
            //tv.setText(fahren + "∞ F");
            if (workCenterList != null && workCenterList.size() > 0) {
                List<WorkLineSelectEntity> workLineSelectEntityList = new ArrayList<>();
                for (WorkCenter workCenter : workCenterList) {
                    WorkLineSelectEntity entity = new WorkLineSelectEntity();
                    entity.setSelect(false);
                    entity.setWorkLinName(workCenter.getWC_Name());
                    workLineSelectEntityList.add(entity);
                }
                if (workLineSelectEntityList.size() > 0) {
                    SelectWorkLineDialog dialog = new SelectWorkLineDialog(PlanShowListActivity.this, workLineSelectEntityList);
                    dialog.show();
                    dialog.setOnViewClickListener(new OnViewClickListener() {
                        @Override public <T> void onClickAction(View v, String tag, T t) {
                            if (t == null) {
                                adapter.setData(originalBeanList);
                            } else {
                                List<String> selectStringList = (List<String>) t;
                                filterTheWorkLine(selectStringList);
                            }
                        }
                    });
                } else {
                    ToastUtil.showToastShort("暂不可选！");
                }
            }

        }

        @Override
        protected void onPreExecute() {
            //pbScan.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    private void filterTheWorkLine(List<String> selectStringList) {
//        List<PlanItemDetailBean> resultBeanList = new ArrayList<>();
        List<PlanItemDetailBean> tempList = new ArrayList<>();
        if (originalBeanList != null && originalBeanList.size() > 0) {
            for (PlanItemDetailBean bean : originalBeanList) {
                if (selectStringList.contains(bean.getWCName())) {
                    tempList.add(bean);
                }
            }
            adapter.setData(tempList);
        }

    }
}
