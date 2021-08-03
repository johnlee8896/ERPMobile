package com.chinashb.www.mobileerp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CommAlertDialog;
import com.chinashb.www.mobileerp.widget.OnDialogViewClickListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2021/7/21 3:11 PM
 * @author 作者: liweifeng
 * @description 成品校验界面
 */
public class ProductCheckSameActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.product_out_check_input_EditText) EditText inputEditText;
    @BindView(R.id.product_out_check_supplier_button) Button supplierButton;
    @BindView(R.id.product_out_check_shb_button) Button shbButton;
    @BindView(R.id.product_out_check_supplier_textView) TextView supplierTextView;

    private boolean hasSupplierClick = false;
    private String supplierCode = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_check_same_layout);
        ButterKnife.bind(this);
        setViewsListeners();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (!TextUtils.isEmpty(result.getContents())) {
                parseScanResult(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setViewsListeners() {
        supplierButton.setOnClickListener(this);
        shbButton.setOnClickListener(this);
        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                System.out.println("========================扫描结果:" + editable.toString());
                parseScanResult(editable.toString());
            }
        });
    }

    private void parseScanResult(String result) {
//        supplierTextView.setText("客户的产品型号是：" + "TJX2A 6804 006-L");
        if (TextUtils.isEmpty(supplierCode)){
            supplierTextView.setText("客户的产品型号是：" + "TJX2686101LP1");
            supplierCode = "TJX2686101LP1";
            inputEditText.setText("");
        }else{
                ToastUtil.showToastLong("客户产品型号校验成功！");
            if (TextUtils.equals(result,supplierCode)){
                ToastUtil.showToastLong("客户产品型号校验成功！");
            }else{
                CommAlertDialog.DialogBuilder builder = new CommAlertDialog.DialogBuilder(ProductCheckSameActivity.this)
                        .setTitle("校验失败！").setMessage("客户产品与公司发货产品不一致！")
                        .setLeftText("确定");


                builder.setOnViewClickListener(new OnDialogViewClickListener() {
                    @Override
                    public void onViewClick(Dialog dialog, View v, int tag) {
                        switch (tag) {
                            case CommAlertDialog.TAG_CLICK_LEFT:
//                                jumpToSwitchBuActivity();
                                dialog.dismiss();
                                break;
//                            case CommAlertDialog.TAG_CLICK_RIGHT:
//                                jumpToStockPartActivity();
//                                dialog.dismiss();
//                                break;
                        }
                    }
                });
                builder.create().show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == supplierButton) {
            hasSupplierClick = true;
            new IntentIntegrator(ProductCheckSameActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
        } else if (v == shbButton) {
            if (!hasSupplierClick) {
                ToastUtil.showToastLong("请先扫描供应商条码");
            }
            new IntentIntegrator(ProductCheckSameActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
        }

    }
}
