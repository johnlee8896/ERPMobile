package com.chinashb.www.mobileerp.printer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.zebra.printer.sdk.ZebraPrinter;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/***
 * @date 创建时间 2024/7/4 3:45 PM
 * @author 作者: liweifeng
 * @description
 */
public class MobilePrinterActivity extends BaseActivity {
    private static final String TAG = "MobilePrinterActivity";

    private final String TextStr = "中华人民共和国";

    private final String textStrUTF8 = "中华人民共和国123abAB UTF8";
    private final String textStrGB18030 = "中华人民共和国123abAB GB18030";

//    private final int pageWidth = 576;
    private final int pageWidth = 1600;

    private static boolean bOpen = false;
    private static boolean bLanguageCPCL = false;
    private static boolean bLanguageESCPOS = false;

    private TextView textTips = null;

    private Button btnConnect = null;
    private Button btnDisConnect = null;

    private RadioGroup languageRadioGroup = null;
    private RadioButton btnCPCL = null;
    private RadioButton btnESCPOS = null;

    private Button btnConfigurationPrint = null;

    private Button btnGetPrinterStatus = null;
    private Button btnPrintText = null;
    private Button btnPrint1DBarcode = null;
    private Button btnPrint2DBarcode = null;
    private Button btnPrintImage = null;
    private Button btnPrintImageZoom = null;
    private Button btnPrintLabel = null;
    private Button btnPrintReceipt = null;
    private ImageView logoImageView;


    private ProgressDialog progressDialog = null;
    private boolean isCurrentPictureZoom = false;

    /**
     * 权限请求码
     */
    private static final int REQUEST_PERMISSION_CODE = 0x001;

    /**
     * 蓝牙连接请求码
     */
    public static final int REQUEST_BLUETOOTH_CONNECT_CODE = 0x002;

    /**
     * 蓝牙所需权限
     */
    private final String[] usePermissions = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    /**
     * 未授予的权限
     */
    private final ArrayList<String> requestPermissions = new ArrayList<>();

    private int boxID;

