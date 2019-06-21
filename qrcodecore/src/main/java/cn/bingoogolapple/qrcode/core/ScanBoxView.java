package cn.bingoogolapple.qrcode.core;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

public class ScanBoxView extends View {
    private int moveStepDistance;
    private int animDelayTime;

    private Rect framingRect;
    private float scanLineTop;
    private float scanLineLeft;
    private Paint paint;
    private TextPaint tipPaint;

    private int maskColor;
    private int cornerColor;
    private int cornerLength;
    private int cornerSize;
    private int rectWidth;
    private int rectHeight;
    private int barcodeRectHeight;
    private int topOffset;
    private int scanLineSize;
    private int scanLineColor;
    private int scanLineMargin;
    private boolean isShowDefaultScanLineDrawable;
    private Drawable customScanLineDrawable;
    private Bitmap scanLineBitmap;
    private int borderSize;
    private int borderColor;
    private int animTime;
    private float verticalBias;
    private int cornerDisplayType;
    private int toolbarHeight;
    private boolean isBarcode;
    private String QRCodeTipText;
    private String barCodeTipText;
    private String tipText;
    private int tipTextSize;
    private int tipTextColor;
    private boolean isTipTextBelowRect;
    private int tipTextMargin;
    private boolean isShowTipTextAsSingleLine;
    private int tipBackgroundColor;
    private boolean isShowTipBackground;
    private boolean isScanLineReverse;
    private boolean isShowDefaultGridScanLineDrawable;
    private Drawable customGridScanLineDrawable;
    private Bitmap gridScanLineBitmap;
    private float gridScanLineBottom;
    private float gridScanLineRight;

    private Bitmap originQRCodeScanLineBitmap;
    private Bitmap originBarCodeScanLineBitmap;
    private Bitmap originQRCodeGridScanLineBitmap;
    private Bitmap originBarCodeGridScanLineBitmap;


    private float halfCornerSize;
    private StaticLayout tipTextSl;
    private int tipBackgroundRadius;

    private boolean isOnlyDecodeScanBoxArea;
    private boolean isShowLocationPoint;
    private boolean isAutoZoom;

    private QRCodeView QRCodeView;

    public ScanBoxView(Context context) {
        super(context);
        paint = new Paint();
        paint.setAntiAlias(true);
        maskColor = Color.parseColor("#33FFFFFF");
        cornerColor = Color.WHITE;
        cornerLength = BGAQRCodeUtil.dp2px(context, 20);
        cornerSize = BGAQRCodeUtil.dp2px(context, 3);
        scanLineSize = BGAQRCodeUtil.dp2px(context, 1);
        scanLineColor = Color.WHITE;
        topOffset = BGAQRCodeUtil.dp2px(context, 90);
        rectWidth = BGAQRCodeUtil.dp2px(context, 200);
        barcodeRectHeight = BGAQRCodeUtil.dp2px(context, 140);
        scanLineMargin = 0;
        isShowDefaultScanLineDrawable = false;
        customScanLineDrawable = null;
        scanLineBitmap = null;
        borderSize = BGAQRCodeUtil.dp2px(context, 1);
        borderColor = Color.WHITE;
        animTime = 1000;
        verticalBias = -1;
        cornerDisplayType = 1;
        toolbarHeight = 0;
        isBarcode = false;
        moveStepDistance = BGAQRCodeUtil.dp2px(context, 2);
        tipText = null;
        tipTextSize = BGAQRCodeUtil.sp2px(context, 14);
        tipTextColor = Color.WHITE;
        isTipTextBelowRect = false;
        tipTextMargin = BGAQRCodeUtil.dp2px(context, 20);
        isShowTipTextAsSingleLine = false;
        tipBackgroundColor = Color.parseColor("#22000000");
        isShowTipBackground = false;
        isScanLineReverse = false;
        isShowDefaultGridScanLineDrawable = false;

        tipPaint = new TextPaint();
        tipPaint.setAntiAlias(true);

        tipBackgroundRadius = BGAQRCodeUtil.dp2px(context, 4);

        isOnlyDecodeScanBoxArea = false;
        isShowLocationPoint = false;
        isAutoZoom = false;
    }

    void init(QRCodeView qrCodeView, AttributeSet attrs) {
        QRCodeView = qrCodeView;

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.QRCodeView);
        final int count = typedArray.getIndexCount();
        for (int i = 0; i < count; i++) {
            initCustomAttr(typedArray.getIndex(i), typedArray);
        }
        typedArray.recycle();

