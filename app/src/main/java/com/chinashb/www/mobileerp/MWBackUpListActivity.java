package com.chinashb.www.mobileerp;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.chinashb.www.mobileerp.adapter.MWBackUpAdapter;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.IssueOutBackUpBean;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CommAlertDialog;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.chinashb.www.mobileerp.widget.EmptyLayoutManageView;
import com.chinashb.www.mobileerp.widget.OnDialogViewClickListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 1/21/26 2:39 PM
 * @author 作者: liweifeng
 * @description 查看备料详情页面
 */
public class MWBackUpListActivity extends BaseActivity {
    @BindView(R.id.mw_backup_title_confirm_Button) Button executeAllButton;
    //    @BindView(R.id.select_mw_backup_title_manageView) TitleLayoutManagerView titleManageView;
    @BindView(R.id.mw_backup_recyclerView) CustomRecyclerView orderRecyclerView;
    @BindView(R.id.mw_backup_empty_layoutView) EmptyLayoutManageView emptyLayoutView;
    private int toBu_ID;
    private long mpiwc_ID;
    private MWBackUpAdapter adapter;
//    private IssueOutBackUpBean backUpBean;
    private List<IssueOutBackUpBean> resultBeanList = new ArrayList<>();
    private List<IssueOutBackUpBean> removedBeanList = new ArrayList<>();
    private List<IssueOutBackUpBean> originalBeanList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mw_backup_manage_layout);
        ButterKnife.bind(this);
//        toBu_ID = getIntent().getIntExtra(IntentConstant.Intent_Extra_MY_Purchase_Order_Bu_ID, -1);
        mpiwc_ID = getIntent().getLongExtra(IntentConstant.Intent_Extra_backup_mpiwc_id, -1);
        adapter = new MWBackUpAdapter();
        orderRecyclerView.setAdapter(adapter);
        adapter.setOnViewClickListener(new OnViewClickListener() {
            @Override
            public <T> void onClickAction(View v, String tag, T t) {
                if (t != null) {
                    IssueOutBackUpBean backUpBean = (IssueOutBackUpBean) t;
                    if (backUpBean != null ){
                        if (backUpBean.isSelected()){
                            resultBeanList.add((backUpBean));
                        }else{
                            if (resultBeanList.contains(backUpBean)){
                                resultBeanList.remove(backUpBean);
                            }
                        }

                    }
//                    jumpBackToStockInActivity();
                } else {
//                    backUpBean = null;
//                    ToastUtil.showToastShort("获取备料数据失败！");
//                    finish();
                }
            }
        });
        if (mpiwc_ID > 0) {
            getBackUpList();
        } else {
            ToastUtil.showToastShort("参数有误，未能获取备料的计划！");
        }
        executeAllButton.setOnClickListener(v -> {

//            jumpBackToStockInActivity();
            handleAllOut();
        });

    }

    private void handleAllOut() {
//       WsResult ws_result = WebServiceUtil.op_Commit_MW_Issue_Item(mpiwc_ID, bi,new Date() ,"");
//        IssueOutBackUpBean backUpBean =
//        StringBuilder stringBuilder = new StringBuilder();
//        if (resultBeanList.size() > 0){
//            for (IssueOutBackUpBean backUpBean : resultBeanList){
//                WsResult ws_result = WebServiceUtil.op_Commit_MW_Issue_Item(mpiwc_ID, UserSingleton.get().getHRID(),backUpBean.getItemId(),backUpBean.getIV_ID(),backUpBean.getLotID(),
//                        backUpBean.getLotNo() != null ? backUpBean.getLotNo():"",backUpBean.getIst_ID(),backUpBean.getSub_Ist_ID(),
//                        backUpBean.getSMLI_ID(),0L,backUpBean.getSMT_ID(),backUpBean.getQty() + "",new Date(),backUpBean.getScanX() != null ? backUpBean.getScanX() : "" );
//                if (ws_result.getResult()){
//                    stringBuilder.append(backUpBean.getScanX()).append("成功！\n");
//                }else {
//                    stringBuilder.append(backUpBean.getScanX()).append("失败！原因").append(ws_result.getErrorInfo()).append("\n");
//
//                }
//
//            }
//            CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(MWBackUpListActivity.this)
//                    .setTitle("备料出库信息").setMessage(stringBuilder.toString())
//                    .setLeftText("确定");
//
//
//            builder.setOnViewClickListener(new OnDialogViewClickListener() {
//                @Override
//                public void onViewClick(Dialog dialog, View v, int tag) {
//                    switch (tag) {
//                        case CommAlertDialog.TAG_CLICK_LEFT:
//    //                        CommonUtil.doLogout(MWBackUpListActivity.this);
//                            dialog.dismiss();
//                            break;
//                    }
//                }
//            });
//            builder.create().show();
//        }

        if (resultBeanList.size() > 0){
            BackupMWOutAsyncTask task = new BackupMWOutAsyncTask();
            task.execute();
        }else{
            ToastUtil.showToastShort("未有待投料出库数据！");
        }


    }