    private void checkPermission() {
        for (String permission : usePermissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, permission)) {
                requestPermissions.add(permission);
            }
        }
    }

    private void requestPermission() {
        if (requestPermissions.size() > 0) {
            String[] sPermission = new String[requestPermissions.size()];
            ActivityCompat.requestPermissions(this, requestPermissions.toArray(sPermission), REQUEST_PERMISSION_CODE);
        }
    }

    private String getVersionName() throws PackageManager.NameNotFoundException {
        PackageManager packageManager = getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(),0);

        return packageInfo.versionName;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: Enter");
        setContentView(R.layout.activity_mobile_print_layout);

        boxID = 816847;

        String versionString = "";

        try {
            versionString = getVersionName();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        setTitle("ZebraPrinterSDK Demo " + versionString);

        textTips = findViewById(R.id.textTips);

        btnConnect    = findViewById(R.id.btnConnect);
        btnDisConnect = findViewById(R.id.btnDisconnect);

        languageRadioGroup = findViewById(R.id.languageRadioGroup);
        btnCPCL   = findViewById(R.id.btnCPCL);
        btnESCPOS = findViewById(R.id.btnESCPOS);

        btnConfigurationPrint = findViewById(R.id.btnConfigurationPrint);
        btnGetPrinterStatus = findViewById(R.id.btnGetPrinterStatus);

        btnConnect.setEnabled(true);
        btnDisConnect.setEnabled(false);
        btnCPCL.setEnabled(false);
        btnESCPOS.setEnabled(false);
        btnConfigurationPrint.setEnabled(false);
        btnGetPrinterStatus.setEnabled(false);

        languageRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.btnCPCL:
                        Log.i(TAG, "onCheckedChanged: " + btnCPCL.getText().toString());
                        break;
                    case R.id.btnESCPOS:
                        Log.i(TAG, "onCheckedChanged: " + btnESCPOS.getText().toString());
                        break;
                    default:
                        Log.i(TAG, "onCheckedChanged: other");
                        break;
                }

                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            final int printerState;
                            StringBuffer strPrinterLanguages = new StringBuffer();
                            switch (checkedId) {
                                case R.id.btnCPCL:
                                    if (btnCPCL.isChecked() && (!bLanguageCPCL && bLanguageESCPOS)) {

                                        Log.i(TAG, "language: CPCL");
                                        printerState = ZebraPrinter.GetPrinterState();

                                        if (1 == printerState) {
                                            runOnUiThread(() -> {
                                                languageRadioGroup.clearCheck();
                                                languageRadioGroup.check(R.id.btnESCPOS);
                                                Toast.makeText(MobilePrinterActivity.this, R.string.language_switched_error, Toast.LENGTH_SHORT).show();
                                            });

                                        } else {

                                            runOnUiThread(() -> {
                                                progressDialog = new ProgressDialog(MobilePrinterActivity.this);
                                                progressDialog.setMessage(getString(R.string.language_switched_to_cpcl));
                                                progressDialog.show();
                                            });

                                            ZebraPrinter.SGD_SetVar("device.languages", "line_print");

                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                            ZebraPrinter.SGD_GetVar("device.languages", strPrinterLanguages);

                                            runOnUiThread(() -> {
                                                if (strPrinterLanguages.toString().contains("line_print")) {
                                                    bLanguageCPCL = true;
                                                    bLanguageESCPOS = false;
                                                    progressDialog.dismiss();
                                                    btnPrintText.setVisibility(View.VISIBLE);
                                                    btnPrint1DBarcode.setVisibility(View.VISIBLE);
                                                    btnPrint2DBarcode.setVisibility(View.VISIBLE);
                                                    btnPrintImage.setVisibility(View.VISIBLE);
                                                    btnPrintLabel.setVisibility(View.VISIBLE);
                                                    btnPrintReceipt.setVisibility(View.GONE);
                                                } else {
                                                    bLanguageCPCL = false;
                                                    bLanguageESCPOS = true;
                                                    progressDialog.dismiss();
                                                    languageRadioGroup.clearCheck();
                                                    languageRadioGroup.check(R.id.btnESCPOS);
                                                    Toast.makeText(MobilePrinterActivity.this, R.string.not_support_cpcl, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                    break;
                                case R.id.btnESCPOS:
                                    if (btnESCPOS.isChecked() && (bLanguageCPCL && !bLanguageESCPOS)) {

                                        Log.i(TAG, "language: ESCPOS");

                                        printerState = ZebraPrinter.GetPrinterState();

                                        if (1 == printerState) {
                                            runOnUiThread(() -> {
                                                languageRadioGroup.clearCheck();
                                                languageRadioGroup.check(R.id.btnCPCL);
                                                Toast.makeText(MobilePrinterActivity.this, R.string.language_switched_error, Toast.LENGTH_SHORT).show();
                                            });

                                        } else {

                                            runOnUiThread(() -> {
                                                progressDialog = new ProgressDialog(MobilePrinterActivity.this);
                                                progressDialog.setMessage(getString(R.string.language_switched_to_escpos));
                                                progressDialog.show();
                                            });

                                            ZebraPrinter.SGD_SetVar("device.languages", "esc_pos");

                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                            ZebraPrinter.SGD_GetVar("device.languages", strPrinterLanguages);
                                            runOnUiThread(() -> {
                                                if (strPrinterLanguages.toString().contains("esc_pos")) {
                                                    bLanguageESCPOS = true;
                                                    bLanguageCPCL = false;
                                                    progressDialog.dismiss();
                                                    btnPrintText.setVisibility(View.VISIBLE);
                                                    btnPrint1DBarcode.setVisibility(View.VISIBLE);
                                                    btnPrint2DBarcode.setVisibility(View.VISIBLE);
                                                    btnPrintImage.setVisibility(View.VISIBLE);
                                                    btnPrintLabel.setVisibility(View.GONE);
                                                    btnPrintReceipt.setVisibility(View.VISIBLE);
                                                } else {
                                                    bLanguageESCPOS = false;
                                                    bLanguageCPCL   = true;
                                                    progressDialog.dismiss();
                                                    languageRadioGroup.clearCheck();
                                                    languageRadioGroup.check(R.id.btnCPCL);
                                                    Toast.makeText(MobilePrinterActivity.this, R.string.not_support_escpos, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                    break;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }.start();
            }
        });

        btnPrintText        = findViewById(R.id.btnPrintText);
        btnPrint1DBarcode   = findViewById(R.id.btnPrint1DBarcode);
        btnPrint2DBarcode   = findViewById(R.id.btnPrint2DBarcode);
        btnPrintImage       = findViewById(R.id.btnPrintImage);
        btnPrintImageZoom = findViewById(R.id.btnPrintImage_zoom);
        btnPrintLabel       = findViewById(R.id.btnPrintLabel);
        btnPrintReceipt     = findViewById(R.id.btnPrintReceipt);
        logoImageView = findViewById(R.id.logo_iamgeView);

        btnPrintText.setVisibility(View.INVISIBLE);
        btnPrint1DBarcode.setVisibility(View.INVISIBLE);
        btnPrint2DBarcode.setVisibility(View.INVISIBLE);
        btnPrintImage.setVisibility(View.INVISIBLE);
        btnPrintLabel.setVisibility(View.INVISIBLE);
        btnPrintReceipt.setVisibility(View.VISIBLE);

        checkPermission();
        requestPermission();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(mPrinterConnectReceiver, intentFilter);

        ZebraPrinter.SetLog(3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: Enter");
        try {
            ZebraPrinter.Close();
            bOpen = false;
            bLanguageCPCL = false;
            bLanguageESCPOS = false;
            ZebraPrinter.SetLog(0);
            unregisterReceiver(mPrinterConnectReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: Enter");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: enter");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: enter");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: enter");
    }

    /**
     * 接收连接状态的广播
     */
    private final BroadcastReceiver mPrinterConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, @NotNull Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                Log.i(TAG, "Activity_Main --> mPrinterConnectReceiver ACTION_ACL_DISCONNECTED");

                if (bOpen) {
                    Toast.makeText(MobilePrinterActivity.this, R.string.connect_error, Toast.LENGTH_SHORT).show();
                    bOpen = false;
                }

                textTips.setText(R.string.please_connect_printer);
                btnConnect.setEnabled(true);
                btnDisConnect.setEnabled(false);
                btnCPCL.setEnabled(false);
                btnESCPOS.setEnabled(false);
                btnConfigurationPrint.setEnabled(false);
                btnGetPrinterStatus.setEnabled(false);

                languageRadioGroup.clearCheck();

                btnPrintText.setVisibility(View.INVISIBLE);
                btnPrint1DBarcode.setVisibility(View.INVISIBLE);
                btnPrint2DBarcode.setVisibility(View.INVISIBLE);
                btnPrintImage.setVisibility(View.INVISIBLE);
                btnPrintLabel.setVisibility(View.INVISIBLE);
                btnPrintReceipt.setVisibility(View.VISIBLE);
            }
        }
    };


    /**
     * 连接请求返回处理函数
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (RESULT_OK == resultCode) {
            if (REQUEST_BLUETOOTH_CONNECT_CODE == requestCode) {
                String printerMACAddress = intent.getStringExtra(BTHDiscoveryActivity.EXTRA_DEVICE_ADDRESS);

                bLanguageCPCL = false;
                bLanguageESCPOS = false;

                final ProgressDialog progressDialog = new ProgressDialog(MobilePrinterActivity.this);
                progressDialog.setMessage(getString(R.string.connecting));
                progressDialog.show();

                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            StringBuffer strPrinterLanguages = new StringBuffer();
                            ZebraPrinter.Close();
                            bOpen = false;
                            final int result = ZebraPrinter.Open(0, printerMACAddress);
                            runOnUiThread(() -> {
                                if (ZebraPrinter.ZEBRA_E_SUCCESS == result) {
                                    bOpen = true;
                                    textTips.setText(R.string.connect_success);
                                    btnConnect.setEnabled(false);
                                    btnDisConnect.setEnabled(true);
                                    btnCPCL.setEnabled(true);
                                    btnESCPOS.setEnabled(true);
                                    btnConfigurationPrint.setEnabled(true);
                                    btnGetPrinterStatus.setEnabled(true);

                                    btnPrintText.setVisibility(View.VISIBLE);
                                    btnPrint1DBarcode.setVisibility(View.VISIBLE);
                                    btnPrint2DBarcode.setVisibility(View.VISIBLE);
                                    btnPrintImage.setVisibility(View.VISIBLE);
                                    ZebraPrinter.SGD_GetVar("device.languages", strPrinterLanguages);
                                    if (strPrinterLanguages.toString().equals("line_print")) {
                                        bLanguageCPCL = true;
                                        bLanguageESCPOS = false;
                                        btnPrintLabel.setVisibility(View.VISIBLE);
                                        btnPrintReceipt.setVisibility(View.VISIBLE);
                                        languageRadioGroup.check(R.id.btnCPCL);
                                    } else if (strPrinterLanguages.toString().equals("esc_pos")) {
                                        bLanguageCPCL = false;
                                        bLanguageESCPOS = true;
                                        btnPrintLabel.setVisibility(View.GONE);
                                        btnPrintReceipt.setVisibility(View.VISIBLE);
                                        languageRadioGroup.check(R.id.btnESCPOS);
                                    }
                                } else {
                                    if (ZebraPrinter.ZEBRA_E_INVALID_MODEL == result) {
                                        Toast.makeText(MobilePrinterActivity.this, R.string.not_support_printer, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MobilePrinterActivity.this, R.string.connect_failed, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            progressDialog.dismiss();
                        } catch (Exception e) {
                            progressDialog.dismiss();
                        }
                    }
                }.start();
            }
        }
    }

    /**
     * 连接处理函数
     */
    public void onBtnConnect(View view) {
        try {
            if (view.getId() == R.id.btnConnect) {
                Intent intent = new Intent(this, BTHDiscoveryActivity.class);
                startActivityForResult(intent, REQUEST_BLUETOOTH_CONNECT_CODE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Activity_Main --> onBtnConnect " + e.getMessage());
        }
    }

    public void onBtnDisconnect(View view) {
        try {

            final ProgressDialog progressDialog = new ProgressDialog(MobilePrinterActivity.this);
            progressDialog.setMessage(getString(R.string.disconnecting));
            progressDialog.show();

            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {

                        bOpen = false;
                        final int result = ZebraPrinter.Close();

                        runOnUiThread(() -> {
                            if (ZebraPrinter.ZEBRA_E_SUCCESS == result) {
                                bLanguageCPCL = false;
                                bLanguageESCPOS = false;
                                textTips.setText(R.string.please_connect_printer);
                                btnConnect.setEnabled(true);
                                btnDisConnect.setEnabled(false);
                                btnCPCL.setEnabled(false);
                                btnESCPOS.setEnabled(false);
                                btnConfigurationPrint.setEnabled(false);
                                btnGetPrinterStatus.setEnabled(false);

                                languageRadioGroup.clearCheck();

                                btnPrintText.setVisibility(View.INVISIBLE);
                                btnPrint1DBarcode.setVisibility(View.INVISIBLE);
                                btnPrint2DBarcode.setVisibility(View.INVISIBLE);
                                btnPrintImage.setVisibility(View.INVISIBLE);
                                btnPrintLabel.setVisibility(View.INVISIBLE);
                                btnPrintReceipt.setVisibility(View.VISIBLE);
                            }
                        });

                        progressDialog.dismiss();
                    } catch (Exception e) {
                        progressDialog.dismiss();
                    }
                }
            }.start();

        } catch (Exception e) {
            Log.e(TAG, "Activity_Main --> onBtnDisconnect " + e.getMessage());
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void onBtnPrintClick(View view) {
        new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                super.run();

                int printerState;

                switch (view.getId()) {

                    case R.id.btnGetPrinterStatus:
                        printerState = ZebraPrinter.GetPrinterState();
                        runOnUiThread(() -> {
                            switch (printerState) {
                                case 0:
                                    Toast.makeText(MobilePrinterActivity.this, R.string.printer_ready, Toast.LENGTH_SHORT).show();
                                    break;
                                case 1:
                                    Toast.makeText(MobilePrinterActivity.this, R.string.printer_busy, Toast.LENGTH_SHORT).show();
                                    break;
                                case 2:
                                    Toast.makeText(MobilePrinterActivity.this, R.string.printer_cover_open, Toast.LENGTH_SHORT).show();
                                    break;
                                case 3:
                                    Toast.makeText(MobilePrinterActivity.this, R.string.printer_media_out, Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        });
                        break;

                    case R.id.btnPrintLabel:
                        Log.i(TAG, "onBtnPrinterClick: btnPrintLabel");

                        printerState = ZebraPrinter.GetPrinterState();
                        if (2 == printerState || 3 == printerState) {
                            runOnUiThread(() -> Toast.makeText(MobilePrinterActivity.this, R.string.printer_error_not_print, Toast.LENGTH_SHORT).show());
                            return;
                        }

                        if (bOpen) {
                            if (bLanguageCPCL) {

                                runOnUiThread(() -> {
                                    progressDialog = new ProgressDialog(MobilePrinterActivity.this);
                                    progressDialog.setMessage(getString(R.string.printing));
                                    progressDialog.show();
                                });

                                ZebraPrinter.CPCL_PrinterInit();
                                ZebraPrinter.CPCL_CreateLabel(0, 1650, 1);
                                ZebraPrinter.CPCL_SetPageWidth(pageWidth);
                                ZebraPrinter.CPCL_SetTextSpace(2);
                                ZebraPrinter.CPCL_SetTextBold(1);
                                ZebraPrinter.CPCL_SetBarcodeText(false, 0, 1,4);
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 169, 0, "寄件方信息：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 420, 169, 0, "自取");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 420, 201, 0, "自寄");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 420, 233, 0, "原寄地");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 287, 0, "收件方信息：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 420, 287, 0, "目的地：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 400, 0, "托寄物品信息：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 294, 400, 0, "附加服务：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 641, 0, "第三方地区：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 585, 0, "付款账号：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 529, 0, "付款方式：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 473, 0, "业务类型：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 294, 641, 0, "费用合计");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 294, 601, 0, "计费重量");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 294, 561, 0, "费用");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 294, 521, 0, "实际重量");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 294, 481, 0, "件数");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 681, 0, "寄方签名：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 176, 681, 0, "收件员：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 294, 681, 0, "收方签名：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 420, 681, 0, "派件员：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 294, 760, 0, "操作日期：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 760, 0, "寄件日期：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 816, 0, "顺丰速运：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 294, 816, 0, "运单号码：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 856, 0, "寄件方信息：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 294, 856, 0, "托寄物品信息：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 982, 0, "收件方信息：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 1126, 0, "付款方式：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 1086, 0, "费用合计：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 1258, 0, "寄件方信息：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 420, 1258, 0, "目的地：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 294, 1430, 0, "附加服务：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 1430, 0, "托寄物品信息：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 1556, 0, "付款账号：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 288, 1558, 0, "费用合计");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 1526, 0, "付款方式：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 288, 1528, 0, "实际重量");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 1496, 0, "业务类型：");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 288, 1498, 0, "件数");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 199, 0, "广东省广州市科学城科学大道181号商业广场");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 223, 0, "A4栋4楼");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 247, 0, "黄兴 13426467689");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 440, 253, 0, "929JL");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 323, 0, "广东省东莞市松山湖科技产业园");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 347, 0, "刘婷婷 15876978978");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 43, 430, 0, "文件，数量 1");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 306, 430, 0, "报价3元，费用7元");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 150, 529, 0, "收方付");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 150, 437, 0, "标准快递");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 430, 641, 0, "13.99");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 430, 601, 0, "1");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 430, 561, 0, "13.99");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 430, 521, 0, "1");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 430, 481, 0, "1");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 107, 762, 0, "2016-01-07 9:09");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 176, 720, 0, "李志英");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 158, 816, 0, "95338");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 400, 816, 0, "033157444802");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 378, 902, 0, "文件");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 378, 926, 0, "数量 1");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 878, 0, "广东省广州市科学城");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 902, 0, "科学大道181号商业广场");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 926, 0, "A4栋4楼");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 950, 0, "黄兴 13426467689");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 158, 1126, 0, "收方付");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 158, 1086, 0, "13.99");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 1020, 0, "广东省东莞市松山湖科技产业园");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 1044, 0, "刘婷婷 15876978978");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 1296, 0, "黄兴 13426467689");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 450, 1296, 0, "769");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 150, 1496, 0, "标准快递");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 150, 1526, 0, "收方付");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 43, 1462, 0, "文件，数量 1");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 306, 1462, 0, "报价3元，费用7元");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 1366, 0, "广东省东莞市松山湖科技产业园");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 30, 1390, 0, "刘婷婷 15876978978");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 430, 1556, 0, "13.99");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 430, 1526, 0, "1");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 430, 1496, 0, "1");
                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 420, 1258, 0, "");
                                //barcode 128
                                ZebraPrinter.CPCL_PrintBarcode(8, 3, 2, 60, 160, 64, 0, "033875444802".getBytes());
                                ZebraPrinter.CPCL_PrintText("7", 0, 192, 130, 0, "033875444802");
                                //2D barcode QR
                                ZebraPrinter.CPCL_PrintQRCode(405, 1010, 2, 5, 1, 0, "顺丰手机客户端".getBytes(StandardCharsets.UTF_8));

                                ZebraPrinter.CPCL_PrintText("GBUNSG16.CPF", 0, 402, 982, 0, "顺丰手机客户端");
                                ZebraPrinter.CPCL_PrintBarcode(8, 3, 2, 30, 160, 1178, 0, "033875444802".getBytes());
                                ZebraPrinter.CPCL_PrintText("7", 0, 192, 1212, 0, "033875444802");
                                //Box
                                ZebraPrinter.CPCL_PrintBox(485, 199, 507, 218, 1);
                                ZebraPrinter.CPCL_PrintBox(485, 168, 505, 188, 1);
                                ZebraPrinter.CPCL_PrintBox(13, 1167, 537, 1581, 1);
                                ZebraPrinter.CPCL_PrintBox(13, 161, 536, 785, 1);
                                ZebraPrinter.CPCL_PrintBox(13, 805, 536, 1146, 1);
                                //Line
                                ZebraPrinter.CPCL_PrintLine(2, 45, 575, 45, 1, false);
                                ZebraPrinter.CPCL_PrintLine(16, 279, 533, 279, 1, false);
                                ZebraPrinter.CPCL_PrintLine(143, 672, 143, 751, 1, false);
                                ZebraPrinter.CPCL_PrintLine(397, 164, 397, 395, 1, false);
                                ZebraPrinter.CPCL_PrintLine(255, 396, 255, 782, 1, false);
                                ZebraPrinter.CPCL_PrintLine(16, 394, 537, 394, 1, false);
                                ZebraPrinter.CPCL_PrintLine(398, 226, 538, 226, 1, false);
                                ZebraPrinter.CPCL_PrintLine(16, 464, 533, 464, 1, false);
                                ZebraPrinter.CPCL_PrintLine(17, 674, 537, 674, 1, false);
                                ZebraPrinter.CPCL_PrintLine(256, 634, 534, 634, 1, false);
                                ZebraPrinter.CPCL_PrintLine(257, 591, 537, 591, 1, false);
                                ZebraPrinter.CPCL_PrintLine(255, 505, 534, 505, 1, false);
                                ZebraPrinter.CPCL_PrintLine(255, 550, 533, 550, 1, false);
                                ZebraPrinter.CPCL_PrintLine(16, 751, 535, 751, 1, false);
                                ZebraPrinter.CPCL_PrintLine(14, 843, 533, 843, 1, false);
                                ZebraPrinter.CPCL_PrintLine(255, 807, 255, 974, 1, false);
                                ZebraPrinter.CPCL_PrintLine(14, 976, 534, 976, 1, false);
                                ZebraPrinter.CPCL_PrintLine(15, 1067, 394, 1067, 1, false);
                                ZebraPrinter.CPCL_PrintLine(14, 1108, 394, 1108, 1, false);
                                ZebraPrinter.CPCL_PrintLine(397, 976, 397, 1149, 1, false);
                                ZebraPrinter.CPCL_PrintLine(16, 1253, 539, 1253, 1, false);
                                ZebraPrinter.CPCL_PrintLine(15, 1324, 538, 1324, 1, false);
                                ZebraPrinter.CPCL_PrintLine(400, 1257, 400, 1324, 1, false);
                                ZebraPrinter.CPCL_PrintLine(15, 1425, 533, 1425, 1, false);
                                ZebraPrinter.CPCL_PrintLine(263, 1430, 263, 1581, 1, false);
                                ZebraPrinter.CPCL_PrintLine(16, 1487, 534, 1487, 1, false);
                                ZebraPrinter.CPCL_PrintLine(264, 1550, 538, 1550, 1, false);
                                ZebraPrinter.CPCL_PrintLine(266, 1519, 534, 1519, 1, false);
                                ZebraPrinter.CPCL_PrintLine(404, 1489, 404, 1581, 1, false);
                                ZebraPrinter.CPCL_PrintLine(399, 464, 399, 750, 1, false);
                                ZebraPrinter.CPCL_PostFeed(100);
                                ZebraPrinter.CPCL_Print();

                                do {
                                    try {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } while (1 == ZebraPrinter.GetPrinterState());

                                runOnUiThread(() -> progressDialog.dismiss());
                            }
                        }

                        break;

                    case R.id.btnPrintReceipt:
                        Log.i(TAG, "onBtnPrinterClick: btnPrintReceipt");

                        printerState = ZebraPrinter.GetPrinterState();
                        if (2 == printerState || 3 == printerState) {
                            runOnUiThread(() -> Toast.makeText(MobilePrinterActivity.this, R.string.printer_error_not_print, Toast.LENGTH_SHORT).show());
                            return;
                        }

                        if (bOpen) {
                            if (bLanguageESCPOS) {

                                runOnUiThread(() -> {
                                    progressDialog = new ProgressDialog(MobilePrinterActivity.this);
                                    progressDialog.setMessage(getString(R.string.printing));
                                    progressDialog.show();
                                });

                                ZebraPrinter.ESCPOS_PrinterInit();
                                ZebraPrinter.ESCPOS_SetTextFont(3);
                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_BOLD, ZebraPrinter.TEXT_SIZE_1WIDTH | ZebraPrinter.TEXT_SIZE_1HEIGHT);
                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
                                ZebraPrinter.ESCPOS_PrintText("钱大妈（黄村店）\n\n");
                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_DEFAULT, ZebraPrinter.TEXT_SIZE_0WIDTH | ZebraPrinter.TEXT_SIZE_0HEIGHT);
                                ZebraPrinter.ESCPOS_PrintText("时间： 2020-02-28 17：55    操作员： 张三\n");
                                ZebraPrinter.ESCPOS_PrintText("----------------------------------------------------\n");
                                ZebraPrinter.ESCPOS_PrintText("品名            数量            单价           小计\n");
                                ZebraPrinter.ESCPOS_PrintText("西红柿          1（份）         6.99           6.99\n");
                                ZebraPrinter.ESCPOS_PrintText("土豆            1.2（千克）     2.5            3.00\n");
                                ZebraPrinter.ESCPOS_PrintText("西兰花          0.6（千克）     5.00           3.00\n");
                                ZebraPrinter.ESCPOS_PrintText("白皮蒜          1（份）         2.99           2.99 \n");
                                ZebraPrinter.ESCPOS_PrintText("排骨            1.3（千克）     48.80          64.80\n");
                                ZebraPrinter.ESCPOS_PrintText("豆腐            1（份）         2.8            2.8\n");
                                ZebraPrinter.ESCPOS_PrintText("中号袋          1（个）         0.3             0.3\n");
                                ZebraPrinter.ESCPOS_PrintText("-----------------------------------------------------\n");
                                ZebraPrinter.ESCPOS_PrintText("                                      总计：   83.79\n");
                                ZebraPrinter.ESCPOS_PrintText("-----------------------------------------------------\n");
                                ZebraPrinter.ESCPOS_PrintText("                                      实收：   83.79\n");
                                ZebraPrinter.ESCPOS_PrintText("                                      支付宝\n");
                                ZebraPrinter.ESCPOS_PrintText("                                      会员：1234567890\n");
                                ZebraPrinter.ESCPOS_PrintText("-----------------------------------------------------\n");
                                ZebraPrinter.ESCPOS_PrintText("地址：黄村西路55号                      电话：7788 9911\n");
                                ZebraPrinter.ESCPOS_FeedLines(2);

                                do {
                                    try {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } while (1 == ZebraPrinter.GetPrinterState());

                                runOnUiThread(() -> progressDialog.dismiss());
                            }
                        }
                        break;

                    case R.id.btnPrintText:
                        Log.i(TAG, "onBtnPrinterClick: btnPrintText");

                        printerState = ZebraPrinter.GetPrinterState();
                        if (2 == printerState || 3 == printerState) {
                            runOnUiThread(() -> Toast.makeText(MobilePrinterActivity.this, R.string.printer_error_not_print, Toast.LENGTH_SHORT).show());
                            return;
                        }

                        if (bOpen) {
                            if (bLanguageCPCL) {
                                // CPCL text
                                Log.i(TAG, "onBtnPrinterClick: bLanguageCPCL");

                                runOnUiThread(() -> {
                                    progressDialog = new ProgressDialog(MobilePrinterActivity.this);
                                    progressDialog.setMessage(getString(R.string.printing));
                                    progressDialog.show();
                                });

                                ZebraPrinter.CPCL_PrinterInit();
                                ZebraPrinter.CPCL_SetDensity(150);
                                ZebraPrinter.CPCL_SetFont4LineMode("7", 0, 20);
                                ZebraPrinter.CPCL_SetPageWidth(pageWidth);
                                ZebraPrinter.CPCL_PrintText4LineMode("--------------------------------\r\n");
                                ZebraPrinter.CPCL_SetRelativePosition4LineMode(40, 0);
                                ZebraPrinter.CPCL_PrintText4LineMode("CPCL TextInLabelMode D:50\r\n");
                                ZebraPrinter.CPCL_PrintText4LineMode("--------------------------------\r\n");
                                // Text style
                                {
                                    ZebraPrinter.CPCL_CreateLabel(0, 1640, 1);
                                    ZebraPrinter.CPCL_PreFeed(24);
                                    ZebraPrinter.CPCL_SetPageWidth(pageWidth);
                                    ZebraPrinter.CPCL_PrintBox(0, 0, pageWidth, 1640, 2);
                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
                                    ZebraPrinter.CPCL_PrintText("7", 0, 16, 10, 0, "TEXT Style Pre:24 Post:80");
                                    ZebraPrinter.CPCL_PrintLine(8, 10, 376, 10, 24, true);

                                    // Text bold alignment
                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
                                    ZebraPrinter.CPCL_PrintText("7", 0, 8, 40, 0, "BOLD Example - Font 7 Size 0");
                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
                                    ZebraPrinter.CPCL_SetTextBold(0);
                                    ZebraPrinter.CPCL_PrintText("7", 0, 8, 70, 0, "BOLD 0 - ACBDabcd1234!@#$%");
                                    ZebraPrinter.CPCL_SetTextBold(1);
                                    ZebraPrinter.CPCL_PrintText("7", 0, 8, 100, 0, "BOLD 1 - ACBDabcd1234!@#$%");
                                    ZebraPrinter.CPCL_SetTextBold(2);
                                    ZebraPrinter.CPCL_PrintText("7", 0, 8, 130, 0, "BOLD 2 - ACBDabcd1234!@#$%");
                                    ZebraPrinter.CPCL_SetTextBold(3);
                                    ZebraPrinter.CPCL_PrintText("7", 0, 8, 160, 0, "BOLD 3 - ACBDabcd1234!@#$%");
                                    ZebraPrinter.CPCL_SetTextBold(0);
                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
                                    ZebraPrinter.CPCL_PrintText("7", 0, 8, 190, 0, "BOLD Example - Font 4 Size 3");
                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
                                    ZebraPrinter.CPCL_SetTextBold(0);
                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
                                    ZebraPrinter.CPCL_PrintText("7", 0, 8, 220, 0, "Alignment left BOLD 0");
                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
                                    ZebraPrinter.CPCL_SetTextBold(0);
                                    ZebraPrinter.CPCL_PrintText("4", 3, 8, 250, 0, "$1234.56");
                                    ZebraPrinter.CPCL_SetTextBold(0);
                                    ZebraPrinter.CPCL_PrintText("7", 0, 8, 350, 0, "Alignment centre BOLD 2");
                                    ZebraPrinter.CPCL_SetTextBold(2);
                                    ZebraPrinter.CPCL_PrintText("4", 3, 8, 380, 0, "$1234.56");
                                    ZebraPrinter.CPCL_SetTextBold(0);
                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_RIGHT);
                                    ZebraPrinter.CPCL_PrintText("7", 0, 8, 480, 0, "Alignment right BOLD 4  ");
                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
                                    ZebraPrinter.CPCL_SetTextBold(4);
                                    ZebraPrinter.CPCL_PrintText("4", 3, 8, 510, 0, "$1234.56");
                                    ZebraPrinter.CPCL_SetTextBold(0);

                                    // Text size underline
                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
                                    ZebraPrinter.CPCL_PrintText("7", 0, 8, 610, 0, "Size Example - Font 0 Size 0");
                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
                                    ZebraPrinter.CPCL_PrintText("7", 0, 8, 640, 0, "W*2 H*2");
                                    ZebraPrinter.CPCL_SetTextSize(2, 2);
                                    ZebraPrinter.CPCL_PrintText("0", 0, 8, 670, 0, "size");
                                    ZebraPrinter.CPCL_SetTextSize(0, 0);

                                    ZebraPrinter.CPCL_PrintText("7", 0, 8, 700, 0, "W*8 H*8");
                                    ZebraPrinter.CPCL_SetTextSize(8, 8);
                                    ZebraPrinter.CPCL_PrintText("0", 0, 8, 730, 0, "size");
                                    ZebraPrinter.CPCL_SetTextSize(0, 0);

                                    ZebraPrinter.CPCL_PrintText("7", 0, 8, 840, 0, "W*16 H*20");
                                    ZebraPrinter.CPCL_SetTextSize(16, 32);
                                    ZebraPrinter.CPCL_PrintText("0", 0, 8, 870, 0, "S");
                                    ZebraPrinter.CPCL_SetTextSize(0, 0);

                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
                                    ZebraPrinter.CPCL_PrintText("7", 0, 8, 1360, 0, "Size Example - NSMTTC16.CPF");
                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
                                    ZebraPrinter.CPCL_PrintText("7", 0, 8, 1390, 0, "UNDERLINE BOLD 2 W*2 H*2");
                                    ZebraPrinter.CPCL_SetTextSize(2, 2);
                                    ZebraPrinter.CPCL_SetTextBold(2);
                                    ZebraPrinter.CPCL_SetTextUnderline(true);
                                    ZebraPrinter.CPCL_PrintText("NSMTTC16.CPF", 0, 8, 1420, 0, TextStr);
                                    ZebraPrinter.CPCL_SetTextSize(0, 0);
                                    ZebraPrinter.CPCL_SetTextBold(0);
                                    ZebraPrinter.CPCL_SetTextUnderline(false);

                                    // Text Space Density
                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
                                    ZebraPrinter.CPCL_PrintText("0", 2, 8, 1460, 0, "SPACE and DENSITY Example - F0 S3");
                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
                                    ZebraPrinter.CPCL_SetTextSpace(4);
                                    ZebraPrinter.CPCL_PrintText("0", 3, 8, 1490, 0, "SPACE 4");
                                    ZebraPrinter.CPCL_SetTextSpace(8);
                                    ZebraPrinter.CPCL_PrintText("0", 3, 8, 1520, 0, "SPACE 8");
                                    ZebraPrinter.CPCL_SetTextSpace(5);
                                    ZebraPrinter.CPCL_PrintText("0", 3, 8, 1550, 0, "S5 D-100");
                                    ZebraPrinter.CPCL_SetTextSpace(5);
                                    ZebraPrinter.CPCL_PrintText("0", 3, 8, 1580, 0, "S5 D200");
                                    ZebraPrinter.CPCL_SetTextSpace(4);
                                    ZebraPrinter.CPCL_SetTextBold(4);
                                    ZebraPrinter.CPCL_PrintText("0", 3, 8, 1610, 0, "S4 BOLD 4");
                                    ZebraPrinter.CPCL_SetTextSpace(0);
                                    ZebraPrinter.CPCL_SetTextBold(0);

                                    ZebraPrinter.CPCL_PostFeed(80);
                                    ZebraPrinter.CPCL_Print();
                                }
                                ZebraPrinter.CPCL_PreFeed(0);
                                ZebraPrinter.CPCL_PostFeed(0);

                                do {
                                    try {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } while (1 == ZebraPrinter.GetPrinterState());

                                runOnUiThread(() -> progressDialog.dismiss());
                            } else if (bLanguageESCPOS) {
                                // ESCPOS text
                                Log.i(TAG, "onBtnPrinterClick: bLanguageESCPOS");

                                runOnUiThread(() -> {
                                    progressDialog = new ProgressDialog(MobilePrinterActivity.this);
                                    progressDialog.setMessage(getString(R.string.printing));
                                    progressDialog.show();
                                });

                                ZebraPrinter.ESCPOS_PrinterInit();
                                ZebraPrinter.ESCPOS_SetTextFont(0);
                                ZebraPrinter.ESCPOS_SelectLineMode();
                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
                                ZebraPrinter.ESCPOS_PrintLine(pageWidth, 1);
                                ZebraPrinter.ESCPOS_PrintText("ESCPOS TextInLineMode\n");
                                ZebraPrinter.ESCPOS_PrintLine(pageWidth, 1);
                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);

                                // Test style
                                {
                                    ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_DEFAULT, 0);
                                    ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
                                    ZebraPrinter.ESCPOS_PrintText("*************************\n");
                                    ZebraPrinter.ESCPOS_PrintText("TEXT STYLE\n");
                                    ZebraPrinter.ESCPOS_PrintText("*************************\n");
                                    ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
                                    ZebraPrinter.ESCPOS_FeedLines(1);
                                    // Use Emphasized print text
                                    ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_BOLD, 0);
                                    ZebraPrinter.ESCPOS_PrintText("Text (Emphasized)");
                                    ZebraPrinter.ESCPOS_FeedPaper(8);
                                    // Use Underline print text
                                    ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_UNDERLINE, 0);
                                    ZebraPrinter.ESCPOS_PrintText("Text (Underline)");
                                    ZebraPrinter.ESCPOS_FeedPaper(8);
                                    // Use Reverse print text
                                    ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_REVERSE, 0);
                                    ZebraPrinter.ESCPOS_PrintText("Text (Reverse)");
                                    ZebraPrinter.ESCPOS_FeedPaper(8);
                                    // Use Default Style print text
                                    ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_DEFAULT, 0);
                                    ZebraPrinter.ESCPOS_PrintText("Text (Default)");
                                    ZebraPrinter.ESCPOS_FeedPaper(8);
                                    // Use Emphasized and Underline print text
                                    ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_BOLD + ZebraPrinter.TEXT_STYLE_UNDERLINE, 0);
                                    ZebraPrinter.ESCPOS_PrintText("Text (Emphasized and Underline)");
                                    ZebraPrinter.ESCPOS_FeedPaper(8);
                                    // Use Emphasized and Reverse print text
                                    ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_BOLD + ZebraPrinter.TEXT_STYLE_REVERSE, 0);
                                    ZebraPrinter.ESCPOS_PrintText("Text (Emphasized and Reverse)");
                                    ZebraPrinter.ESCPOS_FeedPaper(8);
                                    // Use Underline and Reverse print text
                                    ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_UNDERLINE + ZebraPrinter.TEXT_STYLE_REVERSE, 0);
                                    ZebraPrinter.ESCPOS_PrintText("Text (Underline and Reverse)");
                                    ZebraPrinter.ESCPOS_FeedPaper(8);
                                    // Use ALL print text
                                    ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_ALL, 0);
                                    ZebraPrinter.ESCPOS_PrintText("Text (ALL)");
                                    ZebraPrinter.ESCPOS_FeedLines(2);

                                    // Text LineSpace
                                    ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_DEFAULT, 0);
                                    ZebraPrinter.ESCPOS_SetTextLineSpace(50);
                                    ZebraPrinter.ESCPOS_PrintText("Text LineSpace : 50\n");
                                    ZebraPrinter.ESCPOS_PrintText("Text LineSpace : 50\n");
                                    ZebraPrinter.ESCPOS_SetTextLineSpace(80);
                                    ZebraPrinter.ESCPOS_PrintText("Text LineSpace : 80\n");
                                    ZebraPrinter.ESCPOS_PrintText("Text LineSpace : 80\n");
                                    ZebraPrinter.ESCPOS_SetTextLineSpace(0);

                                    // Text RightSpace
                                    ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_DEFAULT, 0);
                                    ZebraPrinter.ESCPOS_SetTextRightSpace(15);
                                    ZebraPrinter.ESCPOS_PrintText("RightSpace : 15\n");
                                    ZebraPrinter.ESCPOS_SetTextRightSpace(20);
                                    ZebraPrinter.ESCPOS_PrintText("RightSpace : 20\n");
                                    ZebraPrinter.ESCPOS_SetTextRightSpace(0);
                                }

                                do {
                                    try {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } while (1 == ZebraPrinter.GetPrinterState());

                                runOnUiThread(() -> progressDialog.dismiss());
                            }
                        }
                        break;

                    case R.id.btnConfigurationPrint:
                        Log.i(TAG, "onBtnPrinterClick: btnConfigurationPrint");

                        printerState = ZebraPrinter.GetPrinterState();
                        if (2 == printerState || 3 == printerState) {
                            runOnUiThread(() -> Toast.makeText(MobilePrinterActivity.this, R.string.printer_error_not_print, Toast.LENGTH_SHORT).show());
                            return;
                        }

                        if (bOpen) {
                            runOnUiThread(() -> {
                                progressDialog = new ProgressDialog(MobilePrinterActivity.this);
                                progressDialog.setMessage(getString(R.string.printing));
                                progressDialog.show();
                            });

                            ZebraPrinter.WriteData("! U1 setvar \"test.print_diags\" \"\"\r\n".getBytes());

                            do {
                                try {
                                    Thread.sleep(200);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } while (1 == ZebraPrinter.GetPrinterState());

                            runOnUiThread(() -> progressDialog.dismiss());
                        }
                        break;

                    case R.id.btnPrint1DBarcode:
                        Log.i(TAG, "onBtnPrinterClick: btnPrint1DBarcode");

                        printerState = ZebraPrinter.GetPrinterState();
                        if (2 == printerState || 3 == printerState) {
                            runOnUiThread(() -> Toast.makeText(MobilePrinterActivity.this, R.string.printer_error_not_print, Toast.LENGTH_SHORT).show());
                            return;
                        }

                        if (bOpen) {
                            if (bLanguageCPCL) {
                                // CPCL 1DBarcode
                                Log.i(TAG, "onBtnPrinterClick: bLanguageCPCL");

                                runOnUiThread(() -> {
                                    progressDialog = new ProgressDialog(MobilePrinterActivity.this);
                                    progressDialog.setMessage(getString(R.string.printing));
                                    progressDialog.show();
                                });

                                ZebraPrinter.CPCL_PrinterInit();
                                ZebraPrinter.CPCL_SetDensity(0);
                                ZebraPrinter.CPCL_SetFont4LineMode("7", 0, 20);
                                ZebraPrinter.CPCL_SetPageWidth(pageWidth);
                                ZebraPrinter.CPCL_PrintText4LineMode("--------------------------------\r\n");
                                ZebraPrinter.CPCL_SetRelativePosition4LineMode(40, 0);
                                ZebraPrinter.CPCL_PrintText4LineMode("CPCL PrintBarcode D:0\r\n");
                                ZebraPrinter.CPCL_PrintText4LineMode("--------------------------------\r\n");
                                // UPC-A
                                {
                                    ZebraPrinter.CPCL_CreateLabel(0, 700, 1);
                                    ZebraPrinter.CPCL_SetPageWidth(pageWidth);
                                    ZebraPrinter.CPCL_PrintBox(0, 0, pageWidth, 700, 2);
                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
                                    ZebraPrinter.CPCL_PrintText("7", 0, 16, 10, 0, "UPC-A LabelMode");
                                    ZebraPrinter.CPCL_PrintLine(8, 10, 368, 10, 24, true);
                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);

                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 40, ZebraPrinter.CPCL_ROTATION0, "UPC-A Width:2 Height:64");
                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 70, ZebraPrinter.CPCL_ROTATION0, "91234500895 (11 F0 S1)");
                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_UPC_A, 2, 0, 64, 70, 100, ZebraPrinter.CPCL_ROTATION0, "91234510895".getBytes());

                                    ZebraPrinter.CPCL_PrintText("7", 0, 10, 500, ZebraPrinter.CPCL_ROTATION90, "UPC-A Width:2 Height:32");
                                    ZebraPrinter.CPCL_PrintText("7", 0, 40, 500, ZebraPrinter.CPCL_ROTATION90, "91234511895 (11 F0 S1)");
                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_UPC_A, 2, 0, 32, 70, 500, ZebraPrinter.CPCL_ROTATION90, "91234511895".getBytes());

                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 690, ZebraPrinter.CPCL_ROTATION180, "UPC-A Width:3 Height:96");
                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 660, ZebraPrinter.CPCL_ROTATION180, "912345008957 (12 F0 S1)");
                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_UPC_A, 3, 0, 96, 70, 520, ZebraPrinter.CPCL_ROTATION0, "912345008957".getBytes());

                                    ZebraPrinter.CPCL_PrintText("7", 0, pageWidth - 14, 210, ZebraPrinter.CPCL_ROTATION270, "UPC-A Width:2 Height:128");
                                    ZebraPrinter.CPCL_PrintText("7", 0, pageWidth - 44, 210, ZebraPrinter.CPCL_ROTATION270, "912345008954 (12 F0 S1)");
                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_UPC_A, 2, 0, 128, pageWidth - 224, 440, ZebraPrinter.CPCL_ROTATION90, "912345008954".getBytes());

                                    ZebraPrinter.CPCL_PostFeed(80);
                                    ZebraPrinter.CPCL_Print();
                                }
