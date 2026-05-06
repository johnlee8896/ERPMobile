package com.chinashb.www.mobileerp.shipment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.JsonUtil;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 * @date 创建时间 2026/5/2 14:12
 * @author 作者: code-x John
 * @description 外贸发运扫码管理
 */
public class AllShipmentAccountDeliveryManagement extends BaseActivity {

    // created by code-x John
    // start: 2026-05-02 14:12:08 CST
    // end: 2026-05-02 14:12:08 CST
    private static final int INPUT_MIN_LENGTH = 3;
    private static final int MAX_RECENT_RECORD_COUNT = 40;

    @BindView(R.id.shipment_attach_shipment_container_button)
    Button attachShipmentContainerButton;
    @BindView(R.id.shipment_detach_shipment_container_button)
    Button detachShipmentContainerButton;
    @BindView(R.id.shipment_attach_container_pallet_button)
    Button attachContainerPalletButton;
    @BindView(R.id.shipment_detach_container_pallet_button)
    Button detachContainerPalletButton;
    @BindView(R.id.shipment_add_pallet_item_button)
    Button addPalletItemButton;
    @BindView(R.id.shipment_remove_pallet_item_button)
    Button removePalletItemButton;
    @BindView(R.id.shipment_status_text)
    TextView statusText;
    @BindView(R.id.shipment_input_edit)
    EditText inputEdit;
    @BindView(R.id.shipment_camera_scan_button)
    Button cameraScanButton;
    @BindView(R.id.shipment_scan_list)
    CustomRecyclerView scanListView;
    @BindView(R.id.shipment_reset_target_button)
    Button resetTargetButton;
    @BindView(R.id.shipment_clear_records_button)
    Button clearRecordsButton;

    private final List<ShipmentDisplayEntity> scanList = new ArrayList<>();
    private final List<ShipmentDisplayEntity> recentRecordList = new ArrayList<>();
    private final LinkedHashMap<String, List<String>> shipmentContainerMap = new LinkedHashMap<>();
    private final LinkedHashMap<String, List<String>> containerPalletMap = new LinkedHashMap<>();
    private final LinkedHashMap<String, List<String>> palletItemMap = new LinkedHashMap<>();

    private ShipmentScanAdapter adapter;
    private ShipmentMode currentMode = ShipmentMode.SHIPMENT_ATTACH_CONTAINER;
    private boolean isParsingScan;

    private String currentShipmentScan = "";
    private TradeShipmentBean currentShipmentBean;

    private String currentContainerScan = "";
    private TradeContainerBean currentContainerBean;

