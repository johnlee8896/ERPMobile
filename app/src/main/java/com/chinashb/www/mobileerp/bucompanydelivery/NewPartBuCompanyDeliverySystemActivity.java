package com.chinashb.www.mobileerp.bucompanydelivery;

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
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 * @date 创建时间 4/25/26 10:42 AM
 * @author 作者: liweifeng
 * @description
 */
public class NewPartBuCompanyDeliverySystemActivity extends BaseActivity {

    // created by code-x John
    // start: 2026-04-29 21:22:07 CST
    // end: 2026-04-29 21:23:39 CST
    private static final long DEFAULT_DIII_ID = 0L;
    private static final String[] MATERIAL_PREFIXES = {
            "VE", "VF", "VG", "V9", "VA", "VB", "VC"
    };

    @BindView(R.id.new_part_delivery_tray_button) Button trayButton;
    @BindView(R.id.new_part_delivery_container_button) Button containerButton;
    @BindView(R.id.new_part_delivery_delivery_order_button) Button deliveryOrderButton;
    @BindView(R.id.new_part_delivery_camera_scan_button) Button cameraScanButton;
    @BindView(R.id.new_part_delivery_status_text) TextView statusText;
    @BindView(R.id.new_part_delivery_input_edit) EditText inputEdit;
    @BindView(R.id.new_part_delivery_scan_list) CustomRecyclerView scanListView;
    @BindView(R.id.new_part_delivery_submit_button) Button actionButton;

    private final List<DeliveryScanEntity> scanList = new ArrayList<>();
    private final LinkedHashMap<String, List<String>> palletItemMap = new LinkedHashMap<>();
    private final LinkedHashMap<String, List<String>> containerPalletMap = new LinkedHashMap<>();
    private final LinkedHashMap<String, List<String>> deliveryContainerMap = new LinkedHashMap<>();

    private ScanBusinessDispatcher.ScanType currentType = ScanBusinessDispatcher.ScanType.TRAY;
    private DeliveryScanAdapter adapter;
    private boolean isParsingScan;

    private long currentPalletId;
    private String currentPalletCode = "";

    private long currentContainerId;
    private String currentContainerCode = "";