//                                // UPC-E
//                                {
//                                    ZebraPrinter.CPCL_CreateLabel(0, 700, 1);
//                                    ZebraPrinter.CPCL_SetPageWidth(pageWidth);
//                                    ZebraPrinter.CPCL_PrintBox(0, 0, pageWidth, 700, 2);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 16, 10, 0, "UPC-E LabelMode");
//                                    ZebraPrinter.CPCL_PrintLine(8, 10, 368, 10, 24, true);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 40, ZebraPrinter.CPCL_ROTATION0, "UPC-E Width:2 Height:64");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 70, ZebraPrinter.CPCL_ROTATION0, "234567 (6 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_UPC_E, 2, 0, 64, 70, 100, ZebraPrinter.CPCL_ROTATION0, "234567".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 10, 500, ZebraPrinter.CPCL_ROTATION90, "UPC-E Width:2 Height:32");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 40, 500, ZebraPrinter.CPCL_ROTATION90, "234567 (6 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_UPC_E, 2, 0, 32, 70, 500, ZebraPrinter.CPCL_ROTATION90, "234567".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 690, ZebraPrinter.CPCL_ROTATION180, "UPC-E Width:3 Height:96");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 660, ZebraPrinter.CPCL_ROTATION180, "0234567 (7 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_UPC_E, 3, 0, 96, 70, 520, ZebraPrinter.CPCL_ROTATION0, "0234567".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 370, 210, ZebraPrinter.CPCL_ROTATION270, "UPC-E Width:2 Height:128");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 340, 210, ZebraPrinter.CPCL_ROTATION270, "0234567 (7 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_UPC_E, 2, 0, 128, 160, 440, ZebraPrinter.CPCL_ROTATION90, "0234567".getBytes());
//
//                                    ZebraPrinter.CPCL_PostFeed(80);
//                                    ZebraPrinter.CPCL_Print();
//                                }
//                                // EAN13
//                                {
//                                    ZebraPrinter.CPCL_CreateLabel(0, 700, 1);
//                                    ZebraPrinter.CPCL_SetPageWidth(pageWidth);
//                                    ZebraPrinter.CPCL_PrintBox(0, 0, pageWidth, 700, 2);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 16, 10, 0, "EAN13 LabelMode");
//                                    ZebraPrinter.CPCL_PrintLine(8, 10, 368, 10, 24, true);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 40, ZebraPrinter.CPCL_ROTATION0, "EAN13 Width:2 Height:32");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 70, ZebraPrinter.CPCL_ROTATION0, "912345008951 (12 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_EAN13, 2, 0, 32, 70, 100, ZebraPrinter.CPCL_ROTATION0, "912345008951".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 10, 500, ZebraPrinter.CPCL_ROTATION90, "EAN13 Width:2 Height:64");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 40, 500, ZebraPrinter.CPCL_ROTATION90, "912345118954 (12 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_EAN13, 2, 0, 64, 70, 500, ZebraPrinter.CPCL_ROTATION90, "912345118954".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 690, ZebraPrinter.CPCL_ROTATION180, "EAN13 Width:2 Height:64");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 660, ZebraPrinter.CPCL_ROTATION180, "9123450089524 (13 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_EAN13, 3, 0, 64, 70, 520, ZebraPrinter.CPCL_ROTATION0, "9123450089524".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 370, 210, ZebraPrinter.CPCL_ROTATION270, "EAN13 Width:2 Height:96");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 340, 210, ZebraPrinter.CPCL_ROTATION270, "9123451179543 (13 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_EAN13, 2, 0, 96, 180, 440, ZebraPrinter.CPCL_ROTATION90, "9123451179543".getBytes());
//
//                                    ZebraPrinter.CPCL_PostFeed(80);
//                                    ZebraPrinter.CPCL_Print();
//                                }
//                                // EAN8
//                                {
//
//                                    ZebraPrinter.CPCL_CreateLabel(0, 700, 1);
//                                    ZebraPrinter.CPCL_SetPageWidth(pageWidth);
//                                    ZebraPrinter.CPCL_PrintBox(0, 0, pageWidth, 700, 2);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 16, 10, 0, "EAN8 LabelMode");
//                                    ZebraPrinter.CPCL_PrintLine(8, 10, 368, 10, 24, true);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 40, ZebraPrinter.CPCL_ROTATION0, "EAN8 Width:3 Height:32");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 70, ZebraPrinter.CPCL_ROTATION0, "1234567 (7 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_EAN8, 3, 0, 32, 70, 100, ZebraPrinter.CPCL_ROTATION0, "1234567".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 10, 500, ZebraPrinter.CPCL_ROTATION90, "EAN8 Width:4 Height:64");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 40, 500, ZebraPrinter.CPCL_ROTATION90, "7123456 (7 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_EAN8, 4, 0, 64, 70, 500, ZebraPrinter.CPCL_ROTATION90, "7123456".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 690, ZebraPrinter.CPCL_ROTATION180, "EAN8 Width:4 Height:64");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 660, ZebraPrinter.CPCL_ROTATION180, "41234007 (8 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_EAN8, 4, 0, 64, 70, 520, ZebraPrinter.CPCL_ROTATION0, "41234007".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 370, 210, ZebraPrinter.CPCL_ROTATION270, "EAN8 Width:2 Height:96");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 340, 210, ZebraPrinter.CPCL_ROTATION270, "01234500 (8 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_EAN8, 2, 0, 96, 180, 440, ZebraPrinter.CPCL_ROTATION90, "01234500".getBytes());
//
//                                    ZebraPrinter.CPCL_PostFeed(80);
//                                    ZebraPrinter.CPCL_Print();
//                                }
//                                // CODE39
//                                {
//                                    ZebraPrinter.CPCL_CreateLabel(0, 700, 1);
//                                    ZebraPrinter.CPCL_SetPageWidth(pageWidth);
//                                    ZebraPrinter.CPCL_PrintBox(0, 0, pageWidth, 700, 2);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 16, 10, 0, "CODE39 LabelMode");
//                                    ZebraPrinter.CPCL_PrintLine(8, 10, 368, 10, 24, true);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 40, ZebraPrinter.CPCL_ROTATION0, "CODE39 Width:2 Height:32");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 70, ZebraPrinter.CPCL_ROTATION0, "A1+B234 (7 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_CODE39, 2, 2, 32, 70, 100, ZebraPrinter.CPCL_ROTATION0, "A1+B234".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 10, 500, ZebraPrinter.CPCL_ROTATION90, "CODE39 Width:3 Height:64");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 40, 500, ZebraPrinter.CPCL_ROTATION90, "71234 (7 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_CODE39, 3, 27, 64, 70, 500, ZebraPrinter.CPCL_ROTATION90, "71234".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 690, ZebraPrinter.CPCL_ROTATION180, "CODE39 Width:2 Height:64");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 660, ZebraPrinter.CPCL_ROTATION180, "%41234B (7 F7 S0)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 7, 0, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_CODE39, 2, 2, 64, 70, 520, ZebraPrinter.CPCL_ROTATION0, "%41234B".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 370, 210, ZebraPrinter.CPCL_ROTATION270, "CODE39 Width:2 Height:96");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 340, 210, ZebraPrinter.CPCL_ROTATION270, "9C2-45 0/$ (10 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_CODE39, 2, 2, 96, 190, 502, ZebraPrinter.CPCL_ROTATION90, "9C2-45 0/$".getBytes());
//
//                                    ZebraPrinter.CPCL_PostFeed(80);
//                                    ZebraPrinter.CPCL_Print();
//                                }
//                                // ITF
//                                {
//                                    ZebraPrinter.CPCL_CreateLabel(0, 900, 1);
//                                    ZebraPrinter.CPCL_SetPageWidth(pageWidth);
//                                    ZebraPrinter.CPCL_PrintBox(0, 0, pageWidth, 900, 2);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 16, 10, 0, "ITF LabelMode");
//                                    ZebraPrinter.CPCL_PrintLine(8, 10, 368, 10, 24, true);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 40, ZebraPrinter.CPCL_ROTATION0, "ITF Width:6 Height:32");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 70, ZebraPrinter.CPCL_ROTATION0, "12 (2 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_ITF, 6, 0, 32, 70, 100, ZebraPrinter.CPCL_ROTATION0, "12".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 10, 700, ZebraPrinter.CPCL_ROTATION90, "ITF Width:2 Height:64");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 40, 700, ZebraPrinter.CPCL_ROTATION90, "071234124658634659884627 (24 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_ITF, 2, 0, 32, 70, 700, ZebraPrinter.CPCL_ROTATION90, "071234124658634659884627".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 890, ZebraPrinter.CPCL_ROTATION180, "ITF Width:4 Height:64");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 860, ZebraPrinter.CPCL_ROTATION180, "1234 (4 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_ITF, 3, 0, 96, 170, 680, ZebraPrinter.CPCL_ROTATION0, "1234".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 370, 210, ZebraPrinter.CPCL_ROTATION270, "ITF Width:3 Height:128");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 340, 210, ZebraPrinter.CPCL_ROTATION270, "1234567890 (10 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_ITF, 2, 0, 128, 160, 440, ZebraPrinter.CPCL_ROTATION90, "1234567890".getBytes());
//
//                                    ZebraPrinter.CPCL_PostFeed(80);
//                                    ZebraPrinter.CPCL_Print();
//                                }
                                // CODEBAR
                                {
                                    ZebraPrinter.CPCL_CreateLabel(0, 700, 1);
                                    ZebraPrinter.CPCL_SetPageWidth(pageWidth);
                                    ZebraPrinter.CPCL_PrintBox(0, 0, pageWidth, 700, 2);
                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
                                    ZebraPrinter.CPCL_PrintText("7", 0, 16, 10, 0, "CODEBAR LabelMode");
                                    ZebraPrinter.CPCL_PrintLine(8, 10, 368, 10, 24, true);
                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);

                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 40, ZebraPrinter.CPCL_ROTATION0, "CODEBAR Width:3 Height:32");
                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 70, ZebraPrinter.CPCL_ROTATION0, "A1234A (6 F0 S1)");
                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_CODABAR, 3, 27, 32, 70, 100, ZebraPrinter.CPCL_ROTATION0, "A1234A".getBytes());

                                    ZebraPrinter.CPCL_PrintText("7", 0, 10, 500, ZebraPrinter.CPCL_ROTATION90, "CODEBAR Width:2 Height:64");
                                    ZebraPrinter.CPCL_PrintText("7", 0, 40, 500, ZebraPrinter.CPCL_ROTATION90, "B714$.+-/:B (11 F0 S1)");
                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_CODABAR, 2, 2, 64, 70, 500, ZebraPrinter.CPCL_ROTATION90, "B714$.+-/:B".getBytes());

                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 690, ZebraPrinter.CPCL_ROTATION180, "CODEBAR Width:4 Height:64");
                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 660, ZebraPrinter.CPCL_ROTATION180, "C$.+C (5 F0 S1)");
                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_CODABAR, 4, 2, 64, 70, 520, ZebraPrinter.CPCL_ROTATION0, "C$.+C".getBytes());

                                    ZebraPrinter.CPCL_PrintText("7", 0, pageWidth - 14, 210, ZebraPrinter.CPCL_ROTATION270, "CODEBAR Width:2 Height:96");
                                    ZebraPrinter.CPCL_PrintText("7", 0, pageWidth - 44, 210, ZebraPrinter.CPCL_ROTATION270, "D123456789D (11 F0 S1)");
                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_CODABAR, 2, 2, 96, pageWidth - 194, 440, ZebraPrinter.CPCL_ROTATION90, "D123456789D".getBytes());

                                    ZebraPrinter.CPCL_PostFeed(80);
                                    ZebraPrinter.CPCL_Print();
                                }
