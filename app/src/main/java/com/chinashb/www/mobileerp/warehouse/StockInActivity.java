package com.chinashb.www.mobileerp.warehouse;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.InBoxItemAdapter;
import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.basicobject.IstPlaceEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.ScanInputDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StockInActivity extends AppCompatActivity implements View.OnClickListener {


    private Button addTrayScannerButton;
    private Button addTrayPhotoButton;
    private Button scanAreaButton;
    //    private Button stockInButton;
//    private Button btnStartMoving;
    private Button warehouseInButton;

    private RecyclerView mRecyclerView;
    private EditText inputEditText;

    private InBoxItemAdapter boxItemAdapter;
    private List<BoxItemEntity> boxitemList = new ArrayList<>();
    private IstPlaceEntity thePlace;
    private String scanContent;

    private ScanInputDialog inputDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_in);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_box_item);
        addTrayScannerButton = (Button) findViewById(R.id.btn_add_tray_scanner);
        addTrayPhotoButton = findViewById(R.id.btn_add_tray_photo);
        scanAreaButton = (Button) findViewById(R.id.btn_scan_area);
        warehouseInButton = (Button) findViewById(R.id.btn_exe_warehouse_in);
        inputEditText = (EditText) findViewById(R.id.input_EditeText);

        setHomeButton();

        if (savedInstanceState != null) {
            boxitemList = (List<BoxItemEntity>) savedInstanceState.getSerializable("BoxItemList");
        }

        boxItemAdapter = new InBoxItemAdapter(StockInActivity.this, boxitemList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        mRecyclerView.setAdapter(boxItemAdapter);
        setViewsListener();

    }

    private void setViewsListener() {
        addTrayScannerButton.setOnClickListener(this);
        addTrayPhotoButton.setOnClickListener(this);
        scanAreaButton.setOnClickListener(this);
        warehouseInButton.setOnClickListener(this);
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
            if (!TextUtils.isEmpty(result.getContents())) {
                parseScanResult(result.getContents());
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void parseScanResult(String content) {
        if (TextUtils.isEmpty(content)){
            return;
        }
//        Toast.makeText(this, "Scanned: " + content, Toast.LENGTH_LONG).show();
        System.out.println("============ scan content = " + content);
        // VB/MT/579807/S/3506/IV/38574/P/T17-1130-1 A0/D/20190619/L/19061903/N/49/Q/114
//        String content = result.getContents();
        if (content.contains("\n")){
            content = content.replace("\n","");
        }
        if (content.contains("/")) {
            String[] qrContent;
            qrContent = content.split("/");
            if (qrContent.length >= 2) {
                String qrTitle = qrContent[0];
                if (!qrTitle.equals("")) {
                    if (qrTitle.equals("VE") || qrTitle.equals("VF") || qrTitle.equals("VG") || qrTitle.equals("V9") || qrTitle.equals("VA") || qrTitle.equals("VB") || qrTitle.equals("VC")) {
                        //物品条码
                        scanContent = content;
                        GetBoxAsyncTask task = new GetBoxAsyncTask();
                        task.execute();
                    }
                }

                if (content.startsWith("/SUB_IST_ID/") || content.startsWith("/IST_ID/")) {
                    //仓库位置码
                    scanContent = content;
                    GetIstAsyncTask task = new GetIstAsyncTask();
                    task.execute();
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view == addTrayScannerButton) {
            //scanContent= "VG/404731";
            //StockInActivity.GetBoxAsyncTask task = new StockInActivity.GetBoxAsyncTask();
            //task.execute();
//            CommAlertDialog.with(StockInActivity.this).setTitle("请点击扫描枪")
//                    .setMessage("请确保蓝牙已连接，若未连接请打开设置--蓝牙--连接QScanner")
//                    .setMiddleText("确定")
//                    .setCancelAble(false).setTouchOutsideCancel(false)
//                    .setClickButtonDismiss(true)
//                    .create().show();
            //todo 这时用dialog的edittext，会直接走ondestroy方法
//            if (inputDialog == null) {
//                inputDialog = new ScanInputDialog(StockInActivity.this);
//            }
//            if (!inputDialog.isShowing()) {
//                inputDialog.show();
//            }
//            inputDialog.setOnViewClickListener(new OnViewClickListener() {
//                @Override
//                public <T> void onClickAction(View v, String tag, T t) {
////                    parseScanResult((String) t);
//                    ToastUtil.showToastLong("Scanned: " + ((String) t));
//                    inputDialog.dismiss();
//                }
//            });
//            inputDialog.setOnEditTextInputCompleteListener(new OnEditTextInputCompleteListener() {
//                @Override
//                public void onEditTextInputComplete(String content) {
//                    parseScanResult(content);
//                }
//            });


//            new IntentIntegrator(StockInActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();

            //todo通过扫描枪 当控制editText显示隐藏时，这个方法会随着扫描反复调用 ，故需要做些处理
            if (inputEditText.getVisibility() == View.GONE) {

                inputEditText.setVisibility(View.VISIBLE);
                addTrayScannerButton.setText("扫描增加托盘");
            } else {
                addTrayScannerButton.setText("开始扫描");
                parseScanResult(inputEditText.getText().toString());

            }
        } else if (view == addTrayPhotoButton){
            new IntentIntegrator( StockInActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
        }else if (view == scanAreaButton) {
            if (boxitemList.size() > 0) {
                int selectedcount = 0;
                for (int i = 0; i < boxitemList.size(); i++) {
                    if (boxitemList.get(i).getSelect() == true) {
                        selectedcount++;
                    }
                }
                if (selectedcount > 0) {
                    new IntentIntegrator(StockInActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
                }

            }
        } else if (view == warehouseInButton) {
            if (boxitemList.size() > 0) {
                int selectedcount = 0;
                for (int i = 0; i < boxitemList.size(); i++) {
                    if (boxitemList.get(i).getSelect() == true) {
                        if (boxitemList.get(i).getIst_ID() == 0) {
                            CommonUtil.ShowToast(StockInActivity.this, "还没有扫描库位", R.mipmap.warning, Toast.LENGTH_SHORT);

                            return;
                        }
                        selectedcount++;
                    }

                }
                if (selectedcount > 0) {
                    StockInActivity.AsyncExeWarehouseIn task = new StockInActivity.AsyncExeWarehouseIn();
                    task.execute();

                }

            }
        }
    }

    private class GetBoxAsyncTask extends AsyncTask<String, Void, Void> {
        BoxItemEntity scanresult;

        @Override
        protected Void doInBackground(String... params) {

            BoxItemEntity bi = WebServiceUtil.op_Check_Commit_DS_Item_Income_Barcode(scanContent);
            scanresult = bi;
            if (bi.getResult()) {
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
                    Toast.makeText(StockInActivity.this, scanresult.getErrorInfo(), Toast.LENGTH_LONG).show();
                }
            }

            boxItemAdapter = new InBoxItemAdapter(StockInActivity.this, boxitemList);
            mRecyclerView.setAdapter(boxItemAdapter);
//            if (inputDialog != null && inputDialog.isShowing()) {
//                inputDialog.dismiss();
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


    private class GetIstAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            IstPlaceEntity bi = WebServiceUtil.op_Check_Commit_IST_Barcode(scanContent);
            if (bi.getResult()) {
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
                Toast.makeText(StockInActivity.this, bi.getErrorInfo(), Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");

            mRecyclerView.setAdapter(boxItemAdapter);
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
        WsResult ws_result;

        @Override
        protected Void doInBackground(String... params) {

            List<BoxItemEntity> SelectList;
            SelectList = new ArrayList<>();

            for (int i = 0; i < boxitemList.size(); i++) {
                if (boxitemList.get(i).getSelect() == true) {
                    SelectList.add(boxitemList.get(i));
                }
            }

            int count = 0;
            int selectedcount = SelectList.size();

            while (count < selectedcount && SelectList.size() > 0) {
                BoxItemEntity boxItemEntity = SelectList.get(0);
                ws_result = WebServiceUtil.op_Commit_DS_Item(boxItemEntity);


                if (ws_result.getResult() == true) {
                    boxitemList.remove(boxItemEntity);
                    SelectList.remove(boxItemEntity);
                }

                count++;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");

            if (ws_result != null) {
                if (!ws_result.getResult()) {
                    //Toast.makeText(StockInActivity.this,ws_result.getErrorInfo(),Toast.LENGTH_LONG).show();
                    CommonUtil.ShowToast(StockInActivity.this, ws_result.getErrorInfo(), R.mipmap.warning);

                } else {
                    //Toast.makeText(StockInActivity.this,"入库完成",Toast.LENGTH_LONG).show();
                    CommonUtil.ShowToast(StockInActivity.this, "入库完成", R.mipmap.smiley);
                }

            }

            boxItemAdapter = new InBoxItemAdapter(StockInActivity.this, boxitemList);
            mRecyclerView.setAdapter(boxItemAdapter);
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


    BroadcastReceiver mFoundReceiver = new BroadcastReceiver() {

        @Override public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //找到设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 添加进一个设备列表，进行显示。
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
//                    Log.v(TAG, "find device:" + device.getName() + device.getAddress());
                }
            }
            //搜索完成
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//                cancelDiscovery();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("===========onDestroy");
        if (inputDialog != null && inputDialog.isShowing()) {
            inputDialog.dismiss();
        }
    }

}
