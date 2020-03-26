package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chinashb.www.mobileerp.adapter.CommonItemBarCodeAdapter;
import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.PartWorkLineItemEntity;
import com.chinashb.www.mobileerp.bean.entity.WcIdNameEntity;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.utils.UnitFormatUtil;
import com.chinashb.www.mobileerp.widget.CommonSelectInputDialog;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2020/1/8 1:55 PM
 * @author 作者: liweifeng
 * @description 零部件中生产线领料（为辅助料、退料）页面
 */
public class PartWorkLinePutActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.part_workline_in_select_wc_button) Button selectWcButton;
    @BindView(R.id.part_workline_in_wc_name_textView) TextView wcNameTextView;
    @BindView(R.id.part_workline_in_select_NO_button) Button selectNOButton;
    @BindView(R.id.part_workline_in_NO_textView) TextView NOTextView;
    @BindView(R.id.part_workline_in_scan_button) Button scanItemButton;
    @BindView(R.id.part_workline_in_warehouse_in_button) Button warehouseInButton;
    @BindView(R.id.part_workline_in_input_EditText) EditText inputEditText;
    @BindView(R.id.part_workline_in_recyclerView) CustomRecyclerView recyclerView;
    @BindView(R.id.part_workline_in_remark_button) Button remarkButton;
    @BindView(R.id.part_workline_in_remark_TextView) TextView remarkTextView;

    private WcIdNameEntity wcIdNameEntity;
    private String scanContent;
    private String regex = "^[0-9]*$";
