package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.MenuItem;
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
 * @date 创建时间 6/25/24 11:07 AM
 * @author 作者: liweifeng
 * @description 专门给成手工非托盘标签盘点使用
 */
public class ProductCheckInventoryManuPalletNotActivity extends BaseActivity {
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
//    private List<Integer> boxIDList;
    private int currentPSID;
    private int currentLotID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //todo  这个页面还没有统一
        setContentView(R.layout.activity_product_checkinventory_manu_pallet_not_layout);
        bindView();
        setButtonClick();
        getExtras();
//        boxIDList = new ArrayList<>();
    }


    private void getExtras() {
        Ac_Type = 2;
        fromSelfProduct = true;
        searchLayout.setVisibility(View.VISIBLE);

    }

    void bindView() {
        btnSelectCheckFile = (Button) findViewById(R.id.product_check_manu_pallet_not_select_check_Button);
        btnScanIst = (Button) findViewById(R.id.product_check_manu_pallet_not__scan_ist_Button);
        btnScanItem = (Button) findViewById(R.id.product_check_manu_pallet_not_add_item_Button);
        btnCommit = (Button) findViewById(R.id.product_check_manu_pallet_not_btn_affirm_qty);
        btnCal = (Button) findViewById(R.id.product_check_manu_pallet_not_btn_check_inv_cal_qty);
        titleLayoutManagerView = findViewById(R.id.product_check_manu_pallet_not_titleLayout);
        tvIst = (TextView) findViewById(R.id.product_check_manu_pallet_not_tv_check_stock_ist);

        tvERPIst = (TextView) findViewById(R.id.product_check_manu_pallet_not_tv_check_stock_ist_erp);
        tvItemCode = (TextView) findViewById(R.id.product_check_manu_pallet_not_tv_check_stock_item_code);
        tvManuLotno = (TextView) findViewById(R.id.tv_product_check_manu_pallet_notcheck_stock_manulotno);
        tvItemName = (TextView) findViewById(R.id.tv_product_check_manu_pallet_notcheck_stock_item_name);
        realQtyTextView = (TextView) findViewById(R.id.et_product_check_manu_pallet_notcheck_stock_box_real_qty);
        totalBoxNOEditText = (EditText) findViewById(R.id.et_product_check_manu_pallet_notcheck_stock_box_n);
        eachBoxQtyEditText = (EditText) findViewById(R.id.et_product_check_manu_pallet_notcheck_stock_box_pn);
        singleQtyEditText = (EditText) findViewById(R.id.et_product_check_manu_pallet_notcheck_stock_box_dq);
        etRemark = (EditText) findViewById(R.id.et_product_check_manu_pallet_notcheck_stock_box_remark);
        inputEditText = findViewById(R.id.product_check_manu_pallet_not_input_editText);

        searchEditText = findViewById(R.id.product_check_manu_pallet_not_search_editText);
        searchButton = findViewById(R.id.product_check_manu_pallet_not_search_Button);
        searchLayout = findViewById(R.id.product_check_manu_pallet_not_search_layout);

        manuLotEditText = findViewById(R.id.et_product_check_manu_pallet_notcheck_stock_manulotno);


    }

    void setButtonClick() {
        btnSelectCheckFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductCheckInventoryManuPalletNotActivity.this, SelectItemActivity.class);
