package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chinashb.www.mobileerp.basicobject.IstPlaceEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.JsonUtil;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * created by code-x John
 * start: 2026-05-06 15:12:18 CST
 * end: 2026-05-06 15:12:18 CST
 * 物流接收及物流区移位管理
 */
public class NewLogisticsManageActivity extends BaseActivity {

    // created by code-x John
    // start: 2026-05-06 15:12:18 CST
    // end: 2026-05-06 15:12:18 CST
    private static final int INPUT_MIN_LENGTH = 3;
    private static final int MAX_RECENT_RECORD_COUNT = 30;

    @BindView(R.id.new_logistics_accept_button)
    Button acceptButton;
    @BindView(R.id.new_logistics_move_manifest_button)
    Button moveManifestButton;
    @BindView(R.id.new_logistics_move_pallet_button)
    Button movePalletButton;
    @BindView(R.id.new_logistics_status_text)
    TextView statusText;
    @BindView(R.id.new_logistics_input_edit)
    EditText inputEdit;
    @BindView(R.id.new_logistics_camera_button)
    Button cameraButton;
    @BindView(R.id.new_logistics_action_button)
    Button actionButton;
    @BindView(R.id.new_logistics_reset_button)
    Button resetButton;
    @BindView(R.id.new_logistics_clear_button)
    Button clearButton;
    @BindView(R.id.new_logistics_scan_list)
    CustomRecyclerView scanListView;

    @BindView(R.id.new_logistics_manifest_scan_text)
    TextView manifestScanText;
    @BindView(R.id.new_logistics_diii_scan_text)
    TextView diiiScanText;
    @BindView(R.id.new_logistics_location_scan_text)
    TextView locationScanText;

    @BindView(R.id.new_logistics_supplier_text)
    TextView supplierText;
    @BindView(R.id.new_logistics_sender_address_text)
    TextView senderAddressText;
    @BindView(R.id.new_logistics_sender_contact_text)
    TextView senderContactText;
    @BindView(R.id.new_logistics_sender_tel_text)
    TextView senderTelText;

    @BindView(R.id.new_logistics_receiver_text)
    TextView receiverText;
    @BindView(R.id.new_logistics_receiver_address_text)
    TextView receiverAddressText;
    @BindView(R.id.new_logistics_receiver_contact_text)
    TextView receiverContactText;
    @BindView(R.id.new_logistics_receiver_tel_text)
    TextView receiverTelText;

    @BindView(R.id.new_logistics_delivery_no_text)
    TextView deliveryNoText;
    @BindView(R.id.new_logistics_company_text)
    TextView logisticsCompanyText;
    @BindView(R.id.new_logistics_track_no_text)
    TextView logisticsTrackNoText;
    @BindView(R.id.new_logistics_logistics_contact_text)
    TextView logisticsContactText;
    @BindView(R.id.new_logistics_logistics_tel_text)
    TextView logisticsTelText;
    @BindView(R.id.new_logistics_ship_time_text)
    TextView shipTimeText;
    @BindView(R.id.new_logistics_receive_time_text)
    TextView receiveTimeText;
    @BindView(R.id.new_logistics_hours_text)
    TextView hoursText;
    @BindView(R.id.new_logistics_remark_text)
    TextView remarkText;

    private final List<DisplayEntity> displayList = new ArrayList<>();
    private final List<DisplayEntity> currentDetailList = new ArrayList<>();
    private final List<DisplayEntity> recentRecordList = new ArrayList<>();

    private ScanAdapter adapter;
    private Mode currentMode = Mode.ACCEPT_MANIFEST;
    private boolean isParsingScan;

    private long currentManifestId;
    private String currentManifestScan = "";
    private String currentManifestInfoJson = "";

    private long currentDiiiId;
    private String currentDiiiScan = "";
    private String currentDiiiInfoJson = "";

