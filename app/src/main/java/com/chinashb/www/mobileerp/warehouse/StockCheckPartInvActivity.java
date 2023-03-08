package com.chinashb.www.mobileerp.warehouse;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.basicobject.IstPlaceEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.PanDianItemBean;
import com.chinashb.www.mobileerp.commonactivity.CommonSelectItemActivity;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.commonactivity.SelectItemActivity;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.StringUtils;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.SwitchPanDianTypeDialog;
import com.chinashb.www.mobileerp.widget.TitleLayoutManagerView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 零部件库存盘点
 */
public class StockCheckPartInvActivity extends BaseActivity {
    Button btnSelectCheckFile;
    Button btnScanIst;
    Button btnScanItem;
    Button btnCommit;

    //    TextView tvCI;
    private TitleLayoutManagerView titleLayoutManagerView;
    Integer CI_ID;
    Boolean ShowERPInv = false;
    private String ScanFor;
    private IstPlaceEntity thePlace;
    private BoxItemEntity boxItemEntity;
    private List<BoxItemEntity> boxItemEntityList;
    private TextView tvIst;
    private TextView tvERPIst;
    private TextView tvItemCode;
    private TextView tvManuLotno;
    private TextView tvLeftQty;
    private TextView tvItemName;
    private TextView tvBoxName;
    private EditText etN;
    private EditText etPN;
    private EditText etDQ;
    private TextView realQtyTextView;
    private Button btnCal;
    private EditText etRemark;
    private EditText inputEditText;
    private EditText searchEditText;
    private Button searchButton;
    private RelativeLayout searchLayout;

    private int Ac_Type = 1;
//    private UserInfoEntity userInfo;

    private String qty = "";
    private String remark = "";

    private String N = "";
    private String PN = "";
    private String DQ = "";
    private boolean inventoryFileSelect = false;
    private boolean istHasSelect = false;
    private boolean fromZaiZhiPin = false;
    private boolean fromSelfProduct;
    private SwitchPanDianTypeDialog panDianTypeDialog;
    private int pandianType = 1;
    private PanDianItemBean panDianItemBean;
    private EditText storeAreaEditText;
    private EditText manuLotEditText;
    private String storeArea = "";
    private String manuLotNO = "";
//    private boolean hasScannItemClickButtonForPhoto = false;
    private boolean fromPart = false;
    private LinearLayout originalDataLayout;
    private LinearLayout productSearchLayout;
    private RecyclerView productSearchRecyclerView;

    public StockCheckPartInvActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //todo  这个页面还没有统一
        setContentView(R.layout.activity_stock_check_part_inv);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        setHomeButton();
        bindView();
        setButtonClick();
        getExtras();
//        userInfo = UserSingleton.get().getUserInfo();
        boxItemEntityList = new ArrayList<>();
//        resumestate(savedInstanceState);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private void getExtras() {
        Intent who = getIntent();
        Ac_Type = (Integer) who.getIntExtra("Ac_Type", 1);
        fromZaiZhiPin = who.getBooleanExtra(IntentConstant.Intent_Extra_check_from_zaizhipin, false);
        fromSelfProduct = who.getBooleanExtra(IntentConstant.Intent_Extra_check_self_product, false);
        fromPart = who.getBooleanExtra(IntentConstant.Intent_Extra_check_part, false);
        if (fromZaiZhiPin) {
//            btnScanIst.setVisibility(View.GONE);
            searchLayout.setVisibility(View.VISIBLE);
            tvIst.setVisibility(View.GONE);
            tvManuLotno.setVisibility(View.GONE);
            storeAreaEditText.setVisibility(View.VISIBLE);
            manuLotEditText.setVisibility(View.VISIBLE);
            if (panDianTypeDialog == null) {
                panDianTypeDialog = new SwitchPanDianTypeDialog(StockCheckPartInvActivity.this);
                panDianTypeDialog.show();
                panDianTypeDialog.setOnViewClickListener(new OnViewClickListener() {
                    @Override public <T> void onClickAction(View v, String tag, T t) {
                        if (t instanceof Integer) {
                            pandianType = (Integer) t;
                            if (pandianType == 1) {
                                Ac_Type = 14;
                            } else if (pandianType == 2) {
                                Ac_Type = 13;
                            }
                        }
                        panDianTypeDialog.dismiss();

                    }
                });
            }
        }else {
            //其实这个暂时意义不大，因为共用页面，需要整合
            if (fromSelfProduct){
                searchLayout.setVisibility(View.VISIBLE);

            }
        }
        productSearchLayout.setVisibility(View.GONE);
    }

    void bindView() {
        btnSelectCheckFile = (Button) findViewById(R.id.btn_select_check_inv);
        btnScanIst = (Button) findViewById(R.id.btn_select_check_inv_scan_ist);
        btnScanItem = (Button) findViewById(R.id.btn_select_check_inv_add_item);
        btnCommit = (Button) findViewById(R.id.btn_affirm_qty);
        btnCal = (Button) findViewById(R.id.btn_check_inv_cal_qty);
//        tvCI = (TextView) findViewById(R.id.tv_checkinventory_file);
        titleLayoutManagerView = findViewById(R.id.stock_check_part_titleLayout);
        tvIst = (TextView) findViewById(R.id.tv_check_stock_ist);

        tvERPIst = (TextView) findViewById(R.id.tv_check_stock_ist_erp);
        tvItemCode = (TextView) findViewById(R.id.tv_check_stock_item_code);
        tvManuLotno = (TextView) findViewById(R.id.tv_check_stock_manulotno);
        tvItemName = (TextView) findViewById(R.id.tv_check_stock_item_name);
        tvBoxName = (TextView) findViewById(R.id.tv_check_stock_box);
        tvLeftQty = (TextView) findViewById(R.id.tv_check_stock_box_left_qty);
        realQtyTextView = (TextView) findViewById(R.id.et_check_stock_box_real_qty);
        etN = (EditText) findViewById(R.id.et_check_stock_box_n);
        etPN = (EditText) findViewById(R.id.et_check_stock_box_pn);
        etDQ = (EditText) findViewById(R.id.et_check_stock_box_dq);
        etRemark = (EditText) findViewById(R.id.et_check_stock_box_remark);
        inputEditText = findViewById(R.id.content_stock_check_part_input_EditeText);

        searchEditText = findViewById(R.id.check_inv_search_editText);
        searchButton = findViewById(R.id.btn_check_inv_search);
        searchLayout = findViewById(R.id.check_inv_search_layout);

        storeAreaEditText = findViewById(R.id.et_check_stock_ist);
        manuLotEditText = findViewById(R.id.et_check_stock_manulotno);

        originalDataLayout = findViewById(R.id.check_part_inv_original_data_layout);
        productSearchLayout = findViewById(R.id.check_part_inv_product_serarch_data_layout);
        productSearchRecyclerView = findViewById(R.id.check_part_inv_product_search_recyclerView);


    }

