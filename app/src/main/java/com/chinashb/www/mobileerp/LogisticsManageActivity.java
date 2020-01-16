package com.chinashb.www.mobileerp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.BuBean;
import com.chinashb.www.mobileerp.bean.CompanyBean;
import com.chinashb.www.mobileerp.bean.ReceiverCompanyBean;
import com.chinashb.www.mobileerp.bean.entity.WCSubProductEntity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
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
    @BindView(R.id.logistics_receiver_address_textView) TextView receiverAddressTextView;
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
    private TimePickerManager timePickerManager;
    private CommonSelectInputDialog commonSelectInputDialog;
    private String currentDate = "";
    private CompanyBean companyBean;
    private BuBean buBean;
    private ReceiverCompanyBean receiverCompanyBean;

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
        receiverAddressTextView.setOnClickListener(this);
//        sampleLotsTextView.setOnClickListener(this);
        sampleTextView.setOnClickListener(this);
        lotsOfTextView.setOnClickListener(this);
        beipinTextView.setOnClickListener(this);
//        sendOrReplashmentEditText.setOnClickListener(this);
        sendOrReplashmentSendTextView.setOnClickListener(this);
        sendOrReplashmentSendTextView.setOnClickListener(this);
//        domesticForeignTextView.setOnClickListener(this);
        domesticTextView.setOnClickListener(this);
        foreignTextView.setOnClickListener(this);
        transportWayTextView.setOnClickListener(this);
        logisticsCompanyTextView.setOnClickListener(this);
        arriveDateTextView.setOnClickListener(this);


    }

    private void showTimePickerDialog(String pickType) {
        if (timePickerManager == null) {
            timePickerManager = new TimePickerManager(LogisticsManageActivity.this);
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
            showTimePickerDialog(TimePickerManager.PICK_TYPE_START);
        } else if (view == loadCarTimeTextView) {
            showTimePickerDialog(TimePickerManager.PICK_TYPE_MINUTE);
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
        } else if (view == receiverAddressTextView) {

        }
//        else if (view == sampleLotsTextView) {
//
//        } else if (view == sendOrReplashmentEditText) {
//
//        } else if (view == domesticForeignTextView) {
//
//        }
        else if (view == transportWayTextView) {

        } else if (view == logisticsCompanyTextView) {

        } else if (view == arriveDateTextView) {
            showTimePickerDialog(TimePickerManager.PICK_TYPE_END);
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
        }
    }

    private void getSelectReceiverCompanyDialog() {
        String sql = String.format("Select Customer_Facility.CF_ID,  Isnull(Customer_Chinese_Name,'')+' ['+isnull(Customer_English_Name,'')+']' As Customer, " +
                "CF_Chinese_Name As factory,CF_Address ,Country.CountryChinese As country From Customer  " +
                "Inner Join Bu_Customer On Bu_Customer.Customer_ID=Customer.Customer_ID  Inner Join Customer_Facility On Customer.Customer_ID=Customer_Facility.Customer_ID " +
                " Left Join Country On CF_Country=Country.ID  Where Bu_Customer.Bu_ID=%s And Isnull(CF_Enabled,1)=1  Order By Customer.Customer_ID ", buBean.getBuId());
        GetCommonNameBeanListAsyncTask task = new GetCommonNameBeanListAsyncTask();
        task.execute(sql);
    }

    private void getSelectBuDialog() {
        GetCommonNameBeanListAsyncTask task = new GetCommonNameBeanListAsyncTask();
        task.execute(String.format("Select Bu_ID,Bu_Name From Bu Where Company_ID= %s  And Enabled=1", companyBean.getCompanyId()));
    }

    private void showSelectCompanyDialog() {
        new GetCompanyListAsyncTask().execute();
    }

    @Override public <T> void onClickAction(View v, String tag, T date) {
        if (tag.equals(TimePickerManager.PICK_TYPE_START)) {
            getText((Date) date);
        } else if (tag.equals(TimePickerManager.PICK_TYPE_END)) {
            getText((Date) date);
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
                        if (commonSelectInputDialog != null && commonSelectInputDialog.isShowing()){
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
            WsResult result = WebServiceUtil.getDataTable(sql);
            List<T> companyList = null;
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                Gson gson = new Gson();
                companyList = gson.fromJson(jsonData, new TypeToken<List<T>>() {
                }.getType());
                if (companyList != null && companyList.size() > 0){
                    if (companyList.get(0) instanceof ReceiverCompanyBean){
                        //去掉重复
                        List<ReceiverCompanyBean> tempList = new ArrayList<>();
                        List<String> nameList = new ArrayList<>();
                        for (T bean : companyList){
                            if (!nameList.contains(((ReceiverCompanyBean)bean).getCustomer())){
                                nameList.add(((ReceiverCompanyBean)bean).getCustomer());
                                tempList.add((ReceiverCompanyBean)bean);
                            }
                        }
                        return (List<T>) tempList;
                    }
                }
            }


            return companyList;
        }

        @Override
        protected void onPostExecute(List<T> companyList) {
            super.onPostExecute(companyList);
            if (commonSelectInputDialog == null) {
                commonSelectInputDialog = new CommonSelectInputDialog(LogisticsManageActivity.this);
            }
            commonSelectInputDialog.show();
            commonSelectInputDialog.refreshContentList(companyList);
            commonSelectInputDialog.setTitle("请选择公司").setSelectOnly(true).setOnViewClickListener(new OnViewClickListener() {
                @Override public <T> void onClickAction(View v, String tag, T t) {
                    if (t != null) {
                        if (t instanceof CompanyBean) {
                            companyBean = (CompanyBean) t;
                            senderCompanyNameTextView.setText(companyBean.getCompanyChineseName());
                        } else if (t instanceof BuBean) {
                            buBean = (BuBean) t;
                            senderBuNameTextView.setText(buBean.getBuName());
                        } else if(t instanceof ReceiverCompanyBean){
                            receiverCompanyBean = (ReceiverCompanyBean) t;
                            receiverCompanyNameTextView.setText(receiverCompanyBean.getCustomer());
                        }

                    }
                    if (commonSelectInputDialog != null && commonSelectInputDialog.isShowing()){
                        commonSelectInputDialog.dismiss();
                    }
                }
            });
        }
    }
}
