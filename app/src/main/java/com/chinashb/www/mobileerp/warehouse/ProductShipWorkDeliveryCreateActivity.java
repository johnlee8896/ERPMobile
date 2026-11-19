package com.chinashb.www.mobileerp.warehouse;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.ShipWorkModels.DeliveryDraft;
import com.chinashb.www.mobileerp.bean.ShipWorkModels.LookupOption;
import com.chinashb.www.mobileerp.funs.ShipWorkApi;
import com.chinashb.www.mobileerp.utils.ShipWorkFormatUtil;
import com.chinashb.www.mobileerp.utils.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 成品发货 P06/P07 - 新建物流单。
 */
public class ProductShipWorkDeliveryCreateActivity extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_DO_ID = "extra_do_id";
    public static final String EXTRA_DELIVERY_ID = "extra_delivery_id";

    private long doId;
    private RadioGroup modeRadioGroup;
    private TextView statusTextView;
    private LinearLayout formContainer;
    private Button submitButton;

    private final Map<String, EditText> editTexts = new HashMap<>();
    private final Map<String, Spinner> spinners = new HashMap<>();
    private DeliveryDraft draft;
    private boolean busy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_ship_work_delivery_create_layout);

        doId = getIntent().getLongExtra(EXTRA_DO_ID, 0);
        modeRadioGroup = findViewById(R.id.product_ship_work_delivery_mode_radioGroup);
        statusTextView = findViewById(R.id.product_ship_work_delivery_status_textView);
        formContainer = findViewById(R.id.product_ship_work_delivery_form_container);
        submitButton = findViewById(R.id.product_ship_work_delivery_submit_button);

        buildForm();
        submitButton.setOnClickListener(this);
        modeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> loadDraft());
        loadInitialData();
    }

    private void buildForm() {
        addText("TrackNo", "内部跟踪号 *", "请输入内部跟踪号", InputType.TYPE_CLASS_TEXT);
        addDate("DeliveryDate", "发货日期 *");
        addDate("ShippingDate", "装运日期 *");
        addText("LoadTime", "装车时间", "yyyy-MM-dd HH:mm", InputType.TYPE_CLASS_TEXT);
        addText("ETA", "运输天数 *", "0-255", InputType.TYPE_CLASS_NUMBER);
        addDate("ArriveDate", "预计到货日期 *");
        addSpinner("DT_ID", "运输方式");
        addSpinner("LC_ID", "物流公司");
        addText("DCNo", "物流跟踪号", "", InputType.TYPE_CLASS_TEXT);
        addText("CheckNo", "回单号", "", InputType.TYPE_CLASS_TEXT);
        addText("ContractNo", "运单号或合同协议号", "", InputType.TYPE_CLASS_TEXT);
        addText("DriverName", "司机姓名 *", "", InputType.TYPE_CLASS_TEXT);
        addText("DriverTel", "司机电话 *", "", InputType.TYPE_CLASS_PHONE);
        addText("LicensePlateNo", "车牌号 *", "", InputType.TYPE_CLASS_TEXT);
        addCarSpinner("CarType", "车辆类型", new String[]{"整车", "零担"});
        addCarSpinner("CarAllLength", "车辆长度", new String[]{"4.2米", "6.8米", "9.6米", "13米半挂"});
        addCarSpinner("TransferBackType", "回程类型", new String[]{"单程", "往返"});
        addText("ReceiverName", "收货人 *", "", InputType.TYPE_CLASS_TEXT);
        addText("ReceiverTel", "收货电话 *", "", InputType.TYPE_CLASS_PHONE);
        addMultiline("DestinationAddress", "目的地地址");
        addText("PalletNumber", "托盘数量 *", "0", InputType.TYPE_CLASS_NUMBER);
        addDecimal("PalletVolume", "单托体积");
        addDecimal("TotalVolume", "总体积");
        addDecimal("SingleWeight", "单托重量");
        addDecimal("TotalWeight", "总重量");
        addCarSpinner("FeeMethod", "付费方", new String[]{"未指定", "我方付费", "客户付费"});
        addDecimal("Fee", "物流费用");
        addMultiline("SenderRemark", "发货方备注");
        addMultiline("ReceiverRemark", "收货方备注");
        addMultiline("Remark", "物流备注");
    }

    private void addText(String key, String label, String hint, int inputType) {
        EditText editText = new EditText(this);
        editText.setSingleLine(true);
        editText.setHint(hint);
        editText.setInputType(inputType);
        editTexts.put(key, editText);
        formContainer.addView(createRow(label, editText));
    }

    private void addDate(String key, String label) {
        EditText editText = new EditText(this);
        editText.setSingleLine(true);
        editText.setHint("yyyy-MM-dd");
        editText.setFocusable(false);
        editText.setOnClickListener(v -> showDatePicker(editText));
        editTexts.put(key, editText);
        formContainer.addView(createRow(label, editText));
    }

    private void addDecimal(String key, String label) {
        EditText editText = new EditText(this);
        editText.setSingleLine(true);
        editText.setHint("0");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editTexts.put(key, editText);
        formContainer.addView(createRow(label, editText));
    }

    private void addMultiline(String key, String label) {
        EditText editText = new EditText(this);
        editText.setMinLines(2);
        editText.setGravity(android.view.Gravity.TOP);
        editTexts.put(key, editText);
        formContainer.addView(createRow(label, editText));
    }

    private void addSpinner(String key, String label) {
        Spinner spinner = new Spinner(this);
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new ArrayList<String>(java.util.Arrays.asList("请选择"))));
        spinners.put(key, spinner);
        formContainer.addView(createRow(label, spinner));
    }

    private void addCarSpinner(String key, String label, String[] values) {
        Spinner spinner = new Spinner(this);
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, values));
        spinners.put(key, spinner);
        formContainer.addView(createRow(label, spinner));
    }

    private LinearLayout createRow(String label, View input) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        row.setPadding(0, 8, 0, 8);
        TextView labelView = new TextView(this);
        labelView.setText(label);
        labelView.setTextSize(14);
        labelView.setTextColor(getResources().getColor(R.color.black));
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.45f);
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.55f);
        row.addView(labelView, labelParams);
        row.addView(input, inputParams);
        return row;
    }

    private void showDatePicker(final EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        String value = editText.getText().toString();
        if (!value.isEmpty()) {
            try {
                calendar.setTime(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(value));
            } catch (Exception ignored) {
            }
        }
        new DatePickerDialog(this, (view, year, month, day) ->
                editText.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day)),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void loadInitialData() {
        statusTextView.setText("正在读取物流单草稿和选项");
        new LoadInitialDataAsyncTask().execute();
    }

    private void loadDraft() {
        boolean copyLast = modeRadioGroup.getCheckedRadioButtonId()
                == R.id.product_ship_work_delivery_copy_last_radioButton;
        new LoadDraftAsyncTask().execute(copyLast);
    }

    @Override
    public void onClick(View v) {
        if (v == submitButton) {
            submit();
        }
    }

    private void submit() {
        if (busy) {
            return;
        }
        if (isBlank("TrackNo") || isBlank("DeliveryDate") || isBlank("ShippingDate")
                || isBlank("ETA") || isBlank("ArriveDate") || isBlank("DriverName")
                || isBlank("DriverTel") || isBlank("LicensePlateNo") || isBlank("ReceiverName")
                || isBlank("ReceiverTel") || isBlank("PalletNumber")) {
            ToastUtil.showToastShort("请填写所有必填项");
            return;
        }
        if (draft == null) {
            draft = new DeliveryDraft();
        }
        draft.trackNo = text("TrackNo");
        draft.deliveryDate = text("DeliveryDate");
        draft.shippingDate = text("ShippingDate");
        draft.loadTime = text("LoadTime");
        draft.arriveDate = text("ArriveDate");
        draft.eta = intValue("ETA");
        draft.dtId = spinnerId("DT_ID");
        draft.lcId = spinnerId("LC_ID");
        draft.dcNo = text("DCNo");
        draft.checkNo = text("CheckNo");
        draft.contractNo = text("ContractNo");
        draft.driverName = text("DriverName");
        draft.driverTel = text("DriverTel");
        draft.licensePlateNo = text("LicensePlateNo");
        draft.receiverName = text("ReceiverName");
        draft.receiverTel = text("ReceiverTel");
        draft.destinationAddress = text("DestinationAddress");
        draft.palletNumber = intValue("PalletNumber");
        draft.palletVolume = doubleValue("PalletVolume");
        draft.totalVolume = doubleValue("TotalVolume");
        draft.singleWeight = doubleValue("SingleWeight");
        draft.totalWeight = doubleValue("TotalWeight");
        draft.carType = spinnerPosition("CarType");
        draft.carAllLength = spinnerPosition("CarAllLength");
        draft.transferBackType = spinnerPosition("TransferBackType");
        draft.feeMethod = feeMethodValue();
        draft.fee = doubleValue("Fee");
        draft.senderRemark = text("SenderRemark");
        draft.receiverRemark = text("ReceiverRemark");
        draft.remark = text("Remark");

        busy = true;
        submitButton.setEnabled(false);
        new CreateDeliveryAsyncTask().execute(doId);
    }

    private boolean isBlank(String key) {
        return text(key).trim().isEmpty();
    }

    private String text(String key) {
        EditText editText = editTexts.get(key);
        return editText == null ? "" : editText.getText().toString().trim();
    }

    private int intValue(String key) {
        try {
            return Integer.parseInt(text(key));
        } catch (Exception e) {
            return 0;
        }
    }

    private double doubleValue(String key) {
        try {
            return Double.parseDouble(text(key));
        } catch (Exception e) {
            return 0;
        }
    }

    private int spinnerPosition(String key) {
        Spinner spinner = spinners.get(key);
        return spinner == null ? 0 : spinner.getSelectedItemPosition();
    }

    private int spinnerId(String key) {
        Spinner spinner = spinners.get(key);
        if (spinner == null) {
            return 0;
        }
        int position = spinner.getSelectedItemPosition();
        return position <= 0 ? 0 : (Integer) spinner.getTag(position);
    }

    private String feeMethodValue() {
        Spinner spinner = spinners.get("FeeMethod");
        int position = spinner == null ? 0 : spinner.getSelectedItemPosition();
        return position <= 0 ? "" : spinner.getSelectedItem().toString();
    }

    private class LoadInitialDataAsyncTask extends AsyncTask<Void, Void, Object[]> {
        @Override
        protected Object[] doInBackground(Void... params) {
            ShipWorkApi.Result<List<LookupOption>> types = ShipWorkApi.getDeliveryTypes();
            ShipWorkApi.Result<List<LookupOption>> companies = ShipWorkApi.getLogisticsCompanies();
            return new Object[]{types, companies};
        }

        @Override
        protected void onPostExecute(Object[] values) {
            @SuppressWarnings("unchecked")
            ShipWorkApi.Result<List<LookupOption>> types = (ShipWorkApi.Result<List<LookupOption>>) values[0];
            @SuppressWarnings("unchecked")
            ShipWorkApi.Result<List<LookupOption>> companies = (ShipWorkApi.Result<List<LookupOption>>) values[1];
            bindLookupSpinner("DT_ID", types);
            bindLookupSpinner("LC_ID", companies);
            loadDraft();
        }
    }

    private void bindLookupSpinner(String key, ShipWorkApi.Result<List<LookupOption>> result) {
        Spinner spinner = spinners.get(key);
        if (spinner == null || result == null || !result.success || result.data == null) {
            return;
        }
        List<String> names = new ArrayList<>();
        names.add("请选择");
        for (LookupOption option : result.data) {
            names.add(option.name);
            spinner.setTag(names.size() - 1, option.id);
        }
        spinner.setAdapter(new ArrayAdapter<>(ProductShipWorkDeliveryCreateActivity.this,
                android.R.layout.simple_spinner_item, names));
    }

    private class LoadDraftAsyncTask extends AsyncTask<Boolean, Void, ShipWorkApi.Result<DeliveryDraft>> {
        @Override
        protected ShipWorkApi.Result<DeliveryDraft> doInBackground(Boolean... params) {
            return ShipWorkApi.getDeliveryDraft(doId, params[0]);
        }

        @Override
        protected void onPostExecute(ShipWorkApi.Result<DeliveryDraft> result) {
            if (result.success) {
                draft = result.data;
                fillDraft();
                statusTextView.setText(draft == null ? "没有最近物流单，已按全新建立准备" : "");
            } else {
                statusTextView.setText(result.errorInfo);
            }
        }
    }

    private void fillDraft() {
        if (draft == null) {
            for (String key : editTexts.keySet()) {
                editTexts.get(key).setText("");
            }
            return;
        }
        setText("TrackNo", draft.trackNo);
        setText("DeliveryDate", ShipWorkFormatUtil.date(draft.deliveryDate));
        setText("ShippingDate", ShipWorkFormatUtil.date(draft.shippingDate));
        setText("LoadTime", ShipWorkFormatUtil.dateTime(draft.loadTime));
        setText("ETA", String.valueOf(draft.eta));
        setText("ArriveDate", ShipWorkFormatUtil.date(draft.arriveDate));
        setText("DCNo", draft.dcNo);
        setText("CheckNo", draft.checkNo);
        setText("ContractNo", draft.contractNo);
        setText("DriverName", draft.driverName);
        setText("DriverTel", draft.driverTel);
        setText("LicensePlateNo", draft.licensePlateNo);
        setText("ReceiverName", draft.receiverName);
        setText("ReceiverTel", draft.receiverTel);
        setText("DestinationAddress", draft.destinationAddress);
        setText("PalletNumber", String.valueOf(draft.palletNumber));
        setText("PalletVolume", formatDecimal(draft.palletVolume));
        setText("TotalVolume", formatDecimal(draft.totalVolume));
        setText("SingleWeight", formatDecimal(draft.singleWeight));
        setText("TotalWeight", formatDecimal(draft.totalWeight));
        setText("Fee", formatDecimal(draft.fee));
        setText("SenderRemark", draft.senderRemark);
        setText("ReceiverRemark", draft.receiverRemark);
        setText("Remark", draft.remark);
        setSpinnerById("DT_ID", draft.dtId);
        setSpinnerById("LC_ID", draft.lcId);
        setSpinnerPosition("CarType", draft.carType);
        setSpinnerPosition("CarAllLength", draft.carAllLength);
        setSpinnerPosition("TransferBackType", draft.transferBackType);
        setSpinnerPosition("FeeMethod", feeMethodPosition(draft.feeMethod));
    }

    private void setText(String key, String value) {
        EditText editText = editTexts.get(key);
        if (editText != null) {
            editText.setText(value == null ? "" : value);
        }
    }

    private void setSpinnerPosition(String key, int position) {
        Spinner spinner = spinners.get(key);
        if (spinner != null && position >= 0 && position < spinner.getAdapter().getCount()) {
            spinner.setSelection(position);
        }
    }

    private void setSpinnerById(String key, int id) {
        Spinner spinner = spinners.get(key);
        if (spinner == null || id <= 0) {
            return;
        }
        for (int i = 1; i < spinner.getAdapter().getCount(); i++) {
            Object tag = spinner.getTag(i);
            if (tag instanceof Integer && (Integer) tag == id) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private int feeMethodPosition(String value) {
        if ("我方付费".equals(value)) {
            return 1;
        }
        if ("客户付费".equals(value)) {
            return 2;
        }
        return 0;
    }

    private String formatDecimal(double value) {
        return value == Math.rint(value) ? String.valueOf((long) value) : String.valueOf(value);
    }

    private class CreateDeliveryAsyncTask extends AsyncTask<Long, Void, ShipWorkApi.Result<Void>> {
        @Override
        protected ShipWorkApi.Result<Void> doInBackground(Long... params) {
            return ShipWorkApi.createDelivery(params[0], draft);
        }

        @Override
        protected void onPostExecute(ShipWorkApi.Result<Void> result) {
            busy = false;
            submitButton.setEnabled(true);
            if (result.success) {
                Intent data = new Intent();
                data.putExtra(EXTRA_DELIVERY_ID, result.id);
                setResult(RESULT_OK, data);
                finish();
            } else {
                statusTextView.setText(result.errorInfo);
            }
        }
    }
}
