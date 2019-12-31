package com.chinashb.www.mobileerp.commonactivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.MobileMainActivity;
import com.chinashb.www.mobileerp.OnQRScanListenerImpl;
import com.chinashb.www.mobileerp.QRCodeScanActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.QueryAsyncTask;
import com.chinashb.www.mobileerp.basicobject.UserInfoEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.entity.MESDataEntity;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.OnLoadDataListener;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.SPSingleton;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.upgrade.APPUpgradeManager;
import com.chinashb.www.mobileerp.utils.FileUtil;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.JsonUtil;
import com.chinashb.www.mobileerp.utils.SPDefine;
import com.chinashb.www.mobileerp.utils.StringConstantUtil;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CommProgressDialog;
import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import static com.chinashb.www.mobileerp.funs.CommonUtil.userPictureMap;

public class LoginActivity extends BaseActivity {

    private EditText nameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button scanHRButton;
//    private ProgressBar progressBar;

    //todo
    private int mobile_erp_ver_id = 1;
    private boolean versionOk = false;
    private boolean isNetReady = false;

    private RadioGroup netRadioGroup;
    private RadioButton intranetRadioButton;
    private RadioButton internetRadioButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);

        nameEditText = (EditText) findViewById(R.id.et_login_name);
        passwordEditText = (EditText) findViewById(R.id.et_login_password);
        loginButton = (Button) findViewById(R.id.name_sign_in_button);
        scanHRButton = (Button) findViewById(R.id.main_scan_hr_card_button);

        netRadioGroup = (RadioGroup) findViewById(R.id.rg_net_link);
        intranetRadioButton = (RadioButton) findViewById(R.id.main_intranet_radioButton);
        internetRadioButton = (RadioButton) findViewById(R.id.main_internet_radioButton);

        CommonUtil.initNetWorkLink(this);
        if (WebServiceUtil.Current_Net_Link.equals("Internet")) {
            internetRadioButton.setChecked(true);
        }

//        progressBar = (ProgressBar) findViewById(R.id.login_progress);
        //最新版本检测
        checkErpVersionOk();
        CommonUtil.initNetWorkLink(this);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                checkNamePwd();
               GetMesDataAsyncTask asyncTask = new GetMesDataAsyncTask();
               asyncTask.execute();
            }
        });

        netRadioGroup.setOnCheckedChangeListener(new NetOnCheckedChangeListener());

        scanHRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (versionOk) {
//                    startScanHR();
//                } else {
//                    if (!isNetReady) {
//                        CommonUtil.ShowToast(LoginActivity.this, "无法连接服务器", R.mipmap.warning, Toast.LENGTH_SHORT);
//                    } else {
//                        CommonUtil.ShowToast(LoginActivity.this, "请升级App", R.mipmap.warning, Toast.LENGTH_SHORT);
//                    }
//
//                }


                if (!isNetReady) {
                    CommonUtil.ShowToast(LoginActivity.this, "无法连接服务器", R.mipmap.warning, Toast.LENGTH_SHORT);
                } else {
                    startScanHR();
                }


//                ToastUtil.showToastLong(" " + GetApkInfo(LoginActivity.this,"http://www.chinashb.com/Download/ShbERP.apk"));
//                new Thread(){
//                    @Override public void run() {
//                        super.run();
//                        String versionCode = WebServiceUtil.getVersionCode();
////                        String versionCode = WebServiceUtil.getMobileVersion();
//                        System.out.println("versionCoce = " + versionCode);
//                    }
//                }.start();
            }
        });

        setHomeButton();
        initView();
//        if (nameEditText.getText().length() > 0){
//            passwordEditText.requestFocus();
//            passwordEditText.setText("john1234@@");
//        }

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

