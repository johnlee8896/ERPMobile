package com.chinashb.www.mobileerp;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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
 * @date 创建时间 2024/2/1 2:05 PM
 * @author 作者: liweifeng
 * @description 成品非托盘移库
 */
public class MoveProductNotPalletActivity extends BaseActivity implements View.OnClickListener {
    private Button btnAddTray;
    private Button btnScanArea;
    private Button btnWarehouseMove;
    private EditText inputEditText;
    //    private List<Long> boxitemList;
    private IstPlaceEntity thePlace;
    private String scanstring;
    private RelativeLayout switchLayout;
    private Switch stockSwitch;
    //    private long boxId;
    private TextView itemInfoTextView;
    private boolean hasScanItem;
    private List<Integer> boxIDList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_move_not_pallet_layout);
        btnAddTray = (Button) findViewById(R.id.btn_move_add_tray_not_pallet);
        btnScanArea = (Button) findViewById(R.id.btn_move_scan_new_place_not_pallet);
        btnWarehouseMove = (Button) findViewById(R.id.btn_move_execute_not_pallet);
        inputEditText = findViewById(R.id.stock_move_input_EditText_not_pallet);
        stockSwitch = findViewById(R.id.stock_move_suggest_stock_Switch_not_pallet);
        switchLayout = findViewById(R.id.stock_move_suggest_stock_Layout_not_pallet);
        itemInfoTextView = findViewById(R.id.product_not_pallet_move_item_info_textview);

        boxIDList = new ArrayList<>();
        btnAddTray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(MoveProductNotPalletActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
            }

        });

        btnScanArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasScanItem ) {
                    new IntentIntegrator(MoveProductNotPalletActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();

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
                if (editable.toString().length() > 0) {
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
            ToastUtil.showToastShort("当前车间非座椅，不可操作非托盘移库！");

        }else{
            if (boxIDList != null && boxIDList.size() > 0) {
                if (UserSingleton.get().getHRID() > 0 && !TextUtils.isEmpty(UserSingleton.get().getHRName())) {

                    NotPalletWarehouseMoveTask task = new NotPalletWarehouseMoveTask();
                    task.execute();
                } else {
                    CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(MoveProductNotPalletActivity.this)
                            .setTitle("").setMessage("您当前程序账号有误，需重新登录！")
                            .setLeftText("确定");


                    builder.setOnViewClickListener(new OnDialogViewClickListener() {
                        @Override
                        public void onViewClick(Dialog dialog, View v, int tag) {
                            switch (tag) {
                                case CommAlertDialog.TAG_CLICK_LEFT:
                                    CommonUtil.doLogout(MoveProductNotPalletActivity.this);
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
        if (content.contains("/")) {
            String[] qrContent;
            qrContent = content.split("/");
            if (qrContent.length >= 2) {

                if (content.startsWith("Pallet") && qrContent.length == 8) {
//                    boxId = Long.parseLong(qrContent[1]);
//                    itemInfoTextView.setText(String.format("托盘ID:%s,托盘序列号：%s,客户图号：%s,箱子数量:%s", qrContent[1], qrContent[3], qrContent[5], qrContent[7]));
//                    inputEditText.setText("");
//                    hasScanItem = true;
//                    if (!boxitemList.contains(boxId)) {
//                        boxitemList.add(boxId);
//                    } else {
//                        ToastUtil.showToastShort("该托盘已扫过，请勿重复扫描");
//                    }
                    int boxId = Integer.parseInt(qrContent[1]);
                    if (boxIDList.contains(boxId)) {
                        ToastUtil.showToastShort("该托盘已在列表中，请勿重复扫描！");
                    } else {

                        if (boxIDList.size() > 2) {
                            ToastUtil.showToastShort("成品连续扫描移库一次不超过3托");
                        } else {
                            StringBuilder tempBoxInfoSBuilder = new StringBuilder();
//                            tempBoxInfoSBuilder.append("\n\n");
                            if (!TextUtils.isEmpty(itemInfoTextView.getText())) {
                                tempBoxInfoSBuilder.append(itemInfoTextView.getText());
                            } else {
                                //  第一行换行
                                tempBoxInfoSBuilder.append("物料信息\n");
                            }

                            tempBoxInfoSBuilder.append(String.format("托盘ID:%s,托盘序列号：%s,客户图号：%s,箱子数量:%s\n", qrContent[1], qrContent[3], qrContent[5], qrContent[7]));
                            //                        itemInfoTextView.setText(String.format("托盘ID:%s,托盘序列号：%s,客户图号：%s,箱子数量:%s", qrContent[1], qrContent[3], qrContent[5], qrContent[7]));
                            itemInfoTextView.setText(tempBoxInfoSBuilder.toString());
                            inputEditText.setText("");
                            hasScanItem = true;
                            boxIDList.add(boxId);
                        }
                    }
                    //添加移库时的建议库位
                    GetProductMoveNotPalletSuggestAreaAsyncTask task = new GetProductMoveNotPalletSuggestAreaAsyncTask();
                    task.execute(boxId);
                } else if (content.startsWith("/SUB_IST_ID/") || content.startsWith("/IST_ID/")) {
                    //仓库位置码
                    scanstring = content;
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
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == switchLayout) {
            stockSwitch.performClick();
        }
    }


    private class GetIstAsyncTask extends AsyncTask<String, Void, Void> {
        IstPlaceEntity placeEntity;

        @Override
        protected Void doInBackground(String... params) {
            placeEntity = WebServiceUtil.op_Check_Commit_IST_Barcode(scanstring);
            thePlace = placeEntity;
            if (placeEntity.getResult()) {
            } else {
                Toast.makeText(MoveProductNotPalletActivity.this, placeEntity.getErrorInfo(), Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Void result) {

            handleMoveStockArea();
            //// TODO: 2024/5/8 这里不能直接清除数据，如果有错误，则boxidlist.clear，再看错误就index为0报错，因为异步
//            finishHandleMove();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    private void finishHandleMove() {
        itemInfoTextView.setText("");
        inputEditText.setText("");
        inputEditText.setHint("请继续扫描");
        boxIDList.clear();
    }

    private class NotPalletWarehouseMoveTask extends AsyncTask<String, Void, List<WsResult>> {
        List<WsResult> wsResultList = new ArrayList<>();
        List<WsResult> wsErrorResultList = new ArrayList<>();
        @Override
        protected List<WsResult> doInBackground(String... params) {
//            WsResult result = WebServiceUtil.moveProductNotPalletArea(thePlace.getIst_ID(), thePlace.getSub_Ist_ID(), boxId);
//            return result;

            int count = 0;
            List<Integer> tempBoxIDList = new ArrayList<>();
            for (int i = 0; i < boxIDList.size(); i++) {
                tempBoxIDList.add(boxIDList.get(i));
            }

            int selectedCount = tempBoxIDList.size();
//            for (int i = 0; i < boxIDList.size(); i++) {
//                WsResult result = WebServiceUtil.moveProductNotPalletArea(thePlace.getIst_ID(), thePlace.getSub_Ist_ID(), boxIDList.get(i));
//                wsResultList.add(result);
//            }


            while (count < selectedCount && tempBoxIDList.size() > 0) {
                //// TODO: 2024/5/8 因为remove，故每次取第0个
                int moveBoxID = tempBoxIDList.get(0);
//                ws_result = WebServiceUtil.op_Commit_DS_Item_Income_To_Warehouse(boxItemEntity,scanCodeList.size() == selectedCount ? scanCodeList.get(0):"");
//               WsResult result = WebServiceUtil.moveProductPalletArea(thePlace.getIst_ID(), thePlace.getSub_Ist_ID(), tempBoxIDList.get(count));
                WsResult result = WebServiceUtil.moveProductNotPalletArea(thePlace.getIst_ID(), thePlace.getSub_Ist_ID(), moveBoxID);
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
                tempBoxIDList.remove(0);
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
//                    CommonUtil.ShowToast(MoveProductNotPalletActivity.this, "移库完成", R.mipmap.smiley, Toast.LENGTH_SHORT);
//                    boxitemList.clear();
//                } else {
//                    CommonUtil.ShowToast(MoveProductNotPalletActivity.this, "移库失败" + result.getErrorInfo(), R.mipmap.monster_mike, Toast.LENGTH_SHORT);
//                }
//            }

            boolean allCorrect = true;

            for (int i = 0; i < wsResultList.size(); i++) {
                if (!wsResultList.get(i).getResult()) {
                    allCorrect = false;
                    break;
                }
            }
            if (allCorrect) {
//                CommonUtil.ShowToast(MoveProductNotPalletActivity.this, "移库完成", R.mipmap.smiley, Toast.LENGTH_SHORT);
                if (boxIDList != null && boxIDList.size() > 1) {

                    CommonUtil.ShowToast(MoveProductNotPalletActivity.this, "全部移库完成", R.mipmap.smiley, Toast.LENGTH_SHORT);
                } else {
                    CommonUtil.ShowToast(MoveProductNotPalletActivity.this, "移库完成", R.mipmap.smiley, Toast.LENGTH_SHORT);

                }
                finishHandleMove();
            } else {
                List<Integer> errorBoxIdList = new ArrayList<>();
                StringBuilder stringBuilder = new StringBuilder();
//                for (WsResult wsResult : wsResultList){
                for (int i = 0; i < wsResultList.size(); i++) {
                    if (!wsResultList.get(i).getResult()) {
                        errorBoxIdList.add(boxIDList.get(i));
                        stringBuilder.append(String.format("箱号为:%d,错误原因:%s", boxIDList.get(i), wsResultList.get(i).getErrorInfo()));
                        stringBuilder.append("\n");
                    }
                }

                CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(MoveProductNotPalletActivity.this)
                        .setTitle("此笔移库信息汇总").setMessage(String.format("此次非托盘移库共执行%d托，其中%d托移库失败，具体信息如下:%s",
                                boxIDList.size(), errorBoxIdList.size(), stringBuilder.toString()))
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

    private class GetProductMoveNotPalletSuggestAreaAsyncTask extends AsyncTask<Integer,Void,Void>{
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
