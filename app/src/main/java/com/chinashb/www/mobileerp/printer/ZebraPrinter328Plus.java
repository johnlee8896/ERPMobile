package com.chinashb.www.mobileerp.printer;

import com.zebra.printer.sdk.connection.PrinterBase;
import com.zebra.printer.sdk.language.CPCL;
import com.zebra.printer.sdk.language.ESCPOS;
import com.zebra.printer.sdk.language.SGD;

import java.io.ByteArrayOutputStream;

/**
 * Zebra ZR328 Plus 蓝牙 + WiFi 打印工具。
 * 沿用项目里已有的 ZebraPrinter_Android_SDK 的 CPCL/ESCPOS/SGD 指令，
 * 只新增 WiFi(TCP) 连接，避免重新下载官方 Link-OS SDK。
 *
 * 用法：
 * ZebraPrinter328Plus p = new ZebraPrinter328Plus();
 * p.openBluetooth("00:11:22:33:44:55"); // 或 p.openWifi("192.168.1.100", 6101);
 * ByteArrayOutputStream out = new ByteArrayOutputStream();
 * p.cpcl.CreateLabel(2, 0, 0, 576, 300, out); // 按需调用 CPCL 指令
 * p.cpcl.AddText(...);
 * p.cpcl.Print(out);
 * p.write(out);
 * p.close();
 */
public class ZebraPrinter328Plus {
    public static final int CONNECTION_BLUETOOTH = 0;
    public static final int CONNECTION_WIFI = 1;

    private PrinterBase printer;
    private String lastOpenError = "";
    public final CPCL cpcl = new CPCL();
    public final ESCPOS escpos = new ESCPOS();
    public final SGD sgd = new SGD();

    public int openBluetooth(String macAddress) {
        lastOpenError = "";
        BluetoothSppPrinter bluetoothPrinter = new BluetoothSppPrinter(macAddress);
        int ret = bluetoothPrinter.PrinterOpen();
        if (ret == 0) {
            printer = bluetoothPrinter;
        } else {
            lastOpenError = bluetoothPrinter.getLastError();
        }
        return ret;
    }

    public int openWifi(String ip, int port) {
        lastOpenError = "";
        TcpPrinter tcpPrinter = new TcpPrinter(ip, port);
        int ret = tcpPrinter.PrinterOpen();
        if (ret == 0) {
            printer = tcpPrinter;
        } else {
            lastOpenError = tcpPrinter.getLastError();
        }
        return ret;
    }

    public int close() {
        if (printer == null) {
            return -1;
        }
        int ret = printer.PrinterClose();
        printer = null;
        return ret;
    }

    public int write(ByteArrayOutputStream outputStream) {
        if (printer == null || outputStream == null) {
            return -1;
        }
        return printer.PrinterWrite(outputStream.toByteArray());
    }

    public int write(byte[] data) {
        if (printer == null || data == null || data.length == 0) {
            return -1;
        }
        return printer.PrinterWrite(data);
    }

    public byte[] read(int size) {
        if (printer == null) {
            return null;
        }
        return printer.PrinterRead(size);
    }

    public int sgdGet(String var, StringBuffer value) {
        if (printer == null) {
            return -1;
        }
        return sgd.GetVar(printer, var, value);
    }

    public int sgdSet(String var, String value) {
        if (printer == null) {
            return -1;
        }
        return sgd.SetVar(printer, var, value);
    }

    public boolean isConnected() {
        return printer != null;
    }

    public String getLastError() {
        if (printer == null) {
            return lastOpenError;
        }
        if (printer instanceof BluetoothSppPrinter) {
            return ((BluetoothSppPrinter) printer).getLastError();
        }
        if (printer instanceof TcpPrinter) {
            return ((TcpPrinter) printer).getLastError();
        }
        return "";
    }

    public String getHandshakeInfo() {
        if (printer instanceof BluetoothSppPrinter) {
            return ((BluetoothSppPrinter) printer).getHandshakeInfo();
        }
        return "";
    }
}