//                                // CODE93
//                                {
//                                    ZebraPrinter.CPCL_CreateLabel(0, 700, 1);
//                                    ZebraPrinter.CPCL_SetPageWidth(pageWidth);
//                                    ZebraPrinter.CPCL_PrintBox(0, 0, pageWidth, 700, 2);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 16, 10, 0, "CODE93 LabelMode");
//                                    ZebraPrinter.CPCL_PrintLine(8, 10, 368, 10, 24, true);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 40, ZebraPrinter.CPCL_ROTATION0, "CODE93 Width:3 Height:32");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 70, ZebraPrinter.CPCL_ROTATION0, "01234A (6 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_CODE93, 3, 0, 32, 70, 100, ZebraPrinter.CPCL_ROTATION0, "01234A".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 10, 500, ZebraPrinter.CPCL_ROTATION90, "CODE93 Width:2 Height:64");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 40, 500, ZebraPrinter.CPCL_ROTATION90, "B714$.+-/: (11 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_CODE93, 2, 0, 64, 70, 500, ZebraPrinter.CPCL_ROTATION90, "B714$.+-/:".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 690, ZebraPrinter.CPCL_ROTATION180, "CODE93 Width:4 Height:64");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 660, ZebraPrinter.CPCL_ROTATION180, "ABCDE (5 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_CODE93, 4, 0, 64, 25, 520, ZebraPrinter.CPCL_ROTATION0, "ABCDE".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 370, 210, ZebraPrinter.CPCL_ROTATION270, "CODE93 Width:2 Height:96");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 340, 210, ZebraPrinter.CPCL_ROTATION270, "YT123456789 (8 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_CODE93, 2, 0, 96, 190, 440, ZebraPrinter.CPCL_ROTATION90, "YT123456789".getBytes());
//
//                                    ZebraPrinter.CPCL_PostFeed(80);
//                                    ZebraPrinter.CPCL_Print();
//                                }
//                                // CODE128
//                                {
//                                    ZebraPrinter.CPCL_CreateLabel(0, 700, 1);
//                                    ZebraPrinter.CPCL_SetPageWidth(pageWidth);
//                                    ZebraPrinter.CPCL_PrintBox(0, 0, pageWidth, 700, 2);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 16, 10, 0, "CODE128 LabelMode");
//                                    ZebraPrinter.CPCL_PrintLine(8, 10, 368, 10, 24, true);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 40, ZebraPrinter.CPCL_ROTATION0, "CODE128 Width:3 Height:32");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 70, ZebraPrinter.CPCL_ROTATION0, "01234A (6 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_CODE128, 3, 0, 32, 70, 100, ZebraPrinter.CPCL_ROTATION0, "01234A".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 10, 500, ZebraPrinter.CPCL_ROTATION90, "CODE128 Width:2 Height:64");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 40, 500, ZebraPrinter.CPCL_ROTATION90, "B714$.+-/: (11 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_CODE128, 2, 0, 64, 70, 500, ZebraPrinter.CPCL_ROTATION90, "B714$.+-/:".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 690, ZebraPrinter.CPCL_ROTATION180, "CODE128 Width:4 Height:64");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 660, ZebraPrinter.CPCL_ROTATION180, "ABCDE (5 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_CODE128, 4, 0, 64, 25, 520, ZebraPrinter.CPCL_ROTATION0, "91234567".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 370, 210, ZebraPrinter.CPCL_ROTATION270, "CODE128 Width:2 Height:96");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 340, 210, ZebraPrinter.CPCL_ROTATION270, "YT123456789 (11 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_CODE128, 2, 0, 96, 190, 440, ZebraPrinter.CPCL_ROTATION90, "YT123456789".getBytes());
//
//                                    ZebraPrinter.CPCL_PostFeed(80);
//                                    ZebraPrinter.CPCL_Print();
//                                }
//                                // UPCA2
//                                {
//                                    ZebraPrinter.CPCL_CreateLabel(0, 700, 1);
//                                    ZebraPrinter.CPCL_SetPageWidth(pageWidth);
//                                    ZebraPrinter.CPCL_PrintBox(0, 0, pageWidth, 700, 2);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 16, 10, 0, "UPCA2 LabelMode");
//                                    ZebraPrinter.CPCL_PrintLine(8, 10, 368, 10, 24, true);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 40, ZebraPrinter.CPCL_ROTATION0, "UPCA2 Width:2 Height:64");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 70, ZebraPrinter.CPCL_ROTATION0, "912345678957 12 (15 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_UPC_A_2, 2, 0, 64, 70, 100, ZebraPrinter.CPCL_ROTATION0, "912345678957 12".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 10, 500, ZebraPrinter.CPCL_ROTATION90, "UPCA2 Width:2 Height:32");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 40, 500, ZebraPrinter.CPCL_ROTATION90, "912345678957 12 (15 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_UPC_A_2, 2, 0, 32, 70, 500, ZebraPrinter.CPCL_ROTATION90, "91234567895 12".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 690, ZebraPrinter.CPCL_ROTATION180, "UPCA2 Width:2 Height:96");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 660, ZebraPrinter.CPCL_ROTATION180, "91234567895 12 (14 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_UPC_A_2, 2, 0, 96, 70, 520, ZebraPrinter.CPCL_ROTATION0, "91234567895 12".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 370, 210, ZebraPrinter.CPCL_ROTATION270, "UPCA2 Width:2 Height:128");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 340, 210, ZebraPrinter.CPCL_ROTATION270, "91234567895 12 (14 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_UPC_A_2, 2, 0, 128, 160, 440, ZebraPrinter.CPCL_ROTATION90, "91234567895 12".getBytes());
//
//                                    ZebraPrinter.CPCL_PostFeed(80);
//                                    ZebraPrinter.CPCL_Print();
//                                }
//                                // UPCA5
//                                {
//                                    ZebraPrinter.CPCL_CreateLabel(0, 700, 1);
//                                    ZebraPrinter.CPCL_SetPageWidth(pageWidth);
//                                    ZebraPrinter.CPCL_PrintBox(0, 0, pageWidth, 700, 2);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 16, 10, 0, "UPCA5 LabelMode");
//                                    ZebraPrinter.CPCL_PrintLine(8, 10, 368, 10, 24, true);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 35, 40, ZebraPrinter.CPCL_ROTATION0, "UPCA5 Width:2 Height:64");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 35, 70, ZebraPrinter.CPCL_ROTATION0, "91234567895 12345 (17 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_UPC_A_5, 2, 0, 64, 70, 100, ZebraPrinter.CPCL_ROTATION0, "91234567895 12345".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 10, 500, ZebraPrinter.CPCL_ROTATION90, "UPCA5 Width:2 Height:32");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 40, 500, ZebraPrinter.CPCL_ROTATION90, "91234567895 12555 (17 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_UPC_A_5, 2, 0, 32, 70, 500, ZebraPrinter.CPCL_ROTATION90, "91234567895 12555".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 690, ZebraPrinter.CPCL_ROTATION180, "UPCA5 Width:2 Height:96");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 660, ZebraPrinter.CPCL_ROTATION180, "912345678951 12345 (18 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_UPC_A_5, 2, 0, 96, 70, 520, ZebraPrinter.CPCL_ROTATION0, "91234567895 12345".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 370, 190, ZebraPrinter.CPCL_ROTATION270, "UPCA5 Width:2 Height:128");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 340, 190, ZebraPrinter.CPCL_ROTATION270, "91234567895 12555 (18 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_UPC_A_5, 2, 0, 128, 160, 500, ZebraPrinter.CPCL_ROTATION90, "91234567895 12555".getBytes());
//
//                                    ZebraPrinter.CPCL_PostFeed(80);
//                                    ZebraPrinter.CPCL_Print();
//                                }
//                                // UPCE2
//                                {
//                                    ZebraPrinter.CPCL_CreateLabel(0, 700, 1);
//                                    ZebraPrinter.CPCL_SetPageWidth(pageWidth);
//                                    ZebraPrinter.CPCL_PrintBox(0, 0, pageWidth, 700, 2);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 16, 10, 0, "UPCE2 LabelMode");
//                                    ZebraPrinter.CPCL_PrintLine(8, 10, 368, 10, 24, true);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 40, ZebraPrinter.CPCL_ROTATION0, "UPCE2 Width:2 Height:64");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 70, ZebraPrinter.CPCL_ROTATION0, "234567 12 (9 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_UPC_E_2, 2, 0, 64, 70, 100, ZebraPrinter.CPCL_ROTATION0, "234567 12".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 10, 500, ZebraPrinter.CPCL_ROTATION90, "UPCE2 Width:2 Height:32");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 40, 500, ZebraPrinter.CPCL_ROTATION90, "234667 22 (9 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_UPC_E_2, 2, 0, 32, 70, 500, ZebraPrinter.CPCL_ROTATION90, "234667 22".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 690, ZebraPrinter.CPCL_ROTATION180, "UPCE2 Width:3 Height:96");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 660, ZebraPrinter.CPCL_ROTATION180, "0234567 12 (10 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_UPC_E_2, 3, 0, 96, 70, 520, ZebraPrinter.CPCL_ROTATION0, "0234567 12".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 370, 210, ZebraPrinter.CPCL_ROTATION270, "UPCE2 Width:2 Height:128");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 340, 210, ZebraPrinter.CPCL_ROTATION270, "0234667 22 (10 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_UPC_E_2, 2, 0, 128, 160, 440, ZebraPrinter.CPCL_ROTATION90, "0234667 22".getBytes());
//
//                                    ZebraPrinter.CPCL_PostFeed(80);
//                                    ZebraPrinter.CPCL_Print();
//                                }
//                                // UPCE5
//                                {
//                                    ZebraPrinter.CPCL_CreateLabel(0, 700, 1);
//                                    ZebraPrinter.CPCL_SetPageWidth(pageWidth);
//                                    ZebraPrinter.CPCL_PrintBox(0, 0, pageWidth, 700, 2);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 16, 10, 0, "UPCE5 LabelMode");
//                                    ZebraPrinter.CPCL_PrintLine(8, 10, 368, 10, 24, true);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 40, ZebraPrinter.CPCL_ROTATION0, "UPCE5 Width:2 Height:64");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 70, ZebraPrinter.CPCL_ROTATION0, "123456 34567 (12 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_UPC_E_5, 2, 0, 64, 70, 100, ZebraPrinter.CPCL_ROTATION0, "123456 34567".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 10, 500, ZebraPrinter.CPCL_ROTATION90, "UPCE5 Width:2 Height:32");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 40, 500, ZebraPrinter.CPCL_ROTATION90, "123336 34557 (12 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_UPC_E_5, 2, 0, 32, 70, 500, ZebraPrinter.CPCL_ROTATION90, "123336 34557".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 690, ZebraPrinter.CPCL_ROTATION180, "UPCE5 Width:3 Height:96");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 660, ZebraPrinter.CPCL_ROTATION180, "0123456 34567 (13 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_UPC_E_5, 3, 0, 96, 40, 520, ZebraPrinter.CPCL_ROTATION0, "0123456 34567".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 370, 210, ZebraPrinter.CPCL_ROTATION270, "UPCE5 Width:2 Height:128");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 340, 210, ZebraPrinter.CPCL_ROTATION270, "0123336 34557 (13 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_UPC_E_5, 2, 0, 128, 140, 440, ZebraPrinter.CPCL_ROTATION90, "0123336 34557".getBytes());
//
//                                    ZebraPrinter.CPCL_PostFeed(80);
//                                    ZebraPrinter.CPCL_Print();
//                                }
//                                // EAN132
//                                {
//                                    ZebraPrinter.CPCL_CreateLabel(0, 700, 1);
//                                    ZebraPrinter.CPCL_SetPageWidth(pageWidth);
//                                    ZebraPrinter.CPCL_PrintBox(0, 0, pageWidth, 700, 2);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 16, 10, 0, "EAN132 LabelMode");
//                                    ZebraPrinter.CPCL_PrintLine(8, 10, 368, 10, 24, true);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 40, ZebraPrinter.CPCL_ROTATION0, "EAN132 Width:2 Height:64");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 70, ZebraPrinter.CPCL_ROTATION0, "912345678950 34 (15 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_EAN13_2, 2, 0, 64, 70, 100, ZebraPrinter.CPCL_ROTATION0, "912345678950 34".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 10, 500, ZebraPrinter.CPCL_ROTATION90, "EAN132 Width:2 Height:32");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 40, 500, ZebraPrinter.CPCL_ROTATION90, "912345778950 44 (15 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_EAN13_2, 2, 0, 32, 70, 500, ZebraPrinter.CPCL_ROTATION90, "912345778950 44".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 690, ZebraPrinter.CPCL_ROTATION180, "EAN132 Width:2 Height:32");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 660, ZebraPrinter.CPCL_ROTATION180, "9123456789503 34 (16 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_EAN13_2, 2, 0, 80, 70, 520, ZebraPrinter.CPCL_ROTATION0, "9123456789503 34".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 370, 210, ZebraPrinter.CPCL_ROTATION270, "EAN132 Width:2 Height:64");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 340, 210, ZebraPrinter.CPCL_ROTATION270, "9123450789501 44 (16 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_EAN13_2, 2, 0, 64, 210, 470, ZebraPrinter.CPCL_ROTATION90, "9123450789501 44".getBytes());
//
//                                    ZebraPrinter.CPCL_PostFeed(80);
//                                    ZebraPrinter.CPCL_Print();
//                                }
//                                // EAN135
//                                {
//                                    ZebraPrinter.CPCL_CreateLabel(0, 700, 1);
//                                    ZebraPrinter.CPCL_SetPageWidth(pageWidth);
//                                    ZebraPrinter.CPCL_PrintBox(0, 0, pageWidth, 700, 2);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 16, 10, 0, "EAN135 LabelMode");
//                                    ZebraPrinter.CPCL_PrintLine(8, 10, 368, 10, 24, true);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 30, 40, ZebraPrinter.CPCL_ROTATION0, "EAN135 Width:2 Height:64");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 30, 70, ZebraPrinter.CPCL_ROTATION0, "912345678950 12345 (18 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_EAN13_5, 2, 0, 64, 60, 100, ZebraPrinter.CPCL_ROTATION0, "912345678950 12345".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 10, 540, ZebraPrinter.CPCL_ROTATION90, "EAN135 Width:2 Height:32");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 40, 540, ZebraPrinter.CPCL_ROTATION90, "912345670950 12325 (18 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_EAN13_5, 2, 0, 32, 70, 500, ZebraPrinter.CPCL_ROTATION90, "912345670950 12325".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 690, ZebraPrinter.CPCL_ROTATION180, "EAN135 Width:2 Height:32");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 660, ZebraPrinter.CPCL_ROTATION180, "9123456789503 12345 (19 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_EAN13_5, 2, 0, 32, 70, 580, ZebraPrinter.CPCL_ROTATION0, "9123456789503 12345".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 370, 210, ZebraPrinter.CPCL_ROTATION270, "EAN135 Width:2 Height:128");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 340, 210, ZebraPrinter.CPCL_ROTATION270, "9123456709507 12325 (19 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_EAN13_5, 2, 0, 128, 160, 500, ZebraPrinter.CPCL_ROTATION90, "912345670950 12325".getBytes());
//
//                                    ZebraPrinter.CPCL_PostFeed(80);
//                                    ZebraPrinter.CPCL_Print();
//                                }
//                                // EAN82
//                                {
//                                    ZebraPrinter.CPCL_CreateLabel(0, 700, 1);
//                                    ZebraPrinter.CPCL_SetPageWidth(pageWidth);
//                                    ZebraPrinter.CPCL_PrintBox(0, 0, pageWidth, 700, 2);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 16, 10, 0, "EAN82 LabelMode");
//                                    ZebraPrinter.CPCL_PrintLine(8, 10, 368, 10, 24, true);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 40, ZebraPrinter.CPCL_ROTATION0, "EAN82 Width:2 Height:64");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 70, ZebraPrinter.CPCL_ROTATION0, "9123456 11 (10 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_EAN8_2, 2, 0, 64, 70, 100, ZebraPrinter.CPCL_ROTATION0, "9123456 11".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 10, 500, ZebraPrinter.CPCL_ROTATION90, "EAN82 Width:2 Height:32");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 40, 500, ZebraPrinter.CPCL_ROTATION90, "9123456 22 (10 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_EAN8_2, 2, 0, 32, 70, 500, ZebraPrinter.CPCL_ROTATION90, "9123456 22".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 690, ZebraPrinter.CPCL_ROTATION180, "EAN82 Width:3 Height:96");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 660, ZebraPrinter.CPCL_ROTATION180, "91234567 11 (11 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_EAN8_2, 3, 0, 96, 70, 520, ZebraPrinter.CPCL_ROTATION0, "91234567 11".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 370, 210, ZebraPrinter.CPCL_ROTATION270, "EAN82 Width:2 Height:128");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 340, 210, ZebraPrinter.CPCL_ROTATION270, "91234567 66 (11 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_EAN8_2, 2, 0, 128, 160, 440, ZebraPrinter.CPCL_ROTATION90, "91234567 66".getBytes());
//
//                                    ZebraPrinter.CPCL_PostFeed(80);
//                                    ZebraPrinter.CPCL_Print();
//                                }
//                                // EAN85
//                                {
//                                    ZebraPrinter.CPCL_CreateLabel(0, 700, 1);
//                                    ZebraPrinter.CPCL_SetPageWidth(pageWidth);
//                                    ZebraPrinter.CPCL_PrintBox(0, 0, pageWidth, 700, 2);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 16, 10, 0, "EAN85 LabelMode");
//                                    ZebraPrinter.CPCL_PrintLine(8, 10, 368, 10, 24, true);
//                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 40, ZebraPrinter.CPCL_ROTATION0, "EAN85 Width:2 Height:64");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 70, ZebraPrinter.CPCL_ROTATION0, "9123456 37659(7 5 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_EAN8_5, 2, 0, 64, 70, 100, ZebraPrinter.CPCL_ROTATION0, "9123456 37659".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 10, 500, ZebraPrinter.CPCL_ROTATION90, "EAN85 Width:2 Height:32");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 40, 500, ZebraPrinter.CPCL_ROTATION90, "9123456 12359(7 5 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_EAN8_5, 2, 0, 32, 70, 500, ZebraPrinter.CPCL_ROTATION90, "9123456 12359".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 690, ZebraPrinter.CPCL_ROTATION180, "EAN85 Width:2 Height:96");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 360, 660, ZebraPrinter.CPCL_ROTATION180, "91234567 37659(8 5 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_EAN8_5, 2, 0, 96, 70, 520, ZebraPrinter.CPCL_ROTATION0, "91234567 37659".getBytes());
//
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 370, 210, ZebraPrinter.CPCL_ROTATION270, "EAN85 Width:2 Height:128");
//                                    ZebraPrinter.CPCL_PrintText("7", 0, 340, 210, ZebraPrinter.CPCL_ROTATION270, "91234567 12359(8 5 F0 S1)");
//                                    ZebraPrinter.CPCL_SetBarcodeText(true, 0, 1, 4);
//                                    ZebraPrinter.CPCL_PrintBarcode(ZebraPrinter.BARCODE_EAN8_5, 2, 0, 128, 160, 440, ZebraPrinter.CPCL_ROTATION90, "91234567 12359".getBytes());
//
//                                    ZebraPrinter.CPCL_PostFeed(80);
//                                    ZebraPrinter.CPCL_Print();
//                                }

                                ZebraPrinter.CPCL_SetBarcodeText(false, 0, 1, 4);

                                do {
                                    try {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } while (1 == ZebraPrinter.GetPrinterState());

                                runOnUiThread(() -> progressDialog.dismiss());

                            } else if (bLanguageESCPOS) {
                                // ESCPOS 1DBarcode
                                Log.i(TAG, "onBtnPrinterClick: bLanguageESCPOS");

                                runOnUiThread(() -> {
                                    progressDialog = new ProgressDialog(MobilePrinterActivity.this);
                                    progressDialog.setMessage(getString(R.string.printing));
                                    progressDialog.show();
                                });

                                ZebraPrinter.ESCPOS_PrinterInit();
                                ZebraPrinter.ESCPOS_SetTextFont(0);
                                ZebraPrinter.ESCPOS_SelectLineMode();
                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
                                ZebraPrinter.ESCPOS_PrintLine(pageWidth, 1);
                                ZebraPrinter.ESCPOS_PrintText("ESCPOS BarcodeInLineMode\n");
                                ZebraPrinter.ESCPOS_PrintLine(pageWidth, 1);
                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);

                                // UPC-A
                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_REVERSE, 0);
                                ZebraPrinter.ESCPOS_PrintText("UPC-A Width:2 Height:128 BELOW\n");
                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_DEFAULT, 0);
                                ZebraPrinter.ESCPOS_PrintText("91234567895\n");
                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
                                ZebraPrinter.ESCPOS_PrintBarcode(true, ZebraPrinter.BARCODE_HRI_BELOW, ZebraPrinter.BARCODE_HRI_FONT_A, ZebraPrinter.BARCODE_UPC_A, 2, 128, "91234567895".getBytes());
                                ZebraPrinter.ESCPOS_FeedPaper(8);
                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_REVERSE, 0);
                                ZebraPrinter.ESCPOS_PrintText("UPC-A Width:3 Height:64 BELOW\n");
                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_DEFAULT, 0);
                                ZebraPrinter.ESCPOS_PrintText("912345678955\n");
                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
                                ZebraPrinter.ESCPOS_PrintBarcode(true, ZebraPrinter.BARCODE_HRI_BELOW, ZebraPrinter.BARCODE_HRI_FONT_A, ZebraPrinter.BARCODE_UPC_A, 3, 64, "912345678955".getBytes());
                                ZebraPrinter.ESCPOS_FeedPaper(8);

