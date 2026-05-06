package com.chinashb.www.mobileerp.task;

/***
 * @date 创建时间 2/28/26 9:55 PM
 * @author 作者: liweifeng
 * @description
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.utils.ToastUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TaskCreateSimpleActivity extends BaseActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int MAX_IMAGES = 3;

    private EditText etTitle, etContent;
    private LinearLayout llImageContainer;
    private List<Uri> imageUris = new ArrayList<>();
    private List<String> base64ImageList = new ArrayList<>(); // 存储Base64字符串
    private ImageButton btnAddImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_create_new_layout);

        // 初始化视图
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        llImageContainer = findViewById(R.id.ll_image_container);
        btnAddImage = findViewById(R.id.btn_add_image);
        Button btnSubmit = findViewById(R.id.btn_submit);

        // 添加图片按钮点击事件
        btnAddImage.setOnClickListener(v -> showImagePickerDialog());

        // 提交按钮点击事件
        btnSubmit.setOnClickListener(v -> submitTask());
    }

    private void showImagePickerDialog() {
        if (imageUris.size() >= MAX_IMAGES) {
            Toast.makeText(this, "最多只能添加三张图片", Toast.LENGTH_SHORT).show();
            return;
        }

        // 实际应用中应使用对话框选择来源
        // 这里简化处理：直接打开相册
        openGallery();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = null;

            if (requestCode == PICK_IMAGE_REQUEST) {
                selectedImageUri = data.getData();
            } else if (requestCode == CAMERA_REQUEST) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                // 将Bitmap保存到临时文件并获取URI
                selectedImageUri = Uri.parse(MediaStore.Images.Media.insertImage(
                        getContentResolver(), imageBitmap, "title", null));
            }

            if (selectedImageUri != null) {
                addImageToContainer(selectedImageUri);
            }
        }
    }

    private void addImageToContainer(Uri imageUri) {
        if (imageUris.size() >= MAX_IMAGES) {
            return;
        }

        imageUris.add(imageUri);

        // 将图片转换为Base64字符串
        String base64String = convertImageToBase64(imageUri);
        if (base64String != null) {
            base64ImageList.add(base64String);
        } else {
            Toast.makeText(this, "图片转换失败", Toast.LENGTH_SHORT).show();
            imageUris.remove(imageUri); // 转换失败则移除
            return;
        }

        // 创建图片视图
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80, 80);
        params.setMargins(0, 0, 8, 0);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageURI(imageUri);

        // 添加删除按钮
        ImageButton deleteBtn = new ImageButton(this);
        deleteBtn.setLayoutParams(new LinearLayout.LayoutParams(20, 20));
        deleteBtn.setBackgroundResource(android.R.drawable.ic_delete);
        int position = imageUris.size() - 1; // 当前图片位置
        deleteBtn.setOnClickListener(v -> removeImage(imageView, position));

        // 包裹容器
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.addView(imageView);
        itemLayout.addView(deleteBtn);

        // 添加到主容器（在添加按钮之前插入）
        llImageContainer.addView(itemLayout, llImageContainer.getChildCount() - 1);
    }

    // 将图片URI转换为Base64字符串
    private String convertImageToBase64(Uri imageUri) {
        try {
            // 获取输入流
            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(imageUri);
                if (inputStream == null) {
                    return null;
                }

                // 解码图片尺寸
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(inputStream, null, options);
                inputStream.close();

                // 计算缩放比例（限制最大尺寸）
                int maxDimension = 1024;
                int scaleFactor = Math.min(options.outWidth / maxDimension, options.outHeight / maxDimension);
                scaleFactor = Math.max(scaleFactor, 1);

                // 加载并缩放图片
                options.inJustDecodeBounds = false;
                options.inSampleSize = scaleFactor;
                inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                inputStream.close();

                if (bitmap == null) {
                    return null;
                }

                // 压缩图片质量
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                byte[] imageBytes = outputStream.toByteArray();
                bitmap.recycle(); // 释放内存

                // 转换为Base64字符串
                return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            Log.e("ImageConversion", "Error converting image to Base64", e);
            return null;
        }
        return null;
    }

    private void removeImage(ImageView imageView, int position) {
        // 从视图中移除
        llImageContainer.removeView((View) imageView.getParent());

        // 从数据列表中移除
        if (position < imageUris.size()) {
            imageUris.remove(position);
        }
        if (position < base64ImageList.size()) {
            base64ImageList.remove(position);
        }

        // 更新后续图片的位置
        updateImagePositions();
    }

    // 更新所有图片的位置标识
    private void updateImagePositions() {
        for (int i = 0; i < llImageContainer.getChildCount() - 1; i++) {
            LinearLayout itemLayout = (LinearLayout) llImageContainer.getChildAt(i);
            ImageButton deleteBtn = (ImageButton) itemLayout.getChildAt(1);
            int position = i;
            deleteBtn.setOnClickListener(v -> removeImage((ImageView) itemLayout.getChildAt(0), position));
        }
    }

    private void submitTask() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "请输入标题", Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取Base64图片列表
        List<String> imagesToUpload = new ArrayList<>(base64ImageList);

        // 这里处理提交逻辑
        Log.d("TaskCreate", "Title: " + title);
        Log.d("TaskCreate", "Content: " + content);
        Log.d("TaskCreate", "Images: " + imagesToUpload.size() + " items");

        // 示例：打印第一张图片的Base64长度
        if (!imagesToUpload.isEmpty()) {
            Log.d("TaskCreate", "First image Base64 length: " + imagesToUpload.get(0).length());
        }

        Toast.makeText(this, "任务创建成功！图片数: " + imagesToUpload.size(), Toast.LENGTH_SHORT).show();

        // 实际应用中应保存数据并返回
//        finish();
        CommitSingleTaskAsyncTask asyncTask = new CommitSingleTaskAsyncTask(title,content,imagesToUpload);
        asyncTask.execute();
    }

    private class CommitSingleTaskAsyncTask extends AsyncTask<String,Void,Void>{
        WsResult wsResult = null;
        String title;
        String content;
        List<String> imageList;

        // 通过构造函数传递参数
        public CommitSingleTaskAsyncTask(String title, String content, List<String> imageList) {
            this.title = title;
            this.content = content;
            this.imageList = imageList;
        }

        @Override
        protected Void doInBackground(String... strings) {
//            String title = strings[0];
//            String content = strings[1];
//            String imageListString = strings[2];
//            if
            wsResult = WebServiceUtil.opCommitEOSSingleTaskFromMobile(title,content,imageList);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (wsResult != null && wsResult.getResult()){

                ToastUtil.showToastShort("任务提交成功！");
            }else{
                ToastUtil.showToastShort("任务提交失败！原因：" + wsResult.getErrorInfo());
            }
        }
    }
}