//    private void resumestate(Bundle savedInstanceState) {
//        if (savedInstanceState != null) {
//            CI_ID = savedInstanceState.getInt("CI_ID");
//            thePlace = (IstPlaceEntity) savedInstanceState.getSerializable("thePlace");
//            scanitem = (BoxItemEntity) savedInstanceState.getSerializable("scanitem");
//            scanstring = savedInstanceState.getString("scanstring");
//            ScanFor = savedInstanceState.getString("ScanFor");
//            saveditems = (List<BoxItemEntity>) savedInstanceState.getSerializable("saveditems");
//            //还需恢复界面数据
//        }
//    }

    void setButtonClick() {
        btnSelectCheckFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StockCheckPartInvActivity.this, SelectItemActivity.class);
//                Intent intent = new Intent(StockCheckPartInvActivity.this, CommonSelectItemActivity.class);
                Integer Bu_ID = UserSingleton.get().getUserInfo().getBu_ID();


//                String sql = "Select CI_ID, CI_Name , Editor_name , Convert(nvarchar(100),CheckDate,20)," +
//                        "Isnull(ShowERPInv,0) As ShowERPInv  From CheckInventory " +
//                        "Inner Join (" +
//                        W + ") As W On W.Warehouse_ID=CheckInventory.Warehouse_ID " +
//                        "Where Bu_ID=" + Bu_ID + " And Wc_ID Is null And Ac_Type=" + Ac_Type ;
                String sql = "";
                if (Ac_Type == 13 || Ac_Type == 14){

                     sql = "Select CI_ID, CI_Name , Editor_name , Convert(nvarchar(100),CheckDate,20),Isnull(ShowERPInv,0) As ShowERPInv  From CheckInventory\n" +
                            " Where Bu_ID=" + Bu_ID + " And  Ac_Type=" + Ac_Type + " And Datediff(day, Insert_Time, Getdate())<100 ";
                }else if (Ac_Type == 1 || Ac_Type == 2){
                    String W = "Select Distinct Warehouse_ID From Bu_W_Ac " +
                            "Inner Join Bu_Ac On Bu_W_Ac.Ac_Book_ID=Bu_Ac.Ac_Book_ID " +
                            "Where Bu_ID=" + Bu_ID + " And Ac_Type = " + Ac_Type;
//
                     sql = "Select CI_ID, CI_Name , Editor_name , Convert(nvarchar(100),CheckDate,20) ," +
                            "Isnull(ShowERPInv,0) As ShowERPInv  From CheckInventory " +
                            "Inner Join (" +
                            W + ") As W On W.Warehouse_ID=CheckInventory.Warehouse_ID " +
                            "Where Bu_ID=" + Bu_ID + " And Wc_ID Is null And Ac_Type=" + Ac_Type +
                            " And " +
//                        "Datediff(day, Insert_Time, Getdate())<30 ";
                            "Datediff(day, Insert_Time, Getdate())<100 ";
                }


                List<Integer> ColWith = new ArrayList<Integer>(Arrays.asList(80, 120, 120, 120));
                List<String> ColCaption = new ArrayList<String>(Arrays.asList("CI_ID", "盘存名称", "盘存者", "盘存日期"));
                List<String> HiddenCol = new ArrayList<String>(Arrays.asList("ShowERPInv"));

                String Title = "选择盘点文件";
                intent.putExtra("Title", Title);
                intent.putExtra("SQL", sql);
                intent.putExtra("ColWidthList", (Serializable) ColWith);
                intent.putExtra("ColCaptionList", (Serializable) ColCaption);
                intent.putExtra("hiddenColList", (Serializable) HiddenCol);
                intent.putExtra(IntentConstant.Intent_Extra_to_select_search_from_postition, IntentConstant.Select_Search_From_Select_Check_File);

                startActivityForResult(intent, 200);

            }
        });


        btnScanIst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SelectCI == null) {
                    Toast.makeText(StockCheckPartInvActivity.this, "请先选择盘点表", Toast.LENGTH_LONG).show();
                    inputEditText.setText("");
                    return;
                }
                ScanFor = "Ist";
                new IntentIntegrator(StockCheckPartInvActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();

            }
        });


        btnScanItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (thePlace == null) {
//                    Toast.makeText(StockCheckPartInvActivity.this, "请先扫描托盘所在的仓库单元位置条码", Toast.LENGTH_LONG).show();
                    ToastUtil.showToastLong("请先扫描托盘所在的仓库单元位置条码");
                    inputEditText.setText("");
                    return;
                }
                ScanFor = "Item";
