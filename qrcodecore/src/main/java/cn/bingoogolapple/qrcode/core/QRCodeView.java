package cn.bingoogolapple.qrcode.core;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public abstract class QRCodeView extends RelativeLayout implements Camera.PreviewCallback {
    private static final int NO_CAMERA_ID = -1;
    protected Camera camera;
    protected CameraPreview cameraPreview;
    protected ScanBoxView scanBoxView;
    protected OnScanCallbackListener delegate;
    protected boolean spotAble = false;
    protected ProcessDataTask processDataTask;
    protected int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private PointF[] locationPoints;
    private Paint paint;
    protected BarcodeType barcodeType = BarcodeType.HIGH_FREQUENCY;
    private long lastPreviewFrameTime = 0;
    private ValueAnimator autoZoomAnimator;
    private long lastAutoZoomTime = 0;

    // 上次环境亮度记录的时间戳
    private long lastAmbientBrightnessRecordTime = System.currentTimeMillis();
    // 上次环境亮度记录的索引
    private int ambientBrightnessDarkIndex = 0;
    // 环境亮度历史记录的数组，255 是代表亮度最大值
    private static final long[] AMBIENT_BRIGHTNESS_DARK_LIST = new long[]{255, 255, 255, 255};
    // 环境亮度扫描间隔
    private static final int AMBIENT_BRIGHTNESS_WAIT_SCAN_TIME = 150;
    // 亮度低的阀值
    private static final int AMBIENT_BRIGHTNESS_DARK = 60;

    public QRCodeView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public QRCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
        setupReader();
    }

    private void initView(Context context, AttributeSet attrs) {
        cameraPreview = new CameraPreview(context);
        cameraPreview.setDelegate(new CameraPreview.Delegate() {
            @Override
            public void onStartPreview() {
                setOneShotPreviewCallback();
            }
        });

        scanBoxView = new ScanBoxView(context);
        scanBoxView.init(this, attrs);
        cameraPreview.setId(R.id.bgaqrcode_camera_preview);
        addView(cameraPreview);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(context, attrs);
        layoutParams.addRule(RelativeLayout.ALIGN_TOP, cameraPreview.getId());
        layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, cameraPreview.getId());
        addView(scanBoxView, layoutParams);

        paint = new Paint();
        paint.setColor(getScanBoxView().getCornerColor());
        paint.setStyle(Paint.Style.FILL);
    }

    private void setOneShotPreviewCallback() {
        if (spotAble && cameraPreview.isPreviewing()) {
            try {
                camera.setOneShotPreviewCallback(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract void setupReader();

    /**
     * 设置扫描二维码的代理
     *
     * @param delegate 扫描二维码的代理
     */
    public void setDelegate(OnScanCallbackListener delegate) {
        this.delegate = delegate;
    }

    public CameraPreview getCameraPreview() {
        return cameraPreview;
    }

    public ScanBoxView getScanBoxView() {
        return scanBoxView;
    }

    /**
     * 显示扫描框
     */
    public void showScanRect() {
        if (scanBoxView != null) {
            scanBoxView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏扫描框
     */
    public void hiddenScanRect() {
        if (scanBoxView != null) {
            scanBoxView.setVisibility(View.GONE);
        }
    }

    /**
     * 打开后置摄像头开始预览，但是并未开始识别
     */
    public void startCamera() {
        if (camera != null || Camera.getNumberOfCameras() == 0) {
            return;
        }
        int ultimateCameraId = findCameraIdByFacing(cameraId);
        if (ultimateCameraId != NO_CAMERA_ID) {
            startCameraById(ultimateCameraId);
            return;
        }

        if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            ultimateCameraId = findCameraIdByFacing(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } else if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            ultimateCameraId = findCameraIdByFacing(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
        if (ultimateCameraId != NO_CAMERA_ID) {
            startCameraById(ultimateCameraId);
        }
    }



    private int findCameraIdByFacing(int cameraFacing) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int cameraId = 0; cameraId < Camera.getNumberOfCameras(); cameraId++) {
            try {
                Camera.getCameraInfo(cameraId, cameraInfo);
                if (cameraInfo.facing == cameraFacing) {
                    return cameraId;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return NO_CAMERA_ID;
    }

    private void startCameraById(int cameraId) {
        try {
            this.cameraId = cameraId;
            camera = Camera.open(cameraId);
            cameraPreview.setCamera(camera);
        } catch (Exception e) {
            e.printStackTrace();
            if (delegate != null) {
                delegate.onScanQRCodeOpenCameraError();
            }
        }
    }

    /**
     * 关闭摄像头预览，并且隐藏扫描框
     */
    public void stopCamera() {
        try {
            stopSpotAndHiddenRect();
            if (camera != null) {
                cameraPreview.stopCameraPreview();
                cameraPreview.setCamera(null);
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始识别
     */
    public void startSpot() {
        spotAble = true;
        startCamera();
        setOneShotPreviewCallback();
    }

    /**
     * 停止识别
     */
    public void stopSpot() {
        spotAble = false;

        if (processDataTask != null) {
            processDataTask.cancelTask();
            processDataTask = null;
        }

        if (camera != null) {
            try {
                camera.setOneShotPreviewCallback(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 停止识别，并且隐藏扫描框
     */
    public void stopSpotAndHiddenRect() {
        stopSpot();
        hiddenScanRect();
    }

    /**
     * 显示扫描框，并开始识别
     */
    public void startSpotAndShowRect() {
        startSpot();
        showScanRect();
    }

    /**
     * 打开闪光灯
     */
    public void openFlashlight() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                cameraPreview.openFlashlight();
            }
        }, cameraPreview.isPreviewing() ? 0 : 500);
    }

    /**
     * 关闭闪光灯
     */
    public void closeFlashlight() {
        cameraPreview.closeFlashlight();
    }

    /**
     * 销毁二维码扫描控件
     */
    public void onDestroy() {
        stopCamera();
        delegate = null;
    }

    /**
     * 切换成扫描条码样式
     */
    public void changeToScanBarcodeStyle() {
        if (!scanBoxView.getIsBarcode()) {
            scanBoxView.setIsBarcode(true);
        }
    }

    /**
     * 切换成扫描二维码样式
     */
    public void changeToScanQRCodeStyle() {
        if (scanBoxView.getIsBarcode()) {
            scanBoxView.setIsBarcode(false);
        }
    }

    /**
     * 当前是否为条码扫描样式
     */
    public boolean getIsScanBarcodeStyle() {
        return scanBoxView.getIsBarcode();
    }

    @Override
    public void onPreviewFrame(final byte[] data, final Camera camera) {
        if (BGAQRCodeUtil.isDebug()) {
            BGAQRCodeUtil.d("两次 onPreviewFrame 时间间隔：" + (System.currentTimeMillis() - lastPreviewFrameTime));
            lastPreviewFrameTime = System.currentTimeMillis();
        }

        if (cameraPreview != null && cameraPreview.isPreviewing()) {
            try {
                handleAmbientBrightness(data, camera);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!spotAble || (processDataTask != null && (processDataTask.getStatus() == AsyncTask.Status.PENDING
                || processDataTask.getStatus() == AsyncTask.Status.RUNNING))) {
            return;
        }

        processDataTask = new ProcessDataTask(camera, data, this, BGAQRCodeUtil.isPortrait(getContext())).perform();
    }

    private void handleAmbientBrightness(byte[] data, Camera camera) {
        if (cameraPreview == null || !cameraPreview.isPreviewing()) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAmbientBrightnessRecordTime < AMBIENT_BRIGHTNESS_WAIT_SCAN_TIME) {
            return;
        }
        lastAmbientBrightnessRecordTime = currentTime;

        int width = camera.getParameters().getPreviewSize().width;
        int height = camera.getParameters().getPreviewSize().height;
        // 像素点的总亮度
        long pixelLightCount = 0L;
        // 像素点的总数
        long pixelCount = width * height;
        // 采集步长，因为没有必要每个像素点都采集，可以跨一段采集一个，减少计算负担，必须大于等于1。
        int step = 10;
        // data.length - allCount * 1.5f 的目的是判断图像格式是不是 YUV420 格式，只有是这种格式才相等
        //因为 int 整形与 float 浮点直接比较会出问题，所以这么比
        if (Math.abs(data.length - pixelCount * 1.5f) < 0.00001f) {
            for (int i = 0; i < pixelCount; i += step) {
                // 如果直接加是不行的，因为 data[i] 记录的是色值并不是数值，byte 的范围是 +127 到 —128，
                // 而亮度 是 11111111 是 -127，所以这里需要先转为无符号 unsigned long 参考 Byte.toUnsignedLong()
                pixelLightCount += ((long) data[i]) & 0xffL;
            }
            // 平均亮度
            long cameraLight = pixelLightCount / (pixelCount / step);
            // 更新历史记录
            int lightSize = AMBIENT_BRIGHTNESS_DARK_LIST.length;
            AMBIENT_BRIGHTNESS_DARK_LIST[ambientBrightnessDarkIndex = ambientBrightnessDarkIndex % lightSize] = cameraLight;
            ambientBrightnessDarkIndex++;
            boolean isDarkEnv = true;
            // 判断在时间范围 AMBIENT_BRIGHTNESS_WAIT_SCAN_TIME * lightSize 内是不是亮度过暗
            for (long ambientBrightness : AMBIENT_BRIGHTNESS_DARK_LIST) {
                if (ambientBrightness > AMBIENT_BRIGHTNESS_DARK) {
                    isDarkEnv = false;
                    break;
                }
            }
            BGAQRCodeUtil.d("摄像头环境亮度为：" + cameraLight);
            if (delegate != null) {
                delegate.onCameraAmbientBrightnessChanged(isDarkEnv);
            }
        }
    }

    /**
     * 解析本地图片二维码。返回二维码图片里的内容 或 null
     *
     * @param picturePath 要解析的二维码图片本地路径
     */
    public void decodeQRCode(String picturePath) {
        processDataTask = new ProcessDataTask(picturePath, this).perform();
    }

    /**
     * 解析 Bitmap 二维码。返回二维码图片里的内容 或 null
     *
     * @param bitmap 要解析的二维码图片
     */
    public void decodeQRCode(Bitmap bitmap) {
        processDataTask = new ProcessDataTask(bitmap, this).perform();
    }

    protected abstract ScanResult processData(byte[] data, int width, int height, boolean isRetry);

    protected abstract ScanResult processBitmapData(Bitmap bitmap);

    void onPostParseData(ScanResult scanResult) {
        if (!spotAble) {
            return;
        }
        String result = scanResult == null ? null : scanResult.result;
        if (TextUtils.isEmpty(result)) {
            try {
                if (camera != null) {
                    camera.setOneShotPreviewCallback(QRCodeView.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            spotAble = false;
            try {
                if (delegate != null) {
                    delegate.onScanQRCodeSuccess(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void onPostParseBitmapOrPicture(ScanResult scanResult) {
        if (delegate != null) {
            String result = scanResult == null ? null : scanResult.result;
            delegate.onScanQRCodeSuccess(result);
        }
    }

    void onScanBoxRectChanged(Rect rect) {
        cameraPreview.onScanBoxRectChanged(rect);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);


        if (!isShowLocationPoint() || locationPoints == null) {
            return;
        }

        for (PointF pointF : locationPoints) {
            canvas.drawCircle(pointF.x, pointF.y, 10, paint);
        }
        locationPoints = null;
        postInvalidateDelayed(2000);
    }

    /**
     * 是否显示定位点
     */
    protected boolean isShowLocationPoint() {
        return scanBoxView != null && scanBoxView.isShowLocationPoint();
    }

    /**
     * 是否自动缩放
     */
    protected boolean isAutoZoom() {
        return scanBoxView != null && scanBoxView.isAutoZoom();
    }

    protected boolean transformToViewCoordinates(final PointF[] pointArr, final Rect scanBoxAreaRect, final boolean isNeedAutoZoom, final String result) {
        if (pointArr == null || pointArr.length == 0) {
            return false;
        }

        try {
            // 不管横屏还是竖屏，size.width 大于 size.height
            Camera.Size size = camera.getParameters().getPreviewSize();
            boolean isMirrorPreview = cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT;
            int statusBarHeight = BGAQRCodeUtil.getStatusBarHeight(getContext());

            PointF[] transformedPoints = new PointF[pointArr.length];
            int index = 0;
            for (PointF qrPoint : pointArr) {
                transformedPoints[index] = transform(qrPoint.x, qrPoint.y, size.width, size.height, isMirrorPreview, statusBarHeight, scanBoxAreaRect);
                index++;
            }
            locationPoints = transformedPoints;
            postInvalidate();

            if (isNeedAutoZoom) {
                return handleAutoZoom(transformedPoints, result);
            }
            return false;
        } catch (Exception e) {
            locationPoints = null;
            e.printStackTrace();
            return false;
        }
    }

    private boolean handleAutoZoom(PointF[] locationPoints, final String result) {
        if (camera == null || scanBoxView == null) {
            return false;
        }
        if (locationPoints == null || locationPoints.length < 1) {
            return false;
        }
        if (autoZoomAnimator != null && autoZoomAnimator.isRunning()) {
            return true;
        }
        if (System.currentTimeMillis() - lastAutoZoomTime < 1200) {
            return true;
        }
        Camera.Parameters parameters = camera.getParameters();
        if (!parameters.isZoomSupported()) {
            return false;
        }

        float point1X = locationPoints[0].x;
        float point1Y = locationPoints[0].y;
        float point2X = locationPoints[1].x;
        float point2Y = locationPoints[1].y;
        float xLen = Math.abs(point1X - point2X);
        float yLen = Math.abs(point1Y - point2Y);
        int len = (int) Math.sqrt(xLen * xLen + yLen * yLen);

        int scanBoxWidth = scanBoxView.getRectWidth();
        if (len > scanBoxWidth / 4) {
            return false;
        }
        // 二维码在扫描框中的宽度小于扫描框的 1/4，放大镜头
        final int maxZoom = parameters.getMaxZoom();
        final int zoomStep = maxZoom / 4;
        final int zoom = parameters.getZoom();
        post(new Runnable() {
            @Override
            public void run() {
                startAutoZoom(zoom, Math.min(zoom + zoomStep, maxZoom), result);
            }
        });
        return true;
    }

    private void startAutoZoom(int oldZoom, int newZoom, final String result) {
        autoZoomAnimator = ValueAnimator.ofInt(oldZoom, newZoom);
        autoZoomAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (cameraPreview == null || !cameraPreview.isPreviewing()) {
                    return;
                }
                int zoom = (int) animation.getAnimatedValue();
                Camera.Parameters parameters = camera.getParameters();
                parameters.setZoom(zoom);
                camera.setParameters(parameters);
            }
        });
        autoZoomAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                onPostParseData(new ScanResult(result));
            }
        });
        autoZoomAnimator.setDuration(600);
        autoZoomAnimator.setRepeatCount(0);
        autoZoomAnimator.start();
        lastAutoZoomTime = System.currentTimeMillis();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (autoZoomAnimator != null) {
            autoZoomAnimator.cancel();
        }
    }

    private PointF transform(float originX, float originY, float cameraPreviewWidth, float cameraPreviewHeight, boolean isMirrorPreview, int statusBarHeight,
            final Rect scanBoxAreaRect) {
        int viewWidth = getWidth();
        int viewHeight = getHeight();

        PointF result;
        float scaleX;
        float scaleY;

        if (BGAQRCodeUtil.isPortrait(getContext())) {
            scaleX = viewWidth / cameraPreviewHeight;
            scaleY = viewHeight / cameraPreviewWidth;
            result = new PointF((cameraPreviewHeight - originX) * scaleX, (cameraPreviewWidth - originY) * scaleY);
            result.y = viewHeight - result.y;
            result.x = viewWidth - result.x;

            if (scanBoxAreaRect == null) {
                result.y += statusBarHeight;
            }
        } else {
            scaleX = viewWidth / cameraPreviewWidth;
            scaleY = viewHeight / cameraPreviewHeight;
            result = new PointF(originX * scaleX, originY * scaleY);
            if (isMirrorPreview) {
                result.x = viewWidth - result.x;
            }
        }

        if (scanBoxAreaRect != null) {
            result.y += scanBoxAreaRect.top;
            result.x += scanBoxAreaRect.left;
        }

        return result;
    }

    public interface OnScanCallbackListener {
        /**
         * 处理扫描结果
         *
         * @param result 摄像头扫码时只要回调了该方法 result 就一定有值，不会为 null。解析本地图片或 Bitmap 时 result 可能为 null
         */
        void onScanQRCodeSuccess(String result);

        /**
         * 摄像头环境亮度发生变化
         *
         * @param isDark 是否变暗
         */
        void onCameraAmbientBrightnessChanged(boolean isDark);

        /**
         * 处理打开相机出错
         */
        void onScanQRCodeOpenCameraError();
    }
}