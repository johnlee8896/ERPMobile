package com.chinashb.www.mobileerp.warehouse;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.ShipWorkSimpleAdapter;
import com.chinashb.www.mobileerp.bean.ShipWorkModels.StageAreaSetting;
import com.chinashb.www.mobileerp.funs.ShipWorkApi;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 成品发货 P05A - 设置唯一发货区。
 */
public class ProductShipWorkStageAreaSettingsActivity extends BaseActivity {

    public static final String EXTRA_DO_ID = "extra_do_id";
    public static final String EXTRA_STAGE_AREA_ID = "extra_stage_area_id";

    private long doId;
    private TextView statusTextView;
    private CustomRecyclerView recyclerView;
    private ShipWorkSimpleAdapter<StageAreaSetting> adapter;
    private boolean busy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_ship_work_stage_area_settings_layout);

        doId = getIntent().getLongExtra(EXTRA_DO_ID, 0);
        statusTextView = findViewById(R.id.product_ship_work_stage_settings_status_textView);
        recyclerView = findViewById(R.id.product_ship_work_stage_settings_recyclerView);

        adapter = new ShipWorkSimpleAdapter<>(item -> item.istName
                + "\n仓库 " + item.warehouseName
                + (item.isConfigured ? "\n当前发货区" : ""));
        adapter.setOnItemClickListener(this::saveStageArea);
        recyclerView.setAdapter(adapter);
        loadSettings();
    }

    private void loadSettings() {
        statusTextView.setText("正在读取存储区域");
        new LoadSettingsAsyncTask().execute(doId);
    }

    private void saveStageArea(StageAreaSetting setting) {
        if (busy) {
            return;
        }
        new android.app.AlertDialog.Builder(this)
                .setTitle("设置发货区")
                .setMessage("确认将发货区设置为：\n" + setting.istName + "？")
                .setPositiveButton("确认", (dialog, which) -> submitStageArea(setting))
                .setNegativeButton("取消", null)
                .show();
    }

    private void submitStageArea(StageAreaSetting setting) {
        busy = true;
        new SaveStageAreaAsyncTask().execute(doId, setting.istId, setting.stageAreaId);
    }

    private class LoadSettingsAsyncTask extends AsyncTask<Long, Void, ShipWorkApi.Result<List<StageAreaSetting>>> {
        @Override
        protected ShipWorkApi.Result<List<StageAreaSetting>> doInBackground(Long... params) {
            return ShipWorkApi.getStageAreaSettings(params[0]);
        }

        @Override
        protected void onPostExecute(ShipWorkApi.Result<List<StageAreaSetting>> result) {
            if (result.success) {
                adapter.setData(result.data == null ? new ArrayList<StageAreaSetting>() : result.data);
                statusTextView.setText(adapter.getRealCount() == 0 ? "没有可用存储区域" : "");
            } else {
                adapter.setData(new ArrayList<StageAreaSetting>());
                statusTextView.setText(result.errorInfo);
            }
        }
    }

    private class SaveStageAreaAsyncTask extends AsyncTask<Long, Void, ShipWorkApi.Result<Void>> {
        @Override
        protected ShipWorkApi.Result<Void> doInBackground(Long... params) {
            long stageAreaId = params[2];
            ShipWorkApi.Result<Void> result = ShipWorkApi.setStageArea(params[0], params[1]);
            result.id = stageAreaId;
            return result;
        }

        @Override
        protected void onPostExecute(ShipWorkApi.Result<Void> result) {
            busy = false;
            if (result.success) {
                ToastUtil.showToastShort("发货区设置成功");
                setResult(RESULT_OK, new android.content.Intent()
                        .putExtra(EXTRA_STAGE_AREA_ID, result.id));
                finish();
            } else {
                ToastUtil.showToastShort(result.errorInfo);
            }
        }
    }
}
