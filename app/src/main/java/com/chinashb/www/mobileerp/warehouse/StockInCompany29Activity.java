package com.chinashb.www.mobileerp.warehouse;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.SelectPurchaseOrderListActivity;
import com.chinashb.www.mobileerp.adapter.InBoxItemAdapter;
import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.basicobject.IstPlaceEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.StockInPurchaseOrderBean;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CommAlertDialog;
import com.chinashb.www.mobileerp.widget.OnDialogViewClickListener;
import com.chinashb.www.mobileerp.widget.ScanInputDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

/***
 * @date 创建时间 1/9/26 2:26 PM
 * @author 作者: liweifeng
 * @description 给胜华波零部件三个结算中心作入库处理，因要选采购订单
 */
public class StockInCompany29Activity extends BaseActivity implements View.OnClickListener {
    BroadcastReceiver mFoundReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
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
    private Button addTrayPhotoButton;
    private Button scanAreaButton;
    private Button warehouseInButton;
    private RecyclerView mRecyclerView;
    private EditText inputEditText;
    private InBoxItemAdapter boxItemAdapter;
    private List<BoxItemEntity> boxItemEntityList = new ArrayList<>();
    private List<StockInPurchaseOrderBean> purchaseEntityList = new ArrayList<>();
    private IstPlaceEntity thePlace;
    private ArrayList<String> scanCodeList = new ArrayList<>();
    private String scanContent;
    private String scanCode = "";
    private ScanInputDialog inputDialog;
    private RelativeLayout switchLayout;
    private Switch stockSwitch;
    private boolean isOpenSuggestStock = true;
    private BoxItemEntity currentBoxItemEntity;
    //// TODO: 2020/1/9 以后要优化，暂时先解决Can't toast on a thread that has not called Looper.prepare() 的问题
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_stock_in_company29_layout);

        mRecyclerView = (RecyclerView) findViewById(R.id.company29_rv_box_item);
        addTrayPhotoButton = findViewById(R.id.company29_btn_add_tray_photo);
        scanAreaButton = (Button) findViewById(R.id.company29_btn_scan_area);
        warehouseInButton = (Button) findViewById(R.id.company29_btn_exe_warehouse_in);
        inputEditText = (EditText) findViewById(R.id.company29_input_EditText);

        stockSwitch = findViewById(R.id.company29_setting_open_suggest_stock_Switch);
        switchLayout = findViewById(R.id.company29_setting_open_suggest_stock_Layout);


        boxItemAdapter = new InBoxItemAdapter(StockInCompany29Activity.this, boxItemEntityList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        mRecyclerView.setAdapter(boxItemAdapter);
        setViewsListener();
        if (UserSingleton.get().getUserInfo().getCompany_ID() != 29) {
            ToastUtil.showToastShort("该界面目前只适用于零部件公司的扫描入库！");
            finish();
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

    private void setViewsListener() {
        addTrayPhotoButton.setOnClickListener(this);
        scanAreaButton.setOnClickListener(this);
        warehouseInButton.setOnClickListener(this);
        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                if (editable.toString().length() > 7 ) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IntentConstant.Intent_Request_Stock_in_To_Purchase_Order_Activity){
//            StockInPurchaseOrderBean bean = getIntent().getParcelableExtra(IntentConstant.Intent_Extra_MY_Purchase_Order_bean);
            StockInPurchaseOrderBean bean = data.getParcelableExtra(IntentConstant.Intent_Extra_MY_Purchase_Order_bean);
            if (bean != null){
                handleCurrentBoxStatus(currentBoxItemEntity);
                purchaseEntityList.add((bean));
            }else{
                ToastUtil.showToastShort("未选中采购订单");
            }
        }else{
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (!TextUtils.isEmpty(result.getContents())) {
                    parseScanResult(result.getContents());
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }



    }

    @Override
    public void onClick(View view) {
       if (view == addTrayPhotoButton) {
            new IntentIntegrator(StockInCompany29Activity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
        } else if (view == scanAreaButton) {
            if (boxItemEntityList.size() > 0) {
                int selectedcount = 0;
                for (int i = 0; i < boxItemEntityList.size(); i++) {
                    if (boxItemEntityList.get(i).getSelect()) {
                        selectedcount++;
                    }
                }
                if (selectedcount > 0) {
                    new IntentIntegrator(StockInCompany29Activity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
                    inputEditText.setText("");
                } else {
                    ToastUtil.showToastShort("请选择条目！");
                }

            } else {
                ToastUtil.showToastShort("没有物品条码或仓库位置码没有成功，请重新扫描！");
            }
        } else if (view == warehouseInButton) {
            handleIntoWareHouse();
        } else if (view == switchLayout) {
            stockSwitch.performClick();
        }
    }

    private void parseScanResult(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        if (content.contains("\n")) {
            content = content.replace("\n", "");
        }
        if (content.contains("/") || content.contains("／")) {
            if (content.contains("／")) {
                content = content.replace("／", "/");
            }

            String[] qrContent;
            qrContent = content.split("/");
            if (qrContent.length >= 2) {
                String qrTitle = qrContent[0];
                if (!qrTitle.equals("")) {
                    if (qrTitle.equals("VE") || qrTitle.equals("VF") || qrTitle.equals("VG") || qrTitle.equals("V9") || qrTitle.equals("VA") || qrTitle.equals("VB") || qrTitle.equals("VC")) {
                        //物品条码
                        scanContent = content;
                        scanCodeList.add(content);
                        scanCode = content;
                        GetBoxAsyncTask task = new GetBoxAsyncTask();
                        task.execute();
                    }
                }

                if (content.startsWith("/SUB_IST_ID/") || content.startsWith("/IST_ID/") ||
                        content.startsWith("/SUB——IST——ID/") || content.startsWith("/IST——ID/")) {
                    if (content.startsWith("/SUB——IST——ID/")) {
                        content = content.replace("/SUB——IST——ID/", "/SUB_IST_ID/");
                    }

                    if (content.startsWith("/IST——ID/")) {
                        content = content.replace("/IST——ID/", "/IST_ID/");
                    }
                    //仓库位置码
                    scanContent = content;
                    GetIstAsyncTask task = new GetIstAsyncTask();
                    task.execute();
                }
            }
        }
    }

    private void handleIntoWareHouse() {
//        防重复，金蝶慢，所以这样处理
        warehouseInButton.setEnabled(false);
        if (boxItemEntityList.size() > 0) {
            int selectedcount = 0;
            for (int i = 0; i < boxItemEntityList.size(); i++) {
                if (boxItemEntityList.get(i).getSelect()) {
                    if (boxItemEntityList.get(i).getIst_ID() == 0) {
                        ToastUtil.showToastLong("还没有扫描库位");
                        return;
                    }
                    selectedcount++;
                }

            }
            if (selectedcount > 0) {
                if (UserSingleton.get().getHRID() > 0 && !TextUtils.isEmpty(UserSingleton.get().getHRName())){
                    AsyncExeWarehouseIn task = new AsyncExeWarehouseIn();
                    task.execute();
                }else{
                    CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(StockInCompany29Activity.this)
                            .setTitle("").setMessage("您当前程序账号有误，需重新登录！")
                            .setLeftText("确定");


                    builder.setOnViewClickListener(new OnDialogViewClickListener() {
                        @Override
                        public void onViewClick(Dialog dialog, View v, int tag) {
                            switch (tag) {
                                case CommAlertDialog.TAG_CLICK_LEFT:
                                    CommonUtil.doLogout(StockInCompany29Activity.this);
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    });
                    builder.create().show();


//                    CommonUtil.doLogout(StockInCompany29Activity.this);
//                    EventBus.getDefault().post(new LogoutEvent());
                }
            }

        } else {
            //// TODO: 2019/7/10  这里应控件按钮的可用性
            ToastUtil.showToastShort("没有物品条码或仓库位置码没有成功，请重新扫描！");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("===========onDestroy");
        if (inputDialog != null && inputDialog.isShowing()) {
            inputDialog.dismiss();
        }
    }

    private class GetIsAlarmItemAsyncTask extends AsyncTask<String, Void, Void> {
        WsResult result;

        @Override
        protected Void doInBackground(String... ids) {
            if (ids.length > 0){
                int itemID = Integer.parseInt(ids[0]);
                result = WebServiceUtil.getIsAlarmItem(UserSingleton.get().getUserInfo().getBu_ID(),itemID);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (result != null) {
                if (result.getResult()) {
                    String  info = result.getErrorInfo();
                    if (info.contains("紧急物料")) {
                        CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(StockInCompany29Activity.this)
                                .setTitle("").setMessage("此物料为紧急物料！")
                                .setLeftText("确定");


                        builder.setOnViewClickListener(new OnDialogViewClickListener() {
                            @Override
                            public void onViewClick(Dialog dialog, View v, int tag) {
                                switch (tag) {
                                    case CommAlertDialog.TAG_CLICK_LEFT:
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        });
                        builder.create().show();
                    } else {

                    }

                }
            }

        }
    }

    private class GetBoxAsyncTask extends AsyncTask<String, Void, Void> {
        BoxItemEntity scanBoxItemEntity;
        boolean isJump = false;

        @Override
        protected Void doInBackground(String... params) {

            BoxItemEntity boxItemEntity = null;

            if(UserSingleton.get().getUserInfo().getBu_ID() ==149 || UserSingleton.get().getUserInfo().getBu_ID() == 155){

                boxItemEntity = WebServiceUtil.op_Check_Commit_DS_Item_Income_Barcode_ML(scanContent);
            }else {
                boxItemEntity = WebServiceUtil.op_Check_Commit_DS_Item_Income_Barcode(scanContent);
            }

            scanBoxItemEntity = boxItemEntity;
            if (boxItemEntity.getResult()) {

                if (boxItemEntity.getCompany_ID() != 0 && boxItemEntity.getCompany_ID() != UserSingleton.get().getUserInfo().getCompany_ID()) {
                    Message message = new Message();
                    message.what = 0;
                    handler.sendMessage(message);
                    return null;
                }
//                }

                if (!is_box_existed(boxItemEntity)) {
//                    boxItemEntity.getCompany_ID() == 29 这里获取的bu是有值的，但company是0

//                    ----------这部分代码先注掉
//                    if((boxItemEntity.getBu_ID() == 154 || boxItemEntity.getBu_ID() == 155 || boxItemEntity.getBu_ID() == 187 )){
//                        isJump = true;
//                        jumpToSelectPurchaseOrderActivity(boxItemEntity.getItem_ID(),boxItemEntity.getBu_ID());
//
//                    }else{
//                        ToastUtil.showToastShort("该入库标签不属于零部件公司下的三个结算中心，请确认！");
//                    }
//                    ----------这部分代码先注掉

                    currentBoxItemEntity = boxItemEntity;
//
                } else {
                    boxItemEntity.setResult(false);
                    boxItemEntity.setErrorInfo("该包装已经在装载列表中");
                }

            } else {

            }

            return null;
        }

        protected Boolean is_box_existed(BoxItemEntity box_item) {
            Boolean result = false;
            if (boxItemEntityList != null) {
                for (int i = 0; i < boxItemEntityList.size(); i++) {
                    if (boxItemEntityList.get(i).getDIII_ID() == box_item.getDIII_ID()) {
                        return true;
                    }
                }
            }

            return result;
        }


        @Override
        protected void onPostExecute(Void result) {
//            -----------添加这句
            handleCurrentBoxStatus(currentBoxItemEntity  );
            //  -----------添加这句


            if (!isJump){
                //tv.setText(fahren + "∞ F");
                if (scanBoxItemEntity != null) {
                    if (!scanBoxItemEntity.getResult()) {
                        Toast.makeText(StockInCompany29Activity.this, scanBoxItemEntity.getErrorInfo(), Toast.LENGTH_LONG).show();
                    }
                }

                boxItemAdapter = new InBoxItemAdapter(StockInCompany29Activity.this, boxItemEntityList);
                mRecyclerView.setAdapter(boxItemAdapter);
                inputEditText.setText("");
                inputEditText.setHint("请继续使用扫描枪");

                //2024-08-06 john判断物料是否是紧急物料
                GetIsAlarmItemAsyncTask task = new GetIsAlarmItemAsyncTask();
                task.execute(scanBoxItemEntity.getItem_ID() + "");
            }

        }

    }

    private void handleCurrentBoxStatus(BoxItemEntity boxItemEntity) {
        if (isOpenSuggestStock) {
//                        ToastUtil.showToastLong("建议仓库存放:" + boxItemEntity.getIstName());


            Message message = new Message();
            message.what = 1;
            Bundle bundle = new Bundle();
            bundle.putString("suggest_ist", boxItemEntity.getIstName());
            message.setData(bundle);
            handler.sendMessage(message);


        }
        boxItemEntity.setSelect(true);
        boxItemEntityList.add(boxItemEntity);


        if (boxItemEntity != null) {
            if (!boxItemEntity.getResult()) {
                Toast.makeText(StockInCompany29Activity.this, boxItemEntity.getErrorInfo(), Toast.LENGTH_LONG).show();
            }
        }

        boxItemAdapter = new InBoxItemAdapter(StockInCompany29Activity.this, boxItemEntityList);
        mRecyclerView.setAdapter(boxItemAdapter);
        inputEditText.setText("");
        inputEditText.setHint("请继续使用扫描枪");
//            if (inputDialog != null && inputDialog.isShowing()) {
//                inputDialog.dismiss();
//            }
        //pbScan.setVisibility(View.INVISIBLE);

        //2024-08-06 john判断物料是否是紧急物料
        GetIsAlarmItemAsyncTask task = new GetIsAlarmItemAsyncTask();
        task.execute(boxItemEntity.getItem_ID() + "");
    }

    private void jumpToSelectPurchaseOrderActivity(long item_ID ,int toBu_ID) {
        Intent intent = new Intent(StockInCompany29Activity.this, SelectPurchaseOrderListActivity.class);
        intent.putExtra(IntentConstant.Intent_Extra_MY_Purchase_Order_Bu_ID,toBu_ID);
        intent.putExtra(IntentConstant.Intent_Extra_MY_Purchase_Order_Item_ID,item_ID);
        startActivityForResult(intent,IntentConstant.Intent_Request_Stock_in_To_Purchase_Order_Activity);

    }


    private class GetIstAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            IstPlaceEntity istPlaceEntity = WebServiceUtil.op_Check_Commit_IST_Barcode(scanContent);
            if (istPlaceEntity.getResult()) {
                thePlace = istPlaceEntity;
                if (istPlaceEntity.getResult()) {
                    for (int i = 0; i < boxItemEntityList.size(); i++) {
                        if (boxItemEntityList.get(i).getSelect()) {
                            boxItemEntityList.get(i).setIstName(istPlaceEntity.getIstName());
                            boxItemEntityList.get(i).setIst_ID(istPlaceEntity.getIst_ID());
                            boxItemEntityList.get(i).setSub_Ist_ID(istPlaceEntity.getSub_Ist_ID());
                        }
                    }
                }
            } else {
//                Toast.makeText(StockInCompany29Activity.this, bi.getErrorInfo(), Toast.LENGTH_LONG).show();
                ToastUtil.showToastLong(istPlaceEntity.getErrorInfo());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");

            mRecyclerView.setAdapter(boxItemAdapter);
            //pbScan.setVisibility(View.INVISIBLE);
            inputEditText.setText("");
            //todo 直接执行入库登帐
            handleIntoWareHouse();

        }


    }

    private class AsyncExeWarehouseIn extends AsyncTask<String, Void, Void> {
        WsResult ws_result;

        @Override
        protected Void doInBackground(String... params) {

            List<BoxItemEntity> SelectList;
            SelectList = new ArrayList<>();

            for (int i = 0; i < boxItemEntityList.size(); i++) {
                if (boxItemEntityList.get(i).getSelect()) {
                    SelectList.add(boxItemEntityList.get(i));
                }
            }

            int count = 0;
            int selectedCount = SelectList.size();
            while (count < selectedCount && SelectList.size() > 0) {
//                StockInPurchaseOrderBean purchaseOrderBean = null;
//                if (purchaseEntityList.size() == SelectList.size()){
//                    purchaseOrderBean = purchaseEntityList.get(0);
//                }

                //todo  这里取的是0，验证多个是否成功
                //// TODO: 2024/5/8 因为SelectList 之后有remove，故取第0个是可以的，可以连续扫
                BoxItemEntity boxItemEntity = SelectList.get(0);
//                String sql = String.format("insert into Ist_SubIst_ManuLot (IST_ID,Sub_IST_ID,Item_ID,IV_ID,LotID,Company_ID,Bu_ID,ManuLotNo，SendToWarehouseTime) values (%d,%d,%d,%d,%d,%d,%d,%s,%s)",
                String sql = String.format("insert into Ist_SubIst_ManuLot (IST_ID,Sub_IST_ID,Item_ID,IV_ID,LotID,Company_ID,Bu_ID,ManuLotNo) values (%d,%d,%d,%d,%d,%d,%d,%s)",
                        boxItemEntity.getIst_ID(), boxItemEntity.getSub_Ist_ID(), boxItemEntity.getItem_ID(), boxItemEntity.getIV_ID(), boxItemEntity.getLotID(),
                        UserSingleton.get().getUserInfo().getCompany_ID(), UserSingleton.get().getUserInfo().getBu_ID(),
//                        !TextUtils.isEmpty(boxItemEntity.getManuLotNo()) ? boxItemEntity.getManuLotNo() : boxItemEntity.getLotNo());
//                        boxItemEntity.getLotNo(),UnitFormatUtil.formatTimeToSecond(System.currentTimeMillis()));
                        boxItemEntity.getLotNo());

//                ws_result = WebServiceUtil.op_Commit_DS_Item_Income_To_Warehouse(boxItemEntity,scanCodeList.size() == selectedCount ? scanCodeList.get(0):"");

//                -------------------这里直接传1000

//                if (purchaseOrderBean != null){
//                    ws_result = WebServiceUtil.op_Commit_DS_Item_Income_To_Warehouse_With_POOrder(boxItemEntity,scanCodeList.size() == selectedCount ? scanCodeList.get(0):"",purchaseOrderBean);
//                }else {
//                    return null;
//                }
//                    ws_result = WebServiceUtil.op_Commit_DS_Item_Income_To_Warehouse_With_POOrder(boxItemEntity,scanCodeList.size() == selectedCount ? scanCodeList.get(0):"",purchaseOrderBean);
                    ws_result = WebServiceUtil.op_Commit_DS_Item_Income_To_Warehouse_With_POI_ID(boxItemEntity,scanCodeList.size() == selectedCount ? scanCodeList.get(0):"",1000);

//                -------------------这里直接传1000

                if (ws_result.getResult()) {
                    //添加库位与manuLot的关联
//                    addIstSubIstManuLotRelation(boxItemEntity);
                    boxItemEntityList.remove(boxItemEntity);
                    SelectList.remove(boxItemEntity);
//                    if (purchaseOrderBean != null && purchaseEntityList.size() > 0){
//                        purchaseEntityList.remove(purchaseOrderBean);
//                    }

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
                    CommonUtil.ShowToast(StockInCompany29Activity.this, ws_result.getErrorInfo(), R.mipmap.warning);

                } else {
                    CommonUtil.ShowToast(StockInCompany29Activity.this, "入库完成", R.mipmap.smiley);
                }
                warehouseInButton.setEnabled(true);

            }

            boxItemAdapter = new InBoxItemAdapter(StockInCompany29Activity.this, boxItemEntityList);
            mRecyclerView.setAdapter(boxItemAdapter);
            scanCodeList.clear();
        }

    }


}

