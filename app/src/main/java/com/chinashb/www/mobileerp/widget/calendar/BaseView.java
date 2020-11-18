/*
 * Copyright (C) 2016  liweifeng
 * WebSite https://github.com/MiracleTimes-Dev
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chinashb.www.mobileerp.widget.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

/**
 * 基本的日历View，派生出MonthView 和 WeekView
 * Created by liweifeng on 2018/1/23.
 */

public abstract class BaseView extends View implements View.OnClickListener {

    CalendarViewDelegate delegate;

    /**
     * 当前月份日期的笔
     */
    protected Paint currentMonthTextPaint = new Paint();

    /**
     * 其它月份日期颜色
     */
    protected Paint otherMonthTextPaint = new Paint();

    /**
     * 当前月份农历文本颜色
     */
    protected Paint currentMonthLunarTextPaint = new Paint();


    /**
     * 当前月份农历文本颜色
     */
    protected Paint selectedLunarTextPaint = new Paint();

    /**
     * 其它月份农历文本颜色
     */
    protected Paint otherMonthLunarTextPaint = new Paint();

    /**
     * 其它月份农历文本颜色
     */
    protected Paint schemeLunarTextPaint = new Paint();

    /**
     * 标记的日期背景颜色画笔
     */
    protected Paint schemePaint = new Paint();

    /**
     * 被选择的日期背景色
     */
    protected Paint selectedPaint = new Paint();

    /**
     * 标记的文本画笔
     */
    protected Paint schemeTextPaint = new Paint();

    /**
     * 选中的文本画笔
     */
    protected Paint selectTextPaint = new Paint();

    /**
     * 当前日期文本颜色画笔
     */
    protected Paint currentDayTextPaint = new Paint();

    /**
     * 当前日期文本颜色画笔
     */
    protected Paint currentDayLunarTextPaint = new Paint();

    /**
     * 日历布局，需要在日历下方放自己的布局
     */
    CalendarLayout parentLayout;

    protected Paint bottomDotPaint = new Paint();

    /**
     * 日历项
     */
    List<CalendarEntity> calendarEntityList;

    /**
     * 每一项的高度
     */
    protected int itemHeight;

    /**
     * 每一项的宽度
     */
    protected int itemWidth;

    /**
     * Text的基线
     */
    protected float textBaseLine;

    /**
     * 点击的x、y坐标
     */
    float clickX, clickY;

    /**
     * 是否点击
     */
    boolean isClick = true;

    /**
     * 字体大小
     */
    static final int TEXT_SIZE = 14;

    /**
     * 当前点击项
     */
    int currentClickItem = -1;

    public BaseView(Context context) {
        this(context, null);
    }