//    private Pattern pattern = Pattern.compile(regex);

    private List<String> noList;
    private CommonSelectInputDialog commonSelectInputDialog;
    private OnViewClickListener onViewClickListener = new OnViewClickListener() {
        @Override public <T> void onClickAction(View v, String tag, T t) {
            if (t != null) {
//                remark = (String) t;
//                Matcher matcher = pattern.matcher(remark);
//                if (matcher.matches()) {
//                    NOTextView.setText((CharSequence) t);
//                }else {
//                    ToastUtil.showToastShort("单据号格式有误，只能是纯数字，请重新输入");
//                }

                NOTextView.setText((CharSequence) t);
            }
            if (commonSelectInputDialog != null && commonSelectInputDialog.isShowing()) {
                commonSelectInputDialog.dismiss();
            }
        }
    };

    private OnViewClickListener remarkOnViewClickListener = new OnViewClickListener() {
        @Override public <T> void onClickAction(View v, String tag, T t) {
            if (t != null) {
                remark = (String) t;
                remarkTextView.setText((CharSequence) t);
            }
            if (remarkDialog != null && remarkDialog.isShowing()) {
                remarkDialog.dismiss();
            }
        }
    };
    private String remark = "";
    private String listNo;//单据号
    private List<PartWorkLineItemEntity> workLineItemEntityList;
    private List<BoxItemEntity> boxItemEntityArrayList = new ArrayList<>();
    //    private InBoxItemAdapter adapter;
    private CommonItemBarCodeAdapter adapter;
    private CommonSelectInputDialog remarkDialog;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_part_workline_layout);
        ButterKnife.bind(this);
        initView();
        setViewsListener();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            if (data != null) {
                wcIdNameEntity = data.getParcelableExtra(IntentConstant.Intent_product_wc_id_name_entity);
                wcNameTextView.setText(wcIdNameEntity.getWcName());
                getPartWorkLineItemList();

            }
        } else {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (!TextUtils.isEmpty(result.getContents())) {
                    parseContent(result.getContents());
                }
            } else {
                // This is important, otherwise the result will not be passed to the fragment
                super.onActivityResult(requestCode, resultCode, data);
            }
        }

    }

    private void initView() {
        adapter = new CommonItemBarCodeAdapter();
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        recyclerView.setAdapter(adapter);

        adapter.setData(boxItemEntityArrayList);
    }

    private void setViewsListener() {
        selectWcButton.setOnClickListener(this);
        scanItemButton.setOnClickListener(this);
        remarkButton.setOnClickListener(this);
//        scanAreaButton.setOnClickListener(this);
        warehouseInButton.setOnClickListener(this);
        selectNOButton.setOnClickListener(this);
        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                parseContent(editable.toString());
            }
        });

        NOTextView.addTextChangedListener(new TextWatcherImpl() {
            @Override public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                listNo = editable.toString();
            }
        });

        remarkTextView.addTextChangedListener(new TextWatcherImpl(){
            @Override public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                remark = editable.toString();
            }
        });
    }

    @Override public void onClick(View view) {
        if (view == selectWcButton) {
            getWCList();


        } else if (view == scanItemButton) {
            if (TextUtils.isEmpty(wcNameTextView.getText())) {
                ToastUtil.showToastShort("请先选择产线");
                return;
            }
            new IntentIntegrator(this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
        } else if (view == warehouseInButton) {
            handleIntoWareHouse();
            wcNameTextView.setText("");
//            subProductItemEntityList = new ArrayList<>()
//            if (subProductItemEntityList != null && subProductItemEntityList.size() > 0) {
//                subProductItemEntityList.clear();
//            }

        } else if (view == selectNOButton) {
            handleSelectNO();
        } else if (view == remarkButton) {
            if (remarkDialog == null) {
                remarkDialog = new CommonSelectInputDialog(PartWorkLinePutActivity.this);
            }
            remarkDialog.show();
            remarkDialog.setOnViewClickListener(remarkOnViewClickListener);
        }
    }

    private void handleIntoWareHouse() {
//        if (TextUtils.isEmpty(remarkTextView.getText().toString())){
//            ToastUtil.showToastShort("请为本操作添加备注");
////                    remarkTextView.requestFocus();
//            return;
//        }

        if (boxItemEntityArrayList.size() > 0) {
            AsyncExeWarehouseOut task = new AsyncExeWarehouseOut();
            task.execute();

        }
    }

    private void handleSelectNO() {
        if (commonSelectInputDialog == null) {
            commonSelectInputDialog = new CommonSelectInputDialog(PartWorkLinePutActivity.this);
        }
        commonSelectInputDialog.show();
        commonSelectInputDialog.setOnViewClickListener(onViewClickListener);
        commonSelectInputDialog.setTitle("请选择或添加单据号");
        commonSelectInputDialog.refreshContent(getNOList());
    }

    private List<String> getNOList() {
        noList = new ArrayList<>();
//        C-20191226-01
        for (int i = 0; i < 15; i++) {
            noList.add(String.format("%s-%s", UnitFormatUtil.formatTimeToDayWithoutLine(System.currentTimeMillis()), String.format("%02d", i + 1)));
        }
        return noList;
    }

    /**
     * 获取产线（工作中心）
     */
    private void getWCList() {
        Intent intent = new Intent(this, SelectProductWCListActivity.class);
        intent.putExtra(IntentConstant.Intent_Extra_work_line_from, IntentConstant.Intent_Extra_work_line_from_part);
        startActivityForResult(intent, 200);
    }

    private void getPartWorkLineItemList() {
        GetWCPartWorkItemListAsyncTask task = new GetWCPartWorkItemListAsyncTask();
        task.execute();
    }

    private void parseContent(String content) {
//        certainWCSubProductEntity = null;
        if (TextUtils.isEmpty(content)) {
            return;
        }
        System.out.println("============ scan content = " + content);
        // V5/B/54/PS/10475/L/191217/LQ/0/Qty/120
        if (content.contains("/")) {
            String[] qrContent;
            qrContent = content.split("/");
            if (qrContent.length >= 2) {
                String qrTitle = qrContent[0];
                if (!qrTitle.equals("")) {
                    if (qrTitle.equals("VE") || qrTitle.equals("VF") || qrTitle.equals("VG") || qrTitle.equals("V9") || qrTitle.equals("VA") || qrTitle.equals("VB") || qrTitle.equals("VC")) {
                        //物品条码
                        scanContent = content;
                        GetItemQRCodeAsyncTask task = new GetItemQRCodeAsyncTask();
                        task.execute();
                    }
                }
            }
        }
    }

//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == 0) {
//                ToastUtil.showToastLong("您当前公司与来料入库公司不符合，请确认来料是否入到该公司！");
//            }
//        }
//    };

    private class GetWCPartWorkItemListAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //// TODO: 2019/12/20  注意pc是取top 100,这里取的是全部
            String sql = String.format("Select Distinct Item.Item_ID, Item_Version.IV_ID,Item.Item+' '+Item.Item_Name+' '+isnull(Item.Item_Spec2,'')  As Transferred_Item_Name, Item_Version.Item_Version " +
                            "   From P_PWC inner Join P on p.Pid=p_pwc.pid and P_pwc.wc_id=%s And not p.abv_Id is null and not P.abv_id=0  inner join abom on abom.abv_id=p.abv_id " +
                            " inner join Item on abom.item_id=item.Item_id  inner join Item_Version on Item.item_id = item_Version.item_ID  Where p_pwc.wc_id=%s", wcIdNameEntity.getWcId()
                    , wcIdNameEntity.getWcId());
            WsResult result = WebServiceUtil.getDataTable(sql);
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                Gson gson = new Gson();
                workLineItemEntityList = gson.fromJson(jsonData, new TypeToken<List<PartWorkLineItemEntity>>() {
                }.getType());
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    private class GetItemQRCodeAsyncTask extends AsyncTask<String, Void, BoxItemEntity> {
        @Override
        protected BoxItemEntity doInBackground(String... params) {
//            BoxItemEntity boxItemEntity = WebServiceUtil.op_Check_Work_Line_Scan_Item_Barcode(scanContent);
//            BoxItemEntity boxItemEntity = WebServiceUtil.op_Check_Work_Line_Scan_Item_Barcode( "VB/MT/691823/S/3508/IV/20591/P/ZDQRB11-6001 A0/D/20191206/L/19120601/N/101/Q/768");
            BoxItemEntity boxItemEntity;
//                    = WebServiceUtil.op_Check_Work_Line_Scan_Item_Barcode_Json( "VB/MT/691823/S/3508/IV/20591/P/ZDQRB11-6001 A0/D/20191206/L/19120601/N/101/Q/768");

//            WsResult wsResult = WebServiceUtil.op_Check_Work_Line_Scan_Item_Barcode_Json( "VB/MT/722025/S/46/IV/920/P//D/20200226/L/20022401/N/4/Q/48");
            boxItemEntity = WebServiceUtil.op_Check_Work_Line_Scan_Item_Barcode(scanContent);

//            if (wsResult .getResult()){
//                boxItemEntity = JsonUtil.parseJsonToObject(wsResult .getErrorInfo() ,BoxItemEntity .class );
//                return boxItemEntity ;
//            }
            return boxItemEntity;
//            return null;
        }

        @Override
        protected void onPostExecute(BoxItemEntity boxItemEntity) {
            if (boxItemEntity.getResult()) {
                if (!is_box_existed(boxItemEntity)) {
                    boxItemEntity.setSelect(true);
                    String nullType = "anyType{}";
                    if (TextUtils.isEmpty(boxItemEntity.getIstName()) || boxItemEntity.getIstName().contains(nullType)) {
                        //// TODO: 2020/3/9  
//                        boxItemEntity.setIstName(boxItemEntity.getIst_ID() + ":" + boxItemEntity.getSub_Ist_ID());
                        boxItemEntity.setIstName("");
                    }

                    //// TODO: 2020/3/9  
                    if (TextUtils.isEmpty(boxItemEntity.getBuName()) || boxItemEntity.getBuName().contains(nullType)) {
                        boxItemEntity.setBuName(UserSingleton.get().getUserInfo().getBu_Name());
                    }


//                    boolean hasThisItem = false;
//                    if (workLineItemEntityList == null || workLineItemEntityList.size() == 0){
//                        return;
//                    }
//                    for (PartWorkLineItemEntity entity : workLineItemEntityList) {
//                        if (entity.getItemId() == boxItemEntity.getItem_ID()) {
//                            hasThisItem = true;
////                            certainWCSubProductEntity = entity;
////                            WCSubProductItemEntity itemEntity = new WCSubProductItemEntity();
////                            itemEntity.setWcSubProductEntity(entity);
////                            itemEntity.setSelect(true);
////                            itemEntity.setLotNo(lotNo);
////                            itemEntity.setQty(qty);
////                            itemEntity.setBuName(UserSingleton.get().getUserInfo().getBu_Name());
//
////                            subProductItemEntityList.add(itemEntity);
////                            adapter = new ItemProductNonTrayAdapter(ProductInNonTrayActivity.this, subProductItemEntityList);
////                            mRecyclerView.setAdapter(adapter);
////                            inputEditText.setText("");
////                            //// TODO: 2019/12/20 去掉扫描枪几个字
////                            inputEditText.setHint("请继续扫描");
//                            break;
//                        }
//                    }
//                    if (!hasThisItem) {
//                        ToastUtil.showToastShort("该产品不属于此产线！");
//                    }else{
//                        boxItemEntityArrayList.add(boxItemEntity);
//                    }

                    boxItemEntityArrayList.add(boxItemEntity);

                } else {
                    boxItemEntity.setResult(false);
                    boxItemEntity.setErrorInfo("该包装已经在装载列表中");
                }
            }

            if (boxItemEntity != null) {
                if (!boxItemEntity.getResult()) {
                    ToastUtil.showToastShort(boxItemEntity.getErrorInfo());
                }
            }

//            recyclerView.setAdapter(adapter);
            adapter.setData(boxItemEntityArrayList);
            inputEditText.setText("");
            inputEditText.setHint("请继续扫描");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    private boolean is_box_existed(BoxItemEntity box_item) {
        Boolean result = false;

        if (boxItemEntityArrayList != null) {
            for (int i = 0; i < boxItemEntityArrayList.size(); i++) {
                if (boxItemEntityArrayList.get(i).getSMLI_ID() == box_item.getSMLI_ID() && box_item.getSMLI_ID() > 0) {
                    return true;
                }
                if (boxItemEntityArrayList.get(i).getSMM_ID() == box_item.getSMM_ID() && box_item.getSMM_ID() > 0) {
                    return true;
                }
                if (boxItemEntityArrayList.get(i).getSMT_ID() == box_item.getSMT_ID() && box_item.getSMT_ID() > 0) {
                    return true;
                }
                if (boxItemEntityArrayList.get(i).getSMT_ID() == box_item.getSMT_ID() && box_item.getSMT_ID() == 0
                        && boxItemEntityArrayList.get(i).getSMM_ID() == box_item.getSMM_ID() && box_item.getSMM_ID() == 0
                        && boxItemEntityArrayList.get(i).getSMLI_ID() == box_item.getSMLI_ID() && box_item.getSMLI_ID() == 0
                        && boxItemEntityArrayList.get(i).getLotID() == box_item.getLotID()) {
                    return true;
                }


            }
        }

        return result;
    }

    private class AsyncExeWarehouseOut extends AsyncTask<String, Void, Void> {
        WsResult ws_result;

        @Override
        protected Void doInBackground(String... params) {

            int count = 0;
            int newissuesize = boxItemEntityArrayList.size();
            while (count < newissuesize && boxItemEntityArrayList.size() > 0) {
                BoxItemEntity boxItemEntity = boxItemEntityArrayList.get(0);
//                op_Commit_Work_line_Item_Non_Plan(Bu_ID As Integer, Exer As Long,
//                        Item_ID As Long, IV_ID As Long,
//                        LotID As Long, LotNo As String,
//                        Ist_ID As Long, Sub_Ist_ID As Long,
//                        SMLI_ID As Long, SMM_ID As Long, SMT_ID As Long,
//                        Qty As Double, txtEntity As String, txtRecord As String, Remark As String, WC_ID As Integer)
                ws_result = WebServiceUtil.op_Commit_Work_line_Item_Non_Plan(boxItemEntity.getItem_ID(), boxItemEntity.getIV_ID(), boxItemEntity.getLotID(),
                        boxItemEntity.getLotNo(), boxItemEntity.getIst_ID(), boxItemEntity.getSub_Ist_ID(), boxItemEntity.getSMLI_ID(), boxItemEntity.getSMM_ID(), boxItemEntity.getSMT_ID(),
                        String.valueOf(boxItemEntity.getQty()), wcIdNameEntity.getWcName(), listNo, remark, wcIdNameEntity.getWcId());

//                ws_result = WebServiceUtil.op_Product_Manu_In_Not_Pallet(wcIdNameEntity,boxItemEntity,new Date(),
//                        listNo,new Date() ,"李伟锋成品入库测试",
//                        13269,"lwf",
//                        thePlace.getIst_ID(),thePlace.getSub_Ist_ID(),boxItemEntity.getQty());

//                if (ws_result.getResult() ) {
//                    boxItemEntityArrayList.remove(boxItemEntity);
//                } else {
//                    return null;
//                }

                count++;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
//            adapter.notifyDataSetChanged();
//            recyclerView.setAdapter(adapter);
            //pbScan.setVisibility(View.INVISIBLE);
//            remarkTextView.setText("");
            remark = "";
            if (ws_result != null) {
                if (!ws_result.getResult()) {
                    ToastUtil.showToastShort(ws_result.getErrorInfo());
                } else {
                    ToastUtil.showToastShort("成功出库");
                    boxItemEntityArrayList.clear();
//                    boxItemEntityArrayList.remove(boxItemen)
                    adapter.notifyDataSetChanged();
                }
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
