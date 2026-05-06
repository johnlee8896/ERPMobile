package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.chinashb.www.mobileerp.utils.StringUtils;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.TitleLayoutManagerView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/***
 * @date 创建时间 10/19/24 11:01 AM
 * @author 作者: liweifeng
 * @description 成品盘点统一，包含托盘标签，及手工补打标签（原包括托盘 ，非托盘 ），原三个，今整合到一个
 * 这里是从托盘标签复制开始改造
 */
public class ProductCheckInventoryCommonAllActivity extends BaseActivity {
    static HashMap<String, String> SelectCI;
    Button btnSelectCheckFile;
    Button btnScanIst;
    Button btnScanItem;
    Button btnCommit;
    Integer CI_ID;
    String scanstring;
    private TitleLayoutManagerView titleLayoutManagerView;
    private IstPlaceEntity thePlace;
    private TextView tvIst;
    private TextView tvERPIst;
    private TextView tvItemCode;
    private TextView tvManuLotno;
    private TextView tvItemName;
    private EditText totalBoxNOEditText;
    private EditText eachBoxQtyEditText;
    private EditText singleQtyEditText;
    private TextView realQtyTextView;
    private TextView tvChayiQty;
    private Button btnCal;
    private EditText etRemark;
    private EditText inputEditText;
    private EditText searchEditText;
    private Button searchButton;
    private RelativeLayout searchLayout;
    private int Ac_Type = 1;
    private String qty = "";
    private String remark = "";
    private String N = "";
    private String PN = "";
    private String DQ = "";
    private boolean inventoryFileSelect = false;
    private boolean istHasSelect = false;
    private PanDianItemBean panDianItemBean;
    private EditText manuLotEditText;
    private String storeArea = "";
    private String manuLotNO = "";
    private boolean fromSelfProduct;
    private List<Integer> boxIDList;
    private int currentBoxID;

    private List<Integer> palletIDList;
    private int currentPalletID;

    private int currentPSID;
    private int currentLotID;
//    private enum CURRENT_PRODUCT_LABEL{
//        int CODE_BOX = 0,
//        int MANU_PALLET = 1,
//        int MANU_PALLET_NOT = 2
//
//    }
    int CODE_BOX = 0;
    int MANU_PALLET = 1;
    int MANU_PALLET_NOT = 2;
    private int CURRENT_PRODUCT_LABEL = CODE_BOX;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //todo  这个页面还没有统一
        setContentView(R.layout.activity_product_checkinventory_layout);
        bindView();
        setButtonClick();
        getExtras();
        boxIDList = new ArrayList<>();
        palletIDList = new ArrayList<>();
    }


    private void getExtras() {
        Ac_Type = 2;
        fromSelfProduct = true;
        searchLayout.setVisibility(View.VISIBLE);

    }

    void bindView() {
        btnSelectCheckFile = (Button) findViewById(R.id.product_check_select_check_Button);
        btnScanIst = (Button) findViewById(R.id.product_check__scan_ist_Button);
        btnScanItem = (Button) findViewById(R.id.product_check_add_item_Button);
        btnCommit = (Button) findViewById(R.id.product_check_btn_affirm_qty);
        btnCal = (Button) findViewById(R.id.product_check_btn_check_inv_cal_qty);
        titleLayoutManagerView = findViewById(R.id.product_check_titleLayout);
        tvIst = (TextView) findViewById(R.id.product_check_tv_check_stock_ist);
        tvChayiQty = (TextView)findViewById(R.id.tv_product_checkcheck_stock_chayi_qty);

        tvERPIst = (TextView) findViewById(R.id.product_check_tv_check_stock_ist_erp);
        tvItemCode = (TextView) findViewById(R.id.product_check_tv_check_stock_item_code);
        tvManuLotno = (TextView) findViewById(R.id.tv_product_checkcheck_stock_manulotno);
        tvItemName = (TextView) findViewById(R.id.tv_product_checkcheck_stock_item_name);
        realQtyTextView = (TextView) findViewById(R.id.et_product_checkcheck_stock_box_real_qty);
        totalBoxNOEditText = (EditText) findViewById(R.id.et_product_checkcheck_stock_box_n);
        eachBoxQtyEditText = (EditText) findViewById(R.id.et_product_checkcheck_stock_box_pn);
        singleQtyEditText = (EditText) findViewById(R.id.et_product_checkcheck_stock_box_dq);
        etRemark = (EditText) findViewById(R.id.et_product_checkcheck_stock_box_remark);
        inputEditText = findViewById(R.id.product_check_input_editText);

        searchEditText = findViewById(R.id.product_check_search_editText);
        searchButton = findViewById(R.id.product_check_search_Button);
        searchLayout = findViewById(R.id.product_check_search_layout);

        manuLotEditText = findViewById(R.id.et_product_checkcheck_stock_manulotno);


    }

    void setButtonClick() {
        btnSelectCheckFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductCheckInventoryCommonAllActivity.this, SelectItemActivity.class);
