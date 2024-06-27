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

import java.util.ArrayList;
import java.util.List;

/***
 * @date 创建时间 2024/6/26 4:19 PM
 * @author 作者: liweifeng
 * @description 成品手工标签移库
 */
public class MoveManuProductPalletActivity extends BaseActivity implements View.OnClickListener {
    private Button btnAddTray;
    private Button btnScanArea;
    private Button btnWarehouseMove;
    private EditText inputEditText;
    private IstPlaceEntity thePlace;
    private String scanstring;
    private RelativeLayout switchLayout;
    private Switch stockSwitch;
    private boolean isOpenSuggestStock = true;
    //    private long boxId;
    private List<Integer> palletIDList;
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
        setContentView(R.layout.activity_stock_move_manu_pallet_layout);
//        tv = (TextView)findViewById(R.id.tv_stock_system_title);
//        mRecyclerView = (RecyclerView) findViewById(R.id.rv_move_box);
        btnAddTray = (Button) findViewById(R.id.btn_move_manu_pallet_add_tray_pallet);
        btnScanArea = (Button) findViewById(R.id.btn_move_manu_pallet_scan_new_place_pallet);
        btnWarehouseMove = (Button) findViewById(R.id.btn_move_manu_pallet_execute_pallet);
        inputEditText = findViewById(R.id.stock_move_manu_pallet_input_EditText_pallet);
        stockSwitch = findViewById(R.id.stock_move_manu_pallet_suggest_stock_Switch_pallet);
        switchLayout = findViewById(R.id.stock_move_manu_pallet_suggest_stock_Layout_pallet);
        itemInfoTextView = findViewById(R.id.product_pallet_move_manu_pallet_item_info_textview);

        palletIDList = new ArrayList<>();
//        boxitemAdapter = new AdapterMoveBoxItem(MoveManuProductPalletActivity.this, boxitemList);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
//        mRecyclerView.setAdapter(boxitemAdapter);