    private String currentPalletScan = "";
    private TradePalletBean currentPalletBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_shipment_account_delivery_management);
        ButterKnife.bind(this);

        initRecyclerView();
        initListener();
        refreshUi();
    }

    private void initRecyclerView() {
        adapter = new ShipmentScanAdapter(this, scanList);
        scanListView.setAdapter(adapter);
    }

    private void initListener() {
        inputEdit.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                if (editable != null && editable.toString().trim().length() > INPUT_MIN_LENGTH) {
                    parseScan(editable.toString());
                }
            }
        });
    }

    @OnClick({
            R.id.shipment_attach_shipment_container_button,
            R.id.shipment_detach_shipment_container_button,
            R.id.shipment_attach_container_pallet_button,
            R.id.shipment_detach_container_pallet_button,
            R.id.shipment_add_pallet_item_button,
            R.id.shipment_remove_pallet_item_button
    })
    public void onModeButtonClick(View view) {
        switch (view.getId()) {
            case R.id.shipment_attach_shipment_container_button:
                switchMode(ShipmentMode.SHIPMENT_ATTACH_CONTAINER);
                break;
            case R.id.shipment_detach_shipment_container_button:
                switchMode(ShipmentMode.SHIPMENT_DETACH_CONTAINER);
                break;
            case R.id.shipment_attach_container_pallet_button:
                switchMode(ShipmentMode.CONTAINER_ATTACH_PALLET);
                break;
            case R.id.shipment_detach_container_pallet_button:
                switchMode(ShipmentMode.CONTAINER_DETACH_PALLET);
                break;
            case R.id.shipment_add_pallet_item_button:
                switchMode(ShipmentMode.PALLET_ADD_ITEM);
                break;
            case R.id.shipment_remove_pallet_item_button:
                switchMode(ShipmentMode.PALLET_REMOVE_ITEM);
                break;
            default:
                break;
        }
    }

    @OnClick(R.id.shipment_camera_scan_button)
    public void onCameraScanClick() {
        new IntentIntegrator(this)
                .setCaptureActivity(CustomScannerActivity.class)
                .initiateScan();
    }

    @OnClick(R.id.shipment_reset_target_button)
    public void onResetTargetClick() {
        resetCurrentTargetForMode();
        refreshUi();
        focusInput();
    }

    @OnClick(R.id.shipment_clear_records_button)
    public void onClearRecordsClick() {
        clearCurrentModeRecords();
        refreshUi();
        focusInput();
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

    private void switchMode(ShipmentMode mode) {
        currentMode = mode;
        refreshUi();
        focusInput();
    }

    // created by code-x John
    // start: 2026-05-02 14:12:08 CST
    // end: 2026-05-02 14:12:08 CST
    private void parseScan(String rawScan) {
        if (isParsingScan) {
            return;
        }

        String scan = normalizeScan(rawScan);
        if (TextUtils.isEmpty(scan)) {
            return;
        }

        switch (currentMode) {
            case SHIPMENT_ATTACH_CONTAINER:
            case SHIPMENT_DETACH_CONTAINER:
                handleShipmentContainerScan(scan);
                break;
            case CONTAINER_ATTACH_PALLET:
            case CONTAINER_DETACH_PALLET:
                handleContainerPalletScan(scan);
                break;
            case PALLET_ADD_ITEM:
            case PALLET_REMOVE_ITEM:
                handlePalletItemScan(scan);
                break;
            default:
                break;
        }
    }

    private void handleShipmentContainerScan(String scan) {
        if (TextUtils.isEmpty(currentShipmentScan) || isShipmentLabel(scan)) {
            isParsingScan = true;
            new ValidateShipmentTask(scan).execute();
            return;
        }

        isParsingScan = true;
        new ShipmentContainerActionTask(scan, currentMode == ShipmentMode.SHIPMENT_ATTACH_CONTAINER).execute();
    }

    private void handleContainerPalletScan(String scan) {
        if (TextUtils.isEmpty(currentContainerScan) || isContainerLabel(scan)) {
            isParsingScan = true;
            new ValidateContainerTask(scan).execute();
            return;
        }

        isParsingScan = true;
        new ContainerPalletActionTask(scan, currentMode == ShipmentMode.CONTAINER_ATTACH_PALLET).execute();
    }

    private void handlePalletItemScan(String scan) {
        if (TextUtils.isEmpty(currentPalletScan) || isPalletLabel(scan)) {
            isParsingScan = true;
            new ValidatePalletTask(scan).execute();
            return;
        }

        isParsingScan = true;
        new PalletItemActionTask(scan, currentMode == ShipmentMode.PALLET_ADD_ITEM).execute();
    }

    private void bindShipment(String scan, TradeShipmentBean shipmentBean) {
        currentShipmentScan = scan;
        currentShipmentBean = shipmentBean;
        ensureGroupExists(shipmentContainerMap, scan);
    }

    private void bindContainer(String scan, TradeContainerBean containerBean) {
        currentContainerScan = scan;
        currentContainerBean = containerBean;
        ensureGroupExists(containerPalletMap, scan);
    }

    private void bindPallet(String scan, TradePalletBean palletBean) {
        currentPalletScan = scan;
        currentPalletBean = palletBean;
        ensureGroupExists(palletItemMap, scan);
    }

    private void resetCurrentTargetForMode() {
        switch (currentMode) {
            case SHIPMENT_ATTACH_CONTAINER:
            case SHIPMENT_DETACH_CONTAINER:
                currentShipmentScan = "";
                currentShipmentBean = null;
                break;
            case CONTAINER_ATTACH_PALLET:
            case CONTAINER_DETACH_PALLET:
                currentContainerScan = "";
                currentContainerBean = null;
                break;
            case PALLET_ADD_ITEM:
            case PALLET_REMOVE_ITEM:
                currentPalletScan = "";
                currentPalletBean = null;
                break;
            default:
                break;
        }
        clearInputAndFocus();
    }

    private void clearCurrentModeRecords() {
        switch (currentMode) {
            case SHIPMENT_ATTACH_CONTAINER:
            case SHIPMENT_DETACH_CONTAINER:
                if (!TextUtils.isEmpty(currentShipmentScan)) {
                    shipmentContainerMap.remove(currentShipmentScan);
                }
                break;
            case CONTAINER_ATTACH_PALLET:
            case CONTAINER_DETACH_PALLET:
                if (!TextUtils.isEmpty(currentContainerScan)) {
                    containerPalletMap.remove(currentContainerScan);
                }
                break;
            case PALLET_ADD_ITEM:
            case PALLET_REMOVE_ITEM:
                if (!TextUtils.isEmpty(currentPalletScan)) {
                    palletItemMap.remove(currentPalletScan);
                }
                break;
            default:
                break;
        }
        if (!recentRecordList.isEmpty()) {
            recentRecordList.clear();
        }
    }

    private void refreshUi() {
        statusText.setText(buildStatusText());
        refreshModeButtons();
        buildDisplayList();
        clearInputAndFocus();
    }

    private void refreshModeButtons() {
        attachShipmentContainerButton.setSelected(currentMode == ShipmentMode.SHIPMENT_ATTACH_CONTAINER);
        detachShipmentContainerButton.setSelected(currentMode == ShipmentMode.SHIPMENT_DETACH_CONTAINER);
        attachContainerPalletButton.setSelected(currentMode == ShipmentMode.CONTAINER_ATTACH_PALLET);
        detachContainerPalletButton.setSelected(currentMode == ShipmentMode.CONTAINER_DETACH_PALLET);
        addPalletItemButton.setSelected(currentMode == ShipmentMode.PALLET_ADD_ITEM);
        removePalletItemButton.setSelected(currentMode == ShipmentMode.PALLET_REMOVE_ITEM);
    }

    private String buildStatusText() {
        StringBuilder builder = new StringBuilder();
        builder.append("当前模式：").append(currentMode.getModeTitle()).append("\n");
        builder.append("当前发运单：").append(TextUtils.isEmpty(currentShipmentScan) ? "-" : currentShipmentScan).append("\n");
        builder.append("当前集装箱：").append(TextUtils.isEmpty(currentContainerScan) ? "-" : currentContainerScan).append("\n");
        builder.append("当前托盘：").append(TextUtils.isEmpty(currentPalletScan) ? "-" : currentPalletScan).append("\n");
        builder.append("下一步：");
        if (hasPrimaryTargetForMode()) {
            builder.append(currentMode.getWaitingSecondaryHint());
        } else {
            builder.append(currentMode.getWaitingPrimaryHint());
        }
        return builder.toString();
    }

    private void buildDisplayList() {
        scanList.clear();
        scanList.add(new ShipmentDisplayEntity("当前模式", currentMode.getModeTitle(), hasPrimaryTargetForMode()
                ? currentMode.getWaitingSecondaryHint()
                : currentMode.getWaitingPrimaryHint()));

        appendCurrentTargetGroup();
        appendCurrentRelationGroup();
        appendRecentRecordGroup();

        adapter.refresh(scanList);
    }

    private void appendCurrentTargetGroup() {
        switch (currentMode) {
            case SHIPMENT_ATTACH_CONTAINER:
            case SHIPMENT_DETACH_CONTAINER:
                if (!TextUtils.isEmpty(currentShipmentScan) && currentShipmentBean != null) {
                    scanList.add(new ShipmentDisplayEntity("当前目标", currentShipmentScan, formatShipmentDetail(currentShipmentBean)));
                }
                break;
            case CONTAINER_ATTACH_PALLET:
            case CONTAINER_DETACH_PALLET:
                if (!TextUtils.isEmpty(currentContainerScan) && currentContainerBean != null) {
                    scanList.add(new ShipmentDisplayEntity("当前目标", currentContainerScan, formatContainerDetail(currentContainerBean)));
                }
                break;
            case PALLET_ADD_ITEM:
            case PALLET_REMOVE_ITEM:
                if (!TextUtils.isEmpty(currentPalletScan) && currentPalletBean != null) {
                    scanList.add(new ShipmentDisplayEntity("当前目标", currentPalletScan, formatPalletDetail(currentPalletBean)));
                }
                break;
            default:
                break;
        }
    }

    private void appendCurrentRelationGroup() {
        String primaryScan = getCurrentPrimaryScanForMode();
        if (TextUtils.isEmpty(primaryScan)) {
            return;
        }

        List<String> relationList = getCurrentRelationList();
        if (relationList == null || relationList.isEmpty()) {
            scanList.add(new ShipmentDisplayEntity(currentMode.getRelationGroupTitle(), "暂无记录", ""));
            return;
        }

        for (String scan : relationList) {
            scanList.add(new ShipmentDisplayEntity(currentMode.getRelationGroupTitle(), scan,
                    currentMode.getSecondaryName() + "已" + currentMode.getActionName()));
        }
    }

    private void appendRecentRecordGroup() {
        if (recentRecordList.isEmpty()) {
            return;
        }
        scanList.addAll(recentRecordList);
    }

    private List<String> getCurrentRelationList() {
        switch (currentMode) {
            case SHIPMENT_ATTACH_CONTAINER:
            case SHIPMENT_DETACH_CONTAINER:
                return shipmentContainerMap.get(currentShipmentScan);
            case CONTAINER_ATTACH_PALLET:
            case CONTAINER_DETACH_PALLET:
                return containerPalletMap.get(currentContainerScan);
            case PALLET_ADD_ITEM:
            case PALLET_REMOVE_ITEM:
                return palletItemMap.get(currentPalletScan);
            default:
                return null;
        }
    }

    private boolean hasPrimaryTargetForMode() {
        return !TextUtils.isEmpty(getCurrentPrimaryScanForMode());
    }

    private String getCurrentPrimaryScanForMode() {
        switch (currentMode) {
            case SHIPMENT_ATTACH_CONTAINER:
            case SHIPMENT_DETACH_CONTAINER:
                return currentShipmentScan;
            case CONTAINER_ATTACH_PALLET:
            case CONTAINER_DETACH_PALLET:
                return currentContainerScan;
            case PALLET_ADD_ITEM:
            case PALLET_REMOVE_ITEM:
                return currentPalletScan;
            default:
                return "";
        }
    }

    private void addRecentRecord(String title, String code, String detail) {
        recentRecordList.add(0, new ShipmentDisplayEntity("最近操作", title + "：" + code, detail));
        if (recentRecordList.size() > MAX_RECENT_RECORD_COUNT) {
            recentRecordList.remove(recentRecordList.size() - 1);
        }
    }

    private void updateRelationMap(LinkedHashMap<String, List<String>> map, String primaryScan, String secondaryScan, boolean add) {
        ensureGroupExists(map, primaryScan);
        List<String> relationList = map.get(primaryScan);
        if (relationList == null) {
            return;
        }

        if (add) {
            if (!relationList.contains(secondaryScan)) {
                relationList.add(secondaryScan);
            }
        } else {
            relationList.remove(secondaryScan);
        }
    }

    private void ensureGroupExists(LinkedHashMap<String, List<String>> map, String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        if (!map.containsKey(key)) {
            map.put(key, new ArrayList<String>());
        }
    }

    private String normalizeScan(String rawScan) {
        if (rawScan == null) {
            return "";
        }
        return rawScan.replace("\n", "").replace("\r", "").trim();
    }

    private boolean isShipmentLabel(String scan) {
        return startsWithIgnoreCase(scan, "TS/");
    }

    private boolean isContainerLabel(String scan) {
        return startsWithIgnoreCase(scan, "TC/");
    }

    private boolean isPalletLabel(String scan) {
        return startsWithIgnoreCase(scan, "PT/");
    }

    private boolean startsWithIgnoreCase(String source, String prefix) {
        if (TextUtils.isEmpty(source) || TextUtils.isEmpty(prefix) || source.length() < prefix.length()) {
            return false;
        }
        return source.substring(0, prefix.length()).equalsIgnoreCase(prefix);
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
        if (!TextUtils.isEmpty(message)) {
            ToastUtil.showToastShort(message);
        }
        clearInputAndFocus();
    }

    private void showSuccess(String message) {
        if (!TextUtils.isEmpty(message)) {
            ToastUtil.showToastShort(message);
        }
        clearInputAndFocus();
    }

    private WsResult ensureWsResult(WsResult wsResult, String emptyMessage) {
        if (wsResult != null) {
            return wsResult;
        }
        WsResult fallback = new WsResult();
        fallback.setResult(false);
        fallback.setErrorInfo(emptyMessage);
        return fallback;
    }

    private TradeShipmentBean parseShipmentBean(WsResult wsResult) {
        if (wsResult == null || TextUtils.isEmpty(wsResult.getInfo())) {
            return null;
        }
        return JsonUtil.parseJsonToObject(wsResult.getInfo(), TradeShipmentBean.class);
    }

    private TradeContainerBean parseContainerBean(WsResult wsResult) {
        if (wsResult == null || TextUtils.isEmpty(wsResult.getInfo())) {
            return null;
        }
        return JsonUtil.parseJsonToObject(wsResult.getInfo(), TradeContainerBean.class);
    }

    private TradePalletBean parsePalletBean(WsResult wsResult) {
        if (wsResult == null || TextUtils.isEmpty(wsResult.getInfo())) {
            return null;
        }
        return JsonUtil.parseJsonToObject(wsResult.getInfo(), TradePalletBean.class);
    }

    private String formatShipmentDetail(TradeShipmentBean bean) {
        return "TrackNo:" + safe(bean.getTrackNo())
                + "  Delivery_ID:" + bean.getDelivery_ID()
                + "  日期:" + safe(bean.getDelivery_Date());
    }

    private String formatContainerDetail(TradeContainerBean bean) {
        return "箱号:" + safe(bean.getContainer_No())
                + "  规格:" + safe(bean.getContainer_Type())
                + "  Seal:" + safe(bean.getSeal_No());
    }

    private String formatPalletDetail(TradePalletBean bean) {
        return "托盘号:" + safe(bean.getPallet_SerialNo())
                + "  已检:" + (bean.isChecked() ? "是" : "否")
                + "  可发运:" + (bean.isLoadForDelivery() ? "是" : "否");
    }

    private String safe(String value) {
        return TextUtils.isEmpty(value) ? "-" : value;
    }

    private class ShipmentTaskResult {
        WsResult wsResult;
        TradeShipmentBean shipmentBean;
        TradeContainerBean containerBean;
        TradePalletBean palletBean;
        String scan;
    }

    // created by code-x John
    // start: 2026-05-02 14:12:08 CST
    // end: 2026-05-02 14:12:08 CST
    private class ValidateShipmentTask extends AsyncTask<String, Void, ShipmentTaskResult> {
        private final String scan;

        ValidateShipmentTask(String scan) {
            this.scan = scan;
        }

        @Override
        protected ShipmentTaskResult doInBackground(String... strings) {
            ShipmentTaskResult result = new ShipmentTaskResult();
            result.scan = scan;
            try {
                result.wsResult = ensureWsResult(WebServiceUtil.opCheckTradeShipmentLabel(scan), "发运单校验无返回结果");
                if (result.wsResult.getResult()) {
                    result.shipmentBean = parseShipmentBean(result.wsResult);
                }
            } catch (Exception e) {
                result.wsResult = new WsResult();
                result.wsResult.setResult(false);
                result.wsResult.setErrorInfo("发运单标签解析失败：" + e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(ShipmentTaskResult result) {
            isParsingScan = false;
            if (result.wsResult != null && result.wsResult.getResult() && result.shipmentBean != null) {
                bindShipment(result.scan, result.shipmentBean);
                refreshUi();
                showSuccess("发运单校验成功：" + result.scan);
            } else {
                showError(result.wsResult == null ? "发运单校验失败" : result.wsResult.getErrorInfo());
            }
        }
    }

    // created by code-x John
    // start: 2026-05-02 14:12:08 CST
    // end: 2026-05-02 14:12:08 CST
    private class ValidateContainerTask extends AsyncTask<String, Void, ShipmentTaskResult> {
        private final String scan;

        ValidateContainerTask(String scan) {
            this.scan = scan;
        }

        @Override
        protected ShipmentTaskResult doInBackground(String... strings) {
            ShipmentTaskResult result = new ShipmentTaskResult();
            result.scan = scan;
            try {
                result.wsResult = ensureWsResult(WebServiceUtil.opCheckTradeContainerLabel(scan), "集装箱校验无返回结果");
                if (result.wsResult.getResult()) {
                    result.containerBean = parseContainerBean(result.wsResult);
                }
            } catch (Exception e) {
                result.wsResult = new WsResult();
                result.wsResult.setResult(false);
                result.wsResult.setErrorInfo("集装箱标签解析失败：" + e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(ShipmentTaskResult result) {
            isParsingScan = false;
            if (result.wsResult != null && result.wsResult.getResult() && result.containerBean != null) {
                bindContainer(result.scan, result.containerBean);
                refreshUi();
                showSuccess("集装箱校验成功：" + result.scan);
            } else {
                showError(result.wsResult == null ? "集装箱校验失败" : result.wsResult.getErrorInfo());
            }
        }
    }

    // created by code-x John
    // start: 2026-05-02 14:12:08 CST
    // end: 2026-05-02 14:12:08 CST
    private class ValidatePalletTask extends AsyncTask<String, Void, ShipmentTaskResult> {
        private final String scan;

        ValidatePalletTask(String scan) {
            this.scan = scan;
        }

        @Override
        protected ShipmentTaskResult doInBackground(String... strings) {
            ShipmentTaskResult result = new ShipmentTaskResult();
            result.scan = scan;
            try {
                result.wsResult = ensureWsResult(WebServiceUtil.opCheckTradePalletLabel(scan), "托盘校验无返回结果");
                if (result.wsResult.getResult()) {
                    result.palletBean = parsePalletBean(result.wsResult);
                }
            } catch (Exception e) {
                result.wsResult = new WsResult();
                result.wsResult.setResult(false);
                result.wsResult.setErrorInfo("托盘标签解析失败：" + e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(ShipmentTaskResult result) {
            isParsingScan = false;
            if (result.wsResult != null && result.wsResult.getResult() && result.palletBean != null) {
                bindPallet(result.scan, result.palletBean);
                refreshUi();
                showSuccess("托盘校验成功：" + result.scan);
            } else {
                showError(result.wsResult == null ? "托盘校验失败" : result.wsResult.getErrorInfo());
            }
        }
    }

    // created by code-x John
    // start: 2026-05-02 14:12:08 CST
    // end: 2026-05-02 14:12:08 CST
    private class ShipmentContainerActionTask extends AsyncTask<String, Void, ShipmentTaskResult> {
        private final String containerScan;
        private final boolean attach;

        ShipmentContainerActionTask(String containerScan, boolean attach) {
            this.containerScan = containerScan;
            this.attach = attach;
        }

        @Override
        protected ShipmentTaskResult doInBackground(String... strings) {
            ShipmentTaskResult result = new ShipmentTaskResult();
            result.scan = containerScan;
            try {
                WsResult checkResult = ensureWsResult(WebServiceUtil.opCheckTradeContainerLabel(containerScan), "集装箱校验无返回结果");
                if (!checkResult.getResult()) {
                    result.wsResult = checkResult;
                    return result;
                }
                result.containerBean = parseContainerBean(checkResult);
                result.wsResult = ensureWsResult(
                        attach
                                ? WebServiceUtil.opTradeLabelAttachShipmentContainer(UserSingleton.get().getHRID(), UserSingleton.get().getHRName(), currentShipmentScan, containerScan)
                                : WebServiceUtil.opTradeLabelDetachShipmentContainer(UserSingleton.get().getHRID(), UserSingleton.get().getHRName(), currentShipmentScan, containerScan),
                        attach ? "发运单装入集装箱无返回结果" : "发运单移出集装箱无返回结果");
            } catch (Exception e) {
                result.wsResult = new WsResult();
                result.wsResult.setResult(false);
                result.wsResult.setErrorInfo("集装箱操作失败：" + e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(ShipmentTaskResult result) {
            isParsingScan = false;
            if (result.wsResult != null && result.wsResult.getResult()) {
                if (result.containerBean != null) {
                    bindContainer(result.scan, result.containerBean);
                }
                updateRelationMap(shipmentContainerMap, currentShipmentScan, result.scan, attach);
                addRecentRecord(attach ? "发运单装箱" : "发运单移箱", result.scan,
                        "目标发运单：" + currentShipmentScan);
                refreshUi();
                showSuccess((attach ? "集装箱装入成功：" : "集装箱移出成功：") + result.scan);
            } else {
                showError(result.wsResult == null ? "集装箱操作失败" : result.wsResult.getErrorInfo());
            }
        }
    }

    // created by code-x John
    // start: 2026-05-02 14:12:08 CST
    // end: 2026-05-02 14:12:08 CST
    private class ContainerPalletActionTask extends AsyncTask<String, Void, ShipmentTaskResult> {
        private final String palletScan;
        private final boolean attach;

        ContainerPalletActionTask(String palletScan, boolean attach) {
            this.palletScan = palletScan;
            this.attach = attach;
        }

        @Override
        protected ShipmentTaskResult doInBackground(String... strings) {
            ShipmentTaskResult result = new ShipmentTaskResult();
            result.scan = palletScan;
            try {
                WsResult checkResult = ensureWsResult(WebServiceUtil.opCheckTradePalletLabel(palletScan), "托盘校验无返回结果");
                if (!checkResult.getResult()) {
                    result.wsResult = checkResult;
                    return result;
                }
                result.palletBean = parsePalletBean(checkResult);
                result.wsResult = ensureWsResult(
                        attach
                                ? WebServiceUtil.opTradeLabelAttachContainerPallet(UserSingleton.get().getHRID(), UserSingleton.get().getHRName(), currentContainerScan, palletScan)
                                : WebServiceUtil.opTradeLabelDetachContainerPallet(UserSingleton.get().getHRID(), UserSingleton.get().getHRName(), currentContainerScan, palletScan),
                        attach ? "集装箱装入托盘无返回结果" : "集装箱移出托盘无返回结果");
            } catch (Exception e) {
                result.wsResult = new WsResult();
                result.wsResult.setResult(false);
                result.wsResult.setErrorInfo("托盘操作失败：" + e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(ShipmentTaskResult result) {
            isParsingScan = false;
            if (result.wsResult != null && result.wsResult.getResult()) {
                if (result.palletBean != null) {
                    bindPallet(result.scan, result.palletBean);
                }
                updateRelationMap(containerPalletMap, currentContainerScan, result.scan, attach);
                addRecentRecord(attach ? "集装箱装托" : "集装箱移托", result.scan,
                        "目标集装箱：" + currentContainerScan);
                refreshUi();
                showSuccess((attach ? "托盘装入成功：" : "托盘移出成功：") + result.scan);
            } else {
                showError(result.wsResult == null ? "托盘操作失败" : result.wsResult.getErrorInfo());
            }
        }
    }

    // created by code-x John
    // start: 2026-05-02 14:12:08 CST
    // end: 2026-05-02 14:12:08 CST
    private class PalletItemActionTask extends AsyncTask<String, Void, ShipmentTaskResult> {
        private final String itemScan;
        private final boolean add;

        PalletItemActionTask(String itemScan, boolean add) {
            this.itemScan = itemScan;
            this.add = add;
        }

        @Override
        protected ShipmentTaskResult doInBackground(String... strings) {
            ShipmentTaskResult result = new ShipmentTaskResult();
            result.scan = itemScan;
            try {
                result.wsResult = ensureWsResult(
                        add
                                ? WebServiceUtil.opTradeLabelAddPalletItem(UserSingleton.get().getHRID(), UserSingleton.get().getHRName(), currentPalletScan, itemScan)
                                : WebServiceUtil.opTradeLabelRemovePalletItem(UserSingleton.get().getHRID(), UserSingleton.get().getHRName(), currentPalletScan, itemScan),
                        add ? "托盘加入物料无返回结果" : "托盘移出物料无返回结果");
            } catch (Exception e) {
                result.wsResult = new WsResult();
                result.wsResult.setResult(false);
                result.wsResult.setErrorInfo("物料操作失败：" + e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(ShipmentTaskResult result) {
            isParsingScan = false;
            if (result.wsResult != null && result.wsResult.getResult()) {
                updateRelationMap(palletItemMap, currentPalletScan, result.scan, add);
                addRecentRecord(add ? "托盘加料" : "托盘移料", result.scan,
                        "目标托盘：" + currentPalletScan);
                refreshUi();
                showSuccess((add ? "物料加入成功：" : "物料移出成功：") + result.scan);
            } else {
                showError(result.wsResult == null ? "物料操作失败" : result.wsResult.getErrorInfo());
            }
        }
    }
}