//                Intent intent = new Intent(ProductCheckInventoryCommonAllActivity.this, CommonSelectItemActivity.class);
                Integer Bu_ID = UserSingleton.get().getUserInfo().getBu_ID();
                String sql = "";
                if (Ac_Type == 1 || Ac_Type == 2) {
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
                    Toast.makeText(ProductCheckInventoryCommonAllActivity.this, "请先选择盘点表", Toast.LENGTH_LONG).show();
                    inputEditText.setText("");
                    return;
                }
                new IntentIntegrator(ProductCheckInventoryCommonAllActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();

            }
        });


        btnScanItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (thePlace == null) {
                    ToastUtil.showToastLong("请先扫描托盘所在的仓库单元位置条码");
                    inputEditText.setText("");
                    return;
                }
                new IntentIntegrator(ProductCheckInventoryCommonAllActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();

            }
        });


        btnCommit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (SelectCI == null) {
                    Toast.makeText(ProductCheckInventoryCommonAllActivity.this, "请先选择盘点表", Toast.LENGTH_LONG).show();
                    return;
                }

                String qty = realQtyTextView.getText().toString();

                if (qty.equals("")) {
                    ToastUtil.showToastShort("数量为空!");
                    return;
                }


                Commit_Result();

            }
        });

        btnCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer n = 1;
                if (totalBoxNOEditText.getText().toString().equals("")) {
                    totalBoxNOEditText.setText("1");
                }
                n = Integer.valueOf(totalBoxNOEditText.getText().toString());

                double pn = 1;
                if (eachBoxQtyEditText.getText().toString().equals("")) {
                    eachBoxQtyEditText.setText("1");
                }

                pn = Double.valueOf(eachBoxQtyEditText.getText().toString());

                double dq = 0;
                if (singleQtyEditText.getText().toString().equals("")) {
                    singleQtyEditText.setText("0");
                }
                dq = Double.valueOf(singleQtyEditText.getText().toString());

                double q;
                q = n * pn + dq;
                realQtyTextView.setText(CommonUtil.DecimalFormat(q));
            }
        });

        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
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


        manuLotEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                manuLotNO = editable.toString();
            }
        });

        realQtyTextView.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                qty = editable.toString();
            }
        });

        etRemark.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                remark = editable.toString();
//                parseScanData(editable.toString());
            }
        });
        totalBoxNOEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                N = editable.toString();
//                parseScanData(editable.toString());
            }
        });
        eachBoxQtyEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                PN = editable.toString();
