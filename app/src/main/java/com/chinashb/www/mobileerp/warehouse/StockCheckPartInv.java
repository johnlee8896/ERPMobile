package com.chinashb.www.mobileerp.warehouse;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.basicobject.Ist_Place;
import com.chinashb.www.mobileerp.basicobject.UserInfoEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.commonactivity.SelectItemActivity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class StockCheckPartInv extends AppCompatActivity {

    Button btnSelectCheckFile;
    Button btnScanIst;
    Button btnScanItem;
    Button btnCommit;

    TextView tvCI;
    Integer CI_ID;
    Boolean ShowERPInv = false;
    String ScanFor;
    private Ist_Place thePlace;
    private BoxItemEntity scanitem;

    private  List<BoxItemEntity> saveditems;

    private TextView tvIst;

    private TextView tvERPIst;
    private TextView tvItemCode;
    private TextView tvManuLotno;
    private TextView tvLeftQty;
    private TextView tvItemName;
    private TextView tvBoxName;
    private EditText etN;
    private EditText etPN;
    private EditText etDQ;
    private EditText etQty;
    private Button btnCal;
    private EditText etRemark;

    private Integer Ac_Type = 1;
    private UserInfoEntity userInfo;

    public StockCheckPartInv() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_check_part_inv);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setHomeButton();

        bindView();
        setButtonClick();

        getExtras();

        userInfo=StockMainActivity.userInfo;
        saveditems = new ArrayList<>();

        resumestate(savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


            }
        });
    }

    void getExtras(){
        Intent who = getIntent();
        Ac_Type=(Integer) who.getIntExtra("Ac_Type",1);
    }

    void bindView(){
        btnSelectCheckFile=(Button)findViewById(R.id.btn_select_check_inv );
        btnScanIst =(Button)findViewById(R.id.btn_select_check_inv_scan_ist);
        btnScanItem =(Button)findViewById(R.id.btn_select_check_inv_add_item);
        btnCommit =(Button)findViewById(R.id.btn_affirm_qty);
        btnCal=(Button)findViewById(R.id.btn_check_inv_cal_qty);
        tvCI=(TextView)findViewById(R.id.tv_checkinventory_file);
        tvIst=(TextView)findViewById(R.id.tv_check_stock_ist);

        tvERPIst=(TextView)findViewById(R.id.tv_check_stock_ist_erp);
        tvItemCode=(TextView)findViewById(R.id.tv_check_stock_item_code);
        tvManuLotno=(TextView)findViewById(R.id.tv_check_stock_manulotno);
        tvItemName = (TextView)findViewById(R.id.tv_check_stock_item_name);
        tvBoxName=(TextView)findViewById(R.id.tv_check_stock_box);
        tvLeftQty=(TextView)findViewById(R.id.tv_check_stock_box_left_qty);
        etQty=(EditText)findViewById(R.id.et_check_stock_box_real_qty);
        etN=(EditText)findViewById(R.id.et_check_stock_box_n);
        etPN=(EditText)findViewById(R.id.et_check_stock_box_pn);
        etDQ=(EditText)findViewById(R.id.et_check_stock_box_dq);
        etRemark=(EditText)findViewById(R.id.et_check_stock_box_remark);


    }

    void resumestate(Bundle savedInstanceState){
        if(savedInstanceState!=null)
        {
            CI_ID=savedInstanceState.getInt("CI_ID");
            thePlace=(Ist_Place)savedInstanceState.getSerializable("thePlace");
            scanitem=(BoxItemEntity)savedInstanceState.getSerializable("scanitem");
            scanstring=savedInstanceState.getString("scanstring");
            ScanFor=savedInstanceState.getString("ScanFor");
            saveditems=(List<BoxItemEntity>)savedInstanceState.getSerializable("saveditems");
            //还需恢复界面数据
        }
    }

    void setButtonClick(){
        btnSelectCheckFile.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StockCheckPartInv.this, SelectItemActivity.class);

                Integer Bu_ID = StockMainActivity.userInfo.getBu_ID();


                String W ="Select Distinct Warehouse_ID From Bu_W_Ac " +
                        "Inner Join Bu_Ac On Bu_W_Ac.Ac_Book_ID=Bu_Ac.Ac_Book_ID " +
                        "Where Bu_ID=" + Bu_ID + " And Ac_Type = " + Ac_Type ;

                String sql="Select CI_ID, CI_Name , Editor_name , Convert(nvarchar(100),CheckDate,20)," +
                        "Isnull(ShowERPInv,0) As ShowERPInv  From CheckInventory " +
                        "Inner Join (" +
                        W + ") As W On W.Warehouse_ID=CheckInventory.Warehouse_ID " +
                        "Where Bu_ID=" + Bu_ID +" And Wc_ID Is null And Ac_Type=" + Ac_Type +
                        " And " +
                        "Datediff(day, Insert_Time, Getdate())<30 ";

                List<Integer> ColWith=new ArrayList<Integer>(Arrays.asList(80,120,120,120));
                List<String>ColCaption=new ArrayList<String>(Arrays.asList("CI_ID","盘存名称","盘存者","盘存日期"));
                List<String>HiddenCol=new ArrayList<String>(Arrays.asList("ShowERPInv"));

                String Title ="选择盘点文件";
                intent.putExtra("Title",Title);
                intent.putExtra("SQL",sql);
                intent.putExtra("ColWidth", (Serializable) ColWith);
                intent.putExtra("ColCaption", (Serializable) ColCaption);
                intent.putExtra("HiddenCol", (Serializable) HiddenCol);

                startActivityForResult(intent,200);

            }
        });


        btnScanIst.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(SelectCI==null)
                {
                    Toast.makeText(StockCheckPartInv.this, "请先选择盘点文件", Toast.LENGTH_LONG).show();
                    return;
                }
                ScanFor="Ist";
                new IntentIntegrator( StockCheckPartInv.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();

            }
        });


        btnScanItem.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(thePlace==null)
                {
                    Toast.makeText(StockCheckPartInv.this, "请先扫描托盘所在的仓库单元位置条码", Toast.LENGTH_LONG).show();
                    return;
                }

                ScanFor="Item";
                new IntentIntegrator( StockCheckPartInv.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();

            }
        });


        btnCommit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(SelectCI==null)
                {
                    Toast.makeText(StockCheckPartInv.this, "请先选择盘点文件", Toast.LENGTH_LONG).show();
                    return;
                }
                if(thePlace==null)
                {
                    Toast.makeText(StockCheckPartInv.this, "请先扫描托盘所在的仓库单元位置条码", Toast.LENGTH_LONG).show();
                    return;
                }

                if(scanitem==null)
                {
                    Toast.makeText(StockCheckPartInv.this, "请扫描物料", Toast.LENGTH_LONG).show();
                    return;
                }

                String qty= etQty.getText().toString();

                if (qty.equals("")){
                    return;
                }

                if(is_box_existed(scanitem))
                {
                    CommonUtil.ShowToast(StockCheckPartInv.this,
                            "前面已经提交过",R.mipmap.warning, Toast.LENGTH_SHORT);
                    return;
                }

                Commit_Result();

            }
        });

        btnCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer n =1;
                if(etN.getText().toString().equals("")){etN.setText("1");}
                n=Integer.valueOf(etN.getText().toString());

                double pn =1;
                if(etPN.getText().toString().equals("")){etPN.setText("1");}

                pn=Double.valueOf(etPN.getText().toString());

                double dq=0;
                if (etDQ.getText().toString().equals("")){etDQ.setText("0");}
                dq=Double.valueOf(etDQ.getText().toString());

                double q;
                q=n*pn+dq;


                etQty.setText(CommonUtil.DecimalFormat(q));
            }
        });


    }

    protected void Commit_Result(){
        StockCheckPartInv.AsyncCommitStockResult task = new StockCheckPartInv.AsyncCommitStockResult();
        task.execute();
    }


    protected Boolean is_box_existed(BoxItemEntity box_item){
        Boolean result=false;

        if ( saveditems != null)
        {
            for(int i = 0; i< saveditems.size(); i++)
            {
                if(saveditems.get(i).getSMT_ID()==box_item.getSMT_ID()
                        && saveditems.get(i).getSMM_ID()==box_item.getSMM_ID()
                        && saveditems.get(i).getSMLI_ID()==box_item.getSMLI_ID() &&
                        saveditems.get(i).getLotID()==box_item.getLotID())
                {
                    return true;
                }


            }
        }

        return result;
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
        //选择盘点文件
        if(requestCode==200 && resultCode ==1 )
        {
            ActivityResultSelectInventoryFile(data);
            return ;
        }

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {

            if(result.getContents() == null) {
            }
            else {
                if(ScanFor.endsWith("Ist"))
                {
                    ActivityResultScanIst(result);
                }
                else
                {
                    ActivityResultScanItem(result);
                }

            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    static HashMap<String,String> SelectCI;

    protected void ActivityResultSelectInventoryFile(Intent data)
    {
        if(data!=null)
        {
            SelectCI= (HashMap<String,String>) data.getSerializableExtra("SelectItem");
            if(SelectCI!=null)
            {
                tvCI.setText(SelectCI.get("CI_Name"));
                CI_ID=Integer.valueOf(SelectCI.get("CI_ID"));
                ShowERPInv=Boolean.valueOf(SelectCI.get("ShowERPInv"));

                //空
                thePlace=null;
                tvIst.setText("");

                scanitem=null;
            }
        }
    }


    protected void ActivityResultScanIst(IntentResult result )
    {

        Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

        String X = result.getContents();

        if (X.startsWith("/SUB_IST_ID/")||X.startsWith("/IST_ID/"))
        {
            //仓库位置码
            scanstring= X;

            StockCheckPartInv.AsyncGetIst task = new StockCheckPartInv.AsyncGetIst();
            task.execute();

        }

    }

    String scanstring;



    protected void ActivityResultScanItem(IntentResult result )
    {

        Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

        String X = result.getContents();

        scanstring= X;

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

                        AsyncGetInvBox task = new AsyncGetInvBox();
                        task.execute();
                    }
                }

            }
        }
    }


    @Override
    protected void onResume() {
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT )
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        }
        super.onResume();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("CI_ID",  CI_ID);
        outState.putSerializable("thePlace",  (Serializable)thePlace );
        outState.putSerializable("scanitem",  (Serializable)scanitem );
        outState.putSerializable("scanstring",  (Serializable)scanstring );
        outState.putSerializable("ScanFor",  (Serializable)ScanFor );

        outState.putSerializable("saveditems", (Serializable) saveditems);

    }

    private class AsyncGetInvBox extends AsyncTask<String, Void, Void> {
        BoxItemEntity scanresult;
        @Override
        protected Void doInBackground(String... params) {

            BoxItemEntity bi=null;
            if (Ac_Type==1 && userInfo.getBu_ID()==1)
            {
                 bi= WebServiceUtil.op_Check_Stock_Item_Barcode(userInfo.getBu_ID(), scanstring);
            }
            else
            {
                bi = WebServiceUtil.op_Check_Stock_Item_Barcode_V2(scanstring);
            }


            scanresult=bi;



            return null;
        }




        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");

            tvItemCode.setText(scanstring);

            if ( scanresult!=null)
            {
                if(scanresult.getResult()==false)
                {
                    Toast.makeText(StockCheckPartInv.this,scanresult.getErrorInfo(),Toast.LENGTH_LONG).show();
                }

                BoxItemEntity bi = scanresult;
                if (bi.getResult()==true )
                {
                    if(is_box_existed(bi))
                    {
                        CommonUtil.ShowToast(StockCheckPartInv.this,
                                "前面已经扫描过",R.mipmap.warning, Toast.LENGTH_SHORT);
                        return;
                    }


                    //暂存一下
                    scanitem=bi;

                    DecimalFormat DF = new DecimalFormat("####.####");


                    tvERPIst.setText(bi.getIstName());
                    if(thePlace.getIst_ID()!=bi.getIst_ID() || thePlace.getSub_Ist_ID()!=bi.getSub_Ist_ID())
                    {
                        tvERPIst.setTextColor(Color.RED);
                    }
                    else
                    {tvERPIst.setTextColor(Color.BLACK);}
                    tvItemName.setText(bi.getItemName());
                    tvBoxName.setText(bi.getBoxNameNo() );
                    tvManuLotno.setText(bi.getManuLotNo());
                    if(!ShowERPInv)
                    {tvLeftQty.setVisibility(View.INVISIBLE);}
                    else
                    {tvLeftQty.setVisibility(View.VISIBLE);}
                    tvLeftQty.setText(DF.format(bi.getQty()));
                    //etQty.setText(DF.format(bi.getBoxQty()));

                    if(bi.getQty()!=bi.getBoxQty())
                    {
                        tvLeftQty.setTextColor(Color.RED);
                    }
                    else
                    {
                        tvLeftQty.setTextColor(Color.BLACK);
                    }
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


    private class AsyncGetIst extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {


            Ist_Place bi = WebServiceUtil.op_Check_Commit_IST_Barcode(scanstring);

            if (bi.getResult()==true)
            {
                thePlace = bi;

                //清空
                scanitem=null;
            }
            else
            {
                Toast.makeText(StockCheckPartInv.this,bi.getErrorInfo(),Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if(thePlace!=null){
             tvIst.setText(thePlace.getIstName());
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




    private class AsyncCommitStockResult extends AsyncTask<String, Void, Void> {
        WsResult ws_result;


        @Override

        protected Void doInBackground(String... params) {

            BoxItemEntity bi=scanitem;
            String qty= etQty.getText().toString();
            String remark=etRemark.getText().toString();

            String N=etN.getText().toString();
            String PN = etPN.getText().toString();
            String DQ=etDQ.getText().toString();

            if(Ac_Type==1 && userInfo.getBu_ID()==1)
            {
                ws_result = WebServiceUtil.op_Commit_Stock_Result_V4(userInfo.getHR_Name(),
                        CI_ID, userInfo.getBu_ID(),scanstring,thePlace.getIst_ID(),thePlace.getSub_Ist_ID(),
                        bi.getSMT_ID(),bi.getSMM_ID(),bi.getSMLI_ID(),bi.getLotID(),qty,N,PN,DQ,remark);

            }
            else
            { ws_result = WebServiceUtil.op_Commit_Stock_Result_V3(userInfo.getHR_Name(),
                    CI_ID,scanstring,thePlace.getIst_ID(),thePlace.getSub_Ist_ID(),
                    bi.getSMT_ID(),bi.getSMM_ID(),bi.getSMLI_ID(),qty,N,PN,DQ,remark);
            }



            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (ws_result.getResult()==true)
            {
                CommonUtil.ShowToast(StockCheckPartInv.this,
                        "提交成功",R.mipmap.smiley, Toast.LENGTH_SHORT);

                saveditems.add(scanitem);

                //Clear Text
                etQty.setText("");
                etRemark.setText("");
                tvBoxName.setText("");
                tvERPIst.setText("");
                tvItemCode.setText("");
                tvItemName.setText("");
                tvLeftQty.setText("");
                tvManuLotno.setText("");
            }
            else
            {
                CommonUtil.ShowToast(StockCheckPartInv.this,
                        "提交失败" + ws_result.getErrorInfo(),R.mipmap.warning, Toast.LENGTH_SHORT);

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



}
