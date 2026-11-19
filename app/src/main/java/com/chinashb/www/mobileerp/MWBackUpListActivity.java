package com.chinashb.www.mobileerp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.chinashb.www.mobileerp.adapter.MWBackUpAdapter;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.IssueOutBackUpBean;
import com.chinashb.www.mobileerp.bean.MWIssueItemOutParam;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.MWPrefKeys;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.warehouse.StockPartMoveActivity;
import com.chinashb.www.mobileerp.widget.CommAlertDialog;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.chinashb.www.mobileerp.widget.EmptyLayoutManageView;
import com.chinashb.www.mobileerp.widget.OnDialogViewClickListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 1/21/26 2:39 PM
 * @author 作者: liweifeng
 * @description 查看备料详情页面
 */
public class MWBackUpListActivity extends BaseActivity {
    private static final long BATCH_DUPLICATE_WINDOW_MS = 30_000L;

    @BindView(R.id.mw_backup_title_confirm_Button) Button executeAllButton;
    //    @BindView(R.id.select_mw_backup_title_manageView) TitleLayoutManagerView titleManageView;
    // by codex -- john 2026-06-25 15:24 - 全选按钮功能
    @BindView(R.id.mw_backup_title_select_all_button) Button selectAllButton;
    // codex by john 2026-09-04 16:01 - 额外出库按钮和自定义返回按钮
    @BindView(R.id.mw_backup_extra_out_button) Button extraOutButton;
    @BindView(R.id.mw_backup_title_back) ImageView backButton;
    @BindView(R.id.mw_backup_recyclerView) CustomRecyclerView orderRecyclerView;
    @BindView(R.id.mw_backup_empty_layoutView) EmptyLayoutManageView emptyLayoutView;
    private int toBu_ID;
    private boolean isAllSelected = false;
    private long mpiwc_ID;
    private MWBackUpAdapter adapter;
//    private IssueOutBackUpBean backUpBean;
    private List<IssueOutBackUpBean> resultBeanList = new ArrayList<>();
    private List<IssueOutBackUpBean> removedBeanList = new ArrayList<>();
    private List<IssueOutBackUpBean> originalBeanList = new ArrayList<>();
    private IssueOutBackUpBean backUpBean;
    private boolean isBatchOutInFlight;
    private String lastBatchFingerprint = "";
    private boolean lastBatchSucceeded;
    private long lastBatchAttemptAt;
    private final Set<String> successfulBatchFingerprints = new HashSet<>();


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

        adapter.setOnReworkClickListener(new MWBackUpAdapter.OnReworkClickListener() {
            @Override
            public void onReworkClick(IssueOutBackUpBean backUpBean) {
                if (backUpBean != null){
                    ToastUtil.showToastShort("进入到移库页面");
                    Intent intent = new Intent(MWBackUpListActivity.this, StockPartMoveActivity.class);
                    intent.putExtra(IntentConstant.Intent_Extra_Backup_ScanCode,backUpBean.getScanX());
                    intent.putExtra(IntentConstant.Intent_Extra_Backup_mpiwc_id_for_move_cancel,mpiwc_ID);
                    intent.putExtra(IntentConstant.Intent_Extra_Backup_bean,backUpBean);
                    startActivityForResult(intent,IntentConstant.Intent_Request_Backup_To_PartMove_Activity);
                }
            }
        });
        if (mpiwc_ID > 0) {
            getBackUpList();
        } else {
            ToastUtil.showToastShort("参数有误，未能获取备料的计划！");
        }
        // codex by john 2026-09-04 16:01 - 自定义返回按钮
        backButton.setOnClickListener(v -> finish());
        // codex by john 2026-09-04 16:01 - 额外出库按钮
        extraOutButton.setOnClickListener(v -> handleExtraOut());