//                                // UPC-E
//                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
//                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_REVERSE, 0);
//                                ZebraPrinter.ESCPOS_PrintText("UPC-E Width:2 Height:128\n");
//                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_DEFAULT, 0);
//                                ZebraPrinter.ESCPOS_PrintText("01200000345\n");
//                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
//                                ZebraPrinter.ESCPOS_PrintBarcode(true, ZebraPrinter.BARCODE_HRI_BELOW, ZebraPrinter.BARCODE_HRI_FONT_A, ZebraPrinter.BARCODE_UPC_E, 2, 128, "01200000345".getBytes());
//                                ZebraPrinter.ESCPOS_FeedPaper(8);
//
//                                // JAN13(EAN13)
//                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
//                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_REVERSE, 0);
//                                ZebraPrinter.ESCPOS_PrintText("EAN13 Width:2 Height:128\n");
//                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_DEFAULT, 0);
//                                ZebraPrinter.ESCPOS_PrintText("912345678950\n");
//                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
//                                ZebraPrinter.ESCPOS_PrintBarcode(true, ZebraPrinter.BARCODE_HRI_BELOW, ZebraPrinter.BARCODE_HRI_FONT_B, ZebraPrinter.BARCODE_EAN13, 2, 128, "912345678950".getBytes());
//                                ZebraPrinter.ESCPOS_FeedPaper(8);
//                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
//                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_REVERSE, 0);
//                                ZebraPrinter.ESCPOS_PrintText("EAN13 Width:3 Height:128\n");
//                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_DEFAULT, 0);
//                                ZebraPrinter.ESCPOS_PrintText("9123456789505\n");
//                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
//                                ZebraPrinter.ESCPOS_PrintBarcode(true, ZebraPrinter.BARCODE_HRI_ABOVE, ZebraPrinter.BARCODE_HRI_FONT_B, ZebraPrinter.BARCODE_EAN13, 3, 128, "9123456789505".getBytes());
//                                ZebraPrinter.ESCPOS_FeedPaper(8);
//
//                                // JAN8(EAN8)
//                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
//                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_REVERSE, 0);
//                                ZebraPrinter.ESCPOS_PrintText("EAN8 Width:2 Height:80\n");
//                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_DEFAULT, 0);
//                                ZebraPrinter.ESCPOS_PrintText("9123456\n");
//                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
//                                ZebraPrinter.ESCPOS_PrintBarcode(true, ZebraPrinter.BARCODE_HRI_BELOW, ZebraPrinter.BARCODE_HRI_FONT_B, ZebraPrinter.BARCODE_EAN8, 2, 80, "9123456".getBytes());
//                                ZebraPrinter.ESCPOS_FeedPaper(8);
//                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
//                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_REVERSE, 0);
//                                ZebraPrinter.ESCPOS_PrintText("EAN8 Width:4 Height:64\n");
//                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_DEFAULT, 0);
//                                ZebraPrinter.ESCPOS_PrintText("91234567\n");
//                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
//                                ZebraPrinter.ESCPOS_PrintBarcode(true, ZebraPrinter.BARCODE_HRI_BOTH, ZebraPrinter.BARCODE_HRI_FONT_B, ZebraPrinter.BARCODE_EAN8, 4, 64, "91234567".getBytes());
//                                ZebraPrinter.ESCPOS_FeedPaper(8);
//
//                                // CODE39
//                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
//                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_REVERSE, 0);
//                                ZebraPrinter.ESCPOS_PrintText("CODE39 Width:2 Height:64\n");
//                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_DEFAULT, 0);
//                                ZebraPrinter.ESCPOS_PrintText("91234ABCXZ\n");
//                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
//                                ZebraPrinter.ESCPOS_PrintBarcode(true, ZebraPrinter.BARCODE_HRI_BELOW, ZebraPrinter.BARCODE_HRI_FONT_A, ZebraPrinter.BARCODE_CODE39, 2, 64, "91234ABCXZ".getBytes());
//                                ZebraPrinter.ESCPOS_FeedPaper(8);
//                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
//                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_REVERSE, 0);
//                                ZebraPrinter.ESCPOS_PrintText("CODE39 Width:2 Height:64\n");
//                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_DEFAULT, 0);
//                                ZebraPrinter.ESCPOS_PrintText("91234567\n");
//                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
//                                ZebraPrinter.ESCPOS_PrintBarcode(true, ZebraPrinter.BARCODE_HRI_BELOW, ZebraPrinter.BARCODE_HRI_FONT_A, ZebraPrinter.BARCODE_CODE39, 2, 64, "91234567".getBytes());
//                                ZebraPrinter.ESCPOS_FeedPaper(8);
//
//                                // ITF
//                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
//                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_REVERSE, 0);
//                                ZebraPrinter.ESCPOS_PrintText("ITF Width:5 Height:192\n");
//                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_DEFAULT, 0);
//                                ZebraPrinter.ESCPOS_PrintText("912345\n");
//                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
//                                ZebraPrinter.ESCPOS_PrintBarcode(true, ZebraPrinter.BARCODE_HRI_BELOW, ZebraPrinter.BARCODE_HRI_FONT_A, ZebraPrinter.BARCODE_ITF, 5, 192, "912345".getBytes());
//                                ZebraPrinter.ESCPOS_FeedPaper(8);
//                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
//                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_REVERSE, 0);
//                                ZebraPrinter.ESCPOS_PrintText("ITF Width:6 Height:164\n");
//                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_DEFAULT, 0);
//                                ZebraPrinter.ESCPOS_PrintText("912345\n");
//                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
//                                ZebraPrinter.ESCPOS_PrintBarcode(true, ZebraPrinter.BARCODE_HRI_BELOW, ZebraPrinter.BARCODE_HRI_FONT_A, ZebraPrinter.BARCODE_ITF, 6, 164, "912345".getBytes());
//                                ZebraPrinter.ESCPOS_FeedPaper(8);

                                // CODEBAR
                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_REVERSE, 0);
                                ZebraPrinter.ESCPOS_PrintText("CODEBAR Width:2 Height:64 ABOVE\n");
                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_DEFAULT, 0);
                                ZebraPrinter.ESCPOS_PrintText("A91234567A\n");
                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
                                ZebraPrinter.ESCPOS_PrintBarcode(true, ZebraPrinter.BARCODE_HRI_ABOVE, ZebraPrinter.BARCODE_HRI_FONT_A, ZebraPrinter.BARCODE_CODABAR, 2, 64, "A91234567A".getBytes());
                                ZebraPrinter.ESCPOS_FeedPaper(8);
                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_REVERSE, 0);
                                ZebraPrinter.ESCPOS_PrintText("CODEBAR Width:2 Height:64 BOTH\n");
                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_DEFAULT, 0);
                                ZebraPrinter.ESCPOS_PrintText("B912345-$:.+/B\n");
                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
                                ZebraPrinter.ESCPOS_PrintBarcode(true, ZebraPrinter.BARCODE_HRI_BOTH, ZebraPrinter.BARCODE_HRI_FONT_A, ZebraPrinter.BARCODE_CODABAR, 2, 64, "B912345-$:.+/B".getBytes());
                                ZebraPrinter.ESCPOS_FeedPaper(8);

