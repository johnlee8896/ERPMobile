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
import android.widget.Toast;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.chinashb.www.mobileerp.adapter.AdapterInBoxItem;
import com.chinashb.www.mobileerp.basicobject.Ist_Place;
import com.chinashb.www.mobileerp.basicobject.Ws_Result;
import com.chinashb.www.mobileerp.funs.CommonUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StockInActivity extends AppCompatActivity {


    private Button btnAddTray;
    private Button btnScanArea;
    private Button btnStockIn;
    private Button btnStartMoving;
    private Button btnWarehouseIn;

    private RecyclerView mRecyclerView;

    private AdapterInBoxItem boxitemAdapter;
    private List<BoxItemEntity> boxitemList=new ArrayList<>();
    private Ist_Place thePlace;
    private String scanstring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_in);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_box_item);
        btnAddTray = (Button)findViewById(R.id.btn_add_tray);
        btnScanArea = (Button) findViewById(R.id.btn_scan_area);
        btnWarehouseIn=(Button) findViewById(R.id.btn_exe_warehouse_in);

        setHomeButton();

        if(savedInstanceState!=null)
        {
            boxitemList=(List<BoxItemEntity>)savedInstanceState.getSerializable("BoxItemList");
        }

        boxitemAdapter= new AdapterInBoxItem(StockInActivity.this, boxitemList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        mRecyclerView.setAdapter(boxitemAdapter);


        btnAddTray.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //scanstring= "VG/404731";
                //StockInActivity.AsyncGetBox task = new StockInActivity.AsyncGetBox();
                //task.execute();

                new IntentIntegrator( StockInActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
            }

        });

        btnScanArea.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                if (boxitemList.size()>0)
                {
                    int selectedcount=0 ;
                    for (int i =0; i<boxitemList.size(); i++)
                    {
                        if (boxitemList.get(i).getSelect()==true){selectedcount++;}
                    }
                    if (selectedcount>0)
                    {
                        new IntentIntegrator( StockInActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
                    }

                }

            }

        });


        btnWarehouseIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                if (boxitemList.size()>0)
                {
                    int selectedcount=0 ;
                    for (int i =0; i<boxitemList.size(); i++)
                    {
                        if (boxitemList.get(i).getSelect()==true)
                        {
                            if(boxitemList.get(i).getIst_ID()==0)
                            {
                                CommonUtil.ShowToast(StockInActivity.this,"还没有扫描库位",R.mipmap.warning, Toast.LENGTH_SHORT);

                                return;
                            }
                            selectedcount++;
                        }

                    }
                    if (selectedcount>0)
                    {
                        StockInActivity.AsyncExeWarehouseIn task = new StockInActivity.AsyncExeWarehouseIn();
                        task.execute();

                    }

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
                //new IntentIntegrator(StockInActivity.this).initiateScan();

            } else {

                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

                String X = result.getContents();
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
                                scanstring= X;

                                StockInActivity.AsyncGetBox task = new StockInActivity.AsyncGetBox();
                                task.execute();
                            }

                        }



                        if (X.startsWith("/SUB_IST_ID/")||X.startsWith("/IST_ID/"))
                        {
                            //仓库位置码
                            scanstring= X;

                            StockInActivity.AsyncGetIst task = new StockInActivity.AsyncGetIst();
                            task.execute();

                        }



                    }
                }


            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    private class AsyncGetBox extends AsyncTask<String, Void, Void> {
        BoxItemEntity scanresult;
        @Override
        protected Void doInBackground(String... params) {

            BoxItemEntity bi = WebServiceUtil.op_Check_Commit_DS_Item_Income_Barcode(scanstring);

            scanresult=bi;

            if (bi.getResult()==true)
            {
                if (!is_box_existed(bi))
                {
                    bi.setSelect(true);
                    boxitemList.add(bi);
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

            if ( boxitemList != null)
            {
                for(int i =0 ; i< boxitemList.size();i++)
                {
                    if(boxitemList .get(i).getDIII_ID()==box_item.getDIII_ID())
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
                    Toast.makeText(StockInActivity.this,scanresult.getErrorInfo(),Toast.LENGTH_LONG).show();
                }
            }

            boxitemAdapter= new AdapterInBoxItem(StockInActivity.this, boxitemList);
            mRecyclerView.setAdapter(boxitemAdapter);
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


            Ist_Place bi = WebServiceUtil.op_Check_Commit_IST_Barcode(scanstring);

            if (bi.getResult()==true)
            {
                thePlace = bi;

                if(bi.getResult()==true)
                {
                    for (int i=0;i<boxitemList.size();i++)
                    {
                        if(boxitemList.get(i).getSelect()==true)
                        {
                            boxitemList.get(i).setIstName(bi.getIstName());
                            boxitemList.get(i).setIst_ID(bi.getIst_ID());
                            boxitemList.get(i).setSub_Ist_ID(bi.getSub_Ist_ID());


                        }
                    }
                }
            }
            else
            {
                Toast.makeText(StockInActivity.this,bi.getErrorInfo(),Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");

            mRecyclerView.setAdapter(boxitemAdapter);
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




    private class AsyncExeWarehouseIn extends AsyncTask<String, Void, Void> {
        Ws_Result ws_result;

        @Override
        protected Void doInBackground(String... params) {

            List<BoxItemEntity> SelectList;
            SelectList= new ArrayList<>();

            for (int i=0;i<boxitemList.size();i++)
            {
                if(boxitemList.get(i).getSelect()==true)
                {
                    SelectList.add(boxitemList.get(i));
                }
            }

            int count=0;
            int selectedcount=SelectList.size();

            while(count < selectedcount && SelectList.size()>0)
            {
                BoxItemEntity bi = SelectList.get(0);
                ws_result = WebServiceUtil.op_Commit_DS_Item(bi);


                if (ws_result.getResult()==true)
                {
                    boxitemList.remove(bi);
                    SelectList.remove(bi);
                }

                count++;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");

            if (ws_result!=null)
            {
                if (ws_result.getResult()==false)
                {
                    //Toast.makeText(StockInActivity.this,ws_result.getErrorInfo(),Toast.LENGTH_LONG).show();
                    CommonUtil.ShowToast(StockInActivity.this, ws_result.getErrorInfo(),R.mipmap.warning);

                }
                else
                {
                    //Toast.makeText(StockInActivity.this,"入库完成",Toast.LENGTH_LONG).show();
                    CommonUtil.ShowToast(StockInActivity.this, "入库完成",R.mipmap.smiley);
                }

            }

            boxitemAdapter= new AdapterInBoxItem(StockInActivity.this, boxitemList);
            mRecyclerView.setAdapter(boxitemAdapter);
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
    //设置为竖屏
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT )
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        }

        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("BoxItemList", (Serializable) boxitemList);

    }

}
