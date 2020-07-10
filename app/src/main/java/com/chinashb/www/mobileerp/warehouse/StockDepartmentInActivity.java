package com.chinashb.www.mobileerp.warehouse;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.OnQRScanListenerImpl;
import com.chinashb.www.mobileerp.QRCodeScanActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.IssueMoreItemAdapter;
import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.basicobject.MpiWcBean;
import com.chinashb.www.mobileerp.basicobject.UserAllInfoEntity;
import com.chinashb.www.mobileerp.basicobject.UserInfoEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.DepartmentBean;
import com.chinashb.www.mobileerp.bean.ResearchItemBean;
import com.chinashb.www.mobileerp.commonactivity.CommonSelectItemActivity;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.JsonUtil;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CommAlertDialog;
import com.chinashb.www.mobileerp.widget.OnDialogViewClickListener;
import com.chinashb.www.mobileerp.widget.SelectUseDialog;
import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 部门领料
 */

public class StockDepartmentInActivity extends BaseActivity {
    private Context mContext;
    //private StatedButton mSbtn_back;
    private LinearLayout mLl_parent;
    private MpiWcBean themw;
    private TextView txtMw_Title;
    private Button selectDepartmentButton;
    private Button selectResearchProgramButton;
    private Button scanAddTrayButton;
    private Button outStockButton;
    private TextView tvDep;
    //    static HashMap<String, String> SelectDep;
    private TextView tvReseachProgram;
    private EditText inputEditText;
    //    static HashMap<String, String> SelectReaseach;
    private RecyclerView recyclerView;
    private IssueMoreItemAdapter issueMoreItemAdapter;
    private List<BoxItemEntity> boxItemEntityList;
    private String scanstring;
    private DepartmentBean departmentBean;
    private ResearchItemBean researchItemBean;
    private TextView useTextView;
    private TextView titleTextView;

