package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.adapter.InBoxItemAdapter;
import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.basicobject.UserInfoEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.BuBean;
import com.chinashb.www.mobileerp.bean.CompanyBean;
import com.chinashb.www.mobileerp.bean.DeliveryTypeBean;
import com.chinashb.www.mobileerp.bean.LogisticsCompanyBean;
import com.chinashb.www.mobileerp.bean.ReceiverCompanyBean;
import com.chinashb.www.mobileerp.bean.SendGoodsSearchItemBean;
import com.chinashb.www.mobileerp.commonactivity.CommonSelectItemActivity;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.JsonUtil;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.utils.UnitFormatUtil;
import com.chinashb.www.mobileerp.widget.CommonSelectInputDialog;
import com.chinashb.www.mobileerp.widget.TimePickerManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SendGoodsManagerActivity extends BaseActivity implements View.OnClickListener, OnViewClickListener {
    @BindView(R.id.send_goods_manage_confirm_button) TextView confirmButton;
    @BindView(R.id.send_goods_manage_cancel_button) TextView cancelButton;
    @BindView(R.id.send_goods_manage_select_item_button) TextView selectItemButton;
    @BindView(R.id.send_goods_manage_receive_company_name_textView) TextView receiveCompanyNameTextView;
    @BindView(R.id.send_goods_manage_receive_bu_name_textView) TextView receiveBuNameTextView;
    @BindView(R.id.send_goods_manage_out_date_textView) TextView outDateTextView;
    @BindView(R.id.send_goods_manage_send_lot_editText) EditText sendLotEditText;
    @BindView(R.id.send_goods_manage_manu_lot_editText) EditText manuLotEditText;
    @BindView(R.id.send_goods_manage_package_textView) TextView packageTextView;
    @BindView(R.id.send_goods_manage_total_box_editText) EditText totalBoxEditText;
    @BindView(R.id.send_goods_manage_total_number_editText) EditText totalNumberEditText;
    @BindView(R.id.send_goods_manage_remark_editText) EditText remarkEditText;
    @BindView(R.id.send_goods_manage_et_keyword_input) EditText keywordInputEditText;
    @BindView(R.id.send_goods_manage_item_detail_textView) TextView itemDetailTextView;
    @BindView(R.id.send_goods_manage_rv_box_item) RecyclerView mRecyclerView;
    @BindView(R.id.send_goods_manage_package_data_layout) LinearLayout packageDataLayout;

    private String keyWord = "";
    private String scanContent = "";
    private CommonSelectInputDialog commonSelectInputDialog;
    private CompanyBean companyBean;
    private BuBean buBean;
    private String remark = "";
    private BoxItemEntity scanBoxItemEntity;
    private TimePickerManager timePickerManager;
    private Date outDate;
    private InBoxItemAdapter boxItemAdapter;
    private List<BoxItemEntity> boxItemEntityList = new ArrayList<>();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                ToastUtil.showToastLong("您当前公司与来料入库公司不符合，请确认来料是否入到该公司！");
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_goods_manage_layout);
        ButterKnife.bind(this);
        initViews();
        setViewsListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 305 && resultCode == 1) {
            if (data != null) {

                SendGoodsSearchItemBean bean = data.getParcelableExtra("SelectItem");

                initItem(bean);
            }
            return;
        } else {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (!TextUtils.isEmpty(result.getContents())) {
                    parseContent(result.getContents());
                }
            }
        }
    }

    private void initItem(SendGoodsSearchItemBean bean) {
        if (bean != null) {
            itemDetailTextView.setText(String.format("Item_ID : %s  IV_ID : %s  名称: %s  图号 ： %s 版本：%s 规格： %s  单位： %s", bean.getItem_ID(), bean.getIV_ID(),
                    bean.getItem_Name(), bean.getItem_DrawNo(), bean.getItem_Version(), bean.getItem_Spec2(), bean.getItem_Unit()));
        }
    }

    private void setViewsListener() {
        selectItemButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        keywordInputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                keyWord = editable.toString();
                if (keyWord.startsWith("V") && keyWord.length() > 4) {
                    parseContent(keyWord);
                }
            }
        });
        remarkEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                remark = editable.toString();
            }
        });
        packageTextView.setOnClickListener(this);
        receiveCompanyNameTextView.setOnClickListener(this);
        receiveBuNameTextView.setOnClickListener(this);
        outDateTextView.setOnClickListener(this);

    }

    private void parseContent(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }

        System.out.println("============ scan content = " + content);
        // VB/579807
        if (content.contains("\n")) {
            content = content.replace("\n", "");
        }
        if (content.contains("/") || content.contains("／")) {
            if (content.contains("／")) {
                content = content.replace("／", "/");
            }

            String[] qrContent;
            qrContent = content.split("/");
            if (qrContent.length >= 2) {
                String qrTitle = qrContent[0];
                if (!qrTitle.equals("")) {
                    if (qrTitle.equals("VE") || qrTitle.equals("VF") || qrTitle.equals("VG") || qrTitle.equals("V9") || qrTitle.equals("VA") || qrTitle.equals("VB") || qrTitle.equals("VC")) {
                        //物品条码
                        scanContent = content;
                        GetBoxAsyncTask task = new GetBoxAsyncTask();
                        task.execute();
                    }
                }

            }
        }
    }

    private void initViews() {
        if (UserSingleton.get().getUserInfo().getCompany_ID() == 1) {
            receiveCompanyNameTextView.setText("上海胜华波汽车电器有限公司");
        } else if (UserSingleton.get().getUserInfo().getCompany_ID() == 14) {
            receiveCompanyNameTextView.setText("胜华波汽车电器(滁州)有限公司");
        }
        outDate = new Date();

        receiveBuNameTextView.setText(UserSingleton.get().getUserInfo().getBu_Name());
        outDateTextView.setText(UnitFormatUtil.formatTimeToDayChinese(System.currentTimeMillis()));
        manuLotEditText.setText(UnitFormatUtil.formatTimeToDayChinese(System.currentTimeMillis()) + "-01");
        sendLotEditText.setText(UnitFormatUtil.formatTimeToDayChinese(System.currentTimeMillis()) + "-01");

        boxItemAdapter = new InBoxItemAdapter(SendGoodsManagerActivity.this, boxItemEntityList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        mRecyclerView.setAdapter(boxItemAdapter);
    }

    private void initPackage(BoxItemEntity boxItemEntity) {
        if (boxItemEntity != null) {
            manuLotEditText.setText(boxItemEntity.getManuLotNo());
            sendLotEditText.setText(boxItemEntity.getLotNo());
            totalBoxEditText.setText(1 + "");
            totalNumberEditText.setText(boxItemEntity.getQty() + "");


        }
    }

    private void getSelectBuDialog() {
        GetCommonNameBeanListAsyncTask<BuBean> task = new GetCommonNameBeanListAsyncTask();
        String sql = String.format("Select Bu_ID,Bu_Name From Bu Where Company_ID= %s  And Enabled=1", companyBean.getCompanyId());
        task.execute(sql, "1");
    }

    private void showSelectCompanyDialog() {
        new GetCompanyListAsyncTask().execute();
    }

    private void showTimePickerDialog(String pickType) {
        if (timePickerManager == null) {
            timePickerManager = new TimePickerManager(SendGoodsManagerActivity.this);
        }
        if (pickType == TimePickerManager.PICK_TYPE_ARRIVE_DATE || pickType == TimePickerManager.PICK_TYPE_OUT_DATE) {
            timePickerManager.setShowType(TimePickerManager.PICK_TYPE_YEAR, 3);
        } else {
            timePickerManager.setShowType(TimePickerManager.PICK_TYPE_YEAR, 5);
        }
        timePickerManager
                .setOnViewClickListener(SendGoodsManagerActivity.this)
                .showDialog(pickType);
    }

    @Override
    public void onClick(View v) {
        if (v == selectItemButton) {
//            String sql = String.format("Select Distinct  Top 50 Item.Item_ID,Item_Version.IV_ID, Item_Name , Item_DrawNo , Item_Version.Item_Version,Item.Item_Spec2,Item.Item_Unit  " +
//                    "From Item Inner Join Item_Version On Item.Item_ID=Item_Version.Item_ID Inner Join Supplier_Material As P On P.IV_ID=Item_Version.IV_ID " +
//                    "Inner Join Bu on Bu.id_supplier = P.Supplier_ID " +
//                    "where item_name like '%" + keyWord + "%' and bu.bu_id  = %d", UserSingleton.get().getUserInfo().getBu_ID());

            String sql = "Select Distinct  Top 50 Item.Item_ID,Item_Version.IV_ID, Item_Name , Item_DrawNo , Item_Version.Item_Version,Item.Item_Spec2,Item.Item_Unit  " +
                    "From Item Inner Join Item_Version On Item.Item_ID=Item_Version.Item_ID Inner Join Supplier_Material As P On P.IV_ID=Item_Version.IV_ID " +
                    "Inner Join Bu on Bu.id_supplier = P.Supplier_ID " +
                    "where (item.item_name like '%" + keyWord + "%' or item.item_id like '%" + keyWord + "%') and bu.bu_id  = " +
//                    (buBean != null ?  buBean.getBuId(): UserSingleton.get().getUserInfo().getBu_ID());
                    UserSingleton.get().getUserInfo().getBu_ID();
            Intent intent = new Intent(SendGoodsManagerActivity.this, CommonSelectItemActivity.class);
            intent.putExtra("SQL", sql);
            intent.putExtra("Title", "请选择发货物料");
            intent.putExtra(IntentConstant.Intent_Extra_to_select_search_from_postition, IntentConstant.Select_Search_From_Select_Send_Goods_Item);

            startActivityForResult(intent, 305);
        } else if (v == confirmButton) {
            if (companyBean == null){
                ToastUtil.showToastShort("未选择接收公司");
                return;
            }
            if (buBean == null){
                ToastUtil.showToastShort("未选择接收车间");
                return;
            }
//            if (companyBean == null || buBean == null){
//
//            }
            CommitSendGoodsAsyncTask task = new CommitSendGoodsAsyncTask();
            task.execute();
        } else if (v == packageTextView) {
            if (companyBean == null){
                ToastUtil.showToastShort("未选择接收公司");
                return;
            }
            if (buBean == null){
                ToastUtil.showToastShort("未选择接收车间");
                return;
            }
            new IntentIntegrator(SendGoodsManagerActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();

        } else if (v == cancelButton) {
            finish();
        } else if (v == receiveCompanyNameTextView) {
            showSelectCompanyDialog();
        } else if (v == receiveBuNameTextView) {
            if (companyBean != null) {
                getSelectBuDialog();
            } else {
                ToastUtil.showToastShort("请先选择公司");
            }
        } else if (v == outDateTextView) {
            showTimePickerDialog(TimePickerManager.PICK_TYPE_OUT_DATE);
        }
    }

    private boolean judgeBarCodeVerified(BoxItemEntity scanBoxItemEntity) {
        UserInfoEntity userInfoEntity = UserSingleton.get().getUserInfo();
//        if (userInfoEntity != null && scanBoxItemEntity != null) {
//            if (scanBoxItemEntity.getCompany_ID() == 0){
//                ToastUtil.showToastShort("标签解析错误！" + scanBoxItemEntity.getErrorInfo());
//                return false;
//            }
//            if (companyBean.getCompanyId() != scanBoxItemEntity.getCompany_ID()) {
//                ToastUtil.showToastShort("当前所选公司与发货公司不符！");
//                return false;
//            }
//            if (buBean.getBuId() != scanBoxItemEntity.getBu_ID()) {
//                ToastUtil.showToastShort("当前所选车间与发货车间不符！");
//                return false;
//            }
//        } else {
//            return false;
//        }
        return true;
    }

    @Override
    public <T> void onClickAction(View v, String tag, T date) {
        if (tag.equals(TimePickerManager.PICK_TYPE_OUT_DATE)) {
            outDateTextView.setText(UnitFormatUtil.sdf_YMD_Chinese.format(date));
            outDate = (Date) date;
        }
    }

    private class GetBoxAsyncTask extends AsyncTask<String, Void, Void> {
        BoxItemEntity scanBoxItemEntity;

        @Override
        protected Void doInBackground(String... params) {
//            BoxItemEntity boxItemEntity = WebServiceUtil.op_Check_Commit_DS_Item_Income_Barcode(scanContent);
            BoxItemEntity boxItemEntity = WebServiceUtil.check_Mobile_Send_Goods_Barcode(scanContent);
            String s = JsonUtil.objectToJson(boxItemEntity);

            scanBoxItemEntity = boxItemEntity;
            if (boxItemEntity.getResult()) {

                if (boxItemEntity.getCompany_ID() != 0 && boxItemEntity.getCompany_ID() != UserSingleton.get().getUserInfo().getCompany_ID()) {
                    Message message = new Message();
                    message.what = 0;
                    handler.sendMessage(message);
                    return null;
                }

                if (!is_box_existed(boxItemEntity)) {
                    boxItemEntity.setSelect(true);
                    boxItemEntityList.add(boxItemEntity);
                } else {
                    boxItemEntity.setResult(false);
                    boxItemEntity.setErrorInfo("该包装已经在装载列表中");
                }
//

            } else {

            }

            return null;
        }

        protected Boolean is_box_existed(BoxItemEntity box_item) {
            Boolean result = false;
            if (boxItemEntityList != null) {
                for (int i = 0; i < boxItemEntityList.size(); i++) {
                    if (boxItemEntityList.get(i).getDIII_ID() == box_item.getDIII_ID()) {
                        return true;
                    }
                }
            }

            return result;
        }

        @Override
        protected void onPreExecute() {
            //pbScan.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");
            if (scanBoxItemEntity != null) {
                if (!scanBoxItemEntity.getResult()) {
                    Toast.makeText(SendGoodsManagerActivity.this, scanBoxItemEntity.getErrorInfo(), Toast.LENGTH_LONG).show();
                }
            }

            if (judgeBarCodeVerified(scanBoxItemEntity)) {
                initPackage(scanBoxItemEntity);
                boxItemAdapter = new InBoxItemAdapter(SendGoodsManagerActivity.this, boxItemEntityList);
                mRecyclerView.setAdapter(boxItemAdapter);
                if (packageDataLayout.getVisibility() == View.GONE){
                    packageDataLayout.setVisibility(View.VISIBLE);
                }
                keywordInputEditText.setText("");
                keywordInputEditText.setHint("请继续使用扫描枪");
            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }


    }

    private class GetCommonNameBeanListAsyncTask<T> extends AsyncTask<String, Void, List<T>> {

        @Override
        protected List<T> doInBackground(String... strings) {
//            String sql = "select Company_ID,Company_Chinese_Name,Company_English_Name from company where Company_Enabled = 1";
            String sql = strings[0];
            int number = Integer.parseInt(strings[1]);
            WsResult result = WebServiceUtil.getDataTable(sql);
            List<T> commonDataList = null;
            //// TODO: 2020/1/17 未来如何优化架构
//            commonDataList = new ArrayList<>();
//            commonDataList.add(new T);
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                Gson gson = new Gson();
                if (number == 1) {//bubean
                    commonDataList = gson.fromJson(jsonData, new TypeToken<List<BuBean>>() {
                    }.getType());
                } else if (number == 2) {//receiverCompanyBean
                    commonDataList = gson.fromJson(jsonData, new TypeToken<List<ReceiverCompanyBean>>() {
                    }.getType());
                } else if (number == 3) {
                    commonDataList = gson.fromJson(jsonData, new TypeToken<List<LogisticsCompanyBean>>() {
                    }.getType());
                } else if (number == 4) {
                    commonDataList = gson.fromJson(jsonData, new TypeToken<List<DeliveryTypeBean>>() {
                    }.getType());
                }

                if (commonDataList != null && commonDataList.size() > 0) {
                    if (commonDataList.get(0) instanceof ReceiverCompanyBean) {
                        commonDataList = gson.fromJson(jsonData, new TypeToken<List<ReceiverCompanyBean>>() {
                        }.getType());
                        //去掉重复
                        List<ReceiverCompanyBean> tempList = new ArrayList<>();
                        List<String> nameList = new ArrayList<>();
                        for (T bean : commonDataList) {
                            if (!nameList.contains(((ReceiverCompanyBean) bean).getCustomer())) {
                                nameList.add(((ReceiverCompanyBean) bean).getCustomer());
                                tempList.add((ReceiverCompanyBean) bean);
                            }
                        }
                        return (List<T>) tempList;
                    }
//                    else if (commonDataList.get(0) instanceof BuBean) {
//                        commonDataList = gson.fromJson(jsonData, new TypeToken<List<BuBean>>() {
//                        }.getType());
//                    }else if (commonDataList.get(0) instanceof LogisticsCompanyBean) {
//
//                    }
                }
            }


            return commonDataList;
        }

        @Override
        protected void onPostExecute(List<T> companyList) {
            super.onPostExecute(companyList);
            if (commonSelectInputDialog == null) {
                commonSelectInputDialog = new CommonSelectInputDialog(SendGoodsManagerActivity.this);
            }
            commonSelectInputDialog.show();
            commonSelectInputDialog.refreshContentList(companyList);
            //// TODO: 2020/1/17 这里可以通用
            commonSelectInputDialog.setTitle("请选择").setSelectOnly(true).setOnViewClickListener(new OnViewClickListener() {
                @Override
                public <T> void onClickAction(View v, String tag, T t) {
                    if (t != null) {
                        if (t instanceof CompanyBean) {
                            companyBean = (CompanyBean) t;
                            receiveCompanyNameTextView.setText(companyBean.getCompanyChineseName());
                        } else if (t instanceof BuBean) {
                            buBean = (BuBean) t;
                            receiveBuNameTextView.setText(buBean.getBuName());
                        }
//                        else if (t instanceof ReceiverCompanyBean) {
//                            receiverCompanyBean = (ReceiverCompanyBean) t;
//                            receiverCompanyNameTextView.setText(receiverCompanyBean.getCustomer());
//                        } else if (t instanceof LogisticsCompanyBean) {
//                            logisticsCompanyBean = (LogisticsCompanyBean) t;
//                            logisticsCompanyTextView.setText(logisticsCompanyBean.getLcName());
//                        } else if (t instanceof DeliveryTypeBean) {
//                            deliveryTypeBean = (DeliveryTypeBean) t;
//                            transportWayTextView.setText(deliveryTypeBean.getDelivery());
//                        }


                    }
                    if (commonSelectInputDialog != null && commonSelectInputDialog.isShowing()) {
                        commonSelectInputDialog.dismiss();
                    }
                }
            });
        }

    }

    private class GetCompanyListAsyncTask extends AsyncTask<String, Void, List<CompanyBean>> {

        @Override
        protected List<CompanyBean> doInBackground(String... strings) {
            String sql = "select Company_ID,Company_Chinese_Name,Company_English_Name from company where Company_Enabled = 1";
            WsResult result = WebServiceUtil.getDataTable(sql);
            List<CompanyBean> companyList = null;
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                Gson gson = new Gson();
                companyList = gson.fromJson(jsonData, new TypeToken<List<CompanyBean>>() {
                }.getType());
            }


            return companyList;
        }

        @Override
        protected void onPostExecute(List<CompanyBean> companyList) {
            super.onPostExecute(companyList);
            if (commonSelectInputDialog == null) {
                commonSelectInputDialog = new CommonSelectInputDialog(SendGoodsManagerActivity.this);
            }
            commonSelectInputDialog.show();
            commonSelectInputDialog.refreshContentList(companyList);
            commonSelectInputDialog.setTitle("请选择公司").setSelectOnly(true).setOnViewClickListener(new OnViewClickListener() {
                @Override
                public <T> void onClickAction(View v, String tag, T t) {
                    if (t != null && t instanceof CompanyBean) {
                        companyBean = (CompanyBean) t;
                        receiveCompanyNameTextView.setText(((CompanyBean) t).getCompanyChineseName());
                        if (commonSelectInputDialog != null && commonSelectInputDialog.isShowing()) {
                            commonSelectInputDialog.dismiss();
                        }

                    }
                }
            });
        }
    }

    private class CommitSendGoodsAsyncTask extends AsyncTask<String, Void, Void> {
        WsResult result;

        @Override
        protected Void doInBackground(String... strings) {
            if (boxItemEntityList != null && boxItemEntityList.size() > 0){
                result = WebServiceUtil.commitSendGoods(companyBean.getCompanyId(), buBean.getBuId(),boxItemEntityList.get(0), remark, 3);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (result != null) {
                if (result.getResult()) {
                    ToastUtil.showToastShort("手机发货成功！");
                } else {
                    ToastUtil.showToastShort("手机发货出错,原因是" + result.getErrorInfo());
                }
            } else {
                ToastUtil.showToastShort("手机发货出错");
            }

            keywordInputEditText.setText("");

        }
    }


}
