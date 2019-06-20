package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.basicobject.UserInfoEntity;
import com.chinashb.www.mobileerp.commonactivity.NetWorkReceiver;
import com.chinashb.www.mobileerp.commonactivity.SelectItemActivity;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.talk.MessageManageActivity;
import com.chinashb.www.mobileerp.task.TasksActivity;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.warehouse.StockMainActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class MobileMainActivity extends AppCompatActivity {

    //    public static UserInfoEntity userInfo;
    public static Bitmap pictureBitmap;
    public static HashMap<Integer, Bitmap> userPictureMap = new HashMap<Integer, Bitmap>();

    private Button btnWarehouseMain;
    private Button btnConversation;
    private Button btnTask;
    //    private Button scanHRButton;
//    private Button loginButton;
    private Button switchBUButton;
    private TextView userNameTextView;
    private ImageView avatarImageView;
    private RadioGroup netRadioGroup;
    private RadioButton intranetRadioButton;
    private RadioButton internetRadioButton;
    private ProgressBar scanProgressBar;

    private NetWorkReceiver netWorkReceiver;
//    private int mobile_erp_ver_id = 1;
//    private int query_erp_id = 0;
//    private boolean versionOk = false;
//    private boolean isNetReady = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerNetReceiver();
        setContentView(R.layout.activity_mobile_main);

        getViewFromXML();

        CommonUtil.initNetWorkLink(this);
        if (WebServiceUtil.Current_Net_Link.equals("Internet")) {
            internetRadioButton.setChecked(true);
        }

        setHomeButton();

//        //最新版本检测
//        checkErpVersionOk();

        setViewListeners();


        btnWarehouseMain = (Button) findViewById(R.id.main_warehouse_manage_button);
        btnConversation = (Button) findViewById(R.id.main_message_button);
        btnTask = (Button) findViewById(R.id.main_task_manage_button);

//        if (savedInstanceState != null) {
//            userInfo = (UserInfoEntity) savedInstanceState.getSerializable("userInfo");
//        }
        int HRID = getIntent().getIntExtra(IntentConstant.Intent_Extra_hr_id, -1);
        if (HRID > 0) {
            GetHrNameAsyncTask task = new GetHrNameAsyncTask();
            task.execute(String.valueOf(HRID));
        }

        btnWarehouseMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!UserSingleton.get().hasLogin()) {
                    ToastUtil.showToastLong("请先登录");
                    return;
                }

                Intent intent = new Intent(MobileMainActivity.this, StockMainActivity.class);
                startActivity(intent);
            }
        });

        btnConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!UserSingleton.get().hasLogin()) {
                    ToastUtil.showToastLong("请先登录");
                    return;
                }

                Intent intent = new Intent(MobileMainActivity.this, MessageManageActivity.class);
                startActivity(intent);
            }
        });

        btnTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!UserSingleton.get().hasLogin()) {
                    ToastUtil.showToastLong("请先登录");
                    return;
                }

                Intent intent = new Intent(MobileMainActivity.this, TasksActivity.class);
                startActivity(intent);
            }
        });


    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        outState.putSerializable("userInfo", (Serializable) userInfo);
//
//    }

//    protected void checkErpVersionOk() {
//        String sql = "select top 1 VerID,Version,Convert(nvarchar(100),UpdateDate,23) As UpdateDate, Des " +
//                " from ERP_Mobile_Ver Where RequireUpdate=1 Order By VerID Desc";
//
//        QueryAsyncTask query = new QueryAsyncTask();
//        query.execute(sql);
//        query.setLoadDataCompleteListener(new isLoadDataListener() {
//            @Override
//            public void loadComplete(List<JsonObject> result) {
//                if (result != null && result.size() == 1) {
//                    //返回结果，说明网络访问没有问题
//                    isNetReady = true;
//
//                    JsonObject o = result.get(0);
//                    Integer ErpVerID = o.get("VerID").getAsInt();
//                    String Version = o.get("Version").getAsString();
//                    String UpdateDate = o.get("UpdateDate").getAsString();
//                    String Des = o.get("Des").getAsString();
//
//                    query_erp_id = ErpVerID;
//                    if (mobile_erp_ver_id >= ErpVerID) {
//                        versionOk = true;
//
//                        debugLogin();
//                    } else {
//                        String newVerWarning = "当前App 版本已经过时。\n" +
//                                "系统已于" + UpdateDate + "升级到版本" + Version + "\n" +
//                                "版本描述：\n" +
//                                Des;
//
//                        CommonUtil.ShowToast(MobileMainActivity.this, newVerWarning, R.mipmap.warning, Toast.LENGTH_SHORT);
//                    }
//                } else {
//
//                }
//            }
//        });
//
//    }

