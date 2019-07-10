package com.chinashb.www.mobileerp.warehouse;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.chinashb.www.mobileerp.adapter.AdapterMoveBoxItem;
import com.chinashb.www.mobileerp.basicobject.IstPlaceEntity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.funs.CommonUtil;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StockMoveActivity extends AppCompatActivity {


    private Button btnAddTray;
    private Button btnScanArea;
    private Button btnWarehouseMove;
    private EditText inputEditText;
    private RecyclerView mRecyclerView;

    private ProgressBar pbScan;

    private AdapterMoveBoxItem boxitemAdapter;
    private List<BoxItemEntity> boxitemList;
    private IstPlaceEntity thePlace;
    private String scanstring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_move_layout);

//        tv = (TextView)findViewById(R.id.tv_stock_system_title);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_move_box);
        pbScan = (ProgressBar) findViewById(R.id.pb_scan_progressbar);

        btnAddTray = (Button) findViewById(R.id.btn_move_add_tray);
        btnScanArea = (Button) findViewById(R.id.btn_move_scan_new_place);
        btnWarehouseMove = (Button) findViewById(R.id.btn_move_execute);
        inputEditText = findViewById(R.id.stock_move_input_EditeText);


        boxitemList = new ArrayList<>();


        if (savedInstanceState != null) {
            boxitemList = (List<BoxItemEntity>) savedInstanceState.getSerializable("BoxItemList");
        }

        boxitemAdapter = new AdapterMoveBoxItem(StockMoveActivity.this, boxitemList);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        mRecyclerView.setAdapter(boxitemAdapter);

        setHomeButton();


        btnAddTray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (boxitemList.size() < 10) {
                    new IntentIntegrator(StockMoveActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
                } else {
                    Toast.makeText(StockMoveActivity.this, "移动清单不超过10个 ", Toast.LENGTH_LONG).show();
                }
            }

        });

        btnScanArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (boxitemList.size() > 0) {
                    int selectedcount = 0;
                    for (int i = 0; i < boxitemList.size(); i++) {
                        if (boxitemList.get(i).getSelect() == true) {
                            selectedcount++;
                        }
                    }
                    if (selectedcount > 0) {
                        new IntentIntegrator(StockMoveActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
                    }

                }

            }

        });


        btnWarehouseMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (boxitemList.size() > 0) {
                    int selectedcount = 0;
                    for (int i = 0; i < boxitemList.size(); i++) {
                        if (boxitemList.get(i).getSelect() == true) {
                            selectedcount++;
                        }
                    }
                    if (selectedcount > 0) {
                        AsyncExeWarehouseMove task = new AsyncExeWarehouseMove();
                        task.execute();

                    }

                }

            }

        });

        inputEditText.addTextChangedListener(new TextWatcherImpl(){
            @Override public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                if (editable.toString().endsWith("\n")){
                    ToastUtil.showToastLong("扫描结果:" + editable.toString());
                    System.out.println("========================扫描结果:" + editable.toString());
                    parseScanResult(editable.toString());
                }
            }
        });


    }

    private void parseScanResult(String result) {
        Toast.makeText(this, "Scanned: " + result, Toast.LENGTH_LONG).show();

//        String X = result.getContents();
        if (result.contains("/")) {
            String[] qrContent;
            qrContent = result.split("/");
            if (qrContent.length >= 2) {
                String qrTitle = qrContent[0];
                if (!qrTitle.equals("")) {
                    if (qrTitle.equals("VE") || qrTitle.equals("VF") || qrTitle.equals("VG") || qrTitle.equals("V9") || qrTitle.equals("VA") || qrTitle.equals("VB") || qrTitle.equals("VC")) {
                        //物品条码
                        scanstring = result;
                        GetBoxAsyncTask task = new GetBoxAsyncTask();
                        task.execute();
                    }
                }

                if (result.startsWith("/SUB_IST_ID/") || result.startsWith("/IST_ID/")) {
                    //仓库位置码
                    scanstring = result;
                    GetIstAsyncTask task = new GetIstAsyncTask();
                    task.execute();
                }

            }
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
        if (result != null) {
            if (result.getContents() == null) {
                //不要重复启动扫码
                //new IntentIntegrator(StockMoveActivity.this).initiateScan();

            } else {

               parseScanResult(result.getContents());


            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private class GetBoxAsyncTask extends AsyncTask<String, Void, Void> {
        BoxItemEntity scanresult;

        @Override
        protected Void doInBackground(String... params) {
            BoxItemEntity bi = WebServiceUtil.op_Check_Commit_Move_Item_Barcode(scanstring);
            scanresult = bi;
            if (bi.getResult() == true) {
                if (!is_box_existed(bi)) {
                    bi.setSelect(true);
                    boxitemList.add(bi);
                } else {
                    bi.setResult(false);
                    bi.setErrorInfo("该包装已经在装载列表中");
                }

            } else {

            }

            return null;
        }

        protected Boolean is_box_existed(BoxItemEntity box_item) {
            Boolean result = false;
            if (boxitemList != null) {
                for (int i = 0; i < boxitemList.size(); i++) {
                    if (boxitemList.get(i).getDIII_ID() == box_item.getDIII_ID()) {
                        return true;
                    }
                }
            }
            return result;
        }


        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");
            if (scanresult != null) {
                if (scanresult.getResult() == false) {
                    Toast.makeText(StockMoveActivity.this, scanresult.getErrorInfo(), Toast.LENGTH_LONG).show();
                }
            }
            mRecyclerView.setAdapter(boxitemAdapter);
            pbScan.setVisibility(View.INVISIBLE);
            inputEditText.setText("");
            inputEditText.setHint("请继续扫描");
        }

        @Override
        protected void onPreExecute() {
            pbScan.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }


    private class GetIstAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            IstPlaceEntity bi = WebServiceUtil.op_Check_Commit_IST_Barcode(scanstring);
            if (bi.getResult() == true) {
                thePlace = bi;
                if (bi.getResult() == true) {
                    for (int i = 0; i < boxitemList.size(); i++) {
                        if (boxitemList.get(i).getSelect() == true) {
                            boxitemList.get(i).setIstName(bi.getIstName());
                            boxitemList.get(i).setIst_ID(bi.getIst_ID());
                            boxitemList.get(i).setSub_Ist_ID(bi.getSub_Ist_ID());
                        }
                    }
                }
            } else {
                Toast.makeText(StockMoveActivity.this, bi.getErrorInfo(), Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");
            boxitemAdapter = new AdapterMoveBoxItem(StockMoveActivity.this, boxitemList);
            mRecyclerView.setAdapter(boxitemAdapter);
            pbScan.setVisibility(View.INVISIBLE);
            inputEditText.setText("");
            inputEditText.setHint("请继续扫描");
        }

        @Override
        protected void onPreExecute() {
            pbScan.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }


    private class AsyncExeWarehouseMove extends AsyncTask<String, Void, Void> {
        Boolean NoNewPlace = false;

        @Override
        protected Void doInBackground(String... params) {

            List<BoxItemEntity> SelectList;
            SelectList = new ArrayList<>();

            for (int i = 0; i < boxitemList.size(); i++) {
                if (boxitemList.get(i).getIst_ID() == 0) {
                    NoNewPlace = true;

                    return null;
                }

                if (boxitemList.get(i).getSelect() == true) {
                    SelectList.add(boxitemList.get(i));
                }


            }

            int count = 0;

            int selectedcount = SelectList.size();
            while (count < selectedcount && SelectList.size() > 0) {
                BoxItemEntity bi = SelectList.get(0);

                WsResult result = WebServiceUtil.op_Commit_Move_Item(bi);

                if (result.getResult() == true) {
                    boxitemList.remove(bi);

                    SelectList.remove(bi);
                } else {
                    //Toast.makeText(StockMoveActivity.this,result.getErrorInfo(),Toast.LENGTH_LONG).show();
                }

                count++;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");

            if (NoNewPlace == true) {
                pbScan.setVisibility(View.INVISIBLE);
                CommonUtil.ShowToast(StockMoveActivity.this, "还没有扫描新位置", R.mipmap.warning);
            } else {
                pbScan.setVisibility(View.INVISIBLE);
                boxitemAdapter = new AdapterMoveBoxItem(StockMoveActivity.this, boxitemList);
                mRecyclerView.setAdapter(boxitemAdapter);

                CommonUtil.ShowToast(StockMoveActivity.this, "移库完成", R.mipmap.smiley, Toast.LENGTH_SHORT);
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
//设置为屏幕
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("BoxItemList", (Serializable) boxitemList);

    }

}