//    private int GetApkInfo(Context context, String apkPath) {
//        PackageManager pm = context.getPackageManager();
//        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
//        pm.getPackageInfo()
//        if (info != null) {
//            ApplicationInfo appInfo = info.applicationInfo;
//            String packageName = appInfo.packageName;  //得到安装包名称
//            String version = info.versionName;//获取安装包的版本号
//            return info.versionCode;
//        }
//        return -1;
//    }

    private int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    private void initView() {
        String userName = SPSingleton.get().getString(SPDefine.SP_login_user_name);
        if (!TextUtils.isEmpty(userName)) {
            nameEditText.setText(userName);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        Toast.makeText(this, " scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
//        String[] qrContent;
//        UserInfoEntity userInfo = new UserInfoEntity();
//        if (result.getContents().contains("/")) {
//            qrContent = result.getContents().split("/");
//            if (qrContent.length >= 4) {
//                userInfo.setHR_ID(Integer.parseInt(qrContent[2]));
//                userInfo.setHrNum(qrContent[4]);
//
//                GetHrNameAsyncTask task = new GetHrNameAsyncTask();
//                task.execute();
//
////                SPSingleton.get().putString(SPDefine.SP_login_user_name, nameEditText.getText().toString());
//                Intent intent = new Intent(LoginActivity.this, MobileMainActivity.class);
//                intent.putExtra(IntentConstant.Intent_Extra_hr_id, Integer.parseInt(qrContent[2]));
//                startActivity(intent);
//                finish();
//            }
//
//        }
    }

    @Override
    protected void onResume() {
        //设置为竖屏幕
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }

    private void startScanHR() {

//        IntentIntegrator scan = new IntentIntegrator(LoginActivity.this);
//        //scan.setOrientationLocked(false);
//        scan.setBeepEnabled(true);
//        scan.setCaptureActivity(CustomScannerActivity.class);
//
//        scan.setPrompt("扫描员工二维码");
//        startActivityForResult(scan.createScanIntent(), 0x0000c0de);
//
//        //new IntentIntegrator( MobileMainActivity.this).initiateScan();

        QRCodeScanActivity.startQRCodeScanner(this, new OnQRScanListenerImpl() {
            @Override
            public void onScanQRCodeSuccess(String result) {
//                if (result.startsWith("http")) {
////                    WebViewActivity.gotoWebViewActivity(LoginActivity.this, result);
//                } else {
//                    CommAlertDialog.with(LoginActivity.this).setTitle("扫描结果展示")
//                            .setMessage(result)
//                            .setMiddleText("确定")
//                            .setCancelAble(false).setTouchOutsideCancel(false)
//                            .setClickButtonDismiss(true)
//                            .create().show();

                String[] qrContent;
                UserInfoEntity userInfo = new UserInfoEntity();
                if (result.contains("/")) {
                    qrContent = result.split("/");
                    if (qrContent.length >= 4) {
                        userInfo.setHR_ID(Integer.parseInt(qrContent[2]));
                        userInfo.setHrNum(qrContent[4]);
                        GetHrNameAsyncTask task = new GetHrNameAsyncTask();
                        task.execute(qrContent[2]);
//                SPSingleton.get().putString(SPDefine.SP_login_user_name, nameEditText.getText().toString());
//                            Intent intent = new Intent(LoginActivity.this, MobileMainActivity.class);
//                            intent.putExtra(IntentConstant.Intent_Extra_hr_id, Integer.parseInt(qrContent[2]));
//                            startActivity(intent);
//                            finish();
                    }

                }
            }
//            }
        });
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
//            MobileMainActivity.AsyncGetHrName task = new MobileMainActivity.AsyncGetHrName();
//            task.execute();
//        }
//    }

    //http://www.chinashb.com/Download/ShbERP.apk
    private void checkErpVersionOk() {
        String sql = "select top 1 VerID,Version,Convert(nvarchar(100),UpdateDate,23) As UpdateDate, Des " +
                " from ERP_Mobile_Ver Where RequireUpdate=1 Order By VerID Desc";
        QueryAsyncTask query = new QueryAsyncTask();
        query.execute(sql);
        query.setLoadDataCompleteListener(new OnLoadDataListener() {
            @Override
            public void loadComplete(List<JsonObject> result) {
                if (result != null && result.size() == 1) {
                    //返回结果，说明网络访问没有问题
                    isNetReady = true;
                    JsonObject o = result.get(0);
                    Integer ErpVerID = o.get("VerID").getAsInt();
                    //// TODO: 2019/7/19 这里根据数据库中Version 的值来判断 Version为实际versionCode值
//                    String Version = o.get("Version").getAsString();
                    String Version = o.get("Version").getAsString();
                    String UpdateDate = o.get("UpdateDate").getAsString();
                    String updateLog = o.get("Des").getAsString();
//                    query_erp_id = ErpVerID;

//                    if (mobile_erp_ver_id >= ErpVerID) {
//                        versionOk = true;
////                        debugLogin();
//                    } else {
//                        String newVerWarning = "当前App 版本已经过时。\n" +
//                                "系统已于" + UpdateDate + "升级到版本" + Version + "\n" +
//                                "版本描述：\n" +
//                                Des;
//                        CommonUtil.ShowToast(LoginActivity.this, newVerWarning, R.mipmap.warning, Toast.LENGTH_SHORT);
//                    }

                    if (getVersionCode(LoginActivity.this) < Integer.parseInt(Version)) {
                        APPUpgradeManager.with(LoginActivity.this)
                                .setNeedShowToast(true)
//                                .setAPIService(APIDefine.SERVICE_BASE)
//                                .setAPIUrl(APIDefine.API_check_new_version)
//                                .setAppName(getString(R.string.app_name))
                                .setApkDownloadedPath(FileUtil.getCachePath())
//                                .setVersionName(APPUtil.getVersionName()).setVersionCode(APPUtil.getVersionCode() + "")
//                                .builder().checkNewVersion(APPUpgradeManager.NAME_MaterialsManager);
                                .builder().showForceUpdateDialog(updateLog);
                    }

                } else {

                }
            }
        });

    }

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

    protected void checkNamePwd() {
        String userName = nameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (userName.isEmpty() || password.isEmpty()) {
            ToastUtil.showToastLong("请输入名字/密码");
        } else {
//            String Name = nameEditText.getText().toString();
//            String password = passwordEditText.getText().toString();
            CheckNameAndPasswordAsyncTask task = new CheckNameAndPasswordAsyncTask();
            task.execute(nameEditText.getText().toString(), passwordEditText.getText().toString());
        }
    }

    private class GetMesDataAsyncTask extends AsyncTask<String, Void, String> {

        @Override protected String doInBackground(String... strings) {
            WsResult result =
//            MESWebServiceUtil.GetSaveFinishedProductCodeDataByMes("XH1910130001");
            WebServiceUtil.GetSaveFinishedProductCodeDataByMes("XH1910130001");
            return result.getErrorInfo();
        }

        @Override protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println("=============================== result = " + result);
            MESDataEntity mesDataEntity = JsonUtil.parseJsonToObject(result, MESDataEntity.class);

            if (mesDataEntity.getCode() == 0){//表示成功
                System.out.println(mesDataEntity.getCode());
                System.out.println(mesDataEntity.getMessage().getItemID() + " " +mesDataEntity.getMessage().getItemUnit());
            }else{
                ToastUtil.showToastLong("接口请求数据错误！");
            }
        }
    }

    private class CheckNameAndPasswordAsyncTask extends AsyncTask<String, Void, Void> {
        private WsResult wsResult = null;
        private CommProgressDialog progressDialog;

        @Override
        protected Void doInBackground(String... params) {
//            String Name = nameEditText.getText().toString();
//            String password = passwordEditText.getText().toString();
            if (params != null && params.length > 1) {
                String userName = params[0];
                String password = params[1];
                wsResult = WebServiceUtil.getTryLogin(userName, password);
                return null;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
//            progressBar.setVisibility(View.VISIBLE);
            progressDialog = new CommProgressDialog.Builder(LoginActivity.this)
                    .setTitle("正在登录..").create();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            if (this.wsResult.getResult()) {
//                UserInfoEntity.ID = this.wsResult.getID().intValue();

//                Intent resultIntent = new Intent();
//                setResult(1, resultIntent);
//                finish();
                UserSingleton.get().setHRID(wsResult.getID().intValue());
                UserSingleton.get().setHRName(nameEditText.getText().toString());
                SPSingleton.get().putString(SPDefine.SP_login_user_name, nameEditText.getText().toString());

                MobclickAgent.onEvent(LoginActivity.this, StringConstantUtil.Umeng_event_login);

                Intent intent = new Intent(LoginActivity.this, MobileMainActivity.class);
                intent.putExtra(IntentConstant.Intent_Extra_from_name_pwd, true);
                intent.putExtra(IntentConstant.Intent_Extra_hr_id, wsResult.getID().intValue());
                startActivity(intent);
                finish();
            } else {
                ToastUtil.showToastLong(wsResult.getErrorInfo());
            }
//            progressBar.setVisibility(View.INVISIBLE);
            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //    private UserInfoEntity userInfo;
//
//
    private class GetHrNameAsyncTask extends AsyncTask<String, Void, Void> {
        //Image hr_photo;
        @Override
        protected Void doInBackground(String... params) {
            int hrId = Integer.parseInt(params[0]);
//            userInfo = WebServiceUtil.getHRName_Bu(userInfo.getHR_ID());
            UserInfoEntity userInfo = WebServiceUtil.getHRName_Bu(hrId);
            UserSingleton.get().setHRID(hrId);
            UserSingleton.get().setHRName(userInfo.getHR_Name());
            UserSingleton.get().setUserInfo(userInfo);
            if (userInfo != null) {
                Bitmap userPic = CommonUtil.getUserPic(LoginActivity.this, userPictureMap, userInfo.getHR_ID());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
//            scanProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
//            if (userInfo != null) {
////                setTvUserName();
////                if (pictureBitmap != null) {
////                    avatarImageView.setImageBitmap(pictureBitmap);
////                }
//            } else {
//                Toast.makeText(LoginActivity.this, "无法访问服务器，请检查网络连接是否正常", Toast.LENGTH_LONG).show();
//            }

//            scanProgressBar.setVisibility(View.GONE);
            MobclickAgent.onEvent(LoginActivity.this, StringConstantUtil.Umeng_event_scan_hr_login);
            if (UserSingleton.get().getUserInfo() != null) {
                Intent intent = new Intent(LoginActivity.this, MobileMainActivity.class);
//            intent.putExtra(IntentConstant.Intent_Extra_hr_id, Integer.parseInt(qrContent[2]));
                intent.putExtra(IntentConstant.Intent_Extra_hr_id, UserSingleton.get().getHRID());
                startActivity(intent);
                finish();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

}