    public BaseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint(context);
    }

    /**
     * 初始化配置
     *
     * @param context context
     */
    private void initPaint(Context context) {
        currentMonthTextPaint.setAntiAlias(true);
        currentMonthTextPaint.setTextAlign(Paint.Align.CENTER);
        currentMonthTextPaint.setColor(0xFF111111);
        currentMonthTextPaint.setFakeBoldText(true);
        currentMonthTextPaint.setTextSize(CalendarUtil.dipToPx(context, TEXT_SIZE));

        otherMonthTextPaint.setAntiAlias(true);
        otherMonthTextPaint.setTextAlign(Paint.Align.CENTER);
        otherMonthTextPaint.setColor(0xFFe1e1e1);
        otherMonthTextPaint.setFakeBoldText(true);
        otherMonthTextPaint.setTextSize(CalendarUtil.dipToPx(context, TEXT_SIZE));

        currentMonthLunarTextPaint.setAntiAlias(true);
        currentMonthLunarTextPaint.setTextAlign(Paint.Align.CENTER);

        selectedLunarTextPaint.setAntiAlias(true);
        selectedLunarTextPaint.setTextAlign(Paint.Align.CENTER);

        otherMonthLunarTextPaint.setAntiAlias(true);
        otherMonthLunarTextPaint.setTextAlign(Paint.Align.CENTER);


        schemeLunarTextPaint.setAntiAlias(true);
        schemeLunarTextPaint.setTextAlign(Paint.Align.CENTER);

        schemeTextPaint.setAntiAlias(true);
        schemeTextPaint.setStyle(Paint.Style.FILL);
        schemeTextPaint.setTextAlign(Paint.Align.CENTER);
        schemeTextPaint.setColor(0xffed5353);
        schemeTextPaint.setFakeBoldText(true);
        schemeTextPaint.setTextSize(CalendarUtil.dipToPx(context, TEXT_SIZE));

        selectTextPaint.setAntiAlias(true);
        selectTextPaint.setStyle(Paint.Style.FILL);
        selectTextPaint.setTextAlign(Paint.Align.CENTER);
        selectTextPaint.setColor(0xffed5353);
        selectTextPaint.setFakeBoldText(true);
        selectTextPaint.setTextSize(CalendarUtil.dipToPx(context, TEXT_SIZE));

//        bottomDotPaint.setAntiAlias(true);
//        bottomDotPaint.setStyle(Paint.Style.FILL);
//        bottomDotPaint.setStrokeWidth(2);
//        bottomDotPaint.setColor(Color.RED);

        schemePaint.setAntiAlias(true);
        schemePaint.setStyle(Paint.Style.FILL);
        schemePaint.setStrokeWidth(2);
        schemePaint.setColor(0xffefefef);

        currentDayTextPaint.setAntiAlias(true);
        currentDayTextPaint.setTextAlign(Paint.Align.CENTER);
        currentDayTextPaint.setColor(Color.RED);
        currentDayTextPaint.setFakeBoldText(true);
        currentDayTextPaint.setTextSize(CalendarUtil.dipToPx(context, TEXT_SIZE));

        currentDayLunarTextPaint.setAntiAlias(true);
        currentDayLunarTextPaint.setTextAlign(Paint.Align.CENTER);
        currentDayLunarTextPaint.setColor(Color.RED);
        currentDayLunarTextPaint.setFakeBoldText(true);
        currentDayLunarTextPaint.setTextSize(CalendarUtil.dipToPx(context, TEXT_SIZE));

        selectedPaint.setAntiAlias(true);
        selectedPaint.setStyle(Paint.Style.FILL);
        selectedPaint.setStrokeWidth(2);

        setOnClickListener(this);
    }

    /**
     * 初始化所有UI配置
     *
     * @param delegate delegate
     */
    void setup(CalendarViewDelegate delegate) {
        this.delegate = delegate;

        this.currentDayTextPaint.setColor(delegate.getCurDayTextColor());
        this.currentDayLunarTextPaint.setColor(delegate.getCurDayLunarTextColor());
        this.currentMonthTextPaint.setColor(delegate.getCurrentMonthTextColor());
        this.otherMonthTextPaint.setColor(delegate.getOtherMonthTextColor());

        this.currentMonthLunarTextPaint.setColor(delegate.getCurrentMonthLunarTextColor());

        this.selectedLunarTextPaint.setColor(delegate.getSelectedLunarTextColor());
        this.selectTextPaint.setColor(delegate.getSelectedTextColor());
        this.otherMonthLunarTextPaint.setColor(delegate.getOtherMonthLunarTextColor());
        this.schemeLunarTextPaint.setColor(delegate.getSchemeLunarTextColor());

        this.schemePaint.setColor(delegate.getSchemeThemeColor());
        this.schemeTextPaint.setColor(delegate.getSchemeTextColor());


        this.currentMonthTextPaint.setTextSize(delegate.getDayTextSize());
        this.otherMonthTextPaint.setTextSize(delegate.getDayTextSize());
        this.currentDayTextPaint.setTextSize(delegate.getDayTextSize());
        this.schemeTextPaint.setTextSize(delegate.getDayTextSize());
        this.selectTextPaint.setTextSize(delegate.getDayTextSize());

        this.currentMonthLunarTextPaint.setTextSize(delegate.getLunarTextSize());
        this.selectedLunarTextPaint.setTextSize(delegate.getLunarTextSize());
        this.currentDayLunarTextPaint.setTextSize(delegate.getLunarTextSize());
        this.otherMonthLunarTextPaint.setTextSize(delegate.getLunarTextSize());
        this.schemeLunarTextPaint.setTextSize(delegate.getLunarTextSize());

        this.selectedPaint.setStyle(Paint.Style.FILL);
        this.selectedPaint.setColor(delegate.getSelectedThemeColor());
        setItemHeight(delegate.getCalendarItemHeight());
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() > 1) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                clickX = event.getX();
                clickY = event.getY();
                isClick = true;
                break;
            case MotionEvent.ACTION_MOVE:
                float mDY;
                if (isClick) {
                    mDY = event.getY() - clickY;
                    isClick = Math.abs(mDY) <= 50;
                }
                break;
            case MotionEvent.ACTION_UP:
                clickX = event.getX();
                clickY = event.getY();
                break;
        }
        return super.onTouchEvent(event);
    }


    /**
     * 开始绘制前的钩子，这里做一些初始化的操作，每次绘制只调用一次，性能高效
     * 没有需要可忽略不实现
     * 例如：
     * 1、需要绘制圆形标记事件背景，可以在这里计算半径
     * 2、绘制矩形选中效果，也可以在这里计算矩形宽和高
     */
    protected void onPreviewHook() {
        // TODO: 2017/11/16
    }


    /**
     * 设置高度
     *
     * @param itemHeight itemHeight
     */
    private void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
        Paint.FontMetrics metrics = currentMonthTextPaint.getFontMetrics();
        textBaseLine = this.itemHeight / 2 - metrics.descent + (metrics.bottom - metrics.top) / 2;
    }


    /**
     * 是否是选中的
     *
     * @param calendar calendar
     * @return true or false
     */
    protected boolean isSelected(CalendarEntity calendar) {
        return calendarEntityList != null && calendarEntityList.indexOf(calendar) == currentClickItem;
    }

    abstract void update();

    abstract void updateCurrentDate();
}
