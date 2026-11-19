package com.chinashb.www.mobileerp.printer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.zebra.printer.sdk.connection.PrinterBase;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Zebra ZR328 Plus 蓝牙 SPP 连接实现。
 * 不用原 SDK 里的 BluetoothPrinter，因为它会校验设备名必须是 ZR138/ZR118，
 * 导致 ZR328 Plus 连不上。
 */
public class BluetoothSppPrinter implements PrinterBase {
    private static final String TAG = "BluetoothSppPrinter";
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final String macAddress;
    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String lastError = "";
    private String handshakeInfo = "";

    public BluetoothSppPrinter(String macAddress) {
        this.macAddress = macAddress == null ? "" : macAddress.trim().toUpperCase();
    }

    public String getLastError() {
        return lastError;
    }

    public String getHandshakeInfo() {
        return handshakeInfo;
    }

    @Override
    public int PrinterOpen() {
        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (adapter == null) {
                lastError = "当前设备不支持蓝牙";
                return -10007;
            }
            if (!adapter.isEnabled()) {
                lastError = "蓝牙未开启";
                return -10008;
            }
            adapter.cancelDiscovery();
            BluetoothDevice device = adapter.getRemoteDevice(macAddress);
            BluetoothSocket candidate = device.createInsecureRfcommSocketToServiceRecord(SPP_UUID);
            String connectionMethod = "insecure";
            if (!connectSocket(candidate)) {
                try {
                    candidate.close();
                } catch (Exception ignored) {
                }
                candidate = device.createRfcommSocketToServiceRecord(SPP_UUID);
                connectionMethod = "secure";
            }
            if (!connectSocket(candidate)) {
                try {
                    candidate.close();
                } catch (Exception ignored) {
                }
                candidate = createRfcommChannel(device, 1);
                connectionMethod = "rfcomm-channel-1";
            }
            if (!connectSocket(candidate)) {
                try {
                    candidate.close();
                } catch (Exception ignored) {
                }
                lastError = "蓝牙连接超时或未配对";
                return -1;
            }
            socket = candidate;
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            handshakeInfo = "蓝牙SPP已连接(" + connectionMethod + ")";
            lastError = "";
            Log.i(TAG, "SPP connected: " + macAddress + ",method=" + connectionMethod);
            return 0;
        } catch (Exception e) {
            lastError = "蓝牙连接异常：" + e.getMessage();
            Log.e(TAG, "open failed", e);
            return -1;
        }
    }

    private BluetoothSocket createRfcommChannel(BluetoothDevice device, int channel) throws Exception {
        Method method = device.getClass().getMethod("createRfcommSocket", int.class);
        return (BluetoothSocket) method.invoke(device, channel);
    }

    private boolean connectSocket(BluetoothSocket socket) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            try {
                socket.connect();
            } catch (Exception ignored) {
            }
        });
        try {
            future.get(5000, TimeUnit.MILLISECONDS);
            return socket.isConnected();
        } catch (Exception e) {
            return false;
        } finally {
            executor.shutdownNow();
        }
    }

    private byte[] readAvailableResponse(int timeoutMs) {
        if (inputStream == null || timeoutMs <= 0) {
            return null;
        }

        ByteArrayOutputStream response = new ByteArrayOutputStream();
        long deadline = System.currentTimeMillis() + timeoutMs;
        long quietDeadline = deadline;
        boolean received = false;
        while (System.currentTimeMillis() < deadline) {
            try {
                int available = inputStream.available();
                if (available > 0) {
                    byte[] buffer = new byte[Math.min(available, 4096)];
                    int count = inputStream.read(buffer);
                    if (count > 0) {
                        response.write(buffer, 0, count);
                        received = true;
                        quietDeadline = System.currentTimeMillis() + 150;
                    }
                    continue;
                }
            } catch (Exception e) {
                lastError = "蓝牙读取失败：" + e.getMessage();
                return received ? response.toByteArray() : null;
            }
            if (received && System.currentTimeMillis() >= quietDeadline) {
                break;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return received ? response.toByteArray() : null;
    }

    @Override
    public int PrinterClose() {
        try {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception ignored) {
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception ignored) {
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception ignored) {
                }
            }
        } finally {
            outputStream = null;
            inputStream = null;
            socket = null;
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return 0;
    }

    @Override
    public int PrinterWrite(byte[] data) {
        if (outputStream == null || data == null || data.length == 0) {
            lastError = "蓝牙连接已断开";
            return -1;
        }
        try {
            outputStream.write(data);
            outputStream.flush();
            lastError = "";
            Log.i(TAG, "write ok, bytes=" + data.length);
            return 0;
        } catch (Exception e) {
            lastError = "蓝牙写入失败：" + e.getMessage();
            Log.e(TAG, "write failed, bytes=" + data.length, e);
            return -1;
        }
    }

    @Override
    public byte[] PrinterRead(int size) {
        return readAvailableResponse(size);
    }
}