//                                // CODE93
//                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
//                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_REVERSE, 0);
//                                ZebraPrinter.ESCPOS_PrintText("CODE93 Width:2 Height:255\n");
//                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_DEFAULT, 0);
//                                ZebraPrinter.ESCPOS_PrintText("91234567895\n");
//                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
//                                ZebraPrinter.ESCPOS_PrintBarcode(true, ZebraPrinter.BARCODE_HRI_BELOW, ZebraPrinter.BARCODE_HRI_FONT_A, ZebraPrinter.BARCODE_CODE93, 2, 255, "91234567895".getBytes());
//                                ZebraPrinter.ESCPOS_FeedPaper(8);
//
//                                // CODE128
//                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
//                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_REVERSE, 0);
//                                ZebraPrinter.ESCPOS_PrintText("CODE128 Width:3 Height:128\n");
//                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_DEFAULT, 0);
//                                ZebraPrinter.ESCPOS_PrintText("91234567\n");
//                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
//                                ZebraPrinter.ESCPOS_PrintBarcode(true, ZebraPrinter.BARCODE_HRI_BOTH, ZebraPrinter.BARCODE_HRI_FONT_B, ZebraPrinter.BARCODE_CODE128, 3, 128, "91234567".getBytes());
//                                ZebraPrinter.ESCPOS_FeedPaper(8);
//                                ZebraPrinter.ESCPOS_FeedLines(2);

                                do {
                                    try {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } while (1 == ZebraPrinter.GetPrinterState());

                                runOnUiThread(() -> progressDialog.dismiss());
                            }
                        }
                        break;

                    case R.id.btnPrint2DBarcode:
                        Log.i(TAG, "onBtnPrinterClick: btnPrint2DBarcode");

                        printerState = ZebraPrinter.GetPrinterState();
                        if (2 == printerState || 3 == printerState) {
                            runOnUiThread(() -> Toast.makeText(MobilePrinterActivity.this, R.string.printer_error_not_print, Toast.LENGTH_SHORT).show());
                            return;
                        }

                        if (bOpen) {
                            if (bLanguageCPCL) {
                                // CPCL 2DBarcode
                                Log.i(TAG, "onBtnPrinterClick: bLanguageCPCL");

                                runOnUiThread(() -> {
                                    progressDialog = new ProgressDialog(MobilePrinterActivity.this);
                                    progressDialog.setMessage(getString(R.string.printing));
                                    progressDialog.show();
                                });

                                ZebraPrinter.CPCL_PrinterInit();
                                ZebraPrinter.CPCL_SetDensity(0);
                                ZebraPrinter.CPCL_SetFont4LineMode("7", 0, 20);
                                ZebraPrinter.CPCL_SetPageWidth(pageWidth);
                                ZebraPrinter.CPCL_PrintText4LineMode("--------------------------------\r\n");
                                ZebraPrinter.CPCL_SetRelativePosition4LineMode(40, 0);
                                ZebraPrinter.CPCL_PrintText4LineMode("CPCL PrintQRCode D:0\r\n");
                                ZebraPrinter.CPCL_PrintText4LineMode("--------------------------------\r\n");

                                {
                                    ZebraPrinter.CPCL_CreateLabel(0, 900, 1);
                                    ZebraPrinter.CPCL_SetPageWidth(pageWidth);
                                    ZebraPrinter.CPCL_PrintBox(0, 0, pageWidth, 900, 2);
                                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);

                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 10, ZebraPrinter.CPCL_ROTATION0, "M2 W2 E0");
                                    ZebraPrinter.CPCL_PrintText("7", 0, 50, 40, ZebraPrinter.CPCL_ROTATION0, "12345abcdeABCD");
                                    ZebraPrinter.CPCL_PrintQRCode(70, 70, 2, 2, 0, ZebraPrinter.CPCL_ROTATION0, "12345abcdeABCD".getBytes());

                                    ZebraPrinter.CPCL_PrintText("7", 0, 10, 580, ZebraPrinter.CPCL_ROTATION90, "M2 W4 E1");
                                    ZebraPrinter.CPCL_PrintText("NSMTTC16.CPF", 0, 40, 580, ZebraPrinter.CPCL_ROTATION90, textStrUTF8);
                                    ZebraPrinter.CPCL_PrintQRCode(70, 560, 2, 4, 1, ZebraPrinter.CPCL_ROTATION90, textStrUTF8.getBytes(StandardCharsets.UTF_8));

                                    ZebraPrinter.CPCL_PrintText("7", 0, 304, 880, ZebraPrinter.CPCL_ROTATION180, "M2 W6 E2");
                                    ZebraPrinter.CPCL_PrintText("NSMTTC16.CPF", 0, 304, 850, ZebraPrinter.CPCL_ROTATION180, textStrGB18030);
                                    try {
                                        ZebraPrinter.CPCL_PrintQRCode(140, 650, 2, 6, 2, ZebraPrinter.CPCL_ROTATION0, textStrGB18030.getBytes("GB18030"));
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }

                                    ZebraPrinter.CPCL_PrintText("7", 0, pageWidth - 14, 180, ZebraPrinter.CPCL_ROTATION270, "M2 W8 E3");
                                    ZebraPrinter.CPCL_PrintText("7", 0, pageWidth - 44, 180, ZebraPrinter.CPCL_ROTATION270, "abcde12345ABCD");
                                    ZebraPrinter.CPCL_PrintQRCode(pageWidth - 274, 380, 2, 8, 3, ZebraPrinter.CPCL_ROTATION90, "abcde12345ABCD".getBytes());

                                    ZebraPrinter.CPCL_PostFeed(80);
                                    ZebraPrinter.CPCL_Print();
                                }

                                do {
                                    try {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } while (1 == ZebraPrinter.GetPrinterState());

                                runOnUiThread(() -> progressDialog.dismiss());

                            } else if (bLanguageESCPOS) {
                                // ESCPOS 2DBarcode
                                Log.i(TAG, "onBtnPrinterClick: bLanguageESCPOS");

                                runOnUiThread(() -> {
                                    progressDialog = new ProgressDialog(MobilePrinterActivity.this);
                                    progressDialog.setMessage(getString(R.string.printing));
                                    progressDialog.show();
                                });

                                ZebraPrinter.ESCPOS_PrinterInit();
                                ZebraPrinter.ESCPOS_SetTextFont(0);
                                ZebraPrinter.ESCPOS_SelectLineMode();
                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
                                ZebraPrinter.ESCPOS_PrintLine(pageWidth, 1);
                                ZebraPrinter.ESCPOS_PrintText("ESCPOS QRCodeInLineMode\n");
                                ZebraPrinter.ESCPOS_PrintLine(pageWidth, 1);
                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);

                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_REVERSE, 0);
                                ZebraPrinter.ESCPOS_SetTextFont(0);
                                ZebraPrinter.ESCPOS_PrintText("QRCode\n");
                                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_DEFAULT, 0);
                                byte[] tabPositionList = {15, 20};
                                ZebraPrinter.ESCPOS_SetTabPosition(false, tabPositionList);

                                ZebraPrinter.ESCPOS_PrintText("Model\t:\t1\n");
                                ZebraPrinter.ESCPOS_PrintText("ModuleSize\t:\t6\n");
                                ZebraPrinter.ESCPOS_PrintText("ECCLevel\t:\tL\n");
                                ZebraPrinter.ESCPOS_PrintText("Data : 123456780ABCDEacbde\n");
                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
                                ZebraPrinter.ESCPOS_PrintQRCode(ZebraPrinter.QRCODE_MODEL_1, 6, ZebraPrinter.QRCODE_ECC_L, "123456780ABCDEacbde".getBytes());
                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
                                ZebraPrinter.ESCPOS_FeedLines(1);

                                ZebraPrinter.ESCPOS_PrintText("Model\t:\t2\n");
                                ZebraPrinter.ESCPOS_PrintText("ModuleSize\t:\t0\n");
                                ZebraPrinter.ESCPOS_PrintText("ECCLevel\t:\tM\n");
                                ZebraPrinter.ESCPOS_PrintText("Data : 123456780ABCDEacbde\n");
                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
                                ZebraPrinter.ESCPOS_PrintQRCode(ZebraPrinter.QRCODE_MODEL_2, 0, ZebraPrinter.QRCODE_ECC_M, "123456780ABCDEacbde".getBytes());
                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
                                ZebraPrinter.ESCPOS_FeedLines(1);

                                ZebraPrinter.ESCPOS_PrintText("Model\t:\t2\n");
                                ZebraPrinter.ESCPOS_PrintText("ModuleSize\t:\t2\n");
                                ZebraPrinter.ESCPOS_PrintText("ECCLevel\t:\tQ\n");
                                ZebraPrinter.ESCPOS_PrintText("Data : 123456780ABCDEacbde\n");
                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_RIGHT);
                                ZebraPrinter.ESCPOS_PrintQRCode(ZebraPrinter.QRCODE_MODEL_2, 2, ZebraPrinter.QRCODE_ECC_Q, "123456780ABCDEacbde".getBytes());
                                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
                                ZebraPrinter.ESCPOS_FeedLines(1);

                                ZebraPrinter.ESCPOS_PrintText("Model\t:\t2\n");
                                ZebraPrinter.ESCPOS_PrintText("ModuleSize\t:\t4\n");
                                ZebraPrinter.ESCPOS_PrintText("ECCLevel\t:\tH\n");
                                ZebraPrinter.ESCPOS_PrintText("Data : 123456780ABCDEacbde\n");
                                ZebraPrinter.ESCPOS_SetAbsolutePrintPosition(32, 0);
                                ZebraPrinter.ESCPOS_PrintQRCode(ZebraPrinter.QRCODE_MODEL_2, 4, ZebraPrinter.QRCODE_ECC_H, "123456780ABCDEacbde".getBytes());
                                ZebraPrinter.ESCPOS_FeedLines(1);

                                do {
                                    try {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } while (1 == ZebraPrinter.GetPrinterState());

                                runOnUiThread(() -> progressDialog.dismiss());
                            }
                        }
                        break;

                    case R.id.btnPrintImage:
                        isCurrentPictureZoom = false;
                        DownloadImageFromServerAsyncTask task = new DownloadImageFromServerAsyncTask();
                        task.execute();

