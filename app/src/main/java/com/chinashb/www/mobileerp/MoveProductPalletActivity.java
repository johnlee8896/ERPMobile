package com.chinashb.www.mobileerp;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.basicobject.IstPlaceEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CommAlertDialog;
import com.chinashb.www.mobileerp.widget.OnDialogViewClickListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/***
 * @date 创建时间 2022/7/8 8:35 AM
 * @author 作者: liweifeng
 * @description 成品托盘移库
 */
public class MoveProductPalletActivity extends BaseActivity implements View.OnClickListener {
    private Button btnAddTray;
    private Button btnScanArea;
    private Button btnWarehouseMove;
    private EditText inputEditText;
//    private RecyclerView mRecyclerView;
//    private ProgressBar pbScan;
//    private AdapterMoveBoxItem boxitemAdapter;
    private List<BoxItemEntity> boxitemList;
    private IstPlaceEntity thePlace;
    private String scanstring;
    private RelativeLayout switchLayout;
    private Switch stockSwitch;
    private boolean isOpenSuggestStock = true;
    private int boxId;
    private TextView itemInfoTextView;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                ToastUtil.showToastLong("您当前公司与来料入库公司不符合，请确认来料是否入到该公司！");
            } else if (msg.what == 1) {
//                ToastUtil.showToastLong("建议仓库存放:" + boxItemEntity.getIstName());
                Bundle bundle = msg.getData();
                if (bundle != null) {
                    ToastUtil.showToastLong("建议仓库存放:" + bundle.getString("suggest_ist"));
                }
            }
        }
    };
    private boolean hasScanItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_move_pallet_layout);
//        tv = (TextView)findViewById(R.id.tv_stock_system_title);
//        mRecyclerView = (RecyclerView) findViewById(R.id.rv_move_box);
        btnAddTray = (Button) findViewById(R.id.btn_move_add_tray_pallet);
        btnScanArea = (Button) findViewById(R.id.btn_move_scan_new_place_pallet);
        btnWarehouseMove = (Button) findViewById(R.id.btn_move_execute_pallet);
        inputEditText = findViewById(R.id.stock_move_input_EditText_pallet);
        stockSwitch = findViewById(R.id.stock_move_suggest_stock_Switch_pallet);
        switchLayout = findViewById(R.id.stock_move_suggest_stock_Layout_pallet);
        itemInfoTextView = findViewById(R.id.product_pallet_move_item_info_textview);

        boxitemList = new ArrayList<>();
//        boxitemAdapter = new AdapterMoveBoxItem(MoveProductPalletActivity.this, boxitemList);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
//        mRecyclerView.setAdapter(boxitemAdapter);

        btnAddTray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (boxitemList.size() < 10) {
                    new IntentIntegrator(MoveProductPalletActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
                } else {
                    Toast.makeText(MoveProductPalletActivity.this, "移动清单不超过10个 ", Toast.LENGTH_LONG).show();
                }
            }

        });

        btnScanArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (boxitemList.size() > 0) {
                    int selectedcount = 0;
                    for (int i = 0; i < boxitemList.size(); i++) {
                        if (boxitemList.get(i).getSelect()) {
                            selectedcount++;
                        }
                    }
                    if (selectedcount > 0) {
                        new IntentIntegrator(MoveProductPalletActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
                    }

                }

            }

        });

        btnWarehouseMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleMoveStockArea();
            }
        });

        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