        executeAllButton.setOnClickListener(v -> {

//            jumpBackToStockInActivity();
            handleAllOut();
        });
        selectAllButton.setOnClickListener(v -> {
            if (isAllSelected) {
                adapter.deselectAll();
                resultBeanList.clear();
                selectAllButton.setText("全选");
                isAllSelected = false;
            } else {
                adapter.selectAll();
                resultBeanList.clear();
                List<IssueOutBackUpBean> list = adapter.getList();
                if (list != null) {
                    resultBeanList.addAll(list);
                }
                selectAllButton.setText("取消全选");
                isAllSelected = true;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentConstant.Intent_Request_Backup_To_PartMove_Activity){
            boolean moveResult = data.getBooleanExtra(IntentConstant.Intent_Extra_backup_rework_move_result_boolean,false);
            if (moveResult){
                ToastUtil.showToastShort("撤回备料的移库成功！");
                backUpBean = data.getParcelableExtra(IntentConstant.Intent_Extra_Backup_bean);
//                intent.putExtra(IntentConstant.Intent_Extra_backup_rework_move_ist_id,thePlace != null ? thePlace.getIst_ID() : 0) ;
//                intent.putExtra(IntentConstant.Intent_Extra_backup_rework_move_sub_ist_id,thePlace != null ? thePlace.getSub_Ist_ID() : 0);
                int toIstID = data.getIntExtra(IntentConstant.Intent_Extra_backup_rework_move_ist_id,0);
                int toSubIstID = data.getIntExtra(IntentConstant.Intent_Extra_backup_rework_move_sub_ist_id,0);

                //借用一下这个变量
                backUpBean.setToIst_ID(toIstID);
                backUpBean.setToSub_Ist_ID(toSubIstID);
                // TODO: 6/16/26 不需要再执行了
//                BackupMWReworkAsyncTask task = new BackupMWReworkAsyncTask();
//                task.execute();

                //2026-07-07 john 撤销成功后刷新界面
                refreshAdapter();

            }else{
                ToastUtil.showToastShort("撤回备料的移库失败！");
            }
        }
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

        // codex by john 2026-09-04 16:15 - 一键出库只处理选中的普通条目，明确过滤掉额外条目，避免额外与普通混出
        if (isBatchOutInFlight) {
            ToastUtil.showToastShort("一键出库正在执行，请勿重复提交");
            return;
        }

        List<IssueOutBackUpBean> selectedNormalList = new ArrayList<>();
        for (IssueOutBackUpBean backUpBean : resultBeanList) {
            if (!backUpBean.isExtra()) {
                selectedNormalList.add(backUpBean);
            }
        }
        if (selectedNormalList.size() == 0) {
            ToastUtil.showToastShort("未有待投料出库数据！");
            return;
        }

        String fingerprint = buildBatchFingerprint(selectedNormalList);
        long elapsed = SystemClock.elapsedRealtime() - lastBatchAttemptAt;
        if (successfulBatchFingerprints.contains(fingerprint)) {
            ToastUtil.showToastShort("该批次已出库成功，请勿重复提交");
            return;
        }
        if (fingerprint.equals(lastBatchFingerprint) && !lastBatchSucceeded) {
            if (elapsed < BATCH_DUPLICATE_WINDOW_MS) {
                long remainSeconds = (BATCH_DUPLICATE_WINDOW_MS - elapsed + 999) / 1000;
                ToastUtil.showToastShort("相同参数不可重复提交，请 " + remainSeconds + " 秒后重试");
                return;
            }
        }

        isBatchOutInFlight = true;
        executeAllButton.setEnabled(false);
        new BackupOUtBatchAsyncTask(selectedNormalList, fingerprint).execute();


    }

    private String buildBatchFingerprint(List<IssueOutBackUpBean> beans) {
        List<IssueOutBackUpBean> copy = new ArrayList<>(beans);
        Collections.sort(copy, new Comparator<IssueOutBackUpBean>() {
            @Override
            public int compare(IssueOutBackUpBean a, IssueOutBackUpBean b) {
                int result = Long.compare(a.getItemId(), b.getItemId());
                if (result == 0) result = Long.compare(a.getIV_ID(), b.getIV_ID());
                if (result == 0) result = Long.compare(a.getLotID(), b.getLotID());
                if (result == 0) result = compareStrings(a.getScanX(), b.getScanX());
                if (result == 0) result = Long.compare(a.getToIst_ID(), b.getToIst_ID());
                if (result == 0) result = Long.compare(a.getToSub_Ist_ID(), b.getToSub_Ist_ID());
                return result;
            }
        });

        StringBuilder raw = new StringBuilder();
        raw.append(mpiwc_ID).append('|').append(UserSingleton.get().getHRID()).append('|');
        for (IssueOutBackUpBean bean : copy) {
            raw.append(bean.getItemId()).append(',')
                    .append(bean.getIV_ID()).append(',')
                    .append(bean.getLotID()).append(',')
                    .append(safe(bean.getLotNo())).append(',')
                    .append(bean.getToIst_ID()).append(',')
                    .append(bean.getToSub_Ist_ID()).append(',')
                    .append(bean.getSMLI_ID()).append(',')
                    .append(bean.getSMT_ID()).append(',')
                    .append(bean.getQty()).append(',')
                    .append(safe(bean.getScanX())).append(';');
        }
        return sha256(raw.toString());
    }

    private int compareStrings(String a, String b) {
        String left = safe(a);
        String right = safe(b);
        return left.compareTo(right);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                hex.append(String.format("%02x", b & 0xff));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(value.hashCode());
        } catch (Exception e) {
            return Integer.toHexString(value.hashCode());
        }
    }