//                hasScannItemClickButtonForPhoto = true;
                new IntentIntegrator(StockCheckPartInvActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();

            }
        });


        btnCommit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (SelectCI == null) {
                    Toast.makeText(StockCheckPartInvActivity.this, "请先选择盘点表", Toast.LENGTH_LONG).show();
                    return;
                }
//                if (!fromZaiZhiPin) {
//                    if (thePlace == null) {
//                        Toast.makeText(StockCheckPartInvActivity.this, "请先扫描托盘所在的仓库单元位置条码", Toast.LENGTH_LONG).show();
//                        return;
//                    }
//                }
//                if (!fromZaiZhiPin) {
//                    if (scanitem == null) {
//                        Toast.makeText(StockCheckPartInvActivity.this, "请扫描物料", Toast.LENGTH_LONG).show();
//                        return;
//                    }
//                    if (is_box_existed(scanitem)) {
//                        CommonUtil.ShowToast(StockCheckPartInvActivity.this,
//                                "前面已经提交过", R.mipmap.warning, Toast.LENGTH_SHORT);
//                        return;
//                    }
//                }

                String qty = realQtyTextView.getText().toString();

                if (qty.equals("")) {
                    ToastUtil.showToastShort("数量为空!");
                    return;
                }


//                if (fromZaiZhiPin){
//                    if (!StringUtils.isStringValid(etRemark.getText().toString())){
//                        ToastUtil.showToastShort("在制品盘存需要输入备注!");
//                        return;
//                    }
//                }

//                if (fromZaiZhiPin){
//                    if (storeArea == null || storeArea.length() == 0){
//                        ToastUtil.showToastShort("库位位置为空!");
//                        return;
//                    }
//
//                    if (manuLotNO == null || manuLotNO.length() == 0){
//                        ToastUtil.showToastShort("生产批次为空!");
//                        return;
//                    }
//                }

                Commit_Result();

            }
        });

        btnCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boxItemEntity == null && panDianItemBean == null){
                    ToastUtil.showToastShort("未选择物料，请扫描物料或搜索！");
                    return;
                }
                Integer n = 1;
                if (etN.getText().toString().equals("")) {
                    etN.setText("1");
                }
                n = Integer.valueOf(etN.getText().toString());

                double pn = 1;
                if (etPN.getText().toString().equals("")) {
                    etPN.setText("1");
                }

                pn = Double.valueOf(etPN.getText().toString());

                double dq = 0;
                if (etDQ.getText().toString().equals("")) {
                    etDQ.setText("0");
                }
                dq = Double.valueOf(etDQ.getText().toString());

                double q;
                q = n * pn + dq;
                realQtyTextView.setText(CommonUtil.DecimalFormat(q));
            }
        });

        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                if (TextUtils.isEmpty(editable.toString())) {
                    return;
                }
                if (!inventoryFileSelect) {
                    ToastUtil.showToastShort("请先选择盘点表！");
                    inputEditText.setText("");
                    return;
                }
                //todo 奇怪的这个endwith \n 居然不执行
//                if (editable.toString().endsWith("\n")){
//                    ToastUtil.showToastLong("扫描结果:" + editable.toString());
                System.out.println("========================扫描结果:" + editable.toString());
                //// TODO: 2019/12/10 scanfor之类的可能无用
//                    if (ScanFor.endsWith("Ist")) {
//                        ActivityResultScanIst(inputEditText.getText().toString());
//                    } else {
//                        ActivityResultScanItem(inputEditText.getText().toString());
//                    }
                parseScanData(editable.toString());