//    private void jumpBackToStockInActivity() {
////        Intent intent = new Intent(this, StockInActivity.class);
//        Intent intent = new Intent(this, StockInCompany29Activity.class);
//        intent.putExtra(IntentConstant.Intent_Extra_MY_Purchase_Order_bean, tempDpOrderDetailBean);
//        setResult(IntentConstant.Intent_Request_Stock_in_To_Purchase_Order_Activity, intent);
//        finish();
//    }

    private void getBackUpList() {
        GetMWBackUpDataAsyncTask task = new GetMWBackUpDataAsyncTask();
        task.execute();
    }

    private class GetMWBackUpDataAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            String sql = "Select MB.Item_id,item_Name,Item_Version.Item_Version,Lot.LotNo ,Item_Storage_Sub.Ist_Name,ScanX,Qty,Bu_ID,HR_Name,Remark,MB.IV_ID,Lot.LotID,MB.Ist_ID,MB.Sub_Ist_ID,SMLI_ID,smmTotalQty,SMT_ID from MW_Issue_Out_BackUp  MB\n" +
                    "Inner Join Item on Item.item_id  = MB.item_id\n" +
                    "Inner Join Item_Version on Item_Version.IV_ID = MB.IV_ID\n" +
                    "Inner Join Lot on Lot.LotID = MB.Lot_ID\n" +
                    "Inner Join Item_Storage_Sub on Item_Storage_Sub.Sub_Ist_ID = mb.sub_ist_id\n" +
                    "where MB.MPIWC_ID =  " + mpiwc_ID;
            WsResult result = WebServiceUtil.getDataTable(sql);
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                System.out.println("============================jsonData = " + jsonData);
                if (!TextUtils.isEmpty(jsonData)) {
                    return jsonData;

                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(String json) {
            if (!TextUtils.isEmpty(json) && !json.trim().equals("[]")) {
                Gson gson = new Gson();
                List<IssueOutBackUpBean> orderBeanList = gson.fromJson(json, new TypeToken<List<IssueOutBackUpBean>>() {
                }.getType());
//                return orderBeanList;
                if (orderBeanList != null && orderBeanList.size() > 0) {
                    adapter.setData(orderBeanList);
                    orderRecyclerView.setVisibility(View.VISIBLE);
                    emptyLayoutView.setVisibility(View.GONE);
                } else {
                    ToastUtil.showToastShort("没有获取到相关数据！");
                    emptyLayoutView.setVisibility(View.VISIBLE);
                    orderRecyclerView.setVisibility(View.GONE);
                }
            }
        }

    }

    private class BackupMWOutAsyncTask  extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder stringBuilder = new StringBuilder();
            if (resultBeanList.size() > 0){
                for (IssueOutBackUpBean backUpBean : resultBeanList){
                    WsResult ws_result = WebServiceUtil.op_Commit_MW_Issue_Item(mpiwc_ID, UserSingleton.get().getHRID(),backUpBean.getItemId(),backUpBean.getIV_ID(),backUpBean.getLotID(),
                            backUpBean.getLotNo() != null ? backUpBean.getLotNo():"",backUpBean.getIst_ID(),backUpBean.getSub_Ist_ID(),
                            backUpBean.getSMLI_ID(),0L,backUpBean.getSMT_ID(),backUpBean.getQty() + "",new Date(),backUpBean.getScanX() != null ? backUpBean.getScanX() : "" );
                    if (ws_result.getResult()){
                        stringBuilder.append(backUpBean.getScanX()).append("成功！\n");
//                        resultBeanList.remove(backUpBean);
//                        removedBeanList.add(backUpBean);
                        originalBeanList.remove(backUpBean);
                    }else {
                        stringBuilder.append(backUpBean.getScanX()).append("失败！原因").append(ws_result.getErrorInfo()).append("\n");

                    }

                }

            }
            return stringBuilder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(MWBackUpListActivity.this)
                    .setTitle("备料出库信息").setMessage(result)
                    .setLeftText("确定");


            builder.setOnViewClickListener(new OnDialogViewClickListener() {
                @Override
                public void onViewClick(Dialog dialog, View v, int tag) {
                    switch (tag) {
                        case CommAlertDialog.TAG_CLICK_LEFT:
                            //                        CommonUtil.doLogout(MWBackUpListActivity.this);
                            dialog.dismiss();
                            refreshAdapter();
                            break;
                    }
                }
            });
            builder.create().show();
        }
    }

    private void refreshAdapter() {
//        List<IssueOutBackUpBean> originalList = adapter.getList();
//        for (IssueOutBackUpBean backUpBean : originalList){
//            if ()
//        }
//        if (originalBeanList.size() > 0)

//        adapter.notifyDataSetChanged();
        if (originalBeanList.size() == 0){
            orderRecyclerView.setVisibility(View.GONE);
            emptyLayoutView.setVisibility(View.VISIBLE);
        }else{
            adapter.setData(originalBeanList);
        }
    }

}