    // codex by john 2026-09-04 16:01 - 额外出库只处理选中的额外条目
    private void handleExtraOut() {
        List<IssueOutBackUpBean> selectedExtraList = new ArrayList<>();
        for (IssueOutBackUpBean backUpBean : resultBeanList) {
            if (backUpBean.isExtra()) {
                selectedExtraList.add(backUpBean);
            }
        }
        if (selectedExtraList.size() > 0) {
            BackupExtraOutAsyncTask task = new BackupExtraOutAsyncTask(selectedExtraList);
            task.execute();
        } else {
            ToastUtil.showToastShort("请先选择额外领料备料条目！");
        }
    }

    // codex by john 2026-08-30 11:25 - 额外领料备料条目出库，逐条走额外出库方法
    private class BackupExtraOutAsyncTask extends AsyncTask<String, Void, String> {
        private final List<IssueOutBackUpBean> extraList;
        private ProgressDialog progressDialog;

        BackupExtraOutAsyncTask(List<IssueOutBackUpBean> extraList) {
            this.extraList = extraList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MWBackUpListActivity.this);
            progressDialog.setMessage("额外领料备料出库中，请稍候...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            for (IssueOutBackUpBean backUpBean : extraList) {
                //// TODO: 9/7/26  这里额外出库备料，取的是ist和subist，原值，因要与part2inv库存扣减
//                        backUpBean.getToIst_ID(), backUpBean.getToSub_Ist_ID(),
//                再改成原来的tosist_id,tosub_ist_id 因为默认要移库，因为担心备了直接出了，互相影响
//                WsResult wsResult = WebServiceUtil.op_Commit_MW_Issue_Extra_Item(
                //
                WsResult wsResult = WebServiceUtil.op_Commit_MW_Issue_Extra_Item_ForBackUp(
                        mpiwc_ID, UserSingleton.get().getHRID(),
                        backUpBean.getItemId(), backUpBean.getIV_ID(), backUpBean.getLotID(), backUpBean.getLotNo(),
                        backUpBean.getToIst_ID(), backUpBean.getToSub_Ist_ID(),
                        backUpBean.getSMLI_ID(), 0L, backUpBean.getSMT_ID(),
                        backUpBean.getQty() + "", backUpBean.getRemark(), new Date(), backUpBean.getScanX());
                if (!wsResult.getResult()) {
                    return "失败！" + wsResult.getErrorInfo();
                }
            }
            return "额外领料备料出库成功！请选择剩余普通条目出库。";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            refreshAdapter();
            CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(MWBackUpListActivity.this)
                    .setTitle("额外领料备料出库信息").setMessage(result)
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

//            String sql = "Select MB.Item_id,item_Name,Item_Version.Item_Version,Lot.LotNo ,Item_Storage_Sub.Ist_Name,ScanX,Qty,Bu_ID,HR_Name,Remark,MB.IV_ID,Lot.LotID,MB.Ist_ID,MB.Sub_Ist_ID,SMLI_ID,smmTotalQty,SMT_ID from MW_Issue_Out_BackUp  MB\n" +
//                    "Inner Join Item on Item.item_id  = MB.item_id\n" +
//                    "Inner Join Item_Version on Item_Version.IV_ID = MB.IV_ID\n" +
//                    "Inner Join Lot on Lot.LotID = MB.Lot_ID\n" +
//                    "Inner Join Item_Storage_Sub on Item_Storage_Sub.Sub_Ist_ID = mb.sub_ist_id\n" +
//                    "where MB.MPIWC_ID =  " + mpiwc_ID;

//            上面语句优化，作一合并
            // codex by john 2026-09-04 15:27 - 查询增加 isExtra 字段用于区分额外领料备料；bit 类型不能直接 MAX，需先 CAST 成 int
            String sql = "Select MB.Item_id, Item.item_Name, Item_Version.Item_Version, Lot.LotNo, Item_Storage_Sub.Ist_Name, " +
                    "MB.ScanX,SUM(MB.Qty) AS Qty,Bu_ID, MB.HR_Name, MB.Remark, MB.IV_ID, Lot.LotID, " +
                    "MB.Ist_ID, MB.Sub_Ist_ID, MAX(MB.SMLI_ID) as SMLI_ID, SUM(MB.smmTotalQty) as smmTotalQty, " +
                    "MAX(MB.SMT_ID) as SMT_ID,To_Ist_ID,To_Sub_Ist_ID,MAX(CAST(MB.isExtra AS int)) as Is_Extra \n" +
                    "from MW_Issue_Out_BackUp MB\n" +
                    "Inner Join Item on Item.item_id = MB.item_id\n" +
                    "Inner Join Item_Version on Item_Version.IV_ID = MB.IV_ID\n" +
                    "Inner Join Lot on Lot.LotID = MB.Lot_ID\n" +
                    "Inner Join Item_Storage_Sub on Item_Storage_Sub.Sub_Ist_ID = mb.sub_ist_id\n" +
                    "where MB.MPIWC_ID = " + mpiwc_ID + "  and isnull(hasOut,0) = 0 " +
                    "Group By MB.Item_id, Item.item_Name, Item_Version.Item_Version, Lot.LotNo, " +
                    "Item_Storage_Sub.Ist_Name, MB.ScanX, Bu_ID, MB.HR_Name, MB.Remark, MB.IV_ID, " +
                    "Lot.LotID, MB.Ist_ID, MB.Sub_Ist_ID,To_Ist_ID,To_Sub_Ist_ID";
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
                    // codex by john 2026-09-04 16:01 - 额外条目排在前面
                    Collections.sort(orderBeanList, new Comparator<IssueOutBackUpBean>() {
                        @Override
                        public int compare(IssueOutBackUpBean a, IssueOutBackUpBean b) {
                            boolean ea = a.isExtra();
                            boolean eb = b.isExtra();
                            if (ea == eb) {
                                return 0;
                            }
                            return ea ? -1 : 1;
                        }
                    });

