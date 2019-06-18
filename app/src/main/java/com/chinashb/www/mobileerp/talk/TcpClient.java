package com.chinashb.www.mobileerp.talk;

import android.content.Intent;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Jason Zhu on 2017-04-25.
 * Email: cloud_happy@163.com
 */

public class TcpClient implements Runnable {
    private String TAG = "TcpClient";
    private String serverIP = "192.168.88.141";
    private int serverPort = 1234;
    private PrintWriter pw;
    private InputStream is;
    private DataInputStream dis;
    private boolean isRun = true;
    private Socket socket = null;
    byte buff[]  = new byte[4096];
    private String rcvMsg;
    private int rcvLen;



    public TcpClient(String ip , int port){
        this.serverIP = ip;
        this.serverPort = port;
    }

    public void closeSelf(){

        isRun = false;
    }

    public void send(String msg){
        pw.println(msg);
        pw.flush();
    }

    @Override
    public void run() {
        try {
            socket = new Socket(serverIP,serverPort);
            socket.setSoTimeout(5000);
            pw = new PrintWriter(socket.getOutputStream(),true);
            is = socket.getInputStream();
            dis = new DataInputStream(is);
        } catch (IOException e) {
            e.printStackTrace();
            //没有连上，停止
            isRun=false;
        }

        Intent connectedintent =new Intent();
        connectedintent.setAction("tcpClientConnected");
        FuncTcpClient.context.sendBroadcast(connectedintent);//将消息发送给主界面

        while (isRun){
            try {
                if(dis!=null)
                {
                    rcvLen = dis.read(buff);
                    if(rcvLen==0)
                    {
                        continue;
                    }
                    rcvMsg = new String(buff,0,rcvLen,"utf-8");
                    Log.i(TAG, "run: 收到消息:"+ rcvMsg);
                    Intent intent =new Intent();
                    intent.setAction("tcpClientReceiver");
                    intent.putExtra("tcpClientReceiver",rcvMsg);
                    FuncTcpClient.context.sendBroadcast(intent);//将消息发送给主界面
                    if (rcvMsg.equals("QuitClient")){   //服务器要求客户端结束
                        isRun = false;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                //Somewrong disconnected
                isRun=false;
            }

        }
        try {
            if(pw !=null)
            {pw.close();}
            if(is!=null)
            {is.close();}
            if(dis!=null)
            {dis.close();}
            if(socket!=null)
            {socket.close();}
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent connectionlostintent =new Intent();
        connectionlostintent.setAction("tcpClientConnectionLost");
        FuncTcpClient.context.sendBroadcast(connectionlostintent);//将消息发送给主界面
    }


}