//    protected Boolean isUserLogin() {
//        if (userInfo != null) {
//            if (userInfo.getHR_ID() > 0) {
//                return true;
//            } else {
//                CommonUtil.ShowToast(this, "请先登录", R.mipmap.warning, Toast.LENGTH_SHORT);
//                return false;
//            }
//
//        } else {
//            CommonUtil.ShowToast(this, "请先登录", R.mipmap.warning, Toast.LENGTH_SHORT);
//            return false;
//        }
//
//    }

    protected void getViewFromXML() {

        avatarImageView = (ImageView) findViewById(R.id.main_avatar_imageView);
//        scanHRButton = (Button) findViewById(R.id.main_scan_hr_card_button);
//        loginButton = (Button) findViewById(R.id.main_login_button);
        switchBUButton = (Button) findViewById(R.id.main_select_bu_button);
        userNameTextView = (TextView) findViewById(R.id.tv_current_user_name);

        netRadioGroup = (RadioGroup) findViewById(R.id.rg_net_link);
        intranetRadioButton = (RadioButton) findViewById(R.id.main_intranet_radioButton);
        internetRadioButton = (RadioButton) findViewById(R.id.main_internet_radioButton);

        scanProgressBar = (ProgressBar) findViewById(R.id.main_scan_progressBar);
    }

    protected void setViewListeners() {
        netRadioGroup.setOnCheckedChangeListener(new NetOnCheckedChangeListener());
//
//        scanHRButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (versionOk) {
//                    startScanHR();
//                } else {
//                    if (!isNetReady) {
//                        CommonUtil.ShowToast(MobileMainActivity.this, "无法连接服务器", R.mipmap.warning, Toast.LENGTH_SHORT);
//                    } else {
//                        CommonUtil.ShowToast(MobileMainActivity.this, "请升级App", R.mipmap.warning, Toast.LENGTH_SHORT);
//                    }
//
//                }
//
//            }
//        });

//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (versionOk) {
//                    Intent intent = new Intent(MobileMainActivity.this, LoginActivity.class);
//                    startActivityForResult(intent, 100);
//                } else {
//                    if (!isNetReady) {
//                        CommonUtil.ShowToast(MobileMainActivity.this, "无法连接服务器", R.mipmap.warning, Toast.LENGTH_SHORT);
//                    } else {
//                        CommonUtil.ShowToast(MobileMainActivity.this, "请升级App", R.mipmap.warning, Toast.LENGTH_SHORT);
//                    }
//
//                }
//
//            }
//        });

        switchBUButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!UserSingleton.get().hasLogin()) {

                    ToastUtil.showToastLong("请先登录");
                    return;
                }


                Intent intent = new Intent(MobileMainActivity.this, SelectItemActivity.class);
                String sql = getSqlBu();

                List<Integer> ColWidth = new ArrayList<Integer>(Arrays.asList(50, 200, 100, 50, 180));
                List<String> ColCaption = new ArrayList<String>(Arrays.asList("Company_ID", "公司", "Brief", "Bu_ID", "车间"));
                List<String> HiddenCol = new ArrayList<String>(Arrays.asList("Company_ID", "Brief", "Bu_ID"));

                String Title = "选择车间";
                intent.putExtra("Title", Title);
                intent.putExtra("SQL", sql);
                intent.putExtra("ColWidth", (Serializable) ColWidth);
                intent.putExtra("ColCaption", (Serializable) ColCaption);
                intent.putExtra("HiddenCol", (Serializable) HiddenCol);
                startActivityForResult(intent, 200);
            }
        });
    }

    private String getSqlBu() {
        String sql = "select distinct company.Company_ID, company.Company_Chinese_Name,Company.Brief,bu.bu_id,bu.bu_name " +
                "from hr_adm " +
                "inner join bu_dep on hr_adm.Department_ID = bu_dep.Dep_ID " +
                "inner join bu on bu.bu_id=bu_dep.Bu_ID " +
                "inner join company on company.company_id=bu.Company_ID " +
                " where hr_adm.HR_ID=" + UserSingleton.get().getHRID() + " and hr_adm.deleted=0 " +
                " and bu.enabled=1 and bu.is_obsolete=0 " +
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

//    protected void debugLogin() {
//
//        if (BuildConfig.DEBUG) {
//            userInfo = new UserInfoEntity();
//            userInfo.setHR_ID(249);
//            userInfo.setBu_ID(1);
//            userInfo.setBu_Name("座椅电机");
//            userInfo.setCompany_ID(1);
//            userInfo.setCompany_Name("上海胜华波汽车电器有限公司");
//
//            GetHrNameAsyncTask task = new GetHrNameAsyncTask();
//            task.execute();
//        }
//    }

//    protected void startScanHR() {
//
//        IntentIntegrator scan = new IntentIntegrator(MobileMainActivity.this);
//        //scan.setOrientationLocked(false);
//        scan.setBeepEnabled(true);
//        scan.setCaptureActivity(CustomScannerActivity.class);
//
//        scan.setPrompt("扫描员工二维码");
//        startActivityForResult(scan.createScanIntent(), 0x0000c0de);
//
//        //new IntentIntegrator( MobileMainActivity.this).initiateScan();
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setHomeButton() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        //登录完成
//        if (requestCode == 100 && resultCode == 1) {
//            //resultCode=1,登录成功
//            //执行getHR_Name，获得其他信息
//            userInfo = new UserInfoEntity();
//            userInfo.setHR_ID(UserInfoEntity.ID);
//
//            GetHrNameAsyncTask task = new GetHrNameAsyncTask();
//            task.execute();
//
//            return;
//        }

        //切换车间
        if (requestCode == 200 && resultCode == 1) {

            ActivityResultSelectBu(data);
            return;
        }

        if (result != null) {
            if (result.getContents() == null) {
                //new IntentIntegrator(StockMainActivity.this).initiateScan();
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
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onPause() {
        if (netWorkReceiver == null) {
            unregisterReceiver(netWorkReceiver);
            System.out.println("注销");
        }

        super.onPause();
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
            HashMap<String, String> SelectBu = (HashMap<String, String>) data.getSerializableExtra("SelectItem");
            if (SelectBu != null) {
                UserInfoEntity userInfoEntity = new UserInfoEntity();
                userInfoEntity.setBu_ID(Integer.valueOf(SelectBu.get("bu_id")));
                userInfoEntity.setBu_Name(SelectBu.get("bu_name"));
                userInfoEntity.setCompany_ID(Integer.valueOf(SelectBu.get("Company_ID")));
                userInfoEntity.setCompany_Name(SelectBu.get("Company_Chinese_Name"));
                //todo 此处应用update
                userInfoEntity.setHR_ID(UserSingleton.get().getHRID());

                UserSingleton.get().setUserInfo(userInfoEntity);

//                userNameTextView.setText(userInfoEntity.getBu_Name() + ":" + userInfoEntity.getHR_Name());
                userNameTextView.setText(userInfoEntity.getBu_Name() + ":" + UserSingleton.get().getHRName());
            }
        }
    }


    protected void registerNetReceiver() {
        if (netWorkReceiver == null) {
            netWorkReceiver = new NetWorkReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkReceiver, filter);
    }

    private class NetOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedID) {
            if (intranetRadioButton.getId() == checkedID) {
                WebServiceUtil.set_net_link_to_intranet();
            }
            if (internetRadioButton.getId() == checkedID) {
                WebServiceUtil.set_net_link_to_internet();
            }
        }


    }

    private class GetHrNameAsyncTask extends AsyncTask<String, Void, Void> {
        //Image hr_photo;

        @Override
        protected Void doInBackground(String... params) {

            int hrId = Integer.parseInt(params[0]);
//            userInfo = WebServiceUtil.getHRName_Bu(userInfo.getHR_ID());
            UserInfoEntity userInfoEntity = WebServiceUtil.getHRName_Bu(hrId);
            UserSingleton.get().setUserInfo(userInfoEntity);


            if (userInfoEntity != null) {
                pictureBitmap = CommonUtil.getUserPic(MobileMainActivity.this, userPictureMap, userInfoEntity.getHR_ID());
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            scanProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            if (UserSingleton.get().getUserInfo() != null) {
                userNameTextView.setText(UserSingleton.get().getUserInfo().getBu_Name() + ":" + UserSingleton.get().getHRName());
                if (pictureBitmap != null) {
                    avatarImageView.setImageBitmap(pictureBitmap);
                }
            } else {
                Toast.makeText(MobileMainActivity.this, "无法访问服务器，请检查网络连接是否正常", Toast.LENGTH_LONG).show();
            }

            scanProgressBar.setVisibility(View.GONE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

}

