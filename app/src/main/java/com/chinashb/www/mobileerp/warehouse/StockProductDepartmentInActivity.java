package com.chinashb.www.mobileerp.warehouse;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.OnQRScanListenerImpl;
import com.chinashb.www.mobileerp.QRCodeScanActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.MpiWcBean;
import com.chinashb.www.mobileerp.basicobject.UserAllInfoEntity;
import com.chinashb.www.mobileerp.basicobject.UserInfoEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.DepartmentBean;
import com.chinashb.www.mobileerp.bean.ProductFeeRdmBean;
import com.chinashb.www.mobileerp.bean.ResearchItemBean;
import com.chinashb.www.mobileerp.commonactivity.CommonSelectItemActivity;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.JsonUtil;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.StringUtils;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CommAlertDialog;
import com.chinashb.www.mobileerp.widget.CommonSelectInputDialog;
import com.chinashb.www.mobileerp.widget.OnDialogViewClickListener;
import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

/***
 * @date 创建时间 6/3/26 3:52 PM
 * @author 作者: liweifeng
 * @description 成品部门领料，扫非托盘标签
 */
public class StockProductDepartmentInActivity extends BaseActivity {
    private Context mContext;
    //private StatedButton mSbtn_back;
    private LinearLayout mLl_parent;
    private MpiWcBean themw;
    private TextView txtMw_Title;
    private Button selectDepartmentButton;
    private Button inputHRNOButton;
    private Button selectResearchProgramButton;
    private Button scanAddTrayButton;
    private Button outStockButton;
    private TextView tvDep;
    //    static HashMap<String, String> SelectDep;
    private TextView tvReseachProgram;
    private EditText inputEditText;
    private String scanstring;
    private DepartmentBean departmentBean;
    private ResearchItemBean researchItemBean;
    private ProductFeeRdmBean productFeeRdmBean;
    private List<ProductFeeRdmBean> productFeeRdmBeanList;
    private Button selectSubResearchProgramButton;
    private TextView tvSubReseachProgram;
    private String selectedSubProgramCode;
    private String selectedSubProgramName;
    private Map<String, String> subResearchProgramMap;
    private TextView useTextView;
    private TextView titleTextView;
    private Button selectUseButton;
    private Button scanHRCodeButton;
//    private SelectUseDialog selectUseDialog;
    private CommonSelectInputDialog selectUseDialog;
    private boolean hasSelectUse = false;
    private String operatorName;
    private String description;
    private boolean hasScanHrCode;
    private float originalScanQty;
    private List<Long> boxIDList;
    private int currentQty = 0;

    private CommonSelectInputDialog hrNODialog;
    private String currentNRDMItemSubNumber = "";

    private OnViewClickListener onHRNOViewClickListener = new OnViewClickListener() {
        @Override
        public <T> void onClickAction(View v, String tag, T t) {
            if (t != null) {
                String hrNO = (String) t;
                if (StringUtils.isStringValid(hrNO)) {
                    GetHrEntityAsyncTask hrEntityAsyncTask = new GetHrEntityAsyncTask();
                    hrEntityAsyncTask.execute(hrNO);
                } else {
                    ToastUtil.showToastShort("工号解析失败！");
                }
                if (hrNODialog != null && hrNODialog.isShowing()) {
                    hrNODialog.dismiss();
                }

            }
        }};

    private boolean isCurrentSmallPackage = false;
    private TextView itemInfoTextView;
    private class JudgeRdmOperationCorrectTask extends AsyncTask<Void, Void, WsResult> {
        @Override
        protected WsResult doInBackground(Void... params) {
            String productItem = (productFeeRdmBean != null) ? productFeeRdmBean.getNumber() : "";
            String subItem = (selectedSubProgramCode != null) ? selectedSubProgramCode : "";
            int entityId = (departmentBean != null) ? departmentBean.getDepartmentID() : 0;
            return WebServiceUtil.judgeProductRdmOperationCorrect(
                    UserSingleton.get().getHRID(),
                    UserSingleton.get().getUserInfo().getBu_ID(),
                    655,
                    entityId,
                    productItem,
                    subItem);
        }