    private ProgressBar pbBackground;
    private Button selectUseButton;
    private Button scanHRCodeButton;
    private SelectUseDialog selectUseDialog;
    private boolean hasSelectUse = false;
    private String operatorName;
    private String description;
    private boolean hasScanHrCode;
    private float originalScanQty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindView(savedInstanceState);
        setHomeButton();
        setButtonListener();
    }

    private View addView1() {
        // TODO 动态添加布局(xml方式)
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//      LayoutInflater inflater1=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//      LayoutInflater inflater2 = getLayoutInflater();
        LayoutInflater inflater3 = LayoutInflater.from(mContext);
        View view = inflater3.inflate(R.layout.listview_jsonlist, null);
        view.setLayoutParams(lp);
        return view;

    }

    private View addView2() {
        // TODO 动态添加布局(java方式)
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout view = new LinearLayout(this);
        view.setLayoutParams(lp);//设置布局参数
        view.setOrientation(LinearLayout.HORIZONTAL);// 设置子View的Linearlayout// 为垂直方向布局
        //定义子View中两个元素的布局
        ViewGroup.LayoutParams vlp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ViewGroup.LayoutParams vlp2 = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView tv1 = new TextView(this);
        TextView tv2 = new TextView(this);
        tv1.setLayoutParams(vlp);//设置TextView的布局
        tv2.setLayoutParams(vlp2);
        tv1.setText("姓名:");
        tv2.setText("李四");
        tv2.setPadding(calculateDpToPx(50), 0, 0, 0);//设置边距
        view.addView(tv1);//将TextView 添加到子View 中
        view.addView(tv2);//将TextView 添加到子View 中
        return view;
    }

    private int calculateDpToPx(int padding_in_dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (padding_in_dp * scale + 0.5f);
    }

    protected void bindView(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_stock_out_dep_layout);
        mContext = this;
        //mLinearLayout=(LinearLayout)findViewById(R.id.box);
        mLl_parent = (LinearLayout) findViewById(R.id.box);
        recyclerView = (RecyclerView) findViewById(R.id.rv_issue_more_extra);
        txtMw_Title = (TextView) findViewById(R.id.tv_issue_more_mw_title);
        tvDep = (TextView) findViewById(R.id.tv_stock_out_dep_select_dep);
        tvReseachProgram = (TextView) findViewById(R.id.tv_stock_out_dep_select_program);
        inputEditText = findViewById(R.id.stock_out_dep_input_EditeText);
        pbBackground = (ProgressBar) findViewById(R.id.pb_webservice_progressbar);
        pbBackground.setVisibility(View.GONE);

        selectDepartmentButton = (Button) findViewById(R.id.btn_stock_out_dep_select_dep);
        selectResearchProgramButton = (Button) findViewById(R.id.btn_stock_out_dep_select_program);
        scanAddTrayButton = (Button) findViewById(R.id.btn_issue_more_add_extra);
        outStockButton = (Button) findViewById(R.id.btn_exe_warehouse_out);
        scanHRCodeButton = findViewById(R.id.deparment_in_scan_hr_code_button);
        selectUseButton = findViewById(R.id.deparment_in_select_use_button);
        useTextView = findViewById(R.id.department_in_use_textView);
        titleTextView = findViewById(R.id.tv_stock_out_dep_title);

        boxItemEntityList = new ArrayList<>();
        if (savedInstanceState != null) {
            boxItemEntityList = (List<BoxItemEntity>) savedInstanceState.getSerializable("BoxItemList");
        }

        issueMoreItemAdapter = new IssueMoreItemAdapter(StockDepartmentInActivity.this, boxItemEntityList);
        issueMoreItemAdapter.showNeedMore = false;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        recyclerView.setAdapter(issueMoreItemAdapter);

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
                Intent intent = new Intent(StockDepartmentInActivity.this, CommonSelectItemActivity.class);
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
//                Intent intent = new Intent(StockDepartmentInActivity.this, SelectItemActivity.class);
                Intent intent = new Intent(StockDepartmentInActivity.this, CommonSelectItemActivity.class);

                String sql = "Select Product.Product_ID,PS_Version.PS_ID,Product.Abb,Fi_Standard_Product_Type.FiPT_ID," +
                        "Fi_Standard_Product_Type.TypeName As Program, St.FSPD_ID,St.Fi_Standard_Product_Developing_Status As Status,Program.Program_ID " +
                        "From Product Inner Join PS_Version On PS_Version.Product_ID=Product.Product_ID And Product.Current_PS=PS_Version.PS_ID " +
                        "Inner Join Program On Product.Program_ID=Program.Program_ID " +
                        " Inner Join Fi_Standard_Product On Product.Product_ID=Fi_Standard_Product.Product_ID " +
                        " Inner Join Fi_Standard_product_Type On Fi_Standard_Product.Fi_Standard_Product_Type=Fi_Standard_Product_Type.FiPT_ID  " +
                        " Inner Join Fi_Standard_Product_Developing_Status As St On Fi_Standard_Product.Fi_Standard_Product_Developing_Status=St.FSPD_ID " +
                        " Where Fi_Standard_product_Type.Obsolete=0 And Program.Company_ID=" + UserSingleton.get().getUserInfo().getCompany_ID() +
                        " And Program.Bu_ID=" + UserSingleton.get().getUserInfo().getBu_ID() + " And Fi_Standard_Product_Type.Obsolete=0  ";

                List<Integer> ColWith = new ArrayList<Integer>(Arrays.asList(10, 10, 120, 10, 120, 10, 120, 10));
                List<String> ColCaption = new ArrayList<String>(Arrays.asList("Product_ID", "PS_ID", "产品通称", "FiPT_ID", "研发项目", "FSPD_ID", "研发状态", "Program_ID"));
                List<String> HiddenCol = new ArrayList<String>(Arrays.asList("Product_ID", "PS_ID", "FiPT_ID", "FSPD_ID", "Program_ID"));

                String Title = "选择研发项目";
                intent.putExtra("Title", Title);
                intent.putExtra("SQL", sql);
                intent.putExtra("ColWidthList", (Serializable) ColWith);
                intent.putExtra("ColCaptionList", (Serializable) ColCaption);
                intent.putExtra("hiddenColList", (Serializable) HiddenCol);
                intent.putExtra(IntentConstant.Intent_Extra_to_select_search_from_postition, IntentConstant.Select_Search_From_Select_ReSearch_Program);

                startActivityForResult(intent, 200);
            }

        });

        scanAddTrayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(StockDepartmentInActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
//                parseScanResult("VB/MT/604140/S/0/IV/132895/P/SG00400-1000B A6/D/20190818/L/20190818-B7-1-1/N/780/Q/1920");
            }
        });

        outStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hasSelectUse) {
                    ToastUtil.showToastShort("您还没有选择用途，请选择!");
                    return;
                }
                if (boxItemEntityList.size() > 0) {
                    if (departmentBean == null) {
                        CommonUtil.ShowToast(StockDepartmentInActivity.this, "没有选择部门", R.mipmap.warning);
                        return;
                    }
                    //// TODO: 2019/7/5 研发项目是非必填，有的为空
//                    if (researchItemBean == null) {
//                        CommonUtil.ShowToast(StockDepartmentInActivity.this, "没有选择研发项目", R.mipmap.warning);
//                        return;
//                    }


                    //// TODO: 2020/4/21 这里可以与isLogin的公共判断方法合并
                    if (UserSingleton.get().getHRID() > 0 && !TextUtils.isEmpty(UserSingleton.get().getHRName())){

                        WarehouseOutAsyncTask task = new WarehouseOutAsyncTask();
                        task.execute();
                    }else{
                        CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(StockDepartmentInActivity.this)
                                .setTitle("").setMessage("您当前程序账号有误，需重新登录！")
                                .setLeftText("确定");


                        builder.setOnViewClickListener(new OnDialogViewClickListener() {
                            @Override
                            public void onViewClick(Dialog dialog, View v, int tag) {
                                switch (tag) {
                                    case CommAlertDialog.TAG_CLICK_LEFT:
                                        CommonUtil.doLogout(StockDepartmentInActivity.this);
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
            @Override public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                if (editable.toString().endsWith("\n")) {
//                    ToastUtil.showToastLong("扫描结果:" + editable.toString());
                    System.out.println("========================扫描结果:" + editable.toString());
//                    parseScanResult(editable.toString());
                    parseScanResult(inputEditText.getText().toString());
                }
            }
        });

        selectUseButton.setOnClickListener(v -> {
            if (selectUseDialog == null) {
                selectUseDialog = new SelectUseDialog(StockDepartmentInActivity.this);
            }
            selectUseDialog.show();
            selectUseDialog.setOnViewClickListener(new OnViewClickListener() {
                @Override
                public <T> void onClickAction(View v, String tag, T t) {
//                    if (!TextUtils.isEmpty(description)) {
//                        ToastUtil.showToastShort("您已经选择用途了");
//                        return;
//                    }
                    if (hasSelectUse){
                        ToastUtil.showToastShort("您已经选择用途了");
                        return;
                    }
                    if (!hasScanHrCode) {
                        ToastUtil.showToastShort("请先扫描员工ID信息码！");
                        if (selectUseDialog != null && selectUseDialog.isShowing()) {
                            selectUseDialog.dismiss();
                        }
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
            startScanHR();
        });
    }

    private void startScanHR() {
        QRCodeScanActivity.startQRCodeScanner(this, new OnQRScanListenerImpl() {
            @Override
            public void onScanQRCodeSuccess(String result) {
                if (result.startsWith("http")) {
//                    WebViewActivity.gotoWebViewActivity(LoginActivity.this, result);
                } else {
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
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected void ActivityResultSelectDep(Intent data) {
        if (data != null) {
//            SelectDep = (HashMap<String, String>) data.getSerializableExtra("SelectItem");
            departmentBean = data.getParcelableExtra("SelectItem");
            if (departmentBean != null) {
                tvDep.setText(departmentBean.getDepartmentName());
            }
        }
    }

    protected void ActivityResultSelectResearchProgram(Intent data) {
        if (data != null) {
            researchItemBean = data.getParcelableExtra("SelectItem");
            if (researchItemBean != null) {
                tvReseachProgram.setText(researchItemBean.getAbb() + "  --  " + researchItemBean.getProgram());
            }
        }
    }

    private boolean isCurrentSmallPackage = false;

    protected void parseScanResult(String result) {
//        Toast.makeText(this, "Scanned: " + result, Toast.LENGTH_LONG).show();
//        String X = result.getContents();
        if (result.contains("/")) {
            String[] qrContent;
            qrContent = result.split("/");
            if (result.contains("HRID") && result.contains("HRNO")) {
                if (qrContent.length >= 4) {
                    GetHrNameAsyncTask task = new GetHrNameAsyncTask();
                    task.execute(qrContent[2]);
                }
            } else if (qrContent.length >= 2) {
                String qrTitle = qrContent[0];
                if (!qrTitle.equals("")) {
                    if (qrTitle.equals("VE") || qrTitle.equals("VF") || qrTitle.equals("VG") || qrTitle.equals("V9") || qrTitle.equals("VA") || qrTitle.equals("VB") || qrTitle.equals("VC")) {
                        if (qrTitle.equals("VE") || qrTitle.equals("V9")){
                            isCurrentSmallPackage = true;
                        }
                        //物品条码
                        scanstring = result;
                        AsyncGetIssueMoreExtraBox task = new AsyncGetIssueMoreExtraBox();
                        task.execute();
                    }
                }
            }
        }
    }

    private class AsyncGetIssueMoreExtraBox extends AsyncTask<String, Void, Void> {
        private BoxItemEntity boxItemEntity;

        @Override
        protected Void doInBackground(String... params) {
            BoxItemEntity boxItemEntity = WebServiceUtil.op_Check_Commit_Inv_Out_Item_Barcode(UserSingleton.get().getUserInfo().getBu_ID(), scanstring);
            originalScanQty = boxItemEntity.getQty();
            this.boxItemEntity = boxItemEntity;
            if (boxItemEntity.getResult()) {
                if (!is_box_existed(boxItemEntity)) {
                    boxItemEntity.setSelect(true);
                    //限制部门领料，只有扫小包装时才能编辑数量
                    boxItemEntity.setCanNotEdit(true);
                    if (isCurrentSmallPackage){
                        boxItemEntity.setCanNotEdit(false);
                    }

                    boxItemEntityList.add(boxItemEntity);
                } else {
                    boxItemEntity.setResult(false);
                    boxItemEntity.setErrorInfo("该包装已经在装载列表中");
                }
            }
            return null;
        }

        private boolean is_box_existed(BoxItemEntity box_item) {
            boolean result = false;
            if (boxItemEntityList != null) {
                for (int i = 0; i < boxItemEntityList.size(); i++) {
                    if (boxItemEntityList.get(i).getSMLI_ID() == box_item.getSMLI_ID() && box_item.getSMLI_ID() > 0) {
                        return true;
                    }
                    if (boxItemEntityList.get(i).getSMM_ID() == box_item.getSMM_ID() && box_item.getSMM_ID() > 0) {
                        return true;
                    }
                    if (boxItemEntityList.get(i).getSMT_ID() == box_item.getSMT_ID() && box_item.getSMT_ID() > 0) {
                        return true;
                    }
                    if (boxItemEntityList.get(i).getSMT_ID() == box_item.getSMT_ID() && box_item.getSMT_ID() == 0
                            && boxItemEntityList.get(i).getSMM_ID() == box_item.getSMM_ID() && box_item.getSMM_ID() == 0
                            && boxItemEntityList.get(i).getSMLI_ID() == box_item.getSMLI_ID() && box_item.getSMLI_ID() == 0
                            && boxItemEntityList.get(i).getLotID() == box_item.getLotID()) {
                        return true;
                    }
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");
            if (boxItemEntity != null) {
                if (!boxItemEntity.getResult()) {
                    Toast.makeText(StockDepartmentInActivity.this, boxItemEntity.getErrorInfo(), Toast.LENGTH_LONG).show();
                }
            }
            issueMoreItemAdapter.notifyDataSetChanged();
//            recyclerView.setAdapter(issueMoreItemAdapter);
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

    private class WarehouseOutAsyncTask extends AsyncTask<String, Void, Void> {
        private WsResult ws_result;

        @Override
        protected Void doInBackground(String... params) {
            int count = 0;
            int boxItemSize = boxItemEntityList.size();
            while (count < boxItemSize && boxItemEntityList.size() > 0) {
                //// TODO: 2019/9/18 大概可能是修改数量后仍是原来的数量导致 ，其他 的那种修改应该也有这个问题
                BoxItemEntity boxItemEntity = boxItemEntityList.get(0);
                //// TODO: 2019/7/15 确认，这是用途备注
//                boxItemEntity.setLotDescription(useTextView.getText().toString());
//                boxItemEntity.setLotDescription(description);
//                ws_result = WebServiceUtil.op_Commit_Dep_Out_Item(UserSingleton.get().getUserInfo().getBu_ID(), SelectDep, SelectReaseach, bi);

                ws_result = WebServiceUtil.op_Commit_Dep_Out_Item(UserSingleton.get().getUserInfo().getBu_ID(), departmentBean, researchItemBean, boxItemEntity, description);
                if (ws_result.getResult()) {
                    boxItemEntityList.remove(boxItemEntity);
                    //如果实际出库数量小于扫描出的要再处理
//                    if (originalScanQty > boxItemEntity.getQty()){
//                        ws_result = WebServiceUtil.op_Commit_Dep_Out_Item_handle_left(UserSingleton.get().getUserInfo().getBu_ID(), departmentBean, researchItemBean, boxItemEntity, description);
//                    }
                } else {
                    return null;
                }
                count++;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            issueMoreItemAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(issueMoreItemAdapter);
            pbBackground.setVisibility(View.GONE);
            if (ws_result != null) {
                if (!ws_result.getResult()) {
                    CommonUtil.ShowToast(StockDepartmentInActivity.this, ws_result.getErrorInfo(), R.mipmap.warning, Toast.LENGTH_LONG);
                } else {
                    //// TODO: 2019/7/15 哪些页面是执行成功后要跳离的？
                    CommonUtil.ShowToast(StockDepartmentInActivity.this, "成功出库", R.mipmap.smiley, Toast.LENGTH_SHORT);
                    description = "";
                    hasSelectUse = false;
                    finish();
                }
            }
        }

        @Override
        protected void onPreExecute() {
            pbBackground.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

//    private void handleGetAllUserEntity(int hrId) {
//        if (UserSingleton.get().getUserAllInfoEntity() == null) {
//            String sql = "select * from hr where hr_id = " + hrId;
//            QueryAsyncTask query = new QueryAsyncTask();
//            query.execute(sql);
//            query.setLoadDataCompleteListener(new OnLoadDataListener() {
//                @Override public void loadComplete(List<JsonObject> jsonObjectList) {
//                    if (jsonObjectList != null && jsonObjectList.size() > 0) {
//                        //                    JsonObject jsonObject = jsonObjectList.get(0);
//                        UserAllInfoEntity userAllInfoEntity = JsonUtil.parseJsonToObject(jsonObjectList.get(0).toString(), UserAllInfoEntity.class);
//                        if (userAllInfoEntity != null) {
//                            UserSingleton.get().setUserAllInfoEntity(userAllInfoEntity);
//                        }
//                    }
//                }
//            });
//        }
//    }

    //// TODO: 2019/7/15 这里根存储的可能有出入，可能有问题
    private class GetHrNameAsyncTask extends AsyncTask<String, Void, UserAllInfoEntity> {
        //Image hr_photo;
        @Override
        protected UserAllInfoEntity doInBackground(String... params) {
            int hrId = Integer.parseInt(params[0]);
//            userInfo = WebServiceUtil.getHRName_Bu(userInfo.getHR_ID());
//            UserInfoEntity userInfoEntity = WebServiceUtil.getHRName_Bu(hrId);
            //// TODO: 2019/7/15 这里根存储的可能有出入，可能有问题
//            UserSingleton.get().setHRID(hrId);
//            UserSingleton.get().setHRName(userInfo.getHR_Name());
//            UserSingleton.get().setUserInfo(userInfo);
//            if (userInfo != null) {
//                Bitmap userPic = CommonUtil.getUserPic(StockDepartmentInActivity.this, userPictureMap, userInfo.getHR_ID());
//            }


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
//            if (userInfo != null) {
////                setTvUserName();
////                if (pictureBitmap != null) {
////                    avatarImageView.setImageBitmap(pictureBitmap);
////                }
//            } else {
//                Toast.makeText(LoginActivity.this, "无法访问服务器，请检查网络连接是否正常", Toast.LENGTH_LONG).show();
//            }

//            scanProgressBar.setVisibility(View.GONE);
//            if (UserSingleton.get().getUserInfo() != null) {
//                Intent intent = new Intent(StockDepartmentInActivity.this, MobileMainActivity.class);
////            intent.putExtra(IntentConstant.Intent_Extra_hr_id, Integer.parseInt(qrContent[2]));
//                intent.putExtra(IntentConstant.Intent_Extra_hr_id, UserSingleton.get().getHRID());
//                startActivity(intent);
//                finish();
//            }
//            titleTextView.setText("操作员：" + UserSingleton.get().getDepartmentMap().get(userInfoEntity) userInfoEntity.getHR_Name());
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

    @Override
    protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("BoxItemList", (Serializable) boxItemEntityList);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (selectUseDialog != null && selectUseDialog.isShowing()) {
            selectUseDialog.dismiss();
        }
    }
}