                    adapter.setData(orderBeanList);
                    orderRecyclerView.setVisibility(View.VISIBLE);
                    emptyLayoutView.setVisibility(View.GONE);

                    // codex by john 2026-09-04 16:01 - 有额外条目时显示额外出库，隐藏一键出库和全选；否则恢复一键出库和全选
                    boolean hasExtra = false;
                    for (IssueOutBackUpBean backUpBean : orderBeanList) {
                        if (backUpBean.isExtra()) {
                            hasExtra = true;
                            break;
                        }
                    }
                    if (hasExtra) {
                        extraOutButton.setVisibility(View.VISIBLE);
                        executeAllButton.setVisibility(View.GONE);
                        selectAllButton.setEnabled(false);
                    } else {
                        extraOutButton.setVisibility(View.GONE);
                        executeAllButton.setVisibility(View.VISIBLE);
                        selectAllButton.setEnabled(true);
                    }

                    selectAllButton.setText("全选");
                    isAllSelected = false;
                    resultBeanList.clear();
                } else {
                    getNoData();
                }
            }else{
                getNoData();
            }
        }

    }

    private void getNoData() {
        ToastUtil.showToastShort("没有获取到相关数据！");
        emptyLayoutView.setVisibility(View.VISIBLE);
        orderRecyclerView.setVisibility(View.GONE);
        selectAllButton.setEnabled(false);
        selectAllButton.setText("全选");
        // codex by john 2026-09-04 16:01 - 无数据时隐藏出库按钮
        extraOutButton.setVisibility(View.GONE);
        executeAllButton.setVisibility(View.GONE);
        isAllSelected = false;
        resultBeanList.clear();

    // by codex -- john end 15:24
    }


    private class BackupOUtBatchAsyncTask extends AsyncTask<String, Void, String> {
        // codex by john 2026-09-04 16:15 - 改为构造传入普通条目列表，避免额外/普通混出
        private final List<IssueOutBackUpBean> normalList;
        private final String fingerprint;
        private ProgressDialog progressDialog;

        BackupOUtBatchAsyncTask(List<IssueOutBackUpBean> normalList, String fingerprint) {
            this.normalList = normalList;
            this.fingerprint = fingerprint;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lastBatchAttemptAt = SystemClock.elapsedRealtime();
            executeAllButton.setEnabled(false);
            progressDialog = new ProgressDialog(MWBackUpListActivity.this);
            progressDialog.setMessage("备料一键出库中，请稍候...");
            progressDialog.setCancelable(false);  // 不能点外部取消
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            try {
                // codex by john 2026-09-04 16:15 - 只批量提交传入的普通条目
                WsResult wsResult = commitBackupBatch(normalList);
                if (wsResult == null) {
                    return "失败！网络请求失败，请稍后重试";
                }
                if (wsResult.getResult()) {
                    return "一键出库成功！";
                } else {
                    return "失败！" + wsResult.getErrorInfo();
                }
            } catch (Exception e) {
                return "失败！" + e.getMessage();
            }
        }

        // codex by john 2026-08-28 22:45 - 抽出批量提交方法，供额外/普通两组复用
        private WsResult commitBackupBatch(List<IssueOutBackUpBean> beans) {
            List<MWIssueItemOutParam> paramList = new ArrayList<>();
            for (IssueOutBackUpBean backUpBean : beans) {
                MWIssueItemOutParam param = new MWIssueItemOutParam();
                param.setItem_ID(backUpBean.getItemId());
                param.setIV_ID(backUpBean.getIV_ID());
                param.setLotID(backUpBean.getLotID());
                param.setLotNo(backUpBean.getLotNo());
                param.setIst_ID(backUpBean.getToIst_ID());
                param.setSub_Ist_ID(backUpBean.getToSub_Ist_ID());
                param.setSMLI_ID(backUpBean.getSMLI_ID());
                param.setSMM_ID(0L);
                param.setSMT_ID(backUpBean.getSMT_ID());
                param.setQty(backUpBean.getQty() + "");
                param.setAcDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                param.setScan_Code(backUpBean.getScanX());
                paramList.add(param);
            }

            // 每次启动 GetMWBatchMethodAsyncTask 从服务端拉取最新方法名并缓存到本地。
            // 服务端当前只执行 Batch4，旧方法(Batch2/Batch3)会返回错误；后续新增 Batch5/6 时在此扩展分支。
            String batchMethod = getSharedPreferences(MWPrefKeys.KEY_MW_BATCH_METHOD, MODE_PRIVATE)
                    .getString(MWPrefKeys.KEY_MW_BATCH_METHOD, "op_Commit_MW_New_Issue_Item_Improve_Batch4");
            if ("op_Commit_MW_New_Issue_Item_Improve_Batch4".equals(batchMethod)) {
                return WebServiceUtil.op_Commit_MW_New_Issue_Item_Improve_Batch4(
                        mpiwc_ID, UserSingleton.get().getHRID(), paramList);
            }
            // 非最新(旧 Batch2/Batch3)也统一按最新 Batch4 提交
            return WebServiceUtil.op_Commit_MW_New_Issue_Item_Improve_Batch4(
                    mpiwc_ID, UserSingleton.get().getHRID(), paramList);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            isBatchOutInFlight = false;
            lastBatchFingerprint = fingerprint;
            lastBatchSucceeded = "一键出库成功！".equals(result);
            if (lastBatchSucceeded) {
                successfulBatchFingerprints.add(fingerprint);
            }
            lastBatchAttemptAt = SystemClock.elapsedRealtime();
            executeAllButton.setEnabled(true);
            // 关闭进度框
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(MWBackUpListActivity.this)
                    .setTitle("备料一键出库信息").setMessage(result)
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

    private class BackupMWOutAsyncTask  extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MWBackUpListActivity.this);
            progressDialog.setMessage("备料出库中，请稍候...");
            progressDialog.setCancelable(false);  // 不能点外部取消
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder stringBuilder = new StringBuilder();
            if (resultBeanList.size() > 0){
                for (IssueOutBackUpBean backUpBean : resultBeanList){
//                    WsResult ws_result = WebServiceUtil.op_Commit_MW_Issue_Item(mpiwc_ID, UserSingleton.get().getHRID(),backUpBean.getItemId(),backUpBean.getIV_ID(),backUpBean.getLotID(),
//                            backUpBean.getLotNo() != null ? backUpBean.getLotNo():"",backUpBean.getIst_ID(),backUpBean.getSub_Ist_ID(),
//                            backUpBean.getSMLI_ID(),0L,backUpBean.getSMT_ID(),backUpBean.getQty() + "",
//                            new Date(),backUpBean.getScanX() != null ? backUpBean.getScanX() : "" );
                    //2026-03-11 john 出库时这里用backup的位置，ist_id改为to_ist_id，以及sub
                    WsResult ws_result = WebServiceUtil.op_Commit_MW_Issue_Item(mpiwc_ID, UserSingleton.get().getHRID(),backUpBean.getItemId(),backUpBean.getIV_ID(),backUpBean.getLotID(),
                            backUpBean.getLotNo() != null ? backUpBean.getLotNo():"",backUpBean.getToIst_ID(),backUpBean.getToSub_Ist_ID(),
                            backUpBean.getSMLI_ID(),0L,backUpBean.getSMT_ID(),backUpBean.getQty() + "",
                            new Date(),backUpBean.getScanX() != null ? backUpBean.getScanX() : "" );
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
            // 关闭进度框
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
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

        getBackUpList();

//        if (originalBeanList.size() == 0){
//            orderRecyclerView.setVisibility(View.GONE);
//            emptyLayoutView.setVisibility(View.VISIBLE);
//        }else{
//            adapter.setData(originalBeanList);
//        }
    }

    private class BackupMWReworkAsyncTask  extends AsyncTask<String, Void, String> {
        WsResult ws_result;
        @Override
        protected String doInBackground(String... strings) {

            ws_result = WebServiceUtil.op_Commit_MW_Backup_Rework(mpiwc_ID,
                            backUpBean.getScanX(),backUpBean.getSMLI_ID(),backUpBean.getSMT_ID(),backUpBean.getToIst_ID(),backUpBean.getToSub_Ist_ID());
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            refreshAdapter();
        }
    }

}
