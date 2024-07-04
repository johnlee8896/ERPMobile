package com.chinashb.www.mobileerp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2024/6/28 2:36 PM
 * @author 作者: liweifeng
 * @description 从服务端获取图片，为手机打印作准备
 */
public class GetPhotoFromServerActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.getPhotoButton) Button getPhotoButton;
    @BindView(R.id.photo_imageView) ImageView imageView;
    private int boxID ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_photo_from_server_layout);
        ButterKnife.bind(this);
        getPhotoButton.setOnClickListener(this);
        boxID = 816847;

    }

    private void showImageFromString(String imageString){
        //decode base64 string to image
        byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        imageView.setImageBitmap(decodedImage);
    }

    @Override
    public void onClick(View v) {
        if (v == getPhotoButton){
            DownloadImageFromServerAsyncTask task = new DownloadImageFromServerAsyncTask();
            task.execute();
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
                    CommonUtil.ShowToast(GetPhotoFromServerActivity.this, ws_result.getErrorInfo(), R.mipmap.warning);

                } else {
                    //Toast.makeText(StockInActivity.this,"入库完成",Toast.LENGTH_LONG).show();
                    CommonUtil.ShowToast(GetPhotoFromServerActivity.this, "获取成功！", R.mipmap.smiley);
                    System.out.println("============== result = " + ws_result.getErrorInfo());
                    showImageFromString(ws_result.getErrorInfo());
                }

            }
        }


    }

//    private class UploadImageAsyncTack extends AsyncTask<String, Void, Void> {
//        WsResult ws_result;
//
//        @Override
//        protected Void doInBackground(String... params) {
//
////                ws_result = WebServiceUtil.Upload_Image_From_Mobile(imageName,bytes);
//            ws_result = WebServiceUtil.Upload_Image_From_Mobile(imageName,uploadString);
//
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            if (ws_result != null) {
//                if (!ws_result.getResult()) {
//                    //Toast.makeText(StockInActivity.this,ws_result.getErrorInfo(),Toast.LENGTH_LONG).show();
//                    CommonUtil.ShowToast(GetPhotoFromServerActivity.this, ws_result.getErrorInfo(), R.mipmap.warning);
//
//                } else {
//                    //Toast.makeText(StockInActivity.this,"入库完成",Toast.LENGTH_LONG).show();
//                    CommonUtil.ShowToast(GetPhotoFromServerActivity.this, "上传成功！", R.mipmap.smiley);
//                }
//
//            }
//        }
//
//
//    }
}
