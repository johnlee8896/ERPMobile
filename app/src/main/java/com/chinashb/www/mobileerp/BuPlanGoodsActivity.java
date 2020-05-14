package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinashb.www.mobileerp.adapter.BuPlanGoodsItemAdapter;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.BuPlanGoodsItemBean;
import com.chinashb.www.mobileerp.bean.PlanGoodsIVIDBean;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.task.TasksActivity;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CommProgressDialog;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.chinashb.www.mobileerp.widget.EmptyLayoutManageView;
import com.chinashb.www.mobileerp.widget.SelectUseAdapter;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2020/5/7 14:30
 * @author 作者: xxblwf
 * @description 车间要货计划
 */

public class BuPlanGoodsActivity extends BaseActivity {
    @BindView(R.id.bu_plan_goods_search_TextView) TextView searchTextView;
    @BindView(R.id.bu_plan_goods_input_editText) EditText searchEditText;
    @BindView(R.id.search_clear_input_ImageView) ImageView clearImageView;
    @BindView(R.id.bu_plan_goods_recyclerView) CustomRecyclerView recyclerView;
    @BindView(R.id.bu_plan_goods_emptyManager) EmptyLayoutManageView emptyManager;

    private BuPlanGoodsItemAdapter adapter;
    private List<BuPlanGoodsItemBean> originalBUDataList;
    private List<BuPlanGoodsItemBean> originalBUDataNoInvQtyList;
    private int buId;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bu_plan_goods_layout);
        ButterKnife.bind(this);

        adapter = new BuPlanGoodsItemAdapter();
        recyclerView.setAdapter(adapter);

        setViewsListener();

        buId = getIntent().getIntExtra(IntentConstant.Intent_Extra_current_bu_id, 0);

        buId = 54;
        if (buId > 0) {
            handleQueryIVList();
        }
        originalBUDataList = new ArrayList<>();
    }

    private void handleQueryIVList() {
        String sql = String.format(" if object_id('tempdb..#MPI_IV') is not null drop Table #MPI_IV\n" +
                " Select Distinct Product_ID,Item_ID, IV_ID Into #MPI_IV From MPI  Where  MPI.Bu_ID=%s And Deleted=0 And MPI.MPI_Date>=GetDate(); Select Distinct Abom.IV_ID From Abomv  Inner Join [Abom]  With (NoLock)  On [Abomv].[Abv_ID]=[Abom].[Abv_ID] \n" +
                " And Abomv.Active=1   Inner Join [#MPI_IV]  With (NoLock)  On [AbomV].[IV_ID]=[#MPI_IV].[IV_ID] \n" +
                " Where   Abom.IV_ID Not In (Select IV_ID From ListP  Inner Join Lists On ListP.LID=Lists.LID    And (Lists.Bu_ID=%s)  And (Plan_Type_ID=9 Or Plan_Type_ID=6 Or Plan_Type_ID=8 Or Plan_Type_ID=10 Or Plan_Type_ID=11)) \n" +
                " Union Select Distinct AbomZ.CIV_ID As IV_ID From AbomZ  Where \n" +
                "AbomZ.Top_IV_ID In  (Select Distinct ListP.IV_ID From ListP  Inner Join Lists On ListP.LID=Lists.LID And Lists.Bu_ID=%s And Plan_Type_ID=10  Inner Join PPP On ListP.IV_ID=PPP.IV_ID And PPP.PPP_Date>=Dateadd(month,-1,GetDate()))  And Not AbomZ.CIV_ID In (Select Distinct PIV_ID From AbomZ)  And Not AbomZ.CIV_ID In (Select IV_ID From ListP   Inner Join Lists On ListP.LID=Lists.LID  And (Plan_Type_ID=9 Or Plan_Type_ID=6 Or Plan_Type_ID=8 Or Plan_Type_ID=10 Or Plan_Type_ID=11))  Union \n" +
                "Select Distinct Item.IV_ID From Item_Assist  Inner Join [Item]  With (NoLock)  On [Item_Assist].[Item_ID]=[Item].[Item_ID] \n" +
                " Inner Join [#MPI_IV] With (NoLock)  On [Item_Assist].[PItem_ID]=[#MPI_IV].[Item_ID] \n" +
                " Where   Item.IV_ID Not In (Select IV_ID From ListP  Inner Join Lists On Lists.LID=ListP.LID And (Lists.Bu_ID=%s) And Plan_Type_ID=9)  Union \n" +
                "Select Distinct Item.IV_ID From Product  Inner Join [Package]  With (NoLock)  On [Product].[Package_ID]=[Package].[Package_ID] \n" +
                " Inner Join [Package_Bom]  With (NoLock)  On [Package].[Package_ID]=[Package_Bom].[Package_ID] \n" +
                " Inner Join [Item]  With (NoLock)  On [Package_Bom].[Item_ID]=[Item].[Item_ID] \n" +
                " Inner Join [#MPI_IV]  With (NoLock)  On [Product].[Product_ID]=[#MPI_IV].[Product_ID] \n" +
                " Where  Item.IV_ID Not In (Select IV_ID From ListP  Inner Join Lists On Lists.LID=ListP.LID    And (Lists.Bu_ID=%s)  And Plan_Type_ID=9) ", buId, buId, buId, buId, buId);
        SelectIVIDListAsyncTask<PlanGoodsIVIDBean> task = new SelectIVIDListAsyncTask();
        task.execute(sql);
    }


    protected void setViewsListener() {
        if (searchTextView != null) {
            //筛选清单 originalBUDataList
            searchTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (searchTextView.getText().equals("取消")) {
                        finish();
                    } else {
                        doSearchAction(searchEditText.getText().toString());
                    }
                }
            });
        }

        searchEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable s) {
                clearImageView.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                searchTextView.setText(s.length() > 0 ? "搜索" : "取消");
                if (s.length() == 0) {
//                    bindObjectListsToAdapterBU(originalBUDataList);
                    adapter.setData(originalBUDataList);
                }
            }
        });
        clearImageView.setOnClickListener(v -> {
            searchEditText.setText("");
            searchTextView.setText("取消");
        });

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String input = searchEditText.getText().toString();
                    if (TextUtils.isEmpty(input)) {
                        ToastUtil.showToastShort("请输入搜索内容");
                    } else {
                        doSearchAction(input);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void doSearchAction(String keyWord) {
        if (originalBUDataList == null) {
            return;
        }
        if (!keyWord.isEmpty()) {
//            List<PlanGoodsIVIDBean> tempList = getFilterList(input);
//            bindObjectListsToAdapterBU(tempList);
//            adapter.setData(getFilterList(keyWord));
//            String whereSql = String.format(" and CF_Chinese_Name like \'%%s%\' ",keyWord);
            String whereSql = " and CF_Chinese_Name like '%" + keyWord + "%'";
            handleQueryIVList();
        } else {
            //todo
//            bindObjectListsToAdapterBU(originalBUDataList);
            adapter.setData(originalBUDataList);
        }
    }

//    private List<?> getFilterList(String keyWord) {
////        List<?> tempList ;
//        if (originalBUDataList != null) {
////            for (int i = 0; i < originalBUDataList.size(); i++) {
//////                JsonObject j = originalBUDataList.get(i);
//////                if (IsJsonObjectContainsKeyString(j, Key)) {
//////                    tempList.add(j);
//////                }
////
////            }
//
////            for (PlanGoodsIVIDBean PlanGoodsIVIDBean : originalBUDataList) {
////                if (PlanGoodsIVIDBean.getCompanyChineseName().contains(keyWord) || PlanGoodsIVIDBean.getBUName().contains(keyWord)) {
////                    tempList.add(PlanGoodsIVIDBean);
////                }
////            }
//            if (originalBUDataList != null && originalBUDataList.size() > 0) {
//                Object bean = originalBUDataList.get(0);
////                    for (PlanGoodsIVIDBean PlanGoodsIVIDBean : originalBUDataList) {
////                        if (PlanGoodsIVIDBean.getCompanyChineseName().contains(keyWord) || PlanGoodsIVIDBean.getBUName().contains(keyWord)) {
////                            tempList.add(PlanGoodsIVIDBean);
////                        }
////                    }
//
//                for (int i = 0; i < originalBUDataList.size(); i++) {
//                    if (bean instanceof PlanGoodsIVIDBean) {
//                        List tempList = new ArrayList<PlanGoodsIVIDBean>();
//                        PlanGoodsIVIDBean companyBean = (PlanGoodsIVIDBean) originalBUDataList.get(i);
//                        if (companyBean.getCompanyChineseName().contains(keyWord) || companyBean.getBUName().contains(keyWord)) {
//                            tempList.add(companyBean);
//                        }
//                        return tempList;
//                    }
//                }
//            }
//        }
//        return null;
//    }


    private class SelectIVIDListAsyncTask<T> extends AsyncTask<String, Void, List<T>> {

        @Override protected List<T> doInBackground(String... strings) {
//            String sql = "select Company_ID,Company_Chinese_Name,Company_English_Name from company where Company_Enabled = 1";
            String sql = strings[0];
            WsResult result = WebServiceUtil.getDataTable(sql);
            List<T> commonDataList = null;
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                Gson gson = new Gson();
                commonDataList = gson.fromJson(jsonData, new TypeToken<List<PlanGoodsIVIDBean>>() {
                }.getType());

                if (commonDataList != null && commonDataList.size() > 0) {
                    if (commonDataList.get(0) instanceof PlanGoodsIVIDBean) {
                        commonDataList = gson.fromJson(jsonData, new TypeToken<List<PlanGoodsIVIDBean>>() {
                        }.getType());
                    }
                }
            }
            return commonDataList;
        }

        @Override protected void onPostExecute(List<T> planGoodsIVIDList) {
            super.onPostExecute(planGoodsIVIDList);
            if (planGoodsIVIDList != null && planGoodsIVIDList.size() > 0) {
//                adapter.setData(companyList);
//                emptyManager.setVisibility(View.GONE);
//                recyclerView.setVisibility(View.VISIBLE);
                StringBuilder temp = new StringBuilder("(");
                int size = planGoodsIVIDList.size();
                int i = 0;
                for (T bean : planGoodsIVIDList) {
                    if (bean instanceof PlanGoodsIVIDBean) {
                        temp.append(((PlanGoodsIVIDBean) bean).getIvID());
                        if (i < size - 1) {
                            temp.append(",");
                        }
                        i++;
                    }
                }
                temp.append(")");

                GetBuPlanGoodsItemListAsyncTask<BuPlanGoodsItemBean> task = new GetBuPlanGoodsItemListAsyncTask();
                String sql = String.format(" if object_id('tempdb..#UnListTemp') is not null drop Table #UnListTemp\n" +
                        "   Select identity(int, 1,1) As No, Convert(bigint,IV_ID) As IV_ID Into #UnListTemp From Item_Version Where IV_ID In  %s ;\n" +
                        "   Select #UnListTemp.No As No, Item.Item_ID, Item_Version.IV_ID, Item.Item + ' '+Item.Item_Name + ' ' + Item.Item_Spec2 As item_spec, Item.Item As item_name_code, \n" +
                        "Item.Item_Name as item_name, Item.Item_Spec2 as spec, Item_Version.Item_Version as version\n" +
                        " From Item Inner Join Item_Version On Item.Item_ID=Item_Version.Item_ID  \n" +
                        " Inner Join #UnlistTemp On #UnlistTemp.IV_ID=Item_Version.IV_ID \n" +
                        "  Left Join Lead_Time On Lead_Time.IV_ID = #UnlistTemp.IV_ID And Lead_Time.Bu_ID=%s And Lead_Time.Ac_Type=1 And Lead_Time.Deleted=0 \n" +
                        "    Where Item_Version.IV_ID IN  %s  Order By No ; ", temp.toString(), buId, temp.toString());
                try {
                    task.execute(sql);
                } catch (Exception e) {
                    ToastUtil.showToastShort("数据获取失败，原因：" + e.getMessage());
                }





            } else {
//                emptyManager.setVisibility(View.VISIBLE);
//                recyclerView.setVisibility(View.GONE);
                ToastUtil.showToastShort("查询语句错误或没有相关数据！");
            }

        }

    }

    private CommProgressDialog progressDialog;
    private class GetBuPlanGoodsItemListAsyncTask<T> extends AsyncTask<String, Void, List<T>> {

        @Override protected List<T> doInBackground(String... strings) {
            String sql = strings[0];
            WsResult result = WebServiceUtil.getDataTable(sql);
            List<T> commonDataList = null;
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                Gson gson = new Gson();
                commonDataList = gson.fromJson(jsonData, new TypeToken<List<BuPlanGoodsItemBean>>() {
                }.getType());

                if (commonDataList != null && commonDataList.size() > 0) {
                    if (commonDataList.get(0) instanceof BuPlanGoodsItemBean) {
                        commonDataList = gson.fromJson(jsonData, new TypeToken<List<BuPlanGoodsItemBean>>() {
                        }.getType());
                    }
                }
            }
            return commonDataList;
        }

        @Override protected void onPostExecute(List<T> buPlanGoodsItemBeanList) {
            super.onPostExecute(buPlanGoodsItemBeanList);
            if (buPlanGoodsItemBeanList != null && buPlanGoodsItemBeanList.size() > 0) {
                StringBuilder temp = new StringBuilder("(");
                int size = buPlanGoodsItemBeanList.size();
                int i = 0;
                for (T bean : buPlanGoodsItemBeanList) {
                    if (bean instanceof BuPlanGoodsItemBean) {
                        temp.append(((BuPlanGoodsItemBean) bean).getItemID());
                        if (i < size - 1) {
                            temp.append(",");
                        }
                        i++;
                    }
                }
                temp.append(")");







                adapter.setData((List<BuPlanGoodsItemBean>) buPlanGoodsItemBeanList);
                emptyManager.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            } else {
                emptyManager.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                ToastUtil.showToastShort("查询语句错误或没有相关数据！");
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        @Override protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog == null) {
                progressDialog = new CommProgressDialog.Builder(BuPlanGoodsActivity.this).setTitle("正在获取数据").create();
            }
            progressDialog.show();
        }
    }



    private class GetInvQtyListAsyncTask<T> extends AsyncTask<String, Void, List<T>> {

        @Override protected List<T> doInBackground(String... strings) {
//            String sql = "select Company_ID,Company_Chinese_Name,Company_English_Name from company where Company_Enabled = 1";
            String sql = strings[0];
            WsResult result = WebServiceUtil.getDataTable(sql);
            List<T> commonDataList = null;
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                Gson gson = new Gson();
                commonDataList = gson.fromJson(jsonData, new TypeToken<List<CurrentInvQtyBean>>() {
                }.getType());

                if (commonDataList != null && commonDataList.size() > 0) {
                    if (commonDataList.get(0) instanceof CurrentInvQtyBean) {
                        commonDataList = gson.fromJson(jsonData, new TypeToken<List<CurrentInvQtyBean>>() {
                        }.getType());
                    }
                }
            }
            return commonDataList;
        }

        @Override protected void onPostExecute(List<T> currentInvQtyBeanList) {
            super.onPostExecute(currentInvQtyBeanList);
            if (currentInvQtyBeanList != null && currentInvQtyBeanList.size() > 0) {






            } else {
//                emptyManager.setVisibility(View.VISIBLE);
//                recyclerView.setVisibility(View.GONE);
                ToastUtil.showToastShort("查询库存语句错误或没有相关数据！");
            }

        }

    }

    private class CurrentInvQtyBean{
        @SerializedName("currentInvQty") double currentInvQty;

        public double getCurrentInvQty() {
            return currentInvQty;
        }

        public void setCurrentInvQty(double currentInvQty) {
            this.currentInvQty = currentInvQty;
        }
    }


}
