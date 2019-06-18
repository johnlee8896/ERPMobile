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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.chinashb.www.mobileerp.adapter.IssueMoreItemAdapter;
import com.chinashb.www.mobileerp.basicobject.Issued_Item;
import com.chinashb.www.mobileerp.basicobject.Ist_Place;
import com.chinashb.www.mobileerp.basicobject.Mpi_Wc;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.funs.CommonUtil;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StockOutMoreActivity extends AppCompatActivity {

    private Mpi_Wc themw;
    private List<Issued_Item> IssuedItemList;

    private TextView txtMw_Title;

    private Button btnAddTray;
    private Button btnScanWC;
    private Button btnWarehouseOut;

    private RecyclerView mRecyclerView;

    private IssueMoreItemAdapter issueMoreItemAdapter;
    private List<BoxItemEntity> newissuelist;
    private Ist_Place thePlace;
    private String scanedItem;
    private ProgressBar pbScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_out_more);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_issue_more);

        txtMw_Title=(TextView)findViewById(R.id.tv_issue_more_mw_title);
        btnAddTray = (Button)findViewById(R.id.btn_issue_more_add);
        //btnScanWC = (Button) findViewById(R.id.btn_issue_more_wc);
        btnWarehouseOut =(Button) findViewById(R.id.btn_exe_warehouse_out);

        pbScan = (ProgressBar)findViewById(R.id.pb_scan_progressbar);

        newissuelist =  new ArrayList<>();

        if(savedInstanceState!=null)
        {
            newissuelist=(List<BoxItemEntity>)savedInstanceState.getSerializable("BoxItemList");
        }

        issueMoreItemAdapter = new IssueMoreItemAdapter(StockOutMoreActivity.this, newissuelist);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        mRecyclerView.setAdapter(issueMoreItemAdapter);

        Intent who = getIntent();
        themw= (Mpi_Wc) who.getSerializableExtra("mw");
        if(themw !=null)
        {
            themw.setMwNameTextView(txtMw_Title);


        }
        IssuedItemList=(List<Issued_Item>)who.getSerializableExtra("IssuedItemList");

        setHomeButton();

        btnAddTray.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                new IntentIntegrator( StockOutMoreActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
            }

        });


        btnWarehouseOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                if (newissuelist.size()>0)
                {
                    StockOutMoreActivity.AsyncExeWarehouseOut task = new StockOutMoreActivity.AsyncExeWarehouseOut();
                    task.execute();

                }

            }

        });


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

    protected void setHomeButton(){
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                //new IntentIntegrator(StockOutMoreActivity.this).initiateScan();

            }
            else {
                String X = result.getContents();
                Toast.makeText(this, "Scanned: " + X, Toast.LENGTH_LONG).show();

                AfterGetItemBarcode(X);

            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void AfterGetItemBarcode(String X)
    {
        //简单分析判别错误条码
        if(X.contains("/"))
        {
            String [] qrContent;
            qrContent = X.split("/");
            if (qrContent.length >=2)
            {

                String qrTitle = qrContent[0];

                if (! qrTitle.equals(""))
                {
                    if (qrTitle.equals("VE")|| qrTitle.equals("VF") || qrTitle.equals("VG") || qrTitle.equals("V9") || qrTitle.equals("VA") || qrTitle.equals("VB")|| qrTitle.equals("VC"))
                    {
                        //物品条码
                        scanedItem = X;

                        AsyncGetIssueMoreBox task = new AsyncGetIssueMoreBox();
                        task.execute();
                    }
                }
            }
        }
    }


    private class AsyncGetIssueMoreBox extends AsyncTask<String, Void, Void> {
        BoxItemEntity scanresult;
        @Override
        protected Void doInBackground(String... params) {

            Long MW_ID=themw.getMPIWC_ID();
            BoxItemEntity bi = WebServiceUtil.op_Check_Commit_MW_Issue_Item_Barcode(MW_ID, scanedItem);

            scanresult=bi;

            if (bi.getResult()==true)
            {
                if (!is_box_existed(bi))
                {
                    bi.setSelect(true);
                    setNeedQty(bi);
                    newissuelist.add(bi);
                }
                else
                    {
                        bi.setResult(false);
                        bi.setErrorInfo("该包装已经在列表中");
                    }
            }

            return null;
        }


        protected Boolean is_box_existed(BoxItemEntity box_item){
            Boolean result=false;

            if ( newissuelist != null)
            {
                for(int i = 0; i< newissuelist.size(); i++)
                {
                    if(box_item.getSMT_ID()>0)
                    {
                        if(newissuelist.get(i).getSMT_ID()==box_item.getSMT_ID())
                        {
                            return true;
                        }
                    }
                    if(box_item.getSMM_ID()>0)
                    {
                        if(newissuelist.get(i).getSMM_ID()==box_item.getSMM_ID())
                        {
                            return true;
                        }
                    }
                    if(box_item.getSMLI_ID()>0)
                    {
                        if(newissuelist.get(i).getSMLI_ID()==box_item.getSMLI_ID())
                        {
                            return true;
                        }
                    }

                }
            }

            return result;
        }

        //获取还需求数
        protected  void setNeedQty(BoxItemEntity box_item)
        {
            if(box_item!=null && IssuedItemList !=null)
            {
                for (int i =0 ; i< IssuedItemList.size(); i++)
                {
                    Issued_Item issued_item= IssuedItemList.get(i);
                    if (issued_item.getItem_ID() == box_item.getItem_ID())
                    {
                        box_item.setNeedMoreQty(issued_item.getMoreQty());
                    }
                }
            }
        }



        @Override
        protected void onPostExecute(Void result) {

            if ( scanresult!=null)
            {
                if(scanresult.getResult()==false)
                {
                    CommonUtil.ShowToast(StockOutMoreActivity.this,scanresult.getErrorInfo(), R.mipmap.warning, Toast.LENGTH_LONG);
                }
            }

            issueMoreItemAdapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(issueMoreItemAdapter);
            pbScan.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPreExecute() {
            pbScan.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }


    //测试从服务器扫码，查看条码是否合规
    private class AsyncDirectGetBox extends AsyncTask<String, Void, Void> {
        BoxItemEntity scanresult;
        @Override
        protected Void doInBackground(String... params) {

            BoxItemEntity bi = WebServiceUtil.op_Check_Commit_MW_Issue_Item_Barcode((long)471059,"VE/3655118");

            scanresult=bi;

            if (bi.getResult()==true)
            {
                if (!is_box_existed(bi))
                {
                    bi.setSelect(true);
                    newissuelist.add(bi);
                }
                else
                {
                    bi.setResult(false);
                    bi.setErrorInfo("该包装已经在装载列表中");
                }



            }
            else
            {

            }

            return null;
        }

        protected Boolean is_box_existed(BoxItemEntity box_item){
            Boolean result=false;

            if ( newissuelist != null)
            {
                for(int i = 0; i< newissuelist.size(); i++)
                {
                    if(newissuelist.get(i).getSMLI_ID()==box_item.getSMLI_ID() && box_item.getSMLI_ID()>0)
                    {
                        return true;
                    }
                    if(newissuelist.get(i).getSMM_ID()==box_item.getSMM_ID() && box_item.getSMM_ID()>0)
                    {
                        return true;
                    }
                    if(newissuelist.get(i).getSMT_ID()==box_item.getSMT_ID() && box_item.getSMT_ID()>0)
                    {
                        return true;
                    }
                    if(newissuelist.get(i).getSMT_ID()==box_item.getSMT_ID() && box_item.getSMT_ID()==0
                            && newissuelist.get(i).getSMM_ID()==box_item.getSMM_ID() && box_item.getSMM_ID()==0
                            && newissuelist.get(i).getSMLI_ID()==box_item.getSMLI_ID() && box_item.getSMLI_ID()==0
                            && newissuelist.get(i).getLotID()==box_item.getLotID())
                    {
                        return true;
                    }


                }
            }

            return result;
        }



        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");

            if ( scanresult!=null)
            {
                if(scanresult.getResult()==false)
                {
                    Toast.makeText(StockOutMoreActivity.this,scanresult.getErrorInfo(),Toast.LENGTH_LONG).show();
                }
            }

            issueMoreItemAdapter = new IssueMoreItemAdapter(StockOutMoreActivity.this, newissuelist);
            mRecyclerView.setAdapter(issueMoreItemAdapter);

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

    private class AsyncGetIst extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {


            Ist_Place bi = WebServiceUtil.op_Check_Commit_IST_Barcode(scanedItem);

            if (bi.getResult()==true)
            {
                thePlace = bi;

                if(bi.getResult()==true)
                {
                    for (int i = 0; i< newissuelist.size(); i++)
                    {
                        if(newissuelist.get(i).getSelect()==true)
                        {
                            newissuelist.get(i).setIstName(bi.getIstName());
                            newissuelist.get(i).setIst_ID(bi.getIst_ID());
                            newissuelist.get(i).setSub_Ist_ID(bi.getSub_Ist_ID());


                        }
                    }
                }
            }
            else
            {
                Toast.makeText(StockOutMoreActivity.this,bi.getErrorInfo(),Toast.LENGTH_LONG).show();
            }
            /*
            List<Boolean> result;
            MealTypeEntity mealType;
            for (Date d:weekDates) {
                mealType = new MealTypeEntity();
                mealType.setDate(d);
                result = WebServiceUtil.getFoodOrderDay(userInfo.getHrID(), d);
                if (result.size()==4)
                {
                    mealType.setBreakfast(result.get(0));
                    mealType.setLunch(result.get(1));
                    mealType.setDinner(result.get(2));
                    mealType.setSnack(result.get(3));
                }
                mealTypes.add(mealType);
            } */
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");

            mRecyclerView.setAdapter(issueMoreItemAdapter);
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


    private class AsyncExeWarehouseOut extends AsyncTask<String, Void, Void> {
        WsResult ws_result;
        @Override
        protected Void doInBackground(String... params) {

            int count=0;

            int newissuesize= newissuelist.size();
;
            while(count<newissuesize && newissuelist.size()>0)
            {
                BoxItemEntity bi = newissuelist.get(0);

                ws_result = WebServiceUtil.op_Commit_MW_Issue_Item(themw.getMPIWC_ID(),bi );

                if (ws_result.getResult()==true)
                {
                    newissuelist.remove(bi);
                    UpdateNeedQty(bi);
                }
                else
                {
                    //遇到错误，停止
                    return null;
                }

                count++;
            }

            return null;
        }


        protected  void UpdateNeedQty(BoxItemEntity box_item)
        {
            if(box_item!=null && IssuedItemList !=null)
            {
                for (int i =0 ; i< IssuedItemList.size(); i++)
                {
                    Issued_Item issued_item= IssuedItemList.get(i);
                    if (issued_item.getItem_ID() == box_item.getItem_ID())
                    {
                        float oldmoreqty=issued_item.getMoreQty();
                        float newmoreqty = oldmoreqty-box_item.getQty();
                        issued_item.setMoreQty(newmoreqty);

                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Void result) {

            issueMoreItemAdapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(issueMoreItemAdapter);
            pbScan.setVisibility(View.INVISIBLE);

            if (ws_result!=null)
            {
                if (ws_result.getResult()==false)
                {
                    CommonUtil.ShowToast(StockOutMoreActivity.this, ws_result.getErrorInfo(),R.mipmap.warning,Toast.LENGTH_LONG);
                }
                else
                {
                    CommonUtil.ShowToast(StockOutMoreActivity.this, "成功出库",R.mipmap.smiley,Toast.LENGTH_SHORT);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            pbScan.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }


    @Override
    protected void onResume() {
//设置为横屏幕
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT )
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        }

        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("BoxItemList", (Serializable) newissuelist);

    }

}
