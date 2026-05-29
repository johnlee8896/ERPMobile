package com.chinashb.www.mobileerp;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.LabelPrintImageBean;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.printer.BTHDiscoveryActivity;
import com.chinashb.www.mobileerp.utils.JsonUtil;
import com.zebra.printer.sdk.ZebraPrinter;

import java.util.ArrayList;

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

    private LabelTypeOption selectedOption = LABEL_TYPE_OPTIONS[0];
    private Bitmap currentPreviewBitmap;
    private static boolean bOpen = false;
    private static boolean bLanguageCPCL = false;
    private static boolean bLanguageESCPOS = false;
    private int cpclPaperWidth = 640;
    private int cpclLabelHeight = 360;
    private int cpclHorizontalOffset = 0;
    private int cpclVerticalOffset = 0;
    private int cpclScalePercent = 100;
    private int escPosTargetWidth = 560;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_get_print_image);
        ButterKnife.bind(this);
        loadCalibrationSettings();
        initTypeSpinner();
        checkPermission();
        requestPermission();
        registerPrinterDisconnectReceiver();
        ZebraPrinter.SetLog(3);
    }

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

            connectPrinterButton.setOnClickListener(v -> openPrinterDiscovery());
            disconnectPrinterButton.setOnClickListener(v -> disconnectPrinter());
            printerStatusButton.setOnClickListener(v -> queryPrinterStatus());
            printerConfigButton.setOnClickListener(v -> printConfigurationPage());
            printerCalibrationButton.setOnClickListener(v -> showCalibrationDialog());
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

        showProgressDialog(getString(R.string.printing));
        new Thread(() -> {
            try {
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
                    CommonUtil.ShowToast(NewGetPrintImageActivity.this, "打印完成", R.mipmap.smiley);
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

    private void printBitmapByEscPos(Bitmap bitmap) {
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
        while (ZebraPrinter.GetPrinterState() == 1) {
            Thread.sleep(200);
        }
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
}
