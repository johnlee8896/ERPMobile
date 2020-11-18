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

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.view.View;

import com.chinashb.www.mobileerp.utils.UnitFormatUtil;


/**
 * 周视图，因为日历UI采用热插拔实现，所以这里必须继承实现，达到UI一致即可
 * Created by liweifeng on 2017/11/21.
 */

public abstract class WeekView extends BaseView {

    public WeekView(Context context) {
        super(context);
    }

    /**
     * 绘制日历文本
     *
     * @param canvas canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (calendarEntityList.size() == 0) {
            return;
        }
        itemWidth = (getWidth() - 2 * delegate.getCalendarPadding()) / 7;
        onPreviewHook();

        for (int i = 0; i < 7; i++) {
            int x = i * itemWidth + delegate.getCalendarPadding();
            onLoopStart(x);
            CalendarEntity calendar = calendarEntityList.get(i);
            boolean isSelected = i == currentClickItem;
            boolean hasScheme = calendar.hasScheme();
            if (hasScheme) {
                boolean isDrawSelected = false;//是否继续绘制选中的onDrawScheme
                if (isSelected) {
                    isDrawSelected = onDrawSelected(canvas, calendar, x, true);
                }
                if (isDrawSelected || !isSelected) {
                    //将画笔设置为标记颜色
                    schemePaint.setColor(calendar.getSchemeColor() != 0 ? calendar.getSchemeColor() : delegate.getSchemeThemeColor());
                    onDrawScheme(canvas, calendar, x);
                }
            } else {
                if (isSelected) {
                    onDrawSelected(canvas, calendar, x, false);
                }
            }

            onDrawText(canvas, calendar, x, hasScheme, isSelected, isbeforeToday(calendar));
            onDrawBottomDot(canvas, calendar, x);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(itemHeight, View.MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private boolean isbeforeToday(CalendarEntity calendar) {
        long dateLong = UnitFormatUtil.formatDateLong(calendar.getYear() + "-" + String.format("%2d-%2d", calendar.getMonth() + 1, calendar.getDay()));
        if (dateLong < System.currentTimeMillis()) {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (isClick) {
            CalendarEntity calendar = getIndex();
            if (calendar != null) {
                if (!CalendarUtil.isCalendarInRange(calendar, delegate.getMinYear(),
                        delegate.getMinYearMonth(), delegate.getMaxYear(), delegate.getMaxYearMonth())) {
                    currentClickItem = calendarEntityList.indexOf(delegate.selectedCalendar);
                    return;
                }
                if (delegate.innerListener != null) {
                    delegate.innerListener.onWeekDateSelected(calendar, true);
                }
                if (parentLayout != null) {
                    int i = CalendarUtil.getWeekFromDayInMonth(calendar, delegate.getWeekStart());
                    parentLayout.setSelectWeek(i);
                }

                if (delegate.dateSelectedListener != null) {
                    delegate.dateSelectedListener.onDateSelected(calendar, true);
                }

                invalidate();
            }
        }
    }

    /**
     * 周视图切换点击默认位置
     *
     * @param calendar calendar
     */
    void performClickCalendar(CalendarEntity calendar, boolean isNotice) {
        if (parentLayout == null || delegate.innerListener == null || calendarEntityList == null || calendarEntityList.size() == 0) {
            return;
        }

        int week = CalendarUtil.getWeekViewIndexFromCalendar(calendar, delegate.getWeekStart());
        if (calendarEntityList.contains(delegate.getCurrentDay())) {
            week = CalendarUtil.getWeekViewIndexFromCalendar(delegate.getCurrentDay(), delegate.getWeekStart());
        }

        currentClickItem = week;

        CalendarEntity currentCalendar = calendarEntityList.get(week);

        if (!CalendarUtil.isCalendarInRange(currentCalendar, delegate.getMinYear(),
                delegate.getMinYearMonth(), delegate.getMaxYear(), delegate.getMaxYearMonth())) {
            currentClickItem = getEdgeIndex(isLeftEdge(currentCalendar));
            currentCalendar = calendarEntityList.get(currentClickItem);
        }

        currentCalendar.setCurrentDay(currentCalendar.equals(delegate.getCurrentDay()));
        delegate.innerListener.onWeekDateSelected(currentCalendar, false);

        int i = CalendarUtil.getWeekFromDayInMonth(currentCalendar, delegate.getWeekStart());
//        parentLayout.setSelectWeek(i);

        if (delegate.dateSelectedListener != null && isNotice) {
            delegate.dateSelectedListener.onDateSelected(currentCalendar, false);
        }
        parentLayout.updateContentViewTranslateY();
        invalidate();
    }

