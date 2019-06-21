package cn.bingoogolapple.qrcode.core;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.text.TextUtils;

import java.lang.ref.WeakReference;

class ProcessDataTask extends AsyncTask<Void, Void, ScanResult> {
    private Camera camera;
    private byte[] data;
    private boolean isPortrait;
    private String picturePath;
    private Bitmap bitmap;
    private WeakReference<QRCodeView> QRCodeViewRef;
    private static long lastStartTime = 0;

    ProcessDataTask(Camera camera, byte[] data, QRCodeView qrCodeView, boolean isPortrait) {
        this.camera = camera;
        this.data = data;
        QRCodeViewRef = new WeakReference<>(qrCodeView);
        this.isPortrait = isPortrait;
    }

    ProcessDataTask(String picturePath, QRCodeView qrCodeView) {
        this.picturePath = picturePath;
        QRCodeViewRef = new WeakReference<>(qrCodeView);
    }

    ProcessDataTask(Bitmap bitmap, QRCodeView qrCodeView) {
        this.bitmap = bitmap;
        QRCodeViewRef = new WeakReference<>(qrCodeView);
    }

    ProcessDataTask perform() {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return this;
    }

    void cancelTask() {
        if (getStatus() != Status.FINISHED) {
            cancel(true);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        QRCodeViewRef.clear();
        bitmap = null;
        data = null;
    }

    private ScanResult processData(QRCodeView qrCodeView) {
        if (data == null) {
            return null;
        }

        int width = 0;
        int height = 0;
        byte[] data = this.data;
        try {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();
            width = size.width;
            height = size.height;

            if (isPortrait) {
                data = new byte[this.data.length];
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        data[x * height + height - y - 1] = this.data[x + y * width];
                    }
                }
                int tmp = width;
                width = height;
                height = tmp;
            }

            return qrCodeView.processData(data, width, height, false);
        } catch (Exception e1) {
            e1.printStackTrace();
            try {
                if (width != 0 && height != 0) {
                    BGAQRCodeUtil.d("识别失败重试");
                    return qrCodeView.processData(data, width, height, true);
                } else {
                    return null;
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                return null;
            }
        }
    }

    @Override
    protected ScanResult doInBackground(Void... params) {
        QRCodeView qrCodeView = QRCodeViewRef.get();
        if (qrCodeView == null) {
            return null;
        }

        if (picturePath != null) {
            return qrCodeView.processBitmapData(BGAQRCodeUtil.getDecodeAbleBitmap(picturePath));
        } else if (bitmap != null) {
            ScanResult result = qrCodeView.processBitmapData(bitmap);
            bitmap = null;
            return result;
        } else {
            if (BGAQRCodeUtil.isDebug()) {
                BGAQRCodeUtil.d("两次任务执行的时间间隔：" + (System.currentTimeMillis() - lastStartTime));
                lastStartTime = System.currentTimeMillis();
            }
            long startTime = System.currentTimeMillis();

            ScanResult scanResult = processData(qrCodeView);

            if (BGAQRCodeUtil.isDebug()) {
                long time = System.currentTimeMillis() - startTime;
                if (scanResult != null && !TextUtils.isEmpty(scanResult.result)) {
                    BGAQRCodeUtil.d("识别成功时间为：" + time);
                } else {
                    BGAQRCodeUtil.e("识别失败时间为：" + time);
                }
            }

            return scanResult;
        }
    }

    @Override
    protected void onPostExecute(ScanResult result) {
        QRCodeView qrCodeView = QRCodeViewRef.get();
        if (qrCodeView == null) {
            return;
        }

        if (picturePath != null || bitmap != null) {
            bitmap = null;
            qrCodeView.onPostParseBitmapOrPicture(result);
        } else {
            qrCodeView.onPostParseData(result);
        }
    }
}
