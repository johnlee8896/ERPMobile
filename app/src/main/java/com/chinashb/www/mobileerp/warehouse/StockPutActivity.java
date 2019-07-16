package com.chinashb.www.mobileerp.warehouse;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.IssuedItemAdapter;
import com.chinashb.www.mobileerp.basicobject.Issued_Item;
import com.chinashb.www.mobileerp.basicobject.IstPlaceEntity;
import com.chinashb.www.mobileerp.basicobject.MpiWcBean;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.StaticVariableUtils;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 生产投料
 */
public class StockPutActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView txtMW;
    private Button selectMWHasPlanButton;//既选计划
    private Button selectMWNewPlanButton;//新选计划
    private Button continuePutButton;
    private Button continueDirectPutButton;//不需要修改
    private Button extraPutButton;
    private MpiWcBean mpiWcBean;

    private RecyclerView mrv_issued_items;

    private IssuedItemAdapter issuedItemAdapter;
    private List<Issued_Item> IssuedItemList;

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
        mrv_issued_items = (RecyclerView) findViewById(R.id.rv_issed_items);
        titleTextView = (TextView) findViewById(R.id.tv_stock_out_title);
        txtMW = (TextView) findViewById(R.id.tv_mpi_wc_title);
        selectMWHasPlanButton = (Button) findViewById(R.id.btn_select_mpi_wc_from_selected);
        selectMWNewPlanButton = (Button) findViewById(R.id.btn_select_mpiwc);
        continuePutButton = (Button) findViewById(R.id.btn_continue_stock_out);
        extraPutButton = (Button) findViewById(R.id.btn_continue_stock_out_extra);
        continueDirectPutButton = findViewById(R.id.btn_continue_stock_out_direct_button);

        mpiWcBeanList = StaticVariableUtils.selected_mws;
        IssuedItemList = new ArrayList<>();
        issuedItemAdapter = new IssuedItemAdapter(StockPutActivity.this, IssuedItemList);
        mrv_issued_items.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        mrv_issued_items.setAdapter(issuedItemAdapter);

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
                handleContinuePut(false);

            }

        });
        continueDirectPutButton.setOnClickListener(v ->{
            handleContinuePut(true);
        });

        extraPutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mpiWcBean != null) {
                    Intent intent = new Intent(StockPutActivity.this, StockOutMoreExtraActivity.class);
                    intent.putExtra("mw", mpiWcBean);
                    startActivityForResult(intent, 400);
                }

            }

        });

    }

    private void handleContinuePut(boolean isDirect) {
        if (mpiWcBean != null) {
            Intent intent = new Intent(StockPutActivity.this, StockOutMoreActivity.class);
            intent.putExtra("mw", mpiWcBean);
            intent.putExtra("IssuedItemList", (Serializable) IssuedItemList);
            intent.putExtra(IntentConstant.Intent_continue_put_directly, isDirect);
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
                mpiWcBean.setMwNameTextView(txtMW);

                StockPutActivity.AsyncShowIssuedMW task = new StockPutActivity.AsyncShowIssuedMW();
                task.execute();
            }
        }

        //投料之后，总刷新已投清单
        if (requestCode == 300 || requestCode == 400) {
            StockPutActivity.AsyncShowIssuedMW task = new StockPutActivity.AsyncShowIssuedMW();
            task.execute();
        }

        if (result != null) {
            if (result.getContents() == null) {
                //new IntentIntegrator(StockPutActivity.this).initiateScan();

            } else {

                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

                String X = result.getContents();
                if (!X.equals("")) {
                    if (X.startsWith(("MI"))) {
                        scanstring = X;

                        StockPutActivity.AsyncGetMW task = new StockPutActivity.AsyncGetMW();
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
        List<Issued_Item> li;

        @Override
        protected Void doInBackground(String... params) {

            MpiWcBean bi = WebServiceUtil.op_Check_Commit_MW_Barcode(scanstring);


            scanresult = bi;

            if (bi.getResult() == true) {
                mpiWcBean = bi;

                li = WebServiceUtil.opGetMWIssedItems(bi.getMPIWC_ID());

            } else {

            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");

            if (scanresult != null) {
                if (scanresult.getResult() == false) {
                    Toast.makeText(StockPutActivity.this, scanresult.getErrorInfo(), Toast.LENGTH_LONG).show();
                } else {
                    txtMW.setText(scanresult.getMwName());

                    IssuedItemList = li;
                    issuedItemAdapter = new IssuedItemAdapter(StockPutActivity.this, IssuedItemList);

                    mrv_issued_items.setAdapter(issuedItemAdapter);
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
        List<Issued_Item> li;

        @Override
        protected Void doInBackground(String... params) {


            if (mpiWcBean != null) {

                li = WebServiceUtil.opGetMWIssedItems(mpiWcBean.getMPIWC_ID());

            } else {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");

            if (li != null) {
                IssuedItemList = li;
                issuedItemAdapter = new IssuedItemAdapter(StockPutActivity.this, IssuedItemList);

                mrv_issued_items.setAdapter(issuedItemAdapter);

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

    private class AsyncGetMWItems extends AsyncTask<String, Void, List<Issued_Item>> {
        MpiWcBean scanresult;

        @Override
        protected List<Issued_Item> doInBackground(String... params) {

            List<Issued_Item> li = WebServiceUtil.opGetMWIssedItems((long) 471058);


            return li;
        }


        @Override
        protected void onPostExecute(List<Issued_Item> result) {
            //tv.setText(fahren + "∞ F");

            if (result != null) {
                IssuedItemList = result;
                issuedItemAdapter = new IssuedItemAdapter(StockPutActivity.this, IssuedItemList);
                mrv_issued_items.setAdapter(issuedItemAdapter);
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


    @Override
    protected void onResume() {
//设置为屏幕
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        }

        super.onResume();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("MWList", (Serializable) mpiWcBeanList);

    }
}