    private long currentDeliveryId;
    private String currentDeliveryCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_part_bu_company_delivery_system);
        ButterKnife.bind(this);

        initRecyclerView();
        setListener();
        refreshUi();
    }

    private void initRecyclerView() {
        adapter = new DeliveryScanAdapter(this, scanList);
        scanListView.setAdapter(adapter);
    }

    private void setListener() {
        inputEdit.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                if (editable.toString().length() > 3) {
                    parseScan(editable.toString());
                }
            }
        });
    }

    @OnClick({
            R.id.new_part_delivery_tray_button,
            R.id.new_part_delivery_container_button,
            R.id.new_part_delivery_delivery_order_button
    })
    public void onModeButtonClick(View view) {
        switch (view.getId()) {
            case R.id.new_part_delivery_tray_button:
                switchType(ScanBusinessDispatcher.ScanType.TRAY);
                break;
            case R.id.new_part_delivery_container_button:
                switchType(ScanBusinessDispatcher.ScanType.CONTAINER);
                break;
            case R.id.new_part_delivery_delivery_order_button:
                switchType(ScanBusinessDispatcher.ScanType.DELIVERY_ORDER);
                break;
        }
    }

    @OnClick(R.id.new_part_delivery_camera_scan_button)
    public void onCameraScanClick() {
        new IntentIntegrator(this)
                .setCaptureActivity(CustomScannerActivity.class)
                .initiateScan();
    }

    @OnClick(R.id.new_part_delivery_submit_button)
    public void onActionClick() {
        if (hasCurrentTarget()) {
            resetCurrentTarget();
        } else {
            clearCurrentModeData();
        }
    }

    private void switchType(ScanBusinessDispatcher.ScanType type) {
        currentType = type;
        refreshUi();
        focusInput();
    }

    // created by code-x John
    // start: 2026-04-29 21:22:07 CST
    // end: 2026-04-29 21:23:39 CST
    private void parseScan(String rawCode) {
        if (isParsingScan) {
            return;
        }

        String scan = normalizeScan(rawCode);
        if (TextUtils.isEmpty(scan)) {
            return;
        }

        switch (currentType) {
            case TRAY:
                handleTrayModeScan(scan);
                break;
            case CONTAINER:
                handleContainerModeScan(scan);
                break;
            case DELIVERY_ORDER:
                handleDeliveryModeScan(scan);
                break;
        }
    }

    private void handleTrayModeScan(String scan) {
        if (isPalletCode(scan)) {
            bindCurrentPallet(scan);
            return;
        }

        if (!isMaterialCode(scan)) {
            showScanError("托盘模式请先扫托盘码，再扫货码");
            return;
        }

        if (currentPalletId <= 0 || TextUtils.isEmpty(currentPalletCode)) {
            showScanError("请先扫描托盘码，如 PT/4");
            return;
        }

        isParsingScan = true;
        new AddMaterialToTradePalletAsyncTask(scan).execute();
    }

    private void handleContainerModeScan(String scan) {
        if (isPalletCode(scan)) {
            if (currentContainerId <= 0 || TextUtils.isEmpty(currentContainerCode)) {
                showScanError("请先扫描集装箱码，再扫描托盘码");
                return;
            }

            isParsingScan = true;
            new AddPalletToContainerAsyncTask(scan).execute();
            return;
        }

        if (isMaterialCode(scan)) {
            showScanError("集装箱模式不能直接扫货码，请先扫集装箱码");
            return;
        }

        bindCurrentContainer(scan);
    }

    private void handleDeliveryModeScan(String scan) {
        if (currentDeliveryId <= 0 || TextUtils.isEmpty(currentDeliveryCode)) {
            if (isMaterialCode(scan) || isPalletCode(scan)) {
                showScanError("请先扫描发货单码，再扫描集装箱码");
                return;
            }

            bindCurrentDelivery(scan);
            return;
        }

        isParsingScan = true;
        new AddContainerToDeliveryAsyncTask(scan).execute();
    }

    private void bindCurrentPallet(String palletCode) {
        long palletId = extractIdFromCode(palletCode);
        if (palletId <= 0) {
            showScanError("托盘码未解析出有效 ID：" + palletCode);
            return;
        }

        currentPalletId = palletId;
        currentPalletCode = palletCode;
        ensureGroupExists(palletItemMap, currentPalletCode);
        clearInputAndFocus();
        refreshUi();
    }

    private void bindCurrentContainer(String containerCode) {
        long containerId = extractIdFromCode(containerCode);
        if (containerId <= 0) {
            showScanError("集装箱码未解析出有效 ID：" + containerCode);
            return;
        }

        currentContainerId = containerId;
        currentContainerCode = containerCode;
        ensureGroupExists(containerPalletMap, currentContainerCode);
        clearInputAndFocus();
        refreshUi();
    }

    private void bindCurrentDelivery(String deliveryCode) {
        long deliveryId = extractIdFromCode(deliveryCode);
        if (deliveryId <= 0) {
            showScanError("发货单码未解析出有效 ID：" + deliveryCode);
            return;
        }

        currentDeliveryId = deliveryId;
        currentDeliveryCode = deliveryCode;
        ensureGroupExists(deliveryContainerMap, currentDeliveryCode);
        clearInputAndFocus();
        refreshUi();
    }

    private void ensureGroupExists(LinkedHashMap<String, List<String>> map, String groupCode) {
        if (!map.containsKey(groupCode)) {
            map.put(groupCode, new ArrayList<String>());
        }
    }

    private void addChildToGroup(LinkedHashMap<String, List<String>> map, String groupCode, String childCode) {
        ensureGroupExists(map, groupCode);
        List<String> childList = map.get(groupCode);
        if (!childList.contains(childCode)) {
            childList.add(childCode);
        }
    }

    private void refreshUi() {
        rebuildDisplayList();
        adapter.refresh(scanList);
        updateStatus();
        updateInputHint();
        updateModeButtons();
        updateActionButton();
    }

    private void rebuildDisplayList() {
        scanList.clear();

        switch (currentType) {
            case TRAY:
                appendModeGroups(
                        palletItemMap,
                        currentPalletCode,
                        "托盘",
                        "货码",
                        "等待扫描货码"
                );
                break;
            case CONTAINER:
                appendModeGroups(
                        containerPalletMap,
                        currentContainerCode,
                        "集装箱",
                        "托盘",
                        "等待扫描托盘码"
                );
                break;
            case DELIVERY_ORDER:
                appendModeGroups(
                        deliveryContainerMap,
                        currentDeliveryCode,
                        "发货单",
                        "集装箱",
                        "等待扫描集装箱码"
                );
                break;
        }
    }

    private void appendModeGroups(
            LinkedHashMap<String, List<String>> map,
            String currentCode,
            String parentLabel,
            String childLabel,
            String emptyTip
    ) {
        if (!TextUtils.isEmpty(currentCode) && map.containsKey(currentCode)) {
            appendSingleGroup(currentCode, map.get(currentCode), parentLabel, childLabel, emptyTip, true);
        }

        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            if (TextUtils.equals(entry.getKey(), currentCode)) {
                continue;
            }
            appendSingleGroup(entry.getKey(), entry.getValue(), parentLabel, childLabel, emptyTip, false);
        }
    }

    private void appendSingleGroup(
            String groupCode,
            List<String> children,
            String parentLabel,
            String childLabel,
            String emptyTip,
            boolean isCurrent
    ) {
        String header = (isCurrent ? "当前" : "") + parentLabel + "  " + groupCode + "  (" + children.size() + ")";
        if (children.isEmpty()) {
            scanList.add(new DeliveryScanEntity(
                    emptyTip,
                    childLabel,
                    header,
                    "扫描成功后会显示在这里"
            ));
            return;
        }

        for (int i = 0; i < children.size(); i++) {
            scanList.add(new DeliveryScanEntity(
                    children.get(i),
                    childLabel,
                    header,
                    childLabel + "第 " + (i + 1) + " 项"
            ));
        }
    }

    private void updateStatus() {
        statusText.setText(
                ScanCounter.buildStatus(
                        currentType,
                        getCurrentTargetCode(),
                        getCurrentChildCount(),
                        palletItemMap.size(),
                        containerPalletMap.size(),
                        deliveryContainerMap.size()
                )
        );
    }

    private void updateInputHint() {
        switch (currentType) {
            case TRAY:
                inputEdit.setHint(TextUtils.isEmpty(currentPalletCode)
                        ? "先扫托盘码，如 PT/4"
                        : "继续扫货码，如 VG/12345");
                break;
            case CONTAINER:
                inputEdit.setHint(TextUtils.isEmpty(currentContainerCode)
                        ? "先扫集装箱码"
                        : "继续扫托盘码");
                break;
            case DELIVERY_ORDER:
                inputEdit.setHint(TextUtils.isEmpty(currentDeliveryCode)
                        ? "先扫发货单码"
                        : "继续扫集装箱码");
                break;
        }
    }

    private void updateModeButtons() {
        trayButton.setEnabled(currentType != ScanBusinessDispatcher.ScanType.TRAY);
        containerButton.setEnabled(currentType != ScanBusinessDispatcher.ScanType.CONTAINER);
        deliveryOrderButton.setEnabled(currentType != ScanBusinessDispatcher.ScanType.DELIVERY_ORDER);

        trayButton.setAlpha(currentType == ScanBusinessDispatcher.ScanType.TRAY ? 1f : 0.6f);
        containerButton.setAlpha(currentType == ScanBusinessDispatcher.ScanType.CONTAINER ? 1f : 0.6f);
        deliveryOrderButton.setAlpha(currentType == ScanBusinessDispatcher.ScanType.DELIVERY_ORDER ? 1f : 0.6f);
    }

    private void updateActionButton() {
        if (hasCurrentTarget()) {
            switch (currentType) {
                case TRAY:
                    actionButton.setText("重置当前托盘");
                    break;
                case CONTAINER:
                    actionButton.setText("重置当前集装箱");
                    break;
                case DELIVERY_ORDER:
                    actionButton.setText("重置当前发货单");
                    break;
            }
        } else {
            switch (currentType) {
                case TRAY:
                    actionButton.setText("清空托盘记录");
                    break;
                case CONTAINER:
                    actionButton.setText("清空集装箱记录");
                    break;
                case DELIVERY_ORDER:
                    actionButton.setText("清空发货单记录");
                    break;
            }
        }
    }

    private void resetCurrentTarget() {
        switch (currentType) {
            case TRAY:
                currentPalletId = 0L;
                currentPalletCode = "";
                ToastUtil.showToastShort("已重置当前托盘");
                break;
            case CONTAINER:
                currentContainerId = 0L;
                currentContainerCode = "";
                ToastUtil.showToastShort("已重置当前集装箱");
                break;
            case DELIVERY_ORDER:
                currentDeliveryId = 0L;
                currentDeliveryCode = "";
                ToastUtil.showToastShort("已重置当前发货单");
                break;
        }
        clearInputAndFocus();
        refreshUi();
    }

    private void clearCurrentModeData() {
        switch (currentType) {
            case TRAY:
                palletItemMap.clear();
                break;
            case CONTAINER:
                containerPalletMap.clear();
                break;
            case DELIVERY_ORDER:
                deliveryContainerMap.clear();
                break;
        }
        ToastUtil.showToastShort("已清空当前模式记录");
        clearInputAndFocus();
        refreshUi();
    }

    private boolean hasCurrentTarget() {
        switch (currentType) {
            case TRAY:
                return !TextUtils.isEmpty(currentPalletCode);
            case CONTAINER:
                return !TextUtils.isEmpty(currentContainerCode);
            case DELIVERY_ORDER:
            default:
                return !TextUtils.isEmpty(currentDeliveryCode);
        }
    }

    private int getCurrentChildCount() {
        switch (currentType) {
            case TRAY:
                return getChildCount(palletItemMap, currentPalletCode);
            case CONTAINER:
                return getChildCount(containerPalletMap, currentContainerCode);
            case DELIVERY_ORDER:
            default:
                return getChildCount(deliveryContainerMap, currentDeliveryCode);
        }
    }

    private int getChildCount(LinkedHashMap<String, List<String>> map, String groupCode) {
        if (TextUtils.isEmpty(groupCode) || !map.containsKey(groupCode)) {
            return 0;
        }
        return map.get(groupCode).size();
    }

    private String getCurrentTargetCode() {
        switch (currentType) {
            case TRAY:
                return currentPalletCode;
            case CONTAINER:
                return currentContainerCode;
            case DELIVERY_ORDER:
            default:
                return currentDeliveryCode;
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

    private void showScanError(String message) {
        clearInputAndFocus();
        ToastUtil.showToastLong(message);
    }

    private String normalizeScan(String content) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }

        String scan = content.trim();
        if (scan.contains("\n")) {
            scan = scan.replace("\n", "");
        }
        if (scan.contains("／")) {
            scan = scan.replace("／", "/");
        }
        return scan;
    }

    private boolean isMaterialCode(String scan) {
        String prefix = getScanPrefix(scan);
        for (String item : MATERIAL_PREFIXES) {
            if (item.equalsIgnoreCase(prefix)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPalletCode(String scan) {
        String prefix = getScanPrefix(scan);
        return "PT".equalsIgnoreCase(prefix)
                || "PLT".equalsIgnoreCase(prefix)
                || "PALLET_ID".equalsIgnoreCase(prefix)
                || "PALLET".equalsIgnoreCase(prefix);
    }

    private String getScanPrefix(String scan) {
        if (TextUtils.isEmpty(scan)) {
            return "";
        }

        String[] parts = scan.split("/");
        return parts.length > 0 ? parts[0].trim() : "";
    }

    private long extractIdFromCode(String scan) {
        if (TextUtils.isEmpty(scan)) {
            return 0L;
        }

        String[] parts = scan.split("/");
        for (int i = parts.length - 1; i >= 0; i--) {
            String numericPart = parts[i].replaceAll("[^0-9]", "");
            if (!TextUtils.isEmpty(numericPart)) {
                try {
                    return Long.parseLong(numericPart);
                } catch (NumberFormatException e) {
                    return 0L;
                }
            }
        }
        return 0L;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && !TextUtils.isEmpty(result.getContents())) {
            parseScan(result.getContents());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // created by code-x John
    // start: 2026-04-29 21:22:07 CST
    // end: 2026-04-29 21:23:39 CST
    private class AddMaterialToTradePalletAsyncTask extends AsyncTask<Void, Void, WsResult> {

        private final String scan;

        AddMaterialToTradePalletAsyncTask(String scan) {
            this.scan = scan;
        }

        @Override
        protected WsResult doInBackground(Void... voids) {
            return WebServiceUtil.opTransferTradeScanToAddTradePallet(
                    UserSingleton.get().getUserInfo().getBu_ID(),
                    currentPalletId,
                    DEFAULT_DIII_ID,
                    scan
            );
        }

        @Override
        protected void onPostExecute(WsResult result) {
            isParsingScan = false;
            clearInputAndFocus();

            if (result != null && result.getResult()) {
                addChildToGroup(palletItemMap, currentPalletCode, scan);
                refreshUi();
                return;
            }

            ToastUtil.showToastLong(result == null ? "装入托盘失败，请检查网络后重试" : result.getErrorInfo());
        }
    }

    // created by code-x John
    // start: 2026-04-29 21:22:07 CST
    // end: 2026-04-29 21:23:39 CST
    private class AddPalletToContainerAsyncTask extends AsyncTask<Void, Void, WsResult> {

        private final String scan;

        AddPalletToContainerAsyncTask(String scan) {
            this.scan = scan;
        }

        @Override
        protected WsResult doInBackground(Void... voids) {
            return WebServiceUtil.opTransferTradeScanToAddPalletToContainer(
                    UserSingleton.get().getUserInfo().getBu_ID(),
                    currentContainerCode,
                    currentContainerId,
                    scan
            );
        }

        @Override
        protected void onPostExecute(WsResult result) {
            isParsingScan = false;
            clearInputAndFocus();

            if (result != null && result.getResult()) {
                addChildToGroup(containerPalletMap, currentContainerCode, scan);
                refreshUi();
                return;
            }

            ToastUtil.showToastLong(result == null ? "装入集装箱失败，请检查网络后重试" : result.getErrorInfo());
        }
    }

    // created by code-x John
    // start: 2026-04-29 21:22:07 CST
    // end: 2026-04-29 21:23:39 CST
    private class AddContainerToDeliveryAsyncTask extends AsyncTask<Void, Void, WsResult> {

        private final String scan;

        AddContainerToDeliveryAsyncTask(String scan) {
            this.scan = scan;
        }

        @Override
        protected WsResult doInBackground(Void... voids) {
            return WebServiceUtil.opTransferTradeScanToAddContainerToDelivery(
                    UserSingleton.get().getUserInfo().getBu_ID(),
                    currentDeliveryCode,
                    currentDeliveryId,
                    scan
            );
        }

        @Override
        protected void onPostExecute(WsResult result) {
            isParsingScan = false;
            clearInputAndFocus();

            if (result != null && result.getResult()) {
                addChildToGroup(deliveryContainerMap, currentDeliveryCode, scan);
                refreshUi();
                return;
            }

            ToastUtil.showToastLong(result == null ? "装入发货单失败，请检查网络后重试" : result.getErrorInfo());
        }
    }
}
