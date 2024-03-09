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
    private List<Long> boxitemList;
    private IstPlaceEntity thePlace;
    private String scanstring;
    private RelativeLayout switchLayout;
    private Switch stockSwitch;
    private long boxId;
    private TextView itemInfoTextView;
    private boolean hasScanItem;

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

        boxitemList = new ArrayList<>();

        btnAddTray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (boxitemList.size() < 10) {
                    new IntentIntegrator(MoveProductNotPalletActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
                } else {
                    Toast.makeText(MoveProductNotPalletActivity.this, "移动清单不超过10个 ", Toast.LENGTH_LONG).show();
                }
            }

        });

        btnScanArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasScanItem && boxitemList.size() > 0) {
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
        if (boxitemList.size() > 0) {
            if (boxId > 0) {
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
                    boxId = Long.parseLong(qrContent[1]);
                    itemInfoTextView.setText(String.format("托盘ID:%s,托盘序列号：%s,客户图号：%s,箱子数量:%s", qrContent[1], qrContent[3], qrContent[5], qrContent[7]));
                    inputEditText.setText("");
                    hasScanItem = true;
                    if (!boxitemList.contains(boxId)) {
                        boxitemList.add(boxId);
                    } else {
                        ToastUtil.showToastShort("该托盘已扫过，请勿重复扫描");
                    }
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
            itemInfoTextView.setText("");
            inputEditText.setText("");
            inputEditText.setHint("请继续扫描");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    private class NotPalletWarehouseMoveTask extends AsyncTask<String, Void, WsResult> {

        @Override
        protected WsResult doInBackground(String... params) {
            WsResult result = WebServiceUtil.moveProductNotPalletArea(thePlace.getIst_ID(), thePlace.getSub_Ist_ID(), boxId);
            return result;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(WsResult result) {
            if (result != null) {
                if (result.getResult()) {
                    CommonUtil.ShowToast(MoveProductNotPalletActivity.this, "移库完成", R.mipmap.smiley, Toast.LENGTH_SHORT);
                    boxitemList.clear();
                } else {
                    CommonUtil.ShowToast(MoveProductNotPalletActivity.this, "移库失败" + result.getErrorInfo(), R.mipmap.monster_mike, Toast.LENGTH_SHORT);
                }
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

}