    private IstPlaceEntity currentLocation;
    private String currentLocationScan = "";
    private String currentLocationInfoJson = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_logistics_manage);
        ButterKnife.bind(this);

        initRecyclerView();
        initListener();
        refreshUi();
    }

    private void initRecyclerView() {
        adapter = new ScanAdapter(this, displayList);
        scanListView.setAdapter(adapter);
    }

    private void initListener() {
        inputEdit.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void onTextChangedSafe(CharSequence text) {
                  if (text != null && text.toString().trim().length() > INPUT_MIN_LENGTH) {
                    parseScan(text.toString());
                }
            }
        });
    }

    @OnClick({
            R.id.new_logistics_accept_button,
            R.id.new_logistics_move_manifest_button,
            R.id.new_logistics_move_pallet_button
    })
    public void onModeClick(View view) {
        if (view == acceptButton) {
            switchMode(Mode.ACCEPT_MANIFEST);
        } else if (view == moveManifestButton) {
            switchMode(Mode.MOVE_MANIFEST);
        } else if (view == movePalletButton) {
            switchMode(Mode.MOVE_PALLET);
        }
    }

    @OnClick(R.id.new_logistics_camera_button)
    public void onCameraClick() {
        new IntentIntegrator(this)
                .setCaptureActivity(CustomScannerActivity.class)
                .initiateScan();
    }

    @OnClick(R.id.new_logistics_action_button)
    public void onActionClick() {
        if (!isReadyToExecute()) {
            ToastUtil.showToastShort(currentMode.getActionHint());
            focusInput();
            return;
        }

        isParsingScan = true;
        if (currentMode == Mode.ACCEPT_MANIFEST) {
            new AcceptManifestTask().execute();
        } else if (currentMode == Mode.MOVE_MANIFEST) {
            new UpdateManifestLocationTask().execute();
        } else if (currentMode == Mode.MOVE_PALLET) {
            new UpdateDiiiLocationTask().execute();
        }
    }

    @OnClick(R.id.new_logistics_reset_button)
    public void onResetClick() {
        resetCurrentModeContext(false);
        refreshUi();
    }

    @OnClick(R.id.new_logistics_clear_button)
    public void onClearClick() {
        recentRecordList.clear();
        currentDetailList.clear();
        resetCurrentModeContext(true);
        refreshUi();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (!TextUtils.isEmpty(result.getContents())) {
                parseScan(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void switchMode(Mode mode) {
        currentMode = mode;
        resetCurrentModeContext(true);
        refreshUi();
    }

    // created by code-x John
    // start: 2026-05-06 15:12:18 CST
    // end: 2026-05-06 15:12:18 CST
    private void parseScan(String rawScan) {
        if (isParsingScan) {
            return;
        }

        String scan = normalizeScan(rawScan);
        if (TextUtils.isEmpty(scan)) {
            clearInputAndFocus();
            return;
        }

        if (currentMode == Mode.ACCEPT_MANIFEST) {
            isParsingScan = true;
            new CheckAcceptManifestTask(scan).execute();
            return;
        }

        if (currentMode == Mode.MOVE_MANIFEST) {
            if (currentManifestId <= 0) {
                isParsingScan = true;
                new CheckMoveManifestTask(scan).execute();
            } else if (currentLocation == null) {
                isParsingScan = true;
                new CheckLocationTask(scan).execute();
            } else {
                showError("当前整单和区位都已就绪，请先执行或重置");
            }
            return;
        }

        if (currentMode == Mode.MOVE_PALLET) {
            if (currentDiiiId <= 0) {
                isParsingScan = true;
                new CheckMoveDiiiTask(scan).execute();
            } else if (currentLocation == null) {
                isParsingScan = true;
                new CheckLocationTask(scan).execute();
            } else {
                showError("当前托盘和区位都已就绪，请先执行或重置");
            }
        }
    }

    private void bindManifest(String scan, WsResult wsResult) {
        currentManifestScan = scan;
        currentManifestId = wsResult != null && wsResult.getID() != null ? wsResult.getID() : 0;
        currentManifestInfoJson = wsResult != null ? wsResult.getInfo() : "";
    }

    private void bindDiii(String scan, WsResult wsResult) {
        currentDiiiScan = scan;
        currentDiiiId = wsResult != null && wsResult.getID() != null ? wsResult.getID() : 0;
        currentDiiiInfoJson = wsResult != null ? wsResult.getInfo() : "";
    }

    private void bindLocation(String scan, WsResult wsResult) {
        currentLocationScan = scan;
        currentLocationInfoJson = wsResult != null ? wsResult.getInfo() : "";
        if (wsResult != null && !TextUtils.isEmpty(wsResult.getInfo())) {
            currentLocation = JsonUtil.parseJsonToObject(wsResult.getInfo(), IstPlaceEntity.class);
        } else {
            currentLocation = null;
        }
    }

    private void resetCurrentModeContext(boolean clearAll) {
        clearInputAndFocus();
        currentDetailList.clear();

        if (clearAll) {
            clearManifestContext();
            clearDiiiContext();
            clearLocationContext();
            return;
        }

        if (currentMode == Mode.ACCEPT_MANIFEST) {
            clearManifestContext();
        } else if (currentMode == Mode.MOVE_MANIFEST) {
            clearManifestContext();
            clearLocationContext();
        } else if (currentMode == Mode.MOVE_PALLET) {
            clearDiiiContext();
            clearLocationContext();
        }
    }

    private void clearManifestContext() {
        currentManifestId = 0;
        currentManifestScan = "";
        currentManifestInfoJson = "";
    }

    private void clearDiiiContext() {
        currentDiiiId = 0;
        currentDiiiScan = "";
        currentDiiiInfoJson = "";
    }

    private void clearLocationContext() {
        currentLocation = null;
        currentLocationScan = "";
        currentLocationInfoJson = "";
    }

    private boolean isReadyToExecute() {
        if (currentMode == Mode.ACCEPT_MANIFEST) {
            return currentManifestId > 0;
        }
        if (currentMode == Mode.MOVE_MANIFEST) {
            return currentManifestId > 0 && currentLocation != null;
        }
        return currentDiiiId > 0 && currentLocation != null;
    }

    private void refreshUi() {
        refreshModeButtons();
        actionButton.setText(currentMode.getActionButtonText());
        statusText.setText(buildStatusText());
        bindSummaryFields();
        buildDisplayList();
        focusInput();
    }

    private void refreshModeButtons() {
        acceptButton.setSelected(currentMode == Mode.ACCEPT_MANIFEST);
        moveManifestButton.setSelected(currentMode == Mode.MOVE_MANIFEST);
        movePalletButton.setSelected(currentMode == Mode.MOVE_PALLET);
    }

    private String buildStatusText() {
        StringBuilder builder = new StringBuilder();
        builder.append("当前模式：").append(currentMode.getTitle()).append("\n");
        builder.append("下一步：").append(buildNextStepHint()).append("\n");
        if (currentLocation != null) {
            builder.append("当前区位：").append(safe(currentLocation.getIstName()))
                    .append(" / ").append(currentLocation.getSub_Ist_ID());
        } else {
            builder.append("当前区位：-");
        }
        return builder.toString();
    }

    private String buildNextStepHint() {
        if (currentMode == Mode.ACCEPT_MANIFEST) {
            return currentManifestId > 0 ? "点击执行物流接收" : "先扫描 Manifest 送货单码";
        }
        if (currentMode == Mode.MOVE_MANIFEST) {
            if (currentManifestId <= 0) {
                return "先扫描 Manifest";
            }
            if (currentLocation == null) {
                return "再扫描物流区位码";
            }
            return "点击执行整单移位";
        }
        if (currentDiiiId <= 0) {
            return "先扫描托盘码";
        }
        if (currentLocation == null) {
            return "再扫描物流区位码";
        }
        return "点击执行逐托盘移位";
    }

    private void bindSummaryFields() {
        manifestScanText.setText("Manifest：" + (TextUtils.isEmpty(currentManifestScan) ? "-" : currentManifestScan));
        diiiScanText.setText("托盘：" + (TextUtils.isEmpty(currentDiiiScan) ? "-" : currentDiiiScan));
        locationScanText.setText("物流区位：" + (TextUtils.isEmpty(currentLocationScan) ? "-" : currentLocationScan));

        JsonElement sourceElement = getCurrentSourceJsonElement();

        setLabelValue(supplierText, "供应商", extractValue(sourceElement,
                "Supplier", "SupplierName", "Supplier_Chinese_Name", "SupplierCompany", "SCF_Name", "SendCompany", "CompanyName"));
        setLabelValue(senderAddressText, "发出地址", extractValue(sourceElement,
                "SenderAddress", "SupplierAddress", "Address", "FromAddress", "SendAddress"));
        setLabelValue(senderContactText, "供方联系人", extractValue(sourceElement,
                "SupplierContact", "SenderContact", "LinkMan", "Contact", "SupplierLinkMan", "SenderLinkMan"));
        setLabelValue(senderTelText, "联系电话", extractValue(sourceElement,
                "SupplierTel", "SenderTel", "Telephone", "SupplierTelephone", "SenderTelephone", "Tel"));

        setLabelValue(receiverText, "客户", extractValue(sourceElement,
                "Customer", "CustomerName", "ReceiverCompany", "CF_Name", "ReceiverName", "ToCompany"));
        setLabelValue(receiverAddressText, "接收地址", extractValue(sourceElement,
                "ReceiverAddress", "CustomerAddress", "ToAddress", "DesAddress", "AddressTo"));
        setLabelValue(receiverContactText, "SHB联系人", extractValue(sourceElement,
                "ReceiverContact", "SHBContact", "CustomerContact", "ReceiverLinkMan", "ContactName"));
        setLabelValue(receiverTelText, "联系电话", extractValue(sourceElement,
                "ReceiverTel", "CustomerTel", "ReceiverTelephone", "CustomerTelephone"));

        String deliveryNo = extractValue(sourceElement,
                "TrackNo", "ManifestNo", "Manifest_NO", "DeliveryNo", "Delivery_NO", "DSIM_NO", "ManifestCode");
        if (TextUtils.isEmpty(deliveryNo) && !TextUtils.isEmpty(currentManifestScan)) {
            deliveryNo = currentManifestScan;
        }
        setLabelValue(deliveryNoText, "送货单号", deliveryNo);
        setLabelValue(logisticsCompanyText, "物流公司", extractValue(sourceElement,
                "LogisticsCompany", "LCName", "TransportCompany", "物流公司", "CompanyLogistics"));
        setLabelValue(logisticsTrackNoText, "物流跟踪号", extractValue(sourceElement,
                "LogisticsTrackNO", "LogisticsTrackNo", "WaybillNo", "TrackNo2", "ExpressNO", "LogisticsNo"));
        setLabelValue(logisticsContactText, "物流联系人", extractValue(sourceElement,
                "LogisticsContact", "DriverName", "CarrierContact", "LogisticsLinkMan", "ExpressContact"));
        setLabelValue(logisticsTelText, "联系电话", extractValue(sourceElement,
                "LogisticsTel", "DriverTel", "CarrierTel", "ExpressTel", "Telephone2"));
        setLabelValue(shipTimeText, "发货时间", extractValue(sourceElement,
                "ShipingDate", "ShippingDate", "DeliveryDate", "SendDate", "OutDate"));
        setLabelValue(receiveTimeText, "收货时间", extractValue(sourceElement,
                "ReceiveDate", "ReceiveTime", "ArriveDate", "AcceptDate", "UpdateDate"));
        setLabelValue(hoursText, "运输小时数", extractValue(sourceElement,
                "DayNumber", "Hours", "TransportHours", "LogisticsHours"));
        setLabelValue(remarkText, "说明", extractValue(sourceElement,
                "Remark", "Des", "Description", "LogisticsRemark", "Memo"));
    }

    private void setLabelValue(TextView textView, String label, String value) {
        textView.setText(label + "：" + (TextUtils.isEmpty(value) ? "-" : value));
    }

    private JsonElement getCurrentSourceJsonElement() {
        String json = "";
        if (!TextUtils.isEmpty(currentManifestInfoJson)) {
            json = currentManifestInfoJson;
        } else if (!TextUtils.isEmpty(currentDiiiInfoJson)) {
            json = currentDiiiInfoJson;
        } else if (!TextUtils.isEmpty(currentLocationInfoJson)) {
            json = currentLocationInfoJson;
        }

        if (TextUtils.isEmpty(json)) {
            return null;
        }

        try {
            return new JsonParser().parse(json);
        } catch (Exception e) {
            return null;
        }
    }

    private String extractValue(JsonElement element, String... keys) {
        if (element == null || keys == null) {
            return "";
        }
        for (String key : keys) {
            String value = findValueByKey(element, normalizeKey(key));
            if (!TextUtils.isEmpty(value)) {
                return value;
            }
        }
        return "";
    }

    private String findValueByKey(JsonElement element, String normalizedTargetKey) {
        if (element == null || element.isJsonNull()) {
            return "";
        }
        if (element.isJsonObject()) {
            JsonObject jsonObject = element.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                if (normalizeKey(entry.getKey()).equals(normalizedTargetKey)
                        && entry.getValue() != null
                        && entry.getValue().isJsonPrimitive()) {
                    return entry.getValue().getAsString();
                }
            }
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                String childValue = findValueByKey(entry.getValue(), normalizedTargetKey);
                if (!TextUtils.isEmpty(childValue)) {
                    return childValue;
                }
            }
        } else if (element.isJsonArray()) {
            JsonArray jsonArray = element.getAsJsonArray();
            for (JsonElement child : jsonArray) {
                String childValue = findValueByKey(child, normalizedTargetKey);
                if (!TextUtils.isEmpty(childValue)) {
                    return childValue;
                }
            }
        }
        return "";
    }

    private String normalizeKey(String key) {
        if (key == null) {
            return "";
        }
        return key.replace("_", "").replace(" ", "").toLowerCase();
    }

    private void buildDisplayList() {
        displayList.clear();
        displayList.add(new DisplayEntity("当前状态", currentMode.getTitle(), buildNextStepHint()));

        if (!TextUtils.isEmpty(currentManifestScan)) {
            displayList.add(new DisplayEntity("当前对象", "Manifest：" + currentManifestScan, "DSIM_ID：" + currentManifestId));
        }
        if (!TextUtils.isEmpty(currentDiiiScan)) {
            displayList.add(new DisplayEntity("当前对象", "托盘：" + currentDiiiScan, "DIII_ID：" + currentDiiiId));
        }
        if (currentLocation != null) {
            displayList.add(new DisplayEntity("当前区位", currentLocationScan,
                    "Ist_ID:" + currentLocation.getIst_ID() + "  Sub_Ist_ID:" + currentLocation.getSub_Ist_ID()
                            + "  " + safe(currentLocation.getIstName())));
        }

        displayList.addAll(currentDetailList);
        displayList.addAll(recentRecordList);
        adapter.refresh(displayList);
    }

    private void rebuildCurrentDetails(String title, String json) {
        currentDetailList.clear();
        if (TextUtils.isEmpty(json)) {
            return;
        }
        try {
            JsonElement root = new JsonParser().parse(json);
            collectDetailEntries(title, title, root, currentDetailList);
        } catch (Exception e) {
            currentDetailList.add(new DisplayEntity(title, "原始信息", json));
        }
    }

    private void collectDetailEntries(String groupTitle, String prefix, JsonElement element, List<DisplayEntity> outList) {
        if (element == null || element.isJsonNull()) {
            return;
        }

        if (element.isJsonArray()) {
            JsonArray jsonArray = element.getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonElement child = jsonArray.get(i);
                if (child != null && child.isJsonObject()) {
                    JsonObject object = child.getAsJsonObject();
                    outList.add(new DisplayEntity(groupTitle,
                            buildItemTitle(prefix, i + 1, object),
                            buildItemDetail(object)));
                } else {
                    outList.add(new DisplayEntity(groupTitle,
                            prefix + "[" + (i + 1) + "]",
                            child == null || child.isJsonNull() ? "-" : child.toString()));
                }
            }
            return;
        }

        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            boolean hasArrayChild = false;
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                if (entry.getValue() != null && entry.getValue().isJsonArray()) {
                    hasArrayChild = true;
                    collectDetailEntries(groupTitle, entry.getKey(), entry.getValue(), outList);
                }
            }
            if (!hasArrayChild) {
                outList.add(new DisplayEntity(groupTitle, prefix, buildItemDetail(object)));
            }
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                if (entry.getValue() != null && entry.getValue().isJsonObject()) {
                    collectDetailEntries(groupTitle, entry.getKey(), entry.getValue(), outList);
                }
            }
            return;
        }

        outList.add(new DisplayEntity(groupTitle, prefix, element.getAsString()));
    }

    private String buildItemTitle(String prefix, int index, JsonObject object) {
        String primary = firstNonEmpty(
                getPrimitive(object, "ItemName"),
                getPrimitive(object, "Item_No"),
                getPrimitive(object, "ItemNo"),
                getPrimitive(object, "BoxName"),
                getPrimitive(object, "Pallet_SerialNo"),
                getPrimitive(object, "PalletNo"),
                getPrimitive(object, "DIII_Code"),
                getPrimitive(object, "Code"),
                getPrimitive(object, "LotNo"),
                getPrimitive(object, "Name")
        );
        if (TextUtils.isEmpty(primary)) {
            primary = prefix + "[" + index + "]";
        }
        return primary;
    }

    private String buildItemDetail(JsonObject object) {
        List<String> pairs = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            if (entry.getValue() != null && entry.getValue().isJsonPrimitive()) {
                String value = entry.getValue().getAsString();
                if (!TextUtils.isEmpty(value)) {
                    pairs.add(entry.getKey() + ":" + value);
                }
            }
        }
        if (pairs.isEmpty()) {
            return "-";
        }
        return TextUtils.join(" | ", pairs);
    }

    private String getPrimitive(JsonObject object, String key) {
        if (object == null || TextUtils.isEmpty(key)) {
            return "";
        }
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            if (normalizeKey(entry.getKey()).equals(normalizeKey(key))
                    && entry.getValue() != null
                    && entry.getValue().isJsonPrimitive()) {
                return entry.getValue().getAsString();
            }
        }
        return "";
    }

    private String firstNonEmpty(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (!TextUtils.isEmpty(value)) {
                return value;
            }
        }
        return "";
    }

    private void addRecentRecord(String title, String code, String detail) {
        recentRecordList.add(0, new DisplayEntity("最近操作", title + "：" + code, detail));
        if (recentRecordList.size() > MAX_RECENT_RECORD_COUNT) {
            recentRecordList.remove(recentRecordList.size() - 1);
        }
    }

    private void clearInputAndFocus() {
        inputEdit.setText("");
        focusInput();
    }

    private void focusInput() {
        inputEdit.requestFocus();
        inputEdit.setSelection(inputEdit.getText().length());
    }

    private void showError(String message) {
        isParsingScan = false;
        if (!TextUtils.isEmpty(message)) {
            ToastUtil.showToastShort(message);
        }
        clearInputAndFocus();
    }

    private void showSuccess(String message) {
        isParsingScan = false;
        if (!TextUtils.isEmpty(message)) {
            ToastUtil.showToastShort(message);
        }
        clearInputAndFocus();
    }

    private String safe(String text) {
        return TextUtils.isEmpty(text) ? "-" : text;
    }

    private String normalizeScan(String rawScan) {
        if (rawScan == null) {
            return "";
        }
        return rawScan.replace("\n", "").replace("\r", "").trim();
    }

    private WsResult ensureWsResult(WsResult wsResult, String defaultError) {
        if (wsResult != null) {
            return wsResult;
        }
        WsResult result = new WsResult();
        result.setResult(false);
        result.setErrorInfo(defaultError);
        return result;
    }

    private class CheckAcceptManifestTask extends AsyncTask<String, Void, WsResult> {
        private final String scan;

        CheckAcceptManifestTask(String scan) {
            this.scan = scan;
        }

        @Override
        protected WsResult doInBackground(String... strings) {
            return ensureWsResult(WebServiceUtil.opCheckLogisticsSupplierManifestBarcode(scan), "Manifest 校验无返回结果");
        }

        @Override
        protected void onPostExecute(WsResult wsResult) {
            if (!wsResult.getResult()) {
                showError(wsResult.getErrorInfo());
                return;
            }
            bindManifest(scan, wsResult);
            rebuildCurrentDetails("明细清单", wsResult.getInfo());
            showSuccess(wsResult.getErrorInfo());
            refreshUi();
        }
    }

    private class AcceptManifestTask extends AsyncTask<Void, Void, WsResult> {
        @Override
        protected WsResult doInBackground(Void... voids) {
            return ensureWsResult(WebServiceUtil.opLogisAcceptSupplierManifest(
                    currentManifestId,
                    UserSingleton.get().getHRID(),
                    UserSingleton.get().getHRName()
            ), "物流接收无返回结果");
        }

        @Override
        protected void onPostExecute(WsResult wsResult) {
            if (!wsResult.getResult()) {
                showError(wsResult.getErrorInfo());
                return;
            }
            addRecentRecord("物流接收", currentManifestScan, safe(wsResult.getErrorInfo()));
            clearManifestContext();
            currentDetailList.clear();
            showSuccess(wsResult.getErrorInfo());
            refreshUi();
        }
    }

    private class CheckMoveManifestTask extends AsyncTask<String, Void, WsResult> {
        private final String scan;

        CheckMoveManifestTask(String scan) {
            this.scan = scan;
        }

        @Override
        protected WsResult doInBackground(String... strings) {
            return ensureWsResult(WebServiceUtil.opCheckLogisticsAreaManifestBarcode(scan), "物流区整单校验无返回结果");
        }

        @Override
        protected void onPostExecute(WsResult wsResult) {
            if (!wsResult.getResult()) {
                showError(wsResult.getErrorInfo());
                return;
            }
            bindManifest(scan, wsResult);
            rebuildCurrentDetails("明细清单", wsResult.getInfo());
            showSuccess(wsResult.getErrorInfo());
            refreshUi();
        }
    }

    private class CheckMoveDiiiTask extends AsyncTask<String, Void, WsResult> {
        private final String scan;

        CheckMoveDiiiTask(String scan) {
            this.scan = scan;
        }

        @Override
        protected WsResult doInBackground(String... strings) {
            return ensureWsResult(WebServiceUtil.opCheckLogisticsAreaDiiiBarcode(scan), "物流区托盘校验无返回结果");
        }

        @Override
        protected void onPostExecute(WsResult wsResult) {
            if (!wsResult.getResult()) {
                showError(wsResult.getErrorInfo());
                return;
            }
            bindDiii(scan, wsResult);
            rebuildCurrentDetails("明细清单", wsResult.getInfo());
            showSuccess(wsResult.getErrorInfo());
            refreshUi();
        }
    }

    private class CheckLocationTask extends AsyncTask<String, Void, WsResult> {
        private final String scan;

        CheckLocationTask(String scan) {
            this.scan = scan;
        }

        @Override
        protected WsResult doInBackground(String... strings) {
            return ensureWsResult(WebServiceUtil.opCheckLogisticsAreaLocationBarcode(scan), "物流区位校验无返回结果");
        }

        @Override
        protected void onPostExecute(WsResult wsResult) {
            if (!wsResult.getResult()) {
                showError(wsResult.getErrorInfo());
                return;
            }
            bindLocation(scan, wsResult);
            showSuccess(wsResult.getErrorInfo());
            refreshUi();
        }
    }

    private class UpdateManifestLocationTask extends AsyncTask<Void, Void, WsResult> {
        @Override
        protected WsResult doInBackground(Void... voids) {
            return ensureWsResult(WebServiceUtil.opUpdateLogisticsAreaManifestLocation(
                    currentManifestId,
                    currentLocation != null ? currentLocation.getIst_ID() : 0,
                    currentLocation != null ? currentLocation.getSub_Ist_ID() : 0
            ), "整单移位无返回结果");
        }

        @Override
        protected void onPostExecute(WsResult wsResult) {
            if (!wsResult.getResult()) {
                showError(wsResult.getErrorInfo());
                return;
            }
            addRecentRecord("整单移位", currentManifestScan, safe(currentLocation != null ? currentLocation.getIstName() : ""));
            clearManifestContext();
            currentDetailList.clear();
            showSuccess(wsResult.getErrorInfo());
            refreshUi();
        }
    }

    private class UpdateDiiiLocationTask extends AsyncTask<Void, Void, WsResult> {
        @Override
        protected WsResult doInBackground(Void... voids) {
            return ensureWsResult(WebServiceUtil.opUpdateLogisticsAreaDiiiLocation(
                    currentDiiiId,
                    currentLocation != null ? currentLocation.getIst_ID() : 0,
                    currentLocation != null ? currentLocation.getSub_Ist_ID() : 0
            ), "逐托盘移位无返回结果");
        }

        @Override
        protected void onPostExecute(WsResult wsResult) {
            if (!wsResult.getResult()) {
                showError(wsResult.getErrorInfo());
                return;
            }
            addRecentRecord("逐托盘移位", currentDiiiScan, safe(currentLocation != null ? currentLocation.getIstName() : ""));
            clearDiiiContext();
            currentDetailList.clear();
            showSuccess(wsResult.getErrorInfo());
            refreshUi();
        }
    }

    private enum Mode {
        ACCEPT_MANIFEST("物流接收", "请先扫描 Manifest 后执行接收", "执行物流接收"),
        MOVE_MANIFEST("整单移位", "请先扫描 Manifest 和物流区位", "执行整单移位"),
        MOVE_PALLET("逐托盘移位", "请先扫描托盘和物流区位", "执行逐托盘移位");

        private final String title;
        private final String actionHint;
        private final String actionButtonText;

        Mode(String title, String actionHint, String actionButtonText) {
            this.title = title;
            this.actionHint = actionHint;
            this.actionButtonText = actionButtonText;
        }

        public String getTitle() {
            return title;
        }

        public String getActionHint() {
            return actionHint;
        }

        public String getActionButtonText() {
            return actionButtonText;
        }
    }

    private static class DisplayEntity {
        private final String groupTitle;
        private final String code;
        private final String detail;

        DisplayEntity(String groupTitle, String code, String detail) {
            this.groupTitle = groupTitle;
            this.code = code;
            this.detail = detail;
        }
    }

    private static class ScanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;

        private final android.content.Context context;
        private final List<Object> groupedDisplayList = new ArrayList<>();

        ScanAdapter(android.content.Context context, List<DisplayEntity> list) {
            this.context = context;
            buildDisplayList(list);
        }

        void refresh(List<DisplayEntity> list) {
            buildDisplayList(list);
            notifyDataSetChanged();
        }

        private void buildDisplayList(List<DisplayEntity> list) {
            groupedDisplayList.clear();
            Map<String, List<DisplayEntity>> groupedMap = new LinkedHashMap<>();
            for (DisplayEntity entity : list) {
                List<DisplayEntity> groupList = groupedMap.get(entity.groupTitle);
                if (groupList == null) {
                    groupList = new ArrayList<>();
                    groupedMap.put(entity.groupTitle, groupList);
                }
                groupList.add(entity);
            }
            for (Map.Entry<String, List<DisplayEntity>> entry : groupedMap.entrySet()) {
                groupedDisplayList.add(entry.getKey());
                groupedDisplayList.addAll(entry.getValue());
            }
        }

        @Override
        public int getItemViewType(int position) {
            return groupedDisplayList.get(position) instanceof String ? TYPE_HEADER : TYPE_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER) {
                View view = LayoutInflater.from(context).inflate(R.layout.item_section_header, parent, false);
                return new HeaderHolder(view);
            }
            View view = LayoutInflater.from(context).inflate(R.layout.item_scan_code, parent, false);
            return new ItemHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof HeaderHolder) {
                ((HeaderHolder) holder).headerText.setText((String) groupedDisplayList.get(position));
                return;
            }

            DisplayEntity entity = (DisplayEntity) groupedDisplayList.get(position);
            ((ItemHolder) holder).codeText.setText(entity.code);
            if (TextUtils.isEmpty(entity.detail)) {
                ((ItemHolder) holder).detailText.setVisibility(View.GONE);
            } else {
                ((ItemHolder) holder).detailText.setVisibility(View.VISIBLE);
                ((ItemHolder) holder).detailText.setText(entity.detail);
            }
        }

        @Override
        public int getItemCount() {
            return groupedDisplayList.size();
        }
    }

    private static class HeaderHolder extends RecyclerView.ViewHolder {
        TextView headerText;

        HeaderHolder(View itemView) {
            super(itemView);
            headerText = itemView.findViewById(R.id.tv_header);
        }
    }

    private static class ItemHolder extends RecyclerView.ViewHolder {
        TextView codeText;
        TextView detailText;

        ItemHolder(View itemView) {
            super(itemView);
            codeText = itemView.findViewById(R.id.tv_code);
            detailText = itemView.findViewById(R.id.tv_detail);
        }
    }
}
