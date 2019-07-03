package com.chinashb.www.mobileerp.warehouse;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.commonactivity.SelectItemActivity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.IssueMoreItemAdapter;
import com.chinashb.www.mobileerp.basicobject.MpiWcBean;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 部门领料
 */

public class StockDepartmentInActivity extends AppCompatActivity {
    private Context mContext;
    //private StatedButton mSbtn_back;
    private LinearLayout mLl_parent;
    private MpiWcBean themw;
    private TextView txtMw_Title;
    private Button selectDepartmentButton;
    private Button selectReseachProgramButton;
    private Button scanAddTrayButton;
    private Button outStockButton;
    private TextView tvDep;
    static HashMap<String, String> SelectDep;
    private TextView tvReseachProgram;
    static HashMap<String, String> SelectReaseach;
    private RecyclerView mRecyclerView;
    private IssueMoreItemAdapter issueMoreItemAdapter;
    private List<BoxItemEntity> newissuelist;
    private String scanstring;

    private ProgressBar pbBackground;
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
        setContentView(R.layout.activity_stock_out_dep_layout);
        mContext = this;
        //mLinearLayout=(LinearLayout)findViewById(R.id.box);

        mLl_parent = (LinearLayout) findViewById(R.id.box);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_issue_more_extra);

        txtMw_Title = (TextView) findViewById(R.id.tv_issue_more_mw_title);
        tvDep = (TextView) findViewById(R.id.tv_stock_out_dep_select_dep);
        tvReseachProgram = (TextView) findViewById(R.id.tv_stock_out_dep_select_program);

        pbBackground = (ProgressBar) findViewById(R.id.pb_webservice_progressbar);
        pbBackground.setVisibility(View.GONE);

        selectDepartmentButton = (Button) findViewById(R.id.btn_stock_out_dep_select_dep);
        selectReseachProgramButton = (Button) findViewById(R.id.btn_stock_out_dep_select_program);
        scanAddTrayButton = (Button) findViewById(R.id.btn_issue_more_add_extra);
        outStockButton = (Button) findViewById(R.id.btn_exe_warehouse_out);

        newissuelist = new ArrayList<>();
        if (savedInstanceState != null) {
            newissuelist = (List<BoxItemEntity>) savedInstanceState.getSerializable("BoxItemList");
        }

        issueMoreItemAdapter = new IssueMoreItemAdapter(StockDepartmentInActivity.this, newissuelist);
        issueMoreItemAdapter.showNeedMore = false;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        mRecyclerView.setAdapter(issueMoreItemAdapter);

        //按照上次选择的结果，直接显示
        if (SelectDep != null) {
            tvDep.setText(SelectDep.get("Department_Name"));
        }
        if (SelectReaseach != null) {
            tvReseachProgram.setText(SelectReaseach.get("Abb") + "  --  " + SelectReaseach.get("Program"));
        }
    }

    protected void setButtonListener() {
        selectDepartmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StockDepartmentInActivity.this, SelectItemActivity.class);
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
                intent.putExtra(IntentConstant.Intent_Extra_to_select_search_from_postition,IntentConstant.Select_Search_From_Select_Department);

                startActivityForResult(intent, 100);
            }

        });

        selectReseachProgramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StockDepartmentInActivity.this, SelectItemActivity.class);

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

                startActivityForResult(intent, 200);
            }

        });

        scanAddTrayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(StockDepartmentInActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
            }
        });

        outStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newissuelist.size() > 0) {
                    if (SelectDep == null) {
                        CommonUtil.ShowToast(StockDepartmentInActivity.this, "没有选择部门", R.mipmap.warning);
                        return;
                    }
                    if (SelectReaseach == null) {
                        CommonUtil.ShowToast(StockDepartmentInActivity.this, "没有选择研发项目", R.mipmap.warning);
                        return;
                    }

                    StockDepartmentInActivity.AsyncExeWarehouseOut task = new StockDepartmentInActivity.AsyncExeWarehouseOut();
                    task.execute();
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
                ActivityResultScan(result);
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected void ActivityResultSelectDep(Intent data) {
        if (data != null) {
            SelectDep = (HashMap<String, String>) data.getSerializableExtra("SelectItem");
            if (SelectDep != null) {
                tvDep.setText(SelectDep.get("Department_Name"));
            }
        }
    }

    protected void ActivityResultSelectResearchProgram(Intent data) {
        if (data != null) {
            SelectReaseach = (HashMap<String, String>) data.getSerializableExtra("SelectItem");
            if (SelectReaseach != null) {
                tvReseachProgram.setText(SelectReaseach.get("Abb") + "  --  " + SelectReaseach.get("Program"));
            }
        }
    }

    protected void ActivityResultScan(IntentResult result) {

        Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

        String X = result.getContents();
        if (X.contains("/")) {
            String[] qrContent;
            qrContent = X.split("/");
            if (qrContent.length >= 2) {
                String qrTitle = qrContent[0];

                if (!qrTitle.equals("")) {
                    if (qrTitle.equals("VE") || qrTitle.equals("VF") || qrTitle.equals("VG") || qrTitle.equals("V9") || qrTitle.equals("VA") || qrTitle.equals("VB") || qrTitle.equals("VC")) {
                        //物品条码
                        scanstring = X;
                        AsyncGetIssueMoreExtraBox task = new AsyncGetIssueMoreExtraBox();
                        task.execute();
                    }
                }

            }
        }
    }


    private class AsyncGetIssueMoreExtraBox extends AsyncTask<String, Void, Void> {
        BoxItemEntity scanresult;

        @Override
        protected Void doInBackground(String... params) {

            BoxItemEntity bi = WebServiceUtil.op_Check_Commit_Inv_Out_Item_Barcode(UserSingleton.get().getUserInfo().getBu_ID(), scanstring);

            scanresult = bi;

            if (bi.getResult() == true) {
                if (!is_box_existed(bi)) {
                    bi.setSelect(true);
                    newissuelist.add(bi);
                } else {
                    bi.setResult(false);
                    bi.setErrorInfo("该包装已经在装载列表中");
                }
            }

            return null;
        }


        protected Boolean is_box_existed(BoxItemEntity box_item) {
            Boolean result = false;

            if (newissuelist != null) {
                for (int i = 0; i < newissuelist.size(); i++) {
                    if (newissuelist.get(i).getSMLI_ID() == box_item.getSMLI_ID() && box_item.getSMLI_ID() > 0) {
                        return true;
                    }
                    if (newissuelist.get(i).getSMM_ID() == box_item.getSMM_ID() && box_item.getSMM_ID() > 0) {
                        return true;
                    }
                    if (newissuelist.get(i).getSMT_ID() == box_item.getSMT_ID() && box_item.getSMT_ID() > 0) {
                        return true;
                    }
                    if (newissuelist.get(i).getSMT_ID() == box_item.getSMT_ID() && box_item.getSMT_ID() == 0
                            && newissuelist.get(i).getSMM_ID() == box_item.getSMM_ID() && box_item.getSMM_ID() == 0
                            && newissuelist.get(i).getSMLI_ID() == box_item.getSMLI_ID() && box_item.getSMLI_ID() == 0
                            && newissuelist.get(i).getLotID() == box_item.getLotID()) {
                        return true;
                    }


                }
            }

            return result;
        }


        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");

            if (scanresult != null) {
                if (scanresult.getResult() == false) {
                    Toast.makeText(StockDepartmentInActivity.this, scanresult.getErrorInfo(), Toast.LENGTH_LONG).show();
                }
            }


            issueMoreItemAdapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(issueMoreItemAdapter);

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

    private class AsyncExeWarehouseOut extends AsyncTask<String, Void, Void> {
        WsResult ws_result;

        @Override
        protected Void doInBackground(String... params) {

            int count = 0;
            int newissuesize = newissuelist.size();
            while (count < newissuesize && newissuelist.size() > 0) {
                BoxItemEntity bi = newissuelist.get(0);
                ws_result = WebServiceUtil.op_Commit_Dep_Out_Item(UserSingleton.get().getUserInfo().getBu_ID(), SelectDep, SelectReaseach, bi);

                if (ws_result.getResult() == true) {
                    newissuelist.remove(bi);
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
            mRecyclerView.setAdapter(issueMoreItemAdapter);

            pbBackground.setVisibility(View.GONE);

            if (ws_result != null) {
                if (ws_result.getResult() == false) {
                    CommonUtil.ShowToast(StockDepartmentInActivity.this, ws_result.getErrorInfo(), R.mipmap.warning, Toast.LENGTH_LONG);
                } else {
                    CommonUtil.ShowToast(StockDepartmentInActivity.this, "成功出库", R.mipmap.smiley, Toast.LENGTH_SHORT);
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

        outState.putSerializable("BoxItemList", (Serializable) newissuelist);

    }
}
