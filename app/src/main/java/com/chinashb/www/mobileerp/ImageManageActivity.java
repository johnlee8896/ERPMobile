package com.chinashb.www.mobileerp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.ImageView;

import com.chinashb.www.mobileerp.widget.MyImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/***
 * @date 创建时间 2023/3/26 6:26 PM
 * @author 作者: liweifeng
 * @description
 */
public class ImageManageActivity extends BaseActivity {
    private ImageView imageView;
    private Button button;
    private MyImageView myImageView;
    private Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    System.out.println("111");
                    Bitmap bmp = (Bitmap) msg.obj;
                    imageView.setImageBitmap(bmp);
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_manage_layout);
        imageView = findViewById(R.id.image_manage_image_view);
        myImageView = findViewById(R.id.image_manage_my_image_view);
        button = findViewById(R.id.image_manage_button);
        //新建线程加载图片信息，发送到消息队列中
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                Bitmap bmp = getURLimage("http://60.172.145.222:8060/download/wobiyuanyou.png");
//                Message msg = new Message();
//                msg.what = 0;
//                msg.obj = bmp;
//                System.out.println("000");
//                handle.sendMessage(msg);
//            }
//        }).start();

        button.setOnClickListener(v -> {
                myImageView.setImageURL("http://60.172.145.222:8060/download/wobiyuanyou.png");

        });

    }

    //加载图片
    public Bitmap getURLimage(String url) {
        Bitmap bmp = null;
        try {
            URL myurl = new URL(url);
            // 获得连接
            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
            conn.setConnectTimeout(6000);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(false);//不缓存
            conn.connect();
            InputStream is = conn.getInputStream();//获得图片的数据流
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }
}