        @Override
        protected void onPostExecute(WsResult result) {
            if (result != null && result.getResult()) {
                new WarehouseOutAsyncTask().execute();
            } else {
                String errMsg = (result != null) ? result.getErrorInfo() : "校验失败";
                CommonUtil.ShowToast(StockProductDepartmentInActivity.this, errMsg, R.mipmap.warning);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindView();
        setButtonListener();
        boxIDList = new ArrayList<>();
    }

//    @Override
//    protected void onResume() {
//        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
//        super.onResume();
//    }

    @Override
    protected void onPause() {
        super.onPause();
        if (selectUseDialog != null && selectUseDialog.isShowing()) {
            selectUseDialog.dismiss();
        }
    }

    protected void bindView() {
        initSubResearchProgramMap();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_product_stock_out_dep_layout);
        mContext = this;
        txtMw_Title = (TextView) findViewById(R.id.tv_issue_more_mw_title);
        tvDep = (TextView) findViewById(R.id.tv_stock_out_dep_product_select_dep_product);
        tvReseachProgram = (TextView) findViewById(R.id.tv_stock_out_dep_product_select_program);
        inputEditText = findViewById(R.id.stock_out_dep_product_input_EditeText);

        selectDepartmentButton = (Button) findViewById(R.id.btn_stock_out_dep_product_select_dep_product);
        selectResearchProgramButton = (Button) findViewById(R.id.btn_stock_out_dep_product_select_program);
        scanAddTrayButton = (Button) findViewById(R.id.product_btn_issue_more_add_extra);
        selectSubResearchProgramButton = (Button) findViewById(R.id.btn_stock_out_dep_product_sub_program);
        tvSubReseachProgram = (TextView) findViewById(R.id.tv_stock_out_dep_product_sub_program);
        outStockButton = (Button) findViewById(R.id.product_btn_exe_warehouse_out);
        scanHRCodeButton = findViewById(R.id.department__in_scan_hr_code_button);
        scanHRCodeButton.setVisibility(View.GONE);
        inputHRNOButton = findViewById(R.id.department__in_input_hrno_button);
        selectUseButton = findViewById(R.id.department__in_select_use_button);
        useTextView = findViewById(R.id.department__in_select_use_button);
        titleTextView = findViewById(R.id.tv_stock_out_dep_product_title);
        itemInfoTextView = findViewById(R.id.product_department_in_info_textview);

        //按照上次选择的结果，直接显示
        if (departmentBean != null) {
            tvDep.setText(departmentBean.getDepartmentName());
        }
        if (researchItemBean != null) {
            tvReseachProgram.setText(researchItemBean.getAbb() + "  --  " + researchItemBean.getProgram());
        }
    }

