package com.chinashb.www.mobileerp.warehouse;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.IssuedItemAdapter;
import com.chinashb.www.mobileerp.basicobject.IstPlaceEntity;
import com.chinashb.www.mobileerp.basicobject.MpiWcBean;
import com.chinashb.www.mobileerp.basicobject.PlanInnerDetailEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.ExtraHRBean;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.StaticVariableUtils;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 生产投料
 */
public class StockPutActivity extends BaseActivity {

    private TextView titleTextView;
    private TextView txtMW;
    private Button selectMWHasPlanButton;//既选计划
    private Button selectMWNewPlanButton;//新选计划
    private Button continuePutButton;
    private Button continueDirectPutButton;//不需要修改
    private Button extraPutButton;
    private MpiWcBean mpiWcBean;

    private RecyclerView issuedItemRecyclerView;

    private IssuedItemAdapter issuedItemAdapter;
    private List<PlanInnerDetailEntity> IssuedItemList;

    private IstPlaceEntity thePlace;
    private String scanstring;
    private List<MpiWcBean> mpiWcBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindView();
        setHomeButton();
        setButtonListener();
    }

    protected void bindView() {
        setContentView(R.layout.activity_stock_out_layout);
        issuedItemRecyclerView = (RecyclerView) findViewById(R.id.rv_issed_items);
        titleTextView = (TextView) findViewById(R.id.tv_stock_out_title);
        txtMW = (TextView) findViewById(R.id.tv_mpi_wc_title);
        selectMWHasPlanButton = (Button) findViewById(R.id.btn_select_mpi_wc_from_selected);
        selectMWNewPlanButton = (Button) findViewById(R.id.btn_select_mpiwc);
        continuePutButton = (Button) findViewById(R.id.btn_continue_stock_out);
        extraPutButton = (Button) findViewById(R.id.btn_continue_stock_out_extra);
        continueDirectPutButton = findViewById(R.id.btn_continue_stock_out_direct_button);

        mpiWcBeanList = StaticVariableUtils.selectMpiWcBeanList;
        IssuedItemList = new ArrayList<>();
        issuedItemAdapter = new IssuedItemAdapter(StockPutActivity.this, IssuedItemList);
        issuedItemRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        issuedItemRecyclerView.setAdapter(issuedItemAdapter);

    }

    protected void setButtonListener() {

        selectMWHasPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //StockPutActivity.AsyncGetMWItems task = new StockPutActivity.AsyncGetMWItems();
                //task.execute();

                //new IntentIntegrator( StockPutActivity.this).initiateScan();

                if (mpiWcBeanList != null) {
                    if (mpiWcBeanList.size() >= 1) {
                        Intent intent = new Intent(StockPutActivity.this, SelectMPIWCStepThreeActivity.class);
                        intent.putExtra("mws", (Serializable) mpiWcBeanList);
                        startActivityForResult(intent, 200);
                    }

                }

            }

        });

        selectMWNewPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StockPutActivity.this, SelectMPIWCStepOneActivity.class);
                intent.putExtra("mw", mpiWcBean);
                startActivityForResult(intent, 100);
            }
        });


        continuePutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleContinuePut(false,"继续投料");

            }

        });
        continueDirectPutButton.setOnClickListener(v ->{
            handleContinuePut(true,"直接投料");
        });

        extraPutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mpiWcBean != null) {
//                    ToastUtil.showToastShort("Company_id =" + UserSingleton.get().getUserInfo().getCompany_ID());
//
//                    //// TODO: 11/5/24  额外领料限制
////                    如果是上海的，作限制，否则直接操作
//                    String  companySql = "Select Company_ID from bu where bu_id = " + UserSingleton.get().getUserInfo().getBu_ID();
//                    int companyID = -1;
//                    WsResult companyResult = WebServiceUtil.getDataTable(companySql);
//                    if (companyResult != null && companyResult.getResult()){
//                        String jsonData = companyResult.getErrorInfo();
//                        ArrayList<Integer> wcList = new ArrayList<Integer>();
//                        Gson gson = new Gson();
//                        wcList = gson.fromJson(jsonData, new TypeToken<List<Integer>>() {
//                        }.getType());
//                        if (wcList.size() == 1){
//                            companyID = wcList.get(0);
//                        }
//                    }
                    if (UserSingleton.get().getUserInfo().getCompany_ID() == 1){
//                    if (companyID == 1){
//                        String sql = "select HR_ID from Permission_MW_Extra_Allow";
//
////                    WebServiceUtil.getDataTable()
                        //// TODO: 11/5/24 错误原因，这个必须是在线程里面调用执行
//                        WsResult result = WebServiceUtil.getDataTable(sql);
//                        ToastUtil.showToastShort(" result =" + result.getErrorInfo() + " " + result.getResult());
//                        if (result != null && result.getResult()) {
//                            String jsonData = result.getErrorInfo();
//                            ArrayList<Integer> wcList = new ArrayList<Integer>();
//                            Gson gson = new Gson();
//                            wcList = gson.fromJson(jsonData, new TypeToken<List<Integer>>() {
//                            }.getType());
//
//                            if (wcList .contains(UserSingleton.get().getHRID())){
//                                Intent intent = new Intent(StockPutActivity.this, StockOutMoreExtraActivity.class);
//                                intent.putExtra("mw", mpiWcBean);
//                                startActivityForResult(intent, 400);
//                            }else{
//                                ToastUtil.showToastShort("您暂无权限操作额外领料");
//                            }
//
//
//                        }
                        GetExtraPutHRListAsyncTask task = new GetExtraPutHRListAsyncTask();
                        task.execute();

                    }else{
                        Intent intent = new Intent(StockPutActivity.this, StockOutMoreExtraActivity.class);
                        intent.putExtra("mw", mpiWcBean);
                        startActivityForResult(intent, 400);
                    }


                }

            }

        });

    }

    private void handleContinuePut(boolean isDirect,String title) {
        if (mpiWcBean != null) {
            Intent intent = new Intent(StockPutActivity.this, StockOutMoreActivity.class);
            intent.putExtra("mw", mpiWcBean);
            intent.putExtra("IssuedItemList", (Serializable) IssuedItemList);
            intent.putExtra(IntentConstant.Intent_continue_put_directly, isDirect);
            intent.putExtra(IntentConstant.Intent_supplier_input_title,title);
            startActivityForResult(intent, 300);
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if ((requestCode == 100 || requestCode == 200) && resultCode == 1) {
            MpiWcBean select_mw;
            select_mw = (MpiWcBean) data.getSerializableExtra("mw");
            if (select_mw != null) {
                mpiWcBean = select_mw;
                if (!Exist_mws()) {
                    mpiWcBeanList.add(mpiWcBean);
                }
                titleTextView.setText("投料出库 #" + mpiWcBean.getMPIWC_ID().toString());
                //// TODO: 2019/7/23
                mpiWcBean.setMwNameTextView(txtMW);
                AsyncShowIssuedMW task = new AsyncShowIssuedMW();
                task.execute();
            }
        }

        //投料之后，总刷新已投清单
        if (requestCode == 300 || requestCode == 400) {
            AsyncShowIssuedMW task = new AsyncShowIssuedMW();
            task.execute();
        }
        if (result != null) {
            if (result.getContents() == null) {
                //new IntentIntegrator(StockPutActivity.this).initiateScan();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                String contents = result.getContents();
                if (!contents.equals("")) {
                    if (contents.startsWith(("MI"))) {
                        scanstring = contents;
                        AsyncGetMW task = new AsyncGetMW();
                        task.execute();
                    }
                }
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private Boolean Exist_mws() {
        Boolean ex = false;
        Long ID = mpiWcBean.getMPIWC_ID();

        for (int i = 0; i < mpiWcBeanList.size(); i++) {
            Long IDi = mpiWcBeanList.get(i).getMPIWC_ID();

            if (ID.equals(IDi)) {
                ex = true;
            }
        }
        return ex;
    }


    private class AsyncGetMW extends AsyncTask<String, Void, Void> {
        MpiWcBean scanresult;
        List<PlanInnerDetailEntity> planInnerDetailEntityList;

        @Override
        protected Void doInBackground(String... params) {
            MpiWcBean mpiWcBean = WebServiceUtil.op_Check_Commit_MW_Barcode(scanstring);
            scanresult = mpiWcBean;
            if (mpiWcBean.getResult() ) {
                StockPutActivity.this.mpiWcBean = mpiWcBean;
                planInnerDetailEntityList = WebServiceUtil.opGetMWIssedItems(mpiWcBean.getMPIWC_ID());
            } else {

            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");
            if (scanresult != null) {
                if (!scanresult.getResult() ) {
                    Toast.makeText(StockPutActivity.this, scanresult.getErrorInfo(), Toast.LENGTH_LONG).show();
                } else {
                    txtMW.setText(scanresult.getMwName());
                    IssuedItemList = planInnerDetailEntityList;
                    issuedItemAdapter = new IssuedItemAdapter(StockPutActivity.this, IssuedItemList);
                    issuedItemRecyclerView.setAdapter(issuedItemAdapter);
                }

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


    private class AsyncShowIssuedMW extends AsyncTask<String, Void, Void> {
        List<PlanInnerDetailEntity> planInnerDetailEntityList;
        @Override
        protected Void doInBackground(String... params) {
            if (mpiWcBean != null) {
                planInnerDetailEntityList = WebServiceUtil.opGetMWIssedItems(mpiWcBean.getMPIWC_ID());
            } else {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");
            if (planInnerDetailEntityList != null) {
                IssuedItemList = planInnerDetailEntityList;
                issuedItemAdapter = new IssuedItemAdapter(StockPutActivity.this, IssuedItemList);
                issuedItemRecyclerView.setAdapter(issuedItemAdapter);
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

    private class AsyncGetMWItems extends AsyncTask<String, Void, List<PlanInnerDetailEntity>> {
        MpiWcBean scanresult;
        @Override
        protected List<PlanInnerDetailEntity> doInBackground(String... params) {
            List<PlanInnerDetailEntity> planInnerDetailEntityList = WebServiceUtil.opGetMWIssedItems((long) 471058);
            return planInnerDetailEntityList;
        }

        @Override
        protected void onPostExecute(List<PlanInnerDetailEntity> result) {
            //tv.setText(fahren + "∞ F");
            if (result != null) {
                IssuedItemList = result;
                issuedItemAdapter = new IssuedItemAdapter(StockPutActivity.this, IssuedItemList);
                issuedItemRecyclerView.setAdapter(issuedItemAdapter);
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


    private class GetExtraPutHRListAsyncTask extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... voids) {




            String sql = "select HR_ID from Permission_MW_Extra_Allow";

//                    WebServiceUtil.getDataTable()
            //// TODO: 11/5/24 错误原因，这个必须是在线程里面调用执行
            WsResult result = WebServiceUtil.getDataTable(sql);
//            ToastUtil.showToastShort(" result =" + result.getErrorInfo() + " " + result.getResult());

            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                if (!TextUtils.isEmpty(jsonData)) {
                    return jsonData;

                }
            }






            return null;
        }

        @Override
        protected void onPostExecute(String jsonData) {
            super.onPostExecute(jsonData);
            ArrayList<ExtraHRBean> wcList = new ArrayList<ExtraHRBean>();
            Gson gson = new Gson();
//            wcList = gson.fromJson(jsonData, new TypeToken<List<Integer>>() {
//            }.getType());
            wcList = gson.fromJson(jsonData, new TypeToken<List<ExtraHRBean>>() {
            }.getType());
            List<Integer> hrIDList = new ArrayList<>();
            for (ExtraHRBean bean : wcList){
                hrIDList.add(bean.getHrID());
            }

//            if (wcList .contains(UserSingleton.get().getHRID())){
            if (hrIDList .contains(UserSingleton.get().getHRID())){
                Intent intent = new Intent(StockPutActivity.this, StockOutMoreExtraActivity.class);
                intent.putExtra("mw", mpiWcBean);
                startActivityForResult(intent, 400);
            }else{
                ToastUtil.showToastShort("您暂无权限操作额外领料");
            }





        }
    }
}
