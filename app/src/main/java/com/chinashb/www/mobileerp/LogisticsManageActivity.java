package com.chinashb.www.mobileerp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.CompanyBean;
import com.chinashb.www.mobileerp.bean.entity.WCSubProductEntity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.UnitFormatUtil;
import com.chinashb.www.mobileerp.widget.CommonSelectInputDialog;
import com.chinashb.www.mobileerp.widget.TimePickerManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
    private TimePickerManager timePickerManager;
    private CommonSelectInputDialog commonSelectInputDialog;
    private String currentDate = "";

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

    }

    private void setViewsLisener() {
        outDateTextView.setOnClickListener(this);
        loadCarTimeTextView.setOnClickListener(this);
        senderCompanyNameTextView.setOnClickListener(this);
        senderBuNameTextView.setOnClickListener(this);
        receiverCompanyNameTextView.setOnClickListener(this);
        receiverAddressTextView.setOnClickListener(this);
        sampleLotsTextView.setOnClickListener(this);
        sendOrReplashmentEditText.setOnClickListener(this);
        domesticForeignTextView.setOnClickListener(this);
        transportWayTextView.setOnClickListener(this);
        logisticsCompanyTextView.setOnClickListener(this);
        arriveDateTextView.setOnClickListener(this);
    }

    private void showTimePickerDialog(String pickType) {
        if (timePickerManager == null){
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
        if (view == outDateTextView){
            showTimePickerDialog(TimePickerManager.PICK_TYPE_START);
        }else if (view == loadCarTimeTextView){
            showTimePickerDialog(TimePickerManager.PICK_TYPE_MINUTE);
        }else if (view == senderCompanyNameTextView){
            showSelectCompanyDialog();
        }else if (view == senderBuNameTextView){

        }else if (view == receiverCompanyNameTextView){

        }else if (view == receiverAddressTextView){

        }else if (view == sampleLotsTextView){

        }else if (view == sendOrReplashmentEditText){

        }else if (view == domesticForeignTextView){

        }else if (view == transportWayTextView){

        }else if (view == logisticsCompanyTextView){

        }else if (view == arriveDateTextView){
            showTimePickerDialog(TimePickerManager.PICK_TYPE_END);
        }
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
                 companyList = gson.fromJson(jsonData, new TypeToken<List<WCSubProductEntity>>() {
                 }.getType());
             }


             return companyList;
         }

         @Override
         protected void onPostExecute(List<CompanyBean> companyList) {
             super.onPostExecute(companyList);
             if (commonSelectInputDialog == null){
                 commonSelectInputDialog = new CommonSelectInputDialog(LogisticsManageActivity.this);
             }
             commonSelectInputDialog.show();
             commonSelectInputDialog.refreshContentList(companyList);
             commonSelectInputDialog.setTitle("请选择公司").setSelectOnly(true).setOnViewClickListener(new OnViewClickListener() {
                 @Override public <T> void onClickAction(View v, String tag, T t) {
                     if (t != null && t instanceof CompanyBean){
                         senderCompanyNameTextView.setText(((CompanyBean)t).getCompanyChineseName());
                     }
                 }
             });
         }
     }
}