    protected void setButtonListener() {
        selectDepartmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(StockDepartmentInActivity.this, SelectItemActivity.class);
                Intent intent = new Intent(StockProductDepartmentInActivity.this, CommonSelectItemActivity.class);
                String sql = "\n" +
                        "select cd.Department_ID, pd.Department_Name as PDN, cd.Department_Name  from org " +
                        " left join department as pd on org.pid=pd.Department_ID " +
                        " inner join department as cd on org.cid=cd.department_ID " +
                        " where cd.Company_ID =" + UserSingleton.get().getUserInfo().getCompany_ID() +
                        " and cd.Hide=0 and cd.Subgroup=0 and cd.IsVirtual=0" +
                        " And " +
                        " (pd.Hide is null or pd.Hide=0) " +
                        " Order by pd.dep_order, cd.Dep_Order ";

                List<Integer> ColWith = new ArrayList<Integer>(Arrays.asList(50, 100, 100));
                List<String> ColCaption = new ArrayList<String>(Arrays.asList("部门ID", "上级部门", "部门名称"));

                String Title = "选择部门";
                intent.putExtra("Title", Title);
                intent.putExtra("SQL", sql);
                intent.putExtra("ColWidthList", (Serializable) ColWith);
                intent.putExtra("ColCaptionList", (Serializable) ColCaption);
                intent.putExtra(IntentConstant.Intent_Extra_to_select_search_from_postition, IntentConstant.Select_Search_From_Select_Department);

                startActivityForResult(intent, 100);
            }

        });

        selectResearchProgramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetProductFeeRdmAsyncTask().execute();
            }

        });

        selectSubResearchProgramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productFeeRdmBean == null) {
                    ToastUtil.showToastShort("请先选择研发项目！");
                    return;
                }
                showSubResearchProgramDialog();
            }
        });

        scanAddTrayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(StockProductDepartmentInActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
            }
        });

        outStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hasSelectUse) {
                    ToastUtil.showToastShort("您还没有选择用途，请选择!");
                    return;
                }
                if (boxIDList.size() > 0) {
                    if (departmentBean == null) {
                        CommonUtil.ShowToast(StockProductDepartmentInActivity.this, "没有选择部门", R.mipmap.warning);
                        return;
                    }

                    if (TextUtils.isEmpty(selectedSubProgramCode)) {
                        CommonUtil.ShowToast(StockProductDepartmentInActivity.this, "没有选择研发子项目", R.mipmap.warning);
                        return;
                    }

                    //// TODO: 2020/4/21 这里可以与isLogin的公共判断方法合并
                    if (UserSingleton.get().getHRID() > 0 && !TextUtils.isEmpty(UserSingleton.get().getHRName())) {

                        new JudgeRdmOperationCorrectTask().execute();
                    } else {
                        CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(StockProductDepartmentInActivity.this)
                                .setTitle("").setMessage("您当前程序账号有误，需重新登录！")
                                .setLeftText("确定");


                        builder.setOnViewClickListener(new OnDialogViewClickListener() {
                            @Override
                            public void onViewClick(Dialog dialog, View v, int tag) {
                                switch (tag) {
                                    case CommAlertDialog.TAG_CLICK_LEFT:
                                        CommonUtil.doLogout(StockProductDepartmentInActivity.this);
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        });
                        builder.create().show();
                    }
                } else {
                    ToastUtil.showToastShort("没有领料信息！");
                }
            }
        });
        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            protected void onTextChangedSafe(CharSequence text) {
                if (text.toString().length() > 0) {
                    parseScanResult(inputEditText.getText().toString());
                }
            }
        });

        selectUseButton.setOnClickListener(v -> {

            if (!hasScanHrCode) {
                ToastUtil.showToastShort("请先扫描员工ID信息码！");
                if (selectUseDialog != null && selectUseDialog.isShowing()) {
                    selectUseDialog.dismiss();
                }
                return;
            }

            if (selectUseDialog == null) {
                selectUseDialog = new CommonSelectInputDialog(StockProductDepartmentInActivity.this);
            }
//            commonSelectInputDialog.show();
//            commonSelectInputDialog.refreshContentList(companyList);
//            commonSelectInputDialog.setTitle("请选择公司").setSelectOnly(true).setOnViewClickListener(new OnViewClickListener() {
//                @Override public <T> void onClickAction(View v, String tag, T t) {
//                    if (t != null && t instanceof CompanyBean) {
//                        companyBean = (CompanyBean) t;
//                        senderCompanyNameTextView.setText(((CompanyBean) t).getCompanyChineseName());
//                        if (commonSelectInputDialog != null && commonSelectInputDialog.isShowing()) {
//                            commonSelectInputDialog.dismiss();
//                        }
//
//                    }
//                }
//            });

            selectUseDialog.show();
            selectUseDialog.setSelectOnly(false);
            selectUseDialog.setOnViewClickListener(new OnViewClickListener() {
                @Override
                public <T> void onClickAction(View v, String tag, T t) {
                    if (hasSelectUse) {
                        ToastUtil.showToastShort("您已经选择用途了");
                        return;
                    }
                    ToastUtil.showToastShort("您选择了" + (String) t);
                    useTextView.setText((String) t);
                    description = description + " 用途：" + (String) t;
                    hasSelectUse = true;
                    if (selectUseDialog != null && selectUseDialog.isShowing()) {
                        selectUseDialog.dismiss();
                    }
                }
            });

        });

        scanHRCodeButton.setOnClickListener(v -> {
            if (StringUtils.isStringValid(operatorName)) {
                ToastUtil.showToastShort("操作员信息已获取，无须再扫描工牌！");
            } else {
                startScanHR();
            }
        });

        inputHRNOButton.setOnClickListener(v -> {
            if (StringUtils.isStringValid(operatorName)) {
                ToastUtil.showToastShort("操作员信息已获取，无须再扫描工牌！");
            } else {
                if (hrNODialog == null) {
                    hrNODialog = new CommonSelectInputDialog(StockProductDepartmentInActivity.this);
                }
                hrNODialog.show();
                //这句必须放在show之后
//            remarkDialog.setSelectOnly(true);
                hrNODialog.setInputOnly(true);
                hrNODialog.setInputTextHint("请输入工号");
                hrNODialog.setOnViewClickListener(onHRNOViewClickListener);
            }

        });
    }

    private void startScanHR() {
        QRCodeScanActivity.startQRCodeScanner(this, new OnQRScanListenerImpl() {
            @Override
            public void onScanQRCodeSuccess(String result) {
                if (result.startsWith("http")) {
                } else {

                    String[] qrContent;
                    UserInfoEntity userInfo = new UserInfoEntity();
                    if (result.contains("/")) {
                        qrContent = result.split("/");
                        if (qrContent.length >= 4) {
                            userInfo.setHR_ID(Integer.parseInt(qrContent[2]));
                            userInfo.setHrNum(qrContent[4]);
                            GetHrNameAsyncTask task = new GetHrNameAsyncTask();
                            task.execute(qrContent[2]);
                        }

                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //选择部门
        if (requestCode == 100 && resultCode == 1) {
            ActivityResultSelectDep(data);
            return;
        }
        //选择研发项目
        if (requestCode == 200 && resultCode == 1) {
            ActivityResultSelectResearchProgram(data);
            return;
        }

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //选择物料
            if (result.getContents() == null) {
            } else {
                parseScanResult(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected void ActivityResultSelectDep(Intent data) {
        if (data != null) {
            departmentBean = data.getParcelableExtra("SelectItem");
            if (departmentBean != null) {
                tvDep.setText(departmentBean.getDepartmentName());
            }
        }
    }

    protected void ActivityResultSelectResearchProgram(Intent data) {
        if (data != null) {
            researchItemBean = data.getParcelableExtra("SelectItem");
            selectedSubProgramCode = null;
            selectedSubProgramName = null;
            tvSubReseachProgram.setText("研发子项目");
            if (researchItemBean != null) {
                tvReseachProgram.setText(researchItemBean.getAbb() + "  --  " + researchItemBean.getProgram());
                selectSubResearchProgramButton.setVisibility(View.VISIBLE);
                tvSubReseachProgram.setVisibility(View.VISIBLE);
            } else {
                selectSubResearchProgramButton.setVisibility(View.GONE);
                tvSubReseachProgram.setVisibility(View.GONE);
            }
        }
    }

    protected void parseScanResult(String result) {
        if (TextUtils.isEmpty(result)) {
            return;
        }
        if (result.contains("/")) {
            String[] qrContent;
            qrContent = result.split("/");
            if (result.contains("HRID") && result.contains("HRNO")) {
                if (qrContent.length >= 4) {
                    GetHrNameAsyncTask task = new GetHrNameAsyncTask();
                    task.execute(qrContent[2]);
                }
            } else if (result.startsWith("Pallet") && qrContent.length >= 8) {
                long boxId = Long.parseLong(qrContent[1]);
                currentQty = Integer.parseInt(qrContent[7]);
                if (boxIDList.contains(boxId)) {
                    ToastUtil.showToastShort("该托盘已在列表中，请勿重复扫描！");
                } else {

                    if (boxIDList.size() > 0) {
                        ToastUtil.showToastShort("部门领料成品每次只能操作一托");
                    } else {
                        StringBuilder tempBoxInfoSBuilder = new StringBuilder();
//                            tempBoxInfoSBuilder.append("\n\n");
                        if (!TextUtils.isEmpty(itemInfoTextView.getText())) {
                            tempBoxInfoSBuilder.append(itemInfoTextView.getText());
                        } else {
                            //  第一行换行
                            tempBoxInfoSBuilder.append("物料信息\n");
                        }

                        tempBoxInfoSBuilder.append(String.format("托盘ID:%s,托盘序列号：%s,客户图号：%s,箱子数量:%s\n", qrContent[1], qrContent[3], qrContent[5], qrContent[7]));
                        //                        itemInfoTextView.setText(String.format("托盘ID:%s,托盘序列号：%s,客户图号：%s,箱子数量:%s", qrContent[1], qrContent[3], qrContent[5], qrContent[7]));
                        itemInfoTextView.setText(tempBoxInfoSBuilder.toString());
                        inputEditText.setText("");
                        boxIDList.add(boxId);
                    }
                }
            }


        }
    }

    private class WarehouseOutAsyncTask extends AsyncTask<String, Void, Void> {
        private WsResult ws_result;

        @Override
        protected Void doInBackground(String... params) {
            int count = 0;
            int boxItemSize = boxIDList.size();
            while (count < boxItemSize && boxIDList.size() > 0) {
                //// TODO: 2019/9/18 大概可能是修改数量后仍是原来的数量导致 ，其他 的那种修改应该也有这个问题
                long tempBoxID = boxIDList.get(0);

                ws_result = WebServiceUtil.opProductDepNotPalletIssue(UserSingleton.get().getUserInfo().getBu_ID(), departmentBean, tempBoxID, currentQty, description,currentNRDMItemSubNumber,productFeeRdmBean != null ? productFeeRdmBean.getFiptID() : 0);
//                if (ws_result.getResult()) {
//                    boxItemEntityList.remove(boxItemEntity);
//                    //如果实际出库数量小于扫描出的要再处理
//                } else {
//                    return null;
//                }
                count++;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
//            issueMoreItemAdapter.notifyDataSetChanged();
//            recyclerView.setAdapter(issueMoreItemAdapter);
//            pbBackground.setVisibility(View.GONE);
            if (ws_result != null) {
                if (!ws_result.getResult()) {
                    CommonUtil.ShowToast(StockProductDepartmentInActivity.this, ws_result.getErrorInfo(), R.mipmap.warning, Toast.LENGTH_LONG);
                } else {
                    //// TODO: 2019/7/15 哪些页面是执行成功后要跳离的？
                    CommonUtil.ShowToast(StockProductDepartmentInActivity.this, "成功出库", R.mipmap.smiley, Toast.LENGTH_SHORT);
                    description = "";
                    hasSelectUse = false;
                    //// TODO: 11/11/24 这里不必每次finish 
//                    finish();
                }
            }
            itemInfoTextView.setText("");
            boxIDList.clear();
            currentNRDMItemSubNumber = "";
        }

    }

    //// TODO: 2019/7/15 这里根存储的可能有出入，可能有问题
    private class GetHrNameAsyncTask extends AsyncTask<String, Void, UserAllInfoEntity> {
        //Image hr_photo;
        @Override
        protected UserAllInfoEntity doInBackground(String... params) {
            int hrId = Integer.parseInt(params[0]);

            String sql = "select * from hr where hr_id = " + hrId;
            List<JsonObject> jsonObjectList;
            jsonObjectList = WebServiceUtil.getJsonList(sql);

            if (jsonObjectList != null && jsonObjectList.size() > 0) {
                //                    JsonObject jsonObject = jsonObjectList.get(0);
                UserAllInfoEntity userAllInfoEntity = JsonUtil.parseJsonToObject(jsonObjectList.get(0).toString(), UserAllInfoEntity.class);
                if (userAllInfoEntity != null) {
                    UserSingleton.get().setUserAllInfoEntity(userAllInfoEntity);
                }
                return userAllInfoEntity;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
//            scanProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(UserAllInfoEntity userAllInfoEntity) {
            String departmentName;
            if (userAllInfoEntity.getHRDepartment() == null || TextUtils.isEmpty(userAllInfoEntity.getHRDepartment().toString())) {
                departmentName = UserSingleton.get().getDepartmentMap().get(userAllInfoEntity.getDepartmentID());
            } else {
                departmentName = userAllInfoEntity.getHRDepartment().toString();
            }
            titleTextView.setText("操作员：" + departmentName + "-" + userAllInfoEntity.getHRName());
            if (departmentBean == null) {
                departmentBean = new DepartmentBean();
                departmentBean.setDepartmentID(userAllInfoEntity.getDepartmentID());
                departmentBean.setDepartmentName(departmentName);
            }
            operatorName = userAllInfoEntity.getHRName();
            description = departmentName + "-" + operatorName + "领料";
            inputEditText.setText("");
            hasScanHrCode = true;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    private class GetHrEntityAsyncTask extends AsyncTask<String, Void, UserAllInfoEntity> {
        @Override
        protected UserAllInfoEntity doInBackground(String... params) {
            String hrNO = params[0];
            String sql = "select * from hr where hr_no = '" + hrNO + "'";
            List<JsonObject> jsonObjectList;
            jsonObjectList = WebServiceUtil.getJsonList(sql);

            if (jsonObjectList != null && jsonObjectList.size() > 0) {
                //                    JsonObject jsonObject = jsonObjectList.get(0);
                UserAllInfoEntity userAllInfoEntity = JsonUtil.parseJsonToObject(jsonObjectList.get(0).toString(), UserAllInfoEntity.class);
                if (userAllInfoEntity != null) {
                    UserSingleton.get().setUserAllInfoEntity(userAllInfoEntity);
                }
                return userAllInfoEntity;
            }
            return null;
        }

        @Override
        protected void onPostExecute(UserAllInfoEntity userAllInfoEntity) {
            if (userAllInfoEntity != null) {
                String departmentName = "";
                if (userAllInfoEntity.getHRDepartment() == null || TextUtils.isEmpty(userAllInfoEntity.getHRDepartment().toString())) {
                    if (UserSingleton.get().getDepartmentMap() != null){
                        departmentName = UserSingleton.get().getDepartmentMap().get(userAllInfoEntity.getDepartmentID());
                    }
                } else {
                    departmentName = userAllInfoEntity.getHRDepartment().toString();
                }
                titleTextView.setText("操作员：" + departmentName + "-" + userAllInfoEntity.getHRName());
                if (departmentBean == null) {
                    departmentBean = new DepartmentBean();
                    departmentBean.setDepartmentID(userAllInfoEntity.getDepartmentID());
                    departmentBean.setDepartmentName(departmentName);
                }
                operatorName = userAllInfoEntity.getHRName();
                description = departmentName + "-" + operatorName + "领料";
                inputEditText.setText("");
                hasScanHrCode = true;
            } else {
                ToastUtil.showToastShort("输入的工号解析失败！");
            }

        }


    }

    private void initSubResearchProgramMap() {
        subResearchProgramMap = new LinkedHashMap<>();
        subResearchProgramMap.put("-SY-002", "环境可靠性试验");
        subResearchProgramMap.put("-SY-003", "耐久可靠性试验");
        subResearchProgramMap.put("-SY-004", "机械负荷试验");
        subResearchProgramMap.put("-SY-005", "电机电气性能试验");
        subResearchProgramMap.put("-SY-006", "常规性能试验");
        subResearchProgramMap.put("-QT-007", "其他试验项目");
        subResearchProgramMap.put("-GY-008", "技术及工艺验证项目");
    }

    private void showSubResearchProgramDialog() {
        final String[] codes = subResearchProgramMap.keySet().toArray(new String[0]);
        final String[] names = new String[codes.length];
        for (int i = 0; i < codes.length; i++) {
            names[i] = codes[i] + "  " + subResearchProgramMap.get(codes[i]);
        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(StockProductDepartmentInActivity.this);
        builder.setTitle("选择研发子项目");
        builder.setItems(names, (dialog, which) -> {
            selectedSubProgramCode = codes[which];
            selectedSubProgramName = subResearchProgramMap.get(selectedSubProgramCode);
            String displayText = selectedSubProgramCode + "  " + selectedSubProgramName;
            tvSubReseachProgram.setText(displayText);
            if (productFeeRdmBean != null) {
                String number = productFeeRdmBean.getNumber();
                currentNRDMItemSubNumber = number;
                tvReseachProgram.setText(number + "  --  " + productFeeRdmBean.getResearchType() + "  |  " + number + "-" + selectedSubProgramCode);
            }
        });
        builder.show();
    }

    private class GetProductFeeRdmAsyncTask extends AsyncTask<Void, Void, List<ProductFeeRdmBean>> {
        @Override
        protected List<ProductFeeRdmBean> doInBackground(Void... params) {
            return WebServiceUtil.getProductFeeRdm(
                    UserSingleton.get().getHRID(),
                    UserSingleton.get().getUserInfo().getBu_ID());
        }

        @Override
        protected void onPostExecute(List<ProductFeeRdmBean> list) {
            if (list == null || list.isEmpty()) {
                ToastUtil.showToastShort("获取研发项目列表失败！");
                return;
            }
            productFeeRdmBeanList = list;
            final String[] typeNames = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                typeNames[i] = list.get(i).getResearchType();
            }
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(StockProductDepartmentInActivity.this);
            builder.setTitle("选择研发项目");
            builder.setItems(typeNames, (dialog, which) -> {
                productFeeRdmBean = productFeeRdmBeanList.get(which);
                selectedSubProgramCode = null;
                selectedSubProgramName = null;
                tvSubReseachProgram.setText("研发子项目");
                tvReseachProgram.setText(productFeeRdmBean.getNumber() + "  --  " + productFeeRdmBean.getResearchType());
                selectSubResearchProgramButton.setVisibility(View.VISIBLE);
                tvSubReseachProgram.setVisibility(View.VISIBLE);
            });
            builder.show();
        }
    }

}
