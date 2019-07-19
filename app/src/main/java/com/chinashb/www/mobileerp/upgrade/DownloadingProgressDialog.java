package com.chinashb.www.mobileerp.upgrade;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;

/***
 * @date 创建时间 2018/5/23 10:53
 * @author 作者: W.YuLong
 * @description 下载进度的对话框
 */
class DownloadingProgressDialog extends Dialog {
    private TextView descTextView;
    private ProgressBar progressBar;

    public DownloadingProgressDialog(@NonNull Context context) {
        super(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_downloading_progress_layout);
        descTextView = findViewById(R.id.dialog_downloading_desc_TextView);
        progressBar = findViewById(R.id.dialog_downloading_ProgressBar);

        configDialog();
    }

    protected void configDialog() {
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void updateProgress(int progress, int max) {
        progressBar.setMax(max);
        int percent = (int) (progress * 100.0f / max);
        descTextView.setText("正在下载中..." + percent + "%");
        progressBar.setProgress(progress);
    }

}
