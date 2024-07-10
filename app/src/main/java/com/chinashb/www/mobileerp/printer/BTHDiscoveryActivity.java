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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;

import java.util.Set;

public class BTHDiscoveryActivity extends BaseActivity {

    private BluetoothAdapter mBluetoothAdapter;

    private ArrayAdapter<String> mBTHPrinterDevicesArrayAdapter;

    public static final String EXTRA_DEVICE_ADDRESS = "printerMACAddress";
    public static final int REQUEST_ENABLE_BT = 2;

    private TextView DiscoveryTextTips = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_print_discovery_layout);

        ListView listViewPrinterDevice = findViewById(R.id.listPrinterDevices);

        // 初始化一个数组适配器，用来显示打印机的设备
        mBTHPrinterDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.item_print_device_name);
        listViewPrinterDevice.setAdapter(mBTHPrinterDevicesArrayAdapter);
        listViewPrinterDevice.setOnItemClickListener(mBTHPrinterDeviceClickListener);

        findViewById(R.id.btnBluetoothDiscovery).setOnClickListener(view -> {
            view.setVisibility(View.GONE);
            discoveryAllBTHPrinterDevices();
        });

        DiscoveryTextTips = findViewById(R.id.discoveryTextTips);


        // 设置广播信息过滤 并注册
        // Register for broadcasts when a device is discovered
        IntentFilter intentFilter;
        intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mBTHPrinterDeviceReceiver, intentFilter);
        // Register for broadcasts when discovery has finished
        intentFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mBTHPrinterDeviceReceiver, intentFilter);

        initBluetoothAdapter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Make sure we're not doing discovery anymore
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
        // Unregister broadcast listeners
        unregisterReceiver(mBTHPrinterDeviceReceiver);
    }

    /**
     * The on-click listener for all devices in the ListViews
     */
    private final OnItemClickListener mBTHPrinterDeviceClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();

            Log.i("TAG", info);
            mBluetoothAdapter.cancelDiscovery();
            //mac 地址
            String MACAddress = info.substring(info.length() - 17);
            Log.i("TAG", MACAddress);
            // Set result and finish this Activity
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, MACAddress);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };


    /**
     * 接收扫描设备的广播
     * changes the title when discovery is finished
     */
    private final BroadcastReceiver mBTHPrinterDeviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 每当发现一个蓝牙设备时
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                //获取设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed
                // 未匹对的情况下添加显示
                assert device != null;
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    int bthClassDevice = device.getBluetoothClass().getDeviceClass();
                    int bthClassDeviceMajor = device.getBluetoothClass().getMajorDeviceClass();
                    if ((BluetoothClass.Device.Major.IMAGING == bthClassDeviceMajor) && 1664 == bthClassDevice)
                        mBTHPrinterDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            // 扫描结束
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                findViewById(R.id.btnBluetoothDiscovery).setVisibility(View.VISIBLE);
                setTitle(getString(R.string.select_printer));
                if (mBTHPrinterDevicesArrayAdapter.getCount() == 0) {
                    Toast.makeText(BTHDiscoveryActivity.this, R.string.not_find_printer, Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    /**
     * 初始化蓝牙
     */
    private void initBluetoothAdapter() {
        // 获取蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // 检查蓝牙是否可用
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.bluetooth_not_support, Toast.LENGTH_SHORT).show();
        } else {
            // 检查蓝牙是否打开
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            } else {
                discoveryPairedBTHPrinterDevicesList();
            }
        }
    }

    /**
     * 查找蓝牙已配对设备
     */
    protected void discoveryPairedBTHPrinterDevicesList() {
        // 已配对数据
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // 添加一个item显示信息
        if (pairedDevices.size() > 0) {
            //遍历填充数据
            DiscoveryTextTips.setVisibility(View.VISIBLE);

            for (BluetoothDevice device : pairedDevices) {
                int bthClassDevice = device.getBluetoothClass().getDeviceClass();
                int bthClassDeviceMajor = device.getBluetoothClass().getMajorDeviceClass();
                if ((BluetoothClass.Device.Major.IMAGING == bthClassDeviceMajor) && 1664 == bthClassDevice)
                    mBTHPrinterDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    }


    /**
     * 扫描所有蓝牙设备
     */
    private void discoveryAllBTHPrinterDevices() {
        setProgressBarIndeterminateVisibility(true);
        setTitle(getString(R.string.scanning));
        DiscoveryTextTips.setVisibility(View.VISIBLE);
        DiscoveryTextTips.setText(R.string.discovery_printer_list);
        // 清除所有item区分显示信息
        mBTHPrinterDevicesArrayAdapter.clear();

        discoveryPairedBTHPrinterDevicesList();

        // If we're already discovering, stop it
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        // 开始扫描，每扫描到一个设备，都会发送一个广播
        mBluetoothAdapter.startDiscovery();
    }
}