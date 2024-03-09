package com.chinashb.www.mobileerp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class WebserviceActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private final String NAMESCROPE = "http://mywebservice.cn/";
    private final String METHOD_NAME = "uploadResume";
    private final String URL = "http://192.168.1.18/3g/WebService.asmx";
    private final String SOAP_ACTION = "http://mywebservice.cn/uploadResume";
    Handler handler = null;    //进程中调用view不安全
    String str1 = "";   //返回调用值
    private Button btn;
    private TextView txt1;
    Runnable runnableUi = new Runnable() {      //给文本框设值
        @Override
        public void run() {                  //更新界面
            txt1.setText("uploadImage(filename,image)=" + str1 + " 成功!");
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//        txt1 = (TextView) findViewById(R.id.txt1);
//        btn = (Button) findViewById(R.id.btnok);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final String filename = "/sdcard/DCIM/012.3gp";
                handler = new Handler();
                @SuppressWarnings("unused")
                Thread webserviceThread = new Thread() {
                    @Override
                    public void run() {
                        uploadTest(filename);
                    }
                };
                webserviceThread.start();
            }
        });
    }

    private void uploadTest(String filename) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd_hhmmss");
        String file1 = sDateFormat.format(new java.util.Date()) + filename.substring(filename.indexOf("."));
        try {
            FileInputStream fis = new FileInputStream(filename);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[100 * 1024];
            int count = 0;
            int i = 0;
            while ((count = fis.read(buffer)) >= 0) {
                String uploadBuffer = new String(Base64.encode(buffer, 0, count, Base64.DEFAULT));
                showServerice(uploadBuffer, file1, i);  //续传
                for (int j = 0; j < 1000; j++) {
                    ;
                }
                i++;
            }
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showServerice(String image, String file1, int tag) {
        SoapObject request = new SoapObject(NAMESCROPE, METHOD_NAME);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        try {
            request.addProperty("filename", file1);
            request.addProperty("image", image);
            request.addProperty("tag", tag);
            envelope.bodyOut = request;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
        } catch (Exception e1) {    // TODO Auto-generated catch block
            Log.e("Error", "错误1");
        }
        HttpTransportSE ht = new HttpTransportSE(URL);
        ht.debug = true;
        try {
            ht.call(SOAP_ACTION, envelope);
            SoapObject result = (SoapObject) envelope.bodyIn;
            str1 = result.getProperty(0).toString();
            txt1.setText("uploadImage(filename,image)=" + str1 + " 成功!"); //在进程中不安全，要加入Runnable
            handler.post(runnableUi);
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
        }
    }

}