        btnAddTray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(MoveManuProductPalletActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
            }

        });

        btnScanArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasScanItem) {
                    new IntentIntegrator(MoveManuProductPalletActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
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
        //2024-05-28 john在移库前做一操作，判断是否是座椅车间，如果不是给出提示
        if ((UserSingleton.get().getUserInfo().getBu_ID() != 1) && (UserSingleton.get().getUserInfo().getBu_ID() != 81)){
            ToastUtil.showToastShort("当前车间非座椅，不可操作手工补打标签托盘移库！");

        }else{
            if (palletIDList != null && palletIDList.size() > 0) {
                if (UserSingleton.get().getHRID() > 0 && !TextUtils.isEmpty(UserSingleton.get().getHRName())) {

                    AsyncExeWarehouseMove task = new AsyncExeWarehouseMove();
                    task.execute();
                } else {
                    CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(MoveManuProductPalletActivity.this)
                            .setTitle("").setMessage("您当前程序账号有误，需重新登录！")
                            .setLeftText("确定");


                    builder.setOnViewClickListener(new OnDialogViewClickListener() {
                        @Override
                        public void onViewClick(Dialog dialog, View v, int tag) {
                            switch (tag) {
                                case CommAlertDialog.TAG_CLICK_LEFT:
                                    CommonUtil.doLogout(MoveManuProductPalletActivity.this);
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

                if (content.startsWith("OldPallet") && qrContent.length > 8) {
                    int palletId = Integer.parseInt(qrContent[1]);
                    if (palletIDList.contains(palletId)) {
                        ToastUtil.showToastShort("该手工补打标签托盘已在列表中，请勿重复扫描！");
                    } else {

                        if (palletIDList.size() > 2) {
                            ToastUtil.showToastShort("成品手工补打标签连续扫描移库一次不超过3托");
                        } else {
                            StringBuilder tempBoxInfoSBuilder = new StringBuilder();
//                            tempBoxInfoSBuilder.append("\n\n");
                            if (!TextUtils.isEmpty(itemInfoTextView.getText())) {
                                tempBoxInfoSBuilder.append(itemInfoTextView.getText());
                            } else {
                                //  第一行换行
                                tempBoxInfoSBuilder.append("物料信息\n");
                            }

                            tempBoxInfoSBuilder.append(String.format("Pallet_ID:%s,托盘序列号：%s,PS_ID：%s,箱子数量:%s\n", qrContent[1], qrContent[3] + "/" +qrContent[4], qrContent[6], qrContent[8]));
                            //                        itemInfoTextView.setText(String.format("托盘ID:%s,托盘序列号：%s,客户图号：%s,箱子数量:%s", qrContent[1], qrContent[3], qrContent[5], qrContent[7]));
                            itemInfoTextView.setText(tempBoxInfoSBuilder.toString());
                            inputEditText.setText("");
                            hasScanItem = true;
                            palletIDList.add(palletId);
                        }
                    }
                    // TODO: 2024/6/27 手工补打标签 托盘 暂不推荐库位
//                    GetProductMoveSuggestAreaAsyncTask task = new GetProductMoveSuggestAreaAsyncTask();
//                    task.execute(palletId);
                } else if (content.startsWith("/SUB_IST_ID/") || content.startsWith("/IST_ID/")) {
                    //仓库位置码
                    scanstring = content;
                    GetIstAsyncTask task = new GetIstAsyncTask();
                    task.execute();
                } else if(content.startsWith("OldNoPallet") && qrContent.length > 8){
                    ToastUtil.showToastShort("该标签是手工补打的非托盘，请到非托盘移库执行！");
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

    private void finishHandleMove() {
        itemInfoTextView.setText("");
        inputEditText.setText("");
        inputEditText.setHint("请继续扫描");
        palletIDList.clear();
    }

    private class GetIstAsyncTask extends AsyncTask<String, Void, Void> {
        IstPlaceEntity placeEntity;

        @Override
        protected Void doInBackground(String... params) {
            placeEntity = WebServiceUtil.op_Check_Commit_IST_Barcode(scanstring);
            thePlace = placeEntity;
            if (placeEntity.getResult()) {
            } else {
                Toast.makeText(MoveManuProductPalletActivity.this, placeEntity.getErrorInfo(), Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");
//            boxitemAdapter = new AdapterMoveBoxItem(MoveManuProductPalletActivity.this, boxitemList);
//            mRecyclerView.setAdapter(boxitemAdapter);

            handleMoveStockArea();
            //// TODO: 2024/5/8 这里不能直接清除数据，如果有错误，则boxidlist.clear，再看错误就index为0报错，因为异步
//            finishHandleMove();
//            boxitemList.clear();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    private class AsyncExeWarehouseMove extends AsyncTask<String, Void, List<WsResult>> {
        List<WsResult> wsResultList = new ArrayList<>();
        List<WsResult> wsErrorResultList = new ArrayList<>();

        //这种循环调用的方式只执行第一个
//        @Override
//        protected List<WsResult> doInBackground(String... params) {
//            System.out.println("========================begin op_Product_Pallet_Move boxIDList.size:=" + boxIDList.size() );
//
//            for (int i = 0; i < boxIDList.size(); i++) {
//            System.out.println("========================in loop  op_Product_Pallet_Move i:=" + i + " boxid = " + boxIDList .get(i)) ;
//                WsResult result = WebServiceUtil.moveProductPalletArea(thePlace.getIst_ID(), thePlace.getSub_Ist_ID(), boxIDList.get(i));
//                wsResultList.add(result);
//            }
//            return wsResultList;
//
//        }

        //参考零件的连续入库
        @Override
        protected List<WsResult> doInBackground(String... params) {
//            System.out.println("========================begin op_Product_Pallet_Move boxIDList.size:=" + boxIDList.size() );
//
//            for (int i = 0; i < boxIDList.size(); i++) {
//                System.out.println("========================in loop  op_Product_Pallet_Move i:=" + i + " boxid = " + boxIDList .get(i)) ;
//                WsResult result = WebServiceUtil.moveProductPalletArea(thePlace.getIst_ID(), thePlace.getSub_Ist_ID(), boxIDList.get(i));
//                wsResultList.add(result);
//            }
//            return wsResultList;


//            复制零件入库
//            List<BoxItemEntity> SelectList;
//            SelectList = new ArrayList<>();
//
//            for (int i = 0; i < boxItemEntityList.size(); i++) {
//                if (boxItemEntityList.get(i).getSelect()) {
//                    SelectList.add(boxItemEntityList.get(i));
//                }
//            }
//
//            int count = 0;
//            int selectedCount = SelectList.size();
//            while (count < selectedCount && SelectList.size() > 0) {
//                //todo  这里取的是0，验证多个是否成功
//                BoxItemEntity boxItemEntity = SelectList.get(0);
////                String sql = String.format("insert into Ist_SubIst_ManuLot (IST_ID,Sub_IST_ID,Item_ID,IV_ID,LotID,Company_ID,Bu_ID,ManuLotNo，SendToWarehouseTime) values (%d,%d,%d,%d,%d,%d,%d,%s,%s)",
//                String sql = String.format("insert into Ist_SubIst_ManuLot (IST_ID,Sub_IST_ID,Item_ID,IV_ID,LotID,Company_ID,Bu_ID,ManuLotNo) values (%d,%d,%d,%d,%d,%d,%d,%s)",
//                        boxItemEntity.getIst_ID(), boxItemEntity.getSub_Ist_ID(), boxItemEntity.getItem_ID(), boxItemEntity.getIV_ID(), boxItemEntity.getLotID(),
//                        UserSingleton.get().getUserInfo().getCompany_ID(), UserSingleton.get().getUserInfo().getBu_ID(),
////                        !TextUtils.isEmpty(boxItemEntity.getManuLotNo()) ? boxItemEntity.getManuLotNo() : boxItemEntity.getLotNo());
////                        boxItemEntity.getLotNo(),UnitFormatUtil.formatTimeToSecond(System.currentTimeMillis()));
//                        boxItemEntity.getLotNo());
//
////                ws_result = WebServiceUtil.op_Commit_DS_Item_Income_To_Warehouse(boxItemEntity,sql);
//                ws_result = WebServiceUtil.op_Commit_DS_Item_Income_To_Warehouse(boxItemEntity,scanCodeList.size() == selectedCount ? scanCodeList.get(0):"");
//                if (ws_result.getResult()) {
//                    //添加库位与manuLot的关联
////                    addIstSubIstManuLotRelation(boxItemEntity);
//                    boxItemEntityList.remove(boxItemEntity);
//                    SelectList.remove(boxItemEntity);
//                }
//
//                count++;
//            }


            int count = 0;
            List<Integer> tempPalletIDList = new ArrayList<>();
            for (int i = 0; i < palletIDList.size(); i++) {
                tempPalletIDList.add(palletIDList.get(i));
            }

            int selectedCount = tempPalletIDList.size();
            while (count < selectedCount && tempPalletIDList.size() > 0) {
                //// TODO: 2024/5/8 因为remove，故每次取第0个
                int movePalletID = tempPalletIDList.get(0);
//                ws_result = WebServiceUtil.op_Commit_DS_Item_Income_To_Warehouse(boxItemEntity,scanCodeList.size() == selectedCount ? scanCodeList.get(0):"");
//               WsResult result = WebServiceUtil.moveProductPalletArea(thePlace.getIst_ID(), thePlace.getSub_Ist_ID(), tempBoxIDList.get(count));
                WsResult result = WebServiceUtil.moveProductManuPalletArea(thePlace.getIst_ID(), thePlace.getSub_Ist_ID(), movePalletID);
                wsResultList.add(result);
                if (result.getResult()) {
//                    tempBoxIDList.remove(tempBoxIDList.get(count));
//                    tempBoxIDList.remove(moveBoxID);
                    //// TODO: 2024/5/8  这里会被理解为remove index而非object
//                    tempBoxIDList.remove(0);
                } else {
                    wsErrorResultList.add(result);
//                    tempBoxIDList.remove(0);
                }
                //// TODO: 2024/5/8 不论成功与否都要移除，否则每次执行的都是同一个
                tempPalletIDList.remove(0);
                count++;
            }

            return wsResultList;

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(List<WsResult> resultList) {
//            if (result != null) {
//                if (result.getResult()) {
//                    CommonUtil.ShowToast(MoveManuProductPalletActivity.this, "移库完成", R.mipmap.smiley, Toast.LENGTH_SHORT);
//                    boxitemList.clear();
//                } else {
//                    CommonUtil.ShowToast(MoveManuProductPalletActivity.this, "移库失败" + result.getErrorInfo(), R.mipmap.monster_mike, Toast.LENGTH_SHORT);
//                }
//            }

            boolean allCorrect = true;
            System.out.println("==================wsResultList.size() = " + wsResultList.size());
            System.out.println("==================wsErrorResultList.size() = " + wsErrorResultList.size());
            for (int i = 0; i < wsResultList.size(); i++) {
                System.out.println("==================wsResultList i= " + i + " result =" + wsResultList.get(i).getResult() +
                        " errorinfo = " + wsResultList.get(i).getErrorInfo());
                if (!wsResultList.get(i).getResult()) {
                    allCorrect = false;
                    break;
                }
            }
            if (allCorrect) {
                if (palletIDList != null && palletIDList.size() > 1) {

                    CommonUtil.ShowToast(MoveManuProductPalletActivity.this, "全部移库完成", R.mipmap.smiley, Toast.LENGTH_SHORT);
                } else {
                    CommonUtil.ShowToast(MoveManuProductPalletActivity.this, "移库完成", R.mipmap.smiley, Toast.LENGTH_SHORT);

                }
                finishHandleMove();
            } else {
                List<Integer> errorBoxIdList = new ArrayList<>();
                StringBuilder stringBuilder = new StringBuilder();
//                for (WsResult wsResult : wsResultList){
                for (int i = 0; i < wsResultList.size(); i++) {
                    if (!wsResultList.get(i).getResult()) {
                        errorBoxIdList.add(palletIDList.get(i));
                        stringBuilder.append(String.format("箱号为:%d,错误原因:%s", palletIDList.get(i), wsResultList.get(i).getErrorInfo()));
                        stringBuilder.append("\n");
                    }
                }

                CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(MoveManuProductPalletActivity.this)
                        .setTitle("此笔移库信息汇总").setMessage(String.format("此次移库共执行%d托，其中%d托移库失败，具体信息如下:%s",
                                palletIDList.size(), errorBoxIdList.size(), stringBuilder.toString()))
                        .setMiddleText("确定");


                builder.setOnViewClickListener((dialog, v, tag) -> {
                    switch (tag) {
                        case CommAlertDialog.TAG_CLICK_MIDDLE:
                            dialog.dismiss();
//                                        handleAllInCorrect();
                            finishHandleMove();
                            break;

                    }
                });
                builder.create().show();

            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }


    private class GetProductMoveSuggestAreaAsyncTask extends AsyncTask<Integer,Void,Void>{
        WsResult ws_result;

        @Override
        protected Void doInBackground(Integer... integers) {
            int boxID = integers[0];
            ws_result = WebServiceUtil.getProductSuggestAreaName(UserSingleton.get().getUserInfo().getBu_ID(),boxID);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (ws_result != null && ws_result.getResult()){
                ToastUtil.showToastShort("建议库位:" + ws_result.getErrorInfo());
            }else{
                ToastUtil.showToastShort("未能获取建议库位");

            }
        }
    }




}


