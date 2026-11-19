package com.chinashb.www.mobileerp.printer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;

import java.util.ArrayList;
import java.util.Set;

/**
 * ZR328 Plus 蓝牙打印机查找：已配对 + 附近可发现的打印机。
 * 不用原来的 BTHDiscoveryActivity，因为那个只认固定类型码，会漏掉 ZR328 Plus。
 */
public class Zebra328PlusBTHDiscoveryActivity extends BaseActivity {
    public static final String EXTRA_DEVICE_ADDRESS = "printerMACAddress";

    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> devicesArrayAdapter;
    private TextView discoveryTextTips;
    private Button discoveryButton;
    private final ArrayList<BluetoothDevice> printerDevices = new ArrayList<>();
    private BluetoothDevice pendingPairingDevice;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null && device.getBondState() != BluetoothDevice.BOND_BONDED && isUnpairedPrinterCandidate(device)) {
                    addPrinterDevice(device);
                    discoveryTextTips.setText(R.string.discovery_printer_list);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                updateDiscoveryState(false);
                discoveryTextTips.setText(devicesArrayAdapter.getCount() == 0
                        ? getString(R.string.not_find_printer)
                        : getString(R.string.discovery_printer_list));
                if (devicesArrayAdapter.getCount() == 0) {
                    Toast.makeText(Zebra328PlusBTHDiscoveryActivity.this, R.string.not_find_printer, Toast.LENGTH_SHORT).show();
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                if (device != null && bondState == BluetoothDevice.BOND_BONDED) {
                    addPrinterDevice(device);
                    Toast.makeText(Zebra328PlusBTHDiscoveryActivity.this, "配对成功，请选择打印机", Toast.LENGTH_SHORT).show();
                    if (pendingPairingDevice != null
                            && pendingPairingDevice.getAddress().equals(device.getAddress())) {
                        pendingPairingDevice = null;
                        returnSelectedDevice(device);
                    }
                } else if (device != null && bondState == BluetoothDevice.BOND_NONE
                        && pendingPairingDevice != null
                        && pendingPairingDevice.getAddress().equals(device.getAddress())) {
                    pendingPairingDevice = null;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_print_discovery_layout);

        ListView listViewPrinterDevice = findViewById(R.id.listPrinterDevices);
        devicesArrayAdapter = new ArrayAdapter<>(this, R.layout.item_print_device_name);
        listViewPrinterDevice.setAdapter(devicesArrayAdapter);
        listViewPrinterDevice.setOnItemClickListener((av, v, position, id) -> {
            if (position < 0 || position >= printerDevices.size()) {
                return;
            }
            BluetoothDevice device = printerDevices.get(position);
            if (bluetoothAdapter != null) {
                bluetoothAdapter.cancelDiscovery();
            }
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                returnSelectedDevice(device);
            } else {
                boolean startBond = false;
                try {
                    startBond = device.createBond();
                } catch (Exception e) {
                    Toast.makeText(this, "无法发起配对：" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                if (startBond) {
                    pendingPairingDevice = device;
                }
                Toast.makeText(this,
                        startBond ? "已发起配对，请在系统弹窗中确认" : "无法发起配对",
                        Toast.LENGTH_SHORT).show();
            }
        });

        discoveryButton = findViewById(R.id.btnBluetoothDiscovery);
        discoveryButton.setOnClickListener(view -> {
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                initBluetoothAdapter();
                return;
            }
            discoveryAllBTHPrinterDevices();
        });

        discoveryTextTips = findViewById(R.id.discoveryTextTips);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(receiver, filter);

        initBluetoothAdapter();
    }

    private void returnSelectedDevice(BluetoothDevice device) {
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DEVICE_ADDRESS, device.getAddress());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
        try {
            unregisterReceiver(receiver);
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BTHDiscoveryActivity.REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            initBluetoothAdapter();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initBluetoothAdapter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, R.string.bluetooth_not_support, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            updateDiscoveryState(false);
            discoveryTextTips.setText(R.string.paired_printer_list);
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, BTHDiscoveryActivity.REQUEST_ENABLE_BT);
        } else {
            loadPairedDevicesOnly();
        }
    }

    private void loadPairedDevicesOnly() {
        devicesArrayAdapter.clear();
        printerDevices.clear();
        updateDiscoveryState(false);
        discoveryTextTips.setVisibility(View.VISIBLE);
        discoveryTextTips.setText(R.string.paired_printer_list);
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices == null || pairedDevices.isEmpty()) {
            discoveryTextTips.setText(getString(R.string.paired_printer_list) + "：暂无");
            return;
        }
        for (BluetoothDevice device : pairedDevices) {
            if (isPrinterDevice(device)) {
                addPrinterDevice(device);
            }
        }
    }

    private void discoveryAllBTHPrinterDevices() {
        updateDiscoveryState(true);
        discoveryTextTips.setVisibility(View.VISIBLE);
        discoveryTextTips.setText(getString(R.string.scanning));
        devicesArrayAdapter.clear();
        printerDevices.clear();
        loadPairedDevicesOnly();
        discoveryTextTips.setText(getString(R.string.scanning));
        updateDiscoveryState(true);
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        try {
            boolean started = bluetoothAdapter.startDiscovery();
            if (!started) {
                updateDiscoveryState(false);
                Toast.makeText(this, "无法开始扫描，请确认蓝牙已开启", Toast.LENGTH_LONG).show();
            }
        } catch (SecurityException e) {
            updateDiscoveryState(false);
            Toast.makeText(this, "缺少蓝牙扫描权限", Toast.LENGTH_LONG).show();
        }
    }

    private void updateDiscoveryState(boolean scanning) {
        setProgressBarIndeterminateVisibility(scanning);
        if (discoveryButton != null) {
            discoveryButton.setEnabled(!scanning);
            discoveryButton.setText(scanning ? getString(R.string.scanning) : getString(R.string.discovery_printer));
        }
    }

    private void addPrinterDevice(BluetoothDevice device) {
        if (!printerDevices.contains(device)) {
            printerDevices.add(device);
            String name = TextUtils.isEmpty(device.getName()) ? "未知设备" : device.getName();
            devicesArrayAdapter.add(name + "\n" + device.getAddress());
        }
    }

    private boolean isPrinterDevice(BluetoothDevice device) {
        BluetoothClass bluetoothClass = device.getBluetoothClass();
        if (bluetoothClass != null) {
            int major = bluetoothClass.getMajorDeviceClass();
            if (major == BluetoothClass.Device.Major.IMAGING
                    || major == BluetoothClass.Device.Major.PERIPHERAL) {
                return true;
            }
        }
        String name = device.getName();
        if (!TextUtils.isEmpty(name)) {
            String lower = name.toLowerCase();
            return lower.contains("zebra")
                    || lower.contains("zr")
                    || lower.contains("printer")
                    || name.contains("打印");
        }
        return false;
    }

    private boolean isUnpairedPrinterCandidate(BluetoothDevice device) {
        BluetoothClass bluetoothClass = device.getBluetoothClass();
        if (bluetoothClass != null) {
            int major = bluetoothClass.getMajorDeviceClass();
            if (major == BluetoothClass.Device.Major.PHONE
                    || major == BluetoothClass.Device.Major.COMPUTER
                    || major == BluetoothClass.Device.Major.AUDIO_VIDEO
                    || major == BluetoothClass.Device.Major.WEARABLE
                    || major == BluetoothClass.Device.Major.HEALTH) {
                return false;
            }
        }
        return true;
    }
}
