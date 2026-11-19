package com.chinashb.www.mobileerp.upgrade;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;

import java.util.Locale;

/**
 * codex by john 2026-11-12 - 无 CDN 下载进度对话框
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

    public void showConnecting() {
        progressBar.setIndeterminate(true);
        descTextView.setText("正在连接下载服务器...");
    }

    public void showVerifying() {
        progressBar.setIndeterminate(true);
        descTextView.setText("正在校验升级包...");
    }

    public void showMessage(String message) {
        progressBar.setIndeterminate(false);
        descTextView.setText(message);
    }

    public void updateProgress(long progress, long max) {
        if (max <= 0) {
            progressBar.setIndeterminate(true);
            descTextView.setText("正在连接下载服务器...");
            return;
        }
        progressBar.setIndeterminate(false);
        int safeMax = max > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) max;
        int safeProgress = progress > safeMax ? safeMax : (int) progress;
        progressBar.setMax(safeMax);
        progressBar.setProgress(safeProgress);
        int percent = (int) (progress * 100L / max);
        descTextView.setText(String.format(Locale.getDefault(),
                "正在下载中...%d%%  %s/%s",
                percent, formatBytes(progress), formatBytes(max)));
    }

    public void updateProgress(int progress, int max) {
        updateProgress((long) progress, (long) max);
    }

    private String formatBytes(long value) {
        if (value >= 1024L * 1024L) {
            return String.format(Locale.getDefault(), "%.1f MB",
                    value / (1024.0 * 1024.0));
        }
        if (value >= 1024L) {
            return String.format(Locale.getDefault(), "%.1f KB", value / 1024.0);
        }
        return value + " B";
    }

}
