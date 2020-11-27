package com.chinashb.www.mobileerp.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;


import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.utils.UnitFormatUtil;

import java.util.Date;

import static android.graphics.Paint.Style.STROKE;

/***
 * @date 创建时间 2018/7/12 23:05
 * @author 作者: liweifeng
 * @description 带有时间刻度盘的布局
 */
public class ClockPanelLayout extends RelativeLayout {
    private static final int DAY_MINUTES = 24 * 60;

    private Paint ringPaint;
    private Paint progressPaint;
    private Paint textPaint;

    //圆环的宽度
    private int ringWidth;
    //实际的进度
    private float progressDegree;


    private int startMinute ;
    private int endMinute;

    private Bitmap normalClockBitmap, exceptionClockBitmap;
    private Bitmap endClockBitmap, startClockBitmap;

    //小闹钟的偏移角度，目的是为了让闹钟图标在显示在边缘的居中
    private float clockOffsetDegree;
    private RectF progressRectF;
    private int normalColor, exceptionColor;
    private int marginRing;

    private int[] colors;
    private float[] positions = {0.2f, 0.8f, 1.5f};
    private boolean isStartBitmapException ;


    public ClockPanelLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ClockPanelLayout);
        ringWidth = a.getDimensionPixelOffset(R.styleable.ClockPanelLayout_app_ring_width, 30);
        int ringColor = a.getColor(R.styleable.ClockPanelLayout_app_ring_color, Color.RED);

        int textColor = a.getColor(R.styleable.ClockPanelLayout_app_time_text_color, Color.WHITE);
        int textSize = a.getDimensionPixelSize(R.styleable.ClockPanelLayout_app_time_text_size, 30);

        normalColor = a.getColor(R.styleable.ClockPanelLayout_app_normal_color, Color.BLUE);
        exceptionColor = a.getColor(R.styleable.ClockPanelLayout_app_exception_color, Color.RED);

        marginRing = a.getDimensionPixelSize(R.styleable.ClockPanelLayout_app_time_text_margin_ring, 0);
        Drawable drawable = a.getDrawable(R.styleable.ClockPanelLayout_app_logo_src);
        Drawable exceptionDrawable = a.getDrawable(R.styleable.ClockPanelLayout_app_logo_src_exception);
        startMinute = a.getInt(R.styleable.ClockPanelLayout_app_start_time, -1);
        endMinute = a.getInt(R.styleable.ClockPanelLayout_app_end_time, -1);
        a.recycle();

        colors = new int[]{normalColor, normalColor, Color.TRANSPARENT};
        positions[0] = (float) startMinute / DAY_MINUTES;
        positions[1] = (float) endMinute / DAY_MINUTES;


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.outWidth = options.outHeight = ringWidth;
        normalClockBitmap = drawableToBitmap(drawable, ringWidth);
        exceptionClockBitmap = drawableToBitmap(exceptionDrawable, ringWidth);
        startClockBitmap = normalClockBitmap.copy(normalClockBitmap.getConfig(), true);
        endClockBitmap = normalClockBitmap.copy(normalClockBitmap.getConfig(), true);


        progressDegree = (float) (endMinute - startMinute) / DAY_MINUTES * 360;

        initializePaints(ringColor, textColor, textSize);
    }

    public void setStartBitmapException(boolean startBitmapException) {
        isStartBitmapException = startBitmapException;
        invalidate();
    }

    private Bitmap drawableToBitmap(Drawable drawable, int bitmapSize) {
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, config);
        //注意，下面三行代码要用到，否则在View或者SurfaceView里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, bitmapSize, bitmapSize);
        drawable.draw(canvas);
        return bitmap;
    }

    private void initializePaints(int ringColor, int textColor, int textSize) {
        ringPaint = new Paint();
        ringPaint.setColor(ringColor); //设置圆的颜色
        ringPaint.setStyle(STROKE); //设置空心
        ringPaint.setStrokeWidth(ringWidth); //设置圆的宽度
        ringPaint.setAntiAlias(true);  //消除锯齿

        progressPaint = new Paint();
        progressPaint.setStyle(STROKE); //设置空心
        progressPaint.setStrokeWidth(ringWidth); //设置圆的宽度
        progressPaint.setAntiAlias(true);  //消除锯齿

        textPaint = new Paint();
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (widthMeasureSpec > heightMeasureSpec) {
            widthMeasureSpec = heightMeasureSpec;
        } else {
            heightMeasureSpec = widthMeasureSpec;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //传值为0是没有进度 为1只画上班打卡的lt
    public void setProgressDegree(float progressDegree) {
        this.progressDegree = progressDegree;
        invalidate();
    }

    public float getTimeDegree() {
        return (float) (endMinute - startMinute) / DAY_MINUTES * 360;
    }

    public ClockPanelLayout setStartException() {
        colors[0] = exceptionColor;
        startClockBitmap = exceptionClockBitmap.copy(endClockBitmap.getConfig(), true);
        return this;
    }

    public ClockPanelLayout setStartNormal() {
        colors[0] = normalColor;
        startClockBitmap = normalClockBitmap.copy(normalClockBitmap.getConfig(), true);
        return this;
    }

    public ClockPanelLayout setEndException() {
        colors[1] = exceptionColor;
        endClockBitmap = exceptionClockBitmap.copy(exceptionClockBitmap.getConfig(), true);
        return this;
    }

    public ClockPanelLayout setEndNormal(){
        colors[1] = normalColor;
        endClockBitmap = normalClockBitmap.copy(normalClockBitmap.getConfig(),true);
        return this;
    }

    public ClockPanelLayout setEndTime(long endTime) {
        Date date = new Date(endTime);
        this.endMinute = date.getHours() * 60 + date.getMinutes();
        positions[1] = (float) endMinute / DAY_MINUTES;
        return this;
    }

    public ClockPanelLayout setStartTime(long startTime) {
        Date date = new Date(startTime);
        this.startMinute = date.getHours() * 60 + date.getMinutes();
        positions[0] = (float) startMinute / DAY_MINUTES;
        return this;
    }

    public ClockPanelLayout setEndTime(String endTime) {
        String tempDate = String.format("%s %s", UnitFormatUtil .getCurrentYMD() );
        Date date = new Date(endTime);
        this.endMinute = date.getHours() * 60 + date.getMinutes();
        positions[1] = (float) endMinute / DAY_MINUTES;
        return this;
    }

    public ClockPanelLayout setStartTime(String startTime) {
        Date date = new Date(startTime);
        this.startMinute = date.getHours() * 60 + date.getMinutes();
        positions[0] = (float) startMinute / DAY_MINUTES;
        return this;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int centerX = getWidth() / 2;
        //画圆环的背景
        canvas.drawCircle(centerX, centerX, centerX - ringWidth / 2, ringPaint);

        //画进度相关的UI
        if (progressDegree != 0){
            drawProgressDegree(canvas,centerX);
        }

        //画时间
        for (int i = 0; i < 24; i++) {
            canvas.save();
            canvas.rotate(360 / 24 * i, centerX, centerX);
            canvas.drawText(String.format("%02d", i), centerX - textPaint.getTextSize() / 2, ringWidth + textPaint.getTextSize() + marginRing, textPaint);
            canvas.restore();
        }
        super.onDraw(canvas);
    }

    private void drawProgressDegree(Canvas canvas, int centerX) {

        if ((startMinute == -1 || endMinute == -1 ) && (progressDegree != 1)){
            return;
        }
        clockOffsetDegree = (float) (((float) ringWidth / 2) / (Math.PI * getWidth()) * 360);

        //用于定义的圆弧的形状和大小的界限
        if (progressRectF == null) {
            progressRectF = new RectF(ringWidth / 2, ringWidth / 2, getWidth() - ringWidth / 2, getHeight() - ringWidth / 2);
        }

        SweepGradient sweepGradient = new SweepGradient(centerX, centerX, colors, positions);
        progressPaint.setShader(sweepGradient);

        //画上班打卡的图标
        drawOnDutyBitmap(canvas, centerX);
        if (progressDegree != 1){
            //画进度
            canvas.save();
            canvas.rotate(-90, centerX, centerX);
            canvas.drawArc(progressRectF, (float) startMinute / DAY_MINUTES * 360, progressDegree, false, progressPaint);
            canvas.restore();

            //画下班打卡的图标
            canvas.save();
            canvas.rotate(((float) endMinute / DAY_MINUTES * 360 - clockOffsetDegree), centerX, centerX);
            canvas.drawBitmap(endClockBitmap, centerX, 0, progressPaint);
            canvas.restore();
        }

    }

    private void drawOnDutyBitmap(Canvas canvas, int centerX) {
        canvas.save();
        canvas.rotate(((float) startMinute / DAY_MINUTES * 360 - clockOffsetDegree), centerX, centerX);
        canvas.drawBitmap(isStartBitmapException ? exceptionClockBitmap : startClockBitmap,centerX,0,progressPaint);
        canvas.restore();
    }

}
