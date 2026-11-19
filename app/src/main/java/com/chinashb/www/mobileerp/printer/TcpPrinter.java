package com.chinashb.www.mobileerp.printer;

import com.zebra.printer.sdk.connection.PrinterBase;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Zebra ZR328 Plus WiFi（TCP 6101）连接实现。
 * 复用现有 com.zebra.printer.sdk.connection.PrinterBase 接口，方便与 CPCL/ESCPOS/SGD 一起用。
 */
public class TcpPrinter implements PrinterBase {
    private final String ip;
    private final int port;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String lastError = "";

    public TcpPrinter(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getLastError() {
        return lastError;
    }

    @Override
    public int PrinterOpen() {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), 5000);
            socket.setSoTimeout(5000);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            lastError = "";
            return 0;
        } catch (Exception e) {
            lastError = "WiFi连接失败：" + e.getMessage();
            return -1;
        }
    }

    @Override
    public int PrinterClose() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
            return 0;
        } catch (Exception e) {
            return -1;
        } finally {
            outputStream = null;
            inputStream = null;
            socket = null;
        }
    }

    @Override
    public int PrinterWrite(byte[] data) {
        if (outputStream == null || data == null || data.length == 0) {
            lastError = "WiFi连接已断开";
            return -1;
        }
        try {
            outputStream.write(data);
            outputStream.flush();
            lastError = "";
            return 0;
        } catch (Exception e) {
            lastError = "WiFi写入失败：" + e.getMessage();
            return -1;
        }
    }

    @Override
    public byte[] PrinterRead(int size) {
        if (inputStream == null || size <= 0) {
            return null;
        }
        try {
            byte[] buffer = new byte[size];
            int len = inputStream.read(buffer);
            if (len <= 0) {
                return null;
            }
            byte[] result = new byte[len];
            System.arraycopy(buffer, 0, result, 0, len);
            return result;
        } catch (Exception e) {
            return null;
        }
    }
}
