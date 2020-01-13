package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.chinashb.www.mobileerp.adapter.ProductWCListAdapter;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.entity.WcIdNameEntity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2019/12/18 16:54
 * @author 作者: xxblwf
 * @description 显示产线
 */

public class SelectProductWCListActivity extends BaseActivity {
    @BindView(R.id.product_wc_recyclerView) CustomRecyclerView recyclerView;
    @BindView(R.id.wc_list_title_textView) TextView titleTextView;

    private ProductWCListAdapter adapter;
    private ArrayList<WcIdNameEntity> wcIdNameEntityList;
    private int work_line_from = 0;

    private OnViewClickListener onViewClickListener = new OnViewClickListener() {
        @Override public <T> void onClickAction(View v, String tag, T t) {
            if (t != null) {
                if (t instanceof WcIdNameEntity) {
                    Intent intent = new Intent(SelectProductWCListActivity.this, ProductInNonTrayActivity.class);
                    intent.putExtra(IntentConstant.Intent_product_wc_id_name_entity, (WcIdNameEntity) t);
                    setResult(200, intent);
                    finish();
                }
            }
        }
    };

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_wc_list_layout);
        ButterKnife.bind(this);

        work_line_from = getIntent().getIntExtra(IntentConstant.Intent_Extra_work_line_from, IntentConstant.Intent_Extra_work_line_from_product);
        if (work_line_from == IntentConstant.Intent_Extra_work_line_from_part) {
            //// TODO: 2020/1/10 可以更加完善，如雨刮生产线，及代码与下面的判断合并
            titleTextView.setText("零件生产线");
        }
        adapter = new ProductWCListAdapter(onViewClickListener);
        recyclerView.setAdapter(adapter);

        GetWCListsAsyncTask task = new GetWCListsAsyncTask();
        task.execute();
    }

    private class GetWCListsAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            //// TODO: 2019/12/19 这里去掉type这个字段
            String sql = null;
            if (work_line_from == 0 || work_line_from == IntentConstant.Intent_Extra_work_line_from_product) {
                sql = String.format(" Select WC_Id,WC_Name  From P_WC Where Bu_ID=%s", UserSingleton.get().getUserInfo().getBu_ID());
            } else if (work_line_from == IntentConstant.Intent_Extra_work_line_from_part) {
                //// TODO: 2020/1/9 为便于开发，目前 只支持上海雨刮和安徽雨刮 ，之后的可以扩展
                if (UserSingleton.get().getUserInfo().getBu_ID() == 3 || UserSingleton.get().getUserInfo().getBu_ID() == 54) {
                    //注意这里共用entity，与pc上的wc_id不一样
                    sql = " Select WC_Id,WC_Name  From P_WC Where (Bu_ID=54 Or Bu_ID=3)";
                }
            }

            WsResult result = WebServiceUtil.getDataTable(sql);
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                ArrayList<WcIdNameEntity> us;
                Gson gson = new Gson();
                us = gson.fromJson(jsonData, new TypeToken<List<WcIdNameEntity>>() {
                }.getType());
                wcIdNameEntityList = us;
//                adapter.setData(wcIdNameEntityList);

            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            adapter.setData(wcIdNameEntityList);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
}
