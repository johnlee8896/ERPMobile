package com.chinashb.www.mobileerp.widget;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.chinashb.www.mobileerp.PartItemMiddleActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.PartInvQueryAdapter;
import com.chinashb.www.mobileerp.basicobject.PartsEntity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.ScreenUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    private Context context;

    private PartInvQueryAdapter partsAdapter;
    private List<PartsEntity> partsEntityList;//零部件
    private PartsEntity partsEntity;
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

    }

    public void initiateView(int item_id) {
        DialogAsyncQueryProductInv asynTask = new DialogAsyncQueryProductInv();
        asynTask.execute(item_id + "");
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
            if (params.length > 0){
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
//                                context.startActivityForResult(intent, 100);
                                getOwnerActivity().startActivityForResult(intent, 100);
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
}
