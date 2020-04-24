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

import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.ReceiverCompanyBean;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.chinashb.www.mobileerp.widget.EmptyLayoutManageView;
import com.chinashb.www.mobileerp.widget.SelectUseAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2020/4/24 8:44
 * @author 作者: xxblwf
 * @description 选择客户公司，如收货公司等可用  页面，可关键字过滤
 */

public class SelectCustomerCompanyActivity extends BaseActivity {
    @BindView(R.id.customer_search_TextView) TextView searchTextView;
    @BindView(R.id.customer_input_editText) EditText searchEditText;
    @BindView(R.id.search_clear_input_ImageView) ImageView clearImageView;
    @BindView(R.id.customer_recyclerView) CustomRecyclerView recyclerView;
    @BindView(R.id.customer_emptyManager) EmptyLayoutManageView emptyManager;

    private SelectUseAdapter adapter;
    private List<ReceiverCompanyBean> originalBUDataList;
    private int buId;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_customer_layout);
        ButterKnife.bind(this);

        adapter = new SelectUseAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setOnViewClickListener(new OnViewClickListener() {
            @Override public <T> void onClickAction(View v, String tag, T t) {
                if (t != null) {
                    if (t instanceof ReceiverCompanyBean) {
                        ReceiverCompanyBean receiverCompanyBean = (ReceiverCompanyBean) t;
                        Intent intent = new Intent(SelectCustomerCompanyActivity.this,LogisticsManageActivity.class);
                        intent.putExtra(IntentConstant.Intent_Extra_logistics_customer_bean,receiverCompanyBean);
                        setResult(IntentConstant.Intent_Request_Code_Logistics_Customer_to_Logistics,intent);
                        finish();
                    }

                }
            }
        });
        
        setViewsListener();

         buId = getIntent().getIntExtra(IntentConstant.Intent_Extra_current_bu_id,0);
        if (buId > 0){
            handleQueryCustomer("");
        }
        originalBUDataList = new ArrayList<>();
    }

    private void handleQueryCustomer(String whereSql) {
        String sql = String.format("Select Customer_Facility.CF_ID,  Isnull(Customer_Chinese_Name,'')+' ['+isnull(Customer_English_Name,'')+']' As Customer, " +
                "CF_Chinese_Name As Factory,CF_Address ,Country.CountryChinese As Country From Customer  " +
                "Inner Join Bu_Customer On Bu_Customer.Customer_ID=Customer.Customer_ID  Inner Join Customer_Facility On Customer.Customer_ID=Customer_Facility.Customer_ID " +
                " Left Join Country On CF_Country=Country.ID  Where Bu_Customer.Bu_ID=%s And Isnull(CF_Enabled,1)=1 %s Order By Customer.Customer_ID ", buId,whereSql);
        GetSelectCustomerComapnyListAsyncTask<ReceiverCompanyBean> task = new GetSelectCustomerComapnyListAsyncTask();
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
//            List<ReceiverCompanyBean> tempList = getFilterList(input);
//            bindObjectListsToAdapterBU(tempList);
//            adapter.setData(getFilterList(keyWord));
//            String whereSql = String.format(" and CF_Chinese_Name like \'%%s%\' ",keyWord);
            String whereSql = " and CF_Chinese_Name like '%"+keyWord+"%'";
            handleQueryCustomer(whereSql);
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
////            for (ReceiverCompanyBean ReceiverCompanyBean : originalBUDataList) {
////                if (ReceiverCompanyBean.getCompanyChineseName().contains(keyWord) || ReceiverCompanyBean.getBUName().contains(keyWord)) {
////                    tempList.add(ReceiverCompanyBean);
////                }
////            }
//            if (originalBUDataList != null && originalBUDataList.size() > 0) {
//                Object bean = originalBUDataList.get(0);
////                    for (ReceiverCompanyBean ReceiverCompanyBean : originalBUDataList) {
////                        if (ReceiverCompanyBean.getCompanyChineseName().contains(keyWord) || ReceiverCompanyBean.getBUName().contains(keyWord)) {
////                            tempList.add(ReceiverCompanyBean);
////                        }
////                    }
//
//                for (int i = 0; i < originalBUDataList.size(); i++) {
//                    if (bean instanceof ReceiverCompanyBean) {
//                        List tempList = new ArrayList<ReceiverCompanyBean>();
//                        ReceiverCompanyBean companyBean = (ReceiverCompanyBean) originalBUDataList.get(i);
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


    private class GetSelectCustomerComapnyListAsyncTask<T> extends AsyncTask<String, Void, List<T>> {

        @Override protected List<T> doInBackground(String... strings) {
//            String sql = "select Company_ID,Company_Chinese_Name,Company_English_Name from company where Company_Enabled = 1";
            String sql = strings[0];
            WsResult result = WebServiceUtil.getDataTable(sql);
            List<T> commonDataList = null;
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                Gson gson = new Gson();
                commonDataList = gson.fromJson(jsonData, new TypeToken<List<ReceiverCompanyBean>>() {
                }.getType());

                if (commonDataList != null && commonDataList.size() > 0) {
                    if (commonDataList.get(0) instanceof ReceiverCompanyBean) {
                        commonDataList = gson.fromJson(jsonData, new TypeToken<List<ReceiverCompanyBean>>() {
                        }.getType());
                        //去掉重复
//                        List<ReceiverCompanyBean> tempList = new ArrayList<>();
//                        List<String> nameList = new ArrayList<>();
//                        for (T bean : commonDataList) {
//                            if (!nameList.contains(((ReceiverCompanyBean) bean).getCustomer())) {
//                                nameList.add(((ReceiverCompanyBean) bean).getCustomer());
//                                tempList.add((ReceiverCompanyBean) bean);
//                            }
//                        }
//                        return (List<T>) tempList;
                    }
                }
            }
            return commonDataList;
        }

        @Override protected void onPostExecute(List<T> companyList) {
            super.onPostExecute(companyList);
            if (companyList != null && companyList.size() > 0){
                adapter.setData(companyList);
                emptyManager.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }else{
                emptyManager.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }

        }

    }
}