//                        handlePrintPicture();
                        break;

                    case R.id.btnPrintImage_zoom:
                        isCurrentPictureZoom = true;

                        DownloadImageFromServerAsyncTask task1 = new DownloadImageFromServerAsyncTask();
                        task1.execute();

//                        handlePrintPicture();
                        break;
                }
            }
        }.start();
    }

    private void handlePrintPicture(Bitmap bitmap) {
        int printerState;
        Log.i(TAG, "onBtnPrinterClick: btnPrintImage");

        printerState = ZebraPrinter.GetPrinterState();
        if (2 == printerState || 3 == printerState) {
            runOnUiThread(() -> Toast.makeText(MobilePrinterActivity.this, R.string.printer_error_not_print, Toast.LENGTH_SHORT).show());
            return;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
//      Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.zebraicon, options);

        if (bOpen) {
            if (bLanguageCPCL) {
                // CPCL btnPrintImage
                Log.i(TAG, "onBtnPrinterClick: bLanguageCPCL");

                runOnUiThread(() -> {
                    progressDialog = new ProgressDialog(MobilePrinterActivity.this);
                    progressDialog.setMessage(getString(R.string.printing));
                    progressDialog.show();
                });

                ZebraPrinter.CPCL_PrinterInit();
                ZebraPrinter.CPCL_SetDensity(75);
//                ZebraPrinter.CPCL_SetDensity(150);
                ZebraPrinter.CPCL_SetFont4LineMode("7", 0, 20);
                ZebraPrinter.CPCL_SetPageWidth(pageWidth);
//                ZebraPrinter.CPCL_PrintText4LineMode("--------------------------------\r\n");
                ZebraPrinter.CPCL_SetRelativePosition4LineMode(0, 0);
//                ZebraPrinter.CPCL_PrintText4LineMode("CPCL PrintImageInLabelMode D:0\r\n");
//                ZebraPrinter.CPCL_PrintText4LineMode("--------------------------------\r\n");

                {
//                    ZebraPrinter.CPCL_CreateLabel(0, 600, 1);
                    ZebraPrinter.CPCL_CreateLabel(0, 1000, 1);
                    ZebraPrinter.CPCL_SetPageWidth(pageWidth);
//                    ZebraPrinter.CPCL_PrintBox(0, 0, pageWidth, 600, 2);
//                    ZebraPrinter.CPCL_PrintBox(0, 0, pageWidth, 400, 2);
                    //打印方框
//                    ZebraPrinter.CPCL_PrintBox(100, 100, pageWidth, 400, 2);
                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);

//                    ZebraPrinter.CPCL_PrintText("7", 0, 10, 10, ZebraPrinter.CPCL_ROTATION0, "PNG Picture Rotation 0");
//                    ZebraPrinter.CPCL_PrintImage(50, 70, ZebraPrinter.CPCL_ROTATION0, bitmap);
//                    ZebraPrinter.CPCL_PrintImage(0, 0, ZebraPrinter.CPCL_ROTATION0, bitmap);
                    ZebraPrinter.CPCL_PrintImage(0, 0, ZebraPrinter.CPCL_ROTATION0, bitmap);

//                    ZebraPrinter.CPCL_PrintText("7", 0, 10, 590, ZebraPrinter.CPCL_ROTATION90, "PNG Picture Rotation 90");
//                    ZebraPrinter.CPCL_PrintImage(50, 520, ZebraPrinter.CPCL_ROTATION90, bitmap);
//
//                    ZebraPrinter.CPCL_PrintText("7", 0, 344, 590, ZebraPrinter.CPCL_ROTATION180, "PNG Picture Rotation 0");
//                    ZebraPrinter.CPCL_PrintImage(210, 490, ZebraPrinter.CPCL_ROTATION0, bitmap);
//
//                    ZebraPrinter.CPCL_PrintText("7", 0, pageWidth - 14, 100, ZebraPrinter.CPCL_ROTATION270, "PNG Picture Rotation 90");
//                    ZebraPrinter.CPCL_PrintImage(pageWidth - 124, 280, ZebraPrinter.CPCL_ROTATION90, bitmap);

                    ZebraPrinter.CPCL_PostFeed(80);
                    ZebraPrinter.CPCL_Print();
                }

                do {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (1 == ZebraPrinter.GetPrinterState());

                runOnUiThread(() -> progressDialog.dismiss());

            } else if (bLanguageESCPOS) {

                // ESCPOS btnPrintImage
                Log.i(TAG, "onBtnPrinterClick: bLanguageESCPOS");

                runOnUiThread(() -> {
                    progressDialog = new ProgressDialog(MobilePrinterActivity.this);
                    progressDialog.setMessage(getString(R.string.printing));
                    progressDialog.show();
                });

                ZebraPrinter.ESCPOS_PrinterInit();
                ZebraPrinter.ESCPOS_SetTextFont(0);
                ZebraPrinter.ESCPOS_SelectLineMode();
                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
                ZebraPrinter.ESCPOS_PrintLine(pageWidth, 1);
                ZebraPrinter.ESCPOS_PrintText("ESCPOS PrintImageInLineMode\n");
                ZebraPrinter.ESCPOS_PrintLine(pageWidth, 1);
                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);

                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_REVERSE, 0);
                ZebraPrinter.ESCPOS_SetTextFont(0);
                ZebraPrinter.ESCPOS_PrintText("Image\n");
                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_DEFAULT, 0);
                byte[] tabPositionList = {15, 20};
                ZebraPrinter.ESCPOS_SetTabPosition(false, tabPositionList);

                ZebraPrinter.ESCPOS_PrintText("ImageType\t:\tPNG\n");
                ZebraPrinter.ESCPOS_PrintText("vEnlarge\t:\t1\n");
                ZebraPrinter.ESCPOS_PrintText("hEnlarge\t:\t1\n");
                ZebraPrinter.ESCPOS_PrintText("Alignment\t:\tleft\n");
                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
                ZebraPrinter.ESCPOS_PrintImage(bitmap, 0);
                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
                ZebraPrinter.ESCPOS_FeedLines(1);

                ZebraPrinter.ESCPOS_PrintText("ImageType\t:\tPNG\n");
                ZebraPrinter.ESCPOS_PrintText("vEnlarge\t:\t2\n");
                ZebraPrinter.ESCPOS_PrintText("hEnlarge\t:\t1\n");
                ZebraPrinter.ESCPOS_PrintText("Alignment\t:\tcentre\n");
                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
                ZebraPrinter.ESCPOS_PrintImage(bitmap, 1);
                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
                ZebraPrinter.ESCPOS_FeedLines(1);

                ZebraPrinter.ESCPOS_PrintText("ImageType\t:\tPNG\n");
                ZebraPrinter.ESCPOS_PrintText("vEnlarge\t:\t2\n");
                ZebraPrinter.ESCPOS_PrintText("hEnlarge\t:\t2\n");
                ZebraPrinter.ESCPOS_PrintText("Alignment\t:\tright\n");
                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_RIGHT);
                ZebraPrinter.ESCPOS_PrintImage(bitmap, 3);
                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
                ZebraPrinter.ESCPOS_FeedLines(1);

                do {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (1 == ZebraPrinter.GetPrinterState());

                runOnUiThread(() -> progressDialog.dismiss());

            }
        }
    }

    private void handlePrintPictureZoom(Bitmap bitmap) {
        int printerState;
        Log.i(TAG, "onBtnPrinterClick: btnPrintImage");

        printerState = ZebraPrinter.GetPrinterState();
        if (2 == printerState || 3 == printerState) {
            runOnUiThread(() -> Toast.makeText(MobilePrinterActivity.this, R.string.printer_error_not_print, Toast.LENGTH_SHORT).show());
            return;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
//      Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.zebraicon, options);

        if (bOpen) {
            if (bLanguageCPCL) {
                // CPCL btnPrintImage
                Log.i(TAG, "onBtnPrinterClick: bLanguageCPCL");

                runOnUiThread(() -> {
                    progressDialog = new ProgressDialog(MobilePrinterActivity.this);
                    progressDialog.setMessage(getString(R.string.printing));
                    progressDialog.show();
                });

                ZebraPrinter.CPCL_PrinterInit();
                ZebraPrinter.CPCL_SetDensity(75);

//                ZebraPrinter.CPCL_SetDensity(150);
//                ZebraPrinter.CPCL_SetFont4LineMode("7", 0, 20);
                ZebraPrinter.CPCL_SetPageWidth(pageWidth);
//                ZebraPrinter.CPCL_PrintText4LineMode("--------------------------------\r\n");
//                ZebraPrinter.CPCL_SetRelativePosition4LineMode(0, 0);
//                ZebraPrinter.CPCL_PrintText4LineMode("CPCL PrintImageInLabelMode D:0\r\n");
//                ZebraPrinter.CPCL_PrintText4LineMode("--------------------------------\r\n");

                {
//                    ZebraPrinter.CPCL_CreateLabel(0, 600, 1);
                    ZebraPrinter.CPCL_CreateLabel(0, 1000, 1);
                    ZebraPrinter.CPCL_SetPageWidth(pageWidth);
//                    ZebraPrinter.CPCL_PrintBox(0, 0, pageWidth, 600, 2);
//                    ZebraPrinter.CPCL_PrintBox(0, 0, pageWidth, 400, 2);
                    //打印方框
//                    ZebraPrinter.CPCL_PrintBox(100, 100, pageWidth, 400, 2);
                    ZebraPrinter.CPCL_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);

//                    ZebraPrinter.CPCL_PrintText("7", 0, 10, 10, ZebraPrinter.CPCL_ROTATION0, "PNG Picture Rotation 0");
//                    ZebraPrinter.CPCL_PrintImage(50, 70, ZebraPrinter.CPCL_ROTATION0, bitmap);
//                    ZebraPrinter.CPCL_PrintImage(0, 0, ZebraPrinter.CPCL_ROTATION0, bitmap);
                    ZebraPrinter.CPCL_PrintImage(0, 0, ZebraPrinter.CPCL_ROTATION0, bitmap);
                    ZebraPrinter.ESCPOS_PrintImage(bitmap,3);
//                    ZebraPrinter.CPCL_PrintText("7", 0, 10, 590, ZebraPrinter.CPCL_ROTATION90, "PNG Picture Rotation 90");
//                    ZebraPrinter.CPCL_PrintImage(50, 520, ZebraPrinter.CPCL_ROTATION90, bitmap);
//
//                    ZebraPrinter.CPCL_PrintText("7", 0, 344, 590, ZebraPrinter.CPCL_ROTATION180, "PNG Picture Rotation 0");
//                    ZebraPrinter.CPCL_PrintImage(210, 490, ZebraPrinter.CPCL_ROTATION0, bitmap);
//
//                    ZebraPrinter.CPCL_PrintText("7", 0, pageWidth - 14, 100, ZebraPrinter.CPCL_ROTATION270, "PNG Picture Rotation 90");
//                    ZebraPrinter.CPCL_PrintImage(pageWidth - 124, 280, ZebraPrinter.CPCL_ROTATION90, bitmap);

                    ZebraPrinter.CPCL_PostFeed(80);
                    ZebraPrinter.CPCL_Print();
                }

                do {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (1 == ZebraPrinter.GetPrinterState());

                runOnUiThread(() -> progressDialog.dismiss());

            } else if (bLanguageESCPOS) {

                // ESCPOS btnPrintImage
                Log.i(TAG, "onBtnPrinterClick: bLanguageESCPOS");

                runOnUiThread(() -> {
                    progressDialog = new ProgressDialog(MobilePrinterActivity.this);
                    progressDialog.setMessage(getString(R.string.printing));
                    progressDialog.show();
                });

                ZebraPrinter.ESCPOS_PrinterInit();
                ZebraPrinter.ESCPOS_SetTextFont(0);
                ZebraPrinter.ESCPOS_SelectLineMode();
                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
                ZebraPrinter.ESCPOS_PrintLine(pageWidth, 1);
                ZebraPrinter.ESCPOS_PrintText("ESCPOS PrintImageInLineMode\n");
                ZebraPrinter.ESCPOS_PrintLine(pageWidth, 1);
                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);

                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_REVERSE, 0);
                ZebraPrinter.ESCPOS_SetTextFont(0);
                ZebraPrinter.ESCPOS_PrintText("Image\n");
                ZebraPrinter.ESCPOS_SetTextStyle(ZebraPrinter.TEXT_STYLE_DEFAULT, 0);
                byte[] tabPositionList = {15, 20};
                ZebraPrinter.ESCPOS_SetTabPosition(false, tabPositionList);

                ZebraPrinter.ESCPOS_PrintText("ImageType\t:\tPNG\n");
                ZebraPrinter.ESCPOS_PrintText("vEnlarge\t:\t1\n");
                ZebraPrinter.ESCPOS_PrintText("hEnlarge\t:\t1\n");
                ZebraPrinter.ESCPOS_PrintText("Alignment\t:\tleft\n");
                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
                ZebraPrinter.ESCPOS_PrintImage(bitmap, 0);
                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
                ZebraPrinter.ESCPOS_FeedLines(1);

                ZebraPrinter.ESCPOS_PrintText("ImageType\t:\tPNG\n");
                ZebraPrinter.ESCPOS_PrintText("vEnlarge\t:\t2\n");
                ZebraPrinter.ESCPOS_PrintText("hEnlarge\t:\t1\n");
                ZebraPrinter.ESCPOS_PrintText("Alignment\t:\tcentre\n");
                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_CENTRE);
                ZebraPrinter.ESCPOS_PrintImage(bitmap, 1);
                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
                ZebraPrinter.ESCPOS_FeedLines(1);

                ZebraPrinter.ESCPOS_PrintText("ImageType\t:\tPNG\n");
                ZebraPrinter.ESCPOS_PrintText("vEnlarge\t:\t2\n");
                ZebraPrinter.ESCPOS_PrintText("hEnlarge\t:\t2\n");
                ZebraPrinter.ESCPOS_PrintText("Alignment\t:\tright\n");
                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_RIGHT);
                ZebraPrinter.ESCPOS_PrintImage(bitmap, 3);
                ZebraPrinter.ESCPOS_SetAlignment(ZebraPrinter.ALIGNMENT_LEFT);
                ZebraPrinter.ESCPOS_FeedLines(1);

                do {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (1 == ZebraPrinter.GetPrinterState());

                runOnUiThread(() -> progressDialog.dismiss());

            }
        }
    }

    private class DownloadImageFromServerAsyncTask extends AsyncTask<String, Void, Void> {
        WsResult ws_result;

        @Override
        protected Void doInBackground(String... params) {

//                ws_result = WebServiceUtil.Upload_Image_From_Mobile(imageName,bytes);
            ws_result = WebServiceUtil.Get_Image_From_Server(boxID);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (ws_result != null) {
                if (!ws_result.getResult()) {
                    //Toast.makeText(StockInActivity.this,ws_result.getErrorInfo(),Toast.LENGTH_LONG).show();
                    CommonUtil.ShowToast(MobilePrinterActivity.this, ws_result.getErrorInfo(), R.mipmap.warning);

                } else {
                    //Toast.makeText(StockInActivity.this,"入库完成",Toast.LENGTH_LONG).show();
                    CommonUtil.ShowToast(MobilePrinterActivity.this, "获取成功！", R.mipmap.smiley);
                    System.out.println("============== result = " + ws_result.getErrorInfo());
                    getBitMap(ws_result.getErrorInfo());
                }

            }
        }


    }

    private void getBitMap(String imageString) {
        //decode base64 string to image
        byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//        imageView.setImageBitmap(decodedImage);
        logoImageView.setImageBitmap(decodedImage);


        if (isCurrentPictureZoom){
            handlePrintPictureZoom(decodedImage);
        }else{
            handlePrintPicture(decodedImage);
        }
    }
}