    private boolean isLeftEdge(CalendarEntity calendar) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.set(delegate.getMinYear(), delegate.getMinYearMonth() - 1, 1);
        long minTime = c.getTimeInMillis();
        c.set(calendar.getYear(), calendar.getMonth() - 1, calendar.getDay());
        long curTime = c.getTimeInMillis();
        return curTime < minTime;
    }

    private int getEdgeIndex(boolean isMinEdge) {
        for (int i = 0; i < calendarEntityList.size(); i++) {
            CalendarEntity item = calendarEntityList.get(i);
            if (isMinEdge && CalendarUtil.isCalendarInRange(item, delegate.getMinYear(), delegate.getMinYearMonth(),
                    delegate.getMaxYear(), delegate.getMaxYearMonth())) {
                return i;
            } else if (!isMinEdge && !CalendarUtil.isCalendarInRange(item, delegate.getMinYear(), delegate.getMinYearMonth(),
                    delegate.getMaxYear(), delegate.getMaxYearMonth())) {
                return i - 1;
            }
        }
        return isMinEdge ? 6 : 0;
    }

    /**
     * 获取点击的日历
     *
     * @return 获取点击的日历
     */
    private CalendarEntity getIndex() {

        int indexX = (int) clickX / itemWidth;
        if (indexX >= 7) {
            indexX = 6;
        }
        int indexY = (int) clickY / itemHeight;
        currentClickItem = indexY * 7 + indexX;// 选择项
        if (currentClickItem >= 0 && currentClickItem < calendarEntityList.size()) {
            return calendarEntityList.get(currentClickItem);
        }
        return null;
    }

    /**
     * 记录已经选择的日期
     *
     * @param calendar calendar
     */
    void setSelectedCalendar(CalendarEntity calendar) {
        currentClickItem = calendarEntityList.indexOf(calendar);
    }

    /**
     * 初始化周视图控件
     *
     * @param calendar calendar
     */
    void setup(CalendarEntity calendar) {

        calendarEntityList = CalendarUtil.initCalendarForWeekView(calendar, delegate, delegate.getWeekStart());

        if (delegate.schemeDateList != null) {
            for (CalendarEntity entityInList : calendarEntityList) {
                for (CalendarEntity entityInSchemeList : delegate.schemeDateList) {
                    if (isCalendarSame(entityInList, entityInSchemeList)) {
                        entityInList.setBottomMarkDotBean(entityInSchemeList.getBottomMarkDotBean());
                    }
                }
            }
        }
        invalidate();
    }

    private boolean isCalendarSame(CalendarEntity entity1, CalendarEntity entity2) {
        if (entity1.getYear() == entity2.getYear() && entity1.getMonth() == entity2.getMonth() && entity1.getDay() == entity2.getDay()) {
            return true;
        }
        return false;
    }

    /**
     * 更新显示模式
     */
    void updateShowMode() {
        invalidate();
    }

    /**
     * 更新周起始
     */
    void updateWeekStart() {

        int position = (int) getTag();
        CalendarEntity calendar = CalendarUtil.getFirstCalendarFromWeekCount(delegate.getMinYear(),
                delegate.getMinYearMonth(),
                position + 1,
                delegate.getWeekStart());
        setSelectedCalendar(delegate.selectedCalendar);
        setup(calendar);
    }

    /**
     * 开始绘制前的钩子，这里做一些初始化的操作，每次绘制只调用一次，性能高效
     * 没有需要可忽略不实现
     * 例如：
     * 1、需要绘制圆形标记事件背景，可以在这里计算半径
     * 2、绘制矩形选中效果，也可以在这里计算矩形宽和高
     */
    @Override
    protected void onPreviewHook() {
        // TODO: 2017/11/16
    }

    /**
     * 更新界面
     */
    @Override
    void update() {
        if (delegate.schemeDateList == null || delegate.schemeDateList.size() == 0) {//清空操作
            for (CalendarEntity a : calendarEntityList) {
                a.setScheme("");
                a.setSchemeColor(0);
                a.setSchemes(null);
            }
            invalidate();
            return;
        }
        for (CalendarEntity a : calendarEntityList) {//添加操作
            if (delegate.schemeDateList.contains(a)) {
                CalendarEntity d = delegate.schemeDateList.get(delegate.schemeDateList.indexOf(a));
                a.setScheme(TextUtils.isEmpty(d.getScheme()) ? delegate.getSchemeText() : d.getScheme());
                a.setSchemeColor(d.getSchemeColor());
                a.setSchemes(d.getSchemes());

                a.setBottomMarkDotBean(d.getBottomMarkDotBean());
            } else {
                a.setScheme("");
                a.setSchemeColor(0);
                a.setSchemes(null);
                a.setBottomMarkDotBean(null);
            }
        }
        invalidate();
    }

    @Override
    void updateCurrentDate() {
        if (calendarEntityList == null) {
            return;
        }
        if (calendarEntityList.contains(delegate.getCurrentDay())) {
            for (CalendarEntity a : calendarEntityList) {//添加操作
                a.setCurrentDay(false);
            }
            int index = calendarEntityList.indexOf(delegate.getCurrentDay());
            calendarEntityList.get(index).setCurrentDay(true);
        }
        invalidate();
    }

    /**
     * 循环绘制开始的回调，不需要可忽略
     * 绘制每个日历项的循环，用来计算baseLine、圆心坐标等都可以在这里实现
     *
     * @param x 日历Card x起点坐标
     */
    @SuppressWarnings("unused")
    protected void onLoopStart(int x) {
        // TODO: 2017/11/16
    }

    /**
     * 绘制选中的日期
     *
     * @param canvas    canvas
     * @param calendar  日历日历calendar
     * @param x         日历Card x起点坐标
     * @param hasScheme hasScheme 非标记的日期
     * @return 是否绘制 onDrawScheme
     */
    protected abstract boolean onDrawSelected(Canvas canvas, CalendarEntity calendar, int x, boolean hasScheme);

    /**
     * 绘制标记的日期
     *
     * @param canvas   canvas
     * @param calendar 日历calendar
     * @param x        日历Card x起点坐标
     */
    protected abstract void onDrawScheme(Canvas canvas, CalendarEntity calendar, int x);

    protected abstract void onDrawBottomDot(Canvas canvas, CalendarEntity calendarEntity, int x);


    /**
     * 绘制日历文本
     *
     * @param canvas     canvas
     * @param calendar   日历calendar
     * @param x          日历Card x起点坐标
     * @param hasScheme  是否是标记的日期
     * @param isSelected 是否选中
     */
    protected abstract void onDrawText(Canvas canvas, CalendarEntity calendar, int x, boolean hasScheme, boolean isSelected, boolean isBeforeToday);
}
