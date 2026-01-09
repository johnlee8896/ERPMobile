package com.chinashb.www.mobileerp.warehouse;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 6/7/25 10:55 AM
 * @author 作者: liweifeng
 * @description 处理物流来料检验，输入一些数据，保存
 */
public class DealCheckIncomeActivity extends BaseActivity {
    @BindView(R.id.deal_check_textView) TextView dealCheckTextView;
    @BindView(R.id.deal_check_btn_add_tray_photo) Button dealCheckBtnAddTrayPhoto;
    @BindView(R.id.deal_check_input_EditText) EditText dealCheckInputEditText;
    @BindView(R.id.deal_check_tv_item_name_col) TextView dealCheckTvItemNameCol;
    @BindView(R.id.deal_check_tv_bu_name_col) TextView dealCheckTvBuNameCol;
    @BindView(R.id.deal_check_tv_inv_in_lotno) TextView dealCheckTvInvInLotno;
    @BindView(R.id.deal_check_tv_qty_col) TextView dealCheckTvQtyCol;
    @BindView(R.id.deal_check_tv_ist_name_col) TextView dealCheckTvIstNameCol;
    @BindView(R.id.deal_check_tv_inv_in_selected) TextView dealCheckTvInvInSelected;
    @BindView(R.id.deal_check_check_number_EditText) EditText dealCheckCheckNumberEditText;
    @BindView(R.id.deal_check_real_number_EditText) EditText dealCheckRealNumberEditText;
    @BindView(R.id.deal_check_btn_save) Button dealCheckBtnSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_income_layout);
        ButterKnife.bind(this);
    }
}
