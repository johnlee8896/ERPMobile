package com.chinashb.www.mobileerp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.chinashb.www.mobileerp.adapter.WCListAdapter;
import com.chinashb.www.mobileerp.basicobject.Issued_Item;
import com.chinashb.www.mobileerp.basicobject.MpiWcBean;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.basicobject.s_WCList;
import com.chinashb.www.mobileerp.funs.OnItemClickListener;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.StaticVariableUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/***
 * @date 创建时间 2019/7/22 16:12
 * @author 作者: xxblwf
 * @description 展示计划的页面
 */

public class PlanShowListActivity extends AppCompatActivity {

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_show_list_layout);



    }


    private class GetMPAsyncTask extends AsyncTask<String, Void, Void> {
        MpiWcBean scanresult;
        List<Issued_Item> li;

        @Override
        protected Void doInBackground(String... params) {
                String sql = " Select Wi.WC_ID, Wi.List_No,WC_Name,M.MPIWC_ID,dbo.get_mw_plan_show_name(mpiwc_ID) As MwName,dbo.get_mw_plan_show_name_html(mpiwc_ID) As HtmlMwName, M.MPI_Remark From" +
                        "        WC_List_Item As Wi Inner Join P_WC As C On Wi.Wc_ID=C.WC_ID" +
                        "        Inner Join MPI_WC As M On M.WC_ID=Wi.WC_ID" +
                        "        Where Wi.LID= 1" +  selectedWCEntity.getLID()
                        "        And M.Deleted=0 And  MPI_Date= '2019-07-22'" +
                        "        Order By Wi.List_No, PShift_ID, Shift_No"   ;
//            String sql = "Select Case When Ac_Type=1 Then '部件' When Ac_Type=2 Then '成品' Else '' End As WCL_Type, LID, Bu_ID, ListName From WC_List Where Bu_ID="
//                    + UserSingleton.get().getUserInfo().getBu_ID() + " Order By Case When Ac_Type=1 Then '部件' When Ac_Type=2 Then '成品' Else '' End";
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
}
