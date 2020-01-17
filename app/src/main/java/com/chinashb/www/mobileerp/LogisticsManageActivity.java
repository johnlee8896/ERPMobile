package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.BuBean;
import com.chinashb.www.mobileerp.bean.CompanyBean;
import com.chinashb.www.mobileerp.bean.DeliveryTypeBean;
import com.chinashb.www.mobileerp.bean.LogisticsCompanyBean;
import com.chinashb.www.mobileerp.bean.ReceiverCompanyBean;
import com.chinashb.www.mobileerp.bean.entity.LogisticsDeliveryEntity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.utils.UnitFormatUtil;
import com.chinashb.www.mobileerp.widget.CommonSelectInputDialog;
import com.chinashb.www.mobileerp.widget.TimePickerManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2020/1/10 16:07
 * @author 作者: xxblwf
 * @description 物流选择页面
 */

public class LogisticsManageActivity extends BaseActivity implements View.OnClickListener, OnViewClickListener {
    @BindView(R.id.logistics_trackNO_editText) EditText trackNOEditText;
    @BindView(R.id.logistics_out_date_textView) TextView outDateTextView;
    @BindView(R.id.logistics_load_car_time_textView) TextView loadCarTimeTextView;
    @BindView(R.id.logistics_sender_company_name_textView) TextView senderCompanyNameTextView;
    @BindView(R.id.logistics_sender_bu_name_textView) TextView senderBuNameTextView;
    @BindView(R.id.logistics_sender_remark_editText) EditText senderRemarkEditText;
    @BindView(R.id.logistics_receiver_company_name_textView) TextView receiverCompanyNameTextView;
    @BindView(R.id.logistics_receiver_address_editText) EditText receiverAddressEditText;
    @BindView(R.id.logistics_receiver_remark_editText) EditText receiverRemarkEditText;
    @BindView(R.id.logistics_domestic_foreign_textView) TextView domesticForeignTextView;
    @BindView(R.id.logistics_sample_lots_textView) TextView sampleLotsTextView;
    @BindView(R.id.logistics_send_or_replashment_editText) TextView sendOrReplashmentEditText;
    @BindView(R.id.logistics_transport_way_textView) TextView transportWayTextView;
    @BindView(R.id.logistics_logistics_company_textView) TextView logisticsCompanyTextView;
    @BindView(R.id.logistics_logistics_trackNO_editText) EditText logisticsTrackNOEditText;
    @BindView(R.id.logistics_arrive_date_textView) TextView arriveDateTextView;
    @BindView(R.id.logistics_logistics_day_number_editText) EditText dayNumberEditText;
    @BindView(R.id.logistics_driver_name_editText) EditText driverNameEditText;
    @BindView(R.id.logistics_telephone_editText) EditText telephoneEditText;
    @BindView(R.id.logistics_car_plate_editText) EditText carPlateEditText;
    @BindView(R.id.logistics_logistics_remark_editText) EditText logisticsRemarkEditText;
    @BindView(R.id.logistics_select_domestic_TextView) TextView domesticTextView;
    @BindView(R.id.logistics_select_foreign_textView) TextView foreignTextView;
    @BindView(R.id.logistics_select_sample_TextView) TextView sampleTextView;
    @BindView(R.id.logistics_select_lots_of_textView) TextView lotsOfTextView;
    @BindView(R.id.logistics_select_beipin_textView) TextView beipinTextView;
    @BindView(R.id.logistics_select_send_or_replashment_send_TextView) TextView sendOrReplashmentSendTextView;
    @BindView(R.id.logistics_select_send_or_replashment_replashment_TextView) TextView sendOrReplashmentReplashmentTextView;
    @BindView(R.id.logistics_title_confirm_button) TextView confirmButton;
    private TimePickerManager timePickerManager;
    private CommonSelectInputDialog commonSelectInputDialog;
    private String currentDate = "";
    private CompanyBean companyBean;
    private BuBean buBean;
    private ReceiverCompanyBean receiverCompanyBean;
    private LogisticsCompanyBean logisticsCompanyBean;
    private DeliveryTypeBean deliveryTypeBean;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logistics_layout);
        ButterKnife.bind(this);

        initView();
        setViewsLisener();
    }

    private void initView() {
        currentDate = UnitFormatUtil.formatTimeToDay(System.currentTimeMillis());
        outDateTextView.setText(UnitFormatUtil.formatTimeToDayChinese(System.currentTimeMillis()));
        arriveDateTextView.setText(UnitFormatUtil.formatTimeToDayChinese(System.currentTimeMillis()));
        loadCarTimeTextView.setText(UnitFormatUtil.formatTimeMDHM(System.currentTimeMillis()));

        domesticTextView.setSelected(true);
        lotsOfTextView.setSelected(true);
        sendOrReplashmentSendTextView.setSelected(true);

        senderCompanyNameTextView.setText("安徽胜华波汽车电器有限公司 ");
        senderBuNameTextView.setText("安徽雨刮");

    }

    private void setViewsLisener() {
        outDateTextView.setOnClickListener(this);
        loadCarTimeTextView.setOnClickListener(this);
        senderCompanyNameTextView.setOnClickListener(this);
        senderBuNameTextView.setOnClickListener(this);
        receiverCompanyNameTextView.setOnClickListener(this);
//        receiverAddressEditText.setOnClickListener(this);
//        sampleLotsTextView.setOnClickListener(this);
        sampleTextView.setOnClickListener(this);
        lotsOfTextView.setOnClickListener(this);
        beipinTextView.setOnClickListener(this);
//        sendOrReplashmentEditText.setOnClickListener(this);
        sendOrReplashmentSendTextView.setOnClickListener(this);
        sendOrReplashmentReplashmentTextView.setOnClickListener(this);
//        domesticForeignTextView.setOnClickListener(this);
        domesticTextView.setOnClickListener(this);
        foreignTextView.setOnClickListener(this);
        transportWayTextView.setOnClickListener(this);
        logisticsCompanyTextView.setOnClickListener(this);
        arriveDateTextView.setOnClickListener(this);
        confirmButton.setOnClickListener(this);


    }


    private void showTimePickerDialog(String pickType) {
        if (timePickerManager == null) {
            timePickerManager = new TimePickerManager(LogisticsManageActivity.this);
        }
        if (pickType == TimePickerManager.PICK_TYPE_ARRIVE_DATE || pickType == TimePickerManager.PICK_TYPE_OUT_DATE) {
            timePickerManager.setShowType(TimePickerManager.PICK_TYPE_YEAR, 3);
        } else {
            timePickerManager.setShowType(TimePickerManager.PICK_TYPE_YEAR, 5);
        }
        timePickerManager
                .setOnViewClickListener(LogisticsManageActivity.this)
                .showDialog(pickType);
    }

    private String getText(Date date) {
        return UnitFormatUtil.sdf_YMD_Chinese.format(date);
    }

    @Override public void onClick(View view) {
        if (view == outDateTextView) {
            showTimePickerDialog(TimePickerManager.PICK_TYPE_OUT_DATE);
        } else if (view == loadCarTimeTextView) {
            //1表示到分
            showTimePickerDialog(TimePickerManager.PICK_TYPE_LOAD_TIME);
        } else if (view == senderCompanyNameTextView) {
            showSelectCompanyDialog();
        } else if (view == senderBuNameTextView) {
            if (companyBean != null) {
                getSelectBuDialog();
            } else {
                ToastUtil.showToastShort("请先选择公司");
            }
        } else if (view == receiverCompanyNameTextView) {
            if (buBean != null) {
                getSelectReceiverCompanyDialog();
            } else {
                ToastUtil.showToastShort("请先选择车间");
            }
        }
//        else if (view == receiverAddressEditText) {
//
//        }
//        else if (view == sampleLotsTextView) {
//
//        } else if (view == sendOrReplashmentEditText) {
//
//        } else if (view == domesticForeignTextView) {
//
//        }
        else if (view == transportWayTextView) {
            getSelectLogisticsTypeDialog();
        } else if (view == logisticsCompanyTextView) {
            getSelectLogisticsCompanyDialog();
        } else if (view == arriveDateTextView) {
            showTimePickerDialog(TimePickerManager.PICK_TYPE_ARRIVE_DATE);
        } else if (view == domesticTextView) {
            domesticTextView.setSelected(true);
            foreignTextView.setSelected(false);
        } else if (view == foreignTextView) {
            domesticTextView.setSelected(false);
            foreignTextView.setSelected(true);
        } else if (view == sampleTextView) {
            sampleTextView.setSelected(true);
            lotsOfTextView.setSelected(false);
            beipinTextView.setSelected(false);
        } else if (view == lotsOfTextView) {
            sampleTextView.setSelected(false);
            lotsOfTextView.setSelected(true);
            beipinTextView.setSelected(false);
        } else if (view == beipinTextView) {
            sampleTextView.setSelected(false);
            lotsOfTextView.setSelected(false);
            beipinTextView.setSelected(true);
        } else if (view == sendOrReplashmentSendTextView) {
            sendOrReplashmentSendTextView.setSelected(true);
            sendOrReplashmentReplashmentTextView.setSelected(false);
        } else if (view == sendOrReplashmentReplashmentTextView) {
            sendOrReplashmentSendTextView.setSelected(false);
            sendOrReplashmentReplashmentTextView.setSelected(true);
        }else if (view == confirmButton){
            //如果是新建，应该要保存的
            if (judgeVerify()){
                LogisticsDeliveryEntity entity = new LogisticsDeliveryEntity();
                entity.setTrackNO(trackNOEditText.getText().toString());
                entity.setTelephone(telephoneEditText.getText().toString());
                entity.setLogisticsCompanyBean(logisticsCompanyBean);
                entity.setReceiverCompanyBean(receiverCompanyBean);
                entity.setSendCompanyBean(companyBean);
                entity.setDeliveryTypeBean(deliveryTypeBean);

                Intent intent = new Intent(this,ProductSaleOutActivity.class);
                //// TODO: 2020/1/17 传递一堆参数
                intent.putExtra(IntentConstant.Intent_Extra_logistics_entity,entity);
                setResult(IntentConstant.Intent_Request_Code_Product_To_Logistics,intent);
                finish();
            }
        }
    }

    private boolean judgeVerify() {
        if (TextUtils.isEmpty(trackNOEditText.getText())){
            ToastUtil.showToastShort("请输入内部跟踪号！");
            return false;
        }
        if (TextUtils.isEmpty(receiverCompanyNameTextView.getText())){
            ToastUtil.showToastShort("请选择客户公司！");
            return false;
        }
        if (TextUtils.isEmpty(receiverAddressEditText.getText())){
            ToastUtil.showToastShort("请填写客户公司地址！");
            return false;
        }

        if (TextUtils.isEmpty(arriveDateTextView.getText())){
            ToastUtil.showToastShort("请选择到货日期！");
            return false;
        }
        if (TextUtils.isEmpty(dayNumberEditText.getText())){
            ToastUtil.showToastShort("请输入运输天数！");
            return false;
        }
        if (TextUtils.isEmpty(driverNameEditText.getText())){
            ToastUtil.showToastShort("请输入司机或联系人姓名！");
            return false;
        }
        if (TextUtils.isEmpty(telephoneEditText.getText())){
            ToastUtil.showToastShort("请输入联系人电话！");
            return false;
        }
        if (TextUtils.isEmpty(carPlateEditText.getText())){
            ToastUtil.showToastShort("请输入车牌号！");
            return false;
        }
        return true;
    }

    private void getSelectLogisticsTypeDialog() {
        String sql = "Select * from dt ";
        GetCommonNameBeanListAsyncTask<DeliveryTypeBean> task = new GetCommonNameBeanListAsyncTask();
        task.execute(sql, "4");
    }

    private void getSelectLogisticsCompanyDialog() {
        String sql = "Select * from LC ";
        GetCommonNameBeanListAsyncTask<LogisticsCompanyBean> task = new GetCommonNameBeanListAsyncTask();
        task.execute(sql, "3");
    }

    private void getSelectReceiverCompanyDialog() {
        String sql = String.format("Select Customer_Facility.CF_ID,  Isnull(Customer_Chinese_Name,'')+' ['+isnull(Customer_English_Name,'')+']' As Customer, " +
                "CF_Chinese_Name As factory,CF_Address ,Country.CountryChinese As country From Customer  " +
                "Inner Join Bu_Customer On Bu_Customer.Customer_ID=Customer.Customer_ID  Inner Join Customer_Facility On Customer.Customer_ID=Customer_Facility.Customer_ID " +
                " Left Join Country On CF_Country=Country.ID  Where Bu_Customer.Bu_ID=%s And Isnull(CF_Enabled,1)=1  Order By Customer.Customer_ID ", buBean.getBuId());
        GetCommonNameBeanListAsyncTask<ReceiverCompanyBean> task = new GetCommonNameBeanListAsyncTask();
        task.execute(sql, "2");
    }

    private void getSelectBuDialog() {
        GetCommonNameBeanListAsyncTask<BuBean> task = new GetCommonNameBeanListAsyncTask();
        String sql = String.format("Select Bu_ID,Bu_Name From Bu Where Company_ID= %s  And Enabled=1", companyBean.getCompanyId());
        task.execute(sql, "1");
    }

    private void showSelectCompanyDialog() {
        new GetCompanyListAsyncTask().execute();
    }

    @Override public <T> void onClickAction(View v, String tag, T date) {
        if (tag.equals(TimePickerManager.PICK_TYPE_OUT_DATE)) {
            outDateTextView.setText(getText((Date) date));
        } else if (tag.equals(TimePickerManager.PICK_TYPE_LOAD_TIME)) {
            loadCarTimeTextView.setText(UnitFormatUtil.sdf_MDHM.format((Date) date));
        } else if (tag.equals(TimePickerManager.PICK_TYPE_ARRIVE_DATE)) {
            arriveDateTextView.setText(getText((Date) date));
        }
    }

    private class GetCompanyListAsyncTask extends AsyncTask<String, Void, List<CompanyBean>> {

        @Override protected List<CompanyBean> doInBackground(String... strings) {
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
                commonSelectInputDialog = new CommonSelectInputDialog(LogisticsManageActivity.this);
            }
            commonSelectInputDialog.show();
            commonSelectInputDialog.refreshContentList(companyList);
            commonSelectInputDialog.setTitle("请选择公司").setSelectOnly(true).setOnViewClickListener(new OnViewClickListener() {
                @Override public <T> void onClickAction(View v, String tag, T t) {
                    if (t != null && t instanceof CompanyBean) {
                        companyBean = (CompanyBean) t;
                        senderCompanyNameTextView.setText(((CompanyBean) t).getCompanyChineseName());
                        if (commonSelectInputDialog != null && commonSelectInputDialog.isShowing()) {
                            commonSelectInputDialog.dismiss();
                        }

                    }
                }
            });
        }
    }

    private class GetCommonNameBeanListAsyncTask<T> extends AsyncTask<String, Void, List<T>> {

        @Override protected List<T> doInBackground(String... strings) {
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

        @Override protected void onPostExecute(List<T> companyList) {
            super.onPostExecute(companyList);
            if (commonSelectInputDialog == null) {
                commonSelectInputDialog = new CommonSelectInputDialog(LogisticsManageActivity.this);
            }
            commonSelectInputDialog.show();
            commonSelectInputDialog.refreshContentList(companyList);
            //// TODO: 2020/1/17 这里可以通用
            commonSelectInputDialog.setTitle("请选择").setSelectOnly(true).setOnViewClickListener(new OnViewClickListener() {
                @Override public <T> void onClickAction(View v, String tag, T t) {
                    if (t != null) {
                        if (t instanceof CompanyBean) {
                            companyBean = (CompanyBean) t;
                            senderCompanyNameTextView.setText(companyBean.getCompanyChineseName());
                        } else if (t instanceof BuBean) {
                            buBean = (BuBean) t;
                            senderBuNameTextView.setText(buBean.getBuName());
                        } else if (t instanceof ReceiverCompanyBean) {
                            receiverCompanyBean = (ReceiverCompanyBean) t;
                            receiverCompanyNameTextView.setText(receiverCompanyBean.getCustomer());
                        } else if (t instanceof LogisticsCompanyBean) {
                            logisticsCompanyBean = (LogisticsCompanyBean) t;
                            logisticsCompanyTextView.setText(logisticsCompanyBean.getLcName());
                        } else if (t instanceof DeliveryTypeBean) {
                            deliveryTypeBean = (DeliveryTypeBean) t;
                            logisticsCompanyTextView.setText(deliveryTypeBean.getDelivery());
                        }


                    }
                    if (commonSelectInputDialog != null && commonSelectInputDialog.isShowing()) {
                        commonSelectInputDialog.dismiss();
                    }
                }
            });
        }

    }

}