//                parseScanData(editable.toString());
            }
        });
        singleQtyEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
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


            String input = searchEditText.getText().toString();
            if (fromSelfProduct) {
                String sql = String.format("Select top 60 Item.Item_ID,Item_Version.IV_ID,product.Product_DrawNO as Item_Name, Item_Version.Item_Version As Version,Item.Item_Unit_Exchange ,Item.Item_Unit ,product.Product_DrawNO " +
                        "From product inner join Item on product.item_id = item.item_id Inner Join Item_Version On Item.Item_ID = Item_Version.Item_ID Where  (product.Product_DrawNO like %s or product.current_ps like %s or item.item_id like %s) ", "'%" + input + "%'", "'%" + input + "%'", "'%" + input + "%'");

                Intent intent = new Intent(ProductCheckInventoryCommonAllActivity.this, CommonSelectItemActivity.class);
                List<Integer> ColWith = new ArrayList<Integer>(Arrays.asList(50, 100, 100));
                List<String> ColCaption = new ArrayList<String>(Arrays.asList("Item_ID", "IV_ID", "物料", "版本"));

                String Title = "选择物料";
                intent.putExtra("Title", Title);
                intent.putExtra("SQL", sql);
                intent.putExtra("ColWidthList", (Serializable) ColWith);
                intent.putExtra("ColCaptionList", (Serializable) ColCaption);
                intent.putExtra(IntentConstant.Intent_Extra_to_select_search_from_postition, IntentConstant.Select_Search_From_Select_PanDina);

                startActivityForResult(intent, 500);

            } else {

                String sql = String.format("Select top 60 Item.Item_ID,Item_Version.IV_ID,Item.Item+' '+Item.Item_Name+' '+isnull(Item.Item_Spec2,'') As Item_Name, Item_Version.Item_Version As Version,Item.Item_Unit_Exchange ,Item.Item_Unit  " +
                        "From Item Inner Join Item_Version On Item.Item_ID = Item_Version.Item_ID Where   (item.item like %s or item.item_drawno like %s or item.item_id like %s) ", "'%" + input + "%'", "'%" + input + "%'", "'%" + input + "%'");

                Intent intent = new Intent(ProductCheckInventoryCommonAllActivity.this, CommonSelectItemActivity.class);
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

    protected void Commit_Result() {
        if (CURRENT_PRODUCT_LABEL == CODE_BOX){
            CommitCodeBoxStockResultAsyncTask task = new CommitCodeBoxStockResultAsyncTask();
            task.execute();
        }else if (CURRENT_PRODUCT_LABEL == MANU_PALLET){
            CommitManuPalletStockResultAsyncTask task = new CommitManuPalletStockResultAsyncTask();
            task.execute();
        }else if (CURRENT_PRODUCT_LABEL == MANU_PALLET_NOT){
            CommitManuNotPalletStockResultAsyncTask task = new CommitManuNotPalletStockResultAsyncTask();
            task.execute();
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

        //下面是解析扫描的结果
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
            } else {
                parseScanData(result.getContents());

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handlePandianItem(PanDianItemBean panDianItemBean) {
        tvItemCode.setText("暂无相关条码");

        //// TODO: 2020/12/29  重复的判断
        DecimalFormat DF = new DecimalFormat("####.####");
        tvERPIst.setText("");
        tvItemName.setText(panDianItemBean.getItem_Name());
//        if (!ShowERPInv) {
//            tvLeftQty.setVisibility(View.INVISIBLE);
//        } else {
//            tvLeftQty.setVisibility(View.VISIBLE);
//        }

        eachBoxQtyEditText.setText(panDianItemBean.getItem_Unit_Exchange() + "");
        inputEditText.setText("");
        inputEditText.findFocus();


    }

    protected void ActivityResultSelectInventoryFile(Intent data) {
        if (data != null) {
            SelectCI = (HashMap<String, String>) data.getSerializableExtra("SelectItem");
            if (SelectCI != null) {
                titleLayoutManagerView.setTitle(SelectCI.get("CI_Name"));
                CI_ID = Integer.valueOf(SelectCI.get("CI_ID"));
                tvIst.setText("");

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

                if (result.startsWith("/SUB_IST_ID/") || result.startsWith("/IST_ID/")) {
                    //仓库位置码
                    scanstring = result;
                    GetIstAsyncTask task = new GetIstAsyncTask();
                    task.execute();
                }
                //2024-10-19 john这里处理成品的扫描
                if (result.startsWith("Pallet") && qrContent.length == 8) {

                    if (!istHasSelect) {
                        ToastUtil.showToastShort("请先扫描库位码！");
                        inputEditText.setText("");
                        return;
                    }
                    int boxId = Integer.parseInt(qrContent[1]);
                    if (boxIDList.contains(boxId)) {
                        ToastUtil.showToastShort("该托盘已在列表中，请勿重复扫描！");
                    } else {
                        tvItemName.setText("客户图号： " + qrContent[5]);
                        tvItemCode.setText("BoxID/" + qrContent[1] + " " + qrContent[3]);
                        eachBoxQtyEditText.setText(qrContent[7]);
                        boxIDList.add(boxId);
                        currentBoxID = boxId;
                        GetCodeBoxERPIstNameByBoxIDAsyncTask task = new GetCodeBoxERPIstNameByBoxIDAsyncTask();
                        task.execute();

                        inputEditText.setText("");
                    }
                    CURRENT_PRODUCT_LABEL = CODE_BOX ;
                }
                //2024-10-19 john这里处理成品的扫描 手工补打标签
                else if (result.startsWith("OldPallet") && qrContent.length > 8) {

                    if (!istHasSelect) {
                        ToastUtil.showToastShort("请先扫描库位码！");
                        inputEditText.setText("");
                        return;
                    }
                    int palletID = Integer.parseInt(qrContent[1]);
                    if (palletIDList.contains(palletID)) {
                        ToastUtil.showToastShort("该托盘已在列表中，请勿重复扫描！");
                    } else {
                        tvItemName.setText("PS_ID： " + qrContent[6]);
                        tvItemCode.setText("Pallet_ID/" + qrContent[1] + " " + qrContent[3] + "/" + qrContent[4]);
                        eachBoxQtyEditText.setText(qrContent[8]);
                        palletIDList.add(palletID);
                        currentPalletID = palletID;
                        GetManuPalletERPIstNameByBoxIDAsyncTask task = new GetManuPalletERPIstNameByBoxIDAsyncTask();
                        task.execute();

                        inputEditText.setText("");
                    }
                    CURRENT_PRODUCT_LABEL = MANU_PALLET;
                }
//                if (result.startsWith("OldNoPallet") && qrContent.length > 8) {
//                    ToastUtil.showToastShort("该标签是非托盘手工，请到非托盘手工界面盘点");
//                }
                else if (result.startsWith("OldNoPallet") && qrContent.length > 8) {
                    if (!istHasSelect) {
                        ToastUtil.showToastShort("请先扫描库位码！");
                        inputEditText.setText("");
                        return;
                    }
//                    int palletID = Integer.parseInt(qrContent[1]);
//                    if (palletIDList.contains(palletID)) {
//                        ToastUtil.showToastShort("该托盘已在列表中，请勿重复扫描！");
//                    } else {
                    tvItemName.setText("PS_ID： " + qrContent[1]);
                    tvItemCode.setText("PS_ID/" + qrContent[1] + " LotID:" + qrContent[3] );
                    eachBoxQtyEditText.setText(qrContent[11]);
//                    palletIDList.add(palletID);
//                    currentPalletID = palletID;
                    currentLotID = Integer.parseInt(qrContent[3]);
                    currentPSID = Integer.parseInt(qrContent[1]);

//                        GetERPIstNameByBoxIDAsyncTask task = new GetERPIstNameByBoxIDAsyncTask();
//                        task.execute();

                    GetEosCheckInvChaYiAsyncTask task = new GetEosCheckInvChaYiAsyncTask();
                    task.execute();

                    inputEditText.setText("");
//                    }
                    CURRENT_PRODUCT_LABEL = MANU_PALLET_NOT;
                }
            }
        }
    }

    private class GetIstAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            IstPlaceEntity istPlaceEntity = WebServiceUtil.op_Check_Commit_IST_Barcode(scanstring);
            if (istPlaceEntity.getResult()) {
                thePlace = istPlaceEntity;
                //清空
            } else {
                Toast.makeText(ProductCheckInventoryCommonAllActivity.this, istPlaceEntity.getErrorInfo(), Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            //pbScan.setVisibility(View.VISIBLE);
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
        protected void onProgressUpdate(Void... values) {
        }

    }


    private class CommitCodeBoxStockResultAsyncTask extends AsyncTask<String, Void, Void> {
        WsResult ws_result;

        @Override

        protected Void doInBackground(String... params) {
            if (fromSelfProduct) {
//               ws_result = WebServiceUtil.commit_Self_Product_Pandian(UserSingleton.get().getHRName(), CI_ID, UserSingleton.get().getUserInfo().getBu_ID(), "",
//                                thePlace != null ? thePlace.getIst_ID() : 0, thePlace != null ? thePlace.getSub_Ist_ID() : 0, panDianItemBean.getItem_ID(), panDianItemBean.getIV_ID(),
//                       0L, qty, N, PN, DQ, remark, storeArea, manuLotNO);
                ws_result = WebServiceUtil.commit_Product_Pandian_ByBox(UserSingleton.get().getHRName(), CI_ID, UserSingleton.get().getUserInfo().getBu_ID(),"BoxID/" + currentBoxID ,
                        thePlace != null ? thePlace.getIst_ID() : 0, thePlace != null ? thePlace.getSub_Ist_ID() : 0,
                        qty, N, PN, DQ, remark, storeArea, manuLotNO,currentBoxID);

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (ws_result.getResult()) {
                CommonUtil.ShowToast(ProductCheckInventoryCommonAllActivity.this,
                        "提交成功", R.mipmap.smiley, Toast.LENGTH_SHORT);


                //Clear Text
                realQtyTextView.setText("");
                etRemark.setText("");
//                tvBoxName.setText("");
                tvERPIst.setText("");
                tvItemCode.setText("");
                tvItemName.setText("");
//                tvLeftQty.setText("");
                tvManuLotno.setText("");

                inputEditText.setText("");
                inputEditText.findFocus();
//                hasScannItemClickButtonForPhoto = false;
                panDianItemBean = null;

                eachBoxQtyEditText.setText("");

            } else {
                ToastUtil.showToastLong("提交失败" + ws_result.getErrorInfo());

            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //手工托盘 标签盘点
    private class CommitManuPalletStockResultAsyncTask extends AsyncTask<String, Void, Void> {
        WsResult ws_result;

        @Override

        protected Void doInBackground(String... params) {
            if (fromSelfProduct) {
//               ws_result = WebServiceUtil.commit_Self_Product_Pandian(UserSingleton.get().getHRName(), CI_ID, UserSingleton.get().getUserInfo().getBu_ID(), "",
//                                thePlace != null ? thePlace.getIst_ID() : 0, thePlace != null ? thePlace.getSub_Ist_ID() : 0, panDianItemBean.getItem_ID(), panDianItemBean.getIV_ID(),
//                       0L, qty, N, PN, DQ, remark, storeArea, manuLotNO);
                ws_result = WebServiceUtil.commit_Product_Manu_Pallet_Pandian_ByBox(UserSingleton.get().getHRName(), CI_ID, UserSingleton.get().getUserInfo().getBu_ID(),"Pallet_ID/" + currentPalletID ,
                        thePlace != null ? thePlace.getIst_ID() : 0, thePlace != null ? thePlace.getSub_Ist_ID() : 0,
                        qty, N, PN, DQ, remark, storeArea, manuLotNO,currentPalletID);

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (ws_result.getResult()) {
                CommonUtil.ShowToast(ProductCheckInventoryCommonAllActivity.this,
                        "提交成功", R.mipmap.smiley, Toast.LENGTH_SHORT);


                //Clear Text
                realQtyTextView.setText("");
                etRemark.setText("");
//                tvBoxName.setText("");
                tvERPIst.setText("");
                tvItemCode.setText("");
                tvItemName.setText("");
//                tvLeftQty.setText("");
                tvManuLotno.setText("");

                inputEditText.setText("");
                inputEditText.findFocus();
//                hasScannItemClickButtonForPhoto = false;
                panDianItemBean = null;
                eachBoxQtyEditText.setText("");

            } else {
                ToastUtil.showToastLong("提交失败" + ws_result.getErrorInfo());

            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //手工非托盘 标签盘点
    private class CommitManuNotPalletStockResultAsyncTask extends AsyncTask<String, Void, Void> {
        WsResult ws_result;

        @Override

        protected Void doInBackground(String... params) {
            if (fromSelfProduct) {
//               ws_result = WebServiceUtil.commit_Self_Product_Pandian(UserSingleton.get().getHRName(), CI_ID, UserSingleton.get().getUserInfo().getBu_ID(), "",
//                                thePlace != null ? thePlace.getIst_ID() : 0, thePlace != null ? thePlace.getSub_Ist_ID() : 0, panDianItemBean.getItem_ID(), panDianItemBean.getIV_ID(),
//                       0L, qty, N, PN, DQ, remark, storeArea, manuLotNO);
                ws_result = WebServiceUtil.commit_Product_Manu_Pallet_Not_Pandian_ByBox(UserSingleton.get().getHRName(), CI_ID, UserSingleton.get().getUserInfo().getBu_ID(),"Product_LotID/" + currentLotID ,
                        thePlace != null ? thePlace.getIst_ID() : 0, thePlace != null ? thePlace.getSub_Ist_ID() : 0,
                        qty, N, PN, DQ, remark, storeArea, manuLotNO,currentLotID,currentPSID);

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (ws_result.getResult()) {
                CommonUtil.ShowToast(ProductCheckInventoryCommonAllActivity.this,
                        "提交成功", R.mipmap.smiley, Toast.LENGTH_SHORT);


                //Clear Text
                realQtyTextView.setText("");
                etRemark.setText("");
//                tvBoxName.setText("");
                tvERPIst.setText("");
                tvItemCode.setText("");
                tvItemName.setText("");
//                tvLeftQty.setText("");
                tvManuLotno.setText("");

                inputEditText.setText("");
                inputEditText.findFocus();
//                hasScannItemClickButtonForPhoto = false;
                panDianItemBean = null;
                eachBoxQtyEditText.setText("");

            } else {
                ToastUtil.showToastLong("提交失败" + ws_result.getErrorInfo());

            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //成品托盘标签的ERP位置
    private class GetCodeBoxERPIstNameByBoxIDAsyncTask extends AsyncTask<Void, Void, Void>{
        WsResult wsResult;
        @Override
        protected Void doInBackground(Void... voids) {
            wsResult = WebServiceUtil.getProductIstNameByBoxID(currentBoxID);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (wsResult != null && wsResult.getResult()){
                tvERPIst.setText(wsResult.getErrorInfo());
                if (!tvIst.getText().equals(wsResult.getErrorInfo())) {
                    tvERPIst.setTextColor(Color.RED);
                } else {
                    tvERPIst.setTextColor(Color.BLACK);
                }
                GetEosCheckInvChaYiAsyncTask task = new GetEosCheckInvChaYiAsyncTask();
                task.execute();

            }else{
                ToastUtil.showToastShort("获取该箱ERP存储位置失败！");

            }

        }
    }

    private class GetManuPalletERPIstNameByBoxIDAsyncTask extends AsyncTask<Void, Void, Void>{
        WsResult wsResult;
        @Override
        protected Void doInBackground(Void... voids) {
            wsResult = WebServiceUtil.getManuPalletProductIstNameByPalletID(currentPalletID);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (wsResult != null && wsResult.getResult()){
                tvERPIst.setText(wsResult.getErrorInfo());
                if (!tvIst.getText().equals(wsResult.getErrorInfo())) {
                    tvERPIst.setTextColor(Color.RED);
                } else {
                    tvERPIst.setTextColor(Color.BLACK);
                }

                GetEosCheckInvChaYiAsyncTask task = new GetEosCheckInvChaYiAsyncTask();
                task.execute();

            }else{
                ToastUtil.showToastShort("获取该箱ERP存储位置失败！");

            }

        }
    }

    private class GetEosCheckInvChaYiAsyncTask extends AsyncTask<Void, Void, Void>{
        WsResult wsResult;
        @Override
        protected Void doInBackground(Void... voids) {
            if (CURRENT_PRODUCT_LABEL == CODE_BOX){
                wsResult = WebServiceUtil.GetProductCompareCheckInventoryToInv(currentBoxID,0,0,CI_ID,thePlace.getIst_ID(),thePlace.getSub_Ist_ID());
            }else if (CURRENT_PRODUCT_LABEL == MANU_PALLET){
                wsResult = WebServiceUtil.GetProductCompareCheckInventoryToInv(0,currentPalletID,0,CI_ID,thePlace.getIst_ID(),thePlace.getSub_Ist_ID());
            }else if (CURRENT_PRODUCT_LABEL == MANU_PALLET_NOT){
                wsResult = WebServiceUtil.GetProductCompareCheckInventoryToInv(0,0,currentPSID,CI_ID,thePlace.getIst_ID(),thePlace.getSub_Ist_ID());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (wsResult != null && wsResult.getResult()){
                tvChayiQty.setText(wsResult.getErrorInfo());
//                if (!tvChayiQty.getText().equals(wsResult.getErrorInfo())) {
//                    tvERPIst.setTextColor(Color.RED);
//                } else {
//                    tvERPIst.setTextColor(Color.BLACK);
//                }

            }else{
                ToastUtil.showToastShort("获取差异失败！" + wsResult.getErrorInfo());

            }






        }
    }


}