//                }
            }
        });

        storeAreaEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override public void afterTextChanged(Editable editable) {
                storeArea = editable.toString();
            }
        });

        manuLotEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override public void afterTextChanged(Editable editable) {
                manuLotNO = editable.toString();
            }
        });

        realQtyTextView.addTextChangedListener(new TextWatcherImpl() {
            @Override public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                qty = editable.toString();
            }
        });

        etRemark.addTextChangedListener(new TextWatcherImpl() {
            @Override public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                remark = editable.toString();
//                parseScanData(editable.toString());
            }
        });
        etN.addTextChangedListener(new TextWatcherImpl() {
            @Override public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                N = editable.toString();
//                parseScanData(editable.toString());
            }
        });
        etPN.addTextChangedListener(new TextWatcherImpl() {
            @Override public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                PN = editable.toString();
//                parseScanData(editable.toString());
            }
        });
        etDQ.addTextChangedListener(new TextWatcherImpl() {
            @Override public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                DQ = editable.toString();
//                parseScanData(editable.toString());
            }
        });

        searchButton.setOnClickListener(v -> {

            if (!StringUtils.isStringValid(searchEditText.getText().toString())) {
                ToastUtil.showToastShort("您的搜索内容为为空！");
                return;
            }
            if (!inventoryFileSelect) {
                ToastUtil.showToastShort("请先选择盘点表！");
                return;
            }

            if (!fromZaiZhiPin) {
                if (thePlace == null) {
                    //                    Toast.makeText(StockCheckPartInvActivity.this, "请先扫描托盘所在的仓库单元位置条码", Toast.LENGTH_LONG).show();
                    ToastUtil.showToastLong("请先扫描托盘所在的仓库单元位置条码");
                    return;
                }
            }

            String input = searchEditText.getText().toString();
//            if (input.contains(" ")){
//
//            }else{
//
//            }
//            String whereSql = " and CF_Chinese_Name like '%"+keyWord+"%'";
            if (fromSelfProduct){
//                QueryProductStockAsyncTask productStockAsyncTask = new QueryProductStockAsyncTask();
//                productStockAsyncTask.execute(input);
                String sql = String.format("Select top 60 Item.Item_ID,Item_Version.IV_ID,product.Product_DrawNO as Item_Name, Item_Version.Item_Version As Version,Item.Item_Unit_Exchange ,Item.Item_Unit ,product.Product_DrawNO " +
                        "From product inner join Item on product.item_id = item.item_id Inner Join Item_Version On Item.Item_ID = Item_Version.Item_ID Where  (product.Product_DrawNO like %s or product.current_ps like %s or item.item_id like %s) ", "'%" + input + "%'", "'%" + input + "%'", "'%" + input + "%'");

                Intent intent = new Intent(StockCheckPartInvActivity.this, CommonSelectItemActivity.class);
                List<Integer> ColWith = new ArrayList<Integer>(Arrays.asList(50, 100, 100));
                List<String> ColCaption = new ArrayList<String>(Arrays.asList("Item_ID", "IV_ID", "物料", "版本"));

                String Title = "选择物料";
                intent.putExtra("Title", Title);
                intent.putExtra("SQL", sql);
                intent.putExtra("ColWidthList", (Serializable) ColWith);
                intent.putExtra("ColCaptionList", (Serializable) ColCaption);
                intent.putExtra(IntentConstant.Intent_Extra_to_select_search_from_postition, IntentConstant.Select_Search_From_Select_PanDina);

                startActivityForResult(intent, 500);

            }else{

                String sql = String.format("Select top 60 Item.Item_ID,Item_Version.IV_ID,Item.Item+' '+Item.Item_Name+' '+isnull(Item.Item_Spec2,'') As Item_Name, Item_Version.Item_Version As Version,Item.Item_Unit_Exchange ,Item.Item_Unit  " +
                        "From Item Inner Join Item_Version On Item.Item_ID = Item_Version.Item_ID Where   (item.item like %s or item.item_drawno like %s or item.item_id like %s) ", "'%" + input + "%'", "'%" + input + "%'", "'%" + input + "%'");

                Intent intent = new Intent(StockCheckPartInvActivity.this, CommonSelectItemActivity.class);
                List<Integer> ColWith = new ArrayList<Integer>(Arrays.asList(50, 100, 100));
                List<String> ColCaption = new ArrayList<String>(Arrays.asList("Item_ID", "IV_ID", "物料", "版本"));

                String Title = "选择物料";
                intent.putExtra("Title", Title);
                intent.putExtra("SQL", sql);
                intent.putExtra("ColWidthList", (Serializable) ColWith);
                intent.putExtra("ColCaptionList", (Serializable) ColCaption);
                intent.putExtra(IntentConstant.Intent_Extra_to_select_search_from_postition, IntentConstant.Select_Search_From_Select_PanDina);

                startActivityForResult(intent, 500);
            }
        });


    }

