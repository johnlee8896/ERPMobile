package com.chinashb.www.mobileerp.warehouse;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.ShipWorkActiveAdapter;
import com.chinashb.www.mobileerp.bean.ShipWorkActiveBean;
import com.chinashb.www.mobileerp.funs.ShipWorkApi;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;

import java.util.List;

/**
 * 成品发货管理新 - 发货作业首页
 */
public class ProductShipWorkMainActivity extends BaseActivity implements View.OnClickListener {

    private TextView operatorTextView;
    private TextView statusTextView;
    private Button createButton;
    private Button continueButton;
    private CustomRecyclerView activeRecyclerView;
    private ShipWorkActiveAdapter activeAdapter;

    private OnViewClickListener onActiveClickListener = new OnViewClickListener() {
        @Override
        public <T> void onClickAction(View v, String tag, T t) {
            if (t instanceof ShipWorkActiveBean) {
                ShipWorkActiveBean bean = (ShipWorkActiveBean) t;
                Intent intent = new Intent(ProductShipWorkMainActivity.this, ProductShipWorkDetailActivity.class);
                intent.putExtra(ProductShipWorkDetailActivity.EXTRA_WORK_ID, bean.getWork_ID());
                intent.putExtra(ProductShipWorkDetailActivity.EXTRA_DO_ID, bean.getDO_ID());
                startActivity(intent);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_ship_work_main_layout);

        operatorTextView = findViewById(R.id.product_ship_work_operator_textView);
        statusTextView = findViewById(R.id.product_ship_work_status_textView);
        createButton = findViewById(R.id.product_ship_work_create_button);
        continueButton = findViewById(R.id.product_ship_work_continue_button);
        activeRecyclerView = findViewById(R.id.product_ship_work_active_recyclerView);

        activeAdapter = new ShipWorkActiveAdapter();
        activeAdapter.setOnViewClickListener(onActiveClickListener);
        activeRecyclerView.setAdapter(activeAdapter);

        createButton.setOnClickListener(this);
        continueButton.setOnClickListener(this);

        com.chinashb.www.mobileerp.basicobject.UserInfoEntity userInfo = UserSingleton.get().getUserInfo();
        String company = userInfo == null ? "" : userInfo.getCompany_Name();
        String bu = userInfo == null ? "" : userInfo.getBu_Name();
        operatorTextView.setText("操作员：" + UserSingleton.get().getHRName()
                + "\n公司：" + company + "  BU：" + bu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadActiveWorks();
    }

    @Override
    public void onClick(View v) {
        if (v == createButton) {
            Intent intent = new Intent(ProductShipWorkMainActivity.this, ProductShipWorkCustomerActivity.class);
            startActivity(intent);
        } else if (v == continueButton) {
            loadActiveWorks();
        }
    }

    private void loadActiveWorks() {
        new ListActiveAsyncTask().execute();
    }

    private class ListActiveAsyncTask extends AsyncTask<Void, Void, ShipWorkApi.Result<List<ShipWorkActiveBean>>> {
        @Override
        protected ShipWorkApi.Result<List<ShipWorkActiveBean>> doInBackground(Void... params) {
            return ShipWorkApi.getActiveWorks();
        }

        @Override
        protected void onPostExecute(ShipWorkApi.Result<List<ShipWorkActiveBean>> result) {
            if (result.success) {
                List<ShipWorkActiveBean> list = result.data == null
                        ? new java.util.ArrayList<ShipWorkActiveBean>() : result.data;
                activeAdapter.setData(list);
                statusTextView.setText(list.isEmpty() ? "当前没有执行中的发货作业" : "");
            } else {
                activeAdapter.setData(new java.util.ArrayList<ShipWorkActiveBean>());
                statusTextView.setText(result.errorInfo + "，点击刷新任务重试");
            }
        }
    }
}
