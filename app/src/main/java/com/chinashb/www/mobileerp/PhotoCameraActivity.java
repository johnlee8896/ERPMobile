package com.chinashb.www.mobileerp;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2023/5/16 4:05 PM
 * @author 作者: liweifeng
 * @description 选取或拍照
 */
public class PhotoCameraActivity extends BaseActivity implements View.OnClickListener {

    public static final int TAKE_CAMERA = 101;
    public static final int PICK_PHOTO = 102;
    @BindView(R.id.btn_photo) Button btnPhoto;
    @BindView(R.id.btn_camera) Button btnCamera;
    @BindView(R.id.image_show) ImageView imageView;
    @BindView(R.id.btn_upload) Button btnUpload;
    private Uri imageUri;
    private  byte[] bytes;
    private String imageName = "";
    private String uploadString = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_camera_layout);
        ButterKnife.bind(this);
        setViewsListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_CAMERA:
                if (resultCode == RESULT_OK) {
                    try {
                        // 将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        imageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case PICK_PHOTO:
                if (resultCode == RESULT_OK) { // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
            default:
                break;
        }
    }

    private void setViewsListener() {
        btnPhoto.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content: //downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        // 根据图片路径显示图片
        displayImage(imagePath);
    }

    /**
     * android 4.4以前的处理方式
     *
     * @param data
     */
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

            bytes = byteArrayOutputStream.toByteArray();

//           uploadString = new String(bytes);


//            String tempString = new String(Base64 .encode(bytes,Base64.DEFAULT)) ;
//            ImageDispose.readStream()
            uploadString = new String(Base64 .encode(bytes,Base64.DEFAULT)) ;


        } else {
            Toast.makeText(this, "获取相册图片失败", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View view) {
        if (view == btnPhoto) {
//动态申请获取访问 读写磁盘的权限
            if (ContextCompat.checkSelfPermission(PhotoCameraActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PhotoCameraActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            } else {
                //打开相册
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //Intent.ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT"
                intent.setType("image/*");
                startActivityForResult(intent, PICK_PHOTO); // 打开相册
            }


        } else if (view == btnCamera) {
            // 创建File对象，用于存储拍照后的图片
            //存放在手机SD卡的应用关联缓存目录下
            File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
 /* 从Android 6.0系统开始，读写SD卡被列为了危险权限，如果将图片存放在SD卡的任何其他目录，
  都要进行运行时权限处理才行，而使用应用关联 目录则可以跳过这一步
 */
            try {
                if (outputImage.exists()) {
                    outputImage.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
 /*
  7.0系统开始，直接使用本地真实路径的Uri被认为是不安全的，会抛 出一个FileUriExposedException异常。
  而FileProvider则是一种特殊的内容提供器，它使用了和内 容提供器类似的机制来对数据进行保护，
  可以选择性地将封装过的Uri共享给外部，从而提高了 应用的安全性
  */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //大于等于版本24（7.0）的场合
                imageUri = FileProvider.getUriForFile(PhotoCameraActivity.this, "com.chinashb.www.mobileerp.fileprovider", outputImage);
            } else {
                //小于android 版本7.0（24）的场合
                imageUri = Uri.fromFile(outputImage);
            }

            //启动相机程序
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //MediaStore.ACTION_IMAGE_CAPTURE = android.media.action.IMAGE_CAPTURE
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, TAKE_CAMERA);

        }else if (view == btnUpload){
            if (bytes != null && bytes.length > 0){
                UploadImageAsyncTack task = new UploadImageAsyncTack();
                task.execute();
            }
        }
    }

    private class UploadImageAsyncTack extends AsyncTask<String, Void, Void> {
        WsResult ws_result;

        @Override
        protected Void doInBackground(String... params) {

//                ws_result = WebServiceUtil.Upload_Image_From_Mobile(imageName,bytes);
                ws_result = WebServiceUtil.Upload_Image_From_Mobile(imageName,uploadString);


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (ws_result != null) {
                if (!ws_result.getResult()) {
                    //Toast.makeText(StockInActivity.this,ws_result.getErrorInfo(),Toast.LENGTH_LONG).show();
                    CommonUtil.ShowToast(PhotoCameraActivity.this, ws_result.getErrorInfo(), R.mipmap.warning);

                } else {
                    //Toast.makeText(StockInActivity.this,"入库完成",Toast.LENGTH_LONG).show();
                    CommonUtil.ShowToast(PhotoCameraActivity.this, "入库完成", R.mipmap.smiley);
                }

            }
        }


    }
}