//    private void setEditTextListener(EditText editText , String text){
//        editText.addTextChangedListener(new TextWatcherImpl(){
//            @Override public void afterTextChanged(Editable editable) {
//                super.afterTextChanged(editable);
//                text = editable.toString();
//            }
//        });
//    }

    protected void Commit_Result() {
        CommitStockResultAsyncTask task = new CommitStockResultAsyncTask();
        task.execute();
    }


    protected Boolean is_box_existed(BoxItemEntity box_item) {
        Boolean result = false;

        if (boxItemEntityList != null) {
            for (int i = 0; i < boxItemEntityList.size(); i++) {
                if (boxItemEntityList.get(i).getSMT_ID() == box_item.getSMT_ID()
                        && boxItemEntityList.get(i).getSMM_ID() == box_item.getSMM_ID()
                        && boxItemEntityList.get(i).getSMLI_ID() == box_item.getSMLI_ID() &&
                        // TODO: 2019/12/3 扫描入库的判断是   if (boxItemEntityList.get(i).getDIII_ID() == box_item.getDIII_ID()) {
                        //                        return true;
                        //                    }
//                        saveditems.get(i).getLotID() == box_item.getLotID() ) {
                        boxItemEntityList.get(i).getLotID() == box_item.getLotID() && boxItemEntityList.get(i).getDIII_ID() == box_item.getDIII_ID()) {
                    return true;
                }


            }
        }

        return result;
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
        //选择盘点文件
        if (requestCode == 200 && resultCode == 1) {
            ActivityResultSelectInventoryFile(data);
            return;
        } else if (requestCode == 500 && resultCode == 1) {
            //选择盘点物料，手动搜索
            panDianItemBean = data.getParcelableExtra("SelectItem");
            handlePandianItem(panDianItemBean);
            return;
        }

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {

            if (result.getContents() == null) {
            } else {
//                if (ScanFor.endsWith("Ist")) {
//                    ActivityResultScanIst(result.getContents());
//                } else {
//                    ActivityResultScanItem(result.getContents());
//                }
                parseScanData(result.getContents());

            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handlePandianItem(PanDianItemBean panDianItemBean) {
        tvItemCode.setText("暂无相关条码");

        //// TODO: 2020/12/29  重复的判断
        DecimalFormat DF = new DecimalFormat("####.####");
//        tvERPIst.setText(bi.getIstName());
        tvERPIst.setText("");
//        if (thePlace.getIst_ID() != bi.getIst_ID() || thePlace.getSub_Ist_ID() != bi.getSub_Ist_ID()) {
//            tvERPIst.setTextColor(Color.RED);
//        } else {
//            tvERPIst.setTextColor(Color.BLACK);
//        }
        tvItemName.setText(panDianItemBean.getItem_Name());
//        tvBoxName.setText(bi.getBoxNameNo());
//        tvManuLotno.setText(bi.getManuLotNo());
        if (!ShowERPInv) {
            tvLeftQty.setVisibility(View.INVISIBLE);
        } else {
            tvLeftQty.setVisibility(View.VISIBLE);
        }
//        tvLeftQty.setText(DF.format(bi.getQty()));
//        //realQtyTextView.setText(DF.format(bi.getBoxQty()));
//        if (bi.getQty() != bi.getBoxQty()) {
//            tvLeftQty.setTextColor(Color.RED);
//        } else {
//            tvLeftQty.setTextColor(Color.BLACK);
//        }

        etPN.setText(panDianItemBean.getItem_Unit_Exchange() + "");
        inputEditText.setText("");
        inputEditText.findFocus();


    }

    static HashMap<String, String> SelectCI;

    protected void ActivityResultSelectInventoryFile(Intent data) {
        if (data != null) {
//            SelectCI= (HashMap<String,String>) data.getSerializableExtra("SelectItem");
            SelectCI = (HashMap<String, String>) data.getSerializableExtra("SelectItem");
            if (SelectCI != null) {
//                tvCI.setText(SelectCI.get("CI_Name"));
                titleLayoutManagerView.setTitle(SelectCI.get("CI_Name"));
                CI_ID = Integer.valueOf(SelectCI.get("CI_ID"));
                ShowERPInv = Boolean.valueOf(SelectCI.get("ShowERPInv"));

                //空
//                thePlace = null;
                tvIst.setText("");

                boxItemEntity = null;
                inventoryFileSelect = true;
            }
        }
    }


    private void parseScanData(String result) {
        //// TODO: 2019/12/10 这块逻辑可优化
        if (result.contains("/")) {
            System.out.println("result = " + result);
            if (!inventoryFileSelect) {
                ToastUtil.showToastShort("请先选择盘点表！");
                inputEditText.setText("");
                return;
            }
            String[] qrContent;
            qrContent = result.split("/");
            if (qrContent.length >= 2) {
                String qrTitle = qrContent[0];
                if (!qrTitle.equals("")) {
                    if (qrTitle.equals("VE") || qrTitle.equals("VF") || qrTitle.equals("VG") || qrTitle.equals("V9") || qrTitle.equals("VA") || qrTitle.equals("VB") || qrTitle.equals("VC")) {
                        //物品条码
                        if (!istHasSelect) {
                            ToastUtil.showToastShort("请先扫描库位码！");
                            inputEditText.setText("");
                            return;
                        }
                        scanstring = result;
                        AsyncGetInvBox task = new AsyncGetInvBox();
                        task.execute();
                    }
                }

                if (result.startsWith("/SUB_IST_ID/") || result.startsWith("/IST_ID/")) {
                    //仓库位置码
                    scanstring = result;
                    GetIstAsynctTask task = new GetIstAsynctTask();
                    task.execute();
                }
            }
        }
    }

    //    protected void ActivityResultScanIst(String result) {
////        Toast.makeText(this, "Scanned: " + result, Toast.LENGTH_LONG).show();
////        String X = result.getContents();
////        String X = result;
//        if (result.startsWith("/SUB_IST_ID/") || result.startsWith("/IST_ID/")) {
//            //仓库位置码
//            scanstring = result;
//            GetIstAsynctTask task = new GetIstAsynctTask();
//            task.execute();
//        }
//    }
    String scanstring;

//    protected void ActivityResultScanItem(String result) {
////        Toast.makeText(this, "Scanned: " + result, Toast.LENGTH_LONG).show();
////        String X = result.getContents();
//        scanstring = result;
//        if (result.contains("/")) {
//            String[] qrContent;
//            qrContent = result.split("/");
//            if (qrContent.length >= 2) {
//                String qrTitle = qrContent[0];
//                if (!qrTitle.equals("")) {
//                    if (qrTitle.equals("VE") || qrTitle.equals("VF") || qrTitle.equals("VG") || qrTitle.equals("V9") || qrTitle.equals("VA") || qrTitle.equals("VB") || qrTitle.equals("VC")) {
//                        //物品条码
//                        AsyncGetInvBox task = new AsyncGetInvBox();
//                        task.execute();
//                    }
//                }
//
//            }
//        }
//    }


    @Override
    protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }


//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        outState.putSerializable("CI_ID", CI_ID);
//        outState.putSerializable("thePlace", (Serializable) thePlace);
//        outState.putSerializable("scanitem", (Serializable) scanitem);
//        outState.putSerializable("scanstring", (Serializable) scanstring);
//        outState.putSerializable("ScanFor", (Serializable) ScanFor);
//
//        outState.putSerializable("saveditems", (Serializable) saveditems);
//
//    }

    private class AsyncGetInvBox extends AsyncTask<String, Void, Void> {
        BoxItemEntity boxItemEntity;

        @Override
        protected Void doInBackground(String... params) {

            BoxItemEntity bi = null;
            //// TODO: 2019/12/12 actype = 1 是零部件，2是成品 ，以下是原来代码注释掉
//            if (Ac_Type == 1 && userInfo.getBu_ID() == 1) {
//                bi = WebServiceUtil.op_Check_Stock_Item_Barcode(userInfo.getBu_ID(), scanstring);
//            } else {
//                bi = WebServiceUtil.op_Check_Stock_Item_Barcode_V2(scanstring);
//            }

            if (Ac_Type == 1) {
                bi = WebServiceUtil.op_Check_Stock_Item_Barcode(UserSingleton.get().getUserInfo().getBu_ID(), scanstring);
            } else {
                bi = WebServiceUtil.op_Check_Stock_Item_Barcode_V2(scanstring);
            }


            boxItemEntity = bi;


            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");

            tvItemCode.setText(scanstring);
            if (boxItemEntity != null) {
                if (!boxItemEntity.getResult()) {
                    Toast.makeText(StockCheckPartInvActivity.this, boxItemEntity.getErrorInfo(), Toast.LENGTH_LONG).show();
                    inputEditText.setText("");
                    return;
                }

                BoxItemEntity bi = boxItemEntity;
                if (bi.getResult()) {
                    if (is_box_existed(bi)) {
                        CommonUtil.ShowToast(StockCheckPartInvActivity.this,
                                "前面已经扫描过", R.mipmap.warning, Toast.LENGTH_SHORT);
                        return;
                    }
                    //暂存一下
                    StockCheckPartInvActivity.this.boxItemEntity = bi;
                    DecimalFormat DF = new DecimalFormat("####.####");
                    tvERPIst.setText(bi.getIstName());
                    if (thePlace.getIst_ID() != bi.getIst_ID() || thePlace.getSub_Ist_ID() != bi.getSub_Ist_ID()) {
                        tvERPIst.setTextColor(Color.RED);
                    } else {
                        tvERPIst.setTextColor(Color.BLACK);
                    }
                    tvItemName.setText(bi.getItemName());
                    tvBoxName.setText(bi.getBoxNameNo());
                    tvManuLotno.setText(bi.getManuLotNo());

                    if (!ShowERPInv) {
                        tvLeftQty.setVisibility(View.INVISIBLE);
                    } else {
                        tvLeftQty.setVisibility(View.VISIBLE);
                    }
                    tvLeftQty.setText(DF.format(bi.getQty()));
                    //realQtyTextView.setText(DF.format(bi.getBoxQty()));
                    if (bi.getQty() != bi.getBoxQty()) {
                        tvLeftQty.setTextColor(Color.RED);
                    } else {
                        tvLeftQty.setTextColor(Color.BLACK);
                    }

                    if (fromZaiZhiPin){
                        tvLeftQty.setVisibility(View.GONE);
//                        storeAreaEditText.setVisibility(View.GONE);
//                        manuLotEditText.setVisibility(View.GONE);


                    }
                    inputEditText.setText("");
                    inputEditText.findFocus();
                }
            }


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


    private class GetIstAsynctTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            IstPlaceEntity istPlaceEntity = WebServiceUtil.op_Check_Commit_IST_Barcode(scanstring);
            if (istPlaceEntity.getResult()) {
                thePlace = istPlaceEntity;
                //清空
                boxItemEntity = null;
            } else {
                Toast.makeText(StockCheckPartInvActivity.this, istPlaceEntity.getErrorInfo(), Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (thePlace != null) {
                tvIst.setText(thePlace.getIstName());
                inputEditText.setText("");
                inputEditText.setHint("请继续扫描");
                istHasSelect = true;
            } else {
                inputEditText.setText("");
            }

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


    private class CommitStockResultAsyncTask extends AsyncTask<String, Void, Void> {
        WsResult ws_result;

        @Override

        protected Void doInBackground(String... params) {
            if (fromZaiZhiPin) {
//                ws_result = WebServiceUtil.op_Commit_Stock_Result_V4(UserSingleton.get().getHRName(),
//                        CI_ID, UserSingleton.get().getUserInfo().getBu_ID(), "", thePlace.getIst_ID(), thePlace.getSub_Ist_ID(),
//                        0, 0, 0, 0, qty, N, PN, DQ, remark);
//
//                ws_result = WebServiceUtil.op_Commit_Stock_Result_V4(UserSingleton.get().getHRName(),CI_ID,UserSingleton.get().getUserInfo().getBu_ID(),"",
//                        thePlace.getIst_ID(),thePlace.getSub_Ist_ID(),0L,0L,0L,0L,qty,N,PN,DQ,remark);
                if ( boxItemEntity != null ) {
                    //扫描模式
                    BoxItemEntity bi = boxItemEntity;
                    if (Ac_Type == 1) {
                        //todo  && userInfo.getBu_ID() == 1
                        ws_result = WebServiceUtil.op_Commit_Stock_Result_V4(UserSingleton.get().getHRName(),
                                CI_ID, UserSingleton.get().getUserInfo().getBu_ID(), scanstring, thePlace.getIst_ID(), thePlace.getSub_Ist_ID(),
                                bi.getSMT_ID(), bi.getSMM_ID(), bi.getSMLI_ID(), bi.getLotID(), qty, N, PN, DQ, remark);

                    } else {
                        ws_result = WebServiceUtil.op_Commit_Stock_Result_V3(UserSingleton.get().getHRName(),
                                CI_ID, scanstring, thePlace.getIst_ID(), thePlace.getSub_Ist_ID(),
                                bi.getSMT_ID(), bi.getSMM_ID(), bi.getSMLI_ID(), qty, N, PN, DQ, remark);
                    }
                } else {
                    if (panDianItemBean != null){
                        //输入模式
                        ws_result = WebServiceUtil.commit_Self_Product_Pandian(UserSingleton.get().getHRName(), CI_ID, UserSingleton.get().getUserInfo().getBu_ID(), "",
                                thePlace != null ? thePlace.getIst_ID() : 0, thePlace != null ? thePlace.getSub_Ist_ID() : 0, panDianItemBean.getItem_ID(), panDianItemBean.getIV_ID(), 0L, qty, N, PN, DQ, remark, storeArea, manuLotNO);

                    }
                   }

            } else {
                if (fromSelfProduct){
                    if ( boxItemEntity != null ) {
                        BoxItemEntity bi = null;
                             bi= boxItemEntity;
                        //解决编译通过但报错的问题
//            String qty = realQtyTextView.getText().toString();
//            String remark = etRemark.getText().toString();
//
//            String N = etN.getText().toString();
//            String PN = etPN.getText().toString();
//            String DQ = etDQ.getText().toString();

                        if(bi != null && bi.getItem_ID() > 0){

                            if (Ac_Type == 1) {
                                //todo  && userInfo.getBu_ID() == 1
                                ws_result = WebServiceUtil.op_Commit_Stock_Result_V4(UserSingleton.get().getHRName(),
                                        CI_ID, UserSingleton.get().getUserInfo().getBu_ID(), scanstring, thePlace.getIst_ID(), thePlace.getSub_Ist_ID(),
                                        bi.getSMT_ID(), bi.getSMM_ID(), bi.getSMLI_ID(), bi.getLotID(), qty, N, PN, DQ, remark);

                            } else {
                                ws_result = WebServiceUtil.op_Commit_Stock_Result_V3(UserSingleton.get().getHRName(),
                                        CI_ID, scanstring, thePlace.getIst_ID(), thePlace.getSub_Ist_ID(),
                                        bi.getSMT_ID(), bi.getSMM_ID(), bi.getSMLI_ID(), qty, N, PN, DQ, remark);
                            }
                        }
                    }else{
                        if (panDianItemBean != null){

                            ws_result = WebServiceUtil.commit_Self_Product_Pandian(UserSingleton.get().getHRName(), CI_ID, UserSingleton.get().getUserInfo().getBu_ID(), "",
                                    thePlace != null ? thePlace.getIst_ID() : 0, thePlace != null ? thePlace.getSub_Ist_ID() : 0, panDianItemBean.getItem_ID(), panDianItemBean.getIV_ID(), 0L, qty, N, PN, DQ, remark, storeArea, manuLotNO);
                        }

                    }




                }else {
                    if (fromPart){
                        //boxitemEntity就扫描，即相机或pda，而pandianbean是搜索的物料
                        if ( boxItemEntity != null && panDianItemBean == null  ) {
                            BoxItemEntity bi = null;
                            bi= boxItemEntity;
                            //解决编译通过但报错的问题
//            String qty = realQtyTextView.getText().toString();
//            String remark = etRemark.getText().toString();
//
//            String N = etN.getText().toString();
//            String PN = etPN.getText().toString();
//            String DQ = etDQ.getText().toString();

                            if(bi != null && bi.getItem_ID() > 0){

//                                if (Ac_Type == 1) {
                                    //todo  && userInfo.getBu_ID() == 1
                                    ws_result = WebServiceUtil.op_Commit_Stock_Result_V4(UserSingleton.get().getHRName(),
                                            CI_ID, UserSingleton.get().getUserInfo().getBu_ID(), scanstring, thePlace.getIst_ID(), thePlace.getSub_Ist_ID(),
                                            bi.getSMT_ID(), bi.getSMM_ID(), bi.getSMLI_ID(), bi.getLotID(), qty, N, PN, DQ, remark);

//                                } else {
//                                    ws_result = WebServiceUtil.op_Commit_Stock_Result_V3(UserSingleton.get().getHRName(),
//                                            CI_ID, scanstring, thePlace.getIst_ID(), thePlace.getSub_Ist_ID(),
//                                            bi.getSMT_ID(), bi.getSMM_ID(), bi.getSMLI_ID(), qty, N, PN, DQ, remark);
//                                }
                            }
                        }else if (panDianItemBean != null){
                            //零件也可以调这个方法
                            ws_result = WebServiceUtil.commit_Self_Product_Pandian(UserSingleton.get().getHRName(), CI_ID, UserSingleton.get().getUserInfo().getBu_ID(), "",
                                    thePlace != null ? thePlace.getIst_ID() : 0, thePlace != null ? thePlace.getSub_Ist_ID() : 0, panDianItemBean.getItem_ID(), panDianItemBean.getIV_ID(), 0L, qty, N, PN, DQ, remark, storeArea, manuLotNO);

                        }




                    }


                }


            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (ws_result.getResult()) {
                CommonUtil.ShowToast(StockCheckPartInvActivity.this,
                        "提交成功", R.mipmap.smiley, Toast.LENGTH_SHORT);

                boxItemEntityList.add(boxItemEntity);

                //Clear Text
                realQtyTextView.setText("");
                etRemark.setText("");
                tvBoxName.setText("");
                tvERPIst.setText("");
                tvItemCode.setText("");
                tvItemName.setText("");
                tvLeftQty.setText("");
                tvManuLotno.setText("");

                inputEditText.setText("");
                inputEditText.findFocus();
//                hasScannItemClickButtonForPhoto = false;
                panDianItemBean = null;
                boxItemEntity = null;

            } else {
//                CommonUtil.ShowToast(StockCheckPartInvActivity.this,
//                        "提交失败" + ws_result.getErrorInfo(), R.mipmap.warning, Toast.LENGTH_SHORT);
                ToastUtil.showToastLong("提交失败" + ws_result.getErrorInfo());

            }

        }

        @Override
        protected void onPreExecute() {
            //pbScan.setVisibility(View.VISIBLE);


        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }


//    private AdapterProInv productAdapter;
//    private List<ProductsEntity> productsEntityList;
//    private int currentPage = 1;
//    private class QueryProductStockAsyncTask extends AsyncTask<String, Void, Void> {
//        //ArrayList<ProductsEntity> us = new ArrayList<ProductsEntity>();
//
//        @Override
//        protected Void doInBackground(String... params) {
//
////            String keyWord = etFilter.getText().toString();
//            String keyWord = params[0];
//            String js = WebServiceUtil.getQueryInv(UserSingleton.get().getUserInfo().getBu_ID(), 2, keyWord, currentPage, 20);
//            Gson gson = new Gson();
//            productsEntityList = gson.fromJson(js, new TypeToken<List<ProductsEntity>>() {
//            }.getType());
//
//            return null;
//        }
//
//
//        @Override
//        protected void onPostExecute(Void result) {
//            //tv.setText(fahren + "∞ F");
//
//            productAdapter = new AdapterProInv(StockCheckPartInvActivity.this, productsEntityList);
//            productSearchRecyclerView.setAdapter(productAdapter);
//            if (productsEntityList != null && productsEntityList.size() > 0){
//                originalDataLayout.setVisibility(View.GONE);
//                productSearchLayout.setVisibility(View.VISIBLE);
//            }
//            productAdapter.setOnItemClickListener((view, position) -> {
////                        if (productsEntityList != null && productsEntityList.size() > 0) {
////                            Intent intent = new Intent(StockCheckPartInvActivity.this, StockQueryProductItemActivity.class);
////                            intent.putExtra("selected_item", (Serializable) productsEntityList.get(position));
////                            startActivityForResult(intent, 100);
////                        }
////                    ToastUtil.showToastLong("正在开发！");
////                PanDianItemBean tempPanDianItemBean = new PanDianItemBean(null);
//
//
//                    }
//            );
//            //pbScan.setVisibility(View.INVISIBLE);
//        }
//
//        @Override
//        protected void onPreExecute() {
//            //pbScan.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected void onProgressUpdate(Void... values) {
//        }
//
//    }


}