        afterInitCustomAttrs();
    }

    private void initCustomAttr(int attr, TypedArray typedArray) {
        if (attr == R.styleable.QRCodeView_qrcv_topOffset) {
            topOffset = typedArray.getDimensionPixelSize(attr, topOffset);
        } else if (attr == R.styleable.QRCodeView_qrcv_cornerSize) {
            cornerSize = typedArray.getDimensionPixelSize(attr, cornerSize);
        } else if (attr == R.styleable.QRCodeView_qrcv_cornerLength) {
            cornerLength = typedArray.getDimensionPixelSize(attr, cornerLength);
        } else if (attr == R.styleable.QRCodeView_qrcv_scanLineSize) {
            scanLineSize = typedArray.getDimensionPixelSize(attr, scanLineSize);
        } else if (attr == R.styleable.QRCodeView_qrcv_rectWidth) {
            rectWidth = typedArray.getDimensionPixelSize(attr, rectWidth);
        } else if (attr == R.styleable.QRCodeView_qrcv_maskColor) {
            maskColor = typedArray.getColor(attr, maskColor);
        } else if (attr == R.styleable.QRCodeView_qrcv_cornerColor) {
            cornerColor = typedArray.getColor(attr, cornerColor);
        } else if (attr == R.styleable.QRCodeView_qrcv_scanLineColor) {
            scanLineColor = typedArray.getColor(attr, scanLineColor);
        } else if (attr == R.styleable.QRCodeView_qrcv_scanLineMargin) {
            scanLineMargin = typedArray.getDimensionPixelSize(attr, scanLineMargin);
        } else if (attr == R.styleable.QRCodeView_qrcv_isShowDefaultScanLineDrawable) {
            isShowDefaultScanLineDrawable = typedArray.getBoolean(attr, isShowDefaultScanLineDrawable);
        } else if (attr == R.styleable.QRCodeView_qrcv_customScanLineDrawable) {
            customScanLineDrawable = typedArray.getDrawable(attr);
        } else if (attr == R.styleable.QRCodeView_qrcv_borderSize) {
            borderSize = typedArray.getDimensionPixelSize(attr, borderSize);
        } else if (attr == R.styleable.QRCodeView_qrcv_borderColor) {
            borderColor = typedArray.getColor(attr, borderColor);
        } else if (attr == R.styleable.QRCodeView_qrcv_animTime) {
            animTime = typedArray.getInteger(attr, animTime);
        } else if (attr == R.styleable.QRCodeView_qrcv_verticalBias) {
            verticalBias = typedArray.getFloat(attr, verticalBias);
        } else if (attr == R.styleable.QRCodeView_qrcv_cornerDisplayType) {
            cornerDisplayType = typedArray.getInteger(attr, cornerDisplayType);
        } else if (attr == R.styleable.QRCodeView_qrcv_toolbarHeight) {
            toolbarHeight = typedArray.getDimensionPixelSize(attr, toolbarHeight);
        } else if (attr == R.styleable.QRCodeView_qrcv_barcodeRectHeight) {
            barcodeRectHeight = typedArray.getDimensionPixelSize(attr, barcodeRectHeight);
        } else if (attr == R.styleable.QRCodeView_qrcv_isBarcode) {
            isBarcode = typedArray.getBoolean(attr, isBarcode);
        } else if (attr == R.styleable.QRCodeView_qrcv_barCodeTipText) {
            barCodeTipText = typedArray.getString(attr);
        } else if (attr == R.styleable.QRCodeView_qrcv_qrCodeTipText) {
            QRCodeTipText = typedArray.getString(attr);
        } else if (attr == R.styleable.QRCodeView_qrcv_tipTextSize) {
            tipTextSize = typedArray.getDimensionPixelSize(attr, tipTextSize);
        } else if (attr == R.styleable.QRCodeView_qrcv_tipTextColor) {
            tipTextColor = typedArray.getColor(attr, tipTextColor);
        } else if (attr == R.styleable.QRCodeView_qrcv_isTipTextBelowRect) {
            isTipTextBelowRect = typedArray.getBoolean(attr, isTipTextBelowRect);
        } else if (attr == R.styleable.QRCodeView_qrcv_tipTextMargin) {
            tipTextMargin = typedArray.getDimensionPixelSize(attr, tipTextMargin);
        } else if (attr == R.styleable.QRCodeView_qrcv_isShowTipTextAsSingleLine) {
            isShowTipTextAsSingleLine = typedArray.getBoolean(attr, isShowTipTextAsSingleLine);
        } else if (attr == R.styleable.QRCodeView_qrcv_isShowTipBackground) {
            isShowTipBackground = typedArray.getBoolean(attr, isShowTipBackground);
        } else if (attr == R.styleable.QRCodeView_qrcv_tipBackgroundColor) {
            tipBackgroundColor = typedArray.getColor(attr, tipBackgroundColor);
        } else if (attr == R.styleable.QRCodeView_qrcv_isScanLineReverse) {
            isScanLineReverse = typedArray.getBoolean(attr, isScanLineReverse);
        } else if (attr == R.styleable.QRCodeView_qrcv_isShowDefaultGridScanLineDrawable) {
            isShowDefaultGridScanLineDrawable = typedArray.getBoolean(attr, isShowDefaultGridScanLineDrawable);
        } else if (attr == R.styleable.QRCodeView_qrcv_customGridScanLineDrawable) {
            customGridScanLineDrawable = typedArray.getDrawable(attr);
        } else if (attr == R.styleable.QRCodeView_qrcv_isOnlyDecodeScanBoxArea) {
            isOnlyDecodeScanBoxArea = typedArray.getBoolean(attr, isOnlyDecodeScanBoxArea);
        } else if (attr == R.styleable.QRCodeView_qrcv_isShowLocationPoint) {
            isShowLocationPoint = typedArray.getBoolean(attr, isShowLocationPoint);
        } else if (attr == R.styleable.QRCodeView_qrcv_isAutoZoom) {
            isAutoZoom = typedArray.getBoolean(attr, isAutoZoom);
        }
    }

    private void afterInitCustomAttrs() {
        if (customGridScanLineDrawable != null) {
            originQRCodeGridScanLineBitmap = ((BitmapDrawable) customGridScanLineDrawable).getBitmap();
        }
        if (originQRCodeGridScanLineBitmap == null) {
            originQRCodeGridScanLineBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.qrcode_default_grid_scan_line);
            originQRCodeGridScanLineBitmap = BGAQRCodeUtil.makeTintBitmap(originQRCodeGridScanLineBitmap, scanLineColor);
        }
        originBarCodeGridScanLineBitmap = BGAQRCodeUtil.adjustPhotoRotation(originQRCodeGridScanLineBitmap, 90);
        originBarCodeGridScanLineBitmap = BGAQRCodeUtil.adjustPhotoRotation(originBarCodeGridScanLineBitmap, 90);
        originBarCodeGridScanLineBitmap = BGAQRCodeUtil.adjustPhotoRotation(originBarCodeGridScanLineBitmap, 90);


        if (customScanLineDrawable != null) {
            originQRCodeScanLineBitmap = ((BitmapDrawable) customScanLineDrawable).getBitmap();
        }
        if (originQRCodeScanLineBitmap == null) {
            originQRCodeScanLineBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.qrcode_default_scan_line);
            originQRCodeScanLineBitmap = BGAQRCodeUtil.makeTintBitmap(originQRCodeScanLineBitmap, scanLineColor);
        }
        originBarCodeScanLineBitmap = BGAQRCodeUtil.adjustPhotoRotation(originQRCodeScanLineBitmap, 90);

        topOffset += toolbarHeight;
        halfCornerSize = 1.0f * cornerSize / 2;

        tipPaint.setTextSize(tipTextSize);
        tipPaint.setColor(tipTextColor);

        setIsBarcode(isBarcode);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (framingRect == null) {
            return;
        }

        // 画遮罩层
        drawMask(canvas);

        // 画边框线
        drawBorderLine(canvas);

        // 画四个直角的线
        drawCornerLine(canvas);

        // 画扫描线
        drawScanLine(canvas);

        // 画提示文本
        drawTipText(canvas);

        // 移动扫描线的位置
        moveScanLine();
    }

    /**
     * 画遮罩层
     */
    private void drawMask(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        if (maskColor != Color.TRANSPARENT) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(maskColor);
            canvas.drawRect(0, 0, width, framingRect.top, paint);
            canvas.drawRect(0, framingRect.top, framingRect.left, framingRect.bottom + 1, paint);
            canvas.drawRect(framingRect.right + 1, framingRect.top, width, framingRect.bottom + 1, paint);
            canvas.drawRect(0, framingRect.bottom + 1, width, height, paint);
        }
    }

    /**
     * 画边框线
     */
    private void drawBorderLine(Canvas canvas) {
        if (borderSize > 0) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(borderColor);
            paint.setStrokeWidth(borderSize);
            canvas.drawRect(framingRect, paint);
        }
    }

    /**
     * 画四个直角的线
     */
    private void drawCornerLine(Canvas canvas) {
        if (halfCornerSize > 0) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(cornerColor);
            paint.setStrokeWidth(cornerSize);
            if (cornerDisplayType == 1) {
                canvas.drawLine(framingRect.left - halfCornerSize, framingRect.top, framingRect.left - halfCornerSize + cornerLength, framingRect.top,
                        paint);
                canvas.drawLine(framingRect.left, framingRect.top - halfCornerSize, framingRect.left, framingRect.top - halfCornerSize + cornerLength,
                        paint);
                canvas.drawLine(framingRect.right + halfCornerSize, framingRect.top, framingRect.right + halfCornerSize - cornerLength, framingRect.top,
                        paint);
                canvas.drawLine(framingRect.right, framingRect.top - halfCornerSize, framingRect.right, framingRect.top - halfCornerSize + cornerLength,
                        paint);

                canvas.drawLine(framingRect.left - halfCornerSize, framingRect.bottom, framingRect.left - halfCornerSize + cornerLength,
                        framingRect.bottom, paint);
                canvas.drawLine(framingRect.left, framingRect.bottom + halfCornerSize, framingRect.left,
                        framingRect.bottom + halfCornerSize - cornerLength, paint);
                canvas.drawLine(framingRect.right + halfCornerSize, framingRect.bottom, framingRect.right + halfCornerSize - cornerLength,
                        framingRect.bottom, paint);
                canvas.drawLine(framingRect.right, framingRect.bottom + halfCornerSize, framingRect.right,
                        framingRect.bottom + halfCornerSize - cornerLength, paint);
            } else if (cornerDisplayType == 2) {
                canvas.drawLine(framingRect.left, framingRect.top + halfCornerSize, framingRect.left + cornerLength, framingRect.top + halfCornerSize,
                        paint);
                canvas.drawLine(framingRect.left + halfCornerSize, framingRect.top, framingRect.left + halfCornerSize, framingRect.top + cornerLength,
                        paint);
                canvas.drawLine(framingRect.right, framingRect.top + halfCornerSize, framingRect.right - cornerLength, framingRect.top + halfCornerSize,
                        paint);
                canvas.drawLine(framingRect.right - halfCornerSize, framingRect.top, framingRect.right - halfCornerSize, framingRect.top + cornerLength,
                        paint);

                canvas.drawLine(framingRect.left, framingRect.bottom - halfCornerSize, framingRect.left + cornerLength,
                        framingRect.bottom - halfCornerSize, paint);
                canvas.drawLine(framingRect.left + halfCornerSize, framingRect.bottom, framingRect.left + halfCornerSize,
                        framingRect.bottom - cornerLength, paint);
                canvas.drawLine(framingRect.right, framingRect.bottom - halfCornerSize, framingRect.right - cornerLength,
                        framingRect.bottom - halfCornerSize, paint);
                canvas.drawLine(framingRect.right - halfCornerSize, framingRect.bottom, framingRect.right - halfCornerSize,
                        framingRect.bottom - cornerLength, paint);
            }
        }
    }

    /**
     * 画扫描线
     */
    private void drawScanLine(Canvas canvas) {
        if (isBarcode) {
            if (gridScanLineBitmap != null) {
                RectF dstGridRectF = new RectF(framingRect.left + halfCornerSize + 0.5f, framingRect.top + halfCornerSize + scanLineMargin,
                        gridScanLineRight, framingRect.bottom - halfCornerSize - scanLineMargin);

                Rect srcGridRect = new Rect((int) (gridScanLineBitmap.getWidth() - dstGridRectF.width()), 0, gridScanLineBitmap.getWidth(),
                        gridScanLineBitmap.getHeight());

                if (srcGridRect.left < 0) {
                    srcGridRect.left = 0;
                    dstGridRectF.left = dstGridRectF.right - srcGridRect.width();
                }

                canvas.drawBitmap(gridScanLineBitmap, srcGridRect, dstGridRectF, paint);
            } else if (scanLineBitmap != null) {
                RectF lineRect = new RectF(scanLineLeft, framingRect.top + halfCornerSize + scanLineMargin, scanLineLeft + scanLineBitmap.getWidth(),
                        framingRect.bottom - halfCornerSize - scanLineMargin);
                canvas.drawBitmap(scanLineBitmap, null, lineRect, paint);
            } else {
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(scanLineColor);
                canvas.drawRect(scanLineLeft, framingRect.top + halfCornerSize + scanLineMargin, scanLineLeft + scanLineSize,
                        framingRect.bottom - halfCornerSize - scanLineMargin, paint);
            }
        } else {
            if (gridScanLineBitmap != null) {
                RectF dstGridRectF = new RectF(framingRect.left + halfCornerSize + scanLineMargin, framingRect.top + halfCornerSize + 0.5f,
                        framingRect.right - halfCornerSize - scanLineMargin, gridScanLineBottom);

                Rect srcRect = new Rect(0, (int) (gridScanLineBitmap.getHeight() - dstGridRectF.height()), gridScanLineBitmap.getWidth(),
                        gridScanLineBitmap.getHeight());

                if (srcRect.top < 0) {
                    srcRect.top = 0;
                    dstGridRectF.top = dstGridRectF.bottom - srcRect.height();
                }

                canvas.drawBitmap(gridScanLineBitmap, srcRect, dstGridRectF, paint);
            } else if (scanLineBitmap != null) {
                RectF lineRect = new RectF(framingRect.left + halfCornerSize + scanLineMargin, scanLineTop,
                        framingRect.right - halfCornerSize - scanLineMargin, scanLineTop + scanLineBitmap.getHeight());
                canvas.drawBitmap(scanLineBitmap, null, lineRect, paint);
            } else {
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(scanLineColor);
                canvas.drawRect(framingRect.left + halfCornerSize + scanLineMargin, scanLineTop, framingRect.right - halfCornerSize - scanLineMargin,
                        scanLineTop + scanLineSize, paint);
            }
        }
    }

    /**
     * 画提示文本
     */
    private void drawTipText(Canvas canvas) {
        if (TextUtils.isEmpty(tipText) || tipTextSl == null) {
            return;
        }

        if (isTipTextBelowRect) {
            if (isShowTipBackground) {
                paint.setColor(tipBackgroundColor);
                paint.setStyle(Paint.Style.FILL);
                if (isShowTipTextAsSingleLine) {
                    Rect tipRect = new Rect();
                    tipPaint.getTextBounds(tipText, 0, tipText.length(), tipRect);
                    float left = (canvas.getWidth() - tipRect.width()) / 2 - tipBackgroundRadius;
                    canvas.drawRoundRect(
                            new RectF(left, framingRect.bottom + tipTextMargin - tipBackgroundRadius, left + tipRect.width() + 2 * tipBackgroundRadius,
                                    framingRect.bottom + tipTextMargin + tipTextSl.getHeight() + tipBackgroundRadius), tipBackgroundRadius,
                            tipBackgroundRadius, paint);
                } else {
                    canvas.drawRoundRect(new RectF(framingRect.left, framingRect.bottom + tipTextMargin - tipBackgroundRadius, framingRect.right,
                                    framingRect.bottom + tipTextMargin + tipTextSl.getHeight() + tipBackgroundRadius), tipBackgroundRadius,
                            tipBackgroundRadius,
                            paint);
                }
            }

            canvas.save();
            if (isShowTipTextAsSingleLine) {
                canvas.translate(0, framingRect.bottom + tipTextMargin);
            } else {
                canvas.translate(framingRect.left + tipBackgroundRadius, framingRect.bottom + tipTextMargin);
            }
            tipTextSl.draw(canvas);
            canvas.restore();
        } else {
            if (isShowTipBackground) {
                paint.setColor(tipBackgroundColor);
                paint.setStyle(Paint.Style.FILL);

                if (isShowTipTextAsSingleLine) {
                    Rect tipRect = new Rect();
                    tipPaint.getTextBounds(tipText, 0, tipText.length(), tipRect);
                    float left = (canvas.getWidth() - tipRect.width()) / 2 - tipBackgroundRadius;
                    canvas.drawRoundRect(new RectF(left, framingRect.top - tipTextMargin - tipTextSl.getHeight() - tipBackgroundRadius,
                                    left + tipRect.width() + 2 * tipBackgroundRadius, framingRect.top - tipTextMargin + tipBackgroundRadius),
                            tipBackgroundRadius,
                            tipBackgroundRadius, paint);
                } else {
                    canvas.drawRoundRect(
                            new RectF(framingRect.left, framingRect.top - tipTextMargin - tipTextSl.getHeight() - tipBackgroundRadius, framingRect.right,
                                    framingRect.top - tipTextMargin + tipBackgroundRadius), tipBackgroundRadius, tipBackgroundRadius, paint);
                }
            }

            canvas.save();
            if (isShowTipTextAsSingleLine) {
                canvas.translate(0, framingRect.top - tipTextMargin - tipTextSl.getHeight());
            } else {
                canvas.translate(framingRect.left + tipBackgroundRadius, framingRect.top - tipTextMargin - tipTextSl.getHeight());
            }
            tipTextSl.draw(canvas);
            canvas.restore();
        }
    }

    /**
     * 移动扫描线的位置
     */
    private void moveScanLine() {
        if (isBarcode) {
            if (gridScanLineBitmap == null) {
                // 处理非网格扫描图片的情况
                scanLineLeft += moveStepDistance;
                int scanLineSize = this.scanLineSize;
                if (scanLineBitmap != null) {
                    scanLineSize = scanLineBitmap.getWidth();
                }

                if (isScanLineReverse) {
                    if (scanLineLeft + scanLineSize > framingRect.right - halfCornerSize || scanLineLeft < framingRect.left + halfCornerSize) {
                        moveStepDistance = -moveStepDistance;
                    }
                } else {
                    if (scanLineLeft + scanLineSize > framingRect.right - halfCornerSize) {
                        scanLineLeft = framingRect.left + halfCornerSize + 0.5f;
                    }
                }
            } else {
                // 处理网格扫描图片的情况
                gridScanLineRight += moveStepDistance;
                if (gridScanLineRight > framingRect.right - halfCornerSize) {
                    gridScanLineRight = framingRect.left + halfCornerSize + 0.5f;
                }
            }
        } else {
            if (gridScanLineBitmap == null) {
                // 处理非网格扫描图片的情况
                scanLineTop += moveStepDistance;
                int scanLineSize = this.scanLineSize;
                if (scanLineBitmap != null) {
                    scanLineSize = scanLineBitmap.getHeight();
                }

                if (isScanLineReverse) {
                    if (scanLineTop + scanLineSize > framingRect.bottom - halfCornerSize || scanLineTop < framingRect.top + halfCornerSize) {
                        moveStepDistance = -moveStepDistance;
                    }
                } else {
                    if (scanLineTop + scanLineSize > framingRect.bottom - halfCornerSize) {
                        scanLineTop = framingRect.top + halfCornerSize + 0.5f;
                    }
                }
            } else {
                // 处理网格扫描图片的情况
                gridScanLineBottom += moveStepDistance;
                if (gridScanLineBottom > framingRect.bottom - halfCornerSize) {
                    gridScanLineBottom = framingRect.top + halfCornerSize + 0.5f;
                }
            }

        }
        postInvalidateDelayed(animDelayTime, framingRect.left, framingRect.top, framingRect.right, framingRect.bottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calFramingRect();
    }

    private void calFramingRect() {
        int leftOffset = (getWidth() - rectWidth) / 2;
        framingRect = new Rect(leftOffset, topOffset, leftOffset + rectWidth, topOffset + rectHeight);

        if (isBarcode) {
            gridScanLineRight = scanLineLeft = framingRect.left + halfCornerSize + 0.5f;
        } else {
            gridScanLineBottom = scanLineTop = framingRect.top + halfCornerSize + 0.5f;
        }

        if (QRCodeView != null && isOnlyDecodeScanBoxArea()) {
            QRCodeView.onScanBoxRectChanged(new Rect(framingRect));
        }
    }

    public Rect getScanBoxAreaRect(int previewHeight) {
        if (isOnlyDecodeScanBoxArea && getVisibility() == View.VISIBLE) {
            Rect rect = new Rect(framingRect);
            float ratio = 1.0f * previewHeight / getMeasuredHeight();

            float centerX = rect.exactCenterX();
            float centerY = rect.exactCenterY();

            float halfWidth = rect.width() / 2f;
            float halfHeight = rect.height() / 2f;
            float newHalfWidth = halfWidth * ratio;
            float newHalfHeight = halfHeight * ratio;

            rect.left = (int) (centerX - newHalfWidth);
            rect.right = (int) (centerX + newHalfWidth);
            rect.top = (int) (centerY - newHalfHeight);
            rect.bottom = (int) (centerY + newHalfHeight);
            return rect;
        } else {
            return null;
        }
    }

    public void setIsBarcode(boolean isBarcode) {
        this.isBarcode = isBarcode;
        refreshScanBox();
    }

    private void refreshScanBox() {
        if (customGridScanLineDrawable != null || isShowDefaultGridScanLineDrawable) {
            if (isBarcode) {
                gridScanLineBitmap = originBarCodeGridScanLineBitmap;
            } else {
                gridScanLineBitmap = originQRCodeGridScanLineBitmap;
            }
        } else if (customScanLineDrawable != null || isShowDefaultScanLineDrawable) {
            if (isBarcode) {
                scanLineBitmap = originBarCodeScanLineBitmap;
            } else {
                scanLineBitmap = originQRCodeScanLineBitmap;
            }
        }

        if (isBarcode) {
            tipText = barCodeTipText;
            rectHeight = barcodeRectHeight;
            animDelayTime = (int) ((1.0f * animTime * moveStepDistance) / rectWidth);
        } else {
            tipText = QRCodeTipText;
            rectHeight = rectWidth;
            animDelayTime = (int) ((1.0f * animTime * moveStepDistance) / rectHeight);
        }

        if (!TextUtils.isEmpty(tipText)) {
            if (isShowTipTextAsSingleLine) {
                tipTextSl = new StaticLayout(tipText, tipPaint, BGAQRCodeUtil.getScreenResolution(getContext()).x, Layout.Alignment.ALIGN_CENTER, 1.0f, 0,
                        true);
            } else {
                tipTextSl = new StaticLayout(tipText, tipPaint, rectWidth - 2 * tipBackgroundRadius, Layout.Alignment.ALIGN_CENTER, 1.0f, 0, true);
            }
        }

        if (verticalBias != -1) {
            int screenHeight = BGAQRCodeUtil.getScreenResolution(getContext()).y - BGAQRCodeUtil.getStatusBarHeight(getContext());
            if (toolbarHeight == 0) {
                topOffset = (int) (screenHeight * verticalBias - rectHeight / 2);
            } else {
                topOffset = toolbarHeight + (int) ((screenHeight - toolbarHeight) * verticalBias - rectHeight / 2);
            }
        }

        calFramingRect();

        postInvalidate();
    }

    public boolean getIsBarcode() {
        return isBarcode;
    }

    public int getMaskColor() {
        return maskColor;
    }

    public void setMaskColor(int maskColor) {
        this.maskColor = maskColor;
        refreshScanBox();
    }

    public int getCornerColor() {
        return cornerColor;
    }

    public void setCornerColor(int cornerColor) {
        this.cornerColor = cornerColor;
        refreshScanBox();
    }

    public int getCornerLength() {
        return cornerLength;
    }

    public void setCornerLength(int cornerLength) {
        this.cornerLength = cornerLength;
        refreshScanBox();
    }

    public int getCornerSize() {
        return cornerSize;
    }

    public void setCornerSize(int cornerSize) {
        this.cornerSize = cornerSize;
        refreshScanBox();
    }

    public int getRectWidth() {
        return rectWidth;
    }

    public void setRectWidth(int rectWidth) {
        this.rectWidth = rectWidth;
        refreshScanBox();
    }

    public int getRectHeight() {
        return rectHeight;
    }

    public void setRectHeight(int rectHeight) {
        this.rectHeight = rectHeight;
        refreshScanBox();
    }

    public int getBarcodeRectHeight() {
        return barcodeRectHeight;
    }

    public void setBarcodeRectHeight(int barcodeRectHeight) {
        this.barcodeRectHeight = barcodeRectHeight;
        refreshScanBox();
    }

    public int getTopOffset() {
        return topOffset;
    }

    public void setTopOffset(int topOffset) {
        this.topOffset = topOffset;
        refreshScanBox();
    }

    public int getScanLineSize() {
        return scanLineSize;
    }

    public void setScanLineSize(int scanLineSize) {
        this.scanLineSize = scanLineSize;
        refreshScanBox();
    }

    public int getScanLineColor() {
        return scanLineColor;
    }

    public void setScanLineColor(int scanLineColor) {
        this.scanLineColor = scanLineColor;
        refreshScanBox();
    }

    public int getScanLineMargin() {
        return scanLineMargin;
    }

    public void setScanLineMargin(int scanLineMargin) {
        this.scanLineMargin = scanLineMargin;
        refreshScanBox();
    }

    public boolean isShowDefaultScanLineDrawable() {
        return isShowDefaultScanLineDrawable;
    }

    public void setShowDefaultScanLineDrawable(boolean showDefaultScanLineDrawable) {
        isShowDefaultScanLineDrawable = showDefaultScanLineDrawable;
        refreshScanBox();
    }

    public Drawable getCustomScanLineDrawable() {
        return customScanLineDrawable;
    }

    public void setCustomScanLineDrawable(Drawable customScanLineDrawable) {
        this.customScanLineDrawable = customScanLineDrawable;
        refreshScanBox();
    }

    public Bitmap getScanLineBitmap() {
        return scanLineBitmap;
    }

    public void setScanLineBitmap(Bitmap scanLineBitmap) {
        this.scanLineBitmap = scanLineBitmap;
        refreshScanBox();
    }

    public int getBorderSize() {
        return borderSize;
    }

    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
        refreshScanBox();
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        refreshScanBox();
    }

    public int getAnimTime() {
        return animTime;
    }

    public void setAnimTime(int animTime) {
        this.animTime = animTime;
        refreshScanBox();
    }

    public float getVerticalBias() {
        return verticalBias;
    }

    public void setVerticalBias(float verticalBias) {
        this.verticalBias = verticalBias;
        refreshScanBox();
    }

    public int getToolbarHeight() {
        return toolbarHeight;
    }

    public void setToolbarHeight(int toolbarHeight) {
        this.toolbarHeight = toolbarHeight;
        refreshScanBox();
    }

    public String getQRCodeTipText() {
        return QRCodeTipText;
    }

    public void setQRCodeTipText(String qrCodeTipText) {
        QRCodeTipText = qrCodeTipText;
        refreshScanBox();
    }

    public String getBarCodeTipText() {
        return barCodeTipText;
    }

    public void setBarCodeTipText(String barCodeTipText) {
        this.barCodeTipText = barCodeTipText;
        refreshScanBox();
    }

    public String getTipText() {
        return tipText;
    }

    public void setTipText(String tipText) {
        if (isBarcode) {
            barCodeTipText = tipText;
        } else {
            QRCodeTipText = tipText;
        }
        refreshScanBox();
    }

    public int getTipTextColor() {
        return tipTextColor;
    }

    public void setTipTextColor(int tipTextColor) {
        this.tipTextColor = tipTextColor;
        tipPaint.setColor(this.tipTextColor);
        refreshScanBox();
    }

    public int getTipTextSize() {
        return tipTextSize;
    }

    public void setTipTextSize(int tipTextSize) {
        this.tipTextSize = tipTextSize;
        tipPaint.setTextSize(this.tipTextSize);
        refreshScanBox();
    }

    public boolean isTipTextBelowRect() {
        return isTipTextBelowRect;
    }

    public void setTipTextBelowRect(boolean tipTextBelowRect) {
        isTipTextBelowRect = tipTextBelowRect;
        refreshScanBox();
    }

    public int getTipTextMargin() {
        return tipTextMargin;
    }

    public void setTipTextMargin(int tipTextMargin) {
        this.tipTextMargin = tipTextMargin;
        refreshScanBox();
    }

    public boolean isShowTipTextAsSingleLine() {
        return isShowTipTextAsSingleLine;
    }

    public void setShowTipTextAsSingleLine(boolean showTipTextAsSingleLine) {
        isShowTipTextAsSingleLine = showTipTextAsSingleLine;
        refreshScanBox();
    }

    public boolean isShowTipBackground() {
        return isShowTipBackground;
    }

    public void setShowTipBackground(boolean showTipBackground) {
        isShowTipBackground = showTipBackground;
        refreshScanBox();
    }

    public int getTipBackgroundColor() {
        return tipBackgroundColor;
    }

    public void setTipBackgroundColor(int tipBackgroundColor) {
        this.tipBackgroundColor = tipBackgroundColor;
        refreshScanBox();
    }

    public boolean isScanLineReverse() {
        return isScanLineReverse;
    }

    public void setScanLineReverse(boolean scanLineReverse) {
        isScanLineReverse = scanLineReverse;
        refreshScanBox();
    }

    public boolean isShowDefaultGridScanLineDrawable() {
        return isShowDefaultGridScanLineDrawable;
    }

    public void setShowDefaultGridScanLineDrawable(boolean showDefaultGridScanLineDrawable) {
        isShowDefaultGridScanLineDrawable = showDefaultGridScanLineDrawable;
        refreshScanBox();
    }

    public float getHalfCornerSize() {
        return halfCornerSize;
    }

    public void setHalfCornerSize(float halfCornerSize) {
        this.halfCornerSize = halfCornerSize;
        refreshScanBox();
    }

    public StaticLayout getTipTextSl() {
        return tipTextSl;
    }

    public void setTipTextSl(StaticLayout tipTextSl) {
        this.tipTextSl = tipTextSl;
        refreshScanBox();
    }

    public int getTipBackgroundRadius() {
        return tipBackgroundRadius;
    }

    public void setTipBackgroundRadius(int tipBackgroundRadius) {
        this.tipBackgroundRadius = tipBackgroundRadius;
        refreshScanBox();
    }

    public boolean isOnlyDecodeScanBoxArea() {
        return isOnlyDecodeScanBoxArea;
    }

    public void setOnlyDecodeScanBoxArea(boolean onlyDecodeScanBoxArea) {
        isOnlyDecodeScanBoxArea = onlyDecodeScanBoxArea;
        calFramingRect();
    }

    public boolean isShowLocationPoint() {
        return isShowLocationPoint;
    }

    public void setShowLocationPoint(boolean showLocationPoint) {
        isShowLocationPoint = showLocationPoint;
    }

    public boolean isAutoZoom() {
        return isAutoZoom;
    }

    public void setAutoZoom(boolean autoZoom) {
        isAutoZoom = autoZoom;
    }
}