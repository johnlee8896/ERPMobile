package com.chinashb.www.mobileerp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;

import com.chinashb.www.mobileerp.permission.PermissionsUtil;
import com.chinashb.www.mobileerp.utils.PermissionGroupDefine;

import java.util.HashMap;
import java.util.Map;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.CustomScanView;

/***
 * @date 创建时间 2019/4/30 16:04
 * @author 作者: liweifeng
 * @description 二维码扫描页面
 */
public class QRCodeScanActivity extends BaseRootActivity implements QRCodeView.OnScanCallbackListener {

    private CustomScanView customScanView;
    private String mapKey;

    private static Map<String, OnQRScanListenerImpl> listenerMap = new HashMap<>(4);
    private OnQRScanListenerImpl callBackListener;
    private boolean hasResult;

    public static void startQRCodeScanner(final Context context, final OnQRScanListenerImpl onQRCodeScannerListener) {
        PermissionsUtil.requestPermission(context, PermissionGroupDefine.PERMISSION_CAMERA, new PermissionsUtil.OnPermissionCallbackImpl() {
            @Override
            public void onSuccess(String[] permission) {
                String key = System.currentTimeMillis() + "";
                Intent intent = new Intent(context, QRCodeScanActivity.class);
                intent.putExtra("Key", key);
                listenerMap.put(key, onQRCodeScannerListener);
                context.startActivity(intent);
            }
        });

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan_layout);
        customScanView = (CustomScanView) findViewById(R.id.scan_customScanView);
        customScanView.setDelegate(this);

        mapKey = getIntent().getStringExtra("Key");
        callBackListener = listenerMap.remove(mapKey);
    }

    @Override
    protected void onStart() {
        super.onStart();
        customScanView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
//        customScanView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT); // 打开前置摄像头开始预览，但是并未开始识别
        customScanView.startSpotAndShowRect(); // 显示扫描框，并开始识别
    }

    @Override
    protected void onStop() {
        customScanView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        customScanView.onDestroy(); // 销毁二维码扫描控件
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        vibrate();
        customScanView.startSpot(); // 开始识别
        //hasResult 解决扫描结果过快，频繁执行（跳转）的问题
        if (callBackListener != null && !hasResult) {
            hasResult = true;
            callBackListener.onScanQRCodeSuccess(result);
        }
        finish();
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
        String tipText = customScanView.getScanBoxView().getTipText();
        String ambientBrightnessTip = "\n环境过暗，请打开闪光灯";
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip)) {
                customScanView.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
            }
        } else {
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
                customScanView.getScanBoxView().setTipText(tipText);
            }
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e("QRCodeScanActivity", "打开相机出错");
    }
}
