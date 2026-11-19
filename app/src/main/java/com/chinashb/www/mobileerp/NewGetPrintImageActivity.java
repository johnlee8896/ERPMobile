package com.chinashb.www.mobileerp;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.LabelPrintImageBean;
import com.chinashb.www.mobileerp.bean.SMLPrintBatchParamsBean;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.printer.BTHDiscoveryActivity;
import com.chinashb.www.mobileerp.utils.JsonUtil;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.zebra.printer.sdk.ZebraPrinter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 * @date 创建时间 2026/5/28 10:16
 * @author 作者: code-x John
 * @description 获取标签打印图片并预览
 */
public class NewGetPrintImageActivity extends BaseActivity {

    private static final String TAG = "NewGetPrintImage";
    private static final String PRINT_PREFS = "print_image_calibration";
    private static final int SOURCE_TRIM_BORDER_PX = 2;
    private static final int SOURCE_LEFT_CLEANUP_PX = 22;
    // created by code-x John
    // start: 2026-06-05 15:42:00 CST
    // 批量打印测试省纸模式：当前只打印最多5张小标签，以及对应少量中/大标签；正式使用时可恢复
    private static final int TEST_MAX_SMLI_PRINT_COUNT = 5;
    // end: 2026-06-05 15:42:00 CST
    private static final int REQUEST_PERMISSION_CODE = 0x101;
    private static final int REQUEST_BLUETOOTH_CONNECT_CODE = 0x102;
    private static final LabelTypeOption[] LABEL_TYPE_OPTIONS = new LabelTypeOption[]{
            new LabelTypeOption("PT", "托盘标签"),
            new LabelTypeOption("TC", "集装箱标签"),
            new LabelTypeOption("TS", "发运单标签"),
            new LabelTypeOption("SMT", "大箱标签"),
            new LabelTypeOption("SMM", "中箱标签"),
            new LabelTypeOption("SMLI", "小箱标签")
    };
    private static final String[] USE_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @BindView(R.id.print_image_type_spinner)
    Spinner typeSpinner;
    @BindView(R.id.print_image_id_edit_text)
    EditText idEditText;
    @BindView(R.id.print_image_fetch_button)
    TextView fetchButton;
    @BindView(R.id.print_image_button)
    TextView printButton;
    @BindView(R.id.print_image_manage_button)
    TextView printerManageButton;
    @BindView(R.id.print_image_result_text)
    TextView resultTextView;
    @BindView(R.id.print_image_preview_image_view)
    ImageView previewImageView;
    @BindView(R.id.print_batch_image_by_sml_button)
    TextView printBatchImageBySmlButton;
    @BindView(R.id.print_batch_image_by_scan_sml_button)
    TextView printBatchImageByScanSmlLabelButton;
    @BindView(R.id.print_batch_test_mode_check_box)
    CheckBox printBatchTestModeCheckBox;

    private final int pageWidth = 1600;
    private final int qrPriorityLeftSafetyMargin = 36;
    private final int qrPriorityRightSafetyMargin = 8;
    private final ArrayList<String> requestPermissions = new ArrayList<>();
    private ProgressDialog progressDialog;
    private AlertDialog printerManagerDialog;
    private AlertDialog calibrationDialog;
    private TextView printerTipsTextView;
    private Button connectPrinterButton;
    private Button disconnectPrinterButton;
    private RadioGroup printerLanguageRadioGroup;
    private RadioButton cpclRadioButton;
    private RadioButton escPosRadioButton;
    private Button printerStatusButton;
    private Button printerConfigButton;
    private Button printerCalibrationButton;
    private Button printerResetButton;

