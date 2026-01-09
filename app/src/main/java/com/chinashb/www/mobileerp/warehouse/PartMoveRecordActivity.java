package com.chinashb.www.mobileerp.warehouse;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.PartMoveRecordAdapter;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.BoxMoveRecordBean;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2023/7/10 10:07 AM
 * @author 作者: liweifeng
 * @description 零件移库查询
 */
public class PartMoveRecordActivity extends BaseActivity {


    @BindView(R.id.move_record_input_EditText) EditText inputEditText;
    @BindView(R.id.move_record_scan_button) Button scanButton;
    @BindView(R.id.move_record_recyclerView) CustomRecyclerView recyclerView;
    private PartMoveRecordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_record_layout);
        ButterKnife.bind(this);
        scanButton.setOnClickListener(v -> {
            new IntentIntegrator(PartMoveRecordActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
        });

        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                if (editable.toString().length() > 7) {
                    parseScanResult(editable.toString());
                }
            }
        });

        adapter = new PartMoveRecordAdapter();
        recyclerView.setAdapter(adapter);

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

    @SuppressLint("NewApi")
    private void parseScanResult(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        content = content.replaceAll("\n","");
        String boxID = "";
        if (content.contains("/") || content.contains("／")) {
            if (content.contains("／")) {
                content = content.replace("／", "/");
            }

            String[] qrContent;
            qrContent = content.split("/");
            if (qrContent.length == 2) {
                String qrTitle = qrContent[0];
                if (!qrTitle.equals("")) {
                    if (qrTitle.equals("VG")  || qrTitle.equals("VC")) {
                        //物品条码
                       boxID = qrContent[1];
                    }else if (qrTitle.equals("VE") ) {
                        //物品条码
                        boxID = qrContent[1];
                    }else{
                        ToastUtil.showToastShort("格式不符，请在输入框中输入VG或VE开头的箱码，点回车结束！");
                    }
                }
//                GetMoveRecordAsyncTask task = new GetMoveRecordAsyncTask();
//                task.execute(boxID);
            }else {
                //  3/24/25   john 处理类似 v9/....等很多split的字段

                if (qrContent .length > 5){
                    String qrTitle = qrContent[0];
                    if (qrTitle.equals("V9")  ) {
                        //物品条码
                        boxID = qrContent[2];
                    }else if (qrTitle.equals("VA") ) {
                        //物品条码
                        boxID = qrContent[2];
                    }else if (qrTitle.equals("VB") ) {
                        //物品条码
                        boxID = qrContent[2];
                    }else{
                        ToastUtil.showToastShort("格式不符，请在输入框中输入VG或VE开头的箱码，点回车结束！");
                    }
                }
            }
            GetMoveRecordAsyncTask task = new GetMoveRecordAsyncTask();
            task.execute(boxID);
        }
    }

    private class GetMoveRecordAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            if (strings.length > 0){
                String boxid = strings[0];
                String sql = String.format("select item.item_id,item.item,item.item_version,supplier_manu_top_box.iqty,box_move.Bu_ID,bu_name,dbo.Ist_Get_Name(fromist_id)  as 移出库位,  dbo.Sub_Ist_Get_Name(fromsub_ist_id) as 移出单元,dbo.Ist_Get_Name(toist_id)  as 移到库位,  dbo.Sub_Ist_Get_Name(tosub_ist_id) as 移到单元,executorName as 移库人,op_time as 移库时间 , case when move_box_type = 1 then  '大箱'  else '小箱'end as 移库类型,boxcode from box_move\n" +
                        "                           inner join bu on box_move.bu_id = bu.bu_id\n" +
                        "                                            inner join supplier_manu_top_box on supplier_manu_top_box.smt_id = box_move.smt_id\n" +
                        "                                            inner join item on supplier_manu_top_box.item_id = item.item_id\n" +
                        "                           where box_move.smt_id = %s  and box_move.Bu_ID = %s\n" +
                        "                                            union\n" +
                        "                                            select item.item_id,item.item,item.item_version,supplier_manu_lot_item.iqty,box_move.Bu_ID,bu_name,dbo.Ist_Get_Name(fromist_id)  as 移出库位,  dbo.Sub_Ist_Get_Name(fromsub_ist_id) as 移出单元,dbo.Ist_Get_Name(toist_id)  as 移到库位,  dbo.Sub_Ist_Get_Name(tosub_ist_id) as 移到单元,executorName as 移库人,op_time as 移库时间 , case when move_box_type = 1 then  '大箱'  else '小箱'end as 移库类型,boxcode from box_move\n" +
                        "                           inner join bu on box_move.bu_id = bu.bu_id\n" +
                        "                                            inner join supplier_manu_lot_item on supplier_manu_lot_item.smli_id = box_move.smli_id\n" +
                        "                                            inner join item on supplier_manu_lot_item.item_id = item.item_id\n" +
                        "                           where box_move.smli_id = %s  and box_move.Bu_ID = %s",boxid,UserSingleton.get().getUserInfo().getBu_ID() + "",boxid, UserSingleton.get().getUserInfo().getBu_ID() + "");
                WsResult result = WebServiceUtil.getDataTable(sql);
                if (result != null && result.getResult()) {
                    String jsonData = result.getErrorInfo();
                    if (!TextUtils.isEmpty(jsonData)) {
                        //// TODO: 2020/1/3 放这里会出错，刷新UI线程问题
                        return jsonData;

                    }
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(String jsonData) {
            super.onPostExecute(jsonData);
            Gson gson = new Gson();
            List<BoxMoveRecordBean> moveRecordBeanList = gson.fromJson(jsonData,new TypeToken<List<BoxMoveRecordBean>>(){}.getType());
            if (moveRecordBeanList != null && moveRecordBeanList.size() > 0){
                adapter.setData(moveRecordBeanList);
            }
//            List<DeliveryOrderBean> deliveryOrderBeanList = gson.fromJson(jsonData, new TypeToken<List<DeliveryOrderBean>>() {
//            }.getType());
////                    DeliveryOrderBean bean = JsonUtil.parseJsonToObject(jsonData,new TypeToken<List<DeliveryOrderBean>>(){});
//            if (deliveryOrderBeanList != null && deliveryOrderBeanList.size() > 0) {
//                //// TODO: 2020/2/26
//                deliveryOrderBean = deliveryOrderBeanList.get(0);
//
//
//
//                adapter.setData(deliveryOrderBeanList);
//                if (emptyLayoutView.getVisibility() == View.VISIBLE) {
//                    emptyLayoutView.setVisibility(View.GONE);
//                    recyclerView.setVisibility(View.VISIBLE);
//                }
//            } else {
//                recyclerView.setVisibility(View.GONE);
//                emptyLayoutView.setVisibility(View.VISIBLE);
//            }

        }
    }
}