//                Intent intent = new Intent(ProductCheckInventoryManuPalletNotActivity.this, CommonSelectItemActivity.class);
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
                    Toast.makeText(ProductCheckInventoryManuPalletNotActivity.this, "请先选择盘点表", Toast.LENGTH_LONG).show();
                    inputEditText.setText("");
                    return;
                }
                new IntentIntegrator(ProductCheckInventoryManuPalletNotActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();

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
                new IntentIntegrator(ProductCheckInventoryManuPalletNotActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();

            }
        });


        btnCommit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (SelectCI == null) {
                    Toast.makeText(ProductCheckInventoryManuPalletNotActivity.this, "请先选择盘点表", Toast.LENGTH_LONG).show();
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
protected void onTextChangedSafe(CharSequence text) {
                                if (TextUtils.isEmpty(text.toString())) {
                    return;
                }
                if (!inventoryFileSelect) {
                    ToastUtil.showToastShort("请先选择盘点表！");
                    inputEditText.setText("");
                    return;
                }
                //todo 奇怪的这个endwith \n 居然不执行
//                if (text.toString().endsWith("\n")){
//                    ToastUtil.showToastLong("扫描结果:" + text.toString());
                System.out.println("========================扫描结果:" + text.toString());
                //// TODO: 2019/12/10 scanfor之类的可能无用
//                    if (ScanFor.endsWith("Ist")) {
//                        ActivityResultScanIst(inputEditText.getText().toString());
//                    } else {
//                        ActivityResultScanItem(inputEditText.getText().toString());
//                    }
                parseScanData(text.toString());
//                }
            }
        });


        manuLotEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
protected void onTextChangedSafe(CharSequence text) {
                manuLotNO = text.toString();
            }
        });

        realQtyTextView.addTextChangedListener(new TextWatcherImpl() {
            @Override
protected void onTextChangedSafe(CharSequence text) {
                                qty = text.toString();
            }
        });

        etRemark.addTextChangedListener(new TextWatcherImpl() {
            @Override
protected void onTextChangedSafe(CharSequence text) {
                                remark = text.toString();
//                parseScanData(text.toString());
            }
        });
        totalBoxNOEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
protected void onTextChangedSafe(CharSequence text) {
                                N = text.toString();
//                parseScanData(text.toString());
            }
        });
        eachBoxQtyEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
protected void onTextChangedSafe(CharSequence text) {
                                PN = text.toString();
//                parseScanData(text.toString());
            }
        });
        singleQtyEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
protected void onTextChangedSafe(CharSequence text) {
                                DQ = text.toString();
//                parseScanData(text.toString());
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

                Intent intent = new Intent(ProductCheckInventoryManuPalletNotActivity.this, CommonSelectItemActivity.class);
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

                Intent intent = new Intent(ProductCheckInventoryManuPalletNotActivity.this, CommonSelectItemActivity.class);
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
        ProductCheckInventoryManuPalletNotActivity.CommitStockResultAsyncTask task = new ProductCheckInventoryManuPalletNotActivity.CommitStockResultAsyncTask();
        task.execute();
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
                //2024-06-25 john这里只处理成品的扫描
                if (result.startsWith("OldNoPallet") && qrContent.length > 8) {
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

                    inputEditText.setText("");
//                    }
                }else if (result.startsWith("OldPallet") && qrContent.length > 8) {
                    ToastUtil.showToastShort("该标签是托盘手工，请到托盘手工界面盘点");
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
                Toast.makeText(ProductCheckInventoryManuPalletNotActivity.this, istPlaceEntity.getErrorInfo(), Toast.LENGTH_LONG).show();

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


    private class CommitStockResultAsyncTask extends AsyncTask<String, Void, Void> {
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
                CommonUtil.ShowToast(ProductCheckInventoryManuPalletNotActivity.this,
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

//    手工非托盘 标签不必找位置 ，基本都为0
//    private class GetERPIstNameByBoxIDAsyncTask extends AsyncTask<Void, Void, Void>{
//        WsResult wsResult;
//        @Override
//        protected Void doInBackground(Void... voids) {
//            wsResult = WebServiceUtil.getProductIstNameByBoxID(currentBoxID);
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            if (wsResult != null && wsResult.getResult()){
//                tvERPIst.setText(wsResult.getErrorInfo());
//                if (!tvIst.getText().equals(wsResult.getErrorInfo())) {
//                    tvERPIst.setTextColor(Color.RED);
//                } else {
//                    tvERPIst.setTextColor(Color.BLACK);
//                }
//
//            }else{
//                ToastUtil.showToastShort("获取该箱ERP存储位置失败！");
//
//            }
//
//
//
//
//
//
//        }
//    }


}