    private LabelTypeOption selectedOption = LABEL_TYPE_OPTIONS[0];
    private Bitmap currentPreviewBitmap;
    private static boolean bOpen = false;
    private static boolean bLanguageCPCL = false;
    private static boolean bLanguageESCPOS = false;
    private String lastPrinterMacAddress;
    private int cpclPaperWidth = 640;
    private int cpclLabelHeight = 360;
    private int cpclHorizontalOffset = 0;
    private int cpclVerticalOffset = 0;
    private int cpclScalePercent = 100;
    private int escPosTargetWidth = 560;
    private boolean testLimitBatchPrint = true;
    private volatile boolean cancelPrintRequested = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_get_print_image);
        ButterKnife.bind(this);
        loadCalibrationSettings();
        initTypeSpinner();
        initBatchTestModeCheckBox();
        checkPermission();
        requestPermission();
        registerPrinterDisconnectReceiver();
        ZebraPrinter.SetLog(3);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
        dismissPrinterManagerDialog();
        dismissCalibrationDialog();
        try {
            unregisterReceiver(printerConnectReceiver);
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode != RESULT_OK || data == null) {
//            return;
//        }
//        if (requestCode == REQUEST_BLUETOOTH_CONNECT_CODE) {
//            String printerMacAddress = data.getStringExtra(BTHDiscoveryActivity.EXTRA_DEVICE_ADDRESS);
//            if (TextUtils.isEmpty(printerMacAddress)) {
//                CommonUtil.ShowToast(this, "未获取到打印机地址", R.mipmap.warning);
//                return;
//            }
//            connectPrinter(printerMacAddress);
//        }

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (!TextUtils.isEmpty(result.getContents())) {
                long smlID = parseScanResult(result.getContents());
                if (smlID == -1){
                    ToastUtil.showToastShort("生产记录标签解析失败！");
                }else{
                    printBatchBitmapBySMLCommon(smlID);
                }
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
            //蓝牙打印机连接，这里作一合并
            if (resultCode != RESULT_OK || data == null) {
                return;
            }
            if (requestCode == REQUEST_BLUETOOTH_CONNECT_CODE) {
                String printerMacAddress = data.getStringExtra(BTHDiscoveryActivity.EXTRA_DEVICE_ADDRESS);
                if (TextUtils.isEmpty(printerMacAddress)) {
                    CommonUtil.ShowToast(this, "未获取到打印机地址", R.mipmap.warning);
                    return;
                }
                connectPrinter(printerMacAddress);
            }
        }
    }

    private void printBatchBitmapBySMLCommon(long smlID) {
        if (!bOpen) {
            CommonUtil.ShowToast(this, "请先连接打印机", R.mipmap.warning);
            showPrinterManagerDialog();
            return;
        }


        new GetBatchImageBySMLAsyncTask().execute(smlID);
    }

    //这里主要扫描生产记录标签解析，若后续有别的类型，再往上加
    private long parseScanResult(String content) {
//        if (content.startsWith("SML/")){
//            String[] strs = content.split("/");
//            if (strs.length == 2){
//                return Integer.parseInt(strs[1]);
//            }else{
//                return -1;
//            }
//
//        }else {
//            return -1;
//        }


        if (content != null && content.startsWith("SML/")) {
            try {
                return Long.parseLong(content.substring(4));
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    private void initTypeSpinner() {
        String[] displayArray = new String[LABEL_TYPE_OPTIONS.length];
        for (int i = 0; i < LABEL_TYPE_OPTIONS.length; i++) {
            displayArray[i] = LABEL_TYPE_OPTIONS[i].code + " - " + LABEL_TYPE_OPTIONS[i].name;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, displayArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedOption = LABEL_TYPE_OPTIONS[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedOption = LABEL_TYPE_OPTIONS[0];
            }
        });
    }

    private void initBatchTestModeCheckBox() {
        printBatchTestModeCheckBox.setChecked(testLimitBatchPrint);
        printBatchTestModeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            testLimitBatchPrint = isChecked;
            saveBatchPrintModeSettings();
        });
    }

    private void checkPermission() {
        requestPermissions.clear();
        for (String permission : USE_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions.add(permission);
            }
        }
    }

    private void requestPermission() {
        if (!requestPermissions.isEmpty()) {
            String[] permissionArray = new String[requestPermissions.size()];
            ActivityCompat.requestPermissions(this, requestPermissions.toArray(permissionArray), REQUEST_PERMISSION_CODE);
        }
    }

    private void registerPrinterDisconnectReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(printerConnectReceiver, intentFilter);
    }

    @OnClick(R.id.print_image_fetch_button)
    public void onFetchButtonClick() {
        String idText = idEditText.getText().toString().trim();
        if (TextUtils.isEmpty(idText)) {
            CommonUtil.ShowToast(this, "请输入对象ID", R.mipmap.warning);
            return;
        }

        long objectId;
        try {
            objectId = Long.parseLong(idText);
        } catch (Exception e) {
            CommonUtil.ShowToast(this, "对象ID格式错误", R.mipmap.warning);
            return;
        }

        new GetPrintImageAsyncTask(selectedOption.code, objectId).execute();
    }

    @OnClick(R.id.print_image_button)
    public void onPrintButtonClick() {
        if (currentPreviewBitmap == null) {
            CommonUtil.ShowToast(this, "请先获取并预览图片", R.mipmap.warning);
            return;
        }
        if (!bOpen) {
            CommonUtil.ShowToast(this, "请先连接打印机", R.mipmap.warning);
            showPrinterManagerDialog();
            return;
        }
        printCurrentPreviewBitmap();
    }

    @OnClick(R.id.print_image_manage_button)
    public void onPrinterManageButtonClick() {
        showPrinterManagerDialog();
    }

    @OnClick(R.id.print_batch_image_by_sml_button)
    public void onPrintBatchImageSMLButtonClick() {

        String idText = idEditText.getText().toString().trim();
        if (TextUtils.isEmpty(idText)) {
            CommonUtil.ShowToast(this, "请在输入框中输入生产记录ID", R.mipmap.warning);
            return;
        }

        long objectId;
        try {
            objectId = Long.parseLong(idText);
        } catch (Exception e) {
            CommonUtil.ShowToast(this, "输入的生产记录ID格式错误", R.mipmap.warning);
            return;
        }

//        if (!bOpen) {
//            CommonUtil.ShowToast(this, "请先连接打印机", R.mipmap.warning);
//            showPrinterManagerDialog();
//            return;
//        }
//
//        new GetBatchImageBySMLAsyncTask().execute(objectId);
        printBatchBitmapBySMLCommon(objectId);
    }

    @OnClick(R.id.print_batch_image_by_scan_sml_button)
    public void onPrintBatchImageByScanSmlButtonClick(){
        new IntentIntegrator(NewGetPrintImageActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();

    }

    private void showPrinterManagerDialog() {
        if (printerManagerDialog == null) {
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_zebra_printer_manage, null, false);
            printerTipsTextView = dialogView.findViewById(R.id.dialog_printer_tips_text_view);
            connectPrinterButton = dialogView.findViewById(R.id.dialog_printer_connect_button);
            disconnectPrinterButton = dialogView.findViewById(R.id.dialog_printer_disconnect_button);
            printerLanguageRadioGroup = dialogView.findViewById(R.id.dialog_printer_language_radio_group);
            cpclRadioButton = dialogView.findViewById(R.id.dialog_printer_cpcl_button);
            escPosRadioButton = dialogView.findViewById(R.id.dialog_printer_escpos_button);
            printerStatusButton = dialogView.findViewById(R.id.dialog_printer_status_button);
            printerConfigButton = dialogView.findViewById(R.id.dialog_printer_configuration_button);
            printerCalibrationButton = dialogView.findViewById(R.id.dialog_printer_calibration_button);
            printerResetButton = dialogView.findViewById(R.id.dialog_printer_reset_button);

            connectPrinterButton.setOnClickListener(v -> openPrinterDiscovery());
            disconnectPrinterButton.setOnClickListener(v -> disconnectPrinter());
            printerStatusButton.setOnClickListener(v -> queryPrinterStatus());
            printerConfigButton.setOnClickListener(v -> printConfigurationPage());
            printerCalibrationButton.setOnClickListener(v -> showCalibrationDialog());
            printerResetButton.setOnClickListener(v -> resetPrinterStateAndReconnect());
            printerLanguageRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (!bOpen) {
                    return;
                }
                if (checkedId == R.id.dialog_printer_cpcl_button && !bLanguageCPCL) {
                    switchPrinterLanguage(true);
                } else if (checkedId == R.id.dialog_printer_escpos_button && !bLanguageESCPOS) {
                    switchPrinterLanguage(false);
                }
            });

            printerManagerDialog = new AlertDialog.Builder(this)
                    .setTitle("斑马打印机管理")
                    .setView(dialogView)
                    .setNegativeButton("关闭", null)
                    .create();
        }
        updatePrinterManagerUi();
        printerManagerDialog.show();
    }

    private void dismissPrinterManagerDialog() {
        if (printerManagerDialog != null && printerManagerDialog.isShowing()) {
            printerManagerDialog.dismiss();
        }
    }

    private void showCalibrationDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_print_image_calibration, null, false);
        EditText paperWidthEditText = dialogView.findViewById(R.id.dialog_calibration_paper_width_edit_text);
        EditText labelHeightEditText = dialogView.findViewById(R.id.dialog_calibration_label_height_edit_text);
        EditText horizontalOffsetEditText = dialogView.findViewById(R.id.dialog_calibration_horizontal_offset_edit_text);
        EditText verticalOffsetEditText = dialogView.findViewById(R.id.dialog_calibration_vertical_offset_edit_text);
        EditText scalePercentEditText = dialogView.findViewById(R.id.dialog_calibration_scale_percent_edit_text);
        EditText escPosWidthEditText = dialogView.findViewById(R.id.dialog_calibration_escpos_width_edit_text);

        paperWidthEditText.setText(String.valueOf(cpclPaperWidth));
        labelHeightEditText.setText(String.valueOf(cpclLabelHeight));
        horizontalOffsetEditText.setText(String.valueOf(cpclHorizontalOffset));
        verticalOffsetEditText.setText(String.valueOf(cpclVerticalOffset));
        scalePercentEditText.setText(String.valueOf(cpclScalePercent));
        escPosWidthEditText.setText(String.valueOf(escPosTargetWidth));

        calibrationDialog = new AlertDialog.Builder(this)
                .setTitle("打印校准")
                .setView(dialogView)
                .setNegativeButton("取消", null)
                .setPositiveButton("保存", (dialog, which) -> {
                    cpclPaperWidth = parseIntOrDefault(paperWidthEditText.getText().toString(), cpclPaperWidth);
                    cpclLabelHeight = parseIntOrDefault(labelHeightEditText.getText().toString(), cpclLabelHeight);
                    cpclHorizontalOffset = parseIntOrDefault(horizontalOffsetEditText.getText().toString(), cpclHorizontalOffset);
                    cpclVerticalOffset = parseIntOrDefault(verticalOffsetEditText.getText().toString(), cpclVerticalOffset);
                    cpclScalePercent = parseIntOrDefault(scalePercentEditText.getText().toString(), cpclScalePercent);
                    escPosTargetWidth = parseIntOrDefault(escPosWidthEditText.getText().toString(), escPosTargetWidth);
                    normalizeCalibrationSettings();
                    saveCalibrationSettings();
                    if (currentPreviewBitmap != null) {
                        updateResultText(null);
                    }
                    CommonUtil.ShowToast(NewGetPrintImageActivity.this, "打印校准参数已保存", R.mipmap.smiley);
                })
                .create();
        calibrationDialog.show();
    }

    private void dismissCalibrationDialog() {
        if (calibrationDialog != null && calibrationDialog.isShowing()) {
            calibrationDialog.dismiss();
        }
    }

    private void openPrinterDiscovery() {
        Intent intent = new Intent(this, BTHDiscoveryActivity.class);
        startActivityForResult(intent, REQUEST_BLUETOOTH_CONNECT_CODE);
    }

    private void connectPrinter(String printerMacAddress) {
        showProgressDialog(getString(R.string.connecting));
        new Thread(() -> {
            int result;
            final StringBuffer printerLanguages = new StringBuffer();
            try {
                ZebraPrinter.Close();
            } catch (Exception ignored) {
            }
            bOpen = false;
            try {
                result = ZebraPrinter.Open(0, printerMacAddress);
            } catch (Exception e) {
                result = -1;
            }

            final int openResult = result;
            runOnUiThread(() -> {
                dismissProgressDialog();
                if (openResult == ZebraPrinter.ZEBRA_E_SUCCESS) {
                    bOpen = true;
                    lastPrinterMacAddress = printerMacAddress;
                    saveLastPrinterMacAddress();
                    ZebraPrinter.SGD_GetVar("device.languages", printerLanguages);
                    String printerLanguage = printerLanguages.toString();
                    bLanguageCPCL = "line_print".equalsIgnoreCase(printerLanguage);
                    bLanguageESCPOS = "esc_pos".equalsIgnoreCase(printerLanguage);
                    if (!bLanguageCPCL && !bLanguageESCPOS) {
                        bLanguageCPCL = true;
                    }
                    updatePrinterManagerUi();
                    CommonUtil.ShowToast(NewGetPrintImageActivity.this, "打印机连接成功", R.mipmap.smiley);
                } else if (openResult == ZebraPrinter.ZEBRA_E_INVALID_MODEL) {
                    CommonUtil.ShowToast(NewGetPrintImageActivity.this, getString(R.string.not_support_printer), R.mipmap.warning);
                } else {
                    CommonUtil.ShowToast(NewGetPrintImageActivity.this, getString(R.string.connect_failed), R.mipmap.warning);
                }
            });
        }).start();
    }

    private void resetPrinterStateAndReconnect() {
        if (TextUtils.isEmpty(lastPrinterMacAddress) && !bOpen) {
            CommonUtil.ShowToast(this, "没有可重连的打印机地址，请先连接一次打印机", R.mipmap.warning);
            return;
        }
        cancelPrintRequested = true;
        showProgressDialog("正在清空打印状态并重连...");
        new Thread(() -> {
            boolean success = false;
            String errorMessage = null;
            StringBuffer printerLanguages = new StringBuffer();
            try {
                try {
                    ZebraPrinter.WriteData("~JA\r\n".getBytes());
                    Thread.sleep(300);
                } catch (Exception ignored) {
                }
                try {
                    ZebraPrinter.Close();
                } catch (Exception ignored) {
                }
                bOpen = false;
                bLanguageCPCL = false;
                bLanguageESCPOS = false;
                if (!TextUtils.isEmpty(lastPrinterMacAddress)) {
                    int result = ZebraPrinter.Open(0, lastPrinterMacAddress);
                    if (result == ZebraPrinter.ZEBRA_E_SUCCESS) {
                        bOpen = true;
                        ZebraPrinter.SGD_GetVar("device.languages", printerLanguages);
                        String printerLanguage = printerLanguages.toString();
                        bLanguageCPCL = "line_print".equalsIgnoreCase(printerLanguage);
                        bLanguageESCPOS = "esc_pos".equalsIgnoreCase(printerLanguage);
                        if (!bLanguageCPCL && !bLanguageESCPOS) {
                            bLanguageCPCL = true;
                        }
                        success = true;
                    } else {
                        errorMessage = "重连打印机失败";
                    }
                } else {
                    errorMessage = "没有可重连的打印机地址";
                }
            } catch (Exception e) {
                Log.e(TAG, "resetPrinterStateAndReconnect error", e);
                errorMessage = "清空打印状态失败：" + e.getMessage();
            }
            final boolean finalSuccess = success;
            final String finalErrorMessage = errorMessage;
            runOnUiThread(() -> {
                dismissProgressDialog();
                cancelPrintRequested = false;
                updatePrinterManagerUi();
                if (finalSuccess) {
                    CommonUtil.ShowToast(NewGetPrintImageActivity.this, "已清空状态并重连打印机", R.mipmap.smiley);
                } else {
                    CommonUtil.ShowToast(NewGetPrintImageActivity.this,
                            TextUtils.isEmpty(finalErrorMessage) ? "清空打印状态失败" : finalErrorMessage,
                            R.mipmap.warning);
                }
            });
        }).start();
    }

    private void disconnectPrinter() {
        showProgressDialog(getString(R.string.disconnecting));
        new Thread(() -> {
            int result;
            try {
                result = ZebraPrinter.Close();
            } catch (Exception e) {
                result = -1;
            }
            final int closeResult = result;
            runOnUiThread(() -> {
                dismissProgressDialog();
                if (closeResult == ZebraPrinter.ZEBRA_E_SUCCESS || closeResult == 0) {
                    bOpen = false;
                    bLanguageCPCL = false;
                    bLanguageESCPOS = false;
                    updatePrinterManagerUi();
                    CommonUtil.ShowToast(NewGetPrintImageActivity.this, "打印机已断开", R.mipmap.smiley);
                } else {
                    CommonUtil.ShowToast(NewGetPrintImageActivity.this, "断开打印机失败", R.mipmap.warning);
                }
            });
        }).start();
    }

    private void switchPrinterLanguage(boolean switchToCpcl) {
        if (!bOpen) {
            return;
        }
        String targetLanguage = switchToCpcl ? "line_print" : "esc_pos";
        showProgressDialog(switchToCpcl ? getString(R.string.language_switched_to_cpcl) : getString(R.string.language_switched_to_escpos));
        new Thread(() -> {
            StringBuffer printerLanguages = new StringBuffer();
            try {
                int printerState = ZebraPrinter.GetPrinterState();
                if (printerState == 1) {
                    runOnUiThread(() -> {
                        dismissProgressDialog();
                        updatePrinterManagerUi();
                        Toast.makeText(NewGetPrintImageActivity.this, R.string.language_switched_error, Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                ZebraPrinter.SGD_SetVar("device.languages", targetLanguage);
                Thread.sleep(1000);
                ZebraPrinter.SGD_GetVar("device.languages", printerLanguages);
                boolean success = printerLanguages.toString().contains(targetLanguage);
                runOnUiThread(() -> {
                    dismissProgressDialog();
                    if (success) {
                        bLanguageCPCL = switchToCpcl;
                        bLanguageESCPOS = !switchToCpcl;
                        updatePrinterManagerUi();
                        CommonUtil.ShowToast(NewGetPrintImageActivity.this, switchToCpcl ? "已切换到CPCL" : "已切换到ESCPOS", R.mipmap.smiley);
                    } else {
                        updatePrinterManagerUi();
                        CommonUtil.ShowToast(NewGetPrintImageActivity.this, switchToCpcl ? getString(R.string.not_support_cpcl) : getString(R.string.not_support_escpos), R.mipmap.warning);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    dismissProgressDialog();
                    updatePrinterManagerUi();
                    CommonUtil.ShowToast(NewGetPrintImageActivity.this, "切换打印机语言失败", R.mipmap.warning);
                });
            }
        }).start();
    }

    private void queryPrinterStatus() {
        if (!bOpen) {
            CommonUtil.ShowToast(this, "请先连接打印机", R.mipmap.warning);
            return;
        }
        int printerState = ZebraPrinter.GetPrinterState();
        switch (printerState) {
            case 0:
                CommonUtil.ShowToast(this, getString(R.string.printer_ready), R.mipmap.smiley);
                break;
            case 1:
                CommonUtil.ShowToast(this, getString(R.string.printer_busy), R.mipmap.warning);
                break;
            case 2:
                CommonUtil.ShowToast(this, getString(R.string.printer_cover_open), R.mipmap.warning);
                break;
            case 3:
                CommonUtil.ShowToast(this, getString(R.string.printer_media_out), R.mipmap.warning);
                break;
            default:
                CommonUtil.ShowToast(this, "打印机状态未知：" + printerState, R.mipmap.warning);
                break;
        }
    }

    private void printConfigurationPage() {
        if (!bOpen) {
            CommonUtil.ShowToast(this, "请先连接打印机", R.mipmap.warning);
            return;
        }
        int printerState = ZebraPrinter.GetPrinterState();
        if (printerState == 2 || printerState == 3) {
            Toast.makeText(this, R.string.printer_error_not_print, Toast.LENGTH_SHORT).show();
            return;
        }
        showProgressDialog(getString(R.string.printing));
        new Thread(() -> {
            try {
                ZebraPrinter.WriteData("! U1 setvar \"test.print_diags\" \"\"\r\n".getBytes());
                waitPrinterComplete();
                runOnUiThread(() -> {
                    dismissProgressDialog();
                    CommonUtil.ShowToast(NewGetPrintImageActivity.this, "打印配置页完成", R.mipmap.smiley);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    dismissProgressDialog();
                    CommonUtil.ShowToast(NewGetPrintImageActivity.this, "打印配置页失败", R.mipmap.warning);
                });
            }
        }).start();
    }

    private void printCurrentPreviewBitmap() {
        Bitmap bitmap = currentPreviewBitmap;
        if (bitmap == null) {
            CommonUtil.ShowToast(this, "请先获取并预览图片", R.mipmap.warning);
            return;
        }

        int printerState = ZebraPrinter.GetPrinterState();
        if (printerState == 2 || printerState == 3) {
            Toast.makeText(this, R.string.printer_error_not_print, Toast.LENGTH_SHORT).show();
            return;
        }

        startPrintProgressDialog(getString(R.string.printing));
        new Thread(() -> {
            try {
                ensurePrinterReadyOrThrow();
                if (bLanguageCPCL) {
                    printBitmapByCpcl(bitmap);
                } else if (bLanguageESCPOS) {
                    printBitmapByEscPos(bitmap);
                } else {
                    throw new IllegalStateException("打印机语言未设置");
                }
                waitPrinterComplete();
                runOnUiThread(() -> {
                    dismissProgressDialog();
                    if (cancelPrintRequested) {
                        CommonUtil.ShowToast(NewGetPrintImageActivity.this, "已取消打印", R.mipmap.warning);
                    } else {
                        CommonUtil.ShowToast(NewGetPrintImageActivity.this, "打印完成", R.mipmap.smiley);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "printCurrentPreviewBitmap error", e);
                runOnUiThread(() -> {
                    dismissProgressDialog();
                    CommonUtil.ShowToast(NewGetPrintImageActivity.this, "打印失败：" + e.getMessage(), R.mipmap.warning);
                });
            }
        }).start();
    }

    private void printBitmapByCpcl(Bitmap bitmap) {
        Bitmap preparedBitmap = prepareBitmapForCpcl(bitmap);
        int printWidth = preparedBitmap.getWidth();
        ZebraPrinter.CPCL_PrinterInit();
        ZebraPrinter.CPCL_SetDensity(75);
        ZebraPrinter.CPCL_SetFont4LineMode("7", 0, 20);
        ZebraPrinter.CPCL_SetPageWidth(printWidth);
        ZebraPrinter.CPCL_SetRelativePosition4LineMode(0, 0);
        ZebraPrinter.CPCL_PreFeed(0);
        ZebraPrinter.CPCL_PostFeed(0);
        ZebraPrinter.CPCL_CreateLabel(0, preparedBitmap.getHeight(), 1);
        ZebraPrinter.CPCL_SetPageWidth(printWidth);
        ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
        ZebraPrinter.CPCL_PrintImage(0, 0, ZebraPrinter.CPCL_ROTATION0, preparedBitmap);
        ZebraPrinter.CPCL_Print();
    }

    private void printBitmapByCpclBatch(List<Bitmap> bitmapList) {
        if (bitmapList == null || bitmapList.isEmpty()) {
            return;
        }
        for (Bitmap bitmap : bitmapList) {
            if (cancelPrintRequested) {
                return;
            }
            ensurePrinterReadyOrThrow();
            if (bitmap == null) {
                continue;
            }
            // 批量时完全复用单张打印流程，避免多张任务共用同一套 CPCL 状态导致实际出纸数量异常。
            printBitmapByCpcl(bitmap);
            try {
                waitPrinterComplete();
                Thread.sleep(150);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        // 批量打印结束后发 FORM 指令，让最后一张标签正常走到撕纸位，避免打印机在末尾卡纸。
        try {
            waitPrinterComplete();
            ZebraPrinter.CPCL_Form();
        } catch (Exception e) {
            Log.w(TAG, "CPCL FormFeed after batch print failed", e);
        }
    }

    private void printBitmapByEscPos(Bitmap bitmap) {
        ensurePrinterReadyOrThrow();
        Bitmap preparedBitmap = prepareBitmapForEscPos(bitmap);
        ZebraPrinter.ESCPOS_PrinterInit();
        ZebraPrinter.ESCPOS_SetTextFont(0);
        ZebraPrinter.ESCPOS_SelectLineMode();
        ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
        ZebraPrinter.ESCPOS_PrintImage(preparedBitmap, 0);
        ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
        ZebraPrinter.ESCPOS_FeedLines(3);
    }

    private Bitmap prepareBitmapForCpcl(Bitmap sourceBitmap) {
        if (sourceBitmap == null) {
            return null;
        }
        sourceBitmap = trimOuterBorder(sourceBitmap);
        sourceBitmap = cleanupLeftGuideArea(sourceBitmap);
        normalizeCalibrationSettings();
        float scale = Math.max(0.1f, cpclScalePercent / 100f);
        int safeLeftMargin = Math.max(qrPriorityLeftSafetyMargin, Math.max(0, cpclHorizontalOffset));
        int safeRightMargin = Math.max(qrPriorityRightSafetyMargin, Math.max(0, -cpclHorizontalOffset));
        int safeContentWidth = Math.max(120, cpclPaperWidth - safeLeftMargin - safeRightMargin);
        int targetContentWidth = Math.min(safeContentWidth, Math.max(1, Math.round(sourceBitmap.getWidth() * scale)));
        float widthScale = targetContentWidth * 1f / sourceBitmap.getWidth();
        float finalScale = Math.min(scale, widthScale);
        int scaledWidth = Math.max(1, Math.round(sourceBitmap.getWidth() * finalScale));
        int scaledHeight = Math.max(1, Math.round(sourceBitmap.getHeight() * finalScale));
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(sourceBitmap, scaledWidth, scaledHeight, true);
        Bitmap canvasBitmap = Bitmap.createBitmap(cpclPaperWidth, cpclLabelHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(canvasBitmap);
        canvas.drawColor(Color.WHITE);
        // 二维码在左侧时优先保证左边留出更大的安全边距，不再强制整图居中。
        int left = safeLeftMargin + cpclHorizontalOffset;
        int maxLeft = Math.max(0, cpclPaperWidth - safeRightMargin - scaledWidth);
        if (left > maxLeft) {
            left = maxLeft;
        }
        int top = (cpclLabelHeight - scaledHeight) / 2 + cpclVerticalOffset;
        left = Math.max(0, left);
        top = Math.max(0, Math.min(top, Math.max(0, cpclLabelHeight - scaledHeight)));
        canvas.drawBitmap(scaledBitmap, left, top, null);
        return canvasBitmap;
    }

    private Bitmap prepareBitmapForEscPos(Bitmap sourceBitmap) {
        if (sourceBitmap == null) {
            return null;
        }
        sourceBitmap = trimOuterBorder(sourceBitmap);
        sourceBitmap = cleanupLeftGuideArea(sourceBitmap);
        normalizeCalibrationSettings();
        float scale = Math.min(1f, escPosTargetWidth * 1f / sourceBitmap.getWidth());
        int scaledWidth = Math.max(1, Math.round(sourceBitmap.getWidth() * scale));
        int scaledHeight = Math.max(1, Math.round(sourceBitmap.getHeight() * scale));
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(sourceBitmap, scaledWidth, scaledHeight, true);
        Bitmap canvasBitmap = Bitmap.createBitmap(escPosTargetWidth, Math.max(scaledHeight + 24, cpclLabelHeight), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(canvasBitmap);
        canvas.drawColor(Color.WHITE);
        int left = Math.max(20, qrPriorityLeftSafetyMargin / 2);
        if (left + scaledWidth > escPosTargetWidth - 4) {
            left = Math.max(0, escPosTargetWidth - 4 - scaledWidth);
        }
        int top = Math.max(12, (canvasBitmap.getHeight() - scaledHeight) / 2);
        canvas.drawBitmap(scaledBitmap, left, top, null);
        return canvasBitmap;
    }

    private void waitPrinterComplete() throws InterruptedException {
        while (!cancelPrintRequested && ZebraPrinter.GetPrinterState() == 1) {
            Thread.sleep(200);
        }
        ensurePrinterReadyOrThrow();
    }

    private void updatePrinterManagerUi() {
        if (printerTipsTextView == null) {
            return;
        }
        printerTipsTextView.setText(bOpen ? "打印机已连接" : "请连接打印机");
        connectPrinterButton.setEnabled(!bOpen);
        disconnectPrinterButton.setEnabled(bOpen);
        cpclRadioButton.setEnabled(bOpen);
        escPosRadioButton.setEnabled(bOpen);
        printerStatusButton.setEnabled(bOpen);
        printerConfigButton.setEnabled(bOpen);
        printerResetButton.setEnabled(bOpen || !TextUtils.isEmpty(lastPrinterMacAddress));

        printerLanguageRadioGroup.setOnCheckedChangeListener(null);
        if (bLanguageCPCL) {
            printerLanguageRadioGroup.check(R.id.dialog_printer_cpcl_button);
        } else if (bLanguageESCPOS) {
            printerLanguageRadioGroup.check(R.id.dialog_printer_escpos_button);
        } else {
            printerLanguageRadioGroup.clearCheck();
        }
        printerLanguageRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (!bOpen) {
                return;
            }
            if (checkedId == R.id.dialog_printer_cpcl_button && !bLanguageCPCL) {
                switchPrinterLanguage(true);
            } else if (checkedId == R.id.dialog_printer_escpos_button && !bLanguageESCPOS) {
                switchPrinterLanguage(false);
            }
        });
    }

    private void showProgressDialog(String message) {
        dismissProgressDialog();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void startPrintProgressDialog(String message) {
        cancelPrintRequested = false;
        dismissProgressDialog();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", (dialog, which) -> cancelPrintRequested = true);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }

    private void showImageFromBean(LabelPrintImageBean bean) {
        if (bean == null || TextUtils.isEmpty(bean.getImageBase64())) {
            currentPreviewBitmap = null;
            previewImageView.setImageBitmap(null);
            return;
        }
        byte[] imageBytes = Base64.decode(bean.getImageBase64(), Base64.DEFAULT);
        currentPreviewBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        previewImageView.setImageBitmap(currentPreviewBitmap);
    }

    private void updateResultText(LabelPrintImageBean bean) {
        if (bean == null && currentPreviewBitmap == null) {
            resultTextView.setText("未获取到图片信息");
            return;
        }
        LabelPrintImageBean displayBean = bean;
        if (displayBean == null) {
            displayBean = new LabelPrintImageBean();
            displayBean.setLabelType(selectedOption.code);
            displayBean.setMimeType("image/png");
            if (currentPreviewBitmap != null) {
                displayBean.setWidth(currentPreviewBitmap.getWidth());
                displayBean.setHeight(currentPreviewBitmap.getHeight());
            }
        }
        StringBuilder builder = new StringBuilder();
        builder.append("类型：").append(displayBean.getLabelType()).append("\n");
        if (displayBean.getObjectID() > 0) {
            builder.append("对象ID：").append(displayBean.getObjectID()).append("\n");
        }
        if (!TextUtils.isEmpty(displayBean.getFileName())) {
            builder.append("文件名：").append(displayBean.getFileName()).append("\n");
        }
        builder.append("尺寸：").append(displayBean.getWidth()).append(" x ").append(displayBean.getHeight()).append("\n");
        builder.append("MimeType：").append(displayBean.getMimeType()).append("\n");
        builder.append("打印机状态：").append(bOpen ? "已连接" : "未连接").append("\n");
        builder.append("CPCL纸宽：").append(cpclPaperWidth).append("  标签高：").append(cpclLabelHeight).append("\n");
        builder.append("左右偏移：").append(cpclHorizontalOffset).append("  上下偏移：").append(cpclVerticalOffset).append("\n");
        builder.append("缩放比例：").append(cpclScalePercent).append("%  ESCPOS宽：").append(escPosTargetWidth);
        resultTextView.setText(builder.toString());
    }

    private void loadCalibrationSettings() {
        SharedPreferences preferences = getSharedPreferences(PRINT_PREFS, MODE_PRIVATE);
        cpclPaperWidth = preferences.getInt("cpclPaperWidth", cpclPaperWidth);
        cpclLabelHeight = preferences.getInt("cpclLabelHeight", cpclLabelHeight);
        cpclHorizontalOffset = preferences.getInt("cpclHorizontalOffset", cpclHorizontalOffset);
        cpclVerticalOffset = preferences.getInt("cpclVerticalOffset", cpclVerticalOffset);
        cpclScalePercent = preferences.getInt("cpclScalePercent", cpclScalePercent);
        escPosTargetWidth = preferences.getInt("escPosTargetWidth", escPosTargetWidth);
        testLimitBatchPrint = preferences.getBoolean("testLimitBatchPrint", true);
        lastPrinterMacAddress = preferences.getString("lastPrinterMacAddress", null);
        normalizeCalibrationSettings();
    }

    private void saveCalibrationSettings() {
        getSharedPreferences(PRINT_PREFS, MODE_PRIVATE)
                .edit()
                .putInt("cpclPaperWidth", cpclPaperWidth)
                .putInt("cpclLabelHeight", cpclLabelHeight)
                .putInt("cpclHorizontalOffset", cpclHorizontalOffset)
                .putInt("cpclVerticalOffset", cpclVerticalOffset)
                .putInt("cpclScalePercent", cpclScalePercent)
                .putInt("escPosTargetWidth", escPosTargetWidth)
                .apply();
    }

    private void saveBatchPrintModeSettings() {
        getSharedPreferences(PRINT_PREFS, MODE_PRIVATE)
                .edit()
                .putBoolean("testLimitBatchPrint", testLimitBatchPrint)
                .apply();
    }

    private void saveLastPrinterMacAddress() {
        getSharedPreferences(PRINT_PREFS, MODE_PRIVATE)
                .edit()
                .putString("lastPrinterMacAddress", lastPrinterMacAddress)
                .apply();
    }

    private void normalizeCalibrationSettings() {
        cpclPaperWidth = clamp(cpclPaperWidth, 300, 1000);
        cpclLabelHeight = clamp(cpclLabelHeight, 180, 800);
        cpclHorizontalOffset = clamp(cpclHorizontalOffset, -200, 200);
        cpclVerticalOffset = clamp(cpclVerticalOffset, -200, 200);
        cpclScalePercent = clamp(cpclScalePercent, 60, 130);
        escPosTargetWidth = clamp(escPosTargetWidth, 300, 800);
    }

    private int parseIntOrDefault(String value, int defaultValue) {
        if (TextUtils.isEmpty(value)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private String normalizeWsJsonPayload(String payload) {
        if (TextUtils.isEmpty(payload)) {
            return payload;
        }
        String trimmed = payload.trim();
        if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
            return trimmed;
        }
        try {
            JsonElement jsonElement = new JsonParser().parse(trimmed);
            if (jsonElement != null && jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
                String innerJson = jsonElement.getAsString();
                return TextUtils.isEmpty(innerJson) ? trimmed : innerJson.trim();
            }
        } catch (Exception ignored) {
        }
        return trimmed;
    }

    private Bitmap trimOuterBorder(Bitmap sourceBitmap) {
        if (sourceBitmap == null) {
            return null;
        }
        int trimPx = SOURCE_TRIM_BORDER_PX;
        if (sourceBitmap.getWidth() <= trimPx * 2 + 40 || sourceBitmap.getHeight() <= trimPx * 2 + 40) {
            return sourceBitmap;
        }
        try {
            return Bitmap.createBitmap(
                    sourceBitmap,
                    trimPx,
                    trimPx,
                    sourceBitmap.getWidth() - trimPx * 2,
                    sourceBitmap.getHeight() - trimPx * 2
            );
        } catch (Exception e) {
            Log.w(TAG, "trimOuterBorder failed", e);
            return sourceBitmap;
        }
    }

    private Bitmap cleanupLeftGuideArea(Bitmap sourceBitmap) {
        if (sourceBitmap == null) {
            return null;
        }
        if (sourceBitmap.getWidth() <= SOURCE_LEFT_CLEANUP_PX + 40) {
            return sourceBitmap;
        }
        try {
            Bitmap mutableBitmap = sourceBitmap.copy(Bitmap.Config.ARGB_8888, true);
            if (mutableBitmap == null) {
                return sourceBitmap;
            }
            Canvas canvas = new Canvas(mutableBitmap);
            android.graphics.Paint whitePaint = new android.graphics.Paint();
            whitePaint.setColor(Color.WHITE);
            whitePaint.setStyle(android.graphics.Paint.Style.FILL);
            canvas.drawRect(0, 0, SOURCE_LEFT_CLEANUP_PX, mutableBitmap.getHeight(), whitePaint);
            return mutableBitmap;
        } catch (Exception e) {
            Log.w(TAG, "cleanupLeftGuideArea failed", e);
            return sourceBitmap;
        }
    }

    private final BroadcastReceiver printerConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                if (bOpen) {
                    Toast.makeText(NewGetPrintImageActivity.this, R.string.connect_error, Toast.LENGTH_SHORT).show();
                }
                bOpen = false;
                bLanguageCPCL = false;
                bLanguageESCPOS = false;
                updatePrinterManagerUi();
            }
        }
    };

    private static class LabelTypeOption {
        final String code;
        final String name;

        LabelTypeOption(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }

    private class GetPrintImageAsyncTask extends AsyncTask<String, Void, Void> {
        private final String labelType;
        private final long objectId;
        private WsResult wsResult;
        private LabelPrintImageBean imageBean;

        GetPrintImageAsyncTask(String labelType, long objectId) {
            this.labelType = labelType;
            this.objectId = objectId;
        }

        @Override
        protected Void doInBackground(String... strings) {
            wsResult = WebServiceUtil.opLabelGetPrintImage(labelType, objectId);
            if (wsResult != null && wsResult.getResult() && !TextUtils.isEmpty(wsResult.getInfo())) {
//                String jsonPayload = normalizeWsJsonPayload(wsResult.getInfo());
//                imageBean = JsonUtil.parseJsonToObject(jsonPayload, LabelPrintImageBean.class);
                imageBean = JsonUtil.parseJsonToObject(wsResult.getInfo(), LabelPrintImageBean.class);

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (wsResult == null) {
                CommonUtil.ShowToast(NewGetPrintImageActivity.this, "获取标签图片失败：无返回结果", R.mipmap.warning);
                return;
            }

            if (!wsResult.getResult()) {
                CommonUtil.ShowToast(NewGetPrintImageActivity.this, wsResult.getErrorInfo(), R.mipmap.warning);
                return;
            }

            if (imageBean == null || TextUtils.isEmpty(imageBean.getImageBase64())) {
                CommonUtil.ShowToast(NewGetPrintImageActivity.this, "标签图片解析失败", R.mipmap.warning);
                return;
            }

            showImageFromBean(imageBean);
            updateResultText(imageBean);
            CommonUtil.ShowToast(NewGetPrintImageActivity.this, "标签图片获取成功", R.mipmap.smiley);
        }
    }

    private class GetBatchImageBySMLAsyncTask extends AsyncTask<Long, Void, Void>{
        private WsResult wsResult;
        private SMLPrintBatchParamsBean smlPrintBatchParamsBean;
        @Override
        protected Void doInBackground(Long... longIDs) {
            long smlID = longIDs[0];
            if (smlID > 0){

                wsResult = WebServiceUtil.opGetBatchPrintImageBySMLID(smlID);
                if (wsResult != null && wsResult.getResult() && !TextUtils.isEmpty(wsResult.getInfo())) {
//                    String jsonPayload = normalizeWsJsonPayload(wsResult.getInfo());
//                    smlPrintBatchParamsBean = JsonUtil.parseJsonToObject(jsonPayload, SMLPrintBatchParamsBean.class);

//                    String jsonPayload = normalizeWsJsonPayload(wsResult.getInfo());
                    smlPrintBatchParamsBean = JsonUtil.parseJsonToObject(wsResult.getInfo(), SMLPrintBatchParamsBean.class);
                }
            }else{
//                ToastUtil.showWrongToastShort("该生产记录无效！");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //这里循环执行，根据参数
            if (smlPrintBatchParamsBean != null){
                batchPrintImageBySML(smlPrintBatchParamsBean);
            }else{
                ToastUtil.showWrongToastShort("该生产记录相关的打印信息获取失败！");
            }
        }
    }

    private void batchPrintImageBySML(SMLPrintBatchParamsBean smlPrintBatchParamsBean) {
        if (smlPrintBatchParamsBean == null) {
            ToastUtil.showWrongToastShort("批量打印参数为空！");
            return;
        }
        if (!bOpen) {
            CommonUtil.ShowToast(this, "请先连接打印机", R.mipmap.warning);
            showPrinterManagerDialog();
            return;
        }

        int printerState = ZebraPrinter.GetPrinterState();
        if (printerState == 2 || printerState == 3) {
            Toast.makeText(this, R.string.printer_error_not_print, Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog("正在准备批量打印图片...");
        new Thread(() -> {
            try {
                BatchPrintPrepareResult prepareResult = new BatchPrintPrepareResult();
                appendBatchBitmapsGrouped(prepareResult, smlPrintBatchParamsBean);

                if (prepareResult.bitmapList.isEmpty()) {
                    runOnUiThread(() -> {
                        dismissProgressDialog();
                        resultTextView.setText(buildBatchPrintSummaryText(prepareResult));
                        ToastUtil.showWrongToastShort("没有可打印的标签图片！");
                    });
                    return;
                }

                runOnUiThread(() -> {
                    resultTextView.setText(buildBatchPrintSummaryText(prepareResult));
                    startPrintProgressDialog("正在批量打印 " + prepareResult.bitmapList.size() + " 张标签...");
                });

                if (bLanguageCPCL) {
                    printBitmapByCpclBatch(prepareResult.bitmapList);
                } else if (bLanguageESCPOS) {
                    for (Bitmap bitmap : prepareResult.bitmapList) {
                        if (cancelPrintRequested) {
                            break;
                        }
                        ensurePrinterReadyOrThrow();
                        if (bitmap == null) {
                            continue;
                        }
                        printBitmapByEscPos(bitmap);
                        waitPrinterComplete();
                    }
                } else {
                    throw new IllegalStateException("打印机语言未设置");
                }

                runOnUiThread(() -> {
                    dismissProgressDialog();
                    if (cancelPrintRequested) {
                        CommonUtil.ShowToast(NewGetPrintImageActivity.this,
                                "已取消后续打印，已发出的任务可能仍会继续出纸",
                                R.mipmap.warning);
                    } else {
                        CommonUtil.ShowToast(NewGetPrintImageActivity.this,
                                "批量打印完成，共 " + prepareResult.bitmapList.size() + " 张",
                                R.mipmap.smiley);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "batchPrintImageBySML error", e);
                runOnUiThread(() -> {
                    dismissProgressDialog();
                    CommonUtil.ShowToast(NewGetPrintImageActivity.this,
                            "批量打印失败：" + e.getMessage(),
                            R.mipmap.warning);
                });
            }
        }).start();
    }

    /**
     * 按组追加打印图片：小→中→大 循环。
     *
     * 三种模式：
     * 1) 小中大：smmPerSMT组{ smlPerSMM个小标签 + 1个中标签 } + 1个大标签，循环
     * 2) 小大：全部小标签 + 全部大标签（无中标签时）
     * 3) 小：仅全部小标签
     *
     * 有余数时，剩余标签继续按同样组规则打印。
     */
    private void appendBatchBitmapsGrouped(BatchPrintPrepareResult prepareResult, SMLPrintBatchParamsBean params) {
        List<Integer> smliIds = params.getSMLIIDList() != null ? params.getSMLIIDList() : new ArrayList<Integer>();
        List<Integer> smmIds = params.getSMMIDList() != null ? params.getSMMIDList() : new ArrayList<Integer>();
        List<Integer> smtIds = params.getSMTIDList() != null ? params.getSMTIDList() : new ArrayList<Integer>();

        int smliCount = smliIds.size();
        int smmCount = smmIds.size();
        int smtCount = smtIds.size();

        // 测试模式下的限制
        int smliLimit = testLimitBatchPrint ? TEST_MAX_SMLI_PRINT_COUNT : Integer.MAX_VALUE;
        int smmSmtLimit = testLimitBatchPrint ? 1 : Integer.MAX_VALUE;

        // 维护各列表的当前索引和已打印计数
        int smliIndex = 0, smmIndex = 0, smtIndex = 0;
        int smliPrinted = 0, smmPrinted = 0, smtPrinted = 0;

        if (smmCount == 0 && smtCount == 0) {
            // 模式3：只有小标签
            while (smliIndex < smliCount && smliPrinted < smliLimit) {
                appendBitmapById(prepareResult, "SMLI", smliIds.get(smliIndex++));
                smliPrinted++;
            }
        } else if (smmCount == 0) {
            // 模式2：小 + 大，无中标签 → 先全部小，再全部大
            while (smliIndex < smliCount && smliPrinted < smliLimit) {
                appendBitmapById(prepareResult, "SMLI", smliIds.get(smliIndex++));
                smliPrinted++;
            }
            while (smtIndex < smtCount && smtPrinted < smmSmtLimit) {
                appendBitmapById(prepareResult, "SMT", smtIds.get(smtIndex++));
                smtPrinted++;
            }
        } else {
            // 模式1：小中大 — 全量模式，按组交错打印
            int smlPerSMM = smliCount / smmCount;
            int smmPerSMT = smtCount > 0 ? smmCount / smtCount : 0;

            if (smtCount == 0) {
                // 有中有小但无大：smlPerSMM个小 + 1个中 循环
                while (smmIndex < smmCount && smmPrinted < smmSmtLimit) {
                    for (int i = 0; i < smlPerSMM && smliIndex < smliCount && smliPrinted < smliLimit; i++) {
                        appendBitmapById(prepareResult, "SMLI", smliIds.get(smliIndex++));
                        smliPrinted++;
                    }
                    appendBitmapById(prepareResult, "SMM", smmIds.get(smmIndex++));
                    smmPrinted++;
                }
                while (smliIndex < smliCount && smliPrinted < smliLimit) {
                    appendBitmapById(prepareResult, "SMLI", smliIds.get(smliIndex++));
                    smliPrinted++;
                }
            } else {
                // 全量小中大
                while (smtIndex < smtCount && smtPrinted < smmSmtLimit) {
                    for (int g = 0; g < smmPerSMT && smmIndex < smmCount && smmPrinted < smmSmtLimit; g++) {
                        for (int i = 0; i < smlPerSMM && smliIndex < smliCount && smliPrinted < smliLimit; i++) {
                            appendBitmapById(prepareResult, "SMLI", smliIds.get(smliIndex++));
                            smliPrinted++;
                        }
                        appendBitmapById(prepareResult, "SMM", smmIds.get(smmIndex++));
                        smmPrinted++;
                    }
                    appendBitmapById(prepareResult, "SMT", smtIds.get(smtIndex++));
                    smtPrinted++;
                }
                // 剩余大标签
                while (smtIndex < smtCount && smtPrinted < smmSmtLimit) {
                    appendBitmapById(prepareResult, "SMT", smtIds.get(smtIndex++));
                    smtPrinted++;
                }
                // 剩余中标签+小标签
                while (smmIndex < smmCount && smmPrinted < smmSmtLimit) {
                    for (int i = 0; i < smlPerSMM && smliIndex < smliCount && smliPrinted < smliLimit; i++) {
                        appendBitmapById(prepareResult, "SMLI", smliIds.get(smliIndex++));
                        smliPrinted++;
                    }
                    appendBitmapById(prepareResult, "SMM", smmIds.get(smmIndex++));
                    smmPrinted++;
                }
                // 剩余小标签
                while (smliIndex < smliCount && smliPrinted < smliLimit) {
                    appendBitmapById(prepareResult, "SMLI", smliIds.get(smliIndex++));
                    smliPrinted++;
                }
            }
        }

        // 更新计划数量
        prepareResult.markPlanned("SMLI", Math.min(smliCount, smliLimit));
        if (smmCount > 0) {
            prepareResult.markPlanned("SMM", Math.min(smmCount, smmSmtLimit));
        }
        if (smtCount > 0) {
            prepareResult.markPlanned("SMT", Math.min(smtCount, smmSmtLimit));
        }
    }

    private void appendBitmapById(BatchPrintPrepareResult prepareResult, String labelType, long objectId) {
        if (objectId <= 0) {
            return;
        }
        Bitmap bitmap = fetchLabelBitmap(labelType, objectId);
        if (bitmap != null) {
            prepareResult.bitmapList.add(bitmap);
            prepareResult.markSuccess(labelType);
        } else {
            prepareResult.failures.add(labelType + ":" + objectId);
        }
    }

    private void appendBatchBitmaps(BatchPrintPrepareResult prepareResult, String labelType, List<Integer> idList) {
        if (prepareResult == null || idList == null || idList.isEmpty()) {
            return;
        }
        int maxCount = resolveBatchPrintLimit(labelType, idList.size());
        int printCount = 0;
        for (Integer id : idList) {
            if (id == null || id <= 0) {
                continue;
            }
            Bitmap bitmap = fetchLabelBitmap(labelType, id.longValue());
            if (bitmap != null) {
                prepareResult.bitmapList.add(bitmap);
                printCount++;
                prepareResult.markSuccess(labelType);
            } else {
                prepareResult.failures.add(labelType + ":" + id);
            }
            if (printCount >= maxCount) {
                break;
            }
        }
        prepareResult.markPlanned(labelType, maxCount);
    }

    private int resolveBatchPrintLimit(String labelType, int sourceCount) {
        if (!testLimitBatchPrint) {
            return sourceCount;
        }
        if ("SMLI".equalsIgnoreCase(labelType)) {
            return Math.min(sourceCount, TEST_MAX_SMLI_PRINT_COUNT);
        }
        if ("SMM".equalsIgnoreCase(labelType) || "SMT".equalsIgnoreCase(labelType)) {
            return Math.min(sourceCount, 1);
        }
        return sourceCount;
    }

    private Bitmap fetchLabelBitmap(String labelType, long objectId) {
        WsResult labelResult = WebServiceUtil.opLabelGetPrintImage(labelType, objectId);
        if (labelResult == null || !labelResult.getResult() || TextUtils.isEmpty(labelResult.getInfo())) {
            Log.w(TAG, "fetchLabelBitmap failed. labelType=" + labelType + ", objectId=" + objectId
                    + ", error=" + (labelResult == null ? "null result" : labelResult.getErrorInfo()));
            return null;
        }
        LabelPrintImageBean imageBean = JsonUtil.parseJsonToObject(labelResult.getInfo(), LabelPrintImageBean.class);
        if (imageBean == null || TextUtils.isEmpty(imageBean.getImageBase64())) {
            return null;
        }
        try {
            byte[] imageBytes = Base64.decode(imageBean.getImageBase64(), Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        } catch (Exception e) {
            Log.e(TAG, "decode label bitmap error. labelType=" + labelType + ", objectId=" + objectId, e);
            return null;
        }
    }

    private String buildBatchPrintSummaryText(BatchPrintPrepareResult prepareResult) {
        StringBuilder builder = new StringBuilder();
        builder.append("批量打印准备结果").append("\n");
        builder.append("测试模式：").append(testLimitBatchPrint ? "开启" : "关闭").append("\n");
        builder.append("小标签 计划/成功：").append(prepareResult.plannedSMLI).append("/").append(prepareResult.successSMLI).append("\n");
        builder.append("中标签 计划/成功：").append(prepareResult.plannedSMM).append("/").append(prepareResult.successSMM).append("\n");
        builder.append("大标签 计划/成功：").append(prepareResult.plannedSMT).append("/").append(prepareResult.successSMT).append("\n");
        builder.append("总张数：").append(prepareResult.bitmapList.size());
        if (!prepareResult.failures.isEmpty()) {
            builder.append("\n失败ID：").append(TextUtils.join(" , ", prepareResult.failures));
        }
        return builder.toString();
    }

    private void ensurePrinterReadyOrThrow() {
        if (cancelPrintRequested) {
            throw new IllegalStateException("打印已取消");
        }
        int printerState = ZebraPrinter.GetPrinterState();
        if (printerState == 2) {
            throw new IllegalStateException(getString(R.string.printer_cover_open));
        }
        if (printerState == 3) {
            throw new IllegalStateException(getString(R.string.printer_media_out));
        }
    }

    private static class BatchPrintPrepareResult {
        final List<Bitmap> bitmapList = new ArrayList<>();
        final List<String> failures = new ArrayList<>();
        int plannedSMLI;
        int plannedSMM;
        int plannedSMT;
        int successSMLI;
        int successSMM;
        int successSMT;

        void markPlanned(String labelType, int count) {
            if ("SMLI".equalsIgnoreCase(labelType)) {
                plannedSMLI = count;
            } else if ("SMM".equalsIgnoreCase(labelType)) {
                plannedSMM = count;
            } else if ("SMT".equalsIgnoreCase(labelType)) {
                plannedSMT = count;
            }
        }

        void markSuccess(String labelType) {
            if ("SMLI".equalsIgnoreCase(labelType)) {
                successSMLI++;
            } else if ("SMM".equalsIgnoreCase(labelType)) {
                successSMM++;
            } else if ("SMT".equalsIgnoreCase(labelType)) {
                successSMT++;
            }
        }
    }
}
