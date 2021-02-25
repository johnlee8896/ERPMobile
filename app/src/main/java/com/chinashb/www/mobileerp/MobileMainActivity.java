package com.chinashb.www.mobileerp;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.basicobject.UserInfoEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.BUItemBean;
import com.chinashb.www.mobileerp.commonactivity.CommonSelectItemActivity;
import com.chinashb.www.mobileerp.commonactivity.NetWorkReceiver;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.talk.MessageManageActivity;
import com.chinashb.www.mobileerp.task.TasksActivity;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.StringConstantUtil;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.warehouse.StockPartMainActivity;
import com.chinashb.www.mobileerp.widget.CommAlertDialog;
import com.chinashb.www.mobileerp.widget.OnDialogViewClickListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MobileMainActivity extends BaseActivity implements View.OnClickListener {

    private TextView warehousePartTextView;
    private TextView warehouseProductTextView;
    private TextView conversationTextView;
    private TextView foodOrderTextView;
    private TextView wageQueryTextView;
    private TextView attendanceTextView;
    private TextView taskTextView;
    private TextView planTextView;
    private TextView switchBUTextView;
    private TextView userNameTextView;
    private ImageView avatarImageView;

    private NetWorkReceiver netWorkReceiver;
    private boolean isFromNamePwdCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerNetReceiver();
        setContentView(R.layout.activity_mobile_main);
        getViewFromXML();

        setViewListeners();
        int HRID = getIntent().getIntExtra(IntentConstant.Intent_Extra_hr_id, -1);
        isFromNamePwdCheck = getIntent().getBooleanExtra(IntentConstant.Intent_Extra_from_name_pwd, false);
        if (isFromNamePwdCheck) {
            if (HRID > 0) {
                GetHrNameAsyncTask task = new GetHrNameAsyncTask();
                task.execute(String.valueOf(HRID));
            }
        } else {
            userNameTextView.setText((UserSingleton.get().getUserInfo() != null ? UserSingleton.get().getUserInfo().getBu_Name() : ""
            ) + ":" + UserSingleton.get().getHRName());
        }

    }

    protected void getViewFromXML() {
        avatarImageView = (ImageView) findViewById(R.id.main_avatar_imageView);
        switchBUTextView = (TextView) findViewById(R.id.main_select_bu_button);
        userNameTextView = (TextView) findViewById(R.id.tv_current_user_name);

        warehousePartTextView = (TextView) findViewById(R.id.main_warehouse_manage_part_button);
        warehouseProductTextView = findViewById(R.id.main_warehouse_manage__product_button);
        conversationTextView = (TextView) findViewById(R.id.main_message_button);
        taskTextView = (TextView) findViewById(R.id.main_task_manage_button);
        planTextView = findViewById(R.id.main_plan_button);
        foodOrderTextView = findViewById(R.id.main_food_order_button);
        wageQueryTextView = findViewById(R.id.main_wage_query_button);
        attendanceTextView = findViewById(R.id.main_attendance_button);
    }

    protected void setViewListeners() {
        switchBUTextView.setOnClickListener(this);
        planTextView.setOnClickListener(this);

        warehousePartTextView.setOnClickListener(this);
        warehouseProductTextView.setOnClickListener(this);
        conversationTextView.setOnClickListener(this);
        taskTextView.setOnClickListener(this);
        foodOrderTextView.setOnClickListener(this);
        wageQueryTextView.setOnClickListener(this);
        attendanceTextView.setOnClickListener(this);
    }

    private String getSqlBu() {
        String sql = "select distinct company.Company_ID, company.Company_Chinese_Name,Company.Brief,bu.bu_id,bu.bu_name " +
                "from hr_adm " +
                "inner join bu_dep on hr_adm.Department_ID = bu_dep.Dep_ID " +
                "inner join bu on bu.bu_id=bu_dep.Bu_ID " +
                "inner join company on company.company_id=bu.Company_ID " +
                " where hr_adm.HR_ID=" + UserSingleton.get().getHRID() + " and hr_adm.deleted=0 " +
                " and bu.enabled=1 and bu.is_obsolete=0 and bu.is_virtual = 0 " +
                " Union " +
                "select distinct company.Company_ID, company.Company_Chinese_Name,Company.Brief,bu.bu_id,bu.bu_name " +
                "from hr_adm " +
                "inner join Dep_Service_Company on hr_adm.Department_ID = Dep_Service_Company.Dep_ID " +
                "inner join bu on bu.company_id=Dep_Service_Company.company_id " +
                "inner join company on company.company_id=Dep_Service_Company.Company_ID " +
                " where hr_adm.HR_ID=" + UserSingleton.get().getHRID() + " and hr_adm.deleted=0 " +
                " and bu.enabled=1 and bu.is_obsolete=0 ";
        sql = "Select * From (" + sql + ") As U " +
                "Order by Company_ID, BU_ID ";
        return sql;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        //切换车间
        if (requestCode == 200 && resultCode == 1) {
            ActivityResultSelectBu(data);
            return;
        }
        if (result != null) {
            if (result.getContents() == null) {
                //new IntentIntegrator(StockPartMainActivity.this).initiateScan();
                //st/////////////art_scan_HR();
            } else {
                Toast.makeText(this, " scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                String[] qrContent;
                UserInfoEntity userInfo = new UserInfoEntity();
                if (result.getContents().contains("/")) {
                    qrContent = result.getContents().split("/");
                    if (qrContent.length >= 4) {
                        userInfo.setHR_ID(Integer.parseInt(qrContent[2]));
                        userInfo.setHrNum(qrContent[4]);
                        GetHrNameAsyncTask task = new GetHrNameAsyncTask();
                        task.execute();
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (netWorkReceiver == null) {
            unregisterReceiver(netWorkReceiver);
            System.out.println("注销");
        }
    }

    @Override
    protected void onResume() {
        //设置为竖屏幕
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }

    protected void ActivityResultSelectBu(Intent data) {
        if (data != null) {
            BUItemBean buItemBean = data.getParcelableExtra("SelectItem");
            if (buItemBean != null) {
                UserInfoEntity userInfoEntity = new UserInfoEntity();
                userInfoEntity.setBu_ID(buItemBean.getBUId());
                userInfoEntity.setBu_Name(buItemBean.getBUName());
                userInfoEntity.setCompany_ID(buItemBean.getCompanyID());
                userInfoEntity.setCompany_Name(buItemBean.getCompanyChineseName());
                //todo 此处应用update
                userInfoEntity.setHR_ID(UserSingleton.get().getHRID());
                UserSingleton.get().setUserInfo(userInfoEntity);
                userNameTextView.setText(userInfoEntity.getBu_Name() + ":" + UserSingleton.get().getHRName());

                UserSingleton.get().setHasSwitchedBu(true);
            }
        }
    }

    private void registerNetReceiver() {
        if (netWorkReceiver == null) {
            netWorkReceiver = new NetWorkReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkReceiver, filter);
    }

    @Override
    public void onClick(View view) {
        if (view == conversationTextView) {
            if (!UserSingleton.get().hasLogin()) {
                ToastUtil.showToastLong("请先登录");
                return;
            }
            Intent intent = new Intent(MobileMainActivity.this, MessageManageActivity.class);
            startActivity(intent);
            MobclickAgent.onEvent(this, StringConstantUtil.Umeng_event_activity_message);
        } else if (view == taskTextView) {
            if (!UserSingleton.get().hasLogin()) {
                ToastUtil.showToastLong("请先登录");
                return;
            }
            Intent intent = new Intent(MobileMainActivity.this, TasksActivity.class);
            MobclickAgent.onEvent(this, StringConstantUtil.Umeng_event_activity_task);
            startActivity(intent);
        } else if (view == warehousePartTextView) {
            if (!UserSingleton.get().hasLogin()) {
                ToastUtil.showToastLong("请先登录");
                return;
            }

            //判断是否切换过车间，如果没有，给个提示，以免弄错
            if (!UserSingleton.get().hasSwitchedBu) {
                CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(MobileMainActivity.this)
                        .setTitle("提示").setMessage("进入仓库操作需要相对应的车间，您是否需要切换车间？")
                        .setLeftText("确定").setRightText("取消");


                builder.setOnViewClickListener(new OnDialogViewClickListener() {
                    @Override
                    public void onViewClick(Dialog dialog, View v, int tag) {
                        switch (tag) {
                            case CommAlertDialog.TAG_CLICK_LEFT:
                                jumpToSwitchBuActivity();
                                dialog.dismiss();
                                break;
                            case CommAlertDialog.TAG_CLICK_RIGHT:
                                jumpToStockPartActivity();
                                dialog.dismiss();
                                break;
                        }
                    }
                });
                builder.create().show();
            } else {
                jumpToStockPartActivity();
            }

        } else if (view == warehouseProductTextView) {
            if (!UserSingleton.get().hasLogin()) {
                ToastUtil.showToastLong("请先登录");
                return;
            }
            Intent intent = new Intent(MobileMainActivity.this, StockProductMainActivity.class);
            startActivity(intent);
            MobclickAgent.onEvent(this, StringConstantUtil.Umeng_event_activity_product_management);
        } else if (view == switchBUTextView) {
            if (!UserSingleton.get().hasLogin()) {
                ToastUtil.showToastLong("请先登录");
                return;
            }
//            Intent intent = new Intent(MobileMainActivity.this, SelectItemActivity.class);
            jumpToSwitchBuActivity();
        } else if (view == planTextView) {
            if (!UserSingleton.get().hasLogin()) {
                ToastUtil.showToastLong("请先登录");
                return;
            }
            Intent intent = new Intent(this, PlanManagerActivity.class);
            startActivity(intent);
            MobclickAgent.onEvent(this, StringConstantUtil.Umeng_event_activity_plan);
        } else if (view == foodOrderTextView) {
//            Intent intent = new Intent(this,FoodOrderActivity.class);
//            startActivity(intent);
            new GetTestService2AsyncTask().execute();
        }else if (view == attendanceTextView){
            Intent intent = new Intent(this,AttendanceActivity.class);
            startActivity(intent);
        }else if (view == wageQueryTextView){
            Intent intent = new Intent(this,WageQueryActivity.class);
            startActivity(intent);
        }
    }

    private void jumpToStockPartActivity() {
        Intent intent = new Intent(MobileMainActivity.this, StockPartMainActivity.class);
        startActivity(intent);
        MobclickAgent.onEvent(this, StringConstantUtil.Umeng_event_activity_part_management);
    }

    private void jumpToSwitchBuActivity() {
        Intent intent = new Intent(MobileMainActivity.this, CommonSelectItemActivity.class);
        String sql = getSqlBu();

        List<Integer> ColWidth = new ArrayList<Integer>(Arrays.asList(50, 200, 100, 50, 180));
        List<String> ColCaption = new ArrayList<String>(Arrays.asList("Company_ID", "公司", "Brief", "Bu_ID", "车间"));
        List<String> HiddenCol = new ArrayList<String>(Arrays.asList("Company_ID", "Brief", "Bu_ID"));

        String Title = "选择车间";
        intent.putExtra("Title", Title);
        intent.putExtra("SQL", sql);
        intent.putExtra("ColWidthList", (Serializable) ColWidth);
        intent.putExtra("ColCaptionList", (Serializable) ColCaption);
        intent.putExtra("hiddenColList", (Serializable) HiddenCol);

        intent.putExtra(IntentConstant.Intent_Extra_to_select_search_from_postition, IntentConstant.Select_Search_From_Select_BU);
        startActivityForResult(intent, 200);
        MobclickAgent.onEvent(this, StringConstantUtil.Umeng_event_activity_switch_bu);
    }


    @Override
    public void onBackPressed() {
        CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(MobileMainActivity.this)
                .setTitle("").setMessage("确定要退出吗？")
                .setLeftText("确定").setRightText("取消");


        builder.setOnViewClickListener(new OnDialogViewClickListener() {
            @Override
            public void onViewClick(Dialog dialog, View v, int tag) {
                switch (tag) {
                    case CommAlertDialog.TAG_CLICK_LEFT:
//                        MobileMainActivity.this.onBackPressed();
                        dialog.dismiss();
                        finish();
                        break;
                    case CommAlertDialog.TAG_CLICK_RIGHT:
                        dialog.dismiss();
                        break;
                }
            }
        });
        builder.create().show();

    }

    private class GetHrNameAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            int hrId = Integer.parseInt(params[0]);
            UserInfoEntity userInfoEntity = WebServiceUtil.getHRName_Bu(hrId);
            UserSingleton.get().setUserInfo(userInfoEntity);
            if (userInfoEntity != null) {
                CommonUtil.pictureBitmap = CommonUtil.getUserPic(MobileMainActivity.this, CommonUtil.userPictureMap, userInfoEntity.getHR_ID());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
//            scanProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            if (UserSingleton.get().getUserInfo() != null) {
                userNameTextView.setText(UserSingleton.get().getUserInfo().getBu_Name() + ":" + UserSingleton.get().getHRName());
                if (CommonUtil.pictureBitmap != null) {
                    avatarImageView.setImageBitmap(CommonUtil.pictureBitmap);
                }
            } else {
                Toast.makeText(MobileMainActivity.this, "无法访问服务器，请检查网络连接是否正常", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }


    private class GetTestService2AsyncTask extends AsyncTask<String, Void, Void> {
        private WsResult wsResult;

        @Override
        protected Void doInBackground(String... params) {
            wsResult = WebServiceUtil.getAnotherTest("hello");
//            wsResult = WebServiceUtil.getTryLogin("杨梅", "881102");
            return null;
        }

        @Override
        protected void onPreExecute() {
//            scanProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            if (wsResult != null && wsResult.getResult()) {
                ToastUtil.showToastShort(wsResult.getErrorInfo());
            } else {
                ToastUtil.showToastShort("err ");
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

}

