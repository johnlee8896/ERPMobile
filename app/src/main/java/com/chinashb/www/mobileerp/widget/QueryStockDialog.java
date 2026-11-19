package com.chinashb.www.mobileerp.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinashb.www.mobileerp.PartItemMiddleActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.ItemUsageLineQueryAdapter;
import com.chinashb.www.mobileerp.adapter.PartInvQueryAdapter;
import com.chinashb.www.mobileerp.basicobject.PartsEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.ItemUsageLineQueryBean;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.JsonUtil;
import com.chinashb.www.mobileerp.utils.ScreenUtil;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


/***
 * @date 创建时间 2021/7/20 9:56
 * @author 作者: xxblwf
 * @description 在投料出库中点击可查看详细库存
 */

public class QueryStockDialog extends BaseDialog {
    @BindView(R.id.dialog_query_inv_recyclerView) RecyclerView recyclerView;
    @BindView(R.id.part_query_data_layout) LinearLayout dataLayout;
    @BindView(R.id.dialog_query_emptyManagerView) EmptyLayoutManageView emptyManagerView;
    @BindView(R.id.dialog_remark_confirm_Button) Button confirmButton;
    @BindView(R.id.dialog_item_usage_line_title_textView) TextView lineUsageTitleTextView;
    @BindView(R.id.dialog_item_usage_line_recyclerView) CustomRecyclerView lineUsageRecyclerView;

    private Context context;

    private PartInvQueryAdapter partsAdapter;
    private List<PartsEntity> partsEntityList;//零部件
    private PartsEntity partsEntity;
    private ItemUsageLineQueryAdapter lineUsageAdapter;
    private long currentItemId;
    private int currentPage = 1;
    private String keyWord = "";

    public QueryStockDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        setContentView(R.layout.dialog_query_stock_layout);
        ButterKnife.bind(this);

        configDialog(Gravity.CENTER);
        setCanceledOnTouchOutside(false);