//                if (editable.toString().endsWith("\n")){
                if (editable.toString().length() > 0) {
//                    ToastUtil.showToastLong("扫描结果:" + editable.toString());
                    System.out.println("========================扫描结果:" + editable.toString());
                    parseScanResult(editable.toString());
                }
            }
        });
        switchLayout.setOnClickListener(this);
        stockSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = stockSwitch.isChecked();
                if (isChecked) {
                    ToastUtil.showToastShort("您已打开建议仓库！");
                } else {
                    ToastUtil.showToastShort("您已关闭建议仓库！");
                }
                isOpenSuggestStock = isChecked;
            }
        });

    }

    @Override
    protected void onResume() {
//设置为屏幕
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        super.onResume();
    }

    private void handleMoveStockArea() {
        if (boxitemList.size() > 0) {
//            int selectedcount = 0;
//            for (int i = 0; i < boxitemList.size(); i++) {
//                if (boxitemList.get(i).getSelect()) {
//                    selectedcount++;
//                }
//            }
//            if (selectedcount > 0) {
            if (boxId > 0) {
                if (UserSingleton.get().getHRID() > 0 && !TextUtils.isEmpty(UserSingleton.get().getHRName())) {

                    AsyncExeWarehouseMove task = new AsyncExeWarehouseMove();
                    task.execute();
                } else {
                    CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(MoveProductPalletActivity.this)
                            .setTitle("").setMessage("您当前程序账号有误，需重新登录！")
                            .setLeftText("确定");


                    builder.setOnViewClickListener(new OnDialogViewClickListener() {
                        @Override
                        public void onViewClick(Dialog dialog, View v, int tag) {
                            switch (tag) {
                                case CommAlertDialog.TAG_CLICK_LEFT:
                                    CommonUtil.doLogout(MoveProductPalletActivity.this);
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    });
                    builder.create().show();
                }
            }
        }
    }

    private void parseScanResult(String content) {
        System.out.println("===== content = " + content);
//        Toast.makeText(this, "Scanned: " + result, Toast.LENGTH_LONG).show();
//        String X = result.getContents();
        if (content.contains("/")) {
            String[] qrContent;
            qrContent = content.split("/");
            if (qrContent.length >= 2) {

                if (content.startsWith("Pallet") && qrContent.length == 8) {
                    boxId = Integer.parseInt(qrContent[1]);
//                    if (boxIDList.contains(boxId)){
//                        ToastUtil.showToastShort("该托盘已入库，请勿重复入库！");
//                    }else{
                    itemInfoTextView.setText(String.format("托盘ID:%s,托盘序列号：%s,客户图号：%s,箱子数量:%s",qrContent[1],qrContent[3],qrContent[5],qrContent[7]));
                    inputEditText.setText("");
                    hasScanItem = true;
//                    }
                } else if (content.startsWith("/SUB_IST_ID/") || content.startsWith("/IST_ID/")) {
                    //仓库位置码
//                    scanContent = content;
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

    @Override
    public void onClick(View view) {
        if (view == switchLayout) {
            stockSwitch.performClick();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("BoxItemList", (Serializable) boxitemList);

    }


    private class GetIstAsyncTask extends AsyncTask<String, Void, Void> {
        IstPlaceEntity placeEntity;
        @Override
        protected Void doInBackground(String... params) {
             placeEntity = WebServiceUtil.op_Check_Commit_IST_Barcode(scanstring);
             thePlace = placeEntity;
            if (placeEntity.getResult()) {
//                thePlace = placeEntity;
                if (placeEntity.getResult()) {
                    for (int i = 0; i < boxitemList.size(); i++) {
                        if (boxitemList.get(i).getSelect()) {
                            boxitemList.get(i).setIstName(placeEntity.getIstName());
                            boxitemList.get(i).setIst_ID(placeEntity.getIst_ID());
                            boxitemList.get(i).setSub_Ist_ID(placeEntity.getSub_Ist_ID());
                        }
                    }
                }
            } else {
                Toast.makeText(MoveProductPalletActivity.this, placeEntity.getErrorInfo(), Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");
//            boxitemAdapter = new AdapterMoveBoxItem(MoveProductPalletActivity.this, boxitemList);
//            mRecyclerView.setAdapter(boxitemAdapter);
            inputEditText.setText("");
            inputEditText.setHint("请继续扫描");
            handleMoveStockArea();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    private class AsyncExeWarehouseMove extends AsyncTask<String, Void, WsResult> {

        @Override
        protected WsResult doInBackground(String... params) {

//            List<BoxItemEntity> SelectList;
//            SelectList = new ArrayList<>();
//
//            for (int i = 0; i < boxitemList.size(); i++) {
//                if (boxitemList.get(i).getIst_ID() == 0) {
//                    NoNewPlace = true;
//
//                    return null;
//                }
//
//                if (boxitemList.get(i).getSelect() ) {
//                    SelectList.add(boxitemList.get(i));
//                }
//
//
//            }

//            int count = 0;
//
//            int selectedcount = SelectList.size();
//            while (count < selectedcount && SelectList.size() > 0) {
//                BoxItemEntity bi = SelectList.get(0);
//
//                WsResult result = WebServiceUtil.op_Commit_Move_Item(bi);
//
//                if (result.getResult() ) {
//                    boxitemList.remove(bi);
//
//                    SelectList.remove(bi);
//                } else {
//                    //Toast.makeText(StockMoveActivity.this,result.getErrorInfo(),Toast.LENGTH_LONG).show();
//                }
//
//                count++;
//            }
//            BoxItemEntity bi = boxitemList.get(0);
            WsResult result = WebServiceUtil.moveProductPalletArea(thePlace.getIst_ID(),thePlace.getSub_Ist_ID(),boxId);
            return result;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(WsResult result) {
            //tv.setText(fahren + "∞ F");

//            if (NoNewPlace) {
//                pbScan.setVisibility(View.INVISIBLE);
//                CommonUtil.ShowToast(StockMoveActivity.this, "还没有扫描新位置", R.mipmap.warning);
//            } else {
//                pbScan.setVisibility(View.INVISIBLE);
//                boxitemAdapter = new AdapterMoveBoxItem(StockMoveActivity.this, boxitemList);
//                mRecyclerView.setAdapter(boxitemAdapter);
//
//                CommonUtil.ShowToast(StockMoveActivity.this, "移库完成", R.mipmap.smiley, Toast.LENGTH_SHORT);
//            }
            if (result != null && result.getResult()){
                CommonUtil.ShowToast(MoveProductPalletActivity.this, "移库完成", R.mipmap.smiley, Toast.LENGTH_SHORT);
            }else{
                CommonUtil.ShowToast(MoveProductPalletActivity.this, "移库失败", R.mipmap.monster_mike, Toast.LENGTH_SHORT);
            }
            

            boxitemList.clear();
//            boxitemAdapter = new AdapterMoveBoxItem(MoveProductPalletActivity.this, boxitemList);
//            mRecyclerView.setAdapter(boxitemAdapter);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

}

