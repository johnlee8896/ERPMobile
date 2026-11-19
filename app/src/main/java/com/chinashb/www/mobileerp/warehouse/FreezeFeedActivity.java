package com.chinashb.www.mobileerp.warehouse;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CommonSelectInputDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

/**
 * codex by john 2026-09-16 16:04 - 冻结投料处理
 */
public class FreezeFeedActivity extends BaseActivity {
    private Button scanSmallLabelButton;
    private Button addRemarkButton;
    private Button processLabelButton;
    private EditText inputEditText;
    private TextView remarkTextView;

    private String remark = "";
    private CommonSelectInputDialog remarkDialog;
    private final List<String> scanCodeList = new ArrayList<>();

    private final OnViewClickListener onViewClickListener = new OnViewClickListener() {
        @Override
        public <T> void onClickAction(View v, String tag, T t) {
            if (t != null) {
                remark = (String) t;
                remarkTextView.setText((CharSequence) t);
            }
            if (remarkDialog != null && remarkDialog.isShowing()) {
                remarkDialog.dismiss();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freeze_feed_layout);

        scanSmallLabelButton = findViewById(R.id.btn_freeze_scan_small_label);
        addRemarkButton = findViewById(R.id.btn_freeze_add_remark);
        processLabelButton = findViewById(R.id.btn_freeze_process_label);
        inputEditText = findViewById(R.id.freeze_feed_input_EditText);
        remarkTextView = findViewById(R.id.freeze_feed_remark_TextView);

        scanSmallLabelButton.setOnClickListener(v -> {
            new IntentIntegrator(FreezeFeedActivity.this)
                    .setCaptureActivity(CustomScannerActivity.class)
                    .initiateScan();
        });

        addRemarkButton.setOnClickListener(v -> {
            if (remarkDialog == null) {
                remarkDialog = new CommonSelectInputDialog(FreezeFeedActivity.this);
            }
            remarkDialog.show();
            remarkDialog.setSelectOnly(true);
            remarkDialog.setOnViewClickListener(onViewClickListener);
        });

        processLabelButton.setOnClickListener(v -> {
            // codex by john 2026-09-16 16:04 - 标签处理后续实现
            ToastUtil.showToastShort("标签处理待实现");
        });

        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            protected void onTextChangedSafe(CharSequence text) {
                if (text.toString().length() > 0) {
                    processScanResult(text.toString());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {
            processScanResult(result.getContents());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void processScanResult(String result) {
        if (result == null || !result.contains("/")) {
            // 输入不完整，不处理，避免打字过程中误弹提示
            return;
        }
        String[] qrContent = result.split("/");
        if (qrContent.length < 2) {
            return;
        }
        String qrTitle = qrContent[0];
        // codex by john 2026-09-16 16:04 - 只处理 VE / V9 小标签，其它提示重扫
        if ("VE".equalsIgnoreCase(qrTitle) || "V9".equalsIgnoreCase(qrTitle)) {
            scanCodeList.add(result);
            System.out.println("============冻结投料扫描到小标签:" + result);
            ToastUtil.showToastShort("扫描成功");
        } else {
            ToastUtil.showToastShort("请扫小包装标签");
        }
    }

    @Override
    protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }
}
