package com.chinashb.www.mobileerp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.bean.LabelPrintImageBean;
import com.chinashb.www.mobileerp.bean.SMLPrintBatchParamsBean;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.printer.ZebraPrinter328Plus;
import com.chinashb.www.mobileerp.printer.Zebra328PlusBTHDiscoveryActivity;
import com.chinashb.www.mobileerp.utils.JsonUtil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.zebra.printer.sdk.utils.ZebraPrinterUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Zebra ZR328 Plus 蓝牙/WiFi 打印调试页面。
 */
public class Zebra328PlusDebugActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "Zebra328PlusDebug";
    private static final String ZR328_PREFS = "zebra328_calibration";
    private static final int REQUEST_PERMISSION_CODE = 0x3301;
    private static final int REQUEST_DISCOVER_PRINTER_CODE = 0x3302;
    private static final int TEST_MAX_SMLI_PRINT_COUNT = 5;
    private static final String[] LABEL_TYPE_CODES = {
            "PT", "TC", "TS", "SMT", "SMM", "SMLI"
    };
    private static final String[] LABEL_TYPE_NAMES = {
            "托盘标签", "集装箱标签", "发运单标签",
            "大箱标签", "中箱标签", "小箱标签"
    };

    private RadioButton bluetoothRadioButton;
    private RadioButton wifiRadioButton;
    private EditText addressEditText;
    private EditText portEditText;
    private Button connectButton;
    private Button disconnectButton;
    private Button printTestButton;
    private Button scanAddressButton;
    private Button findPrinterButton;
    private Button calibrationButton;
    private Button calibrationTestButton;
    private Spinner labelTypeSpinner;
    private EditText labelIdEditText;
    private Button fetchLabelButton;
    private Button printLabelButton;
    private Button batchBySmlButton;
    private Button scanSmlButton;
    private CheckBox batchTestModeCheckBox;
    private ImageView labelPreviewImageView;
    private TextView statusTextView;

    private final ZebraPrinter328Plus printer = new ZebraPrinter328Plus();
    private ProgressDialog progressDialog;
    private AlertDialog calibrationDialog;
    private boolean connectAfterPermissionRequest;
    private boolean discoverAfterPermissionRequest;
    private int paperWidth = 576;
    private int labelHeight = 400;
    private int printSpeed = 5;
    private int leftMargin = 0;
    private int postFeed = 120;
    private boolean continuousPaper = true;
    private String selectedLabelType = LABEL_TYPE_CODES[0];
    private Bitmap currentLabelBitmap;
    private boolean batchTestMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zebra_328plus_debug);

        bluetoothRadioButton = findViewById(R.id.rb_bluetooth);
        wifiRadioButton = findViewById(R.id.rb_wifi);
        addressEditText = findViewById(R.id.et_printer_address);
        portEditText = findViewById(R.id.et_printer_port);
        connectButton = findViewById(R.id.btn_connect);
        disconnectButton = findViewById(R.id.btn_disconnect);
        printTestButton = findViewById(R.id.btn_print_test);
        scanAddressButton = findViewById(R.id.btn_scan_address);
        findPrinterButton = findViewById(R.id.btn_find_printer);
        calibrationButton = findViewById(R.id.btn_calibration);
        calibrationTestButton = findViewById(R.id.btn_calibration_test);
        labelTypeSpinner = findViewById(R.id.zebra328_label_type_spinner);
        labelIdEditText = findViewById(R.id.zebra328_label_id_edit_text);
        fetchLabelButton = findViewById(R.id.zebra328_fetch_label_button);
        printLabelButton = findViewById(R.id.zebra328_print_label_button);
        batchBySmlButton = findViewById(R.id.zebra328_batch_by_sml_button);
        scanSmlButton = findViewById(R.id.zebra328_scan_sml_button);
        batchTestModeCheckBox = findViewById(R.id.zebra328_batch_test_mode_check_box);
        labelPreviewImageView = findViewById(R.id.zebra328_label_preview_image_view);
        statusTextView = findViewById(R.id.tv_printer_status);
        loadCalibrationSettings();
        initLabelTypeSpinner();

        connectButton.setOnClickListener(this);
        disconnectButton.setOnClickListener(this);
        printTestButton.setOnClickListener(this);
        scanAddressButton.setOnClickListener(this);
        findPrinterButton.setOnClickListener(this);
        calibrationButton.setOnClickListener(this);
        calibrationTestButton.setOnClickListener(this);
        fetchLabelButton.setOnClickListener(this);
        printLabelButton.setOnClickListener(this);
        batchBySmlButton.setOnClickListener(this);
        scanSmlButton.setOnClickListener(this);
        batchTestModeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            batchTestMode = isChecked;
            saveCalibrationSettings();
        });
        batchTestModeCheckBox.setChecked(batchTestMode);
    }

    @Override
    public void onClick(View v) {
        if (v == connectButton) {
            connect();
        } else if (v == disconnectButton) {
            disconnect();
        } else if (v == printTestButton) {
            printTest();
        } else if (v == scanAddressButton) {
            initiateScan();
        } else if (v == findPrinterButton) {
            findPrinter();
        } else if (v == calibrationButton) {
            showCalibrationDialog();
        } else if (v == calibrationTestButton) {
            printCalibrationLabel();
        } else if (v == fetchLabelButton) {
            fetchLabelPreview();
        } else if (v == printLabelButton) {
            printCurrentLabel();
        } else if (v == batchBySmlButton) {
            batchPrintBySmlId();
        } else if (v == scanSmlButton) {
            new IntentIntegrator(this)
                    .setCaptureActivity(CustomScannerActivity.class)
                    .initiateScan();
        }
    }

    private void initiateScan() {
        new IntentIntegrator(this)
                .setCaptureActivity(CustomScannerActivity.class)
                .initiateScan();
    }

    private void findPrinter() {
        if (!hasBluetoothPermission()) {
            connectAfterPermissionRequest = false;
            discoverAfterPermissionRequest = true;
            requestBluetoothPermission();
            return;
        }
        openPrinterDiscovery();
    }

    private void openPrinterDiscovery() {
        startActivityForResult(
                new Intent(this, Zebra328PlusBTHDiscoveryActivity.class),
                REQUEST_DISCOVER_PRINTER_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && !TextUtils.isEmpty(result.getContents())) {
            handleScanResult(result.getContents());
            return;
        }
        if (requestCode == REQUEST_DISCOVER_PRINTER_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                String address = data.getStringExtra(Zebra328PlusBTHDiscoveryActivity.EXTRA_DEVICE_ADDRESS);
                if (!TextUtils.isEmpty(address)) {
                    bluetoothRadioButton.setChecked(true);
                    addressEditText.setText(address);
                    connect();
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            boolean granted = hasBluetoothPermission();
            if (granted && connectAfterPermissionRequest) {
                connectAfterPermissionRequest = false;
                connect();
            } else if (granted && discoverAfterPermissionRequest) {
                discoverAfterPermissionRequest = false;
                openPrinterDiscovery();
            } else if (!granted) {
                discoverAfterPermissionRequest = false;
                statusTextView.setText("蓝牙权限未全部授予，可手动输入打印机MAC后重试");
                Toast.makeText(this, "蓝牙权限未全部授予，请在系统设置中允许", Toast.LENGTH_LONG).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void handleScanResult(String content) {
        content = content.trim();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "条码解析失败", Toast.LENGTH_SHORT).show();
            return;
        }

        if (content.startsWith("SML/")) {
            try {
                long smlId = Long.parseLong(content.substring(4));
                batchPrintBySmlId(smlId);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "生产记录ID解析失败", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        // 蓝牙 MAC：6 组十六进制，如 00:11:22:33:44:55
        if (content.matches("^[0-9a-fA-F]{2}(:[0-9a-fA-F]{2}){5}$")) {
            bluetoothRadioButton.setChecked(true);
            addressEditText.setText(content.toUpperCase());
            return;
        }

        // WiFi 可能是 IP，也可能带端口：192.168.1.100:6101
        if (content.contains(".")) {
            wifiRadioButton.setChecked(true);
            String ip = content;
            String port = "6101";
            if (content.contains(":")) {
                String[] parts = content.split(":");
                ip = parts[0];
                if (parts.length > 1) {
                    port = parts[1];
                }
            }
            addressEditText.setText(ip);
            portEditText.setText(port);
            return;
        }

        addressEditText.setText(content);
    }

    private void connect() {
        String address = addressEditText.getText().toString().trim();
        if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "请输入蓝牙MAC或WiFi IP", Toast.LENGTH_SHORT).show();
            return;
        }

        if (bluetoothRadioButton.isChecked()) {
            if (!hasBluetoothPermission()) {
                connectAfterPermissionRequest = true;
                requestBluetoothPermission();
                return;
            }
            showProgressDialog("正在连接，请稍后");
            new Thread(() -> {
                printer.close();
                int ret = printer.openBluetooth(address);
                runOnUiThread(() -> showConnectResult(ret));
            }).start();
        } else {
            showProgressDialog("正在连接，请稍后");
            int port = 6101;
            String portText = portEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(portText)) {
                try {
                    port = Integer.parseInt(portText);
                } catch (NumberFormatException ignored) {
                }
            }
            final int finalPort = port;
            new Thread(() -> {
                printer.close();
                int ret = printer.openWifi(address, finalPort);
                runOnUiThread(() -> showConnectResult(ret));
            }).start();
        }
    }

    private void showConnectResult(int ret) {
        dismissProgressDialog();
        if (ret == 0) {
            String handshakeInfo = printer.getHandshakeInfo();
            statusTextView.setText(TextUtils.isEmpty(handshakeInfo)
                    ? "已连接"
                    : "已连接\n" + handshakeInfo);
            Toast.makeText(this, "打印机连接成功", Toast.LENGTH_SHORT).show();
        } else {
            String reason;
            if (ret == -10008) {
                reason = "蓝牙未开启";
            } else if (ret == -10007) {
                reason = "设备不支持蓝牙";
            } else if (ret == -10009) {
                reason = printer.getLastError();
            } else if (ret == -1) {
                reason = TextUtils.isEmpty(printer.getLastError())
                        ? "连接超时或打印机未配对"
                        : printer.getLastError();
            } else {
                reason = "错误码 " + ret;
            }
            statusTextView.setText("连接失败：" + reason);
            Toast.makeText(this, "连接失败：" + reason, Toast.LENGTH_SHORT).show();
        }
    }

    private void disconnect() {
        showProgressDialog("正在断开连接，请稍后");
        new Thread(() -> {
            int ret = printer.close();
            runOnUiThread(() -> {
                dismissProgressDialog();
                statusTextView.setText("未连接");
                Toast.makeText(this, ret == 0 ? "打印机已断开" : "断开失败", Toast.LENGTH_SHORT).show();
            });
        }).start();
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

    private void showCalibrationDialog() {
        View dialogView = LayoutInflater.from(this).inflate(
                R.layout.dialog_zebra328_calibration, null, false);
        EditText paperWidthEditText = dialogView.findViewById(
                R.id.dialog_zebra328_paper_width_edit_text);
        EditText labelHeightEditText = dialogView.findViewById(
                R.id.dialog_zebra328_label_height_edit_text);
        EditText printSpeedEditText = dialogView.findViewById(
                R.id.dialog_zebra328_print_speed_edit_text);
        EditText leftMarginEditText = dialogView.findViewById(
                R.id.dialog_zebra328_left_margin_edit_text);
        EditText postFeedEditText = dialogView.findViewById(
                R.id.dialog_zebra328_post_feed_edit_text);
        CheckBox continuousPaperCheckBox = dialogView.findViewById(
                R.id.dialog_zebra328_continuous_paper_check_box);

        paperWidthEditText.setText(String.valueOf(paperWidth));
        labelHeightEditText.setText(String.valueOf(labelHeight));
        printSpeedEditText.setText(String.valueOf(printSpeed));
        leftMarginEditText.setText(String.valueOf(leftMargin));
        postFeedEditText.setText(String.valueOf(postFeed));
        continuousPaperCheckBox.setChecked(continuousPaper);

        calibrationDialog = new AlertDialog.Builder(this)
                .setTitle("ZR328 纸张校准")
                .setView(dialogView)
                .setNegativeButton("取消", null)
                .setPositiveButton("保存", (dialog, which) -> {
                    paperWidth = parseIntOrDefault(paperWidthEditText.getText().toString(), paperWidth);
                    labelHeight = parseIntOrDefault(labelHeightEditText.getText().toString(), labelHeight);
                    printSpeed = parseIntOrDefault(printSpeedEditText.getText().toString(), printSpeed);
                    leftMargin = parseIntOrDefault(leftMarginEditText.getText().toString(), leftMargin);
                    postFeed = parseIntOrDefault(postFeedEditText.getText().toString(), postFeed);
                    continuousPaper = continuousPaperCheckBox.isChecked();
                    paperWidth = clamp(paperWidth, 200, 1000);
                    labelHeight = clamp(labelHeight, 100, 3000);
                    printSpeed = clamp(printSpeed, 0, 5);
                    leftMargin = clamp(leftMargin, 0, paperWidth / 2);
                    postFeed = clamp(postFeed, 0, 1000);
                    saveCalibrationSettings();
                    updateCalibrationStatus();
                    Toast.makeText(this, "纸张校准参数已保存", Toast.LENGTH_SHORT).show();
                })
                .create();
        calibrationDialog.show();
    }

    private void dismissCalibrationDialog() {
        if (calibrationDialog != null && calibrationDialog.isShowing()) {
            calibrationDialog.dismiss();
        }
        calibrationDialog = null;
    }

    private void loadCalibrationSettings() {
        SharedPreferences preferences = getSharedPreferences(ZR328_PREFS, MODE_PRIVATE);
        paperWidth = preferences.getInt("paperWidth", paperWidth);
        labelHeight = preferences.getInt("labelHeight", labelHeight);
        printSpeed = preferences.getInt("printSpeed", printSpeed);
        leftMargin = preferences.getInt("leftMargin", leftMargin);
        postFeed = preferences.getInt("postFeed", postFeed);
        continuousPaper = preferences.getBoolean("continuousPaper", continuousPaper);
        batchTestMode = preferences.getBoolean("batchTestMode", batchTestMode);
        normalizeCalibrationSettings();
    }

    private void saveCalibrationSettings() {
        getSharedPreferences(ZR328_PREFS, MODE_PRIVATE)
                .edit()
                .putInt("paperWidth", paperWidth)
                .putInt("labelHeight", labelHeight)
                .putInt("printSpeed", printSpeed)
                .putInt("leftMargin", leftMargin)
                .putInt("postFeed", postFeed)
                .putBoolean("continuousPaper", continuousPaper)
                .putBoolean("batchTestMode", batchTestMode)
                .apply();
    }

    private void normalizeCalibrationSettings() {
        paperWidth = clamp(paperWidth, 200, 1000);
        labelHeight = clamp(labelHeight, 100, 3000);
        printSpeed = clamp(printSpeed, 0, 5);
        leftMargin = clamp(leftMargin, 0, paperWidth / 2);
        postFeed = clamp(postFeed, 0, 1000);
    }

    private void updateCalibrationStatus() {
        statusTextView.setText("校准参数 宽=" + paperWidth
                + " 高=" + labelHeight
                + " 速度=" + printSpeed
                + " 左边距=" + leftMargin
                + " 出纸=" + postFeed
                + " 连续纸=" + continuousPaper);
    }

    private void printCalibrationLabel() {
        if (!printer.isConnected()) {
            Toast.makeText(this, "请先连接打印机", Toast.LENGTH_SHORT).show();
            return;
        }
        showProgressDialog("正在打印校准标签，请稍后");
        new Thread(() -> {
            clearPrinterJobs();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int speedRet = printer.cpcl.SetSpeed(printSpeed, out);
            int createRet = printer.cpcl.CreateLabel(0, 200, 200, labelHeight, 1, out);
            int widthRet = printer.cpcl.SetPageWidth(paperWidth, out);
            int boxRet = printer.cpcl.AddBox(0, 0, paperWidth, labelHeight, 2, out);
            int textRet = printer.cpcl.AddText("7", 0, leftMargin + 8, 20, 0,
                    "W=" + paperWidth + " H=" + labelHeight + " S=" + printSpeed, out);
            int postFeedRet = printer.cpcl.PostFeed(postFeed, out);
            int formRet = continuousPaper ? 0 : printer.cpcl.Form(out);
            int printRet = printer.cpcl.Print(out);
            int writeRet = speedRet < 0 || createRet < 0 || widthRet < 0
                    || boxRet < 0 || textRet < 0 || postFeedRet < 0
                    || formRet < 0 || printRet < 0
                    ? -1 : printer.write(out);
            String status = writeRet == 0 ? queryPrinterStatus() : printer.getLastError();
            final int finalWriteRet = writeRet;
            final String finalStatus = status;
            final String detail = "校准指令=" + speedRet + "," + createRet + ","
                    + widthRet + "," + boxRet + "," + textRet + ","
                    + postFeedRet + "," + formRet + "," + printRet
                    + " 发送=" + writeRet
                    + (writeRet == 0 ? " 状态=" + status : "");
            runOnUiThread(() -> {
                dismissProgressDialog();
                statusTextView.setText(detail);
                Toast.makeText(this, finalWriteRet == 0
                        ? "校准标签已发送，状态：" + finalStatus
                        : "校准标签发送失败：" + finalStatus, Toast.LENGTH_LONG).show();
            });
        }).start();
    }

    private int parseIntOrDefault(String value, int defaultValue) {
        if (TextUtils.isEmpty(value)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private void initLabelTypeSpinner() {
        String[] displayArray = new String[LABEL_TYPE_CODES.length];
        for (int i = 0; i < LABEL_TYPE_CODES.length; i++) {
            displayArray[i] = LABEL_TYPE_CODES[i] + " - " + LABEL_TYPE_NAMES[i];
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, displayArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        labelTypeSpinner.setAdapter(adapter);
        labelTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLabelType = LABEL_TYPE_CODES[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedLabelType = LABEL_TYPE_CODES[0];
            }
        });
    }

    private void fetchLabelPreview() {
        long objectId = parseLabelId();
        if (objectId <= 0) {
            return;
        }
        new GetLabelImageAsyncTask(selectedLabelType, objectId).execute();
    }

    private void printCurrentLabel() {
        if (currentLabelBitmap == null) {
            Toast.makeText(this, "请先获取标签图片", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!printer.isConnected()) {
            Toast.makeText(this, "请先连接打印机", Toast.LENGTH_SHORT).show();
            return;
        }
        showProgressDialog("正在打印标签图片，请稍后");
        new Thread(() -> {
            int ret = writeCpclLabelBitmap(currentLabelBitmap);
            String status = ret == 0 ? queryPrinterStatus() : printer.getLastError();
            final int finalRet = ret;
            final String finalStatus = status;
            runOnUiThread(() -> {
                dismissProgressDialog();
                statusTextView.setText("标签图片 发送=" + ret
                        + (ret == 0 ? " 状态=" + status : " 错误=" + status));
                Toast.makeText(this, finalRet == 0
                        ? "标签图片已发送，状态：" + finalStatus
                        : "标签图片发送失败：" + finalStatus, Toast.LENGTH_LONG).show();
            });
        }).start();
    }

    private void batchPrintBySmlId() {
        long smlId = parseLabelId();
        if (smlId > 0) {
            batchPrintBySmlId(smlId);
        }
    }

    private void batchPrintBySmlId(long smlId) {
        if (!printer.isConnected()) {
            Toast.makeText(this, "请先连接打印机", Toast.LENGTH_SHORT).show();
            return;
        }
        new GetBatchImageBySMLAsyncTask().execute(smlId);
    }

    private long parseLabelId() {
        String idText = labelIdEditText.getText().toString().trim();
        if (TextUtils.isEmpty(idText)) {
            Toast.makeText(this, "请输入对象ID或生产记录ID", Toast.LENGTH_SHORT).show();
            return -1;
        }
        try {
            return Long.parseLong(idText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID格式错误", Toast.LENGTH_SHORT).show();
            return -1;
        }
    }

    private void showLabelBitmap(Bitmap bitmap) {
        currentLabelBitmap = bitmap;
        labelPreviewImageView.setImageBitmap(bitmap);
        if (bitmap != null) {
            statusTextView.setText("标签图片尺寸=" + bitmap.getWidth()
                    + " x " + bitmap.getHeight()
                    + "，纸张=" + paperWidth + " x " + labelHeight);
        } else {
            labelPreviewImageView.setImageBitmap(null);
            statusTextView.setText("标签图片解析失败");
        }
    }

    private Bitmap prepareLabelBitmapForZebra328(Bitmap sourceBitmap) {
        if (sourceBitmap == null) {
            return null;
        }
        int contentWidth = Math.max(100, paperWidth - leftMargin * 2);
        int contentHeight = Math.max(100, labelHeight - 20);
        float scale = Math.min(
                contentWidth * 1f / sourceBitmap.getWidth(),
                contentHeight * 1f / sourceBitmap.getHeight());
        scale = Math.min(1f, scale);
        int scaledWidth = Math.max(1, Math.round(sourceBitmap.getWidth() * scale));
        int scaledHeight = Math.max(1, Math.round(sourceBitmap.getHeight() * scale));
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(sourceBitmap, scaledWidth, scaledHeight, true);
        Bitmap canvasBitmap = Bitmap.createBitmap(paperWidth, labelHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(canvasBitmap);
        canvas.drawColor(Color.WHITE);
        int left = leftMargin + (paperWidth - leftMargin * 2 - scaledWidth) / 2;
        int top = Math.max(10, (labelHeight - scaledHeight) / 2);
        canvas.drawBitmap(scaledBitmap, left, top, null);
        return canvasBitmap;
    }

    private int writeCpclLabelBitmap(Bitmap sourceBitmap) {
        clearPrinterJobs();
        Bitmap preparedBitmap = prepareLabelBitmapForZebra328(sourceBitmap);
        if (preparedBitmap == null) {
            return -1;
        }
        byte[] raster = ZebraPrinterUtils.bitmapToBytes(preparedBitmap);
        int widthBytes = (int) Math.ceil(preparedBitmap.getWidth() / 8f);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int speedRet = printer.cpcl.SetSpeed(printSpeed, out);
        int densityRet = printer.cpcl.SetDensity(75, out);
        int createRet = printer.cpcl.CreateLabel(0, 200, 200,
                preparedBitmap.getHeight(), 1, out);
        int widthRet = printer.cpcl.SetPageWidth(preparedBitmap.getWidth(), out);
        int imageRet = printer.cpcl.AddBitmapImage(0, 0, widthBytes,
                preparedBitmap.getHeight(), 0, raster, out);
        int postFeedRet = printer.cpcl.PostFeed(postFeed, out);
        int formRet = continuousPaper ? 0 : printer.cpcl.Form(out);
        int printRet = printer.cpcl.Print(out);
        if (speedRet < 0 || densityRet < 0 || createRet < 0
                || widthRet < 0 || imageRet < 0 || postFeedRet < 0
                || formRet < 0 || printRet < 0) {
            return -1;
        }
        int ret = printer.write(out);
        Log.i(TAG, "label image bytes=" + out.size() + ",write=" + ret
                + ",error=" + printer.getLastError());
        return ret;
    }

    private void clearPrinterJobs() {
        int ret = printer.write("~JA\r\n".getBytes());
        Log.i(TAG, "clear printer jobs write=" + ret
                + ",error=" + printer.getLastError());
        sleepQuietly(350);
    }

    private class GetLabelImageAsyncTask extends AsyncTask<Void, Void, Bitmap> {
        private final String labelType;
        private final long objectId;
        private String errorMessage = "";

        GetLabelImageAsyncTask(String labelType, long objectId) {
            this.labelType = labelType;
            this.objectId = objectId;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            WsResult wsResult = WebServiceUtil.opLabelGetPrintImage(labelType, objectId);
            if (wsResult == null || !wsResult.getResult()) {
                errorMessage = wsResult == null ? "获取标签图片无返回" : wsResult.getErrorInfo();
                return null;
            }
            LabelPrintImageBean bean = JsonUtil.parseJsonToObject(
                    wsResult.getInfo(), LabelPrintImageBean.class);
            if (bean == null || TextUtils.isEmpty(bean.getImageBase64())) {
                errorMessage = "标签图片解析失败";
                return null;
            }
            byte[] imageBytes = Base64.decode(bean.getImageBase64(), Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            showLabelBitmap(bitmap);
            if (bitmap != null) {
                Toast.makeText(Zebra328PlusDebugActivity.this,
                        "标签图片获取成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Zebra328PlusDebugActivity.this,
                        errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

    private class GetBatchImageBySMLAsyncTask extends AsyncTask<Long, Void, SMLPrintBatchParamsBean> {
        private String errorMessage = "";

        @Override
        protected SMLPrintBatchParamsBean doInBackground(Long... ids) {
            WsResult wsResult = WebServiceUtil.opGetBatchPrintImageBySMLID(ids[0]);
            if (wsResult == null || !wsResult.getResult()
                    || TextUtils.isEmpty(wsResult.getInfo())) {
                errorMessage = wsResult == null ? "批量打印信息无返回" : wsResult.getErrorInfo();
                return null;
            }
            return JsonUtil.parseJsonToObject(wsResult.getInfo(), SMLPrintBatchParamsBean.class);
        }

        @Override
        protected void onPostExecute(SMLPrintBatchParamsBean bean) {
            if (bean == null) {
                Toast.makeText(Zebra328PlusDebugActivity.this,
                        errorMessage, Toast.LENGTH_LONG).show();
                return;
            }
            showProgressDialog("正在准备批量标签图片...");
            new Thread(() -> printSmlBatch(bean)).start();
        }
    }

    private void printSmlBatch(SMLPrintBatchParamsBean bean) {
        final List<Bitmap> bitmaps = new ArrayList<>();
        int smliLimit = batchTestMode ? TEST_MAX_SMLI_PRINT_COUNT : Integer.MAX_VALUE;
        int smmSmtLimit = batchTestMode ? 1 : Integer.MAX_VALUE;

        List<Integer> smliIds = bean.getSMLIIDList() == null
                ? new ArrayList<Integer>() : bean.getSMLIIDList();
        List<Integer> smmIds = bean.getSMMIDList() == null
                ? new ArrayList<Integer>() : bean.getSMMIDList();
        List<Integer> smtIds = bean.getSMTIDList() == null
                ? new ArrayList<Integer>() : bean.getSMTIDList();

        for (int i = 0; i < smliIds.size() && bitmaps.size() < smliLimit; i++) {
            Bitmap bitmap = fetchLabelBitmap("SMLI", smliIds.get(i).longValue());
            if (bitmap != null) {
                bitmaps.add(bitmap);
            }
        }
        for (int i = 0; i < smmIds.size() && i < smmSmtLimit; i++) {
            Bitmap bitmap = fetchLabelBitmap("SMM", smmIds.get(i).longValue());
            if (bitmap != null) {
                bitmaps.add(bitmap);
            }
        }
        for (int i = 0; i < smtIds.size() && i < smmSmtLimit; i++) {
            Bitmap bitmap = fetchLabelBitmap("SMT", smtIds.get(i).longValue());
            if (bitmap != null) {
                bitmaps.add(bitmap);
            }
        }
        if (bitmaps.isEmpty()) {
            runOnUiThread(() -> {
                dismissProgressDialog();
                Toast.makeText(Zebra328PlusDebugActivity.this,
                        "没有可打印的标签图片", Toast.LENGTH_LONG).show();
            });
            return;
        }

        int success = 0;
        String firstError = "";
        for (Bitmap bitmap : bitmaps) {
            int ret = writeCpclLabelBitmap(bitmap);
            if (ret == 0) {
                success++;
                sleepQuietly(600);
            } else {
                firstError = printer.getLastError();
                break;
            }
        }
        final int finalSuccess = success;
        final String finalError = firstError;
        runOnUiThread(() -> {
            dismissProgressDialog();
            statusTextView.setText("批量打印 成功=" + finalSuccess
                    + "/" + bitmaps.size()
                    + (TextUtils.isEmpty(finalError) ? "" : " 错误=" + finalError));
            Toast.makeText(Zebra328PlusDebugActivity.this, finalSuccess == bitmaps.size()
                    ? "批量打印完成：" + finalSuccess + " 张"
                    : "批量打印中断：" + finalError, Toast.LENGTH_LONG).show();
        });
    }

    private Bitmap fetchLabelBitmap(String labelType, long objectId) {
        WsResult wsResult = WebServiceUtil.opLabelGetPrintImage(labelType, objectId);
        if (wsResult == null || !wsResult.getResult()
                || TextUtils.isEmpty(wsResult.getInfo())) {
            return null;
        }
        LabelPrintImageBean bean = JsonUtil.parseJsonToObject(
                wsResult.getInfo(), LabelPrintImageBean.class);
        if (bean == null || TextUtils.isEmpty(bean.getImageBase64())) {
            return null;
        }
        byte[] bytes = Base64.decode(bean.getImageBase64(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissCalibrationDialog();
        new Thread(() -> printer.close()).start();
        dismissProgressDialog();
    }

    private void printTest() {
        if (!printer.isConnected()) {
            Toast.makeText(this, "请先连接打印机", Toast.LENGTH_SHORT).show();
            return;
        }
        final String address = addressEditText.getText().toString().trim();
        showProgressDialog("正在发送打印测试，请稍后");
        new Thread(() -> {
            int firstRet = trySendLineMode();
            String firstMode = "直接CPCL";
            String firstError = printer.getLastError();
            String firstPrinterStatus = "";
            int ret = firstRet;
            String usedMode = firstMode;
            String secondMode = "";
            int secondRet = -1;
            String secondError = "";
            String secondPrinterStatus = "";
            String thirdMode = "";
            int thirdRet = -1;
            String thirdPrinterStatus = "";

            if (firstRet == 0) {
                firstPrinterStatus = queryPrinterStatus();
                ret = "就绪".equals(firstPrinterStatus) ? 0 : -2;
            }

            boolean needsLanguageSwitch = firstRet != 0
                    || !"就绪".equals(firstPrinterStatus);
            if (needsLanguageSwitch) {
                printer.close();
                sleepQuietly(800);
                int openRet = printer.openBluetooth(address);
                if (openRet == 0) {
                    int languageRet = printer.sgdSet("device.languages", "line_print");
                    String languageError = printer.getLastError();
                    Log.i(TAG, "language switch send=" + languageRet
                            + ",error=" + languageError);
                    printer.close();
                    sleepQuietly(4000);
                    int reopenRet = printer.openBluetooth(address);
                    if (reopenRet == 0) {
                        ret = trySendLineMode();
                        if (ret == 0) {
                            secondPrinterStatus = queryPrinterStatus();
                            ret = "就绪".equals(secondPrinterStatus) ? 0 : -2;
                        }
                        usedMode = "切换line_print后等待重启再CPCL";
                    } else {
                        ret = reopenRet;
                        usedMode = "等待重启后重连失败";
                    }
                } else {
                    ret = openRet;
                    usedMode = "重连失败";
                }
                secondMode = usedMode;
                secondRet = ret;
                secondError = printer.getLastError();
            }

            if (ret != 0 && !TextUtils.isEmpty(address)) {
                printer.close();
                sleepQuietly(5000);
                int openRet = printer.openBluetooth(address);
                if (openRet == 0) {
                    thirdRet = trySendLineMode();
                    if (thirdRet == 0) {
                        thirdPrinterStatus = queryPrinterStatus();
                        thirdRet = "就绪".equals(thirdPrinterStatus) ? 0 : -2;
                    }
                    thirdMode = "继续等待后再次CPCL";
                    ret = thirdRet;
                } else {
                    thirdRet = openRet;
                    thirdMode = "再次重连失败";
                }
            }

            String detail = "第1次：" + firstMode + "=" + firstRet
                    + (TextUtils.isEmpty(firstPrinterStatus)
                        ? (TextUtils.isEmpty(firstError) ? "" : "，" + firstError)
                        : "，打印机状态=" + firstPrinterStatus)
                    + (TextUtils.isEmpty(secondMode) ? "" :
                    "\n第2次：" + secondMode + "=" + secondRet
                            + (TextUtils.isEmpty(secondPrinterStatus)
                                ? (TextUtils.isEmpty(secondError) ? "" : "，" + secondError)
                                : "，打印机状态=" + secondPrinterStatus))
                    + (TextUtils.isEmpty(thirdMode) ? "" :
                    "\n第3次：" + thirdMode + "=" + thirdRet
                            + (TextUtils.isEmpty(thirdPrinterStatus)
                                ? (thirdRet == 0 ? "" : "，" + printer.getLastError())
                                : "，打印机状态=" + thirdPrinterStatus));
            final int finalRet = ret;
            final String latestPrinterStatus = !TextUtils.isEmpty(thirdPrinterStatus)
                    ? thirdPrinterStatus
                    : !TextUtils.isEmpty(secondPrinterStatus)
                        ? secondPrinterStatus
                        : firstPrinterStatus;
            final String finalError = ret == -2
                    ? "打印机状态：" + latestPrinterStatus
                    : printer.getLastError();
            final String finalDetail = detail;
            runOnUiThread(() -> {
                dismissProgressDialog();
                statusTextView.setText(finalDetail);
                Toast.makeText(this, finalRet == 0 ? "已发送打印" : "打印发送失败：" + finalError,
                        Toast.LENGTH_LONG).show();
            });
        }).start();
    }

    private String queryPrinterStatus() {
        int writeRet = printer.write(new byte[]{0x1B, 'h'});
        Log.i(TAG, "status query write=" + writeRet + ",error=" + printer.getLastError());
        if (writeRet != 0) {
            return "查询写入失败：" + printer.getLastError();
        }
        sleepQuietly(250);
        byte[] response = printer.read(1000);
        if (response == null || response.length < 3) {
            return "无响应";
        }
        int statusByte = response[response.length - 1] & 0xFF;
        Log.i(TAG, "status byte=" + statusByte);
        if ((statusByte & 0x04) != 0) {
            return "缺纸";
        }
        if ((statusByte & 0x02) != 0) {
            return "纸仓或盖子打开";
        }
        return "就绪";
    }

    private int trySendLineMode() {
        clearPrinterJobs();
        ByteArrayOutputStream init = new ByteArrayOutputStream();
        int speedRet = printer.cpcl.SetSpeed(printSpeed, init);
        int marginRet = printer.cpcl.SetLeftMargin4LineMode(leftMargin, init);
        int fontRet = printer.cpcl.SetFont4LineMode("7", 0, 24, init);
        if (speedRet < 0 || marginRet < 0 || fontRet < 0) {
            return -1;
        }

        int initRet = printer.write(init);
        Log.i(TAG, "CPCL init bytes=" + init.size() + ",write=" + initRet
                + ",error=" + printer.getLastError());
        if (initRet != 0) {
            Log.e(TAG, "CPCL init failed,error=" + printer.getLastError());
            return initRet;
        }
        sleepQuietly(150);

        int text1Ret = printer.write("Zebra ZR328 Plus Test\r\n".getBytes());
        if (text1Ret != 0) {
            Log.e(TAG, "text1 failed,error=" + printer.getLastError());
            return text1Ret;
        }
        sleepQuietly(150);

        int text2Ret = printer.write("1234567890\r\n".getBytes());
        if (text2Ret != 0) {
            Log.e(TAG, "text2 failed,error=" + printer.getLastError());
            return text2Ret;
        }
        return 0;
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean hasBluetoothPermission() {
        boolean granted = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (Build.VERSION.SDK_INT >= 31) {
            granted &= ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH_CONNECT") == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH_SCAN") == PackageManager.PERMISSION_GRANTED;
        }
        return granted;
    }

    private void requestBluetoothPermission() {
        ArrayList<String> permissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.BLUETOOTH);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.BLUETOOTH_ADMIN);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (Build.VERSION.SDK_INT >= 31) {
            if (ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH_CONNECT") != PackageManager.PERMISSION_GRANTED) {
                permissions.add("android.permission.BLUETOOTH_CONNECT");
            }
            if (ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH_SCAN") != PackageManager.PERMISSION_GRANTED) {
                permissions.add("android.permission.BLUETOOTH_SCAN");
            }
        }
        if (!permissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), REQUEST_PERMISSION_CODE);
        }
    }
}