        partsEntityList = new ArrayList<>();
        partsAdapter = new PartInvQueryAdapter(context, partsEntityList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));//这里用线性显示 类似于listview
        recyclerView.setAdapter(partsAdapter);

        lineUsageAdapter = new ItemUsageLineQueryAdapter();
        lineUsageRecyclerView.setAdapter(lineUsageAdapter);

        confirmButton.setOnClickListener(v->{
            dismiss();
        });

    }

    public void initiateView(int item_id) {
        currentItemId = item_id;
        DialogAsyncQueryProductInv asynTask = new DialogAsyncQueryProductInv();
        asynTask.execute(item_id + "");
        new LineUsageAsyncTask().execute((long) item_id);
    }

    private void configDialog(int gravity) {
        WindowManager.LayoutParams wl = getWindow().getAttributes();
        wl.gravity = gravity;// 设置重力
        if (gravity != Gravity.CENTER) {
            getWindow().setWindowAnimations(R.style.bottomDialogWindowAnim);
        }
        getWindow().setAttributes(wl);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCancelable(true);
        getWindow().setLayout((int) (ScreenUtil.getScreenWidth() * 0.75),
                WindowManager.LayoutParams.WRAP_CONTENT);

    }

    private class DialogAsyncQueryProductInv extends AsyncTask<String, Void, Void> {
        //ArrayList<PartsEntity> us = new ArrayList<PartsEntity>();
        @Override
        protected Void doInBackground(String... params) {
//            String keyWord = filterEditText.getText().toString();
            String keyWord = "";
            if (params.length > 0) {
                keyWord = params[0];
            }
            String js = WebServiceUtil.getQueryInv(UserSingleton.get().getUserInfo().getBu_ID(), 1, keyWord, 1, 20);
            Gson gson = new Gson();
            partsEntityList = gson.fromJson(js, new TypeToken<List<PartsEntity>>() {
            }.getType());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");b
            if (partsEntityList == null || partsEntityList.size() == 0) {
                dataLayout.setVisibility(View.GONE);
                emptyManagerView.setVisibility(View.VISIBLE);
            } else {
                partsAdapter = new PartInvQueryAdapter(context, partsEntityList);
                recyclerView.setAdapter(partsAdapter);
                partsAdapter.setOnItemClickListener((view, position) -> {
                            if (partsEntityList != null) {
                                partsEntity = partsEntityList.get(position);
//                            QueryPartInvItemAsyncTask task = new QueryPartInvItemAsyncTask();
//                            task.execute(selected_item.getItem_ID());
                                Intent intent = new Intent(context, PartItemMiddleActivity.class);
                                intent.putExtra("selected_item", (Serializable) partsEntity);
//                                intent.putExtra("InvQueryMiddleRequestCode", IntentConstant.Intent_Request_Code_Inv_Query_Middle_from_Dialog_To_Activity);
                                intent.putExtra("InvQueryMiddleRequestCode", IntentConstant.Intent_Request_Code_Inv_Query_Middle_from_Activity_To_Activity_From_Issue);
//                                context.startActivityForResult(intent, 100);
                                Activity activity = (Activity) context;
//                                getOwnerActivity().startActivityForResult(intent, IntentConstant.Intent_Request_Code_Inv_Query_Middle_from_Dialog_To_Activity);
                                activity.startActivityForResult(intent, IntentConstant.Intent_Request_Code_Inv_Query_Middle_from_Activity_To_Activity_From_Issue);
                            }
                        }
                );
                dataLayout.setVisibility(View.VISIBLE);
                emptyManagerView.setVisibility(View.GONE);
            }
//            AppUtil.forceHideInputMethod(getOwnerActivity());
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

    private class LineUsageAsyncTask extends AsyncTask<Long, Void, List<ItemUsageLineQueryBean>> {
        @Override
        protected List<ItemUsageLineQueryBean> doInBackground(Long... params) {
            long itemId = params.length > 0 ? params[0] : 0L;
            int buId = UserSingleton.get().getUserInfo().getBu_ID();
            String mpiDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(new Date()) + " 00:00:00.000";
            String sql = String.format(Locale.US,
                    "SELECT CASE WHEN GROUPING(m.wc_id) = 1 THEN N'合计' ELSE w.wc_name END AS 产线 "
                            + ",SUM(CASE WHEN m.PShift_ID = 1 THEN m.MPI_Quantity ELSE 0 END) AS 白班需求数 "
                            + ",SUM(CASE WHEN m.PShift_ID = 2 THEN m.MPI_Quantity ELSE 0 END) AS 夜班需求数 "
                            + ",SUM(m.MPI_Quantity) AS 合计需求数 "
                            + ",COUNT(*) AS 需求计划数 "
                            + "FROM mpi_wc m "
                            + "INNER JOIN abom b ON b.abv_id = m.abv_id AND b.item_id = %d "
                            + "LEFT JOIN p_wc w ON w.wc_id = m.wc_id "
                            + "WHERE m.bu_id = %d AND m.actived = 1 "
                            + "AND m.MPI_Date = '%s' "
                            + "GROUP BY GROUPING SETS ((m.wc_id, w.wc_name),()) "
                            + "ORDER BY GROUPING(m.wc_id) DESC, m.wc_id",
                    itemId, buId, mpiDate);
            try {
                WsResult result = WebServiceUtil.getDataTable(sql);
                if (result == null || !result.getResult()) {
                    return null;
                }
                Type listType = new TypeToken<List<ItemUsageLineQueryBean>>() {
                }.getType();
                return JsonUtil.parseJsonToObject(result.getErrorInfo(), listType);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<ItemUsageLineQueryBean> list) {
            if (list == null || list.isEmpty()) {
                lineUsageAdapter.setData(new ArrayList<ItemUsageLineQueryBean>());
                lineUsageTitleTextView.setText("该物料线别投料明细（暂无数据）");
            } else {
                lineUsageAdapter.setData(list);
                lineUsageTitleTextView.setText("该物料线别投料明细");
            }
        }
    }
}
