package com.chinashb.www.mobileerp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chinashb.www.mobileerp.adapter.IssueItemContainsBackupAdapter;
import com.chinashb.www.mobileerp.basicobject.PlannInnerDetailContainBackupEntity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.utils.IntentConstant;

import java.util.ArrayList;
import java.util.List;

/***
 * @date 创建时间 3/30/26 10:21 AM
 * @author 作者: liweifeng
 * @description 查看包含备料信息的投料
 */
public class StockIssueBomContainsBackupListActivity extends BaseActivity{

    private RecyclerView issuedItemRecyclerView;

    private IssueItemContainsBackupAdapter issuedItemAdapter;
    private List<PlannInnerDetailContainBackupEntity> IssuedItemList;
    private long mPIWC_ID = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_bom_contains_backup_layout);
        mPIWC_ID = getIntent().getLongExtra(IntentConstant.Intent_Extra_backup_mpiwc_id_for_check_list,0);
        issuedItemRecyclerView = (RecyclerView) findViewById(R.id.rv_backup_issed_items);
        IssuedItemList = new ArrayList<>();
        issuedItemAdapter = new IssueItemContainsBackupAdapter(StockIssueBomContainsBackupListActivity.this,IssuedItemList);
        issuedItemRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview

        issuedItemRecyclerView.setAdapter(issuedItemAdapter);
        AsyncShowIssuedMWContainsBackUpTask task = new AsyncShowIssuedMWContainsBackUpTask();
        task.execute();
    }

    private class AsyncShowIssuedMWContainsBackUpTask extends AsyncTask<String, Void, Void> {
        List<PlannInnerDetailContainBackupEntity> PlannInnerDetailContainBackupEntityList;

        @Override
        protected Void doInBackground(String... params) {
            PlannInnerDetailContainBackupEntityList = WebServiceUtil.opGetMWIssedItemsContainsBackup(mPIWC_ID);

            return null;
        }

        @Override
        protected void onPreExecute() {
            //pbScan.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");
            if (PlannInnerDetailContainBackupEntityList != null) {
                IssuedItemList = PlannInnerDetailContainBackupEntityList;
                issuedItemAdapter = new IssueItemContainsBackupAdapter(StockIssueBomContainsBackupListActivity.this, PlannInnerDetailContainBackupEntityList);
                issuedItemRecyclerView.setLayoutManager(new LinearLayoutManager(StockIssueBomContainsBackupListActivity.this));//这里用线性显示 类似于listview
                issuedItemRecyclerView.setAdapter(issuedItemAdapter);
            }
            //pbScan.setVisibility(View.INVISIBLE);
        }


    }
}
